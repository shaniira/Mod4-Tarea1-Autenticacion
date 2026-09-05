package com.andinaseguros.infrastructure.persistence.repository;

import com.andinaseguros.infrastructure.persistence.document.UsuarioDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataUsuarioMongoRepository extends MongoRepository<UsuarioDocument, String> {
    Optional<UsuarioDocument> findByUsername(String username);
}
