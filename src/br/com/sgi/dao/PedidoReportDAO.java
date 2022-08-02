/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.PedidoReport;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.sgi.interfaces.InterfacePedidoReportDAO;

/**
 *
 * @author jairosilva Teste Jairo
 */
public class PedidoReportDAO implements InterfacePedidoReportDAO<PedidoReport> {
    
    private Connection con;
    private UtilDatas utilDatas;
    
    private String usu = "usu_";
    
    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }
    
    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    
    private List<PedidoReport> getLista(ResultSet rs) throws SQLException, ParseException {
        
        NotaFiscalDAO dao = new NotaFiscalDAO();
        List<PedidoReport> resultado = new ArrayList<>();
        
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            PedidoReport e = new PedidoReport();
            e.setUsu_codcli(rs.getInt(usu + "codcli"));
            e.setUsu_codemp(rs.getInt(usu + "codemp"));
            e.setUsu_codfil(rs.getInt(usu + "codfil"));
            e.setUsu_codlan(rs.getInt(usu + "codlan"));
            e.setUsu_datenv(rs.getDate(usu + "datenv"));
            e.setUsu_horenv(rs.getString(usu + "horenv"));
            e.setUsu_linped(rs.getString(usu + "linped"));
            e.setUsu_logenv(rs.getString(usu + "logenv"));
            e.setUsu_motenv(rs.getString(usu + "motenv"));
            e.setUsu_numped(rs.getInt(usu + "numped"));
            e.setUsu_pesped(rs.getDouble(usu + "pesped"));
            e.setUsu_qtdped(rs.getDouble(usu + "qtdped"));
            e.setUsu_sitenv(rs.getString(usu + "sitenv"));
            e.setUsu_seqmes(rs.getInt(usu + "seqmes"));
            e.setUsu_datemi(rs.getDate(usu+"datemi"));
            e.setUsu_numdia(rs.getDouble(usu+"numdia"));
            e.setUsu_nomset(rs.getString(usu+"nomset"));
            
            if (e.getUsu_codcli() > 0) {
                Cliente cli = new Cliente();
                cli.setCodigo(rs.getInt("codcli"));
                cli.setNome(rs.getString("nomcli"));
                cli.setCidade(rs.getString("cidcli"));
                cli.setEstado(rs.getString("sigufs"));
                cli.setTelefone(rs.getString("foncl5"));
                if (cli.getTelefone() != null) {
                    cli.setTelefone(cli.getTelefone().trim());
                    if (cli.getTelefone().isEmpty()) {
                        cli.setTelefone("55");
                    }
                } else {
                    cli.setTelefone("55");
                }
                e.setCadCliente(cli);
            } else {
                Cliente cli = new Cliente();
                e.setCadCliente(cli);
            }
            
            resultado.add(e);
        }
        return resultado;
    }
    
    public int arredondar(double num) {
        if ((num - (int) num) > 0.0) {
            num += 1;
        }
        return (int) num;
    }
    
    @Override
    public List<PedidoReport> getPedidoReports(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<PedidoReport> resultado = new ArrayList<PedidoReport>();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "select usu_codemp,\n"
                + "       usu_codfil,\n"
                + "       usu_codcli,\n"
                + "       usu_seqmes,\n"
                + "       usu_nomset,\n"
                + "       usu_numped,\n"
                + "       usu_codlan,\n"
                + "       usu_datenv,\n"
                + "       usu_datemi,\n"
                + "       usu_numdia,\n"
                + "       usu_sitenv,\n"
                + "       usu_horenv,\n"
                + "       usu_motenv,\n"
                + "       usu_linped,\n"
                + "       usu_qtdped,\n"
                + "       usu_pesped,\n"
                + "       usu_logenv,\n"
                + "       cli.codcli,\n"
                + "       cli.nomcli,\n"
                + "       cli.cidcli,\n"
                + "       cli.sigufs,\n"
                + "       cli.intnet,\n"
                + "       cli.foncl5\n"
                + "  from usu_t120pedw pedw, e085cli cli\n"
                + " where 0 = 0\n"
                + "   and pedw.usu_codcli = cli.codcli\n"
                + " ";
        
        sqlSelect += PESQUISA;
        sqlSelect += "order by usu_codcli, usu_numped, usu_motenv asc";
        
        System.out.print(" " + sqlSelect);
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            
            rs = pst.executeQuery();
            
            resultado = getLista(rs);
            
            pst.close();
            rs.close();
            // integrar os pedidos no app

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
