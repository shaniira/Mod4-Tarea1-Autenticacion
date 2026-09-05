package com.andinaseguros.application.service.cliente;

import static com.andinaseguros.application.mapper.ClienteResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.ClienteResponse;
import com.andinaseguros.domain.repository.ClienteRepository;
import java.util.List;

public class ListarClientesUseCase {

    private final ClienteRepository clienteRepository;

    public ListarClientesUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> execute() {
        return clienteRepository.listar().stream().map(cliente -> toResponse(cliente)).toList();
    }
}
