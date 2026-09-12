package com.andinaseguros.interfaceadapters.out.persistence.mongodb.repository;

import com.andinaseguros.interfaceadapters.out.persistence.mongodb.OAuthStateDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuthStateRepository extends MongoRepository<OAuthStateDocument, String> {}
