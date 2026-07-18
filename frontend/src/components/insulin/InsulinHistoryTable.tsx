import type { InsulinHistoryPage } from '../../types/insulin'
import { formatDateTime } from '../../utils/dateTime'

interface InsulinHistoryTableProps {
  historyPage: InsulinHistoryPage | undefined
  currentPage: number
  isLoading: boolean
  onPageChange: (page: number) => void
}

export function InsulinHistoryTable({
  historyPage,
  currentPage,
  isLoading,
  onPageChange,
}: InsulinHistoryTableProps) {
  if (!historyPage) {
    return <p className="text-sm text-slate-600">Loading insulin history…</p>
  }

  const pages = Array.from({ length: historyPage.totalPages }, (_, index) => index)

  return (
    <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <h2 className="text-lg font-semibold text-slate-900">Injection history</h2>
          <p className="mt-1 text-sm text-slate-600">
            Showing {historyPage.numberOfElements} of {historyPage.totalElements} injection{historyPage.totalElements === 1 ? '' : 's'}.
          </p>
        </div>
        {isLoading ? <span className="text-sm text-slate-500">Refreshing…</span> : null}
      </div>
      {historyPage.empty ? (
        <div className="mt-6 rounded-lg border border-dashed border-slate-300 bg-slate-50 px-4 py-8 text-center text-sm text-slate-600">
          No insulin injections logged yet. Use the form above to record the first one.
        </div>
      ) : (
        <div className="mt-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200 text-left text-sm">
            <thead className="bg-slate-50">
              <tr>
                <th className="px-4 py-3 font-medium text-slate-500">Injection time</th>
                <th className="px-4 py-3 font-medium text-slate-500">Insulin type</th>
                <th className="px-4 py-3 font-medium text-slate-500">Amount</th>
                <th className="px-4 py-3 font-medium text-slate-500">Batch</th>
                <th className="px-4 py-3 font-medium text-slate-500">Notes</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {historyPage.content.map((log) => (
                <tr key={log.id} className="align-top">
                  <td className="px-4 py-3 text-slate-700">{formatDateTime(log.injectionTime)}</td>
                  <td className="px-4 py-3 font-medium text-slate-900">{log.insulinType}</td>
                  <td className="px-4 py-3 text-slate-900">{log.amountUnits} U</td>
                  <td className="px-4 py-3 text-slate-600">{log.batchNumber || '—'}</td>
                  <td className="px-4 py-3 text-slate-600">{log.notes || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {historyPage.totalPages > 1 ? (
        <div className="mt-6 flex flex-wrap items-center gap-2">
          <button
            type="button"
            onClick={() => onPageChange(currentPage - 1)}
            disabled={historyPage.first}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:text-slate-400"
          >
            Previous
          </button>
          {pages.map((page) => (
            <button
              key={page}
              type="button"
              onClick={() => onPageChange(page)}
              className={`rounded-md px-3 py-1.5 text-sm font-medium transition ${
                page === currentPage ? 'bg-indigo-600 text-white' : 'border border-slate-300 text-slate-700 hover:bg-slate-50'
              }`}
            >
              {page + 1}
            </button>
          ))}
          <button
            type="button"
            onClick={() => onPageChange(currentPage + 1)}
            disabled={historyPage.last}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:text-slate-400"
          >
            Next
          </button>
        </div>
      ) : null}
    </section>
  )
}
