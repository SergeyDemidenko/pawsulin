export {}

declare global {
  interface Window {
    FB?: FacebookNamespace
    fbAsyncInit?: () => void
  }
}

interface FacebookNamespace {
  init: (options: FacebookInitOptions) => void
  login: (callback: (response: FacebookLoginResponse) => void, options?: FacebookLoginOptions) => void
}

interface FacebookInitOptions {
  appId: string
  cookie?: boolean
  xfbml?: boolean
  version: string
}

interface FacebookLoginOptions {
  scope?: string
}

interface FacebookLoginResponse {
  authResponse?: {
    accessToken?: string
  }
  status?: string
}
