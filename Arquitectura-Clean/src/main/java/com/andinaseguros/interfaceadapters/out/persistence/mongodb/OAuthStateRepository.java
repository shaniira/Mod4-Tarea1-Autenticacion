package com.andinaseguros.interfaceadapters.out.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthStateRepository extends MongoRepository<OAuthStateDocument, String> {}
