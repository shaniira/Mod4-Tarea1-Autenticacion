package com.andinaseguros.core.application.dto;

import java.time.LocalDate;

public record CrearClienteCommand(
        String tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String correo,
        String telefono) {}
