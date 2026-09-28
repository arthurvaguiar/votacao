# API de Votação em Assembleias

API REST para gerenciar sessões de votação de cooperativas: cadastro de pautas, abertura de sessões, registro de votos e apuração do resultado. Inclui uma **API de telas** (server-driven UI) que devolve os JSONs `FORMULARIO` e `SELECAO` consumidos pelo aplicativo mobile, conforme o Anexo 1 do desafio.

## Stack

- Java 21 · Spring Boot 4 · Spring Data JPA · Bean Validation
- PostgreSQL 16 · Flyway
- JUnit 5 · Mockito · Testcontainers · H2 (testes) · MockRestServiceServer · JaCoCo
- k6 (teste de carga) · springdoc-openapi (Swagger)
- GitHub Actions (CI)

## Como executar

**Pré-requisitos:** Java 21 e Docker em execução.

```bash
./mvnw spring-boot:run        # Linux/macOS
.\mvnw.cmd spring-boot:run    # Windows
```

O Postgres sobe automaticamente pelo `docker-compose.yml` (suporte a Docker Compose do Spring Boot). As migrations do Flyway criam o schema na inicialização, e os dados persistem entre restarts, em um volume Docker.

A aplicação sobe em `http://localhost:8081`.

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8081/swagger-ui.html |
| Health check | http://localhost:8081/actuator/health |
| Tela inicial (app mobile) | http://localhost:8081/api/v1/telas |

O arquivo `http/pautas.http` tem as requisições prontas para executar no IntelliJ.

### Variáveis de ambiente

| Variável | Default | Descrição |
|---|---|---|
| `SERVER_PORT` | `8081` | Porta HTTP |
| `APP_BASE_URL` | `http://localhost:8081` | Domínio usado nas URLs dos botões das telas. Troque pelo IP da máquina para testar em emulador ou dispositivo físico |
| `DB_URL` / `DB_USER` / `DB_PASSWORD` | Postgres local | Conexão com o banco |
| `DB_POOL_SIZE` | `20` | Tamanho do pool de conexões |
| `USER_INFO_MODO` | `fake` | Validação de CPF: `fake`, `http` ou `desabilitado` (ver Bônus 1) |
| `USER_INFO_URL` | `https://user-info.herokuapp.com` | URL do serviço de CPF (modo `http`) |
| `USER_INFO_TIMEOUT` | `3s` | Timeout da chamada ao serviço de CPF |
| `USER_INFO_CHANCE_APTO` | `0.5` | Probabilidade de o associado poder votar (modo `fake`) |

## API de negócio

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/pautas` | Cadastra uma pauta |
| `GET` | `/api/v1/pautas/{id}` | Busca uma pauta |
| `GET` | `/api/v1/pautas` | Lista pautas (paginado) |
| `POST` | `/api/v1/pautas/{id}/sessao` | Abre a sessão de votação. Body opcional: `{"duracaoEmMinutos": 5}`; sem body, dura 1 minuto |
| `POST` | `/api/v1/pautas/{id}/votos` | Registra um voto: `{"associadoId": "19839091069", "voto": "Sim"}` |
| `GET` | `/api/v1/pautas/{id}/resultado` | Apuração: contagem, total e status |

**Regras:**
- Uma sessão por pauta.
- Só é possível votar com a sessão aberta.
- Um voto por associado por pauta.
- O voto aceita `Sim`/`Não`, sem diferenciar maiúsculas e acentos.
- O `associadoId` é o CPF do associado.
- O resultado é `EM_ANDAMENTO` enquanto a sessão está aberta, com contagem parcial, e `APROVADA`, `REPROVADA` ou `EMPATE` depois do encerramento.

## API de telas (app mobile)

O app mobile é genérico e só sabe desenhar telas `FORMULARIO` e `SELECAO`. O backend conduz toda a navegação: cada resposta traz a tela e as URLs dos botões, e o app faz `POST` nessas URLs enviando o `body` do botão junto com os valores digitados.

```
Início (SELECAO)
 ├─ Cadastrar pauta (FORMULARIO) → Pauta cadastrada → Abrir sessão
 └─ Pautas (SELECAO) → Pauta X (SELECAO)
                        ├─ Abrir sessão (FORMULARIO) → Sessão aberta → Votar
                        ├─ Votar: CPF (FORMULARIO) → Sim/Não (SELECAO) → Voto registrado
                        └─ Ver resultado (FORMULARIO, com botão Atualizar)
```

- **Ponto de entrada:** `GET /api/v1/telas`. As demais rotas aceitam `POST`, como o app faz ao acionar um botão.
- **Voto em dois passos:** o `FORMULARIO` tem no máximo dois botões. Por isso o associado informa o CPF primeiro e escolhe Sim/Não em uma `SELECAO`, e o CPF segue no `body` de cada opção.
- **Erros viram telas:** o app não sabe exibir outro formato, então erros de negócio e de validação retornam uma tela `FORMULARIO` com a mensagem e um botão "Início", mantendo o status HTTP correspondente (4xx/5xx).
- **URLs configuráveis:** o domínio dos botões vem de `APP_BASE_URL`.

## Tratamento de erros

A API de negócio responde erros no padrão **ProblemDetail (RFC 9457)**:

```json
{
  "title": "Conflito",
  "status": 409,
  "detail": "Associado 19839091069 já votou na pauta 3",
  "instance": "/api/v1/pautas/3/votos",
  "timestamp": "2026-09-28T12:00:00Z"
}
```

| Situação | Status |
|---|---|
| Campos inválidos (com lista `erros` por campo) ou JSON mal formado | 400 |
| Pauta ou sessão inexistente | 404 |
| Sessão já aberta ou voto duplicado | 409 |
| Sessão encerrada, CPF inválido ou associado não habilitado | 422 |
| Serviço de validação de CPF indisponível | 503 |
| Erro inesperado (sem stacktrace na resposta) | 500 |

As exceções de domínio não conhecem HTTP: elas herdam de três categorias (`RecursoNaoEncontrado`, `Conflito`, `RegraNegocio`), e só os handlers traduzem cada categoria para status e formato de resposta.

## Decisões de arquitetura

- **Pacotes por funcionalidade** (`pauta`, `sessao`, `voto`, `resultado`, `tela`, `associado`), e não por camada técnica. Cada funcionalidade acessa apenas o próprio repositório e conversa com as outras pelos services.
- **Unicidade garantida no banco:** `UNIQUE (pauta_id)` na sessão e `UNIQUE (pauta_id, associado_id)` no voto. Isso cobre também requisições simultâneas, sem lock na aplicação, e a violação vira 409.
- **`Clock` injetado:** as regras de tempo (sessão aberta ou encerrada) são testadas sem `Thread.sleep`.
- **Schema versionado com Flyway** e `ddl-auto: validate`: o Hibernate só confere se as entidades batem com o banco.
- **`open-in-view: false`**, para evitar consultas escondidas durante a serialização.
- **DTOs como `record`**, com validação por Bean Validation e mensagens em português.

## Bônus 1 — Validação de CPF em sistema externo

O serviço `https://user-info.herokuapp.com` **não está mais no ar**: ele responde `404 No such app` para qualquer CPF, desde o fim do plano gratuito do Heroku. Com o aval do LT responsável pelo desafio, a integração ganhou um **client fake** e três modos de funcionamento (`USER_INFO_MODO`):

| Modo | Comportamento |
|---|---|
| `fake` (default) | Valida os dígitos verificadores do CPF (inválido → 422) e sorteia `ABLE_TO_VOTE`/`UNABLE_TO_VOTE` como o serviço original. A chance é configurável em `USER_INFO_CHANCE_APTO` |
| `http` | Chama o serviço real com `RestClient` e timeout configurável |
| `desabilitado` | Não valida (usado no teste de carga) |

**Tratamento de falhas no modo `http`:**
- `404` da API → CPF inválido (422).
- `UNABLE_TO_VOTE` → associado não habilitado (422).
- Timeout, erro 5xx ou `404` com HTML (a página do Heroku para app inexistente) → **503**.
- **Fail closed:** se a validação está ligada e o serviço falha, o voto é recusado. Numa votação, é preferível pedir para o associado tentar de novo a aceitar alguém que não poderia votar.
- A validação externa roda **depois** das verificações locais (sessão aberta, voto duplicado) e **fora de transação**, para não segurar conexão do banco durante a chamada HTTP.
- O CPF aparece mascarado nos logs.

Os cenários são cobertos por testes com `MockRestServiceServer`.

## Bônus 2 — Performance

**Otimizações:**
- A apuração conta os votos no banco (`COUNT ... GROUP BY`), sem carregar votos em memória.
- O índice de cobertura `(pauta_id, opcao)` permite contar só pelo índice (*index-only scan*).
- O voto guarda apenas o `pauta_id`, sem relacionamento JPA, e por isso não carrega a pauta a cada voto.
- Virtual threads (Java 21) e pool de conexões configurável.

**Teste de carga (k6)**, com cada voto de um associado diferente e a validação de CPF desligada.

Ambiente: Intel Core i5-8300H (4 núcleos, 2,3 GHz) · 16 GB RAM · Windows, com aplicação, Postgres, IDE e k6 na mesma máquina.

| Carga | Vazão atingida | Erros | Mediana | p95 | Votos enviados = apurados |
|---|---|---|---|---|---|
| **150 votos/s** | 150/s | 0% | 13 ms | **29 ms** | 9.001 = 9.001 ✓ |
| 200 votos/s | 198/s | 0% | 33 ms | 301 ms | 11.933 = 11.933 ✓ |
| 400 votos/s (saturação) | 365/s | 0% | 517 ms | 1,45 s | 21.927 = 21.927 ✓ |

Nessa máquina, a aplicação sustenta **150 votos/s com p95 de 29 ms**. Acima de ~200/s ela satura e passa a enfileirar requisições, mas **sem erros e sem perder ou duplicar nenhum voto**.

**Apuração com 500 mil votos:** o Postgres executou a contagem em **~67 ms** com `Parallel Index Only Scan using idx_voto_pauta_opcao` e `Heap Fetches: 0`, ou seja, sem acessar a tabela.

**Como reproduzir:**
```bash
# Teste de carga (aplicação rodando com USER_INFO_MODO=desabilitado)
k6 run perf/votacao.js

# Massa de 500 mil votos e plano de execução
docker compose exec -T postgres psql -U votacao -d votacao < perf/massa.sql
docker compose exec postgres psql -U votacao -d votacao -c \
  "EXPLAIN ANALYZE SELECT opcao, count(*) FROM voto WHERE pauta_id = <ID> GROUP BY opcao;"
```

## Bônus 3 — Versionamento da API

A estratégia adotada é o **versionamento pela URI** (`/api/v1/...`), aplicado também às rotas de telas.

**Por quê:**
- É explícito e fácil de testar e depurar: a versão aparece na URL, nos logs e no Swagger.
- Funciona bem com app mobile. Versões antigas do app continuam instaladas nos celulares por muito tempo, então `v1` e `v2` precisam conviver lado a lado, e na URI isso é trivial de rotear, inclusive em gateways e caches.
- Headers ou media types (`Accept: application/vnd...v2+json`) deixam a URL mais limpa, mas escondem a versão e complicam o teste manual e o roteamento.

**Política:**
- Mudanças compatíveis, como campos novos opcionais, entram na versão atual.
- Mudanças incompatíveis, como remover ou renomear campos ou mudar regras, criam a `v2`, mantendo a `v1` até que as versões antigas do app deixem de ser usadas.
- A versão descontinuada é sinalizada com os headers `Deprecation` e `Sunset` antes de ser removida.

Como evolução, o Spring Framework 7 oferece versionamento nativo de API (`ApiVersionConfigurer`), que pode ser adotado mantendo a estratégia por URI.

## Testes

```bash
./mvnw verify    # testes unitários + integração + relatório de cobertura
```

- **Unitários:** regras de domínio (sessão, apuração, opção de voto, CPF) e services com Mockito e `Clock` fixo.
- **Controller (`@WebMvcTest`):** contrato HTTP, validações, formato dos erros e o JSON das telas no formato do Anexo 1.
- **Integração com PostgreSQL real (Testcontainers):** queries, índices e constraints no mesmo banco de produção (`VotoRepositoryIT`).
- **Integração ponta a ponta com H2 em memória:** fluxo completo da API REST (pauta, sessão, votos, duplicidade e apuração) e navegação pelas telas seguindo as URLs devolvidas, como o app mobile (`FluxoVotacaoIT`). Roda sem Docker e com o schema gerado pelo JPA. Por isso as regras que dependem do Postgres, como índices e `CHECK`, ficam com o Testcontainers.
- **Cliente HTTP:** cenários do serviço de CPF com `MockRestServiceServer`.
- **Cobertura:** 88% das instruções e 80% dos branches (relatório em `target/site/jacoco/index.html`).
- **CI:** GitHub Actions executa `./mvnw verify` a cada push.

## Logs

- `INFO` para ações de negócio (pauta criada, sessão aberta, voto registrado).
- `WARN` para erros de negócio, sem stacktrace.
- `ERROR` com stacktrace para falhas inesperadas e indisponibilidade de integração.
- CPF sempre mascarado.

## Evoluções possíveis

- **Circuit breaker** (Resilience4j) e retry curto na integração de CPF, para parar de chamar o serviço quando ele estiver fora.
- **Evento de encerramento da sessão** publicado em mensageria (ex.: Kafka), com o resultado para o restante da plataforma.
- **Cache do resultado** de sessões encerradas, que é imutável.
- **Autenticação** (OAuth2/JWT) e rate limiting.
- **Observabilidade** com métricas (Micrometer/Prometheus) e tracing distribuído.

## Estrutura

```
src/main/java/br/com/cooperativa/votacao
├── pauta/        cadastro e consulta de pautas
├── sessao/       abertura e estado da sessão
├── voto/         registro e contagem de votos
├── resultado/    apuração
├── associado/    validação de CPF (fake, http, desabilitado)
├── tela/         API de telas para o app mobile (Anexo 1)
├── comum/        exceções base e handler global
└── config/       Clock e OpenAPI
src/main/resources/db/migration   migrations do Flyway
perf/                             teste de carga (k6) e massa de 500 mil votos
http/                             requisições de exemplo
```
