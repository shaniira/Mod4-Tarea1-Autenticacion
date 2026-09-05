package com.andinaseguros.application.mapper;

import com.andinaseguros.application.dto.Responses.ClienteResponse;
import com.andinaseguros.domain.model.Cliente;

public final class ClienteResponseMapper {
    private ClienteResponseMapper() {}

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getFechaNacimiento(),
                cliente.getCorreo(),
                cliente.getTelefono(),
                cliente.isActivo());
    }
}
