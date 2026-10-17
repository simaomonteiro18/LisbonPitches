import { useEffect, useRef } from 'react'
import { animate, useInView, useReducedMotion } from 'motion/react'
import { aoVer, easings } from '../motion'
import './Contador.css'

// conta ate ao valor quando fica visivel, a partir do valor anterior (0 da primeira vez)
// leitores de ecra leem logo o valor final
function Contador({ valor, duracao = 1.2 }) {
  const ref = useRef(null)
  const visivel = useInView(ref, aoVer)
  const reduzir = useReducedMotion()
  const anteriorRef = useRef(0)

  useEffect(() => {
    const el = ref.current
    if (!el || !visivel) return

    if (reduzir) {
      el.textContent = String(valor)
      anteriorRef.current = valor
      return
    }

    const controlo = animate(anteriorRef.current, valor, {
      duration: duracao,
      ease: easings.out,
      onUpdate: (v) => {
        anteriorRef.current = v
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
