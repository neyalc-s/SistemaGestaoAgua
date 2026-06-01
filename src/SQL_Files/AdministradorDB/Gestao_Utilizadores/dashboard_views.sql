SET ECHO OFF;

-- Views remotas consumidas pelo dashboard do AdministradorDB.

CREATE OR REPLACE FORCE VIEW VW_FAMILIA_BENEFICIARIA AS
SELECT * FROM Familia_Beneficiaria;

CREATE OR REPLACE FORCE VIEW VW_COTA_AGUA_STATUS AS
SELECT * FROM Cota_Agua_Status;

CREATE OR REPLACE FORCE VIEW VW_FAMILIA_COTA AS
SELECT codigo_fb,
       nome_responsavel_fb,
       num_membros_fb,
       contacto_fb,
       perfil_socioeconomico_fb,
       codigo_cota,
       volume_semanal_ca,
       cota_atribuida,
       periodo_validade_ca,
       ajuste_sazonal_ca,
       transferencia_autorizada_ca,
       saldo_disponivel_ca,
       minimo_vital_familia,
       status_cota,
       status_validade_cota,
       uso_percent,
       status_saldo
  FROM Familia_Cota;

CREATE OR REPLACE FORCE VIEW VW_PONTO_DISTRIBUICAO AS
SELECT * FROM Ponto_Distribuicao;

CREATE OR REPLACE FORCE VIEW VW_REGISTRO_CONSUMO AS
SELECT * FROM Registro_Consumo;

CREATE OR REPLACE FORCE VIEW VW_HISTORICO_ABASTECIMENTO AS
SELECT cod_abastecimento,
       codigo_pd,
       codigo_rh,
       volume_abastecido,
       data_inicio,
       data_fim,
       duracao_horas,
       estado_abastecimento
  FROM Historico_Abastecimento;

CREATE OR REPLACE FORCE VIEW VW_ABAST_EM_CURSO AS
SELECT cod_abastecimento,
       codigo_pd,
       codigo_rh,
       volume_abastecido,
       data_inicio,
       data_fim,
       duracao_horas,
       estado_abastecimento
  FROM Historico_Abastecimento
 WHERE estado_abastecimento = 'Em curso';

CREATE OR REPLACE FORCE VIEW VW_RECURSO_HIDRICO AS
SELECT * FROM Recurso_Hidrico;

CREATE OR REPLACE FORCE VIEW VW_MEDICAO_QUALIDADE_AGUA AS
SELECT cod_qualidade_agua,
       codigo_rh,
       codigo_parametro,
       nome_parametro,
       unidade_padrao,
       valor,
       data_medicao,
       equipe_id,
       nome_analista
  FROM RH_Qualidade_Agua
 WHERE cod_qualidade_agua IS NOT NULL;

ALTER VIEW VW_FAMILIA_COTA COMPILE;
