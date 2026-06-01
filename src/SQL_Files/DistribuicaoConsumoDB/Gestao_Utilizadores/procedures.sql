SET ECHO OFF;

CREATE OR REPLACE PROCEDURE PRC_REFRESH_FUNCIONARIO_LOCAL
IS
BEGIN
    DBMS_MVIEW.REFRESH('MV_FUNCIONARIO_LOCAL', 'C');
END;
/

CREATE OR REPLACE PROCEDURE PRC_CRIAR_USER_FUNC_LOCAL (
    p_username_oracle    IN  VARCHAR2,
    p_criado             OUT NUMBER,
    p_sucesso            OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_username VARCHAR2(30);
    v_password VARCHAR2(30);
BEGIN
    p_criado := 0;
    p_sucesso := 0;
    p_mensagem := NULL;

    v_username := UPPER(TRIM(p_username_oracle));

    IF v_username IS NULL THEN
        p_mensagem := 'Informe o username.';
        RETURN;
    END IF;

    IF LENGTH(v_username) > 27 OR NOT REGEXP_LIKE(v_username, '^[A-Za-z][A-Za-z0-9_]*$') THEN
        p_mensagem := 'Username invalido para criacao do utilizador Oracle local.';
        RETURN;
    END IF;

    v_password := LOWER(v_username) || '123';

    BEGIN
        EXECUTE IMMEDIATE
            'CREATE USER ' || v_username ||
            ' IDENTIFIED BY "' || v_password || '"' ||
            ' DEFAULT TABLESPACE TS_DISTRIBUICAO_CONSUMO' ||
            ' TEMPORARY TABLESPACE TEMP' ||
            ' QUOTA 0K ON TS_DISTRIBUICAO_CONSUMO' ||
            ' PROFILE PERFIL_FUNCIONARIO_APP ACCOUNT UNLOCK';
        p_criado := 1;
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -1920 THEN
                p_criado := 0;
            ELSE
                p_mensagem := 'Nao foi possivel criar o utilizador local neste momento.';
                RETURN;
            END IF;
    END;

    EXECUTE IMMEDIATE 'GRANT CREATE SESSION TO ' || v_username;
    EXECUTE IMMEDIATE 'GRANT R_DIST_FUNC TO ' || v_username;
    EXECUTE IMMEDIATE 'ALTER USER ' || v_username || ' PROFILE PERFIL_FUNCIONARIO_APP ACCOUNT UNLOCK';

    p_sucesso := 1;
    IF p_criado = 1 THEN
        p_mensagem := 'Utilizador criado com sucesso.';
    ELSE
        p_mensagem := 'Utilizador preparado com sucesso.';
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        p_sucesso := 0;
        p_mensagem := 'Nao foi possivel preparar o utilizador local neste momento.';
END;
/

CREATE OR REPLACE PROCEDURE PRC_SYNC_USER_FUNC_LOCAL (
    p_criados      OUT NUMBER,
    p_actualizados OUT NUMBER,
    p_falhas       OUT NUMBER,
    p_mensagem     OUT VARCHAR2
)
IS
    v_criado   NUMBER;
    v_sucesso  NUMBER;
    v_mensagem VARCHAR2(4000);
BEGIN
    p_criados := 0;
    p_actualizados := 0;
    p_falhas := 0;
    p_mensagem := NULL;

    PRC_REFRESH_FUNCIONARIO_LOCAL;

    FOR r IN (
        SELECT username_oracle
          FROM MV_FUNCIONARIO_LOCAL
         WHERE UPPER(TRIM(estado_funcionario)) = 'ACTIVO'
           AND username_oracle IS NOT NULL
    ) LOOP
        PRC_CRIAR_USER_FUNC_LOCAL(r.username_oracle, v_criado, v_sucesso, v_mensagem);

        IF v_sucesso = 1 THEN
            IF v_criado = 1 THEN
                p_criados := p_criados + 1;
            ELSE
                p_actualizados := p_actualizados + 1;
            END IF;
        ELSE
            p_falhas := p_falhas + 1;
        END IF;
    END LOOP;

    p_mensagem := 'Sincronizacao concluida. Criados: ' || p_criados ||
                  ', confirmados: ' || p_actualizados ||
                  ', falhas: ' || p_falhas || '.';
EXCEPTION
    WHEN OTHERS THEN
        p_falhas := NVL(p_falhas, 0) + 1;
        p_mensagem := 'Nao foi possivel sincronizar utilizadores locais.';
END;
/

CREATE OR REPLACE PROCEDURE PRC_JOB_SYNC_USER_LOCAL
IS
    v_criados NUMBER;
    v_actualizados NUMBER;
    v_falhas NUMBER;
    v_mensagem VARCHAR2(4000);
BEGIN
    PRC_SYNC_USER_FUNC_LOCAL(v_criados, v_actualizados, v_falhas, v_mensagem);
END;
/
CREATE OR REPLACE PROCEDURE PRC_EXPIRAR_SESSOES_ANT_LOCAL (
    p_sessoes_expiradas OUT NUMBER,
    p_sucesso           OUT NUMBER,
    p_mensagem          OUT VARCHAR2
)
IS
BEGIN
    p_sessoes_expiradas := 0;
    p_sucesso := 0;
    p_mensagem := NULL;

    UPDATE SESSAO_FUNCIONARIO_LOCAL
       SET estado_sessao = 'EXPIRADA',
           data_fim = SYSDATE
     WHERE estado_sessao = 'ABERTA'
       AND data_inicio < SYSDATE - 1;

    p_sessoes_expiradas := SQL%ROWCOUNT;
    COMMIT;

    p_sucesso := 1;
    p_mensagem := p_sessoes_expiradas || ' sessao(oes) local(is) expirada(s).';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_sessoes_expiradas := 0;
        p_sucesso := 0;
        p_mensagem := 'Erro ao expirar sessoes locais antigas.';
END;
/

CREATE OR REPLACE PROCEDURE PRC_JOB_EXPIRAR_SESSOES_LOCAL
IS
    v_sessoes_expiradas NUMBER;
    v_sucesso NUMBER;
    v_mensagem VARCHAR2(4000);
BEGIN
    PRC_EXPIRAR_SESSOES_ANT_LOCAL(v_sessoes_expiradas, v_sucesso, v_mensagem);
END;
/

CREATE OR REPLACE PROCEDURE PRC_ABRIR_SESSAO_FUNC_LOCAL (
    p_username_oracle    IN  VARCHAR2,
    p_modo_login         IN  VARCHAR2,
    p_codigo_sessao      OUT NUMBER,
    p_cod_funcionario    OUT NUMBER,
    p_codigo_no          OUT NUMBER,
    p_nome_funcionario   OUT VARCHAR2,
    p_pode_entrar        OUT NUMBER,
    p_mensagem           OUT VARCHAR2
)
IS
    v_estado_funcionario MV_FUNCIONARIO_LOCAL.estado_funcionario%TYPE;
    v_modo_login VARCHAR2(20);
    v_sessoes NUMBER;
    v_sessoes_expiradas NUMBER;
    v_sucesso_expiracao NUMBER;
    v_mensagem_expiracao VARCHAR2(4000);
BEGIN
    p_codigo_sessao := NULL;
    p_cod_funcionario := NULL;
    p_codigo_no := NULL;
    p_nome_funcionario := NULL;
    p_pode_entrar := 0;
    p_mensagem := NULL;

    v_modo_login := UPPER(TRIM(NVL(p_modo_login, 'ONLINE')));

    IF v_modo_login NOT IN ('ONLINE', 'OFFLINE') THEN
        p_mensagem := 'Modo de login invalido. Seleccione ONLINE ou OFFLINE.';
        RETURN;
    END IF;

    PRC_EXPIRAR_SESSOES_ANT_LOCAL(v_sessoes_expiradas, v_sucesso_expiracao, v_mensagem_expiracao);

    IF v_sucesso_expiracao <> 1 THEN
        p_mensagem := 'Nao foi possivel validar sessoes locais antigas neste momento.';
        RETURN;
    END IF;

    SELECT cod_funcionario,
           codigo_no,
           nome_funcionario,
           estado_funcionario
      INTO p_cod_funcionario,
           p_codigo_no,
           p_nome_funcionario,
           v_estado_funcionario
      FROM MV_FUNCIONARIO_LOCAL
     WHERE UPPER(TRIM(username_oracle)) = UPPER(TRIM(p_username_oracle));

    IF UPPER(TRIM(v_estado_funcionario)) <> 'ACTIVO' THEN
        p_mensagem := 'Este funcionario esta inactivo neste no.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO v_sessoes
      FROM SESSAO_FUNCIONARIO_LOCAL
     WHERE cod_funcionario = p_cod_funcionario
       AND estado_sessao = 'ABERTA';

    IF v_sessoes > 0 THEN
        p_mensagem := 'Este funcionario ja possui uma sessao aberta neste no.';
        RETURN;
    END IF;

    INSERT INTO SESSAO_FUNCIONARIO_LOCAL (
        codigo_sessao,
        cod_funcionario,
        codigo_no,
        data_inicio,
        estado_sessao,
        modo_login
    ) VALUES (
        SEQ_SESSAO_FUNC_LOCAL.NEXTVAL,
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
    WHEN NO_DATA_FOUND THEN
        p_mensagem := 'Funcionario nao encontrado neste no.';
    WHEN TOO_MANY_ROWS THEN
        p_mensagem := 'Username Oracle duplicado neste no.';
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        p_pode_entrar := 0;
        p_mensagem := 'Este funcionario ja possui uma sessao aberta neste no.';
    WHEN OTHERS THEN
        ROLLBACK;
        p_pode_entrar := 0;
        p_mensagem := 'Nao foi possivel abrir a sessao local. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_FECHAR_SESSAO_FUNC_LOCAL (
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
        p_mensagem := 'Informe o codigo da sessao local.';
        RETURN;
    END IF;

    UPDATE SESSAO_FUNCIONARIO_LOCAL
       SET data_fim = SYSDATE,
           estado_sessao = 'FECHADA'
     WHERE codigo_sessao = p_codigo_sessao
       AND estado_sessao = 'ABERTA';

    v_linhas := SQL%ROWCOUNT;

    IF v_linhas = 0 THEN
        ROLLBACK;
        p_mensagem := 'Nao foi encontrada uma sessao local aberta para encerrar.';
        RETURN;
    END IF;

    COMMIT;

    p_sucesso := 1;
    p_mensagem := 'Sessao encerrada com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_sucesso := 0;
        p_mensagem := 'Nao foi possivel fechar a sessao local. Detalhe: ' || SQLERRM;
END;
/
