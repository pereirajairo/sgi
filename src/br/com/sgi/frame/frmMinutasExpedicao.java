/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.Pedido;
import br.com.sgi.dao.MinutaDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.main.Menu;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class frmMinutasExpedicao extends InternalFrame {

    private Minuta minuta;
    private List<Minuta> listMinuta = new ArrayList<Minuta>();
    private MinutaDAO minutaDAO;

    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;

    private String processoUsuario;

    public frmMinutasExpedicao() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Minuta de faturamento"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (minutaDAO == null) {
                minutaDAO = new MinutaDAO();
            }
            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
            txtDatFim.setDate(new Date());
            getProcessoUsuario();
            pegarDataDigitada();
            pesquisarPorData("", "");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void cancelarMinuta() throws SQLException, Exception {
        if (minuta != null) {
            if (minuta.getUsu_codlan() > 0) {
                this.minuta.setUsu_sitmin("CANCELADA");
                if (!minutaDAO.remover(minuta)) {

                } else {
                    if (!minutaDAO.removerItens(minuta)) {

                    } else {
                        PedidoDAO dao = new PedidoDAO();
                        Pedido p = new Pedido();
                        p.setCodigominuta(minuta.getUsu_codlan());
                        if (!dao.removerMinutaPedido(p)) {

                        } else {
                            pesquisarPorData("", "");
                        }
                    }
                }

            }
        }
    }

    private String getProcessoUsuario() throws SQLException {
        processoUsuario = Menu.getUsrLogado().getProcessoUsuario();
        return this.processoUsuario;

    }

    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
    }

    private void pesquisarPorData(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        pegarDataDigitada();
        PESQUISA_POR = "DATA";
        PESQUISA = " and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "'";
        getMinutas(PESQUISA_POR, PESQUISA);
    }

    private void getMinuta(String codigominuta) throws SQLException {
        minuta = new Minuta();
        minuta = minutaDAO.getMinuta("", "and usu_codlan = " + codigominuta);
        btnManutencao.setEnabled(true);
        btnCancelar.setEnabled(false);
        if (minuta != null) {
            if (minuta.getUsu_codlan() > 0 && minuta.getUsu_numnfv() == 0) {
                btnManutencao.setEnabled(true);
                btnCancelar.setEnabled(true);
            }
        }
    }

    private void getMinutas(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (this.processoUsuario.equals("ALL")) {

        } else {
            PESQUISA += " and usu_orimin = '" + this.processoUsuario + "'";
        }

        listMinuta = this.minutaDAO.getMinutas(PESQUISA_POR, PESQUISA, "N");
        if (listMinuta != null) {
            carregarTabela();
        }
    }

    private void getMinutasDetalhada(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (this.processoUsuario.equals("ALL")) {

        } else {
            PESQUISA += " and usu_orimin = '" + this.processoUsuario + "'";

        }
        listMinuta = this.minutaDAO.getMinutasDetalhada(PESQUISA_POR, PESQUISA, "S");
        if (listMinuta != null) {
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
        ImageIcon LibeIcon = getImage("/images/sitMedio.png");

        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon MetIcon = getImage("/images/bateriaindu.png");

        ImageIcon MktIcon = getImage("/images/bateriaindu.png");
        ImageIcon NotIcon = getImage("/images/Nota.png");
        ImageIcon RomIcon = getImage("/images/NotaRomaneio.png");
        ImageIcon NotGarIcon = getImage("/images/NotaGarantia.png");

        for (Minuta mi : listMinuta) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            TableCellRenderer renderer = new frmMinutasExpedicao.ColorirRenderer();
            frmMinutasExpedicao.JTableRenderer renderers = new frmMinutasExpedicao.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = AndaIcon;
            if (mi.getUsu_sitmin().equals("LIBERADA")) {
                linha[0] = LibeIcon;
            }
            if (mi.getUsu_sitmin().equals("FATURADA")) {
                linha[0] = BomIcon;
            }

            if (mi.getUsu_sitmin().equals("CANCELADA")) {
                linha[0] = RuimIcon;
            }

            linha[1] = mi.getUsu_codlan();
            linha[2] = mi.getUsu_codtra();
            linha[3] = mi.getUsu_nommin();

            columnModel.getColumn(4).setCellRenderer(renderers);
            if (mi.getUsu_libmot() != null) {
                linha[4] = AutIcon;
                if (mi.getUsu_libmot().equals("M")) {
                    linha[4] = MotIcon;
                }
                if (mi.getUsu_libmot().equals("I")) {
                    linha[4] = MetIcon;
                }
            }

            linha[5] = this.utilDatas.converterDateToStr(mi.getUsu_datemi());
            linha[6] = mi.getUsu_pesfat();
            linha[7] = mi.getUsu_qtdfat();
            linha[8] = mi.getUsu_qtdvol();
            jTableCarga.getColumnModel().getColumn(9).setCellRenderer(renderer);
            linha[9] = mi.getUsu_sitmin();
            // linha[9] = mi.getEnviaremail();

            linha[10] = mi.getUsu_codemp();
            linha[11] = mi.getUsu_codfil();
            linha[13] = mi.getMinutaPedido().getUsu_numped();
            linha[14] = mi.getMinutaPedido().getUsu_numpfa();
            linha[15] = mi.getMinutaPedido().getUsu_numana();
            linha[16] = mi.getMinutaPedido().getUsu_numnfv();
            if (mi.getMinutaPedido() != null) {
                if (mi.getMinutaPedido().getUsu_numped() > 0) {
                    columnModel.getColumn(17).setCellRenderer(renderers);
                    if (mi.getMinutaPedido().getTipopedido().equals("FAT")) {
                        linha[17] = NotIcon;
                    }
                    if (mi.getMinutaPedido().getTipopedido().equals("MKT")) {
                        linha[17] = MktIcon;
                    }
                    if (mi.getMinutaPedido().getTipopedido().equals("GAR_N")) {
                        linha[17] = NotGarIcon;
                    }
                    if (mi.getMinutaPedido().getTipopedido().equals("GAR_R")) {
                        linha[17] = RomIcon;
                    }
                }

            }

            modeloCarga.addRow(linha);
        }
    }

    public void retornarMinuta() {
        try {
            pesquisarPorData("", "");
            btnManutencao.setEnabled(false);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);

        jTableCarga.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(8).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(11).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(14).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(15).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(16).setCellRenderer(direita);

        jTableCarga.setRowHeight(35);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //jTableCarga.setAutoResizeMode(0);

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

    public final void gerarRelatorio(String valor) throws SQLException, Exception {

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            try {
                String Data = "";
                String arquivo = "1";
                String relatorio = "RFNF601";

                Data = this.utilDatas.converterDateToStr(txtDatIni.getDate()) + "-" + this.utilDatas.converterDateToStr(txtDatFim.getDate());

                String entrada = "<EDatEmi=" + Data + ">";
                String diretorio = "\\\\SRV-SPNS01\\Senior_ERP\\Sapiens\\Relatorios\\Expedicao\\";
                WSRelatorio.chamarMetodoWsXmlHttpSapiens(arquivo, relatorio, entrada, diretorio, "tsfNormal");
                desktop.open(new File(diretorio + arquivo + ".IMP"));
            } catch (Exception ex) {
                Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado");
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
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
            if ("CANCELADA".equals(str)) {
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        optAbe = new javax.swing.JRadioButton();
        optFec = new javax.swing.JRadioButton();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar = new javax.swing.JButton();
        btnFiltrar1 = new javax.swing.JButton();
        btnManutencao = new javax.swing.JButton();
        btnFiltrar4 = new javax.swing.JButton();
        optExc = new javax.swing.JRadioButton();
        txtPesquisar = new org.openswing.swing.client.TextControl();
        txtSelecionar = new javax.swing.JComboBox<>();
        optPed = new javax.swing.JRadioButton();
        btnFiltrar2 = new javax.swing.JButton();
        optExc1 = new javax.swing.JRadioButton();
        btnFiltrar3 = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "MINUTA", "TRANSPORTADORA", "DESCRIÇÃO MINUTA", "#", "DATA", "PESO", "QTDY", "VOLUME", "SITUAÇÃO", "EMAIL", "EMPRESA", "FILIAL", "Pedido", "Pré Fatura", "Analise", "Nota Fiscal", "#"
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
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(300);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(300);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(300);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(100);
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
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(0);
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
            jTableCarga.getColumnModel().getColumn(17).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(17).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(17).setMaxWidth(50);
        }

        buttonGroup1.add(optAbe);
        optAbe.setSelected(true);
        optAbe.setText("Abertas");
        optAbe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optAbeActionPerformed(evt);
            }
        });

        buttonGroup1.add(optFec);
        optFec.setText("Liberada");
        optFec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optFecActionPerformed(evt);
            }
        });

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnFiltrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/auto.png"))); // NOI18N
        btnFiltrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar1ActionPerformed(evt);
            }
        });

        btnManutencao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/book.png"))); // NOI18N
        btnManutencao.setText("Minuta");
        btnManutencao.setEnabled(false);
        btnManutencao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManutencaoActionPerformed(evt);
            }
        });

        btnFiltrar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        btnFiltrar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar4ActionPerformed(evt);
            }
        });

        buttonGroup1.add(optExc);
        optExc.setText("A Faturar");
        optExc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optExcActionPerformed(evt);
            }
        });

        txtPesquisar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarActionPerformed(evt);
            }
        });

        txtSelecionar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pedido", "Cliente", "Minuta", "Pré Fatura" }));

        buttonGroup1.add(optPed);
        optPed.setText("Analitico");
        optPed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optPedActionPerformed(evt);
            }
        });

        btnFiltrar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        btnFiltrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(optExc1);
        optExc1.setText("Faturada");
        optExc1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optExc1ActionPerformed(evt);
            }
        });

        btnFiltrar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        btnFiltrar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar3ActionPerformed(evt);
            }
        });

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnCancelar.setEnabled(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(optAbe)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optFec)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optExc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optExc1)
                .addGap(4, 4, 4)
                .addComponent(optPed, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(btnFiltrar)
                .addGap(1, 1, 1)
                .addComponent(btnFiltrar2)
                .addGap(4, 4, 4)
                .addComponent(txtSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(btnCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar4)
                .addGap(4, 4, 4)
                .addComponent(btnManutencao)
                .addGap(4, 4, 4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFiltrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFiltrar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addComponent(txtSelecionar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addComponent(txtPesquisar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFiltrar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFiltrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFiltrar4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnManutencao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(optPed)
                            .addComponent(optExc1)
                            .addComponent(optExc)
                            .addComponent(optFec)
                            .addComponent(optAbe))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar, btnFiltrar1, btnFiltrar2, btnFiltrar3, btnFiltrar4, btnManutencao, optAbe, optExc, optExc1, optFec, optPed, txtDatFim, txtDatIni, txtPesquisar, txtSelecionar});

        jTabbedPane1.addTab("Minutas", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1338, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String codigolancamento;
    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();
        if (evt.getClickCount() == 2) {
            try {
                codigolancamento = jTableCarga.getValueAt(linhaSelSit, 1).toString();
                btnManutencao.setEnabled(false);
                getMinuta(codigolancamento);
            } catch (SQLException ex) {
                Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void optAbeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optAbeActionPerformed
        try {
            pegarDataDigitada();
            String sql = "and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "' and usu_sitmin = 'ABERTA'";
            getMinutas("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optAbeActionPerformed

    private void optFecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optFecActionPerformed
        try {
            pegarDataDigitada();
            String sql = "and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "' and usu_sitmin = 'LIBERADA'";
            getMinutas("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optFecActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        try {
            pegarDataDigitada();
            pesquisarPorData("", "");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnFiltrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar1ActionPerformed

        try {
            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
            pegarDataDigitada();
            getMinutas("moto", " and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "'  and usu_libmot = 'A'");

        } catch (ParseException ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_btnFiltrar1ActionPerformed

    private void btnManutencaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManutencaoActionPerformed
        try {
            frmMinutasExpedicaoGerar sol = new frmMinutasExpedicaoGerar();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado
            //  sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavraManutencao(this, this.minuta);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnManutencaoActionPerformed

    private void btnFiltrar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar4ActionPerformed

        try {
            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
            pegarDataDigitada();
            getMinutas("moto", " and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "'  and usu_libmot = 'M'");

        } catch (ParseException ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar4ActionPerformed

    private void optExcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optExcActionPerformed
        try {
            pegarDataDigitada();
            getMinutasDetalhada("pedido", " \nand min.usu_datemi >= '" + datIni + "' "
                    + "\nand min.usu_datemi <='" + datFim + "'"
                    + "\n and minI.usu_sitmin in ('ENVIADA','FATURAR') ");
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_optExcActionPerformed

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed
        try {
            pegarDataDigitada();
            if (txtSelecionar.getSelectedItem().equals("Pedido")) {
                getMinutasDetalhada("pedido", " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' and minI.usu_numped = " + txtPesquisar.getText());
            }
            if (txtSelecionar.getSelectedItem().equals("Cliente")) {
                getMinutasDetalhada("pedido", " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' and minI.usu_codcli = " + txtPesquisar.getText());
            }

            if (txtSelecionar.getSelectedItem().equals("Minuta")) {
                getMinutasDetalhada("pedido", " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "'and minI.usu_codlan = " + txtPesquisar.getText());
            }

            if (txtSelecionar.getSelectedItem().equals("Pré Fatura")) {
                getMinutasDetalhada("pedido", " and min.usu_datemi >= '" + datIni + "' and min.usu_datemi <='" + datFim + "' and minI.usu_numnfa = " + txtPesquisar.getText());
            }

        } catch (ParseException ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void optPedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPedActionPerformed
        try {
            pegarDataDigitada();
            getMinutasDetalhada("pedido", " \nand min.usu_datemi >= '" + datIni + "'"
                    + "\n and min.usu_datemi <='" + datFim + "' ");
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optPedActionPerformed

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed
        try {
            gerarRelatorio("");
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar2ActionPerformed

    private void optExc1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optExc1ActionPerformed

        try {
            pegarDataDigitada();
            getMinutasDetalhada("pedido", " \nand min.usu_datemi >= '" + datIni + "' "
                    + "\nand min.usu_datemi <='" + datFim + "'"
                    + "\n and minI.usu_sitmin = 'FATURADA' ");
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_optExc1ActionPerformed

    private void btnFiltrar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar3ActionPerformed
        try {
            txtDatIni.setDate(this.utilDatas.retornaDataIni(new Date()));
            pegarDataDigitada();
            getMinutas("met", " and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "'  and usu_libmot = 'I'");

        } catch (ParseException ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar3ActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        btnCancelar.setEnabled(false);
        if (ManipularRegistros.gravarRegistros(" cancelar ")) {
            try {

                cancelarMinuta();
            } catch (SQLException ex) {
                Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnCancelarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar1;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar3;
    private javax.swing.JButton btnFiltrar4;
    private javax.swing.JButton btnManutencao;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JRadioButton optAbe;
    private javax.swing.JRadioButton optExc;
    private javax.swing.JRadioButton optExc1;
    private javax.swing.JRadioButton optFec;
    private javax.swing.JRadioButton optPed;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private org.openswing.swing.client.TextControl txtPesquisar;
    private javax.swing.JComboBox<String> txtSelecionar;
    // End of variables declaration//GEN-END:variables
}
