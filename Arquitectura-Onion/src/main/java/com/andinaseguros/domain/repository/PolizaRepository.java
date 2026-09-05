package com.andinaseguros.domain.repository;

import com.andinaseguros.domain.model.Poliza;
import java.util.*;

public interface PolizaRepository {
    Poliza guardar(Poliza poliza);

    Optional<Poliza> buscarPorId(UUID id);

    Optional<Poliza> buscarPorNumero(String numero);

    Optional<Poliza> buscarPorCotizacionId(UUID cotizacionId);

    List<Poliza> listar();

    List<Poliza> listarPorCliente(UUID clienteId);
}
