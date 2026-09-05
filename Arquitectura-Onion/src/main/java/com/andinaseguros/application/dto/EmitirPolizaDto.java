package com.andinaseguros.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EmitirPolizaDto(UUID cotizacionId, LocalDate inicioVigencia) {}
