package com.andinaseguros.core.ports.out.persistence;

import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import java.util.*;

public interface RenovacionRepositoryPort {
    PropuestaRenovacion guardar(PropuestaRenovacion propuesta);

    Optional<PropuestaRenovacion> buscarPorId(UUID id);

    List<PropuestaRenovacion> listar();

    List<PropuestaRenovacion> listarPorPoliza(UUID polizaId);
}
