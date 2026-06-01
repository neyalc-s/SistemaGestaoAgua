SET ECHO ON;

-- Instala AdministradorDB na VM5.
-- Para uma instalacao limpa, executar os nos na ordem: VM5, VM1, VM3, VM2, VM4.

-- Base Oracle e utilizadores.
CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/tablespaces.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/criar_utilizadores_roles.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/perfil_funcionario.sql

-- Objectos de integracao do owner local.
CONNECT ADM_OWNER/"20240629"
@Gestao_Utilizadores/all.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/trigger_sys.sql

-- Permissoes da interface Java.
CONNECT ADM_OWNER/"20240629"
@Gestao_Utilizadores/grants_roles.sql

-- Utilizadores remotos para DB links recebidos.
CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/PermissoesFamiliasCotasDB.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/PermissoesDistribuicaoConsumoDB.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/PermissoesTransferenciasRecursosDB.sql

CONNECT sys/bd2.isctem AS SYSDBA
@Gestao_Utilizadores/PermissoesEquipesGestaoDB.sql

SET ECHO OFF;
