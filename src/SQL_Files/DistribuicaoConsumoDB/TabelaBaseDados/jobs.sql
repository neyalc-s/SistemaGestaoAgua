SET ECHO OFF;

-- Agenda tarefas periodicas usadas pela aplicacao.

DECLARE
    v_job NUMBER;
BEGIN
    FOR r IN (
        SELECT job
          FROM USER_JOBS
         WHERE UPPER(what) LIKE '%PRC_FINALIZAR_ABAST_JOB%'
    ) LOOP
        DBMS_JOB.REMOVE(r.job);
    END LOOP;

    DBMS_JOB.SUBMIT(
        job       => v_job,
        what      => 'BEGIN PRC_FINALIZAR_ABAST_JOB; END;',
        next_date => SYSDATE,
        interval  => 'SYSDATE + (1/1440)'
    );

    FOR r IN (
        SELECT job
          FROM USER_JOBS
         WHERE UPPER(what) LIKE '%PRC_PROC_RETIRADAS_PEND%'
    ) LOOP
        DBMS_JOB.REMOVE(r.job);
    END LOOP;

    DBMS_JOB.SUBMIT(
        job       => v_job,
        what      => 'BEGIN PRC_PROC_RETIRADAS_PEND; END;',
        next_date => SYSDATE,
        interval  => 'SYSDATE + (5/1440)'
    );
    COMMIT;
END;
/
