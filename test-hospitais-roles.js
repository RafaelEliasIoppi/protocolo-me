#!/usr/bin/env node

/**
 * Script para testar carregamento de hospitais com diferentes roles
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

async function testRole(email, senha, role) {
  console.log(`\n=== Testando com role: ${role} ===`);
  console.log(`Email: ${email}`);

  try {
    // 1. Fazer login
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

    console.log(`Login Status: ${loginResponse.status}`);

    if (loginResponse.status !== 200) {
      console.log(`❌ Erro no login: ${loginResponse.body.mensagem}`);
      return;
    }

    const token = loginResponse.body.token;
    console.log(`✅ Login bem-sucedido!`);

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

    console.log(`Hospitais Status: ${hospitaisResponse.status}`);

    if (hospitaisResponse.status === 200) {
      const count = Array.isArray(hospitaisResponse.body) ? hospitaisResponse.body.length : 0;
      console.log(`✅ GET /api/hospitais retornou ${count} hospitais`);
    } else if (hospitaisResponse.status === 403) {
      console.log(`❌ Acesso negado (403) - usuário NÃO tem permissão!`);
    } else {
      console.log(`❌ Erro: ${hospitaisResponse.status}`);
      console.log(`   Response:`, hospitaisResponse.body);
    }

  } catch (error) {
    console.error('❌ Erro:', error.message);
  }
}

async function main() {
  console.log('=== Testando GET /api/hospitais com diferentes roles ===');

  // Primeiro, vamos criar usuários de teste se não existirem
  console.log('\n1️⃣  Criando usuários de teste...\n');

  const usersToCreate = [
    {
      email: 'medico@teste.com',
      senha: 'Senha123!',
      nome: 'Dr. Teste Médico',
      role: 'MEDICO'
    },
    {
      email: 'enfermeiro@teste.com',
      senha: 'Senha123!',
      nome: 'Enfermeiro Teste',
      role: 'ENFERMEIRO'
    },
    {
      email: 'central@teste.com',
      senha: 'Senha123!',
      nome: 'Central Transplantes',
      role: 'CENTRAL_TRANSPLANTES'
    }
  ];

  for (const user of usersToCreate) {
    try {
      const registerData = JSON.stringify(user);
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

      if (registerResponse.status === 201 || registerResponse.status === 400) {
        console.log(`✅ Usuário ${user.email} (${user.role}) pronto`);
      }
    } catch (e) {
      console.log(`⚠️ Erro ao criar ${user.email}: ${e.message}`);
    }
  }

  // Agora testar cada role
  console.log('\n2️⃣  Testando acesso a /api/hospitais...\n');

  await testRole('admin@protocolo.me', 'Admin123!', 'ADMIN');
  await testRole('medico@teste.com', 'Senha123!', 'MEDICO');
  await testRole('enfermeiro@teste.com', 'Senha123!', 'ENFERMEIRO');
  await testRole('central@teste.com', 'Senha123!', 'CENTRAL_TRANSPLANTES');
}

main();
