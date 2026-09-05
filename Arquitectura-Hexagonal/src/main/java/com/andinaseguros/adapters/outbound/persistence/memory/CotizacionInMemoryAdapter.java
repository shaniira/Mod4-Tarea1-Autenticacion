package com.andinaseguros.adapters.outbound.persistence.memory;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.domain.enums.EstadoCotizacion; import com.andinaseguros.core.domain.model.Cotizacion;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository
@Profile("memory")
public class CotizacionInMemoryAdapter implements CotizacionRepositoryPort {
 private final Map<UUID,Cotizacion> data=new ConcurrentHashMap<>();
 public Cotizacion guardar(Cotizacion x){data.put(x.getId(),x);return x;}
 public Optional<Cotizacion> buscarPorId(UUID id){return Optional.ofNullable(data.get(id));}
 public List<Cotizacion> listar(){return List.copyOf(data.values());}
 public List<Cotizacion> listarPorEstado(EstadoCotizacion e){return data.values().stream().filter(x->x.getEstado()==e).toList();}
 public List<Cotizacion> listarPorCliente(UUID id){return data.values().stream().filter(x->x.getClienteId().equals(id)).toList();}
}