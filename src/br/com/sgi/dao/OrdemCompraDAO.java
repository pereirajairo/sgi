/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.OrdemCompra;
import br.com.sgi.bean.OrdemCompraItens;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Produto;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import br.com.sgi.util.UtilDatas;
import br.com.sgi.interfaces.InterfaceOdemCompraDAO;
import java.sql.PreparedStatement;

/**
 *
 * @author jairosilva
 */
public class OrdemCompraDAO implements InterfaceOdemCompraDAO<OrdemCompraItens> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(OrdemCompraItens e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

    }

    private List<OrdemCompraItens> getLista(ResultSet rs) throws SQLException, ParseException {
        List<OrdemCompraItens> resultado = new ArrayList<OrdemCompraItens>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            OrdemCompraItens e = new OrdemCompraItens();
            e.setComplementoProdutoOrdem(rs.getString("complementoprodutoordem"));
            e.setEmpresa(rs.getInt("empresa"));
            e.setFilial(rs.getInt("filial"));
            e.setNumeroOrdemCompra(rs.getInt("numeroordemcompra"));
            e.setPesoBruto(rs.getDouble("pesoliquidoordem"));
            e.setPesoLiquido(rs.getDouble("pesoliquidoordem"));
            e.setQuantidadeAberta(rs.getDouble("quantidadeaberta"));
            e.setQuantidadePedida(rs.getDouble("quantidadepedida"));
            e.setQuantidadeRecebida(rs.getDouble("quantidaderecebida"));
            e.setSequenciaItem(rs.getInt("situacaoprodutoordem"));
            e.setUnidadeMedida(rs.getString("unidademedida"));

            Produto pro = new Produto();
            pro.setCodigoproduto(rs.getString("codigoproduto"));
            pro.setDescricaoproduto(rs.getString("descricaoproduto"));
            pro.setFamiliaproduto(rs.getString("familiaproduto"));
            pro.setEmpresa(rs.getInt("empresa"));
            e.setProduto(pro);

            OrdemCompra oc = new OrdemCompra();
            oc.setCodigoFornecedor(rs.getInt("codigofornecedor"));
            oc.setEmpresa(e.getEmpresa());
            oc.setFilial(e.getFilial());
            oc.setNomeFornecedor(rs.getString("nomefornecedor"));
            oc.setNumeroOrdemCompra(e.getNumeroOrdemCompra());
            oc.setSituacaoOrdemCompra(rs.getString("situacaoordem"));
            oc.setSituacaoaprovacao(rs.getString("situacaoaprovacao"));
            e.setOrdemCompra(oc);
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<OrdemCompraItens> getOrdemCompraItenss(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<OrdemCompraItens> resultado = new ArrayList<OrdemCompraItens>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(ocp.codemp) empresa,\n"
                + "       max(ocp.codfil) filial,\n"
                + "       max(ocp.codfor) codigofornecedor,\n"
                + "       max(ocp.numocp) numeroordemcompra,\n"
                + "       max(ocp.datemi) dataemissao,\n"
                + "       max(ocp.sitocp) situacaoordem,\n"
                + "       max(ocp.sitapr) situacaoaprovacao,\n"
                + "       max(ipo.sitipo) situacaoprodutoordem,\n"
                + "       max(ipo.codpro) codigoproduto,\n"
                + "       sum(ipo.qtdped) quantidadepedida,\n"
                + "       sum(ipo.qtdrec) quantidaderecebida,\n"
                + "       sum(ipo.qtdabe) quantidadeaberta,\n"
                + "       max(ipo.unimed) unidademedida,\n"
                + "       sum(ipo.pesliq) pesoliquidoordem,\n"
                + "       sum(ipo.pesbru) pesobrutoordem,\n"
                + "       max(upper(ipo.cplipo)) complementoprodutoordem,\n"
                + "       max(upper(pro.despro)) descricaoproduto,\n"
                + "       sum(pro.pesliq) pesoliquido,\n"
                + "       sum(pro.pesbru) pesobruto,\n"
                + "       max(pro.codfam) familiaproduto,\n"
                + "       max(upper(forn.nomfor)) nomefornecedor,\n"
                + "       max(forn.clifor) clientefornecedor,\n"
                + "       max(forn.codcli) codigoCliente\n"
                + "  from e420ipo ipo, e420ocp ocp, e075pro pro, e095for forn\n"
                + " where  ipo.codemp = ocp.codemp\n"
                + "   and ipo.codfil = ocp.codfil\n"
                + "   and ipo.numocp = ocp.numocp\n"
                // + "   and ocp.numocp in (" + PESQUISA + ")\n"
                + "   and ipo.codemp = pro.codemp\n"
                + "   and ipo.codpro = pro.codpro\n"
                + "   and ocp.sitapr = 'APR'\n"
                + "   and ocp.sitocp in (1, 2)\n"
                + "   and ocp.codfor = forn.codfor\n";

        sqlSelect += PESQUISA;

        sqlSelect += " group by ipo.numocp, ipo.codpro ";

        System.out.println("br.com.recebimento.dao.OrdemCompraDAO.getOrdemCompraItenss()" + sqlSelect);

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
    public OrdemCompraItens getOrdemCompraItens(String PESQUISA_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "Select * from usu_tintcarga where usu_codemp > 0 ";
        sqlSelect += PESQUISA;

        sqlSelect += "order by usu_datent desc";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<OrdemCompraItens> resultado = new ArrayList<OrdemCompraItens>();
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
    public List<OrdemCompraItens> getOrdemCompraColeta(String PESQUISA_POR, String PESQUISA, Pedido pedido) throws SQLException {
        List<OrdemCompraItens> resultado = new ArrayList<OrdemCompraItens>();
        OrdemCompraItens ord = new OrdemCompraItens();
        ord.setOrdemCompra(new OrdemCompra());

        Produto pro = new Produto();
        ord.setNumeroOrdemCompra(0);

        if (PESQUISA_POR.isEmpty()) {
            pro.setCodigoproduto("AUTO");
            pro.setDescricaoproduto("COLETA DE AUTO");
            ord.getOrdemCompra().setNomeFornecedor("COLETA DE BATERIAS AUTO");

            if (PESQUISA.equals("CM")) {
                pro.setCodigoproduto("MOTO");
                pro.setDescricaoproduto("COLETA DE MOTO");
                ord.getOrdemCompra().setNomeFornecedor("COLETA DE BATERIAS MOTO");
            }
            if (PESQUISA.equals("CI")) {
                pro.setCodigoproduto("INTERNO");
                pro.setDescricaoproduto("PESAGEM INTERNA");
                ord.getOrdemCompra().setNomeFornecedor("PESAGEM  INTERNA");
            }
            if (PESQUISA.equals("CMETAIS")) {
                pro.setCodigoproduto(pedido.getProduto());
                pro.setDescricaoproduto(pedido.getCadProduto().getDescricaoproduto());

            }
            if (PESQUISA.equals("VMETAIS")) {
                pro.setCodigoproduto(pedido.getProduto());
                pro.setDescricaoproduto(pedido.getCadProduto().getDescricaoproduto());

            }
        } else {
            pro.setCodigoproduto(PESQUISA_POR);
            pro.setDescricaoproduto(PESQUISA);
            ord.getOrdemCompra().setNomeFornecedor("NÃO INFORMADO");
        }

        ord.setProduto(pro);
        ord.setEmpresa(1);
        ord.setFilial(1);
        ord.setQuantidadePedida(0.0);
        ord.getOrdemCompra().setCodigoFornecedor(0);
        if (PESQUISA.equals("CMETAIS")||PESQUISA.equals("VMETAIS")) {
            OrdemCompra oc = new OrdemCompra();
            oc.setCodigoFornecedor(pedido.getCliente());
            oc.setNomeFornecedor(pedido.getCadCliente().getNome());
            ord.setOrdemCompra(oc);
            ord.setQuantidadePedida(pedido.getQuantidade());
        }

        ord.getOrdemCompra().setSituacaoaprovacao("APR");
        ord.setPesoBruto(0.0);

        ord.setSequenciaItem(1);
        ord.setUnidadeMedida("");
        resultado.add(ord);
        return resultado;
    }

    @Override
    public List<OrdemCompra> getOrdemCompras(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<OrdemCompra> resultado = new ArrayList<OrdemCompra>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        this.utilDatas = new UtilDatas();
        String sqlSelect = "select max(ocp.codemp) as codemp,\n"
                + "       max(ocp.codfil) as codfil,\n"
                + "       max(ocp.numocp) as numocp,\n"
                + "       max(ocp.codfor) as codfor,\n"
                + "       max(ocp.sitocp) as sitocp,\n"
                + "       max(ocp.sitapr) as sitapr,\n"
                + "       max(ocp.tnspro) as tnspro,\n"
                + "       max(ocp.tnsser) as tnsser,\n"
                + "       max(ocp.datemi) as datemi,\n"
                + "       max(forn.nomfor) as nomfor,\n"
                + "       max(forn.apefor) as apefor,\n"
                + "       max(forn.cidfor) as cidfor,\n"
                + "       max(forn.sigufs) as sigufs,\n"
                + "       max(forn.cgccpf) as cgccpf,\n"
                + "       max(ipo.codpro) as codpro,\n"
                + "       sum(ipo.qtdped) as qtdped,\n"
                + "       sum(ipo.qtdabe) as qtdabe\n"
                + "  from e420ocp ocp, e420ipo ipo, e095for forn\n"
                + " where ocp.codemp = 1\n"
                + "   and ocp.codfil = 1\n"
                + "   and ocp.sitapr = 'APR'\n"
                + "   and ocp.datemi >= '" + PESQUISA_POR + "'\n"
                + "   and ocp.sitocp in (1, 2)\n"
                + "   and ipo.sitipo in (1, 2)\n"
                + "   and ocp.codfor = forn.codfor\n"
                + "   and ocp.codemp = ipo.codemp\n"
                + "   and ocp.codfil = ipo.codfil\n"
                + "   and ocp.numocp = ipo.numocp\n"
                + "   and ipo.qtdped > 0\n"
                + "   ";

        sqlSelect += PESQUISA;

        sqlSelect += " group by ocp.codemp, ocp.codfil, ocp.codfor, ocp.numocp \n"
                + " order by datemi desc";

        System.out.println("br.com.recebimento.dao.OrdemCompraDAO.getOrdemCompras() " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            while (rs.next()) {
                OrdemCompra oc = new OrdemCompra();
                oc.setEmpresa(rs.getInt("codemp"));
                oc.setFilial(rs.getInt("codfil"));
                oc.setDataemissao(utilDatas.converterDateToStr(rs.getDate("datemi")));
                oc.setCodigoFornecedor(rs.getInt("codfor"));
                oc.setNomeFornecedor(rs.getString("nomfor"));
                oc.setNumeroOrdemCompra(rs.getInt("numocp"));
                oc.setApelidoFornecedor(rs.getString("apefor"));
                oc.setCnpj(rs.getString("cgccpf"));
                oc.setQuantidade(rs.getDouble("qtdabe"));
                oc.setCidade(rs.getString("cidfor"));
                oc.setEstado(rs.getString("sigufs"));

                resultado.add(oc);
            }
            //   System.out.println("br.com.recebimento.dao.OrdemCompraDAO.getOrdemCompras() "+sqlSelect);
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
    public boolean alterar(OrdemCompra t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE e420ocp SET \n"
                    + " tnspro=?"
                    + " where codemp=? and codfil=? and numocp=? and codfor =?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, "");
            pst.setInt(2, 1);
            pst.setInt(3, 1);
            pst.setInt(4, t.getNumeroOrdemCompra());
            pst.setInt(5, t.getCodigoFornecedor());

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
}
