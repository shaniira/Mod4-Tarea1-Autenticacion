package com.andinaseguros.presentation.controller;

import com.andinaseguros.presentation.request.CrearTablaRequest;
import static com.andinaseguros.presentation.mapper.RestRequestMapper.toApplication;
import com.andinaseguros.application.dto.Responses.*;
import com.andinaseguros.application.service.tarifa.*;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tablas-tarifarias")
public class TablaTarifariaController {
    private final CrearTablaTarifariaUseCase crearTablaUseCase;
    private final ListarTablasTarifariasUseCase listarTablasUseCase;
    private final ObtenerTablaTarifariaUseCase obtenerTablaUseCase;

    public TablaTarifariaController(
            CrearTablaTarifariaUseCase crearTablaUseCase,
            ListarTablasTarifariasUseCase listarTablasUseCase,
            ObtenerTablaTarifariaUseCase obtenerTablaUseCase) {
        this.crearTablaUseCase = crearTablaUseCase;
        this.listarTablasUseCase = listarTablasUseCase;
        this.obtenerTablaUseCase = obtenerTablaUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ACTUARIO')")
    public ResponseEntity<TablaResponse> crear(@Valid @RequestBody CrearTablaRequest solicitud) {
        return ResponseEntity.status(201).body(crearTablaUseCase.execute(toApplication(solicitud)));
    }

    @GetMapping
    public List<TablaResponse> listar() {
        return listarTablasUseCase.execute();
    }

    @GetMapping("/{id}")
    public TablaDetalleResponse obtener(@PathVariable UUID id) {
        return obtenerTablaUseCase.execute(id);
    }
}
