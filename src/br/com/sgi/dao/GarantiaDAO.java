/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Fornecedor;
import br.com.sgi.bean.Garantia;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceGarantiaDAO;
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
public class GarantiaDAO implements InterfaceGarantiaDAO<Garantia> {

    private Connection con;
    private UtilDatas utilDatas;
    private final String PREFIXO = "usu_";

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public boolean remover(Garantia t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "delete  usu_tcte \n"
                + "where  usu_codlan=? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            //  pst.setInt(1, t.getUsu_codlan());
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
    public boolean alterar(Garantia t) throws SQLException {
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
    public boolean inserir(Garantia t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_t440nfc  \n"
                + "( usu_cgccpf,usu_sitnfc, usu_codpro, usu_datemi, usu_tipnfc, "
                + " usu_retter,usu_obsnfc, usu_codsnf, usu_garfis, usu_numdoc,"
                + " usu_numnfc, usu_empdes,  usu_codfor, usu_codfil )\n"
                + " values \n"
                + " (?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Garantia registrado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex +"\n\r "+t.getUsu_codfor(),
                    "Atenção: ", JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<Garantia> getGarantias(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<Garantia> resultado = new ArrayList<Garantia>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select usu_t440nfc.*, fo.codfor, fo.nomfor, fo.cgccpf\n"
                + "  from usu_t440nfc\n"
                + "  left join e095for fo\n"
                + "    on (fo.codfor = usu_t440nfc.usu_codfor)\n"
                + " where 0 = 0  \n";
        sqlSelect += PESQUISA;
        sqlSelect += " order by usu_datemi asc";

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
    public Garantia getGarantia(String PESQUISAR_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;
        ResultSet rs = null;

        String sqlSelect = "select usu_t440nfc.*, fo.codfor, fo.nomfor, fo.cgccpf\n"
                + "  from usu_t440nfc\n"
                + "  left join e095for fo\n"
                + "    on (fo.codfor = usu_t440nfc.usu_codfor)\n"
                + " where 0 = 0  \n";
        sqlSelect += PESQUISA;

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<Garantia> resultado = new ArrayList<Garantia>();
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

    private void setPreparedStatement(Garantia t,
            java.sql.PreparedStatement pst, String acao) throws SQLException {
        //usu_cgcpf,usu_sitnfc, usu_codpro, usu_datemi, usu_tipnfc, 
        pst.setString(1, t.getUsu_cgccpf());
        pst.setInt(2, t.getUsu_sitnfc());
        pst.setString(3, t.getUsu_codpro());
        pst.setDate(4, new Date(t.getUsu_datemi().getTime()));
        pst.setString(5, t.getUsu_tipnfc());

        //usu_retter,usu_obsnfc, usu_codsnf, usu_garfis, usu_numdc,"
        pst.setString(6, t.getUsu_retter());
        pst.setString(7, t.getUsu_obsnfc());
        pst.setString(8, t.getUsu_codsnf());
        pst.setString(9, t.getUsu_garfis());
        pst.setInt(10, t.getUsu_numdoc());

        //usu_empdes, usu_numnfc, usu_codfor, usu_codfil 
        pst.setInt(11, t.getUsu_numnfc());
        
        // chave
        pst.setInt(12, t.getUsu_empdes());
        pst.setInt(13, t.getUsu_codfor());
        pst.setInt(14, t.getUsu_codfil());

    }

    private List<Garantia> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Garantia> resultado = new ArrayList<Garantia>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Garantia a = new Garantia();
            a.setUsu_cgccpf(rs.getString(PREFIXO + "cgccpf"));
            a.setUsu_codfil(rs.getInt(PREFIXO + "codfil"));
            a.setUsu_codfor(rs.getInt(PREFIXO + "codfor"));
            if (a.getUsu_codfor() > 0) {
                a.setCadFornecedor(new Fornecedor(a.getUsu_codfor(), rs.getString("nomfor"), rs.getString("cgccpf")));
            } else {
                a.setCadFornecedor(new Fornecedor(0, "", ""));
            }

            a.setUsu_codpro(rs.getString(PREFIXO + "codpro"));
            a.setUsu_codsnf(rs.getString(PREFIXO + "codsnf"));
            a.setUsu_datemi(rs.getDate(PREFIXO + "datemi"));
            a.setUsu_empdes(rs.getInt(PREFIXO + "empdes"));
            a.setUsu_garfis(rs.getString(PREFIXO + "garfis"));
            a.setUsu_numdoc(rs.getInt(PREFIXO + "numdoc"));
            a.setUsu_numnfc(rs.getInt(PREFIXO + "numnfc"));
            a.setUsu_obsnfc(rs.getString(PREFIXO + "obsnfc"));
            a.setUsu_retter(rs.getString(PREFIXO + "retter"));
            a.setUsu_sitnfc(rs.getInt(PREFIXO + "sitnfc"));

            switch (a.getUsu_sitnfc()) {
                case 1:
                    a.setSituacao("Digitada");
                    break;

                case 2:
                    a.setSituacao("Inspecionada");
                    break;
                case 3:
                    a.setSituacao("Fechada");
                    break;
                case 4:
                    a.setSituacao("Nf Gerada");
                    break;
                default:
                    break;
            }
            a.setUsu_tipnfc(rs.getString(PREFIXO + "tipnfc"));

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

}
