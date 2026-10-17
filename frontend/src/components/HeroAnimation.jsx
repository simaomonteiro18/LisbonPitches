import { motion, useReducedMotion } from 'motion/react'
import { durations, easings } from '../motion'
import './HeroAnimation.css'

// cena em viewBox 1600x900, campo centrado em (1080, 430) e rodado -6 graus
const ruas = [
  'M0 180 C 260 220, 480 140, 760 200 S 1240 260, 1600 150',
  'M0 420 C 220 380, 420 470, 640 430 S 900 330, 1100 360',
  'M120 0 C 180 200, 140 420, 260 620 S 380 760, 360 900',
  'M520 0 C 560 160, 700 260, 680 420 S 600 640, 700 760',
  'M1500 0 C 1460 180, 1520 360, 1440 520 S 1300 680, 1340 760',
  'M900 0 C 940 120, 860 200, 920 260',
  'M1600 600 C 1480 620, 1460 680, 1380 700',
  'M0 640 C 160 600, 300 680, 480 650',
]

const tejo = 'M0 770 C 300 730, 520 810, 820 780 S 1300 710, 1600 750 L1600 900 L0 900 Z'

const linhasCampo = [
  'M780 240 H1380 V620 H780 Z',
  'M1080 240 V620',
  'M1080 370 A60 60 0 1 1 1080 490 A60 60 0 1 1 1080 370',
  'M780 320 H880 V540 H780',
  'M1380 320 H1280 V540 H1380',
  'M780 375 H816 V485 H780',
  'M1380 375 H1344 V485 H1380',
  'M880 378 A60 60 0 0 1 880 482',
  'M1280 378 A60 60 0 0 0 1280 482',
]

// campo, brilho e bola deslocados para a direita para nao ficarem por baixo do texto
const DESLOCAMENTO_CAMPO = 120

const pins = [
  { x: 1555, y: 215, tipo: 'publico' },
  { x: 1230, y: 170, tipo: 'publico' },
  { x: 1565, y: 650, tipo: 'privado' },
  { x: 900, y: 700, tipo: 'publico' },
  { x: 1340, y: 700, tipo: 'privado' },
  { x: 650, y: 300, tipo: 'privado' },
  { x: 560, y: 620, tipo: 'publico' },
  { x: 1560, y: 380, tipo: 'publico' },
  { x: 820, y: 160, tipo: 'privado' },
  { x: 1150, y: 720, tipo: 'publico' },
]

// 4-2-3-1 da casa, a atacar para a direita; a de fora e o reflexo pelo centro
const formacao = [
  { n: 1, x: 805, y: 430 },
  { n: 3, x: 890, y: 280 },
  { n: 4, x: 875, y: 385 },
  { n: 5, x: 875, y: 475 },
  { n: 2, x: 890, y: 580 },
  { n: 6, x: 945, y: 385 },
  { n: 8, x: 945, y: 475 },
  { n: 11, x: 1010, y: 300 },
  { n: 10, x: 1000, y: 430 },
  { n: 7, x: 1010, y: 560 },
  { n: 9, x: 1050, y: 448 },
]

// a equipa de fora fica fora do circulo central no pontape de saida
const ajustesFora = { 9: [1148, 430], 10: [1195, 430] }

const jogadores = [
  ...formacao.map((j) => ({ ...j, equipa: 'casa' })),
  ...formacao.map((j) => {
    const [x, y] = ajustesFora[j.n] ?? [2160 - j.x, 860 - j.y]
    return { n: j.n, x, y, equipa: 'fora' }
  }),
]

// trajetoria da bola: bezier quadratica do canto ate ao centro do campo
function arco(p0, c, p2, passos) {
  const xs = []
  const ys = []
  for (let i = 0; i <= passos; i++) {
    const t = i / passos
    const u = 1 - t
    xs.push(u * u * p0[0] + 2 * u * t * c[0] + t * t * p2[0])
    ys.push(u * u * p0[1] + 2 * u * t * c[1] + t * t * p2[1])
  }
  return { xs, ys }
}

const trajetoria = arco([380, 860], [760, 60], [1080, 430], 16)

const T = {
  mapa: 0,
  linhas: 0.3,
  jogadores: 1.0,
  bola: 1.6,
  voo: 1.2,
  pins: 1.9,
}

function Bola() {
  return (
    <>
      <clipPath id="hero-bola-clip">
        <circle r="16" />
      </clipPath>
      <circle className="hero-scene__bola-base" r="16" />
      <g clipPath="url(#hero-bola-clip)" className="hero-scene__bola-manchas">
        <path d="M0 -6 L5.7 -1.9 L3.5 4.9 L-3.5 4.9 L-5.7 -1.9 Z" />
        {[-54, 18, 90, 162, 234].map((a) => (
          <path
            key={a}
            transform={`rotate(${a + 90}) translate(0 -18)`}
            d="M0 -6 L5.7 -1.9 L3.5 4.9 L-3.5 4.9 L-5.7 -1.9 Z"
          />
        ))}
        <path
          className="hero-scene__bola-costuras"
          d="M0 -6 L0 -12 M5.7 -1.9 L11.4 -3.7 M3.5 4.9 L7 9.7 M-3.5 4.9 L-7 9.7 M-5.7 -1.9 L-11.4 -3.7"
        />
      </g>
      <circle className="hero-scene__bola-contorno" r="16" />
      {/* circulo invisivel para centrar a origem da rotacao */}
      <circle r="24" fill="none" />
    </>
  )
}

function HeroAnimation() {
  const reduzir = useReducedMotion()
  const inicio = reduzir ? false : 'escondido'

  return (
    <div className="hero-scene" aria-hidden="true">
      <svg viewBox="0 0 1600 900" preserveAspectRatio="xMaxYMid slice">
        <defs>
          <radialGradient id="hero-brilho">
            <stop offset="0%" stopColor="var(--color-grass)" stopOpacity="0.35" />
            <stop offset="100%" stopColor="var(--color-grass)" stopOpacity="0" />
          </radialGradient>
        </defs>

        <motion.g
          className="hero-scene__mapa"
          initial={inicio}
          animate="visivel"
          variants={{ escondido: { opacity: 0 }, visivel: { opacity: 1 } }}
          transition={{ duration: durations.slower, ease: easings.out, delay: T.mapa }}
        >
          <path className="hero-scene__tejo" d={tejo} />
          {ruas.map((d) => (
            <path key={d} className="hero-scene__rua" d={d} />
          ))}
        </motion.g>

        <motion.ellipse
          cx={1080 + DESLOCAMENTO_CAMPO}
          cy="430"
          rx="420"
          ry="300"
          fill="url(#hero-brilho)"
          initial={reduzir ? false : { opacity: 0 }}
          animate={reduzir ? { opacity: 1 } : { opacity: [0, 1, 0.6, 1] }}
          transition={
            reduzir
              ? undefined
              : {
                  delay: T.bola + T.voo,
                  duration: 4,
                  times: [0, 0.2, 0.6, 1],
                  repeat: Infinity,
                  repeatType: 'reverse',
                  ease: 'easeInOut',
                }
          }
        />

        {pins.map((pin, i) => (
          <g key={`${pin.x}-${pin.y}`} transform={`translate(${pin.x} ${pin.y})`}>
            <motion.g
              className={`hero-scene__pin hero-scene__pin--${pin.tipo}`}
              initial={inicio}
              animate="visivel"
              variants={{
                escondido: { y: -70, opacity: 0 },
                visivel: { y: 0, opacity: 1 },
              }}
              transition={{
                y: { type: 'spring', stiffness: 420, damping: 18, delay: T.pins + i * 0.09 },
                opacity: { duration: durations.fast, delay: T.pins + i * 0.09 },
              }}
            >
              <path d="M0 0 C -6 -10 -14 -16 -14 -26 A14 14 0 1 1 14 -26 C 14 -16 6 -10 0 0 Z" />
              <circle className="hero-scene__pin-centro" cx="0" cy="-26" r="5" />
            </motion.g>
          </g>
        ))}

        <g transform={`translate(${DESLOCAMENTO_CAMPO} 0)`}>
          <g transform="rotate(-6 1080 430)">
            {linhasCampo.map((d, i) => (
              <motion.path
                key={d}
                className="hero-scene__linha"
                d={d}
                initial={inicio}
                animate="visivel"
                variants={{
                  escondido: { pathLength: 0, opacity: 0 },
                  visivel: { pathLength: 1, opacity: 1 },
                }}
                transition={{
                  pathLength: { duration: 1.1, ease: easings.inOut, delay: T.linhas + i * 0.08 },
                  opacity: { duration: durations.fast, delay: T.linhas + i * 0.08 },
                }}
              />
            ))}
            <motion.circle
              className="hero-scene__marca"
              cx="1080"
              cy="430"
              r="5"
              initial={inicio}
              animate="visivel"
              variants={{ escondido: { opacity: 0 }, visivel: { opacity: 1 } }}
              transition={{ delay: T.linhas + 0.6, duration: durations.base }}
            />

            {jogadores.map((j, i) => (
              <g key={`${j.equipa}-${j.n}`} transform={`translate(${j.x} ${j.y})`}>
                <motion.g
                  className={`hero-scene__jogador hero-scene__jogador--${j.equipa}`}
                  initial={reduzir ? false : { scale: 0, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  transition={{
                    scale: { type: 'spring', stiffness: 420, damping: 20, delay: T.jogadores + i * 0.03 },
                    opacity: { duration: durations.fast, delay: T.jogadores + i * 0.03 },
                  }}
                >
                  <circle r="14" />
                  <text dy="0.35em">{j.n}</text>
                </motion.g>
              </g>
            ))}
          </g>

          <motion.g
            className="hero-scene__bola"
            initial={
              reduzir
                ? false
                : { x: trajetoria.xs[0], y: trajetoria.ys[0], opacity: 0, rotate: 0, scale: 1.2 }
            }
            animate={
              reduzir
                ? { x: 1080, y: 430, opacity: 1, scale: 0.6 }
                : { x: trajetoria.xs, y: trajetoria.ys, opacity: 1, rotate: 540, scale: 0.6 }
            }
            transition={
              reduzir
                ? undefined
                : {
                    x: { duration: T.voo, ease: 'linear', delay: T.bola },
                    y: { duration: T.voo, ease: 'linear', delay: T.bola },
                    rotate: { duration: T.voo + 0.3, ease: easings.out, delay: T.bola },
                    scale: { duration: T.voo, ease: 'linear', delay: T.bola },
                    opacity: { duration: durations.fast, delay: T.bola },
                  }
            }
          >
            <Bola />
          </motion.g>
        </g>
      </svg>
      <div className="hero-scene__veu" />
    </div>
  )
}

export default HeroAnimation
