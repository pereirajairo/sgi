/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Motorista;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.interfaces.InterfaceMotoristaDAO;

/**
 *
 * @author jairosilva
 */
public class MotoristaDAO implements InterfaceMotoristaDAO<Motorista> {

    private Connection con;

    private void openConnectionOracle() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private UtilDatas utilDatas;

    @Override
    public List<Motorista> getMotoristas(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Motorista> list = new ArrayList<Motorista>();
        Statement st = null;

        try {

            openConnectionOracle();

            st = con.createStatement();

            String sql = "select codtra, codmtr, upper(nommot) as nommot\n"
                    + "  from e073mot\n"
                    + " where 0=0 ";
            sql += PESQUISA;

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            while (rs.next()) {
                Motorista pro = new Motorista();
                pro.setCodtra(rs.getInt("codtra"));
                pro.setCodmtr(rs.getInt("codmtr"));
                pro.setNommot(rs.getString("nommot"));

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
    public Motorista getMotorista(String PESQUISA_POR, String PESQUISA) throws SQLException {
        Motorista pro = new Motorista();
        Statement st = null;

        try {

            openConnectionOracle();

            st = con.createStatement();

            String sql = "select codtra, codmtr, upper(nommot) as nommot\n"
                    + "  from e073mot\n"
                    + " where 0=0 ";
            sql += PESQUISA;

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            if (rs.next()) {

                pro.setCodtra(rs.getInt("codtra"));
                pro.setCodmtr(rs.getInt("codmtr"));
                pro.setNommot(rs.getString("nommot"));
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

}
