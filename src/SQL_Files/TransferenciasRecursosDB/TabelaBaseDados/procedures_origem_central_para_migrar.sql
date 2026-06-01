SET ECHO OFF;

-- Referencia historica. Nao entra no fluxo principal de instalacao.

CREATE OR REPLACE PROCEDURE prc_transferir_cota (
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
    v_saldo_doadora        Cota_Agua.saldo_disponivel_ca%TYPE;
    v_saldo_receptora      Cota_Agua.saldo_disponivel_ca%TYPE;
    v_volume_doadora       Cota_Agua.volume_semanal_ca%TYPE;
    v_volume_receptora     Cota_Agua.volume_semanal_ca%TYPE;
    v_aut_doadora          Cota_Agua.transferencia_autorizada_ca%TYPE;
    v_aut_receptora        Cota_Agua.transferencia_autorizada_ca%TYPE;
    v_validade_doadora     Cota_Agua.periodo_validade_ca%TYPE;
    v_validade_receptora   Cota_Agua.periodo_validade_ca%TYPE;
    v_num_membros_doadora  Familia_Beneficiaria.num_membros_fb%TYPE;
    v_min_vital_doadora    NUMBER;
    v_novo_saldo_doadora   NUMBER;
    v_novo_saldo_receptora NUMBER;
    v_validade_transferencia DATE;
BEGIN
    p_codigo_tc := NULL;
    p_mensagem  := NULL;

    IF p_volume_transferido IS NULL OR p_volume_transferido <= 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'O volume transferido deve ser maior que zero.');
    END IF;

    IF p_cod_fam_doadora = p_cod_fam_receptora
       AND p_cod_cota_doadora = p_cod_cota_receptora THEN
        RAISE_APPLICATION_ERROR(-20002, 'A cota doadora e a cota receptora nao podem ser a mesma.');
    END IF;

    BEGIN
        SELECT saldo_disponivel_ca,
               volume_semanal_ca,
               transferencia_autorizada_ca,
               periodo_validade_ca
          INTO v_saldo_doadora,
               v_volume_doadora,
               v_aut_doadora,
               v_validade_doadora
          FROM Cota_Agua
         WHERE codigo_fb   = p_cod_fam_doadora
           AND codigo_cota = p_cod_cota_doadora
         FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20003, 'Cota da familia doadora nao encontrada.');
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
         WHERE codigo_fb   = p_cod_fam_receptora
           AND codigo_cota = p_cod_cota_receptora
         FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20004, 'Cota da familia receptora nao encontrada.');
    END;

    BEGIN
        SELECT num_membros_fb
          INTO v_num_membros_doadora
          FROM Familia_Beneficiaria
         WHERE codigo_fb = p_cod_fam_doadora;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20005, 'Familia doadora nao encontrada.');
    END;

    IF UPPER(NVL(v_aut_doadora, 'NAO')) <> 'SIM' THEN
        RAISE_APPLICATION_ERROR(-20006, 'A cota doadora nao esta autorizada para transferencia.');
    END IF;

    IF UPPER(NVL(v_aut_receptora, 'NAO')) <> 'SIM' THEN
        RAISE_APPLICATION_ERROR(-20007, 'A cota receptora nao esta autorizada para transferencia.');
    END IF;

    IF v_validade_doadora < TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(-20008, 'A cota doadora esta expirada.');
    END IF;

    IF v_validade_receptora < TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(-20009, 'A cota receptora esta expirada.');
    END IF;

    IF v_validade_doadora IS NULL OR v_validade_receptora IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'As cotas seleccionadas devem possuir periodo de validade.');
    END IF;

    v_min_vital_doadora    := fn_calcular_min_vital_fam(v_num_membros_doadora);
    v_novo_saldo_doadora   := v_saldo_doadora - p_volume_transferido;
    v_novo_saldo_receptora := v_saldo_receptora + p_volume_transferido;

    IF p_volume_transferido > v_saldo_doadora THEN
        RAISE_APPLICATION_ERROR(-20011, 'Saldo insuficiente na cota doadora.');
    END IF;

    IF v_novo_saldo_doadora < v_min_vital_doadora THEN
        RAISE_APPLICATION_ERROR(
            -20012,
            'Transferencia invalida: a familia doadora ficaria abaixo do minimo vital.'
        );
    END IF;

    IF v_novo_saldo_receptora > v_volume_receptora THEN
        RAISE_APPLICATION_ERROR(
            -20013,
            'Transferencia invalida: a cota receptora ultrapassaria o volume semanal.'
        );
    END IF;

    v_validade_transferencia := LEAST(v_validade_doadora, v_validade_receptora);

    INSERT INTO Transferencia_Cota (
        cod_fam_doadora_tc,
        cod_fam_receptora_tc,
        volume_cedido_tc,
        motivo_solicitacao_tc,
        data_aprovacao_tc,
        validade_transferencia_tc
    ) VALUES (
        p_cod_fam_doadora,
        p_cod_fam_receptora,
        p_volume_transferido,
        p_motivo,
        SYSDATE,
        v_validade_transferencia
    )
    RETURNING codigo_tc INTO p_codigo_tc;

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (codigo_tc, codigo_fb, tipo_participacao)
    VALUES (p_codigo_tc, p_cod_fam_doadora, 'Doa');

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (codigo_tc, codigo_fb, tipo_participacao)
    VALUES (p_codigo_tc, p_cod_fam_receptora, 'Recebe');

    INSERT INTO TRANSFER_ASSOC_COTA (codigo_tc, codigo_cota, codigo_fb)
    VALUES (p_codigo_tc, p_cod_cota_doadora, p_cod_fam_doadora);

    INSERT INTO TRANSFER_ASSOC_COTA (codigo_tc, codigo_cota, codigo_fb)
    VALUES (p_codigo_tc, p_cod_cota_receptora, p_cod_fam_receptora);

    UPDATE Cota_Agua
       SET saldo_disponivel_ca = v_novo_saldo_doadora
     WHERE codigo_fb   = p_cod_fam_doadora
       AND codigo_cota = p_cod_cota_doadora;

    UPDATE Cota_Agua
       SET saldo_disponivel_ca = v_novo_saldo_receptora
     WHERE codigo_fb   = p_cod_fam_receptora
       AND codigo_cota = p_cod_cota_receptora;

    p_mensagem := 'Transferencia realizada com sucesso. Codigo: ' || p_codigo_tc;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_tc := NULL;
        p_mensagem  := SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_ACT_MOTIVO_TRANSF (
    p_codigo_tc          IN NUMBER,
    p_motivo_original    IN VARCHAR2,
    p_novo_motivo        IN VARCHAR2,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_motivo_actual Transferencia_Cota.motivo_solicitacao_tc%TYPE;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_tc IS NULL THEN
        p_mensagem := 'Codigo da transferencia e obrigatorio.';
        RETURN;
    END IF;

    BEGIN
        SELECT motivo_solicitacao_tc
          INTO v_motivo_actual
          FROM Transferencia_Cota
         WHERE codigo_tc = p_codigo_tc;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Transferencia nao encontrada.';
            RETURN;
    END;

    IF NVL(TRIM(v_motivo_actual), '#NULL#') <> NVL(TRIM(p_motivo_original), '#NULL#') THEN
        p_mensagem := 'O motivo desta transferencia foi alterado por outro utilizador. Actualize a tabela e tente novamente.';
        RETURN;
    END IF;

    IF p_novo_motivo IS NULL OR TRIM(p_novo_motivo) IS NULL THEN
        p_mensagem := 'O novo motivo e obrigatorio.';
        RETURN;
    END IF;

    IF LENGTH(TRIM(p_novo_motivo)) > 200 THEN
        p_mensagem := 'O motivo nao pode exceder 200 caracteres.';
        RETURN;
    END IF;

    IF NVL(TRIM(p_motivo_original), '#NULL#') = NVL(TRIM(p_novo_motivo), '#NULL#') THEN
        p_mensagem := 'O novo motivo deve ser diferente do motivo actual.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Motivo valido para actualizacao.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ACT_MOTIVO_TRANSF (
    p_codigo_tc          IN NUMBER,
    p_motivo_original    IN VARCHAR2,
    p_novo_motivo        IN VARCHAR2,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_lock_codigo Transferencia_Cota.codigo_tc%TYPE;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_ACT_MOTIVO_TRANSF(
        p_codigo_tc,
        p_motivo_original,
        p_novo_motivo,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    SELECT codigo_tc
      INTO v_lock_codigo
      FROM Transferencia_Cota
     WHERE codigo_tc = p_codigo_tc
     FOR UPDATE;

    PRC_PRE_ACT_MOTIVO_TRANSF(
        p_codigo_tc,
        p_motivo_original,
        p_novo_motivo,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        ROLLBACK;
        RETURN;
    END IF;

    UPDATE Transferencia_Cota
       SET motivo_solicitacao_tc = TRIM(p_novo_motivo)
     WHERE codigo_tc = p_codigo_tc;

    COMMIT;
    p_pode_continuar := 1;
    p_mensagem := 'Motivo da transferencia actualizado com sucesso. Os saldos das cotas nao foram alterados.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

/* NOT TESTED*/

CREATE OR REPLACE PROCEDURE PRC_PRE_ACT_MED_REC_HIDRICO (
    p_codigo_rh          IN NUMBER,
    p_codigos_medidas    IN VARCHAR2,
    p_operacao           IN VARCHAR2,
    p_data_impl          IN DATE,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_operacao VARCHAR2(10);
    v_codigo_texto VARCHAR2(30);
    v_codigo_medida NUMBER;
    v_posicao NUMBER := 1;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Codigo do recurso hidrico e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Recurso_Hidrico
     WHERE codigo_rh = p_codigo_rh;

    IF v_count = 0 THEN
        p_mensagem := 'Recurso hidrico nao encontrado.';
        RETURN;
    END IF;

    v_operacao := UPPER(TRIM(p_operacao));
    IF v_operacao NOT IN ('ADICIONAR', 'REMOVER') THEN
        p_mensagem := 'Operacao invalida.';
        RETURN;
    END IF;

    IF v_operacao = 'ADICIONAR' AND p_data_impl IS NULL THEN
        p_mensagem := 'Data de implementacao e obrigatoria ao adicionar medidas.';
        RETURN;
    END IF;

    IF v_operacao = 'ADICIONAR' AND TRUNC(p_data_impl) > TRUNC(SYSDATE) THEN
        p_mensagem := 'Data de implementacao nao pode ser futura.';
        RETURN;
    END IF;

    IF p_codigos_medidas IS NULL OR TRIM(p_codigos_medidas) IS NULL THEN
        p_mensagem := 'Seleccione pelo menos uma medida de proteccao.';
        RETURN;
    END IF;

    IF NOT REGEXP_LIKE(TRIM(p_codigos_medidas), '^[0-9]+(,[0-9]+)*$') THEN
        p_mensagem := 'Lista de medidas de proteccao invalida.';
        RETURN;
    END IF;

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(TRIM(p_codigos_medidas), '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_medida := TO_NUMBER(v_codigo_texto);

        SELECT COUNT(*)
          INTO v_count
          FROM Medida_Proteccao
         WHERE cod_medida_proteccao = v_codigo_medida;

        IF v_count = 0 THEN
            p_mensagem := 'Medida de proteccao nao encontrada: ' || v_codigo_medida;
            RETURN;
        END IF;

        SELECT COUNT(*)
          INTO v_count
          FROM REC_APLICA_MED_PROT
         WHERE codigo_rh = p_codigo_rh
           AND cod_medida_proteccao = v_codigo_medida;

        IF v_operacao = 'ADICIONAR' AND v_count > 0 THEN
            p_mensagem := 'Medida ja aplicada ao recurso hidrico: ' || v_codigo_medida;
            RETURN;
        END IF;

        IF v_operacao = 'REMOVER' AND v_count = 0 THEN
            p_mensagem := 'Medida nao esta aplicada ao recurso hidrico: ' || v_codigo_medida;
            RETURN;
        END IF;

        v_posicao := v_posicao + 1;
    END LOOP;

    p_pode_continuar := 1;
    p_mensagem := 'Medidas validas para ' || LOWER(v_operacao) || '.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ACT_MED_REC_HIDRICO (
    p_codigo_rh          IN NUMBER,
    p_codigos_medidas    IN VARCHAR2,
    p_operacao           IN VARCHAR2,
    p_data_impl          IN DATE,
    p_afectadas          OUT NUMBER,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_codigo_texto VARCHAR2(30);
    v_codigo_medida NUMBER;
    v_posicao NUMBER := 1;
    v_operacao VARCHAR2(10);
BEGIN
    p_afectadas := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_ACT_MED_REC_HIDRICO(
        p_codigo_rh,
        p_codigos_medidas,
        p_operacao,
        p_data_impl,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    v_operacao := UPPER(TRIM(p_operacao));

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(TRIM(p_codigos_medidas), '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_medida := TO_NUMBER(v_codigo_texto);

        IF v_operacao = 'ADICIONAR' THEN
            INSERT INTO REC_APLICA_MED_PROT (
                cod_medida_proteccao,
                codigo_rh,
                data_impl
            ) VALUES (
                v_codigo_medida,
                p_codigo_rh,
                TRUNC(p_data_impl)
            );
        ELSE
            DELETE FROM REC_APLICA_MED_PROT
             WHERE cod_medida_proteccao = v_codigo_medida
               AND codigo_rh = p_codigo_rh;
        END IF;

        p_afectadas := p_afectadas + SQL%ROWCOUNT;
        v_posicao := v_posicao + 1;
    END LOOP;

    COMMIT;
    p_pode_continuar := 1;
    p_mensagem := 'Medidas de proteccao actualizadas com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_afectadas := 0;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_INS_MED_PROT_RESP (
    p_nome_responsavel  IN VARCHAR2,
    p_sucesso           OUT NUMBER,
    p_mensagem          OUT VARCHAR2
)
IS
BEGIN
    p_sucesso := 0;
    p_mensagem := NULL;

    IF p_nome_responsavel IS NULL OR TRIM(p_nome_responsavel) IS NULL THEN
        p_mensagem := 'Nome do responsavel e obrigatorio.';
        RETURN;
    END IF;

    INSERT INTO Medida_Prot_Responsavel (
        nome_responsavel
    ) VALUES (
        TRIM(p_nome_responsavel)
    );

    COMMIT;
    p_sucesso := 1;
    p_mensagem := 'Responsavel inserido com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_sucesso := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_REG_MED_PROT (
    p_cod_responsavel   IN NUMBER,
    p_descricao_medida  IN VARCHAR2,
    p_pode_continuar    OUT NUMBER,
    p_mensagem          OUT VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_cod_responsavel IS NULL THEN
        p_mensagem := 'Responsavel pela medida e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Medida_Prot_Responsavel
     WHERE cod_responsavel = p_cod_responsavel;

    IF v_count = 0 THEN
        p_mensagem := 'Responsavel pela medida nao encontrado.';
        RETURN;
    END IF;

    IF p_descricao_medida IS NULL OR TRIM(p_descricao_medida) IS NULL THEN
        p_mensagem := 'Descricao da medida de proteccao e obrigatoria.';
        RETURN;
    END IF;

    IF LENGTH(TRIM(p_descricao_medida)) > 100 THEN
        p_mensagem := 'Descricao da medida nao pode ter mais de 100 caracteres.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Medida_Proteccao
     WHERE cod_responsavel = p_cod_responsavel
       AND UPPER(TRIM(descricao_medida)) = UPPER(TRIM(p_descricao_medida));

    IF v_count > 0 THEN
        p_mensagem := 'Este responsavel ja coordena uma medida com esta descricao.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Medida de proteccao valida para registo.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REG_MED_PROT (
    p_cod_responsavel   IN NUMBER,
    p_descricao_medida  IN VARCHAR2,
    p_codigo_medida     OUT NUMBER,
    p_pode_continuar    OUT NUMBER,
    p_mensagem          OUT VARCHAR2
)
IS
BEGIN
    p_codigo_medida := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_REG_MED_PROT(
        p_cod_responsavel,
        p_descricao_medida,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    INSERT INTO Medida_Proteccao (
        cod_responsavel,
        descricao_medida
    ) VALUES (
        p_cod_responsavel,
        TRIM(p_descricao_medida)
    )
    RETURNING cod_medida_proteccao INTO p_codigo_medida;

    COMMIT;
    p_pode_continuar := 1;
    p_mensagem := 'Medida de proteccao registada com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_medida := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_REG_RECURSO_HIDRICO (
    p_tipo_rh              IN VARCHAR2,
    p_localizacao_rh       IN VARCHAR2,
    p_volume_rh            IN NUMBER,
    p_sazonalidade_rh      IN VARCHAR2,
    p_vulnerabilidade_rh   IN VARCHAR2,
    p_nivel_exploracao_rh  IN VARCHAR2,
    p_cod_medidas          IN VARCHAR2,
    p_eq_ph                IN NUMBER,
    p_valor_ph             IN NUMBER,
    p_eq_turbidez          IN NUMBER,
    p_valor_turbidez       IN NUMBER,
    p_eq_temperatura       IN NUMBER,
    p_valor_temperatura    IN NUMBER,
    p_eq_cloro             IN NUMBER,
    p_valor_cloro          IN NUMBER,
    p_eq_oxigenio          IN NUMBER,
    p_valor_oxigenio       IN NUMBER,
    p_pode_continuar       OUT NUMBER,
    p_mensagem             OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_cod_ph Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_turbidez Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_temperatura Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_cloro Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_oxigenio Parametro_Qualidade.codigo_parametro%TYPE;
    v_medidas VARCHAR2(4000);
    v_tokens NUMBER;
    v_existentes NUMBER;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_tipo_rh IS NULL OR UPPER(TRIM(p_tipo_rh)) NOT IN ('SUBTERRANEA', 'SUPERFICIAL', 'PLUVIAL') THEN
        p_mensagem := 'Tipo do recurso hidrico deve ser Subterranea, Superficial ou Pluvial.';
        RETURN;
    END IF;

    IF p_localizacao_rh IS NULL OR TRIM(p_localizacao_rh) IS NULL THEN
        p_mensagem := 'Localizacao do recurso hidrico e obrigatoria.';
        RETURN;
    END IF;

    IF p_volume_rh IS NULL OR p_volume_rh <= 0 THEN
        p_mensagem := 'Vazao ou volume estimado deve ser maior que zero.';
        RETURN;
    END IF;

    IF p_sazonalidade_rh IS NULL OR TRIM(p_sazonalidade_rh) IS NULL THEN
        p_mensagem := 'Sazonalidade do recurso hidrico e obrigatoria.';
        RETURN;
    END IF;

    IF p_vulnerabilidade_rh IS NULL OR TRIM(p_vulnerabilidade_rh) IS NULL THEN
        p_mensagem := 'Vulnerabilidade do recurso hidrico e obrigatoria.';
        RETURN;
    END IF;

    IF p_nivel_exploracao_rh IS NULL
       OR UPPER(TRIM(p_nivel_exploracao_rh)) NOT IN ('BAIXO', 'MEDIO', 'ALTO', 'CRITICO') THEN
        p_mensagem := 'Nivel de exploracao deve ser Baixo, Medio, Alto ou Critico.';
        RETURN;
    END IF;

    SELECT codigo_parametro INTO v_cod_ph
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'PH';

    SELECT codigo_parametro INTO v_cod_turbidez
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'TURBIDEZ';

    SELECT codigo_parametro INTO v_cod_temperatura
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'TEMPERATURA';

    SELECT codigo_parametro INTO v_cod_cloro
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'CLORO RESIDUAL';

    SELECT codigo_parametro INTO v_cod_oxigenio
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) LIKE 'OXIG%NIO DISSOLVIDO';

    SELECT COUNT(*) INTO v_count
      FROM (
        SELECT p_eq_ph equipe_id FROM dual
        UNION
        SELECT p_eq_turbidez FROM dual
        UNION
        SELECT p_eq_temperatura FROM dual
        UNION
        SELECT p_eq_cloro FROM dual
        UNION
        SELECT p_eq_oxigenio FROM dual
      );

    IF v_count <> 5 THEN
        p_mensagem := 'Devem ser seleccionados exactamente 5 analistas diferentes.';
        RETURN;
    END IF;

    IF p_valor_ph IS NULL OR p_valor_turbidez IS NULL OR p_valor_temperatura IS NULL
       OR p_valor_cloro IS NULL OR p_valor_oxigenio IS NULL THEN
        p_mensagem := 'Todas as 5 medicoes de qualidade devem ter valor.';
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count
      FROM Analista_Qualidade
     WHERE (equipe_id = p_eq_ph AND codigo_parametro = v_cod_ph)
        OR (equipe_id = p_eq_turbidez AND codigo_parametro = v_cod_turbidez)
        OR (equipe_id = p_eq_temperatura AND codigo_parametro = v_cod_temperatura)
        OR (equipe_id = p_eq_cloro AND codigo_parametro = v_cod_cloro)
        OR (equipe_id = p_eq_oxigenio AND codigo_parametro = v_cod_oxigenio);

    IF v_count <> 5 THEN
        p_mensagem := 'Cada analista deve monitorar o parametro de qualidade correspondente.';
        RETURN;
    END IF;

    v_medidas := TRIM(p_cod_medidas);
    IF v_medidas IS NOT NULL THEN
        IF NOT REGEXP_LIKE(v_medidas, '^[0-9]+(,[0-9]+)*$') THEN
            p_mensagem := 'Lista de medidas de proteccao invalida.';
            RETURN;
        END IF;

        v_tokens := LENGTH(v_medidas) - LENGTH(REPLACE(v_medidas, ',', '')) + 1;

        SELECT COUNT(DISTINCT cod_medida_proteccao)
          INTO v_existentes
          FROM Medida_Proteccao
         WHERE INSTR(',' || v_medidas || ',', ',' || cod_medida_proteccao || ',') > 0;

        IF v_existentes <> v_tokens THEN
            p_mensagem := 'Uma ou mais medidas de proteccao seleccionadas nao existem.';
            RETURN;
        END IF;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para registar recurso hidrico.';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_pode_continuar := 0;
        p_mensagem := 'Os 5 parametros de qualidade obrigatorios devem existir.';
    WHEN TOO_MANY_ROWS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Parametros de qualidade duplicados encontrados.';
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REGISTAR_RECURSO_HIDRICO (
    p_tipo_rh              IN VARCHAR2,
    p_localizacao_rh       IN VARCHAR2,
    p_volume_rh            IN NUMBER,
    p_sazonalidade_rh      IN VARCHAR2,
    p_vulnerabilidade_rh   IN VARCHAR2,
    p_nivel_exploracao_rh  IN VARCHAR2,
    p_cod_medidas          IN VARCHAR2,
    p_eq_ph                IN NUMBER,
    p_valor_ph             IN NUMBER,
    p_eq_turbidez          IN NUMBER,
    p_valor_turbidez       IN NUMBER,
    p_eq_temperatura       IN NUMBER,
    p_valor_temperatura    IN NUMBER,
    p_eq_cloro             IN NUMBER,
    p_valor_cloro          IN NUMBER,
    p_eq_oxigenio          IN NUMBER,
    p_valor_oxigenio       IN NUMBER,
    p_codigo_rh            OUT NUMBER,
    p_pode_continuar       OUT NUMBER,
    p_mensagem             OUT VARCHAR2
)
IS
    v_cod_ph Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_turbidez Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_temperatura Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_cloro Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_oxigenio Parametro_Qualidade.codigo_parametro%TYPE;
    v_medidas VARCHAR2(4000);
BEGIN
    p_codigo_rh := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_REG_RECURSO_HIDRICO(
        p_tipo_rh,
        p_localizacao_rh,
        p_volume_rh,
        p_sazonalidade_rh,
        p_vulnerabilidade_rh,
        p_nivel_exploracao_rh,
        p_cod_medidas,
        p_eq_ph,
        p_valor_ph,
        p_eq_turbidez,
        p_valor_turbidez,
        p_eq_temperatura,
        p_valor_temperatura,
        p_eq_cloro,
        p_valor_cloro,
        p_eq_oxigenio,
        p_valor_oxigenio,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    SELECT codigo_parametro INTO v_cod_ph
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'PH';

    SELECT codigo_parametro INTO v_cod_turbidez
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'TURBIDEZ';

    SELECT codigo_parametro INTO v_cod_temperatura
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'TEMPERATURA';

    SELECT codigo_parametro INTO v_cod_cloro
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'CLORO RESIDUAL';

    SELECT codigo_parametro INTO v_cod_oxigenio
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) LIKE 'OXIG%NIO DISSOLVIDO';

    INSERT INTO Recurso_Hidrico (
        tipo_rh,
        localizacao_rh,
        volume_rh,
        sazonalidade_rh,
        vulnerabilidade_rh,
        nivel_exploracao_rh
    ) VALUES (
        TRIM(p_tipo_rh),
        TRIM(p_localizacao_rh),
        p_volume_rh,
        TRIM(p_sazonalidade_rh),
        TRIM(p_vulnerabilidade_rh),
        TRIM(p_nivel_exploracao_rh)
    )
    RETURNING codigo_rh INTO p_codigo_rh;

    v_medidas := TRIM(p_cod_medidas);
    IF v_medidas IS NOT NULL THEN
        INSERT INTO REC_APLICA_MED_PROT (
            cod_medida_proteccao,
            codigo_rh,
            data_impl
        )
        SELECT cod_medida_proteccao,
               p_codigo_rh,
               SYSDATE
          FROM Medida_Proteccao
         WHERE INSTR(',' || v_medidas || ',', ',' || cod_medida_proteccao || ',') > 0;
    END IF;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_ph, p_eq_ph, p_valor_ph, SYSDATE);

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_turbidez, p_eq_turbidez, p_valor_turbidez, SYSDATE);

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_temperatura, p_eq_temperatura, p_valor_temperatura, SYSDATE);

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_cloro, p_eq_cloro, p_valor_cloro, SYSDATE);

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_oxigenio, p_eq_oxigenio, p_valor_oxigenio, SYSDATE);

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Recurso hidrico registado com sucesso com 5 medicoes iniciais.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_rh := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_REG_MED_QUAL_AGUA (
    p_codigo_rh            IN NUMBER,
    p_eq_ph                IN NUMBER,
    p_valor_ph             IN NUMBER,
    p_eq_turbidez          IN NUMBER,
    p_valor_turbidez       IN NUMBER,
    p_eq_temperatura       IN NUMBER,
    p_valor_temperatura    IN NUMBER,
    p_eq_cloro             IN NUMBER,
    p_valor_cloro          IN NUMBER,
    p_eq_oxigenio          IN NUMBER,
    p_valor_oxigenio       IN NUMBER,
    p_total_medicoes       OUT NUMBER,
    p_pode_continuar       OUT NUMBER,
    p_mensagem             OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_cod_ph Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_turbidez Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_temperatura Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_cloro Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_oxigenio Parametro_Qualidade.codigo_parametro%TYPE;
BEGIN
    p_total_medicoes := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Recurso hidrico e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Recurso_Hidrico
     WHERE codigo_rh = p_codigo_rh;

    IF v_count = 0 THEN
        p_mensagem := 'Recurso hidrico nao encontrado.';
        RETURN;
    END IF;

    SELECT codigo_parametro INTO v_cod_ph
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'PH';

    SELECT codigo_parametro INTO v_cod_turbidez
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'TURBIDEZ';

    SELECT codigo_parametro INTO v_cod_temperatura
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'TEMPERATURA';

    SELECT codigo_parametro INTO v_cod_cloro
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'CLORO RESIDUAL';

    SELECT codigo_parametro INTO v_cod_oxigenio
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) LIKE 'OXIG%NIO DISSOLVIDO';

    SELECT COUNT(*) INTO v_count
      FROM (
        SELECT p_eq_ph equipe_id FROM dual
        UNION
        SELECT p_eq_turbidez FROM dual
        UNION
        SELECT p_eq_temperatura FROM dual
        UNION
        SELECT p_eq_cloro FROM dual
        UNION
        SELECT p_eq_oxigenio FROM dual
      );

    IF v_count <> 5 THEN
        p_mensagem := 'Devem ser seleccionados exactamente 5 analistas diferentes.';
        RETURN;
    END IF;

    IF p_valor_ph IS NULL OR p_valor_turbidez IS NULL OR p_valor_temperatura IS NULL
       OR p_valor_cloro IS NULL OR p_valor_oxigenio IS NULL THEN
        p_mensagem := 'Todas as 5 medicoes de qualidade devem ter valor.';
        RETURN;
    END IF;

    IF p_valor_ph < 0 OR p_valor_turbidez < 0 OR p_valor_temperatura < 0
       OR p_valor_cloro < 0 OR p_valor_oxigenio < 0 THEN
        p_mensagem := 'Valores de medicao nao podem ser negativos.';
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count
      FROM Analista_Qualidade
     WHERE (equipe_id = p_eq_ph AND codigo_parametro = v_cod_ph)
        OR (equipe_id = p_eq_turbidez AND codigo_parametro = v_cod_turbidez)
        OR (equipe_id = p_eq_temperatura AND codigo_parametro = v_cod_temperatura)
        OR (equipe_id = p_eq_cloro AND codigo_parametro = v_cod_cloro)
        OR (equipe_id = p_eq_oxigenio AND codigo_parametro = v_cod_oxigenio);

    IF v_count <> 5 THEN
        p_mensagem := 'Cada analista deve monitorar o parametro de qualidade correspondente.';
        RETURN;
    END IF;

    p_total_medicoes := 5;
    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para registar as 5 medicoes de qualidade da agua.';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_total_medicoes := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Os 5 parametros de qualidade obrigatorios devem existir.';
    WHEN TOO_MANY_ROWS THEN
        p_total_medicoes := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Parametros de qualidade duplicados encontrados.';
    WHEN OTHERS THEN
        p_total_medicoes := 0;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REG_MED_QUAL_AGUA (
    p_codigo_rh            IN NUMBER,
    p_eq_ph                IN NUMBER,
    p_valor_ph             IN NUMBER,
    p_eq_turbidez          IN NUMBER,
    p_valor_turbidez       IN NUMBER,
    p_eq_temperatura       IN NUMBER,
    p_valor_temperatura    IN NUMBER,
    p_eq_cloro             IN NUMBER,
    p_valor_cloro          IN NUMBER,
    p_eq_oxigenio          IN NUMBER,
    p_valor_oxigenio       IN NUMBER,
    p_total_registadas     OUT NUMBER,
    p_pode_continuar       OUT NUMBER,
    p_mensagem             OUT VARCHAR2
)
IS
    v_cod_ph Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_turbidez Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_temperatura Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_cloro Parametro_Qualidade.codigo_parametro%TYPE;
    v_cod_oxigenio Parametro_Qualidade.codigo_parametro%TYPE;
BEGIN
    p_total_registadas := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_REG_MED_QUAL_AGUA(
        p_codigo_rh,
        p_eq_ph,
        p_valor_ph,
        p_eq_turbidez,
        p_valor_turbidez,
        p_eq_temperatura,
        p_valor_temperatura,
        p_eq_cloro,
        p_valor_cloro,
        p_eq_oxigenio,
        p_valor_oxigenio,
        p_total_registadas,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    SELECT codigo_parametro INTO v_cod_ph
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'PH';

    SELECT codigo_parametro INTO v_cod_turbidez
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'TURBIDEZ';

    SELECT codigo_parametro INTO v_cod_temperatura
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'TEMPERATURA';

    SELECT codigo_parametro INTO v_cod_cloro
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = 'CLORO RESIDUAL';

    SELECT codigo_parametro INTO v_cod_oxigenio
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) LIKE 'OXIG%NIO DISSOLVIDO';

    p_total_registadas := 0;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_ph, p_eq_ph, p_valor_ph, SYSDATE);
    p_total_registadas := p_total_registadas + 1;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_turbidez, p_eq_turbidez, p_valor_turbidez, SYSDATE);
    p_total_registadas := p_total_registadas + 1;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_temperatura, p_eq_temperatura, p_valor_temperatura, SYSDATE);
    p_total_registadas := p_total_registadas + 1;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_cloro, p_eq_cloro, p_valor_cloro, SYSDATE);
    p_total_registadas := p_total_registadas + 1;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_oxigenio, p_eq_oxigenio, p_valor_oxigenio, SYSDATE);
    p_total_registadas := p_total_registadas + 1;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := '5 medicoes de qualidade da agua registadas com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_total_registadas := 0;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/
