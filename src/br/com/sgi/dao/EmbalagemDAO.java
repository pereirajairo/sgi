/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceEmbalagemDAO;
import br.com.sgi.bean.Embalagem;
import br.com.sgi.bean.Funcionario;
import br.com.sgi.conexao.ConnectionOracleSap;
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
public class EmbalagemDAO implements InterfaceEmbalagemDAO<Embalagem> {

    private Connection con;

    private void openConnectionOracle() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private UtilDatas utilDatas;

    @Override
    public List<Embalagem> getEmbalagems() throws SQLException {
        List<Embalagem> list = new ArrayList<Embalagem>();
        Statement st = null;

        try {

            openConnectionOracle();

            st = con.createStatement();

            String sql = "SELECT  0 AS codemb , 'SELECIONE EMBALAGEM' AS desemb ,0 AS pesemb FROM DUAL\n"
                    + "UNION ALL\n"
                    + "select codemb, UPPER(desemb) desemb, pesemb  from e059emb where 0=0 order by codemb , desemb";

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
    public Embalagem getEmbalagem(String PESQUISA_POR, String PESQUISA) throws SQLException {
        Embalagem emb = new Embalagem();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select codemb, UPPER(desemb) desemb, pesemb  from e059emb where 0 = 0 ";
        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA;
        }

        sqlSelect += " order by codemb , desemb";

        try {
            openConnectionOracle();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            if (rs.next()) {
                emb.setEmpresa(0);
                emb.setEmbalagem(rs.getInt("codemb"));
                emb.setDescricaoEmbalagem(rs.getString("desemb"));
                emb.setPesoEmbalagem(rs.getDouble("pesemb"));
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
        return emb;
    }
}
