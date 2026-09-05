package com.andinaseguros.domain.repository;

import com.andinaseguros.domain.model.PropuestaRenovacion;
import java.util.*;

public interface RenovacionRepository {
    PropuestaRenovacion guardar(PropuestaRenovacion propuesta);

    Optional<PropuestaRenovacion> buscarPorId(UUID id);

    List<PropuestaRenovacion> listar();

    List<PropuestaRenovacion> listarPorPoliza(UUID polizaId);
}
