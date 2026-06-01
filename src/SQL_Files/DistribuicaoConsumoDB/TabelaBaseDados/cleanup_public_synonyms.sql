SET ECHO OFF;

-- Remove sinonimos publicos antigos que podem esconder objectos locais.

DROP PUBLIC SYNONYM MV_DADOS_RETIRADA_AGUA;
DROP PUBLIC SYNONYM VW_COTA_AGUA_STATUS;
DROP PUBLIC SYNONYM VW_FAMILIA_BENEFICIARIA;

DECLARE
BEGIN
    FOR r IN (
        SELECT synonym_name
          FROM all_synonyms
         WHERE owner = 'PUBLIC'
           AND (
               table_owner = USER
               OR synonym_name IN (
                   'MV_DADOS_RETIRADA_AGUA',
                   'VW_COTA_AGUA_STATUS',
                   'VW_FAMILIA_BENEFICIARIA'
               )
           )
    ) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP PUBLIC SYNONYM ' || r.synonym_name;
        EXCEPTION
            WHEN OTHERS THEN NULL;
        END;
    END LOOP;
END;
/
