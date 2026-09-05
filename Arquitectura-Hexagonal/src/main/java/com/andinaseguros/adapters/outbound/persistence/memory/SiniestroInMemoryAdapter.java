package com.andinaseguros.adapters.outbound.persistence.memory;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort; import com.andinaseguros.core.domain.model.Siniestro;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository
@Profile("memory")
public class SiniestroInMemoryAdapter implements SiniestroRepositoryPort {
 private final Map<UUID,Siniestro> data=new ConcurrentHashMap<>();
 public Siniestro guardar(Siniestro x){data.put(x.id(),x);return x;}
 public Optional<Siniestro> buscarPorId(UUID id){return Optional.ofNullable(data.get(id));}
 public List<Siniestro> listarPorPoliza(UUID id){return data.values().stream().filter(x->x.polizaId().equals(id)).toList();}
}