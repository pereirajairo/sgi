/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.OrdemCompraComissao;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.sgi.util.UtilDatas;
import java.sql.PreparedStatement;

/**
 *
 * @author jairosilva
 */
public class OrdemCompraComissaoDAO {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    public List<OrdemCompraComissao> getOrdemComprasComissao(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<OrdemCompraComissao> resultado = new ArrayList<OrdemCompraComissao>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        this.utilDatas = new UtilDatas();
        String sqlSelect = "SELECT \n"
                + "    	USU_CODEMP,\n"
                + "	USU_CODFIL,\n"
                + "	USU_CODREP,\n"
                + "	USU_DATBAS,\n"
                + "	USU_NUMOCP,\n"
                + "	USU_CODFOR,\n"
                + "	USU_VLRLIQ,\n"
                + "	USU_VLRIRF,\n"
                + "	USU_EMPDES,\n"
                + "	USU_FILDES,\n"
                + "	USU_INTNET\n"
                + "     FROM SAPIENS.USU_T420OCP \n"
                + "     WHERE 0=0";

        sqlSelect += PESQUISA;

        sqlSelect += "Order by USU_CODREP, USU_NUMOCP";

        System.out.println("br.com.sgi.dao.OrdemCompraComissaoDAO.getOrdemCompras() " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            while (rs.next()) {
                OrdemCompraComissao oc = new OrdemCompraComissao();
                oc.setEmpresa(rs.getInt("usu_codemp"));
                oc.setFilial(rs.getInt("usu_codfil"));
                oc.setRepresentante(rs.getInt("usu_codrep"));
                oc.setDataBase(rs.getString("usu_datbas"));
                oc.setNumeroOrdemCompra(rs.getInt("usu_numocp"));
                oc.setCodigoFornecedor(rs.getInt("usu_codfor"));
                oc.setValorLiquido(rs.getDouble("usu_vlrliq"));
                oc.setValorIrf(rs.getDouble("usu_vlrirf"));
                oc.setEmpresaDestino(rs.getInt("usu_empdes"));
                oc.setFilialDestino(rs.getInt("usu_fildes"));
                oc.setEmailRep(rs.getString("usu_intnet"));
                resultado.add(oc);
            }
            //   System.out.println("br.com.sgi.dao.OrdemCompraDAO.getOrdemCompras() "+sqlSelect);
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

    public boolean alterarHistorico(OrdemCompraComissao t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE USU_T420ocp SET \n"
                    + " USU_codfor=?"
                    + " where USU_codemp=? and USU_codfil=? and USU_numocp=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, t.getCodigoFornecedor());
            pst.setInt(2, 1);
            pst.setInt(3, 1);
            pst.setInt(4, t.getNumeroOrdemCompra());
            pst.executeUpdate();
            pst.close();
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

        public boolean alterar(OrdemCompraComissao t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE e420ocp SET \n"
                    + " codfor=?"
                    + " where codemp=? and codfil=? and numocp=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, t.getCodigoFornecedor());
            pst.setInt(2, 1);
            pst.setInt(3, 1);
            pst.setInt(4, t.getNumeroOrdemCompra());
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "OC Alterada com sucesso",
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
        
    public boolean inserir(OrdemCompraComissao t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "INSERT INTO SAPIENS.USU_T420OCP \n"
                + "    (USU_CODEMP,\n"
                + "	USU_CODFIL,\n"
                + "	USU_CODREP,\n"
                + "	USU_DATBAS,\n"
                + "	USU_NUMOCP,\n"
                + "	USU_CODFOR,\n"
                + "	USU_VLRLIQ,\n"
                + "	USU_VLRIRF,\n"
                + "	USU_EMPDES,\n"
                + "	USU_FILDES,\n"
                + "	USU_INTNET) \n"
                + "VALUES \n"
                + "(?,?,?,?,?,\n"
                + " ?,?,?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Ordem de Compra gravada com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    private void setPreparedStatement(OrdemCompraComissao t,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        //USU_CODEMP, USU_CODFIL, USU_CODREP, USU_DATBAS, USU_NUMOCP, USU_CODFOR,
        pst.setInt(1, t.getEmpresa());
        pst.setInt(2, t.getFilial());
        pst.setInt(3, t.getRepresentante());
        pst.setString(4, t.getDataBase());
        pst.setInt(5, t.getNumeroOrdemCompra());
        pst.setInt(6, t.getCodigoFornecedor());

        //USU_VLRLIQ, USU_VLRIRF, USU_EMPDES, USU_FILDES, USU_INTNET
        pst.setDouble(7, t.getValorLiquido());
        pst.setDouble(8, t.getValorIrf());
        pst.setInt(9, t.getEmpresaDestino());
        pst.setInt(10, t.getFilialDestino());
        pst.setString(11, t.getEmailRep());

    }

    public boolean removerItens(OrdemCompraComissao t) throws SQLException {
        boolean retorno = false;
        java.sql.PreparedStatement pst = null;
        String sqlExcluirItensServico = "DELETE FROM E420ISO Where CodEmp = ? and CodFil = ? and NumOcp = ? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlExcluirItensServico);
            pst.setInt(1, t.getEmpresa());
            pst.setInt(2, t.getFilial());
            pst.setInt(3, t.getNumeroOrdemCompra());
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

    public boolean remover(OrdemCompraComissao t) throws SQLException {
        boolean retorno = false;
        java.sql.PreparedStatement pst = null;
        String sqlExcluir = "DELETE FROM E420OCP Where CodEmp = ? and CodFil = ? and NumOcp = ? and CodFor = ?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlExcluir);
            pst.setInt(1, t.getEmpresa());
            pst.setInt(2, t.getFilial());
            pst.setInt(3, t.getNumeroOrdemCompra());
            pst.setInt(4, t.getCodigoFornecedor());
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "OC Excluida com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
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

    public boolean removerHistorico(OrdemCompraComissao t) throws SQLException {
        boolean retorno = false;
        java.sql.PreparedStatement pst = null;
        String sqlExcluir = "DELETE FROM USU_T420OCP Where USU_CodEmp = ? and USU_CodFil = ? and USU_NumOcp = ? and USU_CodFor = ? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlExcluir);
            pst.setInt(1, t.getEmpresa());
            pst.setInt(2, t.getFilial());
            pst.setInt(3, t.getNumeroOrdemCompra());
            pst.setInt(4, t.getCodigoFornecedor());
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
}
