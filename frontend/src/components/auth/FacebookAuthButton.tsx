import { useCallback, useEffect, useState } from 'react'
import { facebookAppId, isFacebookAuthEnabled } from '../../config/auth'

const FACEBOOK_SDK_SCRIPT_ID = 'facebook-jssdk'
const FACEBOOK_SDK_SCRIPT_SRC = 'https://connect.facebook.net/en_US/sdk.js'
const FACEBOOK_GRAPH_API_VERSION = 'v22.0'

interface FacebookAuthButtonProps {
  onCredential: (accessToken: string) => void | Promise<void>
  onError: (message: string) => void
}

function loadFacebookSdk(appId: string, onLoad: () => void, onError: () => void) {
  const initializeSdk = () => {
    if (!window.FB) {
      onError()
      return
    }

    window.FB.init({
      appId,
      cookie: true,
      xfbml: false,
      version: FACEBOOK_GRAPH_API_VERSION,
    })
    onLoad()
  }

  if (window.FB) {
    initializeSdk()
    return
  }

  const existingScript = document.getElementById(FACEBOOK_SDK_SCRIPT_ID) as HTMLScriptElement | null
  if (existingScript) {
    existingScript.addEventListener('load', initializeSdk, { once: true })
    existingScript.addEventListener('error', onError, { once: true })
    return
  }

  const script = document.createElement('script')
  script.id = FACEBOOK_SDK_SCRIPT_ID
  script.src = FACEBOOK_SDK_SCRIPT_SRC
  script.async = true
  script.defer = true
  script.crossOrigin = 'anonymous'
  script.addEventListener('load', initializeSdk, { once: true })
  script.addEventListener('error', onError, { once: true })
  document.head.appendChild(script)
}

export function FacebookAuthButton({ onCredential, onError }: FacebookAuthButtonProps) {
  const [isReady, setIsReady] = useState(false)

  useEffect(() => {
    if (!isFacebookAuthEnabled || !facebookAppId) {
      return
    }

    let isCancelled = false

    const handleLoad = () => {
      if (!isCancelled) {
        setIsReady(true)
      }
    }

    const handleError = () => {
      if (!isCancelled) {
        onError('Unable to load Facebook sign-in. Please try again later.')
      }
    }

    loadFacebookSdk(facebookAppId, handleLoad, handleError)

    return () => {
      isCancelled = true
    }
  }, [onError])

  const handleClick = useCallback(() => {
    if (!window.FB) {
      onError('Facebook sign-in is not available right now.')
      return
    }

    window.FB.login((response) => {
      const accessToken = response.authResponse?.accessToken
      if (!accessToken) {
        onError('Facebook sign-in did not return an access token.')
        return
      }

      void onCredential(accessToken)
    }, { scope: 'email' })
  }, [onCredential, onError])

  if (!isFacebookAuthEnabled) {
    return null
  }

  return (
    <button
      type="button"
      onClick={handleClick}
      disabled={!isReady}
      className="w-full rounded-full border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:text-slate-400"
    >
      Continue with Facebook
    </button>
  )
}
