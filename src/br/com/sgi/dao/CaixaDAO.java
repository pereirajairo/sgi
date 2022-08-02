/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceCaixaDAO;
import br.com.sgi.bean.Caixa;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva Teste Jairo
 */
public class CaixaDAO implements InterfaceCaixaDAO<Caixa> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(Caixa e, java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setDouble(1, e.getPesoCaixa());
        pst.setString(2, "N");
        pst.setString(3, e.getSituacaoCaixa());
        pst.setString(4," ");
        pst.setInt(5, 0);    
        pst.setInt(6, e.getCodigoCaixa());
  
    }

    private List<Caixa> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Caixa> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Caixa e = new Caixa();
            e.setCodigoCaixa(rs.getInt("USU_CODCAI"));
            e.setPesoCaixa(rs.getDouble("USU_PESCAI"));
            e.setEmusuCaixa(rs.getString("USU_USUCAI"));
            e.setSituacaoCaixa(rs.getString("USU_SITCAI"));
            e.setCodigoBalanca(rs.getInt("USU_CODBAL"));
            e.setCodigoProduto(rs.getString("USU_CODPRO"));
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<Caixa> getCaixas(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Caixa> resultado = new ArrayList<Caixa>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_CODCAI, USU_PESCAI, USU_USUCAI, USU_SITCAI  ,USU_CODBAL,USU_CODPRO FROM USU_TCAIXA WHERE 0=0 ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by USU_CODCAI asc";

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
    public Caixa getCaixa(String PESQUISA_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_CODCAI, USU_PESCAI, USU_USUCAI, USU_SITCAI ,USU_CODBAL,USU_CODPRO FROM USU_TCAIXA WHERE 0=0   ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by USU_CODCAI asc";
         
        System.out.println(sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<Caixa> resultado = new ArrayList<Caixa>();
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
    public boolean remover(Caixa t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean alterar(Caixa t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlUpdate = "UPDATE USU_TCAIXA SET   USU_PESCAI =?,  USU_SITCAI  = ?  WHERE  USU_CODCAI= ?";

            ConnectionOracleSap();
            pst.setDouble(1, t.getPesoCaixa());
            pst.setString(2, t.getSituacaoCaixa());
            pst.setInt(3, t.getCodigoCaixa());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registro alterado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public boolean inserir(Caixa t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "INSERT INTO USU_TCAIXA (USU_PESCAI, USU_USUCAI, USU_SITCAI,USU_CODPRO,USU_CODBAL,USU_CODCAI) "
                + "VALUES (?,?,?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "Peso cadastrado com sucesso.",
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
    public boolean alterarEmuso(Caixa t) throws SQLException {
        PreparedStatement pst = null;

        try {
            
     
              String sqlUpdate = "UPDATE USU_TCAIXA SET USU_USUCAI =? , USU_CODPRO = ? , USU_CODBAL = ? WHERE USU_CODCAI = ?";
            
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlUpdate);
            pst.setString(1, t.getEmusuCaixa());
            pst.setString(2,t.getCodigoProduto());
            pst.setInt(3, t.getCodigoBalanca());  
            pst.setInt(4, t.getCodigoCaixa());
            pst.executeUpdate();
            return true;

        } catch (SQLException e) {

            return false;
        } catch (Exception ex) {
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

}
