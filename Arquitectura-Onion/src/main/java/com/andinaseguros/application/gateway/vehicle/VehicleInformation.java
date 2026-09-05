package com.andinaseguros.application.gateway.vehicle;

public record VehicleInformation(
        String placa,
        String marca,
        String modelo,
        Integer anio,
        String tipoUso,
        String descripcion,
        String vin,
        String fuente) {}
