package com.andinaseguros.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.andinaseguros.domain.enums.EstadoSiniestro;
import com.andinaseguros.domain.exception.ReglaNegocioException;
import com.andinaseguros.domain.valueobject.Dinero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SiniestroTest {
    @Test
    void closesPendingClaimAndPreventsReopeningIt() {
        var claim =
                new Siniestro(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        LocalDate.now(),
                        "COLISION",
                        Dinero.soles(new BigDecimal("1000")),
                        true,
                        "LEVE",
                        EstadoSiniestro.EN_EVALUACION);
        claim.cambiarEstado(EstadoSiniestro.LIQUIDADO);
        assertEquals(EstadoSiniestro.LIQUIDADO, claim.estado());
        assertThrows(
                ReglaNegocioException.class, () -> claim.cambiarEstado(EstadoSiniestro.REPORTADO));
    }
}
