package Repository_SQL.FamiliasCotasDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class ActualizarNecessidadeFamiliasSQL
{
  public static final String CONSULTAR_FAMILIAS = "SELECT codigo_fb, nome_responsavel_fb, contacto_fb, "
      + "num_membros_fb, estado_fb, codigo_pd, aldeia FROM VW_FAMILIA_LOCALIZACAO ORDER BY codigo_fb";

  public static final String CONSULTAR_NECESSIDADES_FAMILIA = "SELECT cod_necessidade, descricao_necessidade "
      + "FROM VW_NECESSIDADE_FAMILIA WHERE codigo_fb = ? ORDER BY cod_necessidade";

  public static final String CONSULTAR_NECESSIDADES_DISPONIVEIS = "SELECT cod_necessidade, descricao_necessidade "
      + "FROM VW_NECESSIDADE_DISP_FAMILIA WHERE codigo_fb = ? ORDER BY cod_necessidade";

  public static final String PRE_VALIDAR_ACTUALIZACAO = "{call PRC_PRE_ACT_NEC_FAMILIA(?,?,?,?,?)}";
  public static final String ACTUALIZAR_NECESSIDADES = "{call PRC_ACT_NEC_FAMILIA(?,?,?,?,?,?)}";
  public static final String INSERIR_NECESSIDADE = "{call PRC_INSERIR_NECESSIDADE(?,?,?)}";

  private ActualizarNecessidadeFamiliasSQL()
  {}

  public static void carregarFamilias(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_FAMILIAS);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("codigo_fb"));
        linha.add(rs.getString("nome_responsavel_fb"));
        linha.add(rs.getString("contacto_fb"));
        linha.add(rs.getObject("num_membros_fb"));
        linha.add(rs.getString("estado_fb"));
        linha.add(rs.getObject("codigo_pd"));
        linha.add(rs.getString("aldeia"));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static void carregarNecessidadesFamilia(Connection connection, DefaultTableModel modeloTabela,
      int codigoFamilia) throws Exception
  {
    carregarNecessidades(connection, modeloTabela, CONSULTAR_NECESSIDADES_FAMILIA, codigoFamilia);
  }

  public static void carregarNecessidadesDisponiveis(Connection connection, DefaultTableModel modeloTabela,
      int codigoFamilia) throws Exception
  {
    carregarNecessidades(connection, modeloTabela, CONSULTAR_NECESSIDADES_DISPONIVEIS, codigoFamilia);
  }

  private static void carregarNecessidades(Connection connection, DefaultTableModel modeloTabela, String sql,
      int codigoFamilia) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(sql);
      ps.setInt(1, codigoFamilia);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("cod_necessidade"));
        linha.add(rs.getString("descricao_necessidade"));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoPreValidacao preValidarActualizacao(Connection connection, int codigoFamilia,
      List<Integer> codigosNecessidades, String operacao) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_ACTUALIZACAO);
      cs.setInt(1, codigoFamilia);
      cs.setString(2, formatarCodigos(codigosNecessidades));
      cs.setString(3, operacao);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();
      return new ResultadoPreValidacao(cs.getInt(4) == 1, cs.getString(5));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoActualizacao actualizarNecessidades(Connection connection, int codigoFamilia,
      List<Integer> codigosNecessidades, String operacao) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(ACTUALIZAR_NECESSIDADES);
      cs.setInt(1, codigoFamilia);
      cs.setString(2, formatarCodigos(codigosNecessidades));
      cs.setString(3, operacao);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.NUMERIC);
      cs.registerOutParameter(6, Types.VARCHAR);
      cs.execute();
      return new ResultadoActualizacao(cs.getInt(4), cs.getInt(5) == 1, cs.getString(6));
    } finally
    {
      fechar(cs);
    }
  }

  public static void inserirNecessidade(Connection connection, String descricao) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(INSERIR_NECESSIDADE);
      cs.setString(1, descricao);
      cs.registerOutParameter(2, Types.NUMERIC);
      cs.registerOutParameter(3, Types.VARCHAR);
      cs.execute();

      if(cs.getInt(2) != 1)
        throw new Exception(cs.getString(3));
    } finally
    {
      fechar(cs);
    }
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
