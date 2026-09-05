package com.andinaseguros.core.ports.in.cotizacion;

import com.andinaseguros.core.application.dto.CrearCotizacionCommand;
import com.andinaseguros.core.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.VehiculoRepositoryPort;
import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Cliente;
import com.andinaseguros.core.domain.model.Cotizacion;
import com.andinaseguros.core.domain.model.ResultadoTarificacion;
import com.andinaseguros.core.domain.model.TablaTarifaria;
import com.andinaseguros.core.domain.model.Vehiculo;
import com.andinaseguros.core.domain.service.MotorDeTarificacion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface CrearCotizacionUseCase {
    CotizacionResponse execute(CrearCotizacionCommand solicitud);
}
