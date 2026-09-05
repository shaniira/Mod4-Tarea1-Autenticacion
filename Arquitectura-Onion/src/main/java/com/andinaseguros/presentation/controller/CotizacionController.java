package com.andinaseguros.presentation.controller;

import com.andinaseguros.presentation.request.CrearCotizacionRequest;
import static com.andinaseguros.presentation.mapper.RestRequestMapper.toApplication;
import com.andinaseguros.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.application.service.CrearCotizacionInputPort;
import com.andinaseguros.application.service.cotizacion.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {
    private final CrearCotizacionInputPort crearCotizacionUseCase;
    private final ObtenerCotizacionUseCase obtenerCotizacionUseCase;
    private final AceptarCotizacionUseCase aceptarCotizacionUseCase;
    private final ListarCotizacionesUseCase listarCotizacionesUseCase;
    private final ListarCotizacionesPendientesEmisionUseCase listarPendientesUseCase;

    public CotizacionController(
            CrearCotizacionInputPort crearCotizacionUseCase,
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
                    com.andinaseguros.domain.enums.EstadoCotizacion estado) {
        return listarCotizacionesUseCase.execute(estado);
    }

    @GetMapping("/pendientes-emision")
    public List<CotizacionResponse> pendientesEmision() {
        return listarPendientesUseCase.execute();
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> crear(@Valid @RequestBody CrearCotizacionRequest solicitud) {
        return ResponseEntity.status(201).body(crearCotizacionUseCase.execute(toApplication(solicitud)));
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
