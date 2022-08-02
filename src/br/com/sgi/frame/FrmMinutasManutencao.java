/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.MinutaPedidoDAO;
import br.com.sgi.dao.PedidoDAO;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
public final class FrmMinutasManutencao extends InternalFrame {

    private UtilDatas utilDatas;
    private frmMinutasExpedicaoGerar veioCampo;
    private MinutaPedido minutaPedido;
    private Minuta minuta;

    private MinutaPedidoDAO minutaPedidoDAO;
    private PedidoDAO pedidoDAO;
    private Pedido pedido;

    private boolean addReg;

    public FrmMinutasManutencao() {
        try {
            initComponents();

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (minutaPedidoDAO == null) {
                this.minutaPedidoDAO = new MinutaPedidoDAO();
            }
            if (pedidoDAO == null) {
                pedidoDAO = new PedidoDAO();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void pegarPedido() throws SQLException {
        if (minuta != null) {
            if (minuta.getUsu_codlan() > 0) {
                this.pedido = new Pedido();
                boolean retornoerro = true;

                String clienteSelecionado="";
                String cliente = minuta.getUsu_nommin();
                if (!cliente.startsWith("CLIENTE ")) {
                    int index = cliente.indexOf("-");
                    clienteSelecionado = cliente.substring(0, index);
                }else{
                    int index = cliente.indexOf("-");
                    clienteSelecionado = cliente.substring(7, index);
                }

                this.pedido = pedidoDAO.getPedidoExpedicao("ped", " \nand E120PED.usu_libmin not in ('S') "
                        + " and E120PED.numped = " + txtPedido.getText() + " "
                        + " and e120ped.codcli = " + clienteSelecionado + "\n");
                //   this.pedido = pedidoDAO.getPedidoExpedicao("ped", "  and E120PED.numped = '" + txtPedido.getText() + "' and e120ped.codcli = " + clienteSelecionado);

                if (this.pedido != null) {
                    if (this.pedido.getPedido() > 0) {
                        btnConfirmar.setEnabled(true);
                        retornoerro = false;
                        txtCliente.setText(pedido.getCadCliente().getCodigo() + " - " + pedido.getCadCliente().getNome());
                        txtInfo.setText(" ADICIONADO O PEDIDO " + pedido.getPedido() + " NA MINUTA " + txtMinuta.getText());
                        txtPre.requestFocus();
                    }
                }

                if (retornoerro) {
                    Mensagem.mensagem("ERROR", "Pedido não encontrado");
                }
            }
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void setRecebePalavra(frmMinutasExpedicaoGerar veioInput, MinutaPedido minutaPedido) throws Exception {
        this.veioCampo = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Manutenção de dados da minuta  " + minutaPedido.getUsu_codlan()));
        this.minuta = new Minuta();
        this.minuta = minutaPedido.getCadMinuta();
        if (minutaPedido.getUsu_seqite() > 0) {
            addReg = false;
            this.minutaPedido = minutaPedido;
            btnPesquisarPedido.setEnabled(false);
            btnConfirmar.setEnabled(true);
            txtSequencia.setText(this.minutaPedido.getUsu_seqite().toString());
            txtInfo.setText(minutaPedido.getUsu_obsmin());
            txtPre.setText(minutaPedido.getUsu_numpfa().toString());
            txtAnalise.setText(minutaPedido.getUsu_numana().toString());
            txtPedido.setEnabled(false);
            txtPedido.setText(minutaPedido.getUsu_numped().toString());
            txtMinuta.setText(minutaPedido.getCadMinuta().getUsu_codlan().toString());
            txtClienteMinuta.setText(minuta.getUsu_nommin());
            txtPre.requestFocus();
        } else {
            addReg = true;
            txtClienteMinuta.setText(minuta.getUsu_nommin());
            txtSequencia.setText("0");
            txtMinuta.setText(minutaPedido.getCadMinuta().getUsu_codlan().toString());
            btnPesquisarPedido.setEnabled(true);
            txtPedido.setEnabled(true);
            txtPedido.requestFocus();
        }

    }

    private void salvar() throws SQLException, Exception {
        if (addReg) {
            if (ManipularRegistros.gravarRegistros("Incluir")) {

                minutaPedido = new MinutaPedido();
                minutaPedido.setCadMinuta(minuta);
                minutaPedido.setUsu_numnfv(0);

                minutaPedido.setUsu_tnspro(pedido.getTransacao());
                minutaPedido.setUsu_codcli(pedido.getCliente());
                Cliente cli = new Cliente();
                cli.setCodigo(minutaPedido.getUsu_codcli());
                cli.setNome(pedido.getCadCliente().getNome());
                cli.setEstado(pedido.getCadCliente().getEstado());
                cli.setCidade(pedido.getCadCliente().getCidade());
                minutaPedido.setCadCliente(cli);

                minutaPedido.setUsu_pesped(pedido.getPeso());
                minutaPedido.setUsu_qtdped(pedido.getQuantidade());

                minutaPedido.setUsu_codemp(1);
                minutaPedido.setUsu_codtpr("");
                minutaPedido.setUsu_pesnfv(minutaPedido.getUsu_pesped());
                minutaPedido.setUsu_numped(pedido.getPedido());
                minutaPedido.setUsu_obsmin(txtMinuta.getText());
                minutaPedido.setUsu_pessuc(minutaPedido.getUsu_pesped());
                minutaPedido.setUsu_qtdfat(minutaPedido.getUsu_qtdped());
                minutaPedido.setUsu_qtdvol(0.0);
                minutaPedido.setUsu_sitmin("LIBERADA");
                minutaPedido.setUsu_codfil(pedido.getFilial());

                minutaPedido.setUsu_codori("");
                minutaPedido.setUsu_codsnf("");
                minutaPedido.setUsu_lansuc(0);
                minutaPedido.setUsu_codpes(0);
                minutaPedido.setUsu_pesbal(0.0);
                minutaPedido.setUsu_pesrec(0.0);

                minutaPedido.setUsu_datemi(new Date());
                minutaPedido.setUsu_datlib(new Date());

                minutaPedido.setUsu_numpfa(Integer.valueOf(txtPre.getText()));
                minutaPedido.setUsu_numana(Integer.valueOf(txtAnalise.getText()));

                minutaPedido.setUsu_codlan(this.minuta.getUsu_codlan());
                minutaPedido.setUsu_seqite(minutaPedidoDAO.proxCodCad());

                if (!minutaPedidoDAO.inserir(minutaPedido, 1, 1)) {

                } else {
                    pedido = new Pedido();
                    pedido.setEmpresa(minutaPedido.getUsu_codemp());
                    pedido.setFilial(minutaPedido.getUsu_codfil());
                    pedido.setPedido(minutaPedido.getUsu_numped());
                    pedido.setCliente(minutaPedido.getUsu_codcli());
                    pedido.setCodigominuta(minutaPedido.getUsu_codlan());

                    pedido.setLiberarMinuta("S");

                    if (!pedidoDAO.AtualizarMinuta(pedido)) {

                    } else {
                        if (veioCampo != null) {
                            veioCampo.retornar();
                            this.dispose();
                        }
                    }

                }
            }
        } else {
            if (ManipularRegistros.gravarRegistros("Alterar")) {
                try {
                    this.minutaPedido.setUsu_numpfa(Integer.valueOf(txtPre.getText()));
                    this.minutaPedido.setUsu_numana(Integer.valueOf(txtAnalise.getText()));
                    this.minutaPedido.setUsu_obsmin("Alteracao de pré fatura de:  " + minutaPedido.getUsu_numpfa() + " Analise " + minutaPedido.getUsu_numpfa() + "\n"
                            + "para  pré fatura " + txtPre.getText() + " Analise " + txtAnalise.getText());

                    if (!this.minutaPedidoDAO.alterar(minutaPedido)) {

                    } else {
                        if (veioCampo != null) {
                            veioCampo.retornar();
                            this.dispose();
                        }
                    }
                } catch (Exception e) {
                    Mensagem.mensagem("ERROR", e.getMessage());
                }

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

        btnConfirmar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        txtSequencia = new org.openswing.swing.client.TextControl();
        jLabel4 = new javax.swing.JLabel();
        txtPre = new org.openswing.swing.client.TextControl();
        txtAnalise = new org.openswing.swing.client.TextControl();
        jLabel5 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        btnPesquisarPedido = new javax.swing.JButton();
        txtMinuta = new org.openswing.swing.client.TextControl();
        jLabel6 = new javax.swing.JLabel();
        txtCliente = new org.openswing.swing.client.TextControl();
        txtClienteMinuta = new org.openswing.swing.client.TextControl();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

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

        jLabel1.setText("Pre-fatura");

        jLabel2.setText("Analise");

        txtInfo.setColumns(20);
        txtInfo.setRows(5);
        txtInfo.setEnabled(false);
        jScrollPane1.setViewportView(txtInfo);

        jLabel3.setText("Info");

        txtSequencia.setEnabled(false);
        txtSequencia.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel4.setText("Sequencia");

        txtPre.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPreActionPerformed(evt);
            }
        });

        txtAnalise.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtAnalise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAnaliseActionPerformed(evt);
            }
        });

        jLabel5.setText("Pedido");

        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoActionPerformed(evt);
            }
        });

        btnPesquisarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesquisarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarPedidoActionPerformed(evt);
            }
        });

        txtMinuta.setEnabled(false);
        txtMinuta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel6.setText("Minuta");

        txtCliente.setEnabled(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        txtClienteMinuta.setEnabled(false);
        txtClienteMinuta.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnConfirmar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar))
                    .addComponent(txtSequencia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAnalise, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addContainerGap(844, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesquisarPedido)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtClienteMinuta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClienteMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(2, 2, 2)
                .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarPedido)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAnalise, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCancelar, btnConfirmar});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnPesquisarPedido, txtPedido});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        try {
            salvar();
        } catch (SQLException ex) {
            Logger.getLogger(FrmMinutasManutencao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmMinutasManutencao.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        if (this.veioCampo != null) {
            try {
                this.veioCampo.retornar();
                this.dispose();
            } catch (Exception ex) {
                Logger.getLogger(FrmMinutasManutencao.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtPreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPreActionPerformed
        txtInfo.setText("Alteracao de pré fatura de:  " + minutaPedido.getUsu_numpfa() + " Analise " + minutaPedido.getUsu_numana() + "\n"
                + "para  pré fatura " + txtPre.getText() + " Analise " + txtAnalise.getText());
        txtAnalise.requestFocus();
    }//GEN-LAST:event_txtPreActionPerformed

    private void txtAnaliseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAnaliseActionPerformed
        txtInfo.setText("Alteracao de pré fatura de:  " + minutaPedido.getUsu_numpfa() + " Analise " + minutaPedido.getUsu_numana() + "\n"
                + "para  pré fatura " + txtPre.getText() + " Analise " + txtAnalise.getText());
        btnConfirmar.requestFocus();

    }//GEN-LAST:event_txtAnaliseActionPerformed

    private void txtPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoActionPerformed
        try {
            pegarPedido();
        } catch (SQLException ex) {
            Logger.getLogger(FrmMinutasManutencao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtPedidoActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnPesquisarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarPedidoActionPerformed
        try {
            pegarPedido();
        } catch (SQLException ex) {
            Logger.getLogger(FrmMinutasManutencao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPesquisarPedidoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnPesquisarPedido;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private org.openswing.swing.client.TextControl txtAnalise;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.TextControl txtClienteMinuta;
    private javax.swing.JTextArea txtInfo;
    private org.openswing.swing.client.TextControl txtMinuta;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.TextControl txtPre;
    private org.openswing.swing.client.TextControl txtSequencia;
    // End of variables declaration//GEN-END:variables
}
