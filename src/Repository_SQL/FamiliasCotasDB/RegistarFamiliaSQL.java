package Repository_SQL.FamiliasCotasDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class RegistarFamiliaSQL
{
  public static final String CONSULTAR_PONTOS_DISTRIBUICAO = "SELECT codigo_pd, localizacao_pd, tipo_infraestrutura_pd, "
      + "capacidade_armazenamento_pd, fonte_abastecimento_pd, estado_operacional_pd " + "FROM VW_PONTO_DISTRIBUICAO "
      + "ORDER BY codigo_pd";

  public static final String CONSULTAR_NECESSIDADES = "SELECT cod_necessidade, descricao_necessidade "
      + "FROM VW_NECESSIDADE ORDER BY cod_necessidade";


  public static final String CONSULTAR_PONTOS_POR_FILTRO = "SELECT codigo_pd, localizacao_pd, tipo_infraestrutura_pd, "
      + "capacidade_armazenamento_pd, fonte_abastecimento_pd, estado_operacional_pd "
      + "FROM VW_PONTO_DISTRIBUICAO WHERE UPPER(%s) LIKE UPPER(?) ORDER BY codigo_pd";

  public static final String CONSULTAR_NECESSIDADES_POR_FILTRO = "SELECT cod_necessidade, descricao_necessidade "
      + "FROM VW_NECESSIDADE WHERE UPPER(%s) LIKE UPPER(?) ORDER BY cod_necessidade";

  public static final String INSERIR_NECESSIDADE = "{call PRC_INSERIR_NECESSIDADE(?,?,?)}";

  public static final String ASSOCIAR_NECESSIDADE_FAMILIA = "{call PRC_ASSOC_NECESSIDADE_FAMILIA(?,?,?)}";

  public static final String PRE_VALIDAR_REGISTO_FAMILIA = "{call PRC_PRE_VALDAR_REGISTAR_FAMIL(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

  public static final String REGISTAR_FAMILIA = "{call PRC_INSERIR_FAMILIA(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

  private RegistarFamiliaSQL()
  {}

  public static void carregarPontosDistribuicao(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_PONTOS_DISTRIBUICAO);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("codigo_pd"));
        linha.add(rs.getString("localizacao_pd"));
        linha.add(rs.getString("tipo_infraestrutura_pd"));
        linha.add(rs.getObject("capacidade_armazenamento_pd"));
        linha.add(rs.getString("fonte_abastecimento_pd"));
        linha.add(rs.getString("estado_operacional_pd"));
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

  public static void pesquisarPontosDistribuicao(Connection connection, DefaultTableModel modeloTabela, String criterio,
      String texto) throws Exception
  {
    String colunaFiltro = getColunaFiltroPonto(criterio);
    String sql = String.format(CONSULTAR_PONTOS_POR_FILTRO, colunaFiltro);

    pesquisarComParametro(connection, modeloTabela, sql, texto);
  }

  public static void carregarNecessidades(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_NECESSIDADES);
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

  public static void pesquisarNecessidades(Connection connection, DefaultTableModel modeloTabela, String criterio,
      String texto) throws Exception
  {
    String colunaFiltro = getColunaFiltroNecessidade(criterio);
    String sql = String.format(CONSULTAR_NECESSIDADES_POR_FILTRO, colunaFiltro);

    pesquisarComParametro(connection, modeloTabela, sql, texto);
  }

  private static void pesquisarComParametro(Connection connection, DefaultTableModel modeloTabela, String sql,
      String texto) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(sql);
      ps.setString(1, texto.trim() + "%");
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();

        if(modeloTabela.getColumnCount() == 6)
        {
          linha.add(rs.getInt("codigo_pd"));
          linha.add(rs.getString("localizacao_pd"));
          linha.add(rs.getString("tipo_infraestrutura_pd"));
          linha.add(rs.getObject("capacidade_armazenamento_pd"));
          linha.add(rs.getString("fonte_abastecimento_pd"));
          linha.add(rs.getString("estado_operacional_pd"));
        }
        else
        {
          linha.add(rs.getInt("cod_necessidade"));
          linha.add(rs.getString("descricao_necessidade"));
        }

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

  private static String getColunaFiltroPonto(String criterio)
  {
    if("Código".equals(criterio))
      return "TO_CHAR(codigo_pd)";
    if("Localização".equals(criterio))
      return "localizacao_pd";
    if("Tipo de Infraestrutura".equals(criterio))
      return "tipo_infraestrutura_pd";
    if("Fonte de Abastecimento".equals(criterio))
      return "fonte_abastecimento_pd";
    if("Estado Operacional".equals(criterio))
      return "estado_operacional_pd";

    return "TO_CHAR(codigo_pd)";
  }

  private static String getColunaFiltroNecessidade(String criterio)
  {
    if("Código".equals(criterio))
      return "TO_CHAR(cod_necessidade)";
    if("Descrição".equals(criterio))
      return "descricao_necessidade";

    return "descricao_necessidade";
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

      if(cs.getInt(2) == 0 && cs.wasNull())
        throw new Exception(cs.getString(3));
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

  public static void associarNecessidadesFamilia(Connection connection, int codigoFamilia, List<Integer> necessidades)
      throws Exception
  {
    if(necessidades == null || necessidades.isEmpty())
      return;

    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(ASSOCIAR_NECESSIDADE_FAMILIA);

      for(Integer codigoNecessidade : necessidades)
      {
        cs.setInt(1, codigoNecessidade.intValue());
        cs.setInt(2, codigoFamilia);
        cs.registerOutParameter(3, Types.VARCHAR);
        cs.execute();

        String mensagem = cs.getString(3);
        if(mensagem != null && mensagem.startsWith("ERRO:"))
          throw new Exception(mensagem);
      }
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

  public static ResultadoRegistoFamilia registarFamilia(Connection connection, DadosRegistoFamilia dados)
      throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_FAMILIA);

      cs.setInt(1, dados.codigoPd);
      cs.setString(2, dados.nomeResponsavelFb);
      cs.setString(3, dados.contactoFb);
      cs.setInt(4, dados.numMembrosFb);
      cs.setString(5, dados.perfilSocioeconomicoFb);
      cs.setString(6, dados.aldeia);
      cs.setString(7, dados.coordenadasGps);
      cs.setString(8, dados.ajusteSazonalCa);
      cs.setString(9, dados.transferenciaAutorizada);

      if(dados.volumeSemanalOpcional == null)
        cs.setNull(10, Types.NUMERIC);
      else
        cs.setDouble(10, dados.volumeSemanalOpcional.doubleValue());

      cs.registerOutParameter(11, Types.NUMERIC);
      cs.registerOutParameter(12, Types.NUMERIC);
      cs.registerOutParameter(13, Types.NUMERIC);
      cs.registerOutParameter(14, Types.NUMERIC);
      cs.registerOutParameter(15, Types.VARCHAR);

      cs.execute();

      int codigoFamiliaGerado = cs.getInt(11);
      boolean falhou = cs.wasNull();

      return new ResultadoRegistoFamilia(codigoFamiliaGerado, cs.getInt(12), cs.getInt(13), cs.getDouble(14), falhou,
          cs.getString(15));
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

  public static PreValidacaoRegistoFamilia preValidarRegistoFamilia(Connection connection, DadosRegistoFamilia dados,
      List<Integer> necessidadesSelecionadas) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_REGISTO_FAMILIA);

      cs.setInt(1, dados.codigoPd);
      cs.setString(2, dados.nomeResponsavelFb);
      cs.setString(3, dados.contactoFb);
      cs.setInt(4, dados.numMembrosFb);
      cs.setString(5, dados.perfilSocioeconomicoFb);
      cs.setString(6, dados.aldeia);
      cs.setString(7, dados.coordenadasGps);
      cs.setString(8, dados.ajusteSazonalCa);
      cs.setString(9, dados.transferenciaAutorizada);

      if(dados.volumeSemanalOpcional == null)
        cs.setNull(10, Types.NUMERIC);
      else
        cs.setDouble(10, dados.volumeSemanalOpcional.doubleValue());

      String codigosNecessidades = formatarCodigosNecessidades(necessidadesSelecionadas);
      if(codigosNecessidades == null)
        cs.setNull(11, Types.VARCHAR);
      else
        cs.setString(11, codigosNecessidades);

      cs.registerOutParameter(12, Types.VARCHAR);
      cs.registerOutParameter(13, Types.NUMERIC);
      cs.registerOutParameter(14, Types.NUMERIC);
      cs.registerOutParameter(15, Types.NUMERIC);
      cs.registerOutParameter(16, Types.VARCHAR);

      cs.execute();

      return new PreValidacaoRegistoFamilia(cs.getString(12), cs.getDouble(13), cs.getDouble(14), cs.getInt(15) == 1,
          cs.getString(16));
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

  private static String formatarCodigosNecessidades(List<Integer> necessidadesSelecionadas)
  {
    if(necessidadesSelecionadas == null || necessidadesSelecionadas.isEmpty())
      return null;

    StringBuilder texto = new StringBuilder();

    for(int i = 0; i < necessidadesSelecionadas.size(); i++)
    {
      if(i > 0)
        texto.append(",");

      texto.append(necessidadesSelecionadas.get(i));
    }

    return texto.toString();
  }

  public static final class DadosRegistoFamilia
  {
    public final int codigoPd;
    public final String nomeResponsavelFb;
    public final String contactoFb;
    public final int numMembrosFb;
    public final String perfilSocioeconomicoFb;
    public final String aldeia;
    public final String coordenadasGps;
    public final String ajusteSazonalCa;
    public final String transferenciaAutorizada;
    public final Double volumeSemanalOpcional;

    public DadosRegistoFamilia(int codigoPd, String nomeResponsavelFb, String contactoFb, int numMembrosFb,
        String perfilSocioeconomicoFb, String aldeia, String coordenadasGps, String ajusteSazonalCa,
        String transferenciaAutorizada, Double volumeSemanalOpcional)
    {
      this.codigoPd = codigoPd;
      this.nomeResponsavelFb = nomeResponsavelFb;
      this.contactoFb = contactoFb;
      this.numMembrosFb = numMembrosFb;
      this.perfilSocioeconomicoFb = perfilSocioeconomicoFb;
      this.aldeia = aldeia;
      this.coordenadasGps = coordenadasGps;
      this.ajusteSazonalCa = ajusteSazonalCa;
      this.transferenciaAutorizada = transferenciaAutorizada;
      this.volumeSemanalOpcional = volumeSemanalOpcional;
    }
  }

  public static final class ResultadoRegistoFamilia
  {
    public final int codigoFamiliaGerado;
    public final int codigoCotaGerada;
    public final int codigoLocalizacaoGerada;
    public final double volumeCalculado;
    public final boolean falhou;
    public final String mensagem;

    public ResultadoRegistoFamilia(int codigoFamiliaGerado, int codigoCotaGerada, int codigoLocalizacaoGerada,
        double volumeCalculado, boolean falhou, String mensagem)
    {
      this.codigoFamiliaGerado = codigoFamiliaGerado;
      this.codigoCotaGerada = codigoCotaGerada;
      this.codigoLocalizacaoGerada = codigoLocalizacaoGerada;
      this.volumeCalculado = volumeCalculado;
      this.falhou = falhou;
      this.mensagem = mensagem;
    }
  }

  public static final class PreValidacaoRegistoFamilia
  {
    public final String estadoPonto;
    public final double volumeActualPonto;
    public final double volumeCalculado;
    public final boolean podeContinuar;
    public final String mensagem;

    public PreValidacaoRegistoFamilia(String estadoPonto, double volumeActualPonto, double volumeCalculado,
        boolean podeContinuar, String mensagem)
    {
      this.estadoPonto = estadoPonto;
      this.volumeActualPonto = volumeActualPonto;
      this.volumeCalculado = volumeCalculado;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }
}
