import { Link, useLocation } from 'react-router-dom'
import { Fragment, useState, useEffect, useRef } from 'react'
import { AnimatePresence, motion, useReducedMotion, useScroll, useTransform } from 'motion/react'
import HeroAnimation from '../components/HeroAnimation'
import CartaoInclinado from '../components/CartaoInclinado'
import { aoVer, durations, easings, revelar, stagger } from '../motion'
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

const revelarLado = {
  escondido: ({ x }) => ({ opacity: 0, x }),
  visivel: ({ atraso }) => ({
    opacity: 1,
    x: 0,
    transition: { duration: durations.slower, ease: easings.out, delay: atraso },
  }),
}

// a linha desenha-se a velocidade constante e cada passo acende quando ela chega
const LINHA_ATRASO = 0.35
const LINHA_DURACAO = 1.8
function momentoPasso(i) {
  return LINHA_ATRASO + (LINHA_DURACAO * i) / 2.6
}

const linhaPercurso = {
  escondido: { scaleX: 0 },
  visivel: { scaleX: 1, transition: { duration: LINHA_DURACAO, ease: 'linear', delay: LINHA_ATRASO } },
}

const pontoPercurso = {
  escondido: { scale: 0, opacity: 0 },
  visivel: (atraso) => ({
    scale: 1,
    opacity: 1,
    transition: { type: 'spring', stiffness: 380, damping: 14, delay: atraso },
  }),
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

  // parallax: o texto sobe e desvanece, a cena desce mais devagar
  const heroRef = useRef(null)
  const { scrollYProgress } = useScroll({ target: heroRef, offset: ['start start', 'end start'] })
  const textoY = useTransform(scrollYProgress, [0, 1], [0, -140])
  const textoOpacidade = useTransform(scrollYProgress, [0, 0.6], [1, 0])
  const cenaY = useTransform(scrollYProgress, [0, 1], [0, 140])
  const setaOpacidade = useTransform(scrollYProgress, [0, 0.08], [1, 0])

  useEffect(() => {
    if (location.hash === '#sugerir-campo') {
      document
        .getElementById('sugerir-campo')
        ?.scrollIntoView({ behavior: reduzir ? 'auto' : 'smooth' })
    }
  }, [location, reduzir])

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
      <section className="hero" ref={heroRef}>
        <motion.div className="hero__parallax" style={reduzir ? undefined : { y: cenaY }}>
          <HeroAnimation />
        </motion.div>
        <motion.div
          className="container hero__inner"
          style={reduzir ? undefined : { y: textoY, opacity: textoOpacidade }}
        >
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
        </motion.div>
        <motion.div className="hero__scroll" style={reduzir ? undefined : { opacity: setaOpacidade }}>
          <motion.a
            href="#como-funciona"
            className="hero__scroll-link"
            initial={reduzir ? false : { opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: durations.slow, delay: 2.6 }}
          >
            <span>Descobre como funciona</span>
            <span className="hero__scroll-seta" aria-hidden="true" />
          </motion.a>
        </motion.div>
      </section>

      <section className="como-funciona" id="como-funciona">
        <motion.div
          className="container"
          initial={inicio}
          whileInView="visivel"
          viewport={aoVer}
        >
          <motion.h2 variants={revelar} custom={0}>
            Como funciona
          </motion.h2>

          <div className="como-funciona__percurso" aria-hidden="true">
            <motion.span className="como-funciona__linha" variants={linhaPercurso} />
            {passos.map((passo, i) => (
              <motion.span
                key={passo.numero}
                className="como-funciona__ponto"
                variants={pontoPercurso}
                custom={momentoPasso(i)}
              />
            ))}
          </div>

          <div className="como-funciona__grid">
            {passos.map((passo, i) => (
              <motion.div key={passo.numero} variants={revelar} custom={momentoPasso(i) + 0.1}>
                <CartaoInclinado className="passo">
                  <span className="passo__numero">{passo.numero}</span>
                  <h3>{passo.titulo}</h3>
                  <p>{passo.texto}</p>
                </CartaoInclinado>
              </motion.div>
            ))}
          </div>
        </motion.div>
      </section>

      <section className="sugerir-campo" id="sugerir-campo">
        <motion.div
          className="container sugerir-campo__inner"
          initial={inicio}
          whileInView="visivel"
          viewport={aoVer}
        >
          <motion.div className="sugerir-campo__texto" variants={revelarLado} custom={{ x: -80, atraso: 0 }}>
            <h2>Falta o teu campo aqui?</h2>
            <p>
              Se costumas jogar num campo que ainda não está na lista,
              diz-nos qual é e tratamos de o adicionar.
            </p>
          </motion.div>

          <motion.div
            className="sugerir-campo__painel"
            variants={revelarLado}
            custom={{ x: 80, atraso: 0.25 }}
          >
            <motion.form
              className="sugerir-campo__form"
              onSubmit={enviarSugestao}
              inert={estadoEnvio === 'sucesso'}
              animate={{
                opacity: estadoEnvio === 'sucesso' ? 0 : 1,
                x: estadoEnvio === 'erro' && !reduzir ? [0, -10, 10, -6, 6, 0] : 0,
              }}
              transition={{ opacity: { duration: durations.base }, x: { duration: 0.45 } }}
            >
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
                <AnimatePresence mode="wait" initial={false}>
                  <motion.span
                    key={estadoEnvio === 'enviando' ? 'enviando' : 'enviar'}
                    className="sugerir-campo__botao-texto"
                    initial={{ opacity: 0, y: 8 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -8 }}
                    transition={{ duration: durations.fast, ease: easings.out }}
                  >
                    {estadoEnvio === 'enviando' ? (
                      <>
                        <span className="sugerir-campo__spinner" aria-hidden="true" />
                        A enviar...
                      </>
                    ) : (
                      'Enviar sugestão'
                    )}
                  </motion.span>
                </AnimatePresence>
              </button>

              <AnimatePresence>
                {estadoEnvio === 'erro' && (
                  <motion.p
                    className="sugerir-campo__aviso sugerir-campo__aviso--erro"
                    role="alert"
                    initial={{ opacity: 0, y: -6 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0 }}
                    transition={{ duration: durations.base, ease: easings.out }}
                  >
                    Não foi possível enviar agora. Tenta novamente ou escreve para {CONTACT_EMAIL}.
                  </motion.p>
                )}
              </AnimatePresence>
            </motion.form>

            <AnimatePresence>
              {estadoEnvio === 'sucesso' && (
                <motion.div
                  className="sugerir-campo__sucesso"
                  role="status"
                  initial={{ opacity: 0, scale: 0.96 }}
                  animate={{ opacity: 1, scale: 1 }}
                  exit={{ opacity: 0, scale: 0.98 }}
                  transition={{ duration: durations.base, ease: easings.out }}
                >
                  <svg viewBox="0 0 64 64" width="72" height="72" aria-hidden="true">
                    <motion.circle
                      cx="32"
                      cy="32"
                      r="28"
                      initial={reduzir ? false : { pathLength: 0 }}
                      animate={{ pathLength: 1 }}
                      transition={{ duration: durations.slow, ease: easings.out, delay: 0.1 }}
                    />
                    <motion.path
                      d="M20 33 L28 41 L44 24"
                      initial={reduzir ? false : { pathLength: 0 }}
                      animate={{ pathLength: 1 }}
                      transition={{ duration: durations.base, ease: easings.out, delay: 0.5 }}
                    />
                  </svg>
                  <h3>Sugestão enviada, obrigado!</h3>
                  <p>Vamos ver o campo e adicioná-lo à lista.</p>
                  <button
                    type="button"
                    className="btn-ghost"
                    onClick={() => setEstadoEnvio('idle')}
                    autoFocus
                  >
                    Sugerir outro campo
                  </button>
                </motion.div>
              )}
            </AnimatePresence>
          </motion.div>
        </motion.div>
      </section>
    </>
  )
}

export default Home
