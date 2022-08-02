/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.interfaces.InterfaceCondicaoPagamentoDAO;
import br.com.sgi.bean.CondicaoPagamento;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class CondicaoPagamentoDAO implements InterfaceCondicaoPagamentoDAO<CondicaoPagamento> {

    private Connection con;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private List<CondicaoPagamento> getLista(ResultSet rs) throws SQLException, ParseException {
        List<CondicaoPagamento> resultado = new ArrayList<CondicaoPagamento>();
        while (rs.next()) {
            CondicaoPagamento e = new CondicaoPagamento();
            e.setEmpresa(rs.getInt("codemp"));
            e.setCodigo(rs.getString("codcpg"));
            e.setDescricao(rs.getString("descpg"));
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<CondicaoPagamento> getCondicaoPagamentos(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<CondicaoPagamento> resultado = new ArrayList<CondicaoPagamento>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select codemp, codcpg, upper(descpg) descpg  from e028cpg \n"
                + "          where 0 = 0\n"
                + "          and aplcpg in ('V','A')\n"
                + "          and sitcpg = 'A'\n"
                + "          and intwmw = 'S'\n";

        sqlSelect += PESQUISA;
        sqlSelect += " ORDER BY descpg asc";

        System.out.println(sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs);

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

    @Override
    public CondicaoPagamento getCondicaoPagamento(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<CondicaoPagamento> resultado = new ArrayList<CondicaoPagamento>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select codfil, sigfil as nomfil, usu_prosuc, vensnp,"
                + " usu_codcpg, usu_tnscpl, usu_tnseco, usu_vlrsuc from e070fil where codemp>0";

        sqlSelect += PESQUISA;

        System.out.println("CondicaoPagamento" + sqlSelect);

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

}
