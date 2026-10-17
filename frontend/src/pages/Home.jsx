import { Link, useLocation } from 'react-router-dom'
import { Fragment, useState, useEffect } from 'react'
import { motion, useReducedMotion } from 'motion/react'
import HeroAnimation from '../components/HeroAnimation'
import { durations, easings, stagger } from '../motion'
import './Home.css'

const TITULO = 'Cansado de perder horas à procura de campos de futebol?'
const palavras = TITULO.split(' ')
const ATRASO_TITULO = 0.2
const fimTitulo = ATRASO_TITULO + palavras.length * stagger

const entrar = {
  escondido: { opacity: 0, y: 24 },
  visivel: { opacity: 1, y: 0 },
}

function transicao(delay) {
  return { duration: durations.slow, ease: easings.out, delay }
}

const CONTACT_EMAIL = 'sasmonteiro07@gmail.com'

const passos = [
  {
    numero: '01',
    titulo: 'Descobre',
    texto: 'Procura campos por nome ou filtra pela tua cidade.',
  },
  {
    numero: '02',
    titulo: 'Escolhe',
    texto: 'Vê os detalhes do campo e o preço por hora.',
  },
  {
    numero: '03',
    titulo: 'Convida',
    texto: 'Junta os amigos à partida com um convite.',
  },
]

function Home() {
  const location = useLocation()
  const reduzir = useReducedMotion()
  const inicio = reduzir ? false : 'escondido'
  const [nomeCampo, setNomeCampo] = useState('')
  const [localizacao, setLocalizacao] = useState('')
  const [notas, setNotas] = useState('')
  const [estadoEnvio, setEstadoEnvio] = useState('idle')

  useEffect(() => {
    if (location.hash === '#sugerir-campo') {
      document.getElementById('sugerir-campo')?.scrollIntoView({ behavior: 'smooth' })
    }
  }, [location])

  async function enviarSugestao(e) {
    e.preventDefault()
    setEstadoEnvio('enviando')

    try {
      const resposta = await fetch(`https://formsubmit.co/ajax/${CONTACT_EMAIL}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
        body: JSON.stringify({
          _subject: `Sugestão de campo - ${nomeCampo}`,
          Campo: nomeCampo,
          Localização: localizacao,
          Notas: notas || '(sem notas)',
        }),
      })

      if (!resposta.ok) {
        throw new Error('Falha ao enviar')
      }

      setEstadoEnvio('sucesso')
      setNomeCampo('')
      setLocalizacao('')
      setNotas('')
    } catch {
      setEstadoEnvio('erro')
    }
  }

  return (
    <>
      <section className="hero">
        <HeroAnimation />
        <div className="container hero__inner">
          <motion.span
            className="badge"
            initial={inicio}
            animate="visivel"
            variants={entrar}
            transition={transicao(0)}
          >
            LisbonPitches
          </motion.span>
          <h1 aria-label={TITULO}>
            {palavras.map((palavra, i) => (
              <Fragment key={i}>
                <span className="hero__palavra-mascara" aria-hidden="true">
                  <motion.span
                    className="hero__palavra"
                    initial={reduzir ? false : { y: '110%', opacity: 0 }}
                    animate={{ y: 0, opacity: 1 }}
                    transition={transicao(ATRASO_TITULO + i * stagger)}
                  >
                    {palavra}
                  </motion.span>
                </span>{' '}
              </Fragment>
            ))}
          </h1>
          <motion.p
            initial={inicio}
            animate="visivel"
            variants={entrar}
            transition={transicao(fimTitulo + 0.1)}
          >
            O LisbonPitches junta num só sítio os campos perto de ti, para
            nunca mais perderes tempo a mandar mensagens a perguntar onde é
            que se pode jogar.
          </motion.p>
          <div className="hero__actions">
            <motion.div
              initial={inicio}
              animate="visivel"
              variants={entrar}
              transition={transicao(fimTitulo + 0.25)}
            >
              <Link to="/pitches" className="btn-primary">
                Ver Campos
              </Link>
            </motion.div>
            <motion.div
              initial={inicio}
              animate="visivel"
              variants={entrar}
              transition={transicao(fimTitulo + 0.25 + stagger * 2)}
            >
              <Link to="/iniciativa" className="btn-ghost">
                Conhece a iniciativa
              </Link>
            </motion.div>
          </div>
        </div>
        <motion.a
          href="#como-funciona"
          className="hero__scroll"
          initial={reduzir ? false : { opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: durations.slow, delay: 2.6 }}
        >
          <span>Descobre como funciona</span>
          <span className="hero__scroll-seta" aria-hidden="true" />
        </motion.a>
      </section>

      <section className="como-funciona" id="como-funciona">
        <div className="container">
          <h2>Como funciona</h2>
          <div className="como-funciona__grid">
            {passos.map((passo) => (
              <div className="passo" key={passo.numero}>
                <span className="passo__numero">{passo.numero}</span>
                <h3>{passo.titulo}</h3>
                <p>{passo.texto}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="sugerir-campo" id="sugerir-campo">
        <div className="container sugerir-campo__inner">
          <div className="sugerir-campo__texto">
            <h2>Falta o teu campo aqui?</h2>
            <p>
              Se costumas jogar num campo que ainda não está na lista,
              diz-nos qual é e tratamos de o adicionar.
            </p>
          </div>

          <form className="sugerir-campo__form" onSubmit={enviarSugestao}>
            <label>
              Nome do campo
              <input
                type="text"
                value={nomeCampo}
                onChange={(e) => setNomeCampo(e.target.value)}
                required
              />
            </label>

            <label>
              Localização
              <input
                type="text"
                value={localizacao}
                onChange={(e) => setLocalizacao(e.target.value)}
                placeholder="Cidade ou morada"
                required
              />
            </label>

            <label>
              Notas (opcional)
              <textarea
                value={notas}
                onChange={(e) => setNotas(e.target.value)}
                rows={3}
              />
            </label>

            <button type="submit" className="btn-primary" disabled={estadoEnvio === 'enviando'}>
              {estadoEnvio === 'enviando' ? 'A enviar...' : 'Enviar sugestão'}
            </button>

            {estadoEnvio === 'sucesso' && (
              <p className="sugerir-campo__aviso sugerir-campo__aviso--sucesso">
                Sugestão enviada, obrigado!
              </p>
            )}

            {estadoEnvio === 'erro' && (
              <p className="sugerir-campo__aviso sugerir-campo__aviso--erro">
                Não foi possível enviar agora. Tenta novamente ou escreve para {CONTACT_EMAIL}.
              </p>
            )}
          </form>
        </div>
      </section>
    </>
  )
}

export default Home
