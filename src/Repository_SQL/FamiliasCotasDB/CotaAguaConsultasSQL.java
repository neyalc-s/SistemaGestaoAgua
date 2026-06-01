package Repository_SQL.FamiliasCotasDB;

import Resources.InterfaceGraficaUtils;
import javax.swing.table.DefaultTableModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

public final class CotaAguaConsultasSQL
{
  public static final String VISUALIZAR_FAMILIA_COTA_SEMANAL = "SELECT * FROM vw_familia_cota";
  public static final String VISUALIZAR_COTA_REGISTROCONSUMO = "SELECT * FROM vw_consumo_por_cota";
  public static final String GERAR_COTAS_SEMANAIS = "{call PRC_GERAR_COTAS_SEMANAIS(?,?,?,?)}";

  private CotaAguaConsultasSQL()
  {}

  public static DefaultTableModel carregarModeloTabela(Connection connection, final String sql) throws Exception
  {
    return InterfaceGraficaUtils.carregarModeloTabela(connection, sql);
  }

  public static ResultadoGeracao gerarCotasSemanais(Connection connection) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(GERAR_COTAS_SEMANAIS);
      cs.registerOutParameter(1, Types.NUMERIC);
      cs.registerOutParameter(2, Types.NUMERIC);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.VARCHAR);

      cs.execute();

      return new ResultadoGeracao(cs.getInt(1), cs.getInt(2), cs.getInt(3), cs.getString(4));
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

  public static final class ResultadoGeracao
  {
    public final int cotasCriadas;
    public final int familiasIgnoradas;
    public final int erros;
    public final String mensagem;

    public ResultadoGeracao(int cotasCriadas, int familiasIgnoradas, int erros, String mensagem)
    {
      this.cotasCriadas = cotasCriadas;
      this.familiasIgnoradas = familiasIgnoradas;
      this.erros = erros;
      this.mensagem = mensagem;
    }
  }
}
