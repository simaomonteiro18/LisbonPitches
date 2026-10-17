import { useEffect, useRef } from 'react'
import { animate, useInView, useReducedMotion } from 'motion/react'
import { aoVer, easings } from '../motion'
import './Contador.css'

// conta de 0 ate ao valor quando fica visivel; leitores de ecra leem logo o valor final
function Contador({ valor, duracao = 1.2 }) {
  const ref = useRef(null)
  const visivel = useInView(ref, aoVer)
  const reduzir = useReducedMotion()

  useEffect(() => {
    const el = ref.current
    if (!el || !visivel) return

    if (reduzir) {
      el.textContent = String(valor)
      return
    }

    const controlo = animate(0, valor, {
      duration: duracao,
      ease: easings.out,
      onUpdate: (v) => {
        el.textContent = String(Math.round(v))
      },
    })
    return () => controlo.stop()
  }, [valor, visivel, reduzir, duracao])

  return (
    <span className="contador">
      <span ref={ref} aria-hidden="true">
        0
      </span>
      <span className="contador__leitor">{valor}</span>
    </span>
  )
}

export default Contador
