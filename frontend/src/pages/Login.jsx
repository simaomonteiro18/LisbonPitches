import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { AnimatePresence, motion } from 'motion/react'
import { loginUser, registerUser } from '../api/auth'
import { saveSession } from '../auth'
import { durations, easings } from '../motion'
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

  // mesmas regras do backend (letras e digitos unicode)
  const requisitos = [
    { texto: 'Pelo menos 8 caracteres', ok: password.length >= 8 },
    { texto: 'Uma letra maiúscula', ok: /\p{Lu}/u.test(password) },
    { texto: 'Uma letra minúscula', ok: /\p{Ll}/u.test(password) },
    { texto: 'Um número', ok: /\p{Nd}/u.test(password) },
    { texto: 'Um símbolo', ok: /[^\p{L}\p{Nd}]/u.test(password) },
  ]
  const passwordForte = requisitos.every((r) => r.ok)

  return (
    <section className="login">
      <div className="container login__inner">
        <div className="login__tabs">
          {[
            { valor: 'login', texto: 'Login' },
            { valor: 'registo', texto: 'Registo' },
          ].map((opcao) => (
            <button
              key={opcao.valor}
              className={modo === opcao.valor ? 'active' : ''}
              onClick={() => setModo(opcao.valor)}
              type="button"
              aria-pressed={modo === opcao.valor}
            >
              {modo === opcao.valor && (
                <motion.span
                  layoutId="login-tab-indicador"
                  className="login__tab-indicador"
                  transition={{ type: 'spring', stiffness: 500, damping: 38 }}
                />
              )}
              <span className="login__tab-texto">{opcao.texto}</span>
            </button>
          ))}
        </div>

        <AnimatePresence mode="wait" initial={false}>
          <motion.form
            key={modo}
            onSubmit={handleSubmit}
            className="login__form"
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0, transition: { duration: durations.base, ease: easings.out } }}
            exit={{ opacity: 0, y: -8, transition: { duration: durations.fast, ease: easings.out } }}
          >
            {modo === 'registo' && (
              <>
                <input
                  type="text"
                  placeholder="Nome"
                  aria-label="Nome"
                  autoComplete="name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                />
                <input
                  type="text"
                  placeholder="Username"
                  aria-label="Username"
                  autoComplete="username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
                <input
                  type="text"
                  placeholder="Telemóvel"
                  aria-label="Telemóvel"
                  autoComplete="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  required
                />
                <input
                  type="text"
                  placeholder="Cidade"
                  aria-label="Cidade"
                  autoComplete="address-level2"
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                  required
                />
              </>
            )}

            <input
              type="email"
              placeholder="Email"
              aria-label="Email"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Password"
              aria-label="Password"
              autoComplete={modo === 'login' ? 'current-password' : 'new-password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            {modo === 'registo' && (
              <ul className="login__requisitos">
                {requisitos.map((r) => (
                  <li key={r.texto} className={r.ok ? 'ok' : ''}>
                    {r.texto}
                  </li>
                ))}
              </ul>
            )}

            {error && <p className="login__error">{error}</p>}

            <button
              type="submit"
              className="btn-primary"
              disabled={loading || (modo === 'registo' && !passwordForte)}
            >
              {loading ? 'Aguarda...' : modo === 'login' ? 'Entrar' : 'Criar conta'}
            </button>
          </motion.form>
        </AnimatePresence>
      </div>
    </section>
  )
}

export default Login
