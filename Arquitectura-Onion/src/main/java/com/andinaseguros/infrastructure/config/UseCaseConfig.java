package com.andinaseguros.infrastructure.config;

import com.andinaseguros.application.service.ConsultarInformacionVehiculoUseCase;
import com.andinaseguros.application.service.ConsultarInformacionVehiculoService;
import com.andinaseguros.application.service.auth.AutenticarUsuarioUseCase;
import com.andinaseguros.application.service.auth.RegistrarUsuarioUseCase;
import com.andinaseguros.application.service.cliente.CrearClienteUseCase;
import com.andinaseguros.application.service.cliente.ListarClientesUseCase;
import com.andinaseguros.application.service.cliente.ObtenerClienteUseCase;
import com.andinaseguros.application.service.cotizacion.AceptarCotizacionUseCase;
import com.andinaseguros.application.service.cotizacion.CrearCotizacionUseCase;
import com.andinaseguros.application.service.cotizacion.ListarCotizacionesPendientesEmisionUseCase;
import com.andinaseguros.application.service.cotizacion.ListarCotizacionesUseCase;
import com.andinaseguros.application.service.cotizacion.ObtenerCotizacionUseCase;
import com.andinaseguros.application.service.poliza.EmitirPolizaUseCase;
import com.andinaseguros.application.service.poliza.ListarPolizasUseCase;
import com.andinaseguros.application.service.poliza.ObtenerPolizaUseCase;
import com.andinaseguros.application.service.renovacion.AprobarRenovacionUseCase;
import com.andinaseguros.application.service.renovacion.EvaluarRenovacionUseCase;
import com.andinaseguros.application.service.renovacion.GenerarPolizaRenovadaUseCase;
import com.andinaseguros.application.service.renovacion.ListarHistorialRenovacionesUseCase;
import com.andinaseguros.application.service.renovacion.ListarRenovacionesUseCase;
import com.andinaseguros.application.service.renovacion.ObtenerRenovacionUseCase;
import com.andinaseguros.application.service.renovacion.RechazarRenovacionUseCase;
import com.andinaseguros.application.service.siniestro.ActualizarEstadoSiniestroUseCase;
import com.andinaseguros.application.service.siniestro.ListarSiniestrosUseCase;
import com.andinaseguros.application.service.siniestro.RegistrarSiniestroUseCase;
import com.andinaseguros.application.service.tarifa.CrearTablaTarifariaUseCase;
import com.andinaseguros.application.service.tarifa.ListarTablasTarifariasUseCase;
import com.andinaseguros.application.service.tarifa.ObtenerTablaTarifariaUseCase;
import com.andinaseguros.application.service.vehiculo.CrearVehiculoUseCase;
import com.andinaseguros.application.service.vehiculo.ListarVehiculosClienteUseCase;
import com.andinaseguros.domain.repository.ClienteRepository;
import com.andinaseguros.domain.repository.CotizacionRepository;
import com.andinaseguros.domain.repository.PolizaRepository;
import com.andinaseguros.domain.repository.RenovacionRepository;
import com.andinaseguros.domain.repository.SiniestroRepository;
import com.andinaseguros.domain.repository.TablaTarifariaRepository;
import com.andinaseguros.domain.repository.UsuarioRepository;
import com.andinaseguros.domain.repository.VehiculoRepository;
import com.andinaseguros.domain.service.CalculadorPrimaRenovacion;
import com.andinaseguros.domain.service.EvaluadorRenovacion;
import com.andinaseguros.domain.service.MotorDeTarificacion;
import com.andinaseguros.domain.service.PoliticaVariacionPrima;
import com.andinaseguros.infrastructure.event.*;
import com.andinaseguros.infrastructure.external.jsonpe.*;
import com.andinaseguros.infrastructure.id.UuidGeneratorAdapter;
import com.andinaseguros.infrastructure.notification.*;
import com.andinaseguros.infrastructure.persistence.adapter.ClienteContactMongoAdapter;
import com.andinaseguros.infrastructure.security.*;
import com.andinaseguros.infrastructure.time.SystemClockAdapter;
import com.andinaseguros.application.gateway.contact.ClienteContactPort;
import com.andinaseguros.application.gateway.event.DomainEventPublisherPort;
import com.andinaseguros.application.gateway.id.IdGeneratorPort;
import com.andinaseguros.application.gateway.notification.NotificationPort;
import com.andinaseguros.application.gateway.security.*;
import com.andinaseguros.application.gateway.time.ClockPort;
import com.andinaseguros.application.gateway.vehicle.VehicleInformationPort;
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
public class UseCaseConfig {

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
            UsuarioRepository usuarios, PasswordEncoderPort passwordEncoder, IdGeneratorPort ids) {
        return new RegistrarUsuarioUseCase(usuarios, passwordEncoder, ids);
    }

    @Bean
    AutenticarUsuarioUseCase autenticarUsuario(
            UsuarioRepository usuarios,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator) {
        return new AutenticarUsuarioUseCase(usuarios, passwordEncoder, tokenGenerator);
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
    CrearClienteUseCase crearCliente(ClienteRepository clienteRepository) {
        return new CrearClienteUseCase(clienteRepository);
    }

    @Bean
    ListarClientesUseCase listarClientes(ClienteRepository clienteRepository) {
        return new ListarClientesUseCase(clienteRepository);
    }

    @Bean
    ObtenerClienteUseCase obtenerCliente(ClienteRepository clienteRepository) {
        return new ObtenerClienteUseCase(clienteRepository);
    }

    @Bean
    CrearVehiculoUseCase crearVehiculo(
            ClienteRepository clienteRepository, VehiculoRepository vehiculoRepository) {
        return new CrearVehiculoUseCase(clienteRepository, vehiculoRepository);
    }

    @Bean
    ListarVehiculosClienteUseCase listarVehiculos(VehiculoRepository vehiculoRepository) {
        return new ListarVehiculosClienteUseCase(vehiculoRepository);
    }

    @Bean
    CrearTablaTarifariaUseCase crearTabla(TablaTarifariaRepository tablaTarifariaRepository) {
        return new CrearTablaTarifariaUseCase(tablaTarifariaRepository);
    }

    @Bean
    ListarTablasTarifariasUseCase listarTablas(TablaTarifariaRepository tablaTarifariaRepository) {
        return new ListarTablasTarifariasUseCase(tablaTarifariaRepository);
    }

    @Bean
    ObtenerTablaTarifariaUseCase obtenerTabla(TablaTarifariaRepository tablaTarifariaRepository) {
        return new ObtenerTablaTarifariaUseCase(tablaTarifariaRepository);
    }

    @Bean
    CrearCotizacionUseCase crearCotizacion(
            ClienteRepository clienteRepository,
            VehiculoRepository vehiculoRepository,
            TablaTarifariaRepository tablaTarifariaRepository,
            CotizacionRepository cotizacionRepository,
            MotorDeTarificacion motorDeTarificacion) {
        return new CrearCotizacionUseCase(
                clienteRepository,
                vehiculoRepository,
                tablaTarifariaRepository,
                cotizacionRepository,
                motorDeTarificacion);
    }

    @Bean
    ObtenerCotizacionUseCase obtenerCotizacion(CotizacionRepository cotizacionRepository) {
        return new ObtenerCotizacionUseCase(cotizacionRepository);
    }

    @Bean
    AceptarCotizacionUseCase aceptarCotizacion(CotizacionRepository cotizacionRepository) {
        return new AceptarCotizacionUseCase(cotizacionRepository);
    }

    @Bean
    ListarCotizacionesUseCase listarCotizaciones(CotizacionRepository cotizacionRepository) {
        return new ListarCotizacionesUseCase(cotizacionRepository);
    }

    @Bean
    ListarCotizacionesPendientesEmisionUseCase listarCotizacionesPendientes(
            CotizacionRepository cotizacionRepository, PolizaRepository polizaRepository) {
        return new ListarCotizacionesPendientesEmisionUseCase(
                cotizacionRepository, polizaRepository);
    }

    @Bean
    EmitirPolizaUseCase emitirPoliza(
            CotizacionRepository cotizacionRepository,
            PolizaRepository polizaRepository,
            DomainEventPublisherPort eventPublisher,
            ClockPort clock,
            IdGeneratorPort ids) {
        return new EmitirPolizaUseCase(
                cotizacionRepository, polizaRepository, eventPublisher, clock, ids);
    }

    @Bean
    ObtenerPolizaUseCase obtenerPoliza(PolizaRepository polizaRepository) {
        return new ObtenerPolizaUseCase(polizaRepository);
    }

    @Bean
    ListarPolizasUseCase listarPolizas(PolizaRepository polizaRepository) {
        return new ListarPolizasUseCase(polizaRepository);
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
    ClienteContactPort clienteContactPort(ClienteRepository clienteRepository) {
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
            PolizaRepository polizaRepository, SiniestroRepository siniestroRepository) {
        return new RegistrarSiniestroUseCase(polizaRepository, siniestroRepository);
    }

    @Bean
    ActualizarEstadoSiniestroUseCase actualizarSiniestro(
            PolizaRepository polizaRepository, SiniestroRepository siniestroRepository) {
        return new ActualizarEstadoSiniestroUseCase(polizaRepository, siniestroRepository);
    }

    @Bean
    ListarSiniestrosUseCase listarSiniestros(SiniestroRepository siniestroRepository) {
        return new ListarSiniestrosUseCase(siniestroRepository);
    }

    @Bean
    EvaluarRenovacionUseCase evaluarRenovacion(
            PolizaRepository polizaRepository,
            SiniestroRepository siniestroRepository,
            RenovacionRepository renovacionRepository,
            EvaluadorRenovacion evaluadorRenovacion,
            CalculadorPrimaRenovacion calculadorPrimaRenovacion,
            PoliticaVariacionPrima politicaVariacionPrima) {
        return new EvaluarRenovacionUseCase(
                polizaRepository,
                siniestroRepository,
                renovacionRepository,
                evaluadorRenovacion,
                calculadorPrimaRenovacion,
                politicaVariacionPrima);
    }

    @Bean
    ObtenerRenovacionUseCase obtenerRenovacion(RenovacionRepository renovacionRepository) {
        return new ObtenerRenovacionUseCase(renovacionRepository);
    }

    @Bean
    AprobarRenovacionUseCase aprobarRenovacion(RenovacionRepository renovacionRepository) {
        return new AprobarRenovacionUseCase(renovacionRepository);
    }

    @Bean
    RechazarRenovacionUseCase rechazarRenovacion(RenovacionRepository renovacionRepository) {
        return new RechazarRenovacionUseCase(renovacionRepository);
    }

    @Bean
    GenerarPolizaRenovadaUseCase generarRenovada(
            RenovacionRepository renovacionRepository, PolizaRepository polizaRepository) {
        return new GenerarPolizaRenovadaUseCase(renovacionRepository, polizaRepository);
    }

    @Bean
    ListarRenovacionesUseCase listarRenovaciones(RenovacionRepository renovacionRepository) {
        return new ListarRenovacionesUseCase(renovacionRepository);
    }

    @Bean
    ListarHistorialRenovacionesUseCase historial(
            PolizaRepository polizaRepository, RenovacionRepository renovacionRepository) {
        return new ListarHistorialRenovacionesUseCase(polizaRepository, renovacionRepository);
    }
}
