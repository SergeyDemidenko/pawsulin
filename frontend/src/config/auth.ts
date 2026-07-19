export const googleClientId = (import.meta.env.VITE_GOOGLE_CLIENT_ID as string | undefined)?.trim()
export const isGoogleAuthEnabled = Boolean(googleClientId)

export const facebookAppId = (import.meta.env.VITE_FACEBOOK_APP_ID as string | undefined)?.trim()
export const isFacebookAuthEnabled = Boolean(facebookAppId)

export const appleClientId = (import.meta.env.VITE_APPLE_CLIENT_ID as string | undefined)?.trim()
export const isAppleAuthEnabled = Boolean(appleClientId)
