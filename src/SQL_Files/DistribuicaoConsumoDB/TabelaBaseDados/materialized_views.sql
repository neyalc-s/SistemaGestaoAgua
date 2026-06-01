SET ECHO OFF;

BEGIN
    EXECUTE IMMEDIATE 'DROP MATERIALIZED VIEW MV_DADOS_RETIRADA_AGUA';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

CREATE MATERIALIZED VIEW MV_DADOS_RETIRADA_AGUA
BUILD IMMEDIATE
REFRESH COMPLETE ON DEMAND
AS
SELECT
    f.codigo_fb,
    f.codigo_pd,
    f.nome_responsavel_fb,
    f.contacto_fb,
    f.num_membros_fb,
    f.perfil_socioeconomico_fb,
    f.estado_fb,
    c.codigo_cota,
    c.volume_semanal_ca,
    c.periodo_validade_ca,
    c.ajuste_sazonal_ca,
    c.transferencia_autorizada_ca,
    c.saldo_disponivel_ca,
    c.status_validade
FROM Familia_Beneficiaria f
JOIN Cota_Agua_Status c
  ON c.codigo_fb = f.codigo_fb;

ALTER MATERIALIZED VIEW MV_DADOS_RETIRADA_AGUA COMPILE;
