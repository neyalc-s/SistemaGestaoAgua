SET ECHO OFF;

CREATE OR REPLACE PROCEDURE PRC_PRE_ACT_REGISTRO_CONSUMO (
    p_codigo_rc              IN NUMBER,
    p_codigo_fb              OUT NUMBER,
    p_codigo_pd              OUT NUMBER,
    p_codigo_cota            OUT NUMBER,
    p_data_hora_rc           OUT TIMESTAMP,
    p_volume_retirado_rc     OUT NUMBER,
    p_pessoa_coleta_rc       OUT VARCHAR2,
    p_metodo_autenticacao_rc OUT VARCHAR2,
    p_saldo_cota_rc          OUT NUMBER,
    p_observacao_rc          OUT VARCHAR2,
    p_periodo_validade_ca    OUT DATE,
    p_status_validade        OUT VARCHAR2,
    p_pode_continuar         OUT NUMBER,
    p_mensagem               OUT VARCHAR2
)
IS
BEGIN
    p_codigo_fb := NULL;
    p_codigo_pd := NULL;
    p_codigo_cota := NULL;
    p_data_hora_rc := NULL;
    p_volume_retirado_rc := NULL;
    p_pessoa_coleta_rc := NULL;
    p_metodo_autenticacao_rc := NULL;
    p_saldo_cota_rc := NULL;
    p_observacao_rc := NULL;
    p_periodo_validade_ca := NULL;
    p_status_validade := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_rc IS NULL THEN
        p_mensagem := 'Codigo do registro de consumo e obrigatorio.';
        RETURN;
    END IF;

    SELECT rc.codigo_fb,
           rc.codigo_pd,
           rc.codigo_cota,
           rc.data_hora_rc,
           rc.volume_retirado_rc,
           rc.pessoa_coleta_rc,
           rc.metodo_autenticacao_rc,
           rc.saldo_cota_rc,
           rc.observacao_rc,
           ca.periodo_validade_ca,
           ca.status_validade
      INTO p_codigo_fb,
           p_codigo_pd,
           p_codigo_cota,
           p_data_hora_rc,
           p_volume_retirado_rc,
           p_pessoa_coleta_rc,
           p_metodo_autenticacao_rc,
           p_saldo_cota_rc,
           p_observacao_rc,
           p_periodo_validade_ca,
           p_status_validade
      FROM Registro_Consumo rc
      JOIN VW_COTA_AGUA_STATUS ca
        ON ca.codigo_fb = rc.codigo_fb
       AND ca.codigo_cota = rc.codigo_cota
     WHERE rc.codigo_rc = p_codigo_rc;

    IF p_periodo_validade_ca IS NULL OR SYSDATE > p_periodo_validade_ca THEN
        p_mensagem := 'Nao e permitido actualizar observacao de registro associado a cota expirada.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Registro de consumo carregado. A observacao pode ser actualizada.';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_pode_continuar := 0;
        p_mensagem := 'Registro de consumo nao encontrado ou cota associada indisponivel.';
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ACT_OBSERVACAO_REG_CONS (
    p_codigo_rc             IN NUMBER,
    p_observacao_original   IN VARCHAR2,
    p_nova_observacao       IN VARCHAR2,
    p_pode_continuar        OUT NUMBER,
    p_mensagem              OUT VARCHAR2
)
IS
    v_periodo_validade_ca   DATE;
    v_observacao_actual     Registro_Consumo.observacao_rc%TYPE;
    v_codigo_fb             Registro_Consumo.codigo_fb%TYPE;
    v_codigo_cota           Registro_Consumo.codigo_cota%TYPE;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_rc IS NULL THEN
        p_mensagem := 'Codigo do registro de consumo e obrigatorio.';
        RETURN;
    END IF;

    SELECT observacao_rc,
           codigo_fb,
           codigo_cota
      INTO v_observacao_actual,
           v_codigo_fb,
           v_codigo_cota
      FROM Registro_Consumo
     WHERE codigo_rc = p_codigo_rc
     FOR UPDATE;

    SELECT periodo_validade_ca
      INTO v_periodo_validade_ca
      FROM VW_COTA_AGUA_STATUS
     WHERE codigo_fb = v_codigo_fb
       AND codigo_cota = v_codigo_cota;

    IF v_periodo_validade_ca IS NULL OR SYSDATE > v_periodo_validade_ca THEN
        p_mensagem := 'Nao e permitido actualizar observacao de registro associado a cota expirada.';
        RETURN;
    END IF;

    IF NOT (
        (v_observacao_actual = p_observacao_original)
        OR (v_observacao_actual IS NULL AND p_observacao_original IS NULL)
    ) THEN
        p_mensagem := 'A observacao foi alterada por outro utilizador. Recarregue o registro.';
        RETURN;
    END IF;

    UPDATE Registro_Consumo
       SET observacao_rc = p_nova_observacao
     WHERE codigo_rc = p_codigo_rc;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Observacao do registro de consumo actualizada com sucesso.';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_pode_continuar := 0;
        p_mensagem := 'Registro de consumo nao encontrado ou cota associada indisponivel.';
    WHEN OTHERS THEN
        ROLLBACK;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

SET ECHO OFF;

CREATE OR REPLACE PROCEDURE PRC_PRE_ALT_EST_PONTO (
    p_codigo_pd        IN NUMBER,
    p_estado_esperado  IN VARCHAR2,
    p_estado_actual    OUT VARCHAR2,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    p_estado_actual := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_pd IS NULL THEN
        p_mensagem := 'Informe o codigo do ponto de distribuicao.';
        RETURN;
    END IF;

    IF p_estado_esperado IS NULL OR TRIM(p_estado_esperado) IS NULL THEN
        p_mensagem := 'Estado esperado e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd;

    IF v_count = 0 THEN
        p_mensagem := 'Ponto de distribuicao nao encontrado.';
        RETURN;
    END IF;

    SELECT estado_operacional_pd
      INTO p_estado_actual
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd;

    IF p_estado_actual IS NULL THEN
        p_mensagem := 'Estado operacional do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF UPPER(TRIM(p_estado_actual)) <> UPPER(TRIM(p_estado_esperado)) THEN
        p_mensagem := 'O ponto de distribuicao foi alterado por outro utilizador.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Ponto de distribuicao validado com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ALT_EST_PONTO (
    p_codigo_pd        IN NUMBER,
    p_estado_esperado  IN VARCHAR2,
    p_novo_estado      IN VARCHAR2,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_estado_actual VARCHAR2(60);
    v_estado_normalizado VARCHAR2(60);
    v_cancelados NUMBER := 0;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_ALT_EST_PONTO(
        p_codigo_pd,
        p_estado_esperado,
        v_estado_actual,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    IF p_novo_estado IS NULL OR TRIM(p_novo_estado) IS NULL THEN
        p_mensagem := 'Novo estado e obrigatorio.';
        RETURN;
    END IF;

    v_estado_normalizado := UPPER(TRIM(p_novo_estado));

    IF v_estado_normalizado NOT IN ('ACTIVO', 'INACTIVO', 'EM MANUTENCAO') THEN
        p_mensagem := 'Novo estado invalido.';
        RETURN;
    END IF;

    UPDATE Ponto_Distribuicao
       SET estado_operacional_pd = CASE
             WHEN v_estado_normalizado = 'ACTIVO' THEN 'Activo'
             WHEN v_estado_normalizado = 'INACTIVO' THEN 'Inactivo'
             ELSE 'Em Manutencao'
           END
     WHERE codigo_pd = p_codigo_pd
       AND UPPER(TRIM(estado_operacional_pd)) = UPPER(TRIM(p_estado_esperado));

    IF SQL%ROWCOUNT = 0 THEN
        p_pode_continuar := 0;
        p_mensagem := 'Nao foi possivel actualizar o estado do ponto de distribuicao.';
        RETURN;
    END IF;

    IF v_estado_normalizado IN ('INACTIVO', 'EM MANUTENCAO') THEN
        UPDATE Historico_Abastecimento
           SET duracao_horas = ROUND(GREATEST((SYSDATE - data_inicio) * 24, 0), 6),
               data_fim = SYSDATE,
               estado_abastecimento = 'Cancelado'
         WHERE codigo_pd = p_codigo_pd
           AND estado_abastecimento = 'Em curso';

        v_cancelados := SQL%ROWCOUNT;
    END IF;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Estado operacional actualizado com sucesso para ' ||
                  CASE
                    WHEN v_estado_normalizado = 'ACTIVO' THEN 'Activo'
                    WHEN v_estado_normalizado = 'INACTIVO' THEN 'Inactivo'
                    ELSE 'Em Manutencao'
                  END;
    IF v_cancelados > 0 THEN
        p_mensagem := p_mensagem || '. Abastecimentos cancelados: ' || v_cancelados;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_ASSOC_PONTOS_RECURSO (
    p_codigos_pontos  IN VARCHAR2,
    p_codigo_rh       IN NUMBER,
    p_total_pontos    OUT NUMBER,
    p_pode_continuar  OUT NUMBER,
    p_mensagem        OUT VARCHAR2
)
IS
    v_posicao       NUMBER := 1;
    v_codigo_texto  VARCHAR2(30);
    v_codigo_pd     NUMBER;
    v_count         NUMBER;
    v_recurso_atual NUMBER;
    v_iguais        NUMBER := 0;
    v_volume_rh     NUMBER;
    v_remote_msg    VARCHAR2(4000);
BEGIN
    p_total_pontos := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigos_pontos IS NULL OR TRIM(p_codigos_pontos) IS NULL THEN
        p_mensagem := 'Selecione pelo menos um ponto de distribuicao.';
        RETURN;
    END IF;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Informe o codigo do recurso hidrico.';
        RETURN;
    END IF;

    PRC_VALIDAR_RECURSO_DIST(
        p_codigo_rh,
        v_count,
        v_volume_rh,
        v_remote_msg
    );

    IF v_count = 0 THEN
        p_mensagem := v_remote_msg;
        RETURN;
    END IF;

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(p_codigos_pontos, '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_texto := TRIM(v_codigo_texto);

        IF NOT REGEXP_LIKE(v_codigo_texto, '^[0-9]+$') THEN
            p_mensagem := 'Codigo de ponto invalido: ' || v_codigo_texto;
            RETURN;
        END IF;

        v_codigo_pd := TO_NUMBER(v_codigo_texto);

        SELECT COUNT(*)
          INTO v_count
          FROM Ponto_Distribuicao
         WHERE codigo_pd = v_codigo_pd;

        IF v_count = 0 THEN
            p_mensagem := 'Ponto de distribuicao nao encontrado: ' || v_codigo_pd;
            RETURN;
        END IF;

        SELECT codigo_rh
          INTO v_recurso_atual
          FROM Ponto_Distribuicao
         WHERE codigo_pd = v_codigo_pd;

        IF v_recurso_atual = p_codigo_rh THEN
            v_iguais := v_iguais + 1;
        END IF;

        SELECT COUNT(*)
          INTO v_count
          FROM Historico_Abastecimento
         WHERE codigo_pd = v_codigo_pd
           AND estado_abastecimento = 'Em curso';

        IF v_count > 0 THEN
            p_mensagem := 'O ponto de distribuicao ' || v_codigo_pd ||
                          ' possui abastecimento em curso. Finalize ou cancele o abastecimento antes de alterar o recurso hidrico.';
            RETURN;
        END IF;

        p_total_pontos := p_total_pontos + 1;
        v_posicao := v_posicao + 1;
    END LOOP;

    IF p_total_pontos = 0 THEN
        p_mensagem := 'Selecione pelo menos um ponto de distribuicao.';
        RETURN;
    END IF;

    IF v_iguais = p_total_pontos THEN
        p_mensagem := 'Todos os pontos selecionados ja estao associados a este recurso hidrico.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para associar os pontos de distribuicao ao recurso hidrico.';
EXCEPTION
    WHEN OTHERS THEN
        p_total_pontos := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ASSOC_PONTOS_RECURSO (
    p_codigos_pontos   IN VARCHAR2,
    p_codigo_rh        IN NUMBER,
    p_total_associados OUT NUMBER,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_posicao          NUMBER := 1;
    v_codigo_texto     VARCHAR2(30);
    v_codigo_pd        NUMBER;
    v_total_validados  NUMBER;
BEGIN
    p_total_associados := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_ASSOC_PONTOS_RECURSO(
        p_codigos_pontos,
        p_codigo_rh,
        v_total_validados,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(p_codigos_pontos, '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_pd := TO_NUMBER(TRIM(v_codigo_texto));

        UPDATE Ponto_Distribuicao
           SET codigo_rh = p_codigo_rh
         WHERE codigo_pd = v_codigo_pd
           AND codigo_rh <> p_codigo_rh;

        p_total_associados := p_total_associados + SQL%ROWCOUNT;
        v_posicao := v_posicao + 1;
    END LOOP;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := p_total_associados || ' ponto(s) de distribuicao associado(s) ao recurso hidrico ' || p_codigo_rh || '.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_total_associados := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_ASSOC_PONTOS_COMITE (
    p_codigos_pontos  IN VARCHAR2,
    p_codigo_comite   IN NUMBER,
    p_total_pontos    OUT NUMBER,
    p_pode_continuar  OUT NUMBER,
    p_mensagem        OUT VARCHAR2
)
IS
    v_posicao       NUMBER := 1;
    v_codigo_texto  VARCHAR2(30);
    v_codigo_pd     NUMBER;
    v_count         NUMBER;
    v_comite_atual  NUMBER;
    v_iguais        NUMBER := 0;
    v_remote_msg    VARCHAR2(4000);
BEGIN
    p_total_pontos := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigos_pontos IS NULL OR TRIM(p_codigos_pontos) IS NULL THEN
        p_mensagem := 'Selecione pelo menos um ponto de distribuicao.';
        RETURN;
    END IF;

    IF p_codigo_comite IS NULL THEN
        p_mensagem := 'Codigo do comite e obrigatorio.';
        RETURN;
    END IF;

    PRC_VALIDAR_COMITE_DIST(
        p_codigo_comite,
        v_count,
        v_remote_msg
    );

    IF v_count = 0 THEN
        p_mensagem := v_remote_msg;
        RETURN;
    END IF;

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(p_codigos_pontos, '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_texto := TRIM(v_codigo_texto);

        IF NOT REGEXP_LIKE(v_codigo_texto, '^[0-9]+$') THEN
            p_mensagem := 'Codigo de ponto invalido: ' || v_codigo_texto;
            RETURN;
        END IF;

        v_codigo_pd := TO_NUMBER(v_codigo_texto);

        SELECT COUNT(*)
          INTO v_count
          FROM Ponto_Distribuicao
         WHERE codigo_pd = v_codigo_pd;

        IF v_count = 0 THEN
            p_mensagem := 'Ponto de distribuicao nao encontrado: ' || v_codigo_pd;
            RETURN;
        END IF;

        SELECT cod_comite_responsavel
          INTO v_comite_atual
          FROM Ponto_Distribuicao
         WHERE codigo_pd = v_codigo_pd;

        IF v_comite_atual = p_codigo_comite THEN
            v_iguais := v_iguais + 1;
        END IF;

        p_total_pontos := p_total_pontos + 1;
        v_posicao := v_posicao + 1;
    END LOOP;

    IF p_total_pontos = 0 THEN
        p_mensagem := 'Selecione pelo menos um ponto de distribuicao.';
        RETURN;
    END IF;

    IF v_iguais = p_total_pontos THEN
        p_mensagem := 'Todos os pontos selecionados ja estao associados a este comite.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para associar os pontos de distribuicao ao comite.';
EXCEPTION
    WHEN OTHERS THEN
        p_total_pontos := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ASSOC_PONTOS_COMITE (
    p_codigos_pontos   IN VARCHAR2,
    p_codigo_comite    IN NUMBER,
    p_total_associados OUT NUMBER,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_posicao          NUMBER := 1;
    v_codigo_texto     VARCHAR2(30);
    v_codigo_pd        NUMBER;
    v_total_validados  NUMBER;
BEGIN
    p_total_associados := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_ASSOC_PONTOS_COMITE(
        p_codigos_pontos,
        p_codigo_comite,
        v_total_validados,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(p_codigos_pontos, '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_pd := TO_NUMBER(TRIM(v_codigo_texto));

        UPDATE Ponto_Distribuicao
           SET cod_comite_responsavel = p_codigo_comite
         WHERE codigo_pd = v_codigo_pd
           AND cod_comite_responsavel <> p_codigo_comite;

        p_total_associados := p_total_associados + SQL%ROWCOUNT;
        v_posicao := v_posicao + 1;
    END LOOP;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := p_total_associados || ' ponto(s) de distribuicao associado(s) ao comite ' || p_codigo_comite || '.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_total_associados := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_REGISTAR_PONTO (
    p_cod_comite              IN NUMBER,
    p_equipe_id               IN NUMBER,
    p_codigo_rh               IN NUMBER,
    p_volume_actual           IN NUMBER,
    p_localizacao             IN VARCHAR2,
    p_tipo_infraestrutura     IN VARCHAR2,
    p_capacidade              IN NUMBER,
    p_fonte_abastecimento     IN VARCHAR2,
    p_tecnologia_tratamento   IN VARCHAR2,
    p_data_instalacao         IN DATE,
    p_estado_operacional      IN VARCHAR2,
    p_pode_continuar          OUT NUMBER,
    p_mensagem                OUT VARCHAR2
)
IS
    v_count       NUMBER;
    v_estado_norm VARCHAR2(30);
    v_volume_rh   NUMBER;
    v_remote_msg  VARCHAR2(4000);
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Recurso hidrico e obrigatorio.';
        RETURN;
    END IF;

    PRC_VALIDAR_RECURSO_DIST(
        p_codigo_rh,
        v_count,
        v_volume_rh,
        v_remote_msg
    );

    IF v_count = 0 THEN
        p_mensagem := v_remote_msg;
        RETURN;
    END IF;

    IF p_cod_comite IS NULL THEN
        p_mensagem := 'Comite responsavel e obrigatorio.';
        RETURN;
    END IF;

    PRC_VALIDAR_COMITE_DIST(
        p_cod_comite,
        v_count,
        v_remote_msg
    );

    IF v_count = 0 THEN
        p_mensagem := v_remote_msg;
        RETURN;
    END IF;

    IF p_equipe_id IS NULL THEN
        p_mensagem := 'Equipe tecnica e obrigatoria.';
        RETURN;
    END IF;

    PRC_VALIDAR_TECNICO_MANUT_DIST(
        p_equipe_id,
        v_count,
        v_remote_msg
    );

    IF v_count = 0 THEN
        p_mensagem := v_remote_msg;
        RETURN;
    END IF;

    IF p_localizacao IS NULL OR TRIM(p_localizacao) IS NULL THEN
        p_mensagem := 'Localizacao do ponto e obrigatoria.';
        RETURN;
    END IF;

    IF p_tipo_infraestrutura IS NULL OR TRIM(p_tipo_infraestrutura) IS NULL THEN
        p_mensagem := 'Tipo de infraestrutura e obrigatorio.';
        RETURN;
    END IF;

    IF p_fonte_abastecimento IS NULL OR TRIM(p_fonte_abastecimento) IS NULL THEN
        p_mensagem := 'Fonte de abastecimento e obrigatoria.';
        RETURN;
    END IF;

    IF p_tecnologia_tratamento IS NULL OR TRIM(p_tecnologia_tratamento) IS NULL THEN
        p_mensagem := 'Tecnologia de tratamento e obrigatoria.';
        RETURN;
    END IF;

    IF p_data_instalacao IS NULL THEN
        p_mensagem := 'Data de instalacao e obrigatoria.';
        RETURN;
    END IF;

    IF p_capacidade IS NULL OR p_capacidade <= 0 THEN
        p_mensagem := 'Capacidade de armazenamento deve ser maior que zero.';
        RETURN;
    END IF;

    IF p_volume_actual IS NULL OR p_volume_actual < 0 THEN
        p_mensagem := 'Volume actual deve ser maior ou igual a zero.';
        RETURN;
    END IF;

    IF p_volume_actual > p_capacidade THEN
        p_mensagem := 'Volume actual nao pode ser maior que a capacidade de armazenamento.';
        RETURN;
    END IF;

    IF p_estado_operacional IS NULL OR TRIM(p_estado_operacional) IS NULL THEN
        p_mensagem := 'Estado operacional e obrigatorio.';
        RETURN;
    END IF;

    v_estado_norm := UPPER(TRIM(p_estado_operacional));

    IF v_estado_norm NOT IN ('ACTIVO', 'INACTIVO', 'EM MANUTENCAO') THEN
        p_mensagem := 'Estado operacional invalido.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para registar ponto de distribuicao.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REGISTAR_PONTO (
    p_cod_comite              IN NUMBER,
    p_equipe_id               IN NUMBER,
    p_codigo_rh               IN NUMBER,
    p_volume_actual           IN NUMBER,
    p_localizacao             IN VARCHAR2,
    p_tipo_infraestrutura     IN VARCHAR2,
    p_capacidade              IN NUMBER,
    p_fonte_abastecimento     IN VARCHAR2,
    p_tecnologia_tratamento   IN VARCHAR2,
    p_data_instalacao         IN DATE,
    p_estado_operacional      IN VARCHAR2,
    p_codigo_pd               OUT NUMBER,
    p_pode_continuar          OUT NUMBER,
    p_mensagem                OUT VARCHAR2
)
IS
BEGIN
    p_codigo_pd := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_REGISTAR_PONTO(
        p_cod_comite,
        p_equipe_id,
        p_codigo_rh,
        p_volume_actual,
        p_localizacao,
        p_tipo_infraestrutura,
        p_capacidade,
        p_fonte_abastecimento,
        p_tecnologia_tratamento,
        p_data_instalacao,
        p_estado_operacional,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    INSERT INTO Ponto_Distribuicao (
        cod_comite_responsavel,
        equipe_id,
        codigo_rh,
        volume_actual_pd,
        localizacao_pd,
        tipo_infraestrutura_pd,
        capacidade_armazenamento_pd,
        fonte_abastecimento_pd,
        tecnologia_tratamento_pd,
        data_instalacao_pd,
        estado_operacional_pd
    ) VALUES (
        p_cod_comite,
        p_equipe_id,
        p_codigo_rh,
        p_volume_actual,
        TRIM(p_localizacao),
        TRIM(p_tipo_infraestrutura),
        p_capacidade,
        TRIM(p_fonte_abastecimento),
        TRIM(p_tecnologia_tratamento),
        p_data_instalacao,
        CASE
            WHEN UPPER(TRIM(p_estado_operacional)) = 'ACTIVO' THEN 'Activo'
            WHEN UPPER(TRIM(p_estado_operacional)) = 'EM MANUTENCAO' THEN 'Em Manutencao'
            ELSE 'Inactivo'
        END
    )
    RETURNING codigo_pd INTO p_codigo_pd;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Ponto de distribuicao registado com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_pd := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_REGISTAR_MANUT_PONTO (
    p_codigo_pd        IN NUMBER,
    p_data_manutencao  IN DATE,
    p_tipo_manutencao  IN VARCHAR2,
    p_alterar_estado   IN VARCHAR2,
    p_novo_estado      IN VARCHAR2,
    p_equipe_id        OUT NUMBER,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_alterar_estado VARCHAR2(10);
    v_novo_estado VARCHAR2(30);
    v_remote_msg VARCHAR2(4000);
BEGIN
    p_equipe_id := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_pd IS NULL THEN
        p_mensagem := 'Informe o codigo do ponto de distribuicao.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM VW_PONTO_DISTRIBUICAO
     WHERE codigo_pd = p_codigo_pd;

    IF v_count = 0 THEN
        p_mensagem := 'Ponto de distribuicao nao encontrado.';
        RETURN;
    END IF;

    SELECT equipe_id
      INTO p_equipe_id
      FROM VW_PONTO_DISTRIBUICAO
     WHERE codigo_pd = p_codigo_pd;

    IF p_equipe_id IS NULL THEN
        p_mensagem := 'Ponto de distribuicao sem equipe tecnica associada.';
        RETURN;
    END IF;

    PRC_VALIDAR_TECNICO_MANUT_DIST(
        p_equipe_id,
        v_count,
        v_remote_msg
    );

    IF v_count = 0 THEN
        p_mensagem := v_remote_msg;
        RETURN;
    END IF;

    IF p_data_manutencao IS NULL THEN
        p_mensagem := 'Data da manutencao e obrigatoria.';
        RETURN;
    END IF;

    IF TRUNC(p_data_manutencao) > TRUNC(SYSDATE) THEN
        p_mensagem := 'Data da manutencao nao pode ser futura.';
        RETURN;
    END IF;

    IF p_tipo_manutencao IS NULL OR TRIM(p_tipo_manutencao) IS NULL THEN
        p_mensagem := 'Tipo ou descricao da manutencao e obrigatorio.';
        RETURN;
    END IF;

    v_alterar_estado := UPPER(TRIM(NVL(p_alterar_estado, 'NAO')));

    IF v_alterar_estado NOT IN ('SIM', 'NAO') THEN
        p_mensagem := 'Opcao de alterar estado invalida.';
        RETURN;
    END IF;

    IF v_alterar_estado = 'SIM' THEN
        IF TRUNC(p_data_manutencao) <> TRUNC(SYSDATE) THEN
            p_mensagem := 'O estado do ponto so pode ser alterado quando a manutencao for registada para hoje.';
            RETURN;
        END IF;

        IF p_novo_estado IS NULL OR TRIM(p_novo_estado) IS NULL THEN
            p_mensagem := 'Novo estado do ponto e obrigatorio.';
            RETURN;
        END IF;

        v_novo_estado := UPPER(TRIM(p_novo_estado));

        IF v_novo_estado NOT IN ('INACTIVO', 'EM MANUTENCAO') THEN
            p_mensagem := 'Novo estado do ponto deve ser Inactivo ou Em Manutencao.';
            RETURN;
        END IF;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para registar manutencao.';
EXCEPTION
    WHEN OTHERS THEN
        p_equipe_id := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REGISTAR_MANUT_PONTO (
    p_codigo_pd              IN NUMBER,
    p_data_manutencao        IN DATE,
    p_tipo_manutencao        IN VARCHAR2,
    p_alterar_estado         IN VARCHAR2,
    p_novo_estado            IN VARCHAR2,
    p_cod_historico          OUT NUMBER,
    p_pode_continuar         OUT NUMBER,
    p_mensagem               OUT VARCHAR2
)
IS
    v_equipe_id NUMBER;
    v_alterar_estado VARCHAR2(10);
    v_novo_estado VARCHAR2(30);
    v_cancelados NUMBER := 0;
BEGIN
    p_cod_historico := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_REGISTAR_MANUT_PONTO(
        p_codigo_pd,
        p_data_manutencao,
        p_tipo_manutencao,
        p_alterar_estado,
        p_novo_estado,
        v_equipe_id,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    INSERT INTO Historico_Manutencao (
        codigo_pd,
        equipe_id,
        data_manutencao,
        tipo_manutencao
    ) VALUES (
        p_codigo_pd,
        v_equipe_id,
        p_data_manutencao,
        TRIM(p_tipo_manutencao)
    )
    RETURNING cod_historico_manutencao INTO p_cod_historico;

    v_alterar_estado := UPPER(TRIM(NVL(p_alterar_estado, 'NAO')));

    IF v_alterar_estado = 'SIM' THEN
        v_novo_estado := UPPER(TRIM(p_novo_estado));

        UPDATE Ponto_Distribuicao
           SET estado_operacional_pd = CASE
                 WHEN v_novo_estado = 'INACTIVO' THEN 'Inactivo'
                 ELSE 'Em Manutencao'
               END
         WHERE codigo_pd = p_codigo_pd;

        UPDATE Historico_Abastecimento
           SET duracao_horas = ROUND(GREATEST((SYSDATE - data_inicio) * 24, 0), 6),
               data_fim = SYSDATE,
               estado_abastecimento = 'Cancelado'
         WHERE codigo_pd = p_codigo_pd
           AND estado_abastecimento = 'Em curso';

        v_cancelados := SQL%ROWCOUNT;
    END IF;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Manutencao registada com sucesso.';
    IF v_alterar_estado = 'SIM' THEN
        p_mensagem := p_mensagem || ' Estado do ponto actualizado.';
        IF v_cancelados > 0 THEN
            p_mensagem := p_mensagem || ' Abastecimentos cancelados: ' || v_cancelados || '.';
        END IF;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_cod_historico := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_REG_ABASTECIMENTO (
    p_codigo_rh          IN NUMBER,
    p_codigo_pd          IN NUMBER,
    p_volume_abastecido  IN NUMBER,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_estado Ponto_Distribuicao.estado_operacional_pd%TYPE;
    v_vazao NUMBER;
    v_capacidade Ponto_Distribuicao.capacidade_armazenamento_pd%TYPE;
    v_volume_actual Ponto_Distribuicao.volume_actual_pd%TYPE;
    v_volume_em_curso NUMBER(12,2);
    v_remote_msg VARCHAR2(4000);
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Informe o codigo do recurso hidrico.';
        RETURN;
    END IF;

    IF p_codigo_pd IS NULL THEN
        p_mensagem := 'Informe o codigo do ponto de distribuicao.';
        RETURN;
    END IF;

    IF p_volume_abastecido IS NULL OR p_volume_abastecido <= 0 THEN
        p_mensagem := 'Volume abastecido deve ser maior que zero.';
        RETURN;
    END IF;

    PRC_VALIDAR_RECURSO_DIST(
        p_codigo_rh,
        v_count,
        v_vazao,
        v_remote_msg
    );

    IF v_count = 0 THEN
        p_mensagem := v_remote_msg;
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd;

    IF v_count = 0 THEN
        p_mensagem := 'Ponto de distribuicao nao encontrado.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd
       AND codigo_rh = p_codigo_rh;

    IF v_count = 0 THEN
        p_mensagem := 'O ponto selecionado nao pertence ao recurso hidrico informado.';
        RETURN;
    END IF;

    IF v_vazao IS NULL OR v_vazao <= 0 THEN
        p_mensagem := 'Vazao do recurso hidrico deve ser maior que zero.';
        RETURN;
    END IF;

    SELECT estado_operacional_pd,
           capacidade_armazenamento_pd,
           NVL(volume_actual_pd, 0)
      INTO v_estado,
           v_capacidade,
           v_volume_actual
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd;

    IF UPPER(TRIM(NVL(v_estado, ''))) <> 'ACTIVO' THEN
        p_mensagem := 'Ponto de distribuicao deve estar Activo para receber abastecimento.';
        RETURN;
    END IF;

    IF v_capacidade IS NULL OR v_capacidade <= 0 THEN
        p_mensagem := 'Capacidade do ponto de distribuicao deve ser maior que zero.';
        RETURN;
    END IF;

    SELECT NVL(SUM(volume_abastecido), 0)
      INTO v_volume_em_curso
      FROM Historico_Abastecimento
     WHERE codigo_pd = p_codigo_pd
       AND estado_abastecimento = 'Em curso';

    SELECT COUNT(*)
      INTO v_count
      FROM Historico_Abastecimento
     WHERE codigo_rh = p_codigo_rh
       AND estado_abastecimento = 'Em curso';

    IF v_count > 0 THEN
        p_mensagem := 'Este recurso hidrico ja possui um abastecimento aberto. Conclua ou cancele o abastecimento actual antes de iniciar outro.';
        RETURN;
    END IF;

    IF v_volume_actual + v_volume_em_curso + p_volume_abastecido > v_capacidade THEN
        p_mensagem := 'Capacidade do ponto seria ultrapassada. Volume actual + abastecimentos em curso + novo volume excede a capacidade.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para registar abastecimento.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REG_ABASTECIMENTO (
    p_codigo_rh          IN NUMBER,
    p_codigo_pd          IN NUMBER,
    p_volume_abastecido  IN NUMBER,
    p_cod_abastecimento  OUT NUMBER,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2,
    p_data_inicio_txt    OUT VARCHAR2,
    p_data_fim_txt       OUT VARCHAR2
)
IS
    v_vazao NUMBER;
    v_recurso_existe NUMBER;
    v_remote_msg VARCHAR2(4000);
    v_duracao_horas NUMBER(12,6);
    v_data_inicio DATE;
    v_data_fim DATE;
BEGIN
    p_cod_abastecimento := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;
    p_data_inicio_txt := NULL;
    p_data_fim_txt := NULL;

    PRC_PRE_REG_ABASTECIMENTO(
        p_codigo_rh,
        p_codigo_pd,
        p_volume_abastecido,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    PRC_VALIDAR_RECURSO_DIST(
        p_codigo_rh,
        v_recurso_existe,
        v_vazao,
        v_remote_msg
    );

    IF v_recurso_existe = 0 THEN
        p_mensagem := v_remote_msg;
        RETURN;
    END IF;

    v_data_inicio := SYSDATE;
    v_duracao_horas := ROUND(p_volume_abastecido / v_vazao, 6);
    v_data_fim := v_data_inicio + ((p_volume_abastecido / v_vazao) / 24);

    INSERT INTO Historico_Abastecimento (
        codigo_pd,
        codigo_rh,
        volume_abastecido,
        data_inicio,
        data_fim,
        duracao_horas,
        estado_abastecimento
    ) VALUES (
        p_codigo_pd,
        p_codigo_rh,
        p_volume_abastecido,
        v_data_inicio,
        v_data_fim,
        v_duracao_horas,
        'Em curso'
    )
    RETURNING cod_abastecimento INTO p_cod_abastecimento;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Abastecimento registado com sucesso. O volume do ponto sera actualizado automaticamente quando terminar.';
    p_data_inicio_txt := TO_CHAR(v_data_inicio, 'YYYY-MM-DD HH24:MI:SS');
    p_data_fim_txt := TO_CHAR(v_data_fim, 'YYYY-MM-DD HH24:MI:SS');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_cod_abastecimento := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
        p_data_inicio_txt := NULL;
        p_data_fim_txt := NULL;
END;
/

CREATE OR REPLACE PROCEDURE PRC_FINALIZAR_ABAST (
    p_finalizados       OUT NUMBER,
    p_pode_continuar    OUT NUMBER,
    p_mensagem          OUT VARCHAR2
)
IS
    CURSOR c_abastecimentos IS
        SELECT cod_abastecimento,
               codigo_pd,
               volume_abastecido
          FROM Historico_Abastecimento
         WHERE estado_abastecimento = 'Em curso'
           AND data_fim IS NOT NULL
           AND data_fim <= SYSDATE
         FOR UPDATE OF estado_abastecimento;
BEGIN
    p_finalizados := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    FOR r IN c_abastecimentos LOOP
        UPDATE Ponto_Distribuicao
           SET volume_actual_pd = NVL(volume_actual_pd, 0) + r.volume_abastecido
         WHERE codigo_pd = r.codigo_pd;

        UPDATE Historico_Abastecimento
           SET estado_abastecimento = 'Concluido'
         WHERE CURRENT OF c_abastecimentos;

        p_finalizados := p_finalizados + 1;
    END LOOP;

    COMMIT;
    p_pode_continuar := 1;
    p_mensagem := 'Finalizacao de abastecimentos concluida.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_finalizados := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_CANCELAR_ABAST (
    p_cod_abastecimento  IN NUMBER,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_cod_abastecimento IS NULL THEN
        p_mensagem := 'Codigo do abastecimento e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Historico_Abastecimento
     WHERE cod_abastecimento = p_cod_abastecimento;

    IF v_count = 0 THEN
        p_mensagem := 'Abastecimento nao encontrado.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Historico_Abastecimento
     WHERE cod_abastecimento = p_cod_abastecimento
       AND (
            estado_abastecimento = 'Em curso' AND data_fim IS NOT NULL AND data_fim > SYSDATE
       );

    IF v_count = 0 THEN
        p_mensagem := 'Apenas abastecimentos em curso nao expirados podem ser cancelados.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Abastecimento valido para cancelamento.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_CANCELAR_ABAST (
    p_cod_abastecimento    IN NUMBER,
    p_volume_entregue      OUT NUMBER,
    p_pode_continuar       OUT NUMBER,
    p_mensagem             OUT VARCHAR2
)
IS
    v_codigo_pd Historico_Abastecimento.codigo_pd%TYPE;
    v_codigo_rh Historico_Abastecimento.codigo_rh%TYPE;
    v_volume_previsto Historico_Abastecimento.volume_abastecido%TYPE;
    v_data_inicio Historico_Abastecimento.data_inicio%TYPE;
    v_data_fim Historico_Abastecimento.data_fim%TYPE;
    v_estado Historico_Abastecimento.estado_abastecimento%TYPE;
    v_tempo_restante NUMBER;
    v_vazao NUMBER;
    v_recurso_existe NUMBER;
    v_remote_msg VARCHAR2(4000);
    v_tempo_decorrido NUMBER;
    v_duracao_total NUMBER;
    v_duracao_real NUMBER(10,6);
BEGIN
    p_volume_entregue := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_CANCELAR_ABAST(p_cod_abastecimento, p_pode_continuar, p_mensagem);

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    SELECT codigo_pd,
           codigo_rh,
           volume_abastecido,
           data_inicio,
           data_fim,
           duracao_horas,
           estado_abastecimento
      INTO v_codigo_pd,
           v_codigo_rh,
           v_volume_previsto,
           v_data_inicio,
           v_data_fim,
           v_tempo_restante,
           v_estado
      FROM Historico_Abastecimento
     WHERE cod_abastecimento = p_cod_abastecimento
       AND estado_abastecimento = 'Em curso'
     FOR UPDATE;

    IF v_estado = 'Em curso' AND (v_data_fim IS NULL OR v_data_fim <= SYSDATE) THEN
        p_pode_continuar := 0;
        p_mensagem := 'Este abastecimento ja atingiu a data prevista de fim e deve ser finalizado, nao cancelado.';
        ROLLBACK;
        RETURN;
    END IF;

    PRC_VALIDAR_RECURSO_DIST(
        v_codigo_rh,
        v_recurso_existe,
        v_vazao,
        v_remote_msg
    );

    IF v_recurso_existe = 0 THEN
        p_pode_continuar := 0;
        p_mensagem := v_remote_msg;
        ROLLBACK;
        RETURN;
    END IF;

    v_tempo_decorrido := GREATEST((SYSDATE - v_data_inicio) * 24, 0);
    p_volume_entregue := LEAST(v_volume_previsto, ROUND(v_vazao * v_tempo_decorrido, 2));
    v_duracao_real := ROUND(v_tempo_decorrido, 6);

    UPDATE Ponto_Distribuicao
       SET volume_actual_pd = NVL(volume_actual_pd, 0) + p_volume_entregue
     WHERE codigo_pd = v_codigo_pd;

    UPDATE Historico_Abastecimento
       SET volume_abastecido = p_volume_entregue,
           duracao_horas = v_duracao_real,
           data_fim = SYSDATE,
           estado_abastecimento = 'Cancelado'
     WHERE cod_abastecimento = p_cod_abastecimento;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Abastecimento cancelado com sucesso. O volume entregue ate ao cancelamento foi adicionado ao ponto.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_volume_entregue := 0;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_FINALIZAR_ABAST_JOB
IS
    v_finalizados NUMBER;
    v_pode_continuar NUMBER;
    v_mensagem VARCHAR2(4000);
BEGIN
    PRC_FINALIZAR_ABAST(v_finalizados, v_pode_continuar, v_mensagem);
END;
/

CREATE OR REPLACE PROCEDURE PRC_REFRESH_DADOS_RET_AGUA
IS
BEGIN
    DBMS_MVIEW.REFRESH('MV_DADOS_RETIRADA_AGUA', 'C');
    EXECUTE IMMEDIATE 'ALTER MATERIALIZED VIEW MV_DADOS_RETIRADA_AGUA COMPILE';
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20100, 'Nao foi possivel actualizar os dados locais de retirada de agua: ' || SQLERRM);
END;
/

CREATE OR REPLACE PROCEDURE PRC_VALIDAR_RETIRADA_LOCAL (
    p_codigo_pd             IN NUMBER,
    p_codigo_fb             IN NUMBER,
    p_codigo_cota           IN NUMBER,
    p_volume_a_retirar      IN NUMBER,
    p_estado_pd             OUT VARCHAR2,
    p_volume_actual_pd      OUT NUMBER,
    p_saldo_cota            OUT NUMBER,
    p_validade_cota         OUT DATE,
    p_estado_familia        OUT VARCHAR2,
    p_pode_continuar        OUT NUMBER,
    p_mensagem              OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_status_validade MV_DADOS_RETIRADA_AGUA.status_validade%TYPE;
BEGIN
    p_estado_pd := NULL;
    p_volume_actual_pd := NULL;
    p_saldo_cota := NULL;
    p_validade_cota := NULL;
    p_estado_familia := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_volume_a_retirar IS NULL OR p_volume_a_retirar <= 0 THEN
        p_mensagem := 'Informe um volume a retirar maior que zero.';
        RETURN;
    END IF;

    BEGIN
        SELECT estado_operacional_pd,
               volume_actual_pd
          INTO p_estado_pd,
               p_volume_actual_pd
          FROM Ponto_Distribuicao
         WHERE codigo_pd = p_codigo_pd;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Ponto de distribuicao nao encontrado.';
            RETURN;
    END;

    IF p_volume_actual_pd IS NULL THEN
        p_mensagem := 'Volume actual do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF p_estado_pd IS NULL THEN
        p_mensagem := 'Estado operacional do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF UPPER(TRIM(p_estado_pd)) NOT IN ('ACTIVO', 'OPERACIONAL') THEN
        p_mensagem := 'Nao e possivel retirar agua de um ponto de distribuicao inactivo ou em manutencao.';
        RETURN;
    END IF;

    IF p_volume_a_retirar > p_volume_actual_pd THEN
        p_mensagem := 'O ponto de distribuicao nao possui volume suficiente.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM MV_DADOS_RETIRADA_AGUA
     WHERE codigo_fb = p_codigo_fb;

    IF v_count = 0 THEN
        p_mensagem := 'Familia beneficiaria nao encontrada nos dados locais.';
        RETURN;
    END IF;

    BEGIN
        SELECT saldo_disponivel_ca,
               periodo_validade_ca,
               estado_fb,
               status_validade
          INTO p_saldo_cota,
               p_validade_cota,
               p_estado_familia,
               v_status_validade
          FROM MV_DADOS_RETIRADA_AGUA
         WHERE codigo_fb = p_codigo_fb
           AND codigo_cota = p_codigo_cota
           AND codigo_pd = p_codigo_pd
           AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Cota de agua nao encontrada para a familia e ponto indicados nos dados locais.';
            RETURN;
    END;

    IF p_estado_familia IS NULL OR UPPER(TRIM(p_estado_familia)) <> 'ACTIVO' THEN
        p_mensagem := 'Familia beneficiaria inactiva. A retirada de agua nao pode continuar.';
        RETURN;
    END IF;

    IF p_validade_cota IS NULL THEN
        p_mensagem := 'Validade da cota de agua esta indefinida.';
        RETURN;
    END IF;

    IF UPPER(TRIM(NVL(v_status_validade, ''))) <> 'VALIDA' OR p_validade_cota < TRUNC(SYSDATE) THEN
        p_mensagem := 'A cota de agua nao esta valida.';
        RETURN;
    END IF;

    IF p_saldo_cota IS NULL THEN
        p_mensagem := 'Saldo disponivel da cota esta indefinido.';
        RETURN;
    END IF;

    IF p_volume_a_retirar > p_saldo_cota THEN
        p_mensagem := 'O volume a retirar ultrapassa o saldo disponivel da cota.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Retirada_Agua_Pendente
     WHERE codigo_fb = p_codigo_fb
       AND codigo_cota = p_codigo_cota
       AND estado_pendente = 'PENDENTE';

    IF v_count > 0 THEN
        p_mensagem := 'Ja existe uma retirada pendente para esta familia e cota.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validados para continuar.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_RETIRAR_AGUA_PENDENTE (
    p_nome_pessoa_coleta IN VARCHAR2,
    p_volume_a_retirar   IN NUMBER,
    p_observacao         IN VARCHAR2,
    p_codigo_pd          IN NUMBER,
    p_codigo_fb          IN NUMBER,
    p_codigo_cota        IN NUMBER,
    p_codigo_pendente    OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_estado_pd VARCHAR2(20);
    v_volume_actual_pd NUMBER;
    v_saldo_cota NUMBER;
    v_validade_cota DATE;
    v_estado_familia VARCHAR2(20);
    v_pode_continuar NUMBER;
    v_mensagem VARCHAR2(4000);
BEGIN
    p_codigo_pendente := NULL;
    p_mensagem := NULL;

    IF p_nome_pessoa_coleta IS NULL OR TRIM(p_nome_pessoa_coleta) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20101, 'O nome da pessoa que colecta e obrigatorio.');
    END IF;

    PRC_VALIDAR_RETIRADA_LOCAL(
        p_codigo_pd,
        p_codigo_fb,
        p_codigo_cota,
        p_volume_a_retirar,
        v_estado_pd,
        v_volume_actual_pd,
        v_saldo_cota,
        v_validade_cota,
        v_estado_familia,
        v_pode_continuar,
        v_mensagem
    );

    IF v_pode_continuar <> 1 THEN
        RAISE_APPLICATION_ERROR(-20102, v_mensagem);
    END IF;

    SELECT SEQ_RETIRADA_AGUA_PENDENTE.NEXTVAL
      INTO p_codigo_pendente
      FROM DUAL;

    INSERT INTO Retirada_Agua_Pendente (
        cod_retirada_pendente,
        codigo_fb,
        codigo_cota,
        codigo_pd,
        volume_retirado,
        pessoa_coleta,
        observacao,
        data_pedido,
        saldo_cota_snapshot,
        periodo_validade_snapshot,
        estado_familia_snapshot,
        estado_pendente
    ) VALUES (
        p_codigo_pendente,
        p_codigo_fb,
        p_codigo_cota,
        p_codigo_pd,
        p_volume_a_retirar,
        p_nome_pessoa_coleta,
        p_observacao,
        SYSTIMESTAMP,
        v_saldo_cota,
        v_validade_cota,
        v_estado_familia,
        'PENDENTE'
    );

    COMMIT;
    p_mensagem := 'PENDENTE: A operacao foi registada como pendente e sera processada posteriormente.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_pendente := NULL;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_VALIDAR_RETIRADA_AGUA (
    p_codigo_pd             IN NUMBER,
    p_codigo_fb             IN NUMBER,
    p_codigo_cota           IN NUMBER,
    p_volume_a_retirar      IN NUMBER,
    p_estado_pd             OUT VARCHAR2,
    p_volume_actual_pd      OUT NUMBER,
    p_saldo_cota            OUT NUMBER,
    p_validade_cota         OUT DATE,
    p_pode_continuar        OUT NUMBER,
    p_mensagem              OUT VARCHAR2
)
IS
    v_teste                 NUMBER;
    v_fam_online            NUMBER := 1;
    v_fam_pode_continuar    NUMBER;
    v_fam_mensagem          VARCHAR2(4000);
    v_estado_familia        VARCHAR2(20);
BEGIN
    p_estado_pd := NULL;
    p_volume_actual_pd := NULL;
    p_saldo_cota := NULL;
    p_validade_cota := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    BEGIN
        SELECT 1 INTO v_teste FROM VW_TESTE_CONEXAO_FAM_COTAS;
    EXCEPTION
        WHEN OTHERS THEN
            v_fam_online := 0;
    END;

    IF v_fam_online = 0 THEN
        PRC_VALIDAR_RETIRADA_LOCAL(
            p_codigo_pd,
            p_codigo_fb,
            p_codigo_cota,
            p_volume_a_retirar,
            p_estado_pd,
            p_volume_actual_pd,
            p_saldo_cota,
            p_validade_cota,
            v_estado_familia,
            p_pode_continuar,
            p_mensagem
        );
        RETURN;
    END IF;

    IF p_volume_a_retirar IS NULL OR p_volume_a_retirar <= 0 THEN
        p_mensagem := 'Informe um volume a retirar maior que zero.';
        RETURN;
    END IF;

    BEGIN
        SELECT estado_operacional_pd,
               volume_actual_pd
          INTO p_estado_pd,
               p_volume_actual_pd
          FROM Ponto_Distribuicao
         WHERE codigo_pd = p_codigo_pd;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Ponto de distribuicao nao encontrado.';
            RETURN;
    END;

    IF p_volume_actual_pd IS NULL THEN
        p_mensagem := 'Volume actual do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF p_estado_pd IS NULL THEN
        p_mensagem := 'Estado operacional do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF UPPER(TRIM(p_estado_pd)) NOT IN ('ACTIVO', 'OPERACIONAL') THEN
        p_mensagem := 'Nao e possivel retirar agua de um ponto de distribuicao inactivo ou em manutencao.';
        RETURN;
    END IF;

    IF p_volume_a_retirar > p_volume_actual_pd THEN
        p_mensagem := 'O ponto de distribuicao nao possui volume suficiente.';
        RETURN;
    END IF;

    PRC_PRE_VALIDAR_COTA_RET(
        p_codigo_pd,
        p_codigo_fb,
        p_codigo_cota,
        p_volume_a_retirar,
        p_saldo_cota,
        p_validade_cota,
        v_fam_pode_continuar,
        v_fam_mensagem
    );

    IF v_fam_pode_continuar <> 1 THEN
        p_mensagem := v_fam_mensagem;
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para continuar.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_RETIRAR_AGUA (
    p_nome_pessoa_coleta   IN VARCHAR2,
    p_volume_a_retirar     IN NUMBER,
    p_observacao           IN VARCHAR2,
    p_codigo_pd            IN NUMBER,
    p_codigo_fb            IN NUMBER,
    p_codigo_cota          IN NUMBER,
    p_codigo_rc            OUT NUMBER,
    p_mensagem             OUT VARCHAR2
)
IS
    v_volume_actual_pd     Ponto_Distribuicao.volume_actual_pd%TYPE;
    v_estado_pd            Ponto_Distribuicao.estado_operacional_pd%TYPE;
    v_novo_saldo_cota      NUMBER;
    v_novo_volume_pd       NUMBER;
    v_fam_pode_continuar   NUMBER;
    v_fam_mensagem         VARCHAR2(4000);
    v_teste                NUMBER;
    v_fam_online           NUMBER := 1;
    v_codigo_pendente      NUMBER;
BEGIN
    p_codigo_rc := NULL;
    p_mensagem := NULL;

    BEGIN
        SELECT 1 INTO v_teste FROM VW_TESTE_CONEXAO_FAM_COTAS;
    EXCEPTION
        WHEN OTHERS THEN
            v_fam_online := 0;
    END;

    IF v_fam_online = 0 THEN
        PRC_RETIRAR_AGUA_PENDENTE(
            p_nome_pessoa_coleta,
            p_volume_a_retirar,
            p_observacao,
            p_codigo_pd,
            p_codigo_fb,
            p_codigo_cota,
            v_codigo_pendente,
            p_mensagem
        );

        p_codigo_rc := NULL;

        IF v_codigo_pendente IS NOT NULL THEN
            p_mensagem := p_mensagem || ' Codigo da pendencia: ' || v_codigo_pendente;
        END IF;

        RETURN;
    END IF;

    IF p_nome_pessoa_coleta IS NULL OR TRIM(p_nome_pessoa_coleta) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'O nome da pessoa que colecta e obrigatorio.');
    END IF;

    IF p_volume_a_retirar IS NULL OR p_volume_a_retirar <= 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'O volume a retirar deve ser maior que zero.');
    END IF;

    BEGIN
        SELECT volume_actual_pd,
               estado_operacional_pd
          INTO v_volume_actual_pd,
               v_estado_pd
          FROM Ponto_Distribuicao
         WHERE codigo_pd = p_codigo_pd
         FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20003, 'Ponto de distribuicao nao encontrado.');
    END;

    IF v_volume_actual_pd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'Volume actual do ponto de distribuicao esta indefinido.');
    END IF;

    IF v_estado_pd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20011, 'Estado operacional do ponto de distribuicao esta indefinido.');
    END IF;

    IF UPPER(TRIM(v_estado_pd)) NOT IN ('ACTIVO', 'OPERACIONAL') THEN
        RAISE_APPLICATION_ERROR(-20012, 'Nao e possivel retirar agua de um ponto de distribuicao inactivo ou em manutencao.');
    END IF;

    IF p_volume_a_retirar > v_volume_actual_pd THEN
        RAISE_APPLICATION_ERROR(-20013, 'O ponto de distribuicao nao possui volume suficiente.');
    END IF;

    PRC_DEBITAR_COTA_RETIRADA(
        p_codigo_pd,
        p_codigo_fb,
        p_codigo_cota,
        p_volume_a_retirar,
        v_novo_saldo_cota,
        v_fam_pode_continuar,
        v_fam_mensagem
    );

    IF v_fam_pode_continuar <> 1 THEN
        RAISE_APPLICATION_ERROR(-20014, v_fam_mensagem);
    END IF;

    v_novo_volume_pd := v_volume_actual_pd - p_volume_a_retirar;

    SELECT SEQ_REGISTRO_CONSUMO.NEXTVAL
      INTO p_codigo_rc
      FROM DUAL;

    INSERT INTO Registro_Consumo (
        codigo_rc,
        codigo_fb,
        codigo_pd,
        codigo_cota,
        data_hora_rc,
        volume_retirado_rc,
        pessoa_coleta_rc,
        metodo_autenticacao_rc,
        saldo_cota_rc,
        observacao_rc
    ) VALUES (
        p_codigo_rc,
        p_codigo_fb,
        p_codigo_pd,
        p_codigo_cota,
        SYSTIMESTAMP,
        p_volume_a_retirar,
        p_nome_pessoa_coleta,
        'MANUAL',
        v_novo_saldo_cota,
        p_observacao
    );

    UPDATE Ponto_Distribuicao
       SET volume_actual_pd = v_novo_volume_pd
     WHERE codigo_pd = p_codigo_pd;

    COMMIT;

    p_mensagem := 'SUCESSO: Retirada de agua registada. Novo saldo da cota: '
        || v_novo_saldo_cota || ' | Novo volume do ponto: ' || v_novo_volume_pd;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_rc := NULL;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PROC_RETIRADAS_PEND
IS
    v_teste NUMBER;
    v_online NUMBER := 1;
    v_saldo_actual NUMBER;
    v_validade_actual DATE;
    v_volume_actual_pd NUMBER;
    v_estado_pd VARCHAR2(20);
    v_novo_saldo NUMBER;
    v_pode_continuar NUMBER;
    v_msg VARCHAR2(4000);
    v_codigo_rc NUMBER;
    v_motivo VARCHAR2(4000);
    v_erro VARCHAR2(4000);
BEGIN
    BEGIN
        SELECT 1 INTO v_teste FROM VW_TESTE_CONEXAO_FAM_COTAS;
    EXCEPTION
        WHEN OTHERS THEN
            v_online := 0;
    END;

    IF v_online = 0 THEN
        RETURN;
    END IF;

    PRC_REFRESH_DADOS_RET_AGUA;

    FOR r IN (
        SELECT ROWID AS rid,
               cod_retirada_pendente,
               codigo_fb,
               codigo_cota,
               codigo_pd,
               volume_retirado,
               pessoa_coleta,
               observacao,
               saldo_cota_snapshot
          FROM Retirada_Agua_Pendente
         WHERE estado_pendente = 'PENDENTE'
         ORDER BY data_pedido, cod_retirada_pendente
    ) LOOP
        v_motivo := NULL;

        BEGIN
            BEGIN
                SELECT volume_actual_pd,
                       estado_operacional_pd
                  INTO v_volume_actual_pd,
                       v_estado_pd
                  FROM Ponto_Distribuicao
                 WHERE codigo_pd = r.codigo_pd
                 FOR UPDATE;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    v_motivo := 'Ponto de distribuicao ja nao existe.';
            END;

            IF v_motivo IS NULL AND (v_estado_pd IS NULL OR UPPER(TRIM(v_estado_pd)) NOT IN ('ACTIVO', 'OPERACIONAL')) THEN
                v_motivo := 'Ponto de distribuicao ja nao esta activo.';
            END IF;

            IF v_motivo IS NULL AND r.volume_retirado > v_volume_actual_pd THEN
                v_motivo := 'Ponto de distribuicao nao possui volume suficiente.';
            END IF;

            IF v_motivo IS NULL THEN
                PRC_PRE_VALIDAR_COTA_RET(
                    r.codigo_pd,
                    r.codigo_fb,
                    r.codigo_cota,
                    r.volume_retirado,
                    v_saldo_actual,
                    v_validade_actual,
                    v_pode_continuar,
                    v_msg
                );

                IF v_pode_continuar <> 1 THEN
                    v_motivo := v_msg;
                END IF;
            END IF;

            IF v_motivo IS NULL AND NVL(v_saldo_actual, -1) <> NVL(r.saldo_cota_snapshot, -1) THEN
                v_motivo := 'Saldo actual da cota mudou desde o pedido pendente.';
            END IF;

            IF v_motivo IS NULL THEN
                PRC_DEBITAR_COTA_RETIRADA(
                    r.codigo_pd,
                    r.codigo_fb,
                    r.codigo_cota,
                    r.volume_retirado,
                    v_novo_saldo,
                    v_pode_continuar,
                    v_msg
                );

                IF v_pode_continuar <> 1 THEN
                    v_motivo := v_msg;
                END IF;
            END IF;

            IF v_motivo IS NULL THEN
                SELECT SEQ_REGISTRO_CONSUMO.NEXTVAL
                  INTO v_codigo_rc
                  FROM DUAL;

                INSERT INTO Registro_Consumo (
                    codigo_rc,
                    codigo_fb,
                    codigo_pd,
                    codigo_cota,
                    data_hora_rc,
                    volume_retirado_rc,
                    pessoa_coleta_rc,
                    metodo_autenticacao_rc,
                    saldo_cota_rc,
                    observacao_rc
                ) VALUES (
                    v_codigo_rc,
                    r.codigo_fb,
                    r.codigo_pd,
                    r.codigo_cota,
                    SYSTIMESTAMP,
                    r.volume_retirado,
                    r.pessoa_coleta,
                    'MANUAL',
                    v_novo_saldo,
                    r.observacao
                );

                UPDATE Ponto_Distribuicao
                   SET volume_actual_pd = volume_actual_pd - r.volume_retirado
                 WHERE codigo_pd = r.codigo_pd;

                UPDATE Retirada_Agua_Pendente
                   SET estado_pendente = 'APROVADA',
                       data_processamento = SYSTIMESTAMP,
                       mensagem_processamento = 'Aprovada. Registro de consumo criado: ' || v_codigo_rc
                 WHERE ROWID = r.rid
                   AND estado_pendente = 'PENDENTE';
            ELSE
                UPDATE Retirada_Agua_Pendente
                   SET estado_pendente = 'REJEITADA',
                       data_processamento = SYSTIMESTAMP,
                       mensagem_processamento = v_motivo
                 WHERE ROWID = r.rid
                   AND estado_pendente = 'PENDENTE';
            END IF;

            COMMIT;
        EXCEPTION
            WHEN OTHERS THEN
                v_erro := SQLERRM;
                ROLLBACK;
                UPDATE Retirada_Agua_Pendente
                   SET estado_pendente = 'REJEITADA',
                       data_processamento = SYSTIMESTAMP,
                       mensagem_processamento = 'Erro ao processar pendencia: ' || v_erro
                 WHERE cod_retirada_pendente = r.cod_retirada_pendente
                   AND estado_pendente = 'PENDENTE';
                COMMIT;
        END;
    END LOOP;

    PRC_REFRESH_DADOS_RET_AGUA;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
END;
/

ALTER PROCEDURE PRC_VALIDAR_RETIRADA_LOCAL COMPILE;

ALTER PROCEDURE PRC_RETIRAR_AGUA_PENDENTE COMPILE;

ALTER PROCEDURE PRC_PRE_VALIDAR_RETIRADA_AGUA COMPILE;

ALTER PROCEDURE PRC_RETIRAR_AGUA COMPILE;

ALTER PROCEDURE PRC_PROC_RETIRADAS_PEND COMPILE;
