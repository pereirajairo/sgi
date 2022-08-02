/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Email;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
public final class CRMEmail extends InternalFrame {

    private Email email;
    private List<Email> listEmails = new ArrayList<Email>();
    private CRMContaManutencao veioCampo;
    private CRMClientesAtendimento veioCampoAtendimento;

    public CRMEmail() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource(" E-mails"));
            this.setSize(800, 500);

            getEmails("", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void setRecebePalavraEmail(CRMContaManutencao veioInput) throws Exception {
        this.veioCampo = veioInput;

    }

    public void setRecebePalavraEmailAtendimento(CRMClientesAtendimento veioInput) throws Exception {
        this.veioCampoAtendimento = veioInput;

    }

    private void getEmails(String string, String string1) throws SQLException, Exception {
        email = new Email();
        email.setId(1);
        email.setNome("Jairo Silva");
        email.setEmail("jairo.silva@erbs.com.br");
        listEmails.add(email);

        email = new Email();
        email.setId(2);
        email.setNome("Alirio Junior");
        email.setEmail("alirio.junior@erbs.com.br");
        listEmails.add(email);

        email = new Email();
        email.setId(3);
        email.setNome("Leonardo Oliveira");
        email.setEmail("leonardo.oliveira@erbs.com.br");
        listEmails.add(email);

        email = new Email();
        email.setId(4);
        email.setNome("Ariel Tiago");
        email.setEmail("ariel.tiago@erbs.com.br");
        listEmails.add(email);

        email = new Email();
        email.setId(5);
        email.setNome("Carla  Lana");
        email.setEmail("carla.lana@erbs.com.br");
        listEmails.add(email);
;

     

        email = new Email();
        email.setId(8);
        email.setNome("Felipe Erbs");
        email.setEmail("felipe.erbs@erbs.com.br");
        listEmails.add(email);

      

        email = new Email();
        email.setId(10);
        email.setNome("Rafael Fink");
        email.setEmail("rafael.fink@erbs.com.br");
        listEmails.add(email);

        email = new Email();
        email.setId(11);
        email.setNome("Christhiano Andre");
        email.setEmail("christhiano.bertochi@erbs.com.br");
        listEmails.add(email);

    

        email = new Email();
        email.setId(13);
        email.setNome("Cátia Regina");
        email.setEmail("catia.gianesini@erbs.com.br");
        listEmails.add(email);

     

        if (listEmails != null) {
            carregarTabela();
        }
    }

    public void carregarTabela() throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/email.png");

        for (Email ema : listEmails) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new ContasEmbarque.ColorirRenderer();
            CRMEmail.JTableRenderer renderers = new CRMEmail.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = BomIcon;

            linha[1] = ema.getNome();
            linha[2] = ema.getEmail();
            linha[3] = ema.isSelecionar();
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
        jTableCarga.setRowHeight(35);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

    private void selecionarRange() {
        emailselecionado = "";
        if (veioCampo != null) {
            veioCampo.retornarEmail(emailselecionado);
        }
        String selecionar = "";
        if (jTableCarga.getRowCount() > 0) {
            for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                if ((Boolean) jTableCarga.getValueAt(i, 3)) {
                    emailselecionado += (jTableCarga.getValueAt(i, 2).toString() + ";");
                }
            }
            int tam = emailselecionado.length();
            if (tam > 0) {
                selecionar = emailselecionado.substring(0, tam - 1);
                if (veioCampo != null) {
                    veioCampo.retornarEmail(selecionar);
                }
                if (veioCampoAtendimento != null) {
                    veioCampoAtendimento.retornarEmail(selecionar);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Nome", "Email", "Selecioar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCarga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCargaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableCarga);
        if (jTableCarga.getColumnModel().getColumnCount() > 0) {
            jTableCarga.getColumnModel().getColumn(0).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(100);
        }

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        jButton1.setText("Selecionar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String emailselecionado;
    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        try {
            int linhaSelSit = jTableCarga.getSelectedRow();
            int colunaSelSit = jTableCarga.getSelectedColumn();
            // emailselecionado = jTableCarga.getValueAt(linhaSelSit, 2).toString();

            selecionarRange();
        } catch (Exception ex) {
            Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        selecionarRange();
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableCarga;
    // End of variables declaration//GEN-END:variables
}
