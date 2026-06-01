package Repository_SQL.DistribuicaoConsumoDB;

import Resources.InterfaceGraficaUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public final class HistoricoManutencaoConsultasSQL
{
  private static final String CONSULTAR_BASE =
    "SELECT * FROM VW_HISTORICO_MANUTENCAO_DET";

  private static final String ORDENAR_MANUTENCOES =
    " ORDER BY data_manutencao DESC, cod_historico_manutencao DESC";

  private static final String FILTRO_PESQUISA = " WHERE %s LIKE ?";

  private HistoricoManutencaoConsultasSQL()
  {}

  public static DefaultTableModel criarModeloVazio()
  {
    return new DefaultTableModel(new Object[]
      {
          "cod_historico_manutencao", "codigo_pd", "equipe_id", "data_manutencao", "tipo_manutencao",
          "localizacao_pd", "estado_operacional_pd", "nome_tecnico", "area_actuacao"
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
    return executarConsulta(connection, CONSULTAR_BASE + ORDENAR_MANUTENCOES, null);
  }

  public static DefaultTableModel pesquisar(Connection connection, String criterio, String valor) throws Exception
  {
    String expressaoColuna = obterExpressaoColuna(criterio);
    String sql = CONSULTAR_BASE + String.format(FILTRO_PESQUISA, expressaoColuna) + ORDENAR_MANUTENCOES;
    return executarConsulta(connection, sql, "%" + valor.trim().toUpperCase() + "%");
  }

  private static String obterExpressaoColuna(String criterio)
  {
    if("Código".equals(criterio))
      return "UPPER(TO_CHAR(cod_historico_manutencao))";
    if("Ponto de distribuição".equals(criterio))
      return "UPPER(TO_CHAR(codigo_pd))";
    if("Equipe técnica".equals(criterio))
      return "UPPER(TO_CHAR(equipe_id))";
    if("Data".equals(criterio))
      return "UPPER(TO_CHAR(data_manutencao, 'YYYY-MM-DD'))";
    if("Tipo/descrição".equals(criterio))
      return "UPPER(tipo_manutencao)";

    return "UPPER(TO_CHAR(cod_historico_manutencao))";
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
