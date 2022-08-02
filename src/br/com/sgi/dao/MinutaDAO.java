/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.interfaces.InterfaceMinutaDAO;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.Transportadora;
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
public class MinutaDAO implements InterfaceMinutaDAO<Minuta> {
    
    private Connection con;
    private UtilDatas utilDatas;
    
    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }
    
    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    
    private void setPreparedStatement(Minuta e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        // usu_codtra, usu_sitmin, usu_datemi, usu_datlib, usu_datsai,"
        pst.setInt(1, e.getUsu_codtra());
        pst.setString(2, e.getUsu_sitmin());
        pst.setDate(3, new Date(e.getUsu_datemi().getTime()));
        if (e.getUsu_datlib() != null) {
            pst.setDate(4, new Date(e.getUsu_datlib().getTime()));
        } else {
            pst.setDate(4, null);
        }
        
        if (e.getUsu_datsai() != null) {
            pst.setDate(5, new Date(e.getUsu_datsai().getTime()));
        } else {
            pst.setDate(5, null);
        }

        // "usu_usuger=?, usu_usulib=?, usu_obsmin=?, usu_libmot=?, usu_codmtr=?,"
        pst.setInt(6, e.getUsu_usuger());
        pst.setInt(7, e.getUsu_usulib());
        pst.setString(8, e.getUsu_obsmin());
        pst.setString(9, e.getUsu_libmot());
        pst.setInt(10, e.getUsu_codmtr());
        // usu_plavei=?, usu_codemp=?, usu_codfil=?, usu_codlan=?, usu_qtdfat=?,
        pst.setString(11, e.getUsu_plavei());
        pst.setDouble(12, e.getUsu_qtdfat());
        pst.setDouble(13, e.getUsu_qtdvol());
        if (e.getUsu_pesfat() == null) {
            e.setUsu_pesfat(0.0);
        }
        pst.setDouble(14, e.getUsu_pesfat());
        pst.setInt(15, e.getUsu_codemp());

        //usu_orimin, usu_nommun, usu_codfil, usu_codemb,  usu_codlan
        pst.setString(16, e.getUsu_orimin());
        pst.setString(17, e.getUsu_nommin());
        pst.setInt(18, e.getUsu_codfil());
        pst.setInt(19, e.getUsu_codemb());
        pst.setInt(20, e.getUsu_codlan());
        
    }
    
    private List<Minuta> getLista(ResultSet rs, String geral) throws SQLException, ParseException {
        List<Minuta> resultado = new ArrayList<Minuta>();
        
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Minuta e = new Minuta();
            e.setUsu_codemp(rs.getInt("usu_codemp"));
            e.setUsu_numnfv(rs.getInt("usu_numnfv"));
            e.setUsu_codfil(rs.getInt("usu_codfil"));
            e.setUsu_codlan(rs.getInt("usu_codlan"));
            e.setUsu_codemb(rs.getInt("usu_codemb"));
            e.setUsu_codmtr(rs.getInt("usu_codmtr"));
            e.setUsu_codtra(rs.getInt("usu_codtra"));
            e.setUsu_datemi(rs.getDate("usu_datemi"));
            e.setUsu_datlib(rs.getDate("usu_datlib"));
            e.setUsu_datsai(rs.getDate("usu_datsai"));
            e.setUsu_libmot(rs.getString("usu_libmot"));
            e.setUsu_obsmin(rs.getString("usu_obsmin"));
            e.setUsu_plavei(rs.getString("usu_plavei"));
            e.setUsu_sitmin(rs.getString("usu_sitmin"));
            e.setUsu_usuger(rs.getInt("usu_usuger"));
            e.setUsu_usulib(rs.getInt("usu_usulib"));
            e.setUsu_qtdfat(rs.getDouble("usu_qtdfat"));
            e.setUsu_pesfat(rs.getDouble("usu_pesfat"));
            e.setUsu_qtdvol(rs.getInt("usu_qtdvol"));
            
            e.setUsu_ticbal(rs.getInt("usu_ticbal"));
            e.setUsu_pesbal(rs.getDouble("usu_pesbal"));
            e.setUsu_pesbalsal(rs.getDouble("usu_pessalbal"));
            e.setUsu_sitmin(rs.getString("usu_sitmin"));
            e.setUsu_libmindiv(rs.getInt("usu_libmindiv"));
            e.setUsu_orimin(rs.getString("usu_orimin"));
            e.setUsu_obspes(rs.getString("usu_obspes"));
            e.setUsu_nommin(rs.getString("usu_nommun"));
            
            Transportadora tra = new Transportadora();
            tra.setCodigoTransportadora(e.getUsu_codtra());
            tra.setNomeTransportadora(rs.getString("nomtra"));
            e.setCadTransportadora(tra);
            
            if (geral.equals("N")) {
                MinutaPedido m = new MinutaPedido();
                
                e.setMinutaPedido(m);
                
            }
            if (geral.equals("S")) {
                MinutaPedido m = new MinutaPedido();
                m.setUsu_numped(rs.getInt("usu_numped"));
                m.setUsu_numnfv(rs.getInt("usu_numnfv"));
                m.setUsu_numpfa(rs.getInt("usu_numpfa"));
                m.setUsu_numana(rs.getInt("usu_numane"));
                m.setUsu_qtdped(rs.getDouble("usu_qtdped"));
                m.setUsu_pesped(rs.getDouble("usu_pesped"));
                m.setUsu_sitmin(rs.getString("sitminped"));
                m.setUsu_codcli(rs.getInt("usu_codcli"));
                
                m.setTipopedido("FAT");
                m.setUsu_tnspro(rs.getString("usu_tnspro"));
                e.setTransacao(m.getUsu_tnspro());
                
                if (m.getUsu_tnspro() != null) {
                    switch (m.getUsu_tnspro()) {
                        case "90126":
                            m.setTipopedido("MKT");
                            
                            break;
                        case "90112":
                            m.setTipopedido("GAR_R");
                            break;
                        case "90113":
                            m.setTipopedido("GAR_N");
                            break;
                        
                        case "90122":
                            m.setTipopedido("GAR_R");
                            break;
                        case "90123":
                            m.setTipopedido("GAR_N");
                            break;
                        case "90124":
                            m.setTipopedido("MET");
                            break;
                        case "G":
                            m.setTipopedido("MG");
                            break;
                        default:
                            break;
                    }
                }
                e.setMinutaPedido(m);
                
                Cliente cli = new Cliente();
                cli.setCodigo(rs.getInt("usu_codcli"));
                cli.setNome(rs.getString("nomcli"));
                cli.setEstado(rs.getString("sigufs"));
                cli.setCidade(rs.getString("cidcli"));
                m.setCadCliente(cli);
                
            }
            
            resultado.add(e);
        }
        return resultado;
    }
    
    @Override
    public boolean remover(Minuta t) throws SQLException {
        PreparedStatement pst = null;
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement("UPDATE usu_tminuta  SET usu_sitmin=? "
                    + "WHERE  usu_codlan = ? ");
            
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
    
    public boolean removerItens(Minuta t) throws SQLException {
        PreparedStatement pst = null;
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement("UPDATE usu_tminuta_i  SET usu_sitmin=? "
                    + "WHERE  usu_codlan = ? ");
            
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
    public boolean alterar(Minuta t) throws SQLException {
        PreparedStatement pst = null;
        
        try {
            
            String sqlInsert = "UPDATE usu_tminuta SET \n"
                    + "usu_codtra=?, usu_sitmin=?, usu_datemi=?, usu_datlib=?, usu_datsai=?,"
                    + "usu_usuger=?, usu_usulib=?, usu_obsmin=?, usu_libmot=?, usu_codmtr=?, "
                    + "usu_plavei=?, usu_qtdfat=?, usu_qtdvol=?, usu_pesfat=?,  usu_codemp=?, "
                    + "usu_orimin=?, usu_nommun=?, usu_codfil=?, usu_codemb=? \n"
                    + " where usu_codlan=?";
            
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "A");
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
    public boolean inserir(Minuta t) throws SQLException {
        PreparedStatement pst = null;
        
        String sqlInsert = "insert into USU_TMINUTA\n"
                + "(usu_codtra, usu_sitmin, usu_datemi, usu_datlib, usu_datsai,"
                + "usu_usuger, usu_usulib, usu_obsmin, usu_libmot, usu_codmtr, "
                + "usu_plavei, usu_qtdfat, usu_qtdvol, usu_pesfat,  usu_codemp,"
                + "usu_orimin, usu_nommun, usu_codfil, usu_codemb,  usu_codlan)\n"
                + "values\n"
                + "(?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,"
                + "?,?,?,?,?)";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();

//            JOptionPane.showMessageDialog(null, "Registro de carga inserido com sucesso",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
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
    public List<Minuta> getMinutas(String PESQUISA_POR, String PESQUISA, String geral) throws SQLException {
        List<Minuta> resultado = new ArrayList<Minuta>();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "Select min.*, "
                + "  tra.codtra, tra.nomtra, tra.cidtra, tra.sigufs, tra.apetra\n"
                + "  from usu_tminuta min\n"
                + "  left join e073tra tra\n"
                + "    on (tra.codtra = min.usu_codtra)\n"
                + " where 0 = 0 \n ";
        sqlSelect += PESQUISA;
        
        sqlSelect += "order by min.usu_codlan desc";
        System.out.println("br.com.recebimento.dao.MinutaDAO.getMinutas()" + sqlSelect);
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            
            rs = pst.executeQuery();
            
            resultado = getLista(rs, "N");
            
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
    public Minuta getMinuta(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Minuta> resultado = new ArrayList<Minuta>();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "Select min.*, "
                + "  tra.codtra, tra.nomtra, tra.cidtra, tra.sigufs, tra.apetra\n"
                + "  from usu_tminuta min\n"
                + "  left join e073tra tra\n"
                + "    on (tra.codtra = min.usu_codtra)\n"
                + " where min.usu_codemp > 0 \n ";
        sqlSelect += PESQUISA;
        
        sqlSelect += "order by min.usu_codlan desc";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            
            rs = pst.executeQuery();
            
            resultado = getLista(rs, "N");
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
        String strSql = "SELECT NVL((MAX(usu_codlan) + 1), 1) PROX_CODALAN FROM usu_tminuta";
        
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
    public boolean imprimir(Minuta t) throws SQLException {
        PreparedStatement pst = null;
        
        try {
            
            String sqlInsert = "UPDATE usu_tminuta SET \n"
                    + " usu_sitsuc='I'\n"
                    + " where usu_codlan=?";
            
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, t.getUsu_codlan());
            pst.executeUpdate();
            pst.close();
            
            JOptionPane.showMessageDialog(null, "Aguarde a geração relatório",
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
    public boolean gravarDadosPesagem(Minuta t) throws SQLException {
        PreparedStatement pst = null;
        
        try {
            
            String sqlInsert = "UPDATE usu_tminuta SET \n"
                    + " usu_pesbal=?, usu_ticbal=?, usu_libmindiv=?, usu_pessalbal=?, usu_obspes=upper(?), usu_sitmin=?, usu_sitsuc=?\n"
                    + " where usu_codlan=?";
            
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            
            pst.setDouble(1, t.getUsu_pesbal());
            pst.setInt(2, t.getUsu_ticbal());
            pst.setInt(3, t.getUsu_libmindiv());
            pst.setDouble(4, t.getUsu_pesbalsal());
            pst.setString(5, t.getUsu_obspes());
            
            pst.setString(6, t.getUsu_sitmin());
            pst.setString(7, t.getUsu_sitsuc());
            
            pst.setInt(8, t.getUsu_codlan());
            pst.executeUpdate();
            pst.close();
            
            JOptionPane.showMessageDialog(null, "Peso da Balança atualizado com Sucesso ",
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
    public List<Minuta> getMinutasDetalhada(String PESQUISA_POR, String PESQUISA, String geral) throws SQLException {
        List<Minuta> resultado = new ArrayList<Minuta>();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "Select min.*,\n"
                + "       tra.codtra,\n"
                + "       tra.nomtra,\n"
                + "       tra.cidtra,\n"
                + "       tra.sigufs,\n"
                + "       tra.apetra,\n"
                + "       minI.usu_sitmin as sitminped,\n"
                + "       minI.usu_qtdped,\n"
                + "       minI.usu_pesped,\n"
                + "       minI.usu_numped,\n"
                + "       minI.Usu_Numpfa,\n"
                + "       minI.Usu_Numane,\n"
                + "       minI.Usu_NumNfv,\n"
                + "       minI.usu_codcli,\n"
                + "       minI.usu_tnspro,\n"
                + "       cli.codcli,\n"
                + "       cli.nomcli,\n"
                + "       cli.sigufs,\n"
                + "       cli.cidcli\n"
                + "  from usu_tminuta_i minI\n"
                + "  left join usu_tminuta min\n"
                + "    on min.usu_codemp = minI.usu_codemp\n"
                + "   and min.usu_codlan = minI.usu_codlan\n"
                + "  left join e085cli cli\n"
                + "    on minI.usu_codcli = cli.codcli\n"
                + "  left join e073tra tra\n"
                + "    on (tra.codtra = min.usu_codtra)\n"
                + " where  0 = 0\n";
        sqlSelect += PESQUISA;
        
        sqlSelect += "order by min.usu_codlan desc";
        
        System.out.println("br.com.recebimento.dao.MinutaDAO.getMinutasDetalhada()" + sqlSelect);
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            
            rs = pst.executeQuery();
            
            resultado = getLista(rs, "S");
            
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
    public boolean gravarDadosMinuta(Minuta t, MinutaPedido mp) throws SQLException {
        PreparedStatement pst = null;
        
        try {
            
            String sqlInsert = "UPDATE E135EMB SET \n"
                    + " codemb=?, qtdemb=? \n"
                    + " where codemp=?´and codfil=? and  numpfa=? and numane=? and seqemp = 1";
            
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            
            pst.setInt(1, t.getUsu_codemb());
            pst.setDouble(2, t.getUsu_qtdvol());
            
            pst.setInt(3, t.getUsu_codemp());
            pst.setInt(4, t.getUsu_codfil());
            pst.setInt(5, mp.getUsu_numpfa());
            pst.setInt(6, mp.getUsu_numana());
            
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
    
}
