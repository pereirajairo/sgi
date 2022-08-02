/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Caixa;
import br.com.sgi.bean.Tela;
import br.com.sgi.interfaces.InterfaceTelaDAO;
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
public class TelaDAO implements InterfaceTelaDAO<Tela> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(Tela e, java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setString(1, e.getNomeTela());
        pst.setString(2, e.getCodigoInteno());
        pst.setInt(3, e.getCodigoTela());
      

    }

    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(USU_NUMTEL) + 1), 1) PROX_USU_NUMTEL FROM USU_T900CTS";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("PROX_USU_NUMTEL");
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

    private List<Tela> getLista(ResultSet rs) throws SQLException {
        List<Tela> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Tela e = new Tela();
            e.setCodigoTela(rs.getInt("USU_NUMTEL"));
            e.setNomeTela(rs.getString("USU_NOMTEL"));
            e.setCodigoInteno(rs.getString("USU_CODTEL"));            

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean remover(Tela t) throws SQLException {
        PreparedStatement pst = null;

        String sqlUpdate = "DELETE FROM USU_T900CTS WHERE USU_NUMTEL = ?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlUpdate);
            pst.setInt(1, t.getCodigoTela());
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "Tela Removido com sucesso.",
                    "Infomativo", JOptionPane.INFORMATION_MESSAGE);
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
    public boolean alterar(Tela t) throws SQLException {
        PreparedStatement pst = null;

        String sqlUpdate = "UPDATE USU_T900CTS SET USU_NOMTEL = ? , USU_CODTEL = ? WHERE USU_NUMTEL =?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlUpdate);
            setPreparedStatement(t, pst, "A");
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "Tela Atualizado com sucesso.",
                    "Infomativo", JOptionPane.INFORMATION_MESSAGE);
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
    public boolean inserir(Tela t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "INSERT INTO USU_T900CTS (USU_NOMTEL,USU_CODTEL,USU_NUMTEL) \n"
                + " VALUES (?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "Tela cadastrado com sucesso.",
                    "Infomativo", JOptionPane.INFORMATION_MESSAGE);
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
    public List<Tela> getTelas(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Tela> resultado = new ArrayList<Tela>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_NUMTEL, USU_NOMTEL,USU_CODTEL   FROM USU_T900CTS WHERE 0=0 ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by USU_NUMTEL asc";

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
    public Tela getTela(String PESQUISA_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_NUMTEL, USU_NOMTEL,USU_CODTEL   FROM USU_T900CTS WHERE 0=0   ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by USU_CODCAI asc";

        System.out.println(sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<Tela> resultado = new ArrayList<Tela>();
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
