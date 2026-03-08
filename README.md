# Climb Services API

API REST construída com Spring Boot 4 e Java 25.

## Pré-requisitos

| Dependência | Versão mínima |
|---|---|
| Java (OpenJDK) | 25 |

O Maven não precisa ser instalado separadamente — o projeto inclui o Maven Wrapper (`mvnw`).

## Dependências do projeto

Gerenciadas automaticamente pelo Maven via `pom.xml`:

| Dependência | Descrição |
|---|---|
| `spring-boot-starter-webmvc` | Framework web (Tomcat embutido + Spring MVC) |
| `spring-boot-starter-validation` | Validação de beans (Jakarta Validation) |
| `spring-boot-starter-actuator` | Endpoints de monitoramento |
| `spring-boot-devtools` | Restart automático durante desenvolvimento |
| `lombok` | Redução de boilerplate |

## Comandos

### Compilar

```bash
./mvnw compile
```

### Executar testes

```bash
./mvnw test
```

### Rodar localmente

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

Para usar uma porta diferente:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```

### Gerar o JAR

```bash
./mvnw package -DskipTests
java -jar target/climb-api-0.0.1-SNAPSHOT.jar
```

### Parar a aplicação

Pressione `Ctrl+C` no terminal. Se a porta continuar ocupada:

```bash
lsof -ti:8080 | xargs kill -9
```

## Endpoints

| Método | URL | Descrição |
|---|---|---|
| `GET` | `/api/hello` | Retorna "Hello, World!" |
| `GET` | `/actuator/health` | Status da aplicação |
| `GET` | `/actuator/info` | Informações da aplicação |
| `GET` | `/actuator/metrics` | Métricas disponíveis |

## Estrutura do projeto

```
src/main/java/com/climb/api/
├── ClimbApiApplication.java        # Entry point
├── config/
│   └── WebConfig.java              # Configuração de CORS
├── controller/
│   └── HelloController.java        # REST controller
├── model/dto/
│   └── ApiResponse.java            # Wrapper padrão de resposta
└── service/
    └── HelloService.java           # Camada de serviço
```
