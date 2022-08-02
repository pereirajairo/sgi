/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.PedidoReport;
import br.com.sgi.bean.Status;
import br.com.sgi.bean.ZapSend;
import br.com.sgi.dao.PedidoReportDAO;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.ConsumirWS;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Type;
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
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class FatPedidoReport extends InternalFrame {

    private PedidoReport pedido;
    private List<PedidoReport> listPedidoReport = new ArrayList<PedidoReport>();
    private PedidoReportDAO pedidoReportDAO;

    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;

    public FatPedidoReport() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Pedidos"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (pedidoReportDAO == null) {
                pedidoReportDAO = new PedidoReportDAO();
            }

            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
            txtDatFim.setDate(this.utilDatas.retornaDataFim(new Date()));
            LoadEstados();
            pegarDataDigitada();
            retornarPedidoReport();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }

    }

    private String chamadaWebService;

    private void chamarMetodoWsEnviarMsgZap(String url, String metodo) throws Exception {
        ConsumirWS http = new ConsumirWS();
        chamadaWebService = url;

        if (listPedidoReport != null) {
            if (listPedidoReport.size() > 0) {
                for (PedidoReport pe : listPedidoReport) {
                    if (pe.getCadCliente().getTelefone() != null) {
                        if (!pe.getCadCliente().getTelefone().equals("55")) {
                            Gson g = new Gson();
                            ZapSend u = new ZapSend();
                            Type usuarioType = new TypeToken<ZapSend>() {
                            }.getType();
                            u.setSessionName("6PQDQHT7SEVRXFLQFRAF");
                            u.setPhonefull(pe.getCadCliente().getTelefone());
                            u.setMsg("Erbs Informa: "
                                    + "\nPrezado cliente"
                                    + "\nSeu pedido " + pe.getUsu_numped() + " de " + pe.getUsu_qtdped() + " Baterias  foi " + pe.getUsu_motenv()
                                    + "\nAgradecemos sua preferencia");

                            String json = g.toJson(u, usuarioType);
                            String retorno = http.sendPost(chamadaWebService, json, metodo);

                            Gson gson = new Gson();
                            Status sta = new Status();
                            // sta = gson.fromJson(retorno, Status.class);
                            Type statusType = new TypeToken<Status>() {
                            }.getType();
                            sta = g.fromJson(retorno, statusType);

                            txtResposta.setText(retorno + "\n" + http.getRESPONSE_CODE());
                            System.out.println("br.com.recebimento.frame.FatPedidoReport.chamarMetodoWsEnviarMsgZap()\n" + http.getRESPONSE_CODE());
                        }
                    }
                }

            }
        }

    }

    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
    }

    private void pesquisarPorData(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        PESQUISA_POR = "DATA";
        PESQUISA = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "'";
        getPedidoReports(PESQUISA_POR, PESQUISA);
    }

    public void iniciarBarra(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Buscando PedidoReports");
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getPedidoReports(PESQUISA_POR, PESQUISA);
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                //    barra.setVisible(false);
                // barra.setString("Filtro carregado");
            }
        };
        worker.execute();

    }

    private boolean gerarMinuta = false;
    private int qtdpre2 = 0;

    void retornarPedidoReport(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        PESQUISA_POR = "DATA";
        PESQUISA = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "'";
        getPedidoReports(PESQUISA_POR, PESQUISA);
    }

    private void getPedidoReports(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listPedidoReport = this.pedidoReportDAO.getPedidoReports(PESQUISA_POR, PESQUISA);
        if (listPedidoReport != null) {
            carregarTabela();
        }
    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);

        ImageIcon AnaIcon = getImage("/images/pedido_analise.png");
        ImageIcon AprIcon = getImage("/images/pedido_liberado.png");
        ImageIcon ProIcon = getImage("/images/pedido_producao.png");
        ImageIcon SepIcon = getImage("/images/pedido_separacao.png");
        ImageIcon FatIcon = getImage("/images/pedido_faturado.png");
        ImageIcon FaPIcon = getImage("/images/pedido_faturar.png");

        ImageIcon SendMsgPend = getImage("/images/wha3.png");
        ImageIcon SendMsgError = getImage("/images/wha1.png");
        ImageIcon SendMsgOKIcon = getImage("/images/wha2.png");

        int contador = 0;

        for (PedidoReport ped : listPedidoReport) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new FatPedidoReport.ColorirRenderer();
            FatPedidoReport.JTableRenderer renderers = new FatPedidoReport.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            switch (ped.getUsu_seqmes()) {
                case 1:
                    linha[0] = AnaIcon;
                    break;
                case 2:
                    linha[0] = AprIcon;
                    break;
                case 3:
                    linha[0] = SepIcon;
                    break;
                case 4:
                    linha[0] = ProIcon;
                    break;
                case 5:
                    linha[0] = SepIcon;
                    break;
                case 6:
                    linha[0] = SepIcon;
                    break;
                case 8:
                    linha[0] = FaPIcon;
                    break;
                case 99:
                    linha[0] = FaPIcon;
                    break;
                case 100:
                    linha[0] = FatIcon;
                    break;
                default:
                    break;
            }

            linha[1] = ped.getUsu_numped();
            linha[2] = ped.getUsu_motenv();
            linha[3] = "";
            linha[4] = ped.getCadCliente().getCodigo();
            linha[5] = ped.getCadCliente().getNome();
            linha[6] = ped.getCadCliente().getEstado();
            linha[7] = ped.getCadCliente().getCidade();
            linha[8] = ped.getCadCliente().getTelefone();
            columnModel.getColumn(9).setCellRenderer(renderers);
            linha[9] = SendMsgPend;

            linha[10] = ped.getUsu_pesped();

            linha[11] = ped.getUsu_qtdped();

            linha[12] = this.utilDatas.converterDateToStr(ped.getUsu_datemi());
            linha[13] = this.utilDatas.converterDateToStr(ped.getUsu_datenv());
            linha[14] = ped.getUsu_numdia();
            linha[15] = ped.getUsu_logenv();
            linha[16] = ped.getUsu_seqmes();
            linha[17] = ped.getUsu_nomset();
            contador++;

            modeloCarga.addRow(linha);
        }
        barra.setString("Total de mensagens " + contador);

        // pintarLinhas();
    }

    private void pintarLinhas() {

        jTableCarga.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (table.getValueAt(row, 25).toString().contains("3")) {
                    setBackground(new Color(0, 100, 0));
                    setForeground(Color.white);
                }

                if (table.getValueAt(row, 25).toString().contains("2")) {
                    setBackground(new Color(255, 140, 0));
                    setForeground(Color.black);
                }

                if (table.getValueAt(row, 25).toString().contains("0")) {
                    setBackground(new Color(211, 211, 211));
                    setForeground(Color.white);
                }

//                    if (table.getValueAt(row, 25).toString().contains("4")) {
//                       setBackground(new Color(75,0,130));
//                        setForeground(Color.white);
//                    }
                return this;
            }
        });

    }

    public void retornarPedidoReport() {
        try {
            iniciarBarra("", " and usu_seqmes not in (10) ");
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

        jTableCarga.setRowHeight(40);
//        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(3).setCellRenderer(direita);
//        // jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
//        //jTableCarga.getColumnModel().getColumn(11).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(15).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(16).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(17).setCellRenderer(direita);
//        
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoCreateRowSorter(true);
        // jTableCarga.setAutoResizeMode(0);
        jTableCarga.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

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

    private void LoadEstados() {
        BaseEstado estado = new BaseEstado();
        Map<String, String> mapas = estado.getEstados();
        for (String uf : mapas.keySet()) {
            txtEstado.addItem(mapas.get(uf));
        }
    }

    private void limparFiltros() throws SQLException, ParseException {
        limparTela();
        txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
        txtPedido.setText("");
        txtCliente.setText("");
        LoadEstados();
        pegarDataDigitada();
        retornarPedidoReport();
        txtEstado.setSelectedItem("TODOS");
    }

    private void limparTela() throws SQLException, ParseException {
        txtPedido.setText("");
        txtCliente.setText("");

    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Boolean b = false;
        if (columnIndex == 2) {
            b = true;
            return true;
        } else {
            b = false;
        }
        return b;

    }

    private void pesquisarTable(String pesquisa) {
        DefaultTableModel tabela_produtos = (DefaultTableModel) jTableCarga.getModel();
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabela_produtos);
        jTableCarga.setRowSorter(sorter);

        int index = pesquisa.indexOf(":");
        String coluna = pesquisa.substring(0, index);
        coluna = coluna.trim();
        int posicao = 0;
        if (coluna.equals("PED") || coluna.equals("ped")) {
            posicao = 1;
        }
        if (coluna.equals("FAT") || coluna.equals("fat")) {
            posicao = 8;
        }
        String campo = pesquisa.substring(index + 1, pesquisa.length());
        if (pesquisa.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<TableModel, Object> rf = null;
                try {
                    rf = RowFilter.regexFilter(campo, posicao);
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                sorter.setRowFilter(rf);
            } catch (PatternSyntaxException pse) {
                System.err.println("Erro");
            }
        }
    }

    private void pegarPedidosSetor(int sequencia) {
        try {
            pegarDataDigitada();
            String sql = " \nand usu_seqmes = " + sequencia + ""
                    + "\n and usu_datenv >= '" + datIni + "'"
                    + "\n and usu_datenv <='" + datFim + "'";
            iniciarBarra("sit", sql);
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
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
            if ("S".equals(str)) {
                setForeground(Color.WHITE);
                setBackground(COR_ESTOQUE_HFF);
            } else {
                // setForeground(Color.WHITE);
                setBackground(Color.WHITE);
            }

            //   setBackground(COR_ESTOQUE_HFF);
            return this;
        }
    }

    public class EditableRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable jTableCarga, Object value, boolean selected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(jTableCarga, value, selected, hasFocus, row, col);

            setVisible(false);

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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        barra = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        txtCliente = new org.openswing.swing.client.TextControl();
        btnFiltrar2 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar4 = new javax.swing.JButton();
        txtEstado = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        btnFiltrar5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtResposta = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemAuto = new javax.swing.JMenuItem();
        jMenuItemMoto = new javax.swing.JMenuItem();
        jMenuItemMkt = new javax.swing.JMenuItem();
        jMenuItemGar = new javax.swing.JMenuItem();
        jMenuItemGarNota = new javax.swing.JMenuItem();
        jMenuItemGarRo = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Pré-Fatura"));

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

        jLabel7.setText("Cliente");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar2)
                .addGap(2, 2, 2))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFiltrar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar2, txtCliente});

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

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

        btnFiltrar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/indu.png"))); // NOI18N
        btnFiltrar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar4ActionPerformed(evt);
            }
        });

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstadoActionPerformed(evt);
            }
        });

        jLabel5.setText("Estado");

        jLabel3.setText("Data Inicio");

        jLabel4.setText("Data Final");

        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoActionPerformed(evt);
            }
        });

        jLabel1.setText("Pedido");

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnFiltrar5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-sinal-de-reciclagem-16.png"))); // NOI18N
        btnFiltrar5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar5ActionPerformed(evt);
            }
        });

        jLabel2.setText("Enviar");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(txtPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltrar)))
                .addGap(4, 4, 4)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltrar4))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEstado, 0, 58, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(btnFiltrar5))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4)
                                .addComponent(jLabel1)))
                        .addGap(4, 4, 4))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar4)
                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnFiltrar5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtEstado, javax.swing.GroupLayout.Alignment.LEADING)))
                .addGap(4, 4, 4))
        );

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Pedido", "Localização", "Transação", "Cliente", "Nome", "UF", "Cidade", "Telefone", "#", "Peso", "Quantidade", "Emissão", "Data", "Dias", "Info", "Seq", "Posição"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

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
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(200);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(200);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(300);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(300);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(300);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(40);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(40);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(40);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(150);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(500);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(500);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(500);
            jTableCarga.getColumnModel().getColumn(16).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(16).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(16).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(17).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(17).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(17).setMaxWidth(100);
        }

        jTabbedPane1.addTab("Pedidos", jScrollPane3);

        txtResposta.setColumns(20);
        txtResposta.setRows(5);
        jScrollPane1.setViewportView(txtResposta);

        jTabbedPane1.addTab("Anotações", jScrollPane1);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pedido_analise.png"))); // NOI18N
        jLabel6.setText("Analise Comercial");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pedido_liberado.png"))); // NOI18N
        jLabel8.setText("Liberado Financeiro");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pedido_separacao.png"))); // NOI18N
        jLabel10.setText("Separação Pelo HUB");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pedido_faturado.png"))); // NOI18N
        jLabel11.setText("Pedido Faturado");
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pedido_faturar.png"))); // NOI18N
        jLabel12.setText("Faturar Pedido");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel11, jLabel12, jLabel6, jLabel8});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(4, 4, 4)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        jMenu1.setText("Pedido(s)");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItemAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/auto.png"))); // NOI18N
        jMenuItemAuto.setText("Auto");
        jMenuItemAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAutoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemAuto);

        jMenuItemMoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        jMenuItemMoto.setText("Moto");
        jMenuItemMoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMotoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemMoto);

        jMenuItemMkt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        jMenuItemMkt.setText("Marketing");
        jMenuItemMkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMktActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemMkt);

        jMenuItemGar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        jMenuItemGar.setText("Garantias");
        jMenuItemGar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGarActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGar);

        jMenuItemGarNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/NotaGarantia.png"))); // NOI18N
        jMenuItemGarNota.setText("Garantias NF");
        jMenuItemGarNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGarNotaActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGarNota);

        jMenuItemGarRo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/NotaRomaneio.png"))); // NOI18N
        jMenuItemGarRo.setText("Garantias RO");
        jMenuItemGarRo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGarRoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGarRo);

        jMenuBar1.add(jMenu1);

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jMenu3.setText("Filtro");

        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-vassoura-16.png"))); // NOI18N
        jMenuItem6.setText("Limpar");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuBar1.add(jMenu3);

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        jMenu4.setText("Fechar");
        jMenu4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu4ActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1042, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private int contador = 0;
    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int l = jTableCarga.convertRowIndexToModel(jTableCarga.getSelectedRow());
        int c = jTableCarga.convertColumnIndexToModel(jTableCarga.getSelectedColumn());

        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();

        String estado = jTableCarga.getValueAt(linhaSelSit, 9).toString();
        String cidade = jTableCarga.getValueAt(linhaSelSit, 10).toString();
        jTableCarga.clearSelection();
        txtPedido.setText(jTableCarga.getValueAt(linhaSelSit, 1).toString());
        txtCliente.setText(jTableCarga.getValueAt(linhaSelSit, 4).toString());


    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        if (txtPedido.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o Pedido");
        } else {
            try {

                String sql = " and usu_numped = '" + txtPedido.getText() + "' ";
                iniciarBarra("sit", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed
        if (txtCliente.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {
                String sql = "\nand usu_codcli =" + txtCliente.getText();

                iniciarBarra("sit", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_btnFiltrar2ActionPerformed


    private void txtPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoActionPerformed

        if (txtPedido.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", " INFORME O PEDIDO");
        } else {
            try {

                String sql = "and usu_numped = '" + txtPedido.getText() + "'";
                iniciarBarra("SIT", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_txtPedidoActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed

    }//GEN-LAST:event_txtClienteActionPerformed

    private void txtDatIniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatIniActionPerformed

        try {
            limparTela();
            pegarDataDigitada();
            String sql = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' ";
            iniciarBarra("SIT", sql);
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_txtDatIniActionPerformed

    private void txtDatFimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatFimActionPerformed

        try {
            pegarDataDigitada();

            String sql = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' ";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_txtDatFimActionPerformed

    private void txtEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadoActionPerformed

        if (!txtEstado.getSelectedItem().equals("TODOS")) {
            try {
                String sql = " and cli.sigufs = '" + txtEstado.getSelectedItem().toString() + "' ";
                iniciarBarra("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(IntegrarPesos.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //  Mensagem.mensagemRegistros("ERRO", "Selecione Estado");
        }
    }//GEN-LAST:event_txtEstadoActionPerformed

    private void btnFiltrar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar4ActionPerformed
        try {
            pegarDataDigitada();
            retornarPedidoReport();

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnFiltrar4ActionPerformed

    private void jMenuItemAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAutoActionPerformed

        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());

            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = 'BA' ";
            iniciarBarra("sit", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItemAutoActionPerformed

    private void jMenuItemMotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMotoActionPerformed

        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());

            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = 'BM' ";
            iniciarBarra("", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItemMotoActionPerformed

    private void jMenuItemMktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMktActionPerformed
        try {

            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            String sql = " and E120PED.tnspro in ('90126')";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItemMktActionPerformed

    private void jMenuItemGarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGarActionPerformed

        try {

            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E120PED.TNSPRO IN ('90112','90113','90122','90123') ";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItemGarActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        try {
            // TODO add your handling code here:
            limparFiltros();
        } catch (SQLException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        } catch (ParseException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItemGarNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGarNotaActionPerformed
        try {
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E120PED.TNSPRO IN ('90113','90123') ";
            iniciarBarra("sit", sql);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoReport.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jMenuItemGarNotaActionPerformed

    private void jMenuItemGarRoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGarRoActionPerformed
        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E120PED.TNSPRO IN ('90112','90122') ";
            iniciarBarra("sit", sql);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoReport.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jMenuItemGarRoActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed

        //
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenu4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenu4ActionPerformed

    private void btnFiltrar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar5ActionPerformed
        try {

            chamarMetodoWsEnviarMsgZap("https://api.connectzap.com.br/sistema/sendText/", "POST");

        } catch (Exception ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar5ActionPerformed

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        pegarPedidosSetor(1);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        pegarPedidosSetor(2);
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        pegarPedidosSetor(3);
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        pegarPedidosSetor(100);
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        pegarPedidosSetor(99);
    }//GEN-LAST:event_jLabel12MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar4;
    private javax.swing.JButton btnFiltrar5;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItemAuto;
    private javax.swing.JMenuItem jMenuItemGar;
    private javax.swing.JMenuItem jMenuItemGarNota;
    private javax.swing.JMenuItem jMenuItemGarRo;
    private javax.swing.JMenuItem jMenuItemMkt;
    private javax.swing.JMenuItem jMenuItemMoto;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtEstado;
    private org.openswing.swing.client.TextControl txtPedido;
    private javax.swing.JTextArea txtResposta;
    // End of variables declaration//GEN-END:variables
}
