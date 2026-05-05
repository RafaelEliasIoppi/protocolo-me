#!/usr/bin/env node

const axios = require('axios');

const API_URL = 'http://localhost:2500';

async function testarFluxoCompleto() {
  console.log('\n🔍 TEST: Simulando fluxo completo de carregamento de hospitais\n');

  try {
    // 1. Login
    console.log('📍 PASSO 1: Fazendo login como enfermeiro...');
    const loginResponse = await axios.post(`${API_URL}/api/usuarios/login`, {
      email: 'enfermeiro.test@hospital.com',
      senha: 'Senha123!'
    });

    const token = loginResponse.data;
    console.log('✅ Login bem-sucedido!');
    console.log('   Token:', token.substring(0, 50) + '...');

    // 2. Chamar GET /api/hospitais
    console.log('\n📍 PASSO 2: Chamando GET /api/hospitais...');
    const config = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };

    const hospitaisResponse = await axios.get(`${API_URL}/api/hospitais`, config);

    console.log('✅ GET /api/hospitais retornou HTTP 200');
    console.log('\n📊 DADOS RECEBIDOS:');
    console.log('   Status:', hospitaisResponse.status);
    console.log('   Headers Content-Type:', hospitaisResponse.headers['content-type']);
    console.log('   Data Type:', typeof hospitaisResponse.data);
    console.log('   Is Array?', Array.isArray(hospitaisResponse.data));

    if (Array.isArray(hospitaisResponse.data)) {
      console.log('   Quantidade de hospitais:', hospitaisResponse.data.length);
      if (hospitaisResponse.data.length > 0) {
        console.log('\n   Primeiro hospital:');
        const primeiro = hospitaisResponse.data[0];
        console.log('     - ID:', primeiro.id);
        console.log('     - Nome:', primeiro.nome);
        console.log('     - CNPJ:', primeiro.cnpj);
        console.log('     - Status:', primeiro.status);
        console.log('     - Cidade:', primeiro.cidade);
        console.log('     - Estado:', primeiro.estado);
      }
    } else if (hospitaisResponse.data?.content) {
      console.log('   Data tem propriedade .content (Array?)', Array.isArray(hospitaisResponse.data.content));
      console.log('   Quantidade de hospitais:', hospitaisResponse.data.content?.length);
    } else {
      console.log('   ⚠️  AVISO: Data não é array e não tem propriedade .content');
      console.log('   Dados completos:', JSON.stringify(hospitaisResponse.data, null, 2));
    }

    console.log('\n📍 PASSO 3: Verificando pacientes...');
    const pacientesResponse = await axios.get(`${API_URL}/api/pacientes`, config);
    console.log('✅ GET /api/pacientes OK');
    console.log('   Tipo:', typeof pacientesResponse.data);
    console.log('   Is Array?', Array.isArray(pacientesResponse.data));
    if (Array.isArray(pacientesResponse.data)) {
      console.log('   Quantidade:', pacientesResponse.data.length);
    }

    console.log('\n✅ TESTE COMPLETO - Tudo funcionando!\n');

  } catch (error) {
    console.error('\n❌ ERRO:', error.message);
    if (error.response) {
      console.error('   Status:', error.response.status);
      console.error('   Data:', error.response.data);
    } else if (error.request) {
      console.error('   Sem resposta do servidor');
    }
    process.exit(1);
  }
}

testarFluxoCompleto();
