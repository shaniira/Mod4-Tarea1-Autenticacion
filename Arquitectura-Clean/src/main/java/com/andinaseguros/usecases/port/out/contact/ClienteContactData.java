package com.andinaseguros.usecases.port.out.contact;

import java.util.UUID;

public record ClienteContactData(UUID clienteId, String nombre, String correo, String telefono) {}
