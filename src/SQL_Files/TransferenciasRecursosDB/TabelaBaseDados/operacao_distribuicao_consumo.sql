SET ECHO OFF;

-- Procedures expostas a outros nos para esta operacao.

CREATE OR REPLACE PROCEDURE PRC_VALIDAR_RECURSO_DIST (
    p_codigo_rh   IN NUMBER,
    p_existe      OUT NUMBER,
    p_volume_rh   OUT NUMBER,
    p_mensagem    OUT VARCHAR2
)
IS
BEGIN
    p_existe := 0;
    p_volume_rh := NULL;
    p_mensagem := NULL;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Codigo do recurso hidrico e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO p_existe
      FROM Recurso_Hidrico
     WHERE codigo_rh = p_codigo_rh
       AND ROWNUM = 1;

    IF p_existe = 0 THEN
        p_mensagem := 'Recurso hidrico nao encontrado.';
        RETURN;
    END IF;

    SELECT volume_rh
      INTO p_volume_rh
      FROM Recurso_Hidrico
     WHERE codigo_rh = p_codigo_rh
       AND ROWNUM = 1;

    p_existe := 1;
    p_mensagem := 'Recurso hidrico encontrado.';
EXCEPTION
    WHEN OTHERS THEN
        p_existe := 0;
        p_volume_rh := NULL;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/
