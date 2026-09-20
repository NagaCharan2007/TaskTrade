import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const submit = async (event) => {
    event.preventDefault(); setError(''); setSaving(true)
    try { await login(form); navigate(location.state?.from?.pathname || '/', { replace: true }) }
    catch (err) { setError(err.message) }
    finally { setSaving(false) }
  }

  return <AuthLayout title="Welcome back" subtitle="Pick up where you left off.">
    <form className="form-stack" onSubmit={submit}>
      {error && <div className="alert error">{error}</div>}
      <label>Email<input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="you@example.com" /></label>
      <label>Password<input type="password" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="Your password" /></label>
      <button className="button primary full" disabled={saving}>{saving ? 'Signing in...' : 'Sign in'}</button>
    </form>
    <p className="auth-switch">New to tasktrade? <Link to="/register">Create an account</Link></p>
  </AuthLayout>
}

export function AuthLayout({ title, subtitle, children }) {
  return <div className="auth-page"><div className="auth-aside"><Link className="brand light" to="/">task<span>trade</span></Link><div><p className="kicker">A little help goes a long way</p><h1>Trade skills.<br /><em>Build momentum.</em></h1><p>Find small tasks to tackle, or find the person who can help you move yours forward.</p></div></div><section className="auth-panel"><div className="auth-card"><p className="kicker">Your next move</p><h2>{title}</h2><p className="muted">{subtitle}</p>{children}</div></section></div>
}
