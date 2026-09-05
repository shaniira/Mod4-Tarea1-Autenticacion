package com.andinaseguros.adapters.inbound.rest.controller;

import com.andinaseguros.core.ports.in.siniestro.ActualizarEstadoSiniestroUseCase;

import com.andinaseguros.core.ports.in.siniestro.RegistrarSiniestroUseCase;

import com.andinaseguros.core.ports.in.siniestro.ListarSiniestrosUseCase;

import com.andinaseguros.adapters.inbound.rest.request.*;
import com.andinaseguros.core.application.dto.Responses.SiniestroResponse;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polizas/{polizaId}/siniestros")
public class SiniestroController {
    private final RegistrarSiniestroUseCase registrarSiniestroUseCase;
    private final ActualizarEstadoSiniestroUseCase actualizarEstadoUseCase;
    private final ListarSiniestrosUseCase listarSiniestrosUseCase;

    public SiniestroController(
            RegistrarSiniestroUseCase registrarSiniestroUseCase,
            ActualizarEstadoSiniestroUseCase actualizarEstadoUseCase,
            ListarSiniestrosUseCase listarSiniestrosUseCase) {
        this.registrarSiniestroUseCase = registrarSiniestroUseCase;
        this.actualizarEstadoUseCase = actualizarEstadoUseCase;
        this.listarSiniestrosUseCase = listarSiniestrosUseCase;
    }

    @PostMapping
    public ResponseEntity<SiniestroResponse> crear(
            @PathVariable UUID polizaId, @Valid @RequestBody RegistrarSiniestroRequest solicitud) {
        return ResponseEntity.status(201)
                .body(
                        registrarSiniestroUseCase.execute(
                                new com.andinaseguros.core.application.dto.RegistrarSiniestroCommand(
                                        polizaId,
                                        solicitud.fecha(),
                                        solicitud.tipo(),
                                        solicitud.montoEstimado(),
                                        solicitud.responsabilidadAsegurado(),
                                        solicitud.gravedad(),
                                        solicitud.estado())));
    }

    @GetMapping
    public List<SiniestroResponse> listar(@PathVariable UUID polizaId) {
        return listarSiniestrosUseCase.execute(polizaId);
    }

    @PatchMapping("/{id}/estado")
    public SiniestroResponse actualizar(
            @PathVariable UUID polizaId,
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarEstadoSiniestroRequest solicitud) {
        return actualizarEstadoUseCase.execute(polizaId, id, solicitud.estado());
    }
}
