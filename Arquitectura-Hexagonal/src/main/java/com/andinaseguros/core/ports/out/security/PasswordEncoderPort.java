package com.andinaseguros.core.ports.out.security;

public interface PasswordEncoderPort {
    String codificar(String textoPlano);

    boolean coincide(String textoPlano, String hash);
}
