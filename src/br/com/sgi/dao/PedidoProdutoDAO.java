/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.PedidoHub;
import br.com.sgi.bean.PedidoHubProduto;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.sgi.interfaces.InterfacePedidoHubDAO;
import java.sql.PreparedStatement;

/**
 *
 * @author jairosilva Teste Jairo
 */
public class PedidoProdutoDAO implements InterfacePedidoHubDAO<PedidoHubProduto> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private List<PedidoHubProduto> getLista(ResultSet rs) throws SQLException, ParseException {

        NotaFiscalDAO dao = new NotaFiscalDAO();
        List<PedidoHubProduto> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            PedidoHubProduto e = new PedidoHubProduto();
            e.setEmpresa(rs.getInt("codemp"));
            e.setFilial(rs.getInt("codfil"));
            e.setPedido(rs.getInt("numped"));

            e.setCidade(rs.getString("cidcli"));
            e.setCliente(rs.getInt("codcli"));
            e.setClientenome(rs.getString("nomcli"));
            e.setDataenvio(new Date());
            e.setDataretorno(null);
            e.setDataseparacao(null);
            e.setDescricao(rs.getString("despro"));
            e.setEstado(rs.getString("sigufs"));
            e.setId(0);

            e.setProduto(rs.getString("codpro"));
            e.setQuantidade(rs.getDouble("qtdped"));
            e.setQuantidade_lidos(0.0);
            e.setQuantidade_saldo(e.getQuantidade());
            e.setSituacao("ENVIADO_HUB");
            e.setSequenciaitem(rs.getInt("seqipd"));
            e.setSucata_id(rs.getInt("sucata_id"));
            e.setPesosucata(rs.getDouble("pesosucata")); // PESO GRAVADO NO CONTROLE DE SUCATA
            e.setPesoproduto(rs.getDouble("pesoproduto"));
            e.setPesopedido(rs.getDouble("pesopedido"));
            e.setOperacao("ENVIADO PARA A BROS LOGÍSTICA");
            e.setRepresentante(rs.getInt("codrep"));
            e.setCodigoacesso(0); // acessar o app

            e.setQuantidadeVolume(0.0);
            e.setQuantidadeCaixa(rs.getDouble("qtdcxa"));
            if (e.getQuantidadeCaixa() > 0) {
                e.setQuantidadeVolume(e.getQuantidade() / e.getQuantidadeCaixa());
             //   e.setQuantidadeVolume(Math.round(e.getQuantidadeVolume()));
                // int roundedNumA = (int)Math.ceil(e.getQuantidadeVolume());
                e.setTotalVolumes((arredondar(e.getQuantidadeVolume())));
            }

            //  integrarPedidoApp(resultado);
            resultado.add(e);
        }
        return resultado;
    }

    public int arredondar(double num) {
        if ((num - (int) num) > 0.0) {
            num += 1;
        }
        return (int) num;
    }

    @Override
    public List<PedidoHubProduto> getPedidoProdutos(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<PedidoHubProduto> resultado = new ArrayList<PedidoHubProduto>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(ped.codemp) as codemp,\n"
                + "       max(ped.codfil) as codfil,\n"
                + "       max(ped.numped) as numped,\n"
                + "       max(ped.datemi) as datemi,\n"
                + "       max(ped.codrep) as codrep,\n"
                + "       max((ped.datemi) + 2) as data_no_cliente,\n"
                + "       max(ped.tnspro) as tnspro,\n"
                + "       max(ped.codcli) as codcli,\n"
                + "       max(pro.usu_qtdcxa) as qtdcxa,\n"
                + "       sum(ipd.qtdped) as qtdped,\n"
                + "       avg(pro.pesliq) as pesoproduto,\n"
                + "       avg(pro.pesliq) * sum(ipd.qtdped) as pesopedido,\n"
                + "       \n"
                + "       max(cli.nomcli) as nomcli,\n"
                + "       max(cli.cidcli) as cidcli,\n"
                + "       max(cli.sigufs) as sigufs,\n"
                + "       max(ipd.codpro) as codpro,\n"
                + "       max(pro.despro) as despro,\n"
                + "       max(ipd.seqipd) as seqipd,\n"
                + "       max(suc.usu_codlan) as sucata_id,\n"
                + "       sum(suc.usu_pesped) as pesosucata\n"
                + "\n"
                + "  from e120ipd ipd, e085cli cli, e075pro pro, e120ped ped\n"
                + "  left join usu_tsuccab suc\n"
                + "    on (suc.usu_codemp = ped.codemp and suc.usu_codfil = ped.codfil and\n"
                + "       suc.usu_numped = ped.numped and suc.usu_codcli = ped.codcli and\n"
                + "       suc.usu_debcre = '3 - DEBITO')\n"
                + " where ped.codemp = 1\n"
                + "   and ped.codfil = 1\n"
                + "   and ped.sitped in (1, 2, 4)\n"
                + "   and ped.datemi >= '01/11/2021'\n"
                + "      -- and ped.codrep = 9006\n"
                + "      --and ped.usu_libped = 'S'\n"
                + "   and ped.codemp = ipd.codemp\n"
                + "   and ped.codfil = ipd.codfil\n"
                + "   and ped.numped = ipd.numped\n"
                + "   and ped.tnspro = ipd.tnspro\n"
                + "   and ped.codcli = cli.codcli\n"
                + "   and ipd.codemp = pro.codemp\n"
                + "   and ipd.codpro = pro.codpro\n"
                + " ";

        sqlSelect += PESQUISA;
        sqlSelect += "  group by ped.numped, ipd.codpro "
                + " order by ped.numped ";

        System.out.print(" " + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs);

            pst.close();
            rs.close();
            // integrar os pedidos no app

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
    public boolean integrarPedidoApp(PedidoHub t, int qtdreg, int contador) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into pedidohub\n"
                + "(empresa,filial, pedido,notafiscal, cliente, \n"
                + "conta_id, sucata_id,codigoacesso,situacao, dataenvio, \n"
                + "dataseparacao,dataretorno,dataentrega, quantidade, pesosucata, \n"
                + "pesorecebido, pesosaldo,endereco, cidade, estado, \n"
                + "clientenome, serienota, operacao, situacaoserie,integrar, \n"
                + "integrarnota, id)\n"
                + "values\n"
                + " (?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?)";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);

            //empresa,filial, pedido,notafiscal, cliente, 
            pst.setInt(1, t.getEmpresa());
            pst.setInt(2, t.getFilial());
            pst.setInt(3, t.getPedido());
            pst.setInt(4, t.getNotafiscal());
            pst.setInt(5, t.getCliente());
            //conta_id, sucata_id,codigoacesso,situacao, dataenvio,
            pst.setInt(6, t.getConta_id());
            pst.setInt(7, t.getSucata_id());
            pst.setInt(8, t.getCodigoacesso());
            pst.setString(9, t.getSituacao());
            pst.setDate(10, new java.sql.Date(t.getDataenvio().getTime()));

            //dataseparacao,dataretorno,dataentrega, quantidade, pesosucata
            pst.setDate(11, null);
            pst.setDate(12, null);
            pst.setDate(13, null);
            pst.setDouble(14, t.getQuantidade());
            pst.setDouble(15, t.getPesosucata());

            //"pesorecebido, pesosaldo,endereco, cidade, estado,
            pst.setDouble(16, t.getPesorecebido());
            pst.setDouble(17, t.getPesosaldo());
            pst.setString(18, t.getEndereco());
            pst.setString(19, t.getCidade());
            pst.setString(20, t.getEstado());

            //clientenome, serienota, operacao, situacaoserie,integrar,
            pst.setString(21, t.getClientenome());
            pst.setString(22, t.getSerienota());
            pst.setString(23, t.getOperacao());
            pst.setString(24, t.getSituacaoserie());
            pst.setString(25, t.getIntegrar());

            //integrarnota, id
            pst.setString(26, t.getIntegrarnota());
            pst.setInt(27, t.getId());
            pst.executeUpdate();

            pst.close();

            if (qtdreg == contador) {
                JOptionPane.showMessageDialog(null, "Minuta liberada para Entrega",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
            }

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

    @Override
    public boolean integrarPedidoProdutoApp(List<PedidoHubProduto> lista) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
