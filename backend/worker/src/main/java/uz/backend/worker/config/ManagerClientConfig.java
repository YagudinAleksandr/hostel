package uz.backend.worker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

/**
 * HTTP-клиент к manager'у, автоматически добавляющий access token Keycloak.
 * <p>
 * Worker — фоновый сервис без входящих HTTP-запросов и без пользователя в контексте,
 * поэтому токен берётся по grant type {@code client_credentials}: сервис
 * аутентифицируется сам как {@code hostel-worker}. Токен кэшируется и
 * переполучается автоматически по истечении срока.
 *
 * @author Aleksandr Yagudin
 */
@Configuration
public class ManagerClientConfig {

    /** Идентификатор регистрации из {@code spring.security.oauth2.client.registration.*} */
    public static final String REGISTRATION_ID = "manager";

    /**
     * Менеджер авторизованных клиентов для фонового режима: в отличие от
     * веб-варианта не требует HTTP-запроса и сессии.
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        final var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);

        manager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build());

        return manager;
    }

    @Bean
    public RestClient managerRestClient(OAuth2AuthorizedClientManager authorizedClientManager,
                                        @Value("${manager.url}") String managerUrl) {

        final var interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(request -> REGISTRATION_ID);

        return RestClient.builder()
                .baseUrl(managerUrl)
                .requestInterceptor(interceptor)
                .build();
    }
}
