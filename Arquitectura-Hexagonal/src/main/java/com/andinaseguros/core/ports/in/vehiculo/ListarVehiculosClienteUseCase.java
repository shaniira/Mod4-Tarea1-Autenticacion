package com.andinaseguros.core.ports.in.vehiculo;

import com.andinaseguros.core.application.dto.Responses.VehiculoResponse;
import com.andinaseguros.core.ports.out.persistence.VehiculoRepositoryPort;
import java.util.List;
import java.util.UUID;

public interface ListarVehiculosClienteUseCase {
    List<VehiculoResponse> execute(UUID clienteId);
}
