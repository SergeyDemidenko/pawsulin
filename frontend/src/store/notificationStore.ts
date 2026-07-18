import { create } from 'zustand'
import type { NotificationMessage } from '../types/alert'

const MAX_NOTIFICATIONS = 50

interface NotificationState {
  notifications: NotificationMessage[]
  unreadCount: number
}

interface NotificationActions {
  addNotification: (notification: NotificationMessage) => void
  markAllRead: () => void
  clearNotifications: () => void
}

type NotificationStore = NotificationState & NotificationActions

export const useNotificationStore = create<NotificationStore>((set) => ({
  notifications: [],
  unreadCount: 0,

  addNotification: (notification) =>
    set((state) => {
      const updated = [notification, ...state.notifications].slice(0, MAX_NOTIFICATIONS)
      return { notifications: updated, unreadCount: state.unreadCount + 1 }
    }),

  markAllRead: () => set({ unreadCount: 0 }),

  clearNotifications: () => set({ notifications: [], unreadCount: 0 }),
}))
