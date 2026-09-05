package com.andinaseguros.application.dto;

import com.andinaseguros.domain.enums.EstadoSiniestro;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarSiniestroDto(
        UUID polizaId,
        LocalDate fecha,
        String tipo,
        BigDecimal montoEstimado,
        boolean responsabilidadAsegurado,
        String gravedad,
        EstadoSiniestro estado) {}
