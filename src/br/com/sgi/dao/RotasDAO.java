/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceRotasDAO;
import br.com.sgi.bean.Rotas;
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
 * 
 */
public class RotasDAO implements InterfaceRotasDAO<Rotas> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public boolean remover(Rotas t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean alterar(Rotas t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean inserir(Rotas t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean gravarSolucao(Rotas t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private List<Rotas> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Rotas> resultado = new ArrayList<Rotas>();
        while (rs.next()) {
            Rotas e = new Rotas();
            e.setCodigoRota(rs.getString("CODROE"));
            e.setDescricaoRota(rs.getString("DESROE"));
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<Rotas> getRotas(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<Rotas> resultado = new ArrayList<Rotas>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT '0' AS CODROE, 'TODOS' AS DESROE FROM DUAL\n"
                + "UNION ALL\n"
                + "SELECT CODROE, DESROE FROM E062ROE WHERE 0 = 0 ";
        sqlSelect += PESQUISA;
        sqlSelect += " order by CODROE asc";

       // System.out.println(sqlSelect);

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
    public Rotas getRota(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<Rotas> resultado = new ArrayList<Rotas>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT '0' AS CODROE, 'TODOS' AS DESROE FROM DUAL\n"
                + "UNION ALL\n"
                + "SELECT CODROE, DESROE FROM E062ROE WHERE 0 = 0 ";
        sqlSelect += PESQUISA;
        sqlSelect += " order by CODROE asc";

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
    

}
