package com.andinaseguros.adapters.outbound.persistence.mongo.document;

import com.andinaseguros.core.domain.enums.RolUsuario;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usuarios")
public class UsuarioDocument {
    @Id public String id;

    @Indexed(unique = true)
    public String username;

    @Indexed(unique = true, sparse = true)
    public String email;

    public String passwordHash;
    public RolUsuario rol;
    public boolean activo;
}
