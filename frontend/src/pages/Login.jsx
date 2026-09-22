import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { loginUser, registerUser } from '../api/auth'
import { saveSession } from '../auth'
import './Login.css'

function Login() {
  const navigate = useNavigate()
  const [modo, setModo] = useState('login')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [username, setUsername] = useState('')
  const [phone, setPhone] = useState('')
  const [city, setCity] = useState('')

  async function entrarComSessao(email, password) {
    const loginData = await loginUser({ email, password })
    saveSession(loginData.token, loginData.userSummaryDTO)
    navigate('/perfil')
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setError(null)

    try {
      if (modo === 'login') {
        await entrarComSessao(email, password)
      } else {
        await registerUser({ name, username, email, password, phone, city })
        await entrarComSessao(email, password)
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="login">
      <div className="container login__inner">
        <div className="login__tabs">
          <button
            className={modo === 'login' ? 'active' : ''}
            onClick={() => setModo('login')}
            type="button"
          >
            Login
          </button>
          <button
            className={modo === 'registo' ? 'active' : ''}
            onClick={() => setModo('registo')}
            type="button"
          >
            Registo
          </button>
        </div>

        <form onSubmit={handleSubmit} className="login__form">
          {modo === 'registo' && (
            <>
              <input
                type="text"
                placeholder="Nome"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
              <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
              <input
                type="text"
                placeholder="Telemóvel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                required
              />
              <input
                type="text"
                placeholder="Cidade"
                value={city}
                onChange={(e) => setCity(e.target.value)}
                required
              />
            </>
          )}

          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          {error && <p className="login__error">{error}</p>}

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Aguarda...' : modo === 'login' ? 'Entrar' : 'Criar conta'}
          </button>
        </form>
      </div>
    </section>
  )
}

export default Login
