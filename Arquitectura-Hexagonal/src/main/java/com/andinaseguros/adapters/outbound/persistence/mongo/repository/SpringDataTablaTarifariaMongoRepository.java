package com.andinaseguros.adapters.outbound.persistence.mongo.repository;

import com.andinaseguros.core.domain.enums.*;
import com.andinaseguros.adapters.outbound.persistence.mongo.document.TablaTarifariaDocument;
import java.time.LocalDate;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataTablaTarifariaMongoRepository
        extends MongoRepository<TablaTarifariaDocument, String> {
    List<TablaTarifariaDocument>
            findByTipoVehiculoAndTipoUsoAndEstadoAndInicioVigenciaLessThanEqualAndFinVigenciaGreaterThanEqualOrderByVersionDesc(
                    TipoVehiculo tipoVehiculo,
                    TipoUso tipoUso,
                    EstadoTablaTarifaria estado,
                    LocalDate inicio,
                    LocalDate fin);
}
