SET ECHO OFF;

CREATE OR REPLACE FORCE VIEW VW_RECURSO_HIDRICO AS SELECT * FROM Recurso_Hidrico;
CREATE OR REPLACE FORCE VIEW VW_PARAMETRO_QUALIDADE AS SELECT * FROM Parametro_Qualidade;
CREATE OR REPLACE FORCE VIEW VW_MEDIDA_PROT_RESPONSAVEL AS SELECT * FROM Medida_Prot_Responsavel;
CREATE OR REPLACE FORCE VIEW VW_MEDIDA_PROTECCAO AS SELECT * FROM Medida_Proteccao;
CREATE OR REPLACE FORCE VIEW VW_REC_APLICA_MED_PROT AS
SELECT rap.codigo_rh,
       rap.cod_medida_proteccao,
       mp.descricao_medida,
       resp.nome_responsavel,
       rap.data_impl
  FROM REC_APLICA_MED_PROT rap
  JOIN Medida_Proteccao mp ON mp.cod_medida_proteccao = rap.cod_medida_proteccao
  JOIN Medida_Prot_Responsavel resp ON resp.cod_responsavel = mp.cod_responsavel;
CREATE OR REPLACE FORCE VIEW VW_MEDICAO_QUALIDADE_AGUA AS SELECT * FROM Medicao_Qualidade_Agua;
CREATE OR REPLACE FORCE VIEW VW_ALERTA_QUALIDADE_AGUA AS
SELECT codigo_alerta,
       codigo_medicao,
       codigo_rh,
       mensagem_alerta,
       data_alerta
  FROM Alerta_Qualidade_Agua;
CREATE OR REPLACE FORCE VIEW VW_TRANSFERENCIA_COTA AS SELECT * FROM Transferencia_Cota;
CREATE OR REPLACE FORCE VIEW VW_FAMILIA_DOA_REC_TRANSFER AS SELECT * FROM FAMILIA_DOA_REC_TRANSFER;
CREATE OR REPLACE FORCE VIEW VW_TRANSFER_ASSOC_COTA AS SELECT * FROM TRANSFER_ASSOC_COTA;

CREATE OR REPLACE FORCE VIEW VW_FAMILIA_BENEFICIARIA AS
SELECT * FROM Familia_Beneficiaria;

CREATE OR REPLACE FORCE VIEW vw_familia_cota AS
SELECT f.codigo_fb,
       f.nome_responsavel_fb,
       f.num_membros_fb,
       f.contacto_fb,
       f.perfil_socioeconomico_fb,
       f.estado_fb,
       c.codigo_cota,
       c.volume_semanal_ca AS cota_atribuida,
       c.periodo_validade_ca,
       c.ajuste_sazonal_ca,
       c.transferencia_autorizada_ca,
       c.saldo_disponivel_ca,
       fn_calcular_min_vital_fam(f.num_membros_fb) AS minimo_vital_familia,
       CASE
         WHEN fn_calcular_min_vital_fam(f.num_membros_fb) > NVL(c.volume_semanal_ca, 0)
         THEN 'ABAIXO DO MINIMO VITAL!'
         ELSE 'OK'
       END AS status_cota,
       CASE
         WHEN c.periodo_validade_ca < TRUNC(SYSDATE) THEN 'EXPIRADA'
         ELSE 'VALIDA'
       END AS status_validade_cota,
       ROUND((c.saldo_disponivel_ca / NULLIF(c.volume_semanal_ca, 0)) * 100, 1) || '%' AS uso_percent,
       CASE
         WHEN c.saldo_disponivel_ca = 0 THEN 'ZERADO'
         WHEN c.saldo_disponivel_ca <= (c.volume_semanal_ca * 0.2) THEN 'BAIXO'
         ELSE 'OK'
       END AS status_saldo
  FROM Familia_Beneficiaria f
  JOIN Cota_Agua c
    ON c.codigo_fb = f.codigo_fb;

CREATE OR REPLACE FORCE VIEW VW_ANALISTA_QUALIDADE AS
SELECT a.equipe_id,
       a.nome,
       a.area_actuacao,
       a.nivel_formacao,
       a.contacto,
       a.supervisor_responsavel,
       a.codigo_parametro,
       p.nome_parametro,
       p.unidade_padrao,
       a.especialidade_analise,
       a.frequencia_amostragem
  FROM Analista_Qualidade a
  LEFT JOIN Parametro_Qualidade p
    ON p.codigo_parametro = a.codigo_parametro;

ALTER VIEW VW_ANALISTA_QUALIDADE COMPILE;

CREATE OR REPLACE FORCE VIEW vw_transferencias_familia AS
SELECT f.codigo_fb,
       f.nome_responsavel_fb,
       f.contacto_fb,
       f.estado_fb,
       t.codigo_tc,
       t.cod_fam_doadora_tc,
       t.cod_fam_receptora_tc,
       t.volume_cedido_tc,
       t.motivo_solicitacao_tc,
       t.data_aprovacao_tc,
       t.validade_transferencia_tc,
       fr.tipo_participacao AS papel_na_transferencia,
       NVL(fp.nome_responsavel_fb, 'Desconhecido') AS nome_parceiro,
       fp.estado_fb AS estado_parceiro_fb,
       CASE WHEN fr.tipo_participacao = 'Doa' THEN t.volume_cedido_tc ELSE 0 END AS volume_doado,
       CASE WHEN fr.tipo_participacao = 'Recebe' THEN t.volume_cedido_tc ELSE 0 END AS volume_recebido
  FROM FAMILIA_DOA_REC_TRANSFER fr
  JOIN Transferencia_Cota t ON t.codigo_tc = fr.codigo_tc
  JOIN Familia_Beneficiaria f
    ON f.codigo_fb = fr.codigo_fb
  LEFT JOIN Familia_Beneficiaria fp
    ON fp.codigo_fb = CASE
                        WHEN fr.tipo_participacao = 'Doa' THEN t.cod_fam_receptora_tc
                        ELSE t.cod_fam_doadora_tc
                      END;

CREATE OR REPLACE FORCE VIEW VW_MEDIDA_PROTECCAO_RESP AS
SELECT m.cod_medida_proteccao, m.cod_responsavel, m.descricao_medida,
       r.nome_responsavel
  FROM Medida_Proteccao m
  JOIN Medida_Prot_Responsavel r ON r.cod_responsavel = m.cod_responsavel;

CREATE OR REPLACE FORCE VIEW VW_MEDIDAS_DISPONIVEIS_RH AS
SELECT rh.codigo_rh, m.cod_medida_proteccao, m.descricao_medida, r.nome_responsavel
  FROM Recurso_Hidrico rh
 CROSS JOIN Medida_Proteccao m
  JOIN Medida_Prot_Responsavel r ON r.cod_responsavel = m.cod_responsavel
 WHERE NOT EXISTS (
       SELECT 1
         FROM REC_APLICA_MED_PROT rap
        WHERE rap.codigo_rh = rh.codigo_rh
          AND rap.cod_medida_proteccao = m.cod_medida_proteccao
 );

CREATE OR REPLACE FORCE VIEW vw_rh_med_prot_responsavel AS
SELECT rh.codigo_rh,
       rh.tipo_rh,
       rh.localizacao_rh,
       rh.volume_rh,
       rh.sazonalidade_rh,
       rh.vulnerabilidade_rh,
       rh.nivel_exploracao_rh,
       mp.cod_medida_proteccao,
       mp.descricao_medida,
       resp.nome_responsavel,
       rap.data_impl
  FROM Recurso_Hidrico rh
  LEFT JOIN REC_APLICA_MED_PROT rap ON rap.codigo_rh = rh.codigo_rh
  LEFT JOIN Medida_Proteccao mp ON mp.cod_medida_proteccao = rap.cod_medida_proteccao
  LEFT JOIN Medida_Prot_Responsavel resp ON resp.cod_responsavel = mp.cod_responsavel;

CREATE OR REPLACE FORCE VIEW vw_rh_ponto_distribuicao AS
SELECT rh.codigo_rh,
       rh.tipo_rh,
       rh.localizacao_rh,
       rh.volume_rh,
       rh.sazonalidade_rh,
       rh.vulnerabilidade_rh,
       rh.nivel_exploracao_rh,
       p.codigo_pd,
       p.localizacao_pd,
       p.tipo_infraestrutura_pd,
       p.capacidade_armazenamento_pd,
       p.fonte_abastecimento_pd,
       p.tecnologia_tratamento_pd,
       p.data_instalacao_pd,
       p.estado_operacional_pd
  FROM Recurso_Hidrico rh
  LEFT JOIN Ponto_Distribuicao p
    ON p.codigo_rh = rh.codigo_rh;

CREATE OR REPLACE FORCE VIEW vw_rh_qualidade_agua AS
SELECT rh.codigo_rh,
       rh.tipo_rh,
       rh.localizacao_rh,
       rh.volume_rh,
       rh.sazonalidade_rh,
       rh.vulnerabilidade_rh,
       rh.nivel_exploracao_rh,
       m.cod_qualidade_agua,
       m.codigo_parametro,
       pq.nome_parametro,
       pq.unidade_padrao,
       m.valor,
       m.data_medicao,
       m.equipe_id,
       a.nome AS nome_analista
  FROM Recurso_Hidrico rh
  LEFT JOIN Medicao_Qualidade_Agua m ON m.codigo_rh = rh.codigo_rh
  LEFT JOIN Parametro_Qualidade pq ON pq.codigo_parametro = m.codigo_parametro
  LEFT JOIN Analista_Qualidade a
    ON a.equipe_id = m.equipe_id;

CREATE OR REPLACE FORCE VIEW VW_HISTORICO_ABASTECIMENTO AS
SELECT h.cod_abastecimento,
       h.codigo_pd,
       p.localizacao_pd,
       h.codigo_rh,
       rh.tipo_rh,
       rh.localizacao_rh,
       rh.sazonalidade_rh,
       rh.nivel_exploracao_rh,
       h.volume_abastecido,
       h.data_inicio,
       h.data_fim,
       h.duracao_horas,
       h.estado_abastecimento
  FROM Historico_Abastecimento h
  LEFT JOIN Ponto_Distribuicao p
    ON p.codigo_pd = h.codigo_pd
  LEFT JOIN Recurso_Hidrico rh
    ON rh.codigo_rh = h.codigo_rh;
