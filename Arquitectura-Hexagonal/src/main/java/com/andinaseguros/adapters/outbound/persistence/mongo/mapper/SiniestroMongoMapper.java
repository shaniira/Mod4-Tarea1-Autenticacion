package com.andinaseguros.adapters.outbound.persistence.mongo.mapper;

import com.andinaseguros.core.domain.model.Siniestro;
import com.andinaseguros.core.domain.valueobject.Dinero;
import com.andinaseguros.adapters.outbound.persistence.mongo.document.SiniestroDocument;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SiniestroMongoMapper {
    public SiniestroDocument toDocument(Siniestro x) {
        var d = new SiniestroDocument();
        d.id = x.id().toString();
        d.polizaId = x.polizaId().toString();
        d.fecha = x.fecha();
        d.tipo = x.tipo();
        d.montoEstimado = x.montoEstimado().valor();
        d.moneda = x.montoEstimado().moneda();
        d.responsabilidadAsegurado = x.responsabilidadAsegurado();
        d.gravedad = x.gravedad();
        d.estado = x.estado();
        return d;
    }

    public Siniestro toDomain(SiniestroDocument d) {
        return new Siniestro(
                UUID.fromString(d.id),
                UUID.fromString(d.polizaId),
                d.fecha,
                d.tipo,
                new Dinero(d.montoEstimado, d.moneda),
                d.responsabilidadAsegurado,
                d.gravedad,
                d.estado);
    }
}
