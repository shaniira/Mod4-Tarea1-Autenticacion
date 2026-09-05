package com.andinaseguros.adapters.outbound.id;

import com.andinaseguros.core.ports.out.id.IdGeneratorPort;
import java.util.UUID;

public class UuidGeneratorAdapter implements IdGeneratorPort {
    public UUID generar() {
        return UUID.randomUUID();
    }
}
