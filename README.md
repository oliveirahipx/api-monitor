# API Monitor

Aplicação web para testar e monitorar APIs REST. Você informa uma ou várias URLs, o backend faz a requisição, mede o tempo de resposta, confere o status HTTP e o `Content-Type`, e guarda o resultado no PostgreSQL. A interface mostra o histórico, um resumo com gráfico e gera relatório em PDF.

Backend em Spring Boot e front-end em HTML, SCSS e JavaScript puro. Projeto acadêmico do curso de Ciência da Computação.

## Como rodar

Você precisa de Java 17 ou mais novo e de um PostgreSQL. Para subir o banco com Docker, na raiz do repositório:

```bash
docker compose up -d
```

Isso cria um Postgres 16 em `localhost:5432` com o banco `BD_Test-API`, aceitando conexões locais sem senha (configuração só para desenvolvimento).

Depois baixe `api-monitor-1.0.0.jar` na [página de releases](https://github.com/oliveirahipx/api-monitor/releases/latest) e rode:

```bash
java -jar api-monitor-1.0.0.jar
```

Abra http://localhost:8080 e entre com o usuário de demonstração, criado automaticamente quando a tabela de usuários está vazia:

- e-mail: `demo@exemplo.com`
- senha: `123456`

As tabelas são criadas pelo Hibernate na primeira execução (`ddl-auto=update`).

### Configuração do banco

| Variável | Padrão |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/BD_Test-API` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | vazio |

Se a porta 5432 já estiver em uso, suba o banco em outra porta e aponte a aplicação para ela:

```bash
POSTGRES_PORT=5433 docker compose up -d
DB_URL=jdbc:postgresql://localhost:5433/BD_Test-API java -jar api-monitor-1.0.0.jar
```

## Funcionalidades

- Teste de uma URL (`GET`) com três verificações: status 2xx, tempo de resposta dentro do limite e `Content-Type` contendo `application/json`. O teste só conta como sucesso se as três passarem.
- Teste em lote: várias URLs de uma vez (uma por linha), executadas em sequência.
- Erros tratados separadamente: 4xx do cliente, falha de conexão/timeout e demais erros.
- Histórico de todos os testes, do mais recente para o mais antigo.
- Resumo com total de testes, sucessos, falhas e gráfico (Chart.js) de testes por URL em cada dia da semana.
- Exportação do histórico em PDF (iText 7).
- Login e tela de Configurações para editar nome, e-mail, projeto, URL base e senha.

O limite de latência fica na constante `MAX_LATENCY_MS` em `ApiTestService` (hoje 50.000 ms).

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/test?url=...` | Testa uma URL e salva o resultado |
| `POST` | `/api/test/multiple` | Testa uma lista de URLs. Corpo: `{"apiUrls": ["https://..."]}` |
| `GET` | `/api/results` | Lista todos os resultados |
| `GET` | `/api/summary` | Totais e dados do gráfico |
| `GET` | `/api/export/pdf` | Relatório em PDF |
| `POST` | `/api/auth/login` | Login. Corpo: `{"email": "...", "password": "..."}` |
| `GET` | `/api/auth/user/{email}` | Dados do usuário |
| `PUT` | `/api/auth/user/update` | Atualiza os dados do usuário |

Exemplo:

```bash
curl "http://localhost:8080/api/test?url=https://jsonplaceholder.typicode.com/todos/1"
```

## Tecnologias

- Java 17, Spring Boot 3.5 (Web, Data JPA), Hibernate, Lombok
- PostgreSQL
- iText 7 para o PDF
- HTML, SCSS (Bootstrap 5) e JavaScript com módulos ES6
- Chart.js e Bootstrap Icons

## Compilar a partir do código

O Maven vem pelo wrapper. Os testes sobem o contexto do Spring, então o Postgres precisa estar rodando:

```bash
cd api-tester
./mvnw package          # Linux/macOS
mvnw.cmd package        # Windows
```

O jar sai em `api-tester/target/api-monitor-1.0.0.jar`, já com o front-end dentro (o Maven copia a pasta `frontend/` para os recursos estáticos). Para compilar sem banco, use `./mvnw package -DskipTests`.

Durante o desenvolvimento, `./mvnw spring-boot:run` sobe o backend e o front juntos em http://localhost:8080. O CSS já compilado está em `frontend/dist/css/main.css`; as fontes SCSS ficam em `frontend/assets/scss/`.

## Estrutura

```
docker-compose.yml          Postgres para desenvolvimento
api-tester/
  src/main/java/com/seumonitor/api_tester/
    Controller/             ApiTestController e AuthController (REST)
    Service/                lógica dos testes, PDF e usuários
    Repository/             Spring Data JPA
    Model/                  entidades ApiTestResult e User
    DTO/                    objetos de requisição e resposta
  src/main/resources/application.properties
  frontend/
    index.html              login
    pages/                  resumo, APIs, resultados e configurações
    assets/js/              módulos JavaScript
    assets/scss/            estilos
    dist/css/main.css       CSS compilado
```

## Limitações conhecidas

- A autenticação é simples: a senha fica em texto puro no banco e não há token nem sessão; o front guarda o e-mail no `localStorage`.
- O CORS está liberado para qualquer origem e o endpoint de teste faz requisições para qualquer URL informada. Use só em ambiente local.
- Os botões de filtro (API, Período, Status) das telas ainda não filtram.
