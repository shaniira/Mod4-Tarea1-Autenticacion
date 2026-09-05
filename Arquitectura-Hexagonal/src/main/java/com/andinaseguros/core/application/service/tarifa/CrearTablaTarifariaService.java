package com.andinaseguros.core.application.service.tarifa;

import com.andinaseguros.core.ports.in.tarifa.CrearTablaTarifariaUseCase;

import static com.andinaseguros.core.application.mapper.TablaTarifariaResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.CrearFactorCommand;
import com.andinaseguros.core.application.dto.CrearTablaCommand;
import com.andinaseguros.core.application.dto.Responses.TablaResponse;
import com.andinaseguros.core.domain.model.FactorRiesgo;
import com.andinaseguros.core.domain.model.TablaTarifaria;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import com.andinaseguros.core.domain.valueobject.Dinero;
import com.andinaseguros.core.domain.valueobject.PeriodoVigencia;
import java.util.List;
import java.util.UUID;

public class CrearTablaTarifariaService implements CrearTablaTarifariaUseCase {

    private final TablaTarifariaRepositoryPort tablaTarifariaRepository;

    public CrearTablaTarifariaService(TablaTarifariaRepositoryPort tablaTarifariaRepository) {
        this.tablaTarifariaRepository = tablaTarifariaRepository;
    }

    public TablaResponse execute(CrearTablaCommand solicitud) {
        List<FactorRiesgo> factores = crearFactores(solicitud.factores());

        TablaTarifaria tablaTarifaria =
                new TablaTarifaria(
                        UUID.randomUUID(),
                        solicitud.codigo(),
                        solicitud.version(),
                        solicitud.tipoVehiculo(),
                        solicitud.tipoUso(),
                        Dinero.soles(solicitud.primaBase()),
                        Dinero.soles(solicitud.primaMinima()),
                        new PeriodoVigencia(solicitud.inicioVigencia(), solicitud.finVigencia()),
                        solicitud.codigoNotaTecnica(),
                        solicitud.estado(),
                        factores);

        TablaTarifaria tablaGuardada = tablaTarifariaRepository.guardar(tablaTarifaria);

        return toResponse(tablaGuardada);
    }

    private List<FactorRiesgo> crearFactores(List<CrearFactorCommand> solicitudes) {
        List<CrearFactorCommand> factores = solicitudes == null ? List.of() : solicitudes;

        return factores.stream().map(this::crearFactor).toList();
    }

    private FactorRiesgo crearFactor(CrearFactorCommand solicitud) {
        return new FactorRiesgo(
                UUID.randomUUID(),
                solicitud.codigo(),
                solicitud.nombre(),
                solicitud.tipoVariable(),
                solicitud.valorMinimo(),
                solicitud.valorMaximo(),
                solicitud.multiplicador(),
                solicitud.orden());
    }
}
