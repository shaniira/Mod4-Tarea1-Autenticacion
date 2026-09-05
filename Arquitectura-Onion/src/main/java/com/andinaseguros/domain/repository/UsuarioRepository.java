package com.andinaseguros.domain.repository;

import com.andinaseguros.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorUsername(String username);
}
