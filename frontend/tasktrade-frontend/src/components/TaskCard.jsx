import { Link } from 'react-router-dom'

export default function TaskCard({ task }) {
  return (
    <Link className="task-card" to={`/tasks/${task.id}`}>
      <div className="task-card-top"><span className="eyebrow">{task.taskType || 'Task'}</span><span className={`status status-${task.status?.toLowerCase()}`}>{task.status || 'OPEN'}</span></div>
      <h3>{task.title}</h3>
      <p>{task.description}</p>
      <div className="task-meta"><span>{task.skillRequired || 'Any skill welcome'}</span><span>{task.createdAt ? new Date(task.createdAt).toLocaleDateString() : 'Recently posted'}</span></div>
    </Link>
  )
}
