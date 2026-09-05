package com.andinaseguros.core.ports.out.persistence;

import com.andinaseguros.core.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorUsername(String username);
}
