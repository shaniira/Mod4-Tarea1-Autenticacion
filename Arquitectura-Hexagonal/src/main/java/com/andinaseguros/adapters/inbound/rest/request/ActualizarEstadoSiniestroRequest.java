package com.andinaseguros.adapters.inbound.rest.request;

import com.andinaseguros.core.domain.enums.EstadoSiniestro;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoSiniestroRequest(@NotNull EstadoSiniestro estado) {}
