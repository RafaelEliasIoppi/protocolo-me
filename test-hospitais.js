#!/usr/bin/env node

/**
 * Script para testar carregamento de hospitais
 * Simula login e depois testa GET /api/hospitais
 */

const http = require('http');

// Credenciais para teste (admin padrão)
const loginData = JSON.stringify({
  email: 'admin@protocolo.me',
  senha: 'Admin123!'
});

// Função para fazer requisições HTTP
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

async function test() {
  console.log('=== Testando carregamento de hospitais ===\n');

  try {
    // 1. Fazer login
    console.log('1. Fazendo login como admin...');
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

    console.log(`   Status: ${loginResponse.status}`);

    if (loginResponse.status !== 200) {
      console.log('   ❌ Erro no login!');
      console.log('   Response:', loginResponse.body);
      process.exit(1);
    }

    const token = loginResponse.body.token;
    console.log(`   ✅ Login bem-sucedido!`);
    console.log(`   Token: ${token.substring(0, 50)}...`);
    console.log(`   Authorities: ${JSON.stringify(loginResponse.body.authorities)}\n`);

    // 2. Testar GET /api/hospitais
    console.log('2. Testando GET /api/hospitais...');
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

    console.log(`   Status: ${hospitaisResponse.status}`);

    if (hospitaisResponse.status === 200) {
      console.log(`   ✅ Requisição bem-sucedida!`);
      console.log(`   Response shape: ${typeof hospitaisResponse.body}`);
      console.log(`   Is Array: ${Array.isArray(hospitaisResponse.body)}`);
      console.log(`   Length: ${Array.isArray(hospitaisResponse.body) ? hospitaisResponse.body.length : 'N/A'}`);

      if (Array.isArray(hospitaisResponse.body)) {
        console.log(`   Primeiros hospitais:`);
        hospitaisResponse.body.slice(0, 3).forEach((h, i) => {
          console.log(`     ${i + 1}. ID: ${h.id}, Nome: ${h.nome || h.nomeHospital}`);
        });
      } else if (hospitaisResponse.body.content) {
        console.log(`   Response tem propriedade 'content': ${hospitaisResponse.body.content.length} itens`);
      } else {
        console.log(`   Full response:`, JSON.stringify(hospitaisResponse.body, null, 2));
      }
    } else if (hospitaisResponse.status === 403) {
      console.log(`   ❌ Acesso negado (403)!`);
      console.log(`   O usuario não tem permissão para acessar /api/hospitais`);
      console.log(`   Authorities do user: ${JSON.stringify(loginResponse.body.authorities)}`);
    } else {
      console.log(`   ❌ Erro na requisição!`);
      console.log(`   Response:`, hospitaisResponse.body);
    }

  } catch (error) {
    console.error('❌ Erro durante teste:', error.message);
    process.exit(1);
  }
}

test();
