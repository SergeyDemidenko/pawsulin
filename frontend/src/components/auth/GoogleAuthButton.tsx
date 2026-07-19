import { useEffect, useRef } from 'react'

const GOOGLE_IDENTITY_SCRIPT_SRC = 'https://accounts.google.com/gsi/client'
const googleClientId = (import.meta.env.VITE_GOOGLE_CLIENT_ID as string | undefined)?.trim()

export const isGoogleAuthEnabled = Boolean(googleClientId)

type GoogleButtonText = 'signin_with' | 'signup_with'

interface GoogleAuthButtonProps {
  text: GoogleButtonText
  onCredential: (idToken: string) => void | Promise<void>
  onError: (message: string) => void
}

function loadGoogleIdentityScript(onLoad: () => void, onError: () => void) {
  const existingScript = document.querySelector<HTMLScriptElement>(`script[src="${GOOGLE_IDENTITY_SCRIPT_SRC}"]`)

  if (existingScript) {
    if (window.google) {
      onLoad()
      return
    }

    existingScript.addEventListener('load', onLoad, { once: true })
    existingScript.addEventListener('error', onError, { once: true })
    return
  }

  const script = document.createElement('script')
  script.src = GOOGLE_IDENTITY_SCRIPT_SRC
  script.async = true
  script.defer = true
  script.addEventListener('load', onLoad, { once: true })
  script.addEventListener('error', onError, { once: true })
  document.head.appendChild(script)
}

export function GoogleAuthButton({ text, onCredential, onError }: GoogleAuthButtonProps) {
  const buttonRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (!isGoogleAuthEnabled || !googleClientId || !buttonRef.current) {
      return
    }

    let isCancelled = false

    const handleLoad = () => {
      if (isCancelled || !buttonRef.current || !window.google) {
        return
      }

      window.google.accounts.id.initialize({
        client_id: googleClientId,
        callback: ({ credential }) => {
          if (!credential) {
            onError('Google sign-in did not return a credential.')
            return
          }

          void onCredential(credential)
        },
      })

      buttonRef.current.innerHTML = ''
      window.google.accounts.id.renderButton(buttonRef.current, {
        theme: 'outline',
        size: 'large',
        shape: 'pill',
        text,
        width: buttonRef.current.offsetWidth || 320,
      })
    }

    const handleScriptError = () => {
      if (!isCancelled) {
        onError('Unable to load Google sign-in. Please try again later.')
      }
    }

    loadGoogleIdentityScript(handleLoad, handleScriptError)

    return () => {
      isCancelled = true
    }
  }, [onCredential, onError, text])

  if (!isGoogleAuthEnabled) {
    return null
  }

  return <div ref={buttonRef} className="w-full overflow-hidden rounded-full" />
}
