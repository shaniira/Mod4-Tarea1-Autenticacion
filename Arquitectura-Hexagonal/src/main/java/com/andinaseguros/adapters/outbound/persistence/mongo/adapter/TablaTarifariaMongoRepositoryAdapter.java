package com.andinaseguros.adapters.outbound.persistence.mongo.adapter;

import com.andinaseguros.core.domain.enums.*;
import com.andinaseguros.core.domain.model.TablaTarifaria;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import com.andinaseguros.adapters.outbound.persistence.mongo.mapper.TablaTarifariaMongoMapper;
import com.andinaseguros.adapters.outbound.persistence.mongo.repository.SpringDataTablaTarifariaMongoRepository;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("!memory")
public class TablaTarifariaMongoRepositoryAdapter implements TablaTarifariaRepositoryPort {
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
