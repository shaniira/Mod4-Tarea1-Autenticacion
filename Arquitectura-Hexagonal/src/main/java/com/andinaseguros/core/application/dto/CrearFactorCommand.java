package com.andinaseguros.core.application.dto;

import java.math.BigDecimal;

public record CrearFactorCommand(
        String codigo,
        String nombre,
        String tipoVariable,
        BigDecimal valorMinimo,
        BigDecimal valorMaximo,
        BigDecimal multiplicador,
        int orden) {}
