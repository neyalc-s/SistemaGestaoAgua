SET ECHO OFF;

CREATE OR REPLACE FUNCTION FN_EXISTE_RECURSO_HIDRICO (
    p_codigo_rh IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Recurso_Hidrico
     WHERE codigo_rh = p_codigo_rh
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_EXISTE_PARAMETRO_QUALIDADE (
    p_codigo_parametro IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Parametro_Qualidade
     WHERE codigo_parametro = p_codigo_parametro
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_EXISTE_TRANSFERENCIA_COTA (
    p_codigo_tc IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Transferencia_Cota
     WHERE codigo_tc = p_codigo_tc
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_EXISTE_TRANSFER_FAMILIA (
    p_codigo_fb IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM FAMILIA_DOA_REC_TRANSFER
     WHERE codigo_fb = p_codigo_fb
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION fn_minimo_vital_por_pessoa
RETURN NUMBER
IS
BEGIN
    RETURN 10;
END;
/

CREATE OR REPLACE FUNCTION fn_calcular_min_vital_fam (
    p_num_membros IN NUMBER
) RETURN NUMBER
IS
BEGIN
    RETURN NVL(p_num_membros, 0) * fn_minimo_vital_por_pessoa();
END;
/
