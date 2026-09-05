package com.andinaseguros.application.service.siniestro;

import static com.andinaseguros.application.mapper.SiniestroResponseMapper.toResponse;

import com.andinaseguros.application.dto.Responses.SiniestroResponse;
import com.andinaseguros.domain.enums.EstadoSiniestro;
import com.andinaseguros.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.domain.exception.ReglaNegocioException;
import com.andinaseguros.domain.model.Siniestro;
import com.andinaseguros.domain.repository.PolizaRepository;
import com.andinaseguros.domain.repository.SiniestroRepository;
import java.util.UUID;

public class ActualizarEstadoSiniestroUseCase {

    private final PolizaRepository polizaRepository;
    private final SiniestroRepository siniestroRepository;

    public ActualizarEstadoSiniestroUseCase(
            PolizaRepository polizaRepository, SiniestroRepository siniestroRepository) {
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
