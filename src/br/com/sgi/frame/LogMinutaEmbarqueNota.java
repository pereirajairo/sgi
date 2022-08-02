/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.dao.NotaFiscalDAO;
import br.com.sgi.dao.TransportadoraDAO;
import br.com.sgi.util.UtilDatas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
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

/**
 *
 * @author jairosilva
 */
public final class LogMinutaEmbarqueNota extends InternalFrame {

    private NotaFiscalDAO notafiscalDAO;
    private List<NotaFiscal> lstNotaFiscal = new ArrayList<NotaFiscal>();

    private frmMinutas veioCampo;
    private UtilDatas utilDatas;

    private String datI;
    private String datF;

    public LogMinutaEmbarqueNota() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("T003 - Etiquetas "));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (notafiscalDAO == null) {
                this.notafiscalDAO = new NotaFiscalDAO();
            }

            limpatela();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void novoRegistro(String PROCESSO, String NOTAS) throws PropertyVetoException, Exception {
        frmMinutasGerar sol = new frmMinutasGerar();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 
        sol.setRecebePalavra(this, PROCESSO, NOTAS,
                txtTransportadora.getText(),
                txtTransportadora1.getText(),
                this.datI,
                this.datF,
                txtEmpresa.getText(),
                txtFilial.getText());
    }

    public void getListar(String PESQUISA_POR, String PESQUISA, boolean selecionar) throws SQLException, Exception {
        if (PESQUISA_POR.equals("NFV")) {
            lstNotaFiscal = this.notafiscalDAO.getNotaFiscais(PESQUISA_POR, PESQUISA);
            if (lstNotaFiscal != null) {
                carregarTabela(selecionar);
                TotalizarPesos();
                btnEdit.setEnabled(false);
                if (lstNotaFiscal.size() > 0) {
                    if (!txtNotasSelecionadas.getText().isEmpty()) {
                        btnEdit.setEnabled(true);
                    }

                }
            }
        } else {

        }
    }

    public void carregarTabela(boolean selecionar) throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableGeral.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        for (NotaFiscal nf : lstNotaFiscal) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableGeral.getColumnModel();
            LogMinutaEmbarqueNota.JTableRenderer renderers = new LogMinutaEmbarqueNota.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            // popula as colunas
            linha[0] = BomIcon;
            linha[1] = nf.getNotafiscal();
            linha[2] = nf.getEmissaoS();
            linha[3] = nf.getPesoLiquido();
            linha[4] = selecionar;

            linha[5] = nf.getCodigocliente();
            linha[6] = nf.getCliente().getNome();
            linha[7] = nf.getCliente().getCidade();
            linha[8] = nf.getCliente().getEstado();

            linha[9] = nf.getTransportadora();
            linha[10] = nf.getNomeTransportadora();
            linha[11] = nf.getPedido();
            linha[12] = nf.getEmpresa();
            linha[13] = nf.getFilial();
            linha[14] = nf.getTransacao();
            linha[15] = nf.getQuantidade();
            linha[16] = nf.getQuantidadevolume();

            modeloCarga.addRow(linha);
        }

    }

    private void TotalizarPesos() {
        Double pesoTotal = 0.0;
        Double volume = 0.0;
        Double quantidade = 0.0;
        for (NotaFiscal nf : lstNotaFiscal) {
            pesoTotal += nf.getPesoLiquido();
            volume += nf.getQuantidadevolume();
            quantidade += nf.getQuantidade();
        }
        txtPeso.setValue(pesoTotal);
        txtPesoSelecionado.setValue(pesoTotal);
        txtVolume.setValue(volume);
        txtQuantidade.setValue(quantidade);
    }

    public void selecionarRange() throws SQLException, Exception {
        txtPesoSelecionado.setValue(0);
        Double pesoSelecionado = 0.0;
        String selecionar = "";
        if (jTableGeral.getRowCount() > 0) {
            for (int i = 0; i < jTableGeral.getRowCount(); i++) {
                if ((Boolean) jTableGeral.getValueAt(i, 4)) {
                    selecionar += (jTableGeral.getValueAt(i, 1).toString() + ",");
                    pesoSelecionado += Double.valueOf(jTableGeral.getValueAt(i, 3).toString());
                }
            }
            int tam = selecionar.length();
            selecionar = selecionar.substring(0, tam - 1);
            txtNotasSelecionadas.setValue(selecionar);
            txtPesoSelecionado.setValue(pesoSelecionado);
            btnSel.setEnabled(false);
            if (pesoSelecionado > 0) {
                btnSel.setEnabled(true);
            }
        }
    }

    public void setRecebePalavra(frmMinutas veioInput, String TIPO_MINUTA) throws Exception {
        this.veioCampo = veioInput;

        txtPeso.setValue(0);
        txtPesoSelecionado.setValue(0);
        txtEmpresa.requestFocus();
    }

    public void retornarMinuta() throws SQLException {
        pesquisarTransportadora(txtTransportadora.getText(), txtEmpresa.getText(), txtFilial.getText());
    }

    private void sair() {
        if (veioCampo != null) {
            try {
                veioCampo.retornarMinuta();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }
    }

    private void pegarDataDigitada() throws ParseException {
        datI = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datF = this.utilDatas.converterDateToStr(txtDatFim.getDate());
    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableGeral.setRowHeight(40);
        jTableGeral.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableGeral.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableGeral.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableGeral.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableGeral.setAutoCreateRowSorter(true);
        jTableGeral.setAutoResizeMode(0);

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void limpatela() throws ParseException, Exception {

        txtEmpresa.setText("");
        txtNota.setText("");
        txtTransportadora.setText("");

        txtDatIni.setDate(this.utilDatas.retornaDataIni(new Date()));
        txtDatFim.setDate(this.utilDatas.retornaDataFim(new Date()));
        txtDatEmb.setDate(new Date());
        txtHorEmb.setText(utilDatas.retornarHoras(new Date()));
        txtEmpresa.requestFocus();
    }

    private void pesquisarNota(String nota) {

        if (!nota.isEmpty()) {
            try {
                pegarDataDigitada();
            } catch (ParseException ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                String sql = " and nfv.codemp >0 and nfv.codfil >0 and nfv.numnfv in (" + nota + ")";

                getListar("NFV", sql, false);
            } catch (Exception ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void pesquisarNotaGeral(String nota) {

        if (!txtTransportadora.getText().isEmpty()) {
            if (!nota.isEmpty()) {
                try {
                    pegarDataDigitada();
                } catch (ParseException ex) {
                    Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    String sql = " and nfv.usu_germin = 'N' "
                            + " and nfv.codemp =  " + txtEmpresa.getText() + " "
                            + " and nfv.codfil = " + txtFilial.getText() + " "
                            + " and nfv.numnfv in (" + nota + ") "
                            + " and nfv.codtra = " + txtTransportadora.getText();

                    getListar("NFV", sql, true);
                } catch (Exception ex) {
                    Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private void pesquisarClienteNota(String nota) {
        if (!nota.isEmpty()) {
            try {
                pegarDataDigitada();
            } catch (ParseException ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                String sql = " and nfv.usu_germin = 'N' and nfv.codemp = 1 and nfv.codfil = 1 and cli.nomcli like ('%" + nota + "%')";
                getListar("NFV", sql, false);
            } catch (Exception ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void pesquisarClienteCodigoNota(String nota) {
        if (!nota.isEmpty()) {
            try {
                pegarDataDigitada();
            } catch (ParseException ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                String sql = " and nfv.usu_germin = 'N' and nfv.codemp = 1 and nfv.codfil = 1 and nfv.codcli in ('" + nota + "')";
                getListar("NFV", sql, false);
            } catch (Exception ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void retornarTranspotadora(String transportadora, String nometransporadora) throws SQLException {
        txtNotasSelecionadas.setText("");
        txtTransportadora.setText(transportadora);
        txtTransportadora1.setText(nometransporadora);
        pesquisarTransportadora(txtTransportadora.getText(), txtEmpresa.getText(), txtFilial.getText());
    }

    private void pesquisarTransportadora(String nota, String empresa, String filial) throws SQLException {
        if (!nota.isEmpty()) {

            Transportadora tra = new Transportadora();
            TransportadoraDAO dao = new TransportadoraDAO();
            tra = dao.getTransportadora("TRA", " and codtra = " + nota);
            if (tra != null) {
                if (tra.getCodigoTransportadora() > 0) {
                    txtTransportadora1.setText(tra.getNomeTransportadora());
                    try {
                        pegarDataDigitada();
                    } catch (ParseException ex) {
                        Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        String sql = " and usu_germin = 'N'"
                                + "and nfv.datemi >= '" + datI + "' "
                                + "and nfv.datemi<= '" + datF + "'"
                                + "and nfv.codemp  = " + empresa + " \n"
                                + "and nfv.codfil  = " + filial + " \n"
                                + "and nfv.codtra in ('" + nota + "')";
                        getListar("NFV", sql, false);
                    } catch (Exception ex) {
                        Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
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
            if ("NC".equals(str)) {
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

        jTabbedCotacao = new javax.swing.JTabbedPane();
        pnlForm = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtDatEmb = new org.openswing.swing.client.DateControl();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtNota = new org.openswing.swing.client.TextControl();
        txtTransportadora = new org.openswing.swing.client.TextControl();
        jLabel2 = new javax.swing.JLabel();
        txtGestor = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        btnEdit = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedEtq = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableGeral = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        txtHorEmb = new org.openswing.swing.client.TextControl();
        btnHoras = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        btnSel = new javax.swing.JButton();
        txtDatFim = new org.openswing.swing.client.DateControl();
        jLabel6 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        txtEmpresa = new org.openswing.swing.client.TextControl();
        txtPeso = new org.openswing.swing.client.NumericControl();
        txtPesoSelecionado = new org.openswing.swing.client.NumericControl();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtNotasSelecionadas = new org.openswing.swing.client.TextControl();
        txtTransportadora1 = new org.openswing.swing.client.TextControl();
        jButton9 = new javax.swing.JButton();
        txtFilial = new org.openswing.swing.client.TextControl();
        txtVolume = new org.openswing.swing.client.NumericControl();
        jLabel15 = new javax.swing.JLabel();
        txtQuantidade = new org.openswing.swing.client.NumericControl();
        jLabel16 = new javax.swing.JLabel();
        textControl1 = new org.openswing.swing.client.TextControl();
        textControl2 = new org.openswing.swing.client.TextControl();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jTabbedCotacao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedCotacaoMouseClicked(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setForeground(new java.awt.Color(0, 0, 153));

        jLabel4.setText("Data Ini:");

        jLabel11.setText("Data Emb:");

        txtDatEmb.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtDatEmb.setRequired(true);

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNota.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNota.setUpperCase(true);
        txtNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotaActionPerformed(evt);
            }
        });

        txtTransportadora.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTransportadora.setRequired(true);
        txtTransportadora.setUpperCase(true);
        txtTransportadora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransportadoraActionPerformed(evt);
            }
        });

        jLabel2.setText("Hora Emb.");

        txtGestor.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtGestor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MOTO", "AUTO" }));
        txtGestor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGestorActionPerformed(evt);
            }
        });

        jLabel14.setText("Linha");

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnEdit.setText("Add");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        jLabel8.setText("Empresa");

        jLabel1.setText("Pedido");

        jTabbedEtq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedEtqMouseClicked(evt);
            }
        });

        jTableGeral.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Nota", "Emissão", "Peso", "Selecionar", "Cliente", "Nome", "Cidade", "Estado", "Transp.", "Nome ", "Pedido", "Empresa", "Filial", "Transacao", "Quantidade", "Volume"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableGeral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableGeralMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableGeral);
        if (jTableGeral.getColumnModel().getColumnCount() > 0) {
            jTableGeral.getColumnModel().getColumn(0).setMinWidth(50);
            jTableGeral.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableGeral.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableGeral.getColumnModel().getColumn(1).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(2).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(3).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(4).setMinWidth(50);
            jTableGeral.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTableGeral.getColumnModel().getColumn(4).setMaxWidth(50);
            jTableGeral.getColumnModel().getColumn(5).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(6).setMinWidth(300);
            jTableGeral.getColumnModel().getColumn(6).setPreferredWidth(300);
            jTableGeral.getColumnModel().getColumn(6).setMaxWidth(300);
            jTableGeral.getColumnModel().getColumn(7).setMinWidth(200);
            jTableGeral.getColumnModel().getColumn(7).setPreferredWidth(200);
            jTableGeral.getColumnModel().getColumn(7).setMaxWidth(200);
            jTableGeral.getColumnModel().getColumn(8).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(9).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(10).setMinWidth(300);
            jTableGeral.getColumnModel().getColumn(10).setPreferredWidth(300);
            jTableGeral.getColumnModel().getColumn(10).setMaxWidth(300);
            jTableGeral.getColumnModel().getColumn(11).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(11).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(12).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(12).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(12).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(13).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(14).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(14).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(15).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(15).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(15).setMaxWidth(100);
            jTableGeral.getColumnModel().getColumn(16).setMinWidth(100);
            jTableGeral.getColumnModel().getColumn(16).setPreferredWidth(100);
            jTableGeral.getColumnModel().getColumn(16).setMaxWidth(100);
        }

        jTabbedEtq.addTab("Notas", jScrollPane1);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        txtHorEmb.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnHoras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        btnHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHorasActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        btnSel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnSel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelActionPerformed(evt);
            }
        });

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel6.setText("Data Fim:");

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtEmpresa.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEmpresa.setRequired(true);
        txtEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmpresaActionPerformed(evt);
            }
        });
        txtEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmpresaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEmpresaKeyTyped(evt);
            }
        });

        txtPeso.setDecimals(2);
        txtPeso.setEnabled(false);
        txtPeso.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPeso.setTextAlignment(SwingConstants.RIGHT);

        txtPesoSelecionado.setDecimals(2);
        txtPesoSelecionado.setEnabled(false);
        txtPesoSelecionado.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPesoSelecionado.setTextAlignment(SwingConstants.RIGHT);

        jLabel12.setText("Total Peso");

        jLabel13.setText("Peso selecionado");

        txtNotasSelecionadas.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtNotasSelecionadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotasSelecionadasActionPerformed(evt);
            }
        });

        txtTransportadora1.setEnabled(false);
        txtTransportadora1.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTransportadora1.setUpperCase(true);
        txtTransportadora1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransportadora1ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        jButton9.setText("Sair");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtFilial.setRequired(true);
        txtFilial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialActionPerformed(evt);
            }
        });

        txtVolume.setDecimals(2);
        txtVolume.setEnabled(false);
        txtVolume.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtVolume.setTextAlignment(SwingConstants.RIGHT);

        jLabel15.setText("Volume");

        txtQuantidade.setDecimals(2);
        txtQuantidade.setEnabled(false);
        txtQuantidade.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtQuantidade.setTextAlignment(SwingConstants.RIGHT);

        jLabel16.setText("Volume");

        textControl1.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        textControl2.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel5.setText("Filial");

        jLabel3.setText("Transportadora");

        jLabel9.setText("Notas ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                        .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedEtq)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(txtVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNotasSelecionadas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                            .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(txtTransportadora, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTransportadora1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                            .addComponent(textControl1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(textControl2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtHorEmb, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDatEmb, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtGestor, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEdit, jLabel8});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel14, jLabel2});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDatFim, txtDatIni, txtNota});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel4, jLabel6});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDatEmb, txtGestor});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton6))
                            .addComponent(textControl1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDatEmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(textControl2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel6)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(btnHoras, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7)
                            .addComponent(txtHorEmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTransportadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTransportadora1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtGestor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)))
                    .addComponent(jLabel3))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnSel)
                        .addComponent(txtNotasSelecionadas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTabbedEtq, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel12)
                                        .addComponent(jLabel13)
                                        .addComponent(jLabel16)))
                                .addGap(6, 6, 6)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtVolume, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnEdit)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jLabel14))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jButton3)))
                .addGap(338, 338, 338))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEdit, btnSel, txtNotasSelecionadas});

        javax.swing.GroupLayout pnlFormLayout = new javax.swing.GroupLayout(pnlForm);
        pnlForm.setLayout(pnlFormLayout);
        pnlFormLayout.setHorizontalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        pnlFormLayout.setVerticalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jTabbedCotacao.addTab("Minutas", pnlForm);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedCotacao)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedCotacao)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtGestorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGestorActionPerformed

    }//GEN-LAST:event_txtGestorActionPerformed


    private void jTabbedCotacaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedCotacaoMouseClicked
        //
    }//GEN-LAST:event_jTabbedCotacaoMouseClicked


    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        pesquisarNota(txtNota.getText());

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTabbedEtqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedEtqMouseClicked
        int abaselecionada = jTabbedEtq.getSelectedIndex();

    }//GEN-LAST:event_jTabbedEtqMouseClicked

    private void btnHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHorasActionPerformed
        txtHorEmb.setText(utilDatas.retornarHoras(new Date()));
    }//GEN-LAST:event_btnHorasActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed

        Object[] options = {" Sim ", " Não "};
        if (JOptionPane.showOptionDialog(this, "Gerar Minuta?", "Aviso:",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null,
                options, options[1]) == JOptionPane.YES_OPTION) {
            try {
                novoRegistro("MINUTAS", txtNotasSelecionadas.getText());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "ERRO " + ex);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Processo cancelado");
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        if (txtDatIni.getValue() != null) {
            try {
                pegarDataDigitada();
            } catch (ParseException ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                String sql = " and nfv.usu_germin = 'N' and nfv.datemi = '" + datI + "' and nfv.codemp = 1 and nfv.codfil = 1 ";
                getListar("NFV", sql, false);
            } catch (Exception ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //
    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnSelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelActionPerformed
        pesquisarNotaGeral(txtNotasSelecionadas.getText());

//        try {
//            selecionarRange();
//            btnEdit.setEnabled(false);
//            if (txtPesoSelecionado.getDouble() > 0) {
//                btnEdit.setEnabled(true);
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(MinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_btnSelActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        if (txtDatFim.getValue() != null) {
            try {
                pegarDataDigitada();
            } catch (ParseException ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                String sql = " and nfv.usu_germin = 'N' and nfv.datemi >= '" + datI + "' and nfv.datemi<= '" + datF + "' and nfv.codemp >0 and nfv.codfil >0 ";

                getListar("NFV", sql, false);
            } catch (Exception ex) {
                Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }//GEN-LAST:event_jButton7ActionPerformed

    private void txtEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmpresaActionPerformed
        txtFilial.requestFocus();
    }//GEN-LAST:event_txtEmpresaActionPerformed

    private void jTableGeralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableGeralMouseClicked
        try {
            int linhaSelSit = jTableGeral.getSelectedRow();
            int colunaSelSit = jTableGeral.getSelectedColumn();
            txtTransportadora.setText(jTableGeral.getValueAt(linhaSelSit, 9).toString());
            txtTransportadora1.setText(jTableGeral.getValueAt(linhaSelSit, 10).toString());

            selecionarRange();
        } catch (Exception ex) {
            Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jTableGeralMouseClicked

    private void txtNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaActionPerformed
        pesquisarNota(txtNota.getText());
    }//GEN-LAST:event_txtNotaActionPerformed

    private void txtTransportadoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransportadoraActionPerformed
        try {
            pesquisarTransportadora(txtTransportadora.getText(), txtEmpresa.getText(), txtFilial.getText());
        } catch (SQLException ex) {
            Logger.getLogger(LogMinutaEmbarqueNota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtTransportadoraActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            IntegrarTransportadora sol = new IntegrarTransportadora();
            MDIFrame.add(sol, true);
            sol.setMaximum(false); // executa maximizado 
            sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavraNota(this);

        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtTransportadora1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransportadora1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTransportadora1ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        sair();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void txtNotasSelecionadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotasSelecionadasActionPerformed
        pesquisarNotaGeral(txtNotasSelecionadas.getText());
    }//GEN-LAST:event_txtNotasSelecionadasActionPerformed

    private void txtFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialActionPerformed
        txtTransportadora.requestFocus();
    }//GEN-LAST:event_txtFilialActionPerformed

    private void txtEmpresaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmpresaKeyTyped
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            System.out.println("aaaaa");
        }
    }//GEN-LAST:event_txtEmpresaKeyTyped

    private void txtEmpresaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmpresaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmpresaKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHoras;
    private javax.swing.JButton btnSel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedCotacao;
    private javax.swing.JTabbedPane jTabbedEtq;
    private javax.swing.JTable jTableGeral;
    private javax.swing.JPanel pnlForm;
    private org.openswing.swing.client.TextControl textControl1;
    private org.openswing.swing.client.TextControl textControl2;
    private org.openswing.swing.client.DateControl txtDatEmb;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.TextControl txtFilial;
    private javax.swing.JComboBox txtGestor;
    private org.openswing.swing.client.TextControl txtHorEmb;
    private org.openswing.swing.client.TextControl txtNota;
    private org.openswing.swing.client.TextControl txtNotasSelecionadas;
    private org.openswing.swing.client.NumericControl txtPeso;
    private org.openswing.swing.client.NumericControl txtPesoSelecionado;
    private org.openswing.swing.client.NumericControl txtQuantidade;
    private org.openswing.swing.client.TextControl txtTransportadora;
    private org.openswing.swing.client.TextControl txtTransportadora1;
    private org.openswing.swing.client.NumericControl txtVolume;
    // End of variables declaration//GEN-END:variables
}
