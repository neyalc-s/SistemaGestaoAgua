package Repository_SQL.EquipesGestaoDB;

public final class DashboardEquipesGestaoSQL
{
  public static final String CONTAR_EQUIPES =
      "SELECT COUNT(*) FROM VW_EQUIPE_TECNICA";

  public static final String CONTAR_TECNICOS_MANUTENCAO =
      "SELECT COUNT(*) FROM VW_TECNICO_MANUTENCAO";

  public static final String CONTAR_ANALISTAS =
      "SELECT COUNT(*) FROM VW_ANALISTA_QUALIDADE";

  public static final String CONTAR_EDUCADORES =
      "SELECT COUNT(*) FROM VW_EDUCADOR_COMUNITARIO";

  public static final String CONTAR_COMITES =
      "SELECT COUNT(*) FROM VW_COMITE";

  public static final String CONTAR_FERRAMENTAS =
      "SELECT COUNT(*) FROM VW_FERRAMENTA_MANUTENCAO";

  public static final String CONTAR_EQUIPAMENTOS =
      "SELECT COUNT(*) FROM VW_EQUIPAMENTO_ANALISTA";

  public static final String CONTAR_MATERIAIS =
      "SELECT COUNT(*) FROM VW_MATERIAL_EDUCADOR";

  public static final String LISTAR_EQUIPES_RECENTES =
      "SELECT principal, detalhe, valor FROM (SELECT nome principal, NVL(area_actuacao, 'Sem area') detalhe, "
          + "NVL(supervisor_responsavel, 'Sem supervisor') valor FROM VW_EQUIPE_TECNICA ORDER BY equipe_id DESC) "
          + "WHERE ROWNUM <= 8";

  public static final String LISTAR_COMITES_RECENTES =
      "SELECT principal, detalhe, valor FROM (SELECT nome_comite principal, 'Codigo ' || cod_comite_responsavel detalhe, "
          + "TO_CHAR(data_criacao, 'DD/MM/YYYY') valor FROM VW_COMITE ORDER BY data_criacao DESC NULLS LAST) "
          + "WHERE ROWNUM <= 8";

  private DashboardEquipesGestaoSQL()
  {}
}
