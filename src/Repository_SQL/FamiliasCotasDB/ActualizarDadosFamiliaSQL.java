package Repository_SQL.FamiliasCotasDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class ActualizarDadosFamiliaSQL
{
  public static final String CONSULTAR_FAMILIAS = "SELECT codigo_fb, nome_responsavel_fb, contacto_fb, "
      + "num_membros_fb, estado_fb, aldeia, coordenadas_gps "
      + "FROM vw_localizacao_todas_familias ORDER BY codigo_fb";

  public static final String CONSULTAR_DADOS_FAMILIA = "SELECT codigo_fb, nome_responsavel_fb, num_membros_fb, "
      + "perfil_socioeconomico_fb, contacto_fb, estado_fb, aldeia, coordenadas_gps "
      + "FROM VW_FAMILIA_LOCALIZACAO WHERE codigo_fb = ?";

  public static final String PRE_VALIDAR_ACTUALIZACAO = "{call PRC_PRE_ACT_DADOS_FAMILIA(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
  public static final String ACTUALIZAR_DADOS = "{call PRC_ACT_DADOS_FAMILIA(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

  private ActualizarDadosFamiliaSQL()
  {}

  public static void carregarFamilias(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_FAMILIAS);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getObject("codigo_fb"));
        linha.add(rs.getObject("nome_responsavel_fb"));
        linha.add(rs.getObject("contacto_fb"));
        linha.add(rs.getObject("num_membros_fb"));
        linha.add(rs.getObject("estado_fb"));
        linha.add(rs.getObject("aldeia"));
        linha.add(rs.getObject("coordenadas_gps"));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static DadosFamilia carregarDadosFamilia(Connection connection, int codigoFamilia) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = connection.prepareStatement(CONSULTAR_DADOS_FAMILIA);
      ps.setInt(1, codigoFamilia);
      rs = ps.executeQuery();

      if(!rs.next())
        throw new IllegalArgumentException("Família não encontrada.");

      return new DadosFamilia(rs.getInt("codigo_fb"), rs.getString("nome_responsavel_fb"),
          rs.getInt("num_membros_fb"), rs.getString("perfil_socioeconomico_fb"), rs.getString("contacto_fb"),
          rs.getString("estado_fb"), rs.getString("aldeia"), rs.getString("coordenadas_gps"));
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoOperacao preValidarActualizacao(Connection connection, DadosActualizacao dados)
      throws Exception
  {
    return executarProcedure(connection, PRE_VALIDAR_ACTUALIZACAO, dados);
  }

  public static ResultadoOperacao actualizarDados(Connection connection, DadosActualizacao dados) throws Exception
  {
    return executarProcedure(connection, ACTUALIZAR_DADOS, dados);
  }

  private static ResultadoOperacao executarProcedure(Connection connection, String chamada, DadosActualizacao dados)
      throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(chamada);
      preencherParametros(cs, dados);
      cs.registerOutParameter(23, Types.NUMERIC);
      cs.registerOutParameter(24, Types.VARCHAR);
      cs.execute();
      return new ResultadoOperacao(cs.getInt(23) == 1, cs.getString(24));
    } finally
    {
      fechar(cs);
    }
  }

  private static void preencherParametros(CallableStatement cs, DadosActualizacao dados) throws Exception
  {
    cs.setInt(1, dados.codigoFamilia);
    cs.setString(2, dados.nomeOriginal);
    cs.setInt(3, dados.numeroMembrosOriginal);
    cs.setString(4, dados.perfilOriginal);
    cs.setString(5, dados.contactoOriginal);
    cs.setString(6, dados.estadoOriginal);
    cs.setString(7, dados.aldeiaOriginal);
    cs.setString(8, dados.coordenadasGpsOriginal);
    cs.setString(9, dados.nomeNovo);
    cs.setInt(10, dados.numeroMembrosNovo);
    cs.setString(11, dados.perfilNovo);
    cs.setString(12, dados.contactoNovo);
    cs.setString(13, dados.estadoNovo);
    cs.setString(14, dados.aldeiaNova);
    cs.setString(15, dados.coordenadasGpsNovas);
    cs.setInt(16, dados.actualizarNome ? 1 : 0);
    cs.setInt(17, dados.actualizarNumeroMembros ? 1 : 0);
    cs.setInt(18, dados.actualizarPerfil ? 1 : 0);
    cs.setInt(19, dados.actualizarContacto ? 1 : 0);
    cs.setInt(20, dados.actualizarEstado ? 1 : 0);
    cs.setInt(21, dados.actualizarAldeia ? 1 : 0);
    cs.setInt(22, dados.actualizarCoordenadasGps ? 1 : 0);
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

  private static void fechar(CallableStatement cs)
  {
    try
    {
      if(cs != null)
        cs.close();
    } catch(Exception ignored)
    {}
  }

  public static final class DadosActualizacao
  {
    public final int codigoFamilia;
    public final String nomeOriginal;
    public final int numeroMembrosOriginal;
    public final String perfilOriginal;
    public final String contactoOriginal;
    public final String estadoOriginal;
    public final String aldeiaOriginal;
    public final String coordenadasGpsOriginal;
    public final String nomeNovo;
    public final int numeroMembrosNovo;
    public final String perfilNovo;
    public final String contactoNovo;
    public final String estadoNovo;
    public final String aldeiaNova;
    public final String coordenadasGpsNovas;
    public final boolean actualizarNome;
    public final boolean actualizarNumeroMembros;
    public final boolean actualizarPerfil;
    public final boolean actualizarContacto;
    public final boolean actualizarEstado;
    public final boolean actualizarAldeia;
    public final boolean actualizarCoordenadasGps;

    public DadosActualizacao(int codigoFamilia, String nomeOriginal, int numeroMembrosOriginal, String perfilOriginal,
        String contactoOriginal, String estadoOriginal, String aldeiaOriginal, String coordenadasGpsOriginal,
        String nomeNovo, int numeroMembrosNovo, String perfilNovo, String contactoNovo, String estadoNovo,
        String aldeiaNova, String coordenadasGpsNovas,
        boolean actualizarNome, boolean actualizarNumeroMembros, boolean actualizarPerfil, boolean actualizarContacto,
        boolean actualizarEstado, boolean actualizarAldeia, boolean actualizarCoordenadasGps)
    {
      this.codigoFamilia = codigoFamilia;
      this.nomeOriginal = nomeOriginal;
      this.numeroMembrosOriginal = numeroMembrosOriginal;
      this.perfilOriginal = perfilOriginal;
      this.contactoOriginal = contactoOriginal;
      this.estadoOriginal = estadoOriginal;
      this.aldeiaOriginal = aldeiaOriginal;
      this.coordenadasGpsOriginal = coordenadasGpsOriginal;
      this.nomeNovo = nomeNovo;
      this.numeroMembrosNovo = numeroMembrosNovo;
      this.perfilNovo = perfilNovo;
      this.contactoNovo = contactoNovo;
      this.estadoNovo = estadoNovo;
      this.aldeiaNova = aldeiaNova;
      this.coordenadasGpsNovas = coordenadasGpsNovas;
      this.actualizarNome = actualizarNome;
      this.actualizarNumeroMembros = actualizarNumeroMembros;
      this.actualizarPerfil = actualizarPerfil;
      this.actualizarContacto = actualizarContacto;
      this.actualizarEstado = actualizarEstado;
      this.actualizarAldeia = actualizarAldeia;
      this.actualizarCoordenadasGps = actualizarCoordenadasGps;
    }
  }

  public static final class ResultadoOperacao
  {
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoOperacao(boolean podeContinuar, String mensagem)
    {
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class DadosFamilia
  {
    public final int codigoFamilia;
    public final String nomeResponsavel;
    public final int numeroMembros;
    public final String perfilSocioeconomico;
    public final String contacto;
    public final String estado;
    public final String aldeia;
    public final String coordenadasGps;

    public DadosFamilia(int codigoFamilia, String nomeResponsavel, int numeroMembros, String perfilSocioeconomico,
        String contacto, String estado, String aldeia, String coordenadasGps)
    {
      this.codigoFamilia = codigoFamilia;
      this.nomeResponsavel = nomeResponsavel;
      this.numeroMembros = numeroMembros;
      this.perfilSocioeconomico = perfilSocioeconomico;
      this.contacto = contacto;
      this.estado = estado;
      this.aldeia = aldeia;
      this.coordenadasGps = coordenadasGps;
    }
  }
}
