package com.andinaseguros.infrastructure.persistence.mapper;

import com.andinaseguros.domain.model.Usuario;
import com.andinaseguros.infrastructure.persistence.document.UsuarioDocument;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMongoMapper {
    public UsuarioDocument toDocument(Usuario x) {
        var d = new UsuarioDocument();
        d.id = x.getId().toString();
        d.username = x.getUsername();
        d.email = x.getUsername().contains("@") ? x.getUsername() : null;
        d.passwordHash = x.getPasswordHash();
        d.rol = x.getRol();
        d.activo = x.isActivo();
        return d;
    }

    public Usuario toDomain(UsuarioDocument d) {
        return new Usuario(UUID.fromString(d.id), d.username, d.passwordHash, d.rol, d.activo);
    }
}
