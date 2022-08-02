/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import br.com.sgi.bean.NotaFiscalItens;
import br.com.sgi.bean.Produto;
import br.com.sgi.bean.SituacaoCliente;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceNotaFiscalItensDAO;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.UtilDatas;
import java.util.Date;

/**
 *
 * @author jairosilva
 */
public class NotaFiscalItensDAO implements InterfaceNotaFiscalItensDAO<NotaFiscalItens> {

    private Connection con;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }
    private UtilDatas utilDatas;

    @Override
    public List<NotaFiscalItens> getNotaFiscalItens(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException {
        this.utilDatas = new UtilDatas();

        List<NotaFiscalItens> list = new ArrayList<NotaFiscalItens>();
        Statement st = null;

        try {

            ConnectionOracleSap();

            st = con.createStatement();

            String sql = "SELECT MAX(E140IPV.CODPRO) CODPRO,\n"
                    + "       MAX(E075PRO.DESPRO) DESPRO,\n"
                    + "       MAX(E075PRO.CODORI) CODORI,\n"
                    + "       SUM(E140IPV.QTDFAT) QTDFAT,\n"
                    + "       MAX(E140NFV.DATEMI) DATEMI,\n"
                    + "       MAX(E140NFV.CODEMP) CODEMP,\n"
                    + "       MAX(E140NFV.CODFIL) CODFIL,\n"
                    + "       MAX(TO_CHAR((E140NFV.DATEMI), 'mm')) MES,\n"
                    + "       MAX(TO_CHAR((E140NFV.DATEMI), 'YYYY')) ANO\n"
                    + "  FROM E140NFV, E140IPV, E075PRO\n"
                    + " WHERE 1 = 1\n"
                    //  + "   AND E140NFV.DATEMI >= '01/01/2020'\n"
                    + "   AND E140NFV.CODEMP = E140IPV.CODEMP\n"
                    + "   AND E140NFV.CODFIL = E140IPV.CODFIL\n"
                    + "   AND E140NFV.NUMNFV = E140IPV.NUMNFV\n"
                    + "   AND E140IPV.CODEMP = E075PRO.CODEMP\n"
                    + "   AND E140IPV.CODPRO = E075PRO.CODPRO\n"
                    + "   AND E075PRO.CODORI IN ('BA','BM')";
            sql += PESQUISA;

            sql += "    GROUP BY (TO_CHAR((E140NFV.DATEMI), 'YYYY')),E140IPV.CODPRO         \n"
                    + " ORDER BY(TO_CHAR((E140NFV.DATEMI), 'YYYY')), E140IPV.CODPRO";

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            while (rs.next()) {
                NotaFiscalItens c = new NotaFiscalItens();
                c.setEmpresa(rs.getInt("codemp"));
                c.setFilial(rs.getInt("codfil"));
                c.setEmissao(rs.getDate("datemi"));
                c.setMes(rs.getInt("mes"));
                c.setAno(rs.getInt("ano"));
                c.setQuantidade(rs.getDouble("qtdfat"));
                c.setProduto(rs.getString("codpro"));
                Produto pro = new Produto();
                pro.setCodigoproduto(c.getProduto());
                pro.setDescricaoproduto(rs.getString("despro"));
                c.setCadProduto(pro);

                c.setDias_ultimo_faturamento(utilDatas.nrDias(DATA, c.getEmissao()));

                c.setDias_ultimo_faturamento(FormatarNumeros.converterDoubleDoisDecimais(c.getDias_ultimo_faturamento()));
                c.setEmissaoS(utilDatas.converterDateToStr(c.getEmissao()));

                list.add(c);
            }

            return list;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro " + e.getClass().getName() + "\n " + e.getMessage(),
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, "Erro " + e.getClass().getName() + "\n " + e.getMessage(),
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }

    @Override
    public NotaFiscalItens getNotaFiscalItem(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException {
        this.utilDatas = new UtilDatas();

        NotaFiscalItens c = new NotaFiscalItens();
        Statement st = null;

        try {

            ConnectionOracleSap();

            st = con.createStatement();

            String sql = "SELECT MAX(TO_CHAR((E140NFV.DATEMI), 'mm')) MES,\n"
                    + "       MAX(TO_CHAR((E140NFV.DATEMI), 'YYYY')) ANO,\n"
                    + "       MAX(E140NFV.DATEMI) DATEMI,\n"
                    + "       MAX(E140NFV.CODREP) CODREP,\n"
                    + "       MAX(E140NFV.CODEMP) CODEMP,\n"
                    + "       MAX(E140NFV.CODFIL) CODFIL,\n"
                    + "       MAX(E140NFV.CODCLI) CODCLI\n"
                    + "  FROM E140NFV, E001TNS, E090HRP, E085CLI, E070EMP, E070FIL\n"
                    + " WHERE ((E140NFV.CODEMP = E001TNS.CODEMP) AND\n"
                    + "       ((E140NFV.TNSPRO = E001TNS.CODTNS) OR\n"
                    + "       (E140NFV.TNSSER = E001TNS.CODTNS)))\n"
                    + "   AND ((E140NFV.CODEMP >= 1))\n"
                    + "   AND ((E140NFV.CODEMP = E070EMP.CODEMP) AND\n"
                    + "       (E140NFV.CODEMP = E070FIL.CODEMP) AND\n"
                    + "       (E140NFV.CODFIL = E070FIL.CODFIL) AND\n"
                    + "       (E140NFV.CODCLI = E085CLI.CODCLI) AND\n"
                    + "       ((E090HRP.CODREP = E140NFV.CODREP) AND\n"
                    + "       (E090HRP.CODEMP = E140NFV.CODEMP)))\n"
                    + "   AND (E001TNS.VENFAT = 'S' AND ((E070FIL.CODFIL >0 )))\n"
                    + "";
            sql += PESQUISA;

            sql += " GROUP BY E140NFV.CODCLI\n"
                    + " ORDER BY E140NFV.CODCLI\n";

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            if (rs.next()) {

                c.setEmpresa(rs.getInt("codemp"));
                c.setFilial(rs.getInt("codfil"));
                c.setEmissao(rs.getDate("datemi"));
                c.setMes(rs.getInt("mes"));
                c.setAno(rs.getInt("ano"));

                c.setDias_ultimo_faturamento(FormatarNumeros.converterDoubleDoisDecimais(c.getDias_ultimo_faturamento()));
                c.setEmissaoS(utilDatas.converterDateToStr(c.getEmissao()));

            }

            return c;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro " + e.getClass().getName() + "\n " + e.getMessage(),
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, "Erro " + e.getClass().getName() + "\n " + e.getMessage(),
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }

    @Override
    public List<SituacaoCliente> getSituacoes(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException {

        this.utilDatas = new UtilDatas();

        List<SituacaoCliente> list = new ArrayList<SituacaoCliente>();
        Statement st = null;

        try {

            ConnectionOracleSap();

            st = con.createStatement();

            String sql = "select a.codcli, max(c.codori) codori, max(a.datemi) datemi\n"
                    + "  from e140nfv a, e140ipv b, e075pro c, e085cli d, e001tns e\n"
                    + " where\n"
                    // + "a.datemi>='25/10/2020' and a.datemi<='28/01/2021' and\n"
                    + " a.sitnfv = 2\n"
                    + " and a.codemp = 1\n"
                    + " and a.codemp = b.codemp\n"
                    + " and a.codfil = b.codfil\n"
                    + " and a.codsnf = b.codsnf\n"
                    + " and a.numnfv = b.numnfv\n"
                    + " and b.codemp = c.codemp\n"
                    + " and b.codpro = c.codpro\n"
                    + " and c.codori in ('BM', 'BA')\n"
                    + " and a.codcli = d.codcli\n"
                    + " and a.codemp = 1\n"
                    + " and a.codcli <> 1\n"
                    + " and a.codemp = e.codemp\n"
                    + " and a.tnspro = e.codtns\n"
                    + " and e.venfat = 'S'\n"
                    //   + " and a.codcli = 105\n"
                    + " ";
            sql += PESQUISA;

            sql += "    and not exists (select 1 from e070fil e where a.codcli = e.filcli)\n"
                    + " group by a.codcli, c.codori\n"
                    + " order by a.codcli";

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            while (rs.next()) {
                SituacaoCliente c = new SituacaoCliente();
                c.setEmissao(rs.getDate("datemi"));
                c.setCodigo(rs.getString("codori"));

                c.setDias_ultimo_faturamento(utilDatas.nrDias(new Date(), c.getEmissao()));
              //  c.setDescricao("ATIVO");

                if (c.getDias_ultimo_faturamento() <= 45) {
                    c.setDescricao("ATIVO");
                } else {
                    if (c.getDias_ultimo_faturamento() > 90) {
                        c.setDescricao("INATIVO");
                    }else{
                        c.setDescricao("PRÉ-INATIVO");
                    }
                }

                c.setDias_ultimo_faturamento(FormatarNumeros.converterDoubleDoisDecimais(c.getDias_ultimo_faturamento()));
                c.setEmissaoS(utilDatas.converterDateToStr(c.getEmissao()));

                list.add(c);
            }

            return list;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro " + e.getClass().getName() + "\n " + e.getMessage(),
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, "Erro " + e.getClass().getName() + "\n " + e.getMessage(),
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }

}
