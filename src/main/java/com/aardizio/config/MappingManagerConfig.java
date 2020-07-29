package com.aardizio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;

@Configuration
public class MappingManagerConfig {

    @Bean
    public MappingManager mappingManager(Session session) {
        return new MappingManager(session);
    }  

}