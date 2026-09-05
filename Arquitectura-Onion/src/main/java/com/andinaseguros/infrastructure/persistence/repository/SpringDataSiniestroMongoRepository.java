package com.andinaseguros.infrastructure.persistence.repository;

import com.andinaseguros.infrastructure.persistence.document.SiniestroDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataSiniestroMongoRepository
        extends MongoRepository<SiniestroDocument, String> {
    List<SiniestroDocument> findByPolizaId(String polizaId);
}
