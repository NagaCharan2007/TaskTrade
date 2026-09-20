import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import TaskCard from '../components/TaskCard'
import { EmptyState, ErrorState, LoadingState } from '../components/PageState'
import { getErrorMessage, taskApi } from '../services/api'

export default function BrowseTasks() {
  const [tasks, setTasks] = useState([]); const [query, setQuery] = useState(''); const [loading, setLoading] = useState(true); const [error, setError] = useState('')
  useEffect(() => { taskApi.list().then(({ data }) => setTasks(data)).catch((err) => setError(getErrorMessage(err))).finally(() => setLoading(false)) }, [])
  const filtered = tasks.filter((task) => String(task.status).toUpperCase() !== 'COMPLETED' && `${task.title} ${task.description} ${task.taskType} ${task.skillRequired}`.toLowerCase().includes(query.toLowerCase()))
  return <div className="page-wrap"><div className="page-heading"><div><p className="kicker">The open board</p><h1>Browse tasks</h1><p className="muted">Find something useful to do, close to home or close to heart.</p></div><Link className="button primary" to="/tasks/new">+ Post a task</Link></div><div className="search-bar"><span>/</span><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search by title, skill, or type" /></div>{loading ? <LoadingState label="Loading the task board..." /> : error ? <ErrorState message={error} /> : filtered.length ? <div className="task-grid">{filtered.map((task) => <TaskCard key={task.id} task={task} />)}</div> : <EmptyState title="No matching tasks" message="Try a different search, or post the task you need." />}</div>
}
