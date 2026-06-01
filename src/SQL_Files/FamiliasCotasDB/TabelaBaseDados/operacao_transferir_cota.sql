SET ECHO OFF;

-- Procedures expostas a outros nos para esta operacao.

BEGIN
    EXECUTE IMMEDIATE 'DROP PROCEDURE PRC_TRANSFERIR_COTA';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE <> -4043 THEN
            NULL;
        END IF;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_VALIDAR_TRANSF_COTA (
    p_cod_fam_doadora        IN NUMBER,
    p_cod_cota_doadora       IN NUMBER,
    p_cod_fam_receptora      IN NUMBER,
    p_cod_cota_receptora     IN NUMBER,
    p_volume_transferido     IN NUMBER,
    p_novo_saldo_doadora     OUT NUMBER,
    p_novo_saldo_receptora   OUT NUMBER,
    p_validade_transferencia OUT DATE,
    p_pode_continuar         OUT NUMBER,
    p_mensagem               OUT VARCHAR2
)
IS
    v_saldo_doadora        Cota_Agua.saldo_disponivel_ca%TYPE;
    v_saldo_receptora      Cota_Agua.saldo_disponivel_ca%TYPE;
    v_volume_receptora     Cota_Agua.volume_semanal_ca%TYPE;
    v_aut_doadora          Cota_Agua.transferencia_autorizada_ca%TYPE;
    v_aut_receptora        Cota_Agua.transferencia_autorizada_ca%TYPE;
    v_validade_doadora     Cota_Agua.periodo_validade_ca%TYPE;
    v_validade_receptora   Cota_Agua.periodo_validade_ca%TYPE;
    v_num_membros_doadora  Familia_Beneficiaria.num_membros_fb%TYPE;
    v_estado_doadora       Familia_Beneficiaria.estado_fb%TYPE;
    v_estado_receptora     Familia_Beneficiaria.estado_fb%TYPE;
    v_min_vital_doadora    NUMBER;
BEGIN
    p_novo_saldo_doadora := NULL;
    p_novo_saldo_receptora := NULL;
    p_validade_transferencia := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_volume_transferido IS NULL OR p_volume_transferido <= 0 THEN
        p_mensagem := 'Informe um volume transferido maior que zero.';
        RETURN;
    END IF;

    IF p_cod_fam_doadora = p_cod_fam_receptora
       AND p_cod_cota_doadora = p_cod_cota_receptora THEN
        p_mensagem := 'Seleccione cotas diferentes para origem e destino.';
        RETURN;
    END IF;

    BEGIN
        SELECT saldo_disponivel_ca,
               transferencia_autorizada_ca,
               periodo_validade_ca
          INTO v_saldo_doadora,
               v_aut_doadora,
               v_validade_doadora
          FROM Cota_Agua
         WHERE codigo_fb = p_cod_fam_doadora
           AND codigo_cota = p_cod_cota_doadora;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Cota da familia doadora nao encontrada.';
            RETURN;
    END;

    BEGIN
        SELECT saldo_disponivel_ca,
               volume_semanal_ca,
               transferencia_autorizada_ca,
               periodo_validade_ca
          INTO v_saldo_receptora,
               v_volume_receptora,
               v_aut_receptora,
               v_validade_receptora
          FROM Cota_Agua
         WHERE codigo_fb = p_cod_fam_receptora
           AND codigo_cota = p_cod_cota_receptora;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Cota da familia receptora nao encontrada.';
            RETURN;
    END;

    BEGIN
        SELECT num_membros_fb,
               estado_fb
          INTO v_num_membros_doadora,
               v_estado_doadora
          FROM Familia_Beneficiaria
         WHERE codigo_fb = p_cod_fam_doadora;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Familia doadora nao encontrada.';
            RETURN;
    END;

    BEGIN
        SELECT estado_fb
          INTO v_estado_receptora
          FROM Familia_Beneficiaria
         WHERE codigo_fb = p_cod_fam_receptora;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Familia receptora nao encontrada.';
            RETURN;
    END;

    IF v_estado_doadora <> 'Activo' THEN
        p_mensagem := 'Familia doadora inactiva. A transferencia nao pode continuar.';
        RETURN;
    END IF;

    IF v_estado_receptora <> 'Activo' THEN
        p_mensagem := 'Familia receptora inactiva. A transferencia nao pode continuar.';
        RETURN;
    END IF;

    IF UPPER(NVL(v_aut_doadora, 'NAO')) <> 'SIM' THEN
        p_mensagem := 'A cota doadora nao esta autorizada para transferencia.';
        RETURN;
    END IF;

    IF UPPER(NVL(v_aut_receptora, 'NAO')) <> 'SIM' THEN
        p_mensagem := 'A cota receptora nao esta autorizada para transferencia.';
        RETURN;
    END IF;

    IF v_validade_doadora IS NULL OR v_validade_receptora IS NULL THEN
        p_mensagem := 'As cotas seleccionadas devem possuir periodo de validade definido.';
        RETURN;
    END IF;

    IF v_validade_doadora < TRUNC(SYSDATE) THEN
        p_mensagem := 'A cota doadora esta expirada.';
        RETURN;
    END IF;

    IF v_validade_receptora < TRUNC(SYSDATE) THEN
        p_mensagem := 'A cota receptora esta expirada.';
        RETURN;
    END IF;

    IF p_volume_transferido > v_saldo_doadora THEN
        p_mensagem := 'Saldo insuficiente na cota doadora para esta transferencia.';
        RETURN;
    END IF;

    v_min_vital_doadora := fn_calcular_min_vital_fam(v_num_membros_doadora);
    p_novo_saldo_doadora := v_saldo_doadora - p_volume_transferido;
    p_novo_saldo_receptora := v_saldo_receptora + p_volume_transferido;

    IF p_novo_saldo_doadora < v_min_vital_doadora THEN
        p_mensagem := 'Transferencia nao permitida: a familia doadora ficaria abaixo do minimo vital.';
        RETURN;
    END IF;

    IF p_novo_saldo_receptora > v_volume_receptora THEN
        p_mensagem := 'Transferencia nao permitida: a cota receptora ultrapassaria o volume semanal autorizado.';
        RETURN;
    END IF;

    p_validade_transferencia := LEAST(v_validade_doadora, v_validade_receptora);
    p_pode_continuar := 1;
    p_mensagem := 'Cotas validadas para transferencia.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_APLICAR_TRANSF_COTA (
    p_cod_fam_doadora        IN NUMBER,
    p_cod_cota_doadora       IN NUMBER,
    p_cod_fam_receptora      IN NUMBER,
    p_cod_cota_receptora     IN NUMBER,
    p_volume_transferido     IN NUMBER,
    p_novo_saldo_doadora     OUT NUMBER,
    p_novo_saldo_receptora   OUT NUMBER,
    p_validade_transferencia OUT DATE,
    p_pode_continuar         OUT NUMBER,
    p_mensagem               OUT VARCHAR2
)
IS
    v_saldo_doadora        Cota_Agua.saldo_disponivel_ca%TYPE;
    v_saldo_receptora      Cota_Agua.saldo_disponivel_ca%TYPE;
    v_volume_receptora     Cota_Agua.volume_semanal_ca%TYPE;
    v_aut_doadora          Cota_Agua.transferencia_autorizada_ca%TYPE;
    v_aut_receptora        Cota_Agua.transferencia_autorizada_ca%TYPE;
    v_validade_doadora     Cota_Agua.periodo_validade_ca%TYPE;
    v_validade_receptora   Cota_Agua.periodo_validade_ca%TYPE;
    v_num_membros_doadora  Familia_Beneficiaria.num_membros_fb%TYPE;
    v_estado_doadora       Familia_Beneficiaria.estado_fb%TYPE;
    v_estado_receptora     Familia_Beneficiaria.estado_fb%TYPE;
    v_min_vital_doadora    NUMBER;
BEGIN
    p_novo_saldo_doadora := NULL;
    p_novo_saldo_receptora := NULL;
    p_validade_transferencia := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_volume_transferido IS NULL OR p_volume_transferido <= 0 THEN
        p_mensagem := 'Informe um volume transferido maior que zero.';
        RETURN;
    END IF;

    IF p_cod_fam_doadora = p_cod_fam_receptora
       AND p_cod_cota_doadora = p_cod_cota_receptora THEN
        p_mensagem := 'Seleccione cotas diferentes para origem e destino.';
        RETURN;
    END IF;

    BEGIN
        SELECT saldo_disponivel_ca,
               transferencia_autorizada_ca,
               periodo_validade_ca
          INTO v_saldo_doadora,
               v_aut_doadora,
               v_validade_doadora
          FROM Cota_Agua
         WHERE codigo_fb = p_cod_fam_doadora
           AND codigo_cota = p_cod_cota_doadora
         FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Cota da familia doadora nao encontrada.';
            RETURN;
    END;

    BEGIN
        SELECT saldo_disponivel_ca,
               volume_semanal_ca,
               transferencia_autorizada_ca,
               periodo_validade_ca
          INTO v_saldo_receptora,
               v_volume_receptora,
               v_aut_receptora,
               v_validade_receptora
          FROM Cota_Agua
         WHERE codigo_fb = p_cod_fam_receptora
           AND codigo_cota = p_cod_cota_receptora
         FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Cota da familia receptora nao encontrada.';
            RETURN;
    END;

    BEGIN
        SELECT num_membros_fb,
               estado_fb
          INTO v_num_membros_doadora,
               v_estado_doadora
          FROM Familia_Beneficiaria
         WHERE codigo_fb = p_cod_fam_doadora;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Familia doadora nao encontrada.';
            RETURN;
    END;

    BEGIN
        SELECT estado_fb
          INTO v_estado_receptora
          FROM Familia_Beneficiaria
         WHERE codigo_fb = p_cod_fam_receptora;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Familia receptora nao encontrada.';
            RETURN;
    END;

    IF v_estado_doadora <> 'Activo' THEN
        p_mensagem := 'Familia doadora inactiva. A transferencia nao pode continuar.';
        RETURN;
    END IF;

    IF v_estado_receptora <> 'Activo' THEN
        p_mensagem := 'Familia receptora inactiva. A transferencia nao pode continuar.';
        RETURN;
    END IF;

    IF UPPER(NVL(v_aut_doadora, 'NAO')) <> 'SIM' THEN
        p_mensagem := 'A cota doadora nao esta autorizada para transferencia.';
        RETURN;
    END IF;

    IF UPPER(NVL(v_aut_receptora, 'NAO')) <> 'SIM' THEN
        p_mensagem := 'A cota receptora nao esta autorizada para transferencia.';
        RETURN;
    END IF;

    IF v_validade_doadora IS NULL OR v_validade_receptora IS NULL THEN
        p_mensagem := 'As cotas seleccionadas devem possuir periodo de validade definido.';
        RETURN;
    END IF;

    IF v_validade_doadora < TRUNC(SYSDATE) THEN
        p_mensagem := 'A cota doadora esta expirada.';
        RETURN;
    END IF;

    IF v_validade_receptora < TRUNC(SYSDATE) THEN
        p_mensagem := 'A cota receptora esta expirada.';
        RETURN;
    END IF;

    IF p_volume_transferido > v_saldo_doadora THEN
        p_mensagem := 'Saldo insuficiente na cota doadora para esta transferencia.';
        RETURN;
    END IF;

    v_min_vital_doadora := fn_calcular_min_vital_fam(v_num_membros_doadora);
    p_novo_saldo_doadora := v_saldo_doadora - p_volume_transferido;
    p_novo_saldo_receptora := v_saldo_receptora + p_volume_transferido;

    IF p_novo_saldo_doadora < v_min_vital_doadora THEN
        p_mensagem := 'Transferencia nao permitida: a familia doadora ficaria abaixo do minimo vital.';
        RETURN;
    END IF;

    IF p_novo_saldo_receptora > v_volume_receptora THEN
        p_mensagem := 'Transferencia nao permitida: a cota receptora ultrapassaria o volume semanal autorizado.';
        RETURN;
    END IF;

    p_validade_transferencia := LEAST(v_validade_doadora, v_validade_receptora);

    UPDATE Cota_Agua
       SET saldo_disponivel_ca = p_novo_saldo_doadora
     WHERE codigo_fb = p_cod_fam_doadora
       AND codigo_cota = p_cod_cota_doadora;

    UPDATE Cota_Agua
       SET saldo_disponivel_ca = p_novo_saldo_receptora
     WHERE codigo_fb = p_cod_fam_receptora
       AND codigo_cota = p_cod_cota_receptora;

    p_pode_continuar := 1;
    p_mensagem := 'Saldos das cotas actualizados com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        p_novo_saldo_doadora := NULL;
        p_novo_saldo_receptora := NULL;
        p_validade_transferencia := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_DOAR_COTA_FAM (
    p_cod_fam_doadora    IN  NUMBER,
    p_cod_cota_doadora   IN  NUMBER,
    p_cod_fam_receptora  IN  NUMBER,
    p_cod_cota_receptora IN  NUMBER,
    p_volume_transferido IN  NUMBER,
    p_motivo             IN  VARCHAR2,
    p_codigo_tc          OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
BEGIN
    p_codigo_tc := NULL;
    p_mensagem := NULL;

    PRC_TRANSFERIR_COTA_TRANS(
        p_cod_fam_doadora,
        p_cod_cota_doadora,
        p_cod_fam_receptora,
        p_cod_cota_receptora,
        p_volume_transferido,
        p_motivo,
        p_codigo_tc,
        p_mensagem
    );
EXCEPTION
    WHEN OTHERS THEN
        p_codigo_tc := NULL;
        p_mensagem := 'Transferencia nao executada. Detalhe: ' || SQLERRM;
END;
/
