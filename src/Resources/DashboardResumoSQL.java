package Resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class DashboardResumoSQL
{
  private DashboardResumoSQL()
  {}

  public static int contar(Connection connection, String sql) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();
      return rs.next() ? rs.getInt(1) : 0;
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static List<LinhaResumo> carregarLinhas(Connection connection, String sql) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<LinhaResumo> linhas = new ArrayList<LinhaResumo>();
    try
    {
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next())
        linhas.add(new LinhaResumo(rs.getString(1), rs.getString(2), rs.getString(3)));
      return linhas;
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static boolean responde(Connection connection, String sql)
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();
      return rs.next();
    } catch(Exception ex)
    {
      return false;
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static String usuarioLigado(Connection connection, String fallback)
  {
    if(fallback != null && fallback.trim().length() > 0)
      return fallback;

    try
    {
      if(connection != null && connection.getMetaData() != null)
        return connection.getMetaData().getUserName();
    } catch(Exception ignored)
    {}
    return "Funcionário";
  }

  private static void fechar(ResultSet rs, PreparedStatement ps)
  {
    try
    {
      if(rs != null)
        rs.close();
    } catch(Exception ignored)
    {}

    try
    {
      if(ps != null)
        ps.close();
    } catch(Exception ignored)
    {}
  }

  public static final class LinhaResumo
  {
    public final String principal;
    public final String detalhe;
    public final String valor;

    public LinhaResumo(String principal, String detalhe, String valor)
    {
      this.principal = principal;
      this.detalhe = detalhe;
      this.valor = valor;
    }
  }
}
