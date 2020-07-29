package com.googolplex.client;


import org.springframework.web.reactive.function.client.ClientResponse;

import com.googolplex.errors.RestClientException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
public class RestClientErrorHandler {


    public static  Mono<Throwable> handle(ClientResponse cr){
        // TODO do exception mapping here
        return Mono.just(new RestClientException(cr.statusCode().toString() , "Message" ,""));
    }

    public static  void handle(Throwable e){

    }

}