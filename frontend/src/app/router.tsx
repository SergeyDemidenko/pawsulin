import { Navigate, Route, Routes } from 'react-router-dom'
import { AppLayout } from '../components/layout/AppLayout'
import { DashboardPage } from '../pages/DashboardPage'
import { GlucoseTrackerPage } from '../pages/GlucoseTrackerPage'
import { HomePage } from '../pages/HomePage'
import { InsulinTrackerPage } from '../pages/InsulinTrackerPage'
import { NotFoundPage } from '../pages/NotFoundPage'
import { PetsPage } from '../pages/PetsPage'

export function AppRouter() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/pets" element={<PetsPage />} />
        <Route path="/glucose" element={<GlucoseTrackerPage />} />
        <Route path="/insulin" element={<InsulinTrackerPage />} />
        <Route path="/home" element={<Navigate to="/" replace />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}
