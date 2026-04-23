export const formatarCpf = (cpf) => {
  if (!cpf) {
    return "-";
  }

  const apenasNumeros = String(cpf).replace(/\D/g, "");
  if (apenasNumeros.length !== 11) {
    return cpf;
  }

  return apenasNumeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
};
