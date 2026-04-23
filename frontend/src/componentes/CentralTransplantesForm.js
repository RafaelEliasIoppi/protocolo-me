import React, { useState, useEffect } from 'react';
import apiClient from '../services/apiClient';
import { formatarTelefone } from '../utils/telefone';
import '../styles/CentralTransplantesForm.css';

const CentralTransplantesForm = ({ onSuccess, centralParaEditar }) => {
  const [formData, setFormData] = useState({
    nome: '',
    cnpj: '',
    endereco: '',
    cidade: '',
    estado: '',
    telefone: '',
    telefonePlantao: '',
    email: '',
    emailPlantao: '',
    coordenador: '',
    telefoneCoordenador: '',
    capacidadeProcessamento: '',
    especialidadesOrgaos: ''
  });

  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');
  const [carregando, setCarregando] = useState(false);

  useEffect(() => {
    if (centralParaEditar) {
      setFormData({
        nome: centralParaEditar.nome || '',
        cnpj: centralParaEditar.cnpj || '',
        endereco: centralParaEditar.endereco || '',
        cidade: centralParaEditar.cidade || '',
        estado: centralParaEditar.estado || '',
        telefone: formatarTelefone(centralParaEditar.telefone),
        telefonePlantao: formatarTelefone(centralParaEditar.telefonePlantao),
        email: centralParaEditar.email || '',
        emailPlantao: centralParaEditar.emailPlantao || '',
        coordenador: centralParaEditar.coordenador || '',
        telefoneCoordenador: formatarTelefone(centralParaEditar.telefoneCoordenador),
        capacidadeProcessamento: centralParaEditar.capacidadeProcessamento || '',
        especialidadesOrgaos: centralParaEditar.especialidadesOrgaos || ''
      });
    }
  }, [centralParaEditar]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === 'telefone' || name === 'telefonePlantao' || name === 'telefoneCoordenador') {
      const telefoneNumerico = value.replace(/\D/g, '').slice(0, 11);
      const telefoneFormatado = formatarTelefone(telefoneNumerico);

      setFormData(prev => ({
        ...prev,
        [name]: telefoneFormatado
      }));
      setErro('');
      return;
    }

    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setErro('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    if (!formData.nome || !formData.cnpj || !formData.endereco || !formData.cidade || !formData.estado || !formData.telefone || !formData.email || !formData.coordenador) {
      setErro('Por favor, preencha todos os campos obrigatórios: Nome, CNPJ, Endereço, Cidade, Estado, Telefone, Email e Coordenador.');
      return;
    }

    setCarregando(true);

    try {
      const dados = {
        ...formData,
        cnpj: formData.cnpj.replace(/\D/g, '')
      };

      let response;
      if (centralParaEditar?.id) {
        response = await apiClient.put(`/api/centrais-transplantes/${centralParaEditar.id}`, dados);
        setSucesso('Central atualizada com sucesso!');
      } else {
        response = await apiClient.post('/api/centrais-transplantes', dados);
        setSucesso('Central de Transplantes cadastrada com sucesso!');
        setFormData({
          nome: '',
          cnpj: '',
          endereco: '',
          cidade: '',
          estado: '',
          telefone: '',
          telefonePlantao: '',
          email: '',
          emailPlantao: '',
          coordenador: '',
          telefoneCoordenador: '',
          capacidadeProcessamento: '',
          especialidadesOrgaos: ''
        });
      }

      if (onSuccess) {
        onSuccess(response.data);
      }

      setTimeout(() => setSucesso(''), 3000);
    } catch (err) {
      const mensagem =
        err.response?.data?.mensagem ||
        err.response?.data?.message ||
        err.message ||
        'Erro ao salvar central';
      setErro(mensagem);
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="central-form-container">
      <h2>{centralParaEditar ? 'Editar Central de Transplantes' : 'Cadastrar Central de Transplantes'}</h2>

      {erro && <div className="alerta alerta-erro">{erro}</div>}
      {sucesso && <div className="alerta alerta-sucesso">{sucesso}</div>}

      <form onSubmit={handleSubmit}>
        <div className="secao">
          <h3>Informações Gerais</h3>

          <div className="form-group">
            <label htmlFor="nome">Nome da Central *</label>
            <input
              type="text"
              id="nome"
              name="nome"
              value={formData.nome}
              onChange={handleChange}
              placeholder="Ex: Central de Transplantes São Paulo"
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
                  handleChange({ target: { name: 'cnpj', value } });
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
              placeholder="Ex: Avenida Paulista, 1000"
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
        </div>

        <div className="secao">
          <h3>Contatos</h3>

          <div className="form-group">
            <label htmlFor="telefone">Telefone Principal *</label>
            <input
              type="tel"
              id="telefone"
              name="telefone"
              value={formData.telefone}
              onChange={handleChange}
              maxLength="15"
              placeholder="(11) 3000-0000"
            />
          </div>

          <div className="form-group">
            <label htmlFor="telefonePlantao">Telefone de Plantão</label>
            <input
              type="tel"
              id="telefonePlantao"
              name="telefonePlantao"
              value={formData.telefonePlantao}
              onChange={handleChange}
              maxLength="15"
              placeholder="(11) 98765-4321"
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email Principal *</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="central@hospital.com"
            />
          </div>

          <div className="form-group">
            <label htmlFor="emailPlantao">Email de Plantão</label>
            <input
              type="email"
              id="emailPlantao"
              name="emailPlantao"
              value={formData.emailPlantao}
              onChange={handleChange}
              placeholder="plantao@hospital.com"
            />
          </div>
        </div>

        <div className="secao">
          <h3>Coordenação</h3>

          <div className="form-group">
            <label htmlFor="coordenador">Coordenador Responsável *</label>
            <input
              type="text"
              id="coordenador"
              name="coordenador"
              value={formData.coordenador}
              onChange={handleChange}
              placeholder="Ex: Dr. Carlos Silva"
            />
          </div>

          <div className="form-group">
            <label htmlFor="telefoneCoordenador">Telefone do Coordenador</label>
            <input
              type="tel"
              id="telefoneCoordenador"
              name="telefoneCoordenador"
              value={formData.telefoneCoordenador}
              onChange={handleChange}
              maxLength="15"
              placeholder="(11) 99999-9999"
            />
          </div>
        </div>

        <div className="secao">
          <h3>Capacidade e Especialidades</h3>

          <div className="form-group">
            <label htmlFor="capacidadeProcessamento">Capacidade de Processamento (casos/mês)</label>
            <input
              type="number"
              id="capacidadeProcessamento"
              name="capacidadeProcessamento"
              value={formData.capacidadeProcessamento}
              onChange={handleChange}
              placeholder="Ex: 50"
              min="0"
            />
          </div>

          <div className="form-group">
            <label htmlFor="especialidadesOrgaos">Especialidades de Órgãos</label>
            <textarea
              id="especialidadesOrgaos"
              name="especialidadesOrgaos"
              value={formData.especialidadesOrgaos}
              onChange={handleChange}
              placeholder="Ex: Coração, Pulmão, Fígado, Rim"
              rows="3"
            />
          </div>
        </div>

        <button type="submit" className="btn-submit" disabled={carregando}>
          {carregando ? 'Carregando...' : (centralParaEditar ? 'Atualizar Central' : 'Cadastrar Central')}
        </button>
      </form>
    </div>
  );
};

export default CentralTransplantesForm;
