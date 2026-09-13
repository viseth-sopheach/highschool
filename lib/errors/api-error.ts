// Normalizes the RFC 7807 ProblemDetail shape returned by GlobalExceptionHandler
export interface ApiFieldErrors {
  [field: string]: string;
}

export interface ApiError {
  status: number;
  title: string;
  detail: string;
  fieldErrors?: ApiFieldErrors;
}

export function toApiError(error: unknown): ApiError {
  const anyErr = error as {
    response?: {
      status?: number;
      data?: { title?: string; detail?: string; fieldErrors?: ApiFieldErrors };
    };
    message?: string;
  };

  if (anyErr.response?.data) {
    const { title, detail, fieldErrors } = anyErr.response.data;
    return {
      status: anyErr.response.status ?? 500,
      title: title ?? "Something went wrong",
      detail: detail ?? "Please try again.",
      fieldErrors,
    };
  }

  return {
    status: 0,
    title: "Network error",
    detail: "Could not reach the server. Check your connection and try again.",
  };
}