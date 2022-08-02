/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

//jeffersonlo
import br.com.sgi.bean.Produto;
import br.com.sgi.dao.ProdutoDAO;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public class IntegrarProduto extends InternalFrame {

    private boolean addNewReg;
    private boolean showMsgErros;
    private IntegrarPesosRegistrar veioCampo;
    private BalancinhaConsulta veioCampoBalancinha;
    private ProdutoDAO produtoDAO;
    private List<Produto> lstProduto = new ArrayList<Produto>();

    public IntegrarProduto() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Produtos"));
            this.setSize(800, 500);
            this.produtoDAO = new ProdutoDAO();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    private String COMPLEMENTO;

    public void setRecebePalavra(IntegrarPesosRegistrar veioInput, String COMPLEMENTO) throws Exception {
        btnPesquisar.setEnabled(false);
        this.veioCampo = veioInput;
        if (!COMPLEMENTO.isEmpty()) {
            getListar("PRODUTO", " and codpro in ('1001','1002','1003','1004','1005','1006')");
        }

        return;
    }

    public void setRecebePalavraBalancinha(BalancinhaConsulta veioInput, String COMPLEMENTO) throws Exception {
        btnPesquisar.setEnabled(false);
        this.veioCampoBalancinha = veioInput;
        if (!COMPLEMENTO.isEmpty()) {
            getListar("PRODUTO", " ");
        }

        return;
    }

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        lstProduto = this.produtoDAO.getProdutos(PESQUISA);
        if (lstProduto != null) {
            carregarTabela();
        }

    }

    private void getFechar() {
        if (veioCampo != null) {
            try {
                veioCampo.carregarProcessoColeta(codigoProduto, nomeProduto);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }

        }
        if (veioCampoBalancinha != null) {
            try {
                veioCampoBalancinha.carregarProcessoColeta(codigoProduto, nomeProduto);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }

            
        }
    }

    public void carregarTabela() throws Exception {
        jTableDefeitos.setRowHeight(32);
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableDefeitos.getModel();
        modeloCarga.setNumRows(0);

        for (Produto pro : lstProduto) {
            Object[] linha = new Object[5];

            // popula as colunas
            linha[0] = pro.getCodigoproduto();
            linha[1] = pro.getDescricaoproduto();
            modeloCarga.addRow(linha);
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
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDefeitos = new javax.swing.JTable();
        txtPesquisar = new org.openswing.swing.client.TextControl();
        txtProduto = new org.openswing.swing.client.TextControl();
        txtDescricao = new org.openswing.swing.client.TextControl();
        btnPesquisar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximizable(false);
        setTitle("Produtos");
        setPreferredSize(new java.awt.Dimension(599, 188));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableDefeitos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Descrição"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableDefeitos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableDefeitosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableDefeitos);
        if (jTableDefeitos.getColumnModel().getColumnCount() > 0) {
            jTableDefeitos.getColumnModel().getColumn(0).setMinWidth(200);
            jTableDefeitos.getColumnModel().getColumn(0).setPreferredWidth(200);
            jTableDefeitos.getColumnModel().getColumn(0).setMaxWidth(200);
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

        txtProduto.setEnabled(false);
        txtProduto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProdutoActionPerformed(evt);
            }
        });

        txtDescricao.setEnabled(false);
        txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
            .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addGap(13, 13, 13)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtProduto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesquisar.setText("Confirmar Pesquisa");
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        jLabel1.setText("Nome");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        jButton1.setText("Sair");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
private String codigoProduto;
    private String nomeProduto;
    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        getFechar();

    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void jTableDefeitosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDefeitosMouseClicked
        int linhaSelSit = jTableDefeitos.getSelectedRow();
        int colunaSelSit = jTableDefeitos.getSelectedColumn();
        this.codigoProduto = jTableDefeitos.getValueAt(linhaSelSit, 0).toString();
        this.nomeProduto = jTableDefeitos.getValueAt(linhaSelSit, 1).toString();
        txtProduto.setText(codigoProduto);
        txtDescricao.setText(nomeProduto);
        if (evt.getClickCount() == 2) {
            if (!codigoProduto.isEmpty()) {
                btnPesquisar.setEnabled(true);
            } else {
                btnPesquisar.setEnabled(false);
            }

        }


    }//GEN-LAST:event_jTableDefeitosMouseClicked

    private void txtPesquisarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyPressed
//       try {
//            getListar("TRANSPOTADORA", " and nomtra like '%" + txtPesquisar.getText() + "%'");
//        } catch (Exception ex) {
//            Logger.getLogger(IntegrarProduto.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_txtPesquisarKeyPressed

    private void txtPesquisarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyTyped
//        try {
//            getListar("TRANSPOTADORA", " and nomtra like '%" + txtPesquisar.getText() + "%'");
//        } catch (Exception ex) {
//            Logger.getLogger(IntegrarProduto.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_txtPesquisarKeyTyped

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed
        try {
            getListar("PRODUTO", " and despro like '%" + txtPesquisar.getText() + "%'");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarProduto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void txtProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProdutoActionPerformed
        try {
            getListar("PRODUTO", " and codpro = '" + txtProduto.getText() + "'");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarProduto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtProdutoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPesquisar;
    private javax.swing.ButtonGroup grp;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableDefeitos;
    private org.openswing.swing.client.TextControl txtDescricao;
    private org.openswing.swing.client.TextControl txtPesquisar;
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
