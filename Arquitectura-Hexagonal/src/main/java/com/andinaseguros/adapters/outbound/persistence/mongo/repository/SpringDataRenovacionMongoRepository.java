package com.andinaseguros.adapters.outbound.persistence.mongo.repository;

import com.andinaseguros.adapters.outbound.persistence.mongo.document.RenovacionDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataRenovacionMongoRepository
        extends MongoRepository<RenovacionDocument, String> {
    List<RenovacionDocument> findByPolizaOrigenIdOrderByCreadaEnDesc(String polizaId);
}
