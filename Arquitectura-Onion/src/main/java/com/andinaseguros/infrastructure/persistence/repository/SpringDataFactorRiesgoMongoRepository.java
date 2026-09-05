package com.andinaseguros.infrastructure.persistence.repository;

import com.andinaseguros.infrastructure.persistence.document.FactorRiesgoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataFactorRiesgoMongoRepository
        extends MongoRepository<FactorRiesgoDocument, String> {}
