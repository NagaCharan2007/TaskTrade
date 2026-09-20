import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { AuthLayout } from './Login'

export default function Register() {
  const { register } = useAuth(); const navigate = useNavigate()
  const [form, setForm] = useState({ name: '', email: '', password: '', bio: '' }); const [error, setError] = useState(''); const [saving, setSaving] = useState(false)
  const submit = async (event) => { event.preventDefault(); setError(''); setSaving(true); try { await register(form); navigate('/') } catch (err) { setError(err.message) } finally { setSaving(false) } }
  return <AuthLayout title="Make an account" subtitle="Join a community that gets things done.">
    <form className="form-stack" onSubmit={submit}>{error && <div className="alert error">{error}</div>}
      <label>Your name<input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Alex Morgan" /></label>
      <label>Email<input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="you@example.com" /></label>
      <label>Password<input type="password" minLength="8" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="At least 8 characters" /></label>
      <label>Short bio <span className="optional">optional</span><textarea rows="3" value={form.bio} onChange={(e) => setForm({ ...form, bio: e.target.value })} placeholder="What are you good at?" /></label>
      <button className="button primary full" disabled={saving}>{saving ? 'Creating account...' : 'Create account'}</button>
    </form><p className="auth-switch">Already a member? <Link to="/login">Sign in</Link></p>
  </AuthLayout>
}
