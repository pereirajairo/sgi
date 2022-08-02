/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Atendimento;
import br.com.sgi.bean.AtendimentoLigacao;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.AtendimentoLigacaoDAO;
import static br.com.sgi.frame.CRMClientesAtendimento.CalculoHora;
import br.com.sgi.util.ManipularRegistros;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.text.ParseException;
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
public final class CRMLigacao extends InternalFrame {

    private AtendimentoLigacao atendimentoLigacao;
    private AtendimentoLigacaoDAO atendimentoLigacaoDAO;
    private CRMClientesAtendimento veioCampo;

    private Atendimento atendimento;
    private Usuario usuario;

    private boolean ligacaoCliente;

    private CRMContaManutencao veioCampoConta;

    private String tipoconta;

    public CRMLigacao() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource(" Registro de ligações"));
            this.setSize(800, 500);
            atendimentoLigacaoDAO = new AtendimentoLigacaoDAO();
            txtLigacao.setLineWrap(true);
            txtLigacao.setWrapStyleWord(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void popularCampoLigacao() throws ParseException {
        atendimentoLigacao.setCodigocliente(Integer.valueOf(txtCodigo.getText()));
        atendimentoLigacao.setDataligacao(new Date());
        atendimentoLigacao.setDataconversao(new Date());
        atendimentoLigacao.setDescricaoligacao(txtLigacao.getText());
        atendimentoLigacao.setUsuario(this.usuario.getId());
        atendimentoLigacao.setTipoconta(this.tipoconta);
        atendimentoLigacao.setPedido(0);
        atendimentoLigacao.setConvertido("N");
        atendimentoLigacao.setHoraligacao(CalculoHora(0));
    }

    private boolean gravarligacao;

    private void salvarLigacao() throws SQLException, ParseException {

        if (!txtCodigo.getText().equals("0")) {
            if (!txtSequenciaLancamento.getText().equals("0")) {
                try {
                    if (gravarligacao) {
                        atendimentoLigacao = new AtendimentoLigacao();
                        atendimentoLigacao.setCodigoatendimento(Integer.valueOf(txtSequenciaLancamento.getText()));
                        atendimentoLigacao.setCodigolancamento(atendimentoLigacaoDAO.proxCodCad(atendimentoLigacao.getCodigoatendimento()));

                        popularCampoLigacao();

                        if (!atendimentoLigacaoDAO.inserir(atendimentoLigacao)) {

                        } else {

                        }

                    } else {
                        //  popularCampoLigacao();
                        atendimentoLigacao.setCodigolancamento(Integer.valueOf(txtLigcaoCodigo.getText()));
                        atendimentoLigacao.setCodigoatendimento(Integer.valueOf(txtSequenciaLancamento.getText()));
                        atendimentoLigacao.setCodigocliente(Integer.valueOf(txtCodigo.getText()));
                        atendimentoLigacao.setDescricaoligacao(txtLigacao.getText());
                        if (!atendimentoLigacaoDAO.alterar(atendimentoLigacao)) {

                        } else {

                        }

                    }
                } catch (Exception e) {
                } finally {
                    enviar();

                }

            }
        }

    }

    public void setRecebePalavra(CRMClientesAtendimento veioInput, String codigoCliente, String nomecliente, String atendimento,
            Usuario usuario, boolean gravarligacao, AtendimentoLigacao atendimentoLigacao,
            boolean ligacaoCliente, String tipoconta) throws Exception {
        this.veioCampo = veioInput;
        this.gravarligacao = gravarligacao;
        btnExcluirLigacao.setEnabled(false);
        this.ligacaoCliente = ligacaoCliente;
        this.tipoconta = tipoconta;
        if (atendimento != null) {
            this.usuario = new Usuario();
            this.usuario = usuario;
            txtCodigo.setText(codigoCliente);
            txtNome.setText(nomecliente);
            txtSequenciaLancamento.setText(atendimento);
            txtDataLigacao.setDate(new Date());
            txtLigcaoCodigo.setText("0");
            this.atendimentoLigacao = new AtendimentoLigacao();
            this.atendimentoLigacao = atendimentoLigacao;

            if (atendimentoLigacao != null) {
                if (atendimentoLigacao.getCodigolancamento() > 0) {
                    txtLigacao.setText(atendimentoLigacao.getDescricaoligacao());
                    txtLigcaoCodigo.setText(String.valueOf(atendimentoLigacao.getCodigolancamento()));

                    txtDataLigacao.setDate(atendimentoLigacao.getDataligacao());

                }
            }
            btnExcluirLigacao.setEnabled(true);
            txtLigacao.setEnabled(true);
            btnGravarLigacao.setEnabled(true);
        }

    }

    public void setRecebePalavraConta(CRMContaManutencao veioInputConta, String codigoCliente, String nomecliente, String atendimento,
            Usuario usuario, boolean gravarligacao, AtendimentoLigacao atendimentoLigacao,
            boolean ligacaoCliente, String tipoconta) throws Exception {
        this.veioCampoConta = veioInputConta;
        this.gravarligacao = gravarligacao;
        btnExcluirLigacao.setEnabled(false);
        this.ligacaoCliente = ligacaoCliente;
        this.tipoconta = tipoconta;
        if (atendimento != null) {
            this.usuario = new Usuario();
            this.usuario = usuario;
            txtCodigo.setText(codigoCliente);
            txtNome.setText(nomecliente);
            txtSequenciaLancamento.setText(atendimento);
            txtDataLigacao.setDate(new Date());
            txtLigcaoCodigo.setText("0");
            this.atendimentoLigacao = new AtendimentoLigacao();
            this.atendimentoLigacao = atendimentoLigacao;
            if (atendimentoLigacao != null) {
                if (atendimentoLigacao.getCodigolancamento() > 0) {
                    txtLigacao.setText(atendimentoLigacao.getDescricaoligacao());
                    txtLigcaoCodigo.setText(String.valueOf(atendimentoLigacao.getCodigolancamento()));
                    txtDataLigacao.setDate(atendimentoLigacao.getDataligacao());
                }
            }
            txtLigacao.setEnabled(true);
            btnGravarLigacao.setEnabled(true);
            btnExcluirLigacao.setEnabled(true);
        }

    }

    private void enviar() throws SQLException {
        if (this.veioCampo != null) {
            if (ligacaoCliente) {
                veioCampo.retornarLigacao("", " and usu_codcli = " + txtCodigo.getText());
            } else {
                veioCampo.retornarLigacao("", "  and usu_codcli = " + txtCodigo.getText() + " \n"
                        + " and usu_codmot = " + txtSequenciaLancamento.getText());

            }

        }
        if (this.veioCampoConta != null) {
            if (ligacaoCliente) {
                veioCampoConta.retornarLigacao("", "and usu_codcli = " + txtCodigo.getText());

            }

        }
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

    private void removerRegistro() throws SQLException {
        if (atendimentoLigacao != null) {
            if (atendimentoLigacao.getCodigolancamento() > 0) {
                if (!this.atendimentoLigacaoDAO.remover(atendimentoLigacao)) {

                } else {
                    btnExcluirLigacao.setEnabled(false);
                    enviar();
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
        jScrollPane6 = new javax.swing.JScrollPane();
        txtLigacao = new javax.swing.JTextArea();
        btnGravarLigacao = new javax.swing.JButton();
        txtCodigo = new org.openswing.swing.client.TextControl();
        txtSequenciaLancamento = new org.openswing.swing.client.TextControl();
        txtNome = new org.openswing.swing.client.TextControl();
        txtLigcaoCodigo = new org.openswing.swing.client.TextControl();
        jButton6 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDataLigacao = new org.openswing.swing.client.DateControl();
        jLabel3 = new javax.swing.JLabel();
        btnExcluirLigacao = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        txtLigacao.setColumns(20);
        txtLigacao.setRows(3);
        txtLigacao.setEnabled(false);
        jScrollPane6.setViewportView(txtLigacao);

        btnGravarLigacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravarLigacao.setText("Salvar");
        btnGravarLigacao.setEnabled(false);
        btnGravarLigacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarLigacaoActionPerformed(evt);
            }
        });

        txtCodigo.setEnabled(false);
        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        txtSequenciaLancamento.setEnabled(false);
        txtSequenciaLancamento.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNome.setEnabled(false);
        txtNome.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        txtLigcaoCodigo.setEnabled(false);
        txtLigcaoCodigo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        jButton6.setText("Sair");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel1.setText("Conta");

        jLabel2.setText("Nome");

        txtDataLigacao.setEnabled(false);
        txtDataLigacao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel3.setText("Data");

        btnExcluirLigacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-delete-16x16.png"))); // NOI18N
        btnExcluirLigacao.setText("Excluir");
        btnExcluirLigacao.setEnabled(false);
        btnExcluirLigacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirLigacaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnGravarLigacao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluirLigacao)
                        .addGap(6, 6, 6)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 461, Short.MAX_VALUE)
                        .addComponent(txtSequenciaLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLigcaoCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDataLigacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))))
                .addGap(8, 8, 8))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDataLigacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtSequenciaLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtLigcaoCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(btnGravarLigacao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(btnExcluirLigacao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnExcluirLigacao, btnGravarLigacao, jButton6});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGravarLigacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarLigacaoActionPerformed
        if (txtLigacao.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Erro: Informe o conteudo da ligação",
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        } else {
            if (ManipularRegistros.pesos(" Gravar ligação ")) {
                try {
                    salvarLigacao();
                } catch (SQLException ex) {
                    Logger.getLogger(CRMClientesAtendimento.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(CRMClientesAtendimento.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }//GEN-LAST:event_btnGravarLigacaoActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        //
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        //
    }//GEN-LAST:event_txtNomeActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnExcluirLigacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirLigacaoActionPerformed
        if (ManipularRegistros.gravarRegistros(" Deseja excluir esse registro")) {
            try {
                removerRegistro();
            } catch (SQLException ex) {
                Logger.getLogger(CRMLigacao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_btnExcluirLigacaoActionPerformed

    private String emailselecionado;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcluirLigacao;
    private javax.swing.JButton btnGravarLigacao;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane6;
    private org.openswing.swing.client.TextControl txtCodigo;
    private org.openswing.swing.client.DateControl txtDataLigacao;
    private javax.swing.JTextArea txtLigacao;
    private org.openswing.swing.client.TextControl txtLigcaoCodigo;
    private org.openswing.swing.client.TextControl txtNome;
    private org.openswing.swing.client.TextControl txtSequenciaLancamento;
    // End of variables declaration//GEN-END:variables
}
