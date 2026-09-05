package com.andinaseguros.core.application.service.renovacion;

import com.andinaseguros.core.ports.in.renovacion.EvaluarRenovacionUseCase;

import static com.andinaseguros.core.application.mapper.RenovacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import com.andinaseguros.core.domain.enums.EstadoRenovacion;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import com.andinaseguros.core.domain.model.Siniestro;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import com.andinaseguros.core.domain.service.CalculadorPrimaRenovacion;
import com.andinaseguros.core.domain.service.EvaluadorRenovacion;
import com.andinaseguros.core.domain.service.PoliticaVariacionPrima;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EvaluarRenovacionService implements EvaluarRenovacionUseCase {

    private final PolizaRepositoryPort polizaRepository;
    private final SiniestroRepositoryPort siniestroRepository;
    private final RenovacionRepositoryPort renovacionRepository;
    private final EvaluadorRenovacion evaluadorRenovacion;
    private final CalculadorPrimaRenovacion calculadorPrimaRenovacion;
    private final PoliticaVariacionPrima politicaVariacionPrima;

    public EvaluarRenovacionService(
            PolizaRepositoryPort polizaRepository,
            SiniestroRepositoryPort siniestroRepository,
            RenovacionRepositoryPort renovacionRepository,
            EvaluadorRenovacion evaluadorRenovacion,
            CalculadorPrimaRenovacion calculadorPrimaRenovacion,
            PoliticaVariacionPrima politicaVariacionPrima) {
        this.polizaRepository = polizaRepository;
        this.siniestroRepository = siniestroRepository;
        this.renovacionRepository = renovacionRepository;
        this.evaluadorRenovacion = evaluadorRenovacion;
        this.calculadorPrimaRenovacion = calculadorPrimaRenovacion;
        this.politicaVariacionPrima = politicaVariacionPrima;
    }

    public RenovacionResponse execute(UUID polizaId) {
        Poliza poliza =
                polizaRepository
                        .buscarPorId(polizaId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Póliza"));

        validarPolizaVigente(poliza);

        List<Siniestro> historialSiniestros = siniestroRepository.listarPorPoliza(polizaId);
        EstadoRenovacion decision = evaluadorRenovacion.evaluar(historialSiniestros);

        int siniestrosResponsables =
                (int)
                        historialSiniestros.stream()
                                .filter(Siniestro::responsabilidadAsegurado)
                                .count();

        var calculoPrima =
                calculadorPrimaRenovacion.calcular(poliza.getPrima(), siniestrosResponsables);

        String motivo = politicaVariacionPrima.explicar(decision, siniestrosResponsables);

        LocalDateTime fechaEvaluacion = LocalDateTime.now();

        PropuestaRenovacion propuesta =
                new PropuestaRenovacion(
                        UUID.randomUUID(),
                        poliza.getId(),
                        poliza.getPrima(),
                        calculoPrima.nuevaPrima(),
                        calculoPrima.porcentajeVariacion(),
                        historialSiniestros.size(),
                        decision,
                        motivo,
                        fechaEvaluacion,
                        fechaEvaluacion.plusDays(30),
                        null,
                        null);

        PropuestaRenovacion propuestaGuardada = renovacionRepository.guardar(propuesta);

        return toResponse(propuestaGuardada);
    }

    private void validarPolizaVigente(Poliza poliza) {
        if (poliza.getEstado() != EstadoPoliza.VIGENTE) {
            throw new ReglaNegocioException(
                    "POLIZA_NO_VIGENTE", "Solo se pueden evaluar pólizas vigentes");
        }
    }
}
