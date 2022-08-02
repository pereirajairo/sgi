/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.PedidoHubProduto;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.dao.MinutaDAO;
import br.com.sgi.dao.MinutaPedidoDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.PedidoProdutoDAO;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.ManipularRegistros;
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
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class frmMinutasFaturamentoGerar extends InternalFrame {

    private List<NotaFiscal> lstNotaFiscal = new ArrayList<NotaFiscal>();

    private Minuta minuta;
    private MinutaDAO minutaDAO;

    private MinutaPedido minutapedido;
    private List<MinutaPedido> listminutaPedido = new ArrayList<MinutaPedido>();
    private MinutaPedidoDAO minutaPedidoDAO;

    private List<PedidoHubProduto> listPedidoProduto = new ArrayList<PedidoHubProduto>();

    private Pedido pedido;
    private Transportadora transportadora;

    private FatPedido veioCampo;
    private frmMinutas veioCampoEmbarque;
    private UtilDatas utilDatas;

    private boolean addReg;
    private double pesoSelecionado = 0;
    private double quantidadeSelecionado = 0;
    private String pedidoSelecionado = "0";
    private String PROCESSO;

    public frmMinutasFaturamentoGerar() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Minutas notas"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (minutaDAO == null) {
                this.minutaDAO = new MinutaDAO();
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
//        txtEmbarque.setDate(new Date());
//        txtHorEmb.setText(utilDatas.retornarHoras(new Date()));
//        txtEntrega.setDate(new Date());
//        txtDataLiberacao.setDate(new Date());
    }

    public void preencherCombo(String transportadora) throws SQLException {
//        String cod = "";
//        String des = "";
//        String desger = "";
//
//        MotoristaDAO daoMot = new MotoristaDAO();
//        List<Motorista> listMotorista = new ArrayList<Motorista>();
//        txtMotorista.removeAllItems();
//        txtMotorista.addItem("0 - SELECIONE");
//        if (transportadora.isEmpty()) {
//            listMotorista = daoMot.getMotoristas("", "AND codtra >=0 AND codmtr in (4,16) ");
//        } else {
//            listMotorista = daoMot.getMotoristas("", "AND codtra = " + transportadora + "");
//        }
//
//        if (listMotorista != null) {
//            for (Motorista mot : listMotorista) {
//                cod = mot.getCodmtr().toString();
//                des = mot.getNommot();
//                desger = cod + " - " + des;
//                txtMotorista.addItem(desger);
//            }
//        }
    }

    private void popularCampo() {
        minuta.setUsu_datemi(new Date());
        minuta.setUsu_codtra(0);
        minuta.setUsu_codemp(Integer.valueOf(txtEmpresa.getText()));
        minuta.setUsu_codfil(Integer.valueOf(txtFilial.getText()));
        minuta.setUsu_codlan(Integer.valueOf(txtMinuta.getText()));
        minuta.setUsu_sitmin(txtSituacao.getSelectedItem().toString());
        minuta.setUsu_usuger(0);
        minuta.setUsu_qtdfat(txtQtdy.getDouble());
          double vol = txtQtdVolume.getDouble();
        int volmin = (int) vol;
        minuta.setUsu_qtdvol(volmin);
        minuta.setUsu_plavei("");
        minuta.setUsu_codmtr(0);
        minuta.setUsu_datlib(new Date());
        minuta.setUsu_datsai(new Date());
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

            }
        }

    }

    private void gravarPesoBalanca() throws SQLException, Exception {

        if (!this.minutaDAO.gravarDadosPesagem(minuta)) {

        } else {

        }

    }

    public void selecionarRange() throws SQLException, Exception {
        if (jTableCarga.getRowCount() > 0) {
            PedidoDAO daoPedido = new PedidoDAO();
            String numeroPedido = "0";
            Integer qtdreg = jTableCarga.getRowCount();
            Integer contador = 0;

            if (jTableCarga.getRowCount() > 0) {
                for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                    if ((Boolean) jTableCarga.getValueAt(i, 12)) {
                        numeroPedido = (jTableCarga.getValueAt(i, 1).toString());
                        if (!numeroPedido.isEmpty()) { // tem pedido
                            MinutaPedido minutaPedido = new MinutaPedido();
                            minutaPedido.setCadMinuta(minuta);
                            minutaPedido.setUsu_numnfv(0);
                            minutaPedido.setUsu_codcli(Integer.valueOf(jTableCarga.getValueAt(i, 4).toString()));
                            Cliente cli = new Cliente();
                            cli.setCodigo(minutaPedido.getUsu_codcli());
                            cli.setNome(jTableCarga.getValueAt(i, 5).toString());
                            cli.setCidade(jTableCarga.getValueAt(i, 7).toString());
                            cli.setEstado(jTableCarga.getValueAt(i, 7).toString());
                            minutaPedido.setCadCliente(cli);

                            minutaPedido.setUsu_pesped(Double.valueOf(jTableCarga.getValueAt(i, 13).toString()));
                            minutaPedido.setUsu_qtdped(Double.valueOf(jTableCarga.getValueAt(i, 14).toString()));

                            minutaPedido.setUsu_codemp(1);
                            minutaPedido.setUsu_codtpr("");
                            minutaPedido.setUsu_pesnfv(minutaPedido.getUsu_pesped());
                            minutaPedido.setUsu_numped(Integer.valueOf(numeroPedido));
                            minutaPedido.setUsu_obsmin("GERANDO MINUTA PEDIDO " + numeroPedido);
                            minutaPedido.setUsu_pessuc(minutaPedido.getUsu_pesped());
                            minutaPedido.setUsu_qtdfat(minutaPedido.getUsu_qtdped());
                            minutaPedido.setUsu_qtdvol(0.0);
                            minutaPedido.setUsu_sitmin(txtSituacao.getSelectedItem().toString());
                            minutaPedido.setUsu_codfil(Integer.valueOf(jTableCarga.getValueAt(i, 17).toString()));
                            minutaPedido.setUsu_tnspro("");
                            minutaPedido.setUsu_codori("");
                            minutaPedido.setUsu_codsnf("");
                            minutaPedido.setUsu_lansuc(0);
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
                                daoPedido.AtualizarMinuta(pedido);

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

    private void sair() {
        if (veioCampo != null) {
            try {
                veioCampo.retornarPedido();
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
            frmMinutasFaturamentoGerar.JTableRenderer renderers = new frmMinutasFaturamentoGerar.JTableRenderer();
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
//            linha[10] = txtTransportadora.getText();
            linha[11] = txtNomeMinuta.getText();
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
            frmMinutasFaturamentoGerar.JTableRenderer renderers = new frmMinutasFaturamentoGerar.JTableRenderer();
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

    public void setRecebePedido(FatPedido veioInput,
            List<Pedido> listPedido,
            String cliente) throws Exception {
        this.veioCampo = veioInput;
        txtEmpresa.setText("1");
        txtFilial.setText("1");
        txtEmissao.setDate(new Date());
        this.minuta = new Minuta();
        btnIncluir.setEnabled(false);
        btnGravar.setEnabled(false);
        if (listPedido != null) {
            if (listPedido.size() > 0) {
                txtNomeMinuta.setText(" Minuta Faturamento cliente " + cliente);
                carregarTabela(listPedido);
                btnIncluir.setEnabled(true);
                btnGravar.setEnabled(true);

            }
        }

    }

    public void carregarTabela(List<Pedido> listPedido) throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon MedIcon = getImage("/images/sitMedio.png");
        ImageIcon ReaIcon = getImage("/images/sitAnd.png");

        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon GarIcon = getImage("/images/ruby_delete.png");
        ImageIcon MktIcon = getImage("/images/bateriaindu.png");
        double peso = 0;
        double qtdy = 0;
        double qtdped = 0;

        for (Pedido ped : listPedido) {
            Object[] linha = new Object[23];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new PedidoEmbarque.ColorirRenderer();
            frmMinutasFaturamentoGerar.JTableRenderer renderers = new frmMinutasFaturamentoGerar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            columnModel.getColumn(11).setCellRenderer(renderers);
            linha[0] = RuimIcon;
            if (ped.getQtddia_atrazo() < 0) {
                linha[0] = BomIcon;
            }

            linha[1] = ped.getPedido();
            linha[2] = ped.getNumeropre();
            linha[3] = ped.getNumeroanalise();
            linha[4] = ped.getCliente();
            linha[5] = ped.getCadCliente().getNome();
            linha[6] = ped.getCadCliente().getEstado();
            linha[7] = ped.getCadCliente().getCidade();
            linha[8] = ped.getDataSeparacaoS();
            linha[9] = ped.getDia_transporte() + " D+1";

            linha[10] = ped.getData_para_faturarS();
            linha[11] = AutIcon;
            if (ped.getLinha().equals("BM")) {
                linha[11] = MotIcon;
            }
            if (ped.getLinha().equals("GAR")) {
                linha[11] = GarIcon;
            }
            if (ped.getLinha().equals("MKT")) {
                linha[11] = MktIcon;
            }
            linha[12] = true;

            linha[13] = ped.getPeso();
            peso += ped.getPeso();
            linha[14] = ped.getQuantidade();
            qtdy += ped.getQuantidade();
            linha[15] = ped.getSituacaoLogistica();
            linha[16] = ped.getEmpresa();
            linha[17] = ped.getFilial();
            linha[18] = ped.getEmissaoS();
            linha[19] = ped.getDataAgendamentoS();
            linha[20] = ped.getQtddia_atrazo();
            linha[21] = ped.getCadTransportadora().getCodigoTransportadora() + " - " + ped.getCadTransportadora().getNomeTransportadora();

            qtdped++;
            modeloCarga.addRow(linha);
        }
        txtPesoSelecionado.setText(FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM));
        txtQtdy.setText(FormatarPeso.mascaraPorcentagem(qtdy, FormatarPeso.PORCENTAGEM));

//        lblQtdyPedido.setText(FormatarPeso.mascaraPorcentagem(qtdped, FormatarPeso.PORCENTAGEM));
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
                if (this.minuta.getUsu_ticbal() > 0) {
                }
            }
        }

    }

    private void desabiltar(boolean acao) {
//        txtTransportadora.setEnabled(acao);
//        txtPlaca.setEnabled(acao);
//        txtDataLiberacao.setEnabled(acao);
//        txtQtdVolume.setEnabled(acao);
//        txtEntrega.setEnabled(acao);
//        txtEmbarque.setEnabled(acao);
//        txtHorEmb.setEnabled(acao);

        //  btnIncluir.setEnabled(acao);
        btnGravar.setEnabled(acao);
    }

    private void popularTela() throws Exception {
        if (minuta.getUsu_codlan() > 0 || minuta.getUsu_codfil() > 0) {
            addReg = true;
            txtEmissao.setDate(minuta.getUsu_datemi());

            txtPesoSelecionado.setValue(minuta.getUsu_pesfat());
            txtQtdy.setValue(minuta.getUsu_qtdfat());
            txtObservacao.setText(minuta.getUsu_obsmin());
            txtMinuta.setText(minuta.getUsu_codlan().toString());
            txtEmpresa.setText(minuta.getUsu_codemp().toString());
            txtFilial.setText(minuta.getUsu_codfil().toString());

            txtQtdVolume.setValue(minuta.getUsu_qtdvol());
            txtSituacao.setSelectedItem(minuta.getUsu_sitmin());

            if (this.minuta.getUsu_codtra() > 0) {
                preencherCombo("");
            }

            btnIncluir.setEnabled(true);
            btnGravar.setEnabled(false);
            if (minuta.getUsu_codlan() > 0) {
                btnGravar.setEnabled(true);
                btnIncluir.setEnabled(false);
                if (!minuta.getUsu_sitmin().equals("ABERTA")) {
                    btnGravar.setEnabled(false);
                    txtSituacao.setEnabled(false);
                }
                getListarMinuta(" minuta ", " and usu_codlan = " + minuta.getUsu_codlan());
            } else {
                txtEmissao.setDate(new Date());
                txtSituacao.setSelectedItem("ABERTA");

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
        jTableCarga.getColumnModel().getColumn(8).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(10).setCellRenderer(direita);
        //   jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
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
        btnGravar = new javax.swing.JButton();
        txtQtdy = new org.openswing.swing.client.NumericControl();
        txtNomeMinuta = new org.openswing.swing.client.TextControl();
        txtSituacao = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        txtEmissao = new org.openswing.swing.client.DateControl();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtMinuta = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        txtEmpresa = new org.openswing.swing.client.TextControl();
        txtFilial = new org.openswing.swing.client.TextControl();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPesoSelecionado = new org.openswing.swing.client.NumericControl();
        jLabel5 = new javax.swing.JLabel();
        txtQtdVolume = new org.openswing.swing.client.NumericControl();
        btnIncluir = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pPed = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        pPro = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTabPro = new javax.swing.JTable();
        pSer = new javax.swing.JPanel();
        txtObservacao = new org.openswing.swing.client.TextAreaControl();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtNomeTransportadora1 = new org.openswing.swing.client.TextControl();
        jButton1 = new javax.swing.JButton();

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

        jLabel7.setText("Descrição Minuta");

        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravar.setEnabled(false);
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        txtQtdy.setDecimals(2);
        txtQtdy.setEnabled(false);
        txtQtdy.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNomeMinuta.setEnabled(false);
        txtNomeMinuta.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNomeMinuta.setUpperCase(true);
        txtNomeMinuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeMinutaActionPerformed(evt);
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

        txtEmissao.setEnabled(false);
        txtEmissao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel8.setText("Emissão");

        jLabel10.setText("Volumes");

        jLabel12.setText("Quantidade");

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

        txtQtdVolume.setEnabled(false);
        txtQtdVolume.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnIncluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnIncluir.setText("Incluir");
        btnIncluir.setEnabled(false);
        btnIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIncluirActionPerformed(evt);
            }
        });

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Pedido", "Pré_fatura", "Analise", "Cliente", "Nome", "UF", "Cidade", "Data Pré", "Transporte", "Data Fat", "#", "Gerar", "Peso", "Quantidade", "Situação", "Empresa", "Filial", "Emissão", "Agendado", "Dias Atrazo", "Transportadora"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(300);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(300);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(300);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(16).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(16).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(16).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(17).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(17).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(17).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setMinWidth(300);
            jTableCarga.getColumnModel().getColumn(21).setPreferredWidth(300);
            jTableCarga.getColumnModel().getColumn(21).setMaxWidth(300);
        }

        javax.swing.GroupLayout pPedLayout = new javax.swing.GroupLayout(pPed);
        pPed.setLayout(pPedLayout);
        pPedLayout.setHorizontalGroup(
            pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1077, Short.MAX_VALUE)
            .addGroup(pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1077, Short.MAX_VALUE))
        );
        pPedLayout.setVerticalGroup(
            pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 153, Short.MAX_VALUE)
            .addGroup(pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1077, Short.MAX_VALUE)
        );
        pProLayout.setVerticalGroup(
            pProLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pProLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        jTabbedPane1.addTab("Produtos", pPro);

        javax.swing.GroupLayout pSerLayout = new javax.swing.GroupLayout(pSer);
        pSer.setLayout(pSerLayout);
        pSerLayout.setHorizontalGroup(
            pSerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1077, Short.MAX_VALUE)
        );
        pSerLayout.setVerticalGroup(
            pSerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 153, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Séries", pSer);

        txtObservacao.setEnabled(false);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel11.setText("Observação");

        jLabel6.setText("Situação");

        txtNomeTransportadora1.setEnabled(false);
        txtNomeTransportadora1.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNomeTransportadora1.setUpperCase(true);
        txtNomeTransportadora1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeTransportadora1ActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Importar.png"))); // NOI18N
        jButton1.setText("jButton1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnGravar)
                        .addGap(4, 4, 4)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
            .addComponent(txtObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNomeMinuta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(txtQtdVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(txtQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                .addGap(6, 6, 6)))))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                        .addGap(19, 19, 19))
                    .addComponent(jLabel2)
                    .addComponent(txtNomeTransportadora1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilial, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(txtSituacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel1)
                    .addComponent(txtMinuta, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(txtEmissao, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                .addGap(4, 4, 4))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnIncluir, jButton2, jLabel7});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel1))
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomeMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel12)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQtdy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtQtdVolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNomeTransportadora1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(4, 4, 4)
                .addComponent(jLabel11)
                .addGap(5, 5, 5)
                .addComponent(txtObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGravar, btnIncluir, jButton2, jButton4});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtEmissao, txtSituacao});

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
                .addComponent(jTabbedCotacao)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jTabbedCotacaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedCotacaoMouseClicked
        //
    }//GEN-LAST:event_jTabbedCotacaoMouseClicked

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();

        //        txtPedido.setText(jTableCarga.getValueAt(linhaSelSit, 1).toString());
        //        txtCliente.setText(jTableCarga.getValueAt(linhaSelSit, 2).toString());
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIncluirActionPerformed
        addReg = true;
        Object[] options = {" Sim ", " Não "};
        if (JOptionPane.showOptionDialog(this, "Gerar minuta ?", "Aviso:",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null,
                options, options[1]) == JOptionPane.YES_OPTION) {
            try {
                this.minuta = new Minuta();
                txtQtdVolume.setEnabled(true);
                txtObservacao.setEnabled(true);
                txtMinuta.setText("0");
                txtQtdVolume.setValue(1);
                txtQtdVolume.requestFocus();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "ERRO " + ex);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Processo cancelado");
        }

    }//GEN-LAST:event_btnIncluirActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            gerarRelatatorio(txtMinuta.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        sair();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtNomeMinutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeMinutaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeMinutaActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        try {
            if (ManipularRegistros.gravarRegistros(" Incluir  ")) {
                addReg = true;
                gravar();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex);
        }
    }//GEN-LAST:event_btnGravarActionPerformed

    private void txtNomeTransportadora1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeTransportadora1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeTransportadora1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnIncluir;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTabPro;
    private javax.swing.JTabbedPane jTabbedCotacao;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JPanel pPed;
    private javax.swing.JPanel pPro;
    private javax.swing.JPanel pSer;
    private javax.swing.JPanel pnlForm;
    private org.openswing.swing.client.DateControl txtEmissao;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.TextControl txtFilial;
    private org.openswing.swing.client.TextControl txtMinuta;
    private org.openswing.swing.client.TextControl txtNomeMinuta;
    private org.openswing.swing.client.TextControl txtNomeTransportadora1;
    private org.openswing.swing.client.TextAreaControl txtObservacao;
    private org.openswing.swing.client.NumericControl txtPesoSelecionado;
    private org.openswing.swing.client.NumericControl txtQtdVolume;
    private org.openswing.swing.client.NumericControl txtQtdy;
    private javax.swing.JComboBox<String> txtSituacao;
    // End of variables declaration//GEN-END:variables
}
