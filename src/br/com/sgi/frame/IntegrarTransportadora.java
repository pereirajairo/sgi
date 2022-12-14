/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Transportadora;
import br.com.sgi.dao.TransportadoraDAO;
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
public class IntegrarTransportadora extends InternalFrame {

    private boolean addNewReg;
    private boolean showMsgErros;
    private IntegrarPesosRegistrar veioCampo;
    private IntegrarPedidoVenda veioCampoTransportadora;
    private LogMinutaEmbarqueNota veioCampoNota;

    private TransportadoraDAO transportadoraDAO;
    private List<Transportadora> lstTransportadora = new ArrayList<Transportadora>();
    private String CodSel = " ";
    private String DesSel = " ";

    public IntegrarTransportadora() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Pesquisa de clientes"));
            this.setSize(800, 500);
            this.transportadoraDAO = new TransportadoraDAO();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void setRecebePalavra(IntegrarPesosRegistrar veioInput) {
        btnPesquisar.setEnabled(false);
        this.veioCampo = veioInput;
        txtPesquisar.requestFocus();
        return;
    }

    public void setRecebePalavraTransportadora(IntegrarPedidoVenda veioInputTransportadora) {
        btnPesquisar.setEnabled(false);
        this.veioCampoTransportadora = veioInputTransportadora;
        txtPesquisar.requestFocus();
        return;
    }

     public void setRecebePalavraNota(LogMinutaEmbarqueNota veioInputNota) {
        btnPesquisar.setEnabled(false);
        this.veioCampoNota = veioInputNota;
        txtPesquisar.requestFocus();
        return;
    }

    
    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        lstTransportadora = this.transportadoraDAO.getTransportadoras(PESQUISA_POR, PESQUISA);
        if (lstTransportadora != null) {
            carregarTabela();

        }

    }

    public void carregarTabela() throws Exception {
        jTableDefeitos.setRowHeight(32);
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableDefeitos.getModel();
        modeloCarga.setNumRows(0);

        for (Transportadora tra : lstTransportadora) {
            Object[] linha = new Object[5];

            // popula as colunas
            linha[0] = tra.getCodigoTransportadora();
            linha[1] = tra.getNomeTransportadora();
            linha[2] = tra.getCidade();
            linha[3] = tra.getEstado();
            linha[4] = tra.getApelido();
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
        btnPesquisar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximizable(false);
        setTitle("Pequisar clientes");
        setPreferredSize(new java.awt.Dimension(599, 188));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableDefeitos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Raz??o Social", "Cidade", "Ufs", "Apelido"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
            jTableDefeitos.getColumnModel().getColumn(0).setMinWidth(70);
            jTableDefeitos.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTableDefeitos.getColumnModel().getColumn(0).setMaxWidth(70);
            jTableDefeitos.getColumnModel().getColumn(2).setMinWidth(150);
            jTableDefeitos.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTableDefeitos.getColumnModel().getColumn(2).setMaxWidth(150);
            jTableDefeitos.getColumnModel().getColumn(3).setMinWidth(50);
            jTableDefeitos.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTableDefeitos.getColumnModel().getColumn(3).setMaxWidth(50);
            jTableDefeitos.getColumnModel().getColumn(4).setMinWidth(50);
            jTableDefeitos.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTableDefeitos.getColumnModel().getColumn(4).setMaxWidth(50);
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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 931, Short.MAX_VALUE)
            .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesquisar.setText("Confirmar Pesquisa");
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        jLabel1.setText("Nome");

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(2, 2, 2))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPesquisar)
                    .addComponent(btnSair))
                .addGap(2, 2, 2))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnPesquisar, btnSair});

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
private String codigoTransportadora;
    private String nomeTransportadora;
    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        if (veioCampo != null) {
            try {
                veioCampo.recebendoTransportes(codigoTransportadora, nomeTransportadora);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }

        }
        if (veioCampoTransportadora != null) {
            try {
                veioCampoTransportadora.recebendoTransportes(codigoTransportadora, nomeTransportadora);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }

        }
        
         if (veioCampoNota != null) {
            try {
                veioCampoNota.retornarTranspotadora(codigoTransportadora, nomeTransportadora);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }

        }

    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void jTableDefeitosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDefeitosMouseClicked
        int linhaSelSit = jTableDefeitos.getSelectedRow();
        int colunaSelSit = jTableDefeitos.getSelectedColumn();
        this.codigoTransportadora = jTableDefeitos.getValueAt(linhaSelSit, 0).toString();
        this.nomeTransportadora = jTableDefeitos.getValueAt(linhaSelSit, 1).toString();
        if (evt.getClickCount() == 2) {
            if (!CodSel.isEmpty()) {
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
//            Logger.getLogger(IntegrarTransportadora.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_txtPesquisarKeyPressed

    private void txtPesquisarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyTyped
//        try {
//            getListar("TRANSPOTADORA", " and nomtra like '%" + txtPesquisar.getText() + "%'");
//        } catch (Exception ex) {
//            Logger.getLogger(IntegrarTransportadora.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_txtPesquisarKeyTyped

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed
        try {
            getListar("TRANSPOTADORA", " and nomtra like '%" + txtPesquisar.getText() + "%'");
        } catch (Exception ex) {
            Logger.getLogger(IntegrarTransportadora.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnSair;
    private javax.swing.ButtonGroup grp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableDefeitos;
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
