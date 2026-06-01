SET ECHO OFF;

-- Consolida sessoes funcionais registadas localmente.

BEGIN EXECUTE IMMEDIATE 'DROP VIEW VW_AUDITORIA_SESSOES_LOCAL'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE OR REPLACE VIEW VW_AUDITORIA_SESSOES_LOCAL AS
SELECT
    s.codigo_sessao,
    s.cod_funcionario,
    f.username_oracle,
    f.nome_funcionario,
    s.codigo_no,
    'EquipesGestaoDB' AS nome_no,
    s.data_inicio,
    s.data_fim,
    s.estado_sessao,
    s.modo_login
FROM SESSAO_FUNCIONARIO_LOCAL s
JOIN MV_FUNCIONARIO_LOCAL f ON f.cod_funcionario = s.cod_funcionario;
