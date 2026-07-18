import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { logout } from '../../services/authService'
import { useAuth } from '../../hooks/useAuth'

const navItems = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/pets', label: 'Pets' },
  { to: '/glucose', label: 'Glucose' },
  { to: '/insulin', label: 'Insulin' },
]

export function AppLayout() {
  const navigate = useNavigate()
  const { clearAuth, email, isAuthenticated } = useAuth()

  const handleLogout = async () => {
    await logout().catch((error: unknown) => {
      console.error('Logout request failed', error)
    })
    clearAuth()
    navigate('/login', { replace: true })
  }

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <header className="border-b border-slate-200 bg-white px-4 py-3 shadow-sm">
        <div className="mx-auto flex max-w-5xl items-center justify-between gap-4">
          <span className="text-lg font-semibold">Pawsulin</span>
          {isAuthenticated ? (
            <div className="flex items-center gap-3">
              <nav className="flex flex-wrap gap-2">
                {navItems.map((item) => (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    className={({ isActive }) =>
                      `rounded-md px-3 py-1.5 text-sm font-medium transition ${
                        isActive
                          ? 'bg-indigo-600 text-white'
                          : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                      }`
                    }
                  >
                    {item.label}
                  </NavLink>
                ))}
              </nav>
              <div className="hidden text-right md:block">
                <p className="text-xs text-slate-500">Signed in as</p>
                <p className="text-sm font-medium text-slate-700">{email}</p>
              </div>
              <button
                type="button"
                onClick={handleLogout}
                className="rounded-md bg-slate-100 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-200"
              >
                Logout
              </button>
            </div>
          ) : null}
        </div>
      </header>
      <main className="mx-auto max-w-5xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}
