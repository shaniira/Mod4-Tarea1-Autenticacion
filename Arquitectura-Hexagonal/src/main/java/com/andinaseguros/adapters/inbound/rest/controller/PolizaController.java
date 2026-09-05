package com.andinaseguros.adapters.inbound.rest.controller;

import com.andinaseguros.core.ports.in.poliza.ObtenerPolizaUseCase;

import com.andinaseguros.core.ports.in.poliza.EmitirPolizaUseCase;

import com.andinaseguros.core.ports.in.poliza.ListarPolizasUseCase;

import com.andinaseguros.adapters.inbound.rest.request.EmitirPolizaRequest;
import static com.andinaseguros.adapters.inbound.rest.mapper.RestRequestMapper.toCore;
import com.andinaseguros.core.application.dto.Responses.PolizaResponse;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polizas")
public class PolizaController {
    private final EmitirPolizaUseCase emitirPolizaUseCase;
    private final ObtenerPolizaUseCase obtenerPolizaUseCase;
    private final ListarPolizasUseCase listarPolizasUseCase;

    public PolizaController(
            EmitirPolizaUseCase emitirPolizaUseCase,
            ObtenerPolizaUseCase obtenerPolizaUseCase,
            ListarPolizasUseCase listarPolizasUseCase) {
        this.emitirPolizaUseCase = emitirPolizaUseCase;
        this.obtenerPolizaUseCase = obtenerPolizaUseCase;
        this.listarPolizasUseCase = listarPolizasUseCase;
    }

    @PostMapping
    public ResponseEntity<PolizaResponse> emitir(@Valid @RequestBody EmitirPolizaRequest solicitud) {
        return ResponseEntity.status(201).body(emitirPolizaUseCase.execute(toCore(solicitud)));
    }

    @GetMapping
    public List<PolizaResponse> listar(
            @RequestParam(required = false) com.andinaseguros.core.domain.enums.EstadoPoliza estado) {
        return listarPolizasUseCase.execute(estado);
    }

    @GetMapping("/{id}")
    public PolizaResponse obtener(@PathVariable UUID id) {
        return obtenerPolizaUseCase.execute(id);
    }
}
