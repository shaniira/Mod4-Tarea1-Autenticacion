package com.andinaseguros.core.ports.out.security;

public interface TokenGeneratorPort {
    String generar(AuthenticatedUser usuario);

    long expirationSeconds();
}
