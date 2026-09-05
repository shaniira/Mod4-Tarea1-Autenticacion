package com.andinaseguros.interfaceadapters.out.persistence.mongodb.adapter;

import com.andinaseguros.usecases.port.out.repository.ClienteRepository;
import com.andinaseguros.usecases.port.out.contact.ClienteContactData;
import com.andinaseguros.usecases.port.out.contact.ClienteContactPort;
import java.util.Optional;
import java.util.UUID;

public class ClienteContactMongoAdapter implements ClienteContactPort {
    private final ClienteRepository clienteRepository;

    public ClienteContactMongoAdapter(ClienteRepository clienteRepository) {
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
