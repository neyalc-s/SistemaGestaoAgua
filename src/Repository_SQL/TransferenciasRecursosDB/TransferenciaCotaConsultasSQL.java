package Repository_SQL.TransferenciasRecursosDB;

import Resources.InterfaceGraficaUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public final class TransferenciaCotaConsultasSQL
{
  public static final String VISUALIZAR_TRANSFERENCIAS =
    "SELECT codigo_tc, cod_fam_doadora_tc, "
      + "CASE WHEN papel_na_transferencia = 'Doa' THEN nome_responsavel_fb ELSE nome_parceiro END AS familia_doadora, "
      + "cod_fam_receptora_tc, "
      + "CASE WHEN papel_na_transferencia = 'Recebe' THEN nome_responsavel_fb ELSE nome_parceiro END AS familia_receptora, "
      + "volume_cedido_tc, motivo_solicitacao_tc, data_aprovacao_tc, validade_transferencia_tc, "
      + "papel_na_transferencia FROM vw_transferencias_familia ORDER BY data_aprovacao_tc DESC, codigo_tc DESC, "
      + "papel_na_transferencia";

  public static final String PESQUISAR_TRANSFERENCIAS_BASE =
      "SELECT * FROM (%s) WHERE ";

  public static final String FILTRO_NUMERICO =
      "TO_CHAR(%s) = ?";

  public static final String FILTRO_TEXTO =
      "UPPER(%s) LIKE UPPER(?)";

  private TransferenciaCotaConsultasSQL()
  {}

  public static DefaultTableModel carregarTransferencias(Connection connection) throws Exception
  {
    return InterfaceGraficaUtils.carregarModeloTabela(connection, VISUALIZAR_TRANSFERENCIAS);
  }

  public static DefaultTableModel pesquisarTransferencias(Connection connection, String criterio, String valor)
      throws Exception
  {
    String coluna = colunaPesquisa(criterio);
    boolean numerico = "codigo_tc".equals(coluna) || "cod_fam_doadora_tc".equals(coluna)
        || "cod_fam_receptora_tc".equals(coluna);
    String sql = String.format(PESQUISAR_TRANSFERENCIAS_BASE, VISUALIZAR_TRANSFERENCIAS)
        + String.format(numerico ? FILTRO_NUMERICO : FILTRO_TEXTO, coluna);

    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      ps = connection.prepareStatement(sql);
      ps.setString(1, numerico ? valor : "%" + valor + "%");
      rs = ps.executeQuery();
      return InterfaceGraficaUtils.criarModeloTabela(rs);
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

  private static String colunaPesquisa(String criterio)
  {
    if("codigo_tc".equals(criterio))
      return "codigo_tc";
    if("cod_fam_doadora_tc".equals(criterio))
      return "cod_fam_doadora_tc";
    if("familia_doadora".equals(criterio))
      return "familia_doadora";
    if("cod_fam_receptora_tc".equals(criterio))
      return "cod_fam_receptora_tc";
    if("familia_receptora".equals(criterio))
      return "familia_receptora";
    if("motivo_solicitacao_tc".equals(criterio))
      return "motivo_solicitacao_tc";
    if("papel_na_transferencia".equals(criterio))
      return "papel_na_transferencia";
    return "codigo_tc";
  }
}
