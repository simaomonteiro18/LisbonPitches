import { Link } from 'react-router-dom'
import './Iniciativa.css'

const etapas = [
  {
    numero: '01',
    titulo: 'O problema',
    texto:
      'Marcar um campo para jogar à bola com os amigos era sempre uma dor de cabeça: grupos de WhatsApp, chamadas para vários campos e a dúvida de saber quais estavam mesmo disponíveis.',
  },
  {
    numero: '02',
    titulo: 'A construção',
    texto:
      'Backend em Java e Spring Boot, frontend em React, tudo construído de raiz. Os campos foram recolhidos e confirmados um a um, sem scraping automático.',
  },
  {
    numero: '03',
    titulo: 'O que vem a seguir',
    texto:
      'Mais campos catalogados e reservas online reais a chegar a cada vez mais pitches.',
  },
]

const numeros = [
  { valor: '50+', legenda: 'Campos catalogados na área de Lisboa' },
  { valor: '100%', legenda: 'Dados reais, confirmados campo a campo' },
]

function Iniciativa() {
  return (
    <>
      <section className="iniciativa-hero">
        <div className="container iniciativa-hero__inner">
          <span className="badge">A nossa história</span>
          <h1>Porque criei o LisbonPitches</h1>
          <p>
            A ideia nasceu de um problema real: marcar um campo para jogar à
            bola com os amigos era sempre uma dor de cabeça, entre grupos de
            WhatsApp, chamadas para vários campos, e a dúvida de saber quais
            estavam mesmo disponíveis. O LisbonPitches nasceu para resolver
            exatamente isso, um sítio único onde é possível descobrir campos
            perto de ti sem complicações.
          </p>
          <p>
            Este projeto é também o meu percurso a aprender a construir
            software a sério, do backend em Java e Spring Boot até a este
            frontend, com o objetivo de o transformar numa ferramenta que
            outras pessoas possam mesmo usar.
          </p>
        </div>
      </section>

      <section className="iniciativa-numeros">
        <div className="container iniciativa-numeros__grid">
          {numeros.map((item) => (
            <div className="iniciativa-numero" key={item.legenda}>
              <span className="iniciativa-numero__valor">{item.valor}</span>
              <p>{item.legenda}</p>
            </div>
          ))}
        </div>
      </section>

      <section className="iniciativa-timeline">
        <div className="container">
          <h2>Como o projeto tem evoluído</h2>
          <div className="iniciativa-timeline__grid">
            {etapas.map((etapa) => (
              <div className="passo" key={etapa.numero}>
                <span className="passo__numero">{etapa.numero}</span>
                <h3>{etapa.titulo}</h3>
                <p>{etapa.texto}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="iniciativa-cta">
        <div className="container iniciativa-cta__inner">
          <h2>Queres ver o que já está disponível?</h2>
          <p>
            Explora os campos já catalogados ou sugere um que ainda não
            esteja na lista.
          </p>
          <div className="iniciativa-cta__actions">
            <Link to="/pitches" className="btn-primary">
              Ver Campos
            </Link>
            <Link to="/#sugerir-campo" className="btn-ghost">
              Sugerir um campo
            </Link>
          </div>
        </div>
      </section>
    </>
  )
}

export default Iniciativa
