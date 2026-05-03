# Sumário do Rebase: feature/oauth2 → dev

## Objetivo
Integrar duas funcionalidades que evoluíram em branches paralelas:
1. **Google Calendar API** (branch `dev`)
2. **OAuth2 Login/Registration** (branch `feature/oauth2`)

---

## Arquivos Modificados

### 1. `SecurityConfig.java`
- Removida configuração `oauth2Login` do Spring Security (substituída por fluxo customizado)
- Removidas dependências dos handlers OAuth2 (`OAuth2AuthenticationSuccessHandler`, `OAuth2AuthenticationFailureHandler`)
- Removido bean duplicado `passwordEncoder()` (já definido em `PasswordConfig.java`)
- Mantida configuração de CORS para todas as origens e métodos
- Mantida integração do `JwtAuthenticationFilter`

### 2. `AuthController.java`
- Removidos endpoints que dependiam do OAuth2 login do Spring:
  - `GET /auth/google` (redirecionava para `/oauth2/authorization/google`)
  - `GET /auth/google/link` (vinculação de conta via Spring OAuth2)
- Mantidos endpoints do fluxo customizado:
  - `POST /auth/login` - Login tradicional
  - `POST /auth/refresh` - Renovação de token
  - `GET /auth/google/url` - Retorna URL de autorização Google
  - `GET /auth/google/callback` - Callback do OAuth Google
  - `POST /auth/google/complete-registration` - Completa cadastro pendente
- **Novo endpoint adicionado:**
  - `POST /auth/exchange` - Troca código temporário por tokens (padrão seguro)

### 3. `GoogleOAuthService.java`
- Mescladas implementações de ambas as branches
- **Constantes de status OAuth2:**
  - `STATUS_LOGIN_SUCCESS`, `STATUS_CADASTRO_PENDENTE`, `STATUS_GOOGLE_NOT_LINKED`, `STATUS_LINK_SUCCESS`
- **Métodos Google Calendar:**
  - `gerarUrlAutorizacao()`, `trocarCodePorToken()`, `gerarRedirecionamentoFrontend()`, `gerarRedirecionamentoErro()`, `isConfigured()`
- **Métodos OAuth2 Login:**
  - `autenticarUsuarioGoogle()`, `criarUsuarioGoogle()`, `obterNomeGoogle()`, `gerarCpfGoogle()`
  - `resolverLoginGoogle()`, `concluirCadastro()`, `vincularConta()`, `validarDadosGoogle()`, `limparPendenciasExpiradas()`

### 4. `application.yml`
- Removida configuração `spring.security.oauth2.client.registration.google` (causava erro quando credenciais não configuradas)
- Mantidas configurações JWT e Google Calendar
- Configuração de credenciais via variáveis de ambiente:
  - `GOOGLE_CALENDAR_CLIENT_ID`, `GOOGLE_CALENDAR_CLIENT_SECRET`
  - `GOOGLE_CALENDAR_REDIRECT_URI`, `GOOGLE_CALENDAR_FRONTEND_URL`

### 5. `pom.xml`
- Resolvidos conflitos de dependências
- Mantidas dependências:
  - `spring-boot-starter-oauth2-client`
  - `google-api-services-calendar`
  - `google-auth-library-oauth2-http`
  - `google-http-client-gson`

---

## Arquivos Criados (recuperados de origin/dev)

- `GoogleAuthorizationUrlResponseDTO.java` - DTO para URL de autorização
- `GoogleTokenResponseDTO.java` - DTO para resposta de token
- `GoogleCalendarConfig.java` - Configuração do Google Calendar API

## Arquivos Criados (refatoração de segurança)

- `OAuth2ExchangeCode.java` - Entidade para códigos temporários de troca
- `OAuth2ExchangeCodeRepository.java` - Repository para códigos de troca
- `ExchangeCodeRequestDTO.java` - DTO para requisição de troca
- `ExchangeCodeResponseDTO.java` - DTO para resposta de troca
- `V9__create_oauth2_exchange_codes.sql` - Migration para tabela de códigos

---

## Arquivos Deletados

- `ClimbApiApplicationTests.java` - Removido conforme commit "fix: removido o contextloads"

---

## Erros Resolvidos

| Erro | Causa | Solução |
|------|-------|---------|
| `Terminal is dumb, but EDITOR unset` | git rebase sem editor | `GIT_EDITOR=true git rebase --continue` |
| `CONFLICT (modify/delete)` em ClimbApiApplicationTests | Arquivo deletado vs modificado | `git rm` para aceitar deleção |
| `cannot find symbol` para DTOs | Arquivos ausentes após rebase | Recriados a partir de origin/dev |
| `Bean 'passwordEncoder' already defined` | Duplicação em SecurityConfig e PasswordConfig | Removido de SecurityConfig |
| `Client id of registration 'google' must not be empty` | Credenciais OAuth2 vazias | Removida registration do application.yml |

---

## Fluxo OAuth2 Atual (Seguro)

O fluxo OAuth2 usa implementação **customizada** via `GoogleOAuthService` com **troca segura de código temporário**:

```
Frontend                    Backend                         Google
   │                           │                              │
   │── GET /auth/google/url ──>│                              │
   │<── { authorizationUrl } ──│                              │
   │                           │                              │
   │── Redirect user ─────────────────────────────────────────>│
   │                           │                              │
   │<── Redirect with code ────────────────────────────────────│
   │                           │                              │
   │── GET /auth/google/callback?code=xxx ──>│                │
   │                           │── Exchange code ────────────>│
   │                           │<── Google tokens, user info ─│
   │                           │                              │
   │                           │ Salva tokens no DB com       │
   │                           │ código temporário (60s)      │
   │                           │                              │
   │<── Redirect: frontend?code=abc123 ──│                    │
   │                           │                              │
   │── POST /auth/exchange ───>│                              │
   │   { code: "abc123" }      │                              │
   │                           │                              │
   │<── { accessToken,         │                              │
   │      refreshToken,        │                              │
   │      googleAccessToken,   │                              │
   │      usuario }            │                              │
```

### Por que essa abordagem é mais segura?

| Antes (inseguro)                          | Agora (seguro)                           |
|-------------------------------------------|------------------------------------------|
| Tokens passados via URL query string      | Apenas código temporário na URL          |
| Tokens aparecem no histórico do navegador | Código expira em 60s, single-use         |
| Tokens podem ser logados por proxies      | Tokens enviados via response body (POST) |
| Vulnerável a shoulder surfing             | Código inútil após consumido             |

---

## Próximos Passos

1. Configurar variáveis de ambiente para Google Calendar:
   - `GOOGLE_CALENDAR_CLIENT_ID`
   - `GOOGLE_CALENDAR_CLIENT_SECRET`
   - `GOOGLE_CALENDAR_REDIRECT_URI`
   - `GOOGLE_CALENDAR_FRONTEND_URL`

2. Testar fluxo completo de autenticação Google
3. Testar integração com Google Calendar API
