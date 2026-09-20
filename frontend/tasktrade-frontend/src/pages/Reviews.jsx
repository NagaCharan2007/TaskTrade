import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getErrorMessage, reviewApi, taskApi } from '../services/api'
import { ErrorState, LoadingState } from '../components/PageState'

const getValue = (object, keys) => keys.map((key) => object?.[key]).find((value) => value !== undefined && value !== null && value !== '')

const getName = (value) => {
	if (!value) return ''
	if (typeof value === 'string') return value
	return value.name || value.fullName || [value.firstName, value.lastName].filter(Boolean).join(' ')
}

const isReviewForUser = (review, userId) => {
	return String(review.revieweeId) === String(userId)
}

const reviewDate = (review) => getValue(review, ['createdAt', 'reviewedAt', 'date'])

const getTaskPerson = (task, userId) => {
	if (String(task.requesterId) === String(userId)) {
		return getValue(task, ['selectedHelperName', 'helperName', 'assignedHelperName']) || getName(getValue(task, ['selectedHelper', 'helper', 'assignedHelper'])) || ''
	}
	return getValue(task, ['requesterName', 'ownerName']) || getName(getValue(task, ['requester', 'owner'])) || ''
}

const getReviewerName = (review) => getValue(review, ['reviewerName', 'authorName', 'fromName']) || getName(getValue(review, ['reviewer', 'author', 'createdBy'])) || ''

export default function Reviews() {
	const { user } = useAuth()
	const [searchParams, setSearchParams] = useSearchParams()
	const [tasks, setTasks] = useState([])
	const [receivedReviews, setReceivedReviews] = useState([])
	const [givenReviews, setGivenReviews] = useState([])
	const [selectedTaskId, setSelectedTaskId] = useState('')
	const [form, setForm] = useState({ rating: 5, comment: '' })
	const [loading, setLoading] = useState(true)
	const [saving, setSaving] = useState(false)
	const [error, setError] = useState('')
	const [message, setMessage] = useState('')

	const load = async () => {
		try {
			const [{ data: allTasks }, { data: received }, { data: given }] = await Promise.all([taskApi.list(), reviewApi.forUser(user.id), reviewApi.givenByUser(user.id)])
			setTasks(allTasks.filter((task) => task.status === 'COMPLETED' && String(task.requesterId) === String(user.id)))
			setReceivedReviews(Array.isArray(received) ? received : [])
			setGivenReviews(Array.isArray(given) ? given : [])
		} catch (err) {
			setError(getErrorMessage(err, 'Could not load your reviews.'))
		} finally {
			setLoading(false)
		}
	}

	useEffect(() => {
		load()
	}, [user.id])

	const reviewsForYou = useMemo(() => receivedReviews.filter((review) => isReviewForUser(review, user.id)), [receivedReviews, user.id])
	const taskById = useMemo(() => new Map(tasks.map((task) => [String(task.id), task])), [tasks])
	const getReviewTaskTitle = (review) => review.taskTitle || review.task?.title || taskById.get(String(review.taskId))?.title || ''
	const reviewForTask = (task) => {
		const revieweeId = String(task.requesterId) === String(user.id) ? task.selectedHelperId : task.requesterId
		return givenReviews.find((review) => String(review.taskId) === String(task.id) && String(review.revieweeId) === String(revieweeId))
	}
	const tasksToReview = tasks.filter((task) => !reviewForTask(task))
	const selectedTask = tasksToReview.find((task) => String(task.id) === selectedTaskId)

	useEffect(() => {
		const requestedTaskId = searchParams.get('taskId')
		if (requestedTaskId && tasksToReview.some((task) => String(task.id) === requestedTaskId)) setSelectedTaskId(requestedTaskId)
	}, [searchParams, tasksToReview])

	const selectTask = (taskId) => {
		setSelectedTaskId(String(taskId))
		setSearchParams({ taskId: String(taskId) })
		setMessage('')
		setError('')
	}

	const cancel = () => {
		setSelectedTaskId('')
		setSearchParams({})
		setMessage('')
		setError('')
	}

	const submit = async (event) => {
		event.preventDefault()
		if (!selectedTask || reviewForTask(selectedTask)) return
		setSaving(true)
		setError('')
		setMessage('')
		try {
			const revieweeId = String(selectedTask.requesterId) === String(user.id) ? selectedTask.selectedHelperId : selectedTask.requesterId
			const { data } = await reviewApi.create({ taskId: selectedTask.id, rating: Number(form.rating), comment: form.comment.trim() })
			const savedReview = { ...data, taskId: data.taskId ?? selectedTask.id, reviewerId: data.reviewerId ?? user.id, revieweeId: data.revieweeId ?? revieweeId }
			setGivenReviews((current) => [...current.filter((review) => !(String(review.taskId) === String(savedReview.taskId) && String(review.revieweeId) === String(savedReview.revieweeId))), savedReview])
			setSelectedTaskId('')
			setSearchParams({})
			setForm({ rating: 5, comment: '' })
			setMessage('Review submitted successfully.')
		} catch (err) {
			setError(getErrorMessage(err, 'Could not submit review.'))
		} finally {
			setSaving(false)
		}
	}

	if (loading) return <div className="page-wrap"><LoadingState label="Loading reviews..." /></div>
	if (error && !tasks.length && !receivedReviews.length && !givenReviews.length) return <div className="page-wrap"><ErrorState message={error} /></div>

	return <div className="page-wrap reviews-page">
		<div className="page-heading reviews-heading"><div><p className="kicker">Your feedback, in one place</p><h1>Reviews</h1></div></div>
		{error && <div className="alert error reviews-alert">{error}</div>}
		{message && <div className="alert success reviews-alert">{message}</div>}

		<div className="reviews-layout">
		<div className="reviews-content">
		<section className="reviews-section">
			<div className="section-heading"><div><p className="kicker">Feedback people leave for you</p><h2>Reviews for you</h2></div></div>
			{reviewsForYou.length ? <div className="compact-review-list">{reviewsForYou.map((review) => <article className="compact-review-card" key={review.id || `${review.taskId}-${reviewDate(review)}`}>
				<strong className="compact-review-task">Task: {getReviewTaskTitle(review)}</strong><div className="compact-review-header"><strong>From: {getReviewerName(review)}</strong><span className="stars" aria-label={`${review.rating} out of 5 stars`}>{'★'.repeat(Number(review.rating) || 0)}<span>{'★'.repeat(5 - (Number(review.rating) || 0))}</span> <b>{review.rating}/5</b></span></div>
				<p className="review-comment">{review.comment || 'No comment provided.'}</p>
				<div className="compact-review-meta"><span>{getReviewTaskTitle(review)}</span>{reviewDate(review) && <time dateTime={reviewDate(review)}>{new Date(reviewDate(review)).toLocaleDateString()}</time>}</div>
			</article>)}</div> : <div className="reviews-empty">No reviews yet</div>}
		</section>

		<section className="reviews-section">
			<div className="section-heading"><div><p className="kicker">Close the loop on completed work</p><h2>Reviews to give</h2></div></div>
			{tasks.length ? <div className="review-to-give-list">{tasks.map((task) => { const review = reviewForTask(task); return <article className="review-to-give-card" key={task.id}>
				<div><h3>Task: {task.title}</h3><p className="muted">Review: <strong>{getTaskPerson(task, user.id)}</strong></p></div>
				{review ? <div className="reviewed-details"><span className="review-status">&#10003; Reviewed</span><span className="stars" aria-label={`${review.rating} out of 5 stars`}>{'★'.repeat(Number(review.rating) || 0)}<span>{'★'.repeat(5 - (Number(review.rating) || 0))}</span> <b>{review.rating}/5</b></span><p className="review-comment">&quot;{review.comment || 'No comment provided.'}&quot;</p><small>{review.revieweeName || getTaskPerson(task, user.id)} · {task.title}</small></div> : <div className="review-to-give-actions"><span className="status status-completed">COMPLETED</span><button className="small-button" type="button" onClick={() => selectTask(task.id)}>Leave review</button></div>}
			</article> })}</div> : <div className="reviews-empty">No completed tasks need a review.</div>}
		</section>
		</div>
		<aside className="review-form-column">{selectedTask ? <form className="surface form-stack compact-review-form" onSubmit={submit}><div className="compact-review-form-heading"><div><p className="kicker">Leave a review</p><h3>{selectedTask.title}</h3></div><button className="text-button" type="button" onClick={cancel}>Cancel</button></div><label>Person being reviewed<input value={getTaskPerson(selectedTask, user.id)} readOnly /></label><label>Rating<select value={form.rating} onChange={(event) => setForm({ ...form, rating: event.target.value })}><option value="5">5 - Excellent</option><option value="4">4 - Great</option><option value="3">3 - Good</option><option value="2">2 - Needs work</option><option value="1">1 - Poor</option></select></label><label>Comment <span className="optional">optional</span><textarea rows="3" maxLength="500" value={form.comment} onChange={(event) => setForm({ ...form, comment: event.target.value })} placeholder="What stood out?" /></label><button className="button primary" disabled={saving}>{saving ? 'Submitting...' : 'Submit review'}</button></form> : <div className="review-form-placeholder">Select a completed task to leave a review.</div>}</aside>
		</div>
	</div>
}
