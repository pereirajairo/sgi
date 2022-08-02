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
import br.com.sgi.bean.Funcionario;
import br.com.sgi.bean.OrdensProducao;
import br.com.sgi.bean.Produto;
import br.com.sgi.dao.BalancaDAO;
import br.com.sgi.dao.BalancaLancamentoDAO;
import br.com.sgi.dao.BalancaParametroDAO;
import br.com.sgi.dao.CaixaDAO;
import br.com.sgi.dao.EmbalagemDAO;
import br.com.sgi.dao.OrdensProducaoDAO;
import br.com.sgi.dao.FuncionarioDAO;
import br.com.sgi.dao.ProdutoDAO;
import br.com.sgi.integracao.Balancinhas;
import br.com.sgi.util.ConversaoHoras;
import br.com.sgi.util.FormatarPeso;
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
public class CadastroFuncionario extends InternalFrame {

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

    private List<Funcionario> listFuncionario = new ArrayList<Funcionario>();

    private static FuncionarioDAO funcionarioDAO;
    private static Funcionario funcionario;

    /**
     * Creates new form BalancinhaFabrica
     */
    public CadastroFuncionario() throws SQLException, Exception {
        initComponents();

        if (funcionarioDAO == null) {
            funcionarioDAO = new FuncionarioDAO();
        }
        getListar("", "");

    }

    private void limparCampos() {

        txtCodigoCracha.setText("");
        txtCodigoFuncionario.setText("");
        txtNomeFuncionario.setText("");
    }

    private void getFuncionario(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listFuncionario = this.funcionarioDAO.getFuncionariosSapiens(PESQUISA_POR, PESQUISA);
        if (listFuncionario != null) {
            carregarTabela();
        }
    }

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {

        PESQUISA += " ";

        listFuncionario = this.funcionarioDAO.getFuncionariosSapiens(PESQUISA_POR, PESQUISA);
        if (listFuncionario != null) {
            carregarTabela();

        }

    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon ProvIcon = getImage("/images/sitAnd.png");
        String data = null;
        double pesoBal = 0.0;
        for (Funcionario mi : listFuncionario) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            CadastroFuncionario.JTableRenderer renderers = new CadastroFuncionario.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = BomIcon;
            linha[1] = mi.getCodigoFuncionario();
            linha[2] = mi.getNomeFuncionario();
            linha[3] = mi.getCodigoCracha();

            modeloCarga.addRow(linha);
        }

    }

    private boolean validaCadastro(Funcionario fun, String processo) throws SQLException {
        boolean retorno = true;
        Funcionario funAux = funcionarioDAO.getFuncionarioSapiens(PESQUISA_POR, " and USU_NUMCAD =" + Integer.valueOf(txtCodigoFuncionario.getValue().toString()) + " "); // buscar p cracha
        Funcionario funCra = funcionarioDAO.getFuncionarioSapiens(PESQUISA_POR, " and USU_CODCRA ='" + txtCodigoCracha.getText().trim() + "' ");
        String novoCracha = txtCodigoCracha.getText().trim();
        String cracha = "";
        String funCracha = "";
        int cod = 0;
        int codCracha = 0;
        if (funCra != null) {
            if (funCra.getCodigoFuncionario() > 0) {
                funCracha = funCra.getCodigoCracha();
                codCracha = funCra.getCodigoFuncionario();
            } else if ((processo.equals("I")) && (!funCra.getCodigoCracha().equals(""))) {
                retorno = false;
                JOptionPane.showMessageDialog(null, "Funcionario já cadastrado com esse crachá ", "Alerta:", JOptionPane.WARNING_MESSAGE);
            } else if ((processo.equals("A")) && ((funCracha.equals(novoCracha)) || (!funCra.getCodigoCracha().equals("")))) {
                retorno = false;
                JOptionPane.showMessageDialog(null, "Funcionario já cadastrado com esse crachá ", "Alerta:", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (funAux != null) {
            if (funAux.getCodigoFuncionario() > 0) {
                cod = funAux.getCodigoFuncionario();
                String nome = funAux.getNomeFuncionario();
                cracha = funAux.getCodigoCracha();
            } else if ((funAux.getCodigoFuncionario() > 0) && (processo.equals("I"))) {
                retorno = false;
                JOptionPane.showMessageDialog(null, "Funcionario já cadastrado ", "Alerta:", JOptionPane.WARNING_MESSAGE);
            } else if ((processo.equals("A")) && ((funCracha.equals(novoCracha)) || (!funCra.getCodigoCracha().equals("")))) {
                retorno = false;
                JOptionPane.showMessageDialog(null, "Funcionario já cadastrado com esse crachá ", "Alerta:", JOptionPane.WARNING_MESSAGE);
            }
        }
        if (cod == codCracha) {
            retorno = true;
        }

        if ((txtCodigoCracha.getText() == "") || (txtNomeFuncionario.getText() == "") || (Integer.valueOf(txtCodigoFuncionario.getValue().toString()) == null)) {
            JOptionPane.showMessageDialog(null, "Favor preencher todos os campos ", "Alerta:", JOptionPane.WARNING_MESSAGE);
            retorno = false;
        }
        return retorno;
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    private void popularRegistro() {
        try {
            funcionario = new Funcionario();
            funcionario.setCodigoFuncionario(Integer.valueOf(txtCodigoFuncionario.getValue().toString()));
            funcionario.setNomeFuncionario(txtNomeFuncionario.getText().trim());
            funcionario.setCodigoCracha(txtCodigoCracha.getText().trim());

        } catch (Exception e) {
        }
    }

    private void salvarRegistro() throws SQLException, Exception {

        popularRegistro();
        if (validaCadastro(funcionario, "I")) {
            if (!funcionarioDAO.inserir(funcionario)) {

            } else {
                limparCampos();
                getListar("", "");
            }
        }

    }

    private void alteraRegistro() throws SQLException, Exception {
        popularRegistro();

        if (validaCadastro(funcionario, "A")) {
            if (!funcionarioDAO.alterar(funcionario)) {
                // btnCadastro.setEnabled(false);
            } else {

                // btnCadastro.setEnabled(true);
                //  txtPesoEntrada.setEnabled(true);
                limparCampos();
                getListar("", "");
            }
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        btnCadastro = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblBalanca = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtNomeFuncionario = new org.openswing.swing.client.TextControl();
        txtCodigoCracha = new org.openswing.swing.client.TextControl();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btnSair = new javax.swing.JButton();
        txtCodigoFuncionario = new org.openswing.swing.client.NumericControl();
        btnAlterar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();

        setTitle("Cadastro Crachá de Funcionario");
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

        jLabel13.setText("Codigo do Crachá:");

        jLabel3.setText("Codigo Funcionario:");

        jLabel15.setText("Nome Funcionario:");

        txtNomeFuncionario.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtCodigoCracha.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jTableCarga.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "#", "CODIGO", "NOME", "CRACHA "
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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

        jLabel4.setText("Funcionario:");

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        txtCodigoFuncionario.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCodigoCracha, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 692, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(131, 131, 131)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCodigoFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNomeFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAlterar, btnCadastro, btnLimpar, btnSair});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtCodigoFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(txtNomeFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigoCracha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)))
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

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCodigoCracha, txtCodigoFuncionario, txtNomeFuncionario});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAlterar, btnCadastro, btnLimpar, btnSair});

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
        this.dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        try {
            int linhaSelSit = jTableCarga.getSelectedRow();
            int colunaSelSit = jTableCarga.getSelectedColumn();
            txtCodigoFuncionario.setText(jTableCarga.getValueAt(linhaSelSit, 1).toString());
            txtNomeFuncionario.setText(jTableCarga.getValueAt(linhaSelSit, 2).toString());
            txtCodigoCracha.setText(jTableCarga.getValueAt(linhaSelSit, 3).toString());

            txtCodigoFuncionario.setEnabled(false);
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
            Logger.getLogger(CadastroFuncionario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCadastroActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        try {

            //    validaCadastro();
            alteraRegistro();

        } catch (Exception ex) {
            Logger.getLogger(CadastroFuncionario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void jTableCargaAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jTableCargaAncestorAdded


    }//GEN-LAST:event_jTableCargaAncestorAdded

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed

        limparCampos();
        btnAlterar.setEnabled(false);
        btnCadastro.setEnabled(true);
        txtCodigoFuncionario.setEnabled(true);
    }//GEN-LAST:event_btnLimparActionPerformed

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
    private static javax.swing.JLabel jLabel13;
    private static javax.swing.JLabel jLabel15;
    private static javax.swing.JLabel jLabel3;
    private static javax.swing.JLabel jLabel4;
    private static javax.swing.JPanel jPanel2;
    private static javax.swing.JScrollPane jScrollPane3;
    private static javax.swing.JTabbedPane jTabbedPane1;
    private static javax.swing.JTable jTableCarga;
    private static javax.swing.JLabel lblBalanca;
    private static org.openswing.swing.client.TextControl txtCodigoCracha;
    private static org.openswing.swing.client.NumericControl txtCodigoFuncionario;
    private static org.openswing.swing.client.TextControl txtNomeFuncionario;
    // End of variables declaration//GEN-END:variables

}
