interface JwtPayload {
  exp?: number
}

function decodeJwtPayload(token: string): JwtPayload | null {
  const parts = token.split('.')
  if (parts.length < 2) {
    return null
  }

  try {
    const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    const normalizedPayload = payload.padEnd(Math.ceil(payload.length / 4) * 4, '=')
    return JSON.parse(atob(normalizedPayload)) as JwtPayload
  } catch {
    return null
  }
}

export function isJwtExpired(token: string): boolean {
  const payload = decodeJwtPayload(token)
  if (!payload?.exp) {
    return true
  }

  return payload.exp * 1000 <= Date.now()
}
