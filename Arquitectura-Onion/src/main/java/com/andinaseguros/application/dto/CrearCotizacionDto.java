package com.andinaseguros.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CrearCotizacionDto(
        UUID clienteId,
        UUID vehiculoId,
        int siniestrosResponsables,
        BigDecimal porcentajeGastos,
        BigDecimal porcentajeRecargo,
        BigDecimal porcentajeDescuento) {}
