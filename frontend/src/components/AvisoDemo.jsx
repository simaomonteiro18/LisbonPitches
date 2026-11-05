import { useState } from 'react'
import './AvisoDemo.css'

const CHAVE = 'aviso-demo-fechado'

function jaFechado() {
  try {
    return sessionStorage.getItem(CHAVE) === '1'
  } catch {
    return false
  }
}

function AvisoDemo() {
  const [aberto, setAberto] = useState(() => !jaFechado())

  function fechar() {
    setAberto(false)
    try {
      sessionStorage.setItem(CHAVE, '1')
    } catch {
      // sem armazenamento, o aviso volta a aparecer na proxima visita
    }
  }

  if (!aberto) return null

  return (
    <aside className="aviso-demo" role="note" aria-label="Aviso de demonstração">
      <p>Projeto de portefólio: as reservas são de demonstração e não chegam ao campo.</p>
      <button type="button" className="aviso-demo__fechar" onClick={fechar} aria-label="Fechar aviso">
        &times;
      </button>
    </aside>
  )
}

export default AvisoDemo
