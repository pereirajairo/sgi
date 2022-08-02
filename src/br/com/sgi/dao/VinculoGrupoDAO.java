/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Permissao;
import br.com.sgi.bean.Tela;
import br.com.sgi.bean.VinculoGrupo;
import br.com.sgi.bean.VinculoGrupo;
import br.com.sgi.interfaces.InterfaceVinculoGrupoDAO;
import br.com.sgi.bean.Usuario;
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
public class VinculoGrupoDAO implements InterfaceVinculoGrupoDAO<VinculoGrupo> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(VinculoGrupo e, java.sql.PreparedStatement pst, String acao) throws SQLException {

        //  pst.setString(1, e.getNomeVinculoGrupo());
        //   pst.setInt(2, e.getCodigoGrupo());
    }

    private List<VinculoGrupo> getLista(ResultSet rs) throws SQLException {
        List<VinculoGrupo> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            VinculoGrupo e = new VinculoGrupo();
            e.setCheckbox(rs.getInt("CHECKBOX"));
            if (e.getCheckbox() == 1) {
                e.setSelecionar(true);
            } else {
                e.setSelecionar(false);
            }

            e.setCodigoTela(rs.getInt("USU_NUMTEL"));
            e.setNomeTela(rs.getString("USU_NOMTEL"));
            resultado.add(e);
        }
        return resultado;
    }

    private List<Permissao> getListaPermissao(ResultSet rs) throws SQLException {
        List<Permissao> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Permissao e = new Permissao();
            e.setCodigoTela(rs.getInt("USU_NUMTEL"));
            e.setCodigoMenu(rs.getString("USU_CODTEL"));
            resultado.add(e);
        }
        return resultado;
    }

    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(USU_SEQLIG) + 1), 1) PROX_USU_SEQLIG FROM USU_T900LGT";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("PROX_USU_SEQLIG");
            }

            return codlct;
        } catch (SQLException e) {

            return -1;
        } catch (Exception ex) {

            return -1;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }

    @Override
    public boolean remover(VinculoGrupo t) throws SQLException {
        PreparedStatement pst = null;

        String sqlDelete = "DELETE FROM USU_T900LGT WHERE USU_PERID = ?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlDelete);
            pst.setInt(1, t.getCodigoGrupo());
            pst.executeUpdate();
            pst.close();

           // JOptionPane.showMessageDialog(null, "Tela Removido com sucesso.",
          //          "Infomativo", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public boolean alterar(VinculoGrupo t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean inserir(VinculoGrupo t) throws SQLException {
       PreparedStatement pst = null;

        String sqlInsert = "INSERT INTO USU_T900LGT (USU_SEQLIG, USU_NUMTEL, USU_PERID) VALUES  (?, ?, ?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, t.getSequeciaLigacao());
            pst.setInt(2, t.getCodigoTela());
            pst.setInt(3, t.getCodigoGrupo());
            pst.executeUpdate();
            pst.close();

           // JOptionPane.showMessageDialog(null, "Tela Removido com sucesso.",
          //          "Infomativo", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        } 
    }

    @Override
    public List<VinculoGrupo> getVinculoGrupos(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<VinculoGrupo> resultado = new ArrayList<VinculoGrupo>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT  \n"
                + "CASE WHEN (SELECT 1 FROM USU_T900LGT WHERE USU_NUMTEL =  USU_T900CTS.USU_NUMTEL AND  USU_PERID = " + PESQUISA + " ) = 1  THEN  1 ELSE 0 END AS CHECKBOX ,\n"
                + "USU_NUMTEL,\n"
                + " USU_NOMTEL \n"
                + " FROM USU_T900CTS"
                + " ORDER BY USU_NUMTEL ";

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
    public VinculoGrupo getVinculoGrupo(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Permissao> getPermissaoAcesso(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Permissao> resultado = new ArrayList<Permissao>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " SELECT USU_T900CTS.USU_NUMTEL,USU_T900CTS.USU_CODTEL  FROM  USU_T900CTS\n"
                + " LEFT JOIN USU_T900LGT ON USU_T900CTS.USU_NUMTEL = USU_T900LGT.USU_NUMTEL\n"
                + " WHERE  USU_T900LGT.USU_PERID IN ( SELECT GRPID FROM R900GRP WHERE MEMID = " + PESQUISA + ") ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaPermissao(rs);

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
