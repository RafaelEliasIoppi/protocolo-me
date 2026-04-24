export function getApiErrorMessage(error, fallbackMessage) {
  return (
    error?.response?.data?.mensagem ||
    error?.response?.data?.erro ||
    error?.response?.data?.message ||
    error?.message ||
    fallbackMessage
  );
}