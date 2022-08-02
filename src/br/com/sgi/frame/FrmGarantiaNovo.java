/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Fornecedor;
import br.com.sgi.bean.Garantia;
import br.com.sgi.bean.GarantiaItens;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.dao.FornecedorDAO;
import br.com.sgi.dao.GarantiaDAO;
import br.com.sgi.dao.GarantiaItensDAO;
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
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class FrmGarantiaNovo extends InternalFrame {

    private Garantia garantia;
    private GarantiaDAO garantiaDAO;
    private List<Garantia> lstGarantia = new ArrayList<Garantia>();

    private GarantiaItens garantiaItens;
    private GarantiaItensDAO garantiaItensDAO;
    private List<GarantiaItens> lstGarantiaItens = new ArrayList<GarantiaItens>();

    private UtilDatas utilDatas;
    private boolean addnewreg = true;
    private String acao;
    private Integer id;

    public FrmGarantiaNovo() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Garantia "));
            this.setSize(800, 500);
            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (garantiaDAO == null) {
                this.garantiaDAO = new GarantiaDAO();
            }
            if (garantiaItensDAO == null) {
                this.garantiaItensDAO = new GarantiaItensDAO();
            }
            limpatela();
            btnImportar.setEnabled(true);
            btnNovo.setEnabled(true);
            pegarData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void pegarData() {
        txtDatIni.setDate(this.utilDatas.retornaDataIni(new Date()));
        txtDatFim.setDate(this.utilDatas.retornaDataFim(new Date()));
    }

    private void buscarGarantias(String opcao) throws SQLException, Exception {
        if (!acao.equals("novo")) {
            String datini = utilDatas.converterDateToStr(txtDatIni.getDate());
            String datfim = utilDatas.converterDateToStr(txtDatFim.getDate());
            if (opcao.equals("CNPJ")) {
                lstGarantia = garantiaDAO.getGarantias(opcao, "and usu_datemi>= '" + datini + "'"
                        + "and usu_datemi <= '" + datfim + "' and usu_cgccpf = '" + txtCgcCpf.getText() + "'");
            }
            if (opcao.equals("CODIGO")) {
                lstGarantia = garantiaDAO.getGarantias(opcao, " and usu_datemi>= '" + datini + "'"
                        + "and usu_datemi <= '" + datfim + "' and usu_codfor = '" + txtCodFor.getText() + "'");
            }
            if (lstGarantia != null) {
                carregarTabela();
            }
        }
    }

    public void carregarTabela() throws Exception {
        redColunastab();
        int linhas = 0;
        ImageIcon NotaIcon = getImage("/images/accept.png");
        ImageIcon DigiIcon = getImage("/images/sitRuim.png");
        ImageIcon FechIcon = getImage("/images/sitAnd.png");
        ImageIcon InspIcon = getImage("/images/sitMedio.png");

        DefaultTableModel modelo = (DefaultTableModel) jTableGarantia.getModel();
        modelo.setNumRows(linhas);

        for (Garantia gar : lstGarantia) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableGarantia.getColumnModel();
            FrmGarantiaNovo.JTableRenderer renderers = new FrmGarantiaNovo.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            switch (gar.getUsu_sitnfc()) {
                case 1:
                    linha[0] = DigiIcon;
                    break;
                case 2:
                    linha[0] = InspIcon;
                    break;
                case 3:
                    linha[0] = NotaIcon;
                    break;
                case 4:
                    linha[0] = NotaIcon;
                    break;
                default:
                    break;
            }

            linha[1] = gar.getUsu_numnfc();
            linha[2] = gar.getCadFornecedor().getCodfor();
            linha[3] = gar.getCadFornecedor().getNomfor();
            linha[4] = this.utilDatas.converterDateToStr(gar.getUsu_datemi());
            linha[5] = gar.getSituacao();
            linha[6] = gar.getUsu_empdes();
            linha[7] = gar.getUsu_codfil();

            modelo.addRow(linha);
        }

    }

    private void buscarGarantiasItens(Garantia gar) throws SQLException, Exception {
        if (gar != null) {
            if (gar.getUsu_codfor() > 0) {
                lstGarantiaItens = garantiaItensDAO.getGarantiaItens(" nota ",
                        " \nand usu_empdes = " + gar.getUsu_empdes() + ""
                        + "\nand usu_codfil = " + gar.getUsu_codfil() + ""
                        + "\nand usu_numnfc = " + gar.getUsu_numnfc() + " "
                        + "\nand usu_codfor = " + gar.getUsu_codfor());
            }
        }
        if (lstGarantiaItens != null) {
            if (lstGarantiaItens.size() > 0) {
                carregarTabelaItens();
                HabilitarTabMat();
                txtNumSep.setEnabled(true);
            }

        }
    }

    public void carregarTabelaItens() throws Exception {
        redColunastab();
        int linhas = 0;
        ImageIcon NotaIcon = getImage("/images/accept.png");
        ImageIcon DigiIcon = getImage("/images/sitRuim.png");
        ImageIcon FechIcon = getImage("/images/sitAnd.png");
        ImageIcon InspIcon = getImage("/images/sitMedio.png");

        DefaultTableModel modelo = (DefaultTableModel) jTableItem.getModel();
        modelo.setNumRows(linhas);

        for (GarantiaItens gar : lstGarantiaItens) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTableItem.getColumnModel();
            FrmGarantiaNovo.JTableRenderer renderers = new FrmGarantiaNovo.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = DigiIcon;

            linha[1] = gar.getUsu_numnfc();
            linha[2] = gar.getUsu_codpro();
            linha[3] = gar.getCadProduto().getDescricaoproduto();
            linha[4] = gar.getUsu_codder();
            linha[5] = gar.getUsu_numsep();
            linha[6] = gar.getUsu_codcli();
            linha[7] = gar.getCadCliente().getNome();
            linha[8] = true;

            linha[9] = gar.getUsu_numnfv();
            linha[10] = gar.getUsu_codsnf();
            linha[11] = utilDatas.converterDateToStr(gar.getUsu_datemi());

            linha[12] = gar.getUsu_tmptot();
            linha[13] = gar.getUsu_tmpfat();
            linha[14] = gar.getUsu_sitgar();
            modelo.addRow(linha);
        }

    }

    private void pesquisarTable(String pesquisa) {
        DefaultTableModel tabela_pedidos = (DefaultTableModel) jTableItem.getModel();

        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabela_pedidos);
        jTableItem.setRowSorter(sorter);
        pesquisa = txtNumSep.getText();

        if (pesquisa.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<TableModel, Object> rf = null;
                try {
                    rf = RowFilter.regexFilter(pesquisa, 5);
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                sorter.setRowFilter(rf);
            } catch (PatternSyntaxException pse) {
                System.err.println("Erro");
            }
        }
    }

    private void buscarFornecedor(String opcao) throws SQLException {
        Fornecedor fo = new Fornecedor();
        FornecedorDAO dao = new FornecedorDAO();
        if (opcao.equals("CODIGO")) {
            fo = dao.getFornecedor(opcao, " and codfor = " + txtCodFor.getText());
        }
        if (opcao.equals("CNPJ")) {
            fo = dao.getFornecedor(opcao, " and cgccpf = " + txtCgcCpf.getText());
        }
        if (fo != null) {
            if (fo.getCodfor() > 0) {
                txtNomFor.setText(fo.getNomfor());
                txtCodFor.setText(fo.getCodfor().toString());
                txtCgcCpf.setText(fo.getCgccpf());
                if (acao.equals("novo")) {
                    txtCodEmp.setEnabled(true);
                    txtNumNfc.setEnabled(true);
                    txtTipDoc.setEnabled(true);
                    txtCodEmp.requestFocus();
                }
            }
        }
    }

    public void HabilitarTabPdu() {
        jTabbedEtq.setEnabledAt(0, true);
        jTabbedEtq.setEnabledAt(1, true);
        jTabbedEtq.setSelectedIndex(0);

    }

    public void HabilitarTabMat() {
        jTabbedEtq.setEnabledAt(0, true);
        jTabbedEtq.setEnabledAt(1, true);
        jTabbedEtq.setSelectedIndex(1);
    }

    private void gerarEntrega() throws Exception {
        limpatela();
        HabilitarTabPdu();
        btnImportar.setEnabled(true);
        btnOcorrencia.setEnabled(true);
        btnNovo.setEnabled(true);
        if (acao.equals("novo")) {
            txtCgcCpf.setEnabled(true);
            txtCodFor.setEnabled(true);
            txtDatIni.setEnabled(true);
            txtDatIni.setDate(new Date());
            txtDatFim.setDate(new Date());
            txtCgcCpf.requestFocus();
        }

        if (acao.equals("consultar")) {
            pegarData();
            txtDatIni.setEnabled(true);
            txtDatFim.setEnabled(true);
            txtCgcCpf.setEnabled(true);
            txtCodFor.setEnabled(true);
            txtCgcCpf.requestFocus();
        }

    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableGarantia.setRowHeight(40);
        jTableGarantia.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTableItem.setRowHeight(40);
        jTableItem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableItem.setAutoResizeMode(0);
        jTableItem.setAutoCreateRowSorter(true);

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void limpatela() throws ParseException, Exception {
        txtSitGar.setText("Digitada");
        txtCgcCpf.setText("");
        txtCgcCpf.setEnabled(false);
        txtNumNfc.setText("");
        txtNumNfc.setEnabled(false);
        txtCodEmp.setText("");
        txtCodEmp.setEnabled(false);
        txtDesCre.setText("");
        txtCodFil.setText("");
        txtNomFor.setText("");
        btnImportar.setEnabled(false);
        btnNovo.setEnabled(false);
        btnGravar.setEnabled(false);
        txtDatIni.setEnabled(false);
        txtDatFim.setEnabled(false);
        txtTipDoc.setEnabled(false);
    }

    public void limpatelaErro() throws ParseException, Exception {
        txtCgcCpf.setText("");
        txtCgcCpf.setEnabled(true);
        txtNumNfc.setText("1");
        txtNumNfc.setEnabled(false);

        txtCodEmp.setText("");
        txtCodEmp.setEnabled(false);

        txtDesCre.setText("");
        txtCodFil.setText("");
        txtNomFor.setText("");

        //   txtControlador.setText("");
        btnImportar.setEnabled(false);
        btnNovo.setEnabled(false);
        btnGravar.setEnabled(false);
        int linhas = 0;
        DefaultTableModel modeloRec = (DefaultTableModel) jTableItem.getModel();
        modeloRec.setNumRows(linhas);

        DefaultTableModel modeloPrd = (DefaultTableModel) jTableGarantia.getModel();
        modeloPrd.setNumRows(linhas);

        txtCgcCpf.requestFocus();

    }

    private void salvar() throws Exception {
    }

    private void salvarMaterial() throws Exception {
    }

    public final void carregarBaixa(String tipo) throws Exception {
    }

    private void finalizar() throws Exception {
    }

//    public void setRecebePalavra(F020Almox veioInput, boolean addNewReg, Pdu pdu) throws SQLException, Exception {
//
//    }
    public void enviar() throws SQLException, Exception {

    }

    private void novoRegistro() throws SQLException {

    }

    private void cancelarRegistro() throws Exception {

    }

    private void acaoSalvar() {

    }

    private void removerItem(Integer idLancamento) throws Exception {
    }

    private ImageIcon getImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return (new ImageIcon(url));
        } else {
            return null;

        }
    }

    private void buscarCentroRecurso() {

    }

    private void buscarEstoque(String barcode, String deposito) throws SQLException {
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
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedEtq = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableGarantia = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableItem = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btnNovo = new javax.swing.JButton();
        btnImportar = new javax.swing.JButton();
        btnGravar = new javax.swing.JButton();
        btnFinalizar = new javax.swing.JButton();
        btnOcorrencia = new javax.swing.JButton();
        bntExcluir = new javax.swing.JButton();
        btnFinalizar1 = new javax.swing.JButton();
        btnConsultar = new javax.swing.JButton();
        txtCgcCpf = new org.openswing.swing.client.TextControl();
        txtCodEmp = new org.openswing.swing.client.TextControl();
        jLabel9 = new javax.swing.JLabel();
        btnCalcular = new javax.swing.JButton();
        txtCodFor = new org.openswing.swing.client.TextControl();
        txtNumSep = new org.openswing.swing.client.TextControl();
        txtDesCre = new org.openswing.swing.client.TextControl();
        txtCodFil = new org.openswing.swing.client.TextControl();
        bntCentroRecurso = new javax.swing.JButton();
        txtNomFor = new org.openswing.swing.client.TextControl();
        bntCentroRecurso1 = new javax.swing.JButton();
        txtNumNfc = new org.openswing.swing.client.TextControl();
        txtSitGar = new org.openswing.swing.client.TextControl();
        txtTipDoc = new javax.swing.JComboBox<>();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        bntCentroRecurso2 = new javax.swing.JButton();
        bntCentroRecurso3 = new javax.swing.JButton();

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

        jLabel8.setText("Cnpj");

        jLabel1.setText("Nota");

        jTabbedEtq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedEtqMouseClicked(evt);
            }
        });

        jTableGarantia.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jTableGarantia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sequencia", "Nota", "Fornecedor", "Nome", "Data", "Situação", "Empresa", "Filial", "Opção"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableGarantia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableGarantiaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableGarantia);
        if (jTableGarantia.getColumnModel().getColumnCount() > 0) {
            jTableGarantia.getColumnModel().getColumn(0).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(1).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(2).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(4).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(5).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(6).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(7).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(8).setMinWidth(50);
            jTableGarantia.getColumnModel().getColumn(8).setPreferredWidth(50);
            jTableGarantia.getColumnModel().getColumn(8).setMaxWidth(50);
        }

        jTabbedEtq.addTab("Garantias", jScrollPane1);

        jTableItem.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jTableItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "id", "Produto", "Descrição", "Derivação", "Serie", "Cliente", "Razão Social", "Gravar", "Nota", "Série", "Emissão", "Tempo Garantia", "Tempo Uso", "Situação"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, false, false, true, true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableItemMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableItem);
        if (jTableItem.getColumnModel().getColumnCount() > 0) {
            jTableItem.getColumnModel().getColumn(0).setMinWidth(50);
            jTableItem.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableItem.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableItem.getColumnModel().getColumn(1).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(2).setMinWidth(150);
            jTableItem.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTableItem.getColumnModel().getColumn(2).setMaxWidth(150);
            jTableItem.getColumnModel().getColumn(3).setMinWidth(300);
            jTableItem.getColumnModel().getColumn(3).setPreferredWidth(300);
            jTableItem.getColumnModel().getColumn(3).setMaxWidth(300);
            jTableItem.getColumnModel().getColumn(4).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(5).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(6).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(7).setMinWidth(300);
            jTableItem.getColumnModel().getColumn(7).setPreferredWidth(300);
            jTableItem.getColumnModel().getColumn(7).setMaxWidth(300);
            jTableItem.getColumnModel().getColumn(8).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(9).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(12).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(12).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(12).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(13).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(14).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(14).setMaxWidth(100);
        }

        jTabbedEtq.addTab("Itens", jScrollPane3);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnNovo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Novo.png"))); // NOI18N
        btnNovo.setText("NOVO");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnImportar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnImportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Importar.png"))); // NOI18N
        btnImportar.setText("IMPORTAR");
        btnImportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarActionPerformed(evt);
            }
        });

        btnGravar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravar.setText("PEDIDO");
        btnGravar.setEnabled(false);
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        btnFinalizar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/connect.png"))); // NOI18N
        btnFinalizar.setText("FINALIZAR");
        btnFinalizar.setEnabled(false);
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });

        btnOcorrencia.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnOcorrencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        btnOcorrencia.setText("OCORRENCIA");
        btnOcorrencia.setEnabled(false);
        btnOcorrencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOcorrenciaActionPerformed(evt);
            }
        });

        bntExcluir.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bntExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        bntExcluir.setText("EXCLUIR");
        bntExcluir.setEnabled(false);
        bntExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntExcluirActionPerformed(evt);
            }
        });

        btnFinalizar1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnFinalizar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        btnFinalizar1.setText("SAIR");
        btnFinalizar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizar1ActionPerformed(evt);
            }
        });

        btnConsultar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnConsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnConsultar.setText("CONSULTAR");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnImportar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConsultar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOcorrencia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGravar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bntExcluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFinalizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFinalizar1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImportar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bntExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFinalizar1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        txtCgcCpf.setEnabled(false);
        txtCgcCpf.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtCgcCpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCgcCpfActionPerformed(evt);
            }
        });

        txtCodEmp.setEnabled(false);
        txtCodEmp.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtCodEmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodEmpActionPerformed(evt);
            }
        });

        jLabel9.setText("Empresa");

        btnCalcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        txtCodFor.setEnabled(false);
        txtCodFor.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtCodFor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodForActionPerformed(evt);
            }
        });

        txtNumSep.setEnabled(false);
        txtNumSep.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtNumSep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumSepActionPerformed(evt);
            }
        });

        txtDesCre.setEnabled(false);
        txtDesCre.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtDesCre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDesCreActionPerformed(evt);
            }
        });

        txtCodFil.setEnabled(false);
        txtCodFil.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtCodFil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodFilActionPerformed(evt);
            }
        });

        bntCentroRecurso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        bntCentroRecurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCentroRecursoActionPerformed(evt);
            }
        });

        txtNomFor.setEnabled(false);
        txtNomFor.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtNomFor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomForActionPerformed(evt);
            }
        });

        bntCentroRecurso1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        bntCentroRecurso1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCentroRecurso1ActionPerformed(evt);
            }
        });

        txtNumNfc.setEnabled(false);
        txtNumNfc.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtNumNfc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumNfcActionPerformed(evt);
            }
        });

        txtSitGar.setEnabled(false);
        txtSitGar.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtSitGar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSitGarActionPerformed(evt);
            }
        });

        txtTipDoc.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtTipDoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione", "N - Nota Fiscal", "R - Romaneio" }));
        txtTipDoc.setEnabled(false);

        txtDatIni.setEnabled(false);
        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        txtDatFim.setEnabled(false);
        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        bntCentroRecurso2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        bntCentroRecurso2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCentroRecurso2ActionPerformed(evt);
            }
        });

        bntCentroRecurso3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        bntCentroRecurso3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCentroRecurso3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedEtq)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNumNfc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCalcular)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSitGar, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTipDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNumSep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtCgcCpf, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCodEmp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bntCentroRecurso)
                            .addComponent(bntCentroRecurso1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtNomFor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCodFor, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtDesCre, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCodFil, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bntCentroRecurso2)
                            .addComponent(bntCentroRecurso3)))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel8});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDatFim, txtDatIni});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(txtCgcCpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bntCentroRecurso1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bntCentroRecurso2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtCodEmp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(txtDesCre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCodFil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(bntCentroRecurso, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bntCentroRecurso3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumNfc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCalcular, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                            .addComponent(txtNumSep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jLabel1))
                    .addComponent(txtSitGar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTipDoc))
                .addGap(16, 16, 16)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jTabbedEtq, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCalcular, txtNumNfc, txtNumSep});

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

        jTabbedCotacao.addTab("Garantias", pnlForm);

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


    private void jTabbedCotacaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedCotacaoMouseClicked
        //
    }//GEN-LAST:event_jTabbedCotacaoMouseClicked

    private void jTableGarantiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableGarantiaMouseClicked
        int linhaSelSit = jTableGarantia.getSelectedRow();
        int colunaSelSit = jTableGarantia.getSelectedColumn();

        txtNumNfc.setText(jTableGarantia.getValueAt(linhaSelSit, 1).toString());
        txtCodFor.setText(jTableGarantia.getValueAt(linhaSelSit, 2).toString());

        txtSitGar.setText(jTableGarantia.getValueAt(linhaSelSit, 5).toString());
        txtCodEmp.setText(jTableGarantia.getValueAt(linhaSelSit, 6).toString());
        txtCodFil.setText(jTableGarantia.getValueAt(linhaSelSit, 7).toString());

        if (!txtCodFor.getText().isEmpty()) {
            try {
                buscarFornecedor("CODIGO");
            } catch (SQLException ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.garantia = new Garantia();
        try {
            this.garantia = this.garantiaDAO.getGarantia("GARANTIA", " and usu_empdes = " + txtCodEmp.getText().trim() + "\n"
                    + "and usu_codfil = " + txtCodFil.getText().trim() + "\n"
                    + "and usu_numnfc = " + txtNumNfc.getText().trim() + "\n"
                    + "and usu_codfor = " + txtCodFor.getText().trim() + " \n");
            if (this.garantia != null) {
                if (this.garantia.getUsu_numnfc() > 0) {
                    if (this.garantia.getUsu_tipnfc().equals("N")) {
                        txtTipDoc.setSelectedItem("N - Nota Fiscal");
                    }
                    if (this.garantia.getUsu_tipnfc().equals("R")) {
                        txtTipDoc.setSelectedItem("R - Romaneio");
                    }
                    buscarGarantiasItens(garantia);

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jTableGarantiaMouseClicked

    private void jTabbedEtqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedEtqMouseClicked
        //
    }//GEN-LAST:event_jTabbedEtqMouseClicked

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        try {
            this.acao = "novo";
            gerarEntrega();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex.getMessage());
        }
    }//GEN-LAST:event_btnNovoActionPerformed

    private void btnImportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarActionPerformed

        try {
            FrmGarantiaImportar sol = new FrmGarantiaImportar();
            MDIFrame.add(sol, true);
            sol.setPosicao();
            sol.setMaximum(true); // executa maximizado
            sol.setRecebePalavra(this);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnImportarActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        //
    }//GEN-LAST:event_btnGravarActionPerformed

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        //
    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void btnOcorrenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOcorrenciaActionPerformed
         try {
            FrmGarantiaImportar sol = new FrmGarantiaImportar();
            MDIFrame.add(sol, true);
            sol.setPosicao();
            sol.setMaximum(true); // executa maximizado
            sol.setRecebePalavraOcorrencias(this);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnOcorrenciaActionPerformed

    private void txtCgcCpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCgcCpfActionPerformed
        if (!txtCgcCpf.getText().isEmpty()) {
            try {
                buscarGarantias("CNPJ");
                buscarFornecedor("CNPJ");
            } catch (SQLException ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtCgcCpfActionPerformed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        //
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void txtCodEmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodEmpActionPerformed
        buscarCentroRecurso();


    }//GEN-LAST:event_txtCodEmpActionPerformed

    private void bntExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntExcluirActionPerformed
        //
    }//GEN-LAST:event_bntExcluirActionPerformed

    private Integer idLct = 0;
    private void jTableItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableItemMouseClicked
        int linhaSelSit = jTableItem.getSelectedRow();
        int colunaSelSit = jTableItem.getSelectedColumn();

        idLct = Integer.valueOf(jTableItem.getValueAt(linhaSelSit, 1).toString());

        if (idLct > 0) {
            txtNumSep.setText(jTableItem.getValueAt(linhaSelSit, 5).toString());
            bntExcluir.setEnabled(true);
        }

    }//GEN-LAST:event_jTableItemMouseClicked

    private void txtNumSepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumSepActionPerformed
        pesquisarTable(txtNumSep.getText());
    }//GEN-LAST:event_txtNumSepActionPerformed

    private void txtDesCreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDesCreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDesCreActionPerformed

    private void bntCentroRecursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCentroRecursoActionPerformed
        buscarCentroRecurso();
    }//GEN-LAST:event_bntCentroRecursoActionPerformed

    private void txtNomForActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomForActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomForActionPerformed

    private void btnFinalizar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizar1ActionPerformed
        //
    }//GEN-LAST:event_btnFinalizar1ActionPerformed

    private void bntCentroRecurso1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCentroRecurso1ActionPerformed
        if (!txtCgcCpf.getText().isEmpty()) {
            try {
                buscarGarantias("CNPJ");
                buscarFornecedor("CNPJ");
            } catch (SQLException ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_bntCentroRecurso1ActionPerformed

    private void txtCodForActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodForActionPerformed
        if (!txtCodFor.getText().isEmpty()) {
            try {
                buscarGarantias("CODIGO");
                buscarFornecedor("CODIGO");
            } catch (SQLException ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtCodForActionPerformed

    private void txtCodFilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodFilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodFilActionPerformed

    private void txtNumNfcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumNfcActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumNfcActionPerformed

    private void txtSitGarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSitGarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSitGarActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            this.acao = "consultar";
            gerarEntrega();
        } catch (Exception ex) {
            Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void bntCentroRecurso2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCentroRecurso2ActionPerformed
        if (!txtCodFor.getText().isEmpty()) {
            try {
                buscarGarantias("CODIGO");
                buscarFornecedor("CODIGO");
            } catch (SQLException ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_bntCentroRecurso2ActionPerformed

    private void bntCentroRecurso3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCentroRecurso3ActionPerformed
        try {
            String datini = utilDatas.converterDateToStr(txtDatIni.getDate());
            String datfim = utilDatas.converterDateToStr(txtDatFim.getDate());
            lstGarantia = garantiaDAO.getGarantias("data", " and usu_datemi>= '" + datini + "'"
                    + "and usu_datemi <= '" + datfim + "'");
            if (lstGarantia != null) {
                carregarTabela();
            }
        } catch (Exception e) {
        }

    }//GEN-LAST:event_bntCentroRecurso3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntCentroRecurso;
    private javax.swing.JButton bntCentroRecurso1;
    private javax.swing.JButton bntCentroRecurso2;
    private javax.swing.JButton bntCentroRecurso3;
    private javax.swing.JButton bntExcluir;
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JButton btnFinalizar1;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnImportar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnOcorrencia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedCotacao;
    private javax.swing.JTabbedPane jTabbedEtq;
    private javax.swing.JTable jTableGarantia;
    private javax.swing.JTable jTableItem;
    private javax.swing.JPanel pnlForm;
    private org.openswing.swing.client.TextControl txtCgcCpf;
    private org.openswing.swing.client.TextControl txtCodEmp;
    private org.openswing.swing.client.TextControl txtCodFil;
    private org.openswing.swing.client.TextControl txtCodFor;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private org.openswing.swing.client.TextControl txtDesCre;
    private org.openswing.swing.client.TextControl txtNomFor;
    private org.openswing.swing.client.TextControl txtNumNfc;
    private org.openswing.swing.client.TextControl txtNumSep;
    private org.openswing.swing.client.TextControl txtSitGar;
    private javax.swing.JComboBox<String> txtTipDoc;
    // End of variables declaration//GEN-END:variables
}
