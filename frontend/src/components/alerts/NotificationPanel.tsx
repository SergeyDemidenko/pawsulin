import { useNotificationStore } from '../../store/notificationStore'
import type { AlertType, NotificationMessage } from '../../types/alert'

const ALERT_TYPE_COLORS: Record<AlertType, string> = {
  LOW_GLUCOSE: 'bg-blue-100 text-blue-800',
  HIGH_GLUCOSE: 'bg-orange-100 text-orange-800',
  CRITICAL_GLUCOSE: 'bg-red-100 text-red-800',
  MISSED_READING: 'bg-slate-100 text-slate-700',
}

function formatTime(iso: string): string {
  return new Date(iso).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })
}

interface NotificationItemProps {
  notification: NotificationMessage
}

function NotificationItem({ notification }: NotificationItemProps) {
  return (
    <li className="px-4 py-3 hover:bg-slate-50">
      <div className="flex items-start gap-2">
        <span className={`mt-0.5 shrink-0 rounded-full px-1.5 py-0.5 text-[10px] font-semibold uppercase ${ALERT_TYPE_COLORS[notification.alertType]}`}>
          {notification.alertType.replace(/_/g, ' ')}
        </span>
        <div className="min-w-0 flex-1">
          <p className="text-sm text-slate-800">{notification.message}</p>
          <p className="mt-0.5 text-xs text-slate-500">
            {notification.petName} · {formatTime(notification.triggeredAt)}
          </p>
        </div>
      </div>
    </li>
  )
}

interface NotificationPanelProps {
  onClose: () => void
}

export function NotificationPanel({ onClose }: NotificationPanelProps) {
  const notifications = useNotificationStore((s) => s.notifications)
  const clearNotifications = useNotificationStore((s) => s.clearNotifications)

  const handleClear = () => {
    clearNotifications()
    onClose()
  }

  return (
    <div className="absolute right-0 top-full z-50 mt-1 w-80 rounded-xl border border-slate-200 bg-white shadow-lg">
      <div className="flex items-center justify-between border-b border-slate-200 px-4 py-2.5">
        <h3 className="text-sm font-semibold text-slate-900">Notifications</h3>
        {notifications.length > 0 && (
          <button
            type="button"
            onClick={handleClear}
            className="text-xs text-slate-500 transition hover:text-red-600"
          >
            Clear all
          </button>
        )}
      </div>

      {notifications.length === 0 ? (
        <p className="px-4 py-6 text-center text-sm text-slate-500">No notifications yet.</p>
      ) : (
        <ul className="max-h-80 divide-y divide-slate-100 overflow-y-auto">
          {notifications.map((n) => (
            <NotificationItem
              key={n.id}
              notification={n}
            />
          ))}
        </ul>
      )}
    </div>
  )
}
