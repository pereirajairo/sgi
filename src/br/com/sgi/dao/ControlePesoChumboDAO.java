/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.ControlePesoChumbo;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceControlePesoChumboDAO;
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
 * @author jairosilva Teste Jairo
 */

public class ControlePesoChumboDAO implements InterfaceControlePesoChumboDAO<ControlePesoChumbo> {

    private Connection con;
    private UtilDatas utilDatas;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    
    private void setPreparedStatement(ControlePesoChumbo e, java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setString(1, e.getSituacaoLancamento());
        pst.setInt(2, e.getCodigoAgrupador());
        pst.setString(3, e.getCodigoProduto());
        pst.setInt(4, e.getCodigoBalancaDestino());  
    }
      
    private List<ControlePesoChumbo> getLista(ResultSet rs) throws SQLException, ParseException {
        List<ControlePesoChumbo> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            ControlePesoChumbo e = new ControlePesoChumbo();
            e.setCodigoAgrupador(rs.getInt("USU_CODAGR"));
            e.setCodigoProduto(rs.getString("USU_CODPRO"));
            e.setSituacaoLancamento(rs.getString("USU_SITLAN"));
            e.setCodigoBalancaDestino(rs.getInt("USU_BALDES"));
       
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean remover(ControlePesoChumbo t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean alterar(ControlePesoChumbo t) throws SQLException {
       PreparedStatement pst = null;

        String sqlUpdate = "UPDATE  USU_TBALCLT SET USU_SITLAN = ? WHERE  USU_CODAGR = ? AND USU_CODPRO = ? AND USU_BALDES = ? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlUpdate);
            setPreparedStatement(t, pst, "U");
            pst.executeUpdate();
            pst.close();

            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
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
    public boolean inserir(ControlePesoChumbo t) throws SQLException {
            PreparedStatement pst = null;

        String sqlInsert = "INSERT INTO USU_TBALCLT (USU_SITLAN, USU_CODAGR,USU_CODPRO, USU_BALDES) VALUES ( ?,?,?,?) ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();

            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
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
    public List<ControlePesoChumbo> getControlePesoChumbos(String PESQUISA_POR, String PESQUISA) throws SQLException {
           List<ControlePesoChumbo> resultado = new ArrayList<ControlePesoChumbo>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_SITLAN, USU_CODAGR,USU_CODPRO, USU_BALDES FROM USU_TBALCLT WHERE 0=0 ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by USU_CODAGR asc";

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
    public ControlePesoChumbo getControlePesoChumbo(String PESQUISA_POR, String PESQUISA) throws SQLException {
        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_SITLAN, USU_CODAGR,USU_CODPRO, USU_BALDES FROM USU_TBALCLT WHERE 0=0  ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by USU_CODAGR asc";
         
        System.out.println(sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<ControlePesoChumbo> resultado = new ArrayList<ControlePesoChumbo>();
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
    
    

  
}
