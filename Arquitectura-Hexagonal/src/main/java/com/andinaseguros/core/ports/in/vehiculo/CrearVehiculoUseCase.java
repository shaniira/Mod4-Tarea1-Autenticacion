package com.andinaseguros.core.ports.in.vehiculo;

import com.andinaseguros.core.application.dto.CrearVehiculoCommand;
import com.andinaseguros.core.application.dto.Responses.VehiculoResponse;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.VehiculoRepositoryPort;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Vehiculo;
import com.andinaseguros.core.domain.valueobject.Placa;
import java.util.UUID;

public interface CrearVehiculoUseCase {
    VehiculoResponse execute(CrearVehiculoCommand solicitud);
}
