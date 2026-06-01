SET ECHO OFF;

CREATE OR REPLACE FUNCTION FN_EXISTE_EQUIPE_TECNICA (
    p_equipe_id IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Equipe_Tecnica
     WHERE equipe_id = p_equipe_id
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_EXISTE_COMITE (
    p_cod_comite_responsavel IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Comite
     WHERE cod_comite_responsavel = p_cod_comite_responsavel
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_EXISTE_ANALISTA_QUALIDADE (
    p_equipe_id        IN NUMBER,
    p_codigo_parametro IN NUMBER DEFAULT NULL
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM Analista_Qualidade
     WHERE equipe_id = p_equipe_id
       AND (p_codigo_parametro IS NULL OR codigo_parametro = p_codigo_parametro)
       AND ROWNUM = 1;

    IF v_count > 0 THEN RETURN 1; END IF;
    RETURN 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION verifica_disjuncao_equipe (
    p_equipe_id IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_count
      FROM (
        SELECT 1 FROM Tecnico_Manutencao WHERE equipe_id = p_equipe_id
        UNION ALL
        SELECT 1 FROM Analista_Qualidade WHERE equipe_id = p_equipe_id
        UNION ALL
        SELECT 1 FROM Educador_Comunitario WHERE equipe_id = p_equipe_id
      );

    IF v_count > 1 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Equipe tecnica em mais de uma subclasse.');
    END IF;

    RETURN 1;
END;
/
