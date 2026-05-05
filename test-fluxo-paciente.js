#!/usr/bin/env node

/**
 * Simula o fluxo completo do frontend para novo paciente
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
            body: responseData ? JSON.parse(responseData) : responseData,
            rawBody: responseData
          });
        } catch (e) {
          resolve({
            status: res.statusCode,
            headers: res.headers,
            body: responseData,
            rawBody: responseData
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

async function testFluxoPaciente() {
  console.log('=== Simulando fluxo de novo paciente ===\n');

  try {
    // 1. Login como ENFERMEIRO (usuário comum, não admin)
    console.log('1️⃣  Fazendo login como ENFERMEIRO...');
    const loginData = JSON.stringify({
      email: 'enfermeiro@teste.com',
      senha: 'Senha123!'
    });

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
      console.log(`❌ Erro no login: ${loginResponse.body.mensagem}`);
      return;
    }

    const token = loginResponse.body.token;
    console.log(`✅ Login bem-sucedido!`);
    console.log(`   Token (primeiros 50 chars): ${token.substring(0, 50)}...\n`);

    // 2. Acessar form de novo paciente (sem fazer requisição, simular visualização)
    console.log('2️⃣  Simulando acesso à página /cadastros/pacientes/novo');
    console.log('   (O frontend faria carregarHospitais(), carregarPacientes(), carregarEstatisticas())\n');

    // 3. Testar GET /api/hospitais (primeira requisição que PacienteForm faz)
    console.log('3️⃣  Testando GET /api/hospitais (carregarHospitais)...');
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
      console.log(`   ✅ GET /api/hospitais bem-sucedido!`);
      console.log(`   Tipo de response.data: ${typeof hospitaisResponse.body}`);
      console.log(`   É Array? ${Array.isArray(hospitaisResponse.body)}`);
      if (Array.isArray(hospitaisResponse.body)) {
        console.log(`   Quantidade: ${hospitaisResponse.body.length}`);
        if (hospitaisResponse.body.length > 0) {
          console.log(`   Primeiro hospital:`, hospitaisResponse.body[0]);
        }
      } else {
        console.log(`   Response:`, hospitaisResponse.body);
      }
    } else if (hospitaisResponse.status === 403) {
      console.log(`   ❌ Acesso negado (403)!`);
      console.log(`   O usuário NÃO pode acessar /api/hospitais`);
    } else {
      console.log(`   ❌ Erro: ${hospitaisResponse.status}`);
      console.log(`   Response:`, hospitaisResponse.body);
    }

    console.log('\n4️⃣  Testando GET /api/pacientes (carregarPacientes)...');
    const pacientesResponse = await makeRequest({
      hostname: 'localhost',
      port: 2500,
      path: '/api/pacientes',
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    console.log(`   Status: ${pacientesResponse.status}`);
    if (pacientesResponse.status === 200) {
      console.log(`   ✅ GET /api/pacientes bem-sucedido!`);
      console.log(`   Tipo: ${typeof pacientesResponse.body}`);
      if (Array.isArray(pacientesResponse.body)) {
        console.log(`   Quantidade: ${pacientesResponse.body.length}`);
      } else if (pacientesResponse.body?.content) {
        console.log(`   response.content.length: ${pacientesResponse.body.content.length}`);
      }
    } else {
      console.log(`   ❌ Erro: ${pacientesResponse.status}`);
    }

    console.log('\n5️⃣  Testando GET /api/pacientes/estatisticas...');
    const estatisticasResponse = await makeRequest({
      hostname: 'localhost',
      port: 2500,
      path: '/api/pacientes/estatisticas',
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    console.log(`   Status: ${estatisticasResponse.status}`);
    if (estatisticasResponse.status === 200) {
      console.log(`   ✅ Estatísticas carregadas`);
    } else {
      console.log(`   ⚠️ Status ${estatisticasResponse.status}`);
    }

    console.log('\n✨ Todas as requisições foram testadas!');

  } catch (error) {
    console.error('❌ Erro durante teste:', error.message);
  }
}

testFluxoPaciente();
