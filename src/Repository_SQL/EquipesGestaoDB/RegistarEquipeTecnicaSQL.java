package Repository_SQL.EquipesGestaoDB;

import Resources.InterfaceGraficaUtils;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import javax.swing.table.DefaultTableModel;

public final class RegistarEquipeTecnicaSQL
{
  public static final String CONSULTAR_PARAMETROS =
      "SELECT codigo_parametro, nome_parametro, unidade_padrao FROM VW_PARAMETRO_QUALIDADE_EQ ORDER BY nome_parametro";

  public static final String CONSULTAR_FERRAMENTAS_DISPONIVEIS =
      "SELECT cod_ferramenta_disponivel, nome_ferramenta FROM VW_FERRAMENTA_MANUTENCAO "
          + "WHERE equipe_id IS NULL ORDER BY nome_ferramenta";

  public static final String PRE_VALIDAR_REGISTO =
      "{call PRC_PRE_REG_EQUIPE_TEC(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

  public static final String REGISTAR_EQUIPE =
      "{call PRC_REG_EQUIPE_TEC(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

  public static final String REGISTAR_PARAMETRO =
      "{call PRC_REG_PARAM_QUALIDADE_LOCAL(?,?,?,?,?)}";

  private RegistarEquipeTecnicaSQL()
  {}

  public static DefaultTableModel carregarParametros(Connection connection) throws Exception
  {
    return InterfaceGraficaUtils.carregarModeloTabela(connection, CONSULTAR_PARAMETROS);
  }

  public static DefaultTableModel carregarFerramentasDisponiveis(Connection connection) throws Exception
  {
    return InterfaceGraficaUtils.carregarModeloTabela(connection, CONSULTAR_FERRAMENTAS_DISPONIVEIS);
  }

  public static ResultadoValidacao preValidar(Connection connection, DadosEquipe dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_REGISTO);
      preencherDados(cs, dados);
      cs.registerOutParameter(21, Types.NUMERIC);
      cs.registerOutParameter(22, Types.VARCHAR);
      cs.execute();
      return new ResultadoValidacao(cs.getInt(21) == 1, cs.getString(22));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoRegisto registar(Connection connection, DadosEquipe dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      if("Analista de Qualidade".equals(dados.tipoEquipe) && dados.codigoParametro == null)
      {
        ResultadoParametro parametro = registarParametro(connection, dados.novoParametroNome, dados.novoParametroUnidade);
        if(!parametro.registado)
          return new ResultadoRegisto(0, false, parametro.mensagem);
        dados = dados.comCodigoParametro(Integer.valueOf(parametro.codigoParametro));
      }

      cs = connection.prepareCall(REGISTAR_EQUIPE);
      preencherDados(cs, dados);
      cs.registerOutParameter(21, Types.NUMERIC);
      cs.registerOutParameter(22, Types.NUMERIC);
      cs.registerOutParameter(23, Types.VARCHAR);
      cs.execute();
      return new ResultadoRegisto(cs.getInt(21), cs.getInt(22) == 1, cs.getString(23));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoParametro registarParametro(Connection connection, String nome, String unidade) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_PARAMETRO);
      cs.setString(1, nome);
      cs.setString(2, unidade);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();
      return new ResultadoParametro(cs.getInt(3), cs.getInt(4) == 1, cs.getString(5));
    } finally
    {
      fechar(cs);
    }
  }

  private static void preencherDados(CallableStatement cs, DadosEquipe dados) throws Exception
  {
    cs.setString(1, dados.nome);
    cs.setString(2, dados.areaActuacao);
    cs.setString(3, dados.nivelFormacao);
    cs.setString(4, dados.contacto);
    cs.setString(5, dados.supervisorResponsavel);
    cs.setString(6, dados.tipoEquipe);
    cs.setString(7, dados.habilidadeTecnica);
    if(dados.tempoMedioResposta == null)
      cs.setNull(8, Types.NUMERIC);
    else
      cs.setInt(8, dados.tempoMedioResposta.intValue());
    cs.setString(9, dados.ferramentasIds);
    cs.setString(10, dados.novasFerramentas);
    if(dados.codigoParametro == null)
      cs.setNull(11, Types.NUMERIC);
    else
      cs.setInt(11, dados.codigoParametro.intValue());
    cs.setString(12, dados.novoParametroNome);
    cs.setString(13, dados.novoParametroUnidade);
    cs.setString(14, dados.especialidadeAnalise);
    cs.setString(15, dados.frequenciaAmostragem);
    cs.setString(16, dados.equipamentosAnalista);
    cs.setString(17, dados.metodologiaSensibilizacao);
    cs.setString(18, dados.linguaLocal);
    cs.setString(19, dados.comunidadeAtendida);
    cs.setString(20, dados.materiaisEducador);
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

  public static final class DadosEquipe
  {
    public final String nome;
    public final String areaActuacao;
    public final String nivelFormacao;
    public final String contacto;
    public final String supervisorResponsavel;
    public final String tipoEquipe;
    public final String habilidadeTecnica;
    public final Integer tempoMedioResposta;
    public final String ferramentasIds;
    public final String novasFerramentas;
    public final Integer codigoParametro;
    public final String novoParametroNome;
    public final String novoParametroUnidade;
    public final String especialidadeAnalise;
    public final String frequenciaAmostragem;
    public final String equipamentosAnalista;
    public final String metodologiaSensibilizacao;
    public final String linguaLocal;
    public final String comunidadeAtendida;
    public final String materiaisEducador;

    public DadosEquipe(String nome, String areaActuacao, String nivelFormacao, String contacto,
        String supervisorResponsavel, String tipoEquipe, String habilidadeTecnica, Integer tempoMedioResposta,
        String ferramentasIds, String novasFerramentas, Integer codigoParametro, String novoParametroNome,
        String novoParametroUnidade, String especialidadeAnalise, String frequenciaAmostragem,
        String equipamentosAnalista, String metodologiaSensibilizacao, String linguaLocal,
        String comunidadeAtendida, String materiaisEducador)
    {
      this.nome = nome;
      this.areaActuacao = areaActuacao;
      this.nivelFormacao = nivelFormacao;
      this.contacto = contacto;
      this.supervisorResponsavel = supervisorResponsavel;
      this.tipoEquipe = tipoEquipe;
      this.habilidadeTecnica = habilidadeTecnica;
      this.tempoMedioResposta = tempoMedioResposta;
      this.ferramentasIds = ferramentasIds;
      this.novasFerramentas = novasFerramentas;
      this.codigoParametro = codigoParametro;
      this.novoParametroNome = novoParametroNome;
      this.novoParametroUnidade = novoParametroUnidade;
      this.especialidadeAnalise = especialidadeAnalise;
      this.frequenciaAmostragem = frequenciaAmostragem;
      this.equipamentosAnalista = equipamentosAnalista;
      this.metodologiaSensibilizacao = metodologiaSensibilizacao;
      this.linguaLocal = linguaLocal;
      this.comunidadeAtendida = comunidadeAtendida;
      this.materiaisEducador = materiaisEducador;
    }

    public DadosEquipe comCodigoParametro(Integer codigoParametro)
    {
      return new DadosEquipe(nome, areaActuacao, nivelFormacao, contacto, supervisorResponsavel, tipoEquipe,
          habilidadeTecnica, tempoMedioResposta, ferramentasIds, novasFerramentas, codigoParametro, novoParametroNome,
          novoParametroUnidade, especialidadeAnalise, frequenciaAmostragem, equipamentosAnalista,
          metodologiaSensibilizacao, linguaLocal, comunidadeAtendida, materiaisEducador);
    }
  }

  public static final class ResultadoParametro
  {
    public final int codigoParametro;
    public final boolean registado;
    public final String mensagem;

    public ResultadoParametro(int codigoParametro, boolean registado, String mensagem)
    {
      this.codigoParametro = codigoParametro;
      this.registado = registado;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoValidacao
  {
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoValidacao(boolean podeContinuar, String mensagem)
    {
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoRegisto
  {
    public final int equipeId;
    public final boolean registado;
    public final String mensagem;

    public ResultadoRegisto(int equipeId, boolean registado, String mensagem)
    {
      this.equipeId = equipeId;
      this.registado = registado;
      this.mensagem = mensagem;
    }
  }
}
