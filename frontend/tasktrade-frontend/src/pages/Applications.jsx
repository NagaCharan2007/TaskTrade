import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { applicationApi, getErrorMessage } from '../services/api'
import { EmptyState, ErrorState, LoadingState } from '../components/PageState'

export default function Applications() {
	const { user } = useAuth()
	const [rows, setRows] = useState([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState('')

	useEffect(() => {
		const load = async () => {
			try {
				const { data } = await applicationApi.mine()
				setRows(Array.isArray(data) ? data : [])
			} catch (err) {
				setError(getErrorMessage(err, 'Could not load applications.'))
			} finally {
				setLoading(false)
			}
		}
		if (user?.id) load()
	}, [user?.id])

	const sections = [
		{ title: 'Pending', description: 'Applications waiting for a requester response.', rows: rows.filter((row) => row.status === 'PENDING') },
		{ title: 'Current / In Progress', description: 'Accepted applications for work that is underway.', rows: rows.filter((row) => row.status === 'ACCEPTED' && row.taskStatus !== 'COMPLETED') },
		{ title: 'Completed', description: 'Accepted applications for completed tasks.', rows: rows.filter((row) => row.status === 'ACCEPTED' && row.taskStatus === 'COMPLETED') },
	]

	return <div className="page-wrap"><div className="page-heading"><div><p className="kicker">Your work with the community</p><h1>Applications</h1><p className="muted">Track the tasks you have applied to help with.</p></div></div>{loading ? <LoadingState label="Checking your applications..." /> : error ? <ErrorState message={error} /> : rows.length ? <div className="application-sections">{sections.map((section) => <section className="application-section" key={section.title}><div className="section-heading"><div><h2>{section.title}</h2><p className="muted">{section.description}</p></div></div>{section.rows.length ? <div className="application-list">{section.rows.map((row) => <div className="application-card" key={row.id}><div className="application-main"><span className="eyebrow">{row.taskTitle}</span><h3>{row.taskStatus === 'COMPLETED' ? 'Completed task' : row.status === 'ACCEPTED' ? 'Accepted application' : 'Waiting for response'}</h3><p>{row.message || 'No note included.'}</p>{row.requesterName && <small className="muted">Owner: {row.requesterName}</small>}</div><div className="application-side"><span className={`status status-${row.status?.toLowerCase()}`}>{row.status}</span><Link className="small-button" to={`/tasks/${row.taskId}`}>View task</Link>{row.status === 'ACCEPTED' && <Link className="small-button" to={`/tasks/${row.taskId}`}>Contact</Link>}</div></div>)}</div> : <p className="muted section-empty">No applications here.</p>}</section>)}</div> : <EmptyState title="No applications yet" message="Apply to a task and your progress will show up here." />}</div>
}
