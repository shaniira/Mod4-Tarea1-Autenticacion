package com.andinaseguros.domain.repository;

import com.andinaseguros.domain.model.Cliente;
import java.util.*;

public interface ClienteRepository {
    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);

    Optional<Cliente> buscarPorDocumento(String doc);

    List<Cliente> listar();
}
