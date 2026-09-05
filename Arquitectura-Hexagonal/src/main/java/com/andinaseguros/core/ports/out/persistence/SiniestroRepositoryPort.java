package com.andinaseguros.core.ports.out.persistence;

import com.andinaseguros.core.domain.model.Siniestro;
import java.util.*;

public interface SiniestroRepositoryPort {
    Siniestro guardar(Siniestro siniestro);

    Optional<Siniestro> buscarPorId(UUID id);

    List<Siniestro> listarPorPoliza(UUID polizaId);
}
