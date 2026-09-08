package com.andinaseguros.interfaceadapters.out.persistence.mongodb.mapper;

import com.andinaseguros.entities.model.Usuario;
import com.andinaseguros.interfaceadapters.out.persistence.mongodb.document.UsuarioDocument;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMongoMapper {
    public UsuarioDocument toDocument(Usuario x) {
        var d = new UsuarioDocument();
        d.id = x.getId().toString();
        d.username = x.getUsername();
        d.email = x.getEmail();
        d.passwordHash = x.getPasswordHash();
        d.googleSubject = x.getGoogleSubject();
        d.rol = x.getRol();
        d.activo = x.isActivo();
        return d;
    }

    public Usuario toDomain(UsuarioDocument d) {
        return new Usuario(
                UUID.fromString(d.id),
                d.username,
                d.email,
                d.passwordHash,
                d.googleSubject,
                d.rol,
                d.activo,
                null,
                false);
    }
}
