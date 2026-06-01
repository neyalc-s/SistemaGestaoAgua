SET ECHO OFF;

BEGIN EXECUTE IMMEDIATE 'DROP INDEX ux_func_user_upper'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP INDEX ux_funcionario_username_upper'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE UNIQUE INDEX ux_funcionario_username_upper ON FUNCIONARIO (UPPER(TRIM(username_oracle)));
CREATE INDEX ix_func_no_estado ON FUNCIONARIO (codigo_no, estado_funcionario);
CREATE INDEX ix_func_nome_upper ON FUNCIONARIO (UPPER(nome_funcionario));
CREATE INDEX ix_sess_func_estado ON SESSAO_FUNCIONARIO (cod_funcionario, estado_sessao);
CREATE INDEX ix_sess_no_estado ON SESSAO_FUNCIONARIO (codigo_no, estado_sessao);
CREATE INDEX ix_sess_inicio ON SESSAO_FUNCIONARIO (data_inicio);
