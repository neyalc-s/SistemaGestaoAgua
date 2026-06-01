SET ECHO OFF;

CREATE OR REPLACE FUNCTION FN_EXISTE_FAMILIA (
    p_codigo_fb IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_fb
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_EXISTE_COTA (
    p_codigo_cota IN NUMBER,
    p_codigo_fb   IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Cota_Agua
     WHERE codigo_cota = p_codigo_cota
       AND codigo_fb = p_codigo_fb
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_EXISTE_NECESSIDADE (
    p_cod_necessidade IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Necessidade
     WHERE cod_necessidade = p_cod_necessidade
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_GET_SALDO_COTA (
    p_codigo_cota IN NUMBER,
    p_codigo_fb   IN NUMBER
) RETURN NUMBER
IS
    v_saldo Cota_Agua.saldo_disponivel_ca%TYPE;
BEGIN
    SELECT saldo_disponivel_ca
      INTO v_saldo
      FROM Cota_Agua
     WHERE codigo_cota = p_codigo_cota
       AND codigo_fb = p_codigo_fb
       AND ROWNUM = 1;

    RETURN NVL(v_saldo, 0);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
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
)
RETURN NUMBER
IS
BEGIN
    RETURN NVL(p_num_membros, 0) * fn_minimo_vital_por_pessoa();
END;
/

CREATE OR REPLACE FUNCTION calcular_cota_semanal (
    p_num_membros IN NUMBER
) RETURN NUMBER
IS
BEGIN
    RETURN NVL(p_num_membros, 0) * 50;
END;
/
