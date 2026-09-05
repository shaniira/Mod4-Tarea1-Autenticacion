package com.andinaseguros.core.ports.in.cliente;

import com.andinaseguros.core.application.dto.Responses.ClienteResponse;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import java.util.List;

public interface ListarClientesUseCase {
    List<ClienteResponse> execute();
}
