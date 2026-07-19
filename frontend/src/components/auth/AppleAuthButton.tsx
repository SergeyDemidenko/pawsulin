import { useCallback, useEffect, useState } from 'react'
import { appleClientId, isAppleAuthEnabled } from '../../config/auth'

const APPLE_JS_SDK_SCRIPT_ID = 'apple-id-auth-sdk'
const APPLE_JS_SDK_SCRIPT_SRC = 'https://appleid.cdn-apple.com/appleauth/static/jsapi/appleid/1/en_US/appleid.auth.js'

interface AppleAuthButtonProps {
  onCredential: (idToken: string, firstName?: string, lastName?: string) => void | Promise<void>
  onError: (message: string) => void
}

function loadAppleSdk(onLoad: () => void, onError: () => void) {
  if (window.AppleID) {
    onLoad()
    return
  }

  const existingScript = document.getElementById(APPLE_JS_SDK_SCRIPT_ID) as HTMLScriptElement | null
  if (existingScript) {
    existingScript.addEventListener('load', onLoad, { once: true })
    existingScript.addEventListener('error', onError, { once: true })
    return
  }

  const script = document.createElement('script')
  script.id = APPLE_JS_SDK_SCRIPT_ID
  script.src = APPLE_JS_SDK_SCRIPT_SRC
  script.async = true
  script.defer = true
  script.crossOrigin = 'anonymous'
  script.addEventListener('load', onLoad, { once: true })
  script.addEventListener('error', onError, { once: true })
  document.head.appendChild(script)
}

export function AppleAuthButton({ onCredential, onError }: AppleAuthButtonProps) {
  const [isReady, setIsReady] = useState(false)

  useEffect(() => {
    if (!isAppleAuthEnabled || !appleClientId) {
      return
    }

    let isCancelled = false
    const clientId = appleClientId

    const handleLoad = () => {
      if (isCancelled || !window.AppleID) {
        return
      }

      window.AppleID.auth.init({
        clientId,
        scope: 'name email',
        redirectURI: window.location.origin,
        usePopup: true,
      })

      if (!isCancelled) {
        setIsReady(true)
      }
    }

    const handleError = () => {
      if (!isCancelled) {
        onError('Unable to load Apple sign-in. Please try again later.')
      }
    }

    loadAppleSdk(handleLoad, handleError)

    return () => {
      isCancelled = true
    }
  }, [onError])

  const handleClick = useCallback(async () => {
    if (!window.AppleID) {
      onError('Apple sign-in is not available right now.')
      return
    }

    try {
      const response = await window.AppleID.auth.signIn()
      const idToken = response.authorization.id_token
      if (!idToken) {
        onError('Apple sign-in did not return an identity token.')
        return
      }

      const firstName = response.user?.name?.firstName
      const lastName = response.user?.name?.lastName
      await onCredential(idToken, firstName, lastName)
    } catch {
      // User cancelled or other sign-in error
      onError('Apple sign-in was cancelled or failed. Please try again.')
    }
  }, [onCredential, onError])

  if (!isAppleAuthEnabled) {
    return null
  }

  return (
    <button
      type="button"
      onClick={handleClick}
      disabled={!isReady}
      className="flex w-full items-center justify-center gap-2 rounded-full border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:text-slate-400"
    >
      <svg aria-hidden="true" viewBox="0 0 24 24" className="h-4 w-4 shrink-0 fill-current">
        <path d="M17.05 20.28c-.98.95-2.05.8-3.08.35-1.09-.46-2.09-.48-3.24 0-1.44.62-2.2.44-3.06-.35C2.79 15.25 3.51 7.7 9.05 7.42c1.38.07 2.33.74 3.14.8 1.2-.24 2.35-.93 3.63-.84 1.54.13 2.69.72 3.44 1.83-3.15 1.88-2.4 5.98.62 7.14-.63 1.62-1.45 3.22-2.83 3.93ZM12.03 7.25c-.15-2.23 1.66-4.07 3.74-4.25.29 2.58-2.34 4.5-3.74 4.25Z" />
      </svg>
      Continue with Apple
    </button>
  )
}
