SET ECHO OFF;

CREATE OR REPLACE PROCEDURE PRC_PRE_REGISTAR_COMITE (
    p_nome_comite      IN VARCHAR2,
    p_data_criacao     IN DATE,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;

    IF p_nome_comite IS NULL OR TRIM(p_nome_comite) IS NULL THEN
        p_mensagem := 'Nome do comite e obrigatorio.';
        RETURN;
    END IF;

    IF LENGTH(TRIM(p_nome_comite)) > 60 THEN
        p_mensagem := 'Nome do comite nao pode exceder 60 caracteres.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Comite
     WHERE UPPER(TRIM(nome_comite)) = UPPER(TRIM(p_nome_comite));

    IF v_count > 0 THEN
        p_mensagem := 'Ja existe um comite com este nome.';
        RETURN;
    END IF;

    IF p_data_criacao IS NULL THEN
        p_mensagem := 'Data de criacao e obrigatoria.';
        RETURN;
    END IF;

    IF TRUNC(p_data_criacao) > TRUNC(SYSDATE) THEN
        p_mensagem := 'Data de criacao nao pode ser futura.';
        RETURN;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para registar comite.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_PRE_REG_EQUIPE_TEC (
    p_nome                    IN VARCHAR2,
    p_area_actuacao           IN VARCHAR2,
    p_nivel_formacao          IN VARCHAR2,
    p_contacto                IN VARCHAR2,
    p_supervisor_responsavel  IN VARCHAR2,
    p_tipo_equipe             IN VARCHAR2,
    p_habilidade_tecnica      IN VARCHAR2,
    p_tempo_medio_resposta    IN NUMBER,
    p_ferramentas_ids         IN VARCHAR2,
    p_novas_ferramentas       IN VARCHAR2,
    p_codigo_parametro        IN NUMBER,
    p_novo_parametro_nome     IN VARCHAR2,
    p_novo_parametro_unidade  IN VARCHAR2,
    p_especialidade_analise   IN VARCHAR2,
    p_frequencia_amostragem   IN VARCHAR2,
    p_equipamentos_analista   IN VARCHAR2,
    p_metodologia_sensib      IN VARCHAR2,
    p_lingua_local            IN VARCHAR2,
    p_comunidade_atendida     IN VARCHAR2,
    p_materiais_educador      IN VARCHAR2,
    p_pode_continuar          OUT NUMBER,
    p_mensagem                OUT VARCHAR2
)
IS
    v_count NUMBER;
    v_tipo  VARCHAR2(60);
BEGIN
    p_pode_continuar := 0;
    p_mensagem := NULL;
    v_tipo := TRIM(p_tipo_equipe);

    IF p_nome IS NULL OR TRIM(p_nome) IS NULL THEN
        p_mensagem := 'Nome da equipe e obrigatorio.';
        RETURN;
    END IF;

    IF LENGTH(TRIM(p_nome)) > 60 THEN
        p_mensagem := 'Nome da equipe nao pode exceder 60 caracteres.';
        RETURN;
    END IF;

    IF p_area_actuacao IS NULL OR TRIM(p_area_actuacao) IS NULL THEN
        p_mensagem := 'Area de actuacao e obrigatoria.';
        RETURN;
    END IF;

    IF p_contacto IS NULL OR TRIM(p_contacto) IS NULL THEN
        p_mensagem := 'Informe o contacto.';
        RETURN;
    END IF;

    IF p_supervisor_responsavel IS NULL OR TRIM(p_supervisor_responsavel) IS NULL THEN
        p_mensagem := 'Supervisor responsavel e obrigatorio.';
        RETURN;
    END IF;

    IF p_nivel_formacao IS NULL OR TRIM(p_nivel_formacao) IS NULL THEN
        p_mensagem := 'Nivel de formacao e obrigatorio.';
        RETURN;
    END IF;

    IF TRIM(p_nivel_formacao) NOT IN (
        'Ensino medio',
        'Tecnico profissional',
        'Licenciatura',
        'Mestrado',
        'Doutoramento',
        'Outro'
    ) THEN
        p_mensagem := 'Nivel de formacao invalido.';
        RETURN;
    END IF;

    IF v_tipo NOT IN ('Tecnico de Manutencao', 'Analista de Qualidade', 'Educador Comunitario') THEN
        p_mensagem := 'Tipo de equipe invalido.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_count
      FROM Equipe_Tecnica
     WHERE UPPER(TRIM(nome)) = UPPER(TRIM(p_nome));

    IF v_count > 0 THEN
        p_mensagem := 'Ja existe uma equipe com este nome.';
        RETURN;
    END IF;

    IF v_tipo = 'Tecnico de Manutencao' THEN
        IF p_habilidade_tecnica IS NULL OR TRIM(p_habilidade_tecnica) IS NULL THEN
            p_mensagem := 'Habilidade tecnica e obrigatoria para tecnico de manutencao.';
            RETURN;
        END IF;

        IF p_tempo_medio_resposta IS NULL OR p_tempo_medio_resposta <= 0 THEN
            p_mensagem := 'Tempo medio de resposta deve ser maior que zero.';
            RETURN;
        END IF;
    ELSIF v_tipo = 'Analista de Qualidade' THEN
        IF p_codigo_parametro IS NULL
           AND (p_novo_parametro_nome IS NULL OR TRIM(p_novo_parametro_nome) IS NULL) THEN
            p_mensagem := 'Seleccione ou registe um parametro de qualidade.';
            RETURN;
        END IF;

        IF p_codigo_parametro IS NULL
           AND (p_novo_parametro_unidade IS NULL OR TRIM(p_novo_parametro_unidade) IS NULL) THEN
            p_mensagem := 'Unidade padrao do novo parametro e obrigatoria.';
            RETURN;
        END IF;

        IF p_especialidade_analise IS NULL OR TRIM(p_especialidade_analise) IS NULL THEN
            p_mensagem := 'Especialidade de analise e obrigatoria.';
            RETURN;
        END IF;

        IF p_frequencia_amostragem IS NULL OR TRIM(p_frequencia_amostragem) IS NULL THEN
            p_mensagem := 'Frequencia de amostragem e obrigatoria.';
            RETURN;
        END IF;

        IF TRIM(p_frequencia_amostragem) NOT IN ('Semanal', 'Quinzenal', 'Mensal') THEN
            p_mensagem := 'Frequencia de amostragem invalida.';
            RETURN;
        END IF;

        IF p_equipamentos_analista IS NULL OR TRIM(p_equipamentos_analista) IS NULL THEN
            p_mensagem := 'Registe pelo menos um equipamento do analista.';
            RETURN;
        END IF;
    ELSE
        IF p_metodologia_sensib IS NULL OR TRIM(p_metodologia_sensib) IS NULL THEN
            p_mensagem := 'Metodologia de sensibilizacao e obrigatoria.';
            RETURN;
        END IF;

        IF p_lingua_local IS NULL OR TRIM(p_lingua_local) IS NULL THEN
            p_mensagem := 'Lingua local e obrigatoria.';
            RETURN;
        END IF;

        IF p_comunidade_atendida IS NULL OR TRIM(p_comunidade_atendida) IS NULL THEN
            p_mensagem := 'Comunidade atendida e obrigatoria.';
            RETURN;
        END IF;

        IF p_materiais_educador IS NULL OR TRIM(p_materiais_educador) IS NULL THEN
            p_mensagem := 'Registe pelo menos um material do educador.';
            RETURN;
        END IF;
    END IF;

    p_pode_continuar := 1;
    p_mensagem := 'Dados validos para registar equipe tecnica.';
EXCEPTION
    WHEN OTHERS THEN
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REG_EQUIPE_TEC (
    p_nome                    IN VARCHAR2,
    p_area_actuacao           IN VARCHAR2,
    p_nivel_formacao          IN VARCHAR2,
    p_contacto                IN VARCHAR2,
    p_supervisor_responsavel  IN VARCHAR2,
    p_tipo_equipe             IN VARCHAR2,
    p_habilidade_tecnica      IN VARCHAR2,
    p_tempo_medio_resposta    IN NUMBER,
    p_ferramentas_ids         IN VARCHAR2,
    p_novas_ferramentas       IN VARCHAR2,
    p_codigo_parametro        IN NUMBER,
    p_novo_parametro_nome     IN VARCHAR2,
    p_novo_parametro_unidade  IN VARCHAR2,
    p_especialidade_analise   IN VARCHAR2,
    p_frequencia_amostragem   IN VARCHAR2,
    p_equipamentos_analista   IN VARCHAR2,
    p_metodologia_sensib      IN VARCHAR2,
    p_lingua_local            IN VARCHAR2,
    p_comunidade_atendida     IN VARCHAR2,
    p_materiais_educador      IN VARCHAR2,
    p_equipe_id               OUT NUMBER,
    p_pode_continuar          OUT NUMBER,
    p_mensagem                OUT VARCHAR2
)
IS
    v_tipo             VARCHAR2(60);
    v_texto            VARCHAR2(4000);
    v_item             VARCHAR2(4000);
    v_pos              NUMBER;
    v_codigo_parametro NUMBER;
    v_pode_remoto      NUMBER;
    v_msg_remoto       VARCHAR2(4000);

    PROCEDURE inserir_lista_texto(
        p_lista IN VARCHAR2,
        p_tabela IN VARCHAR2
    )
    IS
    BEGIN
        v_texto := TRIM(p_lista);

        WHILE v_texto IS NOT NULL LOOP
            v_pos := INSTR(v_texto, '|');
            IF v_pos > 0 THEN
                v_item := TRIM(SUBSTR(v_texto, 1, v_pos - 1));
                v_texto := TRIM(SUBSTR(v_texto, v_pos + 1));
            ELSE
                v_item := TRIM(v_texto);
                v_texto := NULL;
            END IF;

            IF v_item IS NOT NULL THEN
                IF p_tabela = 'FERRAMENTA' THEN
                    INSERT INTO Ferramenta_Manutencao (equipe_id, nome_ferramenta)
                    VALUES (p_equipe_id, v_item);
                ELSIF p_tabela = 'EQUIPAMENTO' THEN
                    INSERT INTO Equipamento_Analista (equipe_id, nome_equipamento)
                    VALUES (p_equipe_id, v_item);
                ELSE
                    INSERT INTO Material_Educador (equipe_id, nome_material)
                    VALUES (p_equipe_id, v_item);
                END IF;
            END IF;
        END LOOP;
    END;

    PROCEDURE assoc_ferr_exist(
        p_lista IN VARCHAR2
    )
    IS
        v_id NUMBER;
    BEGIN
        v_texto := TRIM(p_lista);

        WHILE v_texto IS NOT NULL LOOP
            v_pos := INSTR(v_texto, ',');
            IF v_pos > 0 THEN
                v_item := TRIM(SUBSTR(v_texto, 1, v_pos - 1));
                v_texto := TRIM(SUBSTR(v_texto, v_pos + 1));
            ELSE
                v_item := TRIM(v_texto);
                v_texto := NULL;
            END IF;

            IF v_item IS NOT NULL THEN
                v_id := TO_NUMBER(v_item);

                UPDATE Ferramenta_Manutencao
                   SET equipe_id = p_equipe_id
                 WHERE cod_ferramenta_disponivel = v_id
                   AND equipe_id IS NULL;

                IF SQL%ROWCOUNT = 0 THEN
                    RAISE_APPLICATION_ERROR(-20002, 'Ferramenta indisponivel ou ja associada: ' || v_item);
                END IF;
            END IF;
        END LOOP;
    END;
BEGIN
    p_equipe_id := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;
    v_tipo := TRIM(p_tipo_equipe);

    PRC_PRE_REG_EQUIPE_TEC(
        p_nome,
        p_area_actuacao,
        p_nivel_formacao,
        p_contacto,
        p_supervisor_responsavel,
        v_tipo,
        p_habilidade_tecnica,
        p_tempo_medio_resposta,
        p_ferramentas_ids,
        p_novas_ferramentas,
        p_codigo_parametro,
        p_novo_parametro_nome,
        p_novo_parametro_unidade,
        p_especialidade_analise,
        p_frequencia_amostragem,
        p_equipamentos_analista,
        p_metodologia_sensib,
        p_lingua_local,
        p_comunidade_atendida,
        p_materiais_educador,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    INSERT INTO Equipe_Tecnica (
        nome,
        area_actuacao,
        nivel_formacao,
        contacto,
        supervisor_responsavel
    ) VALUES (
        TRIM(p_nome),
        TRIM(p_area_actuacao),
        TRIM(p_nivel_formacao),
        TRIM(p_contacto),
        TRIM(p_supervisor_responsavel)
    )
    RETURNING equipe_id INTO p_equipe_id;

    IF v_tipo = 'Tecnico de Manutencao' THEN
        INSERT INTO Tecnico_Manutencao (
            equipe_id,
            habilidade_tecnica,
            tempo_medio_resposta
        ) VALUES (
            p_equipe_id,
            TRIM(p_habilidade_tecnica),
            p_tempo_medio_resposta
        );

        assoc_ferr_exist(p_ferramentas_ids);
        inserir_lista_texto(p_novas_ferramentas, 'FERRAMENTA');
    ELSIF v_tipo = 'Analista de Qualidade' THEN
        v_codigo_parametro := p_codigo_parametro;

        IF v_codigo_parametro IS NULL THEN
            p_pode_continuar := 0;
            p_mensagem := 'Parametro deve ser registado antes da equipe.';
            ROLLBACK;
            RETURN;
        END IF;

        INSERT INTO Analista_Qualidade (
            equipe_id,
            codigo_parametro,
            especialidade_analise,
            frequencia_amostragem
        ) VALUES (
            p_equipe_id,
            v_codigo_parametro,
            TRIM(p_especialidade_analise),
            TRIM(p_frequencia_amostragem)
        );

        inserir_lista_texto(p_equipamentos_analista, 'EQUIPAMENTO');
    ELSE
        INSERT INTO Educador_Comunitario (
            equipe_id,
            metodologia_sensibilizacao,
            lingua_local,
            comunidade_atendida
        ) VALUES (
            p_equipe_id,
            TRIM(p_metodologia_sensib),
            TRIM(p_lingua_local),
            TRIM(p_comunidade_atendida)
        );

        inserir_lista_texto(p_materiais_educador, 'MATERIAL');
    END IF;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Equipe tecnica registada com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_equipe_id := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REGISTAR_COMITE (
    p_nome_comite      IN VARCHAR2,
    p_data_criacao     IN DATE,
    p_codigo_comite    OUT NUMBER,
    p_pode_continuar   OUT NUMBER,
    p_mensagem         OUT VARCHAR2
)
IS
BEGIN
    p_codigo_comite := NULL;
    p_pode_continuar := 0;
    p_mensagem := NULL;

    PRC_PRE_REGISTAR_COMITE(
        p_nome_comite,
        p_data_criacao,
        p_pode_continuar,
        p_mensagem
    );

    IF p_pode_continuar <> 1 THEN
        RETURN;
    END IF;

    INSERT INTO Comite (
        nome_comite,
        data_criacao
    ) VALUES (
        TRIM(p_nome_comite),
        p_data_criacao
    )
    RETURNING cod_comite_responsavel INTO p_codigo_comite;

    COMMIT;

    p_pode_continuar := 1;
    p_mensagem := 'Comite registado com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_codigo_comite := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_REG_PARAM_QUALIDADE_LOCAL (
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

    SELECT Seq_Parametro_Qualidade.NEXTVAL
      INTO p_codigo_parametro
      FROM DUAL;

    INSERT INTO Parametro_Qualidade (
        codigo_parametro,
        nome_parametro,
        unidade_padrao
    ) VALUES (
        p_codigo_parametro,
        TRIM(p_nome_parametro),
        TRIM(p_unidade_padrao)
    );

    p_pode_continuar := 1;
    p_mensagem := 'Parametro de qualidade registado com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        p_codigo_parametro := NULL;
        p_pode_continuar := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/
