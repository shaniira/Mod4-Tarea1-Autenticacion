package com.andinaseguros.core.ports.out.external.contact;

import java.util.Optional;
import java.util.UUID;

public interface ClienteContactPort {
    Optional<ClienteContactData> buscarPorClienteId(UUID clienteId);
}
