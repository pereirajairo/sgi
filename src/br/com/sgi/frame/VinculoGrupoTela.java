/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.GrupoUsuario;
import br.com.sgi.bean.VinculoGrupo;
import br.com.sgi.dao.GrupoUsuarioDAO;
import br.com.sgi.dao.VinculoGrupoDAO;

import br.com.sgi.util.ConversaoHoras;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.ManipularRegistros;
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
public class VinculoGrupoTela extends InternalFrame {

    private String PESQUISA;
    private static String PESQUISA_POR;

    private String PROCESSO;
    private String COMPLEMENTO;
    private String PARAMETROCOM;
    private Integer codigoGrupo;
    private Integer codigoTela;

    private List<GrupoUsuario> listGrupoGrupoUsuario = new ArrayList<GrupoUsuario>();
    private List<VinculoGrupo> listVinculoGrupo = new ArrayList<VinculoGrupo>();

    private static GrupoUsuarioDAO grupoUsuarioDAO;
    private static GrupoUsuario grupoUsuario;

    private static VinculoGrupoDAO vinculoGrupoDAO;
    private static VinculoGrupo vinculoGrupo;

    /**
     * Creates new form BalancinhaFabrica
     */
    public VinculoGrupoTela() throws SQLException, Exception {
        initComponents();

        if (grupoUsuarioDAO == null) {
            grupoUsuarioDAO = new GrupoUsuarioDAO();
        }
        if (vinculoGrupoDAO == null) {
            vinculoGrupoDAO = new VinculoGrupoDAO();
        }
        getListar("", "");

    }

    private void limparCampos() {

    }

    private void getGrupoGrupoUsuario(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listGrupoGrupoUsuario = this.grupoUsuarioDAO.getGrupoUsuarios(PESQUISA_POR, PESQUISA);
        if (listGrupoGrupoUsuario != null) {
            carregarTabela();
        }
    }

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {

        PESQUISA += " ";

        listGrupoGrupoUsuario = this.grupoUsuarioDAO.getGrupoUsuarios(PESQUISA_POR, PESQUISA);
        if (listGrupoGrupoUsuario != null) {
            carregarTabela();

        }

    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableGrupo.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon ProvIcon = getImage("/images/sitAnd.png");
        String data = null;
        for (GrupoUsuario mi : listGrupoGrupoUsuario) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableGrupo.getColumnModel();

            VinculoGrupoTela.JTableRenderer renderers = new VinculoGrupoTela.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            //linha[0] = BomIcon;
            linha[0] = mi.getCodigoGrupo();
            linha[1] = mi.getNomeGrupo();

            modeloCarga.addRow(linha);
        }

    }

    public void selecionarRange() throws SQLException, Exception {

        String selecionar = "";
        if (jTableTela.getRowCount() > 0) {
            for (int i = 0; i < jTableTela.getRowCount(); i++) {
                if ((Boolean) jTableTela.getValueAt(i, 0)) {
                    vinculoGrupo = new VinculoGrupo();
                    int linhaSelSit = jTableTela.getSelectedRow();
                    int colunaSelSit = jTableTela.getSelectedColumn();
                    codigoTela = Integer.parseInt(jTableTela.getValueAt(linhaSelSit, 0).toString());
                    vinculoGrupo.setCodigoGrupo(codigoGrupo);
                    vinculoGrupo.setCodigoTela(codigoTela);
                }
            }
        }
    }

    public void selecionarRangeSalvar() throws SQLException, Exception {

        int qtdreg = 0;
        int contador = 0;
        for (int i = 0; i < jTableTela.getRowCount(); i++) {
            if ((Boolean) jTableTela.getValueAt(i, 0)) {
                qtdreg++;
            }
        }
         vinculoGrupo.setCodigoGrupo(codigoGrupo);
        if (codigoGrupo == 0) {
          
        } else {
              if (!vinculoGrupoDAO.remover(vinculoGrupo)) {

            }

            if (jTableTela.getRowCount() > 0) {
                for (int i = 0; i < jTableTela.getRowCount(); i++) {
                    if ((Boolean) jTableTela.getValueAt(i, 0)) {
                        contador++;
                        vinculoGrupo = new VinculoGrupo();
                        int linhaSelSit = jTableTela.getSelectedRow();
                        int colunaSelSit = jTableTela.getSelectedColumn();
                        codigoTela = Integer.parseInt(jTableTela.getValueAt(i, 1).toString());
                           
                        vinculoGrupo.setCodigoGrupo(codigoGrupo);
                        vinculoGrupo.setCodigoTela(codigoTela);
                        vinculoGrupo.setSequeciaLigacao(vinculoGrupoDAO.proxCodCad());
                        if (!vinculoGrupoDAO.inserir(vinculoGrupo)) {

                        }
                    }
                }
            }
        }
        if (contador == qtdreg) {
            JOptionPane.showMessageDialog(rootPane, "Grupo atualizado com sucesso");
        }

    }

    public void carregarTabelaTela(boolean selecionar) throws Exception {

        redColunastabTela();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableTela.getModel();
        modeloCarga.setNumRows(0);
        for (VinculoGrupo mi : listVinculoGrupo) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableTela.getColumnModel();

            linha[0] = mi.isSelecionar();
            linha[1] = mi.getCodigoTela();
            linha[2] = mi.getNomeTela().toUpperCase();

            modeloCarga.addRow(linha);
        }

    }

    private void buscaTela() throws SQLException, Exception {
        PESQUISA += " ";

        listVinculoGrupo = this.vinculoGrupoDAO.getVinculoGrupos(PESQUISA_POR, codigoGrupo.toString());
        if (listVinculoGrupo != null) {
            boolean selecionar = false;
            carregarTabelaTela(selecionar);

        }
    }

    private boolean validaCadastro(GrupoUsuario fun, String processo) throws SQLException {
        boolean retorno = true;

        return retorno;
    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableGrupo = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btnSair = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableTela = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();

        setTitle("Vinculo Tela e Grupo");
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

        btnCadastro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnCadastro.setText("Atualizar Permissão");
        btnCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastroActionPerformed(evt);
            }
        });

        jTableGrupo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTableGrupo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "CODIGO", "NOME"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableGrupo.setVerifyInputWhenFocusTarget(false);
        jTableGrupo.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jTableGrupoAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jTableGrupo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableGrupoMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableGrupo);
        if (jTableGrupo.getColumnModel().getColumnCount() > 0) {
            jTableGrupo.getColumnModel().getColumn(0).setMinWidth(100);
            jTableGrupo.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableGrupo.getColumnModel().getColumn(0).setMaxWidth(100);
        }

        jLabel4.setText("Grupo:");

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        jTableTela.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTableTela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "SELECIONAR", "CODIGO", "NOME"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTela.setVerifyInputWhenFocusTarget(false);
        jTableTela.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jTableTelaAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jTableTela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTelaMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTableTela);
        if (jTableTela.getColumnModel().getColumnCount() > 0) {
            jTableTela.getColumnModel().getColumn(0).setMinWidth(80);
            jTableTela.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTableTela.getColumnModel().getColumn(0).setMaxWidth(80);
            jTableTela.getColumnModel().getColumn(1).setMinWidth(100);
            jTableTela.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableTela.getColumnModel().getColumn(1).setMaxWidth(100);
        }

        jLabel5.setText("Telas:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(180, 180, 180)
                .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addGap(10, 10, 10))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnLimpar, btnSair});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                        .addGap(21, 21, 21))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCadastro, btnLimpar, btnSair});

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

    private void jTableGrupoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableGrupoMouseClicked
        try {
            int linhaSelSit = jTableGrupo.getSelectedRow();
            int colunaSelSit = jTableGrupo.getSelectedColumn();
            codigoGrupo = Integer.parseInt(jTableGrupo.getValueAt(linhaSelSit, 0).toString());
            buscaTela();

        } catch (Exception ex) {
            Logger.getLogger(SucatasManutencao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableGrupoMouseClicked

    private void btnCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastroActionPerformed
        try {

            if (ManipularRegistros.gravarRegistros(" Gravar ")) {
                selecionarRangeSalvar();
            }

            //    validaCadastro();
        } catch (Exception ex) {
            Logger.getLogger(VinculoGrupoTela.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCadastroActionPerformed

    private void jTableGrupoAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jTableGrupoAncestorAdded

    }//GEN-LAST:event_jTableGrupoAncestorAdded

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed

        limparCampos();

        btnCadastro.setEnabled(true);

    }//GEN-LAST:event_btnLimparActionPerformed

    private void jTableTelaAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jTableTelaAncestorAdded

    }//GEN-LAST:event_jTableTelaAncestorAdded

    private void jTableTelaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTelaMouseClicked
        try {
            selecionarRange();
        } catch (Exception ex) {
            Logger.getLogger(VinculoGrupoTela.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableTelaMouseClicked

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableGrupo.getColumnModel().getColumn(0).setCellRenderer(centralizado);
        jTableGrupo.setRowHeight(25);
        jTableGrupo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //  jTableCarga.setAutoResizeMode(0);

    }

    public void redColunastabTela() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableTela.setRowHeight(25);
        jTableTela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableTela.getColumnModel().getColumn(1).setCellRenderer(centralizado);

        jTableTela.setAutoCreateRowSorter(true);

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
    private static javax.swing.JButton btnCadastro;
    private static javax.swing.JButton btnLimpar;
    private static javax.swing.JButton btnSair;
    private static javax.swing.JLabel jLabel4;
    private static javax.swing.JLabel jLabel5;
    private static javax.swing.JPanel jPanel2;
    private static javax.swing.JScrollPane jScrollPane3;
    private static javax.swing.JScrollPane jScrollPane4;
    private static javax.swing.JTabbedPane jTabbedPane1;
    private static javax.swing.JTable jTableGrupo;
    private static javax.swing.JTable jTableTela;
    // End of variables declaration//GEN-END:variables

}
