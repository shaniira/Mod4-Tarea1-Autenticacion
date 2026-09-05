package com.andinaseguros.domain.repository;

import com.andinaseguros.domain.enums.*;
import com.andinaseguros.domain.model.TablaTarifaria;
import java.time.LocalDate;
import java.util.*;

public interface TablaTarifariaRepository {
    TablaTarifaria guardar(TablaTarifaria tablaTarifaria);

    Optional<TablaTarifaria> buscarPorId(UUID id);

    Optional<TablaTarifaria> buscarVigente(TipoVehiculo tipo, TipoUso uso, LocalDate fecha);

    List<TablaTarifaria> listar();
}
