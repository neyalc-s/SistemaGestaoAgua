SET ECHO OFF;

BEGIN
    FOR r IN (
        SELECT object_name
        FROM user_objects
        WHERE object_type IN ('VIEW', 'MATERIALIZED VIEW')
    ) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'GRANT SELECT ON ' || r.object_name || ' TO R_EQ_FUNC';
            EXECUTE IMMEDIATE 'GRANT SELECT ON ' || r.object_name || ' TO EQ_FUNC_01';
            EXECUTE IMMEDIATE 'GRANT SELECT ON ' || r.object_name || ' TO EQ_FUNC_02';
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
    END LOOP;
END;
/

BEGIN
    FOR r IN (
        SELECT object_name
        FROM user_objects
        WHERE object_type IN ('PROCEDURE', 'FUNCTION')
    ) LOOP
        EXECUTE IMMEDIATE 'GRANT EXECUTE ON ' || r.object_name || ' TO R_EQ_FUNC';
        EXECUTE IMMEDIATE 'GRANT EXECUTE ON ' || r.object_name || ' TO EQ_FUNC_01';
        EXECUTE IMMEDIATE 'GRANT EXECUTE ON ' || r.object_name || ' TO EQ_FUNC_02';
    END LOOP;
END;
/

GRANT SELECT ON EQ_OWNER.VW_LOGIN_FUNCIONARIO_LOCAL TO APP_LOGIN_EQ;
GRANT SELECT ON EQ_OWNER.VW_TESTE_CONEXAO_DIST_CONS TO R_EQ_FUNC;
GRANT SELECT ON EQ_OWNER.VW_TESTE_CONEXAO_TRANS_REC TO R_EQ_FUNC;

SET ECHO OFF;
