package com.andinaseguros.infrastructure.persistence.repository;

import com.andinaseguros.domain.enums.*;
import com.andinaseguros.infrastructure.persistence.document.TablaTarifariaDocument;
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
