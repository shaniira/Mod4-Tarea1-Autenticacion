package com.andinaseguros.adapters.outbound.persistence.mongo.adapter;

import com.andinaseguros.core.domain.model.Siniestro;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import com.andinaseguros.adapters.outbound.persistence.mongo.mapper.SiniestroMongoMapper;
import com.andinaseguros.adapters.outbound.persistence.mongo.repository.SpringDataSiniestroMongoRepository;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("!memory")
public class SiniestroMongoRepositoryAdapter implements SiniestroRepositoryPort {
    private final SpringDataSiniestroMongoRepository repo;
    private final SiniestroMongoMapper mapper;

    public SiniestroMongoRepositoryAdapter(
            SpringDataSiniestroMongoRepository r, SiniestroMongoMapper m) {
        repo = r;
        mapper = m;
    }

    public Siniestro guardar(Siniestro x) {
        return mapper.toDomain(repo.save(mapper.toDocument(x)));
    }

    public Optional<Siniestro> buscarPorId(UUID id) {
        return repo.findById(id.toString()).map(mapper::toDomain);
    }

    public List<Siniestro> listarPorPoliza(UUID id) {
        return repo.findByPolizaId(id.toString()).stream().map(mapper::toDomain).toList();
    }
}
