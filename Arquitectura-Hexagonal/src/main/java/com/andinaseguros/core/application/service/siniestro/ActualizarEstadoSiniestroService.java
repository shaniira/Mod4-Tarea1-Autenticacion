package com.andinaseguros.core.application.service.siniestro;

import com.andinaseguros.core.ports.in.siniestro.ActualizarEstadoSiniestroUseCase;

import static com.andinaseguros.core.application.mapper.SiniestroResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.core.domain.enums.EstadoSiniestro;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Siniestro;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import java.util.UUID;

public class ActualizarEstadoSiniestroService implements ActualizarEstadoSiniestroUseCase {

    private final PolizaRepositoryPort polizaRepository;
    private final SiniestroRepositoryPort siniestroRepository;

    public ActualizarEstadoSiniestroService(
            PolizaRepositoryPort polizaRepository, SiniestroRepositoryPort siniestroRepository) {
        this.polizaRepository = polizaRepository;
        this.siniestroRepository = siniestroRepository;
    }

    public SiniestroResponse execute(UUID polizaId, UUID siniestroId, EstadoSiniestro nuevoEstado) {
        polizaRepository
                .buscarPorId(polizaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Póliza"));

        Siniestro siniestro =
                siniestroRepository
                        .buscarPorId(siniestroId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Siniestro"));

        validarPertenencia(siniestro, polizaId);

        siniestro.cambiarEstado(nuevoEstado);

        Siniestro siniestroGuardado = siniestroRepository.guardar(siniestro);

        return toResponse(siniestroGuardado);
    }

    private void validarPertenencia(Siniestro siniestro, UUID polizaId) {
        if (!siniestro.polizaId().equals(polizaId)) {
            throw new ReglaNegocioException(
                    "SINIESTRO_NO_PERTENECE", "El siniestro no pertenece a la póliza indicada");
        }
    }
}
