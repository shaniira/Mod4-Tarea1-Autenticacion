package com.andinaseguros.application.service.cliente;

import static com.andinaseguros.application.mapper.ClienteResponseMapper.toResponse;

import com.andinaseguros.application.dto.CrearClienteDto;
import com.andinaseguros.application.dto.Responses.ClienteResponse;
import com.andinaseguros.application.service.RegistrarClienteUseCase;
import com.andinaseguros.domain.exception.ReglaNegocioException;
import com.andinaseguros.domain.model.Cliente;
import com.andinaseguros.domain.repository.ClienteRepository;
import java.util.UUID;

public class CrearClienteUseCase implements RegistrarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public CrearClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public ClienteResponse execute(CrearClienteDto solicitud) {
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
