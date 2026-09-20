import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import './App.css'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import { AuthProvider } from './context/AuthContext'
import Applications from './pages/Applications'
import BrowseTasks from './pages/BrowseTasks'
import CreateTask from './pages/CreateTask'
import Home from './pages/Home'
import Login from './pages/Login'
import MyTasks from './pages/MyTasks'
import Profile from './pages/Profile'
import Register from './pages/Register'
import Reviews from './pages/Reviews'
import TaskDetails from './pages/TaskDetails'
import { ThemeProvider } from './context/ThemeContext'

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/" element={<Home />} />
              <Route path="/tasks" element={<BrowseTasks />} />
              <Route path="/tasks/new" element={<CreateTask />} />
              <Route path="/tasks/:id" element={<TaskDetails />} />
              <Route path="/my-tasks" element={<MyTasks />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="/applications" element={<Applications />} />
              <Route path="/reviews" element={<Reviews />} />
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  )
}

export default App
