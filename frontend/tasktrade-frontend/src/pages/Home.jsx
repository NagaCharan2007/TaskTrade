import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { taskApi, getErrorMessage } from '../services/api'
import TaskCard from '../components/TaskCard'
import { EmptyState, ErrorState, LoadingState } from '../components/PageState'

export default function Home() {
  const { user } = useAuth(); const [tasks, setTasks] = useState([]); const [loading, setLoading] = useState(true); const [error, setError] = useState('')
  useEffect(() => { taskApi.list().then(({ data }) => setTasks(data.filter((task) => String(task.status).toUpperCase() !== 'COMPLETED').slice(0, 3))).catch((err) => setError(getErrorMessage(err))).finally(() => setLoading(false)) }, [])
  return <div className="home-page"><section className="welcome-band"><div><p className="kicker">Your student dashboard</p><h1>Welcome back, {user?.name?.split(' ')[0] || 'friend'}</h1><p className="lede">Find tasks. Help other students. Build your reputation.</p><div className="button-row"><Link className="button primary" to="/tasks">Browse tasks</Link><Link className="button secondary" to="/tasks/new">+ Post a task</Link></div></div></section><section className="section-block"><div className="section-heading"><div><p className="kicker">Fresh on the board</p><h2>Tasks you could tackle</h2></div><Link className="arrow-link" to="/tasks">See all tasks <span>-&gt;</span></Link></div>{loading ? <LoadingState label="Finding fresh tasks..." /> : error ? <ErrorState message={error} /> : tasks.length ? <div className="task-grid">{tasks.map((task) => <TaskCard key={task.id} task={task} />)}</div> : <EmptyState title="The board is quiet" message="Be the first person to post a task." action={<Link className="button secondary" to="/tasks/new">Post a task</Link>} />}</section></div>
}
