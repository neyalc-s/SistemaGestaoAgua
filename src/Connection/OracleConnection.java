package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.TimeZone;

public class OracleConnection
{
  private static final String APP_LOGIN_PASSWORD = "20240629";
  //Colocar IPs aqui
  private static final String URL_ADMINISTRADOR = "jdbc:oracle:thin:@//127.0.0.1:1525/XE";
  private static final String URL_FAMILIAS_COTAS = "jdbc:oracle:thin:@//127.0.0.1:1521/XE";
  private static final String URL_DISTRIBUICAO_CONSUMO = "jdbc:oracle:thin:@//127.0.0.1:1522/XE";
  private static final String URL_TRANSFERENCIAS_RECURSOS = "jdbc:oracle:thin:@//127.0.0.1:1523/XE";
  private static final String URL_EQUIPES_GESTAO = "jdbc:oracle:thin:@//127.0.0.1:1524/XE";

  public OracleConnection()
  {}

  public enum NoBD
  {
    ADMINISTRADOR(1, "AdministradorDB", URL_ADMINISTRADOR, "ADM_OWNER", "APP_LOGIN_ADMIN"),
    FAMILIAS_COTAS(2, "FamiliasCotasDB", URL_FAMILIAS_COTAS, "FAM_OWNER", "APP_LOGIN_FAM"),
    DISTRIBUICAO_CONSUMO(3, "DistribuicaoConsumoDB", URL_DISTRIBUICAO_CONSUMO, "DIST_OWNER", "APP_LOGIN_DIST"),
    TRANSFERENCIAS_RECURSOS(4, "TransferenciasRecursosDB", URL_TRANSFERENCIAS_RECURSOS, "TRANS_OWNER", "APP_LOGIN_TRANS"),
    EQUIPES_GESTAO(5, "EquipesGestaoDB", URL_EQUIPES_GESTAO, "EQ_OWNER", "APP_LOGIN_EQ");

    private final int codigo;
    private final String nome;
    private final String url;
    private final String owner;
    private final String loginUser;

    NoBD(int codigo, String nome, String url, String owner, String loginUser)
    {
      this.codigo = codigo;
      this.nome = nome;
      this.url = url;
      this.owner = owner;
      this.loginUser = loginUser;
    }

    public int getCodigo()
    { return codigo; }

    public String getNome()
    { return nome; }

    public String getUrl()
    { return url; }

    public String getOwner()
    { return owner; }

    public String getLoginUser()
    { return loginUser; }

    public static NoBD porCodigo(int codigo) throws SQLException
    {
      for(NoBD no : values())
      {
        if(no.codigo == codigo)
          return no;
      }
      throw new SQLException("Codigo de no desconhecido: " + codigo);
    }
  }

  public static void closeConnection(final Connection connection)
  {
    try
    {
      if(connection != null)
        connection.close();
    } catch(SQLException ex)
    {
      System.out.println(ex.getMessage());
    }
  }

  public static final Connection getConnection(NoBD no, String username, String senha) throws SQLException
  {
    setTimeZone();
    if(no == null)
      throw new SQLException("No da base de dados nao informado.");
    return DriverManager.getConnection(no.getUrl(), username, senha);
  }

  public static final Connection getConnectionLogin(NoBD no) throws SQLException
  {
    if(no == null)
      throw new SQLException("No da base de dados nao informado.");
    return getConnection(no, no.getLoginUser(), APP_LOGIN_PASSWORD);
  }

  public static final Connection getConnectionAdministrador(String username, String senha) throws SQLException
  {
    return getConnection(NoBD.ADMINISTRADOR, username, senha);
  }

  public static final Connection getConnectionFamiliasCotas(String username, String senha) throws SQLException
  {
    return getConnection(NoBD.FAMILIAS_COTAS, username, senha);
  }

  public static final Connection getConnectionDistribuicaoConsumo(String username, String senha) throws SQLException
  {
    return getConnection(NoBD.DISTRIBUICAO_CONSUMO, username, senha);
  }

  public static final Connection getConnectionTransferenciasRecursos(String username, String senha) throws SQLException
  {
    return getConnection(NoBD.TRANSFERENCIAS_RECURSOS, username, senha);
  }

  public static final Connection getConnectionEquipesGestao(String username, String senha) throws SQLException
  {
    return getConnection(NoBD.EQUIPES_GESTAO, username, senha);
  }

  private static void setTimeZone()
  {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}
