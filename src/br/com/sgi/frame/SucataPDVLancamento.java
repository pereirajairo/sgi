/*
 * To change this template, choose Tools | txtPeso
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.bean.SucataPDV;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.SucataEcoDao;
import br.com.sgi.dao.SucataPDVDao;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.integracao.Balancinhas;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ConversaoHoras;
import static br.com.sgi.util.FormatarPeso.limpaValorEmbalagem;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSEmailAtendimento;
import br.com.sgi.ws.WsEstoque;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.openswing.swing.mdi.client.InternalFrame;

/**
 *
 * @author jairosilva s
 */
public final class SucataPDVLancamento extends InternalFrame {

    private UtilDatas utilDatas;
    private SucataConsultaPDV veioCampo;

    private SucataPDV sucataPDV;
    private List<Filial> listFilial = new ArrayList<Filial>();
    private List<SucataPDV> listSucataPDV = new ArrayList<SucataPDV>();

    private FilialDAO filialDAO;
    private SucataPDVDao sucataPDVDao;
    private SucataEcoDao sucataEcoDao;
    private Usuario usuario;
    private SucataEcoParametros sucataEcoParametros;
    private String retornoWs;

    public SucataPDVLancamento() throws FontFormatException, IOException {
        try {
            initComponents();
            File font_file = new File("DigitaldreamSkewNarrow.TTF");
            Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);
            Font sizedFont = font.deriveFont(48f);
            txtPeso.setFont(sizedFont);
            //txtPeso.setText("105.00 KG");

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (filialDAO == null) {
                filialDAO = new FilialDAO();
            }
            if (sucataPDVDao == null) {
                sucataPDVDao = new SucataPDVDao();
            }

            if (sucataEcoDao == null) {
                sucataEcoDao = new SucataEcoDao();
            }
            if (sucataEcoParametros == null) {
                sucataEcoParametros = new SucataEcoParametros();
            }

            Balancinhas.ClosePort();
            Balancinhas.main(null);
            txtPrecoSucata.setValue(0);
            txtTotalSucata.setValue(0);
            txtPesoProduto.setValue(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public static void getPesoBalanca(String peso) {
        try {
            txtPeso.setText(peso + " KG");
        } catch (Exception e) {
        }
    }

    public void setRecebePalavra(SucataConsultaPDV veioInput, SucataPDV sucataPDV) throws Exception {
        this.veioCampo = veioInput;
        this.sucataPDV = sucataPDV;

        if (sucataPDV != null) {
            if (sucataPDV.getNota() > 0) {
                txtEmpresa.setText(String.valueOf(sucataPDV.getEmpresa()));
                txtFilial.setText(String.valueOf(sucataPDV.getFilial()));
                txtNota.setText(String.valueOf(sucataPDV.getNota()));
                txtSerie.setText(sucataPDV.getSerie());
                if (sucataPDV.getPesoSucata() > 0) {
                    btnConfirmar.setEnabled(true);
                    btnPagar.setEnabled(false);
                    btnConsultar.setEnabled(false);
                }
            }
        }

    }

    private void enviarAtualiza() throws Exception {
        if (veioCampo != null) {
            veioCampo.RetornarCampoAtualiza();
        }
        this.dispose();
    }

    public void setRecebePalavraAtualiza(SucataConsultaPDV veioInput) throws Exception {
        this.veioCampo = veioInput;

    }

    private void gerarProcessoERP() throws Exception {
        WSEmailAtendimento wsEma = new WSEmailAtendimento();
        wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
    }

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        UsuarioERPDAO dao = new UsuarioERPDAO();

        usuario = dao.getUsuario(Menu.username.toLowerCase());
        return usuario;
    }

    private void popularRegistro() {
        try {
            getUsuarioLogado();
            int totMin = CalculoHora(0);
            if (sucataPDV == null) {
                sucataPDV = new SucataPDV();
            }
            sucataPDV.setEmpresa(Integer.valueOf(txtEmpresa.getText().trim()));
            sucataPDV.setFilial(Integer.valueOf(txtFilial.getText().trim()));
            sucataPDV.setSerie(txtSerie.getText().trim());
            sucataPDV.setNota(Integer.valueOf(txtNota.getText().trim()));
            if (!txtPeso.getText().equals("0 KG")) {
                sucataPDV.setPesoNota(limpaValorEmbalagem(txtPeso.getText()));
            }
            sucataPDV.setUsuarioLancamento(usuario.getId());
            sucataPDV.setDataLancamento(new Date());
            sucataPDV.setHoraLancamento(totMin);
            Double valorTotal;
            if (txtPrecoSucata.getDouble() != 0) {
                sucataPDV.setPrecoSucata(txtPrecoSucata.getDouble());
                txtPesoProduto.setText(String.valueOf(sucataPDV.getPesoNota()));
                sucataPDV.setPesoSucata(txtPesoProduto.getDouble());
                valorTotal = sucataPDV.getPesoNota() * txtPrecoSucata.getDouble();
                txtTotalSucata.setValue(valorTotal);
                sucataPDV.setPrecoTotalSucata(valorTotal);
            } else {
                JOptionPane.showMessageDialog(null, "É obrigatório infomar o valor a ser cobrado pela sucata! Caso não saiba clique em consultar preço",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);

            }
            if (txtTotalSucata.getDouble() == 0) {
                sucataPDV.setIndicativoSucata("S");
            } else {
                sucataPDV.setIndicativoSucata("N");
            }

        } catch (Exception e) {
        }
    }

    private void wsEstoqueMovimentoPdvSucata() throws Exception {
        sucataEcoParametros = new SucataEcoParametros();

        double peso = limpaValorEmbalagem(txtPeso.getText());
        String nota = txtNota.getText().trim();
        String empresa = txtEmpresa.getText().trim();
        String filial = txtFilial.getText().trim();
        String serie = txtSerie.getText().trim();

        sucataEcoParametros = sucataEcoDao.getSucataEcoParamentros(filial, empresa);

        retornoWs = WsEstoque.estoqueMovimentoSucataPDVSapiens(sucataEcoParametros, peso, nota, serie);

    }

    private void fecharTela() throws PropertyVetoException {
        this.closeFrame();
    }

    private void salvarRegistros() throws SQLException, Exception {

        popularRegistro();
        if (txtTotalSucata.getDouble() == 0) {
            wsEstoqueMovimentoPdvSucata();
        } else {
            if (!sucataPDVDao.inserir(sucataPDV)) {

            } else {
                fecharTela();
            }
        }
        if (retornoWs.equals("Processado com Sucesso.")) {
            if (!sucataPDVDao.inserir(sucataPDV)) {

            } else {
                fecharTela();
            }
        } else {
            JOptionPane.showMessageDialog(null, retornoWs);
        }
    }

    public static Integer CalculoHora(int totMin) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        Date data = calendar.getTime();
        SimpleDateFormat sdhora = new SimpleDateFormat("HH:mm");
        String hora = sdhora.format(data);
        ConversaoHoras coversaoHoras = new ConversaoHoras();
        totMin = coversaoHoras.ConverterHoras(hora);
        return totMin;
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
        btnConfirmar = new javax.swing.JButton();
        txtEmpresa = new org.openswing.swing.client.TextControl();
        txtFilial = new org.openswing.swing.client.TextControl();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtSerie = new org.openswing.swing.client.TextControl();
        jLabel16 = new javax.swing.JLabel();
        txtNota = new org.openswing.swing.client.TextControl();
        txtPeso = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        btnFinalizar = new javax.swing.JButton();
        btnPagar = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtPrecoSucata = new org.openswing.swing.client.NumericControl();
        txtPesoProduto = new org.openswing.swing.client.NumericControl();
        txtTotalSucata = new org.openswing.swing.client.NumericControl();
        btnConsultar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lançamento Sucata PDV");
        setToolTipText("");
        setMinimumSize(new java.awt.Dimension(700, 60));
        setNormalBounds(new java.awt.Rectangle(0, 0, 200, 0));
        setPreferredSize(new java.awt.Dimension(900, 500));
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jLabel12.setText("Empresa");

        btnConfirmar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnConfirmar.setText("Pesar Sucata");
        btnConfirmar.setEnabled(false);
        btnConfirmar.setFocusCycleRoot(true);
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        txtEmpresa.setEnabled(false);
        txtEmpresa.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtFilial.setEnabled(false);
        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel14.setText("Filial");

        jLabel15.setText("Serie");

        txtSerie.setEnabled(false);
        txtSerie.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel16.setText("Nota");

        txtNota.setEnabled(false);
        txtNota.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotaActionPerformed(evt);
            }
        });

        txtPeso.setBackground(new java.awt.Color(204, 204, 204));
        txtPeso.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPeso.setForeground(new java.awt.Color(255, 0, 51));
        txtPeso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPeso.setText("0 KG");
        txtPeso.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        txtPeso.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        txtPeso.setOpaque(true);

        jLabel17.setText("Peso:");

        btnFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnFinalizar.setText("Sair");
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });

        btnPagar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnPagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnPagar.setText("Pagar Sucata");
        btnPagar.setEnabled(false);
        btnPagar.setFocusCycleRoot(true);
        btnPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarActionPerformed(evt);
            }
        });

        jLabel18.setText("Preço Sucata");

        jLabel19.setText("Peso do Produto");

        jLabel20.setText("Valor Total");

        txtPrecoSucata.setDecimals(2);
        txtPrecoSucata.setEnabled(false);
        txtPrecoSucata.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtPesoProduto.setDecimals(2);
        txtPesoProduto.setEnabled(false);
        txtPesoProduto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtTotalSucata.setDecimals(2);
        txtTotalSucata.setEnabled(false);
        txtTotalSucata.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnConsultar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnConsultar.setText("Consulta Preço");
        btnConsultar.setFocusCycleRoot(true);
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnConfirmar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPagar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConsultar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFinalizar, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                    .addComponent(txtPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel15)
                            .addComponent(jLabel14)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel12)
                                .addComponent(txtSerie, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                                .addComponent(txtNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFilial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtPrecoSucata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtPesoProduto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtTotalSucata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel18)))
                    .addComponent(txtEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnConfirmar, btnConsultar, btnPagar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGap(8, 8, 8)
                .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel18))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtPrecoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPesoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20)
                        .addGap(5, 5, 5)
                        .addComponent(txtTotalSucata, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addGap(8, 8, 8)
                        .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFinalizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConsultar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPagar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfirmar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnConfirmar, btnConsultar, btnFinalizar, btnPagar});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        try {
            salvarRegistros();
            enviarAtualiza();
            this.closeFrame();
        } catch (SQLException ex) {
            Logger.getLogger(SucataPDVLancamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SucataPDVLancamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SucataPDVLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        try {
            enviarAtualiza();
        } catch (Exception ex) {
            Logger.getLogger(SucataPDVLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formInternalFrameClosing

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        try {
            enviarAtualiza();
            this.closeFrame();
        } catch (Exception ex) {
            Logger.getLogger(SucataPDVLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        try {
            salvarRegistros();
        } catch (SQLException ex) {
            Logger.getLogger(SucataPDVLancamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SucataPDVLancamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SucataPDVLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPagarActionPerformed

    private void txtNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNotaActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            consultarpreco();
        } catch (SQLException ex) {
            Logger.getLogger(SucataPDVLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConsultarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JButton btnPagar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.TextControl txtFilial;
    private org.openswing.swing.client.TextControl txtNota;
    private static javax.swing.JLabel txtPeso;
    private org.openswing.swing.client.NumericControl txtPesoProduto;
    private org.openswing.swing.client.NumericControl txtPrecoSucata;
    private org.openswing.swing.client.TextControl txtSerie;
    private org.openswing.swing.client.NumericControl txtTotalSucata;
    // End of variables declaration//GEN-END:variables

    private void consultarpreco() throws SQLException {
        Filial filial = new Filial();
        FilialDAO dao = new FilialDAO();
        filial = dao.getFilia(" filial ", " and codemp = 1 and  codfil = " + txtFilial.getText().trim());
        if (filial != null) {
            if (filial.getFilial() > 0) {
                txtPrecoSucata.setValue(filial.getPrecoSucata());
                 btnPagar.setEnabled(true);
            }
        }
    }

}
