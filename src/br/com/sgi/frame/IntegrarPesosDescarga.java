/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.TirarPrint;
import br.com.sgi.bean.CargaItens;
import br.com.sgi.bean.Embalagem;
import br.com.sgi.dao.CargaItensDAO;
import br.com.sgi.dao.ProdutoDAO;
import java.awt.AWTException;
import java.awt.Dimension;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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
public class IntegrarPesosDescarga extends InternalFrame {

    private boolean addNewReg;
    private boolean showMsgErros;

    private IntegrarPesosRegistrar veioCampo;
    private frmMinutasExpedicaoGerar veioCampoPedido;

    private CargaItens cargaItens;

    public IntegrarPesosDescarga() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Produtos"));
            this.setSize(800, 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    private void gravar() throws SQLException, AWTException, IOException {
        cargaItens.setCodigoEmbalagem(codigoEmbalagem);
        cargaItens.setPesoEmbalagem(txtEmbalagemDesconto.getDouble());
        cargaItens.setPesoItem(txtEmbalagemQuantidade.getDouble());
        cargaItens.setPesoUnitario(txtEmbalagemPeso.getDouble());
        if (txtObservacao.getValue() != null) {
            cargaItens.setObservacao(txtObservacao.getValue().toString());
        }

        CargaItensDAO dao = new CargaItensDAO();
        if (!dao.gravarEmbalagem(cargaItens)) {

        } else {
            atualizarLista();
            TirarPrint.tirarPrint(cargaItens.getNumerocarga().toString() + "-EMB.png");
        }

    }

    private void atualizarLista() {
        if (veioCampo != null) {
            try {
                veioCampo.retonarEmbalagem();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {

            }
        }
    }

    private void sair() {
        if (veioCampo != null) {
            try {
                // veioCampo.retonarEmbalagem();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {

                this.dispose();
            }

        }
    }

    public void setRecebePalavra(IntegrarPesosRegistrar veioInput, CargaItens cargaItens) throws Exception {
        this.veioCampo = veioInput;
        this.cargaItens = cargaItens;
        if (cargaItens != null) {
            if (cargaItens.getSequenciacarga() > 0) {
                txtNota.setText(cargaItens.getNota().toString());
                txtProduto.setText(cargaItens.getProduto());
                txtDescricao.setText(cargaItens.getDescricao());
                getEmbalagens();
            }
        }

    }

    public void setRecebePalavraPedidos(frmMinutasExpedicaoGerar veioInput, CargaItens cargaItens) throws Exception {
        this.veioCampoPedido = veioInput;
        this.cargaItens = new CargaItens();
        txtNota.setText(cargaItens.getNota().toString());
        txtProduto.setText(cargaItens.getProduto());
        txtDescricao.setText(cargaItens.getDescricao());
        getEmbalagens();

    }

    private void calcularDesconto() {
        btnGravar.setEnabled(false);
        double pesoEmb = txtEmbalagemPeso.getDouble();
        double qtdyEmb = txtEmbalagemQuantidade.getDouble();
        double descEmb = pesoEmb * qtdyEmb;
        if (descEmb > 0) {
            btnGravar.setEnabled(true);
        }
        txtEmbalagemDesconto.setValue(descEmb);
    }

    private void getEmbalagens() throws SQLException, Exception {
        List<Embalagem> lista = new ArrayList<Embalagem>();
        ProdutoDAO dao = new ProdutoDAO();
        lista = dao.getEmbalagemsGeral();
        carregarTabela(lista);

    }

    public void carregarTabela(List<Embalagem> lista) throws Exception {
        addNewReg = true;
        ImageIcon sitok = getImage("/images/sitBom.png");
        redColunastab();
        int linhas = 0;

        DefaultTableModel modeloCarga = (DefaultTableModel) jTableLista.getModel();
        modeloCarga.setNumRows(linhas);
        Object[] linha = new Object[15];
        for (Embalagem prg : lista) {
            TableColumnModel columnModel = jTableLista.getColumnModel();
            IntegrarPesosDescarga.JTableRenderer renderers = new IntegrarPesosDescarga.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = sitok;
            linha[1] = prg.getEmbalagem();

            linha[2] = prg.getDescricaoEmbalagem();
            linha[3] = prg.getPesoEmbalagem();
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

        jTableLista.setRowHeight(32);
        jTableLista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableLista.getColumnModel().getColumn(3).setCellRenderer(direita);

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grp = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btnGravar = new javax.swing.JButton();
        txtNota = new org.openswing.swing.client.TextControl();
        txtProduto = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDescricao = new org.openswing.swing.client.TextControl();
        txtEmbalagemQuantidade = new org.openswing.swing.client.NumericControl();
        txtEmbalagemPeso = new org.openswing.swing.client.NumericControl();
        txtEmbalagemDesconto = new org.openswing.swing.client.NumericControl();
        btnSair = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnCalcular = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLista = new javax.swing.JTable();
        txtObservacao = new org.openswing.swing.client.TextAreaControl();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pequisar clientes");
        setPreferredSize(new java.awt.Dimension(599, 188));

        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravar.setText("Gravar");
        btnGravar.setEnabled(false);
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        txtNota.setEnabled(false);
        txtNota.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNota.setRequired(true);
        txtNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotaActionPerformed(evt);
            }
        });

        txtProduto.setEnabled(false);
        txtProduto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProdutoActionPerformed(evt);
            }
        });

        jLabel1.setText("Produto");

        jLabel12.setText("Quantidade Embalagem");

        jLabel6.setText("Nota");

        txtDescricao.setEnabled(false);
        txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEmbalagemQuantidade.setDecimals(2);
        txtEmbalagemQuantidade.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEmbalagemQuantidade.setRequired(true);
        txtEmbalagemQuantidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmbalagemQuantidadeActionPerformed(evt);
            }
        });

        txtEmbalagemPeso.setDecimals(2);
        txtEmbalagemPeso.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEmbalagemPeso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmbalagemPesoActionPerformed(evt);
            }
        });

        txtEmbalagemDesconto.setDecimals(2);
        txtEmbalagemDesconto.setEnabled(false);
        txtEmbalagemDesconto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        btnSair.setText("Fechar");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        jLabel4.setText("Peso total");

        jLabel5.setText("Peso Embalagem");

        btnCalcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        btnCalcular.setText("Calcular");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        jTableLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "#", "Codigo", "Descricão", "Peso"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableListaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableLista);
        if (jTableLista.getColumnModel().getColumnCount() > 0) {
            jTableLista.getColumnModel().getColumn(0).setMinWidth(50);
            jTableLista.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableLista.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableLista.getColumnModel().getColumn(1).setMinWidth(50);
            jTableLista.getColumnModel().getColumn(1).setPreferredWidth(50);
            jTableLista.getColumnModel().getColumn(1).setMaxWidth(50);
            jTableLista.getColumnModel().getColumn(3).setMinWidth(100);
            jTableLista.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableLista.getColumnModel().getColumn(3).setMaxWidth(100);
        }

        txtObservacao.setMaxCharacters(500);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(txtObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        jTabbedPane1.addTab("Embalagens", jPanel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmbalagemQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel12))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmbalagemPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel5))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txtEmbalagemDesconto, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCalcular)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSair)))
                .addGap(2, 2, 2))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel12, jLabel6});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCalcular, btnSair});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(5, 5, 5))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmbalagemQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmbalagemDesconto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmbalagemPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSair)))
                .addGap(2, 2, 2))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCalcular, btnGravar, btnSair});

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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        try {
            try {
                gravar();
            } catch (AWTException ex) {
                Logger.getLogger(IntegrarPesosDescarga.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(IntegrarPesosDescarga.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(IntegrarPesosDescarga.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGravarActionPerformed

    private void txtProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProdutoActionPerformed

    private void txtNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNotaActionPerformed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        calcularDesconto();
    }//GEN-LAST:event_btnCalcularActionPerformed
    private String codigoEmbalagem;
    private void jTableListaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableListaMouseClicked
        int linhaSelSit = jTableLista.getSelectedRow();
        int colunaSelSit = jTableLista.getSelectedColumn();
        codigoEmbalagem = jTableLista.getValueAt(linhaSelSit, 1).toString();
        double peso = Double.parseDouble(jTableLista.getValueAt(linhaSelSit, 3).toString());
        txtEmbalagemPeso.setValue(peso);

    }//GEN-LAST:event_jTableListaMouseClicked

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        sair();
    }//GEN-LAST:event_btnSairActionPerformed

    private void txtEmbalagemQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmbalagemQuantidadeActionPerformed
        calcularDesconto();
        txtEmbalagemPeso.requestFocus();
    }//GEN-LAST:event_txtEmbalagemQuantidadeActionPerformed

    private void txtEmbalagemPesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmbalagemPesoActionPerformed
        calcularDesconto();
    }//GEN-LAST:event_txtEmbalagemPesoActionPerformed
    private Integer coderp;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnSair;
    private javax.swing.ButtonGroup grp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableLista;
    private org.openswing.swing.client.TextControl txtDescricao;
    private org.openswing.swing.client.NumericControl txtEmbalagemDesconto;
    private org.openswing.swing.client.NumericControl txtEmbalagemPeso;
    private org.openswing.swing.client.NumericControl txtEmbalagemQuantidade;
    private org.openswing.swing.client.TextControl txtNota;
    private org.openswing.swing.client.TextAreaControl txtObservacao;
    private org.openswing.swing.client.TextControl txtProduto;
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
