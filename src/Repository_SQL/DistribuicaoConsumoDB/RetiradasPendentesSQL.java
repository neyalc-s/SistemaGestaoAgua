package Repository_SQL.DistribuicaoConsumoDB;

import Resources.InterfaceGraficaUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public final class RetiradasPendentesSQL
{
  private static final String COLUNAS =
    "cod_retirada_pendente, codigo_fb, codigo_cota, codigo_pd, localizacao_pd, volume_retirado, " +
      "pessoa_coleta, data_pedido, estado_pendente, data_processamento, mensagem_processamento";

  private static final String CONSULTAR_BASE =
    "SELECT " + COLUNAS + " FROM VW_RETIRADA_AGUA_PENDENTE";

  private static final String ORDENAR =
    " ORDER BY data_pedido DESC, cod_retirada_pendente DESC";

  private static final String PROCESSAR_PENDENTES =
    "{call PRC_PROC_RETIRADAS_PEND}";

  private RetiradasPendentesSQL()
  {}

  public static DefaultTableModel criarModeloVazio()
  {
    return new DefaultTableModel(new Object[]
      {
          "Código Pendência", "Família", "Cota", "Ponto", "Localização", "Volume", "Pessoa de Coleta",
          "Data Pedido", "Estado", "Data Processamento", "Mensagem"
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
    return executarConsulta(connection, CONSULTAR_BASE + ORDENAR, null);
  }

  public static DefaultTableModel pesquisar(Connection connection, String criterio, String valor) throws Exception
  {
    String coluna = obterExpressaoColuna(criterio);
    return executarConsulta(connection, CONSULTAR_BASE + " WHERE " + coluna + " LIKE ?" + ORDENAR,
        "%" + valor.trim().toUpperCase() + "%");
  }

  public static void processarPendentes(Connection connection) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PROCESSAR_PENDENTES);
      cs.execute();
    }
    finally
    {
      if(cs != null)
        cs.close();
    }
  }

  private static String obterExpressaoColuna(String criterio)
  {
    if("Código Pendência".equals(criterio))
      return "UPPER(TO_CHAR(cod_retirada_pendente))";
    if("Família".equals(criterio))
      return "UPPER(TO_CHAR(codigo_fb))";
    if("Cota".equals(criterio))
      return "UPPER(TO_CHAR(codigo_cota))";
    if("Ponto".equals(criterio))
      return "UPPER(TO_CHAR(codigo_pd))";
    if("Estado".equals(criterio))
      return "UPPER(estado_pendente)";
    if("Pessoa de Coleta".equals(criterio))
      return "UPPER(pessoa_coleta)";

    return "UPPER(TO_CHAR(cod_retirada_pendente))";
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
