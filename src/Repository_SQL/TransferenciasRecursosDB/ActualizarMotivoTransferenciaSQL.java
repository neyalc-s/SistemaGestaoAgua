package Repository_SQL.TransferenciasRecursosDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class ActualizarMotivoTransferenciaSQL
{
  public static final String CONSULTAR_TRANSFERENCIAS = "SELECT codigo_tc, cod_fam_doadora_tc, "
      + "nome_responsavel_fb AS nome_doadora, cod_fam_receptora_tc, nome_parceiro AS nome_receptora, "
      + "volume_cedido_tc, motivo_solicitacao_tc, data_aprovacao_tc, validade_transferencia_tc "
      + "FROM vw_transferencias_familia WHERE papel_na_transferencia = 'Doa' ORDER BY data_aprovacao_tc DESC";

  public static final String CONSULTAR_TRANSFERENCIA = "SELECT codigo_tc, cod_fam_doadora_tc, "
      + "nome_responsavel_fb AS nome_doadora, cod_fam_receptora_tc, nome_parceiro AS nome_receptora, "
      + "volume_cedido_tc, motivo_solicitacao_tc, data_aprovacao_tc, validade_transferencia_tc "
      + "FROM vw_transferencias_familia WHERE papel_na_transferencia = 'Doa' AND codigo_tc = ?";

  public static final String PRE_VALIDAR_ACTUALIZACAO = "{call PRC_PRE_ACT_MOTIVO_TRANSF(?,?,?,?,?)}";
  public static final String ACTUALIZAR_MOTIVO = "{call PRC_ACT_MOTIVO_TRANSF(?,?,?,?,?)}";

  private ActualizarMotivoTransferenciaSQL()
  {}

  public static void carregarTransferencias(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_TRANSFERENCIAS);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("codigo_tc"));
        linha.add(rs.getInt("cod_fam_doadora_tc"));
        linha.add(rs.getString("nome_doadora"));
        linha.add(rs.getInt("cod_fam_receptora_tc"));
        linha.add(rs.getString("nome_receptora"));
        linha.add(rs.getObject("volume_cedido_tc"));
        linha.add(rs.getString("motivo_solicitacao_tc"));
        linha.add(rs.getObject("data_aprovacao_tc"));
        linha.add(rs.getObject("validade_transferencia_tc"));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static DadosTransferencia carregarTransferencia(Connection connection, int codigoTransferencia)
      throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = connection.prepareStatement(CONSULTAR_TRANSFERENCIA);
      ps.setInt(1, codigoTransferencia);
      rs = ps.executeQuery();

      if(!rs.next())
        throw new IllegalArgumentException("Transferencia nao encontrada.");

      return new DadosTransferencia(rs.getInt("codigo_tc"), rs.getInt("cod_fam_doadora_tc"),
          rs.getString("nome_doadora"), rs.getInt("cod_fam_receptora_tc"), rs.getString("nome_receptora"),
          rs.getDouble("volume_cedido_tc"), rs.getString("motivo_solicitacao_tc"),
          String.valueOf(rs.getObject("data_aprovacao_tc")), String.valueOf(rs.getObject("validade_transferencia_tc")));
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoOperacao preValidarActualizacao(Connection connection, int codigoTransferencia,
      String motivoOriginal, String novoMotivo) throws Exception
  {
    return executarProcedure(connection, PRE_VALIDAR_ACTUALIZACAO, codigoTransferencia, motivoOriginal, novoMotivo);
  }

  public static ResultadoOperacao actualizarMotivo(Connection connection, int codigoTransferencia,
      String motivoOriginal, String novoMotivo) throws Exception
  {
    return executarProcedure(connection, ACTUALIZAR_MOTIVO, codigoTransferencia, motivoOriginal, novoMotivo);
  }

  private static ResultadoOperacao executarProcedure(Connection connection, String chamada, int codigoTransferencia,
      String motivoOriginal, String novoMotivo) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(chamada);
      cs.setInt(1, codigoTransferencia);
      cs.setString(2, motivoOriginal);
      cs.setString(3, novoMotivo);
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

  public static final class DadosTransferencia
  {
    public final int codigoTransferencia;
    public final int codigoFamiliaDoadora;
    public final String nomeDoadora;
    public final int codigoFamiliaReceptora;
    public final String nomeReceptora;
    public final double volumeCedido;
    public final String motivo;
    public final String dataAprovacao;
    public final String validadeTransferencia;

    public DadosTransferencia(int codigoTransferencia, int codigoFamiliaDoadora, String nomeDoadora,
        int codigoFamiliaReceptora, String nomeReceptora, double volumeCedido, String motivo, String dataAprovacao,
        String validadeTransferencia)
    {
      this.codigoTransferencia = codigoTransferencia;
      this.codigoFamiliaDoadora = codigoFamiliaDoadora;
      this.nomeDoadora = nomeDoadora;
      this.codigoFamiliaReceptora = codigoFamiliaReceptora;
      this.nomeReceptora = nomeReceptora;
      this.volumeCedido = volumeCedido;
      this.motivo = motivo;
      this.dataAprovacao = dataAprovacao;
      this.validadeTransferencia = validadeTransferencia;
    }
  }

  public static final class ResultadoOperacao
  {
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoOperacao(boolean podeContinuar, String mensagem)
    {
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }
}
