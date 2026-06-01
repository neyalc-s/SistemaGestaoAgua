package Repository_SQL.TransferenciasRecursosDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class ActualizarMedidasRecursoHidricoSQL
{
  public static final String CONSULTAR_RECURSOS = "SELECT codigo_rh, tipo_rh, localizacao_rh, volume_rh, "
      + "sazonalidade_rh, nivel_exploracao_rh FROM VW_RECURSO_HIDRICO ORDER BY codigo_rh";

  public static final String CONSULTAR_MEDIDAS_APLICADAS = "SELECT cod_medida_proteccao, descricao_medida, "
      + "nome_responsavel, data_impl FROM VW_REC_APLICA_MED_PROT WHERE codigo_rh = ? "
      + "ORDER BY cod_medida_proteccao";

  public static final String CONSULTAR_MEDIDAS_DISPONIVEIS = "SELECT cod_medida_proteccao, "
      + "descricao_medida, nome_responsavel FROM VW_MEDIDAS_DISPONIVEIS_RH "
      + "WHERE codigo_rh = ? ORDER BY cod_medida_proteccao";

  public static final String PRE_VALIDAR_ACTUALIZACAO = "{call PRC_PRE_ACT_MED_REC_HIDRICO(?,?,?,?,?,?)}";
  public static final String ACTUALIZAR_MEDIDAS = "{call PRC_ACT_MED_REC_HIDRICO(?,?,?,?,?,?,?)}";

  private ActualizarMedidasRecursoHidricoSQL()
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
        linha.add(rs.getString("nivel_exploracao_rh"));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static void carregarMedidasAplicadas(Connection connection, DefaultTableModel modeloTabela, int codigoRh)
      throws Exception
  {
    carregarMedidas(connection, modeloTabela, CONSULTAR_MEDIDAS_APLICADAS, codigoRh, true);
  }

  public static void carregarMedidasDisponiveis(Connection connection, DefaultTableModel modeloTabela, int codigoRh)
      throws Exception
  {
    carregarMedidas(connection, modeloTabela, CONSULTAR_MEDIDAS_DISPONIVEIS, codigoRh, false);
  }

  private static void carregarMedidas(Connection connection, DefaultTableModel modeloTabela, String sql, int codigoRh,
      boolean incluiData) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(sql);
      ps.setInt(1, codigoRh);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("cod_medida_proteccao"));
        linha.add(rs.getString("descricao_medida"));
        linha.add(rs.getString("nome_responsavel"));
        if(incluiData)
          linha.add(rs.getDate("data_impl"));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoPreValidacao preValidarActualizacao(Connection connection, int codigoRh,
      List<Integer> codigosMedidas, String operacao, LocalDate dataImplementacao) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_ACTUALIZACAO);
      cs.setInt(1, codigoRh);
      cs.setString(2, formatarCodigos(codigosMedidas));
      cs.setString(3, operacao);
      setData(cs, 4, dataImplementacao);
      cs.registerOutParameter(5, Types.NUMERIC);
      cs.registerOutParameter(6, Types.VARCHAR);
      cs.execute();
      return new ResultadoPreValidacao(cs.getInt(5) == 1, cs.getString(6));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoActualizacao actualizarMedidas(Connection connection, int codigoRh,
      List<Integer> codigosMedidas, String operacao, LocalDate dataImplementacao) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(ACTUALIZAR_MEDIDAS);
      cs.setInt(1, codigoRh);
      cs.setString(2, formatarCodigos(codigosMedidas));
      cs.setString(3, operacao);
      setData(cs, 4, dataImplementacao);
      cs.registerOutParameter(5, Types.NUMERIC);
      cs.registerOutParameter(6, Types.NUMERIC);
      cs.registerOutParameter(7, Types.VARCHAR);
      cs.execute();
      return new ResultadoActualizacao(cs.getInt(5), cs.getInt(6) == 1, cs.getString(7));
    } finally
    {
      fechar(cs);
    }
  }

  private static void setData(CallableStatement cs, int indice, LocalDate data) throws Exception
  {
    if(data == null)
      cs.setNull(indice, Types.DATE);
    else
      cs.setDate(indice, Date.valueOf(data));
  }

  private static String formatarCodigos(List<Integer> codigos)
  {
    if(codigos == null || codigos.isEmpty())
      return null;

    StringBuilder texto = new StringBuilder();
    for(int i = 0; i < codigos.size(); i++)
    {
      if(i > 0)
        texto.append(',');
      texto.append(codigos.get(i));
    }
    return texto.toString();
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

  public static final class ResultadoActualizacao
  {
    public final int afectadas;
    public final boolean actualizado;
    public final String mensagem;

    public ResultadoActualizacao(int afectadas, boolean actualizado, String mensagem)
    {
      this.afectadas = afectadas;
      this.actualizado = actualizado;
      this.mensagem = mensagem;
    }
  }
}
