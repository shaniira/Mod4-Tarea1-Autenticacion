package com.andinaseguros.core.application.service.siniestro;

import com.andinaseguros.core.ports.in.siniestro.RegistrarSiniestroUseCase;

import static com.andinaseguros.core.application.mapper.SiniestroResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.RegistrarSiniestroCommand;
import com.andinaseguros.core.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.core.domain.enums.EstadoPoliza;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Poliza;
import com.andinaseguros.core.domain.model.Siniestro;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import com.andinaseguros.core.domain.valueobject.Dinero;
import java.util.UUID;

public class RegistrarSiniestroService implements RegistrarSiniestroUseCase {

    private final PolizaRepositoryPort polizaRepository;
    private final SiniestroRepositoryPort siniestroRepository;

    public RegistrarSiniestroService(
            PolizaRepositoryPort polizaRepository, SiniestroRepositoryPort siniestroRepository) {
        this.polizaRepository = polizaRepository;
        this.siniestroRepository = siniestroRepository;
    }

    public SiniestroResponse execute(RegistrarSiniestroCommand solicitud) {
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
