import { Link } from 'react-router-dom'
import { motion, useReducedMotion } from 'motion/react'
import { aoVer, durations, easings, revelar } from '../motion'
import Contador from '../components/Contador'
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
  { valor: 50, sufixo: '+', legenda: 'Campos catalogados na área de Lisboa' },
  { valor: 100, sufixo: '%', legenda: 'Dados reais, confirmados campo a campo' },
]

// o topo entra logo ao abrir a pagina, um elemento de cada vez
const entrarTopo = {
  escondido: { opacity: 0, y: 24 },
  visivel: (atraso = 0) => ({
    opacity: 1,
    y: 0,
    transition: { duration: durations.slow, ease: easings.out, delay: atraso },
  }),
}

function Iniciativa() {
  const reduzir = useReducedMotion()
  const inicio = reduzir ? false : 'escondido'

  return (
    <>
      <section className="iniciativa-hero">
        <motion.div
          className="container iniciativa-hero__inner"
          initial={inicio}
          animate="visivel"
        >
          <motion.span className="badge" variants={entrarTopo} custom={0.1}>
            A nossa história
          </motion.span>
          <motion.h1 variants={entrarTopo} custom={0.18}>
            Porque criei o LisbonPitches
          </motion.h1>
          <motion.p variants={entrarTopo} custom={0.3}>
            A ideia nasceu de um problema real: marcar um campo para jogar à
            bola com os amigos era sempre uma dor de cabeça, entre grupos de
            WhatsApp, chamadas para vários campos, e a dúvida de saber quais
            estavam mesmo disponíveis. O LisbonPitches nasceu para resolver
            exatamente isso, um sítio único onde é possível descobrir campos
            perto de ti sem complicações.
          </motion.p>
          <motion.p variants={entrarTopo} custom={0.4}>
            Este projeto é também o meu percurso a aprender a construir
            software a sério, do backend em Java e Spring Boot até a este
            frontend, com o objetivo de o transformar numa ferramenta que
            outras pessoas possam mesmo usar.
          </motion.p>
        </motion.div>
      </section>

      <section className="iniciativa-numeros">
        <motion.div
          className="container iniciativa-numeros__grid"
          initial={inicio}
          whileInView="visivel"
          viewport={aoVer}
        >
          {numeros.map((item, i) => (
            <motion.div className="iniciativa-numero" key={item.legenda} variants={revelar} custom={i * 0.12}>
              <span className="iniciativa-numero__valor">
                <Contador valor={item.valor} />
                {item.sufixo}
              </span>
              <p>{item.legenda}</p>
            </motion.div>
          ))}
        </motion.div>
      </section>

      <section className="iniciativa-timeline">
        <div className="container">
          <motion.h2 initial={inicio} whileInView="visivel" viewport={aoVer} variants={revelar}>
            Como o projeto tem evoluído
          </motion.h2>
          <motion.div
            className="iniciativa-timeline__grid"
            initial={inicio}
            whileInView="visivel"
            viewport={aoVer}
          >
            {etapas.map((etapa, i) => (
              <motion.div className="passo" key={etapa.numero} variants={revelar} custom={i * 0.12}>
                <span className="passo__numero">{etapa.numero}</span>
                <h3>{etapa.titulo}</h3>
                <p>{etapa.texto}</p>
              </motion.div>
            ))}
          </motion.div>
        </div>
      </section>

      <section className="iniciativa-cta">
        <motion.div
          className="container iniciativa-cta__inner"
          initial={inicio}
          whileInView="visivel"
          viewport={aoVer}
          variants={revelar}
        >
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
        </motion.div>
      </section>
    </>
  )
}

export default Iniciativa
