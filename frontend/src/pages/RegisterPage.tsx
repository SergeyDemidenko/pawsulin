import { useCallback, useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { FacebookAuthButton } from '../components/auth/FacebookAuthButton'
import { GoogleAuthButton } from '../components/auth/GoogleAuthButton'
import { isFacebookAuthEnabled, isGoogleAuthEnabled } from '../config/auth'
import { loginWithFacebook, loginWithGoogle, register } from '../services/authService'
import { useAuth } from '../hooks/useAuth'
import { extractApiErrorMessage } from '../utils/apiError'

export function RegisterPage() {
  const navigate = useNavigate()
  const { setSession } = useAuth()
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isGoogleSubmitting, setIsGoogleSubmitting] = useState(false)
  const [isFacebookSubmitting, setIsFacebookSubmitting] = useState(false)
  const isSocialAuthEnabled = isGoogleAuthEnabled || isFacebookAuthEnabled

  const handleGoogleSignUp = useCallback(async (idToken: string) => {
    setError(null)
    setIsGoogleSubmitting(true)

    try {
      const session = await loginWithGoogle(idToken)
      setSession(session)
      navigate('/dashboard', { replace: true })
    } catch (requestError) {
      setError(extractApiErrorMessage(requestError, 'Could not continue with Google. Please try again.'))
    } finally {
      setIsGoogleSubmitting(false)
    }
  }, [navigate, setSession])

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError(null)

    if (password !== confirmPassword) {
      setError('Passwords do not match')
      return
    }

    const handleFacebookSignUp = useCallback(async (accessToken: string) => {
      setError(null)
      setIsFacebookSubmitting(true)

      try {
        const session = await loginWithFacebook(accessToken)
        setSession(session)
        navigate('/dashboard', { replace: true })
      } catch (requestError) {
        setError(extractApiErrorMessage(requestError, 'Could not continue with Facebook. Please try again.'))
      } finally {
        setIsFacebookSubmitting(false)
      }
    }, [navigate, setSession])

    setIsSubmitting(true)

    try {
      await register({ firstName, lastName, email, password })
      navigate('/login', {
        replace: true,
        state: { message: 'Account created successfully. Please sign in.' },
      })
    } catch (requestError) {
      setError(
        extractApiErrorMessage(requestError, 'Could not create account. Please verify input and try again.'),
      )
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <section className="mx-auto w-full max-w-md rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
      <h1 className="text-2xl font-semibold text-slate-900">Create account</h1>
      <p className="mt-2 text-sm text-slate-600">Register to start tracking your pet’s data.</p>
      {isSocialAuthEnabled ? (
        <>
          <div className="mt-6 space-y-4">
            {isGoogleAuthEnabled ? (
              <GoogleAuthButton
                text="signup_with"
                onCredential={handleGoogleSignUp}
                onError={setError}
              />
            ) : null}
            {isFacebookAuthEnabled ? (
              <FacebookAuthButton
                onCredential={handleFacebookSignUp}
                onError={setError}
              />
            ) : null}
            <p className="text-center text-xs font-medium uppercase tracking-[0.2em] text-slate-400">or continue with email</p>
          </div>
          {isGoogleSubmitting ? <p className="mt-3 text-sm text-slate-600">Continuing with Google…</p> : null}
          {isFacebookSubmitting ? <p className="mt-3 text-sm text-slate-600">Continuing with Facebook…</p> : null}
        </>
      ) : null}
      <form onSubmit={handleSubmit} className="mt-6 space-y-4">
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <label className="block">
            <span className="mb-1 block text-sm font-medium text-slate-700">First name</span>
            <input
              required
              minLength={2}
              autoComplete="given-name"
              value={firstName}
              onChange={(event) => setFirstName(event.target.value)}
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
            />
          </label>
          <label className="block">
            <span className="mb-1 block text-sm font-medium text-slate-700">Last name</span>
            <input
              required
              minLength={2}
              autoComplete="family-name"
              value={lastName}
              onChange={(event) => setLastName(event.target.value)}
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
            />
          </label>
        </div>
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
            minLength={8}
            type="password"
            autoComplete="new-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">Confirm password</span>
          <input
            required
            minLength={8}
            type="password"
            autoComplete="new-password"
            value={confirmPassword}
            onChange={(event) => setConfirmPassword(event.target.value)}
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
          />
        </label>
        {error ? <p className="text-sm text-red-600">{error}</p> : null}
        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:bg-indigo-400"
        >
          {isSubmitting ? 'Creating account…' : 'Create account'}
        </button>
      </form>
      <p className="mt-4 text-sm text-slate-600">
        Already have an account?{' '}
        <Link className="font-medium text-indigo-600 hover:text-indigo-500" to="/login">
          Sign in
        </Link>
      </p>
    </section>
  )
}
