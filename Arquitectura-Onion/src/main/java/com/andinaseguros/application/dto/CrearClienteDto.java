package com.andinaseguros.application.dto;

import java.time.LocalDate;

public record CrearClienteDto(
        String tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String correo,
        String telefono) {}
