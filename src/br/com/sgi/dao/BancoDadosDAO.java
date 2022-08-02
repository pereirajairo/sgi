/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.BancoDados;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.interfaces.InterfaceBancoDadoDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class BancoDadosDAO implements InterfaceBancoDadoDAO<BancoDados> {

    private Connection con;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    @Override
    public boolean remover(BancoDados t) throws SQLException {
        PreparedStatement pst = null;

        try {
            openConnectionMySql();
            pst = con.prepareStatement("drop table bancodados ");

            pst.setInt(1, t.getId());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registro excluido com sucesso ",
                    "Erro:", JOptionPane.INFORMATION_MESSAGE);

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
    public boolean alterar(BancoDados t) throws SQLException {
        PreparedStatement pst = null;

        try {
            openConnectionMySql();

            pst = con.prepareStatement("UPDATE bancodados \n"
                    + "	SET	\n"
                    + "	user_banco = ?, \n"
                    + "	password_banco = ?, \n"
                    + "	driver_banco = ?, \n"
                    + "	database_banco = ?, \n"
                    + "	situacao_banco = ?, \n"
                    + "	host_banco = ?, \n"
                    + "	schema_banco = ?, \n"
                    + "	databasetype_banco = ?,	\n"
                    + " conexao_name = ?"
                    + "	WHERE\n"
                    + "	id = ?");

            pst.setString(1, t.getUser());
            pst.setString(2, t.getPassword());
            pst.setString(3, t.getDriver());
            pst.setString(4, t.getDatabase());
            pst.setString(5, t.getSituacao());
            pst.setString(6, t.getHost());
            pst.setString(7, t.getSchema());
            pst.setString(8, t.getDatabasetype());
            pst.setString(9, t.getConexao_name());

            pst.setInt(10, t.getId());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registro atualizado com sucesso ",
                    "Erro:", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro " + e,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public boolean inserir(BancoDados t) throws SQLException {
        PreparedStatement pst = null;

        try {
            openConnectionMySql();

            pst = con.prepareStatement("INSERT INTO bancodados \n"
                    + "	(id, \n"
                    + "	user_banco, \n"
                    + "	password_banco, \n"
                    + "	driver_banco, \n"
                    + "	database_banco, \n"
                    + "	situacao_banco, \n"
                    + "	host_banco, \n"
                    + "	schema_banco, \n"
                    + "	databasetype_banco,"
                    + " conexao_name\n"
                    + "	)\n"
                    + "	VALUES\n"
                    + "	(?,?,?,?,?,?,?,?,?,?)");

            pst.executeUpdate();

            pst.setInt(1, 0);
            pst.setString(2, t.getUser());
            pst.setString(3, t.getPassword());
            pst.setString(4, t.getDriver());
            pst.setString(5, t.getDatabase());
            pst.setString(6, t.getSituacao());
            pst.setString(7, t.getHost());
            pst.setString(8, t.getSchema());
            pst.setString(9, t.getDatabasetype());
            pst.setString(10, t.getConexao_name());

            JOptionPane.showMessageDialog(null, "Registro cadastrado com sucesso ",
                    "Erro:", JOptionPane.INFORMATION_MESSAGE);

            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro " + e,
                    "Erro:", JOptionPane.ERROR_MESSAGE);

            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public BancoDados getBancoDados() throws SQLException {
        BancoDados banco = new BancoDados();

        Statement st = null;

        String sql = "SELECT * FROM bancodados";
        try {
            openConnectionMySql();
            st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                banco.setId(rs.getInt("id"));
                banco.setDatabasetype(rs.getString("databasetype_banco"));
                banco.setDriver(rs.getString("driver_banco"));
                banco.setDatabase(rs.getString("database_banco"));
                banco.setHost(rs.getString("host_banco"));
                banco.setPassword(rs.getString("password_banco"));
                banco.setSchema(rs.getString("schema_banco"));
                banco.setSituacao(rs.getString("situacao_banco"));
                banco.setUser(rs.getString("user_banco"));
                banco.setConexao_name(rs.getString("conexao_name"));

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro " + e,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);

            return null;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
        return banco;
    }

}
