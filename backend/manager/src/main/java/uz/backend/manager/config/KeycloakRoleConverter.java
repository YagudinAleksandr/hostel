package uz.backend.manager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Достаёт роли из access token Keycloak и превращает их в authorities
 * Spring Security с префиксом {@code ROLE_}.
 * <p>
 * Читает оба места, куда Keycloak кладёт роли:
 * <pre>
 * "realm_access":    { "roles": ["hostel-admin"] }
 * "resource_access": { "hostel-web": { "roles": ["room-manager"] } }
 * </pre>
 * Роли реалма берутся всегда. Клиентские — у клиентов из
 * {@code keycloak.roles.clients}; если список пуст, у всех, кроме служебных
 * клиентов самого Keycloak ({@code account}, {@code realm-management} и прочих),
 * чьи роли к прикладной авторизации отношения не имеют.
 * <p>
 * Роли реалма и клиента складываются в одно пространство имён: {@code hostel-admin}
 * даст {@code ROLE_hostel-admin} независимо от того, откуда пришла. Одноимённые
 * роли из разных источников схлопнутся в одну authority.
 *
 * @author Aleksandr Yagudin
 */
@Component
@RequiredArgsConstructor
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    /** Клиенты, которые Keycloak заводит сам; их роли не про наш домен. */
    private static final Set<String> INTERNAL_CLIENTS = Set.of(
            "account", "account-console", "broker", "realm-management", "security-admin-console");

    private final KeycloakRolesProperties properties;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        final Set<String> roles = new LinkedHashSet<>();

        roles.addAll(rolesOf(jwt.getClaimAsMap(REALM_ACCESS)));

        final Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);
        if (resourceAccess != null) {
            resourceAccess.forEach((clientId, claim) -> {
                if (accepts(clientId) && claim instanceof Map<?, ?> clientClaim) {
                    roles.addAll(rolesOf(clientClaim));
                }
            });
        }

        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .toList();
    }

    private boolean accepts(String clientId) {
        return properties.clients().isEmpty()
                ? !INTERNAL_CLIENTS.contains(clientId)
                : properties.clients().contains(clientId);
    }

    private List<String> rolesOf(Map<?, ?> claim) {
        if (claim == null || !(claim.get(ROLES) instanceof Collection<?> roles)) {
            return List.of();
        }
        return roles.stream().map(String::valueOf).toList();
    }
}
