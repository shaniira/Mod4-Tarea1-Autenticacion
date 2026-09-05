package com.andinaseguros.application.dto;

import com.andinaseguros.domain.enums.EstadoTablaTarifaria;
import com.andinaseguros.domain.enums.TipoUso;
import com.andinaseguros.domain.enums.TipoVehiculo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CrearTablaDto(
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
        List<CrearFactorDto> factores) {}
