package com.andinaseguros.core.application.service.poliza;

import com.andinaseguros.core.ports.in.poliza.EmitirPolizaUseCase;

import static com.andinaseguros.core.application.mapper.PolizaResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.EmitirPolizaCommand;
import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import com.andinaseguros.core.domain.event.PolizaEmitidaEvent;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Cotizacion;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.domain.valueobject.PeriodoVigencia;
import com.andinaseguros.core.ports.out.event.DomainEventPublisherPort;
import com.andinaseguros.core.ports.out.id.IdGeneratorPort;
import com.andinaseguros.core.ports.out.clock.ClockPort;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneOffset;

public class EmitirPolizaService implements EmitirPolizaUseCase {

    private final CotizacionRepositoryPort cotizacionRepository;
    private final PolizaRepositoryPort polizaRepository;
    private final DomainEventPublisherPort eventPublisher;
    private final ClockPort clock;
    private final IdGeneratorPort ids;

    public EmitirPolizaService(
            CotizacionRepositoryPort cotizacionRepository,
            PolizaRepositoryPort polizaRepository,
            DomainEventPublisherPort eventPublisher,
            ClockPort clock,
            IdGeneratorPort ids) {
        this.cotizacionRepository = cotizacionRepository;
        this.polizaRepository = polizaRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
        this.ids = ids;
    }

    @Override
    public PolizaResponse execute(EmitirPolizaCommand solicitud) {
        Cotizacion cotizacion =
                cotizacionRepository
                        .buscarPorId(solicitud.cotizacionId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Cotización"));

        validarCotizacionAceptada(cotizacion);
        if (polizaRepository.buscarPorCotizacionId(cotizacion.getId()).isPresent()) {
            throw new ReglaNegocioException(
                    "COTIZACION_YA_EMITIDA", "La cotización ya tiene una póliza emitida");
        }
        cotizacion.marcarEmitida();

        LocalDate inicioVigencia = solicitud.inicioVigencia();
        String numeroPoliza =
                "POL-"
                        + Year.from(clock.now().atZone(ZoneOffset.UTC)).getValue()
                        + "-"
                        + ids.generar().toString().substring(0, 8).toUpperCase();

        Poliza poliza =
                new Poliza(
                        ids.generar(),
                        numeroPoliza,
                        cotizacion.getId(),
                        cotizacion.getClienteId(),
                        cotizacion.getVehiculoId(),
                        cotizacion.getPrima(),
                        new PeriodoVigencia(inicioVigencia, inicioVigencia.plusYears(1)),
                        EstadoPoliza.VIGENTE);

        cotizacionRepository.guardar(cotizacion);
        Poliza polizaGuardada = polizaRepository.guardar(poliza);
        eventPublisher.publicar(
                new PolizaEmitidaEvent(
                        ids.generar(),
                        clock.now(),
                        polizaGuardada.getId(),
                        cotizacion.getId(),
                        cotizacion.getClienteId(),
                        polizaGuardada.getNumero()));

        return toResponse(polizaGuardada);
    }

    private void validarCotizacionAceptada(Cotizacion cotizacion) {
        if (cotizacion.getEstado() != EstadoCotizacion.ACEPTADA) {
            throw new ReglaNegocioException(
                    "COTIZACION_NO_ACEPTADA",
                    "La cotización debe estar aceptada antes de emitir la póliza");
        }
    }
}
