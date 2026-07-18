import { lazy, Suspense } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from './ProtectedRoute'
import { PublicOnlyRoute } from './PublicOnlyRoute'
import { AppLayout } from '../components/layout/AppLayout'

const DashboardPage = lazy(async () => import('../pages/DashboardPage').then((module) => ({ default: module.DashboardPage })))
const GlucoseTrackerPage = lazy(async () =>
  import('../pages/GlucoseTrackerPage').then((module) => ({ default: module.GlucoseTrackerPage })),
)
const InsulinTrackerPage = lazy(async () =>
  import('../pages/InsulinTrackerPage').then((module) => ({ default: module.InsulinTrackerPage })),
)
const LoginPage = lazy(async () => import('../pages/LoginPage').then((module) => ({ default: module.LoginPage })))
const NotFoundPage = lazy(async () => import('../pages/NotFoundPage').then((module) => ({ default: module.NotFoundPage })))
const PetDetailPage = lazy(async () => import('../pages/PetDetailPage').then((module) => ({ default: module.PetDetailPage })))
const PetsPage = lazy(async () => import('../pages/PetsPage').then((module) => ({ default: module.PetsPage })))
const RegisterPage = lazy(async () => import('../pages/RegisterPage').then((module) => ({ default: module.RegisterPage })))

function RouterFallback() {
  return <p className="px-4 py-8 text-sm text-slate-600">Loading page…</p>
}

export function AppRouter() {
  return (
    <Suspense fallback={<RouterFallback />}>
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
    </Suspense>
  )
}
