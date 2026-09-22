import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import Home from './pages/Home'
import Iniciativa from './pages/Iniciativa'
import Pitches from './pages/Pitches'
import Login from './pages/Login'
import Perfil from './pages/Perfil'
import Reserva from './pages/Reserva'

function App() {
  return (
    <div className="app">
      <Navbar />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/iniciativa" element={<Iniciativa />} />
          <Route path="/pitches" element={<Pitches />} />
          <Route path="/login" element={<Login />} />
          <Route path="/perfil" element={<Perfil />} />
          <Route path="/reservas/:id" element={<Reserva />} />
        </Routes>
      </main>
      <Footer />
    </div>
  )
}

export default App
