import { Link } from 'react-router-dom'
import './Home.css'

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
    </>
  )
}

export default Home
