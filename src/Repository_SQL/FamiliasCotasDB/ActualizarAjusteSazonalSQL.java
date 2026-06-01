package Repository_SQL.FamiliasCotasDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Date;

public final class ActualizarAjusteSazonalSQL
{
  public static final String PRE_VALIDAR_ACTUALIZACAO = "{call PRC_PRE_ACT_AJUSTE_SAZONAL(?,?,?,?,?,?,?)}";
  public static final String ACTUALIZAR_AJUSTE = "{call PRC_ACT_AJUSTE_SAZONAL(?,?,?,?,?)}";

  private ActualizarAjusteSazonalSQL()
  {}

  public static ResultadoPreValidacao preValidarActualizacao(Connection connection, int codigoFamilia, int codigoCota,
      String novoAjuste) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_ACTUALIZACAO);
      cs.setInt(1, codigoFamilia);
      cs.setInt(2, codigoCota);
      cs.setString(3, novoAjuste);
      cs.registerOutParameter(4, Types.VARCHAR);
      cs.registerOutParameter(5, Types.DATE);
      cs.registerOutParameter(6, Types.NUMERIC);
      cs.registerOutParameter(7, Types.VARCHAR);

      cs.execute();

      return new ResultadoPreValidacao(cs.getString(4), cs.getDate(5), cs.getInt(6) == 1, cs.getString(7));
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

  public static ResultadoActualizacao actualizarAjuste(Connection connection, int codigoFamilia, int codigoCota,
      String novoAjuste) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(ACTUALIZAR_AJUSTE);
      cs.setInt(1, codigoFamilia);
      cs.setInt(2, codigoCota);
      cs.setString(3, novoAjuste);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);

      cs.execute();

      return new ResultadoActualizacao(cs.getInt(4) == 1, cs.getString(5));
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

  public static final class ResultadoPreValidacao
  {
    public final String ajusteActual;
    public final Date validadeCota;
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoPreValidacao(String ajusteActual, Date validadeCota, boolean podeContinuar, String mensagem)
    {
      this.ajusteActual = ajusteActual;
      this.validadeCota = validadeCota;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoActualizacao
  {
    public final boolean actualizado;
    public final String mensagem;

    public ResultadoActualizacao(boolean actualizado, String mensagem)
    {
      this.actualizado = actualizado;
      this.mensagem = mensagem;
    }
  }
}
