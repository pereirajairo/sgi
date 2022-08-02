/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.PedidoHub;
import br.com.sgi.bean.PedidoHubProduto;
import br.com.sgi.bean.PedidoHubSerie;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfacePedidoSerieDAO;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva Teste Jairo
 */
public class PedidoHubDAO implements InterfacePedidoSerieDAO<PedidoHubProduto> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private List<PedidoHub> getLista(ResultSet rs) throws SQLException, ParseException {

        NotaFiscalDAO dao = new NotaFiscalDAO();
        List<PedidoHub> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            PedidoHub e = new PedidoHub();
            e.setDiatransporte(rs.getInt("diatra"));
            e.setDatapedido(rs.getDate("datemi"));

            if (e.getDatapedido() != null) {
                e.setDatapedidoS(this.utilDatas.converterDateToStr(e.getDatapedido()));
                if (e.getDiatransporte() > 0) {
                    e.setDatafaturar(utilDatas.getAdicionarDia(e.getDatapedido(), e.getDiatransporte() + 1));
                } else {
                    e.setDatafaturar(utilDatas.getAdicionarDia(e.getDatapedido(), 1));
                }
                e.setDatafaturarS(this.utilDatas.converterDateToStr(e.getDatafaturar()));

            }
            e.setTransacao(rs.getString("tnspro"));
            e.setLinha(rs.getString("codori"));
            e.setSucata_id(rs.getInt("sucata_id"));
            e.setPesosucata(rs.getDouble("pesosucata"));
            e.setFilial(rs.getInt("codfil"));
            e.setCidade(rs.getString("cidcli"));
            e.setCliente(rs.getInt("codcli"));
            e.setDataenvio(new Date());
            e.setDataretorno(null);
            e.setDataseparacao(null);
            e.setEstado(rs.getString("sigufs"));
            e.setId(0);
            e.setPedido(rs.getInt("numped"));
            e.setQuantidade(rs.getDouble("qtdped"));
            e.setPesopedido(rs.getDouble("pesliq"));
            e.setSituacao("ENVIADO_HUB");
            e.setSituacaoPedido(rs.getString("sitped"));
            if (e.getSituacaoPedido().equals("4")) {
                e.setCadNotaFiscal(dao.getNotaFiscal("nota ", " \nand ipv.codfil = " + e.getFilial() + " \nand ipv.numped = " + e.getPedido()));
            }

            if (e.getCadNotaFiscal() != null) {
                if (e.getCadNotaFiscal().getNotafiscal() > 0) {
                    e.setNotafiscal(e.getCadNotaFiscal().getNotafiscal());
                }
            } else {
                NotaFiscal nf = new NotaFiscal();
                nf.setEmpresa(0);
                nf.setFilial(0);
                nf.setNotafiscal(0);
                nf.setSerie(" ");
                e.setCadNotaFiscal(nf);
            }

            e.setSituacaoMinuta(rs.getString("libmin"));
            if (e.getSituacaoMinuta() == null) {
                e.setSituacaoMinuta("N");
            }
            e.setSituacaoPedidoHub(rs.getString("sitpedhub"));
            if (e.getSituacaoPedidoHub() == null) {
                e.setSituacaoPedidoHub("N");
            }

            e.setLiberadoHub(rs.getString("libped"));
            if (e.getLiberadoHub() == null) {
                e.setLiberadoHub("N");
            }

            if (e.getNotafiscal() > 0) {
                e.setSituacao("FATURADO");
            } else {
                e.setSituacao("FECHADO");
                if (e.getSituacaoMinuta().equals("S")) {
                    e.setSituacao("MINUTA EMITIDA");
                    if (e.getLiberadoHub().equals("S")) {
                        e.setSituacao("LIBERADO HUB");
                    }
                }

            }

            e.setTabelapreco(rs.getString("codtpr"));
            e.setTransportadora(rs.getInt("codtra"));
            e.setCadCliente(new Cliente(e.getCliente(), rs.getString("nomcli"), e.getEstado(), e.getCidade()));
            e.setCadTransportadora(new Transportadora(e.getTransportadora(), rs.getString("nomtra")));

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<PedidoHub> getPedidoHubs(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<PedidoHub> resultado = new ArrayList<PedidoHub>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select max(ped.codemp) as codemp,\n"
                + "       max(ped.codfil) as codfil,\n"
                + "       max(ped.pedblo) as pedblo,\n"
                + "       max(ped.sitped) as sitped,\n"
                + "       max(ped.numped) as numped,\n"
                + "       max(ped.datemi) as datemi,\n"
                + "       max((ped.datemi) + 2) as data_no_cliente,\n"
                + "       max(ped.tnspro) as tnspro,\n"
                + "       max(ped.codcli) as codcli,\n"
                + "       max(ipd.codpro) as codpro,\n"
                + "       max(ipd.codtpr) as codtpr,\n"
                + "       sum(ipd.qtdped) as qtdped,\n"
                + "       sum(ipd.qtdped * pro.pesliq) as pesliq,\n"
                + "       max(cli.nomcli) as nomcli,\n"
                + "       max(cli.cidcli) as cidcli,\n"
                + "       max(cli.sigufs) as sigufs,\n"
                + "       max(ipd.codpro) as codpro,\n"
                + "       max(pro.codori) as codori,\n"
                + "       max(pro.despro) as despro,\n"
                + "       max(ped.usu_libmin) as libmin,\n"
                + "       max(ped.usu_sitpedhub) as sitpedhub,\n"
                + "       max(ped.usu_libped) as libped,\n"
                + "       max(ped.usu_lansuc) as sucata_id,\n"
                + "       avg(suc.usu_pesped) as pesosucata,\n"
                + "       max(ped.codtra) as codtra,\n"
                + "       max(tra.nomtra) as  nomtra,\n"
                + "       max(fil.usu_diatra) diatra\n"
                + "  from e120ipd ipd, e085cli cli, e075pro pro, e120ped ped\n"
                + "  left join usu_tsuccab suc\n"
                + "    on (suc.usu_codemp = ped.codemp and suc.usu_codfil = 1 and\n"
                + "       suc.usu_numped = ped.numped and suc.usu_codcli = ped.codcli and\n"
                + "       suc.usu_debcre = '3 - DEBITO')\n"
                + "       \n"
                + "   left join e073tra tra on (tra.codtra = ped.codtra)\n"
                + "   left join e070fil fil on (fil.codemp = ped.codemp and fil.codfil = ped.codfil) \n"
                + " where ped.codemp = 1\n"
                + "   and ped.codemp = ipd.codemp\n"
                + "   and ped.codfil = ipd.codfil\n"
                + "   and ped.numped = ipd.numped\n"
                + "   and ped.tnspro = ipd.tnspro\n"
                + "   and ped.codcli = cli.codcli\n"
                + "   and ipd.codemp = pro.codemp\n"
                + "   and ipd.codpro = pro.codpro\n"
                + " ";

        sqlSelect += PESQUISA;
        sqlSelect += "  group by  ped.codcli, ped.numped\n"
                + " order by  ped.codcli, ped.numped asc";

        System.out.print(" " + sqlSelect);

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

    public List<PedidoHub> getPedidoClientes(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<PedidoHub> resultado = new ArrayList<PedidoHub>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select max(ped.codemp) as codemp,\n"
                + "       max(ped.codfil) as codfil,\n"
                + "       max(ped.sitped) as sitped,\n"
                + "       max(ped.numped) as numped,\n"
                + "       max(ped.datemi) as datemi,\n"
                + "       max((ped.datemi) + 2) as data_no_cliente,\n"
                + "       max(ped.tnspro) as tnspro,\n"
                + "       max(ped.codcli) as codcli,\n"
                + "       max(ipd.codpro) as codpro,\n"
                + "       sum(ipd.qtdped) as qtdped,\n"
                + "       sum(ipd.qtdped * pro.pesliq) as pesliq,\n"
                + "       max(cli.nomcli) as nomcli,\n"
                + "       max(cli.cidcli) as cidcli,\n"
                + "       max(cli.sigufs) as sigufs,\n"
                + "       max(ipd.codpro) as codpro,\n"
                + "       max(pro.codori) as codori,\n"
                + "       max(pro.despro) as despro,\n"
                + "       max(ped.usu_libmin) as libmin,\n"
                + "       max(ped.usu_sitpedhub) as sitpedhub,\n"
                + "       max(ped.usu_libped) as libped,\n"
                + "       max(ped.usu_lansuc) as sucata_id,\n"
                + "       avg(suc.usu_pesped) as pesosucata,\n"
                + "       max(ped.codtra) as codtra,\n"
                + "       max(tra.nomtra) as  nomtra,\n"
                + "       max(fil.usu_diatra) diatra\n"
                + "  from e120ipd ipd, e085cli cli, e075pro pro, e120ped ped\n"
                + "  left join usu_tsuccab suc\n"
                + "    on (suc.usu_codemp = ped.codemp and suc.usu_codfil = 1 and\n"
                + "       suc.usu_numped = ped.numped and suc.usu_codcli = ped.codcli and\n"
                + "       suc.usu_debcre = '3 - DEBITO')\n"
                + "       \n"
                + "   left join e073tra tra on (tra.codtra = ped.codtra)\n"
                + "   left join e070fil fil on (fil.codemp = ped.codemp and fil.codfil = ped.codfil) \n"
                + " where ped.codemp = 1\n"
                //    + "   and ped.codfil not in (1,10,11,13)\n"
                + "   and ped.sitped in (1, 2, 4,9)\n"
                + "   and ped.tnspro not in ('90124','90112','90113','90123','90222','90223','90126','90133','90134','90132','90170','90127')\n"
                + "   and ped.datemi >= '01/01/2021'\n"
                + "   and ped.codemp = ipd.codemp\n"
                + "   and ped.codfil = ipd.codfil\n"
                + "   and ped.numped = ipd.numped\n"
                + "   and ped.tnspro = ipd.tnspro\n"
                + "   and ped.codcli = cli.codcli\n"
                + "   and ipd.codemp = pro.codemp\n"
                + "   and ipd.codpro = pro.codpro\n"
                + " ";

        sqlSelect += PESQUISA;
        sqlSelect += "  group by ped.datemi, ped.numped\n"
                + " order by ped.datemi, ped.numped desc";

        System.out.println("br.com.recebimento.dao.PedidoHubDAO.getPedidoClientes()\n" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs);

            pst.close();
            rs.close();

            // integrar os pedidos no app
            //  integrarPedidoApp(resultado);
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
    public boolean integrarPedidoApp(List<PedidoHubProduto> lista) throws SQLException {

        PreparedStatement pst = null;

        String sqlInsert = "insert into pedidohub\n"
                + "(cliente, pedido, situacao, dataenvio, dataseparacao,\n"
                + " dataretorno, produto, descricao, quantidade, cidade, \n"
                + " estado, id)\n"
                + "values\n"
                + " (?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?)";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);

            for (PedidoHubProduto p : lista) {

                //cliente, pedido, situacao, dataenvio, dataseparacao
                pst.setInt(1, p.getCliente());
                pst.setInt(2, p.getPedido());
                pst.setString(3, p.getSituacao());
                pst.setDate(4, new java.sql.Date(p.getDataenvio().getTime()));
                pst.setDate(5, null);

                //dataretorno, produto, descricao, quantidade, cidade,
                pst.setDate(6, null);
                pst.setString(7, p.getProduto());
                pst.setString(8, p.getDescricao());
                pst.setDouble(9, p.getQuantidade());
                pst.setString(10, p.getCidade());
                // estado, id
                pst.setString(11, p.getEstado());
                pst.setInt(12, p.getId());
                pst.executeUpdate();

            }
            pst.close();

            JOptionPane.showMessageDialog(null, "SUCESSO: Contas registrado com sucesso",
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

    @Override
    public List<PedidoHubSerie> getPedidoHubSeries(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<PedidoHubSerie> resultado = new ArrayList<PedidoHubSerie>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        int contador = 0;

        String sqlSelect = "select ps.*, ph.* \n"
                + "from pedidoserie ps, pedidohub ph\n"
                + "where ps.pedido_id = ph.id\n"
                + "and ph.situacao = 'ENVIADO_FABRICA'\n"
                + "AND ph.integrar = 'S'";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            while (rs.next()) {
                PedidoHubSerie p = new PedidoHubSerie();
                p.setEmpresa(1);
                p.setFilial(16);
                p.setCliente(rs.getInt("ph.cliente"));
                p.setDataregistro(new Date());
                p.setHub("DAIVID");
                if (contador == 0) {
                    p.setLancamento(proxCodCad());
                    contador = p.getLancamento();
                } else {
                    contador++;
                    p.setLancamento(contador);
                }

                p.setPedido(rs.getInt("ph.pedido"));
                p.setProduto(rs.getString("ph.produto"));
                p.setSerie(rs.getString("ps.serie"));
                p.setSituacao("N");
                resultado.add(p);
            }

            pst.close();
            rs.close();

            // integrar os pedidos no app
            if (resultado != null) {
                if (resultado.size() > 0) {
                    if (!integrarSerieERP(resultado)) {

                    } else {
                        for (PedidoHubSerie ps : resultado) {
                            PedidoHubProduto ph = new PedidoHubProduto();
                            ph.setPedido(ps.getPedido());
                            atualizarSerieIntegrada(ph);
                        }
                    }
                }
            }
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
    public boolean integrarSerieERP(List<PedidoHubSerie> lista) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_tpedserhub\n"
                + "(usu_codemp,usu_codfil,usu_codcli,usu_numped,usu_codlan, \n"
                + "usu_codpro,usu_numsep,usu_sitreg,usu_datreg,usu_pedhub)\n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + "?,?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            for (PedidoHubSerie t : lista) {
                //usu_codemp,usu_codfil,usu_codcli,usu_numped,usu_codlan
                pst.setInt(1, t.getEmpresa());
                pst.setInt(2, t.getFilial());
                pst.setInt(3, t.getCliente());
                pst.setInt(4, t.getPedido());
                pst.setInt(5, t.getLancamento());

                //usu_codpro,usu_numsep,usu_sitreg,usu_datreg,usu_pedhub
                pst.setString(6, t.getProduto());
                pst.setString(7, t.getSerie());
                pst.setString(8, t.getSituacao());
                pst.setDate(9, new java.sql.Date(t.getDataregistro().getTime()));
                pst.setString(10, t.getHub());
                pst.executeUpdate();
            }

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

    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_codlan) + 1), 1) PROX_CODALAN FROM usu_tpedserhub";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("PROX_CODALAN");
            }
            if (codlct == 0) {
                codlct = 1;
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
    public boolean atualizarSerieIntegrada(PedidoHubProduto ph) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update pedidohub set integrar = ? where pedido = ?";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, "N");
            pst.setInt(2, ph.getPedido());

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
    public boolean inserir(PedidoHub t, int qtdreg, int contador) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into pedidohub\n"
                + "(empresa, filial, pedido, notafiscal, cliente, \n"
                + "conta_id, sucata_id, codigoacesso, situacao, dataenvio, \n"
                + "dataseparacao, dataretorno, dataentrega, quantidade, pesosucata, \n"
                + "pesorecebido, pesosaldo, endereco, cidade, estado, \n"
                + "clientenome, serienota, operacao, situacaoserie, integrar, \n"
                + "integrarnota, transacao, id)\n"
                + "values\n"
                + " (?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?,?)";

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
            pst.setString(27, t.getTransacao());
            pst.setInt(28, t.getId());
            pst.executeUpdate();

            pst.close();

//            if (qtdreg == contador) {
//                JOptionPane.showMessageDialog(null, "Minuta liberada para Entrega",
//                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
//            }
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
    public boolean inserirProduto(PedidoHub pedHub, List<PedidoHubProduto> lista, int qtdreg, int contador) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into pedidohubproduto\n"
                + "(empresa,filial, pedido_id, pedido, conta_id, \n"
                + "sequenciaitem, situacaoserie, dataseparacao, produto, descricao, \n"
                + "quantidade, quantidadelido, quantidadesaldo, sucata_id, pesosucata, \n"
                + "pesorecebido, operacao, notafiscal, id)\n"
                + "values\n"
                + " (?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ?,?,?,?)";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            if (lista != null) {
                if (lista.size() > 0) {
                    for (PedidoHubProduto t : lista) {
                        //empresa,filial, pedido_id, pedido, conta_id,
                        pst.setInt(1, t.getEmpresa());
                        pst.setInt(2, t.getFilial());
                        pst.setInt(3, pedHub.getId());
                        pst.setInt(4, t.getPedido());
                        pst.setInt(5, t.getConta_id());
                        //sequenciaitem, situacaoserie, dataseparacao, produto, descricao,
                        pst.setInt(6, t.getSequenciaitem());
                        pst.setString(7, "SEPARAR");
                        pst.setDate(8, new java.sql.Date(pedHub.getDataseparacao().getTime()));
                        pst.setString(9, t.getProduto());
                        pst.setString(10, t.getDescricao());

                        //quantidade, quantidadelido, quantidadesaldo, sucata_id, pesosucata,             
                        pst.setDouble(11, t.getQuantidade());
                        pst.setDouble(12, 0.0);
                        pst.setDouble(13, 0.0);
                        pst.setInt(14, t.getSucata_id());
                        pst.setDouble(15, t.getPesopedido());
                        //"pesorecebido, operacao, notafiscal, id,
                        pst.setDouble(16, 0.0);
                        if (t.getSucata_id() > 0) {
                            pst.setString(17, "COM SUCATA");
                        } else {
                            pst.setString(17, "SEM RETORNO");
                        }
                        pst.setInt(18, t.getNotafiscal());

                        pst.setInt(19, t.getId());

                        pst.executeUpdate();
                    }
                }
            }

            pst.close();

            JOptionPane.showMessageDialog(null, "Produtos liberados para separação",
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

    public int proxCodCadPed() throws SQLException {
        Statement st = null;
        String strSql = "SELECT (MAX(id) + 1) PROX_CODALAN FROM pedidohub";

        Integer codlct = 0;
        try {

            openConnectionMySql();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("PROX_CODALAN");
            }
            if (codlct == 0) {
                codlct = 1;
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
    public boolean atualizar(PedidoHubProduto t, int qtdreg, int contador) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update  pedidohub set \n"
                + "notafiscal=?, situacao=?, integrar=?, sucata_id=?, pesosucata=?, pesorecebido=?,"
                + " serienota=?"
                + " where  empresa=? "
                + " and filial=? "
                + " and pedido=?  ";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);

            pst.setInt(1, t.getNotafiscal());
            pst.setString(2, "FATURADO");
            pst.setString(3, "N");
            pst.setInt(4, t.getSucata_id());
            pst.setDouble(5, t.getPesosucata());
            pst.setDouble(6, 0.0);
            pst.setString(7, t.getSerienota());

            pst.setInt(8, t.getEmpresa());
            pst.setInt(9, t.getFilial());
            pst.setInt(10, t.getPedido());
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
    public boolean liberarParaEntrega(MinutaPedido t, int qtdreg, int contador) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update pedidohub set integrar = ? where pedido = ? ";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, "F");
            pst.setInt(2, t.getUsu_numped());

            pst.executeUpdate();
            pst.close();
            if (qtdreg == contador) {
                JOptionPane.showMessageDialog(null, "Atenção : Pedido(s) Liberado(s) para entrega",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
            }

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

    public boolean atualizarCampoCancelar(MinutaPedido t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE e120ped  SET   \n"
                    + " usu_libped=?, "
                    + " usu_sitpedhub=?   \n"
                    + " WHERE     codcli= ?"
                    + " AND    numped=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, "");
            pst.setString(2, "C");
            pst.setInt(3, t.getUsu_codcli());
            pst.setInt(4, t.getUsu_numped());
            pst.executeUpdate();

//            JOptionPane.showMessageDialog(null, "Pedido Atualizado com sucesso",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    public boolean eliminarSeriesImportadas(MinutaPedido t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "delete USU_TPEDSERHUB  \n"
                    + " WHERE  usu_codemp= ?"
                    + " AND usu_codfil=? "
                    + " AND usu_numped = ?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, t.getUsu_codemp());
            pst.setInt(2, t.getUsu_codfil());
            pst.setInt(3, t.getUsu_numped());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, " Séries liberadas no ERP",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    public boolean CancelarPedidoHub(MinutaPedido t, int qtdreg, int contador) throws SQLException {
        PreparedStatement pst = null;
        boolean retorno = false;
        String sqlInsert = "UPDATE pedidohub set situacao=?, observacaocancelamento=? where pedido=? ";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, "CANCELADO");
            pst.setString(2, t.getUsu_obsmin());
            pst.setInt(3, t.getUsu_numped());

            pst.executeUpdate();
            pst.close();

            retorno = true;
            if (retorno) {
                retorno = removerPedidoProduto(t);
                retorno = removerSeriePedidoProduto(t);
            }

            JOptionPane.showMessageDialog(null, "Atenção: Pedido(s) Cancelados(s) no sistema HUB",
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

    public boolean removerPedidoProduto(MinutaPedido t) throws SQLException {
        boolean retorno = false;
        java.sql.PreparedStatement pst = null;
        String sqlExcluir = "UPDATE pedidohubproduto set situacaoserie='CANCELADO' WHERE pedido = ? ";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlExcluir);
            pst.setInt(1, t.getUsu_numped());
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

    public boolean removerSeriePedidoProduto(MinutaPedido t) throws SQLException {
        boolean retorno = false;
        java.sql.PreparedStatement pst = null;
        String sqlExcluir = "delete from  pedidohubserie  WHERE pedido = ? ";

        try {
            if (t.getUsu_numped() > 0) {
                openConnectionMySql();
                pst = con.prepareStatement(sqlExcluir);
                pst.setInt(1, t.getUsu_numped());

                pst.executeUpdate();
                pst.close();
                retorno = true;
            } else {
                
            }

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
    public List<PedidoHub> getPedidoHubsSucata(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<PedidoHub> resultado = new ArrayList<PedidoHub>();

        java.sql.PreparedStatement pst = null;

        String sqlSelect = "SELECT *\n"
                + "  FROM pedidohub ped\n"
                + " WHERE 0 = 0\n "
                + " and cliente not in (10469)";

        // sqlSelect += " \n and MONTH(ped.dataenvio) = '" + mes + "' \n and YEAR(ped.dataenvio) = '" + ano + "'";
        if (!PESQUISA_POR.isEmpty() && !PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\n order BY ped.id  ";
        ResultSet rs = null;
        System.out.println("sql\n" + sqlSelect);
        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();

            resultado = getListaPedidoHub(rs, "AGRUPADO");

            rs.close();
            pst.close();
        } catch (Exception ex) {

            return null;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
        return resultado;
    }

    private List<PedidoHub> getListaPedidoHub(ResultSet rs, String tipo) throws SQLException {
        List<PedidoHub> resultado = new ArrayList<PedidoHub>();

        while (rs.next()) {
            PedidoHub e = new PedidoHub();

            e.setId(rs.getInt("id"));
            e.setCliente(rs.getInt("cliente"));
            e.setPedido(rs.getInt("pedido"));
            e.setDataenvio(rs.getDate("dataenvio"));
            e.setDataseparacao(rs.getDate("dataseparacao"));
            e.setDataretorno(rs.getDate("dataretorno"));
            e.setDataentrega(rs.getDate("dataentrega"));
            e.setCidade(rs.getString("cidade"));
            e.setEstado(rs.getString("estado"));

            e.setNotafiscal(rs.getInt("notafiscal"));
            e.setClientenome(rs.getString("clientenome"));
            e.setClientenome(e.getClientenome().substring(0, 15));

            e.setSerienota(rs.getString("serienota"));
            e.setEndereco(rs.getString("endereco"));
            e.setEmpresa(rs.getInt("empresa"));
            e.setFilial(rs.getInt("filial"));
            e.setSituacao(rs.getString("situacao"));

            e.setGerarminutacoleta(rs.getString("gerarminutacoleta"));
            e.setDataminuta(rs.getDate("dataminuta"));
            if (e.getGerarminutacoleta().equals("S")) {
                e.setColor_minuta("info");
            }

            e.setQuantidade(rs.getDouble("quantidade"));
            e.setQuantidade_lidos(rs.getDouble("quantidadelido"));
            e.setQuantidade_saldo(e.getQuantidade() - e.getQuantidade_lidos());
            e.setPerSeparacao(0.0);

            if (e.getQuantidade_lidos() > 0) {
                e.setPerSeparacao((e.getQuantidade_lidos() / e.getQuantidade()) * 100);

            }

            e.setPesopedido(0.0);
            e.setSucata_id(rs.getInt("sucata_id"));
            e.setPesosucata(rs.getDouble("pesosucata"));

            /*--------- Tratr os pesos de sucata*/
            e.setPesorecebido(rs.getDouble("pesorecebido"));
            e.setSituacaoSucata("SEM  RETORNO DE SUCATA");
            if (e.getSucata_id() > 0) {
                e.setSituacaoSucata("COM RETORNO DE SUCATA");
                e.setColor_sucata("danger");

                e.setPesosaldo(e.getPesorecebido() - e.getPesosucata());

                if (e.getPesorecebido() > 0) {
                    e.setPerEntrega((e.getPesorecebido() / e.getPesosucata()) * 100);

                }
            }

            e.setBtn_confimar_entrega("true");
            e.setBtn_enviar("true");
            e.setBtn_entregar("true");
            e.setBtn_excluir_serie("false");
            e.setBtn_minuta("true");
            switch (e.getSituacao()) {
                case "SEPARAR":  // PEDIDO FECHADO
                    e.setColor_categoria("danger");
                    e.setFa_categoria("fa-thumbs-up");
                    break;

                case "SEPARANDO": // SERIE FINALIZADA 
                    e.setColor_categoria("warning");
                    e.setFa_categoria("fa-thumbs-up");

                    break;

                case "SEPARADO": // SEPARANDO SÉRIE 
                    e.setColor_categoria("info");
                    e.setFa_categoria("fa-thumbs-up");
                    e.setBtn_enviar("false");

                    break;

                case "FATURAR": // RETORNO DO PEDIDO PARA FABRICA FATURAR
                    e.setColor_categoria("primary");
                    e.setFa_categoria("fa-thumbs-up");
                    e.setBtn_excluir_serie("true");
                    e.setBtn_entregar("false");
                    break;

                case "FATURADO": // PEDIDO FATURADO E LIBERADO PARA ENTREGA
                    e.setColor_categoria("info");
                    e.setFa_categoria("fa-thumbs-up");
                    e.setBtn_entregar("false");
                    break;

                case "PESANDO_SUCATA": // A SUCATA ESTA SENDO PESADA NO CLIENTE
                    e.setColor_categoria("warning");
                    e.setFa_categoria("fa-thumbs-up");
                    e.setBtn_entregar("false");
                    e.setBtn_confimar_entrega("false");
                    if ((e.getPesorecebido() == 0) && (e.getPesosucata() == 0)) {
                        e.setBtn_confimar_entrega("true");
                    }
                    if (e.getSucata_id() == 0) {
                        e.setBtn_confimar_entrega("false");
                        e.setPesosaldo(0.0);
                        e.setPesosucata(0.0);
                        e.setSituacao("ENTREGANDO_BATERIA");
                    }
                    break;

                case "ENTREGA_PROCESSADA": // A SUCATA ESTA SENDO PESADA NO CLIENTE
                    e.setColor_categoria("warning");
                    e.setFa_categoria("fa-thumbs-up");
                    e.setBtn_entregar("false");
                    e.setBtn_confimar_entrega("false");

                    break;
                case "ENTREGA_REALIZADA": // ENTREGA REALIZADA COM SUCESSO
                    e.setColor_categoria("success");
                    e.setFa_categoria("fa-thumbs-up");
                    e.setBtn_enviar("true");
                    e.setBtn_entregar("true");
                    e.setBtn_confimar_entrega("true");
                    e.setBtn_minuta("false");
                    if (e.getSucata_id() > 0) { // sucata foi coletada
                        e.setColor_minuta("success");
                        e.setBtn_minuta("true");
                    }

                    break;

                default: // ALGUM BO
                    e.setColor_categoria("danger");
                    e.setFa_categoria("fa-thumbs-up");
                    if (e.getQuantidade_saldo() <= 0) {
                        e.setColor_categoria("success");
                        e.setFa_categoria("fa-thumbs-up");
                    }
                    e.setBtn_enviar("true");
                    if (e.getQuantidade_saldo() <= 0) {
                        e.setBtn_enviar("false");
                    }
                    break;

            }

            resultado.add(e);
        }
        return resultado;
    }

    public boolean atualizarGeracaoMinuta(MinutaPedido t, int qtdreg, int contador) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update pedidohub set gerarminutacoleta = ?, dataminuta=?, minuta_id=? where pedido = ? and cliente = ? ";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, "G");
            pst.setDate(2, new java.sql.Date(t.getUsu_datemi().getTime()));
            pst.setInt(3, t.getUsu_codlan());
            pst.setInt(4, t.getUsu_numped());
            pst.setInt(5, t.getUsu_codcli());

            pst.executeUpdate();
            pst.close();
            if (qtdreg == contador) {
                JOptionPane.showMessageDialog(null, "Atenção : Sucata(s) Liberado(s) para coleta no HUB",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
            }

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
}
