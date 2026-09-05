package com.andinaseguros.infrastructure.persistence.repository;

import com.andinaseguros.infrastructure.persistence.document.PolizaDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataPolizaMongoRepository extends MongoRepository<PolizaDocument, String> {
    Optional<PolizaDocument> findByNumero(String numero);

    Optional<PolizaDocument> findByCotizacionId(String cotizacionId);

    List<PolizaDocument> findByClienteId(String clienteId);
}
