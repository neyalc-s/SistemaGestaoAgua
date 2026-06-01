SET ECHO OFF;

CREATE OR REPLACE FUNCTION FN_EXISTE_PONTO_DISTRIBUICAO (
    p_codigo_pd IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_EXISTE_REGISTRO_CONSUMO (
    p_codigo_rc IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Registro_Consumo
     WHERE codigo_rc = p_codigo_rc
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_EXISTE_CONSUMO_FAMILIA (
    p_codigo_fb IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Registro_Consumo
     WHERE codigo_fb = p_codigo_fb
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/
