package Repository_SQL.AdministradorDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class AlertasQualidadeAguaAdminSQL
{
  public static final String REFRESH_ALERTAS =
      "{call PRC_REFRESH_ALERTA_QUAL_ADMIN(?, ?)}";

  public static final String COLUNAS_ALERTA =
      "codigo_alerta, codigo_medicao, codigo_rh, mensagem_alerta, data_alerta";

  public static final String CONSULTAR_ALERTAS_BASE =
      "SELECT " + COLUNAS_ALERTA + " FROM VW_ALERTA_QUALIDADE_ADMIN WHERE 1 = 1 ";


  public static final String FILTRO_PESQUISA_ALERTA =
      "AND (TO_CHAR(codigo_alerta) LIKE ? OR TO_CHAR(codigo_medicao) LIKE ? "
          + "OR TO_CHAR(codigo_rh) LIKE ? OR UPPER(mensagem_alerta) LIKE ?) ";

  public static final String ORDENAR_ALERTAS =
      "ORDER BY data_alerta DESC, codigo_alerta DESC";

  private AlertasQualidadeAguaAdminSQL()
  {}

  public static ResultadoRefresh actualizarAlertas(Connection connection) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REFRESH_ALERTAS);
      cs.registerOutParameter(1, Types.NUMERIC);
      cs.registerOutParameter(2, Types.VARCHAR);
      cs.execute();
      return new ResultadoRefresh(cs.getInt(1) == 1, cs.getString(2));
    } finally
    {
      if(cs != null)
        cs.close();
    }
  }

  public static List<AlertaQualidade> pesquisar(Connection connection, String pesquisa) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<Object> params = new ArrayList<Object>();
    StringBuilder sql = new StringBuilder();

    sql.append(CONSULTAR_ALERTAS_BASE);

    if(temTexto(pesquisa))
    {
      sql.append(FILTRO_PESQUISA_ALERTA);
      params.add("%" + pesquisa.trim() + "%");
      params.add("%" + pesquisa.trim() + "%");
      params.add("%" + pesquisa.trim() + "%");
      params.add("%" + pesquisa.trim().toUpperCase() + "%");
    }

    sql.append(ORDENAR_ALERTAS);

    try
    {
      ps = connection.prepareStatement(sql.toString());
      for(int i = 0; i < params.size(); i++)
        ps.setObject(i + 1, params.get(i));

      rs = ps.executeQuery();
      List<AlertaQualidade> alertas = new ArrayList<AlertaQualidade>();
      while(rs.next())
        alertas.add(new AlertaQualidade(rs.getInt("codigo_alerta"), rs.getInt("codigo_medicao"),
            rs.getInt("codigo_rh"), rs.getString("mensagem_alerta"), rs.getTimestamp("data_alerta")));
      return alertas;
    } finally
    {
      fechar(rs, ps);
    }
  }

  private static boolean temTexto(String valor)
  {
    return valor != null && valor.trim().length() > 0;
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

  public static final class AlertaQualidade
  {
    public final int codigoAlerta;
    public final int codigoMedicao;
    public final int codigoRh;
    public final String mensagemAlerta;
    public final java.util.Date dataAlerta;

    public AlertaQualidade(int codigoAlerta, int codigoMedicao, int codigoRh, String mensagemAlerta,
        java.util.Date dataAlerta)
    {
      this.codigoAlerta = codigoAlerta;
      this.codigoMedicao = codigoMedicao;
      this.codigoRh = codigoRh;
      this.mensagemAlerta = mensagemAlerta;
      this.dataAlerta = dataAlerta;
    }
  }

  public static final class ResultadoRefresh
  {
    public final boolean sucesso;
    public final String mensagem;

    public ResultadoRefresh(boolean sucesso, String mensagem)
    {
      this.sucesso = sucesso;
      this.mensagem = mensagem;
    }
  }
}
