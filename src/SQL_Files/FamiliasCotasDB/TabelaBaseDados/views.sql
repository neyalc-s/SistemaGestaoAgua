SET ECHO OFF;

CREATE OR REPLACE FORCE VIEW VW_LOCALIZACAO AS SELECT * FROM Localizacao;
CREATE OR REPLACE FORCE VIEW VW_NECESSIDADE AS SELECT * FROM Necessidade;
CREATE OR REPLACE FORCE VIEW VW_FAMILIA_BENEFICIARIA AS SELECT * FROM Familia_Beneficiaria;
CREATE OR REPLACE FORCE VIEW VW_COTA_AGUA AS SELECT * FROM Cota_Agua;
CREATE OR REPLACE FORCE VIEW VW_NECESS_ASSOC_FAMILIA AS SELECT * FROM NECESS_ASSOC_FAMILIA;

CREATE OR REPLACE FORCE VIEW VW_PONTO_DISTRIBUICAO AS
SELECT
    codigo_pd,
    cod_comite_responsavel,
    equipe_id,
    codigo_rh,
    volume_actual_pd,
    localizacao_pd,
    tipo_infraestrutura_pd,
    capacidade_armazenamento_pd,
    fonte_abastecimento_pd,
    tecnologia_tratamento_pd,
    data_instalacao_pd,
    estado_operacional_pd
FROM Ponto_Distribuicao;

CREATE OR REPLACE FORCE VIEW VW_FAMILIA_LOCALIZACAO AS
SELECT f.codigo_fb, f.codigo_pd, f.cod_localizacao, f.nome_responsavel_fb,
       f.num_membros_fb, f.perfil_socioeconomico_fb, f.contacto_fb,
       f.estado_fb, l.aldeia, l.coordenadas_gps
  FROM Familia_Beneficiaria f
  LEFT JOIN Localizacao l ON l.cod_localizacao = f.cod_localizacao;

CREATE OR REPLACE FORCE VIEW vw_localizacao_todas_familias AS
SELECT * FROM VW_FAMILIA_LOCALIZACAO;

CREATE OR REPLACE FORCE VIEW VW_NECESSIDADE_FAMILIA AS
SELECT nf.codigo_fb, n.cod_necessidade, n.descricao_necessidade
  FROM NECESS_ASSOC_FAMILIA nf
  JOIN Necessidade n ON n.cod_necessidade = nf.cod_necessidade;

CREATE OR REPLACE FORCE VIEW VW_NECESSIDADE_DISP_FAMILIA AS
SELECT f.codigo_fb, n.cod_necessidade, n.descricao_necessidade
  FROM Familia_Beneficiaria f
 CROSS JOIN Necessidade n
 WHERE NOT EXISTS (
       SELECT 1
         FROM NECESS_ASSOC_FAMILIA nf
        WHERE nf.codigo_fb = f.codigo_fb
          AND nf.cod_necessidade = n.cod_necessidade
 );

CREATE OR REPLACE FORCE VIEW vw_necess_todas_familias AS
SELECT fl.codigo_fb, fl.nome_responsavel_fb, fl.estado_fb, n.cod_necessidade, n.descricao_necessidade
  FROM VW_FAMILIA_LOCALIZACAO fl
  LEFT JOIN VW_NECESSIDADE_FAMILIA n ON n.codigo_fb = fl.codigo_fb;

CREATE OR REPLACE FORCE VIEW VW_COTA_AGUA_STATUS AS
SELECT c.*,
       CASE
         WHEN c.periodo_validade_ca IS NOT NULL AND c.periodo_validade_ca < SYSDATE THEN 'EXPIRADA'
         ELSE 'VALIDA'
       END AS status_validade
  FROM Cota_Agua c;

CREATE OR REPLACE FORCE VIEW vw_familia_cota AS
SELECT
    f.codigo_fb,
    f.nome_responsavel_fb,
    f.num_membros_fb,
    f.contacto_fb,
    f.perfil_socioeconomico_fb,
    c.codigo_cota,
    c.volume_semanal_ca,
    c.volume_semanal_ca AS cota_atribuida,
    c.periodo_validade_ca,
    c.ajuste_sazonal_ca,
    c.transferencia_autorizada_ca,
    c.saldo_disponivel_ca,
    fn_calcular_min_vital_fam(f.num_membros_fb) AS minimo_vital_familia,
    CASE
        WHEN fn_calcular_min_vital_fam(f.num_membros_fb) > NVL(c.volume_semanal_ca, 0) THEN 'ABAIXO DO MINIMO VITAL'
        ELSE 'OK'
    END AS status_cota,
    CASE
        WHEN c.periodo_validade_ca IS NOT NULL AND c.periodo_validade_ca < SYSDATE THEN 'EXPIRADA'
        ELSE 'VALIDA'
    END AS status_validade_cota,
    CASE
        WHEN c.volume_semanal_ca IS NULL OR c.volume_semanal_ca = 0 THEN '0%'
        ELSE ROUND((NVL(c.saldo_disponivel_ca, 0) / c.volume_semanal_ca) * 100, 1) || '%'
    END AS uso_percent,
    CASE
        WHEN NVL(c.saldo_disponivel_ca, 0) = 0 THEN 'ZERADO'
        WHEN NVL(c.saldo_disponivel_ca, 0) <= (NVL(c.volume_semanal_ca, 0) * 0.2) THEN 'BAIXO'
        ELSE 'OK'
    END AS status_saldo
  FROM Familia_Beneficiaria f
  JOIN Cota_Agua c ON c.codigo_fb = f.codigo_fb;

CREATE OR REPLACE FORCE VIEW vw_cotas_familias AS
SELECT * FROM vw_familia_cota;

CREATE OR REPLACE FORCE VIEW vw_familias_por_ponto AS
SELECT f.codigo_fb, f.codigo_pd, f.nome_responsavel_fb, f.num_membros_fb,
       f.perfil_socioeconomico_fb, f.contacto_fb, f.estado_fb, l.aldeia
  FROM Familia_Beneficiaria f
  LEFT JOIN Localizacao l ON l.cod_localizacao = f.cod_localizacao;

CREATE OR REPLACE FORCE VIEW VW_CONSUMO_POR_COTA AS
SELECT rc.*
  FROM Registro_Consumo rc;

CREATE OR REPLACE FORCE VIEW vw_transferencias_familia AS
SELECT t.*
  FROM Transferencia_Cota t;

ALTER VIEW VW_PONTO_DISTRIBUICAO COMPILE;

ALTER VIEW VW_FAMILIA_COTA COMPILE;
