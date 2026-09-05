package com.andinaseguros.adapters.outbound.persistence.mongo.repository;

import com.andinaseguros.adapters.outbound.persistence.mongo.document.FactorRiesgoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataFactorRiesgoMongoRepository
        extends MongoRepository<FactorRiesgoDocument, String> {}
