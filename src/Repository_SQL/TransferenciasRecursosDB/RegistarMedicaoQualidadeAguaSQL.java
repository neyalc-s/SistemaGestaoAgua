package Repository_SQL.TransferenciasRecursosDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class RegistarMedicaoQualidadeAguaSQL
{
  public static final String CONSULTAR_RECURSOS = "SELECT codigo_rh, tipo_rh, localizacao_rh, volume_rh, "
      + "sazonalidade_rh, nivel_exploracao_rh FROM VW_RECURSO_HIDRICO ORDER BY codigo_rh";

  public static final String CONSULTAR_ANALISTAS = "SELECT equipe_id, nome, codigo_parametro, nome_parametro, "
      + "unidade_padrao, especialidade_analise, frequencia_amostragem "
      + "FROM VW_ANALISTA_QUALIDADE ORDER BY codigo_parametro, equipe_id";

  public static final String PRE_VALIDAR_REGISTO = "{call PRC_PRE_REG_MED_QUAL_AGUA(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
  public static final String REGISTAR_MEDICOES = "{call PRC_REG_MED_QUAL_AGUA(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

  private RegistarMedicaoQualidadeAguaSQL()
  {}

  public static void carregarRecursos(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_RECURSOS, new String[]
      {
          "codigo_rh", "tipo_rh", "localizacao_rh", "volume_rh", "sazonalidade_rh", "nivel_exploracao_rh"
      });
  }

  public static void carregarAnalistas(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_ANALISTAS, new String[]
      {
          "equipe_id", "nome", "codigo_parametro", "nome_parametro", "unidade_padrao", "especialidade_analise",
          "frequencia_amostragem"
      });
  }

  private static void carregarTabela(Connection connection, DefaultTableModel modeloTabela, String sql,
      String[] colunas) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(sql);
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

  public static ResultadoPreValidacao preValidarRegisto(Connection connection, DadosMedicoes dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_REGISTO);
      preencherParametros(cs, dados);
      cs.registerOutParameter(12, Types.NUMERIC);
      cs.registerOutParameter(13, Types.NUMERIC);
      cs.registerOutParameter(14, Types.VARCHAR);
      cs.execute();
      return new ResultadoPreValidacao(cs.getInt(12), cs.getInt(13) == 1, cs.getString(14));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoRegisto registarMedicoes(Connection connection, DadosMedicoes dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_MEDICOES);
      preencherParametros(cs, dados);
      cs.registerOutParameter(12, Types.NUMERIC);
      cs.registerOutParameter(13, Types.NUMERIC);
      cs.registerOutParameter(14, Types.VARCHAR);
      cs.execute();
      return new ResultadoRegisto(cs.getInt(12), cs.getInt(13) == 1, cs.getString(14));
    } finally
    {
      fechar(cs);
    }
  }

  private static void preencherParametros(CallableStatement cs, DadosMedicoes dados) throws Exception
  {
    cs.setInt(1, dados.codigoRh);
    cs.setInt(2, dados.equipePh);
    cs.setDouble(3, dados.valorPh);
    cs.setInt(4, dados.equipeTurbidez);
    cs.setDouble(5, dados.valorTurbidez);
    cs.setInt(6, dados.equipeTemperatura);
    cs.setDouble(7, dados.valorTemperatura);
    cs.setInt(8, dados.equipeCloro);
    cs.setDouble(9, dados.valorCloro);
    cs.setInt(10, dados.equipeOxigenio);
    cs.setDouble(11, dados.valorOxigenio);
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

  public static final class DadosMedicoes
  {
    public final int codigoRh;
    public final int equipePh;
    public final double valorPh;
    public final int equipeTurbidez;
    public final double valorTurbidez;
    public final int equipeTemperatura;
    public final double valorTemperatura;
    public final int equipeCloro;
    public final double valorCloro;
    public final int equipeOxigenio;
    public final double valorOxigenio;

    public DadosMedicoes(int codigoRh, int equipePh, double valorPh, int equipeTurbidez, double valorTurbidez,
        int equipeTemperatura, double valorTemperatura, int equipeCloro, double valorCloro, int equipeOxigenio,
        double valorOxigenio)
    {
      this.codigoRh = codigoRh;
      this.equipePh = equipePh;
      this.valorPh = valorPh;
      this.equipeTurbidez = equipeTurbidez;
      this.valorTurbidez = valorTurbidez;
      this.equipeTemperatura = equipeTemperatura;
      this.valorTemperatura = valorTemperatura;
      this.equipeCloro = equipeCloro;
      this.valorCloro = valorCloro;
      this.equipeOxigenio = equipeOxigenio;
      this.valorOxigenio = valorOxigenio;
    }
  }

  public static final class ResultadoPreValidacao
  {
    public final int totalMedicoes;
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoPreValidacao(int totalMedicoes, boolean podeContinuar, String mensagem)
    {
      this.totalMedicoes = totalMedicoes;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoRegisto
  {
    public final int totalRegistadas;
    public final boolean registado;
    public final String mensagem;

    public ResultadoRegisto(int totalRegistadas, boolean registado, String mensagem)
    {
      this.totalRegistadas = totalRegistadas;
      this.registado = registado;
      this.mensagem = mensagem;
    }
  }
}
