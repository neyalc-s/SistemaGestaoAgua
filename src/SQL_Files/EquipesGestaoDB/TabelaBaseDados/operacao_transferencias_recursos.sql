SET ECHO OFF;

-- Procedures expostas a outros nos para esta operacao.

CREATE OR REPLACE PROCEDURE PRC_VAL_ANALISTAS_TRANS (
    p_eq_ph              IN NUMBER,
    p_cod_ph             IN NUMBER,
    p_eq_turbidez        IN NUMBER,
    p_cod_turbidez       IN NUMBER,
    p_eq_temperatura     IN NUMBER,
    p_cod_temperatura    IN NUMBER,
    p_eq_cloro           IN NUMBER,
    p_cod_cloro          IN NUMBER,
    p_eq_oxigenio        IN NUMBER,
    p_cod_oxigenio       IN NUMBER,
    p_total_validos      OUT NUMBER,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
BEGIN
    p_total_validos := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    SELECT COUNT(*)
      INTO p_total_validos
      FROM Analista_Qualidade
     WHERE (equipe_id = p_eq_ph AND codigo_parametro = p_cod_ph)
        OR (equipe_id = p_eq_turbidez AND codigo_parametro = p_cod_turbidez)
        OR (equipe_id = p_eq_temperatura AND codigo_parametro = p_cod_temperatura)
        OR (equipe_id = p_eq_cloro AND codigo_parametro = p_cod_cloro)
        OR (equipe_id = p_eq_oxigenio AND codigo_parametro = p_cod_oxigenio);

    IF p_total_validos <> 5 THEN
        p_mensagem := 'Cada analista deve monitorar o parametro de qualidade correspondente.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Analistas validos para os parametros de qualidade.';
EXCEPTION
    WHEN OTHERS THEN
        p_total_validos := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/
