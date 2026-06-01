package Resources;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public final class SessaoFuncional
{
  private static final String FECHAR_SESSAO_LOCAL =
      "{call PRC_FECHAR_SESSAO_FUNC_LOCAL(?, ?, ?)}";

  private static final String FECHAR_SESSAO_ADMINISTRADOR =
      "{call PRC_FECHAR_SESSAO_FUNC(?, ?, ?)}";

  private SessaoFuncional()
  {}

  public static void fecharSessaoLocal(Connection connection, int codigoSessaoLocal)
  {
    tentarFecharSessaoLocal(connection, codigoSessaoLocal);
  }

  public static boolean tentarFecharSessaoLocal(Connection connection, int codigoSessaoLocal)
  {
    if(connection == null || codigoSessaoLocal <= 0)
      return true;

    CallableStatement statement = null;
    try
    {
      if(connection.isClosed())
        return false;

      statement = connection.prepareCall(FECHAR_SESSAO_LOCAL);
      statement.setInt(1, codigoSessaoLocal);
      statement.registerOutParameter(2, Types.NUMERIC);
      statement.registerOutParameter(3, Types.VARCHAR);
      statement.execute();

      return statement.getInt(2) == 1;
    } catch(SQLException ex)
    {
      return false;
    } finally
    {
      if(statement != null)
      {
        try
        {
          statement.close();
        } catch(SQLException ignored)
        {}
      }
    }
  }

  public static void fecharSessaoAdministrador(Connection connection, int codigoSessao)
  {
    tentarFecharSessaoAdministrador(connection, codigoSessao);
  }

  public static boolean tentarFecharSessaoAdministrador(Connection connection, int codigoSessao)
  {
    if(connection == null || codigoSessao <= 0)
      return true;

    CallableStatement statement = null;
    try
    {
      if(connection.isClosed())
        return false;

      statement = connection.prepareCall(FECHAR_SESSAO_ADMINISTRADOR);
      statement.setInt(1, codigoSessao);
      statement.registerOutParameter(2, Types.NUMERIC);
      statement.registerOutParameter(3, Types.VARCHAR);
      statement.execute();

      return statement.getInt(2) == 1;
    } catch(SQLException ex)
    {
      return false;
    } finally
    {
      if(statement != null)
      {
        try
        {
          statement.close();
        } catch(SQLException ignored)
        {}
      }
    }
  }
}
