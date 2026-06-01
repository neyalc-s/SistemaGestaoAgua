package Login;

import Connection.OracleConnection.NoBD;
import java.sql.Connection;

public final class ResultadoLogin
{
  private final boolean sucesso;
  private final String mensagem;
  private final NoBD no;
  private final int codigoNo;
  private final String nomeNo;
  private final String usernameOracle;
  private final int codFuncionario;
  private final String nomeFuncionario;
  private final int codigoSessaoLocal;
  private final Connection connection;
  private final boolean loginViaFallback;

  public ResultadoLogin(boolean sucesso, String mensagem, NoBD no, int codigoNo, String nomeNo, String usernameOracle,
      int codFuncionario, String nomeFuncionario, int codigoSessaoLocal, Connection connection,
      boolean loginViaFallback)
  {
    this.sucesso = sucesso;
    this.mensagem = mensagem;
    this.no = no;
    this.codigoNo = codigoNo;
    this.nomeNo = nomeNo;
    this.usernameOracle = usernameOracle;
    this.codFuncionario = codFuncionario;
    this.nomeFuncionario = nomeFuncionario;
    this.codigoSessaoLocal = codigoSessaoLocal;
    this.connection = connection;
    this.loginViaFallback = loginViaFallback;
  }

  public boolean isSucesso()
  { return sucesso; }

  public String getMensagem()
  { return mensagem; }

  public NoBD getNo()
  { return no; }

  public int getCodigoNo()
  { return codigoNo; }

  public String getNomeNo()
  { return nomeNo; }

  public String getUsernameOracle()
  { return usernameOracle; }

  public int getCodFuncionario()
  { return codFuncionario; }

  public String getNomeFuncionario()
  { return nomeFuncionario; }

  public int getCodigoSessaoLocal()
  { return codigoSessaoLocal; }

  public Connection getConnection()
  { return connection; }

  public boolean isLoginViaFallback()
  { return loginViaFallback; }
}
