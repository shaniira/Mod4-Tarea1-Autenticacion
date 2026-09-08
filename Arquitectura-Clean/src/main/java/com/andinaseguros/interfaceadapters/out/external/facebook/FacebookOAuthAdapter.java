package com.andinaseguros.interfaceadapters.out.external.facebook;

import com.andinaseguros.entities.exception.ReglaNegocioException;
import com.andinaseguros.usecases.port.out.facebook.FacebookOAuthPort;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class FacebookOAuthAdapter implements FacebookOAuthPort {
    private final RestClient client;
    private final FacebookProperties properties;

    public FacebookOAuthAdapter(RestClient client, FacebookProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    public String authorizationUrl(String state) {
        requireConfiguration();
        return properties.authorizationUri() + "?client_id=" + encode(properties.appId())
                + "&redirect_uri=" + encode(properties.redirectUri()) + "&response_type=code&state="
                + encode(state) + "&scope=" + encode(properties.scopes());
    }

    public FacebookIdentity exchangeCode(String code) {
        requireConfiguration();
        try {
            TokenResponse token = client.get().uri(properties.tokenUri() + "?client_id=" + encode(properties.appId())
                    + "&client_secret=" + encode(properties.appSecret()) + "&redirect_uri="
                    + encode(properties.redirectUri()) + "&code=" + encode(code)).retrieve().body(TokenResponse.class);
            if (token == null || token.access_token() == null) throw new IllegalStateException();
            ProfileResponse profile = client.get().uri(properties.graphBaseUrl() + "/me?fields=id,name,email&access_token="
                    + encode(token.access_token())).retrieve().body(ProfileResponse.class);
            if (profile == null) throw new IllegalStateException();
                PermissionsResponse permissions = client.get().uri(properties.graphBaseUrl()
                    + "/me/permissions?access_token=" + encode(token.access_token()))
                    .retrieve().body(PermissionsResponse.class);
                Set<String> scopes = permissions == null || permissions.data() == null ? Set.of()
                    : permissions.data().stream().filter(permission -> "granted".equals(permission.status()))
                        .map(PermissionData::permission).collect(java.util.stream.Collectors.toUnmodifiableSet());
            return new FacebookIdentity(profile.id(), profile.email(), profile.name(), token.access_token(),
                    token.expires_in() == null ? null : Instant.now().plusSeconds(token.expires_in()), scopes);
        } catch (RestClientException | IllegalStateException exception) {
            throw new ReglaNegocioException("FACEBOOK_NO_DISPONIBLE", "No fue posible validar Facebook");
        }
    }

    private void requireConfiguration() {
        if (isBlank(properties.appId()) || isBlank(properties.appSecret()) || isBlank(properties.redirectUri())
                || isBlank(properties.authorizationUri()) || isBlank(properties.tokenUri()) || isBlank(properties.graphBaseUrl())) {
            throw new ReglaNegocioException("FACEBOOK_CONFIGURACION_INVALIDA", "Facebook no está configurado");
        }
    }

    private boolean isBlank(String value) { return value == null || value.isBlank(); }
    private String encode(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
    private record TokenResponse(String access_token, Long expires_in) {}
    private record ProfileResponse(String id, String name, String email) {}
    private record PermissionsResponse(List<PermissionData> data) {}
    private record PermissionData(String permission, String status) {}
}