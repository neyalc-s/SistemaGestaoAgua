package Repository_SQL.DistribuicaoConsumoDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class RetirarAguaSQL
{
  public static final String CONSULTAR_PONTOS_DISTRIBUICAO =
    "SELECT codigo_pd, localizacao_pd, tipo_infraestrutura_pd, " +
      "volume_actual_pd, fonte_abastecimento_pd, estado_operacional_pd " +
      "FROM VW_PONTO_DISTRIBUICAO " +
      "ORDER BY codigo_pd";

  public static final String CONSULTAR_FAMILIAS_DO_PONTO =
    "SELECT codigo_fb, nome_responsavel_fb, contacto_fb, num_membros_fb, perfil_socioeconomico_fb, estado_fb " +
      "FROM VW_FAMILIA_BENEFICIARIA " +
      "WHERE codigo_pd = ? " +
      "ORDER BY codigo_fb";

  public static final String CONSULTAR_COTAS_DA_FAMILIA =
    "SELECT codigo_cota, volume_semanal_ca, saldo_disponivel_ca, periodo_validade_ca, " +
      "ajuste_sazonal_ca, transferencia_autorizada_ca, status_validade " +
      "FROM VW_COTA_AGUA_STATUS " +
      "WHERE codigo_fb = ? " +
      "ORDER BY codigo_cota DESC";

  public static final String PRE_VALIDAR_RETIRADA =
    "{call PRC_PRE_VALIDAR_RETIRADA_AGUA(?,?,?,?,?,?,?,?,?,?)}";

  public static final String REGISTAR_RETIRADA =
    "{call PRC_RETIRAR_AGUA(?,?,?,?,?,?,?,?)}";

  private RetirarAguaSQL()
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

  public static void carregarFamiliasDoPonto(Connection connection, DefaultTableModel modelo, int codigoPd)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modelo.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_FAMILIAS_DO_PONTO);
      ps.setInt(1, codigoPd);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("codigo_fb"));
        linha.add(rs.getString("nome_responsavel_fb"));
        linha.add(rs.getString("contacto_fb"));
        linha.add(rs.getInt("num_membros_fb"));
        linha.add(rs.getString("perfil_socioeconomico_fb"));
        linha.add(rs.getString("estado_fb"));
        modelo.addRow(linha);
      }
    }
    finally
    {
      fechar(rs, ps);
    }
  }

  public static void carregarCotasDaFamilia(Connection connection, DefaultTableModel modelo, int codigoFb)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modelo.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_COTAS_DA_FAMILIA);
      ps.setInt(1, codigoFb);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("codigo_cota"));
        linha.add(rs.getObject("volume_semanal_ca"));
        linha.add(rs.getObject("saldo_disponivel_ca"));
        linha.add(rs.getObject("periodo_validade_ca"));
        linha.add(rs.getString("ajuste_sazonal_ca"));
        linha.add(rs.getString("transferencia_autorizada_ca"));
        linha.add(rs.getString("status_validade"));
        modelo.addRow(linha);
      }
    }
    finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoRetirada registarRetirada(Connection connection, DadosRetirada dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_RETIRADA);

      cs.setString(1, dados.nomePessoaColeta);
      cs.setDouble(2, dados.volumeARetirar);

      if(dados.observacao == null || dados.observacao.isEmpty())
        cs.setNull(3, Types.VARCHAR);
      else
        cs.setString(3, dados.observacao);

      cs.setInt(4, dados.codigoPd);
      cs.setInt(5, dados.codigoFb);
      cs.setInt(6, dados.codigoCota);

      cs.registerOutParameter(7, Types.NUMERIC);
      cs.registerOutParameter(8, Types.VARCHAR);

      cs.execute();

      int codigoGerado = cs.getInt(7);
      boolean falhou = cs.wasNull();
      return new ResultadoRetirada(codigoGerado, falhou, cs.getString(8));
    }
    finally
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
  }

  public static PreValidacaoRetirada preValidarRetirada(Connection connection, int codigoPd, int codigoFb,
    int codigoCota, double volumeARetirar) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_RETIRADA);

      cs.setInt(1, codigoPd);
      cs.setInt(2, codigoFb);
      cs.setInt(3, codigoCota);
      cs.setDouble(4, volumeARetirar);

      cs.registerOutParameter(5, Types.VARCHAR);
      cs.registerOutParameter(6, Types.NUMERIC);
      cs.registerOutParameter(7, Types.NUMERIC);
      cs.registerOutParameter(8, Types.DATE);
      cs.registerOutParameter(9, Types.NUMERIC);
      cs.registerOutParameter(10, Types.VARCHAR);

      cs.execute();

      return new PreValidacaoRetirada(
        cs.getString(5),
        cs.getDouble(6),
        cs.getDouble(7),
        cs.getDate(8),
        cs.getInt(9) == 1,
        cs.getString(10)
      );
    }
    finally
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

  public static final class DadosRetirada
  {
    public final String nomePessoaColeta;
    public final double volumeARetirar;
    public final String observacao;
    public final int codigoPd;
    public final int codigoFb;
    public final int codigoCota;

    public DadosRetirada(String nomePessoaColeta, double volumeARetirar, String observacao, int codigoPd,
      int codigoFb, int codigoCota)
    {
      this.nomePessoaColeta = nomePessoaColeta;
      this.volumeARetirar = volumeARetirar;
      this.observacao = observacao;
      this.codigoPd = codigoPd;
      this.codigoFb = codigoFb;
      this.codigoCota = codigoCota;
    }
  }

  public static final class ResultadoRetirada
  {
    public final int codigoRegistoConsumoGerado;
    public final boolean falhou;
    public final String mensagem;
    public final boolean pendente;

    public ResultadoRetirada(int codigoRegistoConsumoGerado, boolean falhou, String mensagem)
    {
      this.codigoRegistoConsumoGerado = codigoRegistoConsumoGerado;
      this.falhou = falhou;
      this.mensagem = mensagem;
      this.pendente = mensagem != null && mensagem.trim().toUpperCase().startsWith("PENDENTE:");
    }
  }

  public static final class PreValidacaoRetirada
  {
    public final String estadoPonto;
    public final double volumeActualPonto;
    public final double saldoCota;
    public final java.sql.Date validadeCota;
    public final boolean podeContinuar;
    public final String mensagem;

    public PreValidacaoRetirada(String estadoPonto, double volumeActualPonto, double saldoCota,
      java.sql.Date validadeCota, boolean podeContinuar, String mensagem)
    {
      this.estadoPonto = estadoPonto;
      this.volumeActualPonto = volumeActualPonto;
      this.saldoCota = saldoCota;
      this.validadeCota = validadeCota;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }
}
