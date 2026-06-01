SET ECHO OFF;

SET DEFINE OFF;

BEGIN EXECUTE IMMEDIATE 'DROP PUBLIC SYNONYM PRC_FECHAR_SESSAO_AUDITORIA'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

DECLARE
    v_sql VARCHAR2(1000);
BEGIN
    FOR r IN (
        SELECT object_name
          FROM user_objects
         WHERE object_type IN ('TABLE', 'VIEW', 'MATERIALIZED VIEW')
           AND object_name NOT LIKE 'BIN$%'
    ) LOOP
        BEGIN
            v_sql := 'GRANT SELECT ON ADM_OWNER.' || r.object_name || ' TO R_ADM_FUNC';
            EXECUTE IMMEDIATE v_sql;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
        BEGIN
            v_sql := 'GRANT SELECT ON ADM_OWNER.' || r.object_name || ' TO ADM_FUNC_01';
            EXECUTE IMMEDIATE v_sql;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
        BEGIN
            v_sql := 'GRANT SELECT ON ADM_OWNER.' || r.object_name || ' TO ADM_FUNC_02';
            EXECUTE IMMEDIATE v_sql;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;
END;
/

DECLARE
    v_sql VARCHAR2(1000);
BEGIN
    FOR r IN (
        SELECT object_name
          FROM user_objects
         WHERE object_type IN ('PROCEDURE', 'FUNCTION')
           AND object_name NOT LIKE 'BIN$%'
    ) LOOP
        BEGIN
            v_sql := 'GRANT EXECUTE ON ADM_OWNER.' || r.object_name || ' TO R_ADM_FUNC';
            EXECUTE IMMEDIATE v_sql;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
        BEGIN
            v_sql := 'GRANT EXECUTE ON ADM_OWNER.' || r.object_name || ' TO ADM_FUNC_01';
            EXECUTE IMMEDIATE v_sql;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
        BEGIN
            v_sql := 'GRANT EXECUTE ON ADM_OWNER.' || r.object_name || ' TO ADM_FUNC_02';
            EXECUTE IMMEDIATE v_sql;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;
END;
/

DECLARE
    v_sql VARCHAR2(1000);
BEGIN
    FOR r IN (
        SELECT object_name
          FROM user_objects
         WHERE object_type IN ('TABLE', 'VIEW', 'PROCEDURE', 'FUNCTION', 'MATERIALIZED VIEW')
           AND object_name NOT LIKE 'BIN$%'
    ) LOOP
        BEGIN
            v_sql := 'DROP PUBLIC SYNONYM ' || r.object_name;
            EXECUTE IMMEDIATE v_sql;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;

        BEGIN
            v_sql := 'CREATE PUBLIC SYNONYM ' || r.object_name || ' FOR ADM_OWNER.' || r.object_name;
            EXECUTE IMMEDIATE v_sql;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;
END;
/

GRANT SELECT ON ADM_OWNER.VW_LOGIN_FUNCIONARIO_ADMIN TO APP_LOGIN_ADMIN;
GRANT SELECT ON ADM_OWNER.VW_LOGIN_NO_ADMIN TO APP_LOGIN_ADMIN;

SET ECHO OFF;
