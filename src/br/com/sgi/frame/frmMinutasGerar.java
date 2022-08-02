/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.CargaRegistro;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaNota;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.Motorista;
import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.PedidoHub;
import br.com.sgi.bean.PedidoHubProduto;
import br.com.sgi.bean.SucataAnalises;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.dao.CargaAberturaDAO;
import br.com.sgi.dao.MinutaDAO;
import br.com.sgi.dao.MinutaNotaDAO;
import br.com.sgi.dao.MinutaPedidoDAO;
import br.com.sgi.dao.MotoristaDAO;
import br.com.sgi.dao.NotaFiscalDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.PedidoHubDAO;
import br.com.sgi.dao.PedidoProdutoDAO;
import br.com.sgi.dao.SucataAnalisesDAO;
import br.com.sgi.dao.TransportadoraDAO;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSRelatorio;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class frmMinutasGerar extends InternalFrame {

    private NotaFiscal notafiscal;
    private NotaFiscalDAO notafiscalDAO;
    private List<NotaFiscal> lstNotaFiscal = new ArrayList<NotaFiscal>();

    private Minuta minuta;
    private MinutaDAO minutaDAO;

    private MinutaPedido minutapedido;
    private List<MinutaPedido> listminutaPedido = new ArrayList<MinutaPedido>();
    private MinutaPedidoDAO minutaPedidoDAO;

    private MinutaNota minutaNota;
    private MinutaNotaDAO minutaNotaDAO;
    private List<MinutaNota> lstMinutaNota = new ArrayList<MinutaNota>();

    // produtos dos pedidos
    private List<PedidoHubProduto> listPedidoProduto = new ArrayList<PedidoHubProduto>();

    private Pedido pedido;
    private Transportadora transportadora;

    private LogMinutaEmbarqueNota veioCampo;
    private frmMinutas veioCampoEmbarque;
    private LogPedido veioCampoLogPedido;
    private SucataTriagem veioCampoSucataTriagem;
    private FatPedidoHub veioCampoFatPedidoHub;
    private UtilDatas utilDatas;

    private boolean minutaHub = false;
    private boolean addReg;
    private SucataAnalises sucataanalises;
    private double pesoSelecionado = 0;
    private double quantidadeSelecionado = 0;

    private String pedidoSelecionado = "0";

    private String PROCESSO;

    public frmMinutasGerar() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Minutas notas"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (notafiscalDAO == null) {
                this.notafiscalDAO = new NotaFiscalDAO();
            }
            if (minutaDAO == null) {
                this.minutaDAO = new MinutaDAO();
            }
            if (minutaNotaDAO == null) {
                this.minutaNotaDAO = new MinutaNotaDAO();
            }
            if (minutaPedidoDAO == null) {
                this.minutaPedidoDAO = new MinutaPedidoDAO();
            }
            limpatela();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void limpatela() throws ParseException, Exception {
        txtEmbarque.setDate(new Date());
        txtHorEmb.setText(utilDatas.retornarHoras(new Date()));
        txtEntrega.setDate(new Date());
        txtDataLiberacao.setDate(new Date());
    }

    public void preencherCombo(String transportadora) throws SQLException {
        String cod = "";
        String des = "";
        String desger = "";

        MotoristaDAO daoMot = new MotoristaDAO();
        List<Motorista> listMotorista = new ArrayList<Motorista>();
        txtMotorista.removeAllItems();
        txtMotorista.addItem("0 - SELECIONE");
        if (transportadora.isEmpty()) {
            listMotorista = daoMot.getMotoristas("", "AND codtra >=0 AND codmtr in (4,16) ");
        } else {
            listMotorista = daoMot.getMotoristas("", "AND codtra = " + transportadora + "");
        }

        if (listMotorista != null) {
            for (Motorista mot : listMotorista) {
                cod = mot.getCodmtr().toString();
                des = mot.getNommot();
                desger = cod + " - " + des;
                txtMotorista.addItem(desger);
            }
        }
    }

    private void popularCampo() {
        minuta.setUsu_datemi(new Date());
        minuta.setUsu_codtra(Integer.valueOf(txtTransportadora.getText()));
        minuta.setUsu_codemp(Integer.valueOf(txtEmpresa.getText()));
        minuta.setUsu_codfil(Integer.valueOf(txtFilial.getText()));
        minuta.setUsu_codlan(Integer.valueOf(txtMinuta.getText()));
        minuta.setUsu_sitmin(txtSituacao.getSelectedItem().toString());
        minuta.setUsu_usuger(0);
        minuta.setUsu_qtdfat(txtQtdy.getDouble());
        minuta.setUsu_pesfat(txtPeso.getDouble());
        double vol = txtQtdVolume.getDouble();
        int volmin = (int) vol;
        minuta.setUsu_qtdvol(volmin);
        minuta.setUsu_plavei(txtPlaca.getText());
        String cod = txtMotorista.getSelectedItem().toString();
        int index = cod.indexOf("-");
        String codcon = cod.substring(0, index);
        minuta.setUsu_codmtr(Integer.valueOf(codcon.trim()));
        minuta.setUsu_datlib(txtDataLiberacao.getDate());
        minuta.setUsu_datsai(txtEmbarque.getDate());
        if (txtObservacao.getValue() != null) {
            minuta.setUsu_obsmin(txtObservacao.getValue().toString());
        }
    }

    private void gravar() throws SQLException, Exception {
        popularCampo();
        if (addReg) {
            minuta.setUsu_codlan(this.minutaDAO.proxCodCad());
            if (!this.minutaDAO.inserir(minuta)) {
            } else {
                txtMinuta.setText(String.valueOf(minuta.getUsu_codlan()));
                selecionarRange();
            }
        } else {
            if (!this.minutaDAO.alterar(minuta)) {

            } else {
                // selecionarRangeLiberarEntrega();

            }
        }

    }

    private void gravarPesoBalanca() throws SQLException, Exception {

        if (!this.minutaDAO.gravarDadosPesagem(minuta)) {

        } else {
            // selecionarRangeLiberarEntrega();

        }

    }

    private void getNota(String PESQUISA_POR, String PESQUISA) throws SQLException {
        SucataAnalisesDAO sucataanalisesDAO = new SucataAnalisesDAO();
        sucataanalises = new SucataAnalises();
        sucataanalises = sucataanalisesDAO.getSucataAnalises(PESQUISA_POR, PESQUISA);
        if (sucataanalises != null) {
            if (sucataanalises.getNota() > 0) {

            }
        }
    }

    public void selecionarRange() throws SQLException, Exception {
        if (jTableCarga.getRowCount() > 0) {
            PedidoDAO daoPedido = new PedidoDAO();
            NotaFiscalDAO daoNota = new NotaFiscalDAO();

            String numeroPedido = "0";

            Integer qtdreg = jTableCarga.getRowCount();
            Integer contador = 0;

            if (jTableCarga.getRowCount() > 0) {
                for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                    if ((Boolean) jTableCarga.getValueAt(i, 5)) {
                        numeroPedido = (jTableCarga.getValueAt(i, 12).toString());
                        if (!numeroPedido.isEmpty()) { // tem pedido
                            MinutaPedido minutaPedido = new MinutaPedido();
                            minutaPedido.setCadMinuta(minuta);
                            minutaPedido.setUsu_numnfv(Integer.valueOf(jTableCarga.getValueAt(i, 1).toString()));
                            minutaPedido.setUsu_qtdped(Double.valueOf(jTableCarga.getValueAt(i, 3).toString()));
                            minutaPedido.setUsu_pesped(Double.valueOf(jTableCarga.getValueAt(i, 4).toString()));
                            minutaPedido.setUsu_codcli(Integer.valueOf(jTableCarga.getValueAt(i, 6).toString()));
                            Cliente cli = new Cliente();
                            cli.setCodigo(minutaPedido.getUsu_codcli());
                            cli.setNome(jTableCarga.getValueAt(i, 7).toString());
                            cli.setCidade(jTableCarga.getValueAt(i, 8).toString());
                            cli.setEstado(jTableCarga.getValueAt(i, 9).toString());

                            minutaPedido.setCadCliente(cli);
                            minutaPedido.setUsu_codemp(1);

                            minutaPedido.setUsu_codtpr("");

                            minutaPedido.setUsu_pesnfv(minutaPedido.getUsu_pesped());

                            minutaPedido.setUsu_numped(Integer.valueOf(numeroPedido));
                            minutaPedido.setUsu_obsmin("GERANDO MINUTA NOTA");

                            minutaPedido.setUsu_pessuc(minutaPedido.getUsu_pesped());
                            minutaPedido.setUsu_qtdfat(minutaPedido.getUsu_qtdped());

                            minutaPedido.setUsu_qtdvol(txtQtdVolume.getDouble());

                            minutaPedido.setUsu_sitmin(txtSituacao.getSelectedItem().toString());

                            minutaPedido.setUsu_codfil(Integer.valueOf(jTableCarga.getValueAt(i, 14).toString()));
                            if (jTableCarga.getValueAt(i, 15).toString() == null) {
                                minutaPedido.setUsu_tnspro(" ");
                            } else {
                                minutaPedido.setUsu_tnspro(jTableCarga.getValueAt(i, 15).toString());

                            }

                            if (jTableCarga.getValueAt(i, 17).toString() != null) {
                                minutaPedido.setUsu_codori(jTableCarga.getValueAt(i, 17).toString());
                            } else {
                                minutaPedido.setUsu_codori(" ");
                            }
                            if (jTableCarga.getValueAt(i, 18).toString() != null) {
                                minutaPedido.setUsu_codsnf(jTableCarga.getValueAt(i, 18).toString());
                            } else {
                                minutaPedido.setUsu_codsnf(" ");
                            }

                            minutaPedido.setUsu_lansuc(Integer.valueOf(jTableCarga.getValueAt(i, 19).toString()));

                            minutaPedido.setUsu_codpes(0);
                            minutaPedido.setUsu_pesbal(0.0);
                            minutaPedido.setUsu_pesrec(0.0);
                            minutaPedido.setUsu_datemi(new Date());
                            minutaPedido.setUsu_datlib(new Date());
                            minutaPedido.setUsu_codlan(this.minuta.getUsu_codlan());
                            minutaPedido.setUsu_seqite(minutaPedidoDAO.proxCodCad());
                            contador++;
                            if (!minutaPedidoDAO.inserir(minutaPedido, qtdreg, contador)) {

                            } else {
                                pedido = new Pedido();
                                pedido.setEmpresa(minutaPedido.getUsu_codemp());
                                pedido.setFilial(minutaPedido.getUsu_codfil());
                                pedido.setPedido(minutaPedido.getUsu_numped());
                                pedido.setCliente(minutaPedido.getUsu_codcli());
                                pedido.setLiberarMinuta("S");
                                pedido.setLiberadoHub("S");
                                daoPedido.AtualizarMinuta(pedido);

                                notafiscal = new NotaFiscal();
                                notafiscal.setEmpresa(minutaPedido.getUsu_codemp());
                                notafiscal.setFilial(minutaPedido.getUsu_codfil());
                                notafiscal.setNotafiscal(minutaPedido.getUsu_numnfv());
                                notafiscal.setSerie(jTableCarga.getValueAt(i, 18).toString());
                                daoNota.alterarNotaMinuta(notafiscal, qtdreg, contador);

                                gravarApp(minutaPedido, qtdreg, contador);

                            }

                        }

                    }
                }

            }
        }
    }

    public void gravarAppProdutos(PedidoHub ped) throws SQLException {

        if (ped != null) {
            if (ped.getPedido() > 0) {
                PedidoProdutoDAO dao = new PedidoProdutoDAO();
                listPedidoProduto = dao.getPedidoProdutos("pedido", "\n and ped.numped  in (" + ped.getPedido() + ")");
                if (listPedidoProduto != null) {
                    if (listPedidoProduto.size() > 0) {
                        PedidoHubDAO pedDao = new PedidoHubDAO();
                        if (pedDao.inserirProduto(ped, listPedidoProduto, 0, 0)) {

                        }
                    }
                }
            }
        }

    }

    public void gravarApp(MinutaPedido minutaPedido, int qtdreg, int contador) throws SQLException {
        PedidoHub ped = new PedidoHub();
        PedidoHubDAO pedDao = new PedidoHubDAO();
        ped.setEmpresa(minutaPedido.getUsu_codemp());
        ped.setFilial(minutaPedido.getUsu_codfil());
        ped.setPedido(minutaPedido.getUsu_numped());
        ped.setCliente(minutaPedido.getUsu_codcli());
        ped.setClientenome(minutaPedido.getCadCliente().getNome());

        if (this.minuta.getUsu_codmtr() == 4) {
            ped.setCodigoacesso(44);
        }
        if (this.minuta.getUsu_codmtr() == 16) {
            ped.setCodigoacesso(47);
        }
        if (txtTransportadora.getText().equals("9382")) {
            ped.setCodigoacesso(46);
        }
        if (txtTransportadora.getText().equals("9359")) {
            ped.setCodigoacesso(46);
        }

        ped.setCidade(minutaPedido.getCadCliente().getCidade());
        ped.setEstado(minutaPedido.getCadCliente().getEstado());
        ped.setDataenvio(new Date());
        ped.setDataretorno(null);
        ped.setDataseparacao(new Date());

        ped.setId(0);
        ped.setNotafiscal(minutaPedido.getUsu_numnfv());

        ped.setPesorecebido(0.0);
        ped.setPesosaldo(0.0);
        ped.setPesosucata(minutaPedido.getUsu_pesnfv());

        ped.setQuantidade(minutaPedido.getUsu_qtdfat());
        ped.setSucata_id(minutaPedido.getUsu_lansuc());
        ped.setOperacao("PEDIDO ENVIADO PARA O HUB SEPARAR");
        ped.setSituacao("SEPARAR");
        ped.setSituacaoserie("SEPARAR");
        ped.setSerienota(minutaPedido.getUsu_codsnf());

        if (ped.getNotafiscal() > 0) {
            ped.setOperacao("PEDIDO ENVIADO PARA O HUB ENTREGAR");
            ped.setSituacao("FATURADO");
            ped.setSituacaoserie("SEPARADO");
        }
        ped.setId(pedDao.proxCodCadPed());
        if (!pedDao.inserir(ped, qtdreg, contador)) {
        } else {
            if (ped.getNotafiscal() == 0) {
                if (!PROCESSO.equals("LOGISTICA")) {
                    gravarAppProdutos(ped);
                }

            }

        }

    }

    public void selecionarRangeLiberarEntrega() throws SQLException, Exception {
        if (jTableCarga.getRowCount() > 0) {
            PedidoHubDAO pedDao = new PedidoHubDAO();

            String numeroPedido = "0";
            Integer qtdreg = jTableCarga.getRowCount();
            Integer contador = 0;

            if (jTableCarga.getRowCount() > 0) {
                for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                    if ((Boolean) jTableCarga.getValueAt(i, 5)) {
                        numeroPedido = (jTableCarga.getValueAt(i, 12).toString());
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

    private void getPedido(String PESQUISA_POR, String PESQUISA) throws SQLException {
        pedido = new Pedido();
        PedidoDAO pedidoDAO = new PedidoDAO();
        pedido = pedidoDAO.getPedido(PESQUISA_POR, PESQUISA);
        if (pedido != null) {
            if (pedido.getPedido() > 0) {

            }
        }
    }

    public void selecionarRanges() throws SQLException, Exception {
        Double pesoSelecionado = 0.0;
        String selecionar = "";
        if (jTableCarga.getRowCount() > 0) {
            for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                if ((Boolean) jTableCarga.getValueAt(i, 4)) {
                    selecionar += (jTableCarga.getValueAt(i, 1).toString() + ",");
                    pesoSelecionado += Double.valueOf(jTableCarga.getValueAt(i, 3).toString());
                }
            }
            int tam = selecionar.length();
            selecionar = selecionar.substring(0, tam - 1);

            btnGravar.setEnabled(false);
            if (pesoSelecionado > 0) {
                btnGravar.setEnabled(true);
            }
        }
    }

    private void sair() {
        if (veioCampoEmbarque != null) {
            try {
                veioCampoEmbarque.retornarMinuta();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }

        if (veioCampoLogPedido != null) {
            try {
                veioCampoLogPedido.retornarMinuta();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }

        if (veioCampoSucataTriagem != null) {
            try {
                veioCampoSucataTriagem.retornarMinuta();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }

        if (veioCampoFatPedidoHub != null) {
            try {
                //  veioCampoFatPedidoHub.retornarMinuta();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }
    }

    public void getListarMinuta(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listminutaPedido = this.minutaPedidoDAO.getMinutaPedidos(PESQUISA_POR, PESQUISA);
        if (listminutaPedido != null) {
            carregarTabelaMinutaNota();
        }
    }

    public void carregarTabelaMinutaNota() throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        for (MinutaPedido mp : listminutaPedido) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableCarga.getColumnModel();
            frmMinutasGerar.JTableRenderer renderers = new frmMinutasGerar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            linha[0] = BomIcon;
            linha[1] = mp.getUsu_numnfv();
            linha[2] = mp.getEmissaoS();
            linha[3] = mp.getUsu_qtdfat();
            linha[4] = mp.getUsu_pesnfv();

            linha[5] = true;
            linha[6] = mp.getUsu_codcli();

            linha[7] = mp.getCadCliente().getNome();
            linha[8] = mp.getCadCliente().getCidade();
            linha[9] = mp.getCadCliente().getEstado();
            linha[10] = txtTransportadora.getText();
            linha[11] = txtNomeTransportadora.getText();
            linha[12] = mp.getUsu_numped();
            linha[13] = mp.getUsu_codemp();
            linha[14] = mp.getUsu_codfil();
            linha[15] = mp.getUsu_tnspro();
            linha[16] = mp.getEmissaoS();
            linha[17] = mp.getUsu_codori();
            linha[18] = mp.getUsu_codsnf();
            linha[19] = mp.getUsu_lansuc();

            modeloCarga.addRow(linha);
        }

    }

    public void getPedidosProdutos() throws SQLException, Exception {
        PedidoProdutoDAO dao = new PedidoProdutoDAO();
        String selecionar = "";
        if (jTableCarga.getRowCount() > 0) {
            for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                selecionar += (jTableCarga.getValueAt(i, 12).toString() + ",");
            }
            int tam = selecionar.length();
            if (tam > 0) {
                if (!selecionar.isEmpty()) {
                    selecionar = selecionar.substring(0, tam - 1);
                    listPedidoProduto = dao.getPedidoProdutos("pedido", "\n and ped.numped  in (" + selecionar + ")");
                    if (listPedidoProduto != null) {
                        if (listPedidoProduto.size() > 0) {
                            carregarTabelaProduto();
                        }
                    }
                }
            }
        }
    }

    public void carregarTabelaProduto() throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTabPro.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        for (PedidoHubProduto mp : listPedidoProduto) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTabPro.getColumnModel();
            frmMinutasGerar.JTableRenderer renderers = new frmMinutasGerar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            linha[0] = BomIcon;
            linha[1] = mp.getPedido();
            linha[2] = mp.getProduto();
            linha[3] = mp.getDescricao();
            linha[4] = mp.getDatapedidoS();
            linha[5] = mp.getQuantidade();
            linha[6] = mp.getPesoproduto();
            linha[7] = mp.getPesopedido();

            modeloCarga.addRow(linha);
        }

    }

    public void setRecebePalavra(LogMinutaEmbarqueNota veioInput,
            String PROCESSO,
            String NOTAS,
            String CODIGOTRA,
            String NOMETRA,
            String DATAINI,
            String DATAFIM,
            String EMPRESA,
            String FILIAL) throws Exception {
        this.veioCampo = veioInput;
        txtPeso.setValue(0);

        txtEmpresa.setText(EMPRESA);
        txtFilial.setText(FILIAL);

        txtTransportadora.setText(CODIGOTRA);
        txtNomeTransportadora.setText(NOMETRA);

        txtTransportadora.requestFocus();
        addReg = true;
        txtEmissao.setDate(new Date());

        this.minuta = new Minuta();

    }

    public void setRecebePalavraPedido(LogPedido veioInput,
            String PROCESSO,
            String PEDIDOS,
            Minuta minuta,
            List<MinutaPedido> listminutaPedido) throws Exception {
        this.veioCampoLogPedido = veioInput;
        this.minuta = new Minuta();
        this.minuta = minuta;
        this.transportadora = new Transportadora();
        this.transportadora = minuta.getCadTransportadora();
        //   this.listminutaPedido = listminutaPedido;
        this.PROCESSO = PROCESSO;

        if (listminutaPedido.size() > 0) {
            this.listminutaPedido = listminutaPedido;
            popularTela();
            getPedidosProdutos();
        }

    }

    public void setRecebePalavraNota(SucataTriagem veioInput,
            String PROCESSO,
            String PEDIDOS,
            Minuta minuta,
            List<MinutaPedido> listminutaPedido) throws Exception {
        this.veioCampoSucataTriagem = veioInput;
        this.minuta = new Minuta();
        this.minuta = minuta;
        this.transportadora = new Transportadora();
        this.transportadora = minuta.getCadTransportadora();
        if (minuta != null) {
            this.listminutaPedido = listminutaPedido;
            popularTela();
        }

    }

    public void setRecebePalavraPedido(FatPedidoHub veioInput,
            String PROCESSO) throws Exception {
        this.veioCampoFatPedidoHub = veioInput;
        this.minuta = new Minuta();

    }

    public void setRecebePalavraPedidoHub(FatPedidoHub veioInput,
            String PROCESSO,
            String PEDIDOS,
            Minuta minuta,
            List<MinutaPedido> listminutaPedido) throws Exception {
        this.veioCampoFatPedidoHub = veioInput;
        this.minuta = new Minuta();
        this.minuta = minuta;
        this.transportadora = new Transportadora();
        this.transportadora = minuta.getCadTransportadora();
        this.minutaHub = true;
        if (listminutaPedido.size() > 0) {
            this.listminutaPedido = listminutaPedido;
            popularTela();
            getPedidosProdutos();
        }
    }

    public void setRecebePalavraManutencao(frmMinutas veioInput,
            Minuta minuta) throws Exception {
        desabiltar(false);
        this.veioCampoEmbarque = veioInput;
        this.minuta = new Minuta();
        this.minuta = minuta;
        if (minuta != null) {

            popularTela();
            this.listminutaPedido = minutaPedidoDAO.getMinutaPedidos(" MINUTAS ", " and minI.usu_codlan = " + txtMinuta.getText().trim());
            getPedidosProdutos();
            if (this.minuta.getUsu_codlan() > 0) {
                txtCodigoTicket.setEnabled(true);
                txtPesoBalanca.setEnabled(false);
                txtPesoBalanca.setValue(0);
                txtSaldo.setValue(0);
                if (this.minuta.getUsu_ticbal() > 0) {
                    txtCodigoTicket.setEnabled(false);
                    txtCodigoTicket.setText(minuta.getUsu_ticbal().toString());
                    txtPesoBalanca.setValue(minuta.getUsu_pesbal());
                    txtSaldo.setValue(minuta.getUsu_pesbalsal());
                    lblAnalise.setText(minuta.getUsu_obsmin());
                    Mensagem.mensagem("Atenção", " Peso de balança ja informado para essa minuta");
                }
                txtCodigoTicket.requestFocus();
            }
        }

    }

    private void desabiltar(boolean acao) {
        txtTransportadora.setEnabled(acao);
        txtPlaca.setEnabled(acao);
        txtDataLiberacao.setEnabled(acao);
        txtQtdVolume.setEnabled(acao);
        txtEntrega.setEnabled(acao);
        txtEmbarque.setEnabled(acao);
        txtHorEmb.setEnabled(acao);
        txtCodigoTicket.setEnabled(acao);
        btnIncluir.setEnabled(acao);
        btnGravar.setEnabled(acao);
    }

    private void popularTela() throws Exception {
        if (minuta.getUsu_codlan() > 0 || minuta.getUsu_codfil() > 0) {
            addReg = true;
            txtEmissao.setDate(minuta.getUsu_datemi());
            txtPeso.setValue(minuta.getUsu_pesfat());
            txtPesoSelecionado.setValue(minuta.getUsu_pesfat());
            txtQtdy.setValue(minuta.getUsu_qtdfat());
            txtObservacao.setText(minuta.getUsu_obsmin());
            txtMinuta.setText(minuta.getUsu_codlan().toString());
            txtEmpresa.setText(minuta.getUsu_codemp().toString());
            txtFilial.setText(minuta.getUsu_codfil().toString());
            txtPlaca.setText(minuta.getUsu_plavei());
            txtQtdVolume.setValue(minuta.getUsu_qtdvol());
            txtSituacao.setSelectedItem(minuta.getUsu_sitmin());
            if (minuta.getUsu_codtra() > 0) {
                txtTransportadora.setText(minuta.getUsu_codtra().toString());
                pesquisarTransportadora(txtTransportadora.getText());
                preencherCombo(txtTransportadora.getText());
                if (minuta.getUsu_codmtr() > 0) {
                    pesquisarMotorista(minuta.getUsu_codmtr().toString());
                }
            }

            btnIncluir.setEnabled(true);
            btnGravar.setEnabled(false);
            if (minuta.getUsu_codlan() > 0) {
                btnGravar.setEnabled(true);
                btnIncluir.setEnabled(false);
                if (!minuta.getUsu_sitmin().equals("ABERTA")) {
                    btnGravar.setEnabled(false);
                    txtMotorista.setEnabled(false);
                    txtSituacao.setEnabled(false);
                }
                getListarMinuta(" minuta ", " and usu_codlan = " + minuta.getUsu_codlan());
            } else {
                txtEmissao.setDate(new Date());
                txtSituacao.setSelectedItem("ABERTA");
                txtTransportadora.setEnabled(true);
                if (listminutaPedido != null) {
                    if (listminutaPedido.size() > 0) {
                        carregarTabelaMinutaNota();
                    }
                }
            }

        }
    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableCarga.setRowHeight(40);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(14).setCellRenderer(direita);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoCreateRowSorter(true);
        jTableCarga.setAutoResizeMode(0);

        jTabPro.setRowHeight(40);
        jTabPro.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTabPro.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTabPro.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTabPro.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTabPro.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTabPro.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTabPro.setAutoCreateRowSorter(true);
        jTabPro.setAutoResizeMode(1);

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public final void gerarRelatatorio(String valor) throws SQLException, Exception {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            try {
                String Data = "";
                String arquivo = txtMinuta.getText();
                String relatorio = "RFNF600";

                String entrada = "<ECodLan=" + txtMinuta.getText() + ">";
                String diretorio = "\\\\SRV-SPNS01\\Senior_ERP\\Sapiens\\Relatorios\\Logistica\\";
                WSRelatorio.chamarMetodoWsXmlHttpSapiens(arquivo, relatorio, entrada, diretorio,"tsfNormal");

                desktop.open(new File(diretorio + txtMinuta.getText() + ".IMP"));
            } catch (Exception ex) {
                Logger.getLogger(frmMinutas.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado");
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon getImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return (new ImageIcon(url));
        } else {
            return null;

        }
    }

    private void pesquisarTransportadora(String text) throws SQLException {
        TransportadoraDAO dao = new TransportadoraDAO();
        transportadora = new Transportadora();
        this.transportadora = dao.getTransportadora(" CodTra ", " and codtra = " + txtTransportadora.getText());
        if (transportadora != null) {
            if (transportadora.getCodigoTransportadora() > 0) {
                txtNomeTransportadora.setText(transportadora.getNomeTransportadora());
                preencherCombo(txtTransportadora.getText());
                btnIncluir.setEnabled(true);
                txtPlaca.requestFocus();
            }
        }
    }

    private void pesquisarMotorista(String text) throws SQLException {
        MotoristaDAO dao = new MotoristaDAO();
        Motorista motorista = new Motorista();
        motorista = dao.getMotorista(" mot ", " and codmtr = " + text);
        if (motorista != null) {
            if (motorista.getCodmtr() > 0) {
                txtMotorista.setSelectedItem(motorista.getCodmtr() + " - " + motorista.getNommot());

            }
        }
    }

    private void saldoSucata() throws SQLException, Exception {

        double saldo = 0;
        if (txtCodigoTicket.getText().isEmpty()) {
            txtCodigoTicket.setText("0");
            txtPesoBalanca.setValue(0);
            txtSaldo.setValue(saldo);
            lblAnalise.setText("INFORME UM CÓDIGO VALIDO");
            Mensagem.mensagem("ERROR", "INFORME UM CÓDIGO VALIDO");
        } else {
            CargaRegistro cargaRegistro = new CargaRegistro();
            CargaAberturaDAO dao = new CargaAberturaDAO();
            cargaRegistro = dao.getCargaRegistro("PLACA", " and usu_nrocar = '" + txtCodigoTicket.getText() + "'");
            if (cargaRegistro != null) {
                if (cargaRegistro.getNumerocarga() > 0) {
                    txtPesoBalanca.setValue(cargaRegistro.getPesoLiquidoCarga());
                    saldo = cargaRegistro.getPesoLiquidoCarga() - minuta.getUsu_pesfat();
                    txtSaldo.setValue(saldo);
                    btnAceitar.setEnabled(false);
                    minuta.setUsu_pesbal(cargaRegistro.getPesoLiquidoCarga());
                    minuta.setUsu_pesbalsal(saldo);
                    minuta.setUsu_ticbal(cargaRegistro.getNumerocarga());

                    if (saldo < 0) {
                        minuta.setUsu_sitsuc("COM_DIVERGENCIA");
                        minuta.setUsu_sitmin("CONCLUIDA");
                        lblAnalise.setText("PESO DE SUCATA ESTA MENOR");
                        minuta.setUsu_obspes(lblAnalise.getText());
                        minuta.setUsu_libmindiv(0);//NÃO LIBERADO
                        btnAceitar.setEnabled(true);
                        gravarPesoBalanca();
                        Mensagem.mensagem("ERROR", "PESO DE SUCATA ESTA MENOR");

                    } else {
                        minuta.setUsu_sitsuc("SEM_DIVERGENCIA");
                        minuta.setUsu_sitmin("CONCLUIDA");
                        lblAnalise.setText("PESO DE SUCATA OK");
                        minuta.setUsu_libmindiv(1);//LIBERADO
                        minuta.setUsu_obspes(lblAnalise.getText());

                        gravarPesoBalanca();
                        Mensagem.mensagem("OK", "PESO DE SUCATA OK");
                    }
                }
            }

        }

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
            if ("NC".equals(str)) {
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

        jTabbedCotacao = new javax.swing.JTabbedPane();
        pnlForm = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtEmbarque = new org.openswing.swing.client.DateControl();
        txtTransportadora = new org.openswing.swing.client.TextControl();
        btnGravar = new javax.swing.JButton();
        txtHorEmb = new org.openswing.swing.client.TextControl();
        btnHoras = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        txtPeso = new org.openswing.swing.client.NumericControl();
        txtEntrega = new org.openswing.swing.client.DateControl();
        txtQtdy = new org.openswing.swing.client.NumericControl();
        txtNomeTransportadora = new org.openswing.swing.client.TextControl();
        txtSituacao = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtEmissao = new org.openswing.swing.client.DateControl();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtMinuta = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        txtEmpresa = new org.openswing.swing.client.TextControl();
        txtFilial = new org.openswing.swing.client.TextControl();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPesoSelecionado = new org.openswing.swing.client.NumericControl();
        jLabel5 = new javax.swing.JLabel();
        txtPlaca = new org.openswing.swing.client.TextControl();
        jLabel6 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtDataLiberacao = new org.openswing.swing.client.DateControl();
        jLabel16 = new javax.swing.JLabel();
        txtQtdVolume = new org.openswing.swing.client.NumericControl();
        btnIncluir = new javax.swing.JButton();
        txtMotorista = new javax.swing.JComboBox<>();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pPed = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        pPro = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTabPro = new javax.swing.JTable();
        pSer = new javax.swing.JPanel();
        pInf = new javax.swing.JPanel();
        txtObservacao = new org.openswing.swing.client.TextAreaControl();
        txtPesoBalanca = new org.openswing.swing.client.NumericControl();
        txtSaldo = new org.openswing.swing.client.NumericControl();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblAnalise = new javax.swing.JLabel();
        btnAceitar = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txtCodigoTicket = new org.openswing.swing.client.TextControl();
        jButton3 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jTabbedCotacao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedCotacaoMouseClicked(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setForeground(new java.awt.Color(0, 0, 153));

        jLabel7.setText("Transportadora");

        txtEmbarque.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEmbarque.setRequired(true);

        txtTransportadora.setEnabled(false);
        txtTransportadora.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTransportadora.setRequired(true);
        txtTransportadora.setUpperCase(true);
        txtTransportadora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransportadoraActionPerformed(evt);
            }
        });

        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravar.setEnabled(false);
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        txtHorEmb.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnHoras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        btnHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHorasActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtPeso.setDecimals(2);
        txtPeso.setEnabled(false);
        txtPeso.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPeso.setTextAlignment(SwingConstants.RIGHT);

        txtEntrega.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEntrega.setRequired(true);

        txtQtdy.setDecimals(2);
        txtQtdy.setEnabled(false);
        txtQtdy.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNomeTransportadora.setEnabled(false);
        txtNomeTransportadora.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNomeTransportadora.setUpperCase(true);
        txtNomeTransportadora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeTransportadoraActionPerformed(evt);
            }
        });

        txtSituacao.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ABERTA", "LIBERADA", "EXCLUIDA", "CONCLUIDA" }));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        jButton2.setText("Sair");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel4.setText("Situação");

        txtEmissao.setEnabled(false);
        txtEmissao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel8.setText("Emissão");

        jLabel10.setText("Volumes");

        jLabel12.setText("Quantidade");

        jLabel9.setText("Entrega");

        jLabel13.setText("Embarque");

        jLabel14.setText("Hora");

        txtMinuta.setEnabled(false);
        txtMinuta.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel1.setText("Minuta");

        txtEmpresa.setEnabled(false);
        txtEmpresa.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtFilial.setEnabled(false);
        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel2.setText("Empresa");

        jLabel3.setText("Filial");

        txtPesoSelecionado.setDecimals(2);
        txtPesoSelecionado.setEnabled(false);
        txtPesoSelecionado.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel5.setText("Peso");

        txtPlaca.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPlaca.setRequired(true);
        txtPlaca.setUpperCase(true);
        txtPlaca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPlacaActionPerformed(evt);
            }
        });

        jLabel6.setText("Placa");

        jLabel11.setText("Motorista");

        txtDataLiberacao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtDataLiberacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataLiberacaoActionPerformed(evt);
            }
        });

        jLabel16.setText("Liberação");

        txtQtdVolume.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnIncluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnIncluir.setText("Incluir");
        btnIncluir.setEnabled(false);
        btnIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIncluirActionPerformed(evt);
            }
        });

        txtMotorista.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtMotorista.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtMotorista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMotoristaActionPerformed(evt);
            }
        });

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Nota", "Emissão", "Quantidade", "Peso", "Selecionar", "Cliente", "Nome", "Cidade", "Estado", "Transp.", "Nome ", "Pedido", "Empresa", "Filial", "Transação", "Emissão Pedido", "Categoria", "Serie Nota", "Lan. Sucata"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
        jScrollPane1.setViewportView(jTableCarga);
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
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(400);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(400);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(400);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(200);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(200);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(200);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(16).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(16).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(16).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(17).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(17).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(17).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(19).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setMaxWidth(100);
        }

        javax.swing.GroupLayout pPedLayout = new javax.swing.GroupLayout(pPed);
        pPed.setLayout(pPedLayout);
        pPedLayout.setHorizontalGroup(
            pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        pPedLayout.setVerticalGroup(
            pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Pedidos", pPed);

        jTabPro.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Pedido", "Produto", "Descrição", "Entrega", "Quantidade", "Peso", "Peso Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTabPro);
        if (jTabPro.getColumnModel().getColumnCount() > 0) {
            jTabPro.getColumnModel().getColumn(0).setMinWidth(50);
            jTabPro.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTabPro.getColumnModel().getColumn(0).setMaxWidth(50);
            jTabPro.getColumnModel().getColumn(1).setMinWidth(100);
            jTabPro.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTabPro.getColumnModel().getColumn(1).setMaxWidth(100);
            jTabPro.getColumnModel().getColumn(2).setMinWidth(100);
            jTabPro.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTabPro.getColumnModel().getColumn(2).setMaxWidth(100);
            jTabPro.getColumnModel().getColumn(4).setMinWidth(100);
            jTabPro.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTabPro.getColumnModel().getColumn(4).setMaxWidth(100);
            jTabPro.getColumnModel().getColumn(5).setMinWidth(100);
            jTabPro.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTabPro.getColumnModel().getColumn(5).setMaxWidth(100);
            jTabPro.getColumnModel().getColumn(6).setMinWidth(100);
            jTabPro.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTabPro.getColumnModel().getColumn(6).setMaxWidth(100);
            jTabPro.getColumnModel().getColumn(7).setMinWidth(100);
            jTabPro.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTabPro.getColumnModel().getColumn(7).setMaxWidth(100);
        }

        javax.swing.GroupLayout pProLayout = new javax.swing.GroupLayout(pPro);
        pPro.setLayout(pProLayout);
        pProLayout.setHorizontalGroup(
            pProLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1145, Short.MAX_VALUE)
        );
        pProLayout.setVerticalGroup(
            pProLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pProLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        jTabbedPane1.addTab("Produtos", pPro);

        javax.swing.GroupLayout pSerLayout = new javax.swing.GroupLayout(pSer);
        pSer.setLayout(pSerLayout);
        pSerLayout.setHorizontalGroup(
            pSerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1145, Short.MAX_VALUE)
        );
        pSerLayout.setVerticalGroup(
            pSerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 204, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Séries", pSer);

        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout pInfLayout = new javax.swing.GroupLayout(pInf);
        pInf.setLayout(pInfLayout);
        pInfLayout.setHorizontalGroup(
            pInfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pInfLayout.setVerticalGroup(
            pInfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Info", pInf);

        txtPesoBalanca.setDecimals(2);
        txtPesoBalanca.setEnabled(false);
        txtPesoBalanca.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPesoBalanca.setTextAlignment(SwingConstants.RIGHT);
        txtPesoBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoBalancaActionPerformed(evt);
            }
        });

        txtSaldo.setDecimals(2);
        txtSaldo.setEnabled(false);
        txtSaldo.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSaldo.setTextAlignment(SwingConstants.RIGHT);

        jLabel15.setText("Peso Total Minuta");

        jLabel17.setText("Peso Balança");

        jLabel18.setText("Saldo");

        lblAnalise.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAceitar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        btnAceitar.setText("Aceitar");
        btnAceitar.setEnabled(false);
        btnAceitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceitarActionPerformed(evt);
            }
        });

        jLabel20.setText("Analise");

        txtCodigoTicket.setEnabled(false);
        txtCodigoTicket.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtCodigoTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoTicketActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/balanca_industrial.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel19.setText("Ticket Pesagem");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(225, 225, 225))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtTransportadora, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6)
                                    .addComponent(txtPlaca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNomeTransportadora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtMotorista, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(8, 8, 8))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtDataLiberacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtSituacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2))
                                        .addGap(8, 8, 8)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel4))))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(txtMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8)
                                            .addComponent(txtEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(6, 6, 6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnGravar)
                        .addGap(4, 4, 4)
                        .addComponent(jButton4)
                        .addGap(10, 10, 10)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtCodigoTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))
                            .addComponent(jLabel19))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPesoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblAnalise, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAceitar)))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(txtQtdVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQtdy, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEntrega, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmbarque, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addContainerGap())
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtHorEmb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnIncluir, jButton2, jLabel7});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtEmpresa, txtFilial});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton1)
                                    .addComponent(txtTransportadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNomeTransportadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel11)
                            .addComponent(jLabel8)
                            .addComponent(jLabel4)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMotorista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataLiberacao, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel12)
                            .addComponent(jLabel9)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel5))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQtdy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHorEmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnHoras)
                            .addComponent(txtEmbarque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtQtdVolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAceitar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton4)
                        .addComponent(btnIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtPeso, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPesoBalanca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSaldo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblAnalise, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCodigoTicket, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGravar, btnIncluir, jButton2, jButton4});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtDataLiberacao, txtEmissao, txtMotorista, txtPlaca, txtSituacao});

        javax.swing.GroupLayout pnlFormLayout = new javax.swing.GroupLayout(pnlForm);
        pnlForm.setLayout(pnlFormLayout);
        pnlFormLayout.setHorizontalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        pnlFormLayout.setVerticalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jTabbedCotacao.addTab("Minutas", pnlForm);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedCotacao)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedCotacao, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jTabbedCotacaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedCotacaoMouseClicked
        //
    }//GEN-LAST:event_jTabbedCotacaoMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        sair();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtNomeTransportadoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeTransportadoraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeTransportadoraActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        try {

            int linhaSelSit = jTableCarga.getSelectedRow();
            int colunaSelSit = jTableCarga.getSelectedColumn();
            pedidoSelecionado = jTableCarga.getValueAt(linhaSelSit, 12).toString();
            PedidoProdutoDAO dao = new PedidoProdutoDAO();
            listPedidoProduto = dao.getPedidoProdutos("pedido", "\n and ped.numped  in (" + pedidoSelecionado + ")");
            if (listPedidoProduto != null) {
                if (listPedidoProduto.size() > 0) {
                    carregarTabelaProduto();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            pesquisarTransportadora(txtTransportadora.getText());
        } catch (SQLException ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHorasActionPerformed
        txtHorEmb.setText(utilDatas.retornarHoras(new Date()));
    }//GEN-LAST:event_btnHorasActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        try {
            if (ManipularRegistros.gravarRegistros(" Alterar ")) {
                addReg = false;
                gravar();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex);
        }
    }//GEN-LAST:event_btnGravarActionPerformed

    private void txtTransportadoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransportadoraActionPerformed
        try {
            pesquisarTransportadora(txtTransportadora.getText());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex);
        }
    }//GEN-LAST:event_txtTransportadoraActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            gerarRelatatorio(txtMinuta.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void txtPlacaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPlacaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPlacaActionPerformed

    private void btnIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIncluirActionPerformed

        String cod = txtMotorista.getSelectedItem().toString();
        int index = cod.indexOf("-");
        String codcon = cod.substring(0, index).trim();
        if (codcon.equals("0") && !this.minutaHub) {
            JOptionPane.showMessageDialog(null, "Selecione o motorista");
        } else {

            Object[] options = {" Sim ", " Não "};
            if (JOptionPane.showOptionDialog(this, "Gravar minuta ?", "Aviso:",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, options[1]) == JOptionPane.YES_OPTION) {
                try {
                    gravar();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "ERRO " + ex);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Processo cancelado");
            }
        }
    }//GEN-LAST:event_btnIncluirActionPerformed

    private void txtMotoristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMotoristaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMotoristaActionPerformed

    private void txtDataLiberacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataLiberacaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDataLiberacaoActionPerformed

    private void btnAceitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceitarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAceitarActionPerformed

    private void txtPesoBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoBalancaActionPerformed
        // saldoSucata();
    }//GEN-LAST:event_txtPesoBalancaActionPerformed

    private void txtCodigoTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoTicketActionPerformed
        try {
            saldoSucata();
        } catch (SQLException ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtCodigoTicketActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            saldoSucata();
        } catch (SQLException ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceitar;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnHoras;
    private javax.swing.JButton btnIncluir;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTabPro;
    private javax.swing.JTabbedPane jTabbedCotacao;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lblAnalise;
    private javax.swing.JPanel pInf;
    private javax.swing.JPanel pPed;
    private javax.swing.JPanel pPro;
    private javax.swing.JPanel pSer;
    private javax.swing.JPanel pnlForm;
    private org.openswing.swing.client.TextControl txtCodigoTicket;
    private org.openswing.swing.client.DateControl txtDataLiberacao;
    private org.openswing.swing.client.DateControl txtEmbarque;
    private org.openswing.swing.client.DateControl txtEmissao;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.DateControl txtEntrega;
    private org.openswing.swing.client.TextControl txtFilial;
    private org.openswing.swing.client.TextControl txtHorEmb;
    private org.openswing.swing.client.TextControl txtMinuta;
    private javax.swing.JComboBox<String> txtMotorista;
    private org.openswing.swing.client.TextControl txtNomeTransportadora;
    private org.openswing.swing.client.TextAreaControl txtObservacao;
    private org.openswing.swing.client.NumericControl txtPeso;
    private org.openswing.swing.client.NumericControl txtPesoBalanca;
    private org.openswing.swing.client.NumericControl txtPesoSelecionado;
    private org.openswing.swing.client.TextControl txtPlaca;
    private org.openswing.swing.client.NumericControl txtQtdVolume;
    private org.openswing.swing.client.NumericControl txtQtdy;
    private org.openswing.swing.client.NumericControl txtSaldo;
    private javax.swing.JComboBox<String> txtSituacao;
    private org.openswing.swing.client.TextControl txtTransportadora;
    // End of variables declaration//GEN-END:variables
}
