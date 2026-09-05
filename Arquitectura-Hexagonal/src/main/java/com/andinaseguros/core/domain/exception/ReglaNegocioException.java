package com.andinaseguros.core.domain.exception;

public class ReglaNegocioException extends DomainException {
    public ReglaNegocioException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
