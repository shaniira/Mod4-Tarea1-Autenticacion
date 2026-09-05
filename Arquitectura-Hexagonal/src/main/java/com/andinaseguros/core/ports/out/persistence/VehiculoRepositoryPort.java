package com.andinaseguros.core.ports.out.persistence;

import com.andinaseguros.core.domain.model.Vehiculo;
import java.util.*;

public interface VehiculoRepositoryPort {
    Vehiculo guardar(Vehiculo vehiculo);

    Optional<Vehiculo> buscarPorId(UUID id);

    Optional<Vehiculo> buscarPorPlaca(String placa);

    List<Vehiculo> listarPorCliente(UUID clienteId);
}
