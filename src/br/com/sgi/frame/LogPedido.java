/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Rotas;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.RotasDAO;
import br.com.sgi.dao.TransportadoraDAO;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.ManipularRegistros;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
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
public final class LogPedido extends InternalFrame {

    private Pedido pedido;
    private Rotas Rotas;
    private LogPedido logPedido;
    private List<Pedido> listPedido = new ArrayList<Pedido>();
    private PedidoDAO pedidoDAO;
    private RotasDAO rotasDAO;

    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;
    private String sqlVerificaCheckBox;
    private String codigoRotas;
    private String descricaoRotas;
    private String sqlPesquisaRotas = " ";
    private String sqlEstado = " ";
    private String codigoRota = " ";
    private String codigoEstado = " ";
    private String sqlcodigoProduto = " ";
    private String sqlcodigoCliente = " ";

    private Transportadora transportadora;

    // minuta
    public LogPedido() {
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
            if (rotasDAO == null) {
                rotasDAO = new RotasDAO();
            }
            txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
            txtDatFim.setDate(new Date());
            desabilitarOpcao();
            buscarTransportadoras();
            LoadEstados();
            pegarDataDigitada();
            retornarPedido("'N'");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }

    }

    private void desabilitarOpcao() {
        optAbe.setSelected(false);
        optFec.setSelected(false);
        optFin.setSelected(false);
        optRea.setSelected(false);
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

    private double pesoSelecionado = 0;
    private double quantidadeSelecionado = 0;

    public void selecionarRange() throws SQLException, Exception {
        List<MinutaPedido> listminutaPedido = new ArrayList<MinutaPedido>();
        Minuta minuta = new Minuta();
        String selecionar = "";
        int filialpedido = 0;
        double quantidadenota = 0;
        if (jTableCarga.getRowCount() > 0) {
            for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                if ((Boolean) jTableCarga.getValueAt(i, 9)) {
                    if (!jTableCarga.getValueAt(i, 10).equals("GERADA")) {
                        if (jTableCarga.getValueAt(i, 11).equals("FATURADO")) {
                            quantidadenota++;

                            selecionar = (jTableCarga.getValueAt(i, 1).toString());
                            pesoSelecionado += Double.valueOf(jTableCarga.getValueAt(i, 13).toString());
                            quantidadeSelecionado += Double.valueOf(jTableCarga.getValueAt(i, 14).toString());
                            if (!selecionar.isEmpty()) {
                                getPedido("", " and ped.usu_numped = '" + selecionar + "'");
                                if (pedido != null) {
                                    if (pedido.getPedido() > 0) {
                                        MinutaPedido minutaPedido = new MinutaPedido();
                                        minutaPedido.setCadMinuta(minuta);
                                        minutaPedido.setUsu_codcli(pedido.getCliente());
                                        minutaPedido.setUsu_codemp(pedido.getEmpresa());
                                        minutaPedido.setUsu_codfil(pedido.getFilial());
                                        filialpedido = pedido.getFilial();
                                        minutaPedido.setUsu_codlan(0);
                                        minutaPedido.setUsu_codori(pedido.getLinha());
                                        minutaPedido.setUsu_codpes(0);

                                        minutaPedido.setUsu_codtpr("");
                                        minutaPedido.setUsu_datemi(new Date());
                                        minutaPedido.setUsu_datlib(null);

                                        minutaPedido.setUsu_numnfv(Integer.valueOf(jTableCarga.getValueAt(i, 24).toString()));
                                        minutaPedido.setUsu_codsnf(" ");
                                        minutaPedido.setUsu_lansuc(Integer.valueOf(jTableCarga.getValueAt(i, 25).toString()));

                                        minutaPedido.setUsu_numped(pedido.getPedido());
                                        minutaPedido.setUsu_obsmin("");
                                        minutaPedido.setUsu_pesbal(0.0);

                                        minutaPedido.setUsu_pesped(pedido.getPeso());
                                        minutaPedido.setUsu_pesrec(0.0);
                                        minutaPedido.setUsu_pessuc(pedido.getPeso());
                                        minutaPedido.setUsu_pesnfv(pedido.getPeso());
                                        minutaPedido.setUsu_qtdfat(pedido.getQuantidade());
                                        minutaPedido.setUsu_qtdped(pedido.getQuantidade());
                                        minutaPedido.setUsu_qtdvol(0.0);
                                        minutaPedido.setUsu_seqite(0);
                                        minutaPedido.setUsu_sitmin("ANDAMENTO");
                                        minutaPedido.setUsu_tnspro(pedido.getTransacao());

                                        minutaPedido.setCadCliente(pedido.getCadCliente());
                                        minutaPedido.setEmissaoS(pedido.getEmissaoS());

                                        listminutaPedido.add(minutaPedido);

                                    }
                                }
                            }

                        }

                    }
                }
            }

            txtPesquisar.setValue(selecionar);
            if (filialpedido > 0) {
                minuta.setUsu_codfil(filialpedido);
            } else {
                minuta.setUsu_codfil(1);
            }
            minuta.setUsu_codemp(1);
            minuta.setUsu_pesfat(pesoSelecionado);
            minuta.setUsu_qtdfat(quantidadeSelecionado);
            minuta.setUsu_qtdvol(0);
            if (transportadora != null) {
                if (transportadora.getCodigoTransportadora() > 0) {
                    minuta.setUsu_codtra(transportadora.getCodigoTransportadora());
                    minuta.setCadTransportadora(transportadora);
                }
            }

            if (listminutaPedido.size() > 0) {
                novoRegistroMinuta("LOGISTICA", selecionar, minuta, listminutaPedido);
            }

        }
    }

    private void novoRegistroMinuta(String PROCESSO, String selecionar, Minuta minuta, List<MinutaPedido> listminutaPedido) throws PropertyVetoException, Exception {
        frmMinutasGerar sol = new frmMinutasGerar();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 
        sol.setRecebePalavraPedido(this, PROCESSO, selecionar, minuta, listminutaPedido);
    }

    private void getPedido(String PESQUISA_POR, String PESQUISA) throws SQLException {
        pedido = new Pedido();
        pedido = pedidoDAO.getPedido(PESQUISA_POR, PESQUISA);
        btnLiberar.setEnabled(false);
        if (pedido != null) {
            if (pedido.getPedido() > 0) {
                btnLiberar.setEnabled(true);
            }
        }
    }

    void retornarPedido(String PESQUISA_POR, String PESQUISA) throws ParseException, Exception {
        PESQUISA_POR = "DATA";
        PESQUISA = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "'";
        getPedidos(PESQUISA_POR, PESQUISA);
    }

    private void getPedidos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listPedido = this.pedidoDAO.getPedidos(PESQUISA_POR, PESQUISA);
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
        double qtdy_log = 0;
        double qtdy_com = 0;

        double qtdped = 0;

        for (Pedido ped : listPedido) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            TableCellRenderer renderer = new LogPedido.ColorirRenderer();
            LogPedido.JTableRenderer renderers = new LogPedido.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            columnModel.getColumn(8).setCellRenderer(renderers);

            linha[0] = RuimIcon;
            if (ped.getSituacaoLogistica().equals("S")) {
                linha[0] = MedIcon;
            }
            if (ped.getSituacaoLogistica().equals("F")) {
                linha[0] = BomIcon;
            }
            if (ped.getSituacaoLogistica().equals("R")) {
                linha[0] = ReaIcon;
            }
            if (ped.getSituacaoLogistica().equals("X")) {
                linha[0] = ReaIcon;
            }
            linha[1] = ped.getPedido();
            linha[2] = ped.getCliente();
            linha[3] = ped.getCadCliente().getNome();
            linha[4] = ped.getCadCliente().getEstado();
            linha[5] = ped.getCadCliente().getCidade();
            linha[6] = ped.getDatabloqueioS();
            linha[7] = ped.getDataliberacaoS();
            linha[8] = AutIcon;
            if (ped.getLinha().equals("BM")) {
                linha[8] = MotIcon;
            }
            if (ped.getLinha().equals("GAR")) {
                linha[8] = GarIcon;
            }
            if (ped.getLinha().equals("MKT")) {
                linha[8] = MktIcon;
            }
            linha[9] = false;
            jTableCarga.getColumnModel().getColumn(10).setCellRenderer(renderer);
            linha[10] = "";
            if (ped.getLiberarMinuta().equals("S")) {
                linha[10] = "GERADA";
            }
            linha[11] = ped.getSituacaoPedido();
            linha[12] = ped.getQuantidadedias();
            qtdy_log += ped.getQuantidadedias();

            linha[13] = ped.getPeso();
            peso += ped.getPeso();
            linha[14] = ped.getQuantidade();
            qtdy += ped.getQuantidade();
            linha[15] = ped.getSituacaoLogistica();
            linha[16] = ped.getEmpresa();
            linha[17] = ped.getFilial();
            linha[18] = ped.getEmissaoS();
            linha[19] = ped.getDataAgendamentoS();
            linha[20] = ped.getQuantidadediascomercial();
            qtdy_com += ped.getQuantidadediascomercial();
            linha[21] = ped.getCadTransportadora().getCodigoTransportadora() + " - " + ped.getCadTransportadora().getNomeTransportadora();
            linha[22] = ped.getObservacaobloqueio();
            linha[23] = ped.getLinha();
            linha[24] = ped.getNota();
            linha[25] = ped.getSucata_id();
            qtdped++;
            modeloCarga.addRow(linha);
        }
        lblPeso.setText(FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM));
        lblQtdy.setText(FormatarPeso.mascaraPorcentagem(qtdy, FormatarPeso.PORCENTAGEM));
        if (qtdped > 0) {
            qtdy_log = qtdy_log / qtdped;
            qtdy_com = qtdy_com / qtdped;
        }

        lblQtdyPedido.setText(FormatarPeso.mascaraPorcentagem(qtdped, FormatarPeso.PORCENTAGEM));
        lblQtdyLogistica.setText(FormatarPeso.mascaraPorcentagem(qtdy_log, FormatarPeso.PORCENTAGEM));
        lblQtdyComercial.setText(FormatarPeso.mascaraPorcentagem(qtdy_com, FormatarPeso.PORCENTAGEM));

    }

    public void retornarPedido(String selecao) {
        try {
            pegarDataDigitada();
            String sql = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib in (" + selecao + ")";

            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retornarPedidoTrasportadora(String selecao) {
        try {
            verificaCheckbox();
            String sql = " and pedsis.codtra in (" + selecao + ")" + sqlVerificaCheckBox;
            getPedidos("SIT", sql);
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

        jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(8).setCellRenderer(direita);

        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(14).setCellRenderer(direita);

        jTableCarga.getColumnModel().getColumn(15).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(16).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(17).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(18).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(19).setCellRenderer(direita);

        jTableCarga.setRowHeight(40);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoResizeMode(0);
        jTableCarga.setAutoCreateRowSorter(true);

    }

    public void preencherComboRotas() throws SQLException {

        List<Rotas> listRotas = new ArrayList<Rotas>();
        String desger;
        txtTransportadora.removeAllItems();
        listRotas = rotasDAO.getRotas("", " ");

        if (listRotas != null) {
            for (Rotas rotas : listRotas) {
                codigoRotas = rotas.getCodigoRota();
                descricaoRotas = rotas.getDescricaoRota();
                desger = codigoRotas + " - " + descricaoRotas;
                txtTransportadora.addItem(desger);
            }
        }

    }

    public void toExcelPedido(Component RdiGrid, File file) throws IOException {
        TableModel model = jTableCarga.getModel();
        FileWriter excel = new FileWriter(file);

        for (int i = 0; i < model.getColumnCount(); i++) {
            excel.write(model.getColumnName(i) + "\t");
        }

        excel.write("\n");

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                excel.write(model.getValueAt(i, j).toString() + "\t");
            }
            excel.write("\n");
        }
        excel.close();
        JOptionPane.showMessageDialog(null, "Arquivo exportado com sucesso em:  " + file);

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

        String selecao = "";
        if (optAbe.isSelected()) {
            selecao = "'N'";
        }
        if (optRea.isSelected()) {
            selecao = "'R','X'";
        }

        if (optFin.isSelected()) {
            selecao = "'F'";
        }
        LogPedidoLiberacao sol = new LogPedidoLiberacao();
        MDIFrame.add(sol, true);

        sol.setMaximum(false); // executa maximizado 
        sol.setSize(800, 500);
        sol.setPosicao();
        sol.setRecebePalavra(this, this.pedido, txtPesquisar.getText(), selecao);
    }

    private void LoadEstados() {
        BaseEstado estado = new BaseEstado();
        Map<String, String> mapas = estado.getEstados();
        for (String uf : mapas.keySet()) {
            txtEstado.addItem(mapas.get(uf));
        }
    }

    private void buscarTransportadoras() throws SQLException {
        String codigo = "";
        String nome = "";
        List<Transportadora> listTra = new ArrayList<Transportadora>();
        TransportadoraDAO dao = new TransportadoraDAO();
        listTra = dao.getTransportadoras(" codigo ", " and codtra in (4,118,204,238,9282)");
        txtTransportadora.removeAllItems();
        txtTransportadora.addItem("SELECIONE");
        for (Transportadora t : listTra) {
            codigo = t.getCodigoTransportadora().toString();
            nome = t.getNomeTransportadora();
            txtTransportadora.addItem(codigo + " - " + nome);
        }

    }

    private void limparFiltros() throws SQLException, ParseException {

        txtDatIni.setDate(this.utilDatas.retornaDataIniMes(new Date()));
        txtPedido.setText("");
        txtCliente.setText("");
        desabilitarOpcao();
        LoadEstados();
        pegarDataDigitada();
        retornarPedido("'N'");
        txtEstado.setSelectedItem("TODOS");
    }

    private void verificaComboEstados() {
        codigoEstado = " ";
        codigoEstado = txtEstado.getSelectedItem().toString();
        if (codigoEstado.equals("TODOS")) {
            sqlEstado = " ";
        } else if (!codigoEstado.equals("TODOS")) {
            sqlEstado = "and sigufs ='" + codigoEstado.trim() + "'";
        } else {

        }

    }

    private void pesquisarTable(String pesquisa) {
        DefaultTableModel tabela_produtos = (DefaultTableModel) jTableCarga.getModel();

        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabela_produtos);
        jTableCarga.setRowSorter(sorter);
        // String text = txtPesquisar.getText().toUpperCase();

        int index = pesquisa.indexOf(":");
        String coluna = pesquisa.substring(0, index);
        coluna = coluna.trim();
        int posicao = 0;
        if (coluna.equals("PED") || coluna.equals("ped")) {
            posicao = 1;
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

    void retornarMinuta() {
        try {
            if (txtTransportadora.getSelectedIndex() != -1) {
                if (!txtTransportadora.getSelectedItem().toString().equals("SELECIONE")) {
                    String cod = txtTransportadora.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);
                    retornarPedidoTrasportadora(codcon.trim());
                    TransportadoraDAO dao = new TransportadoraDAO();
                    this.transportadora = dao.getTransportadora(" CodTra ", " and codtra = " + codcon.trim());

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);

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
            if (!"GERADA".equals(str)) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.WHITE);
                setBackground(COR_ESTOQUE_HFF);
            }

            //   setBackground(COR_ESTOQUE_HFF);
            return this;
        }
    }

    public class HabilitarRenderer extends DefaultTableCellRenderer {

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
        optFec = new javax.swing.JRadioButton();
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
        btnFiltrar5 = new javax.swing.JButton();
        btnFiltrar6 = new javax.swing.JButton();
        lblPeso = new javax.swing.JLabel();
        lblQtdy = new javax.swing.JLabel();
        optFin = new javax.swing.JRadioButton();
        lblQtdyPedido = new javax.swing.JLabel();
        btnExcel = new javax.swing.JButton();
        txtEstado = new javax.swing.JComboBox<>();
        lblQtdyLogistica = new javax.swing.JLabel();
        lblQtdyComercial = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnGarantia = new javax.swing.JButton();
        optRea = new javax.swing.JRadioButton();
        btnMarketing = new javax.swing.JButton();
        txtTransportadora = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        btnFiltrar7 = new javax.swing.JButton();
        txtPesquisar = new org.openswing.swing.client.TextControl();

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
                "#", "Pedido", "Cliente", "Nome", "UF", "Cidade", "Data", "Liberação", "#", "Gerar", "Minuta", "Faturado", "Dias", "Peso", "Quantidade", "Situação", "Empresa", "Filial", "Emissão", "Agendado", "Dias Comerc.", "Transportadora", "Observação", "Origem", "Nota", "Lan. Sucata"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false
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
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(400);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(400);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(400);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(40);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(40);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(40);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(150);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(11).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(11).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(12).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(15).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(15).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(15).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(16).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(16).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(16).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(17).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(17).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(17).setMaxWidth(50);
            jTableCarga.getColumnModel().getColumn(18).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(20).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setMinWidth(400);
            jTableCarga.getColumnModel().getColumn(21).setPreferredWidth(400);
            jTableCarga.getColumnModel().getColumn(21).setMaxWidth(400);
            jTableCarga.getColumnModel().getColumn(22).setMinWidth(400);
            jTableCarga.getColumnModel().getColumn(22).setPreferredWidth(400);
            jTableCarga.getColumnModel().getColumn(22).setMaxWidth(400);
            jTableCarga.getColumnModel().getColumn(23).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(23).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(23).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(24).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(24).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(24).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(25).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(25).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(25).setMaxWidth(100);
        }

        buttonGroup1.add(optAbe);
        optAbe.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        optAbe.setForeground(new java.awt.Color(255, 51, 51));
        optAbe.setSelected(true);
        optAbe.setText("Pendente");
        optAbe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optAbeActionPerformed(evt);
            }
        });

        buttonGroup1.add(optFec);
        optFec.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        optFec.setText("Erro");
        optFec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optFecActionPerformed(evt);
            }
        });

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
        btnLiberar.setText("Liberar");
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

        btnFiltrar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar2ActionPerformed(evt);
            }
        });

        btnFiltrar3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnFiltrar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar3ActionPerformed(evt);
            }
        });

        btnFiltrar5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/carros.png"))); // NOI18N
        btnFiltrar5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnFiltrar5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar5ActionPerformed(evt);
            }
        });

        btnFiltrar6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        btnFiltrar6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnFiltrar6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar6ActionPerformed(evt);
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

        buttonGroup1.add(optFin);
        optFin.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        optFin.setForeground(new java.awt.Color(0, 153, 0));
        optFin.setText("Finalizado");
        optFin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optFinActionPerformed(evt);
            }
        });

        lblQtdyPedido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdyPedido.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdyPedido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdyPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio_manual.png"))); // NOI18N
        lblQtdyPedido.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Pedidos"));
        lblQtdyPedido.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyPedido.setOpaque(true);

        btnExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        btnExcel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

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
        lblQtdyLogistica.setBorder(javax.swing.BorderFactory.createTitledBorder("Dias Logistica"));
        lblQtdyLogistica.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyLogistica.setOpaque(true);

        lblQtdyComercial.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblQtdyComercial.setForeground(new java.awt.Color(0, 102, 0));
        lblQtdyComercial.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQtdyComercial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bug_link.png"))); // NOI18N
        lblQtdyComercial.setBorder(javax.swing.BorderFactory.createTitledBorder("Dias Comercial"));
        lblQtdyComercial.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblQtdyComercial.setOpaque(true);

        jLabel5.setText("Região de Venda");

        btnGarantia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        btnGarantia.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnGarantia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGarantiaActionPerformed(evt);
            }
        });

        buttonGroup1.add(optRea);
        optRea.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        optRea.setForeground(new java.awt.Color(51, 51, 255));
        optRea.setText("Reabilitado");
        optRea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optReaActionPerformed(evt);
            }
        });

        btnMarketing.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        btnMarketing.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnMarketing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarketingActionPerformed(evt);
            }
        });

        txtTransportadora.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTransportadora.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtTransportadora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransportadoraActionPerformed(evt);
            }
        });

        jLabel6.setText("Transportadora");

        btnFiltrar7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-vassoura-16.png"))); // NOI18N
        btnFiltrar7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnFiltrar7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar7ActionPerformed(evt);
            }
        });

        txtPesquisar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtTransportadora, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optAbe)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optFec, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(optRea)
                        .addGap(18, 18, 18)
                        .addComponent(optFin))
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(lblPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQtdy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblQtdyPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQtdyLogistica, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQtdyComercial, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLiberar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMarketing, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltrar5, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltrar6, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltrar7, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
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
                                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtDatFim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnFiltrar3))
                                    .addComponent(jLabel4)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(txtEstado, 0, 0, Short.MAX_VALUE))))
                .addGap(4, 4, 4))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {optAbe, optRea});

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar2)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar3)
                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTransportadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(optAbe, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(optFec, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(optRea)
                        .addComponent(optFin)))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQtdy, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQtdyPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQtdyLogistica, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQtdyComercial, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLiberar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMarketing, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar5, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar6, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar7, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFiltrar, btnFiltrar2, btnFiltrar3, txtCliente, txtDatFim, txtDatIni, txtEstado, txtPedido});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {optAbe, optFec, optFin, optRea});

        jTabbedPane1.addTab("Pedido", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String codigolancamento;
    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();

        btnLiberar.setEnabled(false);
        if (evt.getClickCount() == 1) {
            try {
                txtPedido.setText(jTableCarga.getValueAt(linhaSelSit, 1).toString());
                txtCliente.setText(jTableCarga.getValueAt(linhaSelSit, 2).toString());
                getPedido("", " and ped.usu_numped = '" + txtPedido.getText() + "'");
            } catch (SQLException ex) {
                Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (evt.getClickCount() == 2) {
            try {
                novoRegistro("MOTO", "");
            } catch (PropertyVetoException ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void optAbeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optAbeActionPerformed
        // String sql = " and usu_sitlib = 'N'";
        String sql = "and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib = 'N'";
        try {
            if (txtTransportadora.getSelectedIndex() != -1) {
                if (!txtTransportadora.getSelectedItem().toString().equals("SELECIONE")) {
                    String cod = txtTransportadora.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);
                    sql = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib = 'N' and pedsis.codtra = " + codcon;
                }
            }

            getPedidos("SIT", sql);
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_optAbeActionPerformed

    private void optFecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optFecActionPerformed
        try {
            pegarDataDigitada();
            String sql = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib = 'S'";
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_optFecActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        desabilitarOpcao();
        if (txtPedido.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o Pedido");
        } else {
            try {

                String sql = " and ped.usu_numped = '" + txtPedido.getText() + "' ";
                getPedidos("SIT", sql);
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
            novoRegistro("MOTO", "");
        } catch (PropertyVetoException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnLiberarActionPerformed

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed
        desabilitarOpcao();
        if (txtCliente.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {
                pegarDataDigitada();
                String sql = " and usu_codcli = '" + txtCliente.getText() + "' and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "'";
                getPedidos("SIT", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_btnFiltrar2ActionPerformed

    private void btnFiltrar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar3ActionPerformed
        desabilitarOpcao();
        try {
            pegarDataDigitada();
            //verificaCheckbox();
            String sql = "and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' ";
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnFiltrar3ActionPerformed

    private void btnFiltrar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar5ActionPerformed
        desabilitarOpcao();
        try {
            pegarDataDigitada();
            verificaCheckbox();
            String sql = "and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_autmot = 'BA' " + sqlVerificaCheckBox;
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnFiltrar5ActionPerformed

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

    private String sqlTransportadora;

    private void verificaTransportadora() {
        sqlTransportadora = "";
        if (txtTransportadora.getSelectedIndex() != -1) {
            if (!txtTransportadora.getSelectedItem().toString().equals("SELECIONE")) {
                String cod = txtTransportadora.getSelectedItem().toString();
                int index = cod.indexOf("-");
                String codcon = cod.substring(0, index);
                sqlTransportadora = " and pedsis.codtra = " + codcon.trim();
            }
        }
    }

    private void verificaCheckbox() {
        try {
            sqlVerificaCheckBox = "";
            if (optAbe.isSelected()) {
                pegarDataDigitada();
                sqlVerificaCheckBox = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib = 'N' ";

            } else if (optFec.isSelected()) {
                pegarDataDigitada();
                sqlVerificaCheckBox = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib = 'S' ";

            } else if (optRea.isSelected()) {
                pegarDataDigitada();
                sqlVerificaCheckBox = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib IN ('R','X') ";

            } else if (optFin.isSelected()) {

                pegarDataDigitada();
                sqlVerificaCheckBox = "and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib = 'F'";
            } else {

            }

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void btnFiltrar6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar6ActionPerformed
        desabilitarOpcao();
        try {
            pegarDataDigitada();
            verificaCheckbox();
            String sql = "and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_autmot = 'BM' " + sqlVerificaCheckBox;
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFiltrar6ActionPerformed

    private void lblPesoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPesoMouseClicked

    }//GEN-LAST:event_lblPesoMouseClicked

    private void optFinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optFinActionPerformed

        String sql = "and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib = 'F'";
        try {
            if (txtTransportadora.getSelectedIndex() != -1) {
                if (!txtTransportadora.getSelectedItem().toString().equals("SELECIONE")) {
                    String cod = txtTransportadora.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);
                    sql = " and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_sitlib = 'F' and pedsis.codtra = " + codcon;
                }
            }

            getPedidos("SIT", sql);
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }


    }//GEN-LAST:event_optFinActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        desabilitarOpcao();
        if (transportadora == null) {
            Mensagem.mensagem("ERROR", "Selecione a transportadora");
        } else {
            if (transportadora.getCodigoTransportadora() > 0) {
                try {
                    if (ManipularRegistros.pesos(" Gerar minuta")) {
                        selecionarRange();
                    }
                } catch (Exception ex) {
                    Mensagem.mensagem("ERROR", ex.toString());
                }
            }
        }


    }//GEN-LAST:event_btnExcelActionPerformed

    private void btnGarantiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGarantiaActionPerformed
        desabilitarOpcao();
        try {
            pegarDataDigitada();
            verificaCheckbox();

            String sql = "and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_autmot = 'GAR' " + sqlPesquisaRotas + sqlVerificaCheckBox;
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGarantiaActionPerformed

    private void optReaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optReaActionPerformed
        try {

            String sql = " and usu_sitlib IN ('R','X')";
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_optReaActionPerformed

    private void btnMarketingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarketingActionPerformed
        desabilitarOpcao();
        try {
            pegarDataDigitada();
            verificaCheckbox();

            String sql = "and usu_datblo >= '" + datIni + "' and usu_datblo <='" + datFim + "' and usu_autmot = 'MKT'" + sqlPesquisaRotas + sqlVerificaCheckBox;
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnMarketingActionPerformed

    private void txtPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoActionPerformed
        desabilitarOpcao();
        if (txtPedido.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", " INFORME O CLIENTE");
        } else {
            try {
                String sql = "and ped.usu_numped = '" + txtPedido.getText() + "'";
                getPedidos("SIT", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_txtPedidoActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        desabilitarOpcao();

        if (txtCliente.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {

                String sql = " and usu_codcli = '" + txtCliente.getText() + "' and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "'";
                getPedidos("SIT", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnFiltrar7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar7ActionPerformed
        try {
            // TODO add your handling code here:
            limparFiltros();
        } catch (SQLException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        } catch (ParseException ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnFiltrar7ActionPerformed

    private void txtTransportadoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransportadoraActionPerformed
        verificaTransportadora();
        try {
            if (txtTransportadora.getSelectedIndex() != -1) {
                if (!txtTransportadora.getSelectedItem().toString().equals("SELECIONE")) {
                    String cod = txtTransportadora.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);
                    TransportadoraDAO dao = new TransportadoraDAO();
                    this.transportadora = dao.getTransportadora(" codtra ", "and codtra = " + codcon);
                    retornarPedidoTrasportadora(codcon.trim());
                }
            }
        } catch (Exception e) {
        }


    }//GEN-LAST:event_txtTransportadoraActionPerformed

    private void txtDatIniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatIniActionPerformed
        desabilitarOpcao();
        try {
            pegarDataDigitada();
            verificaCheckbox();
            String sql = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' " + sqlVerificaCheckBox;
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_txtDatIniActionPerformed

    private void txtDatFimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatFimActionPerformed
        desabilitarOpcao();
        try {
            pegarDataDigitada();
            verificaCheckbox();
            String sql = " and usu_datblo>= '" + datIni + "' and usu_datblo <='" + datFim + "' " + sqlVerificaCheckBox;
            getPedidos("SIT", sql);

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_txtDatFimActionPerformed

    private void txtEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadoActionPerformed
        String sql = "";
        if (txtEstado.getSelectedIndex() != -1) {
            if (!txtEstado.getSelectedItem().toString().equals("TODOS")) {
                desabilitarOpcao();
                verificaCheckbox();
                verificaTransportadora();
                sql = " and cli.sigufs = '" + txtEstado.getSelectedItem().toString() + "' " + sqlVerificaCheckBox + " " + sqlTransportadora;
            } else {
                verificaCheckbox();
                sql = sqlVerificaCheckBox;
            }
        }
        try {
            getPedidos("SIT", sql);
        } catch (Exception ex) {
            Logger.getLogger(LogPedido.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_txtEstadoActionPerformed

    private void jTabbedPane1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTabbedPane1KeyPressed
        int keyCode = evt.getKeyCode();
        if (keyCode == KeyEvent.VK_TAB) {
            System.out.println("AQUI CHAMA O TEU PLAY");
        }
    }//GEN-LAST:event_jTabbedPane1KeyPressed

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed
        pesquisarTable(txtPesquisar.getText().trim());
    }//GEN-LAST:event_txtPesquisarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar3;
    private javax.swing.JButton btnFiltrar5;
    private javax.swing.JButton btnFiltrar6;
    private javax.swing.JButton btnFiltrar7;
    private javax.swing.JButton btnGarantia;
    private javax.swing.JButton btnLiberar;
    private javax.swing.JButton btnManutencao;
    private javax.swing.JButton btnMarketing;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblQtdy;
    private javax.swing.JLabel lblQtdyComercial;
    private javax.swing.JLabel lblQtdyLogistica;
    private javax.swing.JLabel lblQtdyPedido;
    private javax.swing.JRadioButton optAbe;
    private javax.swing.JRadioButton optFec;
    private javax.swing.JRadioButton optFin;
    private javax.swing.JRadioButton optRea;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtEstado;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.TextControl txtPesquisar;
    private javax.swing.JComboBox<String> txtTransportadora;
    // End of variables declaration//GEN-END:variables
}
