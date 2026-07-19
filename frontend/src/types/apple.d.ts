export {}

declare global {
  interface Window {
    AppleID?: AppleIDNamespace
  }
}

interface AppleIDNamespace {
  auth: {
    init: (options: AppleAuthInitOptions) => void
    signIn: () => Promise<AppleSignInResponse>
  }
}

interface AppleAuthInitOptions {
  clientId: string
  scope: string
  redirectURI: string
  usePopup: boolean
}

interface AppleSignInResponse {
  authorization: {
    code: string
    id_token: string
    state?: string
  }
  user?: {
    email?: string
    name?: {
      firstName?: string
      lastName?: string
    }
  }
}
