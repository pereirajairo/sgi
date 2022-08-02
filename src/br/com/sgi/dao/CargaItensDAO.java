
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.CargaItens;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceCargaItensDAO;
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
public class CargaItensDAO implements InterfaceCargaItensDAO<CargaItens> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(CargaItens t,
            java.sql.PreparedStatement pst, String acao) throws SQLException {
        pst.setInt(1, t.getEmpresa());
        pst.setInt(2, t.getFilial());
        pst.setInt(3, t.getNumerocarga());
        pst.setInt(4, t.getFornecedor());
        pst.setInt(5, t.getSequenciacarga());

        pst.setString(6, t.getProduto());
        pst.setString(7, t.getDescricao());
        pst.setDouble(8, t.getQuantidadePrevista());
        pst.setString(9, t.getObservacao());
        pst.setInt(10, t.getUsuariocadastro());

        pst.setInt(11, t.getHorascadastro());
        pst.setDate(12, new Date(t.getDatacadastro().getTime()));
        pst.setInt(13, t.getSequenciaPeso());
        pst.setDouble(14, t.getPesoItem());
        pst.setDouble(15, t.getPesoLiquido());

        pst.setDate(16, new Date(t.getDataIntegracao().getTime()));
        pst.setInt(17, t.getHorasItegracao());
        pst.setInt(18, t.getUsuarioIntegracao());
        pst.setInt(19, t.getUsuarioAlteracao());
        pst.setInt(20, t.getHorascadastro());

        pst.setDate(21, new Date(t.getDataAlteracao().getTime()));
        pst.setString(22, t.getSerieNota());
        pst.setInt(23, t.getNota());
        pst.setInt(24, t.getSequenciaItem());
        pst.setDouble(25, 0.0);

        pst.setString(26, t.getUnidadeMedida());
        pst.setString(27, t.getCodigoEmbalagem());
        pst.setDouble(28, t.getPesoBruto());
        pst.setDouble(29, t.getPesoEmbalagem());

    }

    private List<CargaItens> getLista(ResultSet rs) throws SQLException, ParseException {
        List<CargaItens> resultado = new ArrayList<CargaItens>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            CargaItens ite = new CargaItens();
            ite.setCodigoEmbalagem(rs.getString("usu_codemb"));
            ite.setDataAlteracao(rs.getDate("usu_datalt"));
            ite.setDataIntegracao(rs.getDate("usu_datite"));
            ite.setDatacadastro(rs.getDate("usu_datcad"));
            ite.setDescricao(rs.getString("usu_despro"));
            ite.setEmpresa(rs.getInt("usu_codemp"));
            ite.setFornecedor(rs.getInt("usu_codfor"));
            ite.setNomeFornecedor(rs.getString("nomfor"));
            ite.setHorasItegracao(rs.getInt("usu_horite"));
            ite.setHorascadastro(rs.getInt("usu_horcad"));
            ite.setNota(rs.getInt("usu_numnfv"));
            ite.setNumerocarga(rs.getInt("usu_nrocar"));
            ite.setObservacao(rs.getString("usu_obsite"));
            ite.setPesoBruto(rs.getDouble("usu_pesbru"));
            ite.setPesoLiquido(rs.getDouble("usu_pesliq"));

            ite.setPesoItem(rs.getDouble("usu_pesite")); // quantidade de embalagem

            ite.setPesoEmbalagem(rs.getDouble("usu_pesemb"));
            ite.setPesoUnitario(rs.getDouble("usu_pesuni"));
            ite.setProduto(rs.getString("usu_codpro"));
            ite.setQuantidadePrevista(rs.getDouble("usu_qtdprv"));
            ite.setSequenciaItem(rs.getInt("usu_seqipv"));
            ite.setSequenciaPeso(rs.getInt("usu_seqpes"));
            ite.setSequenciacarga(rs.getInt("usu_seqcar"));
            ite.setSerieNota(rs.getString("usu_codsnf"));
            ite.setUnidadeMedida(rs.getString("usu_unimed"));
            ite.setUsuarioAlteracao(rs.getInt("usu_usualt"));
            ite.setUsuarioIntegracao(rs.getInt("usu_usuite"));
            ite.setUsuariocadastro(rs.getInt("usu_usucad"));

            ite.setPesoImpureza(rs.getDouble("usu_desimp"));

            ite.setSituacaoPesagem("Gravado");
            resultado.add(ite);

        }
        return resultado;
    }

    @Override
    public boolean remover(CargaItens t) throws SQLException {
        boolean retorno = false;
        java.sql.PreparedStatement pst = null;
        String sqlExcluir = "DELETE FROM usuario WHERE id = ? ";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlExcluir);
            pst.setInt(1, t.getNumerocarga());
            pst.executeUpdate();
            pst.close();
            retorno = true;
        } catch (Exception ex) {

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
    public boolean alterar(CargaItens t) throws SQLException {
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

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }

    }

    @Override
    public boolean inserir(CargaItens t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_tintitens(\n"
                + "usu_codemp,usu_codfil,usu_nrocar,usu_codfor,usu_seqcar, \n"
                + "usu_codpro,usu_despro,usu_qtdprv,usu_obsite,usu_usucad, \n"
                + "usu_horcad,usu_datcad,usu_seqpes,usu_pesite,usu_pesliq, \n"
                + "usu_datite,usu_horite,usu_usuite,usu_usualt,usu_horalt, \n"
                + "usu_datalt,usu_codsnf,usu_numnfv,usu_seqipv,usu_pesuni, \n"
                + "usu_unimed,usu_codemb,usu_pesbru,usu_pesemb) \n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,"
                + " ?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
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
    public List<CargaItens> getCargaItens(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<CargaItens> resultado = new ArrayList<CargaItens>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select ite.*, fornecedor.nomfor\n"
                + "   from usu_tintitens ite\n"
                + "   left join e095for fornecedor\n"
                + "     on (fornecedor.codfor = ite.usu_codfor)\n"
                + "  where usu_codemp > 0";
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
    public CargaItens getCargaItem(String PESQUISA_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select ite.*, fornecedor.nomfor\n"
                + "   from usu_tintitens ite\n"
                + "   left join e095for fornecedor\n"
                + "     on (fornecedor.codfor = ite.usu_codfor)\n"
                + "  where usu_codemp > 0";
        sqlSelect += PESQUISA;

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<CargaItens> resultado = new ArrayList<CargaItens>();
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
        String strSql = "SELECT NVL((MAX(usu_seqcar) + 1), 1) PROX_CODALAN FROM usu_tintitens";

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

    @Override
    public boolean gravarEmbalagem(CargaItens t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "UPDATE  usu_tintitens SET  \n"
                + "	usu_codemb = ? , usu_pesemb=?, usu_pesite=?, usu_pesuni=?, usu_obsite=? \n"
                + "       where usu_seqcar =  ?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, t.getCodigoEmbalagem());
            pst.setDouble(2, t.getPesoEmbalagem());
            pst.setDouble(3, t.getPesoItem());
            pst.setDouble(4, t.getPesoUnitario());
            pst.setString(5, t.getObservacao());
            pst.setInt(6, t.getSequenciacarga());

            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "Embalagem registrada ",
                    "Atenção:", JOptionPane.OK_OPTION);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

  
}
