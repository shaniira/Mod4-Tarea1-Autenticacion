package com.andinaseguros.infrastructure.persistence.adapter;

import com.andinaseguros.domain.model.Usuario;
import com.andinaseguros.domain.repository.UsuarioRepository;
import com.andinaseguros.infrastructure.persistence.mapper.UsuarioMongoMapper;
import com.andinaseguros.infrastructure.persistence.repository.SpringDataUsuarioMongoRepository;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioMongoRepositoryAdapter implements UsuarioRepository {
    private final SpringDataUsuarioMongoRepository repo;
    private final UsuarioMongoMapper mapper;

    public UsuarioMongoRepositoryAdapter(SpringDataUsuarioMongoRepository r, UsuarioMongoMapper m) {
        repo = r;
        mapper = m;
    }

    public Usuario guardar(Usuario x) {
        return mapper.toDomain(repo.save(mapper.toDocument(x)));
    }

    public Optional<Usuario> buscarPorUsername(String x) {
        return repo.findByUsername(x).map(mapper::toDomain);
    }
}
