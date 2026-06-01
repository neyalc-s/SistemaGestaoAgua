package Repository_SQL.DistribuicaoConsumoDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;

public final class ActualizarObservacaoRegistroConsumoSQL
{
  public static final String PRE_CARREGAR_REGISTRO =
      "{call PRC_PRE_ACT_REGISTRO_CONSUMO(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
  public static final String ACTUALIZAR_OBSERVACAO =
      "{call PRC_ACT_OBSERVACAO_REG_CONS(?,?,?,?,?)}";

  private ActualizarObservacaoRegistroConsumoSQL()
  {}

  public static RegistroConsumo carregarRegistro(Connection connection, int codigoRc) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_CARREGAR_REGISTRO);
      cs.setInt(1, codigoRc);
      cs.registerOutParameter(2, Types.NUMERIC);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.TIMESTAMP);
      cs.registerOutParameter(6, Types.NUMERIC);
      cs.registerOutParameter(7, Types.VARCHAR);
      cs.registerOutParameter(8, Types.VARCHAR);
      cs.registerOutParameter(9, Types.NUMERIC);
      cs.registerOutParameter(10, Types.VARCHAR);
      cs.registerOutParameter(11, Types.DATE);
      cs.registerOutParameter(12, Types.VARCHAR);
      cs.registerOutParameter(13, Types.NUMERIC);
      cs.registerOutParameter(14, Types.VARCHAR);

      cs.execute();

      return new RegistroConsumo(
          codigoRc,
          cs.getObject(2),
          cs.getObject(3),
          cs.getObject(4),
          cs.getTimestamp(5),
          cs.getObject(6),
          cs.getString(7),
          cs.getString(8),
          cs.getObject(9),
          cs.getString(10),
          cs.getDate(11),
          cs.getString(12),
          cs.getInt(13) == 1,
          cs.getString(14));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoActualizacao actualizarObservacao(Connection connection, int codigoRc,
      String observacaoOriginal, String novaObservacao) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(ACTUALIZAR_OBSERVACAO);
      cs.setInt(1, codigoRc);

      if(observacaoOriginal == null || observacaoOriginal.isEmpty())
        cs.setNull(2, Types.VARCHAR);
      else
        cs.setString(2, observacaoOriginal);

      if(novaObservacao == null || novaObservacao.isEmpty())
        cs.setNull(3, Types.VARCHAR);
      else
        cs.setString(3, novaObservacao);

      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();

      return new ResultadoActualizacao(cs.getInt(4) == 1, cs.getString(5));
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

  public static final class RegistroConsumo
  {
    public final int codigoRc;
    public final Object codigoFb;
    public final Object codigoPd;
    public final Object codigoCota;
    public final Timestamp dataHoraRc;
    public final Object volumeRetiradoRc;
    public final String pessoaColetaRc;
    public final String metodoAutenticacaoRc;
    public final Object saldoCotaRc;
    public final String observacaoRc;
    public final java.sql.Date periodoValidadeCa;
    public final String statusValidade;
    public final boolean podeContinuar;
    public final String mensagem;

    public RegistroConsumo(int codigoRc, Object codigoFb, Object codigoPd, Object codigoCota, Timestamp dataHoraRc,
        Object volumeRetiradoRc, String pessoaColetaRc, String metodoAutenticacaoRc, Object saldoCotaRc,
        String observacaoRc, java.sql.Date periodoValidadeCa, String statusValidade, boolean podeContinuar,
        String mensagem)
    {
      this.codigoRc = codigoRc;
      this.codigoFb = codigoFb;
      this.codigoPd = codigoPd;
      this.codigoCota = codigoCota;
      this.dataHoraRc = dataHoraRc;
      this.volumeRetiradoRc = volumeRetiradoRc;
      this.pessoaColetaRc = pessoaColetaRc;
      this.metodoAutenticacaoRc = metodoAutenticacaoRc;
      this.saldoCotaRc = saldoCotaRc;
      this.observacaoRc = observacaoRc;
      this.periodoValidadeCa = periodoValidadeCa;
      this.statusValidade = statusValidade;
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
