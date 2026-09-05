package com.andinaseguros.core.domain.exception;

public class RecursoNoEncontradoException extends DomainException {
    public RecursoNoEncontradoException(String recurso) {
        super("RECURSO_NO_ENCONTRADO", recurso + " no encontrado");
    }
}
