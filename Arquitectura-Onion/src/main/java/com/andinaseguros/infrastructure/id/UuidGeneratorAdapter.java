package com.andinaseguros.infrastructure.id;

import com.andinaseguros.application.gateway.id.IdGeneratorPort;
import java.util.UUID;

public class UuidGeneratorAdapter implements IdGeneratorPort {
    public UUID generar() {
        return UUID.randomUUID();
    }
}
