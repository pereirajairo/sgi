/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.Representante;
import br.com.sgi.dao.MinutaDAO;
import br.com.sgi.dao.RepresentanteDAO;
import br.com.sgi.util.UtilDatas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class frmMinutas extends InternalFrame {

    private Minuta minuta;
    private List<Minuta> listMinuta = new ArrayList<Minuta>();
    private MinutaDAO minutaDAO;

    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;

    public frmMinutas() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Minuta embarque"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (minutaDAO == null) {
                minutaDAO = new MinutaDAO();
            }
            preencherComboHub(0);
            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(this.utilDatas.retornaDataIni(new Date())));
            txtDatFim.setDate(this.utilDatas.retornaDataFim(new Date()));
            pegarDataDigitada();
            pesquisarPorData("", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void preencherComboHub(Integer id) throws SQLException, Exception {
        RepresentanteDAO dao = new RepresentanteDAO();
        List<Representante> listRepresentante = new ArrayList<Representante>();
        String cod;
        String des;
        String desger;
        txtHub.setSelectedItem("TODOS");
        listRepresentante = dao.getRepresentantesHub("", " and usu_codhub >0 ");

        if (listRepresentante != null) {
            for (Representante rep : listRepresentante) {
                cod = rep.getCodigoHub();
                des = rep.getNomeHub();
                desger = cod + " - " + des;
                txtHub.addItem(desger);
            }
        }
    }

    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());

    }

    private void pesquisarPorData(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        PESQUISA_POR = "DATA";
        PESQUISA = " and usu_orimin = 'HUB'";
        getMinutas(PESQUISA_POR, PESQUISA);
    }

    private void getMinuta(String codigominuta) throws SQLException {
        minuta = new Minuta();
        minuta = minutaDAO.getMinuta("", "and usu_codlan = " + codigominuta);
        btnManutencao.setEnabled(false);
        if (minuta != null) {
            if (minuta.getUsu_codlan() > 0) {
                btnManutencao.setEnabled(true);
            }
        }
    }

    private void getMinutas(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listMinuta = this.minutaDAO.getMinutas(PESQUISA_POR, PESQUISA, "N");
        if (listMinuta != null) {
            carregarTabela();
        }
    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        for (Minuta mi : listMinuta) {
            Object[] linha = new Object[15];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            TableCellRenderer renderer = new frmMinutas.ColorirRenderer();
            frmMinutas.JTableRenderer renderers = new frmMinutas.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = RuimIcon;
            if (mi.getUsu_sitmin().equals("LIBERADA")) {
                linha[0] = BomIcon;
            }

            linha[1] = mi.getUsu_codlan();
            linha[2] = mi.getUsu_codtra();
            linha[3] = mi.getCadTransportadora().getNomeTransportadora();
            linha[4] = this.utilDatas.converterDateToStr(mi.getUsu_datemi());
            linha[5] = mi.getUsu_pesfat();
            linha[6] = mi.getUsu_qtdfat();
            linha[7] = mi.getUsu_qtdvol();
            jTableCarga.getColumnModel().getColumn(8).setCellRenderer(renderer);
            linha[8] = mi.getUsu_sitmin();
            // linha[9] = mi.getEnviaremail();

            linha[10] = mi.getUsu_codemp();
            linha[11] = mi.getUsu_codfil();
            linha[12] = mi.getUsu_nommin();

            modeloCarga.addRow(linha);
        }
    }

    public void retornarMinuta() {
        try {
            pesquisarPorData("", "");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    private boolean AddRegEtq;

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);

        jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCarga.setRowHeight(35);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //jTableCarga.setAutoResizeMode(0);

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

    private void novoRegistro(String PROCESSO, String string) throws PropertyVetoException, Exception {
        LogMinutaEmbarqueNota sol = new LogMinutaEmbarqueNota();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 
        Minuta car = new Minuta();
        sol.setRecebePalavra(this, PROCESSO);
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        optAbe = new javax.swing.JRadioButton();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar = new javax.swing.JButton();
        btnFiltrar1 = new javax.swing.JButton();
        optDat = new javax.swing.JRadioButton();
        btnManutencao = new javax.swing.JButton();
        btnFiltrar4 = new javax.swing.JButton();
        optExc = new javax.swing.JRadioButton();
        txtHub = new javax.swing.JComboBox<>();
        btnHoras = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "MUNUTA", "TRANSPORTADORA", "NOME", "DATA", "PESO", "QTDY", "VOLUME", "SITUAÇÃO", "EMAIL", "EMPRESA", "FILIAL", "MINUTA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

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
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(150);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(0);
        }

        buttonGroup1.add(optAbe);
        optAbe.setSelected(true);
        optAbe.setText("Todas");
        optAbe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optAbeActionPerformed(evt);
            }
        });

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnFiltrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnFiltrar1.setText("Minuta Moto");
        btnFiltrar1.setEnabled(false);
        btnFiltrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(optDat);
        optDat.setText("Concluida");
        optDat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optDatActionPerformed(evt);
            }
        });

        btnManutencao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/book.png"))); // NOI18N
        btnManutencao.setText("Finalizar");
        btnManutencao.setEnabled(false);
        btnManutencao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManutencaoActionPerformed(evt);
            }
        });

        btnFiltrar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        btnFiltrar4.setText("Minuta Auto");
        btnFiltrar4.setEnabled(false);
        btnFiltrar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar4ActionPerformed(evt);
            }
        });

        buttonGroup1.add(optExc);
        optExc.setText("Excluidos");
        optExc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optExcActionPerformed(evt);
            }
        });

        txtHub.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtHub.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtHub.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtHubMouseClicked(evt);
            }
        });
        txtHub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHubActionPerformed(evt);
            }
        });

        btnHoras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        btnHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHorasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(optAbe)
                .addGap(4, 4, 4)
                .addComponent(optExc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optDat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(btnFiltrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHub, 0, 220, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(btnFiltrar1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFiltrar4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnManutencao))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar)
                    .addComponent(txtHub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(optAbe)
                    .addComponent(optExc)
                    .addComponent(optDat)
                    .addComponent(btnHoras))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFiltrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar, optAbe, optDat, optExc, txtDatFim, txtDatIni, txtHub});

        jTabbedPane1.addTab("Minutas", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String codigolancamento;
    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();
        if (evt.getClickCount() == 2) {
            try {
                codigolancamento = jTableCarga.getValueAt(linhaSelSit, 1).toString();
                btnManutencao.setEnabled(false);
                getMinuta(codigolancamento);
            } catch (SQLException ex) {
                Logger.getLogger(frmMinutas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void optAbeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optAbeActionPerformed
        try {
            pegarDataDigitada();
            String sql = "and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "' and usu_sitmin not in ('EXCLUIDA')  and usu_orimin = 'HUB'";
            getMinutas("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optAbeActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        try {
            pegarDataDigitada();
            String sql = "and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "'  and usu_orimin = 'HUB'";
            getMinutas("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnFiltrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar1ActionPerformed

        try {
            novoRegistro("MOTO", "");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(frmMinutas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutas.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_btnFiltrar1ActionPerformed

    private void btnManutencaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManutencaoActionPerformed
        try {
            frmMinutasGerar sol = new frmMinutasGerar();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado
            //  sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavraManutencao(this, this.minuta);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnManutencaoActionPerformed

    private void btnFiltrar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar4ActionPerformed
        try {
            novoRegistro("MOTO", "");
        } catch (PropertyVetoException ex) {
            Logger.getLogger(frmMinutas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar4ActionPerformed

    private void optExcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optExcActionPerformed
        try {
            pegarDataDigitada();
            String sql = "and usu_datemi >= '" + datIni + "' and usu_datemi <='" + datFim + "' and usu_sitmin = 'EXCLUIDA'  and usu_orimin = 'HUB'";
            getMinutas("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optExcActionPerformed

    private void optDatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optDatActionPerformed
        try {
            pesquisarPorData("", "");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optDatActionPerformed

    private void btnHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHorasActionPerformed

        try {
            if (txtHub.getSelectedIndex() != -1) {
                if (!txtHub.getSelectedItem().equals("TODOS")) {
                    String cod = txtHub.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);
                    pegarDataDigitada();
                    String sql = " and usu_nommun = '" + txtHub.getSelectedItem().toString() + "'"
                            + " and usu_datemi >= '" + datIni + "' "
                            + "and usu_datemi <='" + datFim + "' "
                            + " and usu_orimin = 'HUB'";
                    getMinutas("SIT", sql);

                } else {

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(frmMinutasHubGerar.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnHorasActionPerformed

    private void txtHubMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtHubMouseClicked
        //

    }//GEN-LAST:event_txtHubMouseClicked

    private void txtHubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHubActionPerformed
        try {
            if (txtHub.getSelectedIndex() != -1) {
                if (!txtHub.getSelectedItem().equals("TODOS")) {
                    String cod = txtHub.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);
                    pegarDataDigitada();
                    String sql = " and usu_nommun = '" + txtHub.getSelectedItem().toString() + "'"
                            + " and usu_datemi >= '" + datIni + "' "
                            + "and usu_datemi <='" + datFim + "' "
                            + " and usu_orimin = 'HUB'";
                    getMinutas("SIT", sql);

                } else {

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(frmMinutasHubGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtHubActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar1;
    private javax.swing.JButton btnFiltrar4;
    private javax.swing.JButton btnHoras;
    private javax.swing.JButton btnManutencao;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JRadioButton optAbe;
    private javax.swing.JRadioButton optDat;
    private javax.swing.JRadioButton optExc;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtHub;
    // End of variables declaration//GEN-END:variables
}
