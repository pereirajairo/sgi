package br.com.sgi.frame;

import br.com.sgi.bean.Balanca;
import br.com.sgi.bean.BalancaLancamento;
import br.com.sgi.bean.BalancaParametro;
import br.com.sgi.bean.Caixa;
import br.com.sgi.bean.ControlePesoChumbo;
import br.com.sgi.bean.Embalagem;
import br.com.sgi.bean.Funcionario;
import br.com.sgi.bean.OrdensProducao;
import br.com.sgi.bean.Produto;
import br.com.sgi.dao.BalancaDAO;
import br.com.sgi.dao.BalancaLancamentoDAO;
import br.com.sgi.dao.BalancaParametroDAO;
import br.com.sgi.dao.CaixaDAO;
import br.com.sgi.dao.ControlePesoChumboDAO;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
public class BalancinhaFabricachumbo extends InternalFrame {

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
    private Integer codigoBalancaDestino;

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
    private static ControlePesoChumboDAO controlePesoChumboDAO;
    private static Caixa caixa;
    private BalancinhaConsulta veioCampo;
    private int iValidaSaida = 0;
    private int iValidaRecebimento = 0;

    /**
     * Creates new form BalancinhaFabrica
     */
    public BalancinhaFabricachumbo() throws SQLException, FontFormatException, IOException {
        initComponents();

        File font_file = new File("DigitaldreamSkewNarrow.TTF");

        Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);
        Font sizedFont = font.deriveFont(30f);
        Font sizedFont2 = font.deriveFont(30f);
        txtPeso.setFont(sizedFont);

        jLabel6.setFont(sizedFont);
        lblPesoTotal.setFont(sizedFont);

        //  txtPeso.setText("105.00 KG");
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
        if (controlePesoChumboDAO == null) {
            controlePesoChumboDAO = new ControlePesoChumboDAO();
        }

        Balancinhas.ClosePort();
        Balancinhas.main(null);

        totalRegistro = 0;
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
        getPegarCodigoBalanca();
        preencherComboBalanca(0);

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
        listBalanca = balancaDao.getBalancas("", " and CODBAL <> " + id);

        if (listBalanca != null) {
            for (Balanca balanca : listBalanca) {
                String codigoBalanca = balanca.getCodigoBalanca().toString();
                String descricaoBalanca = balanca.getNomeBalanca();
                desger = codigoBalanca + " - " + descricaoBalanca;
                comboBalanca.addItem(desger);
            }
        }

    }

    public static void getPesoBalanca(String peso) {
        try {
            txtPeso.setText(peso + " KG");
        } catch (Exception e) {
        }
    }

    private void limparCampos() throws Exception {

        txtCodigoFuncionario.setText("");
        lblFuncionario.setText("");

        lblProduto.setText("");
        txtProduto.setText("");
        txtCodigoFuncionario.setEnabled(true);
        txtCodigoFuncionario.requestFocus();
        txtProduto.setEnabled(true);

        codigoRelacionado = 0;

        codigoAgrupador = 0;
        somapeso1 = 0;
        somapeso2 = 0;
        emUso = "";
        lblPesoTotal.setText("0.0 KG");

        btnInicioPesagem.setEnabled(true);
        jLabel2.setEnabled(false);
        txtNtotalCaixas.setEnabled(false);
        txtNtotalCaixas.setText(" ");
        jLabel2.setText(" ");
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
            BalancinhaFabricachumbo.JTableRenderer renderers = new BalancinhaFabricachumbo.JTableRenderer();
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
                preencherComboBalanca(CodigoBalanca);
            }
        }
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
            PegaComboBox();
            int totMin = CalculoHora(0);
            balancaLancamento = new BalancaLancamento();
            balancaLancamento.setCodigoEmpresa(1);
            balancaLancamento.setCodigoFilial(1);
            balancaLancamento.setCodigoBalanca(CodigoBalanca);
            balancaLancamento.setDataLancamento(new Date());
            balancaLancamento.setCodigoProduto(txtProduto.getText().toUpperCase().trim());
            balancaLancamento.setCodigoCaixa(0);
            balancaLancamento.setCodigoEmbalagem(16);
            balancaLancamento.setCodigoFuncionario(codigoFuncionario);
            balancaLancamento.setNomeFuncionario(lblFuncionario.getText().trim());
            balancaLancamento.setPesoBalanca(limpaValorEmbalagem(txtPeso.getText()));
            balancaLancamento.setPesoLiquido(limpaValorEmbalagem(txtPeso.getText()));
            balancaLancamento.setCodigoRelacionado(codigoRelacionado);
            balancaLancamento.setCodigoAgrupamento(codigoAgrupador);
            balancaLancamento.setAgrupamentoRelacionado(agrupadorRelacionado);
            balancaLancamento.setCodigoBalancaDestino(codigoBalancaDestino);
            balancaLancamento.setHoraLancamento(totMin);

        } catch (Exception e) {
        }
    }

    private void PegaComboBox() {
        String codigo = comboBalanca.getSelectedItem().toString();

        if (!codigo.equals("")) {
            int index = codigo.indexOf("-");
            String codigoSelecao = codigo.substring(0, index);

            codigoBalancaDestino = Integer.parseInt(codigoSelecao.trim());

        }
    }

    private boolean VerificarCampos() {
        boolean retorno = true;

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
            if (jRBSaida.isSelected()) {
                comboBalanca.setEnabled(false);

                ControlePesoChumbo d = new ControlePesoChumbo();
                popularRegistro();
                d.setCodigoAgrupador(codigoAgrupador);
                d.setCodigoBalancaDestino(codigoBalancaDestino);
                d.setCodigoProduto(txtProduto.getText().toUpperCase().trim());
                d.setSituacaoLancamento("A");
                balancaLancamento.setCodigoLancamento(balancaLancamentoDAO.proxCodCad());
                if (!balancaLancamentoDAO.inserir(balancaLancamento)) {
                    btnPesarEntrada.setEnabled(false);

                } else {
                    if (iValidaSaida == 0) {
                        if (!controlePesoChumboDAO.inserir(d)) {

                        } else {
                            iValidaSaida = 1;
                        }
                        double peso1 = balancaLancamento.getPesoLiquido();
                        double peso2 = limpaValorEmbalagem(txtPeso.getText());
                        somapeso1 += peso1;
                        somapeso2 += peso2;

                        lblPesoTotal.setText(somapeso2 + " KG");
                    }
                    codigoRelacionado = 0;
                }
            } else if (jRBRecebimento.isSelected()) {
                if (finalPesagem()) {

                    ControlePesoChumbo d = new ControlePesoChumbo();
                    popularRegistro();
                    codigoBalancaDestino = 0;
                    balancaLancamento.setCodigoBalancaDestino(codigoBalancaDestino);
                    d.setCodigoAgrupador(agrupadorRelacionado);
                    d.setCodigoBalancaDestino(CodigoBalanca);
                    d.setCodigoProduto(txtProduto.getText().toUpperCase().trim());
                    d.setSituacaoLancamento("I");
                    balancaLancamento.setCodigoLancamento(balancaLancamentoDAO.proxCodCad());
                    if (!balancaLancamentoDAO.inserir(balancaLancamento)) {
                        btnPesarEntrada.setEnabled(false);

                    } else {
                        if (contRegistro == totalRegistro) {
                            if (!controlePesoChumboDAO.alterar(d)) {

                            } else {

                                btnPesarEntrada.setEnabled(false);
                                JOptionPane.showMessageDialog(null, "Pesagem Finalizada. ",
                                        "Informação", JOptionPane.INFORMATION_MESSAGE);
                            }

                        }

                    }

                } else {
                }
            } else {
            }
        }

    }

    private boolean finalPesagem() throws SQLException {
        boolean retorno = true;
        if (balancaLancamento == null) {
            balancaLancamento = new BalancaLancamento();
        }
        if (agrupadorRelacionado == 0) {
            balancaLancamento = balancaLancamentoDAO.getBalancaLancamento("BALANCINHA", " AND USU_TBALFAB.usu_baldes = " + CodigoBalanca + " \n"
                    + "AND USU_TBALFAB.USU_CODPRO = '" + txtProduto.getText().toUpperCase().trim() + "' \n"
                    + "AND EXISTS ( SELECT 1 FROM usu_tbalclt WHERE USU_CODAGR = USU_TBALFAB.USU_CODAGR AND USU_CODPRO = USU_TBALFAB.USU_CODPRO AND USU_SITLAN ='A'"
                    + " AND USU_BALDES = USU_TBALFAB.usu_baldes)", " ORDER BY USU_DATLAN , USU_HORGER DESC");
            agrupadorRelacionado = balancaLancamento.getCodigoAgrupamento();

        }

        if (totalRegistro == 0) {
            totalRegistro = balancaLancamentoDAO.totalRegistro(PESQUISA_POR,
                    " AND USU_TBALFAB.usu_baldes = " + CodigoBalanca + " \n"
                    + "AND USU_TBALFAB.USU_CODPRO = '" + txtProduto.getText().toUpperCase().trim() + "' \n"
                    + "AND EXISTS ( SELECT 1 FROM usu_tbalclt WHERE USU_CODAGR = USU_TBALFAB.USU_CODAGR AND USU_CODPRO = USU_TBALFAB.USU_CODPRO AND USU_SITLAN ='A'"
                    + " AND USU_BALDES = USU_TBALFAB.usu_baldes)", " ORDER BY USU_DATLAN , USU_HORGER DESC");

            jLabel2.setEnabled(true);
            txtNtotalCaixas.setEnabled(true);
            txtNtotalCaixas.setText(String.valueOf(totalRegistro));
            jLabel2.setText("Total de lançamentos :");
        }
        if (contRegistro < totalRegistro) {

            if ((balancaLancamento != null) && (balancaLancamento.getCodigoBalanca() > 0)) {
                double peso1 = balancaLancamento.getPesoLiquido();
                double peso2 = limpaValorEmbalagem(txtPeso.getText());
                somapeso1 += peso1;
                somapeso2 += peso2;

                lblPesoTotal.setText(somapeso2 + " KG");

                balancaLancamento = balancaLancamentoDAO.getBalancaLancamento("BALANCINHA",
                        " AND USU_TBALFAB.usu_baldes = " + CodigoBalanca + " \n"
                        + "AND USU_TBALFAB.USU_CODPRO = '" + txtProduto.getText().toUpperCase().trim() + "' \n"
                        + "AND EXISTS ( SELECT 1 FROM usu_tbalclt WHERE USU_CODAGR = USU_TBALFAB.USU_CODAGR AND USU_CODPRO = USU_TBALFAB.USU_CODPRO AND USU_SITLAN ='A' "
                        + " AND USU_BALDES = USU_TBALFAB.usu_baldes)", " ORDER BY USU_DATLAN , USU_HORGER DESC");

                if (balancaLancamento == null) {
                    balancaLancamento = new BalancaLancamento();
                }

                codigoRelacionado = balancaLancamento.getCodigoLancamento();   /// BALANCAlANCAMENTO ESTÁ EM NULL NA SEGUNDA LINHA  

                contRegistro++;

                if (contRegistro == totalRegistro) {

                    if ((somapeso2 >= somapeso1 - (1.00 * totalRegistro)) && (somapeso2 <= somapeso1 + (1.00 * totalRegistro))) {
                    } else {
                        JOptionPane.showMessageDialog(null, "Pesagem não corresponde com o peso de entrada. ",
                                "Atenção", JOptionPane.WARNING_MESSAGE);

                        retorno = true;
                    }
                }

            } else {

                retorno = false;
            }

        } else {

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
                    txtProduto.setEnabled(false);

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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        btnPesarEntrada = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblBalanca = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtPeso = new javax.swing.JLabel();
        lblFuncionario = new javax.swing.JLabel();
        txtProduto = new org.openswing.swing.client.TextControl();
        lblProduto = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btnSair = new javax.swing.JButton();
        txtCodigoFuncionario = new org.openswing.swing.client.TextControl();
        btnLimpar = new javax.swing.JButton();
        btnInicioPesagem = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNtotalCaixas = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        comboBalanca = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jRBRecebimento = new javax.swing.JRadioButton();
        jRBSaida = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        lblPesoTotal = new javax.swing.JLabel();

        setTitle("Registro de Peso de Chumbo");
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

        jLabel3.setText("Funcionario:");

        jLabel1.setText("Balança:");

        jLabel15.setText("Produto:");

        txtPeso.setBackground(new java.awt.Color(204, 204, 204));
        txtPeso.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPeso.setForeground(new java.awt.Color(255, 0, 0));
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

        jLabel16.setText("Local destino:");

        comboBalanca.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 - Selecionar Balança", " " }));
        comboBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBalancaActionPerformed(evt);
            }
        });

        jLabel5.setText("Processo:");

        buttonGroup1.add(jRBRecebimento);
        jRBRecebimento.setText("Recebimento");
        jRBRecebimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBRecebimentoActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRBSaida);
        jRBSaida.setText("Saida");
        jRBSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSaidaActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 51, 51));
        jLabel6.setText("PESO TOTAL:");

        lblPesoTotal.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblPesoTotal.setForeground(new java.awt.Color(255, 0, 0));
        lblPesoTotal.setText("0.0 kg");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnInicioPesagem, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesarEntrada)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLimpar)
                        .addGap(165, 165, 165)
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jRBRecebimento)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRBSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtCodigoFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(comboBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPesoTotal)
                                .addGap(68, 68, 68)))
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCodigoFuncionario, txtProduto});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jRBRecebimento)
                            .addComponent(jRBSaida))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCodigoFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(lblPesoTotal))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(comboBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel15)
                                            .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnPesarEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnInicioPesagem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblFuncionario, txtCodigoFuncionario, txtProduto});

        jTabbedPane1.addTab("Pesagem Balança", jPanel2);
        jPanel2.getAccessibleContext().setAccessibleName("Balança");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Balança ");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded

        BalancaParametroDAO balancaParametroDAO = new BalancaParametroDAO();
        BalancaParametro balancaParametro = new BalancaParametro();
        try {
            balancaParametro = balancaParametroDAO.getBalancaParametro();
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabricachumbo.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblBalanca.setText(balancaParametro.getNomeBalanca());
        try {
            getPegarCodigoBalanca();
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabricachumbo.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_formAncestorAdded

    private void comboBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBalancaActionPerformed
        try {
            // getListar("", "");

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_comboBalancaActionPerformed

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
                Logger.getLogger(BalancinhaFabricachumbo.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btnInicioPesagemActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed

        try {
            limparCampos();
        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabricachumbo.class.getName()).log(Level.SEVERE, null, ex);
        }
        btnPesarEntrada.setEnabled(false);
    }//GEN-LAST:event_btnLimparActionPerformed

    private void txtCodigoFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoFuncionarioActionPerformed
        try {
            pegarFuncionario("FUNCIONARIO", txtCodigoFuncionario.getText().trim());
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabricachumbo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtCodigoFuncionarioActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        // TODO add your handling code here:
        Balancinhas.ClosePort();
        try {
            enviar();
        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabricachumbo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnSairActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked

    }//GEN-LAST:event_jTableCargaMouseClicked

    private void txtProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProdutoActionPerformed
        try {
            pegarProduto("1", txtProduto.getText());
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabricachumbo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabricachumbo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtProdutoActionPerformed

    private void btnPesarEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesarEntradaActionPerformed
        try {

            salvarRegistro();
            getListar(PESQUISA_POR, " and  usu_codagr =" + codigoAgrupador + " ");

        } catch (Exception ex) {
            Logger.getLogger(BalancinhaFabricachumbo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPesarEntradaActionPerformed

    private void jRBSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSaidaActionPerformed
        // TODO add your handling code here:
        comboBalanca.setVisible(true);
        jLabel16.setVisible(true);
        jRBRecebimento.setEnabled(false);

    }//GEN-LAST:event_jRBSaidaActionPerformed

    private void jRBRecebimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBRecebimentoActionPerformed

        codigoBalancaDestino = 0;
        comboBalanca.setVisible(false);
        jLabel16.setVisible(false);
        jRBSaida.setEnabled(false);
    }//GEN-LAST:event_jRBRecebimentoActionPerformed

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
    private static javax.swing.ButtonGroup buttonGroup1;
    private static javax.swing.JComboBox<String> comboBalanca;
    private static javax.swing.JLabel jLabel1;
    private static javax.swing.JLabel jLabel14;
    private static javax.swing.JLabel jLabel15;
    private static javax.swing.JLabel jLabel16;
    private static javax.swing.JLabel jLabel2;
    private static javax.swing.JLabel jLabel3;
    private static javax.swing.JLabel jLabel4;
    private static javax.swing.JLabel jLabel5;
    private static javax.swing.JLabel jLabel6;
    private static javax.swing.JPanel jPanel1;
    private static javax.swing.JPanel jPanel2;
    private static javax.swing.JRadioButton jRBRecebimento;
    private static javax.swing.JRadioButton jRBSaida;
    private static javax.swing.JScrollPane jScrollPane3;
    private static javax.swing.JTabbedPane jTabbedPane1;
    private static javax.swing.JTable jTableCarga;
    private static javax.swing.JLabel lblBalanca;
    private static javax.swing.JLabel lblFuncionario;
    private static javax.swing.JLabel lblPesoTotal;
    private static javax.swing.JLabel lblProduto;
    private static org.openswing.swing.client.TextControl txtCodigoFuncionario;
    private static javax.swing.JLabel txtNtotalCaixas;
    private static javax.swing.JLabel txtPeso;
    private static org.openswing.swing.client.TextControl txtProduto;
    // End of variables declaration//GEN-END:variables

}
