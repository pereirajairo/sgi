/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.CargaItensImpureza;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceCargaItensImpurezaDAO;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import br.com.sgi.util.UtilDatas;

/**
 *
 * @author jairosilva
 */
public class CargaItensImpurezaDAO implements InterfaceCargaItensImpurezaDAO<CargaItensImpureza> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(CargaItensImpureza t,
            java.sql.PreparedStatement pst, String acao) throws SQLException {
        pst.setInt(1, t.getEmpresa());
        pst.setInt(2, t.getFilial());
        pst.setInt(3, t.getNumeroCarga());
        pst.setInt(4, t.getFornecedor());
        pst.setInt(5, t.getSequenciacarga());

        pst.setString(6, t.getProduto());
        pst.setDouble(7, t.getQuantidade());
        pst.setDouble(8, t.getPercentualDesconto());
        pst.setDouble(9, t.getPesoImpureza());
        pst.setInt(10, t.getUsusarioCadastro());

        pst.setInt(11, t.getCodigoImpureza());
        pst.setInt(12, t.getHorarioCadastro());
        pst.setDate(13, new Date(t.getDataCadastro().getTime()));
        pst.setDouble(14, t.getPesoTotalCarga());
        pst.setInt(15, t.getSequenciaCadastro());
        pst.setDouble(16, t.getQuantidadePeso());
        
        
    }

    private List<CargaItensImpureza> getLista(ResultSet rs) throws SQLException, ParseException {
        List<CargaItensImpureza> resultado = new ArrayList<CargaItensImpureza>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            CargaItensImpureza ite = new CargaItensImpureza();
            ite.setEmpresa(rs.getInt("usu_codemp"));
            ite.setFilial(rs.getInt("usu_codfil"));
            ite.setNumeroCarga(rs.getInt("usu_nrocar"));
            ite.setFornecedor(rs.getInt("usu_codfor"));
            ite.setSequenciacarga(rs.getInt("usu_seqcar"));

            ite.setProduto(rs.getString("usu_codpro"));
            ite.setQuantidade(rs.getDouble("usu_qtdpal"));
            ite.setPercentualDesconto(rs.getDouble("usu_perdes"));
            ite.setPesoImpureza(rs.getDouble("usu_pesimp"));
            ite.setDataCadastro(rs.getDate("usu_datcad"));

            ite.setCodigoImpureza(rs.getInt("usu_codimp"));
            ite.setUsusarioCadastro(rs.getInt("usu_usucad"));
            ite.setHorarioCadastro(rs.getInt("usu_horcad"));
            ite.setDataCadastro(rs.getDate("usu_datcad"));
            ite.setPesoTotalCarga(rs.getDouble("usu_perdes"));

            ite.setSequenciaCadastro(rs.getInt("usu_seqcad"));

            resultado.add(ite);

        }
        return resultado;
    }

    @Override
    public boolean remover(CargaItensImpureza t) throws SQLException {
        boolean retorno = false;
        java.sql.PreparedStatement pst = null;
        String sqlExcluir = "DELETE FROM usu_tintimp where usu_nrocar = ? and usu_codimp=? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlExcluir);
            pst.setInt(1, t.getNumeroCarga());
            pst.setInt(2, t.getCodigoImpureza());
            pst.executeUpdate();
            pst.close();
            retorno = true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            retorno = false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();

        }
        return retorno;

    }

    @Override
    public boolean alterar(CargaItensImpureza t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "UPDATE  apontar SET  \n"
                + "	apontar=?, razao_social=upper(?), situacao=upper(?), created=?, modified=? \n"
                + "       where id =  ?";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "A");
            pst.executeUpdate();
            pst.close();

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
    public boolean inserir(CargaItensImpureza t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_tintimp(\n"
                + "usu_codemp,usu_codfil,usu_nrocar,usu_codfor,usu_seqcar, \n"
                + "usu_codpro,usu_qtdpal,usu_perdes,usu_pesimp,usu_usucad, \n"
                + "usu_codimp,usu_horcad,usu_datcad,usu_pescar,usu_seqcad, \n"
                + "usu_qtdprv) \n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "Registro de impurezas realializado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

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
    public List<CargaItensImpureza> getCargaItensImpurezas(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<CargaItensImpureza> resultado = new ArrayList<CargaItensImpureza>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select imp.*\n"
                + "  from usu_tintimp imp\n"
                + " where usu_codemp > 0\n ";
        sqlSelect += PESQUISA;

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
    public CargaItensImpureza getCargaItensImpureza(String PESQUISA_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select imp.*\n"
                + "  from usu_tintimpu imp\n"
                + " where usu_codemp > 0\n ";
        sqlSelect += PESQUISA;

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<CargaItensImpureza> resultado = new ArrayList<CargaItensImpureza>();
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

    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_seqcad) + 1), 1) PROX_CODALAN FROM usu_tintimp";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("PROX_CODALAN");
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

}
