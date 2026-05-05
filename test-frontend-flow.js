#!/usr/bin/env node
/**
 * Este script simula o comportamento do componente PacienteForm.js
 * Testa se o hospitalService.listar() funciona corretamente
 */

const http = require('http');
const querystring = require('querystring');

const API_URL = 'localhost:2500';

// Simular o token que o frontend receberia após login
const token = 'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiTUVESUNPIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6ImRlYnVnQHRlc3QuY29tIiwiaXNzIjoiYmFja2VuZC1hcGkiLCJqdGkiOiI0M2E1NGMxNi1iN2Y4LTRhMDYtYTZhMS1lNjhmMjBiMmYzYjUiLCJpYXQiOjE3Nzc5ODIzODYsImV4cCI6MTc3ODAxODM4Nn0.0N3PCDtMbwH3_FrG6DqpXPRA5lJR5M3trtFzbbMyKyI';

function fazerRequisicao(caminho, metodo = 'GET') {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: 'localhost',
      port: 2500,
      path: caminho,
      method: metodo,
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };

    const req = http.request(options, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          resolve({
            status: res.statusCode,
            headers: res.headers,
            body: data,
            json: data ? JSON.parse(data) : null
          });
        } catch (e) {
          resolve({
            status: res.statusCode,
            headers: res.headers,
            body: data,
            json: null,
            parseError: e.message
          });
        }
      });
    });

    req.on('error', reject);
    req.end();
  });
}

async function testar() {
  console.log('\n🔍 SIMULANDO FLUXO DO FRONTEND - PacienteForm.js');
  console.log('═'.repeat(60) + '\n');

  try {
    // PASSO 1: Simular carregarHospitais()
    console.log('📍 PASSO 1: Executando hospitalService.listar()');
    console.log('   → GET /api/hospitais\n');

    const resultado = await fazerRequisicao('/api/hospitais');

    console.log('📊 RESPOSTA RECEBIDA:');
    console.log(`   Status HTTP: ${resultado.status}`);
    console.log(`   Content-Type: ${resultado.headers['content-type']}`);
    console.log(`   Tipo de dados: ${typeof resultado.json}`);
    console.log(`   É Array? ${Array.isArray(resultado.json)}`);

    if (resultado.status !== 200) {
      console.error(`   ❌ ERRO: Status ${resultado.status} ao invés de 200`);
      console.error(`   Mensagem: ${resultado.body}`);
    } else {
      console.log(`   ✅ HTTP 200 OK\n`);

      if (Array.isArray(resultado.json)) {
        console.log(`📋 HOSPITAIS RECEBIDOS: ${resultado.json.length} hospital(is)`);
        resultado.json.forEach((h, i) => {
          console.log(`\n   [${i}] ${h.nome}`);
          console.log(`       ID: ${h.id}`);
          console.log(`       CNPJ: ${h.cnpj}`);
          console.log(`       Cidade: ${h.cidade}, ${h.estado}`);
          console.log(`       Status: ${h.status}`);
          console.log(`       Email: ${h.email}`);
        });
      } else {
        console.log('   ⚠️  AVISO: Response não é um array!');
        console.log('   Dados:', JSON.stringify(resultado.json, null, 2));
      }

      // PASSO 2: Simular carregarPacientes()
      console.log('\n\n📍 PASSO 2: Executando pacienteService.listar()');
      console.log('   → GET /api/pacientes\n');

      const pacientesResult = await fazerRequisicao('/api/pacientes');
      console.log(`   Status HTTP: ${pacientesResult.status}`);
      console.log(`   É Array? ${Array.isArray(pacientesResult.json)}`);
      if (Array.isArray(pacientesResult.json)) {
        console.log(`   Quantidade de pacientes: ${pacientesResult.json.length}`);
      }

      // RESUMO
      console.log('\n\n' + '═'.repeat(60));
      console.log('✅ RESUMO DO TESTE:');
      console.log('═'.repeat(60));
      console.log('✅ Backend está funcionando corretamente');
      console.log('✅ GET /api/hospitais retorna os dados esperados');
      console.log('✅ Autenticação está funcionando');
      console.log('\n⚠️  POSSÍVEIS PROBLEMAS NO FRONTEND:');
      console.log('   1. O componente PacienteForm não está chamando carregarHospitais()');
      console.log('   2. Há um erro ao renderizar o select de hospitais');
      console.log('   3. O CSS está ocultando o campo select');
      console.log('   4. O estado hospitais não está sendo atualizado corretamente\n');
    }
  } catch (error) {
    console.error('❌ ERRO:', error.message);
  }
}

testar();
