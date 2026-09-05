package com.andinaseguros.core.application.service.cotizacion;

import com.andinaseguros.core.ports.in.cotizacion.CrearCotizacionUseCase;

import static com.andinaseguros.core.application.mapper.CotizacionResponseMapper.toResponse;

import com.andinaseguros.core.application.dto.CrearCotizacionCommand;
import com.andinaseguros.core.application.dto.Responses.CotizacionResponse;
import com.andinaseguros.core.domain.enums.EstadoCotizacion;
import com.andinaseguros.core.domain.exception.RecursoNoEncontradoException;
import com.andinaseguros.core.domain.exception.ReglaNegocioException;
import com.andinaseguros.core.domain.model.Cliente;
import com.andinaseguros.core.domain.model.Cotizacion;
import com.andinaseguros.core.domain.model.ResultadoTarificacion;
import com.andinaseguros.core.domain.model.TablaTarifaria;
import com.andinaseguros.core.domain.model.Vehiculo;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.VehiculoRepositoryPort;
import com.andinaseguros.core.domain.service.MotorDeTarificacion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class CrearCotizacionService implements CrearCotizacionUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final VehiculoRepositoryPort vehiculoRepository;
    private final TablaTarifariaRepositoryPort tablaTarifariaRepository;
    private final CotizacionRepositoryPort cotizacionRepository;
    private final MotorDeTarificacion motorDeTarificacion;

    public CrearCotizacionService(
            ClienteRepositoryPort clienteRepository,
            VehiculoRepositoryPort vehiculoRepository,
            TablaTarifariaRepositoryPort tablaTarifariaRepository,
            CotizacionRepositoryPort cotizacionRepository,
            MotorDeTarificacion motorDeTarificacion) {
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.tablaTarifariaRepository = tablaTarifariaRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.motorDeTarificacion = motorDeTarificacion;
    }

    @Override
    public CotizacionResponse execute(CrearCotizacionCommand solicitud) {
        Cliente cliente = obtenerCliente(solicitud.clienteId());
        Vehiculo vehiculo = obtenerVehiculo(solicitud.vehiculoId());

        validarPropietario(cliente, vehiculo);

        TablaTarifaria tablaTarifaria = obtenerTablaTarifaria(vehiculo);
        ResultadoTarificacion resultado =
                calcularTarificacion(solicitud, cliente, vehiculo, tablaTarifaria);

        Cotizacion cotizacion = crearCotizacion(solicitud, resultado, tablaTarifaria);
        Cotizacion cotizacionGuardada = cotizacionRepository.guardar(cotizacion);

        return toResponse(cotizacionGuardada, resultado);
    }

    private Cliente obtenerCliente(UUID clienteId) {
        return clienteRepository
                .buscarPorId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente"));
    }

    private Vehiculo obtenerVehiculo(UUID vehiculoId) {
        return vehiculoRepository
                .buscarPorId(vehiculoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo"));
    }

    private void validarPropietario(Cliente cliente, Vehiculo vehiculo) {
        if (!vehiculo.getClienteId().equals(cliente.getId())) {
            throw new ReglaNegocioException(
                    "VEHICULO_NO_PERTENECE", "El vehículo no pertenece al cliente");
        }
    }

    private TablaTarifaria obtenerTablaTarifaria(Vehiculo vehiculo) {
        return tablaTarifariaRepository
                .buscarVigente(vehiculo.getTipo(), vehiculo.getUso(), LocalDate.now())
                .orElseThrow(
                        () ->
                                new ReglaNegocioException(
                                        "TABLA_NO_DISPONIBLE",
                                        "No existe tabla tarifaria vigente"));
    }

    private ResultadoTarificacion calcularTarificacion(
            CrearCotizacionCommand solicitud,
            Cliente cliente,
            Vehiculo vehiculo,
            TablaTarifaria tablaTarifaria) {
        return motorDeTarificacion.calcular(
                tablaTarifaria,
                cliente,
                vehiculo,
                solicitud.siniestrosResponsables(),
                valorOInicial(solicitud.porcentajeGastos(), "0.10"),
                valorOInicial(solicitud.porcentajeRecargo(), "0.03"),
                valorOInicial(solicitud.porcentajeDescuento(), "0.00"));
    }

    private Cotizacion crearCotizacion(
            CrearCotizacionCommand solicitud,
            ResultadoTarificacion resultado,
            TablaTarifaria tablaTarifaria) {
        LocalDateTime fechaCreacion = LocalDateTime.now();
        String numeroCotizacion =
                "COT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return new Cotizacion(
                UUID.randomUUID(),
                numeroCotizacion,
                solicitud.clienteId(),
                solicitud.vehiculoId(),
                tablaTarifaria.getId(),
                resultado.primaComercial(),
                fechaCreacion,
                fechaCreacion.plusDays(15),
                EstadoCotizacion.VIGENTE,
                resultado);
    }

    private BigDecimal valorOInicial(BigDecimal valor, String valorInicial) {
        return valor == null ? new BigDecimal(valorInicial) : valor;
    }
}
