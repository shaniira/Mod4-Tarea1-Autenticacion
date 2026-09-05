package com.andinaseguros.core.application.dto;

import com.andinaseguros.core.domain.enums.EstadoSiniestro;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarSiniestroCommand(
        UUID polizaId,
        LocalDate fecha,
        String tipo,
        BigDecimal montoEstimado,
        boolean responsabilidadAsegurado,
        String gravedad,
        EstadoSiniestro estado) {}
