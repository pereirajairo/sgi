/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.Motorista;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.PedidoHub;
import br.com.sgi.bean.Representante;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.dao.MinutaDAO;
import br.com.sgi.dao.MinutaPedidoDAO;
import br.com.sgi.dao.MotoristaDAO;
import br.com.sgi.dao.NotaFiscalDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.PedidoHubDAO;
import br.com.sgi.dao.RepresentanteDAO;
import br.com.sgi.dao.TransportadoraDAO;
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
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class frmMinutasHubGerar extends InternalFrame {

    private PedidoHub pedidoHub;
    private PedidoHubDAO pedidoHubDAO;
    private List<PedidoHub> lstPedidoHub = new ArrayList<PedidoHub>();

    private Minuta minuta;
    private MinutaDAO minutaDAO;
    private MinutaPedido minutapedido;
    private List<MinutaPedido> listminutaPedido = new ArrayList<MinutaPedido>();
    private MinutaPedidoDAO minutaPedidoDAO;

    private Pedido pedido;
    private Transportadora transportadora;

    private frmMinutas veioCampoEmbarque;
    private UtilDatas utilDatas;
    private boolean minutaHub = false;
    private boolean addReg;
    private double pesoSelecionado = 0;
    private double quantidadeSelecionado = 0;

    private String pedidoSelecionado = "0";

    private String PROCESSO;

    public frmMinutasHubGerar() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Minutas Coleta de Hub"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (pedidoHubDAO == null) {
                this.pedidoHubDAO = new PedidoHubDAO();
            }
            if (minutaDAO == null) {
                this.minutaDAO = new MinutaDAO();
            }
            if (minutaPedidoDAO == null) {
                this.minutaPedidoDAO = new MinutaPedidoDAO();
            }
            preencherComboHub(0);
            txtDataLiberacao.setDate(new Date());
            txtEmissao.setDate(new Date());
            txtEntrega.setDate(new Date());
            txtEmbarque.setDate(new Date());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void preencherComboHub(Integer id) throws SQLException, Exception {
        RepresentanteDAO dao = new RepresentanteDAO();
        List<Representante> listRepresentante = new ArrayList<Representante>();
        String cod;
        String des;
        String desger;
        txtHub.setSelectedItem("TODOS");
        listRepresentante = dao.getRepresentantesHub("", " and usu_codhub >0 ");

        if (listRepresentante != null) {
            for (Representante rep : listRepresentante) {
                cod = rep.getCodigoHub();
                des = rep.getNomeHub();
                desger = cod + " - " + des;
                txtHub.addItem(desger);
            }
        }
    }

    public void limpatela() throws ParseException, Exception {
        txtEmbarque.setDate(new Date());
        txtHub.setSelectedItem("TODOS");
        txtEntrega.setDate(new Date());
        txtDataLiberacao.setDate(new Date());
        txtEmpresa.setText("1");
        txtFilial.setText("1");
        txtMinuta.setText("0");

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
        minuta.setUsu_pesfat(txtPesoSelecionado.getDouble());
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
        minuta.setUsu_nommin(txtHub.getSelectedItem().toString());
        minuta.setUsu_orimin("HUB");
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
        addReg = false;
    }

    public void iniciarBarra(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Buscando informação");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getPedidosHub(PESQUISA_POR, PESQUISA);
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

    private void getPedidosHub(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        lstPedidoHub = pedidoHubDAO.getPedidoHubsSucata(" ped ", PESQUISA);
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
        double peso = 0;
        int qtd = 0;
        for (PedidoHub mp : lstPedidoHub) {
            Object[] linha = new Object[22];
            TableColumnModel columnModel = jTableCarga.getColumnModel();
            frmMinutasHubGerar.JTableRenderer renderers = new frmMinutasHubGerar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            linha[0] = BomIcon;
            linha[1] = mp.getNotafiscal();
            if (mp.getDataentrega() != null) {
                linha[2] = this.utilDatas.converterDateToStr(mp.getDataentrega());
            }

            linha[3] = 1;
            linha[4] = mp.getPesosucata();

            linha[5] = true;
            linha[6] = mp.getCliente();

            linha[7] = mp.getClientenome();
            linha[8] = mp.getCidade();
            linha[9] = mp.getEstado();
            linha[10] = txtTransportadora.getText();
            linha[11] = txtNomeTransportadora.getText();
            linha[12] = mp.getPedido();
            linha[13] = 1;
            linha[14] = 1;
            linha[15] = "";
            linha[16] = "";
            linha[17] = "";
            linha[18] = "";
            linha[19] = mp.getSucata_id();
            linha[20] = mp.getQuantidade();
            linha[21] = mp.getPesorecebido();
            peso += mp.getPesorecebido();
            qtd++;
            modeloCarga.addRow(linha);
        }
        txtPesoSelecionado.setValue(peso);

        txtQtdVolume.setText(String.valueOf(qtd));
        txtQtdy.setText(String.valueOf(qtd));

    }

    public void selecionarRange() throws SQLException, Exception {
        if (jTableCarga.getRowCount() > 0) {
            PedidoDAO daoPedido = new PedidoDAO();
            NotaFiscalDAO daoNota = new NotaFiscalDAO();

            String numeroPedido = "0";

            Integer qtdreg = jTableCarga.getRowCount();
            Integer contador = 0;
            if (jTableCarga.getRowCount() > 0) {

                if (jTableCarga.getRowCount() > 0) {
                    for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                        if ((Boolean) jTableCarga.getValueAt(i, 5)) {
                            numeroPedido = (jTableCarga.getValueAt(i, 12).toString());

                            if (!numeroPedido.isEmpty()) { // tem pedido
                                MinutaPedido minutaPedido = new MinutaPedido();
                                minutaPedido.setCadMinuta(minuta);
                                minutaPedido.setUsu_numnfv(Integer.valueOf(jTableCarga.getValueAt(i, 1).toString()));
                                minutaPedido.setUsu_qtdped(Double.valueOf(jTableCarga.getValueAt(i, 20).toString()));
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
                                minutaPedido.setUsu_pesrec(Double.valueOf(jTableCarga.getValueAt(i, 21).toString()));
                                minutaPedido.setUsu_datemi(new Date());
                                minutaPedido.setUsu_datlib(new Date());
                                minutaPedido.setUsu_codlan(this.minuta.getUsu_codlan());
                                minutaPedido.setUsu_seqite(minutaPedidoDAO.proxCodCad());
                                contador++;
                                if (!minutaPedidoDAO.inserir(minutaPedido, qtdreg, contador)) {

                                } else {
                                    pedidoHubDAO.atualizarGeracaoMinuta(minutaPedido, qtdreg, contador);

                                }

                            }

                        }
                    }

                }
            }
        }

    }

    private void sair() {
        this.dispose();

    }

    public void setRecebePalavraManutencao(frmMinutas veioInput,
            Minuta minuta) throws Exception {
        desabiltar(false);
        this.veioCampoEmbarque = veioInput;
        this.minuta = new Minuta();
        this.minuta = minuta;
        if (minuta != null) {
            popularTela();

        }

    }

    private void desabiltar(boolean acao) {
        txtTransportadora.setEnabled(acao);
        txtPlaca.setEnabled(acao);
        txtDataLiberacao.setEnabled(acao);
        txtQtdVolume.setEnabled(acao);
        txtEntrega.setEnabled(acao);
        txtEmbarque.setEnabled(acao);
        txtHub.setEnabled(acao);
        btnIncluir.setEnabled(acao);
        txtObservacao.setEnabled(acao);
        txtTransportadora.requestFocus();

    }

    private void popularTela() throws Exception {
        if (minuta.getUsu_codfil() > 0) {
            addReg = true;
            txtEmissao.setDate(minuta.getUsu_datemi());

            txtPesoSelecionado.setValue(minuta.getUsu_pesfat());
            txtQtdy.setValue(minuta.getUsu_qtdfat());
            txtObservacao.setText(minuta.getUsu_obsmin());
            txtMinuta.setText(minuta.getUsu_codlan().toString());
            txtEmpresa.setText(minuta.getUsu_codemp().toString());
            txtFilial.setText(minuta.getUsu_codfil().toString());
            txtPlaca.setText(minuta.getUsu_plavei());
            txtQtdVolume.setValue(minuta.getUsu_qtdvol());
            txtSituacao.setSelectedItem(minuta.getUsu_sitmin());

            if (this.minuta.getUsu_codtra() > 0) {
                preencherCombo("");
            }
            if (minuta.getUsu_codtra() > 0) {
                txtTransportadora.setText(minuta.getUsu_codtra().toString());
                pesquisarTransportadoda(txtTransportadora.getText());
            }
            btnIncluir.setEnabled(true);

            if (minuta.getUsu_codlan() > 0) {

                btnIncluir.setEnabled(false);
                if (!minuta.getUsu_sitmin().equals("ABERTA")) {

                    txtMotorista.setEnabled(false);
                    txtSituacao.setEnabled(false);
                }
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
            txtTransportadora.requestFocus();
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

    private void pesquisarTransportadoda(String text) throws SQLException {
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
        btnHoras = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
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
        txtObservacao = new org.openswing.swing.client.TextAreaControl();
        barra = new javax.swing.JProgressBar();
        btnNovo = new javax.swing.JButton();
        txtHub = new javax.swing.JComboBox<>();

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

        txtEmbarque.setEnabled(false);
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

        txtEntrega.setEnabled(false);
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
        txtSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ABERTA", "LIBERADA", "EXCLUIDA" }));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        jButton2.setText("Sair");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        jButton4.setText("Imprimir");
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

        jLabel14.setText("Hub");

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

        jLabel5.setText("Peso Coletar");

        txtPlaca.setEnabled(false);
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

        txtDataLiberacao.setEnabled(false);
        txtDataLiberacao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtDataLiberacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataLiberacaoActionPerformed(evt);
            }
        });

        jLabel16.setText("Liberação");

        txtQtdVolume.setEnabled(false);
        txtQtdVolume.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnIncluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
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
                "#", "Nota", "Entrega", "Quantidade", "Peso", "Selecionar", "Cliente", "Nome", "Cidade", "Estado", "Transp.", "Nome ", "Pedido", "Empresa", "Filial", "Transação", "Emissão Pedido", "Categoria", "Serie Nota", "Lan. Sucata", "Quantidade", "Peso Recebido"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true
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
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(0);
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
            jTableCarga.getColumnModel().getColumn(20).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setMaxWidth(100);
        }

        txtObservacao.setEnabled(false);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout pPedLayout = new javax.swing.GroupLayout(pPed);
        pPed.setLayout(pPedLayout);
        pPedLayout.setHorizontalGroup(
            pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1116, Short.MAX_VALUE)
            .addComponent(txtObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pPedLayout.setVerticalGroup(
            pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pPedLayout.createSequentialGroup()
                .addComponent(txtObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Coletas", pPed);

        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnNovo.setText("Novo");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        txtHub.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtHub.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtHub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHubActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(btnIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
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
                                .addGap(4, 4, 4)))
                        .addGap(2, 2, 2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(txtQtdVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 49, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEntrega, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                            .addComponent(jLabel9))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(txtEmbarque, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtHub, 0, 189, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(barra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTabbedPane1))
                        .addGap(4, 4, 4))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnIncluir, jButton4, jLabel7});

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
                            .addComponent(jLabel5)))
                    .addComponent(txtMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtQtdy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHoras)
                    .addComponent(txtEmbarque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtQtdVolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton4)
                        .addComponent(btnIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnIncluir, jButton2, jButton4});

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
                .addComponent(jTabbedCotacao)
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

        } catch (Exception ex) {
            Logger.getLogger(frmMinutasHubGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            pesquisarTransportadoda(txtTransportadora.getText());
        } catch (SQLException ex) {
            Logger.getLogger(frmMinutasHubGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHorasActionPerformed
        try {
            if (txtHub.getSelectedIndex() != -1) {
                if (!txtHub.getSelectedItem().equals("TODOS")) {
                    String cod = txtHub.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);
                    iniciarBarra("ped", "and ped.situacao  not in ('') and sucata_id > 0 "
                            + "and ped.pesorecebido>0 and gerarminutacoleta not in ('G','F')"
                            + " and codigoacesso = " + codcon.trim());

                } else {

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(frmMinutasHubGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnHorasActionPerformed

    private void txtTransportadoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransportadoraActionPerformed
        try {
            pesquisarTransportadoda(txtTransportadora.getText());
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
        if (txtHub.getSelectedIndex() != -1) {
            if (!txtHub.getSelectedItem().equals("TODOS")) {
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

            } else {
                JOptionPane.showMessageDialog(null, "Hub não selecionado");
            }
        }

    }//GEN-LAST:event_btnIncluirActionPerformed

    private void txtMotoristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMotoristaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMotoristaActionPerformed

    private void txtDataLiberacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataLiberacaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDataLiberacaoActionPerformed

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed

        if (jTableCarga.getRowCount() <= 0) {
            Mensagem.mensagem("ERROR", "Selecione o Hub para gerar a minuta");
        } else {
            try {
                limpatela();
                desabiltar(true);
                this.minuta = new Minuta();
                this.addReg = true;
            } catch (Exception ex) {
                Logger.getLogger(frmMinutasHubGerar.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btnNovoActionPerformed

    private void txtHubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHubActionPerformed
        try {
            if (txtHub.getSelectedIndex() != -1) {
                if (!txtHub.getSelectedItem().equals("TODOS")) {
                    String cod = txtHub.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);
                    iniciarBarra("ped", "and ped.situacao  not in ('') and sucata_id > 0 "
                            + "and ped.pesorecebido>0 and gerarminutacoleta not in ('G','F')"
                            + " and codigoacesso = " + codcon.trim());

                } else {

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(frmMinutasHubGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtHubActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnHoras;
    private javax.swing.JButton btnIncluir;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedCotacao;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JPanel pPed;
    private javax.swing.JPanel pnlForm;
    private org.openswing.swing.client.DateControl txtDataLiberacao;
    private org.openswing.swing.client.DateControl txtEmbarque;
    private org.openswing.swing.client.DateControl txtEmissao;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.DateControl txtEntrega;
    private org.openswing.swing.client.TextControl txtFilial;
    private javax.swing.JComboBox<String> txtHub;
    private org.openswing.swing.client.TextControl txtMinuta;
    private javax.swing.JComboBox<String> txtMotorista;
    private org.openswing.swing.client.TextControl txtNomeTransportadora;
    private org.openswing.swing.client.TextAreaControl txtObservacao;
    private org.openswing.swing.client.NumericControl txtPesoSelecionado;
    private org.openswing.swing.client.TextControl txtPlaca;
    private org.openswing.swing.client.NumericControl txtQtdVolume;
    private org.openswing.swing.client.NumericControl txtQtdy;
    private javax.swing.JComboBox<String> txtSituacao;
    private org.openswing.swing.client.TextControl txtTransportadora;
    // End of variables declaration//GEN-END:variables
}
