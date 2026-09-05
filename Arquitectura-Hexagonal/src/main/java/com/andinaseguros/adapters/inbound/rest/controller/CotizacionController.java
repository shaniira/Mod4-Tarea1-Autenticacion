package com.andinaseguros.adapters.inbound.rest.controller;

import com.andinaseguros.core.ports.in.cotizacion.ListarCotizacionesPendientesEmisionUseCase;

import com.andinaseguros.core.ports.in.cotizacion.AceptarCotizacionUseCase;

import com.andinaseguros.core.ports.in.cotizacion.ObtenerCotizacionUseCase;

import com.andinaseguros.core.ports.in.cotizacion.ListarCotizacionesUseCase;

import com.andinaseguros.core.ports.in.cotizacion.CrearCotizacionUseCase;

import com.andinaseguros.adapters.inbound.rest.request.CrearCotizacionRequest;
import static com.andinaseguros.adapters.inbound.rest.mapper.RestRequestMapper.toCore;
import com.andinaseguros.core.application.dto.Responses.CotizacionResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {
    private final CrearCotizacionUseCase crearCotizacionUseCase;
    private final ObtenerCotizacionUseCase obtenerCotizacionUseCase;
    private final AceptarCotizacionUseCase aceptarCotizacionUseCase;
    private final ListarCotizacionesUseCase listarCotizacionesUseCase;
    private final ListarCotizacionesPendientesEmisionUseCase listarPendientesUseCase;

    public CotizacionController(
            CrearCotizacionUseCase crearCotizacionUseCase,
            ObtenerCotizacionUseCase obtenerCotizacionUseCase,
            AceptarCotizacionUseCase aceptarCotizacionUseCase,
            ListarCotizacionesUseCase listarCotizacionesUseCase,
            ListarCotizacionesPendientesEmisionUseCase listarPendientesUseCase) {
        this.crearCotizacionUseCase = crearCotizacionUseCase;
        this.obtenerCotizacionUseCase = obtenerCotizacionUseCase;
        this.aceptarCotizacionUseCase = aceptarCotizacionUseCase;
        this.listarCotizacionesUseCase = listarCotizacionesUseCase;
        this.listarPendientesUseCase = listarPendientesUseCase;
    }

    @GetMapping
    public List<CotizacionResponse> listar(
            @RequestParam(required = false)
                    com.andinaseguros.core.domain.enums.EstadoCotizacion estado) {
        return listarCotizacionesUseCase.execute(estado);
    }

    @GetMapping("/pendientes-emision")
    public List<CotizacionResponse> pendientesEmision() {
        return listarPendientesUseCase.execute();
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> crear(@Valid @RequestBody CrearCotizacionRequest solicitud) {
        return ResponseEntity.status(201).body(crearCotizacionUseCase.execute(toCore(solicitud)));
    }

    @GetMapping("/{id}")
    public CotizacionResponse obtener(@PathVariable UUID id) {
        return obtenerCotizacionUseCase.execute(id);
    }

    @PatchMapping("/{id}/aceptar")
    public CotizacionResponse aceptar(@PathVariable UUID id) {
        return aceptarCotizacionUseCase.execute(id);
    }
}
