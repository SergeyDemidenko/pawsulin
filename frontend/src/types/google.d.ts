export {}

declare global {
  interface Window {
    google?: GoogleNamespace
  }
}

interface GoogleNamespace {
  accounts: {
    id: {
      initialize: (options: GoogleInitializeOptions) => void
      renderButton: (element: HTMLElement, options: GoogleRenderButtonOptions) => void
    }
  }
}

interface GoogleInitializeOptions {
  client_id: string
  callback: (response: GoogleCredentialResponse) => void
}

interface GoogleCredentialResponse {
  credential?: string
}

interface GoogleRenderButtonOptions {
  theme?: 'outline' | 'filled_blue' | 'filled_black'
  size?: 'large' | 'medium' | 'small'
  text?: 'signin_with' | 'signup_with'
  shape?: 'rectangular' | 'pill' | 'circle' | 'square'
  width?: number
}
