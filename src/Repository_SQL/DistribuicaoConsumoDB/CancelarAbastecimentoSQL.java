package Repository_SQL.DistribuicaoConsumoDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class CancelarAbastecimentoSQL
{
  public static final String CONSULTAR_RECURSOS = "SELECT codigo_rh, tipo_rh, localizacao_rh, volume_rh, "
      + "sazonalidade_rh, nivel_exploracao_rh FROM VW_RECURSO_HIDRICO ORDER BY codigo_rh";

  public static final String CONSULTAR_ABASTECIMENTOS = "SELECT cod_abastecimento, codigo_pd, "
      + "localizacao_pd, volume_abastecido, data_inicio, data_fim, duracao_horas, "
      + "estado_abastecimento FROM VW_ABAST_CANCELAVEL "
      + "WHERE codigo_rh = ? ORDER BY data_inicio";

  public static final String PRE_VALIDAR_CANCELAMENTO = "{call PRC_PRE_CANCELAR_ABAST(?,?,?)}";
  public static final String CANCELAR_ABASTECIMENTO = "{call PRC_CANCELAR_ABAST(?,?,?,?)}";

  private CancelarAbastecimentoSQL()
  {}

  public static void carregarRecursos(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_RECURSOS, null, new String[]
      {
          "codigo_rh", "tipo_rh", "localizacao_rh", "volume_rh", "sazonalidade_rh", "nivel_exploracao_rh"
      });
  }

  public static void carregarAbastecimentos(Connection connection, DefaultTableModel modeloTabela, int codigoRh)
      throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_ABASTECIMENTOS, Integer.valueOf(codigoRh), new String[]
      {
          "cod_abastecimento", "codigo_pd", "localizacao_pd", "volume_abastecido", "data_inicio", "data_fim",
          "duracao_horas", "estado_abastecimento"
      });
  }

  private static void carregarTabela(Connection connection, DefaultTableModel modeloTabela, String sql,
      Integer parametro, String[] colunas) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(sql);
      if(parametro != null)
        ps.setInt(1, parametro.intValue());
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        for(String coluna : colunas)
          linha.add(rs.getObject(coluna));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoPreValidacao preValidarCancelamento(Connection connection, int codigoAbastecimento)
      throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_CANCELAMENTO);
      cs.setInt(1, codigoAbastecimento);
      cs.registerOutParameter(2, Types.NUMERIC);
      cs.registerOutParameter(3, Types.VARCHAR);
      cs.execute();
      return new ResultadoPreValidacao(cs.getInt(2) == 1, cs.getString(3));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoCancelamento cancelarAbastecimento(Connection connection, int codigoAbastecimento)
      throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(CANCELAR_ABASTECIMENTO);
      cs.setInt(1, codigoAbastecimento);
      cs.registerOutParameter(2, Types.NUMERIC);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.VARCHAR);
      cs.execute();
      return new ResultadoCancelamento(cs.getDouble(2), cs.getInt(3) == 1, cs.getString(4));
    } finally
    {
      fechar(cs);
    }
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

  private static void fechar(CallableStatement cs)
  {
    try
    {
      if(cs != null)
        cs.close();
    } catch(Exception ignored)
    {}
  }

  public static final class ResultadoPreValidacao
  {
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoPreValidacao(boolean podeContinuar, String mensagem)
    {
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoCancelamento
  {
    public final double volumeEntregue;
    public final boolean cancelado;
    public final String mensagem;

    public ResultadoCancelamento(double volumeEntregue, boolean cancelado, String mensagem)
    {
      this.volumeEntregue = volumeEntregue;
      this.cancelado = cancelado;
      this.mensagem = mensagem;
    }
  }
}
