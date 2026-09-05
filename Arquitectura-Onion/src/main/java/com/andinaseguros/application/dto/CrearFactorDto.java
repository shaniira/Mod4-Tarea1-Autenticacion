package com.andinaseguros.application.dto;

import java.math.BigDecimal;

public record CrearFactorDto(
        String codigo,
        String nombre,
        String tipoVariable,
        BigDecimal valorMinimo,
        BigDecimal valorMaximo,
        BigDecimal multiplicador,
        int orden) {}
