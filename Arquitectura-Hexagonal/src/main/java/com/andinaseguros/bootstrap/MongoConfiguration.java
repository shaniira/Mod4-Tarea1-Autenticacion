package com.andinaseguros.bootstrap;

import com.andinaseguros.adapters.outbound.persistence.mongo.repository.SpringDataClienteMongoRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = SpringDataClienteMongoRepository.class)
public class MongoConfiguration {}
