package com.andinaseguros.adapters.outbound.persistence.memory;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort; import com.andinaseguros.core.domain.model.Poliza;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository
@Profile("memory")
public class PolizaInMemoryAdapter implements PolizaRepositoryPort {
 private final Map<UUID,Poliza> data=new ConcurrentHashMap<>();
 public Poliza guardar(Poliza x){data.put(x.getId(),x);return x;}
 public Optional<Poliza> buscarPorId(UUID id){return Optional.ofNullable(data.get(id));}
 public Optional<Poliza> buscarPorNumero(String n){return data.values().stream().filter(x->x.getNumero().equals(n)).findFirst();}
 public Optional<Poliza> buscarPorCotizacionId(UUID id){return data.values().stream().filter(x->x.getCotizacionId().equals(id)).findFirst();}
 public List<Poliza> listar(){return List.copyOf(data.values());}
 public List<Poliza> listarPorCliente(UUID id){return data.values().stream().filter(x->x.getClienteId().equals(id)).toList();}
}