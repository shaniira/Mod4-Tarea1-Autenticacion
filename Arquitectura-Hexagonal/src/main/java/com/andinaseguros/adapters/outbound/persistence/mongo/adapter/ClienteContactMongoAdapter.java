package com.andinaseguros.adapters.outbound.persistence.mongo.adapter;

import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import com.andinaseguros.core.ports.out.external.contact.ClienteContactData;
import com.andinaseguros.core.ports.out.external.contact.ClienteContactPort;
import java.util.Optional;
import java.util.UUID;

public class ClienteContactMongoAdapter implements ClienteContactPort {
    private final ClienteRepositoryPort clienteRepository;

    public ClienteContactMongoAdapter(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Optional<ClienteContactData> buscarPorClienteId(UUID clienteId) {
        return clienteRepository
                .buscarPorId(clienteId)
                .map(
                        cliente ->
                                new ClienteContactData(
                                        cliente.getId(),
                                        nombreCompleto(
                                                cliente.getNombres(), cliente.getApellidos()),
                                        cliente.getCorreo(),
                                        cliente.getTelefono()));
    }

    private String nombreCompleto(String nombres, String apellidos) {
        return (nombres + " " + (apellidos == null ? "" : apellidos)).trim();
    }
}
