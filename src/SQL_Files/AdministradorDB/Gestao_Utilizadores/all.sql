SET ECHO OFF;

-- DB links e sinonimos entram antes das views que dependem deles.

@@dblinks.sql

@@sinonimos.sql
@@sequences.sql
@@criar.sql
@@indexes.sql
@@triggers.sql
@@newInsert.sql
@@materialized_views.sql
@@views.sql
@@dashboard_views.sql
@@auditoria_sessoes.sql
@@procedures.sql
@@jobs.sql

SET ECHO OFF;
