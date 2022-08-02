/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.PedidoHub;
import br.com.sgi.bean.SucataAnalises;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.PedidoHubDAO;
import br.com.sgi.dao.SucataAnalisesDAO;
import br.com.sgi.dao.TransportadoraDAO;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;

/**
 *
 * @author jairosilva
 */
public final class FatPedidoMinuta extends InternalFrame {

    private List<PedidoHub> listPedidoHub = new ArrayList<PedidoHub>();
    private PedidoHubDAO pedidoHubDAO;
    private Pedido pedido;

    PedidoDAO pedidoDAO = new PedidoDAO();

    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;
    private String sqlPesquisaRotas = " ";
    private String sqlEstado = " ";
    private String sqlcodigoProduto = " ";
    private String sqlcodigoCliente = " ";
    private String filialSelecionada = "TODOS";
    private String estadoSelecionado = "TODOS";

    private String sqlPadrao = "\n and ped.codfil not in (1, 10, 11, 13, 90) "
            + "\n and ped.tnspro not in ('90124','90112','90113','90222','90223','90126','90133','90134','90132','90170')"
            + "\n and ped.sitped in (1, 2, 4)";

    public FatPedidoMinuta() {
        try {
            initComponents();
            //   setTitle(ClientSettings.getInstance().getResources().getResource("Pedido Clientes"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (pedidoHubDAO == null) {
                pedidoHubDAO = new PedidoHubDAO();
            }
            sqlPadrao = "\n and ped.codfil not in (1, 10, 11, 13,90) "
                    + "\n and ped.tnspro not in ('90124','90170')"
                    + "\n and ped.sitped in (1) "
                    + "\n and ped.codcli not in (2,5)";

            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
            txtDatFim.setDate(new Date());
            LoadEstados();
            pegarDataDigitada();
            preencherComboFilial(0);
            preencherComboTransportadora();
            iniciarBarra(" SIT ", sqlPadrao);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }

    }

    private void LoadEstados() {
        BaseEstado estado = new BaseEstado();
        Map<String, String> mapas = estado.getEstados();
        for (String uf : mapas.keySet()) {
            txtEstado.addItem(mapas.get(uf));
        }
    }

    public void iniciarBarra(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Buscando dados");
        tipoPedido = "fat";
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getPedidos(PESQUISA_POR, PESQUISA);
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                // barra.setString("Filtro carregado");
            }
        };
        worker.execute();
    }

    private String tipoPedido;

    public void iniciarBarraDiversos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Buscando dados");

        tipoPedido = "gar";
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getPedidos(PESQUISA_POR, PESQUISA);
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                // barra.setString("Filtro carregado");
            }
        };
        worker.execute();
    }

    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
    }

    private void pesquisarPorData(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        PESQUISA_POR = "DATA";
        PESQUISA = " and ped.datemi >= '" + datIni + "' and ped.datemi <='" + datFim + "'";
        iniciarBarra(PESQUISA_POR, PESQUISA);
    }

    public void selecionarRange() throws SQLException, Exception {
        String selecionar = "";
        if (jTableCarga.getRowCount() > 0) {
            for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                if ((Boolean) jTableCarga.getValueAt(i, 9)) {
                    selecionar += (jTableCarga.getValueAt(i, 1).toString() + ",");
                }
            }
            int tam = selecionar.length();
            if (tam > 0) {
                if (!selecionar.isEmpty()) {
                    selecionar = selecionar.substring(0, tam - 1);
                }
            }
        }
    }

    private double pesoSelecionado = 0;
    private double quantidadeSelecionado = 0;

    public void selecionarNotaRange() throws SQLException, Exception {
        List<MinutaPedido> listminutaPedido = new ArrayList<MinutaPedido>();
        Minuta minuta = new Minuta();
        String empresa = "1";
        String pedido = "";
        pesoSelecionado = 0;
        quantidadeSelecionado = 0;
        if (!txtPedidoSelecionado.getText().isEmpty()) {
            String ped = txtPedidoSelecionado.getText();
            int tam = ped.length();
            pedido = ped.substring(0, tam - 1);
            listPedidoHub = pedidoHubDAO.getPedidoHubs(" PEDIDOS", "and ped.numped in (" + pedido + ") and ped.codfil not in (1,10,11,13) ");

            for (PedidoHub p : listPedidoHub) {
                if (p.getPedido() > 0) {
                    MinutaPedido minutaPedido = new MinutaPedido();
                    minutaPedido.setCadMinuta(minuta);
                    minutaPedido.setUsu_codcli(p.getCliente());
                    Cliente cli = new Cliente();
                    cli.setCodigo(minutaPedido.getUsu_codcli());
                    cli.setNome(p.getCadCliente().getNome());
                    cli.setEstado(p.getCadCliente().getEstado());
                    cli.setCidade(p.getCadCliente().getCidade());
                    minutaPedido.setCadCliente(cli);
                    minutaPedido.setUsu_codemp(1);
                    minutaPedido.setUsu_codlan(0);
                    minutaPedido.setUsu_codori("");
                    minutaPedido.setUsu_codpes(0);
                    minutaPedido.setUsu_codfil(1);
                    minutaPedido.setUsu_codtpr("");
                    minutaPedido.setUsu_datemi(new Date());
                    minutaPedido.setEmissaoS(this.utilDatas.converterDateToStr(new Date()));
                    minutaPedido.setUsu_datlib(null);
                    pesoSelecionado += p.getPesopedido();
                    minutaPedido.setUsu_pesped(p.getPesopedido());
                    minutaPedido.setUsu_pesnfv(p.getPesopedido());
                    quantidadeSelecionado += p.getQuantidade();
                    minutaPedido.setUsu_qtdped(p.getQuantidade());
                    minutaPedido.setUsu_numnfv(p.getNotafiscal());
                    minutaPedido.setUsu_numped(p.getPedido());
                    minutaPedido.setUsu_obsmin("GERANDO MINUTA DO PEDIDO " + minutaPedido.getUsu_numped());
                    minutaPedido.setUsu_pesbal(0.0);
                    minutaPedido.setUsu_pesrec(0.0);
                    minutaPedido.setUsu_pessuc(p.getPesosucata());
                    minutaPedido.setUsu_qtdfat(p.getQuantidade());
                    minutaPedido.setUsu_qtdvol(0.0);
                    minutaPedido.setUsu_seqite(0);
                    minutaPedido.setUsu_sitmin("ABERTA");
                    minutaPedido.setUsu_codsnf(p.getSerienota());
                    minutaPedido.setUsu_tnspro(p.getTransacao());
                    minutaPedido.setUsu_lansuc(p.getSucata_id());

                    listminutaPedido.add(minutaPedido);

                }
            }
            listPedidoHub = pedidoHubDAO.getPedidoHubs(" PEDIDOS", "and ped.numped in (" + pedido + ") and ped.codfil  in (1) and ped.tnspro  in ('90123','90122','90112','90113') ");
            for (PedidoHub p : listPedidoHub) {
                if (p.getPedido() > 0) {
                    MinutaPedido minutaPedido = new MinutaPedido();
                    minutaPedido.setCadMinuta(minuta);
                    minutaPedido.setUsu_codcli(p.getCliente());
                    Cliente cli = new Cliente();
                    cli.setCodigo(minutaPedido.getUsu_codcli());
                    cli.setNome(p.getCadCliente().getNome());
                    cli.setEstado(p.getCadCliente().getEstado());
                    cli.setCidade(p.getCadCliente().getCidade());
                    minutaPedido.setCadCliente(cli);
                    minutaPedido.setUsu_codemp(1);
                    minutaPedido.setUsu_codlan(0);
                    minutaPedido.setUsu_codori("");
                    minutaPedido.setUsu_codpes(0);
                    minutaPedido.setUsu_codfil(1);
                    minutaPedido.setUsu_codtpr("");
                    minutaPedido.setUsu_datemi(new Date());
                    minutaPedido.setEmissaoS(this.utilDatas.converterDateToStr(new Date()));
                    minutaPedido.setUsu_datlib(null);
                    pesoSelecionado += p.getPesopedido();
                    minutaPedido.setUsu_pesped(p.getPesopedido());
                    minutaPedido.setUsu_pesnfv(p.getPesopedido());
                    quantidadeSelecionado += p.getQuantidade();
                    minutaPedido.setUsu_qtdped(p.getQuantidade());
                    minutaPedido.setUsu_numnfv(p.getNotafiscal());
                    minutaPedido.setUsu_numped(p.getPedido());
                    minutaPedido.setUsu_obsmin("GERANDO MINUTA DO PEDIDO " + minutaPedido.getUsu_numped());
                    minutaPedido.setUsu_pesbal(0.0);
                    minutaPedido.setUsu_pesrec(0.0);
                    minutaPedido.setUsu_pessuc(p.getPesosucata());
                    minutaPedido.setUsu_qtdfat(p.getQuantidade());
                    minutaPedido.setUsu_qtdvol(0.0);
                    minutaPedido.setUsu_seqite(0);
                    minutaPedido.setUsu_sitmin("ABERTA");
                    minutaPedido.setUsu_codsnf(p.getSerienota());
                    minutaPedido.setUsu_tnspro(p.getTransacao());
                    minutaPedido.setUsu_lansuc(p.getSucata_id());

                    listminutaPedido.add(minutaPedido);

                }
            }
            if (listminutaPedido.size() > 0) {
                minuta.setUsu_pesfat(pesoSelecionado);
                minuta.setUsu_qtdfat(quantidadeSelecionado);
                minuta.setUsu_codfil(1);
                minuta.setUsu_codtra(Integer.valueOf(transportadoraSelecionada));
                minuta.setUsu_codemp(1);
                //  minuta.setCadTransportadora(CadTransportadora);
                novoRegistroMinuta("PEDIDO", "0", minuta, listminutaPedido);
            }
        }

//        if (jTableCarga.getRowCount() > 0) {
//            double qtdped = jTableCarga.getRowCount();
//            for (int i = 0; i < jTableCarga.getRowCount(); i++) {
//                if ((Boolean) jTableCarga.getValueAt(i, 10)) {
//                    if (jTableCarga.getValueAt(i, 11).toString().equals("FECHADO")) {
//                        pedido = (jTableCarga.getValueAt(i, 1).toString());
//                        if (!pedido.isEmpty()) { // tem pedido
//                            MinutaPedido minutaPedido = new MinutaPedido();
//                            minutaPedido.setCadMinuta(minuta);
//                            minutaPedido.setUsu_codcli(Integer.valueOf(jTableCarga.getValueAt(i, 2).toString()));
//                            Cliente cli = new Cliente();
//                            cli.setCodigo(minutaPedido.getUsu_codcli());
//                            cli.setNome(jTableCarga.getValueAt(i, 3).toString());
//                            cli.setEstado(jTableCarga.getValueAt(i, 4).toString());
//                            cli.setCidade(jTableCarga.getValueAt(i, 5).toString());
//                            minutaPedido.setCadCliente(cli);
//                            minutaPedido.setUsu_codemp(1);
//                            minutaPedido.setUsu_codlan(0);
//                            minutaPedido.setUsu_codori("");
//                            minutaPedido.setUsu_codpes(0);
//                            minutaPedido.setUsu_codfil(1);
//                            minutaPedido.setUsu_codtpr("");
//                            minutaPedido.setUsu_datemi(new Date());
//                            minutaPedido.setEmissaoS(this.utilDatas.converterDateToStr(new Date()));
//                            minutaPedido.setUsu_datlib(null);
//                            pesoSelecionado += Double.valueOf(jTableCarga.getValueAt(i, 12).toString());
//                            minutaPedido.setUsu_pesped(Double.valueOf(jTableCarga.getValueAt(i, 12).toString()));
//                            minutaPedido.setUsu_pesnfv(minutaPedido.getUsu_pesped());
//                            quantidadeSelecionado += Double.valueOf(jTableCarga.getValueAt(i, 13).toString());
//                            minutaPedido.setUsu_qtdped(Double.valueOf(jTableCarga.getValueAt(i, 13).toString()));
//                            minutaPedido.setUsu_numnfv(Integer.valueOf(jTableCarga.getValueAt(i, 14).toString()));
//                            minutaPedido.setUsu_numped(Integer.valueOf(pedido));
//                            minutaPedido.setUsu_obsmin("GERANDO MINUTA NOTA");
//                            minutaPedido.setUsu_pesbal(0.0);
//                            minutaPedido.setUsu_pesrec(0.0);
//                            minutaPedido.setUsu_pessuc(minutaPedido.getUsu_pesped());
//                            minutaPedido.setUsu_qtdfat(minutaPedido.getUsu_qtdped());
//                            minutaPedido.setUsu_qtdvol(qtdped);
//                            minutaPedido.setUsu_seqite(0);
//                            minutaPedido.setUsu_sitmin("ABERTA");
//                            minutaPedido.setUsu_codsnf(jTableCarga.getValueAt(i, 17).toString());
//                            minutaPedido.setUsu_tnspro(jTableCarga.getValueAt(i, 18).toString());
//                            minutaPedido.setUsu_lansuc(Integer.valueOf(jTableCarga.getValueAt(i, 21).toString()));
//
//                            listminutaPedido.add(minutaPedido);
//
//                        }
//                    }
//                }
//            }
//
//        }
    }

    public void selecionarRangeLiberarEntrega() throws SQLException, Exception {
        if (jTableCarga.getRowCount() > 0) {
            PedidoHubDAO pedDao = new PedidoHubDAO();

            String numeroPedido = "0";
            Integer qtdreg = 0;
            if (jTableCarga.getRowCount() > 0) {
                for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                    if ((Boolean) jTableCarga.getValueAt(i, 10)) {
                        qtdreg++;
                    }
                }
            }

            Integer contador = 0;

            if (jTableCarga.getRowCount() > 0) {
                for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                    if ((Boolean) jTableCarga.getValueAt(i, 10)) {
                        numeroPedido = (jTableCarga.getValueAt(i, 1).toString());
                        if (!numeroPedido.isEmpty()) { // tem pedido
                            MinutaPedido minutaPedido = new MinutaPedido();
                            minutaPedido.setUsu_codemp(1);
                            minutaPedido.setUsu_numped(Integer.valueOf(numeroPedido));
                            contador++;
                            if (!pedDao.liberarParaEntrega(minutaPedido, qtdreg, contador)) {

                            }

                        }

                    }
                }

            }
        }
    }

    private void novoRegistroMinuta(String PROCESSO, String selecionar, Minuta minuta, List<MinutaPedido> listminutaPedido) throws PropertyVetoException, Exception {
//        frmMinutasGerar sol = new frmMinutasGerar();
//        MDIFrame.add(sol, true);
//        sol.setPosicao();
//        sol.setMaximum(true); // executa maximizado 
//
//        sol.setRecebePalavraPedidoHub(this, PROCESSO, selecionar, minuta, listminutaPedido);

    }
    private SucataAnalises sucataanalises;

    private void getNota(String PESQUISA_POR, String PESQUISA) throws SQLException {
        SucataAnalisesDAO dao = new SucataAnalisesDAO();

        sucataanalises = new SucataAnalises();
        sucataanalises = dao.getSucataAnalises(PESQUISA_POR, PESQUISA);

        if (sucataanalises != null) {
            if (sucataanalises.getNota() > 0) {

            }
        }
    }

    private void getPedido(String PESQUISA_POR, String PESQUISA) throws SQLException {
        pedido = new Pedido();
        PedidoDAO pedidoDAO = new PedidoDAO();
        pedido = pedidoDAO.getPedido(PESQUISA_POR, PESQUISA);
        if (pedido != null) {
            if (pedido.getPedido() > 0) {

            }
        }
    }

    void retornarPedido(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        PESQUISA_POR = "DATA";
        PESQUISA = " and ped.datemi >= '" + datIni + "' and ped.datemi <='" + datFim + "'";
        getPedidos(PESQUISA_POR, PESQUISA);
    }

    private void getPedidos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listPedidoHub = this.pedidoHubDAO.getPedidoHubs(PESQUISA_POR, PESQUISA);
        if (listPedidoHub != null) {
            carregarTabela();
        }
    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon AndaIcon = getImage("/images/sitAnd.png");
        ImageIcon HubIcon = getImage("/images/sitMedio.png");

        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon GarIcon = getImage("/images/bateriaindu.png");

        double peso = 0;
        double peso_sucata = 0;
        double qtdy = 0;
        double qtdy_bat = 0;
        double qtdy_com = 0;

        double qtdped = 0;

        for (PedidoHub ped : listPedidoHub) {
            Object[] linha = new Object[23];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new PedidoEmbarque.ColorirRenderer();
            FatPedidoMinuta.JTableRenderer renderers = new FatPedidoMinuta.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            columnModel.getColumn(9).setCellRenderer(renderers);
            linha[0] = AutIcon;
            if (ped.getLinha().equals("BM")) {
                linha[0] = MotIcon;
            }
            if (ped.getTransacao().equals("90112")) {
                linha[0] = GarIcon;
            }
            if (ped.getTransacao().equals("90113")) {
                linha[0] = GarIcon;
            }
            if (ped.getTransacao().equals("90122")) {
                linha[0] = GarIcon;
            }
            if (ped.getTransacao().equals("90123")) {
                linha[0] = GarIcon;
            }

            linha[1] = ped.getPedido();
            linha[2] = ped.getCliente();
            linha[3] = ped.getCadCliente().getNome();
            linha[4] = ped.getCadCliente().getEstado();
            linha[5] = ped.getCadCliente().getCidade();
            linha[6] = ped.getDatapedidoS();
            linha[7] = ped.getDiatransporte();
            linha[9] = AndaIcon;
            if (ped.getSituacao().equals("FATURADO")) {
                linha[9] = RuimIcon;
            } else if (ped.getSituacao().equals("LIBERADO HUB")) {
                linha[9] = HubIcon;
            }

            linha[8] = ped.getDatafaturarS();
            linha[10] = false;
            linha[11] = ped.getSituacao();
            linha[12] = ped.getPesopedido();
            if (ped.getPesopedido() > 0) {
                peso += ped.getPesopedido();
            }

            linha[13] = ped.getQuantidade();
            if (ped.getQuantidade() > 0) {
                qtdy_bat += ped.getQuantidade();
            }

            linha[15] = ped.getFilial();
            if (ped.getCadNotaFiscal() != null) {
                linha[14] = ped.getCadNotaFiscal().getNotafiscal();
                linha[16] = ped.getCadNotaFiscal().getEmissaoS();

            }
            linha[17] = ped.getCadNotaFiscal().getSerie();
            linha[18] = ped.getTransacao();
            linha[19] = ped.getTransportadora() + " - " + ped.getCadTransportadora().getNomeTransportadora();
            linha[21] = ped.getSucata_id();
            if (ped.getSucata_id() > 0) {
                peso_sucata += ped.getPesopedido();
            }
            linha[22] = ped.getTabelapreco();
            qtdped++;
            modeloCarga.addRow(linha);
        }
        lblPeso.setText(FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM));
        lblQtdy.setText(FormatarPeso.mascaraPorcentagem(qtdy_bat, FormatarPeso.PORCENTAGEM));
        lblQtdyPedido.setText(FormatarPeso.mascaraPorcentagem(qtdped, FormatarPeso.PORCENTAGEM));
        lblPesoSucata.setText(FormatarPeso.mascaraPorcentagem(peso_sucata, FormatarPeso.PORCENTAGEM));

    }

    public void retornarPedido() {
        try {
            pegarDataDigitada();
            iniciarBarra(" SIT ", "\nand ped.datemi >= '" + datIni + "' and ped.datemi <='" + datFim + "' \n");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    private boolean AddRegEtq;

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);

        jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(8).setCellRenderer(direita);

        // jTableCarga.getColumnModel().getColumn(9).setCellRenderer(direita);
        //jTableCarga.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(14).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(15).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(16).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(17).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(18).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(20).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(21).setCellRenderer(direita);
        jTableCarga.setRowHeight(40);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoResizeMode(0);
        jTableCarga.setAutoCreateRowSorter(true);

    }
    private Component RdiGrid;

    public void executarExportacaoPedido() {
        int qtdreg = jTableCarga.getRowCount();

        if (qtdreg <= 0) {
            JOptionPane.showMessageDialog(null, "Não existe informações para exportar  ");
        } else {
            JFileChooser fc = new JFileChooser();
            int option = fc.showSaveDialog(RdiGrid);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filename = fc.getSelectedFile().getName();
                String path = fc.getSelectedFile().getParentFile().getPath();
                int len = filename.length();
                String ext = "";
                String file = "";
                if (len > 4) {
                    ext = filename.substring(len - 4, len);
                }
                if (ext.equals(".xls")) {
                    file = path + "\\" + filename;
                } else {
                    file = path + "\\" + filename + ".xls";
                }
                try {
                    toExcelPedido(RdiGrid, new File(file));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Problemas    " + ex);

                } catch (Error ex) {
                    JOptionPane.showMessageDialog(null, "Problemas    " + ex);
                }
            }
        }
    }

    public void toExcelPedido(Component RdiGrid, File file) throws IOException {
        TableModel model = jTableCarga.getModel();
        FileWriter excel = new FileWriter(file);

        for (int i = 0; i < model.getColumnCount(); i++) {
            excel.write(model.getColumnName(i) + "\t");
        }

        excel.write("\n");

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                excel.write(model.getValueAt(i, j).toString() + "\t");
            }
            excel.write("\n");
        }
        excel.close();
        JOptionPane.showMessageDialog(null, "Arquivo exportado com sucesso em:  " + file);

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    private ImageIcon getImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return (new ImageIcon(url));
        } else {
            return null;

        }
    }

    public void preencherComboFilial(Integer id) throws SQLException, Exception {
        FilialDAO filialDAO = new FilialDAO();
        List<Filial> listFilial = new ArrayList<Filial>();
        String cod;
        String des;
        String desger;

        listFilial = filialDAO.getFilias("", " and e070fil.codemp = 1");

        if (listFilial != null) {
            for (Filial filial : listFilial) {
                cod = filial.getFilial().toString();
                des = filial.getRazao_social();
                desger = cod + " - " + des;
                txtFilial.addItem(desger);

            }
        }
    }

    private void preencherComboTransportadora() throws SQLException {
        String codigo = "";
        String nome = "";
        List<Transportadora> listTra = new ArrayList<Transportadora>();
        TransportadoraDAO dao = new TransportadoraDAO();
        listTra = dao.getTransportadoras(" codigo ", " and codtra in (4,118,204,238,5109,9282,9312, 9327,9359,9382,9298)");
        txtTransportadora.removeAllItems();
        txtTransportadora.addItem("TODOS");
        for (Transportadora t : listTra) {
            codigo = t.getCodigoTransportadora().toString();
            nome = t.getNomeTransportadora() + " - " + t.getCidade();
            txtTransportadora.addItem(codigo + " - " + nome);
        }

    }

    private void limparFiltros() throws SQLException, ParseException {
        txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
        this.filialSelecionada = "0";
        this.transportadoraSelecionada = "0";
        this.pedidoSelecionado = "";
        this.estadoSelecionado = "";
        txtPedido.setText("");
        txtCliente.setText("");
        txtPedidoSelecionado.setText("");
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);

    }

    private void pesquisarTable(String pesquisa) {
        DefaultTableModel tabela_produtos = (DefaultTableModel) jTableCarga.getModel();
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabela_produtos);
        jTableCarga.setRowSorter(sorter);

        // int index = pesquisa.indexOf(":");
        // String coluna = pesquisa.substring(0, index);
        // coluna = coluna.trim();
        String coluna = "PED";
        int posicao = 0;
        if (coluna.equals("PED") || coluna.equals("ped")) {
            posicao = 1;
        }
        if (coluna.equals("FAT") || coluna.equals("fat")) {
            posicao = 8;
        }

        if (pesquisa.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<TableModel, Object> rf = null;
                try {
                    rf = RowFilter.regexFilter(pesquisa, posicao);
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                sorter.setRowFilter(rf);
            } catch (PatternSyntaxException pse) {
                System.err.println("Erro");
            }
        }
    }

    void retornarMinuta() throws ParseException {
        pegarDataDigitada();
        retornarPedido();
    }

    public class JTableRenderer extends DefaultTableCellRenderer {

        protected void setValue(Object value) {
            if (value instanceof ImageIcon) {
                if (value != null) {
                    ImageIcon d = (ImageIcon) value;
                    setIcon(d);
                } else {
                    setText("");
                    setIcon(null);
                }
            } else {
                super.setValue(value);
            }
        }
    }
    private static Color COR_ESTOQUE_HFF = new Color(66, 111, 66);

    public class ColorirRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable jTableCarga, Object value, boolean selected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(jTableCarga, value, selected, hasFocus, row, col);
            setBackground(Color.WHITE);
            String str = (String) value;
            if (str == null) {
                str = "";
            }
            if ("ABERTA".equals(str)) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.WHITE);
                setBackground(COR_ESTOQUE_HFF);
            }

            //   setBackground(COR_ESTOQUE_HFF);
            return this;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpSimNao = new javax.swing.ButtonGroup();
        grpAutoMoto = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar = new javax.swing.JButton();
        btnManutencao = new javax.swing.JButton();
        btnLiberar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        txtCliente = new org.openswing.swing.client.TextControl();
        btnFiltrar2 = new javax.swing.JButton();
        btnFiltrar3 = new javax.swing.JButton();
        lblPeso = new javax.swing.JLabel();
        lblQtdy = new javax.swing.JLabel();
        lblQtdyPedido = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtFilial = new javax.swing.JComboBox<>();
        lblPesoSucata = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtAutoMoto = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtTransportadora = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        btnLiberar1 = new javax.swing.JButton();
        btnGarantias = new javax.swing.JButton();
        barra = new javax.swing.JProgressBar();
        jLabel7 = new javax.swing.JLabel();
        txtPesquisar = new org.openswing.swing.client.TextControl();
        txtPedidoSelecionado = new org.openswing.swing.client.TextControl();
        txtEstado = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pedidos Clientes");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Pedido", "Cliente", "Nome", "UF", "Cidade", "Data Pedido", "Transporte", "Data Fat", "#", "Liberar", "Situação", "Peso", "Quantidade", "Nota", "Filial", "Emissão", "Serie", "Transação", "Transportadora", "Pré_fatura", "Analise", "Nota", "Tabela"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCarga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCargaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableCarga);
        if (jTableCarga.getColumnModel().getColumnCount() > 0) {
            jTableCarga.getColumnModel().getColumn(0).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(400);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(400);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(400);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(40);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(40);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(40);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(150);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(16).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(16).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(16).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(17).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(17).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(17).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setMinWidth(400);
            jTableCarga.getColumnModel().getColumn(19).setPreferredWidth(400);
            jTableCarga.getColumnModel().getColumn(19).setMaxWidth(400);
            jTableCarga.getColumnModel().getColumn(20).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(22).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(22).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(22).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(23).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(23).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(23).setMaxWidth(100);
        }

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDatIni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDatIniActionPerformed(evt);
            }
        });

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDatFim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDatFimActionPerformed(evt);
            }
        });

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnManutencao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnManutencao.setText("Finalizar");
        btnManutencao.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnManutencao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManutencaoActionPerformed(evt);
            }
        });

        btnLiberar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        btnLiberar.setText("Minuta");
        btnLiberar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnLiberar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiberarActionPerformed(evt);
            }
        });

        jLabel1.setText("Pedido");

        jLabel3.setText("Data Inicio");

        jLabel4.setText("Data Final");

        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoActionPerformed(evt);
            }
        });

        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        btnFiltrar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar2ActionPerformed(evt);
            }
        });

        btnFiltrar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar3ActionPerformed(evt);
            }
        });

        lblPeso.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPeso.setForeground(new java.awt.Color(51, 102, 255));
        lblPeso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPeso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_retorno.png"))); // NOI18N
        lblPeso.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso"));
        lblPeso.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblPeso.setOpaque(true);
        lblPeso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPesoMouseClicked(evt);
            }
        });

        lblQtdy.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdy.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio.png"))); // NOI18N
        lblQtdy.setBorder(javax.swing.BorderFactory.createTitledBorder("Quantidade Baterias"));
        lblQtdy.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdy.setOpaque(true);

        lblQtdyPedido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdyPedido.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdyPedido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdyPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio_manual.png"))); // NOI18N
        lblQtdyPedido.setBorder(javax.swing.BorderFactory.createTitledBorder("Pedidos Liberados"));
        lblQtdyPedido.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyPedido.setOpaque(true);

        jLabel5.setText("Estado");

        jLabel6.setText("Filial");

        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtFilial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtFilial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialActionPerformed(evt);
            }
        });

        lblPesoSucata.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPesoSucata.setForeground(new java.awt.Color(0, 102, 0));
        lblPesoSucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPesoSucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio_cancelar.png"))); // NOI18N
        lblPesoSucata.setBorder(javax.swing.BorderFactory.createTitledBorder("Retorno Sucata"));
        lblPesoSucata.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblPesoSucata.setOpaque(true);

        jLabel8.setText("Cliente");

        txtAutoMoto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAutoMoto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "AUTO", "MOTO", "HUB" }));
        txtAutoMoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAutoMotoActionPerformed(evt);
            }
        });

        jLabel11.setText("Auto/Moto/Hub");

        txtTransportadora.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTransportadora.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "SIM", "NAO" }));
        txtTransportadora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransportadoraActionPerformed(evt);
            }
        });

        jLabel2.setText("Trasportadora");

        btnLiberar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-vassoura-16.png"))); // NOI18N
        btnLiberar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnLiberar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiberar1ActionPerformed(evt);
            }
        });

        btnGarantias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        btnGarantias.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnGarantias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGarantiasActionPerformed(evt);
            }
        });

        jLabel7.setText("Pesquisar na Grid");

        txtPesquisar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarActionPerformed(evt);
            }
        });

        txtPedidoSelecionado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedidoSelecionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoSelecionadoActionPerformed(evt);
            }
        });

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(195, 195, 195))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                .addGap(6, 6, 6)
                                .addComponent(btnFiltrar2)
                                .addGap(4, 4, 4)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAutoMoto, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtFilial, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(91, 91, 91)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(123, 123, 123))
                            .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(lblQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQtdyPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPesoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(btnLiberar1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGarantias, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btnLiberar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(20, 20, 20)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFiltrar3))
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTransportadora, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPedidoSelecionado, javax.swing.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnFiltrar3)
                            .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel1)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel7)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTransportadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFiltrar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAutoMoto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(txtPedidoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(lblQtdyPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(btnManutencao, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(btnLiberar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPesoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(btnLiberar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGarantias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar, btnFiltrar2, btnFiltrar3, txtAutoMoto, txtCliente, txtDatFim, txtDatIni, txtFilial, txtPedido, txtTransportadora});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnLiberar, btnLiberar1, btnManutencao, lblPeso, lblPesoSucata, lblQtdy, lblQtdyPedido});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String pedidoSelecionado = "";

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();
        String selecao = jTableCarga.getValueAt(linhaSelSit, 1).toString();
        if (!selecao.isEmpty()) {
            pedidoSelecionado += selecao + ",";
        }
        txtPedidoSelecionado.setText(pedidoSelecionado.trim());

    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed

        if (txtPedido.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", " INFORME O CLIENTE");
        } else {
            try {
                String sql = "and ped.numped = '" + txtPedido.getText() + "'";
                //  getPedidos("SIT", sql);
                iniciarBarraDiversos("DIV ", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnManutencaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManutencaoActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnManutencaoActionPerformed

    private void btnLiberarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiberarActionPerformed
        try {
            if (!transportadoraSelecionada.equals("TODOS")) {
                selecionarNotaRange();
            } else {
                Mensagem.mensagemRegistros("ERRO", "SELECIONE A TRANSPORTADORA");
            }

        } catch (PropertyVetoException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnLiberarActionPerformed

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed

        if (txtCliente.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {

                String sql = " and ped.codcli = '" + txtCliente.getText() + "' ";
                getPedidos("SIT", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_btnFiltrar2ActionPerformed

    private void btnFiltrar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar3ActionPerformed
        try {
            pegarDataDigitada();
            String sql = sqlPadrao + " and ped.datemi >= '" + datIni + "' and ped.datemi <='" + datFim + "' ";
            iniciarBarra(" sql ", sql);
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnFiltrar3ActionPerformed

    private void verificaProduto() {
        if (!txtPedido.getText().trim().equals("")) {
            sqlcodigoProduto = " and pedsis.numped = " + txtPedido.getText().trim() + " ";
        } else {
            sqlcodigoProduto = " ";
        }
    }

    private void verificaCliente() {
        if (!txtCliente.getText().trim().equals("")) {
            sqlcodigoCliente = " and cli.codcli = " + txtCliente.getText().trim() + " ";

        } else {
            sqlcodigoCliente = " ";
        }

    }


    private void lblPesoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPesoMouseClicked
        try {
            String sql = " \nand ped.sitped  IN (1,2) ";
            getPedidos("SIT", sql);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoMinuta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_lblPesoMouseClicked

    private void txtPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoActionPerformed

        if (txtPedido.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", " INFORME O CLIENTE");
        } else {
            try {
                String sql = "and ped.numped = '" + txtPedido.getText() + "'";
                //  getPedidos("SIT", sql);
                iniciarBarraDiversos("DIV ", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_txtPedidoActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed

        if (txtCliente.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {

                String sql = " and ped.codcli = '" + txtCliente.getText() + "' ";
                getPedidos("SIT", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_txtClienteActionPerformed

    private void txtDatIniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatIniActionPerformed

        try {
            pegarDataDigitada();

            String sql = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "'";
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_txtDatIniActionPerformed

    private void txtDatFimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatFimActionPerformed

        try {
            pegarDataDigitada();

            String sql = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' ";
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_txtDatFimActionPerformed

    private void txtFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialActionPerformed
        try {
            if (txtFilial.getSelectedIndex() != -1) {
                if (!txtFilial.getSelectedItem().equals("TODOS")) {
                    try {
                        String filial = txtFilial.getSelectedItem().toString().trim();
                        int index = filial.indexOf("-");
                        filialSelecionada = filial.substring(0, index).trim();
                        pegarDataDigitada();

                        if (!transportadoraSelecionada.equals("0") && !transportadoraSelecionada.equals("TODOS")) {
                            // getPedidos("SIT", " \n and ped.sitped in (1,2)  and ped.datemi >= '" + datIni + "' and ped.datemi <='" + datFim + "' and ped.codfil = " + filialSelecionada + "\nand ped.codtra = " + transportadoraSelecionada + "\n ");
                            iniciarBarra(" SIT ", "\n and ped.codfil not in (1, 10, 11, 13) and ped.sitped in (1,2)  and ped.datemi >= '" + datIni + "' and ped.datemi <='" + datFim + "' and ped.codfil = " + filialSelecionada + "\nand ped.codtra = " + transportadoraSelecionada + "\n ");
                        } else {
                            // getPedidos("SIT", " \nand ped.datemi >= '" + datIni + "' and ped.datemi <='" + datFim + "' and ped.codfil = " + filialSelecionada + "\n ");
                            iniciarBarra(" SIT ", " \n and ped.codfil not in (1, 10, 11, 13) and ped.datemi >= '" + datIni + "' and ped.datemi <='" + datFim + "' and ped.codfil = " + filialSelecionada + "\n ");
                        }

                    } catch (Exception ex) {
                        Logger.getLogger(IntegrarPesos.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);

        }


    }//GEN-LAST:event_txtFilialActionPerformed

    private void txtAutoMotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAutoMotoActionPerformed
        try {
            if (txtAutoMoto.getSelectedIndex() != -1) {
                if (!txtAutoMoto.getSelectedItem().toString().equals("TODOS")) {
                    pegarDataDigitada();
                    if (txtAutoMoto.getSelectedItem().toString().equals("AUTO")) {
                       
                        iniciarBarra(" SIT", sqlPadrao + " and pro.codori = 'BA'\n");
                    }
                    if (txtAutoMoto.getSelectedItem().toString().equals("MOTO")) {
                        iniciarBarra(" SIT", sqlPadrao + " and pro.codori = 'BM'\n");
                    }
                    if (txtAutoMoto.getSelectedItem().toString().equals("HUB")) {
                        iniciarBarra(" SIT", sqlPadrao + " and pro.codori = 'P3'\n");
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);

        }
    }//GEN-LAST:event_txtAutoMotoActionPerformed

    private void txtTransportadoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransportadoraActionPerformed
        if (txtTransportadora.getSelectedIndex() != -1) {
            if (!txtTransportadora.getSelectedItem().equals("TODOS")) {
                try {
                    String filial = txtTransportadora.getSelectedItem().toString().trim();
                    int index = filial.indexOf("-");
                    transportadoraSelecionada = filial.substring(0, index).trim();
                    pegarDataDigitada();
                    if (!estadoSelecionado.equals("TODOS")) {

                        estadoSelecionado = filial.substring(0, index).trim();
                        iniciarBarra(" SIT ", sqlPadrao + " \nand cli.sigufs '" + estadoSelecionado + "' "
                                + "\n and ped.datemi >= '" + datIni + "' "
                                + "\n and ped.datemi <='" + datFim + "' "
                                + "\n and ped.codtra = " + transportadoraSelecionada + "\n");

                    } else {

                        iniciarBarra(" SIT ", sqlPadrao + " \nand cli.sigufs '" + estadoSelecionado + "' "
                                + "\n and ped.codtra = " + transportadoraSelecionada + "\n");

                    }

                } catch (Exception ex) {
                    Logger.getLogger(IntegrarPesos.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }//GEN-LAST:event_txtTransportadoraActionPerformed

    private void btnLiberar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiberar1ActionPerformed
        try {
            limparFiltros();
        } catch (SQLException ex) {
            Logger.getLogger(FatPedidoMinuta.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(FatPedidoMinuta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLiberar1ActionPerformed

    private void btnGarantiasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGarantiasActionPerformed
        try {
            iniciarBarra(" gar ", sqlPadrao);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoMinuta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGarantiasActionPerformed

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed
        pesquisarTable(txtPesquisar.getText());
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void txtPedidoSelecionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoSelecionadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPedidoSelecionadoActionPerformed

    private void txtEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadoActionPerformed

        if (txtEstado.getSelectedIndex() != -1) {
            if (!txtEstado.getSelectedItem().equals("TODOS")) {
                try {
                    estadoSelecionado = txtEstado.getSelectedItem().toString().trim();
                    iniciarBarra(" SIT ", sqlPadrao + " \nand cli.sigufs = '" + estadoSelecionado + "'");

                } catch (Exception ex) {
                    Logger.getLogger(IntegrarPesos.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }//GEN-LAST:event_txtEstadoActionPerformed

    private String transportadoraSelecionada = "0";

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar3;
    private javax.swing.JButton btnGarantias;
    private javax.swing.JButton btnLiberar;
    private javax.swing.JButton btnLiberar1;
    private javax.swing.JButton btnManutencao;
    private javax.swing.ButtonGroup grpAutoMoto;
    private javax.swing.ButtonGroup grpSimNao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblPesoSucata;
    private javax.swing.JLabel lblQtdy;
    private javax.swing.JLabel lblQtdyPedido;
    private javax.swing.JComboBox<String> txtAutoMoto;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtEstado;
    private javax.swing.JComboBox<String> txtFilial;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.TextControl txtPedidoSelecionado;
    private org.openswing.swing.client.TextControl txtPesquisar;
    private javax.swing.JComboBox<String> txtTransportadora;
    // End of variables declaration//GEN-END:variables
}
