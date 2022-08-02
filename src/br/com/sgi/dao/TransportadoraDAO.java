/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Transportadora;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.interfaces.InterfaceTransportadoraDAO;

/**
 *
 * @author jairosilva
 */
public class TransportadoraDAO implements InterfaceTransportadoraDAO<Transportadora> {

    private Connection con;

    private void openConnectionOracle() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private UtilDatas utilDatas;

    @Override
    public List<Transportadora> getTransportadoras(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Transportadora> list = new ArrayList<Transportadora>();
        Statement st = null;

        try {

            openConnectionOracle();

            st = con.createStatement();

            String sql = "select CodTra, nomtra, ciffob, trafor, cidtra, sigufs, apetra, trafor  from e073tra where sittra = 'A' ";
            sql += PESQUISA;

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            while (rs.next()) {
                Transportadora pro = new Transportadora();
                pro.setCodigoTransportadora(rs.getInt("codtra"));
                pro.setNomeTransportadora(rs.getString("nomtra"));
                pro.setCidade(rs.getString("cidtra"));
                pro.setEstado(rs.getString("sigufs"));
                pro.setApelido(rs.getString("apetra"));
                pro.setFornecedor(rs.getString("trafor"));

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
    public Transportadora getTransportadora(String PESQUISA_POR, String PESQUISA) throws SQLException {
        Transportadora pro = new Transportadora();
        Statement st = null;

        try {

            openConnectionOracle();

            st = con.createStatement();

            String sql = "select CodTra, nomtra, ciffob, trafor, cidtra, sigufs, apetra, trafor from e073tra where sittra = 'A' ";
            sql += PESQUISA;

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            if (rs.next()) {
              
                pro.setCodigoTransportadora(rs.getInt("codtra"));
                pro.setNomeTransportadora(rs.getString("nomtra"));
                pro.setCidade(rs.getString("cidtra"));
                pro.setEstado(rs.getString("sigufs"));
                pro.setApelido(rs.getString("apetra"));
                pro.setFornecedor(rs.getString("trafor"));
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
