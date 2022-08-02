/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import br.com.sgi.util.UtilDatas;
import br.com.sgi.interfaces.InterfaceNotaFiscalDAO;
import br.com.sgi.util.FormatarNumeros;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;

/**
 *
 * @author jairosilva
 */
public class NotaFiscalDAO implements InterfaceNotaFiscalDAO<NotaFiscal> {

    private Connection con;
    private UtilDatas utilDatas;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private List<NotaFiscal> getLista(ResultSet rs) throws SQLException, ParseException {
        List<NotaFiscal> resultado = new ArrayList<NotaFiscal>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            NotaFiscal e = new NotaFiscal();
            e.setCodigocliente(rs.getInt("codigocliente"));
            e.setEmissao(rs.getDate("emissao"));
            e.setEmissaoS(utilDatas.converterDateToStr(e.getEmissao()));
            e.setEmpresa(rs.getInt("empresa"));
            e.setFilial(rs.getInt("filial"));
            e.setTransacao(rs.getString("transacao"));
            e.setPedido(rs.getInt("pedido"));
            e.setPesoBruto(rs.getDouble("pesobruto"));
            e.setPesoLiquido(rs.getDouble("pesoliquido"));
            e.setPedido(rs.getInt("pedido"));
            e.setNotafiscal(rs.getInt("notafiscal"));
            e.setTransportadora(rs.getInt("transportadora"));
            e.setNomeTransportadora(rs.getString("nometransportadora"));
            e.setQuantidade(rs.getDouble("quantidade"));
            e.setQuantidadevolume(rs.getDouble("quantidadevolume"));
            e.setSerie(rs.getString("codsnf"));
            Cliente cli = new Cliente();
            cli.setCodigo(e.getCodigocliente());
            cli.setNome(rs.getString("nomecliente"));
            cli.setEstado(rs.getString("estado"));
            cli.setCidade(rs.getString("cidade"));
            e.setCliente(cli);

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public NotaFiscal getNotaFiscal(String PESQUISA_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(nfv.codemp) empresa,\n"
                + "       max(nfv.codfil) filial,\n"
                + "       max(nfv.codsnf) codsnf,\n"
                + "       max(nfv.codcli) codigocliente,\n"
                + "       max(nfv.codtra) transportadora,\n"
                + "       max(nfv.datemi) emissao,\n"
                + "       max(nfv.numnfv) notafiscal,\n"
                + "       sum(ipv.qtdfat) quantidade,\n"
                + "       sum(ipv.pesliq) pesoliquido,\n"
                + "        avg(nfv.qtdemb) quantidadevolume,\n"
                + "       sum(ipv.pesbru) pesobruto,\n"
                + "       max(ipv.numped) pedido,\n"
                + "       max(ipv.tnspro) transacao,\n"
                + "       max(cli.nomcli) nomecliente,\n"
                + "       max(cli.sigufs) estado,\n"
                + "       max(cli.cidcli) cidade,\n"
                + "       max(tra.nomtra) nometransportadora\n"
                + "  from e140nfv nfv, e140ipv ipv, e085cli cli, e073tra tra\n"
                + " where nfv.codemp = ipv.codemp\n"
                + "   and nfv.codfil = ipv.codfil\n"
                + "   and nfv.codsnf = ipv.codsnf\n"
                + "   and nfv.numnfv = ipv.numnfv\n"
                + "   and nfv.codcli = cli.codcli\n"
                + "   and nfv.codtra = tra.codtra\n"
                + "   and nfv.sitnfv = 2\n";
        sqlSelect += PESQUISA;
        sqlSelect += " group by nfv.codemp, nfv.codfil, nfv.numnfv\n"
                + " order by nfv.codemp, nfv.codfil, nfv.numnfv desc ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<NotaFiscal> resultado = new ArrayList<NotaFiscal>();
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
    public List<NotaFiscal> getNotaFiscais(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<NotaFiscal> resultado = new ArrayList<NotaFiscal>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(nfv.codemp) empresa,\n"
                + "       max(nfv.codfil) filial,\n"
                + "       max(nfv.codcli) codigocliente,\n"
                + "       max(nfv.codtra) transportadora,\n"
                + "       max(nfv.datemi) emissao,\n"
                + "       max(nfv.numnfv) notafiscal,\n"
                + "       sum(ipv.qtdfat) quantidade,\n"
                + "       sum(ipv.pesliq) pesoliquido,\n"
                + "        avg(nfv.qtdemb) quantidadevolume,\n"
                + "       sum(ipv.pesbru) pesobruto,\n"
                + "       max(ipv.numped) pedido,\n"
                + "       max(ipv.tnspro) transacao,\n"
                + "       max(cli.nomcli) nomecliente,\n"
                + "       max(cli.sigufs) estado,\n"
                + "       max(cli.cidcli) cidade,\n"
                + "       max(tra.nomtra) nometransportadora\n"
                + "  from e140nfv nfv, e140ipv ipv, e085cli cli, e073tra tra\n"
                + " where nfv.codemp = ipv.codemp\n"
                + "   and nfv.codfil = ipv.codfil\n"
                + "   and nfv.codsnf = ipv.codsnf\n"
                + "   and nfv.numnfv = ipv.numnfv\n"
                + "   and nfv.codcli = cli.codcli\n"
                + "   and nfv.codtra = tra.codtra\n"
                + "   and nfv.sitnfv = 2\n";
        sqlSelect += PESQUISA;
        sqlSelect += " group by nfv.codemp, nfv.codfil,  cli.sigufs, nfv.numnfv\n"
                + " order by nfv.codemp, nfv.codfil, cli.sigufs, nfv.numnfv desc ";

        System.out.print(" nota" + sqlSelect);

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
    public boolean alterarNotaMinuta(NotaFiscal t, int qtdreg, int contador) throws SQLException {
        PreparedStatement pst = null;

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement("UPDATE e140nfv SET usu_indsuc='M' "
                    + "WHERE  codemp = ? "
                    + "and codfil=? "
                    + "and numnfv=? "
                    + "and codsnf=?");

            pst.setInt(1, t.getEmpresa());
            pst.setInt(2, t.getFilial());
            pst.setInt(3, t.getNotafiscal());
            pst.setString(4, t.getSerie());

            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro " + e,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
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
    public NotaFiscal getNotaFiscalCarteira(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException {
        this.utilDatas = new UtilDatas();

        NotaFiscal c = new NotaFiscal();
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

                c.setSituacao("ATIVO");
                c.setDias_ultimo_faturamento(utilDatas.nrDias(DATA, c.getEmissao()));
                if (c.getDias_ultimo_faturamento() > 90) {
                    c.setSituacao("INATIVO");
                }
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
    public NotaFiscal getNotaFiscalCte(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException {
        this.utilDatas = new UtilDatas();

        NotaFiscal c = new NotaFiscal();
        Statement st = null;

        try {

            ConnectionOracleSap();

            st = con.createStatement();

            String sql = sql = "SELECT MAX(E140IPV.CODEMP) CODEMP,\n"
                    + "       MAX(E140IPV.CODFIL) CODFIL,\n"
                    + "       MAX(E140IPV.CODSNF) CODSNF,\n"
                    + "       MAX(E140IPV.NUMNFV) NUMNFV,\n"
                    + "       MAX(E140NFV.CODCLI) CODCLI,\n"
                    + "       MAX(E140NFV.CODTRA) CODTRA,\n"
                    + "       \n"
                    + "       \n"
                    + "       MAX(E120PED.NUMPED) NUMPED,\n"
                    + "       MAX(E140NFV.DATEMI) DATEMI,\n"
                    + "       MAX(E140IPV.TNSPRO) TNSPRO,\n"
                    + "       SUM(E140IPV.QTDFAT) QTDFAT,\n"
                    + "       SUM(E140IPV.PESBRU) PESBRU,\n"
                    + "       SUM(E140IPV.PESLIQ) PESLIQ,\n"
                    + "       SUM(E140IPV.VLRBRU) VLRBRU,\n"
                    + "       SUM(E140IPV.VLRLIQ) VLRLIQ,\n"
                    + "       SUM(E140IPV.VLRFIN) VLRFIN,\n"
                    + "       \n"
                    + "       MAX(E085CLI.NOMCLI) NOMCLI,\n"
                    + "       MAX(E085CLI.SIGUFS) SIGUFS,\n"
                    + "       MAX(E085CLI.CIDCLI) CIDCLI,\n"
                    + "       MAX(E085CLI.CEPCLI) CEPCLI,\n"
                    + "       MAX(E085CLI.ENDCLI) ENDCLI,\n"
                    + "       MAX(E085CLI.NENCLI) NENCLI,\n"
                    + "       MAX(E085CLI.BAICLI) BAICLI,\n"
                    + "       MAX(E075PRO.CODORI) CODORI, \n"
                    + "       MAX(E001TNS.CODTNS) CODTNS,\n"
                    + "       MAX(E001TNS.VENFAT) VENFAT\n"
                    + "\n"
                    + "  FROM E140IPV,\n"
                    + "       E140NFV,\n"
                    + "       E075PRO,\n"
                    + "       E120PED,\n"
                    + "       E085CLI,\n"
                    + "       E090REP,\n"
                    + "       E120IPD,\n"
                    + "       E001TNS,\n"
                    + "       E140IDE\n"
                    + " WHERE 0 = 0\n"
                    + "   AND ((E140IPV.CODEMP = 1))\n"
                    + "      \n"
                    + "   AND ((E001TNS.VENFAT = 'S'))\n"
                    + "   AND (((E075PRO.CODEMP = E140IPV.CODEMP) AND\n"
                    + "       (E075PRO.CODPRO = E140IPV.CODPRO)) AND\n"
                    + "       (E085CLI.CODCLI = E140NFV.CODCLI) AND\n"
                    + "       (E090REP.CODREP = E140NFV.CODREP) AND\n"
                    + "       ((E120IPD.CODEMP = E140IPV.CODEMP) AND\n"
                    + "       (E120IPD.CODFIL = E140IPV.FILPED) AND\n"
                    + "       (E120IPD.NUMPED = E140IPV.NUMPED) AND\n"
                    + "       (E120IPD.SEQIPD = E140IPV.SEQIPD)) AND\n"
                    + "       ((E140NFV.CODEMP = E140IPV.CODEMP) AND\n"
                    + "       (E140NFV.CODFIL = E140IPV.CODFIL) AND\n"
                    + "       (E140NFV.CODSNF = E140IPV.CODSNF) AND\n"
                    + "       (E140NFV.NUMNFV = E140IPV.NUMNFV)) AND\n"
                    + "       (E140IPV.CODEMP = E120PED.CODEMP) AND\n"
                    + "       (E140IPV.FILPED = E120PED.CODFIL) AND\n"
                    + "       (E140IPV.NUMPED = E120PED.NUMPED) AND\n"
                    + "       ((E001TNS.CODEMP = E140NFV.CODEMP) AND\n"
                    + "       (E001TNS.CODTNS = E140NFV.TNSPRO)))\n"
                    + "   AND E140NFV.CODEMP = E140IDE.CODEMP\n"
                    + "   AND E140NFV.CODFIL = E140IDE.CODFIL\n"
                    + "   AND E140NFV.NUMNFV = E140IDE.NUMNFV\n"
                    + "   AND E140NFV.CODSNF = E140IDE.CODSNF";

            sql = "select ide.codemp,\n"
                    + "       ide.codfil,\n"
                    + "       ide.codsnf,\n"
                    + "       ide.numnfv,\n"
                    + "       ide.sitdoe,\n"
                    + "       ide.sitdea,\n"
                    + "       ide.numprt,\n"
                    + "       nfv.codcli,\n"
                    + "       nfv.datemi,\n"
                    + "       nfv.tnspro,\n"
                    + "       cli.nomcli,\n"
                    + "       cli.cidcli,\n"
                    + "       cli.sigufs\n"
                    + "  from e140ide ide, e140nfv nfv\n"
                    + "  left join e085cli cli\n"
                    + "    on cli.codcli = nfv.codcli\n"
                    + " where 0=0 \n"
                    + "   and ide.codemp = nfv.codemp\n"
                    + "   and ide.codfil = nfv.codfil\n"
                    + "   and ide.numnfv = nfv.numnfv\n"
                    + "   and ide.codsnf = nfv.codsnf\n"
                    + "";

            sql = "select max(nfv.codemp) codemp,\n"
                    + "       max(nfv.codfil) codfil,\n"
                    + "       max(nfv.codsnf) codsnf,\n"
                    + "       max(nfv.numnfv) numnfv,\n"
                    + "       max(nfv.datemi) datemi,\n"
                    + "       max(nfv.tnspro) tnspro,\n"
                    + "       max(nfv.codtra) codtra,\n"
                    + "       avg(nfv.vlrliq) vlrliq,\n"
                    + "       avg(nfv.vlrfin) vlrfin,\n"
                    + "       sum(ipv.qtdfat * ipv.preuni) as valliq,\n"
                    + "       \n"
                    + "       max(ide.sitdoe) sitdoe,\n"
                    + "       max(ide.sitdea) sitdea,\n"
                    + "       max(ide.numprt) numprt,\n"
                    + "       max(nfv.codcli) codcli,\n"
                    + "       \n"
                    + "         max(TRANSLATE(cli.nomcli,\n"
                    + "                     'ŠŽšžŸÁÇÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕËÜÏÖÑÝåáçéíóúàèìòùâêîôûãõëüïöñýÿ&',                     \n"
                    + "                     'SZszYACEIOUAEIOUAEIOUAOEUIONYaaceiouaeiouaeiouaoeuionyy ')) nomcli,\n"
                    + "       max(cli.cidcli) cidcli,\n"
                    + "       max(cli.sigufs) sigufs,\n"
                    + "       max(cli.baicli) baicli,\n"
                    + "       max(cli.nencli) nencli,\n"
                    + "       max(cli.cepcli) cepcli,\n"
                    + "       \n"
                    + "       sum(ipv.qtdfat) qtdfat,\n"
                    + "       sum(ipv.pesliq) pesliq,\n"
                    + "       max(ipv.codpro) codpro,\n"
                    + "       \n"
                    + "       max(pro.codori) codori,\n"
                    + "       max(pro.pesliq) pesliqpro,\n"
                    + "       sum(ipv.qtdfat * pro.pesliq) as pesliqcal\n"
                    + "\n"
                    + "  from e140ide ide, e140nfv nfv\n"
                    + "  left join e085cli cli\n"
                    + "    on cli.codcli = nfv.codcli\n"
                    + "  left join e140ipv ipv\n"
                    + "    on ipv.codemp = nfv.codemp\n"
                    + "   and ipv.codfil = nfv.codfil\n"
                    + "   and ipv.numnfv = nfv.numnfv\n"
                    + "   and ipv.codsnf = nfv.codsnf\n"
                    + "  left join e075pro pro\n"
                    + "    on pro.codemp = ipv.codemp\n"
                    + "   and pro.codpro = ipv.codpro\n"
                    + "\n"
                    + " where 0 = 0\n"
                    + "   and ide.codemp = nfv.codemp\n"
                    + "   and ide.codfil = nfv.codfil\n"
                    + "   and ide.numnfv = nfv.numnfv\n"
                    + "   and ide.codsnf = nfv.codsnf\n"
                    + "";

            sql += PESQUISA;

            ResultSet rs = st.executeQuery(sql);

            System.out.println("Produção \n" + sql);

            if (rs.next()) {

                c.setEmpresa(rs.getInt("codemp"));
                if (c.getEmpresa() > 0) {
                    c.setFilial(rs.getInt("codfil"));
                    c.setEmissao(rs.getDate("datemi"));
                    c.setCodigocliente(rs.getInt("codcli"));
                    c.setNotafiscal(rs.getInt("numnfv"));
                    c.setTransacao(rs.getString("tnspro"));
                    c.setSerie(rs.getString("codsnf"));
                    c.setQuantidade(rs.getDouble("qtdfat"));
                    c.setTransportadora(rs.getInt("codtra"));
                    c.setPesoLiquido(rs.getDouble("pesliq"));
                    c.setValorLiquido(rs.getDouble("vlrliq"));
                    c.setOrigem(rs.getString("codori"));
                    c.setLinhaProduto("OUTROS");
                    if (c.getOrigem().equals("BA")) {
                        c.setLinhaProduto("AUTO");
                    }
                    if (c.getOrigem().equals("BM")) {
                        c.setLinhaProduto("MOTO");
                    }

                    c.setCliente(new Cliente(c.getCodigocliente(),
                            rs.getString("nomcli"),
                            rs.getString("sigufs"),
                            rs.getString("cidcli")));
                }

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

}
