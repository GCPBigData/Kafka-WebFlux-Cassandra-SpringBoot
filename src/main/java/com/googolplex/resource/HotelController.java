package com.googolplex.resource;

import brave.Tracer;
import com.googolplex.client.RestClientExample;
import com.googolplex.errors.ApiErrorHandler;
import com.googolplex.errors.CustomException;
import com.googolplex.errors.DefaultBusinessProblem;
import com.googolplex.model.Hotel;
import com.googolplex.repository.ReactiveHotelRepository;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.List;

@Slf4j
@RestController
public class HotelController {

	@Autowired
	private ApiErrorHandler apiErrorHandler;

	@Autowired
	@Qualifier("simpleProducer")
	private KafkaSender<String, String> hotelSender;

	@Autowired
	private Tracer tracer;

	@Autowired
	private ReactiveHotelRepository hotelRepo;

	@Autowired
	private ReactiveCassandraTemplate reactiveCassandraTemplate;

	@Autowired
	private RestClientExample hotelRestClient;

	// @ExceptionHandler({Exception.class})
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		return apiErrorHandler.handleAll(ex, request);
	}

	@GetMapping(value = "/clientExample", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody Flux<Hotel> clientExample() {
		return hotelRestClient.getAllHotels();
	}

	@DeleteMapping(value = "/hotels/{id}", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<Hotel> delete(@PathVariable String id) {

		tracer.currentSpan().tag("hotelid", id);
		return hotelRepo.deleteByUuid(id);
	}

	@PostMapping(value = "/hotels", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody Mono<Hotel> create(@RequestBody Hotel hotel) {
		tracer.currentSpan().tag("hotelid", hotel.getUuid());


		return hotelRepo.save(hotel).flatMapMany(h -> {
			ProducerRecord<String, String> record = new ProducerRecord<String, String>("prova", null, hotel.getUuid(),
					hotel.toString());
			Mono<SenderRecord<String, String, String>> mono = Mono.just(SenderRecord.create(record, null));
			return hotelSender.send(mono);
		}).collectList().flatMap(m -> Mono.just(hotel));

	}

	@GetMapping(value = "/hotels/{uuid}", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<Hotel> search(@PathVariable String uuid) {
		tracer.currentSpan().tag("hotelid", uuid);
		return hotelRepo.findByUuid(uuid);
	}

	/**
	 * Get all hotels and retry proof settings consistency level to THREE with one
	 * cassandra node .
	 */
	@GetMapping(value = "/hotels", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<List<Hotel>> getAllRetryProof() {
		Statement search = QueryBuilder.select().from("hotel").setConsistencyLevel(ConsistencyLevel.THREE)
				.setRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE);
		return reactiveCassandraTemplate.select(search, Hotel.class).collectList();
	}

	@GetMapping(value = "/hotels/error", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<List<Hotel>> generateProblem() throws Exception {
		throw new Exception("Problem ");
	}

	@GetMapping(value = "/hotels/error2", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<List<Hotel>> generateProblem2() throws Exception {
		throw new CustomException("Problem");
	}

	@GetMapping(value = "/hotels/error3", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public Mono<List<Hotel>> generateProblem3() throws Exception {
		throw new DefaultBusinessProblem("Problem", "0001", "10");
	}

}