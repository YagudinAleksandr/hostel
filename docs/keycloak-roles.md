# Роли и доступы через Keycloak

Документ описывает, как в проекте `hostel` устроены роли: где они заводятся,
как попадают в токен, как превращаются в права Spring Security и как их
применять в коде.

## 1. Что где живёт

| Компонент | Где | Порт |
|---|---|---|
| Keycloak | `backend/docker-compose.yaml`, сервис `keycloak` | 8180 (внутри контейнера 8080) |
| Реалм `hostel` | `backend/keycloak/realm-hostel.json`, импортируется при старте | — |
| Проверка токена | `manager`, OAuth2 resource server | 8710 |
| Разбор ролей | `manager/config/KeycloakRoleConverter.java` | — |
| Получение токена сервисом | `worker/config/ManagerClientConfig.java` | 8610 |

Админ-консоль: <http://localhost:8180>, логин `admin` / `admin`.
После входа переключитесь с реалма `master` на `hostel` — селектор вверху слева.

## 2. Два вида ролей

Keycloak различает роли реалма и роли клиента и кладёт их в разные места токена.

**Роль реалма** — общая для всего реалма, не привязана к приложению:

```json
"realm_access": { "roles": ["hostel-admin", "hostel-user"] }
```

**Роль клиента** — принадлежит конкретному clientId:

```json
"resource_access": {
  "hostel-web":    { "roles": ["room-manager"] },
  "hostel-worker": { "roles": ["process-writer"] }
}
```

Практическое правило: если право осмысленно для всей системы (`hostel-admin`) —
роль реалма. Если оно про конкретное приложение и в другом контексте
бессмысленно (`room-manager` только во фронтенде) — роль клиента.

## 3. Как роль становится правом в коде

`KeycloakRoleConverter` читает **оба** места и складывает результат в одно
пространство имён с префиксом `ROLE_`. Поведение на реальном токене:

```
claim realm_access:    hostel-admin, hostel-user
claim resource_access: hostel-web    -> room-manager, hostel-user
                       hostel-worker -> process-writer
                       account       -> view-profile, manage-account

authorities: [ROLE_hostel-admin, ROLE_hostel-user, ROLE_process-writer, ROLE_room-manager]
```

Три вещи, которые здесь произошли:

- Роли реалма и клиентов **объединились**. `hostel-admin` из реалма и
  `room-manager` из клиента дают authorities одного вида — источник в имени
  не отражается.
- Дубликаты **схлопнулись**. `hostel-user` был и в реалме, и в `hostel-web` —
  authority одна.
- Клиент `account` **отброшен**. Keycloak заводит его сам, вместе с
  `account-console`, `broker`, `realm-management`, `security-admin-console`.
  Их роли к домену отношения не имеют и в authorities не попадают.

### Ограничить список клиентов

По умолчанию берутся роли всех клиентов, кроме служебных. Если нужно сузить —
в `manager.yaml`:

```yaml
keycloak:
  roles:
    clients: hostel-web,hostel-worker
```

Тогда роли остальных клиентов игнорируются. Пустое значение или отсутствие
свойства — поведение по умолчанию.

### Следствие: имена ролей глобальны

Раз всё сливается в одно пространство, роль `admin` у клиента `hostel-web` и
роль `admin` реалма — одно и то же право. Поэтому давайте ролям осмысленные
имена (`room-manager`, а не `manager`) и не заводите одноимённые роли
в разных клиентах с разным смыслом.

## 4. Как добавить роль

### Через консоль (разовые эксперименты)

- Роль реалма: **Realm roles → Create role**.
- Роль клиента: **Clients → нужный клиент → Roles → Create role**.

### Через `realm-hostel.json` (постоянные роли)

Роли реалма — в массив `roles.realm`, роли клиентов — в объект `roles.client`,
ключ которого равен clientId:

```json
"roles": {
  "realm": [
    { "name": "hostel-admin",   "description": "Полный доступ" },
    { "name": "hostel-user",    "description": "Обычный пользователь" },
    { "name": "hostel-service", "description": "Межсервисный доступ" }
  ],
  "client": {
    "hostel-web": [
      { "name": "room-manager", "description": "Управление номерами" }
    ]
  }
}
```

**Важно про импорт.** Keycloak запущен как `start-dev --import-realm`, данные
лежат во встроенной H2 внутри контейнера, без volume. Отсюда:

- `docker compose stop` / `start` — данные сохраняются, правки из консоли на месте;
- `docker compose down` — контейнер удаляется вместе с базой, при следующем
  `up` реалм импортируется из JSON заново;
- существующий реалм `--import-realm` **не перезаписывает**. Правки в JSON
  применятся только после `down`.

Поэтому: то, что нужно всей команде, — в JSON; то, что нужно на пять минут, —
в консоли.

### Через CLI

```bash
docker compose exec keycloak /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080 --realm master --user admin --password admin

docker compose exec keycloak /opt/keycloak/bin/kcadm.sh create roles \
  -r hostel -s name=hostel-auditor -s "description=Только чтение журнала"
```

## 5. Как назначить роль

### Пользователю

Консоль: **Users → пользователь → Role mapping → Assign role**. В фильтре
переключите «Filter by realm roles» / «Filter by clients» — по умолчанию виден
только один из списков.

JSON: поля `realmRoles` и `clientRoles` в записи пользователя.

```json
{
  "username": "ivanov",
  "enabled": true,
  "credentials": [{ "type": "password", "value": "ivanov", "temporary": false }],
  "realmRoles": ["hostel-user"],
  "clientRoles": { "hostel-web": ["room-manager"] }
}
```

Снимайте «Temporary» у пароля, иначе Keycloak потребует сменить его при первом входе.

CLI:
```bash
docker compose exec keycloak /opt/keycloak/bin/kcadm.sh add-roles \
  -r hostel --uusername ivanov --rolename hostel-user
```

### Сервисному аккаунту

У конфиденциального клиента с `serviceAccountsEnabled` есть собственный
«пользователь» — `service-account-<clientId>`. Роли назначаются там же:
**Clients → hostel-worker → Service accounts roles**.

В JSON он описывается как обычный пользователь с полем `serviceAccountClientId`:

```json
{
  "username": "service-account-hostel-worker",
  "enabled": true,
  "serviceAccountClientId": "hostel-worker",
  "realmRoles": ["hostel-service"]
}
```

## 6. Как использовать в коде

### На уровне URL — `SecurityConfig`

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
        .requestMatchers("/api/v1/processes/*/update").hasRole("hostel-service")
        .requestMatchers(HttpMethod.POST, "/api/v1/rooms/**").hasRole("room-manager")
        .anyRequest().authenticated())
```

### На уровне метода

`@EnableMethodSecurity` включён, поэтому работает:

```java
@PreAuthorize("hasRole('hostel-admin')")
public void deleteRoom(Long id) { ... }

@PreAuthorize("hasAnyRole('hostel-admin', 'room-manager')")
public RoomDto update(Long id, RoomDto dto) { ... }
```

### `hasRole` против `hasAuthority`

`hasRole("hostel-admin")` сам добавляет префикс и сверяется с
`ROLE_hostel-admin`. `hasAuthority("hostel-admin")` префикс не добавляет и
**не сработает** — конвертер выдаёт authorities с префиксом. Пишите `hasRole`
и имя роли без `ROLE_`.

### Данные о пользователе

```java
@GetMapping("/me")
public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
    return Map.of(
            "sub", jwt.getSubject(),
            "username", jwt.getClaimAsString("preferred_username"),
            "email", jwt.getClaimAsString("email"));
}
```

## 7. Текущая конфигурация реалма

**Клиенты**

| clientId | Тип | Назначение |
|---|---|---|
| `hostel-web` | публичный | фронтенд; включены standard flow и direct access grants |
| `hostel-worker` | конфиденциальный | service account для worker → manager, секрет `worker-secret` |

**Роли реалма**

| Роль | Смысл |
|---|---|
| `hostel-admin` | полный доступ |
| `hostel-user` | обычный пользователь |
| `hostel-service` | межсервисный доступ, выдана сервисному аккаунту worker'а |

**Тестовые пользователи** (только для локальной разработки)

| Логин | Пароль | Роли |
|---|---|---|
| `admin-user` | `admin-user` | `hostel-admin`, `hostel-user` |
| `plain-user` | `plain-user` | `hostel-user` |

## 8. Как проверить, что в токене

Токен от имени пользователя:

```bash
curl -s -d "client_id=hostel-web" -d "grant_type=password" \
     -d "username=admin-user" -d "password=admin-user" \
     http://localhost:8180/realms/hostel/protocol/openid-connect/token
```

Токен от имени сервиса — то же, что делает worker:

```bash
curl -s -d "client_id=hostel-worker" -d "client_secret=worker-secret" \
     -d "grant_type=client_credentials" \
     http://localhost:8180/realms/hostel/protocol/openid-connect/token
```

Посмотреть содержимое (payload — вторая часть, разделитель `.`):

```bash
echo "$TOKEN" | cut -d. -f2 | base64 -d 2>/dev/null | python -m json.tool
```

Обратиться к manager'у:

```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8710/api/v1/processes
```

`401` — токен не принят: истёк, подписан другим реалмом или не передан.
`403` — токен валиден, но роли не хватает; смотрите `realm_access` и
`resource_access` в payload.

## 9. Подводные камни

**Роли не появились в токене.** Клиентские роли попадают в `resource_access`
только если клиент входит в аудиторию токена. Если роль назначена, а в токене
её нет — проверьте **Clients → клиент → Client scopes → dedicated → Scope**,
включён ли `Full scope allowed`.

**Composite roles.** Роль может включать другие роли (**Realm roles → роль →
Action → Add associated roles**). Keycloak разворачивает состав при выдаче
токена, так что конвертер увидит и вложенные роли — отдельной поддержки не нужно.

**Manager не стартует без Keycloak.** В `manager.yaml` задан `issuer-uri`,
Spring на старте идёт за метаданными реалма. Если Keycloak не поднят,
приложение падает при инициализации. Поднимайте Keycloak первым либо
переключайтесь на `jwk-set-uri` — он ленивый, но тогда claim `iss` не проверяется
автоматически.

**Worker стартует независимо.** У него намеренно указан `token-uri`, а не
`issuer-uri`: для `client_credentials` нужен только token endpoint, discovery
на старте не выполняется.

**Секреты в репозитории.** `realm-hostel.json` содержит пароли тестовых
пользователей и секрет клиента открытым текстом. Это допустимо для локального
реалма и недопустимо для стенда: туда реалм переносится отдельным экспортом,
без пользователей, а `worker-secret` подставляется через переменную
`KEYCLOAK_WORKER_SECRET`.
