package com.andinaseguros.presentation.mapper;

import com.andinaseguros.presentation.request.*;
import com.andinaseguros.application.dto.*;
import java.util.List;

public final class RestRequestMapper {
    private RestRequestMapper() {}

    public static CrearClienteDto toApplication(CrearClienteRequest value) {
        return new CrearClienteDto(value.tipoDocumento(), value.numeroDocumento(), value.nombres(), value.apellidos(), value.fechaNacimiento(), value.correo(), value.telefono());
    }

    public static CrearVehiculoDto toApplication(CrearVehiculoRequest value) {
        return new CrearVehiculoDto(value.clienteId(), value.placa(), value.marca(), value.modelo(), value.anioFabricacion(), value.tipo(), value.uso(), value.zonaCirculacion());
    }

    public static CrearCotizacionDto toApplication(CrearCotizacionRequest value) {
        return new CrearCotizacionDto(value.clienteId(), value.vehiculoId(), value.siniestrosResponsables(), value.porcentajeGastos(), value.porcentajeRecargo(), value.porcentajeDescuento());
    }

    public static EmitirPolizaDto toApplication(EmitirPolizaRequest value) {
        return new EmitirPolizaDto(value.cotizacionId(), value.inicioVigencia());
    }

    public static RegistrarSiniestroDto toApplication(RegistrarSiniestroRequest value) {
        return new RegistrarSiniestroDto(value.polizaId(), value.fecha(), value.tipo(), value.montoEstimado(), value.responsabilidadAsegurado(), value.gravedad(), value.estado());
    }

    public static CrearTablaDto toApplication(CrearTablaRequest value) {
        List<CrearFactorDto> factores = value.factores() == null ? List.of() : value.factores().stream().map(RestRequestMapper::toApplication).toList();
        return new CrearTablaDto(value.codigo(), value.version(), value.tipoVehiculo(), value.tipoUso(), value.primaBase(), value.primaMinima(), value.inicioVigencia(), value.finVigencia(), value.codigoNotaTecnica(), value.estado(), factores);
    }

    private static CrearFactorDto toApplication(CrearFactorRequest value) {
        return new CrearFactorDto(value.codigo(), value.nombre(), value.tipoVariable(), value.valorMinimo(), value.valorMaximo(), value.multiplicador(), value.orden());
    }

    public static LoginDto toApplication(LoginRequest value) {
        return new LoginDto(value.username(), value.password());
    }

    public static CrearUsuarioDto toApplication(CrearUsuarioRequest value) {
        return new CrearUsuarioDto(value.username(), value.password(), value.rol());
    }
}
