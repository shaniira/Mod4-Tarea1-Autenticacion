package com.andinaseguros.adapters.outbound.persistence.mongo.adapter;

import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import com.andinaseguros.adapters.outbound.persistence.mongo.mapper.RenovacionMongoMapper;
import com.andinaseguros.adapters.outbound.persistence.mongo.repository.SpringDataRenovacionMongoRepository;
import java.util.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("!memory")
public class RenovacionMongoRepositoryAdapter implements RenovacionRepositoryPort {
    private final SpringDataRenovacionMongoRepository repo;
    private final RenovacionMongoMapper mapper;

    public RenovacionMongoRepositoryAdapter(
            SpringDataRenovacionMongoRepository r, RenovacionMongoMapper m) {
        repo = r;
        mapper = m;
    }

    public PropuestaRenovacion guardar(PropuestaRenovacion x) {
        return mapper.toDomain(repo.save(mapper.toDocument(x)));
    }

    public Optional<PropuestaRenovacion> buscarPorId(UUID id) {
        return repo.findById(id.toString()).map(mapper::toDomain);
    }

    public List<PropuestaRenovacion> listar() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "creadaEn")).stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<PropuestaRenovacion> listarPorPoliza(UUID id) {
        return repo.findByPolizaOrigenIdOrderByCreadaEnDesc(id.toString()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
