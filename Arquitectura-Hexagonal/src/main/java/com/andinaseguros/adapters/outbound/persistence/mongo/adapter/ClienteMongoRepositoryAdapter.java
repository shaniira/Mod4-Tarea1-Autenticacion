package com.andinaseguros.adapters.outbound.persistence.mongo.adapter;

import com.andinaseguros.core.domain.model.Cliente;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import com.andinaseguros.adapters.outbound.persistence.mongo.mapper.ClienteMongoMapper;
import com.andinaseguros.adapters.outbound.persistence.mongo.repository.SpringDataClienteMongoRepository;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("!memory")
public class ClienteMongoRepositoryAdapter implements ClienteRepositoryPort {
    private final SpringDataClienteMongoRepository repo;
    private final ClienteMongoMapper mapper;

    public ClienteMongoRepositoryAdapter(SpringDataClienteMongoRepository r, ClienteMongoMapper m) {
        repo = r;
        mapper = m;
    }

    public Cliente guardar(Cliente x) {
        return mapper.toDomain(repo.save(mapper.toDocument(x)));
    }

    public Optional<Cliente> buscarPorId(UUID id) {
        return repo.findById(id.toString()).map(mapper::toDomain);
    }

    public Optional<Cliente> buscarPorDocumento(String x) {
        return repo.findByNumeroDocumento(x).map(mapper::toDomain);
    }

    public List<Cliente> listar() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }
}
