package com.andinaseguros.core.ports.out.persistence;

import com.andinaseguros.core.domain.model.Poliza;
import java.util.*;

public interface PolizaRepositoryPort {
    Poliza guardar(Poliza poliza);

    Optional<Poliza> buscarPorId(UUID id);

    Optional<Poliza> buscarPorNumero(String numero);

    Optional<Poliza> buscarPorCotizacionId(UUID cotizacionId);

    List<Poliza> listar();

    List<Poliza> listarPorCliente(UUID clienteId);
}
