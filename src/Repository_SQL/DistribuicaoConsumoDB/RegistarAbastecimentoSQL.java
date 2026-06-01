package Repository_SQL.DistribuicaoConsumoDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class RegistarAbastecimentoSQL
{
  public static final String CONSULTAR_RECURSOS = "SELECT codigo_rh, tipo_rh, localizacao_rh, volume_rh, "
      + "sazonalidade_rh, nivel_exploracao_rh FROM VW_RECURSO_HIDRICO ORDER BY codigo_rh";

  public static final String CONSULTAR_PONTOS_RECURSO = "SELECT codigo_pd, localizacao_pd, tipo_infraestrutura_pd, "
      + "capacidade_armazenamento_pd, volume_actual_pd, estado_operacional_pd FROM VW_PONTOS_POR_RECURSO "
      + "WHERE codigo_rh = ? ORDER BY codigo_pd";

  public static final String PRE_VALIDAR_REGISTO = "{call PRC_PRE_REG_ABASTECIMENTO(?,?,?,?,?)}";
  public static final String REGISTAR_ABASTECIMENTO = "{call PRC_REG_ABASTECIMENTO(?,?,?,?,?,?,?,?)}";
  public static final String FINALIZAR_ABASTECIMENTOS = "{call PRC_FINALIZAR_ABAST(?,?,?)}";

  public static final String CONTAR_ABASTECIMENTOS_ABERTOS =
      "SELECT COUNT(*) FROM VW_ABAST_RECURSO_ABERTO WHERE codigo_rh = ?";

  private RegistarAbastecimentoSQL()
  {}

  public static void carregarRecursos(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_RECURSOS, null, new String[]
      {
          "codigo_rh", "tipo_rh", "localizacao_rh", "volume_rh", "sazonalidade_rh", "nivel_exploracao_rh"
      });
  }

  public static void carregarPontosRecurso(Connection connection, DefaultTableModel modeloTabela, int codigoRh)
      throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_PONTOS_RECURSO, Integer.valueOf(codigoRh), new String[]
      {
          "codigo_pd", "localizacao_pd", "tipo_infraestrutura_pd", "capacidade_armazenamento_pd",
          "volume_actual_pd", "estado_operacional_pd"
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

  public static ResultadoPreValidacao preValidarRegisto(Connection connection, int codigoRh, int codigoPd,
      double volumeAbastecido) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_REGISTO);
      cs.setInt(1, codigoRh);
      cs.setInt(2, codigoPd);
      cs.setDouble(3, volumeAbastecido);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();
      return new ResultadoPreValidacao(cs.getInt(4) == 1, cs.getString(5));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoRegisto registarAbastecimento(Connection connection, int codigoRh, int codigoPd,
      double volumeAbastecido) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_ABASTECIMENTO);
      cs.setInt(1, codigoRh);
      cs.setInt(2, codigoPd);
      cs.setDouble(3, volumeAbastecido);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.NUMERIC);
      cs.registerOutParameter(6, Types.VARCHAR);
      cs.registerOutParameter(7, Types.VARCHAR);
      cs.registerOutParameter(8, Types.VARCHAR);
      cs.execute();

      return new ResultadoRegisto(cs.getInt(4), cs.getInt(5) == 1, cs.getString(6), cs.getString(7),
          cs.getString(8));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoFinalizacao finalizarAbastecimentos(Connection connection) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(FINALIZAR_ABASTECIMENTOS);
      cs.registerOutParameter(1, Types.NUMERIC);
      cs.registerOutParameter(2, Types.NUMERIC);
      cs.registerOutParameter(3, Types.VARCHAR);
      cs.execute();
      return new ResultadoFinalizacao(cs.getInt(1), cs.getInt(2) == 1, cs.getString(3));
    } finally
    {
      fechar(cs);
    }
  }

  public static int contarAbastecimentosAbertosRecurso(Connection connection, int codigoRh) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = connection.prepareStatement(CONTAR_ABASTECIMENTOS_ABERTOS);
      ps.setInt(1, codigoRh);
      rs = ps.executeQuery();
      if(rs.next())
        return rs.getInt(1);
      return 0;
    } finally
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

  public static final class ResultadoRegisto
  {
    public final int codigoAbastecimento;
    public final boolean registado;
    public final String mensagem;
    public final String dataInicio;
    public final String dataFim;

    public ResultadoRegisto(int codigoAbastecimento, boolean registado, String mensagem, String dataInicio,
        String dataFim)
    {
      this.codigoAbastecimento = codigoAbastecimento;
      this.registado = registado;
      this.mensagem = mensagem;
      this.dataInicio = dataInicio;
      this.dataFim = dataFim;
    }
  }

  public static final class ResultadoFinalizacao
  {
    public final int finalizados;
    public final boolean sucesso;
    public final String mensagem;

    public ResultadoFinalizacao(int finalizados, boolean sucesso, String mensagem)
    {
      this.finalizados = finalizados;
      this.sucesso = sucesso;
      this.mensagem = mensagem;
    }
  }
}
