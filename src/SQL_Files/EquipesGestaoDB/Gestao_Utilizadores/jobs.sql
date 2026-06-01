SET ECHO OFF;

-- Agenda tarefas periodicas usadas pela aplicacao.

DECLARE
    v_job NUMBER;
BEGIN
    FOR r IN (
        SELECT job
          FROM user_jobs
         WHERE what = 'BEGIN PRC_JOB_EXPIRAR_SESSOES_LOCAL; END;'
    ) LOOP
        DBMS_JOB.REMOVE(r.job);
    END LOOP;

    DBMS_JOB.SUBMIT(
        job       => v_job,
        what      => 'BEGIN PRC_JOB_EXPIRAR_SESSOES_LOCAL; END;',
        next_date => SYSDATE,
        interval  => 'SYSDATE + 1/24'
    );

    COMMIT;
END;
/

DECLARE
    v_job NUMBER;
BEGIN
    FOR r IN (
        SELECT job
          FROM USER_JOBS
         WHERE UPPER(what) LIKE '%PRC_JOB_REF_ALERTAS_QUAL%'
    ) LOOP
        DBMS_JOB.REMOVE(r.job);
    END LOOP;

    DBMS_JOB.SUBMIT(
        job       => v_job,
        what      => 'BEGIN PRC_JOB_REF_ALERTAS_QUAL; END;',
        next_date => SYSDATE,
        interval  => 'SYSDATE + (15/1440)'
    );

    COMMIT;
END;
/

DECLARE
    v_job NUMBER;
BEGIN
    FOR r IN (
        SELECT job
          FROM user_jobs
         WHERE what = 'BEGIN PRC_JOB_SYNC_USER_LOCAL; END;'
    ) LOOP
        DBMS_JOB.REMOVE(r.job);
    END LOOP;

    DBMS_JOB.SUBMIT(
        job       => v_job,
        what      => 'BEGIN PRC_JOB_SYNC_USER_LOCAL; END;',
        next_date => SYSDATE,
        interval  => 'SYSDATE + 1/1440'
    );

    COMMIT;
END;
/
