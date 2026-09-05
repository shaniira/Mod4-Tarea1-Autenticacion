package com.andinaseguros.adapters.outbound.persistence.mongo.adapter;

import com.andinaseguros.core.domain.model.FactorRiesgo;
import com.andinaseguros.adapters.outbound.persistence.mongo.mapper.FactorRiesgoMongoMapper;
import com.andinaseguros.adapters.outbound.persistence.mongo.repository.SpringDataFactorRiesgoMongoRepository;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("!memory")
public class FactorRiesgoMongoRepositoryAdapter {
    private final SpringDataFactorRiesgoMongoRepository repo;
    private final FactorRiesgoMongoMapper mapper;

    public FactorRiesgoMongoRepositoryAdapter(
            SpringDataFactorRiesgoMongoRepository r, FactorRiesgoMongoMapper m) {
        repo = r;
        mapper = m;
    }

    public List<FactorRiesgo> guardarTodos(List<FactorRiesgo> factores) {
        return repo.saveAll(factores.stream().map(mapper::toDocument).toList()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
