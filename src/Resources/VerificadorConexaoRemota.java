package Resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class VerificadorConexaoRemota
{
  public static final String SQL_TESTE_FAM_COTAS = "SELECT 1 FROM TESTE_CONEXAO_FAM_COTAS";
  public static final String SQL_TESTE_DIST_CONS = "SELECT 1 FROM TESTE_CONEXAO_DIST_CONS";
  public static final String SQL_TESTE_TRANS_REC = "SELECT 1 FROM TESTE_CONEXAO_TRANS_REC";
  public static final String SQL_TESTE_EQ_GESTAO = "SELECT 1 FROM TESTE_CONEXAO_EQ_GESTAO";
  private static final int TEMPO_LIMITE_TESTE_SEGUNDOS = 3;

  private VerificadorConexaoRemota()
  {}

  public enum NoRemoto
  {
    FAMILIAS_COTAS("FamiliasCotasDB", SQL_TESTE_FAM_COTAS),
    DISTRIBUICAO_CONSUMO("DistribuicaoConsumoDB", SQL_TESTE_DIST_CONS),
    TRANSFERENCIAS_RECURSOS("TransferenciasRecursosDB", SQL_TESTE_TRANS_REC),
    EQUIPES_GESTAO("EquipesGestaoDB", SQL_TESTE_EQ_GESTAO);

    private final String nome;
    private final String sqlTeste;

    NoRemoto(String nome, String sqlTeste)
    {
      this.nome = nome;
      this.sqlTeste = sqlTeste;
    }

    public String getNome()
    {
      return nome;
    }

    public String getSqlTeste()
    {
      return sqlTeste;
    }
  }

  public static final class ResultadoVerificacao
  {
    private final boolean disponivel;
    private final String mensagem;

    private ResultadoVerificacao(boolean disponivel, String mensagem)
    {
      this.disponivel = disponivel;
      this.mensagem = mensagem;
    }

    public boolean isDisponivel()
    {
      return disponivel;
    }

    public String getMensagem()
    {
      return mensagem;
    }
  }

  public static ResultadoVerificacao verificar(Connection connection, NoRemoto[] nosRemotos)
  {
    if(nosRemotos == null || nosRemotos.length == 0)
      return new ResultadoVerificacao(true, "");

    StringBuilder indisponiveis = new StringBuilder();

    for(int i = 0; i < nosRemotos.length; i++)
    {
      NoRemoto noRemoto = nosRemotos[i];
      if(!estaOnline(connection, noRemoto))
      {
        if(indisponiveis.length() > 0)
          indisponiveis.append(", ");
        indisponiveis.append(noRemoto.getNome());
      }
    }

    if(indisponiveis.length() == 0)
      return new ResultadoVerificacao(true, "");

    return new ResultadoVerificacao(false, BloqueadorOperacaoRemota.MENSAGEM_PADRAO);
  }

  private static boolean estaOnline(Connection connection, NoRemoto noRemoto)
  {
    if(connection == null || noRemoto == null)
      return false;

    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      if(connection.isClosed())
        return false;

      statement = connection.prepareStatement(noRemoto.getSqlTeste());
      try
      {
        statement.setQueryTimeout(TEMPO_LIMITE_TESTE_SEGUNDOS);
      } catch(SQLException ignored)
      {}

      resultSet = statement.executeQuery();
      return resultSet.next();
    } catch(SQLException ex)
    {
      return false;
    } finally
    {
      if(resultSet != null)
      {
        try
        {
          resultSet.close();
        } catch(SQLException ignored)
        {}
      }

      if(statement != null)
      {
        try
        {
          statement.close();
        } catch(SQLException ignored)
        {}
      }
    }
  }
}
