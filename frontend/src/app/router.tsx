import { Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from './ProtectedRoute'
import { PublicOnlyRoute } from './PublicOnlyRoute'
import { AppLayout } from '../components/layout/AppLayout'
import { DashboardPage } from '../pages/DashboardPage'
import { GlucoseTrackerPage } from '../pages/GlucoseTrackerPage'
import { InsulinTrackerPage } from '../pages/InsulinTrackerPage'
import { LoginPage } from '../pages/LoginPage'
import { NotFoundPage } from '../pages/NotFoundPage'
import { PetDetailPage } from '../pages/PetDetailPage'
import { PetsPage } from '../pages/PetsPage'
import { RegisterPage } from '../pages/RegisterPage'

export function AppRouter() {
  return (
    <Routes>
      <Route element={<PublicOnlyRoute />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/pets" element={<PetsPage />} />
          <Route path="/pets/:petId" element={<PetDetailPage />} />
          <Route path="/glucose" element={<GlucoseTrackerPage />} />
          <Route path="/insulin" element={<InsulinTrackerPage />} />
          <Route path="/home" element={<Navigate to="/dashboard" replace />} />
        </Route>
      </Route>
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
