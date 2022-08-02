/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceRepresentanteDAO;
import br.com.sgi.bean.Representante;

import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.util.UtilDatas;
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
public class RepresentanteDAO implements InterfaceRepresentanteDAO<Representante> {

    private Connection con;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    private UtilDatas utilDatas;

    private List<Representante> getLista(ResultSet rs) throws SQLException, ParseException {
        this.utilDatas = new UtilDatas();
        List<Representante> resultado = new ArrayList<Representante>();
        while (rs.next()) {
            Representante c = new Representante();
            c.setCodigo(rs.getInt("codrep"));
            c.setNome(rs.getString("nomrep"));
            c.setEmail(rs.getString("intnet"));

            resultado.add(c);

        }
        return resultado;
    }

    @Override
    public List<Representante> getRepresentantes(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Representante> resultado = new ArrayList<Representante>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "    select codrep,\n"
                + "            upper(nomrep) nomrep,\n"
                + "            upper(aperep) aperep,\n"
                + "            lower(intnet) intnet,\n"
                + "            tiprep,\n"
                + "            upper(cidrep) cidrep,\n"
                + "            upper(sigufs) sigufs\n"
                + "       from e090rep rep\n"
                + "      where codrep > 0\n"
                + "        and rep.sitrep = 'A'\n"
                + "        and codrep in (select Max(codrep)\n"
                + "                         from e090hrp hrp\n"
                + "                        where hrp.codrep = rep.codrep\n"
                + "                          and hrp.catrep = '" + PESQUISA_POR + "')\n";
        sqlSelect += PESQUISA;
        sqlSelect += "Order by rep.nomrep";

        System.out.println(" sql" + sqlSelect);

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
    public Representante getRepresentante(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Representante> resultado = new ArrayList<Representante>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "    select codrep,\n"
                + "            upper(nomrep) nomrep,\n"
                + "            upper(aperep) aperep,\n"
                + "            lower(intnet) intnet,\n"
                + "            tiprep,\n"
                + "            upper(cidrep) cidrep,\n"
                + "            upper(sigufs) sigufs\n"
                + "       from e090rep rep\n"
                + "      where codrep > 0\n"
                + "        and rep.sitrep = 'A'\n"
                + "        and codrep in (select Max(codrep)\n"
                + "                         from e090hrp hrp\n"
                + "                        where hrp.codrep = rep.codrep\n"
                + "                          and hrp.catrep = '" + PESQUISA_POR + "')";

        sqlSelect += PESQUISA;
        System.out.println("br.com.recebimento.dao.RepresentanteDAO.getRepresentante()");
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

    @Override
    public List<Representante> getRepresentantesHub(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Representante> resultado = new ArrayList<Representante>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select usu_codhub, usu_nomhub, usu_dephub, codrep, nomrep, intnet from e090rep where  0 = 0\n";
        sqlSelect += PESQUISA;
        

        System.out.println(" sql" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            while (rs.next()) {
            Representante c = new Representante();
            c.setCodigo(rs.getInt("codrep"));
            c.setNome(rs.getString("nomrep"));
            c.setEmail(rs.getString("intnet"));
            c.setCodigoHub(rs.getString("usu_codhub"));
            c.setNomeHub(rs.getString("usu_nomhub"));
            c.setDepositoHub(rs.getString("usu_dephub"));

            resultado.add(c);

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
        return resultado;
    }

}
