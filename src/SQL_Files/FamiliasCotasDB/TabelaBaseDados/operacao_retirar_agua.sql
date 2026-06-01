SET ECHO OFF;

-- Procedures expostas a outros nos para esta operacao.

CREATE OR REPLACE PROCEDURE PRC_PRE_VALIDAR_COTA_RET (
    p_codigo_pd             IN NUMBER,
    p_codigo_fb             IN NUMBER,
    p_codigo_cota           IN NUMBER,
    p_volume_a_retirar      IN NUMBER,
    p_saldo_cota            OUT NUMBER,
    p_validade_cota         OUT DATE,
    p_pode_continuar        OUT NUMBER,
    p_mensagem              OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_estado_fb Familia_Beneficiaria.estado_fb%TYPE;
BEGIN
    p_saldo_cota := NULL;
    p_validade_cota := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_volume_a_retirar IS NULL OR p_volume_a_retirar <= 0 THEN
        p_mensagem := 'Informe um volume a retirar maior que zero.';
        RETURN;
    END IF;

    BEGIN
        SELECT estado_fb
          INTO v_estado_fb
          FROM Familia_Beneficiaria
         WHERE codigo_fb = p_codigo_fb;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Familia beneficiaria nao encontrada.';
            RETURN;
    END;

    IF UPPER(TRIM(NVL(v_estado_fb, ''))) <> 'ACTIVO' THEN
        p_mensagem := 'Familia beneficiaria inactiva. A retirada de agua nao pode continuar.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_fb
       AND codigo_pd = p_codigo_pd
       AND ROWNUM = 1;

    IF v_count = 0 THEN
        p_mensagem := 'A familia nao esta associada ao ponto de distribuicao seleccionado.';
        RETURN;
    END IF;

    BEGIN
        SELECT saldo_disponivel_ca,
               periodo_validade_ca
          INTO p_saldo_cota,
               p_validade_cota
          FROM Cota_Agua
         WHERE codigo_cota = p_codigo_cota
           AND codigo_fb = p_codigo_fb;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Cota de agua nao encontrada para a familia seleccionada.';
            RETURN;
    END;

    IF p_saldo_cota IS NULL THEN
        p_mensagem := 'Saldo disponivel da cota esta indefinido.';
        RETURN;
    END IF;

    IF p_validade_cota IS NULL THEN
        p_mensagem := 'Validade da cota de agua esta indefinida.';
        RETURN;
    END IF;

    IF p_validade_cota < TRUNC(SYSDATE) THEN
        p_mensagem := 'A cota de agua esta expirada.';
        RETURN;
    END IF;

    IF p_volume_a_retirar > p_saldo_cota THEN
        p_mensagem := 'O volume a retirar ultrapassa o saldo disponivel da cota.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Cota validada para retirada.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_DEBITAR_COTA_RETIRADA (
    p_codigo_pd             IN NUMBER,
    p_codigo_fb             IN NUMBER,
    p_codigo_cota           IN NUMBER,
    p_volume_a_retirar      IN NUMBER,
    p_novo_saldo_cota       OUT NUMBER,
    p_pode_continuar        OUT NUMBER,
    p_mensagem              OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_saldo_cota Cota_Agua.saldo_disponivel_ca%TYPE;
    v_validade_cota Cota_Agua.periodo_validade_ca%TYPE;
    v_estado_fb Familia_Beneficiaria.estado_fb%TYPE;
BEGIN
    p_novo_saldo_cota := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_volume_a_retirar IS NULL OR p_volume_a_retirar <= 0 THEN
        p_mensagem := 'Informe um volume a retirar maior que zero.';
        RETURN;
    END IF;

    BEGIN
        SELECT estado_fb
          INTO v_estado_fb
          FROM Familia_Beneficiaria
         WHERE codigo_fb = p_codigo_fb;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Familia beneficiaria nao encontrada.';
            RETURN;
    END;

    IF UPPER(TRIM(NVL(v_estado_fb, ''))) <> 'ACTIVO' THEN
        p_mensagem := 'Familia beneficiaria inactiva. A retirada de agua nao pode continuar.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_fb
       AND codigo_pd = p_codigo_pd
       AND ROWNUM = 1;

    IF v_count = 0 THEN
        p_mensagem := 'A familia nao esta associada ao ponto de distribuicao seleccionado.';
        RETURN;
    END IF;

    BEGIN
        SELECT saldo_disponivel_ca,
               periodo_validade_ca
          INTO v_saldo_cota,
               v_validade_cota
          FROM Cota_Agua
         WHERE codigo_cota = p_codigo_cota
           AND codigo_fb = p_codigo_fb
         FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Cota de agua nao encontrada para a familia seleccionada.';
            RETURN;
    END;

    IF v_saldo_cota IS NULL THEN
        p_mensagem := 'Saldo disponivel da cota esta indefinido.';
        RETURN;
    END IF;

    IF v_validade_cota IS NULL THEN
        p_mensagem := 'Validade da cota de agua esta indefinida.';
        RETURN;
    END IF;

    IF v_validade_cota < TRUNC(SYSDATE) THEN
        p_mensagem := 'A cota de agua esta expirada.';
        RETURN;
    END IF;

    IF p_volume_a_retirar > v_saldo_cota THEN
        p_mensagem := 'O volume a retirar ultrapassa o saldo disponivel da cota.';
        RETURN;
    END IF;

    p_novo_saldo_cota := v_saldo_cota - p_volume_a_retirar;

    UPDATE Cota_Agua
       SET saldo_disponivel_ca = p_novo_saldo_cota
     WHERE codigo_cota = p_codigo_cota
       AND codigo_fb = p_codigo_fb;

    p_pode_continuar := 1;
    p_mensagem := 'Cota debitada com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        p_novo_saldo_cota := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/
