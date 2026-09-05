package com.andinaseguros.application.service.poliza;

import static com.andinaseguros.application.mapper.PolizaResponseMapper.toResponse;

import com.andinaseguros.application.dto.EmitirPolizaDto;
import com.andinaseguros.application.dto.Responses.PolizaResponse;
import com.andinaseguros.application.service.EmitirPolizaInputPort;
import com.andinaseguros.domain.enums.EstadoCotizacion;
import com.andinaseguros.domain.enums.EstadoPoliza;
import com.andinaseguros.domain.event.PolizaEmitidaEvent;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.exception.ReglaNegocioException;
import com.andinaseguros.domain.model.Cotizacion;
import com.andinaseguros.domain.model.Poliza;
import com.andinaseguros.domain.repository.CotizacionRepository;
import com.andinaseguros.domain.repository.PolizaRepository;
import com.andinaseguros.domain.valueobject.PeriodoVigencia;
import com.andinaseguros.application.gateway.event.DomainEventPublisherPort;
import com.andinaseguros.application.gateway.id.IdGeneratorPort;
import com.andinaseguros.application.gateway.time.ClockPort;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneOffset;

public class EmitirPolizaUseCase implements EmitirPolizaInputPort {

    private final CotizacionRepository cotizacionRepository;
    private final PolizaRepository polizaRepository;
    private final DomainEventPublisherPort eventPublisher;
    private final ClockPort clock;
    private final IdGeneratorPort ids;

    public EmitirPolizaUseCase(
            CotizacionRepository cotizacionRepository,
            PolizaRepository polizaRepository,
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
    public PolizaResponse execute(EmitirPolizaDto solicitud) {
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
