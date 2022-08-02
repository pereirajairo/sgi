/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.OrdemCompra;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.dao.OrdemCompraDAO;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.util.UtilDatas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
public class IntegrarOrdemCompra extends InternalFrame {

    private boolean addNewReg;
    private boolean showMsgErros;
    private IntegrarPesosRegistrar veioCampo;

    private SucataMovimento sucataMovimento;
    private List<SucataMovimento> listSucataMovimento = new ArrayList<SucataMovimento>();
    private SucataMovimentoDAO sucataMovimentoDAO;

    private UtilDatas utilDatas;
    private String datacorte;

    public IntegrarOrdemCompra() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Ordem de Compra"));
            this.setSize(800, 500);
            this.sucataMovimentoDAO = new SucataMovimentoDAO();
            this.utilDatas = new UtilDatas();
            txtDataCorte.setValue(this.utilDatas.retornaDataIniMes(new Date()));
            txtDataCorte1.setValue(new Date());

            this.processo = "IND";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String retornsarData() throws ParseException {

        datacorte = utilDatas.converterDateToStr(txtDataCorte.getDate());
        return datacorte;
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void setRecebePalavra(IntegrarPesosRegistrar veioInput, String processo, String complemento) throws Exception {
        btnPesquisar.setEnabled(false);
        this.processo = processo;
        btnAut.setEnabled(false);
        btnMot.setEnabled(false);
        btnInd.setEnabled(false);

        if (processo.equals("D")) {
            this.processo = "IND";
            btnInd.setEnabled(true);

        }
        if (complemento.equals("ECO")) {
            this.processo = "AUT";
            btnInd.setEnabled(false);
            btnAut.setEnabled(true);
            btnMot.setEnabled(true);
        }

        getListar("", "");
        this.veioCampo = veioInput;

    }

    private String processo;

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (this.processo.equals("IND")) {
            PESQUISA += "\nand usu_debcre  in ('1 - GERADO','2 - PROVISIONADO') and usu_autmot in ('" + this.processo + "') and usu_numocp > 0";
        } else {
            PESQUISA += "\nand usu_debcre  in ('3 - DEBITO') and usu_autmot in ('" + this.processo + "') and usu_numocp > 0";
        }

        listSucataMovimento = this.sucataMovimentoDAO.getSucatasMovimentoOcp(PESQUISA_POR, PESQUISA);
        if (listSucataMovimento != null) {
            carregarTabela();
        }

    }

    public void carregarTabela() throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableInfo.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon IndIcon = getImage("/images/bateriaindu.png");

        for (SucataMovimento oc : listSucataMovimento) {
            Object[] linha = new Object[15];
            TableColumnModel columnModel = jTableInfo.getColumnModel();
            IntegrarOrdemCompra.JTableRenderer renderers = new IntegrarOrdemCompra.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            // popula as colunas
            if (oc.getAutomoto().equals("AUT")) {
                linha[0] = AutIcon;
            }
            if (oc.getAutomoto().equals("MOT")) {
                linha[0] = MotIcon;
            }
            if (oc.getAutomoto().equals("IND")) {
                linha[0] = IndIcon;
            }
            linha[1] = oc.getOrdemcompra();
            linha[2] = oc.getCliente();
            linha[3] = oc.getCadCliente().getNome();
            linha[4] = oc.getDatageracaoS();
            linha[5] = oc.getEmpresa();
            linha[6] = oc.getFilial();
            linha[7] = oc.getCadCliente().getCpfcnpj();
            linha[8] = oc.getPesoordemcompra();
            linha[9] = oc.getPesoordemcompra();
            linha[10] = oc.getCadCliente().getCidade();
            linha[11] = oc.getCadCliente().getEstado();
            linha[12] = oc.getObservacaomovimento();
            
            linha[13] = oc.getProduto();
            

            modeloCarga.addRow(linha);
        }

    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableInfo.setRowHeight(40);
        jTableInfo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTableInfo.getColumnModel().getColumn(1).setCellRenderer(direita);
       // jTableInfo.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableInfo.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableInfo.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableInfo.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableInfo.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableInfo.getColumnModel().getColumn(8).setCellRenderer(direita);

        jTableInfo.setAutoCreateRowSorter(true);
        jTableInfo.setAutoResizeMode(0);

    }

    private ImageIcon getImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return (new ImageIcon(url));
        } else {
            return null;

        }
    }

    private void filtrar() {
        try {
            if (btnOc.isSelected()) {
                getListar("ORDEMCOMPRA", " and suc.usu_numocp = '" + txtPesquisar.getText() + "'");
            }
            if (opcCnpj.isSelected()) {
                getListar("ORDEMCOMPRA", " and cli.cgccpf  like '%" + txtPesquisar.getText() + "%'");
            }
            if (opcNome.isSelected()) {
                getListar("ORDEMCOMPRA", " and cli.nomcli  like '%" + txtPesquisar.getText() + "%'");
            }
            if (opcCod.isSelected()) {
                getListar("ORDEMCOMPRA", " and ocp.codfor  = '" + txtPesquisar.getText() + "'");
            }

        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filtrarData() throws ParseException {
        String dataini = this.utilDatas.converterDateToStr(txtDataCorte.getDate());
        String datafim = this.utilDatas.converterDateToStr(txtDataCorte1.getDate());
        try {

            getListar("ORDEMCOMPRA", " and suc.usu_datmov = '" + dataini + "' and suc.usu_datmov ='" + datafim + "'");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompra.class.getName()).log(Level.SEVERE, null, ex);
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

    private void limparTela() {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableInfo.getModel();
        modeloCarga.setNumRows(0);
        txtPesquisar.setText("");
        txtPesquisar.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grp = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableInfo = new javax.swing.JTable();
        txtPesquisar = new org.openswing.swing.client.TextControl();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        txtNomeFornecedor = new org.openswing.swing.client.TextControl();
        btnOc = new javax.swing.JRadioButton();
        opcCnpj = new javax.swing.JRadioButton();
        txtFilial = new org.openswing.swing.client.TextControl();
        txtEmpresa = new org.openswing.swing.client.TextControl();
        opcNome = new javax.swing.JRadioButton();
        opcCod = new javax.swing.JRadioButton();
        txtDataCorte = new org.openswing.swing.client.DateControl();
        jLabel2 = new javax.swing.JLabel();
        txtDataCorte1 = new org.openswing.swing.client.DateControl();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnPesquisar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnPesquisar1 = new javax.swing.JButton();
        btnAut = new javax.swing.JButton();
        btnMot = new javax.swing.JButton();
        btnInd = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximizable(false);
        setTitle("Ordem de Compras");
        setPreferredSize(new java.awt.Dimension(599, 188));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableInfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Ordem compra", "Código", "Nome", "Data ", "Empresa", "Filial", "Cnpj", "Peso", "Quantidade", "Cidade", "Estado", "Observação", "Pro", "Despro"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableInfoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableInfo);
        if (jTableInfo.getColumnModel().getColumnCount() > 0) {
            jTableInfo.getColumnModel().getColumn(0).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(1).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(2).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(3).setMinWidth(500);
            jTableInfo.getColumnModel().getColumn(3).setPreferredWidth(500);
            jTableInfo.getColumnModel().getColumn(3).setMaxWidth(500);
            jTableInfo.getColumnModel().getColumn(4).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(5).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(5).setMaxWidth(0);
            jTableInfo.getColumnModel().getColumn(6).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(6).setMaxWidth(0);
            jTableInfo.getColumnModel().getColumn(7).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(7).setMaxWidth(0);
            jTableInfo.getColumnModel().getColumn(8).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(9).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(10).setMinWidth(100);
            jTableInfo.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableInfo.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableInfo.getColumnModel().getColumn(11).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(11).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(11).setMaxWidth(0);
            jTableInfo.getColumnModel().getColumn(12).setMinWidth(400);
            jTableInfo.getColumnModel().getColumn(12).setPreferredWidth(400);
            jTableInfo.getColumnModel().getColumn(12).setMaxWidth(400);
            jTableInfo.getColumnModel().getColumn(13).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(13).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(13).setMaxWidth(0);
            jTableInfo.getColumnModel().getColumn(14).setMinWidth(0);
            jTableInfo.getColumnModel().getColumn(14).setPreferredWidth(0);
            jTableInfo.getColumnModel().getColumn(14).setMaxWidth(0);
        }

        txtPesquisar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarActionPerformed(evt);
            }
        });
        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyTyped(evt);
            }
        });

        txtOrdemCompra.setEnabled(false);
        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtOrdemCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOrdemCompraActionPerformed(evt);
            }
        });

        txtNomeFornecedor.setEnabled(false);
        txtNomeFornecedor.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        buttonGroup1.add(btnOc);
        btnOc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnOc.setSelected(true);
        btnOc.setText("Ordem");
        btnOc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOcActionPerformed(evt);
            }
        });

        buttonGroup1.add(opcCnpj);
        opcCnpj.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        opcCnpj.setText("Por Cnpj");
        opcCnpj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcCnpjActionPerformed(evt);
            }
        });

        txtFilial.setEnabled(false);
        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtFilial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialActionPerformed(evt);
            }
        });

        txtEmpresa.setEnabled(false);
        txtEmpresa.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmpresaActionPerformed(evt);
            }
        });

        buttonGroup1.add(opcNome);
        opcNome.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        opcNome.setText("Por Nome");
        opcNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcNomeActionPerformed(evt);
            }
        });

        buttonGroup1.add(opcCod);
        opcCod.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        opcCod.setText("Por Código");
        opcCod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcCodActionPerformed(evt);
            }
        });

        txtDataCorte.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataCorte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataCorteActionPerformed(evt);
            }
        });

        jLabel2.setText("Data Inicial");

        txtDataCorte1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDataCorte1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataCorte1ActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N

        jLabel3.setText("Data Final");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNomeFornecedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDataCorte, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnOc, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(opcCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(opcNome, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(opcCod, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtDataCorte1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addGap(2, 2, 2))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                        .addComponent(opcCnpj, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(opcNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(opcCod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3))
                    .addComponent(btnOc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDataCorte1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
                    .addComponent(txtDataCorte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomeFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnOc, opcCnpj, opcCod, opcNome});

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, txtPesquisar});

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton2, txtDataCorte1});

        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesquisar.setText("Confirmar Ordem");
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        jLabel1.setText("Ordens de Compra");

        btnPesquisar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnPesquisar1.setText("Sair");
        btnPesquisar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisar1ActionPerformed(evt);
            }
        });

        btnAut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/carros.png"))); // NOI18N
        btnAut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutActionPerformed(evt);
            }
        });

        btnMot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        btnMot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMotActionPerformed(evt);
            }
        });

        btnInd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        btnInd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIndActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPesquisar1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMot, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPesquisar1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAut, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
private String codigoOrdemCompra;
    private String nomeFornecedor;
    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        if (veioCampo != null) {
            try {
                Pedido pedido = new Pedido();
                veioCampo.retornarOrdemCompra(codigoOrdemCompra, nomeFornecedor, txtEmpresa.getText(), txtFilial.getText(), "ENTRADA", pedido, " ");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }

        }

    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void jTableInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableInfoMouseClicked
        int linhaSelSit = jTableInfo.getSelectedRow();
        int colunaSelSit = jTableInfo.getSelectedColumn();
        this.codigoOrdemCompra = jTableInfo.getValueAt(linhaSelSit, 1).toString();
        this.nomeFornecedor = jTableInfo.getValueAt(linhaSelSit, 3).toString();

        txtEmpresa.setText(jTableInfo.getValueAt(linhaSelSit, 5).toString());
        txtFilial.setText(jTableInfo.getValueAt(linhaSelSit, 6).toString());
        txtOrdemCompra.setText(codigoOrdemCompra);
        txtNomeFornecedor.setText(nomeFornecedor);
        btnPesquisar.setEnabled(false);
        if (evt.getClickCount() == 2) {
            if (!codigoOrdemCompra.isEmpty()) {
                btnPesquisar.setEnabled(true);
            } else {
                btnPesquisar.setEnabled(false);
            }
        }

    }//GEN-LAST:event_jTableInfoMouseClicked

    private void txtPesquisarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyPressed
//       try {
//            getListar("TRANSPOTADORA", " and nomtra like '%" + txtPesquisar.getText() + "%'");
//        } catch (Exception ex) {
//            Logger.getLogger(IntegrarOrdemCompra.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_txtPesquisarKeyPressed

    private void txtPesquisarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyTyped
//        try {
//            getListar("TRANSPOTADORA", " and nomtra like '%" + txtPesquisar.getText() + "%'");
//        } catch (Exception ex) {
//            Logger.getLogger(IntegrarOrdemCompra.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_txtPesquisarKeyTyped

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed

        filtrar();
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void txtOrdemCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOrdemCompraActionPerformed
        try {
            getListar("PRODUTO", " and codpro = '" + txtOrdemCompra.getText() + "'");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtOrdemCompraActionPerformed

    private void btnPesquisar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisar1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnPesquisar1ActionPerformed

    private void txtFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilialActionPerformed

    private void txtEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmpresaActionPerformed

    private void opcCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcCodActionPerformed
        limparTela();
    }//GEN-LAST:event_opcCodActionPerformed

    private void btnOcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOcActionPerformed
        limparTela();
    }//GEN-LAST:event_btnOcActionPerformed

    private void opcCnpjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcCnpjActionPerformed
        limparTela();
    }//GEN-LAST:event_opcCnpjActionPerformed

    private void opcNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcNomeActionPerformed
        limparTela();
    }//GEN-LAST:event_opcNomeActionPerformed

    private void txtDataCorteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataCorteActionPerformed
        try {
            filtrarData();
        } catch (ParseException ex) {
            Logger.getLogger(IntegrarOrdemCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtDataCorteActionPerformed

    private void txtDataCorte1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataCorte1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDataCorte1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        filtrar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnAutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutActionPerformed

        try {
            this.processo = "AUT";
            getListar("", "");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAutActionPerformed

    private void btnMotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMotActionPerformed

        try {
            this.processo = "MOT";
            getListar("", "");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnMotActionPerformed

    private void btnIndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIndActionPerformed

        try {
            this.processo = "IND";
            getListar("", "");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarOrdemCompra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnIndActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAut;
    private javax.swing.JButton btnInd;
    private javax.swing.JButton btnMot;
    private javax.swing.JRadioButton btnOc;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnPesquisar1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup grp;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableInfo;
    private javax.swing.JRadioButton opcCnpj;
    private javax.swing.JRadioButton opcCod;
    private javax.swing.JRadioButton opcNome;
    private org.openswing.swing.client.DateControl txtDataCorte;
    private org.openswing.swing.client.DateControl txtDataCorte1;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.TextControl txtFilial;
    private org.openswing.swing.client.TextControl txtNomeFornecedor;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.TextControl txtPesquisar;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the pessoa
     */
    /**
     * @return the addNewReg
     */
    public boolean isAddNewReg() {
        return addNewReg;
    }

    /**
     * @param addNewReg the addNewReg to set
     */
    public void setAddNewReg(boolean addNewReg) {
        this.addNewReg = addNewReg;
    }

    /**
     * @return the showMsgErros
     */
    public boolean isShowMsgErros() {
        return showMsgErros;
    }

    /**
     * @param showMsgErros the showMsgErros to set
     */
    public void setShowMsgErros(boolean showMsgErros) {
        this.showMsgErros = showMsgErros;
    }
}
