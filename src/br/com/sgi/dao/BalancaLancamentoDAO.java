/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Balanca;
import br.com.sgi.interfaces.InterfaceBalancaDAO;
import br.com.sgi.bean.BalancaLancamento;
import br.com.sgi.bean.Embalagem;
import br.com.sgi.bean.Produto;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceBalancaLancamentoDAO;
import br.com.sgi.util.UtilDatas;
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

/**
 *
 * @author jairosilva
 */
public class BalancaLancamentoDAO implements InterfaceBalancaLancamentoDAO<BalancaLancamento> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private List<BalancaLancamento> getLista(ResultSet rs) throws SQLException, ParseException {
        List<BalancaLancamento> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            BalancaLancamento e = new BalancaLancamento();
            e.setCodigoCaixa(rs.getInt("USU_CODCAI"));
            e.setCodigoEmpresa(rs.getInt("USU_CODEMP"));
            e.setCodigoFilial(rs.getInt("USU_CODFIL"));
            e.setCodigoFuncionario(rs.getInt("USU_CODFUN"));
            e.setPesoBalanca(rs.getDouble("USU_PESBAL"));
            e.setCodigoLancamento(rs.getInt("USU_CODLCT"));
            e.setCodigoBalanca(rs.getInt("USU_CODBAL"));
            e.setCodigoProduto(rs.getString("USU_CODPRO"));
            e.setDataLancamento(rs.getDate("USU_DATLAN"));
            e.setHoraLancamento(rs.getInt("USU_HORGER"));
            e.setNomeFuncionario(rs.getString("USU_NOMFUN"));
            e.setPesoLiquido(rs.getDouble("USU_PESLIQ"));
            e.setCodigoEmbalagem(rs.getInt("USU_CODEMB"));
            e.setMostrarPeso(rs.getString("MOSTRARPESO"));
            e.setCodigoAgrupamento(rs.getInt("USU_CODAGR"));
            e.setAgrupamentoRelacionado(rs.getInt("USU_AGRREL"));
            e.setCodigoBalancaDestino(rs.getInt("USU_BALDES"));
            Balanca balanca = new Balanca();
            balanca.setCodigoBalanca(e.getCodigoBalanca());
            balanca.setNomeBalanca(rs.getString("DESBAL"));

            e.setBalanca(balanca);

            Produto produto = new Produto();
            produto.setCodigoproduto(e.getCodigoProduto());
            produto.setDescricaoproduto(rs.getString("DESPRO"));
            e.setProduto(produto);

            Embalagem embalagem = new Embalagem();
            embalagem.setEmbalagem(e.getCodigoEmbalagem());
            embalagem.setDescricaoEmbalagem(rs.getString("DESEMB"));
            e.setEmbalagem(embalagem);

            resultado.add(e);
        }
        return resultado;
    }

    private List<BalancaLancamento> getListaAgrupado(ResultSet rs) throws SQLException, ParseException {
        List<BalancaLancamento> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            BalancaLancamento e = new BalancaLancamento();
            e.setCodigoAgrupamento(rs.getInt("USU_CODAGR"));
            e.setCodigoBalanca(rs.getInt("USU_CODBAL"));
            e.setCodigoProduto(rs.getString("USU_CODPRO"));
            e.setPesoBalanca(rs.getDouble("USU_PESBAL"));
            e.setPesoLiquido(rs.getDouble("USU_PESLIQ"));
            e.setCodigoEmbalagem(rs.getInt("USU_CODEMB"));
            e.setDataLancamento(rs.getDate("USU_DATLAN"));
            e.setHoraLancamento(rs.getInt("USU_HORGER"));
            e.setCodigoFuncionario(rs.getInt("USU_CODFUN"));
            e.setNomeFuncionario(rs.getString("USU_NOMFUN"));
            e.setMostrarPeso(rs.getString("MOSTRARPESO"));

            Balanca balanca = new Balanca();
            balanca.setCodigoBalanca(e.getCodigoBalanca());
            balanca.setNomeBalanca(rs.getString("DESBAL"));

            e.setBalanca(balanca);

           Produto produto = new Produto();
            produto.setCodigoproduto(e.getCodigoProduto());
            produto.setDescricaoproduto(rs.getString("DESPRO"));
            e.setProduto(produto);

            Embalagem embalagem = new Embalagem();
            embalagem.setEmbalagem(e.getCodigoEmbalagem());
            embalagem.setDescricaoEmbalagem(rs.getString("DESEMB"));
            e.setEmbalagem(embalagem);

            resultado.add(e);
        }
        return resultado;
    }

    private void setPreparedStatement(BalancaLancamento e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setInt(1, e.getCodigoCaixa());
        pst.setInt(2, e.getCodigoEmpresa());
        pst.setInt(3, e.getCodigoFilial());
        pst.setInt(4, e.getCodigoFuncionario());
        pst.setDouble(5, e.getPesoBalanca());
        pst.setInt(6, e.getCodigoBalanca());
        pst.setString(7, e.getCodigoProduto());
        pst.setDate(8, new Date(e.getDataLancamento().getTime()));
        pst.setInt(9, e.getHoraLancamento());
        pst.setInt(10, e.getCodigoLancamento());
        pst.setString(11, e.getNomeFuncionario());
        pst.setDouble(12, e.getPesoLiquido());
        pst.setInt(13, e.getCodigoRelacionado());
        pst.setInt(14, e.getCodigoEmbalagem());
        pst.setInt(15, e.getCodigoAgrupamento());
        pst.setInt(16, e.getAgrupamentoRelacionado());
        pst.setInt(17, e.getCodigoBalancaDestino());

    }

    @Override
    public boolean inserir(BalancaLancamento t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "INSERT INTO USU_TBALFAB\n"
                + "(USU_CODCAI,USU_CODEMP,USU_CODFIL,USU_CODFUN,USU_PESBAL,USU_CODBAL,\n"
                + "USU_CODPRO,USU_DATLAN,USU_HORGER,USU_CODLCT,USU_NOMFUN,USU_PESLIQ,USU_CODREL,USU_CODEMB,USU_CODAGR,USU_AGRREL,USU_BALDES)\n"
                + "VALUES \n"
                + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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

    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(USU_CODLCT) + 1), 1) PROX_CODALAN FROM USU_TBALFAB";

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

    public int proxCodAgr() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(USU_CODAGR) + 1), 1) PROX_CODAGR FROM USU_TBALFAB";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("PROX_CODAGR");
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

    public int totalRegistro(String PESQUISA_POR, String PESQUISA, String ORDER) throws SQLException {
        Statement st = null;
        String strSql = "SELECT count (usu_codlct) AS totalCodLct FROM USU_TBALFAB WHERE 0=0" + PESQUISA + " ";

        Integer toralcodlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                toralcodlct = rs.getInt("totalCodLct");
            }

            return toralcodlct;
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
    public List<BalancaLancamento> getBalancaLancamentos(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<BalancaLancamento> resultado = new ArrayList<BalancaLancamento>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_CODCAI, USU_CODEMP, USU_CODFIL, USU_CODFUN, USU_PESBAL, USU_CODLCT, USU_CODBAL, USU_CODPRO, USU_DATLAN, USU_HORGER ,DESBAL,USU_NOMFUN,DESPRO, \n"
                + "               USU_PESLIQ, USU_CODEMB ,DESEMB, \n"
                + "                      CASE          WHEN  (SELECT USU_AGRREL FROM USU_TBALFAB  A WHERE A.USU_AGRREL = USU_TBALFAB.USU_CODAGR AND ROWNUM =1) >0  \n"
                + "                                      AND (SELECT USU_PESLIQ FROM USU_TBALFAB  A WHERE A.USU_AGRREL = USU_TBALFAB.USU_CODAGR AND ROWNUM =1) <>  USU_PESLIQ THEN 'PESODIF'\n"
                + "                     WHEN (SELECT USU_AGRREL FROM USU_TBALFAB  A WHERE A.USU_AGRREL = USU_TBALFAB.USU_CODAGR AND ROWNUM =1) >0  THEN   'MOSTRAR'\n"
                + "                                 WHEN  USU_AGRREL > 0 THEN 'MOSTRAR'\n"
                + "                                 WHEN USU_AGRREL =0 THEN 'NAOMOSTRAR'                       \n"
                + "                                                  ELSE 'NAOMOSTRAR' END MOSTRARPESO,  USU_CODAGR , USU_AGRREL, USU_BALDES \n"
                + "              FROM USU_TBALFAB  LEFT join E063BAL  ON USU_TBALFAB.USU_CODEMP = E063bal.CODEMP AND USU_TBALFAB.USU_CODFIL = e063bal.CODFIL AND USU_TBALFAB.USU_CODBAL = e063bal.CODBAL \n"
                + "               LEFT JOIN E075PRO ON USU_TBALFAB.USU_CODEMP = E075PRO.CODEMP  AND USU_TBALFAB.USU_CODPRO = E075PRO.CODPRO \n"
                + "               LEFT JOIN E059EMB ON USU_TBALFAB.USU_CODEMB = E059EMB.CODEMB \n"
                + "              WHERE  USU_CODEMP = 1 ";
        sqlSelect += PESQUISA;

        sqlSelect += " order by USU_CODLCT DESC";

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
        return resultado;
    }

    @Override
    public BalancaLancamento getBalancaLancamento(String PESQUISA_POR, String PESQUISA, String ORDER) throws SQLException {
        List<BalancaLancamento> resultado = new ArrayList<BalancaLancamento>();
        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT USU_CODCAI, USU_CODEMP, USU_CODFIL, USU_CODFUN, USU_PESBAL, USU_CODLCT, USU_CODBAL, USU_CODPRO, USU_DATLAN, USU_HORGER ,DESBAL,USU_NOMFUN,DESPRO, \n"
                + "               USU_PESLIQ, USU_CODEMB ,DESEMB,  \n"
                + "                     CASE          WHEN  (SELECT USU_AGRREL FROM USU_TBALFAB  A WHERE A.USU_AGRREL = USU_TBALFAB.USU_CODAGR AND ROWNUM =1) >0  \n"
                + "                                      AND (SELECT USU_PESLIQ FROM USU_TBALFAB  A WHERE A.USU_AGRREL = USU_TBALFAB.USU_CODAGR AND ROWNUM =1) <>  USU_PESLIQ THEN 'PESODIF'\n"
                + "                     WHEN (SELECT USU_AGRREL FROM USU_TBALFAB  A WHERE A.USU_AGRREL = USU_TBALFAB.USU_CODAGR AND ROWNUM =1) >0  THEN   'MOSTRAR'\n"
                + "                                 WHEN  USU_AGRREL > 0 THEN 'MOSTRAR'\n"
                + "                                 WHEN USU_AGRREL =0 THEN 'NAOMOSTRAR'                       \n"
                + "                                                  ELSE 'NAOMOSTRAR' END MOSTRARPESO, USU_CODAGR , USU_AGRREL, USU_BALDES \n"
                + "              FROM USU_TBALFAB  LEFT join E063BAL  ON USU_TBALFAB.USU_CODEMP = E063bal.CODEMP AND USU_TBALFAB.USU_CODFIL = e063bal.CODFIL AND USU_TBALFAB.USU_CODBAL = e063bal.CODBAL \n"
                + "               LEFT JOIN E075PRO ON USU_TBALFAB.USU_CODEMP = E075PRO.CODEMP  AND USU_TBALFAB.USU_CODPRO = E075PRO.CODPRO \n"
                + "               LEFT JOIN E059EMB ON USU_TBALFAB.USU_CODEMB = E059EMB.CODEMB \n"
                + "              WHERE  USU_CODEMP = 1  ";
        sqlSelect += PESQUISA;

        sqlSelect += ORDER;
        //System.out.println(sqlSelect);

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
    public List<BalancaLancamento> getBalancaLancamentosAgrupados(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<BalancaLancamento> resultado = new ArrayList<BalancaLancamento>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "           SELECT USU_CODAGR,USU_CODBAL, DESBAL,USU_CODPRO, DESPRO ,SUM(USU_PESBAL) AS USU_PESBAL,SUM(USU_PESLIQ) AS USU_PESLIQ, USU_CODEMB, DESEMB,USU_DATLAN, MIN(USU_HORGER) AS USU_HORGER,\n"
                + "           USU_CODFUN ,USU_NOMFUN,\n"
                + "             CASE WHEN(   SELECT  sum(USU_PESLIQ) FROM  USU_TBALFAB A   WHERE A.USU_AGRREL = USU_TBALFAB.USU_CODAGR  AND A. USU_AGRREL > 0  )  <> sum(USU_PESLIQ) THEN 'PESODIF'     \n"
                + "       WHEN( SELECT  sum(USU_PESLIQ) FROM  USU_TBALFAB A   WHERE A.USU_AGRREL = USU_TBALFAB.USU_CODAGR  AND A. USU_AGRREL > 0 )  = sum(USU_PESLIQ) THEN 'MOSTRAR'   \n"
                + "       WHEN(   SELECT  sum(USU_PESLIQ) FROM  USU_TBALFAB A   WHERE A.USU_CODAGR = USU_TBALFAB.USU_AGRREL  AND A. USU_AGRREL =0 )  <> sum(USU_PESLIQ) THEN 'PESODIF'     \n"
                + "       WHEN( SELECT  sum(USU_PESLIQ) FROM  USU_TBALFAB A   WHERE A.USU_CODAGR = USU_TBALFAB.USU_AGRREL AND A. USU_AGRREL =0   )  = sum(USU_PESLIQ) THEN 'MOSTRAR'                                  \n"
                + "                                                 ELSE 'NAOMOSTRAR'     END MOSTRARPESO\n"
                + "           FROM  USU_TBALFAB             \n"
                + "           LEFT JOIN E063BAL   ON USU_TBALFAB.USU_CODEMP     = E063bal.CODEMP AND USU_TBALFAB.USU_CODFIL = e063bal.CODFIL  AND USU_TBALFAB.USU_CODBAL = e063bal.CODBAL\n"
                + "           LEFT JOIN E059EMB ON USU_TBALFAB.USU_CODEMB = E059EMB.CODEMB\n"
                + "           LEFT JOIN E075PRO  ON USU_TBALFAB.USU_CODEMP= E075PRO.CODEMP AND USU_TBALFAB.USU_CODPRO = E075PRO.CODPRO"
                + "           WHERE 0=0";
        sqlSelect += PESQUISA;

        sqlSelect += " GROUP BY  USU_CODAGR,USU_CODBAL ,DESBAL,USU_CODPRO,DESPRO, USU_DATLAN,USU_CODFUN,USU_CODEMB, DESEMB,USU_NOMFUN  ,USU_AGRREL";
        sqlSelect += " ORDER BY   USU_DATLAN, USU_HORGER DESC";

        //System.out.print(sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaAgrupado(rs);

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
    public List<BalancaLancamento> getBalancaLancamentosChumbo(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BalancaLancamento getBalancaLancamentoChumbo(String PESQUISA_POR, String PESQUISA, String ORDER) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
