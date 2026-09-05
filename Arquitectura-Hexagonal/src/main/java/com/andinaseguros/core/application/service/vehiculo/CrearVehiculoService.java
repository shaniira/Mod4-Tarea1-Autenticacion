package com.andinaseguros.core.application.service.vehiculo;

import com.andinaseguros.core.ports.in.vehiculo.CrearVehiculoUseCase;

import static com.andinaseguros.core.application.mapper.VehiculoResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.CrearVehiculoCommand;
import com.andinaseguros.core.application.dto.Responses.VehiculoResponse;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Vehiculo;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.VehiculoRepositoryPort;
import com.andinaseguros.core.domain.valueobject.Placa;
import java.util.UUID;

public class CrearVehiculoService implements CrearVehiculoUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final VehiculoRepositoryPort vehiculoRepository;

    public CrearVehiculoService(
            ClienteRepositoryPort clienteRepository, VehiculoRepositoryPort vehiculoRepository) {
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public VehiculoResponse execute(CrearVehiculoCommand solicitud) {
        validarCliente(solicitud.clienteId());
        validarPlacaDisponible(solicitud.placa());
        Vehiculo vehiculo =
                new Vehiculo(
                        UUID.randomUUID(),
                        solicitud.clienteId(),
                        new Placa(solicitud.placa()),
                        solicitud.marca(),
                        solicitud.modelo(),
                        solicitud.anioFabricacion(),
                        solicitud.tipo(),
                        solicitud.uso(),
                        solicitud.zonaCirculacion());

        Vehiculo vehiculoGuardado = vehiculoRepository.guardar(vehiculo);

        return toResponse(vehiculoGuardado);
    }

    private void validarCliente(UUID clienteId) {
        clienteRepository
                .buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente"));
    }

    private void validarPlacaDisponible(String placa) {
        if (vehiculoRepository.buscarPorPlaca(placa).isPresent()) {
            throw new ReglaNegocioException("PLACA_DUPLICADA", "La placa ya está registrada");
        }
    }
}
