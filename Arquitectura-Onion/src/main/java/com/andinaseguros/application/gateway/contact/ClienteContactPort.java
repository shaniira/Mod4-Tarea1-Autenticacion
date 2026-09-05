package com.andinaseguros.application.gateway.contact;

import java.util.Optional;
import java.util.UUID;

public interface ClienteContactPort {
    Optional<ClienteContactData> buscarPorClienteId(UUID clienteId);
}
