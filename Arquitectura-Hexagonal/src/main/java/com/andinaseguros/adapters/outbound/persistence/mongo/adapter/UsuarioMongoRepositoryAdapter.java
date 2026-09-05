package com.andinaseguros.adapters.outbound.persistence.mongo.adapter;

import com.andinaseguros.core.domain.model.Usuario;
import com.andinaseguros.core.ports.out.persistence.UsuarioRepositoryPort;
import com.andinaseguros.adapters.outbound.persistence.mongo.mapper.UsuarioMongoMapper;
import com.andinaseguros.adapters.outbound.persistence.mongo.repository.SpringDataUsuarioMongoRepository;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("!memory")
public class UsuarioMongoRepositoryAdapter implements UsuarioRepositoryPort {
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
