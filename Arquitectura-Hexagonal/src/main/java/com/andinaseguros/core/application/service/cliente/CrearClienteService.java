package com.andinaseguros.core.application.service.cliente;

import com.andinaseguros.core.ports.in.cliente.CrearClienteUseCase;

import static com.andinaseguros.core.application.mapper.ClienteResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.CrearClienteCommand;
import com.andinaseguros.core.application.dto.Responses.ClienteResponse;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Cliente;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import java.util.UUID;

public class CrearClienteService implements CrearClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public CrearClienteService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public ClienteResponse execute(CrearClienteCommand solicitud) {
        boolean documentoRegistrado =
                clienteRepository.buscarPorDocumento(solicitud.numeroDocumento()).isPresent();

        if (documentoRegistrado) {
            throw new ReglaNegocioException(
                    "DOCUMENTO_DUPLICADO", "Ya existe un cliente con ese documento");
        }

        Cliente cliente =
                new Cliente(
                        UUID.randomUUID(),
                        solicitud.tipoDocumento(),
                        solicitud.numeroDocumento(),
                        solicitud.nombres(),
                        solicitud.apellidos(),
                        solicitud.fechaNacimiento(),
                        solicitud.correo(),
                        solicitud.telefono(),
                        true);

        Cliente clienteGuardado = clienteRepository.guardar(cliente);

        return toResponse(clienteGuardado);
    }
}
