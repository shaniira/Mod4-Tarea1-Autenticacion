package com.andinaseguros.adapters.inbound.rest.mapper;

import com.andinaseguros.adapters.inbound.rest.request.*;
import com.andinaseguros.core.application.dto.*;
import java.util.List;

public final class RestRequestMapper {
    private RestRequestMapper() {}

    public static CrearClienteCommand toCore(CrearClienteRequest value) {
        return new CrearClienteCommand(value.tipoDocumento(), value.numeroDocumento(), value.nombres(), value.apellidos(), value.fechaNacimiento(), value.correo(), value.telefono());
    }

    public static CrearVehiculoCommand toCore(CrearVehiculoRequest value) {
        return new CrearVehiculoCommand(value.clienteId(), value.placa(), value.marca(), value.modelo(), value.anioFabricacion(), value.tipo(), value.uso(), value.zonaCirculacion());
    }

    public static CrearCotizacionCommand toCore(CrearCotizacionRequest value) {
        return new CrearCotizacionCommand(value.clienteId(), value.vehiculoId(), value.siniestrosResponsables(), value.porcentajeGastos(), value.porcentajeRecargo(), value.porcentajeDescuento());
    }

    public static EmitirPolizaCommand toCore(EmitirPolizaRequest value) {
        return new EmitirPolizaCommand(value.cotizacionId(), value.inicioVigencia());
    }

    public static RegistrarSiniestroCommand toCore(RegistrarSiniestroRequest value) {
        return new RegistrarSiniestroCommand(value.polizaId(), value.fecha(), value.tipo(), value.montoEstimado(), value.responsabilidadAsegurado(), value.gravedad(), value.estado());
    }

    public static CrearTablaCommand toCore(CrearTablaRequest value) {
        List<CrearFactorCommand> factores = value.factores() == null ? List.of() : value.factores().stream().map(RestRequestMapper::toCore).toList();
        return new CrearTablaCommand(value.codigo(), value.version(), value.tipoVehiculo(), value.tipoUso(), value.primaBase(), value.primaMinima(), value.inicioVigencia(), value.finVigencia(), value.codigoNotaTecnica(), value.estado(), factores);
    }

    private static CrearFactorCommand toCore(CrearFactorRequest value) {
        return new CrearFactorCommand(value.codigo(), value.nombre(), value.tipoVariable(), value.valorMinimo(), value.valorMaximo(), value.multiplicador(), value.orden());
    }

    public static LoginCommand toCore(LoginRequest value) {
        return new LoginCommand(value.username(), value.password());
    }

    public static CrearUsuarioCommand toCore(CrearUsuarioRequest value) {
        return new CrearUsuarioCommand(value.username(), value.password(), value.rol());
    }
}
