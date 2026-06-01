package Repository_SQL.DistribuicaoConsumoDB;

import Resources.InterfaceGraficaUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public final class RegistroConsumoConsultasSQL
{
  private static final String COLUNAS_REGISTRO_CONSUMO =
    "codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, " +
      "pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc";

  private static final String CONSULTAR_BASE =
    "SELECT " + COLUNAS_REGISTRO_CONSUMO + " FROM VW_REGISTRO_CONSUMO";

  private static final String ORDENAR_REGISTROS =
    " ORDER BY data_hora_rc DESC, codigo_rc DESC";

  private static final String FILTRO_PESQUISA = " WHERE %s LIKE ?";

  private RegistroConsumoConsultasSQL()
  {}

  public static DefaultTableModel criarModeloVazio()
  {
    return new DefaultTableModel(new Object[]
      {
          "codigo_rc", "codigo_fb", "codigo_pd", "codigo_cota", "data_hora_rc", "volume_retirado_rc",
          "pessoa_coleta_rc", "metodo_autenticacao_rc", "saldo_cota_rc", "observacao_rc"
      }, 0)
      {
        @Override
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
  }

  public static DefaultTableModel consultarTodos(Connection connection) throws Exception
  {
    return executarConsulta(connection, CONSULTAR_BASE + ORDENAR_REGISTROS, null);
  }

  public static DefaultTableModel pesquisar(Connection connection, String criterio, String valor) throws Exception
  {
    String expressaoColuna = obterExpressaoColuna(criterio);
    String sql = CONSULTAR_BASE + String.format(FILTRO_PESQUISA, expressaoColuna) + ORDENAR_REGISTROS;
    return executarConsulta(connection, sql, "%" + valor.trim().toUpperCase() + "%");
  }

  private static String obterExpressaoColuna(String criterio)
  {
    if("Código do Registo".equals(criterio))
      return "UPPER(TO_CHAR(codigo_rc))";
    if("Código da Família".equals(criterio))
      return "UPPER(TO_CHAR(codigo_fb))";
    if("Código do Ponto".equals(criterio))
      return "UPPER(TO_CHAR(codigo_pd))";
    if("Código da Cota".equals(criterio))
      return "UPPER(TO_CHAR(codigo_cota))";
    if("Pessoa de Coleta".equals(criterio))
      return "UPPER(pessoa_coleta_rc)";
    if("Método de Autenticação".equals(criterio))
      return "UPPER(metodo_autenticacao_rc)";
    if("Data/Hora".equals(criterio))
      return "UPPER(TO_CHAR(data_hora_rc, 'YYYY-MM-DD HH24:MI:SS'))";

    return "UPPER(TO_CHAR(codigo_rc))";
  }

  private static DefaultTableModel executarConsulta(Connection connection, String sql, String parametro) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = connection.prepareStatement(sql);
      if(parametro != null)
        ps.setString(1, parametro);

      rs = ps.executeQuery();
      return InterfaceGraficaUtils.criarModeloTabela(rs);
    }
    finally
    {
      try
      {
        if(rs != null)
          rs.close();
      }
      catch(Exception ignored)
      {}

      try
      {
        if(ps != null)
          ps.close();
      }
      catch(Exception ignored)
      {}
    }
  }
}
