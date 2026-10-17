# Briefing: remodelação visual do frontend do LisbonPitches

Itens marcados com [confirmar] são escolhas por omissão. Edita o que não te agradar antes de começares.

## 1. Objetivo

Transformar o frontend num site moderno, muito interativo e cheio de movimento, que cause boa primeira impressão a recrutadores e a quem procura campos. O destaque é a Home, com uma animação alusiva ao futebol e a Lisboa.

O backend não muda. A API, as rotas e os dados ficam como estão.

## 2. Estado atual (não partir)

- React + Vite, `mapbox-gl`, CSS por ficheiro (`Pagina.jsx` + `Pagina.css`), sem framework de UI.
- Tema claro/escuro com variáveis semânticas (`--color-bg`, `--color-text`, `--color-surface`, ...). Escuro por omissão, claro bege (`#e8e2d3`). Escolha guardada em `localStorage` e aplicada em `index.html` antes do React. Botão `ThemeToggle` fixo no canto inferior direito.
- Cores de marca: verde `#3fae4a` (campos públicos), laranja `#e0a05f` (campos privados).
- Páginas: Home, `/pitches` (destaque + mapa), `/pitches/todos` (pesquisa, filtros, ordenação, paginação), `/pitches/:id` (detalhe, mapa, reserva), `/reservas/:id`, Iniciativa, Perfil, login/registo (sem destaque na navegação de propósito).
- Navbar em forma de pílula, estreita, com "Pitches" à esquerda e "Iniciativa" à direita do logótipo.

## 3. Direção visual [confirmar]

- Manter a identidade: fundo escuro, verde como cor de destaque, tipografia limpa e grande nos títulos.
- Mais profundidade: gradientes subtis, brilho verde atrás de elementos-chave, cartões com vidro (blur) e bordas finas.
- Tema claro tem de continuar a funcionar em tudo o que for novo.
- Referências: [colar aqui 2 a 3 sites de que gostes].

## 4. Animação da Home [confirmar]

Opção A (recomendada, feita em código): SVG e CSS/JS, nítida em qualquer ecrã, leve e com cores que seguem o tema.
- Linhas de um campo de futebol a desenharem-se (traço animado).
- Bola a percorrer uma trajetória em arco até ao centro do campo.
- Pins verdes e laranja a cair sobre um fundo de mapa estilizado da área de Lisboa.
- Título a revelar-se palavra a palavra; botões a entrar com atraso escalonado.

Opção B (vídeo): vídeo ambiente em loop por baixo do título.
- 6 a 10 s, sem som, 1280x720, `mp4` (H.264) e `webm`, até 4 MB, com imagem `poster`.
- `autoplay muted loop playsinline`, só em ecrãs grandes. No telemóvel mostra o poster.
- Gradiente escuro por cima para o texto ler-se bem.
- Fontes de vídeos gratuitos: Pexels e Mixkit (confirmar a licença de cada um) ou filmagem própria de um campo.

Combinação possível: A primeiro; B como camada opcional depois.

## 5. Interações e animações por página

Home
- Hero animado (secção 4), scroll suave, "Como funciona" com cartões a entrar um a um e ligeira inclinação ao passar o rato.
- Secção "sugerir campo" com feedback animado ao submeter.

`/pitches`
- Pins do mapa a cair em cascata ao carregar; ao passar o rato num cartão em destaque, o mapa voa até ao pin (`flyTo`).
- Contador animado do número de campos.

`/pitches/todos`
- Cartões a entrar ao fazer scroll, reordenação animada ao mudar filtros ou ordenação, esqueletos de carregamento em vez de "A carregar...".

`/pitches/:id` e reservas
- Entrada do detalhe com transição, formulário de reserva a abrir com animação, confirmação com visual de sucesso.

Global
- Transição entre páginas, botões com micro-interações, navbar que reage ao scroll, indicador de progresso de scroll [confirmar].

## 6. Regras técnicas

- Uma biblioteca de animação principal: `motion` (React). Só acrescentar GSAP se algo não for possível com `motion`. Perguntar antes de instalar qualquer dependência nova.
- Animar só `transform` e `opacity`, para manter 60 fps.
- Respeitar `prefers-reduced-motion`: sem movimento forte para quem o desativa.
- Lighthouse em mobile: Performance acima de 85. Nada de vídeo a bloquear o carregamento.
- Responsivo desde 360 px de largura. Testar em ecrã de telemóvel.
- Não alterar chamadas à API, rotas nem lógica de negócio. Só apresentação.
- Texto da interface em português de Portugal, com acentos. Sem travessões no texto.
- Comentários no código sem acentos e poucos.
- Não ler nem criar ficheiros `.env`. As variáveis `VITE_*` existentes ficam como estão.

## 7. Fases (um commit por fase)

1. Base: ficheiro `CLAUDE.md` na raiz do repositório, tokens de movimento (durações, curvas) em CSS e instalação do `motion`.
2. Hero da Home (animação principal).
3. Resto da Home (scroll, cartões, formulário).
4. `/pitches` e interações do mapa.
5. `/pitches/todos` (cartões, filtros, esqueletos).
6. Detalhe do campo e reservas.
7. Transições globais, navbar e acabamento.
8. Acessibilidade, desempenho (Lighthouse) e testes em telemóvel.

Cada fase corre na sua própria worktree/ramo e termina com `npm run build` sem erros, revisão no browser (claro e escuro), commit e pull request. Só se avança depois de a anterior estar integrada, porque várias fases mexem nos mesmos ficheiros.

## 8. Critérios de aceitação

- Build sem erros nem avisos novos.
- Tudo funciona nos temas claro e escuro.
- Sem saltos de layout (nada "salta" ao carregar).
- Home fluida em telemóvel de gama média.
- O fluxo ponta a ponta continua a funcionar: pesquisar, abrir campo, reservar, convidar.

## 9. Texto para o CLAUDE.md

```
# LisbonPitches frontend
- React + Vite. Estilos em CSS por componente, com variaveis de tema em :root e [data-theme='light'].
- Correr: npm install, npm run dev. Validar: npm run build.
- Trabalhar so dentro de frontend/. Nao alterar API, rotas nem backend. So apresentacao.
- UI em portugues de Portugal com acentos, sem travessoes. Comentarios sem acentos.
- Animar transform e opacity, respeitar prefers-reduced-motion.
- Perguntar antes de instalar dependencias.
- Nunca ler nem criar ficheiros .env.
- Briefing completo em BRIEFING-FRONTEND.md.
```
