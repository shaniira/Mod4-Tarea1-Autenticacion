package com.andinaseguros.domain.repository;

import com.andinaseguros.domain.enums.EstadoCotizacion;
import com.andinaseguros.domain.model.Cotizacion;
import java.util.*;

public interface CotizacionRepository {
    Cotizacion guardar(Cotizacion cotizacion);

    Optional<Cotizacion> buscarPorId(UUID id);

    List<Cotizacion> listar();

    List<Cotizacion> listarPorEstado(EstadoCotizacion estado);

    List<Cotizacion> listarPorCliente(UUID clienteId);
}
