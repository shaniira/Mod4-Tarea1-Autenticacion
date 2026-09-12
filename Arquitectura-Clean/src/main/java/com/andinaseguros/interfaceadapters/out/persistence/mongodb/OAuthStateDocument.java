package com.andinaseguros.interfaceadapters.out.persistence.mongodb;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "oauth_states")
public class OAuthStateDocument {
    @Id private String state;
    private Instant expiresAt;

    public OAuthStateDocument() {}

    public OAuthStateDocument(String state, Instant expiresAt) {
        this.state = state;
        this.expiresAt = expiresAt;
    }

    public String getState() { return state; }
    public Instant getExpiresAt() { return expiresAt; }
}
