SET ECHO OFF;

-- Referencia historica. Nao entra no fluxo principal de instalacao.

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
    v_saldo_cota           Cota_Agua.saldo_disponivel_ca%TYPE;
    v_validade_cota        Cota_Agua.periodo_validade_ca%TYPE;
    v_volume_actual_pd     Ponto_Distribuicao.volume_actual_pd%TYPE;
    v_estado_pd            Ponto_Distribuicao.estado_operacional_pd%TYPE;
    v_novo_saldo_cota      NUMBER;
    v_novo_volume_pd       NUMBER;
    v_count                NUMBER;
BEGIN
    p_codigo_rc := NULL;
    p_mensagem := NULL;

    IF p_nome_pessoa_coleta IS NULL OR TRIM(p_nome_pessoa_coleta) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'O nome da pessoa que colecta e obrigatorio.');
    END IF;

    IF p_volume_a_retirar IS NULL OR p_volume_a_retirar <= 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'O volume a retirar deve ser maior que zero.');
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Ponto de distribuicao nao encontrado.');
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_fb;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20014, 'Familia beneficiaria nao encontrada.');
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_fb
       AND codigo_pd = p_codigo_pd;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'A familia nao esta associada ao ponto de distribuicao indicado.');
    END IF;

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
            RAISE_APPLICATION_ERROR(-20005, 'Cota de agua nao encontrada para a familia indicada.');
    END;

    IF v_saldo_cota IS NULL THEN
        RAISE_APPLICATION_ERROR(-20006, 'Saldo disponivel da cota esta indefinido.');
    END IF;

    IF v_validade_cota IS NULL THEN
        RAISE_APPLICATION_ERROR(-20007, 'Validade da cota de agua esta indefinida.');
    END IF;

        RAISE_APPLICATION_ERROR(-20008, 'A cota de agua esta expirada.');
    END IF;

        RAISE_APPLICATION_ERROR(-20009, 'O volume a retirar ultrapassa o saldo disponivel da cota.');
    END IF;

    SELECT volume_actual_pd,
           estado_operacional_pd
      INTO v_volume_actual_pd,
           v_estado_pd
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd
     FOR UPDATE;

    IF v_volume_actual_pd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'Volume actual do ponto de distribuicao esta indefinido.');
    END IF;

    IF v_estado_pd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20011, 'Estado operacional do ponto de distribuicao esta indefinido.');
    END IF;

    IF UPPER(TRIM(v_estado_pd)) NOT IN ('ACTIVO', 'ATIVO', 'OPERACIONAL') THEN
        RAISE_APPLICATION_ERROR(-20012, 'Nao e possivel retirar agua de um ponto de distribuicao inactivo ou em manutencao.');
    END IF;

        RAISE_APPLICATION_ERROR(-20013, 'O ponto de distribuicao nao possui volume suficiente.');
    END IF;

    v_novo_saldo_cota := v_saldo_cota - p_volume_a_retirar;
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

    UPDATE Cota_Agua
       SET saldo_disponivel_ca = v_novo_saldo_cota
     WHERE codigo_cota = p_codigo_cota
       AND codigo_fb = p_codigo_fb;

    UPDATE Ponto_Distribuicao
       SET volume_actual_pd = v_novo_volume_pd
     WHERE codigo_pd = p_codigo_pd;

    COMMIT;

    p_mensagem :=
        'SUCESSO: Retirada de agua registada. Novo saldo da cota: '
        || v_novo_saldo_cota ||
        ' | Novo volume do ponto: ' || v_novo_volume_pd;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_rc := NULL;
        p_mensagem := 'ERRO: ' || SQLERRM;
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
    v_count                 NUMBER;
BEGIN
    p_estado_pd := NULL;
    p_volume_actual_pd := NULL;
    p_saldo_cota := NULL;
    p_validade_cota := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_volume_a_retirar IS NULL OR p_volume_a_retirar <= 0 THEN
        p_mensagem := 'O volume a retirar deve ser maior que zero.';
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
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_fb;

    IF v_count = 0 THEN
        p_mensagem := 'Familia beneficiaria nao encontrada.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Cota_Agua
     WHERE codigo_cota = p_codigo_cota
       AND codigo_fb = p_codigo_fb;

    IF v_count = 0 THEN
        p_mensagem := 'Cota de agua nao encontrada para a familia indicada.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_fb
       AND codigo_pd = p_codigo_pd;

    IF v_count = 0 THEN
        p_mensagem := 'A familia nao esta associada ao ponto de distribuicao indicado.';
        RETURN;
    END IF;

    SELECT pd.estado_operacional_pd,
           pd.volume_actual_pd,
           ca.saldo_disponivel_ca,
           ca.periodo_validade_ca
      INTO p_estado_pd,
           p_volume_actual_pd,
           p_saldo_cota,
           p_validade_cota
      FROM Ponto_Distribuicao pd
      JOIN Familia_Beneficiaria fb
        ON fb.codigo_pd = pd.codigo_pd
      JOIN Cota_Agua ca
        ON ca.codigo_fb = fb.codigo_fb
     WHERE pd.codigo_pd = p_codigo_pd
       AND fb.codigo_fb = p_codigo_fb
       AND ca.codigo_cota = p_codigo_cota;

    IF p_volume_actual_pd IS NULL THEN
        p_mensagem := 'Volume actual do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF p_estado_pd IS NULL THEN
        p_mensagem := 'Estado operacional do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF UPPER(TRIM(p_estado_pd)) NOT IN ('ACTIVO', 'ATIVO', 'OPERACIONAL') THEN
        p_mensagem := 'Nao e possivel retirar agua de um ponto de distribuicao inactivo ou em manutencao.';
        RETURN;
    END IF;

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

    IF p_volume_a_retirar > p_volume_actual_pd THEN
        p_mensagem := 'O ponto de distribuicao nao possui volume suficiente.';
        RETURN;
    END IF;

    IF p_volume_a_retirar > p_saldo_cota THEN
        p_mensagem := 'O volume a retirar ultrapassa o saldo disponivel da cota.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados actuais validos para continuar.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
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
        p_mensagem := 'Codigo do ponto de distribuicao e obrigatorio.';
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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
    v_pausados NUMBER := 0;
    v_retomados NUMBER := 0;
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
           SET duracao_horas = GREATEST(duracao_horas - ((SYSDATE - data_inicio) * 24), 0.000001),
               data_fim = NULL,
               estado_abastecimento = 'Pausado'
         WHERE codigo_pd = p_codigo_pd
           AND estado_abastecimento = 'Em curso';

        v_pausados := SQL%ROWCOUNT;
    ELSIF v_estado_normalizado = 'ACTIVO' THEN
        UPDATE Historico_Abastecimento
           SET data_inicio = SYSDATE,
               data_fim = SYSDATE + (duracao_horas / 24),
               estado_abastecimento = 'Em curso'
         WHERE codigo_pd = p_codigo_pd
           AND estado_abastecimento = 'Pausado';

        v_retomados := SQL%ROWCOUNT;
    END IF;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Estado operacional actualizado com sucesso para ' ||
                  CASE
                    WHEN v_estado_normalizado = 'ACTIVO' THEN 'Activo'
                    WHEN v_estado_normalizado = 'INACTIVO' THEN 'Inactivo'
                    ELSE 'Em Manutencao'
                  END;
    IF v_pausados > 0 THEN
        p_mensagem := p_mensagem || '. Abastecimentos pausados: ' || v_pausados;
    END IF;
    IF v_retomados > 0 THEN
        p_mensagem := p_mensagem || '. Abastecimentos retomados: ' || v_retomados;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
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
BEGIN
    p_total_pontos := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigos_pontos IS NULL OR TRIM(p_codigos_pontos) IS NULL THEN
        p_mensagem := 'Selecione pelo menos um ponto de distribuicao.';
        RETURN;
    END IF;

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
           AND estado_abastecimento IN ('Em curso', 'Pausado');

        IF v_count > 0 THEN
            p_mensagem := 'O ponto de distribuicao ' || v_codigo_pd ||
                          ' possui abastecimento em curso ou pausado. Finalize ou cancele o abastecimento antes de alterar o recurso hidrico.';
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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
        p_mensagem := 'ERRO: ' || SQLERRM;
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

    SELECT COUNT(*)
      INTO v_count
      FROM Comite
     WHERE cod_comite_responsavel = p_codigo_comite;

    IF v_count = 0 THEN
        p_mensagem := 'Comite nao encontrado.';
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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
BEGIN
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

    IF p_cod_comite IS NULL THEN
        p_mensagem := 'Comite responsavel e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Comite
     WHERE cod_comite_responsavel = p_cod_comite;

    IF v_count = 0 THEN
        p_mensagem := 'Comite responsavel nao encontrado.';
        RETURN;
    END IF;

    IF p_equipe_id IS NULL THEN
        p_mensagem := 'Equipe tecnica e obrigatoria.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Tecnico_Manutencao
     WHERE equipe_id = p_equipe_id;

    IF v_count = 0 THEN
        p_mensagem := 'A equipe selecionada deve ser do tipo Tecnico de Manutencao.';
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

    IF v_estado_norm NOT IN ('ACTIVO', 'ATIVO', 'INACTIVO', 'EM MANUTENCAO') THEN
        p_mensagem := 'Estado operacional invalido.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para registar ponto de distribuicao.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
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
            WHEN UPPER(TRIM(p_estado_operacional)) = 'ATIVO' THEN 'Activo'
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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
BEGIN
    p_equipe_id := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_pd IS NULL THEN
        p_mensagem := 'Codigo do ponto de distribuicao e obrigatorio.';
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

    SELECT COUNT(*)
      INTO v_count
      FROM VW_TECNICO_MANUTENCAO
     WHERE equipe_id = p_equipe_id;

    IF v_count = 0 THEN
        p_mensagem := 'A equipe associada ao ponto deve ser Tecnico de Manutencao.';
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

    v_alterar_estado := UPPER(TRIM(NVL(p_alterar_estado, 'Nao')));

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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
    v_equipe_id Equipe_Tecnica.equipe_id%TYPE;
    v_alterar_estado VARCHAR2(10);
    v_novo_estado VARCHAR2(30);
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

    v_alterar_estado := UPPER(TRIM(NVL(p_alterar_estado, 'Nao')));

    IF v_alterar_estado = 'SIM' THEN
        v_novo_estado := UPPER(TRIM(p_novo_estado));

        UPDATE Ponto_Distribuicao
           SET estado_operacional_pd = CASE
                 WHEN v_novo_estado = 'INACTIVO' THEN 'Inactivo'
                 ELSE 'Em Manutencao'
               END
         WHERE codigo_pd = p_codigo_pd;
    END IF;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Manutencao registada com sucesso.';
    IF v_alterar_estado = 'SIM' THEN
        p_mensagem := p_mensagem || ' Estado do ponto actualizado.';
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_cod_historico := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
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
    v_vazao Recurso_Hidrico.volume_rh%TYPE;
    v_capacidade Ponto_Distribuicao.capacidade_armazenamento_pd%TYPE;
    v_volume_actual Ponto_Distribuicao.volume_actual_pd%TYPE;
    v_volume_em_curso NUMBER(12,2);
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_rh IS NULL THEN
        p_mensagem := 'Codigo do recurso hidrico e obrigatorio.';
        RETURN;
    END IF;

    IF p_codigo_pd IS NULL THEN
        p_mensagem := 'Codigo do ponto de distribuicao e obrigatorio.';
        RETURN;
    END IF;

    IF p_volume_abastecido IS NULL OR p_volume_abastecido <= 0 THEN
        p_mensagem := 'Volume abastecido deve ser maior que zero.';
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

    SELECT volume_rh
      INTO v_vazao
      FROM Recurso_Hidrico
     WHERE codigo_rh = p_codigo_rh;

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
       AND estado_abastecimento IN ('Em curso', 'Pausado');

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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
    v_vazao Recurso_Hidrico.volume_rh%TYPE;
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

    SELECT volume_rh
      INTO v_vazao
      FROM Recurso_Hidrico
     WHERE codigo_rh = p_codigo_rh;

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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
            (estado_abastecimento = 'Em curso' AND data_fim IS NOT NULL AND data_fim > SYSDATE)
            OR estado_abastecimento = 'Pausado'
       );

    IF v_count = 0 THEN
        p_mensagem := 'Apenas abastecimentos em curso nao expirados ou pausados podem ser cancelados.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Abastecimento valido para cancelamento.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
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
    v_vazao Recurso_Hidrico.volume_rh%TYPE;
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
       AND estado_abastecimento IN ('Em curso', 'Pausado')
     FOR UPDATE;

    IF v_estado = 'Em curso' AND (v_data_fim IS NULL OR v_data_fim <= SYSDATE) THEN
        p_pode_continuar := 0;
        p_mensagem := 'Este abastecimento ja atingiu a data prevista de fim e deve ser finalizado, nao cancelado.';
        ROLLBACK;
        RETURN;
    END IF;

    SELECT volume_rh
      INTO v_vazao
      FROM Recurso_Hidrico
     WHERE codigo_rh = v_codigo_rh;

    IF v_estado = 'Em curso' THEN
        v_tempo_decorrido := GREATEST((SYSDATE - v_data_inicio) * 24, 0);
        p_volume_entregue := LEAST(v_volume_previsto, ROUND(v_vazao * v_tempo_decorrido, 2));
        v_duracao_real := ROUND(v_tempo_decorrido, 6);
    ELSE
        v_duracao_total := ROUND(v_volume_previsto / v_vazao, 6);
        v_duracao_real := GREATEST(v_duracao_total - NVL(v_tempo_restante, 0), 0);
        p_volume_entregue := LEAST(v_volume_previsto, ROUND(v_vazao * v_duracao_real, 2));
    END IF;

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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
