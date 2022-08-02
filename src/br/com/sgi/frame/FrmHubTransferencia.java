/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Filial;
import br.com.sgi.bean.NotaSerie;
import br.com.sgi.bean.Representante;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.NotaSerieDAO;
import br.com.sgi.dao.RepresentanteDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;

import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author jairosilva
 */
public final class FrmHubTransferencia extends InternalFrame {

    private List<NotaSerie> listNotaSerie = new ArrayList<NotaSerie>();
    private NotaSerieDAO notaSerieDAO;
    private NotaSerie notaSerie;
    private String pesquisa_por;
    private String pesquisa;
    private UtilDatas utilDatas;

    public FrmHubTransferencia() {
        try {
            initComponents();
            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

            if (notaSerieDAO == null) {
                notaSerieDAO = new NotaSerieDAO();
            }

            this.setSize(800, 500);
            getUsuarioLogado();
            preencherComboHub(0);
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

    private void pesquisarTable(String pesquisa) {
        DefaultTableModel tabela_pedidos = (DefaultTableModel) jTableEnvio.getModel();

        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabela_pedidos);
        jTableEnvio.setRowSorter(sorter);
        pesquisa = txtPesquisar.getText().toUpperCase();

        if (pesquisa.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<TableModel, Object> rf = null;
                try {
                    rf = RowFilter.regexFilter(pesquisa, 4);
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                sorter.setRowFilter(rf);
            } catch (PatternSyntaxException pse) {
                System.err.println("Erro");
            }
        }
    }
    private int contador = 0;

    private void integrarSeries() throws SQLException {
        if (listNotaSerie != null) {
            if (listNotaSerie.size() > 0) {
                if (!notaSerieDAO.integrarNotaSerieApp(listNotaSerie)) {
                } else {

                }
            }
        }
    }

    public static void cadastro(String contador) {
        lblInfo.setText(" --> " + contador);
    }

    private void getNota(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listNotaSerie = notaSerieDAO.getNotaSeries(" nota ", " and E140NFV.NUMNFV =  " + txtNotaSaida.getText() + " ");
        if (listNotaSerie != null) {
            lblInfo1.setText(" " + listNotaSerie.size());
            carregarTabela(true);
        }
    }
    
     private void getNotaOrigem(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listNotaSerie = notaSerieDAO.getNotaSeriesHub(PESQUISA_POR, PESQUISA);
        if (listNotaSerie != null) {
            lblInfo1.setText(" " + listNotaSerie.size());
            carregarTabela(true);
        }
    }

    public void iniciarBarraTabela(String pesquisa_por, String pesquisa) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Integrando processo ");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getNota("", "");
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                barra.setString("Filtro carregado");
            }
        };
        worker.execute();
    }

    public void iniciarBarraIntegrar(String pesquisa_por, String pesquisa) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Filtrando processo ");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                integrarSeries();
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                barra.setString("Filtro carregado");
            }
        };
        worker.execute();
    }

    public void iniciarBarraOrigem(String pesquisa_por, String pesquisa) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Integrando processo ");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getNotaOrigem("", "");
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                barra.setString("Filtro carregado");
            }
        };
        worker.execute();
    }

    private String linha;

    public void carregarTabela(boolean selecionar) throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableEnvio.getModel();
        modeloCarga.setNumRows(0);

        ImageIcon motIcon = getImage("/images/moto_erbs.png");
        ImageIcon autIcon = getImage("/images/carros.png");

        int cont = 0;

        for (NotaSerie nf : listNotaSerie) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableEnvio.getColumnModel();
            FrmHubTransferencia.JTableRenderer renderers = new FrmHubTransferencia.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            linha[0] = motIcon;
            if (nf.getOrigem().equals("BA")) {
                linha[0] = autIcon;
            }

            linha[1] = nf.getNota();
            linha[2] = nf.getProduto();
            linha[3] = nf.getProdutodescricao();
            linha[4] = nf.getSerie();
            linha[5] = true;
            linha[6] = "hub";
            linha[7] = "nome hub";
            modeloCarga.addRow(linha);

        }

    }

    private Filial filial;

    private void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableEnvio.getColumnModel().getColumn(2).setCellRenderer(direita);

        jTableEnvio.getColumnModel().getColumn(4).setCellRenderer(direita);

        jTableEnvio.setRowHeight(40);
        jTableEnvio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableEnvio.setAutoCreateRowSorter(true);
        // jTableEnvio.setAutoResizeMode(0);

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

    private Usuario usuario;

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        usuario.setId(Menu.getUsuario().getId());

        return usuario;

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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabSucata = new javax.swing.JTabbedPane();
        jPanelMovimento = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableEnvio = new javax.swing.JTable();
        btnHoras1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtNotaSaida = new org.openswing.swing.client.TextControl();
        txtPesquisar = new org.openswing.swing.client.TextControl();
        txtHub = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        btnHoras2 = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();
        lblInfo1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnHoras3 = new javax.swing.JButton();
        barra = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Triagem Sucata");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanelMovimento.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jTableEnvio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Nota", "Produto", "Descricao", "Serie", "Selecionar", "Hub", "Nome", "Empresa", "Serie Nota"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableEnvio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableEnvioMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTableEnvioMouseEntered(evt);
            }
        });
        jScrollPane4.setViewportView(jTableEnvio);
        if (jTableEnvio.getColumnModel().getColumnCount() > 0) {
            jTableEnvio.getColumnModel().getColumn(0).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(0).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(1).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(2).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(2).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(4).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(5).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(6).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(6).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(7).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(7).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(8).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(8).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(8).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(9).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(9).setMaxWidth(0);
        }

        btnHoras1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        btnHoras1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoras1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Informe a Nota Saída");

        txtNotaSaida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtPesquisar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarActionPerformed(evt);
            }
        });

        txtHub.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtHub.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtHub.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtHubMouseClicked(evt);
            }
        });
        txtHub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHubActionPerformed(evt);
            }
        });

        jLabel2.setText("Serie");

        btnHoras2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pedido_faturado.png"))); // NOI18N
        btnHoras2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoras2ActionPerformed(evt);
            }
        });

        lblInfo.setBackground(new java.awt.Color(0, 0, 0));
        lblInfo.setForeground(new java.awt.Color(51, 255, 51));
        lblInfo.setText("Int");
        lblInfo.setOpaque(true);

        lblInfo1.setBackground(new java.awt.Color(0, 0, 0));
        lblInfo1.setForeground(new java.awt.Color(255, 255, 255));
        lblInfo1.setText("Reg");
        lblInfo1.setOpaque(true);

        jLabel3.setText("HUB");

        btnHoras3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        btnHoras3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoras3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMovimentoLayout = new javax.swing.GroupLayout(jPanelMovimento);
        jPanelMovimento.setLayout(jPanelMovimentoLayout);
        jPanelMovimentoLayout.setHorizontalGroup(
            jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtNotaSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHoras1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4))
                .addGap(5, 5, 5)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtHub, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHoras2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHoras3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(177, 177, 177)))
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblInfo1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jPanelMovimentoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblInfo, lblInfo1});

        jPanelMovimentoLayout.setVerticalGroup(
            jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNotaSaida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHoras1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtHub)
                    .addComponent(btnHoras2)
                    .addComponent(btnHoras3)
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblInfo1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        jPanelMovimentoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnHoras1, btnHoras2, btnHoras3, lblInfo, lblInfo1, txtHub, txtNotaSaida, txtPesquisar});

        jTabSucata.addTab("Nota", jPanelMovimento);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabSucata)
            .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jTabSucata)
                .addGap(2, 2, 2)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHoras1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoras1ActionPerformed
        if (!txtNotaSaida.getText().isEmpty()) {
            try {
                iniciarBarraTabela("", "");
            } catch (Exception ex) {
                Logger.getLogger(FrmHubTransferencia.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Mensagem.mensagem("ERROR", "Informe a nota fiscal de transferencia");
        }
    }//GEN-LAST:event_btnHoras1ActionPerformed


    private void jTableEnvioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEnvioMouseClicked
        try {

            int linhaSelSit = jTableEnvio.getSelectedRow();
            int colunaSelSit = jTableEnvio.getSelectedColumn();
            txtNotaSaida.setText(jTableEnvio.getValueAt(linhaSelSit, 1).toString());

        } catch (Exception ex) {
            Logger.getLogger(SucatasManutencao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableEnvioMouseClicked

    private void jTableEnvioMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEnvioMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableEnvioMouseEntered

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed
        pesquisarTable(txtPesquisar.getText());
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void txtHubMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtHubMouseClicked
        //
    }//GEN-LAST:event_txtHubMouseClicked

    private void txtHubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHubActionPerformed
        //
    }//GEN-LAST:event_txtHubActionPerformed

    private void btnHoras2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoras2ActionPerformed
        if (ManipularRegistros.gravarRegistros(" Integrar ")) {
            if (listNotaSerie != null) {
                if (listNotaSerie.size() > 0) {
                    try {
                        iniciarBarraIntegrar("", "");
                    } catch (SQLException ex) {
                        Logger.getLogger(FrmHubTransferencia.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(FrmHubTransferencia.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }


    }//GEN-LAST:event_btnHoras2ActionPerformed

    private void btnHoras3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoras3ActionPerformed
        try {
            iniciarBarraOrigem(pesquisa_por, pesquisa);
        } catch (SQLException ex) {
            Logger.getLogger(FrmHubTransferencia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmHubTransferencia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnHoras3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnHoras1;
    private javax.swing.JButton btnHoras2;
    private javax.swing.JButton btnHoras3;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanelMovimento;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabSucata;
    private javax.swing.JTable jTableEnvio;
    private static javax.swing.JLabel lblInfo;
    private static javax.swing.JLabel lblInfo1;
    private javax.swing.JComboBox<String> txtHub;
    private org.openswing.swing.client.TextControl txtNotaSaida;
    private org.openswing.swing.client.TextControl txtPesquisar;
    // End of variables declaration//GEN-END:variables
}
