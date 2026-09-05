package com.andinaseguros.adapters.inbound.rest.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record EmitirPolizaRequest(
        @NotNull UUID cotizacionId, @NotNull LocalDate inicioVigencia) {}
