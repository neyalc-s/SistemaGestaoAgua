SET ECHO OFF;

-- Consolida sessoes funcionais registadas localmente.

SET DEFINE OFF;

BEGIN EXECUTE IMMEDIATE 'DROP PUBLIC SYNONYM VW_AUDITORIA_SESSOES_ADMIN'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PUBLIC SYNONYM VW_AUDITORIA_SESSOES_GERAL'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW VW_AUDITORIA_SESSOES_ADMIN'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW VW_AUDITORIA_SESSOES_GERAL'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP PROCEDURE PRC_FECHAR_SESSAO_AUDITORIA';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

CREATE OR REPLACE FORCE VIEW VW_AUDITORIA_SESSOES_GERAL AS
SELECT s.codigo_sessao,
       s.cod_funcionario,
       f.username_oracle,
       f.nome_funcionario,
       s.codigo_no,
       'AdministradorDB' AS nome_no,
       s.data_inicio,
       s.data_fim,
       s.estado_sessao,
       s.modo_login
  FROM SESSAO_FUNCIONARIO s
  JOIN FUNCIONARIO f
    ON f.cod_funcionario = s.cod_funcionario
UNION ALL
SELECT codigo_sessao,
       cod_funcionario,
       username_oracle,
       nome_funcionario,
       codigo_no,
       nome_no,
       data_inicio,
       data_fim,
       estado_sessao,
       modo_login
  FROM MV_AUD_SESSOES_FAMILIAS
UNION ALL
SELECT codigo_sessao,
       cod_funcionario,
       username_oracle,
       nome_funcionario,
       codigo_no,
       nome_no,
       data_inicio,
       data_fim,
       estado_sessao,
       modo_login
  FROM MV_AUD_SESSOES_DISTRIBUICAO
UNION ALL
SELECT codigo_sessao,
       cod_funcionario,
       username_oracle,
       nome_funcionario,
       codigo_no,
       nome_no,
       data_inicio,
       data_fim,
       estado_sessao,
       modo_login
  FROM MV_AUD_SESSOES_TRANSFERENCIAS
UNION ALL
SELECT codigo_sessao,
       cod_funcionario,
       username_oracle,
       nome_funcionario,
       codigo_no,
       nome_no,
       data_inicio,
       data_fim,
       estado_sessao,
       modo_login
  FROM MV_AUD_SESSOES_EQUIPES;

CREATE OR REPLACE FORCE VIEW VW_AUDITORIA_SESSOES_ADMIN AS
SELECT codigo_sessao,
       cod_funcionario,
       username_oracle,
       nome_funcionario,
       codigo_no,
       nome_no,
       data_inicio,
       data_fim,
       estado_sessao,
       modo_login,
       CASE
           WHEN data_fim IS NOT NULL THEN ROUND((data_fim - data_inicio) * 24 * 60)
           ELSE ROUND((SYSDATE - data_inicio) * 24 * 60)
       END AS duracao_minutos
  FROM VW_AUDITORIA_SESSOES_GERAL;

ALTER VIEW VW_AUDITORIA_SESSOES_GERAL COMPILE;
ALTER VIEW VW_AUDITORIA_SESSOES_ADMIN COMPILE;

CREATE OR REPLACE PROCEDURE PRC_REFRESH_AUDITORIA_SESSOES (
    p_sucesso  OUT NUMBER,
    p_mensagem OUT VARCHAR2
)
IS
    v_teste NUMBER;
    v_actualizados NUMBER := 0;
    v_indisponiveis VARCHAR2(1000) := NULL;

    PROCEDURE tentar_refresh (
        p_nome_no      IN VARCHAR2,
        p_teste_sql    IN VARCHAR2,
        p_nome_mv      IN VARCHAR2
    )
    IS
    BEGIN
        EXECUTE IMMEDIATE p_teste_sql INTO v_teste;
        DBMS_MVIEW.REFRESH(p_nome_mv, 'C');
        v_actualizados := v_actualizados + 1;
    EXCEPTION
        WHEN OTHERS THEN
            IF v_indisponiveis IS NULL THEN
                v_indisponiveis := p_nome_no;
            ELSE
                v_indisponiveis := v_indisponiveis || ', ' || p_nome_no;
            END IF;
    END;
BEGIN
    p_sucesso := 0;
    p_mensagem := NULL;

    tentar_refresh('FamiliasCotasDB', 'SELECT 1 FROM TESTE_CONEXAO_FAM_COTAS', 'MV_AUD_SESSOES_FAMILIAS');
    tentar_refresh('DistribuicaoConsumoDB', 'SELECT 1 FROM TESTE_CONEXAO_DIST_CONS', 'MV_AUD_SESSOES_DISTRIBUICAO');
    tentar_refresh('TransferenciasRecursosDB', 'SELECT 1 FROM TESTE_CONEXAO_TRANS_REC', 'MV_AUD_SESSOES_TRANSFERENCIAS');
    tentar_refresh('EquipesGestaoDB', 'SELECT 1 FROM TESTE_CONEXAO_EQ_GESTAO', 'MV_AUD_SESSOES_EQUIPES');

    p_sucesso := 1;
    IF v_indisponiveis IS NULL THEN
        p_mensagem := 'Auditoria de sessoes actualizada com sucesso.';
    ELSIF v_actualizados > 0 THEN
        p_mensagem := 'Auditoria de sessoes actualizada parcialmente. Nos indisponiveis: ' || v_indisponiveis || '.';
    ELSE
        p_mensagem := 'Nenhum no remoto foi actualizado. A consulta pode mostrar dados anteriores.';
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        p_sucesso := 0;
        p_mensagem := 'Erro ao actualizar auditoria de sessoes.';
END;
/

CREATE OR REPLACE PROCEDURE PRC_JOB_REF_AUD_SESSOES
IS
    v_sucesso  NUMBER;
    v_mensagem VARCHAR2(4000);
BEGIN
    PRC_REFRESH_AUDITORIA_SESSOES(v_sucesso, v_mensagem);
END;
/
