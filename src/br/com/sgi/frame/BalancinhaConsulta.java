/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Balanca;
import br.com.sgi.bean.BalancaLancamento;
import br.com.sgi.bean.BalancaParametro;
import br.com.sgi.dao.BalancaDAO;
import br.com.sgi.dao.BalancaLancamentoDAO;
import br.com.sgi.dao.BalancaParametroDAO;

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
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;
import static br.com.sgi.util.ConversaoHoras.converterMinutosHora;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.UtilDatas;
import java.text.DecimalFormat;

/**
 *
 * @author jairosilva
 */
//testematheus 
public final class BalancinhaConsulta extends InternalFrame {

    private BalancaLancamento balancaLancamento;
    private List<BalancaLancamento> listPesos = new ArrayList<BalancaLancamento>();

    private BalancaLancamentoDAO balancaLancamentoDAO;
    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;

    private static final Color COR_CON = new Color(102, 255, 102);
    private static final Color COR_CON_PRO = new Color(255, 0, 0);
    private static final Color COR_CON_PEN = new Color(51, 51, 255);

    private String PESQUISA;
    private String PESQUISA_POR;

    private String PROCESSO;
    private String COMPLEMENTO;
    private String codigoBalanca;
    private String descricaoBalanca;
    private boolean AddRegEtq;
    private String codigolancamento;

    FormatarNumeros formatarNumeros = new FormatarNumeros();
    
    public BalancinhaConsulta() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Gestão de Pesos da Fabrica"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (balancaLancamentoDAO == null) {
                balancaLancamentoDAO = new BalancaLancamentoDAO();
            }

            txtDatIni.setDate(this.utilDatas.retornaDataIni(new Date()));
            txtDatFim.setDate(new Date());
            pegarDataDigitada();
            preencherComboBalanca(0);
            getListar("", "");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }
    

    public void preencherComboBalanca(Integer id) throws SQLException {
        BalancaParametroDAO balancaParametroDAO = new BalancaParametroDAO();
        BalancaParametro balancaParametro = new BalancaParametro();
        String nomeBalanca;
        try {
            balancaParametro = balancaParametroDAO.getBalancaParametro();
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }
        nomeBalanca = balancaParametro.getNomeBalanca();

        BalancaDAO balancaDao = new BalancaDAO();

        List<Balanca> listBalanca = new ArrayList<Balanca>();
        String desger;
        comboBalanca.removeAllItems();
        if (nomeBalanca.equals("")) {
            listBalanca = balancaDao.getBalancas("", "");
        } else {
            listBalanca = balancaDao.getBalancas("", " AND CODEMP = 1 AND CODFIL = 1  and DesBal ='" + nomeBalanca + "' ");
        }
        if (listBalanca != null) {
            for (Balanca balanca : listBalanca) {
                codigoBalanca = balanca.getCodigoBalanca().toString();
                descricaoBalanca = balanca.getNomeBalanca();
                desger = codigoBalanca + " - " + descricaoBalanca;
                comboBalanca.addItem(desger);
            }
        }

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
    }

    private void getPesos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listPesos = this.balancaLancamentoDAO.getBalancaLancamentosAgrupados(PESQUISA_POR, PESQUISA);
        if (listPesos != null) {
            carregarTabela();
        }
    }

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        pegarDataDigitada();
        String codigo = comboBalanca.getSelectedItem().toString();

        if (!codigo.equals("")) {
            int index = codigo.indexOf("-");
            String codigoSelecao = codigo.substring(0, index);

            PESQUISA += " AND USU_CODFIL = 1 AND USU_CODBAL = " + codigoSelecao + " AND USU_DATLAN BETWEEN '" + datIni + "' AND '" + datFim + "' ";

            listPesos = this.balancaLancamentoDAO.getBalancaLancamentosAgrupados(PESQUISA_POR, PESQUISA);
            if (listPesos != null) {
                carregarTabela();   

            }
        }

    }
    
    public void RetornarCampo() throws Exception {
       
        getListar("", "");
      }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon ProvIcon = getImage("/images/sitMedio.png");
        String data = null;
        lblSomaPeso.setText("SOMA PESO: 0.00");
        double pesoBal = 0.0;
        double pesoLiq = 0.0;
        String mostrarPeso = "";
        for (BalancaLancamento mi : listPesos) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new SucataEmbarque.ColorirRenderer();
            BalancinhaConsulta.JTableRenderer renderers = new BalancinhaConsulta.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            mostrarPeso = mi.getMostrarPeso();

            if (mostrarPeso.equals("MOSTRAR")) {
                linha[0] = BomIcon;
                pesoBal += mi.getPesoBalanca();
                pesoLiq += mi.getPesoLiquido();
            } else if (mostrarPeso.equals("PESODIF")) {
                linha[0] = ProvIcon ;
                pesoBal += mi.getPesoBalanca();
                pesoLiq += mi.getPesoLiquido();
            } else {
                linha[0] = RuimIcon;
            }

            linha[1] = mi.getCodigoAgrupamento();
            linha[2] = mi.getCodigoBalanca()  + " - " + mi.getBalanca().getNomeBalanca();
            linha[3] = mi.getCodigoProduto() + " - " + mi.getProduto().getDescricaoproduto();

            if (mostrarPeso.equals("MOSTRAR")) {
                linha[4] = mi.getPesoBalanca() + " KG";
            } else if  (mostrarPeso.equals("PESODIF")) {
                linha[4] = mi.getPesoBalanca() + " KG";
            } else {
                linha[4] = 0.0 + " KG";
            }
            if (mostrarPeso.equals("MOSTRAR")) {
                linha[5] = mi.getPesoLiquido() + " KG";
            }else if (mostrarPeso.equals("PESODIF")) {
                linha[5] = mi.getPesoLiquido() + " KG";
            }  else {
                linha[5] = 0.0 + " KG";
            }         
          
            linha[6] = mi.getCodigoEmbalagem() + " -" + mi.getEmbalagem().getDescricaoEmbalagem();
            linha[7] = this.utilDatas.converterDateToStr(mi.getDataLancamento());
            linha[8] = converterMinutosHora(mi.getHoraLancamento());
            linha[9] = mi.getCodigoFuncionario() + " - " + mi.getNomeFuncionario();

            modeloCarga.addRow(linha);
        }
               
        lblSomaPeso.setText("SOMA PESO: " + formatarNumeros.FormatarDouble(pesoBal) + " KG");
        lblSomaPesoLiquido.setText("SOMA PESO LIQUIDO: " + formatarNumeros.FormatarDouble(pesoLiq) + " KG");
    }


    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(5).setCellRenderer(direita);
      //  jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.setRowHeight(27);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //  jTableCarga.setAutoResizeMode(0);

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
        //
    }

    void carregarProcessoColeta(String codigoProduto, String nomeProduto) throws Exception {
        if (!codigoProduto.isEmpty()) {
            getListar("PRODUTO", " and usu_codpro = '" + codigoProduto + "' ");
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar = new javax.swing.JButton();
        btnFinalizar = new javax.swing.JButton();
        comboBalanca = new javax.swing.JComboBox<>();
        btnFiltrar3 = new javax.swing.JButton();
        btnPesPro = new javax.swing.JButton();
        lblSomaPeso = new javax.swing.JLabel();
        lblSomaPesoLiquido = new javax.swing.JLabel();
        btnPesoChumbo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "CODIGO AGRUPADOR", "BALANCA", "PRODUTO", "PESO BALANÇA", "PESO LIQUIDO", "EMBALAGEM", "DATA", "HORARIO", "FUNCIONARIO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(550);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(550);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(550);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(100);
        }

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnFinalizar.setText("Finalizar");
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });

        comboBalanca.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 - Selecionar Balança", "" }));
        comboBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBalancaActionPerformed(evt);
            }
        });

        btnFiltrar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-balança-industrial-16.png"))); // NOI18N
        btnFiltrar3.setText("Pesar Refugo e Retalho");
        btnFiltrar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar3ActionPerformed(evt);
            }
        });

        btnPesPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/book.png"))); // NOI18N
        btnPesPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesProActionPerformed(evt);
            }
        });

        lblSomaPeso.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblSomaPeso.setForeground(new java.awt.Color(51, 102, 255));
        lblSomaPeso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSomaPeso.setText("10000");
        lblSomaPeso.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lblSomaPeso.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblSomaPeso.setOpaque(true);

        lblSomaPesoLiquido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblSomaPesoLiquido.setForeground(new java.awt.Color(51, 102, 255));
        lblSomaPesoLiquido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSomaPesoLiquido.setText("1000");
        lblSomaPesoLiquido.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lblSomaPesoLiquido.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblSomaPesoLiquido.setOpaque(true);

        btnPesoChumbo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-balança-industrial-16.png"))); // NOI18N
        btnPesoChumbo.setText("Pesar Chumbo");
        btnPesoChumbo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesoChumboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(comboBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesPro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltrar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnFiltrar3, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnPesoChumbo, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblSomaPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSomaPesoLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(btnFinalizar)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesPro)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSomaPesoLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFiltrar3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSomaPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPesoChumbo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Balança", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1246, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   
    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();
        codigolancamento = jTableCarga.getValueAt(linhaSelSit, 1).toString();

      

    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        try {
            getListar("", "");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        this.dispose();

    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void btnFiltrar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar3ActionPerformed
        try {
            BalancinhaFabrica sol = new BalancinhaFabrica();
            MDIFrame.add(sol);
            sol.setMaximum(true); // executa maximizado 
            sol.setPosicao();
            sol.setRecebePalavra(this);
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar3ActionPerformed

    private void comboBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBalancaActionPerformed
        try {
            getListar("", "");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_comboBalancaActionPerformed

    private void btnPesProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesProActionPerformed
        try {
            IntegrarProduto sol = new IntegrarProduto();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado 
            //  sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavraBalancinha(this, this.COMPLEMENTO);

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPesProActionPerformed

    private void btnPesoChumboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesoChumboActionPerformed
         try {
            BalancinhaFabricachumbo sol = new BalancinhaFabricachumbo();
            MDIFrame.add(sol);
            sol.setMaximum(true); // executa maximizado 
            sol.setPosicao();
            sol.setRecebePalavra(this);
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_btnPesoChumboActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar3;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JButton btnPesPro;
    private javax.swing.JButton btnPesoChumbo;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> comboBalanca;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lblSomaPeso;
    private javax.swing.JLabel lblSomaPesoLiquido;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    // End of variables declaration//GEN-END:variables
}
