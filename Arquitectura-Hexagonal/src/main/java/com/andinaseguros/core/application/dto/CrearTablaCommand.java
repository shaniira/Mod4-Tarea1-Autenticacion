package com.andinaseguros.core.application.dto;

import com.andinaseguros.core.domain.enums.EstadoTablaTarifaria;
import com.andinaseguros.core.domain.enums.TipoUso;
import com.andinaseguros.core.domain.enums.TipoVehiculo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CrearTablaCommand(
        String codigo,
        int version,
        TipoVehiculo tipoVehiculo,
        TipoUso tipoUso,
        BigDecimal primaBase,
        BigDecimal primaMinima,
        LocalDate inicioVigencia,
        LocalDate finVigencia,
        String codigoNotaTecnica,
        EstadoTablaTarifaria estado,
        List<CrearFactorCommand> factores) {}
