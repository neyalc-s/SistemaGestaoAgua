SET ECHO OFF;

-- Replica alertas de qualidade recebidos do no de recursos.

CREATE OR REPLACE PROCEDURE PRC_REFRESH_ALERTAS_QUALIDADE (
    p_sucesso  OUT NUMBER,
    p_mensagem OUT VARCHAR2
)
IS
BEGIN
    p_sucesso := 0;
    p_mensagem := NULL;

    DBMS_MVIEW.REFRESH('MV_ALERTA_QUALIDADE_AGUA', 'C');

    p_sucesso := 1;
    p_mensagem := 'Alertas de qualidade actualizados com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        p_sucesso := 0;
        p_mensagem := 'Erro ao actualizar alertas de qualidade: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_JOB_REF_ALERTAS_QUAL
IS
    v_sucesso  NUMBER;
    v_mensagem VARCHAR2(4000);
BEGIN
    PRC_REFRESH_ALERTAS_QUALIDADE(v_sucesso, v_mensagem);
END;
/
