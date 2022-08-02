/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.SucataDAO;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class SucataSenha extends InternalFrame {

    private UtilDatas utilDatas;
    private SucatasManual veioCampo;
    private SucataManualIndustrializacao veioCampoIndustrializacao;
    private SucataManualAuto veioCampoManualAuto;
    private FatPedidoHub veioFatPedidoHub;

    private Sucata sucata;

    private SucataDAO sucataDAO;
    private SucataMovimentoDAO sucataMovimentoDAO;
    private SucataMovimento sucataMovimento;

    public SucataSenha() {
        try {
            initComponents();

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void setRecebePalavraPedidoHub(FatPedidoHub veioInput) throws Exception {
        this.veioFatPedidoHub = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Manutenção de pedido HUB " ));
        txtSenha.requestFocus();

    }

    public void setRecebePalavra(SucatasManual veioInput, String TIPO_PESAGEM, String PROCESSO, SucataMovimento sucataMovimento) throws Exception {
        this.veioCampo = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Manutenção de sucata " + sucataMovimento.getSequencia()));
        txtSenha.requestFocus();

    }

    public void setRecebePalavraAuto(SucataManualAuto veioInput, String TIPO_PESAGEM, String PROCESSO, SucataMovimento sucataMovimento) throws Exception {
        this.veioCampoManualAuto = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Manutenção de sucata " + sucataMovimento.getSequencia()));
        txtSenha.requestFocus();

    }

    public void setRecebePalavraIndustrializacao(SucataManualIndustrializacao veioInput, String TIPO_PESAGEM, String PROCESSO, SucataMovimento sucataMovimento) throws Exception {
        this.veioCampoIndustrializacao = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Manutenção de sucata " + sucataMovimento.getSequencia()));
        txtSenha.requestFocus();

    }
    private Usuario usuario;

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        UsuarioERPDAO dao = new UsuarioERPDAO();

        usuario = dao.getUsuario(Menu.username.toLowerCase());
        return usuario;
    }

    private void validarSenha(String senhaDig) {
        int dia = this.utilDatas.retornaDia(new Date());
        String senha = "ADMIN" + dia;
        txtInfo.setEnabled(false);
        btnConfirmar.setEnabled(false);
        if (txtSenha.getText().equals(senha)) {
            btnConfirmar.setEnabled(true);
            txtInfo.setEnabled(true);
            txtInfo.requestFocus();

        } else {
            Mensagem.mensagemRegistros("ERRO", "Senha Não confere");
        }

    }

    private void validarPeso(String retorno) {
        if (retorno.equals("ERRO")) {
            Mensagem.mensagemRegistros("ERRO", "Informe um motivo");
            txtInfo.requestFocus();
        } else {
            if (veioCampo != null) {
                try {
                    getUsuarioLogado();
                    veioCampo.removerRegistro(true, "REGISTRADO " + this.usuario.getId() + " - " + this.usuario.getNome() + " " + txtInfo.getValue().toString());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Problemas." + ex);
                } finally {
                    this.dispose();
                }
            }
            if (veioCampoManualAuto != null) {
                try {
                    getUsuarioLogado();
                    veioCampoManualAuto.removerRegistro(true, "REGISTRADO " + this.usuario.getId() + " - " + this.usuario.getNome() + " " + txtInfo.getValue().toString());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Problemas." + ex);
                } finally {
                    this.dispose();
                }
            }
            if (veioCampoIndustrializacao != null) {
                try {
                    getUsuarioLogado();
                    veioCampoIndustrializacao.removerRegistro(true, "REGISTRADO " + this.usuario.getId() + " - " + this.usuario.getNome() + " " + txtInfo.getValue().toString());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Problemas." + ex);
                } finally {
                    this.dispose();
                }
            }

            if (veioFatPedidoHub != null) {
                try {
                    getUsuarioLogado();
                    veioFatPedidoHub.cancelarPedido("PEDIDO CANCELADO " + this.usuario.getId() + " - " + this.usuario.getNome() + " \n" + txtInfo.getValue().toString());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Problemas." + ex);
                } finally {
                    this.dispose();
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

        jLabel12 = new javax.swing.JLabel();
        txtSenha = new javax.swing.JPasswordField();
        jLabel13 = new javax.swing.JLabel();
        txtInfo = new org.openswing.swing.client.TextAreaControl();
        btnConfirmar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jLabel12.setText("Senha");

        txtSenha.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSenha.setText("jPasswordField1");
        txtSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaActionPerformed(evt);
            }
        });

        jLabel13.setText("Info:");

        txtInfo.setEnabled(false);
        txtInfo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnConfirmar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnConfirmar.setText("Confirmar");
        btnConfirmar.setEnabled(false);
        btnConfirmar.setFocusCycleRoot(true);
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSenha)
                    .addComponent(txtInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(btnConfirmar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                        .addComponent(btnCancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCancelar, btnConfirmar});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaActionPerformed
        validarSenha(txtSenha.getText());
    }//GEN-LAST:event_txtSenhaActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        if (ManipularRegistros.pesos("Deseja excluir esse registro?")) {
            if (txtInfo.getValue() != null) {
                String retonro = txtInfo.getValue().toString();
                if (retonro.isEmpty()) {
                    validarPeso("ERRO");
                } else {
                    validarPeso("OK");
                }
            }
        }


    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private org.openswing.swing.client.TextAreaControl txtInfo;
    private javax.swing.JPasswordField txtSenha;
    // End of variables declaration//GEN-END:variables
}
