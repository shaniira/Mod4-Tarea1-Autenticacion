package com.andinaseguros.core.application.service.vehiculo;

import com.andinaseguros.core.ports.in.vehiculo.ListarVehiculosClienteUseCase;

import static com.andinaseguros.core.application.mapper.VehiculoResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.VehiculoResponse;
import com.andinaseguros.core.ports.out.persistence.VehiculoRepositoryPort;
import java.util.List;
import java.util.UUID;

public class ListarVehiculosClienteService implements ListarVehiculosClienteUseCase {

    private final VehiculoRepositoryPort vehiculoRepository;

    public ListarVehiculosClienteService(VehiculoRepositoryPort vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<VehiculoResponse> execute(UUID clienteId) {
        return vehiculoRepository.listarPorCliente(clienteId).stream()
                .map(vehiculo -> toResponse(vehiculo))
                .toList();
    }
}
