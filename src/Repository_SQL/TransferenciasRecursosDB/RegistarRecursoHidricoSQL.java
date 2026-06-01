package Repository_SQL.TransferenciasRecursosDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class RegistarRecursoHidricoSQL
{
  public static final String CONSULTAR_MEDIDAS = "SELECT cod_medida_proteccao, cod_responsavel, "
      + "nome_responsavel, descricao_medida "
      + "FROM VW_MEDIDA_PROTECCAO_RESP ORDER BY cod_medida_proteccao";

  public static final String CONSULTAR_ANALISTAS = "SELECT equipe_id, nome, codigo_parametro, nome_parametro, "
      + "unidade_padrao, especialidade_analise, frequencia_amostragem "
      + "FROM VW_ANALISTA_QUALIDADE ORDER BY codigo_parametro, equipe_id";

  public static final String PRE_VALIDAR_REGISTO = "{call PRC_PRE_REG_RECURSO_HIDRICO(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
  public static final String REGISTAR_RECURSO = "{call PRC_REGISTAR_RECURSO_HIDRICO(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

  private RegistarRecursoHidricoSQL()
  {}

  public static void carregarMedidas(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_MEDIDAS, new String[]
      {
          "cod_medida_proteccao", "cod_responsavel", "nome_responsavel", "descricao_medida"
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
  }

  public static ResultadoPreValidacao preValidarRegisto(Connection connection, DadosRecurso dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_REGISTO);
      preencherParametrosRecurso(cs, dados);
      cs.registerOutParameter(18, Types.NUMERIC);
      cs.registerOutParameter(19, Types.VARCHAR);
      cs.execute();
      return new ResultadoPreValidacao(cs.getInt(18) == 1, cs.getString(19));
    } finally
    {
      try
      {
        if(cs != null)
          cs.close();
      } catch(Exception ignored)
      {}
    }
  }

  public static ResultadoRegisto registarRecurso(Connection connection, DadosRecurso dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_RECURSO);
      preencherParametrosRecurso(cs, dados);
      cs.registerOutParameter(18, Types.NUMERIC);
      cs.registerOutParameter(19, Types.NUMERIC);
      cs.registerOutParameter(20, Types.VARCHAR);
      cs.execute();
      return new ResultadoRegisto(cs.getInt(18), cs.getInt(19) == 1, cs.getString(20));
    } finally
    {
      try
      {
        if(cs != null)
          cs.close();
      } catch(Exception ignored)
      {}
    }
  }

  private static void preencherParametrosRecurso(CallableStatement cs, DadosRecurso dados) throws Exception
  {
    cs.setString(1, dados.tipoRh);
    cs.setString(2, dados.localizacaoRh);
    cs.setDouble(3, dados.volumeRh);
    cs.setString(4, dados.sazonalidadeRh);
    cs.setString(5, dados.vulnerabilidadeRh);
    cs.setString(6, dados.nivelExploracaoRh);
    if(dados.codMedidas == null || dados.codMedidas.trim().isEmpty())
      cs.setNull(7, Types.VARCHAR);
    else
      cs.setString(7, dados.codMedidas);
    cs.setInt(8, dados.equipePh);
    cs.setDouble(9, dados.valorPh);
    cs.setInt(10, dados.equipeTurbidez);
    cs.setDouble(11, dados.valorTurbidez);
    cs.setInt(12, dados.equipeTemperatura);
    cs.setDouble(13, dados.valorTemperatura);
    cs.setInt(14, dados.equipeCloro);
    cs.setDouble(15, dados.valorCloro);
    cs.setInt(16, dados.equipeOxigenio);
    cs.setDouble(17, dados.valorOxigenio);
  }

  public static final class DadosRecurso
  {
    public final String tipoRh;
    public final String localizacaoRh;
    public final double volumeRh;
    public final String sazonalidadeRh;
    public final String vulnerabilidadeRh;
    public final String nivelExploracaoRh;
    public final String codMedidas;
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

    public DadosRecurso(String tipoRh, String localizacaoRh, double volumeRh, String sazonalidadeRh,
        String vulnerabilidadeRh, String nivelExploracaoRh, String codMedidas, int equipePh, double valorPh,
        int equipeTurbidez, double valorTurbidez, int equipeTemperatura, double valorTemperatura, int equipeCloro,
        double valorCloro, int equipeOxigenio, double valorOxigenio)
    {
      this.tipoRh = tipoRh;
      this.localizacaoRh = localizacaoRh;
      this.volumeRh = volumeRh;
      this.sazonalidadeRh = sazonalidadeRh;
      this.vulnerabilidadeRh = vulnerabilidadeRh;
      this.nivelExploracaoRh = nivelExploracaoRh;
      this.codMedidas = codMedidas;
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
    public final int codigoRecurso;
    public final boolean registado;
    public final String mensagem;

    public ResultadoRegisto(int codigoRecurso, boolean registado, String mensagem)
    {
      this.codigoRecurso = codigoRecurso;
      this.registado = registado;
      this.mensagem = mensagem;
    }
  }
}
