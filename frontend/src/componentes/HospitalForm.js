import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/HospitalForm.css';

const HospitalForm = ({ onSuccess, hospitalParaEditar }) => {
  const [formData, setFormData] = useState({
    nome: '',
    cnpj: '',
    endereco: '',
    cidade: '',
    estado: '',
    telefone: '',
    email: '',
    responsavelMedico: ''
  });

  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');
  const [carregando, setCarregando] = useState(false);

  useEffect(() => {
    if (hospitalParaEditar) {
      setFormData({
        nome: hospitalParaEditar.nome || '',
        cnpj: hospitalParaEditar.cnpj || '',
        endereco: hospitalParaEditar.endereco || '',
        cidade: hospitalParaEditar.cidade || '',
        estado: hospitalParaEditar.estado || '',
        telefone: hospitalParaEditar.telefone || '',
        email: hospitalParaEditar.email || '',
        responsavelMedico: hospitalParaEditar.responsavelMedico || ''
      });
    }
  }, [hospitalParaEditar]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setErro('');
  };

  const validarCNPJ = (cnpj) => {
    return cnpj.replace(/\D/g, '').length === 14;
  };

  const formatarCNPJ = (cnpj) => {
    const cleaned = cnpj.replace(/\D/g, '');
    return cleaned.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    // Validações
    if (!formData.nome || !formData.cnpj || !formData.endereco || !formData.cidade) {
      setErro('Por favor, preencha todos os campos obrigatórios');
      return;
    }

    if (!validarCNPJ(formData.cnpj)) {
      setErro('CNPJ inválido');
      return;
    }

    setCarregando(true);

    try {
      const dados = {
        ...formData,
        cnpj: formData.cnpj.replace(/\D/g, '')
      };

      let response;
      if (hospitalParaEditar?.id) {
        // Atualizar
        response = await axios.put(`/api/hospitais/${hospitalParaEditar.id}`, dados);
        setSucesso('Hospital atualizado com sucesso!');
      } else {
        // Criar
        response = await axios.post('/api/hospitais', dados);
        setSucesso('Hospital cadastrado com sucesso!');
        setFormData({
          nome: '',
          cnpj: '',
          endereco: '',
          cidade: '',
          estado: '',
          telefone: '',
          email: '',
          responsavelMedico: ''
        });
      }

      if (onSuccess) {
        onSuccess(response.data);
      }

      setTimeout(() => setSucesso(''), 3000);
    } catch (err) {
      const mensagem = err.response?.data?.message || err.message || 'Erro ao salvar hospital';
      setErro(mensagem);
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="hospital-form-container">
      <h2>{hospitalParaEditar ? 'Editar Hospital' : 'Cadastrar Novo Hospital'}</h2>

      {erro && <div className="alerta alerta-erro">{erro}</div>}
      {sucesso && <div className="alerta alerta-sucesso">{sucesso}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="nome">Nome do Hospital *</label>
          <input
            type="text"
            id="nome"
            name="nome"
            value={formData.nome}
            onChange={handleChange}
            placeholder="Ex: Hospital Central"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="cnpj">CNPJ *</label>
          <input
            type="text"
            id="cnpj"
            name="cnpj"
            value={formData.cnpj}
            onChange={(e) => {
              let value = e.target.value.replace(/\D/g, '');
              if (value.length <= 14) {
                handleChange({
                  target: { name: 'cnpj', value }
                });
              }
            }}
            placeholder="00.000.000/0000-00"
            maxLength="18"
          />
        </div>

        <div className="form-group">
          <label htmlFor="endereco">Endereço *</label>
          <input
            type="text"
            id="endereco"
            name="endereco"
            value={formData.endereco}
            onChange={handleChange}
            placeholder="Ex: Rua das Flores, 123"
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="cidade">Cidade *</label>
            <input
              type="text"
              id="cidade"
              name="cidade"
              value={formData.cidade}
              onChange={handleChange}
              placeholder="Ex: São Paulo"
            />
          </div>

          <div className="form-group">
            <label htmlFor="estado">Estado *</label>
            <input
              type="text"
              id="estado"
              name="estado"
              value={formData.estado}
              onChange={handleChange}
              placeholder="Ex: SP"
              maxLength="2"
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="telefone">Telefone</label>
            <input
              type="tel"
              id="telefone"
              name="telefone"
              value={formData.telefone}
              onChange={handleChange}
              placeholder="Ex: (11) 98765-4321"
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Ex: hospital@email.com"
            />
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="responsavelMedico">Responsável Médico</label>
          <input
            type="text"
            id="responsavelMedico"
            name="responsavelMedico"
            value={formData.responsavelMedico}
            onChange={handleChange}
            placeholder="Ex: Dr. João Silva"
          />
        </div>

        <button type="submit" className="btn-submit" disabled={carregando}>
          {carregando ? 'Carregando...' : (hospitalParaEditar ? 'Atualizar Hospital' : 'Cadastrar Hospital')}
        </button>
      </form>
    </div>
  );
};

export default HospitalForm;
