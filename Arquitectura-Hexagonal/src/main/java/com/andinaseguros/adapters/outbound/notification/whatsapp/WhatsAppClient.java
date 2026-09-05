package com.andinaseguros.adapters.outbound.notification.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;

public class WhatsAppClient {
    private static final Logger log = LoggerFactory.getLogger(WhatsAppClient.class);
    private final RestClient client;
    private final WhatsAppProperties properties;

    public WhatsAppClient(RestClient client, WhatsAppProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    WhatsAppTextResponse enviarTexto(WhatsAppTextRequest request) {
        log.debug(
                "Invocando WhatsApp API endpoint=/send/text destinatario={}",
                enmascarar(request.number()));
        WhatsAppTextResponse response =
                client.post()
                        .uri("/send/text")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(WhatsAppTextResponse.class);
        log.debug(
                "Respuesta recibida de WhatsApp API success={} detalle={}",
                response != null && response.success(),
                response == null ? "respuesta-vacia" : response.message());
        return response;
    }

    private static String enmascarar(String numero) {
        if (numero == null || numero.isBlank()) return "sin-numero";
        int visibles = Math.min(4, numero.length());
        return "***" + numero.substring(numero.length() - visibles);
    }
}
