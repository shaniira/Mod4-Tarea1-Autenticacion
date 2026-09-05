package com.andinaseguros.adapters.outbound.persistence.mongo.document;

import com.andinaseguros.core.domain.enums.EstadoSiniestro;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("siniestros")
public class SiniestroDocument {
    @Id public String id;
    @Indexed public String polizaId;
    public LocalDate fecha;
    public String tipo;
    public BigDecimal montoEstimado;
    public String moneda;
    public boolean responsabilidadAsegurado;
    public String gravedad;
    public EstadoSiniestro estado;
}
