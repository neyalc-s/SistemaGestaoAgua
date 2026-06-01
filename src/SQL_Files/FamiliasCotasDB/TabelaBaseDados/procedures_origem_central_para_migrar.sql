SET ECHO OFF;

-- Referencia historica. Nao entra no fluxo principal de instalacao.

CREATE OR REPLACE PROCEDURE PRC_INSERIR_FAMILIA (
    p_codigo_pd                 IN NUMBER,
    p_nome_responsavel_fb       IN VARCHAR2,
    p_contacto_fb               IN VARCHAR2,
    p_num_membros_fb            IN NUMBER,
    p_perfil_socioeconomico_fb  IN VARCHAR2,

    p_aldeia                    IN VARCHAR2,
    p_coordenadas_gps           IN VARCHAR2,

    p_ajuste_sazonal_ca         IN VARCHAR2,
    p_transferencia_autorizada  IN VARCHAR2,
    p_volume_semanal_ca         IN NUMBER DEFAULT NULL,

    p_codigo_fb                 OUT NUMBER,
    p_codigo_cota               OUT NUMBER,
    p_codigo_localizacao        OUT NUMBER,
    p_volume_calculado          OUT NUMBER,
    p_mensagem                  OUT VARCHAR2
)
IS
    v_cod_localizacao   Localizacao.cod_localizacao%TYPE;
    v_cod_fb            Familia_Beneficiaria.codigo_fb%TYPE;
    v_cod_cota          Cota_Agua.codigo_cota%TYPE;
    v_volume_semanal    NUMBER(8,2);
    v_validade_cota     DATE;
    v_aut_transf        VARCHAR2(3);
    v_count             NUMBER;
    v_estado_pd         Ponto_Distribuicao.estado_operacional_pd%TYPE;
    v_volume_actual_pd  Ponto_Distribuicao.volume_actual_pd%TYPE;
BEGIN
    p_codigo_fb := NULL;
    p_codigo_cota := NULL;
    p_codigo_localizacao := NULL;
    p_volume_calculado := NULL;
    p_mensagem := NULL;

    IF p_codigo_pd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'Codigo do ponto de distribuicao e obrigatorio.');
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'Ponto de distribuicao nao encontrado.');
    END IF;

    SELECT estado_operacional_pd,
           volume_actual_pd
      INTO v_estado_pd,
           v_volume_actual_pd
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd;

    IF v_estado_pd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20007, 'Estado operacional do ponto de distribuicao esta indefinido.');
    END IF;

    IF UPPER(TRIM(v_estado_pd)) NOT IN ('ACTIVO', 'ATIVO', 'OPERACIONAL') THEN
        RAISE_APPLICATION_ERROR(-20008, 'O ponto de distribuicao nao esta operacional.');
    END IF;

    IF v_volume_actual_pd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20009, 'Volume actual do ponto de distribuicao esta indefinido.');
    END IF;

    IF p_nome_responsavel_fb IS NULL OR TRIM(p_nome_responsavel_fb) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20002, 'Nome do responsavel e obrigatorio.');
    END IF;

    IF p_contacto_fb IS NULL OR TRIM(p_contacto_fb) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20012, 'Contacto e obrigatorio.');
    END IF;

    IF NOT REGEXP_LIKE(TRIM(p_contacto_fb), '^[0-9]+$') THEN
        RAISE_APPLICATION_ERROR(-20012, 'Contacto deve conter apenas numeros.');
    END IF;

    IF TO_NUMBER(TRIM(p_contacto_fb)) < 820000000 OR TO_NUMBER(TRIM(p_contacto_fb)) > 879999999 THEN
        RAISE_APPLICATION_ERROR(-20012, 'Contacto deve estar entre 820000000 e 879999999.');
    END IF;

    IF p_num_membros_fb IS NULL OR p_num_membros_fb <= 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Numero de membros invalido.');
    END IF;

    IF p_perfil_socioeconomico_fb IS NULL OR TRIM(p_perfil_socioeconomico_fb) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20013, 'Perfil socioeconomico e obrigatorio.');
    END IF;

    IF p_aldeia IS NULL OR TRIM(p_aldeia) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20004, 'A aldeia e obrigatoria.');
    END IF;

    IF p_coordenadas_gps IS NULL OR TRIM(p_coordenadas_gps) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'Coordenadas GPS sao obrigatorias.');
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE TRIM(contacto_fb) = TRIM(p_contacto_fb);

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'Ja existe uma familia registada com esse contacto.');
    END IF;

    v_aut_transf := UPPER(TRIM(NVL(p_transferencia_autorizada, 'NAO')));

    IF v_aut_transf NOT IN ('SIM', 'NAO') THEN
        RAISE_APPLICATION_ERROR(-20005, 'Transferencia autorizada deve ser SIM ou NAO.');
    END IF;

    v_volume_semanal := calcular_cota_semanal(p_num_membros_fb);

        v_volume_semanal := p_volume_semanal_ca;
    ELSIF p_volume_semanal_ca IS NOT NULL AND p_volume_semanal_ca <= 0 THEN
        RAISE_APPLICATION_ERROR(-20014, 'Volume semanal opcional deve ser maior que zero.');
    END IF;

    v_validade_cota := NEXT_DAY(TRUNC(SYSDATE) - 1, 'SUNDAY') + (86399 / 86400);

    SELECT SEQ_LOCALIZACAO.NEXTVAL
      INTO v_cod_localizacao
      FROM DUAL;

    INSERT INTO Localizacao (
        cod_localizacao,
        aldeia,
        coordenadas_gps
    ) VALUES (
        v_cod_localizacao,
        p_aldeia,
        p_coordenadas_gps
    );

    SELECT SEQ_FAMILIA_BENEFICIARIA.NEXTVAL
      INTO v_cod_fb
      FROM DUAL;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb,
        codigo_pd,
        cod_localizacao,
        nome_responsavel_fb,
        num_membros_fb,
        perfil_socioeconomico_fb,
        contacto_fb
    ) VALUES (
        v_cod_fb,
        p_codigo_pd,
        v_cod_localizacao,
        p_nome_responsavel_fb,
        p_num_membros_fb,
        p_perfil_socioeconomico_fb,
        p_contacto_fb
    );

    SELECT SEQ_COTA_AGUA.NEXTVAL
      INTO v_cod_cota
      FROM DUAL;

    INSERT INTO Cota_Agua (
        codigo_cota,
        codigo_fb,
        volume_semanal_ca,
        saldo_disponivel_ca,
        periodo_validade_ca,
        ajuste_sazonal_ca,
        transferencia_autorizada_ca
    ) VALUES (
        v_cod_cota,
        v_cod_fb,
        v_volume_semanal,
        v_volume_semanal,
        v_validade_cota,
        p_ajuste_sazonal_ca,
        INITCAP(v_aut_transf)
    );

    COMMIT;

    p_codigo_fb := v_cod_fb;
    p_codigo_cota := v_cod_cota;
    p_codigo_localizacao := v_cod_localizacao;
    p_volume_calculado := v_volume_semanal;
    p_mensagem :=
        'SUCESSO: Familia cadastrada com cota inicial de ' ||
        v_volume_semanal || ' litros.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_fb := NULL;
        p_codigo_cota := NULL;
        p_codigo_localizacao := NULL;
        p_volume_calculado := NULL;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_VALDAR_REGISTAR_FAMIL (
    p_codigo_pd                 IN NUMBER,
    p_nome_responsavel_fb       IN VARCHAR2,
    p_contacto_fb               IN VARCHAR2,
    p_num_membros_fb            IN NUMBER,
    p_perfil_socioeconomico_fb  IN VARCHAR2,
    p_aldeia                    IN VARCHAR2,
    p_coordenadas_gps           IN VARCHAR2,
    p_ajuste_sazonal_ca         IN VARCHAR2,
    p_transferencia_autorizada  IN VARCHAR2,
    p_volume_semanal_ca         IN NUMBER DEFAULT NULL,
    p_codigos_necessidades      IN VARCHAR2 DEFAULT NULL,
    p_estado_pd                 OUT VARCHAR2,
    p_volume_actual_pd          OUT NUMBER,
    p_volume_calculado          OUT NUMBER,
    p_pode_continuar            OUT NUMBER,
    p_mensagem                  OUT VARCHAR2
)
IS
    v_count             NUMBER;
    v_aut_transf        VARCHAR2(3);
    v_posicao           NUMBER := 1;
    v_codigo_texto      VARCHAR2(30);
    v_codigo_necess     NUMBER;
BEGIN
    p_estado_pd := NULL;
    p_volume_actual_pd := NULL;
    p_volume_calculado := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_pd IS NULL THEN
        p_mensagem := 'Codigo do ponto de distribuicao e obrigatorio.';
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

    SELECT estado_operacional_pd,
           volume_actual_pd
      INTO p_estado_pd,
           p_volume_actual_pd
      FROM Ponto_Distribuicao
     WHERE codigo_pd = p_codigo_pd;

    IF p_estado_pd IS NULL THEN
        p_mensagem := 'Estado operacional do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF UPPER(TRIM(p_estado_pd)) NOT IN ('ACTIVO', 'ATIVO', 'OPERACIONAL') THEN
        p_mensagem := 'O ponto de distribuicao nao esta operacional.';
        RETURN;
    END IF;

    IF p_volume_actual_pd IS NULL THEN
        p_mensagem := 'Volume actual do ponto de distribuicao esta indefinido.';
        RETURN;
    END IF;

    IF p_nome_responsavel_fb IS NULL OR TRIM(p_nome_responsavel_fb) IS NULL THEN
        p_mensagem := 'Nome do responsavel e obrigatorio.';
        RETURN;
    END IF;

    IF p_contacto_fb IS NULL OR TRIM(p_contacto_fb) IS NULL THEN
        p_mensagem := 'Contacto e obrigatorio.';
        RETURN;
    END IF;

    IF NOT REGEXP_LIKE(TRIM(p_contacto_fb), '^[0-9]+$') THEN
        p_mensagem := 'Contacto deve conter apenas numeros.';
        RETURN;
    END IF;

    IF TO_NUMBER(TRIM(p_contacto_fb)) < 820000000 OR TO_NUMBER(TRIM(p_contacto_fb)) > 879999999 THEN
        p_mensagem := 'Contacto deve estar entre 820000000 e 879999999.';
        RETURN;
    END IF;

    IF p_num_membros_fb IS NULL OR p_num_membros_fb <= 0 THEN
        p_mensagem := 'Numero de membros invalido.';
        RETURN;
    END IF;

    IF p_perfil_socioeconomico_fb IS NULL OR TRIM(p_perfil_socioeconomico_fb) IS NULL THEN
        p_mensagem := 'Perfil socioeconomico e obrigatorio.';
        RETURN;
    END IF;

    IF p_aldeia IS NULL OR TRIM(p_aldeia) IS NULL THEN
        p_mensagem := 'A aldeia e obrigatoria.';
        RETURN;
    END IF;

    IF p_coordenadas_gps IS NULL OR TRIM(p_coordenadas_gps) IS NULL THEN
        p_mensagem := 'Coordenadas GPS sao obrigatorias.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE TRIM(contacto_fb) = TRIM(p_contacto_fb);

    IF v_count > 0 THEN
        p_mensagem := 'Ja existe uma familia registada com esse contacto.';
        RETURN;
    END IF;

    v_aut_transf := UPPER(TRIM(NVL(p_transferencia_autorizada, 'NAO')));

    IF v_aut_transf NOT IN ('SIM', 'NAO') THEN
        p_mensagem := 'Transferencia autorizada deve ser SIM ou NAO.';
        RETURN;
    END IF;

    p_volume_calculado := calcular_cota_semanal(p_num_membros_fb);

    IF p_volume_semanal_ca IS NOT NULL THEN
        IF p_volume_semanal_ca <= 0 THEN
            p_mensagem := 'Volume semanal opcional deve ser maior que zero.';
            RETURN;
        END IF;

        p_volume_calculado := p_volume_semanal_ca;
    END IF;

    IF p_codigos_necessidades IS NOT NULL AND TRIM(p_codigos_necessidades) IS NOT NULL THEN
        LOOP
            v_codigo_texto := REGEXP_SUBSTR(p_codigos_necessidades, '[^,]+', 1, v_posicao);
            EXIT WHEN v_codigo_texto IS NULL;

            v_codigo_texto := TRIM(v_codigo_texto);

            IF NOT REGEXP_LIKE(v_codigo_texto, '^[0-9]+$') THEN
                p_mensagem := 'Codigo de necessidade invalido: ' || v_codigo_texto;
                RETURN;
            END IF;

            v_codigo_necess := TO_NUMBER(v_codigo_texto);

            SELECT COUNT(*)
              INTO v_count
              FROM Necessidade
             WHERE cod_necessidade = v_codigo_necess;

            IF v_count = 0 THEN
                p_mensagem := 'Necessidade selecionada nao encontrada: ' || v_codigo_necess;
                RETURN;
            END IF;

            v_posicao := v_posicao + 1;
        END LOOP;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados actuais validos para registar familia.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE prc_gerar_cota_semanal (
    p_codigo_fb       IN  NUMBER,
    p_ajuste_sazonal  IN  VARCHAR2,
    p_nova_aut_transf IN  VARCHAR2,
    p_cod_cota_nova   OUT NUMBER,
    p_mensagem        OUT VARCHAR2
)
IS
    v_num_membros         Familia_Beneficiaria.num_membros_fb%TYPE;
    v_volume_semanal      NUMBER;
    v_hoje                DATE;
    v_nova_validade       DATE;
    v_cod_cota_antiga     Cota_Agua.codigo_cota%TYPE;
BEGIN
    p_cod_cota_nova := NULL;
    p_mensagem := NULL;

    v_hoje := TRUNC(SYSDATE);

    BEGIN
        SELECT num_membros_fb
          INTO v_num_membros
          FROM Familia_Beneficiaria
         WHERE codigo_fb = p_codigo_fb;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20001, 'Familia nao encontrada.');
    END;

    v_volume_semanal := calcular_cota_semanal(v_num_membros);

    /*
      Domingo da semana actual as 23:59:59
      Se hoje ja for uma segunda, esta formula continua a achar
      o domingo dessa semana.
    */
    v_nova_validade := NEXT_DAY(v_hoje - 1, 'SUNDAY') + (86399 / 86400);

    /*
      Procurar a ultima cota ainda valida da familia.
      Se existir, ela deixa de estar valida e a transferencia autorizada vira 'Nao'.
      O saldo antigo permanece igual.
    */
    BEGIN
        SELECT codigo_cota
          INTO v_cod_cota_antiga
          FROM (
                SELECT codigo_cota
                  FROM Cota_Agua
                 WHERE codigo_fb = p_codigo_fb
                   AND periodo_validade_ca >= v_hoje
                 ORDER BY periodo_validade_ca DESC
               )
         WHERE ROWNUM = 1;

        UPDATE Cota_Agua
           SET periodo_validade_ca = SYSDATE,
               transferencia_autorizada_ca = 'Nao'
         WHERE codigo_cota = v_cod_cota_antiga;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;
    END;

    INSERT INTO Cota_Agua (
        codigo_fb,
        volume_semanal_ca,
        periodo_validade_ca,
        ajuste_sazonal_ca,
        transferencia_autorizada_ca,
        saldo_disponivel_ca
    ) VALUES (
        p_codigo_fb,
        v_volume_semanal,
        v_nova_validade,
        p_ajuste_sazonal,
        p_nova_aut_transf,
        v_volume_semanal
    )
    RETURNING codigo_cota INTO p_cod_cota_nova;

    p_mensagem :=
        'Nova cota semanal gerada com sucesso. Codigo da nova cota: ' || p_cod_cota_nova;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_cod_cota_nova := NULL;
        p_mensagem := SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE prc_renovar_aut_transf_cota (
    p_codigo_fb      IN  NUMBER,
    p_codigo_cota    IN  NUMBER,
    p_nova_aut       IN  VARCHAR2,
    p_mensagem       OUT VARCHAR2
)
IS
    v_validade_cota   Cota_Agua.periodo_validade_ca%TYPE;
    v_aut_normalizada VARCHAR2(3);
BEGIN
    p_mensagem := NULL;

    v_aut_normalizada := INITCAP(TRIM(p_nova_aut));

    IF v_aut_normalizada NOT IN ('Sim', 'Nao') THEN
        RAISE_APPLICATION_ERROR(-20001, 'O valor da autorizacao deve ser Sim ou Nao.');
    END IF;

    BEGIN
        SELECT periodo_validade_ca
          INTO v_validade_cota
          FROM Cota_Agua
         WHERE codigo_fb   = p_codigo_fb
           AND codigo_cota = p_codigo_cota;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20002, 'Cota nao encontrada para a familia indicada.');
    END;

    IF v_validade_cota < TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(-20003, 'Nao e possivel alterar autorizacao: a cota esta expirada.');
    END IF;

    UPDATE Cota_Agua
       SET transferencia_autorizada_ca = v_aut_normalizada
     WHERE codigo_fb   = p_codigo_fb
       AND codigo_cota = p_codigo_cota;

    p_mensagem := 'Autorizacao de transferencia alterada com sucesso para ' || v_aut_normalizada || '.';

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_mensagem := SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_INSERIR_NECESSIDADE (
    p_descricao_necessidade IN VARCHAR2,
    p_codigo_necessidade    OUT NUMBER,
    p_mensagem              OUT VARCHAR2
)
IS
BEGIN
    p_codigo_necessidade := NULL;
    p_mensagem := NULL;

    IF p_descricao_necessidade IS NULL OR TRIM(p_descricao_necessidade) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'Descricao da necessidade e obrigatoria.');
    END IF;

    INSERT INTO Necessidade (
        descricao_necessidade
    ) VALUES (
        TRIM(p_descricao_necessidade)
    )
    RETURNING cod_necessidade INTO p_codigo_necessidade;

    COMMIT;

    p_mensagem := 'Necessidade inserida com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_necessidade := NULL;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ASSOC_NECESSIDADE_FAMILIA (
    p_codigo_necessidade IN NUMBER,
    p_codigo_familia     IN NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    p_mensagem := NULL;

    SELECT COUNT(*)
      INTO v_count
      FROM Necessidade
     WHERE cod_necessidade = p_codigo_necessidade;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Necessidade nao encontrada.');
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_familia;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Familia beneficiaria nao encontrada.');
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM NECESS_ASSOC_FAMILIA
     WHERE cod_necessidade = p_codigo_necessidade
       AND codigo_fb = p_codigo_familia;

    IF v_count > 0 THEN
        p_mensagem := 'Necessidade ja associada a familia.';
        RETURN;
    END IF;

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade,
        codigo_fb
    ) VALUES (
        p_codigo_necessidade,
        p_codigo_familia
    );

    COMMIT;

    p_mensagem := 'Necessidade associada a familia com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_ACT_NEC_FAMILIA (
    p_codigo_familia   IN NUMBER,
    p_codigos          IN VARCHAR2,
    p_operacao         IN VARCHAR2,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_operacao VARCHAR2(10);
    v_codigo_texto VARCHAR2(30);
    v_codigo_necess NUMBER;
    v_posicao NUMBER := 1;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_familia IS NULL THEN
        p_mensagem := 'Codigo da familia e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_familia;

    IF v_count = 0 THEN
        p_mensagem := 'Familia beneficiaria nao encontrada.';
        RETURN;
    END IF;

    v_operacao := UPPER(TRIM(p_operacao));
    IF v_operacao NOT IN ('ADICIONAR', 'REMOVER') THEN
        p_mensagem := 'Operacao invalida.';
        RETURN;
    END IF;

    IF p_codigos IS NULL OR TRIM(p_codigos) IS NULL THEN
        p_mensagem := 'Seleccione pelo menos uma necessidade.';
        RETURN;
    END IF;

    IF NOT REGEXP_LIKE(TRIM(p_codigos), '^[0-9]+(,[0-9]+)*$') THEN
        p_mensagem := 'Lista de necessidades invalida.';
        RETURN;
    END IF;

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(TRIM(p_codigos), '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_necess := TO_NUMBER(v_codigo_texto);

        SELECT COUNT(*)
          INTO v_count
          FROM Necessidade
         WHERE cod_necessidade = v_codigo_necess;

        IF v_count = 0 THEN
            p_mensagem := 'Necessidade nao encontrada: ' || v_codigo_necess;
            RETURN;
        END IF;

        SELECT COUNT(*)
          INTO v_count
          FROM NECESS_ASSOC_FAMILIA
         WHERE codigo_fb = p_codigo_familia
           AND cod_necessidade = v_codigo_necess;

        IF v_operacao = 'ADICIONAR' AND v_count > 0 THEN
            p_mensagem := 'Necessidade ja associada a familia: ' || v_codigo_necess;
            RETURN;
        END IF;

        IF v_operacao = 'REMOVER' AND v_count = 0 THEN
            p_mensagem := 'Necessidade nao esta associada a familia: ' || v_codigo_necess;
            RETURN;
        END IF;

        v_posicao := v_posicao + 1;
    END LOOP;

    p_pode_continuar := 1;
    p_mensagem := 'Necessidades validas para ' || LOWER(v_operacao) || '.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ACT_NEC_FAMILIA (
    p_codigo_familia   IN NUMBER,
    p_codigos          IN VARCHAR2,
    p_operacao         IN VARCHAR2,
    p_afectadas        OUT NUMBER,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_codigo_texto VARCHAR2(30);
    v_codigo_necess NUMBER;
    v_posicao NUMBER := 1;
    v_operacao VARCHAR2(10);
BEGIN
    p_afectadas := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_ACT_NEC_FAMILIA(
        p_codigo_familia,
        p_codigos,
        p_operacao,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    v_operacao := UPPER(TRIM(p_operacao));

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(TRIM(p_codigos), '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_necess := TO_NUMBER(v_codigo_texto);

        IF v_operacao = 'ADICIONAR' THEN
            INSERT INTO NECESS_ASSOC_FAMILIA (
                cod_necessidade,
                codigo_fb
            ) VALUES (
                v_codigo_necess,
                p_codigo_familia
            );
        ELSE
            DELETE FROM NECESS_ASSOC_FAMILIA
             WHERE cod_necessidade = v_codigo_necess
               AND codigo_fb = p_codigo_familia;
        END IF;

        p_afectadas := p_afectadas + 1;
        v_posicao := v_posicao + 1;
    END LOOP;

    COMMIT;
    p_pode_continuar := 1;
    p_mensagem := 'Necessidades actualizadas com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_afectadas := 0;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_ACT_DADOS_FAMILIA (
    p_codigo_familia          IN NUMBER,
    p_orig_nome               IN VARCHAR2,
    p_orig_num_membros        IN NUMBER,
    p_orig_perfil             IN VARCHAR2,
    p_orig_contacto           IN VARCHAR2,
    p_orig_aldeia             IN VARCHAR2,
    p_orig_coord_gps          IN VARCHAR2,
    p_novo_nome               IN VARCHAR2,
    p_novo_num_membros        IN NUMBER,
    p_novo_perfil             IN VARCHAR2,
    p_novo_contacto           IN VARCHAR2,
    p_nova_aldeia             IN VARCHAR2,
    p_nova_coord_gps          IN VARCHAR2,
    p_act_nome                IN NUMBER,
    p_act_num_membros         IN NUMBER,
    p_act_perfil              IN NUMBER,
    p_act_contacto            IN NUMBER,
    p_act_aldeia              IN NUMBER,
    p_act_coord_gps           IN NUMBER,
    p_pode_continuar          OUT NUMBER,
    p_mensagem                OUT VARCHAR2
)
IS
    v_nome Familia_Beneficiaria.nome_responsavel_fb%TYPE;
    v_num_membros Familia_Beneficiaria.num_membros_fb%TYPE;
    v_perfil Familia_Beneficiaria.perfil_socioeconomico_fb%TYPE;
    v_contacto Familia_Beneficiaria.contacto_fb%TYPE;
    v_cod_localizacao Familia_Beneficiaria.cod_localizacao%TYPE;
    v_aldeia Localizacao.aldeia%TYPE;
    v_coord_gps Localizacao.coordenadas_gps%TYPE;
    v_total_flags NUMBER;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_familia IS NULL THEN
        p_mensagem := 'Codigo da familia e obrigatorio.';
        RETURN;
    END IF;

    IF NVL(p_act_nome, 0) NOT IN (0, 1)
       OR NVL(p_act_num_membros, 0) NOT IN (0, 1)
       OR NVL(p_act_perfil, 0) NOT IN (0, 1)
       OR NVL(p_act_contacto, 0) NOT IN (0, 1)
       OR NVL(p_act_aldeia, 0) NOT IN (0, 1)
       OR NVL(p_act_coord_gps, 0) NOT IN (0, 1) THEN
        p_mensagem := 'Indicadores de actualizacao invalidos.';
        RETURN;
    END IF;

    v_total_flags := NVL(p_act_nome, 0) + NVL(p_act_num_membros, 0) + NVL(p_act_perfil, 0)
        + NVL(p_act_contacto, 0) + NVL(p_act_aldeia, 0) + NVL(p_act_coord_gps, 0);

    IF v_total_flags = 0 THEN
        p_mensagem := 'Seleccione pelo menos um campo para actualizar.';
        RETURN;
    END IF;

    BEGIN
        SELECT f.nome_responsavel_fb,
               f.num_membros_fb,
               f.perfil_socioeconomico_fb,
               f.contacto_fb,
               f.cod_localizacao,
               l.aldeia,
               l.coordenadas_gps
          INTO v_nome,
               v_num_membros,
               v_perfil,
               v_contacto,
               v_cod_localizacao,
               v_aldeia,
               v_coord_gps
          FROM Familia_Beneficiaria f
          LEFT JOIN Localizacao l ON f.cod_localizacao = l.cod_localizacao
         WHERE f.codigo_fb = p_codigo_familia;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'Familia beneficiaria nao encontrada.';
            RETURN;
    END;

    IF NVL(TRIM(v_nome), '#NULL#') <> NVL(TRIM(p_orig_nome), '#NULL#')
       OR NVL(v_num_membros, -999999) <> NVL(p_orig_num_membros, -999999)
       OR NVL(TRIM(v_perfil), '#NULL#') <> NVL(TRIM(p_orig_perfil), '#NULL#')
       OR NVL(TRIM(v_contacto), '#NULL#') <> NVL(TRIM(p_orig_contacto), '#NULL#')
       OR NVL(TRIM(v_aldeia), '#NULL#') <> NVL(TRIM(p_orig_aldeia), '#NULL#')
       OR NVL(TRIM(v_coord_gps), '#NULL#') <> NVL(TRIM(p_orig_coord_gps), '#NULL#') THEN
        p_mensagem := 'Os dados desta familia foram alterados por outro utilizador. Actualize a tabela e tente novamente.';
        RETURN;
    END IF;

    IF (p_act_nome = 1 AND (p_novo_nome IS NULL OR TRIM(p_novo_nome) IS NULL)) THEN
        p_mensagem := 'Nome do responsavel e obrigatorio.';
        RETURN;
    END IF;

    IF (p_act_nome = 1 AND LENGTH(TRIM(p_novo_nome)) > 60) THEN
        p_mensagem := 'Nome do responsavel nao pode exceder 60 caracteres.';
        RETURN;
    END IF;

    IF (p_act_num_membros = 1 AND (p_novo_num_membros IS NULL OR p_novo_num_membros < 1 OR p_novo_num_membros > 99)) THEN
        p_mensagem := 'Numero de membros deve estar entre 1 e 99.';
        RETURN;
    END IF;

    IF (p_act_perfil = 1 AND p_novo_perfil NOT IN ('Baixa renda', 'Media renda', 'Alta renda')) THEN
        p_mensagem := 'Perfil socioeconomico invalido.';
        RETURN;
    END IF;

    IF (p_act_contacto = 1 AND p_novo_contacto IS NOT NULL AND LENGTH(TRIM(p_novo_contacto)) > 20) THEN
        p_mensagem := 'Contacto nao pode exceder 20 caracteres.';
        RETURN;
    END IF;

    IF (p_act_aldeia = 1 OR p_act_coord_gps = 1) AND v_cod_localizacao IS NULL THEN
        p_mensagem := 'A familia nao possui localizacao associada para actualizar.';
        RETURN;
    END IF;

    IF (p_act_aldeia = 1 AND (p_nova_aldeia IS NULL OR TRIM(p_nova_aldeia) IS NULL)) THEN
        p_mensagem := 'Aldeia e obrigatoria.';
        RETURN;
    END IF;

    IF (p_act_aldeia = 1 AND LENGTH(TRIM(p_nova_aldeia)) > 60) THEN
        p_mensagem := 'Aldeia nao pode exceder 60 caracteres.';
        RETURN;
    END IF;

    IF (p_act_coord_gps = 1 AND p_nova_coord_gps IS NOT NULL AND LENGTH(TRIM(p_nova_coord_gps)) > 60) THEN
        p_mensagem := 'Coordenadas GPS nao podem exceder 60 caracteres.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para actualizacao.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ACT_DADOS_FAMILIA (
    p_codigo_familia          IN NUMBER,
    p_orig_nome               IN VARCHAR2,
    p_orig_num_membros        IN NUMBER,
    p_orig_perfil             IN VARCHAR2,
    p_orig_contacto           IN VARCHAR2,
    p_orig_aldeia             IN VARCHAR2,
    p_orig_coord_gps          IN VARCHAR2,
    p_novo_nome               IN VARCHAR2,
    p_novo_num_membros        IN NUMBER,
    p_novo_perfil             IN VARCHAR2,
    p_novo_contacto           IN VARCHAR2,
    p_nova_aldeia             IN VARCHAR2,
    p_nova_coord_gps          IN VARCHAR2,
    p_act_nome                IN NUMBER,
    p_act_num_membros         IN NUMBER,
    p_act_perfil              IN NUMBER,
    p_act_contacto            IN NUMBER,
    p_act_aldeia              IN NUMBER,
    p_act_coord_gps           IN NUMBER,
    p_pode_continuar          OUT NUMBER,
    p_mensagem                OUT VARCHAR2
)
IS
    v_cod_localizacao Familia_Beneficiaria.cod_localizacao%TYPE;
    v_lock_localizacao NUMBER;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_ACT_DADOS_FAMILIA(
        p_codigo_familia,
        p_orig_nome,
        p_orig_num_membros,
        p_orig_perfil,
        p_orig_contacto,
        p_orig_aldeia,
        p_orig_coord_gps,
        p_novo_nome,
        p_novo_num_membros,
        p_novo_perfil,
        p_novo_contacto,
        p_nova_aldeia,
        p_nova_coord_gps,
        p_act_nome,
        p_act_num_membros,
        p_act_perfil,
        p_act_contacto,
        p_act_aldeia,
        p_act_coord_gps,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    SELECT cod_localizacao
      INTO v_cod_localizacao
      FROM Familia_Beneficiaria
     WHERE codigo_fb = p_codigo_familia
     FOR UPDATE;

    IF v_cod_localizacao IS NOT NULL THEN
        SELECT cod_localizacao
          INTO v_lock_localizacao
          FROM Localizacao
         WHERE cod_localizacao = v_cod_localizacao
         FOR UPDATE;
    END IF;

    PRC_PRE_ACT_DADOS_FAMILIA(
        p_codigo_familia,
        p_orig_nome,
        p_orig_num_membros,
        p_orig_perfil,
        p_orig_contacto,
        p_orig_aldeia,
        p_orig_coord_gps,
        p_novo_nome,
        p_novo_num_membros,
        p_novo_perfil,
        p_novo_contacto,
        p_nova_aldeia,
        p_nova_coord_gps,
        p_act_nome,
        p_act_num_membros,
        p_act_perfil,
        p_act_contacto,
        p_act_aldeia,
        p_act_coord_gps,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        ROLLBACK;
        RETURN;
    END IF;

    UPDATE Familia_Beneficiaria
       SET nome_responsavel_fb = CASE WHEN p_act_nome = 1 THEN TRIM(p_novo_nome) ELSE nome_responsavel_fb END,
           num_membros_fb = CASE WHEN p_act_num_membros = 1 THEN p_novo_num_membros ELSE num_membros_fb END,
           perfil_socioeconomico_fb = CASE WHEN p_act_perfil = 1 THEN p_novo_perfil ELSE perfil_socioeconomico_fb END,
           contacto_fb = CASE WHEN p_act_contacto = 1 THEN TRIM(p_novo_contacto) ELSE contacto_fb END
     WHERE codigo_fb = p_codigo_familia;

    IF p_act_aldeia = 1 OR p_act_coord_gps = 1 THEN
        UPDATE Localizacao
           SET aldeia = CASE WHEN p_act_aldeia = 1 THEN TRIM(p_nova_aldeia) ELSE aldeia END,
               coordenadas_gps = CASE WHEN p_act_coord_gps = 1 THEN TRIM(p_nova_coord_gps) ELSE coordenadas_gps END
         WHERE cod_localizacao = v_cod_localizacao;
    END IF;

    COMMIT;
    p_pode_continuar := 1;
    p_mensagem := 'Dados da familia actualizados com sucesso. A cota semanal actualmente valida nao foi alterada.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_ACT_AJUSTE_SAZONAL (
    p_codigo_fb          IN NUMBER,
    p_codigo_cota        IN NUMBER,
    p_novo_ajuste        IN VARCHAR2,
    p_ajuste_actual      OUT VARCHAR2,
    p_validade_cota      OUT DATE,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_count          NUMBER;
    v_ajuste_normal  VARCHAR2(60);
BEGIN
    p_ajuste_actual := NULL;
    p_validade_cota := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigo_fb IS NULL THEN
        p_mensagem := 'Codigo da familia e obrigatorio.';
        RETURN;
    END IF;

    IF p_codigo_cota IS NULL THEN
        p_mensagem := 'Codigo da cota e obrigatorio.';
        RETURN;
    END IF;

    IF p_novo_ajuste IS NULL OR TRIM(p_novo_ajuste) IS NULL THEN
        p_mensagem := 'Novo ajuste sazonal e obrigatorio.';
        RETURN;
    END IF;

    v_ajuste_normal := UPPER(TRIM(p_novo_ajuste));

    IF v_ajuste_normal NOT IN (
        'VERAO',
        'INVERNO',
        'CHUVOSO',
        'SECO',
        'TRANSICAO SECA',
        'PICO SECO',
        'INICIO CHUVOSO',
        'ALTA DEMANDA',
        'FIM DE ANO',
        'NORMAL'
    ) THEN
        p_mensagem := 'Ajuste sazonal invalido.';
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
     WHERE codigo_fb = p_codigo_fb
       AND codigo_cota = p_codigo_cota;

    IF v_count = 0 THEN
        p_mensagem := 'Cota nao encontrada para a familia indicada.';
        RETURN;
    END IF;

    SELECT ajuste_sazonal_ca,
           periodo_validade_ca
      INTO p_ajuste_actual,
           p_validade_cota
      FROM Cota_Agua
     WHERE codigo_fb = p_codigo_fb
       AND codigo_cota = p_codigo_cota;

    IF p_validade_cota IS NULL THEN
        p_mensagem := 'Validade da cota esta indefinida.';
        RETURN;
    END IF;

    IF p_validade_cota < TRUNC(SYSDATE) THEN
        p_mensagem := 'Cota expirada. Nao e permitido actualizar ajuste sazonal.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Cota valida para actualizar ajuste sazonal.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ACT_AJUSTE_SAZONAL (
    p_codigo_fb          IN NUMBER,
    p_codigo_cota        IN NUMBER,
    p_novo_ajuste        IN VARCHAR2,
    p_pode_continuar     OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_ajuste_actual  Cota_Agua.ajuste_sazonal_ca%TYPE;
    v_validade_cota  Cota_Agua.periodo_validade_ca%TYPE;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_ACT_AJUSTE_SAZONAL(
        p_codigo_fb,
        p_codigo_cota,
        p_novo_ajuste,
        v_ajuste_actual,
        v_validade_cota,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    UPDATE Cota_Agua
       SET ajuste_sazonal_ca = TRIM(p_novo_ajuste)
     WHERE codigo_fb = p_codigo_fb
       AND codigo_cota = p_codigo_cota;

    IF SQL%ROWCOUNT = 0 THEN
        p_pode_continuar := 0;
        p_mensagem := 'Nao foi possivel actualizar a cota indicada.';
        RETURN;
    END IF;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Ajuste sazonal actualizado com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_ASSOC_FAMILIAS_PONTO (
    p_codigos_familias IN VARCHAR2,
    p_codigo_pd        IN NUMBER,
    p_total_familias   OUT NUMBER,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_posicao       NUMBER := 1;
    v_codigo_texto  VARCHAR2(30);
    v_codigo_fb     NUMBER;
    v_count         NUMBER;
BEGIN
    p_total_familias := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_codigos_familias IS NULL OR TRIM(p_codigos_familias) IS NULL THEN
        p_mensagem := 'Selecione pelo menos uma familia.';
        RETURN;
    END IF;

    IF p_codigo_pd IS NULL THEN
        p_mensagem := 'Codigo do ponto de distribuicao e obrigatorio.';
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

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(p_codigos_familias, '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_texto := TRIM(v_codigo_texto);

        IF NOT REGEXP_LIKE(v_codigo_texto, '^[0-9]+$') THEN
            p_mensagem := 'Codigo de familia invalido: ' || v_codigo_texto;
            RETURN;
        END IF;

        v_codigo_fb := TO_NUMBER(v_codigo_texto);

        SELECT COUNT(*)
          INTO v_count
          FROM Familia_Beneficiaria
         WHERE codigo_fb = v_codigo_fb;

        IF v_count = 0 THEN
            p_mensagem := 'Familia beneficiaria nao encontrada: ' || v_codigo_fb;
            RETURN;
        END IF;

        p_total_familias := p_total_familias + 1;
        v_posicao := v_posicao + 1;
    END LOOP;

    IF p_total_familias = 0 THEN
        p_mensagem := 'Selecione pelo menos uma familia.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para associar familias ao ponto de distribuicao.';
EXCEPTION
    WHEN OTHERS THEN
        p_total_familias := 0;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ASSOC_FAMILIAS_PONTO (
    p_codigos_familias IN VARCHAR2,
    p_codigo_pd        IN NUMBER,
    p_total_associadas OUT NUMBER,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_posicao          NUMBER := 1;
    v_codigo_texto     VARCHAR2(30);
    v_codigo_fb        NUMBER;
    v_total_validadas  NUMBER;
BEGIN
    p_total_associadas := 0;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_ASSOC_FAMILIAS_PONTO(
        p_codigos_familias,
        p_codigo_pd,
        v_total_validadas,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    LOOP
        v_codigo_texto := REGEXP_SUBSTR(p_codigos_familias, '[^,]+', 1, v_posicao);
        EXIT WHEN v_codigo_texto IS NULL;

        v_codigo_fb := TO_NUMBER(TRIM(v_codigo_texto));

        UPDATE Familia_Beneficiaria
           SET codigo_pd = p_codigo_pd
         WHERE codigo_fb = v_codigo_fb;

        p_total_associadas := p_total_associadas + SQL%ROWCOUNT;
        v_posicao := v_posicao + 1;
    END LOOP;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := p_total_associadas || ' familia(s) associada(s) ao ponto de distribuicao ' || p_codigo_pd || '.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_total_associadas := 0;
        p_pode_continuar := 0;
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/
