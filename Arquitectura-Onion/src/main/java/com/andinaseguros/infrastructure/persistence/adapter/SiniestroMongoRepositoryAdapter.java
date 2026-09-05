package com.andinaseguros.infrastructure.persistence.adapter;

import com.andinaseguros.domain.model.Siniestro;
import com.andinaseguros.domain.repository.SiniestroRepository;
import com.andinaseguros.infrastructure.persistence.mapper.SiniestroMongoMapper;
import com.andinaseguros.infrastructure.persistence.repository.SpringDataSiniestroMongoRepository;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class SiniestroMongoRepositoryAdapter implements SiniestroRepository {
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
