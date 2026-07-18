import axios from 'axios'

interface ErrorResponseData extends Record<string, unknown> {
  message?: string
  error?: string
}

export function extractApiErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as unknown

    if (typeof data === 'string' && data.length > 0) {
      return data
    }

    if (typeof data === 'object' && data !== null) {
      const errorData = data as ErrorResponseData

      if (typeof errorData.message === 'string' && errorData.message.length > 0) {
        return errorData.message
      }

      if (typeof errorData.error === 'string' && errorData.error.length > 0) {
        return errorData.error
      }
    }

    if (!error.response) {
      return 'Unable to connect to the server. Please try again.'
    }
  }

  return fallback
}
