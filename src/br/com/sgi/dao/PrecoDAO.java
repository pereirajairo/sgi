/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfacePrecoDAO;
import br.com.sgi.bean.Preco;
import br.com.sgi.bean.Produto;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.util.UtilDatas;
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
public class PrecoDAO implements InterfacePrecoDAO<Preco> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public Preco getPreco(Produto produto) throws SQLException {
        Preco pre = new Preco();

        Statement st = null;

        String sql = " select codemp, codtpr, codpro, prebas\n"
                + "   from e081itp\n"
                + "  where codemp = 1\n"
                + "    and codtpr = 'MET'\n"
                + "    and codpro in ('" + produto.getCodigoproduto() + "')\n"
                + "  order by datini desc";
        try {
            ConnectionOracleSap();
            st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                pre.setEmpresa(rs.getInt("codemp"));
                pre.setPreco(rs.getDouble("prebas"));
                pre.setProduto(rs.getString("codpro"));
                pre.setTabela(rs.getString("codtpr"));
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
        return pre;
    }

}
