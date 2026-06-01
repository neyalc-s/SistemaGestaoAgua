package Repository_SQL.TransferenciasRecursosDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class ActualizarSazonalidadeRecursoHidricoSQL
{
  public static final String CONSULTAR_RECURSOS = "SELECT codigo_rh, tipo_rh, localizacao_rh, volume_rh, "
      + "sazonalidade_rh, vulnerabilidade_rh, nivel_exploracao_rh FROM VW_RECURSO_HIDRICO ORDER BY codigo_rh";

  public static final String PRE_CARREGAR_RECURSO = "{call PRC_PRE_ACT_SAZONALIDADE_RH(?,?,?,?,?,?,?,?,?,?)}";
  public static final String ACTUALIZAR_SAZONALIDADE = "{call PRC_ACT_SAZONALIDADE_RH(?,?,?,?,?)}";

  private ActualizarSazonalidadeRecursoHidricoSQL()
  {}

  public static void carregarRecursos(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_RECURSOS);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("codigo_rh"));
        linha.add(rs.getString("tipo_rh"));
        linha.add(rs.getString("localizacao_rh"));
        linha.add(rs.getObject("volume_rh"));
        linha.add(rs.getString("sazonalidade_rh"));
        linha.add(rs.getString("vulnerabilidade_rh"));
        linha.add(rs.getString("nivel_exploracao_rh"));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static DadosRecurso preCarregarRecurso(Connection connection, int codigoRh) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_CARREGAR_RECURSO);
      cs.setInt(1, codigoRh);
      cs.registerOutParameter(2, Types.NUMERIC);
      cs.registerOutParameter(3, Types.VARCHAR);
      cs.registerOutParameter(4, Types.VARCHAR);
      cs.registerOutParameter(5, Types.NUMERIC);
      cs.registerOutParameter(6, Types.VARCHAR);
      cs.registerOutParameter(7, Types.VARCHAR);
      cs.registerOutParameter(8, Types.VARCHAR);
      cs.registerOutParameter(9, Types.NUMERIC);
      cs.registerOutParameter(10, Types.VARCHAR);
      cs.execute();

      return new DadosRecurso(cs.getInt(2), cs.getString(3), cs.getString(4), cs.getObject(5),
          cs.getString(6), cs.getString(7), cs.getString(8), cs.getInt(9) == 1, cs.getString(10));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoOperacao actualizarSazonalidade(Connection connection, int codigoRh,
      String sazonalidadeActual, String novaSazonalidade) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(ACTUALIZAR_SAZONALIDADE);
      cs.setInt(1, codigoRh);
      cs.setString(2, sazonalidadeActual);
      cs.setString(3, novaSazonalidade);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();
      return new ResultadoOperacao(cs.getInt(4) == 1, cs.getString(5));
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

  public static final class DadosRecurso
  {
    public final int codigoRh;
    public final String tipoRh;
    public final String localizacaoRh;
    public final Object volumeRh;
    public final String sazonalidadeRh;
    public final String vulnerabilidadeRh;
    public final String nivelExploracaoRh;
    public final boolean podeContinuar;
    public final String mensagem;

    public DadosRecurso(int codigoRh, String tipoRh, String localizacaoRh, Object volumeRh, String sazonalidadeRh,
        String vulnerabilidadeRh, String nivelExploracaoRh, boolean podeContinuar, String mensagem)
    {
      this.codigoRh = codigoRh;
      this.tipoRh = tipoRh;
      this.localizacaoRh = localizacaoRh;
      this.volumeRh = volumeRh;
      this.sazonalidadeRh = sazonalidadeRh;
      this.vulnerabilidadeRh = vulnerabilidadeRh;
      this.nivelExploracaoRh = nivelExploracaoRh;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoOperacao
  {
    public final boolean sucesso;
    public final String mensagem;

    public ResultadoOperacao(boolean sucesso, String mensagem)
    {
      this.sucesso = sucesso;
      this.mensagem = mensagem;
    }
  }
}
