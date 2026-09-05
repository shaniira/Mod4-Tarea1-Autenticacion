package com.andinaseguros.adapters.inbound.rest.controller;

import com.andinaseguros.core.ports.in.vehiculo.ListarVehiculosClienteUseCase;

import com.andinaseguros.core.ports.in.cliente.CrearClienteUseCase;

import com.andinaseguros.core.ports.in.cliente.ObtenerClienteUseCase;

import com.andinaseguros.core.ports.in.cliente.ListarClientesUseCase;

import com.andinaseguros.core.ports.in.vehiculo.CrearVehiculoUseCase;

import com.andinaseguros.adapters.inbound.rest.request.*;
import static com.andinaseguros.adapters.inbound.rest.mapper.RestRequestMapper.toCore;
import com.andinaseguros.core.application.dto.Responses.*;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final CrearClienteUseCase crearClienteUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    private final ObtenerClienteUseCase obtenerClienteUseCase;
    private final CrearVehiculoUseCase crearVehiculo;
    private final ListarVehiculosClienteUseCase listarVehiculos;

    public ClienteController(
            CrearClienteUseCase crearClienteUseCase,
            ListarClientesUseCase listarClientesUseCase,
            ObtenerClienteUseCase obtenerClienteUseCase,
            CrearVehiculoUseCase crearVehiculoUseCase,
            ListarVehiculosClienteUseCase listarVehiculosClienteUseCase) {
        this.crearClienteUseCase = crearClienteUseCase;
        this.listarClientesUseCase = listarClientesUseCase;
        this.obtenerClienteUseCase = obtenerClienteUseCase;
        this.crearVehiculo = crearVehiculoUseCase;
        this.listarVehiculos = listarVehiculosClienteUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGENTE')")
    ResponseEntity<ClienteResponse> crear(@Valid @RequestBody CrearClienteRequest solicitud) {
        return ResponseEntity.status(201).body(crearClienteUseCase.execute(toCore(solicitud)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGENTE')")
    List<ClienteResponse> listar() {
        return listarClientesUseCase.execute();
    }

    @GetMapping("/{id}")
    ClienteResponse obtener(@PathVariable UUID id) {
        return obtenerClienteUseCase.execute(id);
    }

    @PostMapping("/{id}/vehiculos")
    ResponseEntity<VehiculoResponse> vehiculo(
            @PathVariable UUID id, @Valid @RequestBody CrearVehiculoRequest solicitud) {
        return ResponseEntity.status(201)
                .body(
                        crearVehiculo.execute(
                                new com.andinaseguros.core.application.dto.CrearVehiculoCommand(
                                        id,
                                        solicitud.placa(),
                                        solicitud.marca(),
                                        solicitud.modelo(),
                                        solicitud.anioFabricacion(),
                                        solicitud.tipo(),
                                        solicitud.uso(),
                                        solicitud.zonaCirculacion())));
    }

    @GetMapping("/{id}/vehiculos")
    List<VehiculoResponse> vehiculos(@PathVariable UUID id) {
        return listarVehiculos.execute(id);
    }
}
