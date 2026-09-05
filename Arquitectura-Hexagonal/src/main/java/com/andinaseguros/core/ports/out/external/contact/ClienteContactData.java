package com.andinaseguros.core.ports.out.external.contact;

import java.util.UUID;

public record ClienteContactData(UUID clienteId, String nombre, String correo, String telefono) {}
