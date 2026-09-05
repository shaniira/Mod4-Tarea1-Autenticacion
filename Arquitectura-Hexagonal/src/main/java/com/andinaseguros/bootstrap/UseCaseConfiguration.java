package com.andinaseguros.bootstrap;

import com.andinaseguros.core.ports.in.auth.RegistrarUsuarioUseCase;

import com.andinaseguros.core.ports.in.renovacion.RechazarRenovacionUseCase;

import com.andinaseguros.core.ports.in.siniestro.ActualizarEstadoSiniestroUseCase;

import com.andinaseguros.core.ports.in.renovacion.ListarRenovacionesUseCase;

import com.andinaseguros.core.ports.in.renovacion.ListarHistorialRenovacionesUseCase;


import com.andinaseguros.core.ports.in.siniestro.RegistrarSiniestroUseCase;

import com.andinaseguros.core.ports.in.poliza.ObtenerPolizaUseCase;

import com.andinaseguros.core.ports.in.vehiculo.ListarVehiculosClienteUseCase;

import com.andinaseguros.core.ports.in.cotizacion.ListarCotizacionesPendientesEmisionUseCase;

import com.andinaseguros.core.ports.in.renovacion.ObtenerRenovacionUseCase;

import com.andinaseguros.core.ports.in.cotizacion.AceptarCotizacionUseCase;

import com.andinaseguros.core.ports.in.poliza.EmitirPolizaUseCase;

import com.andinaseguros.core.ports.in.auth.AutenticarUsuarioUseCase;

import com.andinaseguros.core.ports.in.tarifa.ObtenerTablaTarifariaUseCase;

import com.andinaseguros.core.ports.in.renovacion.GenerarPolizaRenovadaUseCase;

import com.andinaseguros.core.ports.in.poliza.ListarPolizasUseCase;

import com.andinaseguros.core.ports.in.cotizacion.ObtenerCotizacionUseCase;

import com.andinaseguros.core.ports.in.cliente.CrearClienteUseCase;

import com.andinaseguros.core.ports.in.siniestro.ListarSiniestrosUseCase;

import com.andinaseguros.core.ports.in.tarifa.ListarTablasTarifariasUseCase;

import com.andinaseguros.core.ports.in.renovacion.AprobarRenovacionUseCase;

import com.andinaseguros.core.ports.in.tarifa.CrearTablaTarifariaUseCase;

import com.andinaseguros.core.ports.in.cotizacion.ListarCotizacionesUseCase;

import com.andinaseguros.core.ports.in.cotizacion.CrearCotizacionUseCase;

import com.andinaseguros.core.ports.in.renovacion.EvaluarRenovacionUseCase;

import com.andinaseguros.core.ports.in.cliente.ObtenerClienteUseCase;

import com.andinaseguros.core.ports.in.cliente.ListarClientesUseCase;

import com.andinaseguros.core.ports.in.vehiculo.CrearVehiculoUseCase;

import com.andinaseguros.core.ports.in.vehiculo.ConsultarInformacionVehiculoUseCase;
import com.andinaseguros.core.application.service.ConsultarInformacionVehiculoService;
import com.andinaseguros.core.application.service.auth.AutenticarUsuarioService;
import com.andinaseguros.core.application.service.auth.RegistrarUsuarioService;
import com.andinaseguros.core.application.service.cliente.CrearClienteService;
import com.andinaseguros.core.application.service.cliente.ListarClientesService;
import com.andinaseguros.core.application.service.cliente.ObtenerClienteService;
import com.andinaseguros.core.application.service.cotizacion.AceptarCotizacionService;
import com.andinaseguros.core.application.service.cotizacion.CrearCotizacionService;
import com.andinaseguros.core.application.service.cotizacion.ListarCotizacionesPendientesEmisionService;
import com.andinaseguros.core.application.service.cotizacion.ListarCotizacionesService;
import com.andinaseguros.core.application.service.cotizacion.ObtenerCotizacionService;
import com.andinaseguros.core.application.service.poliza.EmitirPolizaService;
import com.andinaseguros.core.application.service.poliza.ListarPolizasService;
import com.andinaseguros.core.application.service.poliza.ObtenerPolizaService;
import com.andinaseguros.core.application.service.renovacion.AprobarRenovacionService;
import com.andinaseguros.core.application.service.renovacion.EvaluarRenovacionService;
import com.andinaseguros.core.application.service.renovacion.GenerarPolizaRenovadaService;
import com.andinaseguros.core.application.service.renovacion.ListarHistorialRenovacionesService;
import com.andinaseguros.core.application.service.renovacion.ListarRenovacionesService;
import com.andinaseguros.core.application.service.renovacion.ObtenerRenovacionService;
import com.andinaseguros.core.application.service.renovacion.RechazarRenovacionService;
import com.andinaseguros.core.application.service.siniestro.ActualizarEstadoSiniestroService;
import com.andinaseguros.core.application.service.siniestro.ListarSiniestrosService;
import com.andinaseguros.core.application.service.siniestro.RegistrarSiniestroService;
import com.andinaseguros.core.application.service.tarifa.CrearTablaTarifariaService;
import com.andinaseguros.core.application.service.tarifa.ListarTablasTarifariasService;
import com.andinaseguros.core.application.service.tarifa.ObtenerTablaTarifariaService;
import com.andinaseguros.core.application.service.vehiculo.CrearVehiculoService;
import com.andinaseguros.core.application.service.vehiculo.ListarVehiculosClienteService;
import com.andinaseguros.core.ports.out.persistence.ClienteRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.CotizacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.PolizaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.RenovacionRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.SiniestroRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.TablaTarifariaRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.UsuarioRepositoryPort;
import com.andinaseguros.core.ports.out.persistence.VehiculoRepositoryPort;
import com.andinaseguros.core.domain.service.CalculadorPrimaRenovacion;
import com.andinaseguros.core.domain.service.EvaluadorRenovacion;
import com.andinaseguros.core.domain.service.MotorDeTarificacion;
import com.andinaseguros.core.domain.service.PoliticaVariacionPrima;
import com.andinaseguros.adapters.outbound.event.*;
import com.andinaseguros.adapters.outbound.external.jsonpe.*;
import com.andinaseguros.adapters.outbound.id.UuidGeneratorAdapter;
import com.andinaseguros.adapters.outbound.notification.whatsapp.*;
import com.andinaseguros.adapters.outbound.persistence.mongo.adapter.ClienteContactMongoAdapter;
import com.andinaseguros.adapters.outbound.security.*;
import com.andinaseguros.adapters.outbound.clock.SystemClockAdapter;
import com.andinaseguros.core.ports.out.external.contact.ClienteContactPort;
import com.andinaseguros.core.ports.out.event.DomainEventPublisherPort;
import com.andinaseguros.core.ports.out.id.IdGeneratorPort;
import com.andinaseguros.core.ports.out.notification.NotificationPort;
import com.andinaseguros.core.ports.out.security.*;
import com.andinaseguros.core.ports.out.clock.ClockPort;
import com.andinaseguros.core.ports.out.external.vehicle.VehicleInformationPort;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({JsonPeProperties.class, WhatsAppProperties.class})
public class UseCaseConfiguration {

    @Bean
    ClockPort clockPort() {
        return new SystemClockAdapter();
    }

    @Bean
    IdGeneratorPort idGeneratorPort() {
        return new UuidGeneratorAdapter();
    }

    @Bean
    PasswordEncoderPort passwordEncoderPort() {
        return new BCryptPasswordEncoderAdapter();
    }

    @Bean
    JwtTokenAdapter jwtTokenAdapter(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds:3600}") long expiration,
            ClockPort clock) {
        return new JwtTokenAdapter(secret, expiration, clock);
    }

    @Bean
    RegistrarUsuarioUseCase registrarUsuario(
            UsuarioRepositoryPort usuarios, PasswordEncoderPort passwordEncoder, IdGeneratorPort ids) {
        return new RegistrarUsuarioService(usuarios, passwordEncoder, ids);
    }

    @Bean
    AutenticarUsuarioUseCase autenticarUsuario(
            UsuarioRepositoryPort usuarios,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator) {
        return new AutenticarUsuarioService(usuarios, passwordEncoder, tokenGenerator);
    }

    @Bean
    MotorDeTarificacion motorDeTarificacion() {
        return new MotorDeTarificacion();
    }

    @Bean
    EvaluadorRenovacion evaluadorRenovacion() {
        return new EvaluadorRenovacion();
    }

    @Bean
    CalculadorPrimaRenovacion calculadorPrimaRenovacion() {
        return new CalculadorPrimaRenovacion();
    }

    @Bean
    PoliticaVariacionPrima politicaVariacionPrima() {
        return new PoliticaVariacionPrima();
    }

    @Bean
    CrearClienteUseCase crearCliente(ClienteRepositoryPort clienteRepository) {
        return new CrearClienteService(clienteRepository);
    }

    @Bean
    ListarClientesUseCase listarClientes(ClienteRepositoryPort clienteRepository) {
        return new ListarClientesService(clienteRepository);
    }

    @Bean
    ObtenerClienteUseCase obtenerCliente(ClienteRepositoryPort clienteRepository) {
        return new ObtenerClienteService(clienteRepository);
    }

    @Bean
    CrearVehiculoUseCase crearVehiculo(
            ClienteRepositoryPort clienteRepository, VehiculoRepositoryPort vehiculoRepository) {
        return new CrearVehiculoService(clienteRepository, vehiculoRepository);
    }

    @Bean
    ListarVehiculosClienteUseCase listarVehiculos(VehiculoRepositoryPort vehiculoRepository) {
        return new ListarVehiculosClienteService(vehiculoRepository);
    }

    @Bean
    CrearTablaTarifariaUseCase crearTabla(TablaTarifariaRepositoryPort tablaTarifariaRepository) {
        return new CrearTablaTarifariaService(tablaTarifariaRepository);
    }

    @Bean
    ListarTablasTarifariasUseCase listarTablas(TablaTarifariaRepositoryPort tablaTarifariaRepository) {
        return new ListarTablasTarifariasService(tablaTarifariaRepository);
    }

    @Bean
    ObtenerTablaTarifariaUseCase obtenerTabla(TablaTarifariaRepositoryPort tablaTarifariaRepository) {
        return new ObtenerTablaTarifariaService(tablaTarifariaRepository);
    }

    @Bean
    CrearCotizacionUseCase crearCotizacion(
            ClienteRepositoryPort clienteRepository,
            VehiculoRepositoryPort vehiculoRepository,
            TablaTarifariaRepositoryPort tablaTarifariaRepository,
            CotizacionRepositoryPort cotizacionRepository,
            MotorDeTarificacion motorDeTarificacion) {
        return new CrearCotizacionService(
                clienteRepository,
                vehiculoRepository,
                tablaTarifariaRepository,
                cotizacionRepository,
                motorDeTarificacion);
    }

    @Bean
    ObtenerCotizacionUseCase obtenerCotizacion(CotizacionRepositoryPort cotizacionRepository) {
        return new ObtenerCotizacionService(cotizacionRepository);
    }

    @Bean
    AceptarCotizacionUseCase aceptarCotizacion(CotizacionRepositoryPort cotizacionRepository) {
        return new AceptarCotizacionService(cotizacionRepository);
    }

    @Bean
    ListarCotizacionesUseCase listarCotizaciones(CotizacionRepositoryPort cotizacionRepository) {
        return new ListarCotizacionesService(cotizacionRepository);
    }

    @Bean
    ListarCotizacionesPendientesEmisionUseCase listarCotizacionesPendientes(
            CotizacionRepositoryPort cotizacionRepository, PolizaRepositoryPort polizaRepository) {
        return new ListarCotizacionesPendientesEmisionService(
                cotizacionRepository, polizaRepository);
    }

    @Bean
    EmitirPolizaUseCase emitirPoliza(
            CotizacionRepositoryPort cotizacionRepository,
            PolizaRepositoryPort polizaRepository,
            DomainEventPublisherPort eventPublisher,
            ClockPort clock,
            IdGeneratorPort ids) {
        return new EmitirPolizaService(
                cotizacionRepository, polizaRepository, eventPublisher, clock, ids);
    }

    @Bean
    ObtenerPolizaUseCase obtenerPoliza(PolizaRepositoryPort polizaRepository) {
        return new ObtenerPolizaService(polizaRepository);
    }

    @Bean
    ListarPolizasUseCase listarPolizas(PolizaRepositoryPort polizaRepository) {
        return new ListarPolizasService(polizaRepository);
    }

    @Bean
    DomainEventPublisherPort domainEventPublisher(ApplicationEventPublisher publisher) {
        return new SpringDomainEventPublisherAdapter(publisher);
    }

    @Bean
    PolizaEmitidaNotificationHandler polizaEmitidaNotificationHandler(
            NotificationPort notificationPort, ClienteContactPort clienteContactPort) {
        return new PolizaEmitidaNotificationHandler(notificationPort, clienteContactPort);
    }

    @Bean
    ClienteContactPort clienteContactPort(ClienteRepositoryPort clienteRepository) {
        return new ClienteContactMongoAdapter(clienteRepository);
    }

    @Bean
    NotificationPort whatsappNotification(WhatsAppProperties properties) {
        RestClient client = timedRestClient(properties.baseUrl(), properties.timeoutSeconds());
        return new WhatsAppNotificationAdapter(
                new WhatsAppClient(client, properties), new WhatsAppNotificationMapper());
    }

    @Bean
    VehicleInformationPort jsonPeVehicleInformation(JsonPeProperties properties) {
        RestClient client = timedRestClient(properties.baseUrl(), properties.timeoutSeconds());
        return new JsonPeVehicleInformationAdapter(
                new JsonPeClient(client, properties), new JsonPeVehicleMapper());
    }

    private RestClient timedRestClient(String baseUrl, Integer configuredTimeout) {
        long timeout = configuredTimeout == null ? 5 : configuredTimeout;
        HttpClient httpClient =
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(timeout)).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(timeout));
        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    @Bean
    ConsultarInformacionVehiculoUseCase consultarInformacionVehiculo(
            VehicleInformationPort vehicleInformationPort) {
        return new ConsultarInformacionVehiculoService(vehicleInformationPort);
    }

    @Bean
    RegistrarSiniestroUseCase registrarSiniestro(
            PolizaRepositoryPort polizaRepository, SiniestroRepositoryPort siniestroRepository) {
        return new RegistrarSiniestroService(polizaRepository, siniestroRepository);
    }

    @Bean
    ActualizarEstadoSiniestroUseCase actualizarSiniestro(
            PolizaRepositoryPort polizaRepository, SiniestroRepositoryPort siniestroRepository) {
        return new ActualizarEstadoSiniestroService(polizaRepository, siniestroRepository);
    }

    @Bean
    ListarSiniestrosUseCase listarSiniestros(SiniestroRepositoryPort siniestroRepository) {
        return new ListarSiniestrosService(siniestroRepository);
    }

    @Bean
    EvaluarRenovacionUseCase evaluarRenovacion(
            PolizaRepositoryPort polizaRepository,
            SiniestroRepositoryPort siniestroRepository,
            RenovacionRepositoryPort renovacionRepository,
            EvaluadorRenovacion evaluadorRenovacion,
            CalculadorPrimaRenovacion calculadorPrimaRenovacion,
            PoliticaVariacionPrima politicaVariacionPrima) {
        return new EvaluarRenovacionService(
                polizaRepository,
                siniestroRepository,
                renovacionRepository,
                evaluadorRenovacion,
                calculadorPrimaRenovacion,
                politicaVariacionPrima);
    }

    @Bean
    ObtenerRenovacionUseCase obtenerRenovacion(RenovacionRepositoryPort renovacionRepository) {
        return new ObtenerRenovacionService(renovacionRepository);
    }

    @Bean
    AprobarRenovacionUseCase aprobarRenovacion(RenovacionRepositoryPort renovacionRepository) {
        return new AprobarRenovacionService(renovacionRepository);
    }

    @Bean
    RechazarRenovacionUseCase rechazarRenovacion(RenovacionRepositoryPort renovacionRepository) {
        return new RechazarRenovacionService(renovacionRepository);
    }

    @Bean
    GenerarPolizaRenovadaUseCase generarRenovada(
            RenovacionRepositoryPort renovacionRepository, PolizaRepositoryPort polizaRepository) {
        return new GenerarPolizaRenovadaService(renovacionRepository, polizaRepository);
    }

    @Bean
    ListarRenovacionesUseCase listarRenovaciones(RenovacionRepositoryPort renovacionRepository) {
        return new ListarRenovacionesService(renovacionRepository);
    }

    @Bean
    ListarHistorialRenovacionesUseCase historial(
            PolizaRepositoryPort polizaRepository, RenovacionRepositoryPort renovacionRepository) {
        return new ListarHistorialRenovacionesService(polizaRepository, renovacionRepository);
    }
}
