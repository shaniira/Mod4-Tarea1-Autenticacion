package com.andinaseguros.adapters.outbound.persistence.mongo.repository;

import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.adapters.outbound.persistence.mongo.document.CotizacionDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataCotizacionMongoRepository
        extends MongoRepository<CotizacionDocument, String> {
    List<CotizacionDocument> findByClienteId(String clienteId);

    List<CotizacionDocument> findByEstadoOrderByFechaCreacionDesc(EstadoCotizacion estado);
}
