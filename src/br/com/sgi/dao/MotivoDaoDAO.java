/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;


import br.com.sgi.bean.Motivo;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceMotivoDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class MotivoDaoDAO implements InterfaceMotivoDAO<Motivo> {

    private Connection con;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public List<Motivo> getMotivos() throws SQLException {
        List<Motivo> resultado = new ArrayList<>();
        Statement st = null;

        try {
            ConnectionOracleSap();

            st = con.createStatement();

            String sql = " select codmot, upper(desmot) desmot, usu_aplset from e021mot where 1 = 1 \n"
                    + "and codmot between 600 and 700 \n ";
            sql += "order by codmot asc ";
            System.out.println(sql);
            ResultSet rs = st.executeQuery(sql);

            resultado = getLista(rs);

            return resultado;
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
    }

    @Override
    public Motivo getMotivo(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Motivo> resultado = new ArrayList<Motivo>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select codmot, upper(desmot) desmot, usu_aplset from e021mot where 1 = 1";
        sqlSelect += PESQUISA;

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

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

    private List<Motivo> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Motivo> resultado = new ArrayList<Motivo>();
        while (rs.next()) {
            Motivo c = new Motivo();

            c.setCodigo(rs.getInt("codmot"));
            c.setDescricao(rs.getString("desmot"));
            c.setSetor(rs.getString("usu_aplset"));
            if (c.getSetor() == null) {
                c.setSetor("0");
            }

            switch (c.getSetor()) {
                case "1":
                    c.setSetor("Vendas");
                    break;
                case "2":
                    c.setSetor("Financeiro");
                    break;
                case "3":
                    c.setSetor("Produção");
                    break;
                default:
                    c.setSetor("Não informado");
                    break;
            }

            resultado.add(c);
        }
        return resultado;
    }

}
