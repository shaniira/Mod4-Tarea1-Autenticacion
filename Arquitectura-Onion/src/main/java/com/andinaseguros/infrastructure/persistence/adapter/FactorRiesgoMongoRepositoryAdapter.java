package com.andinaseguros.infrastructure.persistence.adapter;

import com.andinaseguros.domain.model.FactorRiesgo;
import com.andinaseguros.infrastructure.persistence.mapper.FactorRiesgoMongoMapper;
import com.andinaseguros.infrastructure.persistence.repository.SpringDataFactorRiesgoMongoRepository;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
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
