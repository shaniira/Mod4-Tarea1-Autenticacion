package com.andinaseguros.core.ports.out.security;

public interface TokenValidationPort {
    TokenClaims validar(String token);
}
