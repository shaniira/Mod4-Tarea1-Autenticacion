package com.andinaseguros.adapters.inbound.rest.request;

import com.andinaseguros.core.domain.enums.TipoUso;
import com.andinaseguros.core.domain.enums.TipoVehiculo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CrearVehiculoRequest(
        UUID clienteId,
        @NotBlank String placa,
        @NotBlank String marca,
        @NotBlank String modelo,
        @Min(1980) int anioFabricacion,
        @NotNull TipoVehiculo tipo,
        @NotNull TipoUso uso,
        @NotBlank String zonaCirculacion) {}
