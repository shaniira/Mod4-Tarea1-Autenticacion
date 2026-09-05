package com.andinaseguros.core.application.service.cliente;

import com.andinaseguros.core.ports.in.cliente.ListarClientesUseCase;

import static com.andinaseguros.core.application.mapper.ClienteResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.ClienteResponse;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import java.util.List;

public class ListarClientesService implements ListarClientesUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public ListarClientesService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> execute() {
        return clienteRepository.listar().stream().map(cliente -> toResponse(cliente)).toList();
    }
}
