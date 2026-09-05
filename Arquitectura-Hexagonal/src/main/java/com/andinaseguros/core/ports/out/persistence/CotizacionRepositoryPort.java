package com.andinaseguros.core.ports.out.persistence;

import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.domain.model.Cotizacion;
import java.util.*;

public interface CotizacionRepositoryPort {
    Cotizacion guardar(Cotizacion cotizacion);

    Optional<Cotizacion> buscarPorId(UUID id);

    List<Cotizacion> listar();

    List<Cotizacion> listarPorEstado(EstadoCotizacion estado);

    List<Cotizacion> listarPorCliente(UUID clienteId);
}
