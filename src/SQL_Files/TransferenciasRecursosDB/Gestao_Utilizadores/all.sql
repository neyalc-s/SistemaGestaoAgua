SET ECHO OFF;

-- DB links e sinonimos entram antes das views que dependem deles.

@@dblinks.sql

@@sinonimos.sql
@@materialized_views.sql
@@views.sql
@@sessao_funcionario_local.sql
@@auditoria_sessoes.sql
@@procedures.sql
@@jobs.sql
@@sinonimos.sql

SET ECHO OFF;
