package com.andinaseguros.application.service.vehiculo;

import static com.andinaseguros.application.mapper.VehiculoResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.VehiculoResponse;
import com.andinaseguros.domain.repository.VehiculoRepository;
import java.util.List;
import java.util.UUID;

public class ListarVehiculosClienteUseCase {

    private final VehiculoRepository vehiculoRepository;

    public ListarVehiculosClienteUseCase(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<VehiculoResponse> execute(UUID clienteId) {
        return vehiculoRepository.listarPorCliente(clienteId).stream()
                .map(vehiculo -> toResponse(vehiculo))
                .toList();
    }
}
