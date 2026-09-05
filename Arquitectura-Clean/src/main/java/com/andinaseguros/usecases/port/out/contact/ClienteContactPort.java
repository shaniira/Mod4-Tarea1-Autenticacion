package com.andinaseguros.usecases.port.out.contact;

import java.util.Optional;
import java.util.UUID;

public interface ClienteContactPort {
    Optional<ClienteContactData> buscarPorClienteId(UUID clienteId);
}
