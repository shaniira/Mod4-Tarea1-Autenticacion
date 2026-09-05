package com.andinaseguros.infrastructure.persistence.repository;

import com.andinaseguros.domain.enums.EstadoCotizacion;
import com.andinaseguros.infrastructure.persistence.document.CotizacionDocument;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataCotizacionMongoRepository
        extends MongoRepository<CotizacionDocument, String> {
    List<CotizacionDocument> findByClienteId(String clienteId);

    List<CotizacionDocument> findByEstadoOrderByFechaCreacionDesc(EstadoCotizacion estado);
}
