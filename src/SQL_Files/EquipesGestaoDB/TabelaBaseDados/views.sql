SET ECHO OFF;

CREATE OR REPLACE FORCE VIEW VW_COMITE AS
SELECT cod_comite_responsavel,
       nome_comite,
       data_criacao
  FROM Comite;
CREATE OR REPLACE FORCE VIEW VW_EQUIPE_TECNICA AS SELECT * FROM Equipe_Tecnica;
CREATE OR REPLACE FORCE VIEW vw_equipe_tecnica AS SELECT * FROM Equipe_Tecnica;
CREATE OR REPLACE FORCE VIEW VW_TECNICO_MANUTENCAO AS
SELECT e.equipe_id, e.nome, e.area_actuacao, e.nivel_formacao, e.contacto,
       e.supervisor_responsavel, t.habilidade_tecnica, t.tempo_medio_resposta
  FROM Equipe_Tecnica e
  JOIN Tecnico_Manutencao t ON t.equipe_id = e.equipe_id;

CREATE OR REPLACE FORCE VIEW VW_ANALISTA_QUALIDADE AS
SELECT e.equipe_id,
       e.nome,
       e.area_actuacao,
       e.nivel_formacao,
       e.contacto,
       e.supervisor_responsavel,
       a.codigo_parametro,
       a.especialidade_analise,
       a.frequencia_amostragem
  FROM Equipe_Tecnica e
  JOIN Analista_Qualidade a
    ON a.equipe_id = e.equipe_id;

ALTER VIEW VW_ANALISTA_QUALIDADE COMPILE;

CREATE OR REPLACE FORCE VIEW VW_EDUCADOR_COMUNITARIO AS
SELECT e.equipe_id, e.nome, e.area_actuacao, e.nivel_formacao, e.contacto,
       e.supervisor_responsavel, ed.metodologia_sensibilizacao,
       ed.lingua_local, ed.comunidade_atendida
  FROM Equipe_Tecnica e
  JOIN Educador_Comunitario ed ON ed.equipe_id = e.equipe_id;

CREATE OR REPLACE FORCE VIEW VW_FERRAMENTA_MANUTENCAO AS
SELECT f.cod_ferramenta_disponivel, f.equipe_id, e.nome AS nome_equipe, f.nome_ferramenta
  FROM Ferramenta_Manutencao f
  LEFT JOIN Equipe_Tecnica e ON e.equipe_id = f.equipe_id;

CREATE OR REPLACE FORCE VIEW VW_EQUIPAMENTO_ANALISTA AS
SELECT eq.cod_equipamento, eq.equipe_id, e.nome AS nome_equipe, eq.nome_equipamento
  FROM Equipamento_Analista eq
  JOIN Equipe_Tecnica e ON e.equipe_id = eq.equipe_id;

CREATE OR REPLACE FORCE VIEW VW_MATERIAL_EDUCADOR AS
SELECT m.cod_material, m.equipe_id, e.nome AS nome_equipe, m.nome_material
  FROM Material_Educador m
  JOIN Equipe_Tecnica e ON e.equipe_id = m.equipe_id;

CREATE OR REPLACE FORCE VIEW VW_PARAMETRO_QUALIDADE_EQ AS
SELECT codigo_parametro, nome_parametro, unidade_padrao
  FROM Parametro_Qualidade;

CREATE OR REPLACE FORCE VIEW vw_equipe_tecnica_com_pds AS
SELECT e.*, p.codigo_pd, p.localizacao_pd
  FROM Equipe_Tecnica e
  LEFT JOIN Ponto_Distribuicao p
    ON p.equipe_id = e.equipe_id;

CREATE OR REPLACE FORCE VIEW vw_historico_equipe_tecnica AS
SELECT e.equipe_id, e.nome, h.cod_historico_manutencao, h.codigo_pd,
       h.data_manutencao, h.tipo_manutencao
  FROM Equipe_Tecnica e
  LEFT JOIN Historico_Manutencao h
    ON h.equipe_id = e.equipe_id;
