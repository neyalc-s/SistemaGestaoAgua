package Repository_SQL.DistribuicaoConsumoDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class RegistarPontoDistribuicaoSQL
{
  public static final String CONSULTAR_RECURSOS = "SELECT codigo_rh, tipo_rh, localizacao_rh, volume_rh, "
      + "sazonalidade_rh, nivel_exploracao_rh FROM VW_RECURSO_HIDRICO ORDER BY codigo_rh";

  public static final String CONSULTAR_COMITES = "SELECT cod_comite_responsavel, nome_comite, data_criacao "
      + "FROM VW_COMITE ORDER BY cod_comite_responsavel";

  public static final String CONSULTAR_EQUIPES = "SELECT equipe_id, nome, tipo_equipe, "
      + "area_actuacao, nivel_formacao, contacto, supervisor_responsavel FROM VW_TECNICO_MANUTENCAO_OPCAO ORDER BY equipe_id";

  public static final String PRE_VALIDAR_REGISTO = "{call PRC_PRE_REGISTAR_PONTO(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
  public static final String REGISTAR_PONTO = "{call PRC_REGISTAR_PONTO(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

  private RegistarPontoDistribuicaoSQL()
  {}

  public static void carregarRecursos(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_RECURSOS, new String[]
      {
          "codigo_rh", "tipo_rh", "localizacao_rh", "volume_rh", "sazonalidade_rh", "nivel_exploracao_rh"
      });
  }

  public static void carregarComites(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_COMITES, new String[]
      {
          "cod_comite_responsavel", "nome_comite", "data_criacao"
      });
  }

  public static void carregarEquipes(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_EQUIPES, new String[]
      {
          "equipe_id", "nome", "tipo_equipe", "area_actuacao", "nivel_formacao", "contacto", "supervisor_responsavel"
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

  public static ResultadoPreValidacao preValidarRegisto(Connection connection, DadosPonto dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_REGISTO);
      preencherParametrosPonto(cs, dados);
      cs.registerOutParameter(12, Types.NUMERIC);
      cs.registerOutParameter(13, Types.VARCHAR);
      cs.execute();

      return new ResultadoPreValidacao(cs.getInt(12) == 1, cs.getString(13));
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

  public static ResultadoRegisto registarPonto(Connection connection, DadosPonto dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_PONTO);
      preencherParametrosPonto(cs, dados);
      cs.registerOutParameter(12, Types.NUMERIC);
      cs.registerOutParameter(13, Types.NUMERIC);
      cs.registerOutParameter(14, Types.VARCHAR);
      cs.execute();

      return new ResultadoRegisto(cs.getInt(12), cs.getInt(13) == 1, cs.getString(14));
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

  private static void preencherParametrosPonto(CallableStatement cs, DadosPonto dados) throws Exception
  {
    cs.setInt(1, dados.codigoComite);
    cs.setInt(2, dados.equipeId);
    cs.setInt(3, dados.codigoRecursoHidrico);
    cs.setDouble(4, dados.volumeActual);
    cs.setString(5, dados.localizacao);
    cs.setString(6, dados.tipoInfraestrutura);
    cs.setDouble(7, dados.capacidade);
    cs.setString(8, dados.fonteAbastecimento);
    cs.setString(9, dados.tecnologiaTratamento);
    cs.setDate(10, Date.valueOf(dados.dataInstalacao));
    cs.setString(11, dados.estadoOperacional);
  }

  public static final class DadosPonto
  {
    public final int codigoComite;
    public final int equipeId;
    public final int codigoRecursoHidrico;
    public final double volumeActual;
    public final String localizacao;
    public final String tipoInfraestrutura;
    public final double capacidade;
    public final String fonteAbastecimento;
    public final String tecnologiaTratamento;
    public final String dataInstalacao;
    public final String estadoOperacional;

    public DadosPonto(int codigoComite, int equipeId, int codigoRecursoHidrico, double volumeActual,
        String localizacao, String tipoInfraestrutura, double capacidade, String fonteAbastecimento,
        String tecnologiaTratamento, String dataInstalacao, String estadoOperacional)
    {
      this.codigoComite = codigoComite;
      this.equipeId = equipeId;
      this.codigoRecursoHidrico = codigoRecursoHidrico;
      this.volumeActual = volumeActual;
      this.localizacao = localizacao;
      this.tipoInfraestrutura = tipoInfraestrutura;
      this.capacidade = capacidade;
      this.fonteAbastecimento = fonteAbastecimento;
      this.tecnologiaTratamento = tecnologiaTratamento;
      this.dataInstalacao = dataInstalacao;
      this.estadoOperacional = estadoOperacional;
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
    public final int codigoPonto;
    public final boolean registado;
    public final String mensagem;

    public ResultadoRegisto(int codigoPonto, boolean registado, String mensagem)
    {
      this.codigoPonto = codigoPonto;
      this.registado = registado;
      this.mensagem = mensagem;
    }
  }
}
