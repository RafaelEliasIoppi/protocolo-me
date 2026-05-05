#!/usr/bin/env node

/**
 * Testa com usuários já existentes e cria novos se necessário
 */

const http = require('http');

function makeRequest(options, data = null) {
  return new Promise((resolve, reject) => {
    const req = http.request(options, (res) => {
      let responseData = '';
      res.on('data', (chunk) => {
        responseData += chunk;
      });
      res.on('end', () => {
        try {
          resolve({
            status: res.statusCode,
            headers: res.headers,
            body: responseData ? JSON.parse(responseData) : responseData
          });
        } catch (e) {
          resolve({
            status: res.statusCode,
            headers: res.headers,
            body: responseData
          });
        }
      });
    });

    req.on('error', reject);
    if (data) {
      req.write(data);
    }
    req.end();
  });
}

async function criarOuTestarUsuario(email, senha, nome, role) {
  // Tentar registrar
  const registerData = JSON.stringify({ email, senha, nome, role });
  const registerResponse = await makeRequest({
    hostname: 'localhost',
    port: 2500,
    path: '/api/usuarios',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': registerData.length
    }
  }, registerData);

  if (registerResponse.status === 201) {
    return { status: 'criado', usuario: registerResponse.body };
  } else if (registerResponse.status === 400 || registerResponse.status === 409) {
    return { status: 'ja_existe', erro: registerResponse.body.detalhes?.email || 'Email já existe' };
  } else {
    return { status: 'erro', erro: registerResponse.body };
  }
}

async function testarComUsuario(email, senha, role) {
  console.log(`\n🧪 Testando com ${role}...`);

  try {
    //1. Login
    const loginData = JSON.stringify({ email, senha });
    const loginResponse = await makeRequest({
      hostname: 'localhost',
      port: 2500,
      path: '/api/usuarios/login',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Content-Length': loginData.length
      }
    }, loginData);

    if (loginResponse.status !== 200) {
      console.log(`   ❌ Login falhou: ${loginResponse.body.mensagem}`);
      return false;
    }

    console.log(`   ✅ Login bem-sucedido`);
    const token = loginResponse.body.token;

    // 2. Testar GET /api/hospitais
    const hospitaisResponse = await makeRequest({
      hostname: 'localhost',
      port: 2500,
      path: '/api/hospitais',
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    console.log(`   GET /api/hospitais: ${hospitaisResponse.status}`);
    if (hospitaisResponse.status === 200) {
      const count = Array.isArray(hospitaisResponse.body) ? hospitaisResponse.body.length : 0;
      console.log(`   ✅ ${count} hospitais encontrados`);
      if (count > 0) {
        console.log(`      Primeiro: ${hospitaisResponse.body[0].nome}`);
      }
      return true;
    } else if (hospitaisResponse.status === 403) {
      console.log(`   ❌ Acesso negado (403)! Role ${role} não tem permissão.`);
      return false;
    } else {
      console.log(`   ❌ Erro: ${hospitaisResponse.status}`);
      return false;
    }
  } catch (error) {
    console.log(`   ❌ Erro: ${error.message}`);
    return false;
  }
}

async function main() {
  console.log('=== Testando acesso  de diferentes usuários ===\n');

  // Criar usuários de teste
  console.log('1️⃣  Criando/verificando usuários de teste...\n');

  const usuarios = [
    { email: 'medico.test@hospital.com', senha: 'Medico@123', nome: 'Dr. Teste Médico', role: 'MEDICO' },
    { email: 'enfermeiro.test@hospital.com', senha: 'Enferm@123', nome: 'Enfermeiro Teste', role: 'ENFERMEIRO' },
    { email: 'coordenador.test@hospital.com', senha: 'Coord@123', nome: 'Coordenador Teste', role: 'COORDENADOR_TRANSPLANTES' },
    { email: 'central.test@hospital.com', senha: 'Central@123', nome: 'Central de Transplantes', role: 'CENTRAL_TRANSPLANTES' }
  ];

  for (const u of usuarios) {
    const result = await criarOuTestarUsuario(u.email, u.senha, u.nome, u.role);
    if (result.status === 'criado') {
      console.log(`   ✅ Criado: ${u.email} (${u.role})`);
    } else if (result.status === 'ja_existe') {
      console.log(`   ℹ️  Já existe: ${u.email} (${u.role})`);
    } else {
      console.log(`   ⚠️ Erro ao criar ${u.email}: ${result.erro}`);
    }
  }

  // Testar cada usuário
  console.log('\n2️⃣  Testando acesso a /api/hospitais com cada role...\n');

  let resultados = [];

  for (const u of usuarios) {
    const success = await testarComUsuario(u.email, u.senha, u.role);
    resultados.push({ role: u.role, success });
  }

  // Resumo
  console.log('\n📊 RESUMO:');
  console.log('─'.repeat(40));

  const sucessos = resultados.filter(r => r.success).length;
  const falhas = resultados.filter(r => !r.success).length;

  for (const r of resultados) {
    const emoji = r.success ? '✅' : '❌';
    console.log(`${emoji} ${r.role}`);
  }

  console.log('─'.repeat(40));
  console.log(`Total: ${sucessos}/${resultados.length} roles com acesso aos hospitais\n`);

  if (falhas > 0) {
    console.log('⚠️ Atenção: Alguns usuários não conseguem acessar GET /api/hospitais');
    console.log('Isso pode ser o motivo pelo qual alguns usuários não veem hospitais no formulário.');
  }
}

main();
