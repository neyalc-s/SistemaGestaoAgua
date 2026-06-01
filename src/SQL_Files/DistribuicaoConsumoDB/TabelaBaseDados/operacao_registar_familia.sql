SET ECHO OFF;

-- Procedures expostas a outros nos para esta operacao.

CREATE OR REPLACE PROCEDURE PRC_VALIDAR_PONTO_FAMILIA (
    p_codigo_pd             IN NUMBER,
    p_estado_pd             OUT VARCHAR2,
    p_volume_actual_pd      OUT NUMBER,
    p_pode_continuar        OUT NUMBER,
    p_mensagem              OUT VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    p_estado_pd := NULL;
    p_volume_actual_pd := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_pd IS NULL THEN
        p_mensagem := 'Codigo do ponto de distribuicao e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd
       AND ROWNUM = 1;

    IF v_count = 0 THEN
        p_mensagem := 'Ponto de distribuicao nao encontrado.';
        RETURN;
    END IF;

    SELECT estado_operacional_pd,
           volume_actual_pd
      INTO p_estado_pd,
           p_volume_actual_pd
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd;

    IF p_estado_pd IS NULL THEN
        p_mensagem := 'Estado operacional do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF UPPER(TRIM(p_estado_pd)) NOT IN ('ACTIVO', 'OPERACIONAL') THEN
        p_mensagem := 'O ponto de distribuicao nao esta operacional.';
        RETURN;
    END IF;

    IF p_volume_actual_pd IS NULL THEN
        p_mensagem := 'Volume actual do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Ponto de distribuicao valido para registo de familia.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/
