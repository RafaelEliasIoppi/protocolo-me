-- Script seguro (transacional) para remoção de médicos, pacientes e hospitais
-- ATENÇÃO: irreversível sem backup. Faça um dump antes de executar.

BEGIN;

-- verificações (não apagam)
SELECT count(*) AS medicos FROM usuario WHERE role = 'MEDICO';
SELECT count(*) AS pacientes FROM paciente;
SELECT count(*) AS hospitais FROM hospital;

-- 1) remover médicos
DELETE FROM usuario WHERE role = 'MEDICO';

-- 2) remover anexos associados aos protocolos dos pacientes
DELETE FROM anexo_documento
WHERE protocolo_me_id IN (
  SELECT id FROM protocolo_me WHERE paciente_id IN (SELECT id FROM paciente)
) OR exame_me_id IN (
  SELECT e.id
  FROM exame_me e
  INNER JOIN protocolo_me p ON p.id = e.protocolo_me_id
  WHERE p.paciente_id IN (SELECT id FROM paciente)
);

-- 3) remover exames ligados a protocolos de pacientes
DELETE FROM exame_me
WHERE protocolo_me_id IN (
  SELECT id FROM protocolo_me WHERE paciente_id IN (SELECT id FROM paciente)
);

-- 4) remover órgãos doados ligados às doações
DELETE FROM orgao_doado
WHERE doacao_id IN (
  SELECT d.id
  FROM doacao d
  INNER JOIN protocolo_me p ON p.id = d.protocolo_me_id
  WHERE p.paciente_id IN (SELECT id FROM paciente)
);

-- 5) remover registros de doação ligados a protocolos de pacientes
DELETE FROM doacao
WHERE protocolo_me_id IN (
  SELECT id FROM protocolo_me WHERE paciente_id IN (SELECT id FROM paciente)
);

-- 6) remover protocolos ME dos pacientes
DELETE FROM protocolo_me
WHERE paciente_id IN (SELECT id FROM paciente);

-- 7) remover estatísticas/relatórios ligados a protocolos (se aplicável)
DELETE FROM estatistica_protocolo_me
WHERE protocolo_me_id IN (
  SELECT id FROM protocolo_me WHERE paciente_id IN (SELECT id FROM paciente)
);

-- 8) remover pacientes
DELETE FROM paciente;

-- 9) remover hospitais
DELETE FROM hospital;

-- verificações finais
SELECT count(*) AS medicos_depois FROM usuario WHERE role = 'MEDICO';
SELECT count(*) AS pacientes_depois FROM paciente;
SELECT count(*) AS hospitais_depois FROM hospital;

COMMIT;

-- Recomendações:
-- 1) Faça backup com pg_dump antes de rodar.
-- 2) Execute em ambiente maintenance/produção com cuidado e, se possível, em janelas de baixa atividade.
-- 3) Se quiser limitar (ex.: apenas médicos sem central), peça para eu gerar versão limitada do script.
