package com.andinaseguros.application.service.tarifa;

import static com.andinaseguros.application.mapper.TablaTarifariaResponseMapper.toResponse;

import com.andinaseguros.application.dto.CrearFactorDto;
import com.andinaseguros.application.dto.CrearTablaDto;
import com.andinaseguros.application.dto.Responses.TablaResponse;
import com.andinaseguros.domain.model.FactorRiesgo;
import com.andinaseguros.domain.model.TablaTarifaria;
import com.andinaseguros.domain.repository.TablaTarifariaRepository;
import com.andinaseguros.domain.valueobject.Dinero;
import com.andinaseguros.domain.valueobject.PeriodoVigencia;
import java.util.List;
import java.util.UUID;

public class CrearTablaTarifariaUseCase {

    private final TablaTarifariaRepository tablaTarifariaRepository;

    public CrearTablaTarifariaUseCase(TablaTarifariaRepository tablaTarifariaRepository) {
        this.tablaTarifariaRepository = tablaTarifariaRepository;
    }

    public TablaResponse execute(CrearTablaDto solicitud) {
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

    private List<FactorRiesgo> crearFactores(List<CrearFactorDto> solicitudes) {
        List<CrearFactorDto> factores = solicitudes == null ? List.of() : solicitudes;

        return factores.stream().map(this::crearFactor).toList();
    }

    private FactorRiesgo crearFactor(CrearFactorDto solicitud) {
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
