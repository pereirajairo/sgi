/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceFuncionarioDAO;
import br.com.sgi.bean.Funcionario;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.conexao.ConnectionOracleVetorh;
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
 * @author jairosilva
 */
public class FuncionarioDAO implements InterfaceFuncionarioDAO<Funcionario> {

    private Connection con;
    private UtilDatas utilDatas;

    private String PESQUISA;
    private String PESQUISA_POR;

    private String PROCESSO;
    private String COMPLEMENTO;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleVetorh() throws Exception {
        con = ConnectionOracleVetorh.openConnection();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(Funcionario e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {
  
        pst.setString(1, e.getNomeFuncionario());
        pst.setString(2, e.getCodigoCracha());
              pst.setInt(3, e.getCodigoFuncionario());

    }

    private List<Funcionario> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Funcionario> resultado = new ArrayList<Funcionario>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Funcionario e = new Funcionario();
            e.setCodigoFuncionario(rs.getInt("NumCad"));
            e.setNomeFuncionario(rs.getString("NomFun"));

            resultado.add(e);
        }
        return resultado;
    }

    private List<Funcionario> getListaSapiens(ResultSet rs) throws SQLException, ParseException {
        List<Funcionario> resultado = new ArrayList<Funcionario>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Funcionario e = new Funcionario();
            e.setCodigoFuncionario(rs.getInt("USU_NUMCAD"));
            e.setNomeFuncionario(rs.getString("USU_NOMFUN"));
            e.setCodigoCracha(rs.getString("USU_CODCRA"));
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<Funcionario> getFuncionarios(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Funcionario> resultado = new ArrayList<Funcionario>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT NumCad, NomFun FROM R034FUN WHERE  NUMEMP = 7  AND TipCol =1  ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by NumCad asc";

        try {
            ConnectionOracleVetorh();
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
    public Funcionario getFuncionario(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Funcionario> resultado = new ArrayList<Funcionario>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT NumCad, NomFun FROM R034FUN WHERE  NUMEMP = 7  AND TipCol =1 ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by NumCad desc";

        try {
            ConnectionOracleVetorh();
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
    public List<Funcionario> getFuncionariosSapiens(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Funcionario> resultado = new ArrayList<Funcionario>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_NUMCAD, USU_NOMFUN, USU_CODCRA FROM USU_TCRACHA WHERE 0=0  ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by USU_NUMCAD asc";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaSapiens(rs);

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
    public Funcionario getFuncionarioSapiens(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Funcionario> resultado = new ArrayList<Funcionario>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_NUMCAD, USU_NOMFUN,USU_CODCRA FROM USU_TCRACHA WHERE 0=0 ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by USU_NUMCAD desc";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaSapiens(rs);
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
    public boolean remover(Funcionario t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean alterar(Funcionario t) throws SQLException {
       PreparedStatement pst = null;

        try {

            String sqlInsert = "update USU_TCRACHA SET USU_NOMFUN =?,USU_CODCRA  =? WHERE  USU_NUMCAD =  ?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "A");
            pst.executeUpdate();
            pst.close();

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
    public boolean inserir(Funcionario t) throws SQLException {
             PreparedStatement pst = null;

        String sqlInsert = "INSERT INTO USU_TCRACHA ( USU_NOMFUN, USU_CODCRA,USU_NUMCAD) "
                + "VALUES (?, ?, ?)";

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

}
