package com.andinaseguros.adapters.outbound.persistence.mongo.repository;

import com.andinaseguros.adapters.outbound.persistence.mongo.document.UsuarioDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataUsuarioMongoRepository extends MongoRepository<UsuarioDocument, String> {
    Optional<UsuarioDocument> findByUsername(String username);
}
