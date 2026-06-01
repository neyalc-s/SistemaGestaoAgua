SET ECHO OFF;

-- Referencia historica. Nao entra no fluxo principal de instalacao.

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
        p_mensagem := 'ERRO: ' || SQLERRM;
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
        p_mensagem := 'ERRO: ' || SQLERRM;
END;
/
