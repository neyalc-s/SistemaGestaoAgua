SET ECHO OFF;

CREATE OR REPLACE PROCEDURE PRC_CRIAR_UTILIZADOR_ADM (
    p_username_oracle    IN  VARCHAR2,
    p_sucesso            OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_username VARCHAR2(30);
    v_password VARCHAR2(30);
    v_total    NUMBER;
BEGIN
    p_sucesso := 0;
    p_mensagem := NULL;

    v_username := UPPER(TRIM(p_username_oracle));

    IF v_username IS NULL THEN
        p_mensagem := 'Informe o username.';
        RETURN;
    END IF;

    IF LENGTH(v_username) > 27 OR NOT REGEXP_LIKE(v_username, '^[A-Za-z][A-Za-z0-9_]*$') THEN
        p_mensagem := 'Username invalido. Comece por letra e use apenas letras, numeros ou underscore. Maximo: 27 caracteres.';
        RETURN;
    END IF;

    v_password := LOWER(v_username) || '123';

    SELECT COUNT(*)
      INTO v_total
      FROM all_users
     WHERE username = v_username;

    IF v_total = 0 THEN
        EXECUTE IMMEDIATE
            'CREATE USER ' || v_username ||
            ' IDENTIFIED BY "' || v_password || '"' ||
            ' DEFAULT TABLESPACE TS_ADMINISTRADOR' ||
            ' TEMPORARY TABLESPACE TEMP' ||
            ' QUOTA 0K ON TS_ADMINISTRADOR' ||
            ' PROFILE PERFIL_FUNCIONARIO_APP ACCOUNT UNLOCK';
    END IF;

    EXECUTE IMMEDIATE 'GRANT CREATE SESSION TO ' || v_username;
    EXECUTE IMMEDIATE 'GRANT R_ADM_FUNC TO ' || v_username;
    EXECUTE IMMEDIATE 'ALTER USER ' || v_username || ' PROFILE PERFIL_FUNCIONARIO_APP ACCOUNT UNLOCK';

    p_sucesso := 1;
    p_mensagem := 'Utilizador preparado com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        p_sucesso := 0;
        p_mensagem := 'Nao foi possivel preparar o utilizador.';
END;
/

CREATE OR REPLACE PROCEDURE PRC_INSERIR_FUNCIONARIO (
    p_nome_funcionario   IN  VARCHAR2,
    p_username_oracle    IN  VARCHAR2,
    p_codigo_no          IN  NUMBER,
    p_cod_funcionario    OUT NUMBER,
    p_sucesso            OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_nome_funcionario FUNCIONARIO.nome_funcionario%TYPE;
    v_username_oracle  FUNCIONARIO.username_oracle%TYPE;
    v_nome_no          NO_SISTEMA.nome_no%TYPE;
    v_total            NUMBER;
    v_user_ok          NUMBER := 1;
    v_user_msg         VARCHAR2(4000);
    v_cod_funcionario  FUNCIONARIO.cod_funcionario%TYPE;
    v_password_inicial VARCHAR2(30);
BEGIN
    p_cod_funcionario := NULL;
    p_sucesso := 0;
    p_mensagem := NULL;

    v_nome_funcionario := TRIM(p_nome_funcionario);
    v_username_oracle := UPPER(TRIM(p_username_oracle));

    IF v_nome_funcionario IS NULL THEN
        p_mensagem := 'Nome do funcionario e obrigatorio.';
        RETURN;
    END IF;

    IF v_username_oracle IS NULL THEN
        p_mensagem := 'Informe o username.';
        RETURN;
    END IF;

    IF LENGTH(v_username_oracle) > 27 OR NOT REGEXP_LIKE(v_username_oracle, '^[A-Za-z][A-Za-z0-9_]*$') THEN
        p_mensagem := 'Username invalido. Comece por letra e use apenas letras, numeros ou underscore. Maximo: 27 caracteres.';
        RETURN;
    END IF;

    IF p_codigo_no NOT IN (1, 2, 3, 4, 5) THEN
        p_mensagem := 'No do sistema invalido para registo de funcionario.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_total
      FROM FUNCIONARIO
     WHERE UPPER(TRIM(username_oracle)) = v_username_oracle;

    IF v_total > 0 THEN
        p_mensagem := 'Username ja existe.';
        RETURN;
    END IF;

    BEGIN
        SELECT nome_no
          INTO v_nome_no
          FROM NO_SISTEMA
         WHERE codigo_no = p_codigo_no
           AND UPPER(TRIM(estado_no)) = 'ACTIVO';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensagem := 'No do sistema nao encontrado ou inactivo.';
            RETURN;
    END;

    IF p_codigo_no = 1 THEN
        PRC_CRIAR_UTILIZADOR_ADM(v_username_oracle, v_user_ok, v_user_msg);

        IF v_user_ok <> 1 THEN
            p_mensagem := 'Nao foi possivel preparar o acesso do funcionario.';
            RETURN;
        END IF;
    END IF;

    SELECT NVL(MAX(cod_funcionario), 0) + 1
      INTO v_cod_funcionario
      FROM FUNCIONARIO;

    INSERT INTO FUNCIONARIO (
        cod_funcionario,
        codigo_no,
        nome_funcionario,
        username_oracle,
        estado_funcionario
    ) VALUES (
        v_cod_funcionario,
        p_codigo_no,
        v_nome_funcionario,
        v_username_oracle,
        'Activo'
    );

    p_cod_funcionario := v_cod_funcionario;
    v_password_inicial := LOWER(v_username_oracle) || '123';

    COMMIT;

    p_sucesso := 1;

    IF p_codigo_no = 1 THEN
        p_mensagem := 'Funcionario registado com sucesso. Password inicial: ' || v_password_inicial;
    ELSE
        p_mensagem := 'Funcionario registado com sucesso. Aguarde alguns minutos antes de tentar fazer login. Password inicial: ' || v_password_inicial;
    END IF;
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        p_cod_funcionario := NULL;
        p_sucesso := 0;

        BEGIN
            SELECT COUNT(*)
              INTO v_total
              FROM FUNCIONARIO
             WHERE UPPER(TRIM(username_oracle)) = v_username_oracle;

            IF v_total > 0 THEN
                p_mensagem := 'Username ja existe.';
            ELSE
                p_mensagem := 'Nao foi possivel registar funcionario por dados duplicados.';
            END IF;
        EXCEPTION
            WHEN OTHERS THEN
                p_mensagem := 'Nao foi possivel registar funcionario por dados duplicados.';
        END;
    WHEN OTHERS THEN
        ROLLBACK;
        p_cod_funcionario := NULL;
        p_sucesso := 0;
        p_mensagem := 'Nao foi possivel registar funcionario.';
END;
/

CREATE OR REPLACE PROCEDURE PRC_REFRESH_ALERTA_QUAL_ADMIN (
    p_sucesso  OUT NUMBER,
    p_mensagem OUT VARCHAR2
)
IS
BEGIN
    p_sucesso := 0;
    p_mensagem := NULL;

    DBMS_MVIEW.REFRESH('MV_ALERTA_QUALIDADE_AGUA_ADMIN', 'C');

    p_sucesso := 1;
    p_mensagem := 'Alertas de qualidade actualizados no AdministradorDB.';
EXCEPTION
    WHEN OTHERS THEN
        p_sucesso := 0;
        p_mensagem := 'Nao foi possivel actualizar os alertas no AdministradorDB. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_JOB_REF_ALERTA_QUAL_ADMIN
IS
    v_sucesso  NUMBER;
    v_mensagem VARCHAR2(4000);
BEGIN
    PRC_REFRESH_ALERTA_QUAL_ADMIN(v_sucesso, v_mensagem);
END;
/

CREATE OR REPLACE PROCEDURE PRC_VALIDAR_FUNCIONARIO (
    p_username_oracle    IN  VARCHAR2,
    p_cod_funcionario    OUT NUMBER,
    p_codigo_no          OUT NUMBER,
    p_nome_no            OUT VARCHAR2,
    p_host_no            OUT VARCHAR2,
    p_pode_entrar        OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_estado_funcionario FUNCIONARIO.estado_funcionario%TYPE;
    v_estado_no          NO_SISTEMA.estado_no%TYPE;
BEGIN
    p_cod_funcionario := NULL;
    p_codigo_no := NULL;
    p_nome_no := NULL;
    p_host_no := NULL;
    p_pode_entrar := 0;
    p_mensagem := NULL;

    IF p_username_oracle IS NULL OR TRIM(p_username_oracle) IS NULL THEN
        p_mensagem := 'Informe o username Oracle.';
        RETURN;
    END IF;

    SELECT f.cod_funcionario,
           f.codigo_no,
           n.nome_no,
           n.host_no,
           f.estado_funcionario,
           n.estado_no
      INTO p_cod_funcionario,
           p_codigo_no,
           p_nome_no,
           p_host_no,
           v_estado_funcionario,
           v_estado_no
      FROM FUNCIONARIO f
      JOIN NO_SISTEMA n ON n.codigo_no = f.codigo_no
     WHERE UPPER(TRIM(f.username_oracle)) = UPPER(TRIM(p_username_oracle));

    IF UPPER(TRIM(v_estado_funcionario)) <> 'ACTIVO' THEN
        p_mensagem := 'Funcionario Inactivo.';
        RETURN;
    END IF;

    IF UPPER(TRIM(v_estado_no)) <> 'ACTIVO' THEN
        p_mensagem := 'No do funcionario nao esta Activo.';
        RETURN;
    END IF;

    p_pode_entrar := 1;
    p_mensagem := 'Funcionario validado.';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_mensagem := 'Funcionario nao encontrado.';
    WHEN TOO_MANY_ROWS THEN
        p_mensagem := 'Username Oracle duplicado.';
    WHEN OTHERS THEN
        p_mensagem := 'Nao foi possivel validar o funcionario. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_ABRIR_SESSAO_FUNC (
    p_username_oracle    IN  VARCHAR2,
    p_modo_login         IN  VARCHAR2 DEFAULT 'ONLINE',
    p_codigo_sessao      OUT NUMBER,
    p_cod_funcionario    OUT NUMBER,
    p_codigo_no          OUT NUMBER,
    p_nome_no            OUT VARCHAR2,
    p_pode_entrar        OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_host_no       NO_SISTEMA.host_no%TYPE;
    v_pode_entrar   NUMBER;
    v_modo_login    VARCHAR2(20);
    v_sessoes       NUMBER;
BEGIN
    p_codigo_sessao := NULL;
    p_cod_funcionario := NULL;
    p_codigo_no := NULL;
    p_nome_no := NULL;
    p_pode_entrar := 0;
    p_mensagem := NULL;

    v_modo_login := UPPER(TRIM(NVL(p_modo_login, 'ONLINE')));

    IF v_modo_login NOT IN ('ONLINE', 'OFFLINE') THEN
        p_mensagem := 'Modo de login invalido. Seleccione ONLINE ou OFFLINE.';
        RETURN;
    END IF;

    PRC_VALIDAR_FUNCIONARIO(
        p_username_oracle,
        p_cod_funcionario,
        p_codigo_no,
        p_nome_no,
        v_host_no,
        v_pode_entrar,
        p_mensagem
    );

    IF v_pode_entrar <> 1 THEN
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_sessoes
      FROM SESSAO_FUNCIONARIO
     WHERE cod_funcionario = p_cod_funcionario
       AND estado_sessao = 'ABERTA';

    IF v_sessoes > 0 THEN
        p_mensagem := 'Funcionario ja possui sessao aberta.';
        RETURN;
    END IF;

    INSERT INTO SESSAO_FUNCIONARIO (
        codigo_sessao,
        cod_funcionario,
        codigo_no,
        data_inicio,
        estado_sessao,
        modo_login
    ) VALUES (
        NULL,
        p_cod_funcionario,
        p_codigo_no,
        SYSDATE,
        'ABERTA',
        v_modo_login
    )
    RETURNING codigo_sessao INTO p_codigo_sessao;

    COMMIT;

    p_pode_entrar := 1;
    p_mensagem := 'Sessao aberta com sucesso.';
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        p_pode_entrar := 0;
        p_mensagem := 'Funcionario ja possui sessao aberta.';
    WHEN OTHERS THEN
        ROLLBACK;
        p_pode_entrar := 0;
        p_mensagem := 'Nao foi possivel abrir a sessao. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_FECHAR_SESSAO_FUNC (
    p_codigo_sessao      IN  NUMBER,
    p_sucesso            OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_linhas NUMBER;
BEGIN
    p_sucesso := 0;
    p_mensagem := NULL;

    IF p_codigo_sessao IS NULL THEN
        p_mensagem := 'Codigo da sessao e obrigatorio.';
        RETURN;
    END IF;

    UPDATE SESSAO_FUNCIONARIO
       SET data_fim = SYSDATE,
           estado_sessao = 'FECHADA'
     WHERE codigo_sessao = p_codigo_sessao
       AND estado_sessao = 'ABERTA';

    v_linhas := SQL%ROWCOUNT;

    IF v_linhas = 0 THEN
        ROLLBACK;
        p_mensagem := 'Sessao aberta nao encontrada.';
        RETURN;
    END IF;

    COMMIT;

    p_sucesso := 1;
    p_mensagem := 'Sessao fechada com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_sucesso := 0;
        p_mensagem := 'Nao foi possivel fechar a sessao. Detalhe: ' || SQLERRM;
END;
/
