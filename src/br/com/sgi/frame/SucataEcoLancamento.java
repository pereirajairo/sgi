/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Filial;
import br.com.sgi.bean.NotaEntrada;
import br.com.sgi.bean.NotaEntradaItem;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.SucataEco;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.SucataEcoDao;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.integracao.Balancinhas;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ConversaoHoras;
import static br.com.sgi.util.FormatarPeso.limpaValorEmbalagem;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSEmailAtendimento;
import br.com.sgi.ws.WsNotaEntrada;
import br.com.sgi.ws.WsOrdemDeCompra;
import static br.com.sgi.ws.WsOrdemDeCompra.ordemDeCompraCancelaSaldoSucataEcoSapiens;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
public final class SucataEcoLancamento extends InternalFrame {

    private UtilDatas utilDatas;
    private SucataConsultaEco veioCampo;

    private SucataEco SucataEco;
    private SucataEcoParametros sucataEcoParametros;
    private NotaEntrada notaEntrada;

    private NotaEntradaItem notaEntradaItem;
    private List<Filial> listFilial = new ArrayList<Filial>();
    private List<SucataEco> listSucataEco = new ArrayList<SucataEco>();
    private List<SucataEcoLancamento> listSucataEcoParametro = new ArrayList<SucataEcoLancamento>();

    private FilialDAO filialDAO;
    private SucataEcoDao sucataEcoDao;
    private WsNotaEntrada wsNotaEntrada;
    private WsOrdemDeCompra wsOrdemDeCompra;
    private String botaoPesagem;
    private String forn = " ";
    private Usuario usuario;

    public SucataEcoLancamento() throws FontFormatException, IOException {

        try {
            initComponents();

            // File font_file = new File("DS-DIGIB.TTF");
            File font_file = new File("DigitaldreamSkewNarrow.TTF");

            Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);
            Font sizedFont = font.deriveFont(48f);
            txtPeso.setFont(sizedFont);
            // txtPeso.setText("105.00 KG");
            this.setSize(400, 1000);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (filialDAO == null) {
                filialDAO = new FilialDAO();
            }
            if (sucataEcoDao == null) {
                sucataEcoDao = new SucataEcoDao();
            }

            if (wsNotaEntrada == null) {
                wsNotaEntrada = new WsNotaEntrada();
            }
            Balancinhas.ClosePort();
            Balancinhas.main(null);

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

    public void setRecebePalavra(SucataConsultaEco veioInput, SucataEco SucataEco, String botaoPesagem) throws Exception {
        this.veioCampo = veioInput;
        this.SucataEco = SucataEco;
        this.botaoPesagem = botaoPesagem;
        if (this.botaoPesagem.equals("sucataEcoNota")) {
            if (SucataEco != null) {
                if (SucataEco.getNota() > 0) {
                    jLabel13.setEnabled(false);
                    txtCliente.setEnabled(false);
                    jLabel13.setVisible(false);
                    txtCliente.setVisible(false);
                    txtEmpresa.setText(String.valueOf(SucataEco.getEmpresa()));
                    txtFilial.setText(String.valueOf(SucataEco.getFilial()));
                    txtSerie.setText(SucataEco.getSerie());
                    txtNota.setText(String.valueOf(SucataEco.getNota()));
                    txtOc.setText(String.valueOf(SucataEco.getNumeroOC()));

                    btnConfirmar.setEnabled(true);
                }
            }
        } else if (botaoPesagem.equals("avulso")) {
            //gerar OC

            btnConfirmar.setEnabled(false);
        }

    }

    private void enviarAtualiza() throws Exception {
        if (veioCampo != null) {
            veioCampo.RetornarCampoAtualiza();
        }
        this.dispose();
    }

    public void setRecebePalavraAtualiza(SucataConsultaEco veioInput) throws Exception {
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

    private void buscaParamentrosSucata() throws SQLException {
        sucataEcoParametros = new SucataEcoParametros();

        if (botaoPesagem.equals("avulso")) {
            forn = String.valueOf(txtCliente.getValue());
        } else {
            forn = SucataEco.getCliente().toString();
        }
        String filial = txtFilial.getText().trim();
        String Oc = txtOc.getText().trim();

        notaEntradaItem = new NotaEntradaItem();

        sucataEcoParametros = sucataEcoDao.getSucataEcoParamentrosOC(filial, Oc, forn);
        if (sucataEcoParametros != null) {
            if (sucataEcoParametros.getEmpresa() != null) {
                notaEntradaItem.setEmpresa(sucataEcoParametros.getEmpresa());
                notaEntradaItem.setFilial(Integer.valueOf(txtFilial.getText().trim()));
                notaEntradaItem.setSerie(txtSerie.getText().trim());
                notaEntradaItem.setProduto(sucataEcoParametros.getProduto());
                notaEntradaItem.setValorSucata(sucataEcoParametros.getValorSucata());
                notaEntradaItem.setNota(Integer.valueOf(txtNota.getText().trim()));
                notaEntradaItem.setTrancacao(sucataEcoParametros.getTransacao()); // Passar a Transacao
                notaEntradaItem.setFilial_oc(sucataEcoParametros.getFilial());
                notaEntradaItem.setOrdem_compra(sucataEcoParametros.getOrdemCompras());
                notaEntradaItem.setQuantidade_recebida(limpaValorEmbalagem(txtPeso.getText()));
                notaEntradaItem.setDeposito(sucataEcoParametros.getDeposito());
                notaEntradaItem.setCentroCusto(sucataEcoParametros.getCentroCustos());
                notaEntradaItem.setSeqIpo(sucataEcoParametros.getSeqIpo());
                notaEntradaItem.setContaFinanceira(sucataEcoParametros.getContaFinanceira());
            }
        }

    }

    private void popularRegistro() {
        try {
            getUsuarioLogado();

            int totMin = CalculoHora(0);
            buscaParamentrosSucata();

            double valorNota = limpaValorEmbalagem(txtPeso.getText()) * sucataEcoParametros.getValorSucata();

            notaEntrada = new NotaEntrada();

            notaEntrada.setEmpresa(Integer.valueOf(txtEmpresa.getText().trim()));
            notaEntrada.setFilial(Integer.valueOf(txtFilial.getText().trim()));
            if ((Integer.valueOf(txtFilial.getText().trim()) >= 10) && (Integer.valueOf(txtFilial.getText().trim()) <= 14)) {
                notaEntrada.setSerie("SUC");
            } else {
                notaEntrada.setSerie(txtSerie.getText().trim());
            }
            notaEntrada.setNota(Integer.valueOf(txtNota.getText().trim()));
            notaEntrada.setFornecedor(Integer.valueOf(forn));
            notaEntrada.setTrancacao(sucataEcoParametros.getTransacao());
            notaEntrada.setValorNota(valorNota);

        } catch (Exception e) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void salvarRegistros() throws SQLException, Exception {

        popularRegistro();

        String retorno = wsNotaEntrada.NotaEntradaSucataEcoSapiens(notaEntrada, notaEntradaItem);
        int indexNotaExiste = retorno.indexOf("Nota fiscal já existe, não pode ser incluída.");
        int indexErroImport = retorno.indexOf("Não foi possível importar a Nota Fiscal");

        if (retorno.equals("OK")) {
            ordemDeCompraCancelaSaldoSucataEcoSapiens(notaEntrada, notaEntradaItem);
            enviarAtualiza();
            JOptionPane.showMessageDialog(null, "Sucata Eco entrada com sucesso");
        } else if ((indexNotaExiste > 0) || (indexErroImport > 0)) {
            JOptionPane.showMessageDialog(null, retorno);
        } else {
            String retornoExclucao = wsNotaEntrada.NotaEntradaExcluir(notaEntrada);
            if (retornoExclucao.equals("Processado com sucesso.") || retornoExclucao.equals("A nota fiscal de entrada informada não existe.")) {
                JOptionPane.showMessageDialog(null, retorno);
            }
            //JOptionPane.showMessageDialog(null, "Favor verificar a nota de entrada da sucata eco Empresa 1 \n"
            //       + " Filial: " + Integer.valueOf(txtFilial.getText().trim()) + " Serie: "
            //       + "SUC Nota: " + Integer.valueOf(txtNota.getText().trim()) + " Fornecedor " + Integer.valueOf(SucataEco.getCliente()));
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
        jLabel18 = new javax.swing.JLabel();
        txtOc = new org.openswing.swing.client.TextControl();
        jLabel13 = new javax.swing.JLabel();
        txtCliente = new org.openswing.swing.client.NumericControl();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lançamento Sucata PDV");
        setToolTipText("");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));
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
        btnConfirmar.setText("Confirmar");
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

        txtPeso.setBackground(new java.awt.Color(204, 204, 204));
        txtPeso.setFont(new java.awt.Font("Calibri Light", 1, 24)); // NOI18N
        txtPeso.setForeground(new java.awt.Color(255, 0, 0));
        txtPeso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPeso.setText("0 KG");
        txtPeso.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        txtPeso.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        txtPeso.setOpaque(true);

        jLabel17.setText("Peso:");

        btnFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnFinalizar.setText("Sair");
        btnFinalizar.setFocusable(false);
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });

        jLabel18.setText("Ordem de Compra");

        txtOc.setEnabled(false);
        txtOc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel13.setText("Cliente");

        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtOc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtSerie, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
            .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel16))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(txtNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnConfirmar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtOc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        try {
            salvarRegistros();
            enviarAtualiza();
            this.closeFrame();
        } catch (SQLException ex) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        try {
            enviarAtualiza();
        } catch (Exception ex) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formInternalFrameClosing

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        try {
            enviarAtualiza();
        } catch (Exception ex) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:

        String empresa = String.valueOf(SucataEco.getEmpresa());
        String filial = String.valueOf(SucataEco.getFilial());
        String fornecedor = String.valueOf(txtCliente.getValue());
        Double peso = limpaValorEmbalagem(txtPeso.getText());

        String re = " ";
        String sNumOcp = " ";
        String resultado = " ";
        String erro = " ";

        try {
            sucataEcoParametros = sucataEcoDao.getSucataEcoParamentros(filial, empresa);

            re = wsOrdemDeCompra.ordemDeCompraSucataEcoSapiens(sucataEcoParametros, fornecedor, peso);
            int intretorno = re.indexOf("<mensagemRetorno>");
            int intFinalRetorno = re.indexOf("</mensagemRetorno>");
            resultado = re.substring(intretorno + 17, intFinalRetorno);
            int retornoNumOcp = re.indexOf("<numOcp>");
            int retornoNumOcpFim = re.indexOf("</numOcp>");
            int retornoErro = re.indexOf("<retorno>");
            int retornoErroFim = re.indexOf("</retorno>");
            sNumOcp = re.substring(retornoNumOcp + 8, retornoNumOcpFim);
            erro = re.substring(retornoErro + 9, retornoErroFim);
        } catch (SQLException ex) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (resultado.equals("Processado com sucesso.")) {
            txtEmpresa.setText(String.valueOf(sucataEcoParametros.getEmpresa()));
            txtFilial.setText(String.valueOf(sucataEcoParametros.getFilial()));
            txtSerie.setText("SUC");
            txtNota.setText(sNumOcp.trim());
            txtOc.setText(sNumOcp.trim());
            //Gera OC
            btnConfirmar.setEnabled(true);
        } else if (resultado.equals("Ocorreram erros.")) {
            JOptionPane.showMessageDialog(null, erro);
        }
    }//GEN-LAST:event_txtClienteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private org.openswing.swing.client.NumericControl txtCliente;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.TextControl txtFilial;
    private org.openswing.swing.client.TextControl txtNota;
    private org.openswing.swing.client.TextControl txtOc;
    private static javax.swing.JLabel txtPeso;
    private org.openswing.swing.client.TextControl txtSerie;
    // End of variables declaration//GEN-END:variables

}
