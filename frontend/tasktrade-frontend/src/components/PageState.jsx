export function LoadingState({ label = 'Loading...' }) {
  return <div className="page-state"><span className="loader" />{label}</div>
}

export function ErrorState({ message }) {
  return <div className="alert error" role="alert">{message}</div>
}

export function EmptyState({ title, message, action }) {
  return <div className="empty-state"><div className="empty-mark">+</div><h3>{title}</h3><p>{message}</p>{action}</div>
}
