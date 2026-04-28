# Fluxo de Autenticação Google OAuth2

## Base URL

```
https://climb-api-dev.72.61.38.51.sslip.io
```

---

## 🔐 Cenário 1: Usuário EXISTENTE (Login)

### Passo 1: Obter URL de autorização do Google

```http
GET /auth/google/url
```

**Response:**

```json
{
  "authorizationUrl": "https://accounts.google.com/o/oauth2/v2/auth?client_id=...&redirect_uri=...&scope=..."
}
```

### Passo 2: Redirecionar usuário

```javascript
window.location.href = response.authorizationUrl;
```

### Passo 3: Callback automático

O Google redireciona para a API, que processa e redireciona para o frontend:

```
https://dev-climb-app.vercel.app?google_oauth=success&code=CODIGO_TEMPORARIO
```

### Passo 4: Trocar código por tokens

```http
POST /auth/exchange
Content-Type: application/json

{
  "code": "CODIGO_TEMPORARIO"
}
```

**Response (usuário existente):**

```json
{
  "success": true,
  "message": "Tokens obtidos com sucesso",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400,
    "googleAccessToken": "ya29.a0AfH6SMB...",
    "googleRefreshToken": "1//0eXXXXXXXXXXXX",
    "usuario": {
      "id": 1,
      "nomeCompleto": "João Silva",
      "cpf": "123.456.789-00",
      "email": "joao@email.com",
      "contato": "(11) 99999-9999",
      "situacao": "ATIVO",
      "cargoNome": "Administrador"
    }
  }
}
```

✅ **Login completo!** Salve os tokens e redirecione para o dashboard.

---

## 📝 Cenário 2: Usuário NOVO (Cadastro)

### Passos 1-3: Igual ao login

### Passo 4: Trocar código por tokens

```http
POST /auth/exchange
Content-Type: application/json

{
  "code": "CODIGO_TEMPORARIO"
}
```

**Response (usuário novo - precisa completar cadastro):**

```json
{
  "success": true,
  "message": "Tokens obtidos com sucesso",
  "data": {
    "accessToken": null,
    "refreshToken": null,
    "expiresIn": null,
    "googleAccessToken": "ya29.a0AfH6SMB...",
    "googleRefreshToken": "1//0eXXXXXXXXXXXX",
    "usuario": null,
    "pendingToken": "abc123xyz...",
    "email": "novo@email.com",
    "nome": "Nome do Google"
  }
}
```

⚠️ **Usuário novo detectado!** Redirecione para tela de completar cadastro.

### Passo 5: Completar cadastro

```http
POST /auth/google/complete-registration
Content-Type: application/json

{
  "pendingToken": "abc123xyz...",
  "cpf": "123.456.789-00",
  "contato": "(11) 99999-9999",
  "senha": "senha123",
  "cargoId": 1
}
```

**Response:**

```json
{
  "success": true,
  "message": "Cadastro Google concluido com sucesso",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400,
    "usuario": {
      "id": 2,
      "nomeCompleto": "Nome do Google",
      "cpf": "123.456.789-00",
      "email": "novo@email.com",
      "contato": "(11) 99999-9999",
      "situacao": "ATIVO",
      "cargoNome": "Usuário"
    }
  }
}
```

✅ **Cadastro completo!**

---

## 🔄 Renovar Token (quando expirar)

```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**

```json
{
  "success": true,
  "message": "Token renovado com sucesso",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## ❌ Tratamento de Erros

Se houver erro no OAuth, o redirect será:

```
https://dev-climb-app.vercel.app?google_oauth=error&message=Descrição+do+erro
```

---

## 📊 Fluxograma Visual

```
┌─────────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                     │
├─────────────────────────────────────────────────────────────────────┤
│  1. Clica "Login com Google"                                        │
│           │                                                          │
│           ▼                                                          │
│  2. GET /auth/google/url                                            │
│           │                                                          │
│           ▼                                                          │
│  3. Redireciona para Google ──────────────────┐                     │
│                                                │                     │
│  4. Google autentica ◄────────────────────────┘                     │
│           │                                                          │
│           ▼                                                          │
│  5. Callback → API processa → Redireciona para Frontend             │
│           │                                                          │
│           ▼                                                          │
│  6. Captura ?code=xxx da URL                                        │
│           │                                                          │
│           ▼                                                          │
│  7. POST /auth/exchange { code }                                    │
│           │                                                          │
│           ▼                                                          │
│  8. Verifica resposta:                                              │
│      ├── usuario != null → Login OK → Dashboard                     │
│      └── usuario == null → Novo usuário → Tela de Cadastro          │
│                                    │                                 │
│                                    ▼                                 │
│                    9. POST /auth/google/complete-registration       │
│                                    │                                 │
│                                    ▼                                 │
│                              Dashboard                               │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 💻 Exemplo de Implementação (React)

```typescript
// 1. Iniciar login com Google
async function loginWithGoogle() {
  const response = await fetch('/auth/google/url');
  const data = await response.json();
  window.location.href = data.authorizationUrl;
}

// 2. Na página de callback (useEffect no componente principal)
useEffect(() => {
  const params = new URLSearchParams(window.location.search);
  const code = params.get('code');
  const googleOauth = params.get('google_oauth');
  
  if (googleOauth === 'success' && code) {
    exchangeCode(code);
  } else if (googleOauth === 'error') {
    const message = params.get('message');
    showError(message);
  }
}, []);

// 3. Trocar código por tokens
async function exchangeCode(code: string) {
  const response = await fetch('/auth/exchange', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ code })
  });
  
  const data = await response.json();
  
  if (data.data.usuario) {
    // Usuário existente - login completo
    saveTokens(data.data.accessToken, data.data.refreshToken);
    navigate('/dashboard');
  } else {
    // Usuário novo - precisa completar cadastro
    navigate('/complete-registration', { 
      state: { 
        pendingToken: data.data.pendingToken,
        email: data.data.email,
        nome: data.data.nome
      }
    });
  }
}
```

---

## 🔒 Notas de Segurança

- O código temporário (`code`) expira em **60 segundos**
- O código só pode ser usado **uma única vez**
- Tokens sensíveis nunca são expostos na URL
- O `pendingToken` para cadastro também tem validade limitada
