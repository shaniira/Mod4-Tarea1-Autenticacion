package com.andinaseguros.application.dto;

import com.andinaseguros.domain.enums.TipoUso;
import com.andinaseguros.domain.enums.TipoVehiculo;
import java.util.UUID;

public record CrearVehiculoDto(
        UUID clienteId,
        String placa,
        String marca,
        String modelo,
        int anioFabricacion,
        TipoVehiculo tipo,
        TipoUso uso,
        String zonaCirculacion) {}
