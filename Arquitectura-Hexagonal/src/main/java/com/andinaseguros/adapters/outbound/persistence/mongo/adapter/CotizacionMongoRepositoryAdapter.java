package com.andinaseguros.adapters.outbound.persistence.mongo.adapter;

import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.domain.model.Cotizacion;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.adapters.outbound.persistence.mongo.mapper.CotizacionMongoMapper;
import com.andinaseguros.adapters.outbound.persistence.mongo.repository.SpringDataCotizacionMongoRepository;
import java.util.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("!memory")
public class CotizacionMongoRepositoryAdapter implements CotizacionRepositoryPort {
    private final SpringDataCotizacionMongoRepository repo;
    private final CotizacionMongoMapper mapper;

    public CotizacionMongoRepositoryAdapter(
            SpringDataCotizacionMongoRepository r, CotizacionMongoMapper m) {
        repo = r;
        mapper = m;
    }

    public Cotizacion guardar(Cotizacion x) {
        return mapper.toDomain(repo.save(mapper.toDocument(x)));
    }

    public Optional<Cotizacion> buscarPorId(UUID id) {
        return repo.findById(id.toString()).map(mapper::toDomain);
    }

    public List<Cotizacion> listar() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "fechaCreacion")).stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Cotizacion> listarPorEstado(EstadoCotizacion e) {
        return repo.findByEstadoOrderByFechaCreacionDesc(e).stream().map(mapper::toDomain).toList();
    }

    public List<Cotizacion> listarPorCliente(UUID id) {
        return repo.findByClienteId(id.toString()).stream().map(mapper::toDomain).toList();
    }
}
