# Pitch Booking

Plataforma de marcação de campos de futebol, construída em Java e Spring Boot.

## O que resolve

Maior facilidade a encontrar campos de futebol na tua zona, e principalmente a
efetuar reservas e convidar outros jogadores para essas reservas.

### Histórias de utilizador

- **Como jogador**, quero encontrar campos na minha zona, filtrando pela
  cidade do meu perfil, ou ver campos de todo o país se o filtro estiver
  desativado.
- **Como organizador**, quero reservar um campo depois de ver as suas
  informações (nome, local, preço, tipo), e convidar outros jogadores para
  se juntarem.
- **Como participante**, quero aceitar convites e saber quanto tenho de
  pagar, com o valor dividido automaticamente por quem confirmou presença.

## Stack

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL (produção) e H2 (desenvolvimento)
- Docker
- JUnit 5, Mockito, MockMvc
- Maven

## Como correr localmente

### Caminho rápido (H2, sem dependências externas)

1. Clona o repositório
2. Tens de ter o Java 21 instalado
3. A partir da raiz do projecto, corre:
   ```
   ./mvnw spring-boot:run
   ```
   (ou abre o projecto numa IDE e corre a classe `PitchbookingApplication`)
4. O perfil `test` já vem activo por defeito, com H2 em memória e dados de
   exemplo inseridos automaticamente
5. A aplicação sobe em `http://localhost:8080`

### Produção (PostgreSQL via Docker)

1. Com o Docker a correr, arranca um container de PostgreSQL:
   ```
   docker run --name pitchbooking-db -e POSTGRES_PASSWORD=<password> -e POSTGRES_DB=pitchbooking -p 5433:5432 -v pitchbooking-data:/var/lib/postgresql/data -d postgres:16
   ```
2. Muda `spring.profiles.active` para `prod` no `application.properties`
3. Corre a aplicação

## Modelo de domínio

![Diagrama UML](docs/Pitch%20Booking%20Architecture.drawio.png)

Quatro entidades: `User`, `Pitch`, `Reservation`, `Invitation`. O convite
(`Invitation`) é a entidade de ligação entre utilizador e reserva, e guarda o
estado do convite.

## Fora do MVP

- **Backoffice** para gerir campos e ver reservas, com autenticação por
  papéis. Fora do MVP porque implicaria autenticação, daí os campos virem
  por seed em vez de serem criados por utilizadores.
- **Sistema de amigos.** Convites são por username ou email na v1.
- **Mapas e coordenadas.** O filtro é feito por cidade (`city`), sem
  geolocalização.
- **Notificações.**
- **Pagamentos reais.**
- **App móvel.**

---

> "Some people think football is a matter of life and death. I don't like that
> attitude. I can assure them it is much more serious than that."
> — Bill Shankly


