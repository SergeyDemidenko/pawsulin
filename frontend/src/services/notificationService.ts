import { useAuthStore } from '../store/authStore'
import { useNotificationStore } from '../store/notificationStore'
import type { NotificationMessage } from '../types/alert'

const WS_BASE_URL = (import.meta.env.VITE_WS_BASE_URL as string | undefined) ?? 'ws://localhost:8080'
const RECONNECT_DELAY_MS = 5000
const MAX_RECONNECT_ATTEMPTS = 10

let socket: WebSocket | null = null
let reconnectTimer: ReturnType<typeof setTimeout> | null = null
let reconnectAttempts = 0
let isIntentionallyClosed = false

function clearReconnectTimer() {
  if (reconnectTimer !== null) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
}

function scheduleReconnect() {
  if (isIntentionallyClosed || reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) return
  clearReconnectTimer()
  reconnectTimer = setTimeout(() => {
    reconnectAttempts += 1
    connect()
  }, RECONNECT_DELAY_MS)
}

function connect() {
  const token = useAuthStore.getState().accessToken
  if (!token) return

  if (socket && (socket.readyState === WebSocket.CONNECTING || socket.readyState === WebSocket.OPEN)) {
    return
  }

  socket = new WebSocket(`${WS_BASE_URL}/ws/notifications?token=${encodeURIComponent(token)}`)

  socket.onopen = () => {
    reconnectAttempts = 0
    clearReconnectTimer()
  }

  socket.onmessage = (event: MessageEvent) => {
    try {
      const message = JSON.parse(event.data as string) as NotificationMessage
      useNotificationStore.getState().addNotification(message)
    } catch (err) {
      if (import.meta.env.DEV) {
        console.warn('[notificationService] Failed to parse WebSocket message:', err)
      }
    }
  }

  socket.onclose = () => {
    socket = null
    if (!isIntentionallyClosed) {
      scheduleReconnect()
    }
  }

  socket.onerror = () => {
    // onclose will fire after onerror and handle reconnect
  }
}

export function startNotificationService() {
  isIntentionallyClosed = false
  reconnectAttempts = 0
  connect()
}

export function stopNotificationService() {
  isIntentionallyClosed = true
  clearReconnectTimer()
  if (socket) {
    socket.close()
    socket = null
  }
}
