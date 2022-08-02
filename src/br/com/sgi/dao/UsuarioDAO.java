/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceUsuarioDAO;
import br.com.sgi.bean.Usuario;
import br.com.sgi.conexao.ConexaoMySql;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class UsuarioDAO implements InterfaceUsuarioDAO<Usuario> {

    private Connection con;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    @Override
    public boolean remover(Usuario t) throws SQLException {
        PreparedStatement pst = null;

        try {
            openConnectionMySql();
            pst = con.prepareStatement("UPDATE usuario  SET situacao='Inativo' "
                    + "WHERE  id = ? ");

            pst.setInt(1, t.getId());
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
    public boolean alterar(Usuario t) throws SQLException {
        PreparedStatement pst = null;

        try {
            openConnectionMySql();

            pst = con.prepareStatement("UPDATE usuario \n"
                    + "	set \n"
                    + "	nome=?, \n"
                    + "	senha=?"
                    + " where id=?");

            pst.setString(1, t.getNome());
            pst.setString(2, t.getSenha());

            pst.setInt(3, t.getId());
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
    public boolean inserir(Usuario t) throws SQLException {
        PreparedStatement pst = null;

        try {
            openConnectionMySql();

            pst = con.prepareStatement("INSERT INTO usuario \n"
                    + "	(nome, \n"
                    + "	senha)\n"
                    + "	VALUES\n"
                    + "	(?,?)");

            pst.setString(1, t.getNome());
            pst.setString(2, t.getSenha());
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
    public List<Usuario> getUsuarios() throws SQLException {
        List<Usuario> lst = new ArrayList<>();
        Statement st = null;

        try {
            openConnectionMySql();

            st = con.createStatement();

            String sql = "SELECT * FROM usuario";

            System.out.println(sql);
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Usuario usr = new Usuario();
                usr.setNome(rs.getString("nome"));
                //usr.setSenha(rs.getString("senha"));
                usr.setId(rs.getInt("id"));

                lst.add(usr);
            }
            return lst;
        } catch (SQLException e) {

            return null;
        } catch (Exception ex) {

            return null;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }

    @Override
    public Usuario getUsuario(String login) throws SQLException {
        Usuario usr = new Usuario();

        Statement st = null;

        String sql = "SELECT * FROM usuario  \n"
                + " WHERE usuario.nome = '" + login + "' \n"
                + " AND situacao = 'ATIVO' \n"
                + " LIMIT 1";
        try {
            openConnectionMySql();
            st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                usr.setNome(rs.getString("nome"));
                usr.setSenha(rs.getString("senha"));
                usr.setId(rs.getInt("id"));
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

}
