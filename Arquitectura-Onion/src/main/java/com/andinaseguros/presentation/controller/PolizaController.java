package com.andinaseguros.presentation.controller;

import com.andinaseguros.presentation.request.EmitirPolizaRequest;
import static com.andinaseguros.presentation.mapper.RestRequestMapper.toApplication;
import com.andinaseguros.application.dto.Responses.PolizaResponse;
import com.andinaseguros.application.service.EmitirPolizaInputPort;
import com.andinaseguros.application.service.poliza.*;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polizas")
public class PolizaController {
    private final EmitirPolizaInputPort emitirPolizaUseCase;
    private final ObtenerPolizaUseCase obtenerPolizaUseCase;
    private final ListarPolizasUseCase listarPolizasUseCase;

    public PolizaController(
            EmitirPolizaInputPort emitirPolizaUseCase,
            ObtenerPolizaUseCase obtenerPolizaUseCase,
            ListarPolizasUseCase listarPolizasUseCase) {
        this.emitirPolizaUseCase = emitirPolizaUseCase;
        this.obtenerPolizaUseCase = obtenerPolizaUseCase;
        this.listarPolizasUseCase = listarPolizasUseCase;
    }

    @PostMapping
    public ResponseEntity<PolizaResponse> emitir(@Valid @RequestBody EmitirPolizaRequest solicitud) {
        return ResponseEntity.status(201).body(emitirPolizaUseCase.execute(toApplication(solicitud)));
    }

    @GetMapping
    public List<PolizaResponse> listar(
            @RequestParam(required = false) com.andinaseguros.domain.enums.EstadoPoliza estado) {
        return listarPolizasUseCase.execute(estado);
    }

    @GetMapping("/{id}")
    public PolizaResponse obtener(@PathVariable UUID id) {
        return obtenerPolizaUseCase.execute(id);
    }
}
