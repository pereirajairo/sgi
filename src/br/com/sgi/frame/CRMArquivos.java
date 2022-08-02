/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Email;
import br.com.sgi.bean.XmlArquivos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class CRMArquivos extends InternalFrame {

    private Email email;
    private List<Email> listEmails = new ArrayList<Email>();
    private CRMContaManutencao veioCampo;
    private CRMClientesAtendimento veioCampoAtendimento;
    private XmlArquivos ediArquivos;
    private List<XmlArquivos> lstEdiArquivos;

    public CRMArquivos() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource(" E-mails"));
            this.setSize(800, 500);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public List<XmlArquivos> listar(String dir) throws SQLException, Exception {
        List<XmlArquivos> lstEdiFile = new ArrayList<XmlArquivos>();
        DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");

        File arquivos[];
        File diretorio = new File(dir);
        arquivos = diretorio.listFiles();

        String dataIni = null;

        if (diretorio.isDirectory()) {
            for (int i = 0; i < arquivos.length; i++) {

                dataIni = new SimpleDateFormat("yyyyMMdd").format(new Date(arquivos[i].lastModified()));
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                Date data = new Date(format.parse(dataIni).getTime());
                ediArquivos = new XmlArquivos();
                ediArquivos.setCaminho(arquivos[i].getAbsolutePath());
                ediArquivos.setFilename(arquivos[i].getName());
                System.out.println(" " + ediArquivos.getFilename());
                String dt_ateracao = formatData.format(new Date(arquivos[i].lastModified()));
                ediArquivos.setLastmodified(dt_ateracao);
                ediArquivos.setLastdate(new Date(arquivos[i].lastModified()));
                ediArquivos.setFileread("Arquivo processado no dia ");

                lstEdiFile.add(ediArquivos);

            }
        }

        return lstEdiFile;
    }

    public void carregarTabelaFiles(String diretorio) throws SQLException, Exception {
        redColunastab();
        lstEdiArquivos = listar(diretorio);
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableXml.getModel();
        modeloCarga.setNumRows(0);
        for (int i = 0; i < lstEdiArquivos.size(); i++) {
            Object[] linha = new Object[5];
            linha[0] = i + 1;
            linha[1] = lstEdiArquivos.get(i).getFilename();
            linha[2] = lstEdiArquivos.get(i).getLastmodified();
            linha[3] = lstEdiArquivos.get(i).getFileread();
            linha[4] = lstEdiArquivos.get(i).getArqimp();
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

        //empresa
        jTableXml.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTableXml.setRowHeight(42);
        jTableXml.setIntercellSpacing(new Dimension(1, 1));
        jTableXml.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableXml.setAutoCreateRowSorter(true);

    }

    public void setRecebePalavraEmail(CRMContaManutencao veioInput) throws Exception {
        this.veioCampo = veioInput;

    }

    public void setRecebeArquivos(CRMClientesAtendimento veioInput, String diretorio, String codigo, String nome) throws Exception {
        this.veioCampoAtendimento = veioInput;
        if (veioInput != null) {
            txtCodigo.setText(codigo);
            txtNome.setText(nome);

            carregarTabelaFiles(diretorio);
        }
    }

    private String arquivo;

    public final void baixarLaudoCliente() {
        Object[] options = {" Sim ", " Não "};
        if (JOptionPane.showOptionDialog(this, "Deseja visualizar o arquivo? " + arquivo, "Aviso:",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null,
                options, options[1]) == JOptionPane.YES_OPTION) {

            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            try {
               
                desktop.open(new File(arquivo));

            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, e1);
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(null, "Arquivo não encontrado");
            } catch (Error e) {
                JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);

            }
        } else {
            JOptionPane.showMessageDialog(null, "Impresssão cancelada");
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
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableXml = new javax.swing.JTable();
        txtNome = new org.openswing.swing.client.TextControl();
        txtCodigo = new org.openswing.swing.client.TextControl();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        jButton1.setText("Selecionar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTableXml.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sequencia", "Arquivo", "Data envio", "Situação", "Arquivo Lido"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableXml.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableXmlMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableXml);
        if (jTableXml.getColumnModel().getColumnCount() > 0) {
            jTableXml.getColumnModel().getColumn(0).setMinWidth(100);
            jTableXml.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableXml.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableXml.getColumnModel().getColumn(2).setMinWidth(100);
            jTableXml.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableXml.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableXml.getColumnModel().getColumn(3).setMinWidth(0);
            jTableXml.getColumnModel().getColumn(3).setPreferredWidth(0);
            jTableXml.getColumnModel().getColumn(3).setMaxWidth(0);
            jTableXml.getColumnModel().getColumn(4).setMinWidth(0);
            jTableXml.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTableXml.getColumnModel().getColumn(4).setMaxWidth(0);
        }

        txtNome.setEnabled(false);
        txtNome.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtCodigo.setEnabled(false);
        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String emailselecionado;
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        baixarLaudoCliente();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTableXmlMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableXmlMouseClicked
        int linhaSelSit = jTableXml.getSelectedRow();
        int colunaSelSit = jTableXml.getSelectedColumn();
        arquivo = "X:\\ERBS\\CRM\\CLIENTE\\" + txtCodigo.getText() + "\\" + jTableXml.getValueAt(linhaSelSit, 1).toString();

    }//GEN-LAST:event_jTableXmlMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableXml;
    private org.openswing.swing.client.TextControl txtCodigo;
    private org.openswing.swing.client.TextControl txtNome;
    // End of variables declaration//GEN-END:variables
}
