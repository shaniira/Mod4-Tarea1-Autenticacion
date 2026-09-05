package com.andinaseguros.application.gateway.contact;

import java.util.UUID;

public record ClienteContactData(UUID clienteId, String nombre, String correo, String telefono) {}
