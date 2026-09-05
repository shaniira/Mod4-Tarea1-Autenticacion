package com.andinaseguros.infrastructure.persistence.config;

import com.andinaseguros.infrastructure.persistence.repository.SpringDataClienteMongoRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = SpringDataClienteMongoRepository.class)
public class MongoConfiguration {}
