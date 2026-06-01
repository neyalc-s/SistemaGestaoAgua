SET ECHO OFF;

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
    v_novo_saldo_doadora     NUMBER;
    v_novo_saldo_receptora   NUMBER;
    v_validade_transferencia DATE;
    v_pode_continuar         NUMBER;
    v_mensagem_fam           VARCHAR2(4000);
BEGIN
    p_codigo_tc := NULL;
    p_mensagem  := NULL;

    PRC_APLICAR_TRANSF_COTA(
        p_cod_fam_doadora,
        p_cod_cota_doadora,
        p_cod_fam_receptora,
        p_cod_cota_receptora,
        p_volume_transferido,
        v_novo_saldo_doadora,
        v_novo_saldo_receptora,
        v_validade_transferencia,
        v_pode_continuar,
        v_mensagem_fam
    );

    IF v_pode_continuar <> 1 THEN
        RAISE_APPLICATION_ERROR(-20001, v_mensagem_fam);
    END IF;

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

    p_mensagem := 'Transferencia realizada com sucesso. Codigo: ' || p_codigo_tc;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_tc := NULL;
        p_mensagem  := SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REG_PARAM_QUALIDADE_EQ (
    p_nome_parametro    IN VARCHAR2,
    p_unidade_padrao    IN VARCHAR2,
    p_codigo_parametro  OUT NUMBER,
    p_pode_continuar    OUT NUMBER,
    p_mensagem          OUT VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    p_codigo_parametro := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_nome_parametro IS NULL OR TRIM(p_nome_parametro) IS NULL THEN
        p_mensagem := 'Nome do parametro e obrigatorio.';
        RETURN;
    END IF;

    IF p_unidade_padrao IS NULL OR TRIM(p_unidade_padrao) IS NULL THEN
        p_mensagem := 'Unidade padrao e obrigatoria.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Parametro_Qualidade
     WHERE UPPER(TRIM(nome_parametro)) = UPPER(TRIM(p_nome_parametro));

    IF v_count > 0 THEN
        SELECT codigo_parametro
          INTO p_codigo_parametro
          FROM Parametro_Qualidade
         WHERE UPPER(TRIM(nome_parametro)) = UPPER(TRIM(p_nome_parametro))
           AND ROWNUM = 1;

        p_pode_continuar := 1;
        p_mensagem := 'Parametro ja existia; codigo existente retornado.';
        RETURN;
    END IF;

    INSERT INTO Parametro_Qualidade (
        nome_parametro,
        unidade_padrao
    ) VALUES (
        TRIM(p_nome_parametro),
        TRIM(p_unidade_padrao)
    )
    RETURNING codigo_parametro INTO p_codigo_parametro;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Parametro de qualidade registado com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_parametro := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
    v_total_cotas NUMBER;
    v_cotas_expiradas NUMBER;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_tc IS NULL THEN
        p_mensagem := 'Informe o codigo da transferencia.';
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
        p_mensagem := 'O motivo desta transferencia foi alterado por outro utilizador. Actualize os dados e tente novamente.';
        RETURN;
    END IF;

    IF p_novo_motivo IS NULL OR TRIM(p_novo_motivo) IS NULL THEN
        p_mensagem := 'Informe o novo motivo.';
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

    SELECT COUNT(*)
      INTO v_total_cotas
      FROM TRANSFER_ASSOC_COTA tac
     WHERE tac.codigo_tc = p_codigo_tc;

    IF v_total_cotas = 0 THEN
        p_mensagem := 'Nao existem cotas associadas a esta transferencia.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_cotas_expiradas
      FROM TRANSFER_ASSOC_COTA tac
      LEFT JOIN Cota_Agua ca
        ON ca.codigo_cota = tac.codigo_cota
       AND ca.codigo_fb = tac.codigo_fb
     WHERE tac.codigo_tc = p_codigo_tc
       AND (ca.codigo_cota IS NULL
            OR ca.periodo_validade_ca IS NULL
            OR ca.periodo_validade_ca < SYSDATE);

    IF v_cotas_expiradas > 0 THEN
        p_mensagem := 'Nao e permitido actualizar o motivo porque uma ou mais cotas associadas estao expiradas.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Motivo validado para actualizacao.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
        p_mensagem := 'Informe o codigo do recurso hidrico.';
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
        p_mensagem := 'Operacao invalida para este contexto.';
        RETURN;
    END IF;

    IF v_operacao = 'ADICIONAR' AND p_data_impl IS NULL THEN
        p_mensagem := 'Informe a data de implementacao para adicionar medidas.';
        RETURN;
    END IF;

    IF v_operacao = 'ADICIONAR' AND TRUNC(p_data_impl) > TRUNC(SYSDATE) THEN
        p_mensagem := 'A data de implementacao nao pode ser futura.';
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
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_ACT_SAZONALIDADE_RH (
    p_codigo_rh              IN NUMBER,
    p_codigo_rh_out          OUT NUMBER,
    p_tipo_rh                OUT VARCHAR2,
    p_localizacao_rh         OUT VARCHAR2,
    p_volume_rh              OUT NUMBER,
    p_sazonalidade_actual    OUT VARCHAR2,
    p_vulnerabilidade_rh     OUT VARCHAR2,
    p_nivel_exploracao_rh    OUT VARCHAR2,
    p_pode_continuar         OUT NUMBER,
    p_mensagem               OUT VARCHAR2
)
IS
BEGIN
    p_codigo_rh_out := NULL;
    p_tipo_rh := NULL;
    p_localizacao_rh := NULL;
    p_volume_rh := NULL;
    p_sazonalidade_actual := NULL;
    p_vulnerabilidade_rh := NULL;
    p_nivel_exploracao_rh := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Informe o codigo do recurso hidrico.';
        RETURN;
    END IF;

    SELECT codigo_rh,
           tipo_rh,
           localizacao_rh,
           volume_rh,
           sazonalidade_rh,
           vulnerabilidade_rh,
           nivel_exploracao_rh
      INTO p_codigo_rh_out,
           p_tipo_rh,
           p_localizacao_rh,
           p_volume_rh,
           p_sazonalidade_actual,
           p_vulnerabilidade_rh,
           p_nivel_exploracao_rh
      FROM Recurso_Hidrico
     WHERE codigo_rh = p_codigo_rh;

    p_pode_continuar := 1;
    p_mensagem := 'Recurso hidrico carregado com sucesso.';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_pode_continuar := 0;
        p_mensagem := 'Recurso hidrico nao encontrado.';
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ACT_SAZONALIDADE_RH (
    p_codigo_rh              IN NUMBER,
    p_sazonalidade_actual    IN VARCHAR2,
    p_nova_sazonalidade      IN VARCHAR2,
    p_sucesso                OUT NUMBER,
    p_mensagem               OUT VARCHAR2
)
IS
    v_sazonalidade_actual Recurso_Hidrico.sazonalidade_rh%TYPE;
BEGIN
    p_sucesso := 0;
    p_mensagem := NULL;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Informe o codigo do recurso hidrico.';
        RETURN;
    END IF;

    IF p_nova_sazonalidade IS NULL OR TRIM(p_nova_sazonalidade) IS NULL THEN
        p_mensagem := 'Seleccione a nova sazonalidade.';
        RETURN;
    END IF;

    IF LENGTH(TRIM(p_nova_sazonalidade)) > 60 THEN
        p_mensagem := 'Sazonalidade nao pode ultrapassar 60 caracteres.';
        RETURN;
    END IF;

    SELECT sazonalidade_rh
      INTO v_sazonalidade_actual
      FROM Recurso_Hidrico
     WHERE codigo_rh = p_codigo_rh
     FOR UPDATE;

    IF NVL(UPPER(TRIM(v_sazonalidade_actual)), '#NULL#')
       <> NVL(UPPER(TRIM(p_sazonalidade_actual)), '#NULL#') THEN
        p_mensagem := 'A sazonalidade actual foi alterada por outra operacao. Recarregue os dados e tente novamente.';
        ROLLBACK;
        RETURN;
    END IF;

    IF NVL(UPPER(TRIM(v_sazonalidade_actual)), '#NULL#')
       = UPPER(TRIM(p_nova_sazonalidade)) THEN
        p_mensagem := 'A nova sazonalidade deve ser diferente da actual.';
        ROLLBACK;
        RETURN;
    END IF;

    UPDATE Recurso_Hidrico
       SET sazonalidade_rh = TRIM(p_nova_sazonalidade)
     WHERE codigo_rh = p_codigo_rh;

    COMMIT;
    p_sucesso := 1;
    p_mensagem := 'Sazonalidade actualizada com sucesso.';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        p_sucesso := 0;
        p_mensagem := 'Recurso hidrico nao encontrado.';
    WHEN OTHERS THEN
        ROLLBACK;
        p_sucesso := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
        p_mensagem := 'Informe o nome do responsavel.';
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
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
    v_analistas_ok NUMBER;
    v_analistas_msg VARCHAR2(4000);
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

    PRC_VAL_ANALISTAS_TRANS(
        p_eq_ph,
        v_cod_ph,
        p_eq_turbidez,
        v_cod_turbidez,
        p_eq_temperatura,
        v_cod_temperatura,
        p_eq_cloro,
        v_cod_cloro,
        p_eq_oxigenio,
        v_cod_oxigenio,
        v_count,
        v_analistas_ok,
        v_analistas_msg
    );

    IF v_analistas_ok <> 1 THEN
        p_mensagem := v_analistas_msg;
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
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_CRIAR_ALERTA_QUALIDADE (
    p_codigo_medicao  IN  NUMBER,
    p_codigo_rh       IN  NUMBER,
    p_mensagem_alerta IN  VARCHAR2,
    p_sucesso         OUT NUMBER,
    p_mensagem        OUT VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    p_sucesso := 0;
    p_mensagem := NULL;

    IF p_codigo_medicao IS NULL THEN
        p_mensagem := 'Codigo da medicao e obrigatorio.';
        RETURN;
    END IF;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Informe o codigo do recurso hidrico.';
        RETURN;
    END IF;

    IF p_mensagem_alerta IS NULL OR TRIM(p_mensagem_alerta) IS NULL THEN
        p_mensagem := 'Mensagem do alerta e obrigatoria.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Medicao_Qualidade_Agua
     WHERE cod_qualidade_agua = p_codigo_medicao;

    IF v_count = 0 THEN
        p_mensagem := 'Medicao de qualidade nao encontrada.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Alerta_Qualidade_Agua
     WHERE codigo_medicao = p_codigo_medicao;

    IF v_count > 0 THEN
        p_sucesso := 1;
        p_mensagem := 'Alerta ja existia para esta medicao.';
        RETURN;
    END IF;

    INSERT INTO Alerta_Qualidade_Agua (
        codigo_medicao,
        codigo_rh,
        mensagem_alerta,
        data_alerta
    ) VALUES (
        p_codigo_medicao,
        p_codigo_rh,
        SUBSTR(TRIM(p_mensagem_alerta), 1, 1000),
        SYSTIMESTAMP
    );

    p_sucesso := 1;
    p_mensagem := 'Alerta de qualidade criado com sucesso.';
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        p_sucesso := 1;
        p_mensagem := 'Alerta ja existia para esta medicao.';
    WHEN OTHERS THEN
        p_sucesso := 0;
        p_mensagem := 'Nao foi possivel criar o alerta de qualidade. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_AVALIAR_MEDICAO_QUALIDADE (
    p_codigo_medicao IN  NUMBER,
    p_sucesso        OUT NUMBER,
    p_mensagem       OUT VARCHAR2
)
IS
    v_codigo_rh      Medicao_Qualidade_Agua.codigo_rh%TYPE;
    v_valor          Medicao_Qualidade_Agua.valor%TYPE;
    v_nome_parametro Parametro_Qualidade.nome_parametro%TYPE;
    v_unidade        Parametro_Qualidade.unidade_padrao%TYPE;
    v_regra_alerta   VARCHAR2(300);
    v_alerta         NUMBER := 0;
BEGIN
    p_sucesso := 1;
    p_mensagem := 'Medicao normal.';

    IF p_codigo_medicao IS NULL THEN
        p_sucesso := 0;
        p_mensagem := 'Codigo da medicao e obrigatorio.';
        RETURN;
    END IF;

    SELECT m.codigo_rh,
           m.valor,
           p.nome_parametro,
           p.unidade_padrao
      INTO v_codigo_rh,
           v_valor,
           v_nome_parametro,
           v_unidade
      FROM Medicao_Qualidade_Agua m
      JOIN Parametro_Qualidade p ON p.codigo_parametro = m.codigo_parametro
     WHERE m.cod_qualidade_agua = p_codigo_medicao;

    IF UPPER(TRIM(v_nome_parametro)) = 'PH'
       AND (v_valor < 6.5 OR v_valor > 8.5) THEN
        v_alerta := 1;
        v_regra_alerta := 'Valor fora do intervalo pH 6.5 a 8.5 definido para o projecto.';
    ELSIF UPPER(TRIM(v_nome_parametro)) = 'TURBIDEZ'
       AND v_valor > 5 THEN
        v_alerta := 1;
        v_regra_alerta := 'Turbidez acima do limite 5 NTU definido para o projecto.';
    ELSIF UPPER(TRIM(v_nome_parametro)) = 'TEMPERATURA'
       AND (v_valor < 5 OR v_valor > 35) THEN
        v_alerta := 1;
        v_regra_alerta := 'Temperatura fora do intervalo 5 a 35 definido para o projecto.';
    ELSIF UPPER(TRIM(v_nome_parametro)) = 'CLORO RESIDUAL'
       AND (v_valor < 0.2 OR v_valor > 2.0) THEN
        v_alerta := 1;
        v_regra_alerta := 'Cloro residual fora do intervalo 0.2 a 2.0 definido para o projecto.';
    ELSIF UPPER(TRIM(v_nome_parametro)) LIKE 'OXIG%NIO DISSOLVIDO'
       AND v_valor < 5 THEN
        v_alerta := 1;
        v_regra_alerta := 'Oxigenio dissolvido abaixo do limite 5 definido para o projecto.';
    END IF;

    IF v_alerta = 1 THEN
        PRC_CRIAR_ALERTA_QUALIDADE(
            p_codigo_medicao,
            v_codigo_rh,
            'Alerta de qualidade da agua: Recurso ' || v_codigo_rh ||
            ' apresentou ' || v_nome_parametro || ' = ' ||
            TO_CHAR(v_valor) || ' ' || NVL(v_unidade, '') ||
            ' na medicao ' || p_codigo_medicao || '. ' || v_regra_alerta,
            p_sucesso,
            p_mensagem
        );
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_sucesso := 0;
        p_mensagem := 'Medicao de qualidade nao encontrada.';
    WHEN OTHERS THEN
        p_sucesso := 0;
        p_mensagem := 'Nao foi possivel avaliar a medicao de qualidade. Detalhe: ' || SQLERRM;
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
    v_cod_medicao Medicao_Qualidade_Agua.cod_qualidade_agua%TYPE;
    v_alerta_sucesso NUMBER;
    v_alerta_msg VARCHAR2(4000);
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
    VALUES (p_codigo_rh, v_cod_ph, p_eq_ph, p_valor_ph, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_turbidez, p_eq_turbidez, p_valor_turbidez, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_temperatura, p_eq_temperatura, p_valor_temperatura, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_cloro, p_eq_cloro, p_valor_cloro, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_oxigenio, p_eq_oxigenio, p_valor_oxigenio, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Recurso hidrico registado com sucesso com 5 medicoes iniciais.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_rh := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
    v_analistas_ok NUMBER;
    v_analistas_msg VARCHAR2(4000);
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

    PRC_VAL_ANALISTAS_TRANS(
        p_eq_ph,
        v_cod_ph,
        p_eq_turbidez,
        v_cod_turbidez,
        p_eq_temperatura,
        v_cod_temperatura,
        p_eq_cloro,
        v_cod_cloro,
        p_eq_oxigenio,
        v_cod_oxigenio,
        v_count,
        v_analistas_ok,
        v_analistas_msg
    );

    IF v_analistas_ok <> 1 THEN
        p_mensagem := v_analistas_msg;
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
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
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
    v_cod_medicao Medicao_Qualidade_Agua.cod_qualidade_agua%TYPE;
    v_alerta_sucesso NUMBER;
    v_alerta_msg VARCHAR2(4000);
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
    VALUES (p_codigo_rh, v_cod_ph, p_eq_ph, p_valor_ph, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);
    p_total_registadas := p_total_registadas + 1;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_turbidez, p_eq_turbidez, p_valor_turbidez, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);
    p_total_registadas := p_total_registadas + 1;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_temperatura, p_eq_temperatura, p_valor_temperatura, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);
    p_total_registadas := p_total_registadas + 1;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_cloro, p_eq_cloro, p_valor_cloro, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);
    p_total_registadas := p_total_registadas + 1;

    INSERT INTO Medicao_Qualidade_Agua (codigo_rh, codigo_parametro, equipe_id, valor, data_medicao)
    VALUES (p_codigo_rh, v_cod_oxigenio, p_eq_oxigenio, p_valor_oxigenio, SYSDATE)
    RETURNING cod_qualidade_agua INTO v_cod_medicao;
    PRC_AVALIAR_MEDICAO_QUALIDADE(v_cod_medicao, v_alerta_sucesso, v_alerta_msg);
    p_total_registadas := p_total_registadas + 1;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := '5 medicoes de qualidade da agua registadas com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_total_registadas := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/
