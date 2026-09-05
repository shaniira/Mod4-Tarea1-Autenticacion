package com.andinaseguros.adapters.outbound.persistence.memory;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort; import com.andinaseguros.core.domain.enums.*; import com.andinaseguros.core.domain.model.TablaTarifaria;
import java.time.LocalDate; import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository
@Profile("memory")
public class TablaTarifariaInMemoryAdapter implements TablaTarifariaRepositoryPort {
 private final Map<UUID,TablaTarifaria> data=new ConcurrentHashMap<>();
 public TablaTarifaria guardar(TablaTarifaria x){data.put(x.getId(),x);return x;}
 public Optional<TablaTarifaria> buscarPorId(UUID id){return Optional.ofNullable(data.get(id));}
 public Optional<TablaTarifaria> buscarVigente(TipoVehiculo t,TipoUso u,LocalDate f){return data.values().stream().filter(x->x.getTipoVehiculo()==t&&x.getTipoUso()==u&&x.getVigencia().contiene(f)).findFirst();}
 public List<TablaTarifaria> listar(){return List.copyOf(data.values());}
}