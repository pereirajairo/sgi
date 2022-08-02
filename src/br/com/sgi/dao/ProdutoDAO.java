/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Embalagem;
import br.com.sgi.bean.Produto;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.interfaces.InterfaceProdutoDAO;

/**
 *
 * @author jairosilva
 */
public class ProdutoDAO implements InterfaceProdutoDAO<Produto> {

    private Connection con;

    private void openConnectionOracle() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private UtilDatas utilDatas;

    @Override
    public List<Produto> getProdutos(String PESQUISA) throws SQLException {
        List<Produto> list = new ArrayList<Produto>();
        Statement st = null;

        try {

            openConnectionOracle();

            st = con.createStatement();

            String sql = "select codemp, codpro, despro\n"
                    + "  from e075pro\n"
                    + " where codemp = 1\n";
            sql += PESQUISA;

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            while (rs.next()) {
                Produto pro = new Produto();
                pro.setEmpresa(rs.getInt("codemp"));
                pro.setCodigoproduto(rs.getString("codpro"));
                pro.setDescricaoproduto(rs.getString("despro"));

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

    @Override
    public Produto getProduto(Integer empresa, String produto) throws SQLException {
        Produto usr = new Produto();

        Statement st = null;

        String sql = "SELECT codemp, codpro, despro, usu_preins FROM e075pro  \n"
                + " WHERE codemp = " + empresa + " \n"
                + " AND codpro = upper('" + produto + "')";
        try {
            openConnectionOracle();
            st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                usr.setEmpresa(rs.getInt("codemp"));
                usr.setCodigoproduto(rs.getString("codpro"));
                usr.setDescricaoproduto(rs.getString("despro"));
                usr.setPrecoinsumo(rs.getDouble("usu_preins"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "PROBLEMAS", JOptionPane.ERROR_MESSAGE, null);
            return null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "PROBLEMAS", JOptionPane.ERROR_MESSAGE, null);
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
    public List<Embalagem> getEmbalagems() throws SQLException {
        List<Embalagem> list = new ArrayList<Embalagem>();
        Statement st = null;

        try {

            openConnectionOracle();

            st = con.createStatement();

            String sql = "select codemb, UPPER(desemb) desemb, pesemb  from e059emb where codemb in (1,11,13,15)  order by  desemb";

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            while (rs.next()) {
                Embalagem emb = new Embalagem();
                emb.setEmpresa(0);
                emb.setEmbalagem(rs.getInt("codemb"));
                emb.setDescricaoEmbalagem(rs.getString("desemb"));
                emb.setPesoEmbalagem(rs.getDouble("pesemb"));

                list.add(emb);
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

    public List<Embalagem> getEmbalagemsGeral() throws SQLException {
        List<Embalagem> list = new ArrayList<Embalagem>();
        Statement st = null;

        try {

            openConnectionOracle();

            st = con.createStatement();

            String sql = "select codemb, UPPER(desemb) desemb, pesemb  from e059emb where codemb > 0   order by  desemb";

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            while (rs.next()) {
                Embalagem emb = new Embalagem();
                emb.setEmpresa(0);
                emb.setEmbalagem(rs.getInt("codemb"));
                emb.setDescricaoEmbalagem(rs.getString("desemb"));
                emb.setPesoEmbalagem(rs.getDouble("pesemb"));

                list.add(emb);
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

    @Override
    public Produto getProdutoLigacao(String PESQUISA, String PESQUISA_POR) throws SQLException {
        Produto pro = new Produto();

        Statement st = null;

        String sql = "  select codemp, codpro, usu_perren, codder\n"
                + "   from e075ppc\n"
                + "  where 0 = 0\n";

        sql += PESQUISA_POR;

        System.out.println("SQL " + sql);

        try {
            openConnectionOracle();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                pro.setEmpresa(rs.getInt("codemp"));
                pro.setCodigoproduto(rs.getString("codpro"));
                pro.setDescricaoproduto("");
                pro.setRentabilidade(rs.getDouble("usu_perren"));

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "PROBLEMAS", JOptionPane.ERROR_MESSAGE, null);
            return null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "PROBLEMAS", JOptionPane.ERROR_MESSAGE, null);
            return null;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
        return pro;
    }

}
