/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.dao.MinutaDAO;
import br.com.sgi.dao.MinutaPedidoDAO;
import br.com.sgi.frame.faturamento.pnPedidoPadrao;
import br.com.sgi.util.UtilDatas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class frmMinutasFaturar extends InternalFrame {

    private Minuta minuta;
    private List<MinutaPedido> listminutaPedido = new ArrayList<MinutaPedido>();
    private MinutaPedidoDAO minutaPedidoDAO;

    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;
    private pnPedidoPadrao veioCampo;

    public frmMinutasFaturar() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Minuta de faturamento"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (minutaPedidoDAO == null) {
                minutaPedidoDAO = new MinutaPedidoDAO();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void setRecebePalavra(pnPedidoPadrao veioInput,
            String minuta) throws Exception {

        this.veioCampo = veioInput;
        if (!minuta.isEmpty()) {
            lbNumMesa.setText(minuta);
            getListarMinuta(" minuta ", " and usu_codlan = " + lbNumMesa.getText());

        }

    }

    public void getListarMinuta(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listminutaPedido = this.minutaPedidoDAO.getMinutaPedidos(PESQUISA_POR, PESQUISA);
        if (listminutaPedido != null) {
            carregarTabelaMinutaNota();
        }
    }

    public void carregarTabelaMinutaNota() throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon MedIcon = getImage("/images/cadeado.png");
        ImageIcon ReaIcon = getImage("/images/sitAnd.png");

        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon GarIcon = getImage("/images/ruby_delete.png");
        ImageIcon MktIcon = getImage("/images/bateriaindu.png");

        ImageIcon NotIcon = getImage("/images/Nota.png");
        ImageIcon RomIcon = getImage("/images/NotaRomaneio.png");
        ImageIcon NotGarIcon = getImage("/images/NotaGarantia.png");
        for (MinutaPedido mp : listminutaPedido) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            frmMinutasFaturar.JTableRenderer renderers = new frmMinutasFaturar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            linha[0] = BomIcon;
            if (mp.getUsu_numpfa() == 0) {
                linha[0] = MedIcon;
            }

            linha[1] = mp.getUsu_numped();
            linha[2] = mp.getUsu_numpfa();
            linha[3] = mp.getUsu_numana();
            linha[4] = true;
            linha[5] = mp.getUsu_tnspro();
            columnModel.getColumn(6).setCellRenderer(renderers);
            linha[6] = NotIcon;
            if (mp.getTipodocumento().equals("R")) {
                linha[6] = RomIcon;
            }
            if (mp.getTipodocumento().equals("NG")) {
                linha[6] = NotGarIcon;
            }
            linha[7] = mp.getUsu_codcli();
            linha[8] = mp.getCadCliente().getNome();
            linha[9] = mp.getCadCliente().getEstado();
            linha[10] = mp.getCadCliente().getCidade();

            linha[11] = mp.getEmissaoS(); // data da pré
            columnModel.getColumn(14).setCellRenderer(renderers);
            linha[14] = AutIcon;
            if (mp.getUsu_codori().equals("BM")) {
                linha[14] = MotIcon;
            }
            if (mp.getUsu_codori().equals("GAR")) {
                linha[14] = GarIcon;
            }
            if (mp.getUsu_codori().equals("MKT")) {
                linha[14] = MktIcon;
            }
            linha[15] = mp.getUsu_sitmin();
            linha[16] = mp.getUsu_pesped();
            linha[17] = mp.getUsu_qtdped();
            linha[19] = mp.getUsu_codemp();
            linha[20] = mp.getUsu_codfil();

            linha[21] = mp.getEmissaoS();
            linha[12] = 0;
            linha[23] = 0;
            linha[24] = mp.getUsu_codtra() + " - ";
            linha[25] = mp.getUsu_seqite();

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

        jTableCarga.setRowHeight(40);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(3).setCellRenderer(direita);
        // jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(11).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(15).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(16).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(17).setCellRenderer(direita);

        jTableCarga.getColumnModel().getColumn(20).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(21).setCellRenderer(direita);

        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoCreateRowSorter(true);
        // jTableCarga.setAutoResizeMode(0);
        jTableCarga.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

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
        Header = new javax.swing.JPanel();
        lbSair = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lbNumMesa = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        Header2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        lblPeso = new javax.swing.JLabel();
        lblQtdy = new javax.swing.JLabel();
        lblQtdyPedidoPreFaturar = new javax.swing.JLabel();
        lblQtdyPrePreparacao = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        Header.setBackground(new java.awt.Color(170, 169, 149));
        Header.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lbSair.setToolTipText("");
        lbSair.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbSairMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        jLabel1.setText("Minuta");

        lbNumMesa.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        lbNumMesa.setText("0000");

        javax.swing.GroupLayout HeaderLayout = new javax.swing.GroupLayout(Header);
        Header.setLayout(HeaderLayout);
        HeaderLayout.setHorizontalGroup(
            HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbNumMesa)
                .addGap(0, 0, 0)
                .addComponent(jLabel12)
                .addGap(4, 4, 4)
                .addComponent(lbSair)
                .addGap(16, 16, 16))
        );
        HeaderLayout.setVerticalGroup(
            HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(lbNumMesa))
                    .addComponent(lbSair))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeaderLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel12))
        );

        Header2.setBackground(new java.awt.Color(170, 169, 149));
        Header2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel4.setText("Itens da Mesa:");

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Pedido", "Pré_fatura", "Analise", "Gerar", "Transação", "#", "Cliente", "Nome", "UF", "Cidade", "Data Pré", "Transporte", "Data Fat", "#", "Situação", "Peso", "Quantidade", "Situação", "Empresa", "Filial", "Emissão", "Agendado", "Dias Atrazo", "Transportadora", "ID", "SitNfv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(19).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(19).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(19).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(24).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(24).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(24).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(25).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(25).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(25).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(26).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(26).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(26).setMaxWidth(0);
        }

        lblPeso.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPeso.setForeground(new java.awt.Color(51, 102, 255));
        lblPeso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPeso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_retorno.png"))); // NOI18N
        lblPeso.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso"));
        lblPeso.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblPeso.setOpaque(true);
        lblPeso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPesoMouseClicked(evt);
            }
        });

        lblQtdy.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdy.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio.png"))); // NOI18N
        lblQtdy.setBorder(javax.swing.BorderFactory.createTitledBorder("Quantidade Baterias"));
        lblQtdy.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdy.setOpaque(true);

        lblQtdyPedidoPreFaturar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdyPedidoPreFaturar.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdyPedidoPreFaturar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdyPedidoPreFaturar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio_manual.png"))); // NOI18N
        lblQtdyPedidoPreFaturar.setBorder(javax.swing.BorderFactory.createTitledBorder("Pré-Faturas - Faturar"));
        lblQtdyPedidoPreFaturar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyPedidoPreFaturar.setOpaque(true);

        lblQtdyPrePreparacao.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdyPrePreparacao.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdyPrePreparacao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdyPrePreparacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio_manual.png"))); // NOI18N
        lblQtdyPrePreparacao.setBorder(javax.swing.BorderFactory.createTitledBorder("Pré-Faturas - Preparação"));
        lblQtdyPrePreparacao.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyPrePreparacao.setOpaque(true);

        javax.swing.GroupLayout Header2Layout = new javax.swing.GroupLayout(Header2);
        Header2.setLayout(Header2Layout);
        Header2Layout.setHorizontalGroup(
            Header2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Header2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(Header2Layout.createSequentialGroup()
                .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQtdyPedidoPreFaturar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQtdyPrePreparacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane3)
        );
        Header2Layout.setVerticalGroup(
            Header2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Header2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Header2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQtdy, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQtdyPedidoPreFaturar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQtdyPrePreparacao, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        Header2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblPeso, lblQtdy, lblQtdyPedidoPreFaturar, lblQtdyPrePreparacao});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Header2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Header2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbSairMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbSairMouseClicked
        this.setVisible(false);
    }//GEN-LAST:event_lbSairMouseClicked

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
//        int linhaSelSit = jTableCarga.getSelectedRow();
//        int colunaSelSit = jTableCarga.getSelectedColumn();
//
//        txtSeqMin.setText(jTableCarga.getValueAt(linhaSelSit, 25).toString());
//        if (!txtSeqMin.getText().isEmpty()) {
//            try {
//                if (evt.getClickCount() == 2 && PROCESSO.equals("MINUTA")) {
//                    minutapedido = new MinutaPedido();
//                    minutapedido = minutaPedidoDAO.getMinutaPedido(" seq ", "and usu_seqite = " + txtSeqMin.getText().trim());
//                    if (minutapedido != null) {
//                        if (minutapedido.getUsu_seqite() > 0) {
//                            alterarDados();
//                        }
//                    }
//                }
//
//            } catch (SQLException ex) {
//                Logger.getLogger(frmMinutasExpedicaoGerar.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

    }//GEN-LAST:event_jTableCargaMouseClicked

    private void lblPesoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPesoMouseClicked

    }//GEN-LAST:event_lblPesoMouseClicked

    private String codigolancamento;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Header;
    private javax.swing.JPanel Header2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lbNumMesa;
    private javax.swing.JLabel lbSair;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblQtdy;
    private javax.swing.JLabel lblQtdyPedidoPreFaturar;
    private javax.swing.JLabel lblQtdyPrePreparacao;
    // End of variables declaration//GEN-END:variables
}
