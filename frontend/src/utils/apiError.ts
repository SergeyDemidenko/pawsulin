import axios from 'axios'

interface ErrorResponseData {
  message?: string
  error?: string
}

export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError<ErrorResponseData>(error)) {
    const data = error.response?.data

    if (typeof data === 'string' && data.length > 0) {
      return data
    }

    if (typeof data?.message === 'string' && data.message.length > 0) {
      return data.message
    }

    if (typeof data?.error === 'string' && data.error.length > 0) {
      return data.error
    }

    if (!error.response) {
      return 'Unable to connect to the server. Please try again.'
    }
  }

  return fallback
}
