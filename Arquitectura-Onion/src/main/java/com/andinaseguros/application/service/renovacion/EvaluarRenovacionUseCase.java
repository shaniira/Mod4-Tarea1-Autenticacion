package com.andinaseguros.application.service.renovacion;

import static com.andinaseguros.application.mapper.RenovacionResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.RenovacionResponse;
import com.andinaseguros.domain.enums.EstadoPoliza;
import com.andinaseguros.domain.enums.EstadoRenovacion;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.exception.ReglaNegocioException;
import com.andinaseguros.domain.model.Poliza;
import com.andinaseguros.domain.model.PropuestaRenovacion;
import com.andinaseguros.domain.model.Siniestro;
import com.andinaseguros.domain.repository.PolizaRepository;
import com.andinaseguros.domain.repository.RenovacionRepository;
import com.andinaseguros.domain.repository.SiniestroRepository;
import com.andinaseguros.domain.service.CalculadorPrimaRenovacion;
import com.andinaseguros.domain.service.EvaluadorRenovacion;
import com.andinaseguros.domain.service.PoliticaVariacionPrima;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EvaluarRenovacionUseCase {

    private final PolizaRepository polizaRepository;
    private final SiniestroRepository siniestroRepository;
    private final RenovacionRepository renovacionRepository;
    private final EvaluadorRenovacion evaluadorRenovacion;
    private final CalculadorPrimaRenovacion calculadorPrimaRenovacion;
    private final PoliticaVariacionPrima politicaVariacionPrima;

    public EvaluarRenovacionUseCase(
            PolizaRepository polizaRepository,
            SiniestroRepository siniestroRepository,
            RenovacionRepository renovacionRepository,
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
