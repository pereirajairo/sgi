/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceBalancaDAO;
import br.com.sgi.bean.Balanca;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
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
 * Teste Jairo
 */
public class BalancaDAO implements InterfaceBalancaDAO<Balanca> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }


    private List<Balanca> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Balanca> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Balanca e = new Balanca();
            e.setCodigoBalanca(rs.getInt("CodBal"));
            e.setNomeBalanca(rs.getString("DesBal"));

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<Balanca> getBalancas(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Balanca> resultado = new ArrayList<Balanca>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT CodBal, DesBal  FROM E063BAL WHERE  CODBAL > 0 and SitBal = 'A'  ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by CodBal asc";
        
        System.out.println(sqlSelect);

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


    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(USU_CODLCT) + 1), 1) PROX_CODALAN FROM USU_TBALFAB";

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
    public Balanca getBalanca(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 

}
