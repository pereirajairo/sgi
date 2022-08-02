/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceMinutaDAO;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceProcessoERPDAO;
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
public class ProcessoERPDAO implements InterfaceProcessoERPDAO<Pedido> {
    
    private Connection con;
    private UtilDatas utilDatas;
    
    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }
    
    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    
   
    
    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_codlan) + 1), 1) PROX_CODALAN FROM usu_tintmin";
        
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
    public Pedido getPedido(String PESQUISA, String PESQUISA_POR) throws SQLException {
       Pedido pedido = new Pedido();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "Select min.*, "
                + "  tra.codtra, tra.nomtra, tra.cidtra, tra.sigufs, tra.apetra\n"
                + "  from usu_tintmin min\n"
                + "  left join e073tra tra\n"
                + "    on (tra.codtra = min.usu_codtra)\n"
                + " where min.usu_codemp > 0 \n ";
        sqlSelect += PESQUISA;
        
        sqlSelect += "order by min.usu_codlan desc";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            
            rs = pst.executeQuery();
            
           if(rs.next()){
               
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
        return pedido;
    }
    
}
