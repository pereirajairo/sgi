/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Usuario;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceUsuarioErpDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class UsuarioERPDAO implements InterfaceUsuarioErpDAO<Usuario> {

    private Connection con;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public Usuario getUsuario(String login) throws SQLException {
        Usuario usr = new Usuario();

        Statement st = null;

        String sql = "select codusu, nomusu, usu_ultnum  from r999usu where nomusu = '" + login + "'";
        try {
            ConnectionOracleSap();
            st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                usr.setNome(rs.getString("nomusu"));
                usr.setId(rs.getInt("codusu"));
                usr.setUltnum(rs.getInt("usu_ultnum"));
                if (usr.getUltnum() == null) {
                    usr.setUltnum(0);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro " + e,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception ex) {

            return null;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
        return usr;
    }

    @Override
    public Usuario getAbrangencia(String login) throws SQLException {
        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        String sqlSelect = " SELECT NUMEMP, CODFIL FROM E099UAB WHERE  CODUSU = '" + login + "'";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<Usuario> resultado = new ArrayList<Usuario>();
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

    private List<Usuario> getLista(ResultSet rs) throws SQLException {
        List<Usuario> resultado = new ArrayList<>();
        while (rs.next()) {
            Usuario usr = new Usuario();
            usr.setCodEmp(rs.getString("NUMEMP").trim());
            usr.setCodfil(rs.getString("CODFIL").trim());

            resultado.add(usr);
        }
        return resultado;
    }

}
