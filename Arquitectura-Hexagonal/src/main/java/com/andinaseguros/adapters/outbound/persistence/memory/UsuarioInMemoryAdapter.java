package com.andinaseguros.adapters.outbound.persistence.memory;
import com.andinaseguros.core.ports.out.persistence.UsuarioRepositoryPort; import com.andinaseguros.core.domain.model.Usuario;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository
@Profile("memory")
public class UsuarioInMemoryAdapter implements UsuarioRepositoryPort {
 private final Map<UUID,Usuario> data=new ConcurrentHashMap<>();
 public Usuario guardar(Usuario x){data.put(x.getId(),x);return x;}
 public Optional<Usuario> buscarPorUsername(String n){return data.values().stream().filter(x->x.getUsername().equals(n)).findFirst();}
}