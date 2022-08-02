/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Balanca;
import br.com.sgi.bean.BalancaLancamento;
import br.com.sgi.bean.BalancaParametro;
import br.com.sgi.bean.Caixa;
import br.com.sgi.bean.Embalagem;
import br.com.sgi.bean.Funcionario;
import br.com.sgi.bean.OrdensProducao;
import br.com.sgi.bean.Produto;
import br.com.sgi.dao.BalancaDAO;
import br.com.sgi.dao.BalancaLancamentoDAO;
import br.com.sgi.dao.BalancaParametroDAO;
import br.com.sgi.dao.CaixaDAO;
import br.com.sgi.dao.EmbalagemDAO;
import br.com.sgi.dao.OrdensProducaoDAO;
import br.com.sgi.dao.FuncionarioDAO;
import br.com.sgi.dao.ProdutoDAO;
import br.com.sgi.integracao.Balancinhas;
import br.com.sgi.util.CompararDouble;
import br.com.sgi.util.ConversaoHoras;
import br.com.sgi.util.FormatarPeso;
import static br.com.sgi.util.FormatarPeso.limpaValorEmbalagem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.openswing.swing.mdi.client.InternalFrame;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author matheus.luiz
 */
public class BalancinhaFabrica extends InternalFrame {

    private Balanca balanca;
    private BalancaLancamento balancaLancamento;
    private String PESQUISA;
    private static String PESQUISA_POR;
    private static String ORDER;

    private String PROCESSO;
    private String COMPLEMENTO;
    private String PARAMETROCOM;
    private Integer CodigoBalanca;
    private String codigoEmbalagem;
    private String nomeEmbalagem;
    private Double pesoEmbalagem;
    private static Double pesoLiquido;
    private static String pesoEmb;
    private static Double pesoDes;
    private static String indexCalculo;
    private static Integer codigoRelacionado = 0;
    private static String emUso;
    private static int codigoFuncionario;
    private static Integer codigoAgrupador = 0;
    private static Integer agrupadorRelacionado = 0;
    private int totalRegistro = 0;
    private int contRegistro = 0;
    private static String produtoCaixa;
    private static int balancaCaixa;
    private static double somapeso1 = 0;
    private static double somapeso2 = 0;
    private static double somaPesoBruto = 0;
    private static double somaPesoLiquido = 0;

    private List<OrdensProducao> listOP = new ArrayList<OrdensProducao>();
    private List<BalancaLancamento> listBalancaLancamento = new ArrayList<BalancaLancamento>();
    private List<Embalagem> listEmb = new ArrayList<Embalagem>();
    BalancaParametroDAO balancaParametroDao = new BalancaParametroDAO();
    BalancaParametro balancaParametro = new BalancaParametro();
    private BalancaDAO balancaDAO;
    private static Embalagem embalagem;
    private BalancaLancamentoDAO balancaLancamentoDAO;
    private OrdensProducaoDAO ordensProducaoDAO;
    private ProdutoDAO produtoDAO;
    private static EmbalagemDAO embalagemDAO;
    private static CaixaDAO caixaDAO;
    private static Caixa caixa;
    private BalancinhaConsulta veioCampo;

    /**
     * Creates new form BalancinhaFabrica
     */
    public BalancinhaFabrica() throws SQLException, FontFormatException, IOException {
        initComponents();

        File font_file = new File("DigitaldreamSkewNarrow.TTF");

        Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);
        Font sizedFont = font.deriveFont(30f);
        Font sizedFont2 = font.deriveFont(30f);
        txtPeso.setFont(sizedFont);
        txtDescontoCaixa.setFont(sizedFont);
        txtPesoLiquido.setFont(sizedFont);
        jLabel6.setFont(sizedFont);
        lblPesoTotal.setFont(sizedFont);

        // txtPeso.setText("105.00 KG");
        if (balancaDAO == null) {
            balancaDAO = new BalancaDAO();
        }
        if (balancaLancamentoDAO == null) {
            balancaLancamentoDAO = new BalancaLancamentoDAO();
        }
        if (ordensProducaoDAO == null) {
            ordensProducaoDAO = new OrdensProducaoDAO();
        }
        if (produtoDAO == null) {
            produtoDAO = new ProdutoDAO();
        }
        if (embalagemDAO == null) {
            embalagemDAO = new EmbalagemDAO();
        }
        if (caixaDAO == null) {
            caixaDAO = new CaixaDAO();
        }

        Balancinhas.ClosePort();
        Balancinhas.main(null);

        txtDescontoCaixa.setText("0.0 KG");
        codigoRelacionado = 0;
        codigoAgrupador = 0;
        somapeso1 = 0;
        somapeso2 = 0;
        agrupadorRelacionado = 0;
        jLabel2.setEnabled(false);
        txtNtotalCaixas.setEnabled(false);
        if (codigoAgrupador != 0) {
            btnInicioPesagem.setEnabled(false);
        }

    }

    private void enviar() throws Exception {
        if (veioCampo != null) {
            veioCampo.RetornarCampo();
        }
        this.dispose();
    }

    public void setRecebePalavra(BalancinhaConsulta veioInput) throws Exception {
        this.veioCampo = veioInput;

    }

    private void buscarCaixaProduto(String PESQUISA_POR, String PESQUISA, String ORDER) throws SQLException, Exception {
        /*
        balancaLancamento = new BalancaLancamento();
        balancaLancamento = balancaLancamentoDAO.getBalancaLancamento(PESQUISA_POR, PESQUISA, ORDER);
        

        if (balancaLancamento != null) {
            if (balancaLancamento.getCodigoLancamento() > 0) {

                txtProduto.setText(balancaLancamento.getCodigoProduto().trim());
                pegarProduto("1", txtProduto.getText());
                btnPesarEntrada.requestFocus();

            }
        }
         */

    }

    public static void getPesoBalanca(String peso) {
        try {
            txtPeso.setText(peso + " KG");

            calculoPesoLiquido();
        } catch (Exception e) {

        }
    }

    private void limparCampos() throws Exception {

        txtCodigoFuncionario.setText("");
        lblFuncionario.setText("");
        txtCaixa.setText("");
        lblProduto.setText("");
        txtProduto.setText("");
        txtCodigoFuncionario.setEnabled(true);
        txtCodigoFuncionario.requestFocus();
        txtProduto.setEnabled(true);
        txtCaixa.setEnabled(true);
        codigoRelacionado = 0;
        txtDescontoCaixa.setText("0.0 KG");
        codigoAgrupador = 0;
        somapeso1 = 0;
        somapeso2 = 0;
        lblPesoTotal.setText("0.0 KG");
        emUso = "";
        txtDescontoCaixa.setText("0.0 KG");
        btnInicioPesagem.setEnabled(true);
        jLabel2.setEnabled(false);
        txtNtotalCaixas.setEnabled(false);
        txtNtotalCaixas.setText(" ");
        jLabel2.setText(" ");
        calculoPesoLiquido();
        getListar(PESQUISA_POR, " and  usu_codagr =" + 0 + " ");

    }

    private void getBalancaLancamento(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listBalancaLancamento = this.balancaLancamentoDAO.getBalancaLancamentos(PESQUISA_POR, PESQUISA);

        if (listBalancaLancamento != null) {
            carregarTabela();
        }
    }

    public void getListar(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {

        listBalancaLancamento = this.balancaLancamentoDAO.getBalancaLancamentos(PESQUISA_POR, PESQUISA);
        if (listBalancaLancamento != null) {
            carregarTabela();

        }

    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon ProvIcon = getImage("/images/sitAnd.png");
        String data = null;
        double pesoBal = 0.0;
        for (BalancaLancamento mi : listBalancaLancamento) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new SucataEmbarque.ColorirRenderer();
            BalancinhaFabrica.JTableRenderer renderers = new BalancinhaFabrica.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = BomIcon;
            linha[1] = mi.getCodigoAgrupamento();
            linha[2] = mi.getCodigoProduto();
            linha[3] = mi.getPesoBalanca();
            linha[4] = mi.getPesoLiquido();
            modeloCarga.addRow(linha);
        }

    }

    public void getPegarCodigoBalanca() throws SQLException {
        BalancaDAO balancaDao = new BalancaDAO();

        List<Balanca> listBalanca = new ArrayList<Balanca>();

        listBalanca = balancaDao.getBalancas("", " and upper(DesBal) ='" + lblBalanca.getText() + "'");

        if (listBalanca != null) {
            for (Balanca balanca : listBalanca) {
                CodigoBalanca = balanca.getCodigoBalanca();
            }

        }
    }

    public static void calculoPesoLiquido() {

        txtDescontoCaixa.setText(caixa.getPesoCaixa().toString() + " KG");

        pesoDes = caixa.getPesoCaixa();
        String peso = txtPeso.getText();
        int index2 = peso.indexOf("KG");
        String pesoConcat = peso.substring(0, index2);
        double pesoBal = Double.parseDouble(pesoConcat);
        pesoLiquido = pesoBal - pesoDes;
        String pesoConvertidoLiq = FormatarPeso.mascaraPorcentagem(pesoLiquido, FormatarPeso.PORCENTAGEM);
        txtPesoLiquido.setText(pesoConvertidoLiq + " KG");

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public static Integer CalculoHora(int totMin) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        Date data = calendar.getTime();
        SimpleDateFormat sdhora = new SimpleDateFormat("HH:mm");
        String hora = sdhora.format(data);
        ConversaoHoras coversaoHoras = new ConversaoHoras();
        totMin = coversaoHoras.ConverterHoras(hora);
        return totMin;
    }

    private void popularRegistro() {
        try {

            int totMin = CalculoHora(0);
            balancaLancamento = new BalancaLancamento();
            balancaLancamento.setCodigoEmpresa(1);
            balancaLancamento.setCodigoFilial(1);
            balancaLancamento.setCodigoBalanca(CodigoBalanca);
            balancaLancamento.setDataLancamento(new Date());
            balancaLancamento.setCodigoProduto(txtProduto.getText().toUpperCase());
            balancaLancamento.setCodigoCaixa(Integer.parseInt(txtCaixa.getText()));
            balancaLancamento.setCodigoFuncionario(codigoFuncionario);
            balancaLancamento.setCodigoEmbalagem(1);
            balancaLancamento.setNomeFuncionario(lblFuncionario.getText().trim());
            balancaLancamento.setPesoBalanca(limpaValorEmbalagem(txtPeso.getText()));
            balancaLancamento.setPesoLiquido(limpaValorEmbalagem(txtPesoLiquido.getText()));
            balancaLancamento.setCodigoRelacionado(codigoRelacionado);
            balancaLancamento.setCodigoAgrupamento(codigoAgrupador);
            balancaLancamento.setAgrupamentoRelacionado(agrupadorRelacionado);
            balancaLancamento.setCodigoBalancaDestino(0);
            balancaLancamento.setHoraLancamento(totMin);

        } catch (Exception e) {
        }
    }

    private boolean VerificarCampos() {
        boolean retorno = true;
        if (!txtCaixa.getText().equals("")) {
        } else {

            JOptionPane.showMessageDialog(null, "Favor infomar uma caixa  ",
                    "Atenção", JOptionPane.WARNING_MESSAGE);

            retorno = false;
        }
        if (!txtCodigoFuncionario.getText().isEmpty()) {
        } else {

            JOptionPane.showMessageDialog(null, "Favor infomar codigo do funcionario  ",
                    "Atenção", JOptionPane.WARNING_MESSAGE);

            retorno = false;
        }
        if (!txtProduto.getText().equals("")) {
        } else {

            JOptionPane.showMessageDialog(null, "Favor infomar o produto  ",
                    "Atenção", JOptionPane.WARNING_MESSAGE);

            retorno = false;
        }
        return retorno;
    }

    private void salvarRegistro() throws SQLException, Exception {

        if (VerificarCampos()) {
            if ((emUso.equals("N")) && (!CodigoBalanca.equals(balancaCaixa))) {
                if (iniciarPesagem()) {
                    popularRegistro();

                    balancaLancamento.setCodigoLancamento(balancaLancamentoDAO.proxCodCad());
                    if (!balancaLancamentoDAO.inserir(balancaLancamento)) {
                        btnPesarEntrada.setEnabled(false);
                    } else {

                        somaPesoBruto += limpaValorEmbalagem(txtPeso.getText());
                        somaPesoLiquido += limpaValorEmbalagem(txtPesoLiquido.getText());
                        lblPesoTotal.setText(" BRUTO: " + somaPesoBruto + " KG  LIQUIDO: " + somaPesoLiquido + " KG");

                        txtCaixa.setText("");
                        txtCaixa.setEnabled(true);
                        txtCaixa.requestFocus();
                        //  btnPesarEntrada.setEnabled(true);
                        codigoRelacionado = 0;

                    }
                }
            } else if ((emUso.equals("S")) && (!CodigoBalanca.equals(balancaCaixa))) {
                if (finalPesagem()) {
                    popularRegistro();

                    balancaLancamento.setCodigoLancamento(balancaLancamentoDAO.proxCodCad());
                    if (!balancaLancamentoDAO.inserir(balancaLancamento)) {
                        btnPesarEntrada.setEnabled(false);
                    } else {

                        //btnPesarEntrada.setEnabled(true);
                        txtCaixa.setText("");
                        txtCaixa.setEnabled(true);
                        btnPesarEntrada.setEnabled(true);
                        txtCaixa.requestFocus();
                        codigoRelacionado = 0;

                    }
                    if (contRegistro == totalRegistro) {
                        btnPesarEntrada.setEnabled(false);
                        JOptionPane.showMessageDialog(null, "Pesagem Finalizada. ",
                                "Informação", JOptionPane.INFORMATION_MESSAGE);

                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Caixa está em sento usada e o inicio da pessagem ocorreu nessa balança  ",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        } else {

        }
    }

    private boolean iniciarPesagem() throws SQLException {
        boolean retorno = true;
        // VEREFICA SE A CAIXA ESTÁ COM O STATUS N

        if (emUso.equals("N")) {
            caixa.setEmusuCaixa("S");
            caixa.setCodigoBalanca(CodigoBalanca);
            caixa.setCodigoProduto(txtProduto.getText().trim().toUpperCase());
            if (!caixaDAO.alterarEmuso(caixa)) {
                retorno = false;
            } else {

                retorno = true;
            }
        } else {
            retorno = false;
            JOptionPane.showMessageDialog(null, "Caixa está em sento usada  ",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            txtCaixa.setText(" ");
        }

        return retorno;

    }

    private boolean finalPesagem() throws SQLException {
        boolean retorno = true;

        if (balancaLancamento == null) {
            balancaLancamento = new BalancaLancamento();
        }
        if (agrupadorRelacionado == 0) {
            balancaLancamento = balancaLancamentoDAO.getBalancaLancamento("BALANCINHA",
                    " AND USU_CODCAI = " + Integer.parseInt(txtCaixa.getText()) + "  AND USU_CODPRO = '" + txtProduto.getText().toUpperCase().trim() + "' \n"
                    + " AND USU_CODBAL <> " + CodigoBalanca + " and USU_AGRREL = 0 \n ",
                    " ORDER BY USU_DATLAN , USU_HORGER DESC");
            agrupadorRelacionado = balancaLancamento.getCodigoAgrupamento();
        }

        if (balancaLancamento.getCodigoBalanca().equals(Integer.parseInt(txtCaixa.getText().trim()))) {
            balancaLancamento = balancaLancamentoDAO.getBalancaLancamento("BALANCINHA",
                    " AND USU_CODCAI = " + Integer.parseInt(txtCaixa.getText()) + "  AND USU_CODPRO = '" + txtProduto.getText().toUpperCase().trim() + "' \n"
                    + " AND USU_CODBAL <> " + CodigoBalanca + " and USU_AGRREL = 0 \n ",
                    " ORDER BY USU_DATLAN , USU_HORGER DESC");
        }
        if (totalRegistro == 0) {
            /* totalRegistro = balancaLancamentoDAO.totalRegistro(PESQUISA_POR,
                    " AND USU_CODCAI = " + Integer.parseInt(txtCaixa.getText()) + "  AND USU_CODPRO = '" + txtProduto.getText().toUpperCase().trim() + "' \n"
                    + " AND USU_CODBAL <> " + CodigoBalanca + "  and usu_codagr = " + agrupadorRelacionado + " ",
                    " ORDER BY USU_DATLAN , USU_HORGER DESC");
             */
            totalRegistro = balancaLancamentoDAO.totalRegistro(PESQUISA_POR,
                    "   AND USU_CODPRO = '" + txtProduto.getText().toUpperCase().trim() + "' \n"
                    + " AND USU_CODBAL <> " + CodigoBalanca + "  and usu_codagr = " + agrupadorRelacionado + " ",
                    " ORDER BY USU_DATLAN , USU_HORGER DESC");

            jLabel2.setEnabled(true);
            txtNtotalCaixas.setEnabled(true);
            txtNtotalCaixas.setText(String.valueOf(totalRegistro));
            jLabel2.setText("Total de lançamentos :");
        }
        if (contRegistro < totalRegistro) {

            if ((balancaLancamento != null) && (balancaLancamento.getCodigoBalanca() > 0)) {
                double peso1 = balancaLancamento.getPesoLiquido();
                double peso2 = limpaValorEmbalagem(txtPesoLiquido.getText());
                somapeso1 += peso1;
                somapeso2 += peso2;
                somaPesoBruto += limpaValorEmbalagem(txtPeso.getText());
                somaPesoLiquido += limpaValorEmbalagem(txtPesoLiquido.getText());
                lblPesoTotal.setText(" BRUTO: " + somaPesoBruto + " KG  LIQUIDO: " + somaPesoLiquido + " KG");
                balancaLancamento = balancaLancamentoDAO.getBalancaLancamento("BALANCINHA",
                        " AND USU_CODCAI = " + Integer.parseInt(txtCaixa.getText()) + "  AND USU_CODPRO = '" + txtProduto.getText().toUpperCase().trim() + "' \n"
                        + "  and usu_codagr = " + agrupadorRelacionado + " ",
                        " ORDER BY USU_DATLAN , USU_HORGER DESC");

                codigoRelacionado = balancaLancamento.getCodigoLancamento();
                caixa.setCodigoCaixa(Integer.parseInt(txtCaixa.getText()));
                caixa.setEmusuCaixa("N");
                caixa.setCodigoBalanca(0);
                caixa.setCodigoProduto(" ");
                contRegistro++;

                if (!caixaDAO.alterarEmuso(caixa)) {
                    retorno = true;

                } else {

                }

                if (contRegistro == totalRegistro) {

                    if ((somapeso2 >= somapeso1 - (1.00 * totalRegistro)) && (somapeso2 <= somapeso1 + (1.00 * totalRegistro))) {
                    } else {
                        JOptionPane.showMessageDialog(null, "Pesagem não corresponde com o peso de entrada. ",
                                "Atenção", JOptionPane.WARNING_MESSAGE);

                        retorno = true;
                    }
                }

            } else {
                JOptionPane.showMessageDialog(null, "Produto " + txtProduto.getText().toUpperCase().trim() + " não corresponde com "
                        + "o produto vinculado com a caixa " + Integer.parseInt(txtCaixa.getText()) + ".",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                retorno = false;
            }

        } else {
            JOptionPane.showMessageDialog(null, "Não tem peso aberto para a caixa " + Integer.parseInt(txtCaixa.getText()) + " ",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            retorno = false;
        }

        return retorno;
    }

    private void pegarProduto(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        if (!PESQUISA.isEmpty()) {
            Produto produto = new Produto();
            produto = produtoDAO.getProduto(1, PESQUISA);
            if (produto != null) {
                if (produto.getCodigoproduto() != null) {
                    lblProduto.setText(produto.getDescricaoproduto());
                    // btnPesarEntrada.setEnabled(true);
                    txtProduto.setEnabled(false);

                }

            }
        }
    }

    private void pegaCaixa(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {

        if (!PESQUISA.isEmpty()) {
            if (caixa == null) {
                caixa = new Caixa();
            }

            caixa = caixaDAO.getCaixa("CAIXA", PESQUISA);
            if (caixa != null) {
                if (caixa.getCodigoCaixa() != null) {
                    txtDescontoCaixa.setText(caixa.getPesoCaixa().toString() + " KG");
                    emUso = caixa.getEmusuCaixa();
                    if (emUso.equals("S")) {
                        produtoCaixa = caixa.getCodigoProduto();
                        balancaCaixa = caixa.getCodigoBalanca();
                        txtProduto.setText(produtoCaixa.trim());
                        pegarProduto("1", produtoCaixa.trim());
                        btnPesarEntrada.requestFocus();
                    } else {
                        produtoCaixa = " ";
                        balancaCaixa = 0;
                    }

                    txtProduto.requestFocus();
                    txtCaixa.setEnabled(false);
                    calculoPesoLiquido();

                } else {

                }

            }

        }

    }

    private void pegarFuncionario(String PESQUISA_POR, String barcode) throws SQLException {
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        if (!barcode.isEmpty()) {

            Funcionario fun = new Funcionario();
            fun = funcionarioDAO.getFuncionarioSapiens(PESQUISA_POR, " and USU_CODCRA ='" + barcode.trim() + "'");
            if (fun != null) {
                if (fun.getCodigoFuncionario() > 0) {
                    codigoFuncionario = fun.getCodigoFuncionario();
                    lblFuncionario.setText(fun.getNomeFuncionario());

                    txtCaixa.setEnabled(true);
                    txtCaixa.requestFocus();
                    txtCodigoFuncionario.setEnabled(false);

                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ler cogio de barras");
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        btnPesarEntrada = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblBalanca = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtPeso = new javax.swing.JLabel();
        lblFuncionario = new javax.swing.JLabel();
        txtProduto = new org.openswing.swing.client.TextControl();
        txtCaixa = new org.openswing.swing.client.TextControl();
        lblProduto = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btnSair = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtDescontoCaixa = new javax.swing.JLabel();
        txtPesoLiquido = new javax.swing.JLabel();
        txtCodigoFuncionario = new org.openswing.swing.client.TextControl();
        btnLimpar = new javax.swing.JButton();
        btnInicioPesagem = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNtotalCaixas = new javax.swing.JLabel();
        btnTrocarCaixa = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        lblPesoTotal = new javax.swing.JLabel();

        setTitle("Registro de Peso de Refugo");
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                formAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        btnPesarEntrada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-balança-industrial-16.png"))); // NOI18N
        btnPesarEntrada.setText("Pesar");
        btnPesarEntrada.setEnabled(false);
        btnPesarEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesarEntradaActionPerformed(evt);
            }
        });

        jLabel14.setText("Peso:");

        jLabel13.setText("Caixa:");

        jLabel3.setText("Funcionario:");

        jLabel1.setText("Balança:");

        jLabel15.setText("Produto:");

        txtPeso.setBackground(new java.awt.Color(204, 204, 204));
        txtPeso.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPeso.setForeground(new java.awt.Color(255, 0, 51));
        txtPeso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPeso.setText("0000");
        txtPeso.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        txtPeso.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        txtPeso.setOpaque(true);

        lblFuncionario.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtProduto.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProdutoActionPerformed(evt);
            }
        });

        txtCaixa.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCaixa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCaixaActionPerformed(evt);
            }
        });

        lblProduto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jTableCarga.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "COD AGRUPADOR", "PRODUTO", "PESO BRUTO", "PESO LIQUIDO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCarga.setVerifyInputWhenFocusTarget(false);
        jTableCarga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCargaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableCarga);

        jLabel4.setText("Pesos:");

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        jLabel17.setText("Desconto Caixa:");

        jLabel19.setText("Peso Líquido:");

        txtDescontoCaixa.setBackground(new java.awt.Color(204, 204, 204));
        txtDescontoCaixa.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtDescontoCaixa.setForeground(new java.awt.Color(255, 0, 0));
        txtDescontoCaixa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDescontoCaixa.setText("0000");
        txtDescontoCaixa.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        txtDescontoCaixa.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        txtDescontoCaixa.setOpaque(true);

        txtPesoLiquido.setBackground(new java.awt.Color(204, 204, 204));
        txtPesoLiquido.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPesoLiquido.setForeground(new java.awt.Color(255, 0, 0));
        txtPesoLiquido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPesoLiquido.setText("0000");
        txtPesoLiquido.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        txtPesoLiquido.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        txtPesoLiquido.setOpaque(true);

        txtCodigoFuncionario.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCodigoFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoFuncionarioActionPerformed(evt);
            }
        });

        btnLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-vassoura-16.png"))); // NOI18N
        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        btnInicioPesagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-balança-industrial-16.png"))); // NOI18N
        btnInicioPesagem.setText("Iniciar Pesagem");
        btnInicioPesagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInicioPesagemActionPerformed(evt);
            }
        });

        jLabel2.setEnabled(false);

        txtNtotalCaixas.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNtotalCaixas, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(246, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNtotalCaixas))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        btnTrocarCaixa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-vassoura-16.png"))); // NOI18N
        btnTrocarCaixa.setText("Trocar Caixa");
        btnTrocarCaixa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTrocarCaixaActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 51, 51));
        jLabel6.setText("PESO TOTAL:");

        lblPesoTotal.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblPesoTotal.setForeground(new java.awt.Color(255, 0, 0));
        lblPesoTotal.setText("0.0 kg");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel15)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel13)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCaixa, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lblProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(txtCodigoFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPesoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(140, 140, 140)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtDescontoCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPesoLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnInicioPesagem, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPesarEntrada)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLimpar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnTrocarCaixa)
                                .addGap(46, 46, 46))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCaixa, txtCodigoFuncionario, txtProduto});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(95, 95, 95)
                                .addComponent(lblProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lblPesoTotal)))
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCodigoFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addComponent(jLabel15)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel4))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel17)
                                .addComponent(jLabel19)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnPesarEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnInicioPesagem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtPesoLiquido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(7, 7, 7))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtPeso, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                                        .addComponent(txtDescontoCaixa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTrocarCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(txtCaixa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblFuncionario, lblProduto, txtCaixa, txtCodigoFuncionario, txtProduto});

        jTabbedPane1.addTab("Pesagem Balança", jPanel2);
        jPanel2.getAccessibleContext().setAccessibleName("Balança");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1088, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Balança ");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void txtCaixaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCaixaActionPerformed
        try {
            // TODO add your handling code here:

            pegaCaixa("CAIXA", " AND  USU_SITCAI = 'A' and USU_CODCAI = " + txtCaixa.getText().trim() + " ");
            if ((emUso.equals("S")) && (!CodigoBalanca.equals(balancaCaixa))) {
                buscarCaixaProduto("LANCAMENTO", "AND USU_CODCAI = " + txtCaixa.getText().trim() + "", "ORDER BY USU_DATLAN, USU_HORGER DESC");

            } else if ((emUso.equals("S")) && (CodigoBalanca.equals(balancaCaixa))) {
                JOptionPane.showMessageDialog(null, "Caixa está em sento usada e o inicio da pessagem ocorreu nessa balança  ",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                limparCampos();
            } else if (emUso.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Favor informar uma caixa Valida! ",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Favor informar uma caixa Valida! ",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_txtCaixaActionPerformed

    private void txtProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProdutoActionPerformed
        try {
            pegarProduto("1", txtProduto.getText());
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtProdutoActionPerformed

    private void btnPesarEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesarEntradaActionPerformed
        try {

            salvarRegistro();
            getListar(PESQUISA_POR, " and  usu_codagr =" + codigoAgrupador + " ");

        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnPesarEntradaActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked

    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        // TODO add your handling code here:
        Balancinhas.ClosePort();
        try {
            enviar();
        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnSairActionPerformed

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded

        BalancaParametroDAO balancaParametroDAO = new BalancaParametroDAO();
        BalancaParametro balancaParametro = new BalancaParametro();
        try {
            balancaParametro = balancaParametroDAO.getBalancaParametro();
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblBalanca.setText(balancaParametro.getNomeBalanca());
        try {
            getPegarCodigoBalanca();
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_formAncestorAdded

    private void txtCodigoFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoFuncionarioActionPerformed
        try {
            pegarFuncionario("FUNCIONARIO", txtCodigoFuncionario.getText().trim());
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_txtCodigoFuncionarioActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed

        try {
            limparCampos();
        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }
        btnPesarEntrada.setEnabled(false);

    }//GEN-LAST:event_btnLimparActionPerformed

    private void btnInicioPesagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInicioPesagemActionPerformed

        if (codigoAgrupador == 0) {
            try {

                if (balancaLancamento == null) {
                    balancaLancamento = new BalancaLancamento();
                }
                balancaLancamento.setCodigoAgrupamento(balancaLancamentoDAO.proxCodAgr());
                codigoAgrupador = balancaLancamento.getCodigoAgrupamento();
                btnInicioPesagem.setEnabled(false);
                btnPesarEntrada.setEnabled(true);
            } catch (SQLException ex) {
                Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btnInicioPesagemActionPerformed

    private void btnTrocarCaixaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTrocarCaixaActionPerformed
        txtCaixa.setText(" ");
        txtCaixa.setEnabled(true);
    }//GEN-LAST:event_btnTrocarCaixaActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        try {
            // TODO add your handling code here:
            enviar();
        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formInternalFrameClosing

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        // jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        //jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        //jTableCarga.getColumnModel().getColumn(5).setCellRenderer(direita);
        //jTableCarga.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableCarga.setRowHeight(35);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //  jTableCarga.setAutoResizeMode(0);

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

    private ImageIcon getImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return (new ImageIcon(url));
        } else {
            return null;

        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton btnInicioPesagem;
    private static javax.swing.JButton btnLimpar;
    private static javax.swing.JButton btnPesarEntrada;
    private static javax.swing.JButton btnSair;
    private static javax.swing.JButton btnTrocarCaixa;
    private static javax.swing.JLabel jLabel1;
    private static javax.swing.JLabel jLabel13;
    private static javax.swing.JLabel jLabel14;
    private static javax.swing.JLabel jLabel15;
    private static javax.swing.JLabel jLabel17;
    private static javax.swing.JLabel jLabel19;
    private static javax.swing.JLabel jLabel2;
    private static javax.swing.JLabel jLabel3;
    private static javax.swing.JLabel jLabel4;
    private static javax.swing.JLabel jLabel6;
    private static javax.swing.JPanel jPanel1;
    private static javax.swing.JPanel jPanel2;
    private static javax.swing.JScrollPane jScrollPane3;
    private static javax.swing.JTabbedPane jTabbedPane1;
    private static javax.swing.JTable jTableCarga;
    private static javax.swing.JLabel lblBalanca;
    private static javax.swing.JLabel lblFuncionario;
    private static javax.swing.JLabel lblPesoTotal;
    private static javax.swing.JLabel lblProduto;
    private static org.openswing.swing.client.TextControl txtCaixa;
    private static org.openswing.swing.client.TextControl txtCodigoFuncionario;
    private static javax.swing.JLabel txtDescontoCaixa;
    private static javax.swing.JLabel txtNtotalCaixas;
    private static javax.swing.JLabel txtPeso;
    private static javax.swing.JLabel txtPesoLiquido;
    private static org.openswing.swing.client.TextControl txtProduto;
    // End of variables declaration//GEN-END:variables

}
