package com.andinaseguros.infrastructure.persistence.repository;

import com.andinaseguros.infrastructure.persistence.document.RenovacionDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataRenovacionMongoRepository
        extends MongoRepository<RenovacionDocument, String> {
    List<RenovacionDocument> findByPolizaOrigenIdOrderByCreadaEnDesc(String polizaId);
}
