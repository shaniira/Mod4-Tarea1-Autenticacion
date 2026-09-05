package com.andinaseguros.adapters.outbound.persistence.mongo.adapter;

import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.adapters.outbound.persistence.mongo.mapper.PolizaMongoMapper;
import com.andinaseguros.adapters.outbound.persistence.mongo.repository.SpringDataPolizaMongoRepository;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("!memory")
public class PolizaMongoRepositoryAdapter implements PolizaRepositoryPort {
    private final SpringDataPolizaMongoRepository repo;
    private final PolizaMongoMapper mapper;

    public PolizaMongoRepositoryAdapter(SpringDataPolizaMongoRepository r, PolizaMongoMapper m) {
        repo = r;
        mapper = m;
    }

    public Poliza guardar(Poliza x) {
        return mapper.toDomain(repo.save(mapper.toDocument(x)));
    }

    public Optional<Poliza> buscarPorId(UUID id) {
        return repo.findById(id.toString()).map(mapper::toDomain);
    }

    public Optional<Poliza> buscarPorNumero(String x) {
        return repo.findByNumero(x).map(mapper::toDomain);
    }

    public Optional<Poliza> buscarPorCotizacionId(UUID id) {
        return repo.findByCotizacionId(id.toString()).map(mapper::toDomain);
    }

    public List<Poliza> listar() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }

    public List<Poliza> listarPorCliente(UUID id) {
        return repo.findByClienteId(id.toString()).stream().map(mapper::toDomain).toList();
    }
}
