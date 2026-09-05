package com.andinaseguros.core.application.service.renovacion;

import com.andinaseguros.core.ports.in.renovacion.GenerarPolizaRenovadaUseCase;

import static com.andinaseguros.core.application.mapper.PolizaResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import com.andinaseguros.core.domain.enums.EstadoRenovacion;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import com.andinaseguros.core.domain.valueobject.PeriodoVigencia;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

public class GenerarPolizaRenovadaService implements GenerarPolizaRenovadaUseCase {

    private final RenovacionRepositoryPort renovacionRepository;
    private final PolizaRepositoryPort polizaRepository;

    public GenerarPolizaRenovadaService(
            RenovacionRepositoryPort renovacionRepository, PolizaRepositoryPort polizaRepository) {
        this.renovacionRepository = renovacionRepository;
        this.polizaRepository = polizaRepository;
    }

    public PolizaResponse execute(UUID renovacionId) {
        PropuestaRenovacion propuesta = obtenerPropuesta(renovacionId);

        validarPropuestaAprobada(propuesta);

        if (propuesta.polizaRenovadaId() != null) {
            return obtenerPolizaExistente(propuesta.polizaRenovadaId());
        }

        Poliza polizaOrigen = obtenerPolizaOrigen(propuesta.polizaOrigenId());
        Poliza polizaRenovada = crearPolizaRenovada(propuesta, polizaOrigen);

        polizaRepository.guardar(polizaRenovada);

        polizaOrigen.marcarRenovada();
        polizaRepository.guardar(polizaOrigen);

        propuesta.vincularPolizaRenovada(polizaRenovada.getId());
        renovacionRepository.guardar(propuesta);

        return toResponse(polizaRenovada);
    }

    private PropuestaRenovacion obtenerPropuesta(UUID renovacionId) {
        return renovacionRepository
                .buscarPorId(renovacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Renovación"));
    }

    private void validarPropuestaAprobada(PropuestaRenovacion propuesta) {
        if (propuesta.estado() != EstadoRenovacion.ACEPTADA) {
            throw new ReglaNegocioException(
                    "RENOVACION_NO_APROBADA",
                    "Debe aprobar la propuesta antes de generar la póliza");
        }
    }

    private PolizaResponse obtenerPolizaExistente(UUID polizaId) {
        Poliza poliza =
                polizaRepository
                        .buscarPorId(polizaId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Póliza"));

        return toResponse(poliza);
    }

    private Poliza obtenerPolizaOrigen(UUID polizaId) {
        return polizaRepository
                .buscarPorId(polizaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Póliza de origen"));
    }

    private Poliza crearPolizaRenovada(PropuestaRenovacion propuesta, Poliza polizaOrigen) {
        LocalDate inicioVigencia = polizaOrigen.getVigencia().fin().plusDays(1);
        String numeroPoliza =
                "POL-REN-"
                        + Year.from(inicioVigencia).getValue()
                        + "-"
                        + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return new Poliza(
                UUID.randomUUID(),
                numeroPoliza,
                null,
                polizaOrigen.getClienteId(),
                polizaOrigen.getVehiculoId(),
                propuesta.nuevaPrima(),
                new PeriodoVigencia(inicioVigencia, inicioVigencia.plusYears(1)),
                EstadoPoliza.VIGENTE,
                propuesta.id());
    }
}
