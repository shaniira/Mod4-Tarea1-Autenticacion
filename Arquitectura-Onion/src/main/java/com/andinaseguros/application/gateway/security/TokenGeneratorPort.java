package com.andinaseguros.application.gateway.security;

public interface TokenGeneratorPort {
    String generar(AuthenticatedUser usuario);

    long expirationSeconds();
}
