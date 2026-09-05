package com.andinaseguros.core.ports.out.persistence;

import com.andinaseguros.core.domain.enums.*;
import com.andinaseguros.core.domain.model.TablaTarifaria;
import java.time.LocalDate;
import java.util.*;

public interface TablaTarifariaRepositoryPort {
    TablaTarifaria guardar(TablaTarifaria tablaTarifaria);

    Optional<TablaTarifaria> buscarPorId(UUID id);

    Optional<TablaTarifaria> buscarVigente(TipoVehiculo tipo, TipoUso uso, LocalDate fecha);

    List<TablaTarifaria> listar();
}
