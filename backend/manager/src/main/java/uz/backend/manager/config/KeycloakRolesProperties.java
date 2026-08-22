package uz.backend.manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Настройка разбора ролей из токена Keycloak.
 *
 * @param clients список clientId, чьи клиентские роли попадают в authorities.
 *                Пустой список — брать роли всех клиентов из {@code resource_access},
 *                кроме служебных клиентов самого Keycloak.
 * @author Aleksandr Yagudin
 */
@ConfigurationProperties(prefix = "keycloak.roles")
public record KeycloakRolesProperties(List<String> clients) {

    public KeycloakRolesProperties {
        clients = clients == null ? List.of() : List.copyOf(clients);
    }
}
