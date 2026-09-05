package com.andinaseguros.application.service.cliente;

import static com.andinaseguros.application.mapper.ClienteResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.ClienteResponse;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.model.Cliente;
import com.andinaseguros.domain.repository.ClienteRepository;
import java.util.UUID;

public class ObtenerClienteUseCase {

    private final ClienteRepository clienteRepository;

    public ObtenerClienteUseCase(ClienteRepository clienteRepository) {
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
