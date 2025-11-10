# 💸 Pix Service API

A **Pix Service** é uma simulação de um sistema de pagamentos e gestão de carteiras digitais, desenvolvida com **Spring Boot 3**, seguindo os princípios de **arquitetura limpa (Clean Architecture)**.  
O sistema permite criar carteiras, realizar depósitos, saques, transferências via Pix com **idempotência**, histórico de transações e processamento de eventos via **webhook**.

---

## 🚀 Funcionalidades

- 🧍 Criação e gerenciamento de carteiras
- 💰 Depósitos e saques
- 🔁 Transferências Pix entre carteiras
- 🧾 Processamento de eventos via Webhook (CONFIRMED / FAILED)
- 🧱 Camada de idempotência por carteira
- 🕵️ Logs estruturados com Logback
- 💾 Persistência de dados com PostgreSQL
- ✅ Consistência transacional e tratamento de exceções


---

## 🧩 Arquitetura do Projeto

O projeto segue uma estrutura modular e organizada em camadas:

com.pixservice
┣ 📁 application
┃ ┗ 📁 wallet → use cases (business logic)
┣ 📁 domain → entities and enums
┣ 📁 infrastructure
┃ ┣ 📁 persistence → JPA repositories
┃ ┗ 📁 web → controllers and API endpoints

- **Application Layer:** Implementa os casos de uso principais (transferência, depósito, webhook etc.)
- **Domain Layer:** Define as entidades centrais (`Wallet`, `Transaction`, `PixKey`, `IdempotencyKey`)
- **Infrastructure Layer:** Responsável por persistência, endpoints REST e integrações externas

---

## ⚙️ Stack Tecnológica

| Camada | Tecnologia |
|--------|-------------|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.x |
| Persistência | Spring Data JPA + PostgreSQL |
| Build Tool | Maven |
| Logging | SLF4J + Logback |
| Serialização JSON | Jackson |
| Testes | JUnit 5 (opcional) |

## 📡 Endpoints REST

### 🧍 Carteiras (Wallets)

| Método | Endpoint | Descrição |
|--------|-----------|-----------|
| `POST` | `/wallets` | Cria uma nova carteira |
| `GET` | `/wallets/{id}/balance` | Retorna o saldo atual |
| `GET` | `/wallets/{id}/balance/historical?at={timestamp}` | Consulta saldo histórico em uma data específica |
| `POST` | `/wallets/{id}/deposit` | Realiza um depósito |
| `POST` | `/wallets/{id}/withdraw` | Realiza um saque |


### 🔑 Chaves Pix

| Método | Endpoint | Descrição |
|--------|-----------|-----------|
| `POST` | `/wallets/{id}/pix-keys` | Registra uma nova chave Pix (CPF, e-mail etc.) |

### 💸 Transferências Pix

| Método | Endpoint | Descrição |
|--------|-----------|-----------|
| `POST` | `/pix/transfer` | Realiza uma transferência Pix entre carteiras |

**Cabeçalhos obrigatórios:**

## Exemplo de Requisição

**Request:**
```
{
  "fromWalletId": 1,
  "toPixKey": "user2@email.com",
  "amount": 150.00
}

{
  "endToEndId": "E2E-1f2c-34a9-8bcd",
  "status": "PENDING"
}

```

## Executando Localmente
## Banco de Dados (PostgreSQL)

docker run --name pix-db -e 
    POSTGRES_DB=pix_base -e 
    POSTGRES_USER=postgres -e 
    POSTGRES_PASSWORD=postgres -p 5439:5432 -d postgres

## Application

mvn spring-boot:run

---

Author

Vinícius Torres Pascucci
Backend Engineer — Java / Spring Ecosystem
📧 vinicius.pascucci1@gmail.com

🔗 [LinkedIn](https://www.linkedin.com/in/vinicius-pascucci-5a4024151/) 
 | [GitHub](https://github.com/viniciuspascucci1993)