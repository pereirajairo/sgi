/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.interfaces.InterfaceSucataAnalisesDAO;
import br.com.sgi.bean.SucataAnalises;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
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
public class SucataAnalisesDAO implements InterfaceSucataAnalisesDAO<SucataAnalises> {

    private Connection con;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private List<SucataAnalises> getLista(ResultSet rs) throws SQLException, ParseException {
        List<SucataAnalises> resultado = new ArrayList<SucataAnalises>();
        while (rs.next()) {
            SucataAnalises e = new SucataAnalises();
            e.setAgrupamento(rs.getString("agrupamento"));
            e.setCliente(rs.getInt("cliente"));
            e.setNome(rs.getString("nome"));
            e.setEstado(rs.getString("estado"));
            e.setEmissao_nota(rs.getDate("emissao_nota"));
            e.setEmissao_pedido(rs.getDate("emissao_pedido"));
            e.setEmissao_sucata(rs.getDate("emissao_sucata"));
            e.setEmpresa(rs.getInt("empresa"));
            e.setEstado(rs.getString("estado"));
            e.setCidade(rs.getString("cidade"));
            e.setFechamento_pedido(rs.getDate("fechamento_pedido"));
            e.setFilial(rs.getInt("filial"));
            e.setEmpresa(rs.getInt("empresa"));
            e.setSerienota(rs.getString("serie_nota"));
            e.setSucata(rs.getString("codpro"));
            e.setLancamento(rs.getInt("lancamento"));
            e.setLinha(rs.getString("linha"));
            e.setNome_filial(rs.getString("nome_filial"));
            e.setNota(rs.getInt("nota"));
            e.setOrdem_compra(rs.getInt("ordem_compra"));
            e.setPedido_nota(rs.getInt("pedido_nota"));
            e.setPedido_sucata(rs.getInt("pedido_sucata"));
            e.setPeso_bruto_nota(rs.getDouble("peso_bruto_nota"));
            e.setPeso_item_nota(rs.getDouble("peso_item_nota"));
            e.setPeso_sucata(rs.getDouble("peso_sucata"));
            e.setQuantidade(rs.getDouble("quantidade"));
            e.setSequencia(rs.getInt("sequencia"));
            e.setTransportadora(rs.getString("transportadora"));
            e.setTabela(rs.getString("tabela"));
            e.setPeso_ordem_compra(rs.getDouble("peso_ordem_compra"));
            e.setNota_entrada(rs.getInt("nota_fiscal_entrada"));
            e.setPeso_entrada(0.0);
            e.setDescricao_transacao(rs.getString("descricao_transacao"));
            e.setPedido_transacao(rs.getString("pedido_transacao"));
            if (e.getPedido_transacao() == null) {
                e.setPedido_transacao(" ");
            }
            e.setGerar_sucata(rs.getString("gerar_sucata"));
            if (e.getGerar_sucata() == null) {
                e.setGerar_sucata("N");
                e.setDescricao_transacao("");
            }
            e.setLiberadoentrega(rs.getString("GERAR_MINUTA"));
            if(e.getLiberadoentrega()==null){
                e.setLiberadoentrega("N");
            }

            e.setCadCliente(new Cliente(e.getCliente(), e.getNome(), e.getEstado(), rs.getString("cidade")));

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<SucataAnalises> getSucataAnalisess(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataAnalises> resultado = new ArrayList<SucataAnalises>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E140NFV.CODEMP EMPRESA,\n"
                + "       MAX(E140NFV.DATEMI) DATEMI,\n"
                + "       MAX(E140NFV.CODFIL) FILIAL,\n"
                + "       MAX(E140NFV.NUMNFV) NOTA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       MAX(E140IPV.CODSNF) SERIE_NOTA,\n"
                + "       MAX(E140IPV.CODTPR) TABELA,\n"
                + "       MAX(E140NFV.DATEMI) EMISSAO_NOTA,\n"
                + "       MAX(E140IPV.CODPRO) CODPRO,\n"
                + "       NULL EMISSAO_PEDIDO,\n"
                + "       NULL FECHAMENTO_PEDIDO,\n"
                + "       MAX(SUC.USU_DATGER) EMISSAO_SUCATA,\n"
                + "       MAX(E140NFV.USU_INDSUC) GERAR_MINUTA,\n"
                + "       MAX(E140NFV.CODCLI) CLIENTE,\n"
                + "       MAX(E085CLI.NOMCLI) NOME,\n"
                + "       MAX(E085CLI.SIGUFS) ESTADO,\n"
                + "       MAX(E085CLI.CIDCLI) CIDADE,\n"
                + "       MAX(E140NFV.PESBRU) PESO_BRUTO_NOTA,\n"
                + "       AVG(SUC.USU_PESFAT) PESO_SUCATA,\n"
                + "       SUM(E140IPV.QTDFAT * E075PRO.PESLIQ) PESO_ITEM_NOTA,\n"
                + "       SUM(E140IPV.QTDFAT) QUANTIDADE,"
                + "       MAX(E140NFV.CODTRA) TRANSPORTADORA,\n"
                + "       MAX(E085CLI.CODGRE) AGRUPAMENTO,\n"
                + "       MAX(E070FIL.NOMFIL) NOME_FILIAL,\n"
                + "       MAX(SUC.USU_CODLAN) LANCAMENTO,\n"
                + "       MAX(SUC.USU_SEQMOV) SEQUENCIA,\n"
                + "       MAX(SUC.USU_NUMOCP) ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_PESORD) PESO_ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_NUMPED) PEDIDO_SUCATA,\n"
                + "       MAX(SUC.USU_TIPMOV) TIPO_SUCATA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       'AUTO' LINHA,\n"
                + "       MAX(IPC.NUMNFC) AS NOTA_FISCAL_ENTRADA,\n"
                + "       MAX(UPPER(TNS.DESTNS)) AS DESCRICAO_TRANSACAO,\n"
                + "       MAX(SUC.USU_TNSPRO) AS PEDIDO_TRANSACAO,\n"
                + "       MAX(UPPER(TNS.USU_GERSUC)) AS GERAR_SUCATA,\n"
                + "       MAX(TNSS.CODTNS) || ' - ' || MAX(UPPER(TNSS.DESTNS)) AS DESCRICAO_TRANSACAO_NF\n"
                + "\n"
                + "  FROM E001TNS, E085CLI, E070EMP, E070FIL, E140IPV, E075PRO, E140NFV\n"
                + "  LEFT JOIN USU_TSUCMOV SUC\n"
                + "    ON (SUC.USU_CODEMP = E140NFV.CODEMP AND SUC.USU_CODFIL = 1 AND\n"
                + "       SUC.USU_NUMNFV = E140NFV.NUMNFV AND SUC.USU_CODCLI = E140NFV.CODCLI AND\n"
                + "       SUC.USU_DEBCRE = '3 - DEBITO')\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNS\n"
                + "    ON TNS.CODTNS = SUC.USU_TNSPRO\n"
                + "   AND TNS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNSS\n"
                + "    ON TNSS.CODTNS = E140NFV.TNSPRO\n"
                + "   AND TNSS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E440IPC IPC\n"
                + "    ON IPC.CODEMP = 1\n"
                + "   AND IPC.CODFIL = 1\n"
                + "   AND IPC.NUMOCP = SUC.USU_NUMOCP\n"
                + "   AND SUC.USU_NUMOCP > 0\n"
                + "\n"
                + " WHERE ((E140NFV.CODEMP = E001TNS.CODEMP) AND\n"
                + "       ((E140NFV.TNSPRO = E001TNS.CODTNS) OR\n"
                + "       (E140NFV.TNSSER = E001TNS.CODTNS)))\n"
                + "   AND ((E140NFV.CODEMP = 1))\n"
                + "   AND ((E140NFV.CODFIL >0))\n"
                + "   AND ((E140NFV.SITNFV = '2'))\n"
                + "   AND ((E140NFV.CODEMP = E070EMP.CODEMP) AND\n"
                + "       (E140NFV.CODEMP = E070FIL.CODEMP) AND\n"
                + "       (E140NFV.CODFIL = E070FIL.CODFIL) AND\n"
                + "       (E140NFV.CODCLI = E085CLI.CODCLI))\n"
                + "   AND (E001TNS.VENFAT = 'S')\n"
                + "      \n"
                + "   AND E140NFV.CODEMP = E140IPV.CODEMP\n"
                + "   AND E140NFV.CODFIL = E140IPV.CODFIL\n"
                + "   AND E140NFV.NUMNFV = E140IPV.NUMNFV\n"
                + "   AND E140NFV.CODSNF = E140IPV.CODSNF\n"
                + "   AND E140NFV.TNSPRO = E140IPV.TNSPRO\n"
                + "      \n"
                + "   AND E140IPV.CODEMP = E075PRO.CODEMP\n"
                + "   AND E140IPV.CODPRO = E075PRO.CODPRO\n"
                + "      --  AND E140IPV.CODTPR NOT IN ('FUNC')\n"
                + "   AND E075PRO.CODORI IN ('BA')\n"
                + " ";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\nGROUP BY E140NFV.DATEMI, E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV";
        sqlSelect += "\nORDER BY E140NFV.DATEMI, E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV ASC";
        System.out.println("Sql  \n" + sqlSelect);

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
    public SucataAnalises getSucataAnalises(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataAnalises> resultado = new ArrayList<SucataAnalises>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E140NFV.CODEMP EMPRESA,\n"
                + "       MAX(E140NFV.DATEMI) DATEMI,\n"
                + "       MAX(E140NFV.CODFIL) FILIAL,\n"
                + "       MAX(E140NFV.NUMNFV) NOTA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       MAX(E140IPV.CODSNF) SERIE_NOTA,\n"
                + "       MAX(E140IPV.CODTPR) TABELA,\n"
                + "       MAX(E140NFV.DATEMI) EMISSAO_NOTA,\n"
                + "       NULL EMISSAO_PEDIDO,\n"
                + "       NULL FECHAMENTO_PEDIDO,\n"
                + "       MAX(SUC.USU_DATGER) EMISSAO_SUCATA,\n"
                + "      MAX(E140NFV.USU_INDSUC) GERAR_MINUTA,\n"
                + "       MAX(E140NFV.CODCLI) CLIENTE,\n"
                + "       MAX(E085CLI.NOMCLI) NOME,\n"
                + "       MAX(E085CLI.SIGUFS) ESTADO,\n"
                + "       MAX(E085CLI.CIDCLI) CIDADE,\n"
                + "       MAX(E140NFV.PESBRU) PESO_BRUTO_NOTA,\n"
                + "       AVG(SUC.USU_PESFAT) PESO_SUCATA,\n"
                + "       SUM(E140IPV.QTDFAT * E075PRO.PESLIQ) PESO_ITEM_NOTA,\n"
                + "       SUM(E140IPV.QTDFAT) QUANTIDADE,"
                + "       MAX(E140NFV.CODTRA) TRANSPORTADORA,\n"
                + "       MAX(E085CLI.CODGRE) AGRUPAMENTO,\n"
                + "       MAX(E070FIL.NOMFIL) NOME_FILIAL,\n"
                + "       MAX(SUC.USU_CODLAN) LANCAMENTO,\n"
                + "       MAX(SUC.USU_SEQMOV) SEQUENCIA,\n"
                + "       MAX(SUC.USU_NUMOCP) ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_PESORD) PESO_ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_NUMPED) PEDIDO_SUCATA,\n"
                + "       MAX(SUC.USU_TIPMOV) TIPO_SUCATA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       'AUTO' LINHA,\n"
                + "       MAX(IPC.NUMNFC) AS NOTA_FISCAL_ENTRADA,\n"
                + "       MAX(UPPER(TNS.DESTNS)) AS DESCRICAO_TRANSACAO,\n"
                + "       MAX(SUC.USU_TNSPRO) AS PEDIDO_TRANSACAO,\n"
                + "       MAX(UPPER(TNS.USU_GERSUC)) AS GERAR_SUCATA,\n"
                + "       MAX(TNSS.CODTNS) || ' - ' || MAX(UPPER(TNSS.DESTNS)) AS DESCRICAO_TRANSACAO_NF\n"
                + "\n"
                + "  FROM E001TNS, E085CLI, E070EMP, E070FIL, E140IPV, E075PRO, E140NFV\n"
                + "  LEFT JOIN USU_TSUCMOV SUC\n"
                + "    ON (SUC.USU_CODEMP = E140NFV.CODEMP AND SUC.USU_CODFIL = 1 AND\n"
                + "       SUC.USU_NUMNFV = E140NFV.NUMNFV AND SUC.USU_CODCLI = E140NFV.CODCLI AND\n"
                + "       SUC.USU_DEBCRE = '3 - DEBITO')\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNS\n"
                + "    ON TNS.CODTNS = SUC.USU_TNSPRO\n"
                + "   AND TNS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNSS\n"
                + "    ON TNSS.CODTNS = E140NFV.TNSPRO\n"
                + "   AND TNSS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E440IPC IPC\n"
                + "    ON IPC.CODEMP = 1\n"
                + "   AND IPC.CODFIL = 1\n"
                + "   AND IPC.NUMOCP = SUC.USU_NUMOCP\n"
                + "   AND SUC.USU_NUMOCP > 0\n"
                + "\n"
                + " WHERE ((E140NFV.CODEMP = E001TNS.CODEMP) AND\n"
                + "       ((E140NFV.TNSPRO = E001TNS.CODTNS) OR\n"
                + "       (E140NFV.TNSSER = E001TNS.CODTNS)))\n"
                + "   AND ((E140NFV.CODEMP = 1))\n"
                + "   AND ((E140NFV.CODFIL NOT IN (10, 11, 13)))\n"
                + "   AND ((E140NFV.SITNFV = '2'))\n"
                + "   AND ((E140NFV.CODEMP = E070EMP.CODEMP) AND\n"
                + "       (E140NFV.CODEMP = E070FIL.CODEMP) AND\n"
                + "       (E140NFV.CODFIL = E070FIL.CODFIL) AND\n"
                + "       (E140NFV.CODCLI = E085CLI.CODCLI))\n"
                + "   AND (E001TNS.VENFAT = 'S')\n"
                + "      \n"
                + "   AND E140NFV.CODEMP = E140IPV.CODEMP\n"
                + "   AND E140NFV.CODFIL = E140IPV.CODFIL\n"
                + "   AND E140NFV.NUMNFV = E140IPV.NUMNFV\n"
                + "   AND E140NFV.CODSNF = E140IPV.CODSNF\n"
                + "   AND E140NFV.TNSPRO = E140IPV.TNSPRO\n"
                + "      \n"
                + "   AND E140IPV.CODEMP = E075PRO.CODEMP\n"
                + "   AND E140IPV.CODPRO = E075PRO.CODPRO\n"
                + "      --  AND E140IPV.CODTPR NOT IN ('FUNC')\n"
                + "   AND E075PRO.CODORI IN ('BA','BM')\n"
                + " ";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\nGROUP BY E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV";

        System.out.println("Sql  \n" + sqlSelect);

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
    public List<SucataAnalises> getSucataAnalisessMoto(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataAnalises> resultado = new ArrayList<SucataAnalises>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT MAX(E140NFV.CODEMP) EMPRESA,\n"
                + "       MAX(E140NFV.CODFIL) FILIAL,\n"
                + "       MAX(E140NFV.NUMNFV) NOTA,\n"
                + "       MAX(E140NFV.CODSNF) SERIE_NOTA,\n"
                + "       MAX(E140IPV.CODTPR) TABELA,\n"
                + "       MAX(E140NFV.DATEMI) EMISSAO_NOTA,\n"
                + "       MAX(E120PED.DATEMI) EMISSAO_PEDIDO,\n"
                + "       MAX(E120PED.DATFEC) FECHAMENTO_PEDIDO,\n"
                + "       MAX(SUC.USU_DATGER) EMISSAO_SUCATA,\n"
                + "       MAX(E140NFV.USU_INDSUC) GERAR_MINUTA,\n"
                + "       MAX(E140NFV.CODCLI) CLIENTE,\n"
                + "       MAX(E085CLI.NOMCLI) NOME,\n"
                + "       MAX(E085CLI.SIGUFS) ESTADO,\n"
                + "       MAX(E085CLI.CIDCLI) CIDADE,\n"
                + "       MAX(E140NFV.PESBRU) PESO_BRUTO_NOTA,\n"
                + "       AVG(SUC.USU_PESFAT) PESO_SUCATA,\n"
                + "       SUM(E140IPV.QTDFAT * E075PRO.PESLIQ) PESO_ITEM_NOTA,\n"
                + "       SUM(E140IPV.QTDFAT) QUANTIDADE,\n"
                + "       MAX(E140NFV.CODTRA) TRANSPORTADORA,\n"
                + "       MAX(E085CLI.CODGRE) AGRUPAMENTO,\n"
                + "       MAX(E070FIL.NOMFIL) NOME_FILIAL,\n"
                + "       MAX(SUC.USU_CODLAN) LANCAMENTO,\n"
                + "       MAX(SUC.USU_SEQMOV) SEQUENCIA,\n"
                + "       MAX(SUC.USU_NUMOCP) ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_PESORD) PESO_ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_NUMPED) PEDIDO_SUCATA,\n"
                + "       MAX(SUC.USU_TIPMOV) TIPO_SUCATA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       'MOTO' LINHA,\n"
                + "       MAX(E140IPV.CODPRO) CODPRO,\n"
                + "       MAX(IPC.NUMNFC) AS NOTA_FISCAL_ENTRADA,\n"
                + "       MAX(UPPER(TNS.DESTNS)) AS DESCRICAO_TRANSACAO,\n"
                + "       MAX(E120PED.TNSPRO) AS PEDIDO_TRANSACAO,\n"
                + "       MAX(TNS.USU_GERSUC) AS GERAR_SUCATA\n"
                + "  FROM E001TNS,\n"
                + "       E085CLI,\n"
                + "       E070EMP,\n"
                + "       E070FIL,\n"
                + "       E140IPV,\n"
                + "       E120PED,\n"
                + "       E075PRO,\n"
                + "       E081TAB,\n"
                + "       E140NFV\n"
                + "  LEFT JOIN USU_TSUCMOV SUC\n"
                + "    ON (SUC.USU_CODEMP = E140NFV.CODEMP AND SUC.USU_CODFIL = 1 AND\n"
                + "       SUC.USU_NUMNFV = E140NFV.NUMNFV AND SUC.USU_CODCLI = E140NFV.CODCLI AND\n"
                + "       SUC.USU_DEBCRE = '3 - DEBITO')\n"
                + " LEFT JOIN E440IPC IPC ON IPC.CODEMP = 1 AND IPC.CODFIL = 1 AND IPC.NUMOCP =    SUC.USU_NUMOCP  AND SUC.USU_NUMOCP >0"
                + "   LEFT JOIN E001TNS TNS\n"
                + "    ON TNS.CODTNS = SUC.USU_TNSPRO\n"
                + "   AND TNS.CODEMP = 1\n"
                + " WHERE ((E140NFV.CODEMP = E001TNS.CODEMP) AND\n"
                + "       ((E140NFV.TNSPRO = E001TNS.CODTNS) OR\n"
                + "       (E140NFV.TNSSER = E001TNS.CODTNS)))\n"
                + "   AND ((E140NFV.CODEMP = 1))\n"
                + "   AND ((E140NFV.CODFIL > 1) AND (E140NFV.CODFIL < 10) OR\n"
                + "       (E140NFV.CODFIL >= 16 AND E140NFV.CODFIL <= 89))\n"
                + "   AND ((E140NFV.SITNFV = '2'))\n"
                + "   AND ((E140NFV.CODEMP = E070EMP.CODEMP) AND\n"
                + "       (E140NFV.CODEMP = E070FIL.CODEMP) AND\n"
                + "       (E140NFV.CODFIL = E070FIL.CODFIL) AND\n"
                + "       (E140NFV.CODCLI = E085CLI.CODCLI))\n"
                + "   AND (E001TNS.VENFAT = 'S')\n"
                + "      AND E140NFV.CODEMP = E140IPV.CODEMP\n"
                + "   AND E140NFV.CODFIL = E140IPV.CODFIL\n"
                + "   AND E140NFV.NUMNFV = E140IPV.NUMNFV\n"
                + "   AND E140NFV.CODSNF = E140IPV.CODSNF\n"
                + "   AND E140NFV.TNSPRO = E140IPV.TNSPRO\n"
                + "   AND E140IPV.CODEMP = E120PED.CODEMP\n"
                + "   AND E140IPV.CODFIL = E120PED.CODFIL\n"
                + "   AND E140IPV.NUMPED = E120PED.NUMPED\n"
                + "   AND E075PRO.CODORI IN ('BM')\n"
                + "   AND E140IPV.CODTPR NOT IN ('FUNC')\n"
                + "   AND E140IPV.CODEMP = E081TAB.CODEMP\n"
                + "   AND E140IPV.CODTPR = E081TAB.CODTPR\n"
                + "   AND E081TAB.USU_GERSUC = 'S'\n"
                + " ";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\nGROUP BY E140NFV.DATEMI, E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV";
        sqlSelect += "\nORDER BY E140NFV.DATEMI, E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV ASC";
        System.out.println(sqlSelect);
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

    public List<SucataAnalises> getSucataAnalisessInd(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataAnalises> resultado = new ArrayList<SucataAnalises>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E140NFV.CODEMP EMPRESA,\n"
                + "       MAX(E140NFV.DATEMI) DATEMI,\n"
                + "       MAX(E140NFV.CODFIL) FILIAL,\n"
                + "       MAX(E140NFV.NUMNFV) NOTA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       MAX(E140IPV.CODSNF) SERIE_NOTA,\n"
                + "       MAX(E140IPV.CODTPR) TABELA,\n"
                + "       MAX(E140NFV.DATEMI) EMISSAO_NOTA,\n"
                + "       NULL EMISSAO_PEDIDO,\n"
                + "       NULL FECHAMENTO_PEDIDO,\n"
                + "       MAX(SUC.USU_DATGER) EMISSAO_SUCATA,\n"
                + "       MAX(E140NFV.USU_INDSUC) GERAR_MINUTA,\n"
                + "       MAX(E140NFV.CODCLI) CLIENTE,\n"
                + "       MAX(E085CLI.NOMCLI) NOME,\n"
                + "       MAX(E085CLI.SIGUFS) ESTADO,\n"
                + "       MAX(E085CLI.CIDCLI) CIDADE,\n"
                + "       MAX(E140NFV.PESBRU) PESO_BRUTO_NOTA,\n"
                + "       AVG(SUC.USU_PESFAT) PESO_SUCATA,\n"
                + "       SUM(E140IPV.QTDFAT * E075PRO.PESLIQ) PESO_ITEM_NOTA,\n"
                + "       SUM(E140IPV.QTDFAT) QUANTIDADE,\n"
                + "       MAX(E140NFV.CODTRA) TRANSPORTADORA,\n"
                + "       MAX(E085CLI.CODGRE) AGRUPAMENTO,\n"
                + "       MAX(E070FIL.NOMFIL) NOME_FILIAL,\n"
                + "       MAX(SUC.USU_CODLAN) LANCAMENTO,\n"
                + "       MAX(SUC.USU_SEQMOV) SEQUENCIA,\n"
                + "       MAX(SUC.USU_NUMOCP) ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_PESORD) PESO_ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_NUMPED) PEDIDO_SUCATA,\n"
                + "       MAX(SUC.USU_TIPMOV) TIPO_SUCATA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       'AUTO' LINHA,\n"
                + "       MAX(E140IPV.CODPRO) CODPRO,\n"
                + "       MAX(IPC.NUMNFC) AS NOTA_FISCAL_ENTRADA,\n"
                + "       MAX(UPPER(TNS.DESTNS)) AS DESCRICAO_TRANSACAO,\n"
                + "       MAX(SUC.USU_TNSPRO) AS PEDIDO_TRANSACAO,\n"
                + "       MAX(UPPER(TNS.USU_GERSUC)) AS GERAR_SUCATA,\n"
                + "       MAX(TNSS.CODTNS) || ' - ' || MAX(UPPER(TNSS.DESTNS)) AS DESCRICAO_TRANSACAO_NF\n"
                + "\n"
                + "  FROM E001TNS,\n"
                + "       E085CLI,\n"
                + "       E070EMP,\n"
                + "       E070FIL,\n"
                + "       E140IPV,\n"
                + "       E075PRO,\n"
                + "       E120PED,\n"
                + "       E140NFV\n"
                + "  LEFT JOIN USU_TSUCMOV SUC\n"
                + "    ON (SUC.USU_CODEMP = E140NFV.CODEMP AND SUC.USU_CODFIL = 1 AND\n"
                + "       SUC.USU_NUMNFV = E140NFV.NUMNFV AND SUC.USU_CODCLI = E140NFV.CODCLI AND\n"
                + "       SUC.USU_DEBCRE = '3 - DEBITO')\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNS\n"
                + "    ON TNS.CODTNS = SUC.USU_TNSPRO\n"
                + "   AND TNS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNSS\n"
                + "    ON TNSS.CODTNS = E140NFV.TNSPRO\n"
                + "   AND TNSS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E440IPC IPC\n"
                + "    ON IPC.CODEMP = 1\n"
                + "   AND IPC.CODFIL = 1\n"
                + "   AND IPC.NUMOCP = SUC.USU_NUMOCP\n"
                + "   AND SUC.USU_NUMOCP > 0\n"
                + "\n"
                + " WHERE ((E140NFV.CODEMP = E001TNS.CODEMP) AND\n"
                + "       ((E140NFV.TNSPRO = E001TNS.CODTNS) OR\n"
                + "       (E140NFV.TNSSER = E001TNS.CODTNS)))\n"
                + "   AND ((E140NFV.CODEMP = 1))\n"
                + "   AND ((E140NFV.CODFIL NOT IN (10, 11, 13)))\n"
                + "   AND ((E140NFV.SITNFV = '2'))\n"
                + "   AND ((E140NFV.CODEMP = E070EMP.CODEMP) AND\n"
                + "       (E140NFV.CODEMP = E070FIL.CODEMP) AND\n"
                + "       (E140NFV.CODFIL = E070FIL.CODFIL) AND\n"
                + "       (E140NFV.CODCLI = E085CLI.CODCLI))\n"
                + "   AND (E001TNS.VENFAT = 'S')\n"
                + "      \n"
                + "   AND E140NFV.CODEMP = E140IPV.CODEMP\n"
                + "   AND E140NFV.CODFIL = E140IPV.CODFIL\n"
                + "   AND E140NFV.NUMNFV = E140IPV.NUMNFV\n"
                + "   AND E140NFV.CODSNF = E140IPV.CODSNF\n"
                + "   AND E140NFV.TNSPRO = E140IPV.TNSPRO\n"
                + "   AND E140IPV.CODEMP = E075PRO.CODEMP\n"
                + "   AND E140IPV.CODPRO = E075PRO.CODPRO\n"
                + "   AND E140NFV.CODEMP = E120PED.CODEMP\n"
                + "   AND E140IPV.CODFIL = E120PED.CODFIL\n"
                + "   AND E140IPV.NUMPED = E120PED.NUMPED\n"
                + "   AND E120PED.TNSPRO = '90124'\n";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\nGROUP BY E140NFV.DATEMI, E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV";
        sqlSelect += "\nORDER BY E140NFV.DATEMI, E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV ASC";
        System.out.println(sqlSelect);
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
    public List<SucataAnalises> getSucataAnalisesGeral(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataAnalises> resultado = new ArrayList<SucataAnalises>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E140NFV.CODEMP EMPRESA,\n"
                + "       MAX(E140NFV.DATEMI) DATEMI,\n"
                + "       MAX(E140NFV.CODFIL) FILIAL,\n"
                + "       MAX(E140NFV.NUMNFV) NOTA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       MAX(E140IPV.CODSNF) SERIE_NOTA,\n"
                + "       MAX(E140IPV.CODTPR) TABELA,\n"
                + "       MAX(E140NFV.DATEMI) EMISSAO_NOTA,\n"
                + "       NULL EMISSAO_PEDIDO,\n"
                + "       NULL FECHAMENTO_PEDIDO,\n"
                + "       MAX(SUC.USU_DATGER) EMISSAO_SUCATA,\n"
                + "       MAX(E140NFV.USU_INDSUC) GERAR_MINUTA,\n"
                + "       MAX(E140NFV.CODCLI) CLIENTE,\n"
                + "       MAX(E085CLI.NOMCLI) NOME,\n"
                + "       MAX(E085CLI.SIGUFS) ESTADO,\n"
                + "       MAX(E085CLI.CIDCLI) CIDADE,\n"
                + "       MAX(E140NFV.PESBRU) PESO_BRUTO_NOTA,\n"
                + "       AVG(SUC.USU_PESFAT) PESO_SUCATA,\n"
                + "       SUM(E140IPV.QTDFAT * E075PRO.PESLIQ) PESO_ITEM_NOTA,\n"
                + "       SUM(E140IPV.QTDFAT) QUANTIDADE,"
                + "       MAX(E140NFV.CODTRA) TRANSPORTADORA,\n"
                + "       MAX(E085CLI.CODGRE) AGRUPAMENTO,\n"
                + "       MAX(E070FIL.NOMFIL) NOME_FILIAL,\n"
                + "       MAX(SUC.USU_CODLAN) LANCAMENTO,\n"
                + "       MAX(SUC.USU_SEQMOV) SEQUENCIA,\n"
                + "       MAX(SUC.USU_NUMOCP) ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_PESORD) PESO_ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_NUMPED) PEDIDO_SUCATA,\n"
                + "       MAX(SUC.USU_TIPMOV) TIPO_SUCATA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       max(case e075pro.codori\n"
                + "             when 'BA' then\n"
                + "              'AUTO'\n"
                + "             else\n"
                + "              'MOTO'\n"
                + "           end) as LINHA,\n"
                + "       MAX(IPC.NUMNFC) AS NOTA_FISCAL_ENTRADA,\n"
                + "       MAX(UPPER(TNS.DESTNS)) AS DESCRICAO_TRANSACAO,\n"
                + "       MAX(SUC.USU_TNSPRO) AS PEDIDO_TRANSACAO,\n"
                + "       MAX(UPPER(TNS.USU_GERSUC)) AS GERAR_SUCATA,\n"
                + "       MAX(TNSS.CODTNS) || ' - ' || MAX(UPPER(TNSS.DESTNS)) AS DESCRICAO_TRANSACAO_NF\n"
                + "\n"
                + "  FROM E001TNS, E085CLI, E070EMP, E070FIL, E140IPV, E075PRO, E140NFV\n"
                + "  LEFT JOIN USU_TSUCMOV SUC\n"
                + "    ON (SUC.USU_CODEMP = E140NFV.CODEMP AND SUC.USU_CODFIL = 1 AND\n"
                + "       SUC.USU_NUMNFV = E140NFV.NUMNFV AND SUC.USU_CODCLI = E140NFV.CODCLI AND\n"
                + "       SUC.USU_DEBCRE = '3 - DEBITO')\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNS\n"
                + "    ON TNS.CODTNS = SUC.USU_TNSPRO\n"
                + "   AND TNS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNSS\n"
                + "    ON TNSS.CODTNS = E140NFV.TNSPRO\n"
                + "   AND TNSS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E440IPC IPC\n"
                + "    ON IPC.CODEMP = 1\n"
                + "   AND IPC.CODFIL = 1\n"
                + "   AND IPC.NUMOCP = SUC.USU_NUMOCP\n"
                + "   AND SUC.USU_NUMOCP > 0\n"
                + "\n"
                + " WHERE ((E140NFV.CODEMP = E001TNS.CODEMP) AND\n"
                + "       ((E140NFV.TNSPRO = E001TNS.CODTNS) OR\n"
                + "       (E140NFV.TNSSER = E001TNS.CODTNS)))\n"
                + "   AND ((E140NFV.CODEMP = 1))\n"
                + "   AND ((E140NFV.SITNFV = '2'))\n"
                + "   AND ((E140NFV.CODEMP = E070EMP.CODEMP) AND\n"
                + "       (E140NFV.CODEMP = E070FIL.CODEMP) AND\n"
                + "       (E140NFV.CODFIL = E070FIL.CODFIL) AND\n"
                + "       (E140NFV.CODCLI = E085CLI.CODCLI))\n"
                + "   AND (E001TNS.VENFAT = 'S')\n"
                + "      \n"
                + "   AND E140NFV.CODEMP = E140IPV.CODEMP\n"
                + "   AND E140NFV.CODFIL = E140IPV.CODFIL\n"
                + "   AND E140NFV.NUMNFV = E140IPV.NUMNFV\n"
                + "   AND E140NFV.CODSNF = E140IPV.CODSNF\n"
                + "   AND E140NFV.TNSPRO = E140IPV.TNSPRO\n"
                + "      \n"
                + "   AND E140IPV.CODEMP = E075PRO.CODEMP\n"
                + "   AND E140IPV.CODPRO = E075PRO.CODPRO\n"
                + "   AND E075PRO.CODORI IN ('BA','BM')\n"
                + " ";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\nGROUP BY E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV";

        System.out.println("Sql  \n" + sqlSelect);

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
    public List<SucataAnalises> getSucataAnalisesEntrada(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataAnalises> resultado = new ArrayList<SucataAnalises>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT MAX(e440nfc.CODEMP) EMPRESA,\n"
                + "       MAX(e440nfc.DATENT) DATEMI,\n"
                + "       MAX(e440nfc.CODFIL) FILIAL,\n"
                + "       MAX(e440nfc.USU_LANSUC) LANCAMENTO,\n"
                + "       MAX(e440nfc.NUMNFC) NOTA,\n"
                + "       sum(e440IPC.QTDREC) PESO_BRUTO_NOTA,\n"
                + "       MAX(e440nfc.CODFOR) CLIENTE,\n"
                + "       MAX(E095FOR.NOMFOR) NOME,\n"
                + "       MAX(e440IPC.CODPRO) CODPRO, \n"
                + "       0 PEDIDO_NOTA,\n"
                + "       'N51' SERIE_NOTA,\n"
                + "       '' TABELA,\n"
                + "       MAX(e440nfc.DATENT) EMISSAO_NOTA,\n"
                + "       NULL EMISSAO_PEDIDO,\n"
                + "       NULL FECHAMENTO_PEDIDO,\n"
                + "       NULL EMISSAO_SUCATA,\n"
                + "       0 GERAR_MINUTA,\n"
                + "       \n"
                + "       MAX(E095FOR.SIGUFS) ESTADO,\n"
                + "       MAX(E095FOR.CIDFOR) CIDADE,\n"
                + "       \n"
                + "       0 PESO_SUCATA,\n"
                + "       0 PESO_ITEM_NOTA,\n"
                + "       1 QUANTIDADE,\n"
                + "       0 TRANSPORTADORA,\n"
                + "       '' AGRUPAMENTO,\n"
                + "       '' NOME_FILIAL,\n"
                + "       \n"
                + "       0 SEQUENCIA,\n"
                + "       MAX(e440IPC.NUMOCP) ORDEM_COMPRA,\n"
                + "       0 PESO_ORDEM_COMPRA,\n"
                + "       0 PEDIDO_SUCATA,\n"
                + "       '4 - CREDITO' TIPO_SUCATA,\n"
                + "       0 PEDIDO_NOTA,\n"
                + "       ' ' LINHA,\n"
                + "       MAX(e440IPC.NUMNFC) AS NOTA_FISCAL_ENTRADA,\n"
                + "       '' AS DESCRICAO_TRANSACAO,\n"
                + "       '' AS PEDIDO_TRANSACAO,\n"
                + "       '' AS GERAR_SUCATA,\n"
                + "       '' AS DESCRICAO_TRANSACAO_NF\n"
                + "\n"
                + "  FROM e440IPC, e075pro, e440nfc\n"
                + "  LEFT JOIN E095FOR\n"
                + "    ON (E095FOR.CODFOR = e440nfc.CODFOR)\n"
                + " WHERE 0 = 0\n"
                + "   AND e440IPC.CODEMP = e440nfc.CODEMP\n"
                + "   AND e440IPC.CODFIL = e440nfc.CODFIL\n"
                + "   AND e440IPC.numnfc = e440nfc.numnfc\n"
                + "   AND e440IPC.codfor = e440nfc.codfor\n"
                + "   and e440IPC.codemp = e075pro.codemp\n"
                + "   and e440IPC.CodPro = e075pro.codpro\n"
                + "   and e440IPC.CodPro in ('1001','REREM0010','P2CBI0001')\n"
                + "   and e440IPC.Codfil not in (10,11,12,13,14)";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\n GROUP BY e440nfc.DATENT, e440nfc.NUMNFC,  e440IPC.CODPRO\n"
                + " ORDER BY e440nfc.DATENT, e440nfc.NUMNFC,  e440IPC.CODPRO desc";
        System.out.println("Sql  \n" + sqlSelect);

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
    public List<SucataAnalises> getSucataAnalisesCd(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataAnalises> resultado = new ArrayList<SucataAnalises>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E140NFV.CODEMP EMPRESA,\n"
                + "       MAX(E140NFV.DATEMI) DATEMI,\n"
                + "       MAX(E140NFV.CODFIL) FILIAL,\n"
                + "       MAX(E140NFV.NUMNFV) NOTA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       MAX(E120PED.TNSPRO) PEDIDO_TRANSACAO,\n"
                + "       MAX(E140IPV.CODPRO) CODPRO,\n"
                + "       MAX(E140IPV.CODSNF) SERIE_NOTA,\n"
                + "       MAX(E140IPV.CODTPR) TABELA,\n"
                + "       MAX(E140NFV.DATEMI) EMISSAO_NOTA,\n"
                + "       NULL EMISSAO_PEDIDO,\n"
                + "       NULL FECHAMENTO_PEDIDO,\n"
                + "       MAX(SUC.USU_DATGER) EMISSAO_SUCATA,\n"
                + "       MAX(E140NFV.USU_INDSUC) GERAR_MINUTA,\n"
                + "       MAX(E140NFV.CODCLI) CLIENTE,\n"
                + "       MAX(E085CLI.NOMCLI) NOME,\n"
                + "       MAX(E085CLI.SIGUFS) ESTADO,\n"
                + "       MAX(E085CLI.CIDCLI) CIDADE,\n"
                + "       MAX(E140NFV.PESBRU) PESO_BRUTO_NOTA,\n"
                + "       AVG(SUC.USU_PESFAT) PESO_SUCATA,\n"
                + "       SUM(E140IPV.QTDFAT * E075PRO.PESLIQ) PESO_ITEM_NOTA,\n"
                + "       SUM(E140IPV.QTDFAT) QUANTIDADE,\n"
                + "       MAX(E140NFV.CODTRA) TRANSPORTADORA,\n"
                + "       MAX(E085CLI.CODGRE) AGRUPAMENTO,\n"
                + "       MAX(E070FIL.NOMFIL) NOME_FILIAL,\n"
                + "       MAX(SUC.USU_CODLAN) LANCAMENTO,\n"
                + "       MAX(SUC.USU_SEQMOV) SEQUENCIA,\n"
                + "       MAX(SUC.USU_NUMOCP) ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_PESORD) PESO_ORDEM_COMPRA,\n"
                + "       MAX(SUC.USU_NUMPED) PEDIDO_SUCATA,\n"
                + "       MAX(SUC.USU_TIPMOV) TIPO_SUCATA,\n"
                + "       MAX(E140IPV.NUMPED) PEDIDO_NOTA,\n"
                + "       'AUTO' LINHA,\n"
                + "       MAX(IPC.NUMNFC) AS NOTA_FISCAL_ENTRADA,\n"
                + "       MAX(UPPER(TNS.DESTNS)) AS DESCRICAO_TRANSACAO,\n"
                + "       MAX(SUC.USU_TNSPRO) AS PEDIDO_TRANSACAO,\n"
                + "       MAX(UPPER(TNS.USU_GERSUC)) AS GERAR_SUCATA,\n"
                + "       MAX(TNSS.CODTNS) || ' - ' || MAX(UPPER(TNSS.DESTNS)) AS DESCRICAO_TRANSACAO_NF\n"
                + "\n"
                + "  FROM E001TNS,\n"
                + "       E085CLI,\n"
                + "       E070EMP,\n"
                + "       E070FIL,\n"
                + "       E140IPV,\n"
                + "       e120ped,\n"
                + "       E075PRO,\n"
                + "       E140NFV\n"
                + "  LEFT JOIN USU_TSUCMOV SUC\n"
                + "    ON (SUC.USU_CODEMP = E140NFV.CODEMP AND SUC.USU_CODFIL = 11 AND\n"
                + "       SUC.USU_NUMNFV = E140NFV.NUMNFV AND SUC.USU_CODCLI = E140NFV.CODCLI AND\n"
                + "       SUC.USU_DEBCRE = '3 - DEBITO')\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNS\n"
                + "    ON TNS.CODTNS = SUC.USU_TNSPRO\n"
                + "   AND TNS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E001TNS TNSS\n"
                + "    ON TNSS.CODTNS = E140NFV.TNSPRO\n"
                + "   AND TNSS.CODEMP = 1\n"
                + "\n"
                + "  LEFT JOIN E440IPC IPC\n"
                + "    ON IPC.CODEMP = 1\n"
                + "   AND IPC.CODFIL = 11\n"
                + "   AND IPC.NUMOCP = SUC.USU_NUMOCP\n"
                + "   AND SUC.USU_NUMOCP > 0\n"
                + "\n"
                + " WHERE ((E140NFV.CODEMP = E001TNS.CODEMP) AND\n"
                + "       ((E140NFV.TNSPRO = E001TNS.CODTNS) OR\n"
                + "       (E140NFV.TNSSER = E001TNS.CODTNS)))\n"
                + "   AND ((E140NFV.CODEMP > 0))\n"
                + "   AND ((E140NFV.CODFIL > 0))\n"
                + "   AND ((E140NFV.SITNFV = '2'))\n"
                + "   AND ((E140NFV.CODEMP = E070EMP.CODEMP) AND\n"
                + "       (E140NFV.CODEMP = E070FIL.CODEMP) AND\n"
                + "       (E140NFV.CODFIL = E070FIL.CODFIL) AND\n"
                + "       (E140NFV.CODCLI = E085CLI.CODCLI))\n"
                + "   AND (E001TNS.VENFAT = 'S')\n"
                + "      \n"
                + "   AND E140NFV.CODEMP = E140IPV.CODEMP\n"
                + "   AND E140NFV.CODFIL = E140IPV.CODFIL\n"
                + "   AND E140NFV.NUMNFV = E140IPV.NUMNFV\n"
                + "   AND E140NFV.CODSNF = E140IPV.CODSNF\n"
                + "   AND E140NFV.TNSPRO = E140IPV.TNSPRO\n"
                + "      \n"
                + "   AND E140IPV.CODEMP = E120PED.CODEMP\n"
                + "   AND E140IPV.CODFIL = E120PED.CODFIL\n"
                + "   AND E140IPV.NUMPED = E120PED.NUMPED\n"
                + "      \n"
                + "   AND E140IPV.CODEMP = E075PRO.CODEMP\n"
                + "   AND E140IPV.CODPRO = E075PRO.CODPRO\n"
                + "   AND E075PRO.CODORI IN ('BA')\n"
                + " ";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\nGROUP BY E140NFV.DATEMI, E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV";
        sqlSelect += "\nORDER BY E140NFV.DATEMI, E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODCLI, E140NFV.NUMNFV ASC";
        System.out.println("Sql  \n" + sqlSelect);

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

}
