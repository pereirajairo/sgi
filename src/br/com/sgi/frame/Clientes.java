/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.ClienteGrupo;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.ClienteGrupoDAO;

import br.com.sgi.util.UtilDatas;
import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class Clientes extends InternalFrame {

    private Cliente cliente;
    private ClienteDAO clienteDAO;
    private List<Cliente> lstCliente = new ArrayList<Cliente>();

    private UtilDatas utilDatas;
    private SucataContaCorrente veioCampo;
    private SucataContaCorrenteIndustrializacao veioCampoConta;

    //  private SucataContaCorrente veioCampoConta;
    public Clientes() {
        try {
            initComponents();

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

            if (clienteDAO == null) {
                this.clienteDAO = new ClienteDAO();
            }
            loadGrupoCliente();
            LoadEstados();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void loadGrupoCliente() throws SQLException {
        List<ClienteGrupo> lstClienteGrupo = new ArrayList<ClienteGrupo>();
        lstClienteGrupo = clienteDAO.getClienteGrupo("", "");
        if (lstClienteGrupo != null) {
            for (ClienteGrupo g : lstClienteGrupo) {
                txtGrupo.addItem(g.getCodgrp() + " - " + g.getNome());
            }

        }

    }

    private void LoadEstados() {
        BaseEstado estado = new BaseEstado();
        Map<String, String> mapas = estado.getEstados();
        for (String uf : mapas.keySet()) {
            txtEstado.addItem(mapas.get(uf));
        }
    }

    private void getClientes(String codigo) throws SQLException, Exception {
        if (!codigo.isEmpty()) {
            lstCliente = this.clienteDAO.getClientesSucata("CLI", codigo);
            if (lstCliente != null) {
                carregarTabela();
            }

        }
    }

    public void carregarTabela() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCad.getModel();
        modeloCarga.setNumRows(0);
        redColunastab();
        jTableCad.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon CreIcon = getImage("/images/user_suit.png");

        for (Cliente cli : lstCliente) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableCad.getColumnModel();
            Clientes.JTableRenderer renderers = new Clientes.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = CreIcon;
            linha[1] = cli.getCodigo();
            linha[2] = cli.getNome();
            linha[3] = cli.getEstado();
            linha[4] = cli.getGruponome();
            linha[5] = cli.getGrupocodigo();

            modeloCarga.addRow(linha);
        }

    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCad.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCad.setAutoCreateRowSorter(true);
        jTableCad.getColumnModel().getColumn(1).setCellRenderer(direita);

        jTableCad.setRowHeight(40);

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void setRecebePalavra(SucataContaCorrente veioInput, String PROCESSO) throws Exception {
        this.veioCampo = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Clientes"));
        txtCodigo.requestFocus();

    }

    public void setRecebePalavraConta(SucataContaCorrenteIndustrializacao veioInput, String PROCESSO) throws Exception {
        this.veioCampoConta = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Clientes"));
        txtCodigo.requestFocus();

    }

    private void validarCliente(String retorno) {
        if (cliente != null) {
            if (cliente.getCodigo() > 0) {
                if (veioCampo != null) {
                    try {
                        veioCampo.retornarCliente("", "", cliente);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Problemas." + ex);
                    } finally {
                        this.dispose();
                    }
                }
                if (veioCampoConta != null) {
                    try {
                        veioCampoConta.retornarCliente("", "", cliente);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Problemas." + ex);
                    } finally {
                        this.dispose();
                    }
                }
            }
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

    private void getPesquisarPorCodigo() {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCad.getModel();
        modeloCarga.setNumRows(0);
        if (!txtCodigo.getText().isEmpty()) {
            try {
                getClientes(" and codcli like '%" + txtCodigo.getText().trim() + "%'");
            } catch (SQLException ex) {
                Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Erro: Informe o código ",
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getPesquisarPorNome() {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCad.getModel();
        modeloCarga.setNumRows(0);
        if (!txtNome.getText().isEmpty()) {
            try {
                getClientes(" and nomcli like '%" + txtNome.getText().trim() + "%'");
            } catch (SQLException ex) {
                Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Erro: Informe o código ",
                    "Erro:", JOptionPane.ERROR_MESSAGE);
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

        jLabel12 = new javax.swing.JLabel();
        btnConfirmar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtCodigo = new org.openswing.swing.client.TextControl();
        txtNome = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCad = new javax.swing.JTable();
        txtEstado = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        btnSucCli = new javax.swing.JButton();
        btnSucCli1 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        txtGrupo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        btnSucCli2 = new javax.swing.JButton();
        btnConfirmarGrupo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clientes");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jLabel12.setText("Cliente");

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
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        jLabel1.setText("Nome");

        jTableCad.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "#", "Código", "Nome", "Estado", "Grupo", "GrupoCodigo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCadMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableCad);
        if (jTableCad.getColumnModel().getColumnCount() > 0) {
            jTableCad.getColumnModel().getColumn(0).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(1).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(3).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableCad.getColumnModel().getColumn(4).setMinWidth(200);
            jTableCad.getColumnModel().getColumn(4).setPreferredWidth(200);
            jTableCad.getColumnModel().getColumn(4).setMaxWidth(200);
            jTableCad.getColumnModel().getColumn(5).setMinWidth(100);
            jTableCad.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableCad.getColumnModel().getColumn(5).setMaxWidth(100);
        }

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));

        jLabel2.setText("Estado");

        btnSucCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnSucCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSucCliActionPerformed(evt);
            }
        });

        btnSucCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnSucCli1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSucCli1ActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtGrupo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtGrupo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));

        jLabel3.setText("Grupo");

        btnSucCli2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnSucCli2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSucCli2ActionPerformed(evt);
            }
        });

        btnConfirmarGrupo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConfirmarGrupo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnConfirmarGrupo.setText("Confirmar Grupo");
        btnConfirmarGrupo.setEnabled(false);
        btnConfirmarGrupo.setFocusCycleRoot(true);
        btnConfirmarGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarGrupoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnConfirmarGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSucCli, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtGrupo, 0, 172, Short.MAX_VALUE)
                            .addComponent(jLabel3))
                        .addGap(4, 4, 4)
                        .addComponent(btnSucCli2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtEstado, 0, 154, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSucCli1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(jLabel1))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEstado)
                                    .addComponent(jButton1)
                                    .addComponent(txtGrupo))
                                .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnSucCli)
                            .addComponent(btnSucCli2))
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnConfirmarGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnSucCli1))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCancelar, btnConfirmar});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        validarCliente("");

    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        getPesquisarPorCodigo();
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        if (!txtNome.getText().isEmpty()) {
            getPesquisarPorNome();
        }
    }//GEN-LAST:event_txtNomeActionPerformed

    private void jTableCadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCadMouseClicked
        int linhaSelSit = jTableCad.getSelectedRow();
        int colunaSelSit = jTableCad.getSelectedColumn();
        this.cliente = new Cliente();
        this.cliente.setCodigo(Integer.valueOf(jTableCad.getValueAt(linhaSelSit, 1).toString()));
        this.cliente.setNome(jTableCad.getValueAt(linhaSelSit, 2).toString());

        this.cliente.setGrupocodigo(jTableCad.getValueAt(linhaSelSit, 5).toString());
        if (this.cliente.getGrupocliente_id() > 0) {
            this.cliente.setGruponome(jTableCad.getValueAt(linhaSelSit, 4).toString());
        }
        btnConfirmar.setEnabled(false);
        btnConfirmar.setText("Confirmar " + cliente.getCodigo());
        if (cliente != null) {
            btnConfirmar.setEnabled(true);
        }

    }//GEN-LAST:event_jTableCadMouseClicked

    private void btnSucCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSucCliActionPerformed
        getPesquisarPorCodigo();
    }//GEN-LAST:event_btnSucCliActionPerformed

    private void btnSucCli1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSucCli1ActionPerformed
        try {
            getClientes(" and sigufs like '%" + txtEstado.getSelectedItem().toString().trim() + "%'");
        } catch (SQLException ex) {
            Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSucCli1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        getPesquisarPorNome();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnSucCli2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSucCli2ActionPerformed
        try {
            String grupos = txtGrupo.getSelectedItem().toString();
            int index = grupos.indexOf("-");
            String grupo = grupos.substring(0, index);
            getClientes(" and cli.codgre = '" + grupo.trim() + "'");
            btnConfirmarGrupo.setEnabled(true);
        } catch (SQLException ex) {
            Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSucCli2ActionPerformed

    private void btnConfirmarGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarGrupoActionPerformed
        if (veioCampo != null) {
            try {
                String grupos = txtGrupo.getSelectedItem().toString();
                int index = grupos.indexOf("-");
                String grupo = grupos.substring(0, index);
                veioCampo.retornarGrupo("", "", grupos);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }
        }
    }//GEN-LAST:event_btnConfirmarGrupoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnConfirmarGrupo;
    private javax.swing.JButton btnSucCli;
    private javax.swing.JButton btnSucCli1;
    private javax.swing.JButton btnSucCli2;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableCad;
    private org.openswing.swing.client.TextControl txtCodigo;
    private javax.swing.JComboBox<String> txtEstado;
    private javax.swing.JComboBox<String> txtGrupo;
    private org.openswing.swing.client.TextControl txtNome;
    // End of variables declaration//GEN-END:variables
}
