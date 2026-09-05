package com.andinaseguros.adapters.outbound.persistence.mongo.repository;

import com.andinaseguros.adapters.outbound.persistence.mongo.document.ClienteDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataClienteMongoRepository extends MongoRepository<ClienteDocument, String> {
    Optional<ClienteDocument> findByNumeroDocumento(String documento);
}
