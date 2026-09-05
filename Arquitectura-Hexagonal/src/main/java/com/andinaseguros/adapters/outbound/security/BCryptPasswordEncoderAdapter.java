package com.andinaseguros.adapters.outbound.security;

import com.andinaseguros.core.ports.out.security.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {
    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

    public String codificar(String textoPlano) {
        return delegate.encode(textoPlano);
    }

    public boolean coincide(String textoPlano, String hash) {
        return delegate.matches(textoPlano, hash);
    }
}
