import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useTheme } from '../context/ThemeContext'

const navItems = [
  ['Home', '/'],
  ['Browse tasks', '/tasks'],
  ['My tasks', '/my-tasks'],
  ['Applications', '/applications'],
  ['Reviews', '/reviews'],
]

export default function Layout() {
  const { user, logout } = useAuth()
  const { theme, toggleTheme } = useTheme()
  const navigate = useNavigate()

  const signOut = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <NavLink className="brand" to="/">task<span>trade</span></NavLink>
        <nav className="main-nav" aria-label="Main navigation">
          {navItems.map(([label, path]) => <NavLink key={path} className={({ isActive }) => isActive ? 'active' : ''} to={path}>{label}</NavLink>)}
        </nav>
        <div className="account-nav">
          <button className="theme-toggle" type="button" onClick={toggleTheme} aria-label={`Switch to ${theme === 'light' ? 'dark' : 'light'} mode`}>
            {theme === 'light' ? 'Dark mode' : 'Light mode'}
          </button>
          <NavLink className="avatar-link" to="/profile" aria-label="Open profile">{user?.name?.charAt(0)?.toUpperCase() || '?'}</NavLink>
          <button className="text-button" type="button" onClick={signOut}>Log out</button>
        </div>
      </header>
      <main className="main-content"><Outlet /></main>
    </div>
  )
}
