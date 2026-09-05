package com.andinaseguros.adapters.outbound.persistence.mongo.repository;

import com.andinaseguros.adapters.outbound.persistence.mongo.document.SiniestroDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataSiniestroMongoRepository
        extends MongoRepository<SiniestroDocument, String> {
    List<SiniestroDocument> findByPolizaId(String polizaId);
}
