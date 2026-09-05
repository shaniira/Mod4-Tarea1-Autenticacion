package com.andinaseguros.adapters.outbound.persistence.mongo.repository;

import com.andinaseguros.adapters.outbound.persistence.mongo.document.PolizaDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataPolizaMongoRepository extends MongoRepository<PolizaDocument, String> {
    Optional<PolizaDocument> findByNumero(String numero);

    Optional<PolizaDocument> findByCotizacionId(String cotizacionId);

    List<PolizaDocument> findByClienteId(String clienteId);
}
