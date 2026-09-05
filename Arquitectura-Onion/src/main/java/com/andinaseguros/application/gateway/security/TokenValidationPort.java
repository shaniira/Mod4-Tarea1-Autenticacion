package com.andinaseguros.application.gateway.security;

public interface TokenValidationPort {
    TokenClaims validar(String token);
}
