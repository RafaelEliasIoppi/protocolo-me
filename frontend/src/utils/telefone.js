export const formatarTelefone = (telefone) => {
  if (!telefone) {
    return "-";
  }

  const apenasNumeros = String(telefone).replace(/\D/g, "").slice(0, 11);

  if (!apenasNumeros) {
    return "-";
  }

  if (apenasNumeros.length <= 2) {
    return `(${apenasNumeros}`;
  }

  if (apenasNumeros.length <= 6) {
    return `(${apenasNumeros.slice(0, 2)}) ${apenasNumeros.slice(2)}`;
  }

  if (apenasNumeros.length <= 10) {
    return `(${apenasNumeros.slice(0, 2)}) ${apenasNumeros.slice(2, 6)}-${apenasNumeros.slice(6)}`;
  }

  return `(${apenasNumeros.slice(0, 2)}) ${apenasNumeros.slice(2, 7)}-${apenasNumeros.slice(7)}`;
};