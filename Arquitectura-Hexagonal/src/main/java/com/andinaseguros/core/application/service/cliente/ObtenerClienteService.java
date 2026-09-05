package com.andinaseguros.core.application.service.cliente;

import com.andinaseguros.core.ports.in.cliente.ObtenerClienteUseCase;

import static com.andinaseguros.core.application.mapper.ClienteResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.ClienteResponse;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.model.Cliente;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import java.util.UUID;

public class ObtenerClienteService implements ObtenerClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public ObtenerClienteService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponse execute(UUID clienteId) {
        Cliente cliente =
                clienteRepository
                        .buscarPorId(clienteId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Cliente"));

        return toResponse(cliente);
    }
}
