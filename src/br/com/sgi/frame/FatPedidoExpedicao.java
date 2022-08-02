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
import java.beans.PropertyVetoException;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
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
public final class FatPedidoExpedicao extends InternalFrame {

    private Pedido pedido;
    private List<Pedido> listPedido = new ArrayList<Pedido>();

    private List<Pedido> listPedidoSelecionado = new ArrayList<Pedido>();

    private List<Pedido> listPedidoSemPre = new ArrayList<Pedido>();
    private List<Pedido> listPedidoSemPreMkt = new ArrayList<Pedido>();
    private List<Pedido> listPedidoSelecionadoSemPre = new ArrayList<Pedido>();

    private PedidoDAO pedidoDAO;

    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;

    private String clienteSelecionado = "";
    private String linhaSelecionado = "";

    private String processo;

    public FatPedidoExpedicao() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Minuta de Expedição"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (pedidoDAO == null) {
                pedidoDAO = new PedidoDAO();
            }

            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
            txtDatFim.setDate(this.utilDatas.retornaDataFim(new Date()));
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

            }
        };
        worker.execute();

    }

    private boolean gerarMinuta = false;
    private int qtdpre2 = 0;

    public void selecionarRangeGerar() throws SQLException, Exception {
        String situacaoPre = "";
        String selecionadoAtual = "";
        qtdpre2 = 0;
        gerarMinuta = true;
        // btnLiberar.setEnabled(true);
        if (jTableCarga.getRowCount() > 0) {
            for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                if ((Boolean) jTableCarga.getValueAt(i, 4) && (jTableCarga.getValueAt(i, 25).equals("3") || jTableCarga.getValueAt(i, 25).equals("2") || jTableCarga.getValueAt(i, 25).equals("0"))) {
                    situacaoPre = jTableCarga.getValueAt(i, 25).toString();
                    selecionadoAtual = (jTableCarga.getValueAt(i, 26).toString());
                    if (selecionadoAtual.equals("NF") && situacaoPre.equals("2")) {
                        //  btnLiberar.setEnabled(false);
                        gerarMinuta = false;
                        qtdpre2++;
                    }

                }
            }
        }
    }

    public void selecionarRange() throws SQLException, Exception {
        selecionarRangeGerar();

        if (gerarMinuta) {

            String selecionar = "";
            String selecionadoAtual = "";
            String selecionadoNF = "N";
            String selecionadoNG = "N";
            String selecionadoR = "N";

            if (jTableCarga.getRowCount() > 0) {
                for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                    if ((Boolean) jTableCarga.getValueAt(i, 4) && (jTableCarga.getValueAt(i, 25).equals("3") || jTableCarga.getValueAt(i, 25).equals("0"))) {
                        selecionadoAtual = (jTableCarga.getValueAt(i, 26).toString());

                        if (selecionadoAtual.equals("NF") && selecionadoNF.equals("N") && selecionadoNG.equals("N")) {
                            selecionadoNG = "N";
                            selecionadoR = "N";
                            selecionadoNF = "S";
                            selecionar += (jTableCarga.getValueAt(i, 1).toString() + ",");
                        }
                        if (selecionadoAtual.equals("NG")) {
                            selecionadoNG = "S";
                            selecionadoR = "N";
                            selecionadoNF = "N";
                            selecionar += (jTableCarga.getValueAt(i, 1).toString() + ",");
                        }
                        if (selecionadoAtual.equals("R") && selecionadoNG.equals("N")) {
                            selecionadoNG = "N";
                            selecionadoR = "S";
                            selecionadoNF = "N";
                            selecionar += (jTableCarga.getValueAt(i, 1).toString() + ",");
                        }
                        if (selecionadoAtual.equals("R") && selecionadoNF.equals("S")) {
                            selecionadoNG = "N";
                            selecionadoR = "S";
                            selecionadoNF = "N";
                            selecionar += (jTableCarga.getValueAt(i, 1).toString() + ",");
                        }

                    }
                }
                int tam = selecionar.length();
                if (tam > 0) {
                    if (!selecionar.isEmpty()) {
                        selecionar = selecionar.substring(0, tam - 1);
                    }
                }
                if (!selecionar.isEmpty()) {
                    listPedidoSelecionado = this.pedidoDAO.getPedidosExpedicao("pedido", "and e120ped.numped in (" + selecionar + ")");
                    listPedidoSelecionadoSemPre = this.pedidoDAO.getPedidosExpedicaoSemPre("pedido", "and e120ped.numped in (" + selecionar + ")");
                    if (listPedidoSelecionado != null || listPedidoSelecionadoSemPre != null) {
                        if (listPedidoSelecionado.size() > 0 || listPedidoSelecionadoSemPre.size() > 0) {
                            novoRegistro("", "");
                        }
                    }
                } else {
                    Mensagem.mensagem("ERROR", " Selecione pré-fatura com a situação 3 - Para Faturar");
                }
            }
        } else {
            Mensagem.mensagem("ERROR", " Existem " + qtdpre2 + " pré-faturas na Situação 2 - Em Preparação");
        }
    }

    void retornarPedido(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        PESQUISA_POR = "DATA";
        PESQUISA = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "'";

        getPedidos(PESQUISA_POR, PESQUISA);
    }

    private void getPedidos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {

        if (listPedidoSemPre != null) {
            if (listPedidoSemPre.size() > 0) {
                listPedidoSemPre.clear();
            }
        }

        PESQUISA += " \nand E120PED.usu_libmin not in ('S')";

        listPedido = this.pedidoDAO.getPedidosExpedicao(PESQUISA_POR, PESQUISA);

//        if (!clienteSelecionado.isEmpty() && !linhaSelecionado.isEmpty()) {
//            getPedidosSemPre("ped", "");
//        }
//        if (txtMostrarPedidoOutros.getSelectedItem().equals("SIM")) {
//            getPedidosSemPre("ped", "");
//        }
        getPedidosSemPre("ped", "");
        if (listPedido != null) {
            carregarTabela();
        }
    }

    private void getPedidosSemPre(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        if (!linhaSelecionado.isEmpty()) {
            PESQUISA += " and e075pro.codori ='" + linhaSelecionado + "'";
        }
        if (!clienteSelecionado.isEmpty()) {
            PESQUISA += " and e120ped.codcli ='" + clienteSelecionado + "'";
        }
        if (!clienteSelecionado.isEmpty()) {
            PESQUISA = " and e120ped.codcli ='" + clienteSelecionado + "'";
        }
        listPedidoSemPre = this.pedidoDAO.getPedidosExpedicaoSemPre(PESQUISA_POR, PESQUISA);
        if (listPedidoSemPre != null) {
            if (listPedidoSemPre.size() > 0) {
                for (Pedido pe : listPedidoSemPre) {
                    this.listPedido.add(pe);
                }
            }
        }
//        if (!clienteSelecionado.isEmpty()) {
//            PESQUISA = " and e120ped.codcli ='" + clienteSelecionado + "'";
//        }
//        listPedidoSemPreMkt = this.pedidoDAO.getPedidosExpedicaoSemPreMkt(PESQUISA_POR, PESQUISA);
//        if (listPedidoSemPreMkt != null) {
//            if (listPedidoSemPreMkt.size() > 0) {
//                for (Pedido p : listPedidoSemPreMkt) {
//                    this.listPedido.add(p);
//                }
//            }
//        }
    }

    private void getPedidosSemPreMkt(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        btnLiberar.setEnabled(true);
        if (listPedido != null) {
            if (listPedido.size() > 0) {
                listPedido.clear();
            }
        }
        listPedidoSemPreMkt = this.pedidoDAO.getPedidosExpedicaoSemPreMkt("", PESQUISA);
        if (listPedidoSemPreMkt != null) {
            if (listPedidoSemPreMkt.size() > 0) {
                for (Pedido p : listPedidoSemPreMkt) {
                    this.listPedido.add(p);

                }
            }
        }
        carregarTabela();
    }

    public void carregarTabela() throws Exception {

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
        ImageIcon MktIcon = getImage("/images/garantias.png");
        ImageIcon NotIcon = getImage("/images/Nota.png");
        ImageIcon RomIcon = getImage("/images/NotaRomaneio.png");
        ImageIcon NotGarIcon = getImage("/images/NotaGarantia.png");

        double peso = 0;
        double qtdy = 0;
        double qtdy_atr = 0;
        double qtdy_com = 0;

        double qtdped = 0;
        double qtdpreF = 0;
        double qtdpreP = 0;
        int contador = 0;

        String liberarMinuta = "N";
        // ((DefaultTableModel) jTableCarga.getModel()).setRowCount(0);
        for (Pedido ped : listPedido) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new FatPedido.ColorirRenderer();
            FatPedidoExpedicao.JTableRenderer renderers = new FatPedidoExpedicao.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = BomIcon;
            if (ped.getSituacaopre().equals("2")) {
                linha[0] = ReaIcon;
            }

            linha[1] = ped.getPedido();
            linha[2] = ped.getNumeroanalise();
            linha[3] = ped.getNumeropre();

            columnModel.getColumn(4).setMinWidth(0);
            columnModel.getColumn(4).setMaxWidth(0);
            linha[4] = false;
            if (!clienteSelecionado.isEmpty()) {
                linha[4] = true;
                columnModel.getColumn(4).setMinWidth(50);
                columnModel.getColumn(4).setMaxWidth(50);
                columnModel.getColumn(4).setPreferredWidth(50);
            }
            linha[5] = ped.getTransacao();
            columnModel.getColumn(6).setCellRenderer(renderers);
            linha[6] = NotIcon;
            if (ped.getTipodocumento().equals("R")) {
                linha[6] = RomIcon;
            }
            if (ped.getTipodocumento().equals("NG")) {
                linha[6] = NotGarIcon;
            }
            if (ped.getTipodpedido().equals("MKT")) {
                linha[6] = MktIcon;
            }
            linha[7] = ped.getCliente();
            linha[8] = ped.getCadCliente().getNome();
            linha[9] = ped.getCadCliente().getEstado();
            linha[10] = ped.getCadCliente().getCidade();
            if (this.clienteSelecionado.isEmpty() || this.clienteSelecionado.equals("0")) {
                txtClienteSelecionado.setText("Cliente não selecionado");
            } else {
                txtClienteSelecionado.setText(ped.getCliente() + " - " + ped.getCadCliente().getNome() + " - " + ped.getCadCliente().getCidade() + "-" + ped.getCadCliente().getEstado());
            }

            linha[11] = ped.getDataSeparacaoS();
            linha[12] = ped.getDia_transporte() + " D+1";
            linha[13] = ped.getData_para_faturarS();
            columnModel.getColumn(14).setCellRenderer(renderers);
            if (ped.getLinha().equals("BA")) {
                linha[14] = AutIcon;
            }

            if (ped.getLinha().equals("BM")) {
                linha[14] = MotIcon;
            }
            if (ped.getTipodpedido().equals("MKT")) {
                if (!ped.getLinha().equals("BM") || ped.getLinha().equals("BA")) {
                    linha[14] = MktIcon;
                }
            }

            linha[16] = ped.getPeso();
            peso += ped.getPeso();
            linha[17] = ped.getQuantidade();
            qtdy += ped.getQuantidade();
            linha[18] = ped.getSituacaoLogistica();
            linha[19] = ped.getEmpresa();
            linha[20] = ped.getFilial();
            linha[21] = ped.getEmissaoS();
            linha[22] = ped.getDataAgendamentoS();
            linha[23] = ped.getQtddia_atrazo();
            linha[24] = ped.getCadTransportadora().getCodigoTransportadora() + " - " + ped.getCadTransportadora().getNomeTransportadora();
            linha[25] = ped.getSituacaopre();
            linha[26] = ped.getTipodocumento();

            // qtdped++;
            if (ped.getSituacaopre().equals("3")) {
                qtdpreF++;
            }
            if (ped.getSituacaopre().equals("2")) {
                qtdpreP++;
            }
            if (ped.getSituacaopre().equals("2") && ped.getTipodocumento().equals("N")) {
                liberarMinuta = "S";
            }

            modeloCarga.addRow(linha);
        }

        lblPeso.setText(FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM));
        lblQtdy.setText(FormatarPeso.mascaraQuantidade(qtdy, FormatarPeso.PORCENTAGEM_QTDY));
        lblQtdyPedidoPreFaturar.setText(FormatarPeso.mascaraQuantidade(qtdpreF, FormatarPeso.PORCENTAGEM_QTDY));
        lblQtdyPrePreparacao.setText(FormatarPeso.mascaraQuantidade(qtdpreP, FormatarPeso.PORCENTAGEM_QTDY));
        lblQtdySemPre.setText(FormatarPeso.mascaraQuantidade(qtdped, FormatarPeso.PORCENTAGEM_QTDY));

        btnLiberar.setEnabled(false);
        if (!clienteSelecionado.isEmpty() && !linhaSelecionado.isEmpty()) {
            btnLiberar.setEnabled(true);
        } else {
            if (optMkt.isSelected()) {
                btnLiberar.setEnabled(true);
            }
        }
        // pintarLinhas();
    }

    private void pintarLinhas() {

        jTableCarga.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (table.getValueAt(row, 25).toString().contains("3")) {
                    setBackground(new Color(0, 100, 0));
                    setForeground(Color.white);
                }

                if (table.getValueAt(row, 25).toString().contains("2")) {
                    setBackground(new Color(255, 140, 0));
                    setForeground(Color.black);
                }

                if (table.getValueAt(row, 25).toString().contains("0")) {
                    setBackground(new Color(211, 211, 211));
                    setForeground(Color.white);
                }

//                    if (table.getValueAt(row, 25).toString().contains("4")) {
//                       setBackground(new Color(75,0,130));
//                        setForeground(Color.white);
//                    }
                return this;
            }
        });

    }

    public void retornarPedido() {
        try {
            optTodas.setSelected(true);
            limparTela();
            pegarDataDigitada();
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' ";
            iniciarBarra("", sql);
            txtCliente.requestFocus();

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
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
//        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(3).setCellRenderer(direita);
//        // jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
//        //jTableCarga.getColumnModel().getColumn(11).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(15).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(16).setCellRenderer(direita);
//        jTableCarga.getColumnModel().getColumn(17).setCellRenderer(direita);
//        
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoCreateRowSorter(true);
        jTableCarga.setAutoResizeMode(0);
        // jTableCarga.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    }

    public void origemProcesso(String processo) {

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

        if (optMkt.isSelected()) {
            this.linhaSelecionado = "K";

        }
        frmMinutasExpedicaoGerar sol = new frmMinutasExpedicaoGerar();
        MDIFrame.add(sol, true);
        sol.setMaximum(true); // executa maximizado 
        sol.setPosicao();
        sol.setRecebePedido(this, this.listPedidoSelecionado, listPedidoSelecionadoSemPre, txtClienteSelecionado.getText(), this.linhaSelecionado, this.clienteSelecionado);
       

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

//        pedidoSelecionado = "";
        clienteSelecionado = "";
        linhaSelecionado = "";
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        lblPeso = new javax.swing.JLabel();
        lblQtdy = new javax.swing.JLabel();
        lblQtdyPedidoPreFaturar = new javax.swing.JLabel();
        barra = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        txtCliente = new org.openswing.swing.client.TextControl();
        btnFiltrar2 = new javax.swing.JButton();
        btnLiberar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtMostrarPedidoOutros = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnFiltrar4 = new javax.swing.JButton();
        txtEstado = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        lblQtdySemPre = new javax.swing.JLabel();
        optTodas = new javax.swing.JRadioButton();
        txtClienteSelecionado = new javax.swing.JLabel();
        optMoto = new javax.swing.JRadioButton();
        optAuto = new javax.swing.JRadioButton();
        optPreProcessada = new javax.swing.JRadioButton();
        optFaltaPre = new javax.swing.JRadioButton();
        lblQtdyPrePreparacao = new javax.swing.JLabel();
        optMkt = new javax.swing.JRadioButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemAuto = new javax.swing.JMenuItem();
        jMenuItemMoto = new javax.swing.JMenuItem();
        jMenuItemMkt = new javax.swing.JMenuItem();
        jMenuItemGar = new javax.swing.JMenuItem();
        jMenuItemGarNota = new javax.swing.JMenuItem();
        jMenuItemGarRo = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Pedido", "Analise", "Pré_fatura", "Gerar", "Transação", "#", "Cliente", "Nome", "UF", "Cidade", "Data Pré", "Transporte", "Data Fat", "#", "Minuta", "Peso", "Quantidade", "Situação", "Empresa", "Filial", "Emissão", "Agendado", "Dias Atrazo", "Transportadora", "Situação Pré", "Tipo Documento"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(300);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(300);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(300);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(40);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(40);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(40);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(150);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(16).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(16).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(16).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(17).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(17).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(17).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(18).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(19).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(19).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(19).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(20).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(20).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(20).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(21).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(21).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(21).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(22).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(22).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(22).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(23).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(23).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(23).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(24).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(24).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(24).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(25).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(25).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(25).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(26).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(26).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(26).setMaxWidth(150);
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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Pré-Fatura"));

        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        btnFiltrar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar2ActionPerformed(evt);
            }
        });

        btnLiberar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnLiberar.setText("Gerar");
        btnLiberar.setEnabled(false);
        btnLiberar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiberarActionPerformed(evt);
            }
        });

        jLabel7.setText("Cliente");

        txtMostrarPedidoOutros.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMostrarPedidoOutros.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NAO", "SIM" }));
        txtMostrarPedidoOutros.setEnabled(false);
        txtMostrarPedidoOutros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMostrarPedidoOutrosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMostrarPedidoOutros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btnFiltrar2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLiberar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(4, 4, 4))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMostrarPedidoOutros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLiberar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar2, btnLiberar, txtCliente, txtMostrarPedidoOutros});

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

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

        btnFiltrar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/indu.png"))); // NOI18N
        btnFiltrar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar4ActionPerformed(evt);
            }
        });

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstadoActionPerformed(evt);
            }
        });

        jLabel5.setText("Estado");

        jLabel3.setText("Data Inicio");

        jLabel4.setText("Data Final");

        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoActionPerformed(evt);
            }
        });

        jLabel1.setText("Pedido");

        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFiltrar)))
                .addGap(9, 9, 9)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltrar4))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addContainerGap())
                    .addComponent(txtEstado, 0, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(jLabel1)))
                .addGap(4, 4, 4)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnFiltrar4)
                        .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        lblQtdySemPre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdySemPre.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdySemPre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdySemPre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio_cancelar.png"))); // NOI18N
        lblQtdySemPre.setBorder(javax.swing.BorderFactory.createTitledBorder("Pedidos sem Pré"));
        lblQtdySemPre.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdySemPre.setOpaque(true);

        buttonGroup1.add(optTodas);
        optTodas.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        optTodas.setSelected(true);
        optTodas.setText("Todos");
        optTodas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optTodasActionPerformed(evt);
            }
        });

        txtClienteSelecionado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtClienteSelecionado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        txtClienteSelecionado.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        buttonGroup1.add(optMoto);
        optMoto.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        optMoto.setText("Moto");
        optMoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optMotoActionPerformed(evt);
            }
        });

        buttonGroup1.add(optAuto);
        optAuto.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        optAuto.setText("Auto");
        optAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optAutoActionPerformed(evt);
            }
        });

        buttonGroup3.add(optPreProcessada);
        optPreProcessada.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        optPreProcessada.setSelected(true);
        optPreProcessada.setText("Pré Fatura Processada");
        optPreProcessada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optPreProcessadaActionPerformed(evt);
            }
        });

        buttonGroup3.add(optFaltaPre);
        optFaltaPre.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        optFaltaPre.setText("Falta Processar Pré Fatura");
        optFaltaPre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optFaltaPreActionPerformed(evt);
            }
        });

        lblQtdyPrePreparacao.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdyPrePreparacao.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdyPrePreparacao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdyPrePreparacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio_manual.png"))); // NOI18N
        lblQtdyPrePreparacao.setBorder(javax.swing.BorderFactory.createTitledBorder("Pré-Faturas - Preparação"));
        lblQtdyPrePreparacao.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyPrePreparacao.setOpaque(true);

        buttonGroup1.add(optMkt);
        optMkt.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        optMkt.setText("Marketing");
        optMkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optMktActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(optTodas, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(optAuto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optMoto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optMkt)
                        .addGap(20, 20, 20)
                        .addComponent(optPreProcessada)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optFaltaPre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClienteSelecionado, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(lblQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQtdyPedidoPreFaturar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQtdyPrePreparacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQtdySemPre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {optAuto, optMoto, optTodas});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(optMoto)
                        .addComponent(optAuto)
                        .addComponent(optTodas)
                        .addComponent(optPreProcessada)
                        .addComponent(optFaltaPre)
                        .addComponent(optMkt))
                    .addComponent(txtClienteSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(lblQtdyPedidoPreFaturar, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(lblQtdyPrePreparacao, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQtdySemPre))
                .addGap(6, 6, 6))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblPeso, lblQtdy, lblQtdyPedidoPreFaturar, lblQtdyPrePreparacao, lblQtdySemPre});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {optAuto, optMoto, optTodas});

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        jMenu1.setText("Pedido(s)");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItemAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/auto.png"))); // NOI18N
        jMenuItemAuto.setText("Auto");
        jMenuItemAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAutoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemAuto);

        jMenuItemMoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        jMenuItemMoto.setText("Moto");
        jMenuItemMoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMotoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemMoto);

        jMenuItemMkt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        jMenuItemMkt.setText("Marketing");
        jMenuItemMkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMktActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemMkt);

        jMenuItemGar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        jMenuItemGar.setText("Garantias");
        jMenuItemGar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGarActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGar);

        jMenuItemGarNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/NotaGarantia.png"))); // NOI18N
        jMenuItemGarNota.setText("Garantias NF");
        jMenuItemGarNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGarNotaActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGarNota);

        jMenuItemGarRo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/NotaRomaneio.png"))); // NOI18N
        jMenuItemGarRo.setText("Garantias RO");
        jMenuItemGarRo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGarRoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGarRo);

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

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        jMenu4.setText("Fechar");
        jMenu4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu4ActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1009, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
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

        String estado = jTableCarga.getValueAt(linhaSelSit, 9).toString();
        String cidade = jTableCarga.getValueAt(linhaSelSit, 10).toString();
        jTableCarga.clearSelection();
        txtPedido.setText(jTableCarga.getValueAt(linhaSelSit, 1).toString());
        txtCliente.setText(jTableCarga.getValueAt(linhaSelSit, 7).toString());
        txtClienteSelecionado.setText(txtCliente.getText() + " - " + jTableCarga.getValueAt(linhaSelSit, 8).toString() + " -  " + cidade + "-" + estado);


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

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed
        if (txtCliente.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {
                String sql = "";
                datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
                this.clienteSelecionado = txtCliente.getText();
                if (optAuto.isSelected()) {
                    sql = " and E120PED.codcli = '" + txtCliente.getText() + "' AND E135PES.DATPRP <= '" + datFim + "' \n"
                            + "AND e075pro.codori =  'BA'";
                }
                if (optMoto.isSelected()) {
                    sql = " and E120PED.codcli = '" + txtCliente.getText() + "' AND E135PES.DATPRP <= '" + datFim + "' \n"
                            + "AND e075pro.codori =  'BM'";
                }
                if (optTodas.isSelected()) {
                    sql = " and E120PED.codcli = '" + txtCliente.getText() + "' AND E135PES.DATPRP <= '" + datFim + "'";
                }
                if (optMkt.isSelected()) {
                    sql = " and E120PED.TNSPRO in ('90126')";
                }

                iniciarBarra("sit", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_btnFiltrar2ActionPerformed

    private void verificaProduto() {
//        if (!txtPedido.getText().trim().equals("")) {
//            sqlcodigoProduto = " and pedsis.numped = " + txtPedido.getText().trim() + " ";
//        } else {
//            sqlcodigoProduto = " ";
//        }
    }

    private void verificaCliente() {
//        if (!txtCliente.getText().trim().equals("")) {
//            sqlcodigoCliente = " and cli.codcli = " + txtCliente.getText().trim() + " ";
//
//        } else {
//            sqlcodigoCliente = " ";
//        }

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
                String sql = "";
                datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
                this.clienteSelecionado = txtCliente.getText();
                if (optAuto.isSelected()) {
                    sql = " and E120PED.codcli = '" + txtCliente.getText() + "' AND E135PES.DATPRP <= '" + datFim + "' \n"
                            + "AND e075pro.codori =  'BA'";
                }
                if (optMoto.isSelected()) {
                    sql = " and E120PED.codcli = '" + txtCliente.getText() + "' AND E135PES.DATPRP <= '" + datFim + "' \n"
                            + "AND e075pro.codori =  'BM'";
                }
                if (optTodas.isSelected()) {
                    sql = " and E120PED.codcli = '" + txtCliente.getText() + "' AND E135PES.DATPRP <= '" + datFim + "'";
                }
                if (optMkt.isSelected()) {
                    sql = " and E120PED.TNSPRO in ('90126')";
                }

                iniciarBarra("sit", sql);
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

    private void jMenuItemAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAutoActionPerformed

        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            this.linhaSelecionado = "BA";
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = 'BA' ";
            iniciarBarra("sit", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItemAutoActionPerformed

    private void jMenuItemMotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMotoActionPerformed

        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            this.linhaSelecionado = "BM";
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = 'BM' ";
            iniciarBarra("", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItemMotoActionPerformed

    private void jMenuItemMktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMktActionPerformed
        try {
            this.clienteSelecionado = "";
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            String sql = " and E120PED.tnspro in ('90126')";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItemMktActionPerformed

    private void jMenuItemGarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGarActionPerformed

        try {
            this.clienteSelecionado = "";
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E120PED.TNSPRO IN ('90112','90113','90122','90123') ";
            iniciarBarra("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jMenuItemGarActionPerformed

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

    private void jMenuItemGarNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGarNotaActionPerformed
        try {
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E120PED.TNSPRO IN ('90113','90123') ";
            iniciarBarra("sit", sql);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jMenuItemGarNotaActionPerformed

    private void jMenuItemGarRoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGarRoActionPerformed
        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E120PED.TNSPRO IN ('90112','90122') ";
            iniciarBarra("sit", sql);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jMenuItemGarRoActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed

        //
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void btnLiberarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiberarActionPerformed
        try {
            selecionarRange();
            clienteSelecionado = "";
            btnLiberar.setEnabled(false);
        } catch (PropertyVetoException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnLiberarActionPerformed

    private void jMenu4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenu4ActionPerformed

    private void optAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optAutoActionPerformed
        btnLiberar.setEnabled(false);
        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            this.clienteSelecionado = "";
            txtCliente.setText("");
            txtPedido.setText("");
            this.linhaSelecionado = "BA";

            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = '" + linhaSelecionado + "' ";
            if (optPreProcessada.isSelected()) {
                sql = " and E135PES.sitpes in (3) AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = '" + linhaSelecionado + "' ";
            }
            if (optFaltaPre.isSelected()) {
                sql = " and E135PES.sitpes in (2) AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = '" + linhaSelecionado + "' ";
            }

            iniciarBarra("sit", sql);
            txtCliente.requestFocus();
        } catch (ParseException ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_optAutoActionPerformed

    private void optMotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optMotoActionPerformed
        btnLiberar.setEnabled(false);
        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            this.clienteSelecionado = "";
            txtCliente.setText("");
            txtPedido.setText("");

            this.linhaSelecionado = "BM";
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = '" + linhaSelecionado + "' ";
            if (optPreProcessada.isSelected()) {
                sql = " and E135PES.sitpes in (3) AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = '" + linhaSelecionado + "' ";
            }
            if (optFaltaPre.isSelected()) {
                sql = " and E135PES.sitpes in (2) AND E135PES.DATPRP <= '" + datFim + "' AND E075PRO.CODORI = '" + linhaSelecionado + "' ";
            }
            iniciarBarra("sit", sql);
            txtCliente.requestFocus();

        } catch (ParseException ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_optMotoActionPerformed

    private void optPreProcessadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPreProcessadaActionPerformed
        btnLiberar.setEnabled(false);
        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            this.clienteSelecionado = "";
            txtCliente.setText("");
            txtPedido.setText("");

            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E135PES.SITPES = 3  AND E075PRO.CODORI = '" + linhaSelecionado + "'";
            iniciarBarra("sit", sql);

        } catch (ParseException ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optPreProcessadaActionPerformed

    private void optFaltaPreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optFaltaPreActionPerformed
        btnLiberar.setEnabled(false);
        try {
            datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
            this.clienteSelecionado = "";
            txtCliente.setText("");
            txtPedido.setText("");
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' AND E135PES.SITPES = 2  AND E075PRO.CODORI = '" + linhaSelecionado + "'";
            iniciarBarra("sit", sql);

        } catch (ParseException ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optFaltaPreActionPerformed

    private void optTodasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optTodasActionPerformed
        try {
            limparTela();
            pegarDataDigitada();
            String sql = " AND E135PES.DATPRP <= '" + datFim + "' ";
            iniciarBarra("", sql);
            txtCliente.requestFocus();

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }

    }//GEN-LAST:event_optTodasActionPerformed

    private void txtMostrarPedidoOutrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMostrarPedidoOutrosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMostrarPedidoOutrosActionPerformed

    private void optMktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optMktActionPerformed
        try {
            //   this.linhaSelecionado="MKT";
            getPedidosSemPreMkt("", "");
        } catch (Exception ex) {
            Logger.getLogger(FatPedidoExpedicao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optMktActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar4;
    private javax.swing.JButton btnLiberar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItemAuto;
    private javax.swing.JMenuItem jMenuItemGar;
    private javax.swing.JMenuItem jMenuItemGarNota;
    private javax.swing.JMenuItem jMenuItemGarRo;
    private javax.swing.JMenuItem jMenuItemMkt;
    private javax.swing.JMenuItem jMenuItemMoto;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblQtdy;
    private javax.swing.JLabel lblQtdyPedidoPreFaturar;
    private javax.swing.JLabel lblQtdyPrePreparacao;
    private javax.swing.JLabel lblQtdySemPre;
    private javax.swing.JRadioButton optAuto;
    private javax.swing.JRadioButton optFaltaPre;
    private javax.swing.JRadioButton optMkt;
    private javax.swing.JRadioButton optMoto;
    private javax.swing.JRadioButton optPreProcessada;
    private javax.swing.JRadioButton optTodas;
    private org.openswing.swing.client.TextControl txtCliente;
    private javax.swing.JLabel txtClienteSelecionado;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtEstado;
    private javax.swing.JComboBox<String> txtMostrarPedidoOutros;
    private org.openswing.swing.client.TextControl txtPedido;
    // End of variables declaration//GEN-END:variables
}
