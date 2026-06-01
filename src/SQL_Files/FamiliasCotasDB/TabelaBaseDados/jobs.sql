SET ECHO OFF;

-- Agenda tarefas periodicas usadas pela aplicacao.

DECLARE
    v_job NUMBER;
BEGIN
    FOR r IN (
        SELECT job
          FROM user_jobs
         WHERE what = 'BEGIN PRC_JOB_GERAR_COTAS_SEMANAIS; END;'
    ) LOOP
        DBMS_JOB.REMOVE(r.job);
    END LOOP;

    DBMS_JOB.SUBMIT(
        job       => v_job,
        what      => 'BEGIN PRC_JOB_GERAR_COTAS_SEMANAIS; END;',
        next_date => TRUNC(SYSDATE) + 1 + (10 / 1440),
        interval  => 'TRUNC(SYSDATE + 1) + (10/1440)'
    );

    COMMIT;
END;
/
