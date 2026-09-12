package com.andinaseguros.interfaceadapters.out.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuthStateRepository extends MongoRepository<OAuthStateDocument, String> {}
