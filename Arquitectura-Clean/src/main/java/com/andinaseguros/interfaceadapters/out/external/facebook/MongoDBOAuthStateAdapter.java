package com.andinaseguros.interfaceadapters.out.external.facebook;

import com.andinaseguros.interfaceadapters.out.persistence.mongodb.OAuthStateDocument;
import com.andinaseguros.interfaceadapters.out.persistence.mongodb.OAuthStateRepository;
import com.andinaseguros.usecases.port.out.facebook.OAuthStatePort;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

public class MongoDBOAuthStateAdapter implements OAuthStatePort {
    private final OAuthStateRepository repository;
    private final SecureRandom random = new SecureRandom();
    private final int ttlSeconds;

    public MongoDBOAuthStateAdapter(OAuthStateRepository repository, int ttlSeconds) {
        this.repository = repository;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public String create() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);
        repository.save(new OAuthStateDocument(state, expiresAt));
        return state;
    }

    @Override
    public boolean consume(String state) {
        if (state == null) return false;
        var doc = repository.findById(state);
        if (doc.isEmpty()) return false;
        
        OAuthStateDocument oauthState = doc.get();
        repository.deleteById(state);
        return oauthState.getExpiresAt().isAfter(Instant.now());
    }
}
