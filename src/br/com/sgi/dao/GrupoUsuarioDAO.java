/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.GrupoUsuario;
import br.com.sgi.interfaces.InterfaceGrupoUsuarioDAO;
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
public class GrupoUsuarioDAO implements InterfaceGrupoUsuarioDAO<GrupoUsuario> {
  private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    private List<GrupoUsuario> getLista(ResultSet rs) throws SQLException {
        List<GrupoUsuario> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            GrupoUsuario e = new GrupoUsuario();
            e.setCodigoGrupo(rs.getInt("PERID"));
            e.setNomeGrupo(rs.getString("PERNAM"));

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean remover(GrupoUsuario t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean alterar(GrupoUsuario t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean inserir(GrupoUsuario t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GrupoUsuario> getGrupoUsuarios(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<GrupoUsuario>resultado = new ArrayList<GrupoUsuario>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT PERID, PERNAM FROM R900PPL WHERE LENGTH(PERID)>=8 AND PERDEL IS NULL AND PERNAM LIKE '\\_%' escape '\\' ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by PERID asc";

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
    public GrupoUsuario getGrupoUsuario(String PESQUISA_POR, String PESQUISA) throws SQLException {
          java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT PERID, PERNAM FROM R900PPL WHERE LENGTH(PERID)>=8 AND PERDEL IS NULL AND PERNAM LIKE '\\_%' escape '\\'  ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by PERID asc";

        System.out.println(sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<GrupoUsuario> resultado = new ArrayList<GrupoUsuario>();
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
