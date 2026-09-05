package com.andinaseguros.core.ports.in.cliente;

import com.andinaseguros.core.application.dto.Responses.ClienteResponse;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.Cliente;
import java.util.UUID;

public interface ObtenerClienteUseCase {
    ClienteResponse execute(UUID clienteId);
}
