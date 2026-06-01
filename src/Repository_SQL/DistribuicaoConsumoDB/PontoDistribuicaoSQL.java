package Repository_SQL.DistribuicaoConsumoDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class PontoDistribuicaoSQL
{
  public static final String CONSULTAR_PONTOS_DISTRIBUICAO =
    "SELECT codigo_pd, localizacao_pd, tipo_infraestrutura_pd, volume_actual_pd, fonte_abastecimento_pd, " +
      "estado_operacional_pd FROM VW_PONTO_DISTRIBUICAO ORDER BY codigo_pd";

  public static final String PRE_VALIDAR_ALTERACAO_ESTADO =
    "{call PRC_PRE_ALT_EST_PONTO(?,?,?,?,?)}";

  public static final String ALTERAR_ESTADO_PONTO =
    "{call PRC_ALT_EST_PONTO(?,?,?,?,?)}";

  public static final String CONTAR_ABASTECIMENTOS_ESTADO =
    "SELECT COUNT(*) FROM VW_HISTORICO_ABASTECIMENTO WHERE codigo_pd = ? AND estado_abastecimento = ?";

  private PontoDistribuicaoSQL()
  {
  }

  public static void carregarPontosDistribuicao(Connection connection, DefaultTableModel modelo) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modelo.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_PONTOS_DISTRIBUICAO);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("codigo_pd"));
        linha.add(rs.getString("localizacao_pd"));
        linha.add(rs.getString("tipo_infraestrutura_pd"));
        linha.add(rs.getObject("volume_actual_pd"));
        linha.add(rs.getString("fonte_abastecimento_pd"));
        linha.add(rs.getString("estado_operacional_pd"));
        modelo.addRow(linha);
      }
    }
    finally
    {
      fechar(rs, ps);
    }
  }

  public static PreValidacaoAlteracaoEstado preValidarAlteracaoEstado(Connection connection, int codigoPd,
    String estadoEsperado) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_ALTERACAO_ESTADO);
      cs.setInt(1, codigoPd);
      cs.setString(2, estadoEsperado);
      cs.registerOutParameter(3, Types.VARCHAR);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();

      return new PreValidacaoAlteracaoEstado(
        codigoPd,
        cs.getString(3),
        cs.getInt(4) == 1,
        cs.getString(5)
      );
    }
    finally
    {
      fechar(cs);
    }
  }

  public static ResultadoAlteracaoEstado alterarEstadoPonto(Connection connection, int codigoPd,
    String estadoEsperado, String novoEstado) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(ALTERAR_ESTADO_PONTO);
      cs.setInt(1, codigoPd);
      cs.setString(2, estadoEsperado);
      cs.setString(3, novoEstado);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();

      return new ResultadoAlteracaoEstado(
        codigoPd,
        novoEstado,
        cs.getInt(4) != 1,
        cs.getString(5)
      );
    }
    finally
    {
      fechar(cs);
    }
  }

  public static int contarAbastecimentosEstado(Connection connection, int codigoPd, String estado) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = connection.prepareStatement(CONTAR_ABASTECIMENTOS_ESTADO);
      ps.setInt(1, codigoPd);
      ps.setString(2, estado);
      rs = ps.executeQuery();
      if(rs.next())
        return rs.getInt(1);
      return 0;
    }
    finally
    {
      fechar(rs, ps);
    }
  }

  private static void fechar(ResultSet rs, PreparedStatement ps)
  {
    try
    {
      if(rs != null)
        rs.close();
    }
    catch(Exception ignored)
    {
    }

    try
    {
      if(ps != null)
        ps.close();
    }
    catch(Exception ignored)
    {
    }
  }

  private static void fechar(CallableStatement cs)
  {
    try
    {
      if(cs != null)
        cs.close();
    }
    catch(Exception ignored)
    {
    }
  }

  public static final class PreValidacaoAlteracaoEstado
  {
    public final int codigoPd;
    public final String estadoActualBanco;
    public final boolean podeContinuar;
    public final String mensagem;

    public PreValidacaoAlteracaoEstado(int codigoPd, String estadoActualBanco, boolean podeContinuar, String mensagem)
    {
      this.codigoPd = codigoPd;
      this.estadoActualBanco = estadoActualBanco;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoAlteracaoEstado
  {
    public final int codigoPd;
    public final String novoEstado;
    public final boolean falhou;
    public final String mensagem;

    public ResultadoAlteracaoEstado(int codigoPd, String novoEstado, boolean falhou, String mensagem)
    {
      this.codigoPd = codigoPd;
      this.novoEstado = novoEstado;
      this.falhou = falhou;
      this.mensagem = mensagem;
    }
  }
}
