/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;


import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.NotaFiscalItens;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.NotaFiscalItensDAO;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.UtilDatas;
import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class CRMProdutosFaturamento extends InternalFrame {

    private UtilDatas utilDatas;
    private CRMClientesAtendimento veioCampo;

    public CRMProdutosFaturamento() {
        try {
            initComponents();

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private Usuario usuario;

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        UsuarioERPDAO dao = new UsuarioERPDAO();

        usuario = dao.getUsuario(Menu.username.toLowerCase());
        return usuario;
    }

    private void getNotaItens(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException, Exception {
        NotaFiscalItensDAO dao = new NotaFiscalItensDAO();
        List<NotaFiscalItens> lstNotaItens = new ArrayList<NotaFiscalItens>();
        lstNotaItens = dao.getNotaFiscalItens(PESQUISA_POR, PESQUISA, DATA);
        if (lstNotaItens != null) {
            carregarTabelaItens(lstNotaItens);
        }

    }

    public void carregarTabelaItens(List<NotaFiscalItens> lstNotaItens) throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableNota.getModel();
        modeloCarga.setNumRows(0);
        jTableNota.setRowHeight(40);
        jTableNota.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon CreIcon = getImage("/images/sitBom.png");

        for (NotaFiscalItens cli : lstNotaItens) {
            Object[] linha = new Object[5];
            TableColumnModel columnModel = jTableNota.getColumnModel();
            CRMProdutosFaturamento.JTableRenderer renderers = new CRMProdutosFaturamento.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = CreIcon;
            linha[1] = cli.getProduto();
            linha[2] = cli.getCadProduto().getDescricaoproduto();
            linha[3] = cli.getMes() + " / " + cli.getAno();
            linha[4] = cli.getQuantidade();
            

            modeloCarga.addRow(linha);
        }

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void setRecebePalavra(CRMClientesAtendimento veioInput, Cliente cliente, String PROCESSO) throws Exception {
        this.veioCampo = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Produtos Faturados Cliente " + cliente.getNome()));
        getNotaItens("NOTA", " and E140NFV.codcli = " + cliente.getCodigo_cliente(), new Date());
    }

    private void limparTela() {

    }

    private ImageIcon getImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return (new ImageIcon(url));
        } else {
            return null;

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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTableNota = new javax.swing.JTable();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Produtos");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jTableNota.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "#", "Produto", "Descrição", "Mês", "Quantidade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableNota.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableNotaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableNota);
        if (jTableNota.getColumnModel().getColumnCount() > 0) {
            jTableNota.getColumnModel().getColumn(0).setMinWidth(100);
            jTableNota.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableNota.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableNota.getColumnModel().getColumn(1).setMinWidth(100);
            jTableNota.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableNota.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableNota.getColumnModel().getColumn(3).setMinWidth(200);
            jTableNota.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTableNota.getColumnModel().getColumn(3).setMaxWidth(200);
            jTableNota.getColumnModel().getColumn(4).setMinWidth(100);
            jTableNota.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableNota.getColumnModel().getColumn(4).setMaxWidth(100);
        }

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnCancelar.setText("Fechar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar)))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableNotaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableNotaMouseClicked
        int linhaSelSit = jTableNota.getSelectedRow();
        int colunaSelSit = jTableNota.getSelectedColumn();

    }//GEN-LAST:event_jTableNotaMouseClicked

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableNota;
    // End of variables declaration//GEN-END:variables
}
