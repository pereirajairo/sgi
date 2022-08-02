/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSEmailAtendimento;

import java.awt.Dimension;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import oracle.sql.DATE;

import org.openswing.swing.mdi.client.InternalFrame;

/**
 *
 * @author jairosilva s
 */
public final class LogPedidoLiberacao extends InternalFrame {

    private UtilDatas utilDatas;
    private LogPedido veioCampo;

    private Pedido pedido;
    private PedidoDAO pedidoDAO;

    public LogPedidoLiberacao() {
        try {
            initComponents();

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (pedidoDAO == null) {
                pedidoDAO = new PedidoDAO();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }
    private String selecao = "";

    public void setRecebePalavra(LogPedido veioInput, Pedido pedido, String pedidos, String selecao) throws Exception {
        this.veioCampo = veioInput;
        this.pedido = pedido;
        btnDataAgendada.setEnabled(false);
        txtDataAgenda.setDate(new Date());
        txtDataAgenda.setEnabled(false);
        if (pedido != null) {
            if (pedido.getPedido() > 0) {
                this.selecao = selecao;
                btnDataAgendada.setEnabled(true);
                  txtDataAgenda.setEnabled(true);
                txtPedido.setText(String.valueOf(pedido.getPedido()));
                txtCliente.setText(String.valueOf(pedido.getCliente()));
                txtNome.setText(pedido.getCadCliente().getNome());
                txtInfo.setText(pedido.getObservacaobloqueio());
                txtInfoLib.setText(pedido.getObservacaoliberacao());

                optSim.setEnabled(false);
                optNao.setEnabled(false);
                optFin.setEnabled(false);
                optRea.setEnabled(false);
                btnConfirmar.setEnabled(false);
                switch (this.pedido.getSituacaoLogistica()) {
                    case "S":
                        optNao.setEnabled(true);
                        optSim.setSelected(true);
                        btnConfirmar.setEnabled(true);
                        break;
                    case "N":
                        optSim.setEnabled(true);
                        optSim.setSelected(true);
                        txtInfoLib.setText("PEDIDO LIBERADO");
                        btnConfirmar.setEnabled(true);
                        break;
                    case "F":
                        optRea.setEnabled(true);
                        optFin.setSelected(true);
                        break;
                    case "R":

                        break;
                    default:
                        break;
                }

                txtInfo.setEnabled(true);
                txtInfo.requestFocus();
                btnConfirmar.setEnabled(true);
                txtDataLiberacao.setDate(new Date());
                if (pedido.getDataliberacao() != null) {
                    txtDataLiberacao.setDate(pedido.getDataliberacao());
                }
            }
        }

    }

    private void liberarPedido(String situacao) throws SQLException, ParseException, Exception {
        if (pedido != null) {
            if (pedido.getPedido() > 0) {
                getUsuarioLogado();
                pedido.setSituacaoLogistica(situacao);

                if (txtInfo.getValue() != null) {
                    pedido.setObservacaobloqueio(txtInfo.getValue().toString());
                }
                pedido.setDataliberacao(new Date());
                if (optNao.isSelected()) {
                    pedido.setDataliberacao(this.utilDatas.converterDataddmmyyyy("31/12/1900"));
                }

                pedido.setUsuario(usuario.getId());

                if (!pedidoDAO.liberarPedido(pedido)) {

                } else {
                    if (optSim.isSelected()) {
                        gerarProcessoERP();
                    }
                    if (optRea.isSelected()) {
                        gerarProcessoERP();
                    }

                    atualizar();
                }
            }
        }
    }

    private void agendarData(String situacao) throws SQLException, ParseException, Exception {
        if (pedido != null) {
            if (pedido.getPedido() > 0) {
                getUsuarioLogado();
                if (txtDataAgenda != null) {
                    this.pedido.setDatagendada(txtDataAgenda.getDate());
                }

                if (!pedidoDAO.AgendarDataPedidoNew(pedido)) {

                } else {
                    atualizar();
                }
            }
        }
    }

    private void gerarProcessoERP() throws Exception {
        WSEmailAtendimento wsEma = new WSEmailAtendimento();
        wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
    }

    private void atualizar() {
        if (veioCampo != null) {
            try {
                veioCampo.retornarPedido(this.selecao);
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }

    }
    private Usuario usuario;

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        UsuarioERPDAO dao = new UsuarioERPDAO();

        usuario = dao.getUsuario(Menu.username.toLowerCase());
        return usuario;
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
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtInfo = new org.openswing.swing.client.TextAreaControl();
        btnConfirmar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtPedido = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        txtCliente = new org.openswing.swing.client.TextControl();
        txtNome = new org.openswing.swing.client.TextControl();
        jLabel3 = new javax.swing.JLabel();
        txtDataLiberacao = new org.openswing.swing.client.DateControl();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtInfoLib = new org.openswing.swing.client.TextControl();
        jPanel1 = new javax.swing.JPanel();
        optSim = new javax.swing.JRadioButton();
        optNao = new javax.swing.JRadioButton();
        optFin = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        optRea = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtDataAgenda = new org.openswing.swing.client.DateControl();
        btnDataAgendada = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pedido");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jLabel12.setText("Pedido(s)");

        jLabel13.setText("Info Bloqueio:");

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

        txtPedido.setEnabled(false);
        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel1.setText("Cliente");

        txtCliente.setEnabled(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtNome.setEnabled(false);
        txtNome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel3.setText("Nome");

        txtDataLiberacao.setEnabled(false);
        txtDataLiberacao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel4.setText("Liberação");

        jLabel5.setText("Info Liberação:");

        txtInfoLib.setEnabled(false);
        txtInfoLib.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Pedido Liberado ?")));

        buttonGroup1.add(optSim);
        optSim.setText("Sim");
        optSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optSimActionPerformed(evt);
            }
        });

        buttonGroup1.add(optNao);
        optNao.setText("Não");
        optNao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optNaoActionPerformed(evt);
            }
        });

        buttonGroup1.add(optFin);
        optFin.setText("Finalizado");
        optFin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optFinActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(optSim)
                .addGap(18, 18, 18)
                .addComponent(optNao)
                .addGap(18, 18, 18)
                .addComponent(optFin)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optSim)
                    .addComponent(optNao)
                    .addComponent(optFin))
                .addGap(4, 4, 4))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Ação")));

        buttonGroup1.add(optRea);
        optRea.setText("Reabilitar");
        optRea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optReaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(optRea)
                .addGap(4, 4, 4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optRea)
                .addGap(4, 4, 4))
        );

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bug_link.png"))); // NOI18N
        jButton1.setText("Forçar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Agendar Data")));

        txtDataAgenda.setEnabled(false);
        txtDataAgenda.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnDataAgendada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        btnDataAgendada.setEnabled(false);
        btnDataAgendada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataAgendadaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(txtDataAgenda, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDataAgendada)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDataAgenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDataAgendada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnConfirmar)
                                .addGap(0, 0, 0)
                                .addComponent(btnCancelar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1))))
                    .addComponent(txtInfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPedido, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(txtDataLiberacao, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(txtInfoLib, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGap(8, 8, 8)
                .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataLiberacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInfoLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCancelar, btnConfirmar, jButton1});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel1, jPanel2, jPanel3});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        String situacao = "";
        String mensagem = "";
        if (optSim.isSelected()) {
            situacao = "S";
            mensagem = "Liberar o pedido?";
        } else {
            if (optNao.isSelected()) {
                mensagem = "Não Liberar o pedido?";
                situacao = "N";
            } else if (optFin.isSelected()) {
                situacao = "F";
            } else if (optRea.isSelected()) {
                situacao = "R";
                mensagem = "Reabilitar o pedido?";

            }
        }

        if (situacao.isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Selecione uma opção de gravação");
        } else {

            if (ManipularRegistros.pesos(mensagem)) {
                try {
                    liberarPedido(situacao);
                } catch (SQLException ex) {
                    Logger.getLogger(LogPedidoLiberacao.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(LogPedidoLiberacao.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(LogPedidoLiberacao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void optSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optSimActionPerformed
        txtInfoLib.setText("PEDIDO LIBERADO");
    }//GEN-LAST:event_optSimActionPerformed

    private void optNaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optNaoActionPerformed
        txtInfoLib.setText("PEDIDO NÃO LIBERADO");
    }//GEN-LAST:event_optNaoActionPerformed

    private void optFinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optFinActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_optFinActionPerformed

    private void optReaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optReaActionPerformed
        txtInfoLib.setText("PEDIDO REABILITADO");
    }//GEN-LAST:event_optReaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String situacao = "";
        String mensagem = "Forçar liberação";

        situacao = "S";
        if (situacao.isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Selecione uma opção de gravação");
        } else {

            if (ManipularRegistros.pesos(mensagem)) {
                try {
                    liberarPedido(situacao);
                    gerarProcessoERP();
                } catch (SQLException ex) {
                    Logger.getLogger(LogPedidoLiberacao.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(LogPedidoLiberacao.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(LogPedidoLiberacao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnDataAgendadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDataAgendadaActionPerformed
        try {
            agendarData("");
        } catch (ParseException ex) {
            Logger.getLogger(LogPedidoLiberacao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LogPedidoLiberacao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnDataAgendadaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnDataAgendada;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton optFin;
    private javax.swing.JRadioButton optNao;
    private javax.swing.JRadioButton optRea;
    private javax.swing.JRadioButton optSim;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.DateControl txtDataAgenda;
    private org.openswing.swing.client.DateControl txtDataLiberacao;
    private org.openswing.swing.client.TextAreaControl txtInfo;
    private org.openswing.swing.client.TextControl txtInfoLib;
    private org.openswing.swing.client.TextControl txtNome;
    private org.openswing.swing.client.TextControl txtPedido;
    // End of variables declaration//GEN-END:variables
}
