/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Balanca;
import br.com.sgi.bean.BalancaLancamento;
import br.com.sgi.bean.BancoDados;
import br.com.sgi.bean.OrdensProducao;
import br.com.sgi.bean.Produto;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.conexao.ConnectionSqlServer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.sgi.interfaces.InterfaceOrdensProducaoDAO;
import br.com.sgi.util.UtilDatas;
import java.text.ParseException;

/**
 *
 * @author jairosilva
 */
public class OrdensProducaoDAO implements InterfaceOrdensProducaoDAO {

    private Connection con;
    private UtilDatas utilDatas;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private List<OrdensProducao> getLista(ResultSet rs) throws SQLException, ParseException {
        List<OrdensProducao> resultado = new ArrayList<OrdensProducao>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            OrdensProducao e = new OrdensProducao();
            e.setSublote(rs.getString("SUBLOT"));
            e.setMinifabrica(rs.getString("CODMNF"));
            e.setDeposito(rs.getString("CODDEP"));
            e.setEmpresa(rs.getInt("CODEMP"));
            e.setOrdenproducao(rs.getInt("NUMORP"));
            e.setDerivacao(rs.getString("CODDER"));
            e.setFilial(rs.getInt("CODFIL"));
            e.setOrigem(rs.getString("CODORI"));
            e.setProduto(rs.getString("CODPRO"));
            e.setData_prev_inicio(rs.getDate("DTPINI"));
            e.setData_prev_fim(rs.getDate("DTPFIM"));
            e.setQuantidade(rs.getDouble("QTDPRV"));
            e.setQuantidade_Realizada_1(rs.getDouble("QTDRE1"));
            e.setQuantidade_Realizada_2(rs.getDouble("QTDRE2"));
            e.setQuantidade_Realizada_3(rs.getDouble("QTDRE3"));
            e.setQuantidade_defeito(rs.getDouble("QTDRFG"));
            e.setQuantidade_produzida(rs.getDouble("PRODUZIDA"));
            e.setQuantidade_saldo(rs.getDouble("SALDO"));
            e.setData_producao(rs.getDate("DATGER"));
            e.setPedido(rs.getInt("NUMPED"));
            e.setProduto(rs.getString("CODPRO"));
            e.setProduto_descricao(rs.getString("DESPRO"));

            if (rs.getString("SITORP").equals("L")) {
                e.setSituacaoERP("LIBERADA");
            } else {
                if (rs.getString("SITORP").equals("A")) {
                    e.setSituacaoERP("ANDAMENTO");
                } else {
                    e.setSituacaoERP("EXPLODIDA");
                }
            }

            resultado.add(e);
        }
        return resultado;

    }

    @Override
    public OrdensProducao getOrdensProducao(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<OrdensProducao> resultado = new ArrayList<OrdensProducao>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E900COP.SUBLOT,\n"
                + "       E900COP.CODMNF,\n"
                + "       E900QDO.CODDEP,\n"
                + "       E900COP.CODEMP,\n"
                + "       E900COP.NUMORP,\n"
                + "       E900QDO.CODDER,\n"
                + "       E900COP.CODFIL,\n"
                + "       E900COP.CODORI,\n"
                + "       E900COP.CODPRO,\n"
                + "       E900COP.DTPINI,\n"
                + "       E900COP.DTPFIM,\n"
                + "       E900COP.QTDPRV,\n"
                + "       E900COP.QTDRE1,\n"
                + "       E900COP.QTDRE2,\n"
                + "       E900COP.QTDRE3,\n"
                + "       E900QDO.QTDRFG,\n"
                + "        E075PRO.CODPRO, \n"
                + "       E075PRO.DESPRO,\n"
                + "       (E900COP.QTDRE1 + E900COP.QTDRE2 + E900COP.QTDRE3) AS PRODUZIDA,\n"
                + "       E900COP.QTDPRV -\n"
                + "       (E900COP.QTDRE1 + E900COP.QTDRE2 + E900COP.QTDRE3 + E900QDO.QTDRFG) AS SALDO,\n"
                + "       E900COP.DATGER,\n"
                + "       E900COP.NUMPED,\n"
                + "       E900COP.SITORP \n"
                + "  from E900COP, E075PRO, E900QDO\n"
                + " WHERE E900COP.CODPRO = E075PRO.CODPRO\n"
                + "   AND E900COP.CODEMP = E075PRO.CODEMP\n"
                + "   AND E900COP.CODEMP = E900QDO.CODEMP\n"
                + "   AND E900COP.CODORI = E900QDO.CODORI\n"
                + "   AND E900COP.NUMORP = E900QDO.NUMORP";
        sqlSelect += PESQUISA;

        sqlSelect += "  ORDER BY E900COP.CODEMP, E900COP.CODFIL, E900COP.NUMORP ";

        //  System.out.print(sqlSelect);
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
        return (OrdensProducao) resultado;
    }

    @Override
    public List<OrdensProducao> getOrdensProducaos(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<OrdensProducao> resultado = new ArrayList<OrdensProducao>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E900COP.SUBLOT,\n"
                + "       E900COP.CODMNF,\n"
                + "       E900QDO.CODDEP,\n"
                + "       E900COP.CODEMP,\n"
                + "       E900COP.NUMORP,\n"
                + "       E900QDO.CODDER,\n"
                + "       E900COP.CODFIL,\n"
                + "       E900COP.CODORI,\n"
                + "       E900COP.CODPRO,\n"
                + "       E900COP.DTPINI,\n"
                + "       E900COP.DTPFIM,\n"
                + "       E900COP.QTDPRV,\n"
                + "       E900COP.QTDRE1,\n"
                + "       E900COP.QTDRE2,\n"
                + "       E900COP.QTDRE3,\n"
                + "       E900QDO.QTDRFG,\n"
                + "        E075PRO.CODPRO, \n"
                + "       E075PRO.DESPRO,\n"
                + "       (E900COP.QTDRE1 + E900COP.QTDRE2 + E900COP.QTDRE3) AS PRODUZIDA,\n"
                + "       E900COP.QTDPRV -\n"
                + "       (E900COP.QTDRE1 + E900COP.QTDRE2 + E900COP.QTDRE3 + E900QDO.QTDRFG) AS SALDO,\n"
                + "       E900COP.DATGER,\n"
                + "       E900COP.NUMPED,\n"
                + "       E900COP.SITORP \n"
                + "  from E900COP, E075PRO, E900QDO\n"
                + " WHERE E900COP.CODPRO = E075PRO.CODPRO\n"
                + "   AND E900COP.CODEMP = E075PRO.CODEMP\n"
                + "   AND E900COP.CODEMP = E900QDO.CODEMP\n"
                + "   AND E900COP.CODORI = E900QDO.CODORI\n"
                + "   AND E900COP.NUMORP = E900QDO.NUMORP";
        sqlSelect += PESQUISA;

        sqlSelect += " ORDER BY E900COP.CODEMP, E900COP.CODFIL, E900COP.NUMORP ";

        // System.out.println(sqlSelect);
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
            System.err.println("ERRO " + ex);
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
