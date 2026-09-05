package com.andinaseguros.application.service.vehiculo;

import static com.andinaseguros.application.mapper.VehiculoResponseMapper.toResponse;

import com.andinaseguros.application.dto.CrearVehiculoDto;
import com.andinaseguros.application.dto.Responses.VehiculoResponse;
import com.andinaseguros.application.service.RegistrarVehiculoUseCase;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.exception.ReglaNegocioException;
import com.andinaseguros.domain.model.Vehiculo;
import com.andinaseguros.domain.repository.ClienteRepository;
import com.andinaseguros.domain.repository.VehiculoRepository;
import com.andinaseguros.domain.valueobject.Placa;
import java.util.UUID;

public class CrearVehiculoUseCase implements RegistrarVehiculoUseCase {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    public CrearVehiculoUseCase(
            ClienteRepository clienteRepository, VehiculoRepository vehiculoRepository) {
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public VehiculoResponse execute(CrearVehiculoDto solicitud) {
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
