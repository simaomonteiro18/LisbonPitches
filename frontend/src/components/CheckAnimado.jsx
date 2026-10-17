import { motion, useReducedMotion } from 'motion/react'
import { durations, easings } from '../motion'
import './CheckAnimado.css'

// circulo e visto desenhados por esta ordem
function CheckAnimado({ tamanho = 72, atraso = 0 }) {
  const reduzir = useReducedMotion()

  return (
    <svg className="check-animado" viewBox="0 0 64 64" width={tamanho} height={tamanho} aria-hidden="true">
      <motion.circle
        cx="32"
        cy="32"
        r="28"
        initial={reduzir ? false : { pathLength: 0 }}
        animate={{ pathLength: 1 }}
        transition={{ duration: durations.slow, ease: easings.out, delay: atraso + 0.1 }}
      />
      <motion.path
        d="M20 33 L28 41 L44 24"
        initial={reduzir ? false : { pathLength: 0 }}
        animate={{ pathLength: 1 }}
        transition={{ duration: durations.base, ease: easings.out, delay: atraso + 0.5 }}
      />
    </svg>
  )
}

export default CheckAnimado
