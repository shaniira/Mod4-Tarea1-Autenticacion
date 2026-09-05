package com.andinaseguros.adapters.inbound.rest.controller;


import com.andinaseguros.core.ports.in.vehiculo.ConsultarInformacionVehiculoUseCase;
import com.andinaseguros.adapters.inbound.rest.response.VehicleInformationRestResponse;
import static com.andinaseguros.adapters.inbound.rest.mapper.VehicleInformationRestMapper.toRestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehiculos")
public class VehicleInformationController {
    private final ConsultarInformacionVehiculoUseCase useCase;

    public VehicleInformationController(ConsultarInformacionVehiculoUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/informacion-externa")
    public ResponseEntity<VehicleInformationRestResponse> consultar(@RequestParam String placa) {
        return ResponseEntity.ok(toRestResponse(useCase.consultarPorPlaca(placa)));
    }
}
