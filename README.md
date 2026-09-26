# LisbonPitches

Plataforma de descoberta e reserva de campos de futebol na Área Metropolitana de Lisboa, construída em Java/Spring Boot no backend e React/Vite no frontend.

**53 campos reais catalogados · 16 endpoints REST · 4 entidades · 26 testes (JUnit 5, Mockito, MockMvc)**

🔗 **[API em produção](https://lisbonpitches-production.up.railway.app/pitches)**
— `GET /pitches` devolve a lista de campos. (O domínio de produção ainda
reflete o nome antigo do projeto, `pitchbooking`; será atualizado se o
serviço vier a ser renomeado no Railway.)

## O que resolve

Encontrar campos de futebol reais na AML — públicos (acesso livre) e
privados (normalmente de clubes ou empresas, pagos) — com morada, mapa,
tipo de jogo e contacto, sem ter de procurar caso a caso. Nos campos que já
suportam reserva online pelo site, também é possível marcar hora e
convidar outros jogadores, com o custo dividido automaticamente por quem
confirmar presença.

### Histórias de utilizador

- **Como jogador**, quero encontrar campos perto de mim, filtrar por
  cidade, nome e se são públicos ou privados, e ver a informação relevante
  (preço, tipo de jogo, contacto) antes de decidir.
- **Como organizador**, quero reservar um campo que suporte reserva
  online, e convidar outros jogadores para essa reserva.
- **Como participante**, quero aceitar convites e saber quanto tenho de
  pagar, com o valor dividido automaticamente por quem confirmou presença.
- **Como jogador interessado num campo privado sem reserva online ainda**,
  quero ver o contacto direto (telefone/email) do campo.

## Dados

**53 campos reais** da Área Metropolitana de Lisboa (Sintra, Amadora,
Oeiras, Loures, Lisboa, entre outros) — aproximadamente 30 privados e 23
públicos — recolhidos manualmente, um a um, sem scraping automático nesta
versão. Cada campo tem tipo (Futebol 5/7/11, Futsal), acesso (público ou
privado) e, quando aplicável, preço, morada, coordenadas, imagem e
contacto. O scope geográfico está limitado à AML por agora; alargar exige
mais campos recolhidos à mão ou submissões de utilizadores.

## Funcionalidades

- **Pesquisa de campos** (`/pitches/todos`) por nome, cidade e acesso
  (público/privado/ambos), entre os 53 campos catalogados.
- **Página de destaque** (`/pitches`) com campos em destaque e um mapa
  interativo de todos os campos, com legenda a distinguir público (verde)
  de privado (laranja).
- **Página de detalhe por campo** (`/pitches/:id`) com morada, tipo, preço
  (quando aplicável), mapa interativo e contacto direto para campos
  privados sem reserva online.
- **Reservas** com validação de sobreposição de horário (ao nível da base
  de dados) e blocos mínimos de 1 hora, convite de outros jogadores e
  divisão automática do custo pelos participantes que aceitaram (incluindo
  o organizador).
- **Autenticação por JWT** (registo/login) com Spring Security aplicado a
  todos os 16 endpoints exceto os públicos (`/pitches`, `/pitches/{id}`,
  `/users`, `/login`), sem destaque na navegação por agora — ver nota
  abaixo.
- **Sugestão de novos campos**, direto da Home, entregue por email via
  FormSubmit (sem backend próprio).

### Nota sobre o login

Registo/login está implementado (Spring Security + JWT, password com
hash BCrypt), mas não tem link em destaque na navegação. É uma decisão
deliberada, não uma funcionalidade em falta: hoje só um campo de
demonstração (InFoot) suporta reserva online de ponta a ponta, por isso
expor login/registo em destaque seria superfície de autenticação sem
benefício real para a maioria dos visitantes. A rota `/login` continua
acessível e funcional; o link volta a aparecer na navegação à medida que
mais campos passarem a suportar reserva online real.

## Stack

**Backend**
- Java 21
- Spring Boot
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL (produção) e H2 (desenvolvimento)
- Docker
- JUnit 5, Mockito, MockMvc — 26 testes em 11 classes, três camadas
  (unidade, mocks e integração HTTP)
- Maven

**Frontend**
- React 19
- Vite
- React Router
- Mapbox GL JS (mapas interativos)

## Como correr localmente

### Backend (H2, sem dependências externas)

1. Clona o repositório
2. Tens de ter o Java 21 instalado
3. A partir da raiz do projecto, corre:
   ```
   ./mvnw spring-boot:run
   ```
   (ou abre o projecto numa IDE e corre a classe `LisbonPitchesApplication`)
4. O perfil `test` já vem activo por defeito, com H2 em memória e dados de
   exemplo inseridos automaticamente (incluindo os campos reais do
   `pitches-import.csv`)
5. A aplicação sobe em `http://localhost:8080`

### Frontend

1. A partir de `frontend/`, corre `npm install`
2. Cria um `.env` com `VITE_API_URL` (ex.: `http://localhost:8080`) e
   `VITE_MAPBOX_TOKEN` (token público do Mapbox, necessário para os mapas)
3. Corre `npm run dev`
4. A aplicação sobe em `http://localhost:5173`

### Produção (PostgreSQL via Docker)

1. Com o Docker a correr, arranca um container de PostgreSQL:
   ```
   docker run --name lisbonpitches-db -e POSTGRES_PASSWORD=<password> -e POSTGRES_DB=lisbonpitches -p 5433:5432 -v lisbonpitches-data:/var/lib/postgresql/data -d postgres:16
   ```
2. Muda `spring.profiles.active` para `prod` no `application.properties`
3. Corre a aplicação

## Modelo de domínio

![Diagrama UML](docs/LisbonPitches%20Architecture.drawio.png)

Quatro entidades principais: `User`, `Pitch`, `Reservation`, `Invitation`,
expostas através de 16 endpoints REST e 7 DTOs. O convite (`Invitation`)
é a entidade de ligação entre utilizador e reserva, e guarda o estado do
convite. `Pitch` guarda tipo, acesso (público/privado), morada/coordenadas,
preço, contacto e se suporta reserva online (`reservable`).

## Fora do MVP

- **Backoffice** para gerir campos e ver reservas, com autenticação por
  papéis. Campos continuam a vir por seed/csv, não por criação direta de
  utilizadores.
- **Sistema de amigos.** Convites são por username ou email.
- **Notificações.**
- **Pagamentos reais.**
- **App móvel.**
- **Upload de imagem próprio** (imagens vêm de URLs externos por agora).

---

> "Some people think football is a matter of life and death. I don't like that
> attitude. I can assure them it is much more serious than that."
> — Bill Shankly
