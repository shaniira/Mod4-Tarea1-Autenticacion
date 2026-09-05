package com.andinaseguros.infrastructure.persistence.adapter;

import com.andinaseguros.domain.enums.*;
import com.andinaseguros.domain.model.TablaTarifaria;
import com.andinaseguros.domain.repository.TablaTarifariaRepository;
import com.andinaseguros.infrastructure.persistence.mapper.TablaTarifariaMongoMapper;
import com.andinaseguros.infrastructure.persistence.repository.SpringDataTablaTarifariaMongoRepository;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class TablaTarifariaMongoRepositoryAdapter implements TablaTarifariaRepository {
    private final SpringDataTablaTarifariaMongoRepository repo;
    private final TablaTarifariaMongoMapper mapper;
    private final FactorRiesgoMongoRepositoryAdapter factores;

    public TablaTarifariaMongoRepositoryAdapter(
            SpringDataTablaTarifariaMongoRepository r,
            TablaTarifariaMongoMapper m,
            FactorRiesgoMongoRepositoryAdapter f) {
        repo = r;
        mapper = m;
        factores = f;
    }

    public TablaTarifaria guardar(TablaTarifaria x) {
        factores.guardarTodos(x.getFactores());
        return mapper.toDomain(repo.save(mapper.toDocument(x)));
    }

    public Optional<TablaTarifaria> buscarPorId(UUID id) {
        return repo.findById(id.toString()).map(mapper::toDomain);
    }

    public Optional<TablaTarifaria> buscarVigente(TipoVehiculo tv, TipoUso tu, LocalDate f) {
        return repo
                .findByTipoVehiculoAndTipoUsoAndEstadoAndInicioVigenciaLessThanEqualAndFinVigenciaGreaterThanEqualOrderByVersionDesc(
                        tv, tu, EstadoTablaTarifaria.VIGENTE, f, f)
                .stream()
                .findFirst()
                .map(mapper::toDomain);
    }

    public List<TablaTarifaria> listar() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }
}
