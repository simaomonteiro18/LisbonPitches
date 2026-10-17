import { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { motion, useScroll, useSpring } from 'motion/react'
import './BarraProgresso.css'

// so aparece em paginas com scroll suficiente para valer a pena
const ALTURA_MINIMA = 1.6

function BarraProgresso() {
  const { pathname } = useLocation()
  const { scrollYProgress } = useScroll()
  const escala = useSpring(scrollYProgress, { stiffness: 260, damping: 40, restDelta: 0.001 })
  const [visivel, setVisivel] = useState(false)

  useEffect(() => {
    function medir() {
      setVisivel(document.documentElement.scrollHeight > window.innerHeight * ALTURA_MINIMA)
    }

    // o conteudo chega depois (pedidos a API), por isso mede sempre que a pagina muda de tamanho
    const observador = new ResizeObserver(medir)
    observador.observe(document.body)
    window.addEventListener('resize', medir)
    return () => {
      observador.disconnect()
      window.removeEventListener('resize', medir)
    }
  }, [pathname])

  return (
    <motion.div
      className="barra-progresso"
      aria-hidden="true"
      style={{ scaleX: escala }}
      animate={{ opacity: visivel ? 1 : 0 }}
      transition={{ duration: 0.2 }}
    />
  )
}

export default BarraProgresso
