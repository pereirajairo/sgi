/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.GarantiaPortal;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceGarantiaPortalDAO;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class GarantiaPortalDAO implements InterfaceGarantiaPortalDAO<GarantiaPortal> {

    private Connection con;
    private UtilDatas utilDatas;
    private final String PREFIXO = "usu_";

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    @Override
    public boolean alterar(GarantiaPortal t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update  usu_tcte set \n"
                + " usu_codtra=?, usu_numocp=?, usu_datlan=?, usu_numcte=?, usu_chacte=?,"
                + " usu_valcte=?, usu_valnfv=?, usu_pesnfv=?, usu_estdes=?, usu_datval=?, "
                + " usu_usu_perfrefat=?, usu_valfrepes=?, usu_numnfv=?,usu_codcli=?,usu_codsnf=?,"
                + " usu_tnspro=?,usu_tipfre=?,usu_linpro=?,usu_pesfat=?,usu_pesfre=?,"
                + " usu_gerocp=?,usu_qtdpro=?\n"
                + " where  usu_codlan=?, usu_codemp=?, usu_codfil=? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Garantia alterado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<GarantiaPortal> getGarantiaPortals(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<GarantiaPortal> resultado = new ArrayList<GarantiaPortal>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select  MAX(gar.id) id,\n"
                + "       MAX(gp.nome) nome,\n"
                + "       MAX(gp.id) codigo,\n"
                + "       MAX(gp.fornecedor_erp) fornecedor_erp,\n"
                + "       MAX(gar.empresa_destino) empresa_destino,\n"
                + "       MAX(gar.agrupador) agrupador,\n"
                + "       MAX(gar.situacao) situacao,\n"
                + "       MAX(gar.situacaogeral) situacaogeral,\n"
                + "       MAX(dataabertura) dataabertura,\n"
                + "       MAX(pontogarantia_id) pontogarantia_id,\n"
                + "       MAX(gar.notafiscalgarantia) notafiscalgarantia,\n"
                + "       MAX(gar.representante_id) representante_id,\n"
                + "       MAX(gar.cliente_id) cliente_id\n"
                + "  FROM garantia gar\n"
                + "  LEFT JOIN garantiaponto gp\n"
                + "    ON (gp.id = gar.pontogarantia_id)\n"
                + " WHERE 0 = 0"
                //  + "   AND gar.situacao in ('ANALISE')\n"
                //  + "   AND gar.importada = 'N'\n"
                //  + "   AND gar.notafiscalgarantia = 0\n"
                //  + "   AND gar.representante_id > 0\n"
                + "  \n";
        sqlSelect += PESQUISA;
        sqlSelect += " GROUP BY gar.pontogarantia_id,  gar.agrupador, notafiscalgarantia";

        System.out.print("  SQL " + sqlSelect);

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs, "S", "REP");

            pst.close();
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();

        }
        return resultado;
    }

    private List<GarantiaPortal> getLista(ResultSet rs, String tipo, String situacao) throws SQLException, ParseException {
        List<GarantiaPortal> resultado = new ArrayList<GarantiaPortal>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            GarantiaPortal a = new GarantiaPortal();
            a.setId(rs.getInt("id"));
            a.setAgrupador(rs.getInt("agrupador"));
            a.setGaratiasde(rs.getString("nome"));
            a.setDataabertura(rs.getDate("dataabertura"));
            a.setNotafiscalgarantia(rs.getString("notafiscalgarantia"));
            a.setGarantiacodigode(rs.getString("codigo"));
            a.setFornecedor_erp(rs.getInt("fornecedor_erp"));
            a.setSituacao(rs.getString("situacao"));
            a.setSituacaogeral(rs.getString("situacaogeral"));

            a.setGerarGarantia(true);
            a.setCliente_id(rs.getInt("cliente_id"));
            a.setRepresentante_id(rs.getInt("representante_id"));
            a.setPontogarantia_id(rs.getInt("pontogarantia_id"));
            a.setEmpresa_destino(rs.getInt("empresa_destino"));

            if (tipo.equals("A")) {

                a.setSequencia_erp(rs.getInt("sequencia_erp"));
                a.setCliente(rs.getInt("cliente"));
                a.setClientenome(rs.getString("clientenome"));
                a.setEmail(rs.getString("email"));
                a.setGarantiadia(rs.getInt("garantiadia"));
                a.setMotivo_id(rs.getInt("motivo_id"));
                a.setNota(rs.getInt("nota"));
                a.setProduto(rs.getString("produto"));
                a.setProdutodescricao(rs.getString("produtodescricao"));
                a.setProdutoderivacao(rs.getString("produtoderivacao"));
                a.setNota(rs.getInt("nota"));
                a.setNotaemissao(rs.getDate("notaemissao"));
                // a.setn
                a.setQuantidade(1.0);
                a.setSerie(rs.getString("serie"));
                // a.setTempouso(rs.getDouble("tempouso"));
                a.setTempousodia(rs.getDouble("tempousodia"));
                a.setTempousomes(rs.getDouble("tempousomes"));
                a.setGarantiadia(rs.getInt("garantiadia"));
                a.setGarantiames(rs.getDouble("garantiames"));
                a.setSituacao(rs.getString("situacao"));
                a.setDescricaoproblema(rs.getString("descricaoproblema"));
                a.setSequenciaitem(rs.getInt("sequenciaitem"));

                a.setNotaemissao(rs.getDate("notaemissao"));
                a.setPrazogarantia(rs.getDate("prazogarantia"));
                a.setPrazogarantiamaximo(this.utilDatas.getAdicionarDia(a.getPrazogarantia(), 31));
                a.setPrecounitario(rs.getDouble("precounitario"));
                a.setNotaserie(rs.getString("notaserie"));

                if (a.getMotivo_id() > 0) {
                    a.setMotivo_codigo(rs.getString("garmot.codigo"));
                    a.setMotivo_descricao(rs.getString("garmot.descricao"));
                } else {

                }

            }

            resultado.add(a);

        }
        return resultado;
    }

    public int proxCodCad(Integer cliente) throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_codlan) + 1), 1) codlan FROM usu_tcte ";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("codlan");
            }

            return codlct;
        } catch (SQLException e) {

            return -1;
        } catch (Exception ex) {

            return -1;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }

    @Override
    public List<GarantiaPortal> getGarantiaPortalsItens(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<GarantiaPortal> resultado = new ArrayList<GarantiaPortal>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT gar.*,\n"
                + "       garmot.codigo,\n"
                + "       garmot.descricao,\n"
                + "       cl.codigo,\n"
                + "       cl.nome,\n"
                + "       re.codigo,\n"
                + "       re.nome\n"
                + "  FROM garantia gar\n"
                + "  LEFT JOIN cliente cl\n"
                + "    ON (cl.id = gar.cliente_id)\n"
                + "  LEFT JOIN representante re\n"
                + "    ON (re.id = gar.representante_id)\n"
                + "  LEFT JOIN garantiamotivo garmot\n"
                + "    ON (garmot.id = gar.motivo_id)\n"
                + " WHERE 0 = 0\n"
                //  + "   AND gar.situacao in ('ANALISE','NOTA GERADA')\n"
                //  + "   AND gar.importada = 'N'\n"
                // + "   AND gar.notafiscalgarantia = 0\n"
                //   + "   AND gar.agrupador = 2\n"
                // + "   AND gar.agrupador = 3\n"
                + "  \n";
        sqlSelect += PESQUISA;

        System.out.print("  SQL " + sqlSelect);

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs, "A", "REP");

            pst.close();
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();

        }
        return resultado;
    }

    @Override
    public boolean liberar(GarantiaPortal t, int contador, int qtdreg) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "UPDATE  garantia SET  \n"
                + " situacao=?, \n"
                + " disposicao=?"
                + "  where id =  ?";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, t.getSituacao());
            pst.setString(2, t.getDisposicao());
            pst.setInt(3, t.getId());
            pst.executeUpdate();
            pst.close();

            if (contador == qtdreg) {
                JOptionPane.showMessageDialog(null, "Atenção: Garantias atualizada com sucesso ",
                        "OK:", JOptionPane.INFORMATION_MESSAGE);
            }

            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    public boolean importadaERP(GarantiaPortal t, int contador, int qtdreg) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "UPDATE  garantia SET  \n"
                + " importada=?,"
                + " empresa_destino=?,"
                + " sequencia_erp=?,"
                + " situacao=?, "
                + " disposicao=? "
                + "  where id =  ?";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, t.getSituacao());
            pst.setInt(2, t.getEmpresa_destino());
            pst.setInt(3, t.getSequencia_erp());
            pst.setString(4, "PROCEDENTE");
            pst.setString(5, "GARANTIA PROCEDENTE");
            pst.setInt(6, t.getId());
            pst.executeUpdate();
            pst.close();

//            if (contador == qtdreg) {
//                JOptionPane.showMessageDialog(null, "Atenção: Garantias atualizada com sucesso ",
//                        "OK:", JOptionPane.INFORMATION_MESSAGE);
//            }
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<GarantiaPortal> getGarantiaReceber(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<GarantiaPortal> resultado = new ArrayList<GarantiaPortal>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT MAX(gar.id) id,\n"
                + "       MAX(gp.nome) nome,\n"
                + "       MAX(gp.id) codigo,\n"
                + "       MAX(gp.fornecedor_erp) fornecedor_erp,\n"
                + "       MAX(gar.empresa_destino) empresa_destino,\n"
                + "       MAX(gar.agrupador) agrupador,\n"
                + "       MAX(gar.situacao) situacao,\n"
                + "       MAX(gar.situacaogeral) situacaogeral,\n"
                + "       MAX(dataabertura) dataabertura,\n"
                + "       MAX(pontogarantia_id) pontogarantia_id,\n"
                + "       MAX(gar.notafiscalgarantia) notafiscalgarantia,\n"
                + "       MAX(gar.representante_id) representante_id,\n"
                + "       MAX(gar.cliente_id) cliente_id\n"
                + "  FROM garantia gar\n"
                + "  LEFT JOIN garantiaponto gp\n"
                + "    ON (gp.id = gar.pontogarantia_id)\n"
                + " WHERE 0 = 0  \n"
                //  + "   AND gar.situacao in ('ANALISE')\n"
                //  + "   AND gar.importada = 'N'\n"
                //  + "   AND gar.notafiscalgarantia = 0\n"
                //  + "   AND gar.representante_id > 0\n"
                + "  \n";
        sqlSelect += PESQUISA;
        sqlSelect += " GROUP BY gar.pontogarantia_id, notafiscalgarantia";

        System.out.print("  SQL " + sqlSelect);

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs, "S", "REP");

            pst.close();
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();

        }
        return resultado;
    }

}
