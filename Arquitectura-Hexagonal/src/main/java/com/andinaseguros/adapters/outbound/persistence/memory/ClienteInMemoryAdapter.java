package com.andinaseguros.adapters.outbound.persistence.memory;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import com.andinaseguros.core.domain.model.Cliente;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository
@Profile("memory")
public class ClienteInMemoryAdapter implements ClienteRepositoryPort {
 private final Map<UUID,Cliente> data=new ConcurrentHashMap<>();
 public Cliente guardar(Cliente x){data.put(x.getId(),x);return x;}
 public Optional<Cliente> buscarPorId(UUID id){return Optional.ofNullable(data.get(id));}
 public Optional<Cliente> buscarPorDocumento(String doc){return data.values().stream().filter(x->x.getNumeroDocumento().equals(doc)).findFirst();}
 public List<Cliente> listar(){return List.copyOf(data.values());}
}