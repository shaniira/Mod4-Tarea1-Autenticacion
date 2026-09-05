package com.andinaseguros.domain.repository;

import com.andinaseguros.domain.model.Siniestro;
import java.util.*;

public interface SiniestroRepository {
    Siniestro guardar(Siniestro siniestro);

    Optional<Siniestro> buscarPorId(UUID id);

    List<Siniestro> listarPorPoliza(UUID polizaId);
}
