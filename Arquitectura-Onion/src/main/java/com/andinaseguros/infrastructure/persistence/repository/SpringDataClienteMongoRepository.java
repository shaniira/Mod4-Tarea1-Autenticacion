package com.andinaseguros.infrastructure.persistence.repository;

import com.andinaseguros.infrastructure.persistence.document.ClienteDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataClienteMongoRepository extends MongoRepository<ClienteDocument, String> {
    Optional<ClienteDocument> findByNumeroDocumento(String documento);
}
