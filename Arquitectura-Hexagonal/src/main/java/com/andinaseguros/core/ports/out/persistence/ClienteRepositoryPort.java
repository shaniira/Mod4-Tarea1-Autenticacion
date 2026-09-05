package com.andinaseguros.core.ports.out.persistence;

import com.andinaseguros.core.domain.model.Cliente;
import java.util.*;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);

    Optional<Cliente> buscarPorDocumento(String doc);

    List<Cliente> listar();
}
