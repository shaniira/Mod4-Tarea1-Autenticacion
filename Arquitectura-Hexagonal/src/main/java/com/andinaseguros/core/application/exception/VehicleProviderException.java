package com.andinaseguros.core.application.exception;

public abstract class VehicleProviderException extends RuntimeException {
    protected VehicleProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
