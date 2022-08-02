/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Balanca;
import br.com.sgi.bean.BalancaLancamento;
import br.com.sgi.bean.BalancaParametro;
import br.com.sgi.bean.Caixa;
import br.com.sgi.bean.Embalagem;
import br.com.sgi.bean.Caixa;
import br.com.sgi.bean.OrdensProducao;
import br.com.sgi.bean.Produto;
import br.com.sgi.dao.BalancaDAO;
import br.com.sgi.dao.BalancaLancamentoDAO;
import br.com.sgi.dao.BalancaParametroDAO;
import br.com.sgi.dao.CaixaDAO;
import br.com.sgi.dao.EmbalagemDAO;
import br.com.sgi.dao.OrdensProducaoDAO;
import br.com.sgi.integracao.Balancinhas;

import br.com.sgi.dao.CaixaDAO;
import br.com.sgi.dao.ProdutoDAO;
import static br.com.sgi.frame.BalancinhaFabrica.calculoPesoLiquido;
import br.com.sgi.integracao.Balancinhas;
import br.com.sgi.util.ConversaoHoras;
import br.com.sgi.util.FormatarPeso;
import static br.com.sgi.util.FormatarPeso.limpaValorEmbalagem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author matheus.luiz
 */
public class CadastroCaixa extends InternalFrame {

    private Balanca balanca;
    private BalancaLancamento balancaLancamento;
    private String PESQUISA;
    private static String PESQUISA_POR;

    private String PROCESSO;
    private String COMPLEMENTO;
    private String PARAMETROCOM;
    private Integer CodigoBalanca;
    private String codigoEmbalagem;
    private String nomeEmbalagem;
    private Double pesoEmbalagem;
    private static Double pesoLiquido;
    private static String pesoEmb;
    private static Double pesoDes;

    private List<Caixa> listCaixa = new ArrayList<Caixa>();

    private static CaixaDAO caixaDAO;
    private static Caixa caixa;

    /**
     * Creates new form BalancinhaFabrica
     */
    public CadastroCaixa() throws SQLException, Exception {
        initComponents();
     //   txtPeso.setText("0.0 KG");
        Balancinhas.ClosePort();
        Balancinhas.main(null);

        if (caixaDAO == null) {
            caixaDAO = new CaixaDAO();
        }
        getListar("", "");

    }
      public static void getPesoBalanca(String peso) {
        try {
            txtPeso.setText(peso + " KG");
            
        } catch (Exception e) {

        }
    }


    private void limparCampos() {
        txtCodigoCaixa.setText("");
    }

    private void getCaixa(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listCaixa = this.caixaDAO.getCaixas(PESQUISA_POR, PESQUISA);
        if (listCaixa != null) {
            carregarTabela();
        }
    }

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {

        PESQUISA += " ";

        listCaixa = this.caixaDAO.getCaixas(PESQUISA_POR, PESQUISA);
        if (listCaixa != null) {
            carregarTabela();

        }

    }

    public void carregarTabela() throws Exception {

        //redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon ProvIcon = getImage("/images/sitAnd.png");
        String data = null;
        double pesoBal = 0.0;
        for (Caixa mi : listCaixa) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            CadastroCaixa.JTableRenderer renderers = new CadastroCaixa.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
           
            String uso = mi.getEmusuCaixa();
            String situacao = mi.getSituacaoCaixa();
            if (uso =="S"){
            linha[0] = RuimIcon;
            }else{
             linha[0] = BomIcon;   
            }
            linha[1] = mi.getCodigoCaixa();
            linha[2] = mi.getPesoCaixa()+ " KG";
            if ( uso.equals("S")){
                linha[3] = "Sim";
            }else{
                linha[3] = "Não";  
            }
            if(situacao.equals("A")){
                linha[4] = "Ativo";
            }else{
                linha[4] = "Inativo";
            }
            
            modeloCarga.addRow(linha);
        }

    }

    private boolean validaCadastro(Caixa fun, String processo) throws SQLException {
        boolean retorno = true;

        Caixa funAux = caixaDAO.getCaixa(PESQUISA_POR, " and USU_CODCAI =" + Integer.valueOf(txtCodigoCaixa.getValue().toString()) + " "); // buscar p cracha
         if ((funAux != null) && (processo == "I")) {
            if (funAux.getCodigoCaixa()> 0) {
                retorno = false;
                JOptionPane.showMessageDialog(null, "Caixa já cadastrado ", "Alerta:", JOptionPane.WARNING_MESSAGE);
            }
        }
        return retorno;

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    private void popularRegistro() {
        try {
            caixa = new Caixa();
            
            String situacao = jComboBox1.getSelectedItem().toString();
            double peso = limpaValorEmbalagem(txtPeso.getText());
            caixa.setCodigoCaixa(Integer.valueOf(txtCodigoCaixa.getValue().toString().trim()));
            caixa.setPesoCaixa(peso);     
            caixa.setSituacaoCaixa(situacao);
     

        } catch (Exception e) {
        }
    }

    private void salvarRegistro() throws SQLException, Exception {

        popularRegistro();
        if (validaCadastro(caixa,"I")) {
            if (!caixaDAO.inserir(caixa)) {
                // btnCadastro.setEnabled(false);
            } else {

                limparCampos();
                getListar("", "");
            }
        }

    }

    private void alteraRegistro() throws SQLException, Exception {
        popularRegistro();

        //  if (validaCadastro(caixa,"A")) {
        if (!caixaDAO.alterar(caixa)) {
            // btnCadastro.setEnabled(false);
        } else {

            // btnCadastro.setEnabled(true);
            //  txtPesoEntrada.setEnabled(true);
            limparCampos();
            getListar("", "");
        }
        //  }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        btnCadastro = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblBalanca = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btnSair = new javax.swing.JButton();
        txtCodigoCaixa = new org.openswing.swing.client.NumericControl();
        btnAlterar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        txtPeso = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();

        setTitle("Cadastro de Caixa");
        setToolTipText("");
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                formAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        btnCadastro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-e-fechar16x16.png"))); // NOI18N
        btnCadastro.setText("Cadastrar");
        btnCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastroActionPerformed(evt);
            }
        });

        jLabel13.setText("Situação:");

        jLabel3.setText("Codigo Caixa:");

        jTableCarga.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "CODIGO", "PESO", "USANDO", "SITUAÇÃO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCarga.setVerifyInputWhenFocusTarget(false);
        jTableCarga.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jTableCargaAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jTableCarga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCargaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableCarga);
        if (jTableCarga.getColumnModel().getColumnCount() > 0) {
            jTableCarga.getColumnModel().getColumn(0).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(250);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(250);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(250);
        }

        jLabel4.setText("Caixas:");

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        txtCodigoCaixa.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-como-16x16.png"))); // NOI18N
        btnAlterar.setText("Alterar");
        btnAlterar.setEnabled(false);
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-vassoura-16.png"))); // NOI18N
        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A", "I", " " }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        txtPeso.setBackground(new java.awt.Color(102, 102, 255));
        txtPeso.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPeso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPeso.setText("0000");
        txtPeso.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        txtPeso.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        txtPeso.setOpaque(true);

        jLabel14.setText("Peso:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(192, 192, 192)
                                .addComponent(lblBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btnAlterar)
                                .addGap(14, 14, 14)
                                .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 488, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigoCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtCodigoCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel13))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)))
                    .addComponent(lblBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAlterar, btnCadastro, btnSair});

        jTabbedPane1.addTab("Cadastro", jPanel2);
        jPanel2.getAccessibleContext().setAccessibleName("Balança");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Balança ");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded

    }//GEN-LAST:event_formAncestorAdded

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        // TODO add your handling code here:
       Balancinhas.ClosePort();
        this.dispose();
        
    }//GEN-LAST:event_btnSairActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        try {
            int linhaSelSit = jTableCarga.getSelectedRow();
            int colunaSelSit = jTableCarga.getSelectedColumn();
            txtCodigoCaixa.setText(jTableCarga.getValueAt(linhaSelSit, 1).toString());
            txtPeso.setText(jTableCarga.getValueAt(linhaSelSit, 2).toString());
          
            
            String sit = jTableCarga.getValueAt(linhaSelSit, 4).toString();
            if(sit.equals("Ativo")){
                sit = "A";
            }else{
                sit = "I"; 
            }           
            
            jComboBox1.setSelectedItem(sit);


            txtCodigoCaixa.setEnabled(false);
            btnAlterar.setEnabled(true);
            btnCadastro.setEnabled(false);

        } catch (Exception ex) {
            Logger.getLogger(SucatasManutencao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastroActionPerformed
        try {

            //    validaCadastro();
            salvarRegistro();

        } catch (Exception ex) {
            Logger.getLogger(CadastroCaixa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCadastroActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        try {

            //    validaCadastro();
            alteraRegistro();

        } catch (Exception ex) {
            Logger.getLogger(CadastroCaixa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void jTableCargaAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jTableCargaAncestorAdded


    }//GEN-LAST:event_jTableCargaAncestorAdded

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed

        limparCampos();
        btnAlterar.setEnabled(false);
        btnCadastro.setEnabled(true);
        txtCodigoCaixa.setEnabled(true);
    }//GEN-LAST:event_btnLimparActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        // jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
        // jTableCarga.getColumnModel().getColumn(5).setCellRenderer(direita);
        //jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.setRowHeight(35);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //  jTableCarga.setAutoResizeMode(0);

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

    private ImageIcon getImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return (new ImageIcon(url));
        } else {
            return null;

        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton btnAlterar;
    private static javax.swing.JButton btnCadastro;
    private static javax.swing.JButton btnLimpar;
    private static javax.swing.JButton btnSair;
    private static javax.swing.JComboBox<String> jComboBox1;
    private static javax.swing.JLabel jLabel13;
    private static javax.swing.JLabel jLabel14;
    private static javax.swing.JLabel jLabel3;
    private static javax.swing.JLabel jLabel4;
    private static javax.swing.JPanel jPanel2;
    private static javax.swing.JScrollPane jScrollPane3;
    private static javax.swing.JTabbedPane jTabbedPane1;
    private static javax.swing.JTable jTableCarga;
    private static javax.swing.JLabel lblBalanca;
    private static org.openswing.swing.client.NumericControl txtCodigoCaixa;
    private static javax.swing.JLabel txtPeso;
    // End of variables declaration//GEN-END:variables

}
