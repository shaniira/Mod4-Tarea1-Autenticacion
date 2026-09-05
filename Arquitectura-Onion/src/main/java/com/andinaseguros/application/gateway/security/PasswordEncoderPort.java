package com.andinaseguros.application.gateway.security;

public interface PasswordEncoderPort {
    String codificar(String textoPlano);

    boolean coincide(String textoPlano, String hash);
}
