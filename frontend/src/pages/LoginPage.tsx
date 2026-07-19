import { useCallback, useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { AppleAuthButton } from '../components/auth/AppleAuthButton'
import { FacebookAuthButton } from '../components/auth/FacebookAuthButton'
import { GoogleAuthButton } from '../components/auth/GoogleAuthButton'
import { isAppleAuthEnabled, isFacebookAuthEnabled, isGoogleAuthEnabled } from '../config/auth'
import { login, loginWithApple, loginWithFacebook, loginWithGoogle } from '../services/authService'
import { useAuth } from '../hooks/useAuth'
import { extractApiErrorMessage } from '../utils/apiError'

export function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { setSession } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isGoogleSubmitting, setIsGoogleSubmitting] = useState(false)
  const [isFacebookSubmitting, setIsFacebookSubmitting] = useState(false)
  const [isAppleSubmitting, setIsAppleSubmitting] = useState(false)
  const message = (location.state as { message?: string } | null)?.message ?? null
  const fromPath = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? '/dashboard'
  const isSocialAuthEnabled = isGoogleAuthEnabled || isFacebookAuthEnabled || isAppleAuthEnabled

  const handleGoogleSignIn = useCallback(async (idToken: string) => {
    setError(null)
    setIsGoogleSubmitting(true)

    try {
      const session = await loginWithGoogle(idToken)
      setSession(session)
      navigate(fromPath, { replace: true })
    } catch (requestError) {
      setError(extractApiErrorMessage(requestError, 'Unable to sign in with Google. Please try again.'))
    } finally {
      setIsGoogleSubmitting(false)
    }
  }, [fromPath, navigate, setSession])

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError(null)
    setIsSubmitting(true)

    try {
      const session = await login({ email, password })
      setSession(session)
      navigate(fromPath, { replace: true })
    } catch (requestError) {
      setError(extractApiErrorMessage(requestError, 'Unable to sign in. Please try again.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleFacebookSignIn = useCallback(async (accessToken: string) => {
    setError(null)
    setIsFacebookSubmitting(true)

    try {
      const session = await loginWithFacebook(accessToken)
      setSession(session)
      navigate(fromPath, { replace: true })
    } catch (requestError) {
      setError(extractApiErrorMessage(requestError, 'Unable to sign in with Facebook. Please try again.'))
    } finally {
      setIsFacebookSubmitting(false)
    }
  }, [fromPath, navigate, setSession])

  const handleAppleSignIn = useCallback(async (idToken: string, firstName?: string, lastName?: string) => {
    setError(null)
    setIsAppleSubmitting(true)

    try {
      const session = await loginWithApple(idToken, firstName, lastName)
      setSession(session)
      navigate(fromPath, { replace: true })
    } catch (requestError) {
      setError(extractApiErrorMessage(requestError, 'Unable to sign in with Apple. Please try again.'))
    } finally {
      setIsAppleSubmitting(false)
    }
  }, [fromPath, navigate, setSession])

  return (
    <section className="mx-auto w-full max-w-md rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <h1 className="text-2xl font-semibold text-slate-900">Sign in</h1>
      <p className="mt-2 text-sm text-slate-600">Use your account credentials to continue.</p>
      {message ? <p className="mt-3 rounded-md bg-emerald-50 px-3 py-2 text-sm text-emerald-700">{message}</p> : null}
      {isSocialAuthEnabled ? (
        <>
          <div className="mt-6 space-y-4">
            {isGoogleAuthEnabled ? (
              <GoogleAuthButton
                text="signin_with"
                onCredential={handleGoogleSignIn}
                onError={setError}
              />
            ) : null}
            {isFacebookAuthEnabled ? (
              <FacebookAuthButton
                onCredential={handleFacebookSignIn}
                onError={setError}
              />
            ) : null}
            {isAppleAuthEnabled ? (
              <AppleAuthButton
                onCredential={handleAppleSignIn}
                onError={setError}
              />
            ) : null}
            <p className="text-center text-xs font-medium uppercase tracking-[0.2em] text-slate-400">or continue with email</p>
          </div>
          {isGoogleSubmitting ? <p className="mt-3 text-sm text-slate-600">Signing in with Google…</p> : null}
          {isFacebookSubmitting ? <p className="mt-3 text-sm text-slate-600">Signing in with Facebook…</p> : null}
          {isAppleSubmitting ? <p className="mt-3 text-sm text-slate-600">Signing in with Apple…</p> : null}
        </>
      ) : null}
      <form onSubmit={handleSubmit} className="mt-6 space-y-4">
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Email</span>
          <input
            required
            type="email"
            autoComplete="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Password</span>
          <input
            required
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        {error ? <p className="text-sm text-red-600">{error}</p> : null}
        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:bg-indigo-400"
        >
          {isSubmitting ? 'Signing in…' : 'Sign in'}
        </button>
      </form>
      <p className="mt-4 text-sm text-slate-600">
        New here?{' '}
        <Link className="font-medium text-indigo-600 hover:text-indigo-500" to="/register">
          Create an account
        </Link>
      </p>
    </section>
  )
}
