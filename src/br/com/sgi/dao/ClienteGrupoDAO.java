/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.ClienteGrupo;
import br.com.sgi.bean.Fornecedor;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceGrupoDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.sgi.util.UtilDatas;

/**
 *
 * @author jairosilva
 */
public class ClienteGrupoDAO implements InterfaceGrupoDAO<ClienteGrupo> {

    private Connection con;

    private void openConnectionOracle() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private UtilDatas utilDatas;

    @Override
    public ClienteGrupo getGrupo(String PESQUISA_POR, String PESQUISA) throws SQLException {
        Statement st = null;
        ClienteGrupo pro = new ClienteGrupo();

        try {
            openConnectionOracle();
            st = con.createStatement();
            String sql = "Select codgre,nomgre from E069GRE Where 0=0 \n";
            sql += PESQUISA;

            System.out.println(" sql" + sql);
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                pro.setCodgrp(rs.getString("codgre"));
                pro.setNome(rs.getString("nomgre"));
            }

            return pro;

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

    @Override
    public List<ClienteGrupo> getClienteGrupos(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<ClienteGrupo> resultado = new ArrayList<ClienteGrupo>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "Select codgre,nomgre from E069GRE Where 0=0 ";
        sqlSelect += " order by nomgre asc";

        System.out.println(sqlSelect);

        try {
            openConnectionOracle();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            while (rs.next()) {
                ClienteGrupo e = new ClienteGrupo();
                e.setCodgrp(rs.getString("codgre"));
                e.setNome(rs.getString("nomgre"));
                resultado.add(e);
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
        return resultado;
    }
}
