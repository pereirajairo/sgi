/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.Pedido;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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
public final class FatPedido extends InternalFrame {

    private Pedido pedido;
    private List<Pedido> listPedido = new ArrayList<Pedido>();
    private List<Pedido> listPedidoSelecionado = new ArrayList<Pedido>();
    private PedidoDAO pedidoDAO;

    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;
    private String sqlcodigoProduto = " ";
    private String sqlcodigoCliente = " ";
    private String pedidoSelecionado = "";
    private String clienteSelecionado = "";

    public FatPedido() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Pedido embarque"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (pedidoDAO == null) {
                pedidoDAO = new PedidoDAO();
            }

            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
            txtDatFim.setDate(new Date());

            LoadEstados();

            pegarDataDigitada();
            retornarPedido();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }

    }

    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
    }

    private void pesquisarPorData(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        PESQUISA_POR = "DATA";
        PESQUISA = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "'";
        getPedidos(PESQUISA_POR, PESQUISA);
    }

    public void iniciarBarra(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Buscando Pedidos");
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getPedidos(PESQUISA_POR, PESQUISA);
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                // barra.setString("Filtro carregado");
            }
        };
        worker.execute();

    }

    public void selecionarRange() throws SQLException, Exception {
        String selecionar = "";
        if (jTableCarga.getRowCount() > 0) {
            for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                if ((Boolean) jTableCarga.getValueAt(i, 12)) {
                    selecionar += (jTableCarga.getValueAt(i, 1).toString() + ",");
                }
            }
            int tam = selecionar.length();
            if (tam > 0) {
                if (!selecionar.isEmpty()) {
                    selecionar = selecionar.substring(0, tam - 1);
                }
            }
            if (!selecionar.isEmpty()) {
                txtPedidoSelecionado.setText(selecionar.trim());
                listPedidoSelecionado = this.pedidoDAO.getPedidosExpedicao("pedido", "and e120ped.numped in (" + selecionar + ")");
                if (listPedidoSelecionado != null) {
                    if (listPedidoSelecionado.size() > 0) {
                        novoRegistro("", "");
                    }
                }
            } else {
                Mensagem.mensagem("ERROR", " Selecione o Pedido");
            }
        }
    }

    void retornarPedido(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        PESQUISA_POR = "DATA";
        PESQUISA = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "'";
        getPedidos(PESQUISA_POR, PESQUISA);
    }

    private void getPedidos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listPedido = this.pedidoDAO.getPedidosExpedicao(PESQUISA_POR, PESQUISA);
        if (listPedido != null) {
            carregarTabela();
        }
    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon MedIcon = getImage("/images/sitMedio.png");
        ImageIcon ReaIcon = getImage("/images/sitAnd.png");

        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon GarIcon = getImage("/images/ruby_delete.png");
        ImageIcon MktIcon = getImage("/images/bateriaindu.png");
        double peso = 0;
        double qtdy = 0;
        double qtdy_atr = 0;
        double qtdy_com = 0;

        double qtdped = 0;
        int contador = 0;

        for (Pedido ped : listPedido) {
            Object[] linha = new Object[23];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new FatPedido.ColorirRenderer();
            FatPedido.JTableRenderer renderers = new FatPedido.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            columnModel.getColumn(11).setCellRenderer(renderers);
            linha[0] = RuimIcon;
            if (ped.getQtddia_atrazo() < 0) {
                linha[0] = BomIcon;
            }

            linha[1] = ped.getPedido();
            linha[2] = ped.getNumeropre();
            linha[3] = ped.getNumeroanalise();
            linha[4] = ped.getCliente();
            linha[5] = ped.getCadCliente().getNome();
            linha[6] = ped.getCadCliente().getEstado();
            linha[7] = ped.getCadCliente().getCidade();
            linha[8] = ped.getDataSeparacaoS();
            linha[9] = ped.getDia_transporte() + " D+1";

            linha[10] = ped.getData_para_faturarS();
            linha[11] = AutIcon;
            if (ped.getLinha().equals("BM")) {
                linha[11] = MotIcon;
            }
            if (ped.getLinha().equals("GAR")) {
                linha[11] = GarIcon;
            }
            if (ped.getLinha().equals("MKT")) {
                linha[11] = MktIcon;
            }

            columnModel.getColumn(12).setMinWidth(0);
            columnModel.getColumn(12).setMaxWidth(0);
            linha[12] = false;
            if (!clienteSelecionado.isEmpty()) {
                linha[12] = true;
                columnModel.getColumn(12).setMinWidth(50);
                columnModel.getColumn(12).setMaxWidth(50);
                columnModel.getColumn(12).setPreferredWidth(50);
            }

            TableCellRenderer renderer = new FatPedido.ColorirRenderer();
            jTableCarga.getColumnModel().getColumn(13).setCellRenderer(renderer);
            linha[13] = ped.getLiberarMinuta();

            linha[14] = ped.getPeso();
            peso += ped.getPeso();
            linha[15] = ped.getQuantidade();
            qtdy += ped.getQuantidade();
            linha[16] = ped.getSituacaoLogistica();
            linha[17] = ped.getEmpresa();
            linha[18] = ped.getFilial();
            linha[19] = ped.getEmissaoS();
            linha[20] = ped.getDataAgendamentoS();
            linha[21] = ped.getQtddia_atrazo();
            linha[22] = ped.getCadTransportadora().getCodigoTransportadora() + " - " + ped.getCadTransportadora().getNomeTransportadora();

            qtdped++;
            modeloCarga.addRow(linha);
        }
        lblPeso.setText(FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM));
        lblQtdy.setText(FormatarPeso.mascaraPorcentagem(qtdy, FormatarPeso.PORCENTAGEM));
        if (qtdped > 0) {
            qtdy_atr = qtdy_atr / qtdped;
            qtdy_com = qtdped * 0.80;
        }

        lblQtdyPedido.setText(FormatarPeso.mascaraPorcentagem(qtdped, FormatarPeso.PORCENTAGEM));
        lblQtdyLogistica.setText(FormatarPeso.mascaraPorcentagem(qtdy_com, FormatarPeso.PORCENTAGEM));
        lblQtdyAtrazo.setText(FormatarPeso.mascaraPorcentagem(qtdy_atr, FormatarPeso.PORCENTAGEM));
        barra.setString("Encontrados " + qtdped + " pedidos encontrados para faturar");
        btnLiberar.setEnabled(false);
        if (!clienteSelecionado.isEmpty()) {
            btnLiberar.setEnabled(true);
        }

    }


    public void retornarPedido() {
        try {
            datFim = this.utilDatas.converterDateToStr(new Date());
            String sql = " AND E135PES.DATPRP + FIL.USU_PERFAT<= '" + datFim + "' ";
            iniciarBarra("", sql);
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

        jTableCarga.setRowHeight(40);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(8).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);

        jTableCarga.getColumnModel().getColumn(14).setCellRenderer(direita);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoCreateRowSorter(true);
        jTableCarga.setAutoResizeMode(0);

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
        frmMinutasFaturamentoGerar sol = new frmMinutasFaturamentoGerar();
        MDIFrame.add(sol, true);
        sol.setMaximum(true); // executa maximizado 
        sol.setPosicao();
        sol.setRecebePedido(this, this.listPedidoSelecionado, txtClienteSelecionado.getText());

    }

    private void LoadEstados() {
        BaseEstado estado = new BaseEstado();
        Map<String, String> mapas = estado.getEstados();
        for (String uf : mapas.keySet()) {
            txtEstado.addItem(mapas.get(uf));
        }
    }

    private void limparFiltros() throws SQLException, ParseException {
        limparTela();
        txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
        txtPedido.setText("");
        txtCliente.setText("");
        LoadEstados();
        pegarDataDigitada();
        retornarPedido();
        txtEstado.setSelectedItem("TODOS");
    }

    private void limparTela() throws SQLException, ParseException {
        txtPedido.setText("");
        txtCliente.setText("");
        txtClienteSelecionado.setText("");
        txtPedidoSelecionado.setText("");
        pedidoSelecionado = "";
        clienteSelecionado = "";
        // limparFiltros();

    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Boolean b = false;
        if (columnIndex == 2) {
            b = true;
            return true;
        } else {
            b = false;
        }
        return b;

    }

    private void pesquisarTable(String pesquisa) {
        DefaultTableModel tabela_produtos = (DefaultTableModel) jTableCarga.getModel();
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabela_produtos);
        jTableCarga.setRowSorter(sorter);

        int index = pesquisa.indexOf(":");
        String coluna = pesquisa.substring(0, index);
        coluna = coluna.trim();
        int posicao = 0;
        if (coluna.equals("PED") || coluna.equals("ped")) {
            posicao = 1;
        }
        if (coluna.equals("FAT") || coluna.equals("fat")) {
            posicao = 8;
        }
        String campo = pesquisa.substring(index + 1, pesquisa.length());
        if (pesquisa.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<TableModel, Object> rf = null;
                try {
                    rf = RowFilter.regexFilter(campo, posicao);
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                sorter.setRowFilter(rf);
            } catch (PatternSyntaxException pse) {
                System.err.println("Erro");
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
            if ("S".equals(str)) {
                setForeground(Color.WHITE);
                setBackground(COR_ESTOQUE_HFF);
            } else {
                // setForeground(Color.WHITE);
                setBackground(Color.WHITE);
            }

            //   setBackground(COR_ESTOQUE_HFF);
            return this;
        }
    }

    public class EditableRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable jTableCarga, Object value, boolean selected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(jTableCarga, value, selected, hasFocus, row, col);

            setVisible(false);

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
        btnManutencao = new javax.swing.JButton();
        btnLiberar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        txtCliente = new org.openswing.swing.client.TextControl();
        btnFiltrar2 = new javax.swing.JButton();
        btnFiltrar3 = new javax.swing.JButton();
        lblPeso = new javax.swing.JLabel();
        lblQtdy = new javax.swing.JLabel();
        lblQtdyPedido = new javax.swing.JLabel();
        txtEstado = new javax.swing.JComboBox<>();
        lblQtdyLogistica = new javax.swing.JLabel();
        lblQtdyAtrazo = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnFiltrar4 = new javax.swing.JButton();
        btnFiltrar8 = new javax.swing.JButton();
        barra = new javax.swing.JProgressBar();
        txtPedidoSelecionado = new org.openswing.swing.client.TextControl();
        txtClienteSelecionado = new org.openswing.swing.client.TextControl();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jTabbedPane1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTabbedPane1KeyPressed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Pedido", "Pré_fatura", "Analise", "Cliente", "Nome", "UF", "Cidade", "Data Pré", "Transporte", "Data Fat", "#", "Gerar", "Minuta", "Peso", "Quantidade", "Situação", "Empresa", "Filial", "Emissão", "Agendado", "Dias Atrazo", "Transportadora"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(400);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(400);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(400);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(40);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(40);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(40);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(150);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(16).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(16).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(16).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(17).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(17).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(17).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(18).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(18).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(19).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(22).setMinWidth(400);
            jTableCarga.getColumnModel().getColumn(22).setPreferredWidth(400);
            jTableCarga.getColumnModel().getColumn(22).setMaxWidth(400);
        }

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDatIni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDatIniActionPerformed(evt);
            }
        });

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtDatFim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDatFimActionPerformed(evt);
            }
        });

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnManutencao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnManutencao.setText("Finalizar");
        btnManutencao.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnManutencao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManutencaoActionPerformed(evt);
            }
        });

        btnLiberar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        btnLiberar.setText("Minuta");
        btnLiberar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnLiberar.setEnabled(false);
        btnLiberar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiberarActionPerformed(evt);
            }
        });

        jLabel1.setText("Pedido");

        jLabel2.setText("Cliente");

        jLabel3.setText("Data Inicio");

        jLabel4.setText("Data Final");

        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoActionPerformed(evt);
            }
        });

        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        btnFiltrar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnFiltrar2.setText("Gerar Minuta");
        btnFiltrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar2ActionPerformed(evt);
            }
        });

        btnFiltrar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        btnFiltrar3.setText("Ped");
        btnFiltrar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar3ActionPerformed(evt);
            }
        });

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

        lblQtdyPedido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdyPedido.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdyPedido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdyPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio_manual.png"))); // NOI18N
        lblQtdyPedido.setBorder(javax.swing.BorderFactory.createTitledBorder("Pedidos Liberados"));
        lblQtdyPedido.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyPedido.setOpaque(true);

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstadoActionPerformed(evt);
            }
        });

        lblQtdyLogistica.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdyLogistica.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdyLogistica.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdyLogistica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        lblQtdyLogistica.setBorder(javax.swing.BorderFactory.createTitledBorder("Meta"));
        lblQtdyLogistica.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyLogistica.setOpaque(true);

        lblQtdyAtrazo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdyAtrazo.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdyAtrazo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdyAtrazo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bug_link.png"))); // NOI18N
        lblQtdyAtrazo.setBorder(javax.swing.BorderFactory.createTitledBorder("Dias Atrazo"));
        lblQtdyAtrazo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyAtrazo.setOpaque(true);

        jLabel5.setText("Região de Venda");

        btnFiltrar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/indu.png"))); // NOI18N
        btnFiltrar4.setText("Pré");
        btnFiltrar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar4ActionPerformed(evt);
            }
        });

        btnFiltrar8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cadeado.png"))); // NOI18N
        btnFiltrar8.setText("Todas");
        btnFiltrar8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar8ActionPerformed(evt);
            }
        });

        txtPedidoSelecionado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedidoSelecionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoSelecionadoActionPerformed(evt);
            }
        });

        txtClienteSelecionado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtClienteSelecionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteSelecionadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(lblQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(lblQtdyPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(lblQtdyLogistica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(lblQtdyAtrazo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(btnLiberar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFiltrar)))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFiltrar2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(btnFiltrar4)
                                .addGap(1, 1, 1)
                                .addComponent(btnFiltrar8)
                                .addGap(4, 4, 4)
                                .addComponent(btnFiltrar3))
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(txtEstado, 0, 0, Short.MAX_VALUE)))
                    .addComponent(barra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPedidoSelecionado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtClienteSelecionado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(4, 4, 4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar2)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar8)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar4)
                    .addComponent(btnFiltrar3)
                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPedidoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClienteSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(lblQtdyPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(lblQtdyLogistica, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(lblQtdyAtrazo, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnManutencao, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                        .addComponent(btnLiberar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar, btnFiltrar2, btnFiltrar3, btnFiltrar4, btnFiltrar8, txtCliente, txtDatFim, txtDatIni, txtEstado, txtPedido});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnLiberar, btnManutencao, lblPeso, lblQtdy, lblQtdyAtrazo, lblQtdyLogistica, lblQtdyPedido});

        jTabbedPane1.addTab("Pedido", jPanel2);

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        jMenu1.setText("Pedido(s)");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/auto.png"))); // NOI18N
        jMenuItem1.setText("Auto");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        jMenuItem2.setText("Moto");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        jMenuItem3.setText("Marketing");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        jMenuItem4.setText("Garantias");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jMenu3.setText("Filtro");

        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-vassoura-16.png"))); // NOI18N
        jMenuItem6.setText("Limpar");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1214, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private int contador = 0;
    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int l = jTableCarga.convertRowIndexToModel(jTableCarga.getSelectedRow());
        int c = jTableCarga.convertColumnIndexToModel(jTableCarga.getSelectedColumn());

        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();

        jTableCarga.clearSelection();
        txtPedido.setText(jTableCarga.getValueAt(linhaSelSit, 1).toString());
        txtCliente.setText(jTableCarga.getValueAt(linhaSelSit, 4).toString());
        txtClienteSelecionado.setText(txtCliente.getText() + " - " + jTableCarga.getValueAt(linhaSelSit, 5).toString());

//        if (txtCliente.getText().isEmpty()) {
//            clienteSelecionado = jTableCarga.getValueAt(linhaSelSit, 4).toString().trim();
//
//        } else {
//            if (clienteSelecionado.equals(txtCliente.getText())) {
//                jTableCarga.getModel().setValueAt(false, l, 12);
//                Mensagem.mensagem("ERROR", " O Pedido " + txtPedido.getText() + " não pertence para o cliente " + txtClienteSelecionado.getText());
//
//            } else {
//
//            }
//        }
//      

    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed

        if (txtPedido.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o Pedido");
        } else {
            try {
                this.clienteSelecionado = "";
                String sql = " and E120PED.numped = '" + txtPedido.getText() + "' ";
                iniciarBarra("sit", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnManutencaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManutencaoActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnManutencaoActionPerformed

    private void btnLiberarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiberarActionPerformed
        try {
            selecionarRange();
        } catch (PropertyVetoException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnLiberarActionPerformed

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed
        if (txtCliente.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {
                this.clienteSelecionado = txtCliente.getText();
                String sql = " and E120PED.codcli = '" + txtCliente.getText() + "' ";
                iniciarBarra("sit", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_btnFiltrar2ActionPerformed

    private void btnFiltrar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar3ActionPerformed

        try {
            limparTela();
            pegarDataDigitada();

            String sql = "and E120PED.DATEMI>= '" + datIni + "' and E120PED.DATEMI <='" + datFim + "' ";
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnFiltrar3ActionPerformed

    private void verificaProduto() {
        if (!txtPedido.getText().trim().equals("")) {
            sqlcodigoProduto = " and pedsis.numped = " + txtPedido.getText().trim() + " ";
        } else {
            sqlcodigoProduto = " ";
        }
    }

    private void verificaCliente() {
        if (!txtCliente.getText().trim().equals("")) {
            sqlcodigoCliente = " and cli.codcli = " + txtCliente.getText().trim() + " ";

        } else {
            sqlcodigoCliente = " ";
        }

    }


    private void lblPesoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPesoMouseClicked

    }//GEN-LAST:event_lblPesoMouseClicked

    private void txtPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoActionPerformed

        if (txtPedido.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", " INFORME O CLIENTE");
        } else {
            try {
                this.clienteSelecionado = "";
                String sql = "and E120PED.numped = '" + txtPedido.getText() + "'";
                iniciarBarra("SIT", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_txtPedidoActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed

        if (txtCliente.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {
                String sql = " and E120PED.codcli = '" + txtCliente.getText() + "' ";
                iniciarBarra("SIT", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_txtClienteActionPerformed

    private void txtDatIniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatIniActionPerformed

        try {
            limparTela();
            pegarDataDigitada();
            String sql = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' ";
            iniciarBarra("SIT", sql);
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_txtDatIniActionPerformed

    private void txtDatFimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatFimActionPerformed

        try {
            pegarDataDigitada();

            String sql = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' ";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_txtDatFimActionPerformed

    private void txtEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadoActionPerformed

        if (!txtEstado.getSelectedItem().equals("TODOS")) {
            try {

                String sql = " and cli.sigufs = '" + txtEstado.getSelectedItem().toString() + "' ";
                iniciarBarra("SIT", sql);
            } catch (Exception ex) {
                Logger.getLogger(IntegrarPesos.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //  Mensagem.mensagemRegistros("ERRO", "Selecione Estado");
        }
    }//GEN-LAST:event_txtEstadoActionPerformed

    private void jTabbedPane1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTabbedPane1KeyPressed
        int keyCode = evt.getKeyCode();
        if (keyCode == KeyEvent.VK_TAB) {
            System.out.println("AQUI CHAMA O TEU PLAY");
        }
    }//GEN-LAST:event_jTabbedPane1KeyPressed

    private void btnFiltrar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar4ActionPerformed
        try {
            limparTela();
            pegarDataDigitada();
            String sql = "and E135PES.DATPRP>= '" + datIni + "' and E135PES.DATPRP <='" + datFim + "' ";

            iniciarBarra("", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnFiltrar4ActionPerformed

    private void btnFiltrar8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar8ActionPerformed
        try {
            limparTela();
            pegarDataDigitada();
            String sql = " AND E135PES.DATPRP + FIL.USU_PERFAT<= '" + datFim + "' ";
            iniciarBarra("", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }

    }//GEN-LAST:event_btnFiltrar8ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

        try {
            pegarDataDigitada();

            String sql = " and E075PRO.codori = 'BM'";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        try {
            this.clienteSelecionado = "";
            pegarDataDigitada();
            String sql = " and E120PED.tnspro in ('90126')";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        try {
            this.clienteSelecionado = "";
            pegarDataDigitada();
            String sql = " and E075PRO.codori = 'BA'";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

        try {
            this.clienteSelecionado = "";
            pegarDataDigitada();
            String sql = " and E120PED.tnspro in ('90112','90113','90222','90122','90123','90223','90133','90134','90132')";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }

    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        try {
            // TODO add your handling code here:
            limparFiltros();
        } catch (SQLException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        } catch (ParseException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void txtPedidoSelecionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoSelecionadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPedidoSelecionadoActionPerformed

    private void txtClienteSelecionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteSelecionadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteSelecionadoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar3;
    private javax.swing.JButton btnFiltrar4;
    private javax.swing.JButton btnFiltrar8;
    private javax.swing.JButton btnLiberar;
    private javax.swing.JButton btnManutencao;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblQtdy;
    private javax.swing.JLabel lblQtdyAtrazo;
    private javax.swing.JLabel lblQtdyLogistica;
    private javax.swing.JLabel lblQtdyPedido;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.TextControl txtClienteSelecionado;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtEstado;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.TextControl txtPedidoSelecionado;
    // End of variables declaration//GEN-END:variables
}
