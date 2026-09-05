package com.andinaseguros.adapters.outbound.persistence.memory;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort; import com.andinaseguros.core.domain.model.PropuestaRenovacion;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository
@Profile("memory")
public class RenovacionInMemoryAdapter implements RenovacionRepositoryPort {
 private final Map<UUID,PropuestaRenovacion> data=new ConcurrentHashMap<>();
 public PropuestaRenovacion guardar(PropuestaRenovacion x){data.put(x.id(),x);return x;}
 public Optional<PropuestaRenovacion> buscarPorId(UUID id){return Optional.ofNullable(data.get(id));}
 public List<PropuestaRenovacion> listar(){return List.copyOf(data.values());}
 public List<PropuestaRenovacion> listarPorPoliza(UUID id){return data.values().stream().filter(x->x.polizaOrigenId().equals(id)).toList();}
}