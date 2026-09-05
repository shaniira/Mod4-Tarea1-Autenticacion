package com.andinaseguros.presentation.request;

import com.andinaseguros.domain.enums.EstadoSiniestro;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoSiniestroRequest(@NotNull EstadoSiniestro estado) {}
