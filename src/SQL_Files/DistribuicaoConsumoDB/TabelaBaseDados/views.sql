SET ECHO OFF;

CREATE OR REPLACE FORCE VIEW VW_PONTO_DISTRIBUICAO AS SELECT * FROM Ponto_Distribuicao;
CREATE OR REPLACE FORCE VIEW VW_REGISTRO_CONSUMO AS SELECT * FROM Registro_Consumo;
CREATE OR REPLACE FORCE VIEW VW_HISTORICO_MANUTENCAO AS SELECT * FROM Historico_Manutencao;
CREATE OR REPLACE FORCE VIEW VW_HISTORICO_ABASTECIMENTO AS SELECT * FROM Historico_Abastecimento;

CREATE OR REPLACE FORCE VIEW VW_FAMILIA_BENEFICIARIA AS
SELECT DISTINCT
       codigo_fb,
       codigo_pd,
       nome_responsavel_fb,
       contacto_fb,
       num_membros_fb,
       perfil_socioeconomico_fb,
       estado_fb
  FROM MV_DADOS_RETIRADA_AGUA;

CREATE OR REPLACE FORCE VIEW VW_COTA_AGUA_STATUS AS
SELECT
       codigo_cota,
       codigo_fb,
       volume_semanal_ca,
       saldo_disponivel_ca,
       periodo_validade_ca,
       ajuste_sazonal_ca,
       transferencia_autorizada_ca,
       status_validade
  FROM MV_DADOS_RETIRADA_AGUA;

CREATE OR REPLACE FORCE VIEW vw_familia_cota AS
SELECT * FROM Familia_Cota;

CREATE OR REPLACE FORCE VIEW VW_RECURSO_HIDRICO AS
SELECT * FROM Recurso_Hidrico;

CREATE OR REPLACE FORCE VIEW VW_COMITE AS
SELECT * FROM Comite;

CREATE OR REPLACE FORCE VIEW VW_TECNICO_MANUTENCAO AS
SELECT * FROM Tecnico_Manutencao;

CREATE OR REPLACE FORCE VIEW VW_TECNICO_MANUTENCAO_OPCAO AS
SELECT equipe_id,
       nome,
       'Tecnico de Manutencao' AS tipo_equipe,
       area_actuacao,
       nivel_formacao,
       contacto,
       supervisor_responsavel
  FROM Tecnico_Manutencao;

CREATE OR REPLACE FORCE VIEW VW_PONTOS_TEC_MANUT AS
SELECT p.*,
       t.nome,
       t.area_actuacao,
       t.nivel_formacao,
       t.contacto,
       t.supervisor_responsavel,
       t.habilidade_tecnica,
       t.tempo_medio_resposta
  FROM Ponto_Distribuicao p
  LEFT JOIN Tecnico_Manutencao t
    ON t.equipe_id = p.equipe_id;

CREATE OR REPLACE FORCE VIEW VW_HISTORICO_MANUTENCAO_DET AS
SELECT h.cod_historico_manutencao,
       h.codigo_pd,
       h.equipe_id,
       h.data_manutencao,
       h.tipo_manutencao,
       p.localizacao_pd,
       p.estado_operacional_pd,
       p.nome AS nome_tecnico,
       p.area_actuacao
  FROM Historico_Manutencao h
  LEFT JOIN VW_PONTOS_TEC_MANUT p
    ON p.codigo_pd = h.codigo_pd;

CREATE OR REPLACE FORCE VIEW VW_PONTOS_POR_RECURSO AS
SELECT p.*
  FROM Ponto_Distribuicao p;

CREATE OR REPLACE FORCE VIEW VW_PONTOS_RECURSO_ASSOC AS
SELECT p.codigo_pd, p.codigo_rh AS codigo_rh_actual, p.localizacao_pd, p.tipo_infraestrutura_pd,
       p.capacidade_armazenamento_pd, p.volume_actual_pd, p.estado_operacional_pd,
       r.tipo_rh, r.localizacao_rh
  FROM Ponto_Distribuicao p
  LEFT JOIN Recurso_Hidrico r
    ON r.codigo_rh = p.codigo_rh;

CREATE OR REPLACE FORCE VIEW VW_PONTOS_COMITE_ASSOC AS
SELECT p.codigo_pd, p.cod_comite_responsavel AS cod_comite_actual, p.localizacao_pd, p.tipo_infraestrutura_pd,
       p.capacidade_armazenamento_pd, p.volume_actual_pd, p.estado_operacional_pd,
       c.nome_comite
  FROM Ponto_Distribuicao p
  LEFT JOIN Comite c
    ON c.cod_comite_responsavel = p.cod_comite_responsavel;

CREATE OR REPLACE FORCE VIEW vw_familias_por_ponto AS
SELECT * FROM Familias_Por_Ponto;

CREATE OR REPLACE FORCE VIEW VW_PONTO_R_CONSUMO AS
SELECT p.codigo_pd, p.localizacao_pd, r.codigo_rc, r.codigo_fb, r.codigo_cota,
       r.data_hora_rc, r.volume_retirado_rc, r.saldo_cota_rc
  FROM Ponto_Distribuicao p
  LEFT JOIN Registro_Consumo r ON r.codigo_pd = p.codigo_pd;

CREATE OR REPLACE FORCE VIEW VW_ABAST_EM_CURSO AS
SELECT *
  FROM Historico_Abastecimento
 WHERE estado_abastecimento = 'Em curso';

CREATE OR REPLACE FORCE VIEW VW_ABAST_CANCELAVEL AS
SELECT h.cod_abastecimento, h.codigo_pd, p.localizacao_pd, h.codigo_rh,
       h.volume_abastecido, h.data_inicio, h.data_fim, h.duracao_horas,
       h.estado_abastecimento
  FROM Historico_Abastecimento h
  JOIN Ponto_Distribuicao p ON p.codigo_pd = h.codigo_pd
 WHERE h.estado_abastecimento = 'Em curso';

CREATE OR REPLACE FORCE VIEW VW_ABAST_RECURSO_ABERTO AS
SELECT *
  FROM Historico_Abastecimento
 WHERE estado_abastecimento = 'Em curso';

CREATE OR REPLACE FORCE VIEW VW_CONSUMO_POR_COTA AS
SELECT * FROM Registro_Consumo;

CREATE OR REPLACE FORCE VIEW VW_RETIRADA_AGUA_PENDENTE AS
SELECT r.cod_retirada_pendente,
       r.codigo_fb,
       r.codigo_cota,
       r.codigo_pd,
       p.localizacao_pd,
       p.estado_operacional_pd,
       r.volume_retirado,
       r.pessoa_coleta,
       r.observacao,
       r.data_pedido,
       r.saldo_cota_snapshot,
       r.periodo_validade_snapshot,
       r.estado_familia_snapshot,
       r.estado_pendente,
       r.data_processamento,
       r.mensagem_processamento
  FROM Retirada_Agua_Pendente r
  LEFT JOIN Ponto_Distribuicao p
    ON p.codigo_pd = r.codigo_pd;
