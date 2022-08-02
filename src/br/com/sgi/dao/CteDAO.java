/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cte;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceCteDAO;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.Date;
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
public class CteDAO implements InterfaceCteDAO<Cte> {

    private Connection con;
    private UtilDatas utilDatas;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public boolean remover(Cte t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "delete  usu_tcte \n"
                + "where  usu_codlan=? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setInt(1, t.getUsu_codlan());

            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Cte alterado com sucesso",
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
    public boolean alterar(Cte t) throws SQLException {
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
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Cte alterado com sucesso",
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
    public boolean inserir(Cte t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_tcte  \n"
                + "( usu_codtra, usu_numocp, usu_datlan, usu_numcte, usu_chacte,"
                + " usu_valcte, usu_valnfv, usu_pesnfv, usu_estdes, usu_datval, "
                + " usu_perfrefat, usu_valfrepes, usu_numnfv,usu_codcli,usu_codsnf,"
                + " usu_tnspro,usu_tipfre,usu_linpro,usu_pesfat,usu_pesfre,"
                + " usu_gerocp,usu_qtdpro,usu_codfor, usu_codccu,usu_ctafin,"
                + " usu_sitocp, usu_cplocp, usu_codser, usu_codpro, usu_codlan,"
                + " usu_codemp, usu_codfil)\n"
                + " values \n"
                + "(?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Cte registrado com sucesso",
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
    public List<Cte> getCtes(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<Cte> resultado = new ArrayList<Cte>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select * from usu_tcte \n"
                + "where 0 = 0 \n";
        sqlSelect += PESQUISA;
        sqlSelect += " order by usu_codlan desc";

        System.out.print("  SQL " + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs);

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
    public Cte getCte(String PESQUISAR_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;
        ResultSet rs = null;

        String sqlSelect = "select * from usu_tcte \n"
                + "where 0 = 0 \n";
        sqlSelect += PESQUISA;
        sqlSelect += " order by usu_codlan desc";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<Cte> resultado = new ArrayList<Cte>();
            resultado = getLista(rs);
            if (resultado.size() > 0) {
                return resultado.get(0);
            } else {

            }
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
        return null;
    }

    private void setPreparedStatement(Cte t,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        // usu_codtra, usu_numocp, usu_datlan, usu_numcte, usu_chacte, 
        pst.setInt(1, t.getUsu_codtra());
        pst.setInt(2, t.getUsu_numocp());
        pst.setDate(3, new Date(t.getUsu_datlan().getTime()));
        pst.setString(4, t.getUsu_numcte());
        pst.setString(5, t.getUsu_chacte());
        //usu_valcte, usu_valnfv, usu_pesnfv, usu_estdes, usu_datval,
        pst.setDouble(6, t.getUsu_valcte());
        pst.setDouble(7, t.getUsu_valnfv());
        pst.setDouble(8, t.getUsu_pesnfv());
        pst.setString(9, t.getUsu_estdes());
        if (t.getUsu_datval() == null) {
            pst.setDate(10, null);
        } else {
            pst.setDate(10, new Date(t.getUsu_datval().getTime()));
        }
        //usu_perfrefat, usu_valfrepes, usu_numnfv,usu_codcli,usu_codsnf,
        pst.setDouble(11, t.getUsu_perfrefat());
        pst.setDouble(12, t.getUsu_valfrepes());
        pst.setInt(13, t.getUsu_numnfv());
        pst.setInt(14, t.getUsu_codcli());
        pst.setString(15, t.getUsu_codsnf());

        //usu_tnspro,usu_tipfre,usu_linpro,usu_pesfat,usu_pesfre
        pst.setString(16, t.getUsu_tnspro());
        pst.setString(17, t.getUsu_tipfre());
        pst.setString(18, t.getUsu_linpro());
        pst.setDouble(19, t.getUsu_pesfat());
        pst.setDouble(20, t.getUsu_pesfre());

        //usu_gerocp,usu_qtdpro,usu_codfor, usu_codccu,usu_ctafin
        pst.setString(21, t.getUsu_gerocp());
        pst.setDouble(22, t.getUsu_qtdpro());
        pst.setInt(23, t.getUsu_codfor());
        pst.setString(24, t.getUsu_codccu());
        pst.setString(25, t.getUsu_ctafin());

        //usu_sitocp, usu_cplocp, usu_codser, usu_codpro, usu_codlan,
        pst.setString(26, t.getUsu_sitocp());
        pst.setString(27, t.getUsu_cplocp());
        pst.setString(28, t.getUsu_codser());
        pst.setString(29, t.getUsu_codpro());
        pst.setInt(30, t.getUsu_codlan());
        // usu_codemp, usu_codfil
        pst.setInt(31, t.getUsu_codemp());
        pst.setInt(32, t.getUsu_codfil());

    }

    private List<Cte> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Cte> resultado = new ArrayList<Cte>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Cte a = new Cte();
            a.setUsu_chacte(rs.getString("usu_chacte"));
            a.setUsu_codemp(rs.getInt("usu_codemp"));
            a.setUsu_codfil(rs.getInt("usu_codfil"));
            a.setUsu_codlan(rs.getInt("usu_codlan"));
            a.setUsu_codtra(rs.getInt("usu_codtra"));

            a.setUsu_datlan(rs.getDate("usu_datlan"));
            a.setUsu_datval(rs.getDate("usu_datval"));
            a.setUsu_estdes(rs.getString("usu_estdes"));
            a.setUsu_numcte(rs.getString("usu_numcte"));
            a.setUsu_numocp(rs.getInt("usu_numocp"));

            a.setUsu_perfrefat(rs.getDouble("usu_perfrefat"));
            a.setUsu_pesnfv(rs.getInt("usu_pesnfv"));
            a.setUsu_valcte(rs.getDouble("usu_valcte"));
            a.setUsu_valfrepes(rs.getDouble("usu_valfrepes"));
            a.setUsu_valnfv(rs.getDouble("usu_valnfv"));

            a.setUsu_numnfv(rs.getInt("usu_numnfv"));
            a.setUsu_codcli(rs.getInt("usu_codcli"));
            a.setUsu_codsnf(rs.getString("usu_codsnf"));
            a.setUsu_tnspro(rs.getString("usu_tnspro"));
            a.setUsu_tipfre(rs.getString("usu_tipfre"));
            a.setUsu_linpro(rs.getString("usu_linpro"));
            a.setUsu_pesfat(rs.getDouble("usu_pesfat"));
            a.setUsu_pesfre(rs.getDouble("usu_pesfre"));
            a.setUsu_gerocp(rs.getString("usu_gerocp"));
            a.setUsu_qtdpro(rs.getDouble("usu_qtdpro"));

            a.setUsu_codfor(rs.getInt("usu_codfor"));
            a.setUsu_codccu(rs.getString("usu_codccu"));
            a.setUsu_ctafin(rs.getString("usu_ctafin"));
            a.setUsu_sitocp(rs.getString("usu_sitocp"));
            a.setUsu_cplocp(rs.getString("usu_cplocp"));

            a.setUsu_codser(rs.getString("usu_codser"));
            a.setUsu_codpro(rs.getString("usu_codpro"));

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
    public boolean atualizarOC(Cte t, int qtdreg, int contador) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update  usu_tcte set  usu_numocp=?, usu_gerocp=?\n"
                + " where  usu_codlan=? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setInt(1, t.getUsu_numocp());
            pst.setString(2, t.getUsu_sitocp());
            pst.setInt(3, t.getUsu_codlan());

            pst.executeUpdate();
            pst.close();
            if (contador == qtdreg) {
                JOptionPane.showMessageDialog(null, " Ordem de compra " + t.getUsu_numocp() + " gerada com sucesso",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
            }

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
    public List<Cte> getCtesAgrupado(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<Cte> resultado = new ArrayList<Cte>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(cte.usu_codtra) usu_codtra,\n"
                + "       max(cte.usu_codfor) usu_codfor,\n"
                + "       max(tra.nomtra) nomtra,\n"
                + "       sum(usu_valcte) usu_valcte,\n"
                + "       sum(usu_valnfv) usu_valnfv,\n"
                + "       sum(usu_pesnfv) usu_pesnfv,\n"
                + "       max(usu_codemp) usu_codemp,\n"
                + "       max(usu_codfil) usu_codfil,\n"
                + "       max(usu_ctafin) usu_ctafin,\n"
                + "       count(*) totalcte\n"
                + "\n"
                + "  from usu_tcte cte\n"
                + "  left join e073tra tra\n"
                + "    on cte.usu_codtra = tra.codtra\n"
                + "  left join e085cli cli\n"
                + "    on cli.codcli = cte.usu_codcli\n"
                + " where 0 = 0\n"
                + " ";
        sqlSelect += PESQUISA;
        sqlSelect += " group by cte.usu_codfor\n"
                + " order by cte.usu_codfor";

        System.out.print("  SQL " + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            while (rs.next()) {

                Cte a = new Cte();
                a.setUsu_codemp(rs.getInt("usu_codemp"));
                a.setUsu_codfil(rs.getInt("usu_codfil"));
                a.setUsu_codtra(rs.getInt("usu_codtra"));
                a.setUsu_pesnfv(rs.getInt("usu_pesnfv"));
                a.setUsu_valcte(rs.getDouble("usu_valcte"));
                a.setUsu_valnfv(rs.getDouble("usu_valnfv"));
                a.setUsu_codfor(rs.getInt("usu_codfor"));
                a.setUsu_ctafin(rs.getString("usu_ctafin"));
                if(a.getUsu_ctafin()==null){
                    a.setUsu_ctafin("0");
                }
                a.setTotalcte(rs.getInt("totalcte"));

                a.setCadTransportadora(new Transportadora(a.getUsu_codtra(), rs.getString("nomtra")));

                resultado.add(a);
            }

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
