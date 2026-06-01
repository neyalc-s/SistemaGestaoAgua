package Repository_SQL.AdministradorDB;

public final class DashboardAdministradorSQL
{
  public static final String CONTAR_FUNCIONARIOS =
      "SELECT COUNT(*) FROM VW_DASH_FUNCIONARIO";

  public static final String CONTAR_FUNCIONARIOS_ACTIVOS =
      "SELECT COUNT(*) FROM VW_DASH_FUNCIONARIO WHERE estado_funcionario = 'Activo'";

  public static final String CONTAR_SESSOES_ABERTAS =
      "SELECT COUNT(*) FROM VW_DASH_SESSAO_ADMIN WHERE estado_sessao = 'ABERTA'";

  public static final String CONTAR_SESSOES_FECHADAS_HOJE =
      "SELECT COUNT(*) FROM VW_DASH_SESSAO_ADMIN WHERE estado_sessao = 'FECHADA' AND TRUNC(data_fim) = TRUNC(SYSDATE)";

  public static final String CONTAR_SESSOES_EXPIRADAS =
      "SELECT COUNT(*) FROM VW_DASH_SESSAO_ADMIN WHERE estado_sessao = 'EXPIRADA'";

  public static final String TESTAR_FAMILIAS_COTAS =
      "SELECT COUNT(*) FROM VW_CONEXAO_FAM_COTAS";

  public static final String TESTAR_DISTRIBUICAO_CONSUMO =
      "SELECT COUNT(*) FROM VW_CONEXAO_DIST_CONS";

  public static final String TESTAR_TRANSFERENCIAS_RECURSOS =
      "SELECT COUNT(*) FROM VW_CONEXAO_TRANS_REC";

  public static final String TESTAR_EQUIPES_GESTAO =
      "SELECT COUNT(*) FROM VW_CONEXAO_EQ_GESTAO";

  public static final String LISTAR_FUNCIONARIOS_POR_NO =
      "SELECT nome_no, descricao, total FROM VW_DASH_FUNCIONARIOS_NO ORDER BY nome_no";

  public static final String LISTAR_SESSOES_POR_ESTADO =
      "SELECT estado_sessao, descricao, total FROM VW_DASH_SESSOES_ESTADO ORDER BY estado_sessao";

  public static final String LISTAR_NOS_REGISTADOS =
      "SELECT nome_no, host_no, estado_no FROM VW_DASH_NOS_REGISTADOS ORDER BY codigo_no";

  private DashboardAdministradorSQL()
  {}
}
