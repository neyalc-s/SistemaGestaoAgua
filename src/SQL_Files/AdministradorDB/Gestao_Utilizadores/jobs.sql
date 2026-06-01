SET ECHO OFF;

-- Agenda tarefas periodicas usadas pela aplicacao.

DECLARE
    v_job NUMBER;
BEGIN
    FOR r IN (
        SELECT job
          FROM USER_JOBS
         WHERE UPPER(what) LIKE '%PRC_JOB_REF_ALERTA_QUAL_ADMIN%'
    ) LOOP
        DBMS_JOB.REMOVE(r.job);
    END LOOP;

    DBMS_JOB.SUBMIT(
        job       => v_job,
        what      => 'BEGIN PRC_JOB_REF_ALERTA_QUAL_ADMIN; END;',
        next_date => SYSDATE,
        interval  => 'SYSDATE + (30/1440)'
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
         WHERE what = 'BEGIN PRC_JOB_REF_AUD_SESSOES; END;'
    ) LOOP
        DBMS_JOB.REMOVE(r.job);
    END LOOP;

    DBMS_JOB.SUBMIT(
        job       => v_job,
        what      => 'BEGIN PRC_JOB_REF_AUD_SESSOES; END;',
        next_date => SYSDATE,
        interval  => 'SYSDATE + (30/1440)'
    );
    COMMIT;
END;
/
