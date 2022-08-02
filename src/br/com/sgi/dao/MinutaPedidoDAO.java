/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Minuta;
import br.com.sgi.interfaces.InterfaceMinutaPedidoDAO;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
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
public class MinutaPedidoDAO implements InterfaceMinutaPedidoDAO<MinutaPedido> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(MinutaPedido e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        //usu_codtra, usu_codcli, usu_numped, usu_codpes, usu_lansuc, \n"
        pst.setInt(1, e.getUsu_codtra());
        pst.setInt(2, e.getUsu_codcli());
        pst.setInt(3, e.getUsu_numped());
        pst.setInt(4, e.getUsu_codpes());
        pst.setInt(5, e.getUsu_lansuc());

        //"usu_numnfv, usu_codsnf, usu_tnspro, usu_codtpr, usu_codori, \n"
        pst.setInt(6, e.getUsu_numnfv());
        pst.setString(7, e.getUsu_codsnf());
        pst.setString(8, e.getUsu_tnspro());
        pst.setString(9, e.getUsu_codtpr());
        pst.setString(10, e.getUsu_codori());
        // "usu_pesped, usu_pesnfv, usu_pessuc, usu_pessld, usu_pesbal, \n"
        pst.setDouble(11, e.getUsu_pesped());
        pst.setDouble(12, e.getUsu_pesnfv());
        pst.setDouble(13, e.getUsu_pessuc());
        pst.setDouble(14, e.getUsu_pessld());
        pst.setDouble(15, e.getUsu_pesbal());
        // "usu_pesrec, usu_qtdped, usu_qtdvol, usu_qtdfat, usu_sitmin, \n"
        pst.setDouble(16, e.getUsu_pesrec());
        pst.setDouble(17, e.getUsu_qtdped());
        pst.setDouble(18, e.getUsu_qtdvol());
        pst.setDouble(19, e.getUsu_qtdfat());
        pst.setString(20, e.getUsu_sitmin());
        // usu_obsmin, usu_datemi, usu_datlib, usu_codemp, usu_codfil, \n"
        pst.setString(21, e.getUsu_obsmin());
        pst.setDate(22, new Date(e.getUsu_datemi().getTime()));
        pst.setDate(23, null);
        pst.setInt(24, e.getUsu_codemp());
        pst.setInt(25, e.getUsu_codfil());

        // usu_seqite, usu_numpfa, usu_numana, usu_salsuc, usu_codlan
        pst.setInt(26, e.getUsu_seqite());
        pst.setInt(27, e.getUsu_numpfa());
        pst.setInt(28, e.getUsu_numana());
        pst.setDouble(29, e.getUsu_salsuc());
        pst.setInt(30, e.getUsu_codlan());

    }

    private List<MinutaPedido> getLista(ResultSet rs) throws SQLException, ParseException {
        List<MinutaPedido> resultado = new ArrayList<MinutaPedido>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            MinutaPedido e = new MinutaPedido();
            e.setUsu_codemp(rs.getInt("usu_codemp"));
            e.setUsu_codfil(rs.getInt("usu_codfil"));
            e.setUsu_codlan(rs.getInt("usu_codlan"));
            e.setUsu_seqite(rs.getInt("usu_seqite"));
            e.setUsu_codori(rs.getString("usu_codori"));
            e.setUsu_codori("BM");
            e.setUsu_codcli(rs.getInt("usu_codcli"));
            e.setUsu_codpes(rs.getInt("usu_codpes"));
            e.setUsu_codsnf(rs.getString("usu_codsnf"));
            e.setUsu_codtpr(rs.getString("usu_codtpr"));
            e.setUsu_codtra(rs.getInt("usu_codtra"));
            e.setUsu_numped(rs.getInt("usu_numped"));
            e.setUsu_numnfv(rs.getInt("usu_numnfv"));
            e.setUsu_lansuc(rs.getInt("usu_lansuc"));

            e.setUsu_numana(rs.getInt("usu_numane"));
            e.setUsu_numpfa(rs.getInt("usu_numpfa"));
            e.setUsu_salsuc(rs.getDouble("usu_salsuc"));

            e.setUsu_datemi(rs.getDate("usu_datemi"));
            if (e.getUsu_datemi() != null) {
                e.setEmissaoS(this.utilDatas.converterDateToStr(e.getUsu_datemi()));
            }
            e.setUsu_datlib(rs.getDate("usu_datlib"));

            e.setUsu_qtdfat(rs.getDouble("usu_qtdfat"));
            e.setUsu_qtdvol(rs.getDouble("usu_qtdvol"));
            e.setUsu_qtdped(rs.getDouble("usu_qtdped"));

            e.setUsu_obsmin(rs.getString("usu_obsmin"));
            e.setUsu_pesbal(rs.getDouble("usu_pesbal"));
            e.setUsu_pesnfv(rs.getDouble("usu_pesnfv"));
            e.setUsu_pesped(rs.getDouble("usu_pesped"));
            e.setUsu_pesrec(rs.getDouble("usu_pesrec"));
            e.setUsu_pessld(rs.getDouble("usu_pessld"));
            e.setUsu_pessuc(rs.getDouble("usu_pessuc"));
            e.setUsu_sitmin(rs.getString("usu_sitmin"));

            e.setTipodocumento("NF");
            e.setUsu_tnspro(rs.getString("usu_tnspro"));
            if (e.getUsu_tnspro() == null) {

            } else {
                switch (e.getUsu_tnspro()) {
                    case "90126":
                        e.setTipodocumento("R");
                        e.setTipopedido("MKT");

                        break;
                    case "90112":
                        e.setTipodocumento("R");
                        e.setTipopedido("GAR");
                        break;
                    case "90113":
                        e.setTipodocumento("NG");
                        e.setTipopedido("GAR");
                        break;

                    case "90122":
                        e.setTipodocumento("R");
                        e.setTipopedido("GAR");
                        break;
                    case "90123":
                        e.setTipodocumento("NG");
                        e.setTipopedido("GAR");
                        break;
                    default:
                        break;
                }
            }

            e.setCadCliente(new Cliente(e.getUsu_codcli(),
                    rs.getString("nomcli"),
                    rs.getString("sigufs"),
                    rs.getString("cidcli")));

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean remover(MinutaPedido t) throws SQLException {
        PreparedStatement pst = null;

        try {
            openConnectionMySql();
            pst = con.prepareStatement("UPDATE usuario  SET situacao=? "
                    + "WHERE  id = ? ");

            pst.setString(1, t.getUsu_sitmin());
            pst.setInt(2, t.getUsu_codlan());
            pst.executeUpdate();
            return true;

        } catch (SQLException e) {

            return false;
        } catch (Exception ex) {
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public boolean alterar(MinutaPedido t) throws SQLException {
        PreparedStatement pst = null;
        try {
            String sqlInsert = "UPDATE usu_tminuta_i SET \n"
                    + " usu_obsmin=?, "
                    + " usu_numpfa=?, "
                    + " usu_numane=?\n"
                    + " where usu_codlan=? "
                    + "  and usu_seqite=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, t.getUsu_obsmin());
            pst.setInt(2, t.getUsu_numpfa());
            pst.setInt(3, t.getUsu_numana());
            pst.setInt(4, t.getUsu_codlan());
            pst.setInt(5, t.getUsu_seqite());

            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "Registro alterado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public boolean inserir(MinutaPedido t, Integer qtdreg, Integer contador) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into  usu_tminuta_i\n"
                + "(usu_codtra, usu_codcli, usu_numped, usu_codpes, usu_lansuc, \n"
                + "usu_numnfv, usu_codsnf, usu_tnspro, usu_codtpr, usu_codori, \n"
                + "usu_pesped, usu_pesnfv, usu_pessuc, usu_pessld, usu_pesbal, \n"
                + "usu_pesrec, usu_qtdped, usu_qtdvol, usu_qtdfat, usu_sitmin, \n"
                + "usu_obsmin, usu_datemi, usu_datlib, usu_codemp, usu_codfil,\n"
                + "usu_seqite, usu_numpfa, usu_numane, usu_salsuc, usu_codlan)\n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,"
                + " ?,?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();

            if (qtdreg == contador) {
                JOptionPane.showMessageDialog(null, "Minuta registrada com sucesso",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
            }

            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO" + ex,
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<MinutaPedido> getMinutaPedidos(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<MinutaPedido> resultado = new ArrayList<MinutaPedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select minI.*, cli.nomcli, cli.cidcli, cli.sigufs\n"
                + "  from usu_tminuta_i minI\n"
                + "  left join e085cli cli\n"
                + "    on cli.codcli = minI.usu_codcli\n"
                + " where 0 = 0 \n ";
        sqlSelect += PESQUISA;

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
    public MinutaPedido getMinutaPedido(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<MinutaPedido> resultado = new ArrayList<MinutaPedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select minI.*, cli.nomcli, cli.cidcli, cli.sigufs\n"
                + "  from usu_tminuta_i minI\n"
                + "  left join e085cli cli\n"
                + "    on cli.codcli = minI.usu_codcli\n"
                + " where 0 = 0 \n ";
        sqlSelect += PESQUISA;

        //sqlSelect += "order by min.usu_codlan desc";
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs);
            if (resultado.size() > 0) {
                return resultado.get(0);
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

    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(USU_SEQITE) + 1), 1) PROX_CODALAN FROM usu_tminuta_i";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("PROX_CODALAN");
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
    public boolean gravarDadosMinuta(Minuta t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE E135EMB SET \n"
                    + " codemb=?, qtdemb=? \n"
                    + " where codemp=? and codfil=? and  numpfa=? and numane=? and seqemb = 1";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setInt(1, t.getUsu_codemb());
            pst.setInt(2, t.getUsu_qtdvol());

            pst.setInt(3, t.getUsu_codemp());
            pst.setInt(4, t.getUsu_codfil());
            pst.setInt(5, t.getUsu_numpfa());
            pst.setInt(6, t.getUsu_numane());

            //pst.executeUpdate();
            if (pst.executeUpdate() > 0) {
                gravarDadosMinutaGeral(t);
            }

            pst.close();

            JOptionPane.showMessageDialog(null, "Volume e embalagem atualizados na pré fatura",
                    "Atenção", JOptionPane.ERROR_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    public boolean gravarDadosMinutaGeral(Minuta t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE E135EMB SET \n"
                    + " codemb=?, qtdemb=? \n"
                    + " where codemp=? and codfil=? and  numpfa=? and numane=? and seqemb > 1";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setInt(1, t.getUsu_codemb());
            pst.setDouble(2, 0.0);

            pst.setInt(3, t.getUsu_codemp());
            pst.setInt(4, t.getUsu_codfil());
            pst.setInt(5, t.getUsu_numpfa());
            pst.setInt(6, t.getUsu_numane());

            pst.executeUpdate();
            pst.close();
//
//            JOptionPane.showMessageDialog(null, "Registro alterado com sucesso",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

}
