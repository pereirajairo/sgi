/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceFornecedorDAO;
import br.com.sgi.bean.Fornecedor;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class FornecedorDAO implements InterfaceFornecedorDAO<Fornecedor> {
    
    private Connection con;
    
    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    
    private List<Fornecedor> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Fornecedor> resultado = new ArrayList<Fornecedor>();
        while (rs.next()) {
            Fornecedor e = new Fornecedor();
            e.setCodfor(rs.getInt("codfor"));
            e.setNomfor(rs.getString("nomfor"));
            e.setCgccpf(rs.getString("cgccpf"));
            resultado.add(e);
        }
        return resultado;
    }
    
    @Override
    public List<Fornecedor> getFornecedors(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Fornecedor> resultado = new ArrayList<Fornecedor>();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "select codfor, nomfor from e095for where 0 = 0 ";
        sqlSelect += PESQUISA;
        sqlSelect += " order by codfor asc";
        
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
    
    @Override
    public Fornecedor getFornecedor(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Fornecedor> resultado = new ArrayList<Fornecedor>();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "select codfor, nomfor, cgccpf from e095for where 0 = 0 ";
        
        sqlSelect += PESQUISA;
        
        System.out.println("Fornecedor" + sqlSelect);
        
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
