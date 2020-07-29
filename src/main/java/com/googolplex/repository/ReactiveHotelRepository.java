package com.googolplex.repository;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.googolplex.model.Hotel;

import reactor.core.publisher.Mono;

public interface ReactiveHotelRepository extends ReactiveCrudRepository<Hotel, String> {

    Mono<Hotel> findByUuid(String id);
    @Query("DELETE FROM HOTEL WHERE UUID=?0")
    Mono<Hotel> deleteByUuid(String id);

}