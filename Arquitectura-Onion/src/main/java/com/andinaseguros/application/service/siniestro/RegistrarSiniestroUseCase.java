package com.andinaseguros.application.service.siniestro;

import static com.andinaseguros.application.mapper.SiniestroResponseMapper.toResponse;

import com.andinaseguros.application.dto.RegistrarSiniestroDto;
import com.andinaseguros.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.domain.enums.EstadoPoliza;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.exception.ReglaNegocioException;
import com.andinaseguros.domain.model.Poliza;
import com.andinaseguros.domain.model.Siniestro;
import com.andinaseguros.domain.repository.PolizaRepository;
import com.andinaseguros.domain.repository.SiniestroRepository;
import com.andinaseguros.domain.valueobject.Dinero;
import java.util.UUID;

public class RegistrarSiniestroUseCase {

    private final PolizaRepository polizaRepository;
    private final SiniestroRepository siniestroRepository;

    public RegistrarSiniestroUseCase(
            PolizaRepository polizaRepository, SiniestroRepository siniestroRepository) {
        this.polizaRepository = polizaRepository;
        this.siniestroRepository = siniestroRepository;
    }

    public SiniestroResponse execute(RegistrarSiniestroDto solicitud) {
        Poliza poliza =
                polizaRepository
                        .buscarPorId(solicitud.polizaId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Póliza"));

        validarPolizaVigente(poliza);

        Siniestro siniestro =
                new Siniestro(
                        UUID.randomUUID(),
                        poliza.getId(),
                        solicitud.fecha(),
                        solicitud.tipo(),
                        Dinero.soles(solicitud.montoEstimado()),
                        solicitud.responsabilidadAsegurado(),
                        solicitud.gravedad(),
                        solicitud.estado());

        Siniestro siniestroGuardado = siniestroRepository.guardar(siniestro);

        return toResponse(siniestroGuardado);
    }

    private void validarPolizaVigente(Poliza poliza) {
        if (poliza.getEstado() != EstadoPoliza.VIGENTE) {
            throw new ReglaNegocioException(
                    "POLIZA_NO_VIGENTE", "Solo se registran siniestros en pólizas vigentes");
        }
    }
}
