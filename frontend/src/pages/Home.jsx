import { Link } from 'react-router-dom'
import { useState } from 'react'
import './Home.css'

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
  const [nomeCampo, setNomeCampo] = useState('')
  const [localizacao, setLocalizacao] = useState('')
  const [notas, setNotas] = useState('')

  function enviarSugestao(e) {
    e.preventDefault()

    const assunto = `Sugestão de campo - ${nomeCampo}`
    const corpo = `Campo: ${nomeCampo}\nLocalização: ${localizacao}\n\n${notas}`

    window.location.href = `mailto:${CONTACT_EMAIL}?subject=${encodeURIComponent(assunto)}&body=${encodeURIComponent(corpo)}`
  }

  return (
    <>
      <section className="hero">
        <div className="container hero__inner">
          <span className="badge">Pitch Booking</span>
          <h1>
            Cansado de perder horas à procura de campos de futebol?
          </h1>
          <p>
            O Pitch Booking junta num só sítio os campos perto de ti, para
            nunca mais perderes tempo a mandar mensagens a perguntar onde é
            que se pode jogar.
          </p>
          <div className="hero__actions">
            <Link to="/pitches" className="btn-primary">
              Ver Campos
            </Link>
            <Link to="/iniciativa" className="btn-ghost">
              Conhece a iniciativa
            </Link>
          </div>
        </div>
      </section>

      <section className="como-funciona">
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

      <section className="sugerir-campo">
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

            <button type="submit" className="btn-primary">
              Enviar sugestão
            </button>
          </form>
        </div>
      </section>
    </>
  )
}

export default Home
