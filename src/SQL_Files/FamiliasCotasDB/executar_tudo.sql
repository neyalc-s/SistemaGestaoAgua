SET ECHO ON;

-- Instala FamiliasCotasDB na VM1.
-- Para uma instalacao limpa, executar os nos na ordem: VM5, VM1, VM3, VM2, VM4.

-- Base Oracle e utilizadores.
CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/tablespaces.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/criar_utilizadores_roles.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/perfil_funcionario.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/activar_auditoria_oracle.sql

-- Objectos de integracao do owner local.
CONNECT FAM_OWNER/"20240629"
@Gestao_Utilizadores/all.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/trigger_sys.sql

-- Modelo de dados e regras do no.
CONNECT FAM_OWNER/"20240629"
@TabelaBaseDados/all.sql

-- Permissoes da interface Java.
CONNECT FAM_OWNER/"20240629"
@Gestao_Utilizadores/grants_roles.sql

CONNECT FAM_OWNER/"20240629"
@Gestao_Utilizadores/sinonimos.sql

-- Utilizadores remotos para DB links recebidos.
CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/PermissoesDistribuicaoConsumoDB.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/PermissoesTransferenciasRecursosDB.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/PermissoesAdministradorDB.sql

SET ECHO OFF;
