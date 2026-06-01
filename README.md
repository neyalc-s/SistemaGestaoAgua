# Como Executar o Programa

## Membros do Grupo

| Nº | Nome | Código de estudante |
|---:|---|---|
| 1 | Igor Cruz dos Santos | 20240459 |
| 2 | Neyal Santilal | 20240629 |
| 3 | Patrícia Da Graça | 20250597 |
| 4 | Stanley Cossa | 20240567 |

---

Este projecto deve ser executado com **5 bases de dados Oracle**, normalmente em **5 VMs separadas**.

## 1. Requisitos

Antes de executar o programa, confirme que tem:

- 5 VMs com Oracle Database instalado;
- listener Oracle activo em cada VM;
- comunicação de rede entre as 5 VMs;
- Java/JDK instalado no computador onde a aplicação será executada;
- bibliotecas da pasta `lib/` mantidas no projecto.

## 2. Nós necessários

O sistema precisa destes 5 nós:

| Nó | Base de dados |
|---|---|
| 1 | `AdministradorDB` |
| 2 | `FamiliasCotasDB` |
| 3 | `DistribuicaoConsumoDB` |
| 4 | `TransferenciasRecursosDB` |
| 5 | `EquipesGestaoDB` |

## 3. Configurar os endereços antes de executar os scripts

Antes de executar qualquer ficheiro `executar_tudo.sql`, configure os endereços das bases de dados.

### 3.1. Configurar os ficheiros `tnsnames.ora`

Actualize os IPs, portas e service names nos ficheiros `tnsnames.ora` de cada nó:

```text
src/SQL_Files/AdministradorDB/tnsnames.ora
src/SQL_Files/FamiliasCotasDB/tnsnames.ora
src/SQL_Files/DistribuicaoConsumoDB/tnsnames.ora
src/SQL_Files/TransferenciasRecursosDB/tnsnames.ora
src/SQL_Files/EquipesGestaoDB/tnsnames.ora
```

Em cada ficheiro `tnsnames.ora`, confirme os valores:

```text
HOST = IP_OU_HOSTNAME_DA_VM
PORT = PORTA_DO_LISTENER
SERVICE_NAME = XE
```

Depois de editar, coloque o `tnsnames.ora` correspondente no local usado pelo Oracle em cada VM.

> Esta etapa deve ser feita antes do `executar_tudo.sql`, porque os DB Links dependem dos nomes configurados no `tnsnames.ora`.

### 3.2. Configurar a conexão Java

Actualize também o ficheiro:

```text
src/Connection/OracleConnection.java
```

Nesse ficheiro, configure as URLs JDBC de cada nó:

```java
private static final String URL_ADMINISTRADOR =
    "jdbc:oracle:thin:@//IP_OU_HOSTNAME:PORTA/XE";
```

Cada URL deve apontar para a VM correcta.

## 4. Executar os scripts SQL

Depois de configurar os ficheiros `tnsnames.ora`, execute os scripts SQL.

A ordem recomendada é:

```text
1. AdministradorDB
2. FamiliasCotasDB
3. TransferenciasRecursosDB
4. DistribuicaoConsumoDB
5. EquipesGestaoDB
```

Em cada VM, entre na pasta SQL do respectivo nó.

Exemplo para `AdministradorDB`:

```bash
cd src/SQL_Files/AdministradorDB
sqlplus / as sysdba
```

Dentro do SQL*Plus, execute:

```sql
@executar_tudo.sql
```

Repita o mesmo processo nos restantes nós.

## 5. Executar a aplicação Java

Na raiz do projecto, execute:

```bash
chmod +x run.sh
./run.sh
```

O script `run.sh` compila e inicia a aplicação Java.

## 6. Verificações rápidas

Antes de fazer login, confirme:

```text
[ ] As 5 VMs estão ligadas
[ ] Os listeners Oracle estão activos
[ ] Os ficheiros tnsnames.ora foram actualizados
[ ] O ficheiro src/Connection/OracleConnection.java foi actualizado
[ ] Os scripts executar_tudo.sql foram executados
[ ] Os DB Links foram criados correctamente
[ ] O ficheiro run.sh tem permissão de execução
```

Depois disso, o programa pode ser usado normalmente pela janela de login.
