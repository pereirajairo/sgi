/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import br.com.sgi.bean.RamoAtividade;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceRamoAtividadeDAO;
import br.com.sgi.util.UtilDatas;

/**
 *
 * @author jairosilva
 */
public class RamoAtivadadeDAO implements InterfaceRamoAtividadeDAO<RamoAtividade> {

    private Connection con;

    private void openConnectionOracle() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private UtilDatas utilDatas;

    @Override
    public List<RamoAtividade> getRamoAtividades() throws SQLException {
        List<RamoAtividade> list = new ArrayList<RamoAtividade>();
        Statement st = null;

        try {

            openConnectionOracle();

            st = con.createStatement();

            String sql = "select codram, upper(desram) desram  from e026ram  ";

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            while (rs.next()) {
                RamoAtividade pro = new RamoAtividade();
                pro.setCodigo(rs.getString("codram"));
                pro.setDescricao(rs.getString("desram"));
                list.add(pro);
            }

            return list;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro " + e.getClass().getName() + "\n " + e.getMessage(),
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, "Erro " + e.getClass().getName() + "\n " + e.getMessage(),
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }

}
