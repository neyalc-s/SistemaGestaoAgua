package Repository_SQL.EquipesGestaoDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class ComiteConsultasSQL
{
  public static final String CONSULTAR_COMITES_BASE =
      "SELECT cod_comite_responsavel, nome_comite, data_criacao FROM VW_COMITE WHERE 1 = 1 ";

  public static final String FILTRAR_CODIGO_COMITE =
      "AND TO_CHAR(cod_comite_responsavel) LIKE ? ";

  public static final String FILTRAR_NOME_COMITE =
      "AND UPPER(nome_comite) LIKE ? ";

  public static final String FILTRAR_DATA_CRIACAO =
      "AND (TO_CHAR(data_criacao, 'YYYY-MM-DD') LIKE ? OR TO_CHAR(data_criacao, 'DD/MM/YYYY') LIKE ?) ";

  public static final String ORDENAR_COMITES =
      "ORDER BY cod_comite_responsavel";

  private ComiteConsultasSQL()
  {}

  public static List<Comite> pesquisar(Connection connection, String criterio, String pesquisa) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<Object> params = new ArrayList<Object>();
    StringBuilder sql = new StringBuilder();

    sql.append(CONSULTAR_COMITES_BASE);

    if(temTexto(pesquisa))
    {
      String texto = pesquisa.trim();

      if("Código".equals(criterio))
      {
        sql.append(FILTRAR_CODIGO_COMITE);
        params.add("%" + texto + "%");
      }
      else if("Nome".equals(criterio))
      {
        sql.append(FILTRAR_NOME_COMITE);
        params.add("%" + texto.toUpperCase() + "%");
      }
      else if("Data de criação".equals(criterio))
      {
        sql.append(FILTRAR_DATA_CRIACAO);
        params.add("%" + texto + "%");
        params.add("%" + texto + "%");
      }
    }

    sql.append(ORDENAR_COMITES);

    try
    {
      ps = connection.prepareStatement(sql.toString());
      for(int i = 0; i < params.size(); i++)
        ps.setObject(i + 1, params.get(i));

      rs = ps.executeQuery();
      List<Comite> comites = new ArrayList<Comite>();
      while(rs.next())
        comites.add(new Comite(rs.getInt("cod_comite_responsavel"), rs.getString("nome_comite"),
            rs.getDate("data_criacao")));
      return comites;
    } finally
    {
      fechar(rs, ps);
    }
  }

  private static boolean temTexto(String valor)
  {
    return valor != null && valor.trim().length() > 0;
  }

  private static void fechar(ResultSet rs, PreparedStatement ps)
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

  public static final class Comite
  {
    public final int codComiteResponsavel;
    public final String nomeComite;
    public final Date dataCriacao;

    public Comite(int codComiteResponsavel, String nomeComite, Date dataCriacao)
    {
      this.codComiteResponsavel = codComiteResponsavel;
      this.nomeComite = nomeComite;
      this.dataCriacao = dataCriacao;
    }
  }
}
