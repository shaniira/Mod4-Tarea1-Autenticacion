package com.andinaseguros.adapters.outbound.persistence.mongo.repository;

import com.andinaseguros.adapters.outbound.persistence.mongo.document.VehiculoDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataVehiculoMongoRepository
        extends MongoRepository<VehiculoDocument, String> {
    Optional<VehiculoDocument> findByPlaca(String placa);

    List<VehiculoDocument> findByClienteId(String clienteId);
}
