package com.andinaseguros.core.ports.in.tarifa;

import com.andinaseguros.core.application.dto.CrearFactorCommand;
import com.andinaseguros.core.application.dto.CrearTablaCommand;
import com.andinaseguros.core.application.dto.Responses.TablaResponse;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import com.andinaseguros.core.domain.model.FactorRiesgo;
import com.andinaseguros.core.domain.model.TablaTarifaria;
import com.andinaseguros.core.domain.valueobject.Dinero;
import com.andinaseguros.core.domain.valueobject.PeriodoVigencia;
import java.util.List;
import java.util.UUID;

public interface CrearTablaTarifariaUseCase {
    TablaResponse execute(CrearTablaCommand solicitud);
}
