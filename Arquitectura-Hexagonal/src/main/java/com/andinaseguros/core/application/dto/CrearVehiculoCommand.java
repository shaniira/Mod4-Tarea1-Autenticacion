package com.andinaseguros.core.application.dto;

import com.andinaseguros.core.domain.enums.TipoUso;
import com.andinaseguros.core.domain.enums.TipoVehiculo;
import java.util.UUID;

public record CrearVehiculoCommand(
        UUID clienteId,
        String placa,
        String marca,
        String modelo,
        int anioFabricacion,
        TipoVehiculo tipo,
        TipoUso uso,
        String zonaCirculacion) {}
