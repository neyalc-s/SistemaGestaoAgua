package Repository_SQL.DistribuicaoConsumoDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class RegistarManutencaoPontoSQL
{
  public static final String CONSULTAR_PONTOS = "SELECT codigo_pd, equipe_id, localizacao_pd, "
      + "tipo_infraestrutura_pd, capacidade_armazenamento_pd, volume_actual_pd, estado_operacional_pd, "
      + "nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel "
      + "FROM VW_PONTOS_TEC_MANUT ORDER BY codigo_pd";

  public static final String PRE_VALIDAR_MANUTENCAO = "{call PRC_PRE_REGISTAR_MANUT_PONTO(?,?,?,?,?,?,?,?)}";
  public static final String REGISTAR_MANUTENCAO = "{call PRC_REGISTAR_MANUT_PONTO(?,?,?,?,?,?,?,?)}";

  private RegistarManutencaoPontoSQL()
  {}

  public static void carregarPontos(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_PONTOS);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("codigo_pd"));
        linha.add(rs.getInt("equipe_id"));
        linha.add(rs.getString("localizacao_pd"));
        linha.add(rs.getString("tipo_infraestrutura_pd"));
        linha.add(rs.getObject("capacidade_armazenamento_pd"));
        linha.add(rs.getObject("volume_actual_pd"));
        linha.add(rs.getString("estado_operacional_pd"));
        linha.add(rs.getString("nome"));
        linha.add(rs.getString("area_actuacao"));
        linha.add(rs.getString("nivel_formacao"));
        linha.add(rs.getString("contacto"));
        linha.add(rs.getString("supervisor_responsavel"));
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

  public static ResultadoPreValidacao preValidarManutencao(Connection connection, DadosManutencao dados)
      throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_MANUTENCAO);
      cs.setInt(1, dados.codigoPonto);
      cs.setDate(2, Date.valueOf(dados.dataManutencao));
      cs.setString(3, dados.tipoManutencao);
      cs.setString(4, dados.alterarEstado ? "SIM" : "NAO");
      if(dados.novoEstado == null || dados.novoEstado.trim().isEmpty())
        cs.setNull(5, Types.VARCHAR);
      else
        cs.setString(5, dados.novoEstado);
      cs.registerOutParameter(6, Types.NUMERIC);
      cs.registerOutParameter(7, Types.NUMERIC);
      cs.registerOutParameter(8, Types.VARCHAR);
      cs.execute();

      return new ResultadoPreValidacao(cs.getInt(6), cs.getInt(7) == 1, cs.getString(8));
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

  public static ResultadoRegisto registarManutencao(Connection connection, DadosManutencao dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_MANUTENCAO);
      cs.setInt(1, dados.codigoPonto);
      cs.setDate(2, Date.valueOf(dados.dataManutencao));
      cs.setString(3, dados.tipoManutencao);
      cs.setString(4, dados.alterarEstado ? "SIM" : "NAO");
      if(dados.novoEstado == null || dados.novoEstado.trim().isEmpty())
        cs.setNull(5, Types.VARCHAR);
      else
        cs.setString(5, dados.novoEstado);
      cs.registerOutParameter(6, Types.NUMERIC);
      cs.registerOutParameter(7, Types.NUMERIC);
      cs.registerOutParameter(8, Types.VARCHAR);
      cs.execute();

      return new ResultadoRegisto(cs.getInt(6), cs.getInt(7) == 1, cs.getString(8));
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

  public static final class DadosManutencao
  {
    public final int codigoPonto;
    public final int equipeId;
    public final String dataManutencao;
    public final String tipoManutencao;
    public final boolean alterarEstado;
    public final String novoEstado;

    public DadosManutencao(int codigoPonto, int equipeId, String dataManutencao, String tipoManutencao,
        boolean alterarEstado, String novoEstado)
    {
      this.codigoPonto = codigoPonto;
      this.equipeId = equipeId;
      this.dataManutencao = dataManutencao;
      this.tipoManutencao = tipoManutencao;
      this.alterarEstado = alterarEstado;
      this.novoEstado = novoEstado;
    }
  }

  public static final class ResultadoPreValidacao
  {
    public final int equipeId;
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoPreValidacao(int equipeId, boolean podeContinuar, String mensagem)
    {
      this.equipeId = equipeId;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoRegisto
  {
    public final int codigoHistorico;
    public final boolean registado;
    public final String mensagem;

    public ResultadoRegisto(int codigoHistorico, boolean registado, String mensagem)
    {
      this.codigoHistorico = codigoHistorico;
      this.registado = registado;
      this.mensagem = mensagem;
    }
  }
}
