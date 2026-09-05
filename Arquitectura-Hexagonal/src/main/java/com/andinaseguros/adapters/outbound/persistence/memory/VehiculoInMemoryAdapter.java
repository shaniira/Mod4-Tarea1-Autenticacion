package com.andinaseguros.adapters.outbound.persistence.memory;
import com.andinaseguros.core.ports.out.persistence.VehiculoRepositoryPort; import com.andinaseguros.core.domain.model.Vehiculo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository
@Profile("memory")
public class VehiculoInMemoryAdapter implements VehiculoRepositoryPort {
 private final Map<UUID,Vehiculo> data=new ConcurrentHashMap<>();
 public Vehiculo guardar(Vehiculo x){data.put(x.getId(),x);return x;}
 public Optional<Vehiculo> buscarPorId(UUID id){return Optional.ofNullable(data.get(id));}
 public Optional<Vehiculo> buscarPorPlaca(String p){return data.values().stream().filter(x->x.getPlaca().valor().equals(p)).findFirst();}
 public List<Vehiculo> listarPorCliente(UUID id){return data.values().stream().filter(x->x.getClienteId().equals(id)).toList();}
}