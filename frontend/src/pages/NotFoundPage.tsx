import { Link } from 'react-router-dom'

export function NotFoundPage() {
  return (
    <section className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <h1 className="text-2xl font-semibold text-slate-900">Page not found</h1>
      <Link to="/" className="mt-3 inline-block text-sm font-medium text-indigo-600 hover:text-indigo-500">
        Go back home
      </Link>
    </section>
  )
}
