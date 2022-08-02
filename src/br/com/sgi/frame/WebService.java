/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Produto;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.ProdutoDAO;
import br.com.sgi.ws.ConsumirWS;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSNotaFiscalSaida;

/**
 *
 * @author jairosilva
 */
public final class WebService extends InternalFrame {

    public WebService() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Abrir horários"));
            this.setSize(600, 400);
            txtResposta.setLineWrap(true);
            txtResposta.setWrapStyleWord(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    private UtilDatas utilDatas = new UtilDatas();
    private String chamadaWebService;

    private void chamarMetodoWsListarCorreios(String url, String metodo) throws Exception {
        ConsumirWS http = new ConsumirWS();
        System.out.println("Testing 1 - Send Http GET request");

        chamadaWebService = url;
        String json = http.sendGet(chamadaWebService, metodo);

        System.out.println("\nTesting 2 - Send Http POST request"); 
       // txtResposta.setText(json);
        txtResposta.setText(json + "\n" + http.getRESPONSE_CODE());

        Gson g = new Gson();
        Usuario u = new Usuario();
        Type usuarioType = new TypeToken<Usuario>() {
        }.getType();
        u = g.fromJson(json, usuarioType);
        txtResposta.setText(json + "\n" + http.getRESPONSE_CODE() + "\n" + u.getNome());
    }

    private void chamarMetodoWsListar(String url, String metodo) throws Exception {
        ConsumirWS http = new ConsumirWS();
        System.out.println("Testing 1 - Send Http GET request");

        chamadaWebService = url;
        String json = http.sendGetHttps(chamadaWebService, metodo);

        System.out.println("\nTesting 2 - Send Http POST request");
        // txtResposta.setText(json);
        txtResposta.setText(json + "\n" + http.getRESPONSE_CODE());

        Gson g = new Gson();
        Usuario u = new Usuario();
        Type usuarioType = new TypeToken<Usuario>() {
        }.getType();
        u = g.fromJson(json, usuarioType);
        txtResposta.setText(json + "\n" + http.getRESPONSE_CODE() + "\n" + u.getNome());
    }

    private void chamarMetodoWsDelete(String url, String metodo) throws Exception {
        ConsumirWS http = new ConsumirWS();
        chamadaWebService = url;
        String retorno = http.sendGet(chamadaWebService, metodo);
        txtResposta.setText(retorno + "\n" + http.getRESPONSE_CODE());

    }

    private void chamarMetodoWsInsert(String url, String metodo) throws Exception {
        ConsumirWS http = new ConsumirWS();
        chamadaWebService = url;

        Gson g = new Gson();
        Usuario u = new Usuario();
        Type usuarioType = new TypeToken<Usuario>() {
        }.getType();
        u.setId(0);
        u.setNome(txtComando.getText());
        u.setSenha("senha");
        u.setSituacao("ativo");
        String json = g.toJson(u, usuarioType);
        String retorno = http.sendPost(chamadaWebService, json, metodo);
        txtResposta.setText(retorno + "\n" + http.getRESPONSE_CODE());

    }

    private void chamarMetodoWsAlterar(String url, String metodo) throws Exception {
        ConsumirWS http = new ConsumirWS();
        chamadaWebService = url;

        Gson g = new Gson();
        Usuario u = new Usuario();
        Type usuarioType = new TypeToken<Usuario>() {
        }.getType();
        u.setId(Integer.valueOf(txtComando.getText()));
        u.setNome("JAIRO SILVA");
        u.setSenha("JP1");
        u.setSituacao("ATIVO");
        String json = g.toJson(u, usuarioType);
        String retorno = http.sendPost(chamadaWebService, json, metodo);
        txtResposta.setText(retorno + "\n" + http.getRESPONSE_CODE());

    }

    private String pegarProdutos() throws SQLException {
        Produto produtoDAO = new Produto();
        List<Produto> lista = new ArrayList<Produto>();
     //   lista = produtoDAO.getProdutoERPs("5630");
        Gson g = new Gson();
        return g.toJson(lista);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedCotacao = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnGravar = new javax.swing.JButton();
        btnGravar1 = new javax.swing.JButton();
        btnMarcar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnGravar2 = new javax.swing.JButton();
        btnGravar3 = new javax.swing.JButton();
        btnGravar4 = new javax.swing.JButton();
        btnMarcar1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtResposta = new javax.swing.JTextArea();
        txtComando = new javax.swing.JTextField();

        setTitle("Situação");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(560, 340));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnGravar.setText("Listar");
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        btnGravar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sitRuim.png"))); // NOI18N
        btnGravar1.setText("Limpar");
        btnGravar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravar1ActionPerformed(evt);
            }
        });

        btnMarcar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnMarcar.setText("Chamar Sapiens");
        btnMarcar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcarActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnGravar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravar2.setText("Inserir");
        btnGravar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravar2ActionPerformed(evt);
            }
        });

        btnGravar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnGravar3.setText("Alterar");
        btnGravar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravar3ActionPerformed(evt);
            }
        });

        btnGravar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnGravar4.setText("Correios");
        btnGravar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravar4ActionPerformed(evt);
            }
        });

        btnMarcar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnMarcar1.setText("Chamar Sapiens");
        btnMarcar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcar1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(btnGravar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExcluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGravar2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGravar3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGravar1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGravar4, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMarcar, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMarcar1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExcluir, btnGravar, btnGravar1, btnGravar2, btnGravar3});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGravar)
                    .addComponent(btnGravar1)
                    .addComponent(btnMarcar)
                    .addComponent(btnExcluir)
                    .addComponent(btnGravar2)
                    .addComponent(btnGravar3)
                    .addComponent(btnGravar4))
                .addGap(18, 18, 18)
                .addComponent(btnMarcar1)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        txtResposta.setColumns(20);
        txtResposta.setRows(5);
        jScrollPane1.setViewportView(txtResposta);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addComponent(txtComando)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(txtComando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedCotacao.addTab("Turnos", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedCotacao)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jTabbedCotacao)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        try {

            chamarMetodoWsListar("http://localhost:8080/digitproWebService/webresources/digitpro/Usuario/get/" + txtComando.getText(), "GET");
        } catch (Exception ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGravarActionPerformed

    private void btnGravar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravar1ActionPerformed
        txtResposta.setText("");
    }//GEN-LAST:event_btnGravar1ActionPerformed

    private void btnMarcarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcarActionPerformed
        try {
            txtResposta.setText(pegarProdutos());
        } catch (SQLException ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnMarcarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        try {

            chamarMetodoWsDelete("http://localhost:8080/digitproWebService/webresources/digitpro/Usuario/excluir/" + txtComando.getText(), "DELETE");
        } catch (Exception ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnGravar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravar2ActionPerformed
        try {

            chamarMetodoWsInsert("http://localhost:8080/digitproWebService/webresources/digitpro/Usuario/inserir", "POST");
        } catch (Exception ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGravar2ActionPerformed

    private void btnGravar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravar3ActionPerformed
        try {
            chamarMetodoWsAlterar("http://localhost:8080/digitproWebService/webresources/digitpro/Usuario/alterar", "PUT");
        } catch (Exception ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGravar3ActionPerformed

    private void btnGravar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravar4ActionPerformed
        try {

            chamarMetodoWsListar("https://viacep.com.br/ws/" + txtComando.getText() + "/json/", "GET");
        } catch (Exception ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGravar4ActionPerformed

    private void btnMarcar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcar1ActionPerformed
     
     
    }//GEN-LAST:event_btnMarcar1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnGravar1;
    private javax.swing.JButton btnGravar2;
    private javax.swing.JButton btnGravar3;
    private javax.swing.JButton btnGravar4;
    private javax.swing.JButton btnMarcar;
    private javax.swing.JButton btnMarcar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedCotacao;
    private javax.swing.JTextField txtComando;
    private javax.swing.JTextArea txtResposta;
    // End of variables declaration//GEN-END:variables
    /**
     * @return the pessoa
     */
    /**
     * @return the addNewReg
     */
    /**
     * @return the numcpf
     */
}
