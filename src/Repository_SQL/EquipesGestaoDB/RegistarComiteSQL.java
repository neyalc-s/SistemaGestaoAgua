package Repository_SQL.EquipesGestaoDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Types;

public final class RegistarComiteSQL
{
  public static final String PRE_VALIDAR_REGISTO = "{call PRC_PRE_REGISTAR_COMITE(?,?,?,?)}";
  public static final String REGISTAR_COMITE = "{call PRC_REGISTAR_COMITE(?,?,?,?,?)}";

  private RegistarComiteSQL()
  {}

  public static ResultadoPreValidacao preValidarRegisto(Connection connection, DadosComite dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_REGISTO);
      cs.setString(1, dados.nomeComite);
      cs.setDate(2, dados.dataCriacao);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.VARCHAR);
      cs.execute();

      return new ResultadoPreValidacao(cs.getInt(3) == 1, cs.getString(4));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoRegisto registarComite(Connection connection, DadosComite dados) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_COMITE);
      cs.setString(1, dados.nomeComite);
      cs.setDate(2, dados.dataCriacao);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();

      return new ResultadoRegisto(cs.getInt(3), cs.getInt(4) == 1, cs.getString(5));
    } finally
    {
      fechar(cs);
    }
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

  public static final class DadosComite
  {
    public final String nomeComite;
    public final Date dataCriacao;

    public DadosComite(String nomeComite, Date dataCriacao)
    {
      this.nomeComite = nomeComite;
      this.dataCriacao = dataCriacao;
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
    public final int codigoComite;
    public final boolean registado;
    public final String mensagem;

    public ResultadoRegisto(int codigoComite, boolean registado, String mensagem)
    {
      this.codigoComite = codigoComite;
      this.registado = registado;
      this.mensagem = mensagem;
    }
  }
}
