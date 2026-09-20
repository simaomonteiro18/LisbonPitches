import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { clearSession, getSession } from '../auth'
import './Perfil.css'

function Perfil() {
  const navigate = useNavigate()
  const [session, setSession] = useState(null)

  useEffect(() => {
    const current = getSession()
    if (!current) {
      navigate('/login')
      return
    }
    setSession(current)
  }, [navigate])

  function handleLogout() {
    clearSession()
    navigate('/login')
  }

  if (!session) {
    return null
  }

  const { user } = session

  return (
    <section className="perfil">
      <div className="container perfil__inner">
        <h1>O teu perfil</h1>

        <div className="perfil__card">
          <div className="perfil__campo">
            <span>Nome</span>
            <strong>{user.name}</strong>
          </div>
          <div className="perfil__campo">
            <span>Email</span>
            <strong>{user.email}</strong>
          </div>
          <div className="perfil__campo">
            <span>Telemóvel</span>
            <strong>{user.phone}</strong>
          </div>
          <div className="perfil__campo">
            <span>Cidade</span>
            <strong>{user.city}</strong>
          </div>
        </div>

        <button onClick={handleLogout} className="btn-ghost">
          Terminar sessão
        </button>
      </div>
    </section>
  )
}

export default Perfil
