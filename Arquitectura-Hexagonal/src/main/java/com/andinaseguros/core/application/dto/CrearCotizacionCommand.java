package com.andinaseguros.core.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CrearCotizacionCommand(
        UUID clienteId,
        UUID vehiculoId,
        int siniestrosResponsables,
        BigDecimal porcentajeGastos,
        BigDecimal porcentajeRecargo,
        BigDecimal porcentajeDescuento) {}
