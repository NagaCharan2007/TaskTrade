import { createContext, useContext, useEffect, useState } from 'react'
import { authApi, getErrorMessage, userApi } from '../services/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('tasktrade_token'))
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('tasktrade_user')
    return saved ? JSON.parse(saved) : null
  })
  const [loading, setLoading] = useState(Boolean(token && !user))

  useEffect(() => {
    const logout = () => {
      setToken(null)
      setUser(null)
    }
    window.addEventListener('tasktrade:logout', logout)
    return () => window.removeEventListener('tasktrade:logout', logout)
  }, [])

  useEffect(() => {
    if (!token || user) return undefined
    userApi.me()
      .then(({ data }) => setUser(data))
      .catch(() => {
        localStorage.removeItem('tasktrade_token')
        setToken(null)
      })
      .finally(() => setLoading(false))
    return undefined
  }, [token, user])

  const saveSession = (data) => {
    localStorage.setItem('tasktrade_token', data.token)
    localStorage.setItem('tasktrade_user', JSON.stringify(data.user))
    setToken(data.token)
    setUser(data.user)
  }

  const login = async (credentials) => {
    try {
      const { data } = await authApi.login(credentials)
      saveSession(data)
      return data
    } catch (error) {
      throw new Error(getErrorMessage(error, 'Unable to log in with those details.'))
    }
  }

  const register = async (details) => {
    try {
      await authApi.register(details)
      return login({ email: details.email, password: details.password })
    } catch (error) {
      throw new Error(getErrorMessage(error, 'Unable to create your account.'))
    }
  }

  const refreshUser = async () => {
    const { data } = await userApi.me()
    localStorage.setItem('tasktrade_user', JSON.stringify(data))
    setUser(data)
    return data
  }

  const logout = () => {
    localStorage.removeItem('tasktrade_token')
    localStorage.removeItem('tasktrade_user')
    setToken(null)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ token, user, loading, login, register, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
