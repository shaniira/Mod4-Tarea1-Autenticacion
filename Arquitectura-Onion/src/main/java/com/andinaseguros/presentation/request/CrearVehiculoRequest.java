package com.andinaseguros.presentation.request;

import com.andinaseguros.domain.enums.TipoUso;
import com.andinaseguros.domain.enums.TipoVehiculo;
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
