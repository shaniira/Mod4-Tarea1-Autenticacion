package com.andinaseguros.core.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EmitirPolizaCommand(UUID cotizacionId, LocalDate inicioVigencia) {}
