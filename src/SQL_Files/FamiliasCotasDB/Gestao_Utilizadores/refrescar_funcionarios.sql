SET ECHO OFF;

-- Actualiza a copia local dos funcionarios deste no.

BEGIN
    DBMS_SNAPSHOT.REFRESH('MV_FUNCIONARIO_LOCAL', 'C');
END;
/
