import { useRef } from 'react'
import { motion, useMotionValue, useReducedMotion, useSpring } from 'motion/react'
import './CartaoInclinado.css'

const mola = { stiffness: 220, damping: 22, mass: 0.6 }

// inclina na direcao do cursor e mostra um brilho que o segue; so com rato
function CartaoInclinado({ className = '', inclinacao = 6, children }) {
  const ref = useRef(null)
  const reduzir = useReducedMotion()

  const rotX = useSpring(0, mola)
  const rotY = useSpring(0, mola)
  const brilhoX = useMotionValue(0)
  const brilhoY = useMotionValue(0)
  const brilhoOpacidade = useSpring(0, mola)

  function aoMover(e) {
    if (reduzir || e.pointerType !== 'mouse' || !ref.current) return
    const caixa = ref.current.getBoundingClientRect()
    const x = e.clientX - caixa.left
    const y = e.clientY - caixa.top
    rotY.set((x / caixa.width - 0.5) * 2 * inclinacao)
    rotX.set(-(y / caixa.height - 0.5) * 2 * inclinacao)
    brilhoX.set(x)
    brilhoY.set(y)
    brilhoOpacidade.set(1)
  }

  function aoSair() {
    rotX.set(0)
    rotY.set(0)
    brilhoOpacidade.set(0)
  }

  return (
    <motion.div
      ref={ref}
      className={`cartao-inclinado ${className}`}
      style={{ rotateX: rotX, rotateY: rotY, transformPerspective: 800 }}
      onPointerMove={aoMover}
      onPointerLeave={aoSair}
    >
      <motion.span
        className="cartao-inclinado__brilho"
        style={{ x: brilhoX, y: brilhoY, opacity: brilhoOpacidade }}
        aria-hidden="true"
      />
      <div className="cartao-inclinado__conteudo">{children}</div>
    </motion.div>
  )
}

export default CartaoInclinado
