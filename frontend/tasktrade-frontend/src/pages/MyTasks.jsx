import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import TaskCard from '../components/TaskCard'
import { EmptyState, ErrorState, LoadingState } from '../components/PageState'
import { applicationApi, getErrorMessage, reviewApi, taskApi } from '../services/api'

export default function MyTasks() {
	const { user } = useAuth()
	const [groups, setGroups] = useState({ needAccept: [], inProgress: [], completed: [] })
	const [reviewedTaskIds, setReviewedTaskIds] = useState([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState('')

	useEffect(() => {
		const load = async () => {
			try {
				const [{ data }, { data: givenReviews }] = await Promise.all([taskApi.list(), reviewApi.givenByUser(user.id)])
				const mine = data.filter((task) => String(task.requesterId) === String(user?.id))
				const withApplications = await Promise.all(mine.map(async (task) => ({ task, applications: (await applicationApi.forTask(task.id)).data })))
				setGroups({
					needAccept: withApplications.filter(({ task, applications }) => task.status === 'OPEN' && applications.length).map(({ task }) => task),
					inProgress: withApplications.filter(({ task }) => task.status === 'ACCEPTED').map(({ task }) => task),
					completed: withApplications.filter(({ task }) => task.status === 'COMPLETED').map(({ task }) => task),
				})
				setReviewedTaskIds((Array.isArray(givenReviews) ? givenReviews : []).filter((review) => String(review.reviewerId) === String(user.id)).map((review) => String(review.taskId)))
			} catch (err) {
				setError(getErrorMessage(err, 'Could not load your tasks.'))
			} finally {
				setLoading(false)
			}
		}
		if (user?.id) load()
	}, [user?.id])

	const sections = [{ title: 'Need to Accept', tasks: groups.needAccept, action: 'View applications' }, { title: 'Accepted / In Progress', tasks: groups.inProgress }, { title: 'Completed', tasks: groups.completed }]
	const total = Object.values(groups).flat().length

	return <div className="page-wrap"><div className="page-heading"><div><p className="kicker">Your corner of the board</p><h1>My tasks</h1><p className="muted">Keep an eye on the things you have asked the community to help with.</p></div><Link className="button primary" to="/tasks/new">+ Post a task</Link></div>{loading ? <LoadingState /> : error ? <ErrorState message={error} /> : total ? <div className="my-task-sections">{sections.map((section) => <section className="my-task-section" key={section.title}><div className="section-heading"><h2>{section.title}</h2></div>{section.tasks.length ? <div className="task-grid">{section.tasks.map((task) => <div className="task-card-wrap" key={task.id}><TaskCard task={task} />{section.action && <Link className="small-button task-card-action" to={`/tasks/${task.id}`}>{section.action}</Link>}{section.title === 'Completed' && (reviewedTaskIds.includes(String(task.id)) ? <span className="review-status task-card-action">Reviewed ✓</span> : <Link className="small-button task-card-action" to={`/reviews?taskId=${task.id}`}>Leave Review</Link>)}</div>)}</div> : <p className="muted section-empty">No tasks here.</p>}</section>)}</div> : <EmptyState title="Nothing here yet" message="Post a task and let the community meet you halfway." action={<Link className="button primary" to="/tasks/new">Post your first task</Link>} />}</div>
}
