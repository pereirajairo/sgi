/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.SucataDAO;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.CompararDouble;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.ExecutarWS;
import br.com.sgi.ws.WSEmailAtendimento;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class SucataManualIndustrializacao extends InternalFrame {

    private Sucata sucata;
    private List<Pedido> listPedido = new ArrayList<Pedido>();
    private SucataDAO sucataDAO;
    private SucataMovimentoDAO sucataMovimentoDAO;
    private SucataMovimento sucataMovimento;
    private SucatasManutencao veioCampo;
    private SucataContaCorrenteIndustrializacao veioCampoContaCorrente;
    private UtilDatas utilDatas;

    private String processo;

    private Cliente cliente;

    private boolean newReg;
    private String executar;

    public SucataManualIndustrializacao() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Gerar Ordem de compra "));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (sucataDAO == null) {
                this.sucataDAO = new SucataDAO();
            }

            if (sucataMovimentoDAO == null) {
                sucataMovimentoDAO = new SucataMovimentoDAO();
            }
            txtObservacao.setLineWrap(true);
            txtObservacao.setWrapStyleWord(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void preencherComboFilial(Integer id) throws SQLException, Exception {
        FilialDAO filialDAO = new FilialDAO();
        List<Filial> listFilial = new ArrayList<Filial>();
        String cod;
        String des;
        String desger;
        txtFilial.removeAllItems();

        listFilial = filialDAO.getFilias("", " and codemp = 1 and codfil in (1)");
        if (listFilial != null) {
            for (Filial filial : listFilial) {
                cod = filial.getFilial().toString();
                des = filial.getRazao_social();
                desger = cod + " - " + des;
                txtFilial.addItem(desger);
            }
        }
    }

    public void setRecebePalavra(SucatasManutencao veioInput, SucataMovimento sucataMovimento, Filial filial,
            String processo, Sucata sucata, Cliente cliente, boolean addReg,
            double pesoSucata, double pesoProduto, String executar,
            String pedido,
            String nota,
            String ordem,
            String notaentrada) throws Exception {
        this.veioCampo = veioInput;
        this.processo = processo;
        this.sucataMovimento = sucataMovimento;
        this.sucata = new Sucata();
        this.cliente = new Cliente();
        this.cliente = cliente;
        this.newReg = addReg;
        this.executar = executar;

        txtCliente.setText(cliente.getCodigo() + " - " + cliente.getNome());

        limparTela();
        DesabilitarCampo(false);
        preencherComboFilial(1);
        if (sucataMovimento != null) {
            if (sucataMovimento.getCodigolancamento() > 0) {
                popularCampo(sucataMovimento);
            }
        }
    }

    public void setRecebePalavraConta(SucataContaCorrenteIndustrializacao veioInput, SucataMovimento sucataMovimento, Filial filial, Sucata sucata, Cliente cliente) throws Exception {
        this.veioCampoContaCorrente = veioInput;
        this.sucataMovimento = sucataMovimento;
        this.sucata = new Sucata();
        this.cliente = new Cliente();
        this.cliente = cliente;
        txtCliente.setText(cliente.getCodigo() + " - " + cliente.getNome());
        DesabilitarCampo(false);
        preencherComboFilial(1);
        limparTela();
        if (sucataMovimento != null) {
            if (sucataMovimento.getCodigolancamento() > 0) {
                validarSucata(sucataMovimento);
                popularCampo(this.sucataMovimento);
            }
        }
    }

    private void validarSucata(SucataMovimento sucataMovimento) throws SQLException {
        double pesoSucata = 0;
        if (sucataMovimento.getPesofaturado() > 0 && sucataMovimento.getPercentualrendimento() > 0) {
            pesoSucata = sucataMovimento.getPesofaturado() / (sucataMovimento.getPercentualrendimento() / 100);
            pesoSucata = FormatarNumeros.converterDoubleDoisDecimais(pesoSucata);
            if (sucataMovimento.getDebitocredito().equals("3 - DEBITO")) {
                int com = CompararDouble.comparar(sucataMovimento.getPesosucata(), pesoSucata);
                if (com == 0) {
                    //  txtPesoSucata.setValue(pesoSucata);
                    this.sucataMovimento.setPesomovimento(sucataMovimento.getPesofaturado());
                } else {
                    this.sucataMovimento.setPesosucata(pesoSucata);
                    this.sucataMovimento.setPesomovimento(sucataMovimento.getPesofaturado());
                    // gravarPeso(this.sucataMovimento);
//                    JOptionPane.showMessageDialog(null, "Sucata Baixada  " + pesoSucata,
//                            "OK:", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    private void gravarPeso(SucataMovimento suc) throws SQLException {
        if (suc.getCodigolancamento() > 0) {

            if (sucataMovimentoDAO.ajustarMovimento(suc, "")) {

            }
        }
    }

    private void popularCampo(SucataMovimento sucataMovimento) {
        double perren = sucataMovimento.getPercentualrendimento() / 100;

        txtSucata.setSelectedItem(sucataMovimento.getSucata());
        txtRentabilidade.setValue(sucataMovimento.getPercentualrendimento());

        txtTipoSucata.setSelectedItem(sucataMovimento.getAutomoto());
        txtPesoSucata.setValue(sucataMovimento.getPesosucata());
        txtPesoChumbo.setValue(sucataMovimento.getPesomovimento());

        if (sucataMovimento.getPesosucata() < 0) {
            txtPesoSucata.setValue(sucataMovimento.getPesosucata() * -1);
            txtPesoChumbo.setValue(sucataMovimento.getPesomovimento() * -1);
        }

        if (sucataMovimento.getDebitocredito().equals("4 - CREDITO")) {

            txtPesoChumbo.setValue(sucataMovimento.getPesosucata() * perren);

        }

        txtPedido.setText(sucataMovimento.getPedido().toString());
        txtNota.setText(sucataMovimento.getNotasaida().toString());
        txtOrdemCompra.setText(sucataMovimento.getOrdemcompra().toString());
        txtLancamento.setText(sucataMovimento.getCodigolancamento().toString());
        txtSequencia.setText(sucataMovimento.getSequencia().toString());
        txtNotaEntrada.setText(sucataMovimento.getNotaentrada().toString());
        txtObservacao.setText(sucataMovimento.getObservacaomovimento());
        txtProduto.setSelectedItem(sucataMovimento.getProduto());
        txtPesoPedido.setValue(sucataMovimento.getPesopedido());
        txtPesoSaida.setValue(sucataMovimento.getPesofaturado());
        txtPesoOrdem.setValue(sucataMovimento.getPesoordemcompra());
        txtPesoEntrada.setValue(sucataMovimento.getPesorecebido());

        if (sucataMovimento.getSituacao().equals("MANUAL")) {
            btnExcluir.setEnabled(true);
//            optDeb.setEnabled(false);
//            optCre.setEnabled(false);

        }

    }

    private Integer lancamento;
    private Integer sequencia;

    private void salvar() throws SQLException, Exception {
        this.sucataMovimento.setGerarordem("OCP");
        this.sucataMovimento.setEnviaremail("N");
        this.sucataMovimento.setEnviaremail("");
        this.sucataMovimento.setDebitocredito("6 - ORDEM MANUAL");
        if (!txtOrdemCompra.getText().equals("0")) {
            this.sucataMovimento.setOrdemcompra(Integer.valueOf(txtOrdemCompra.getText()));
        }
        this.sucataMovimento.setPesosucata(txtPesoSucata.getDouble());
        if (!this.sucataMovimentoDAO.gerarOrdem(this.sucataMovimento)) {
        } else {
            if (!sucataDAO.gerarOrdem(sucataMovimento)) {
            } else {
                ExecutarWS ws = new ExecutarWS();
                ws.executar(Menu.username, Menu.userpwd);
                atualizar();
            }
        }

    }

    private boolean validarCampos() {
        boolean retorno = true;
        if (!optCre.isSelected() && !optDeb.isSelected()) {
            retorno = false;
            JOptionPane.showMessageDialog(null, "ERRO: Selecione o tipo de transação Crédito Manual/Débito Manual",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (txtPesoSucata.getDouble() == 0) {
                retorno = false;
                JOptionPane.showMessageDialog(null, "ERRO: Informe o Peso",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
            } else if (txtSucata.getSelectedItem().equals("SELECIONE")) {
                retorno = false;
                JOptionPane.showMessageDialog(null, "ERRO: Informe a sucata",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
            } else if (txtTipoSucata.getSelectedItem().equals("SELECIONE")) {
                retorno = false;
                JOptionPane.showMessageDialog(null, "ERRO: Selecione o tipo de sucata",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return retorno;
    }

    private void gravarSucata(String gerarpedido) throws SQLException {

        if (validarCampos()) {
            if (this.cliente != null) {
                if (this.cliente.getCodigo() > 0) {
                    if (sucataDAO == null) {
                        sucataDAO = new SucataDAO();
                    }
                    if (sucataMovimentoDAO == null) {
                        sucataMovimentoDAO = new SucataMovimentoDAO();
                    }
                    sucata.setCodigolancamento(sucataDAO.proxCodCad());
                    sucataMovimento.setCodigolancamento(sucata.getCodigolancamento());
                    if (sucataMovimento.getCodigolancamento() == 0) {
                        sucataMovimento.setCodigolancamento(1);
                    }
                    sucataMovimento.setSequencia(1);
                    sucataMovimento.setEmpresa(1);
                    String filial = txtFilial.getSelectedItem().toString();
                    int index = filial.indexOf("-");
                    filialSelecionada = filial.substring(0, index).trim();
                    sucataMovimento.setFilial(Integer.valueOf(filialSelecionada));
                    sucataMovimento.setFilialsucata(Integer.valueOf(filialSelecionada));

                    sucataMovimento.setCliente(cliente.getCodigo());
                    sucataMovimento.setPedido(0);
                    sucataMovimento.setNotasaida(0);
                    sucataMovimento.setNotaentrada(0);
                    sucataMovimento.setOrdemcompra(0);
                    sucataMovimento.setPesosaldo(0.0);
                    sucataMovimento.setQuantidadedevolvida(0.0);
                    sucataMovimento.setPesodevolvido(0.0);
                    sucataMovimento.setPesofaturado(0.0);
                    sucataMovimento.setPesorecebido(0.0);

                    sucataMovimento.setAutomoto("IND");
                    sucataMovimento.setObservacaomovimento(txtObservacao.getText());
                    sucataMovimento.setObservacaoacerto("SUCATA REGISTRADA MANUALMENTE PELO USUÁRIO ");
                    sucataMovimento.setNumerotitulo("");
                    sucataMovimento.setTipomovimento("V");

                    sucataMovimento.setUsuario(0);
                    sucataMovimento.setDatageracao(new Date());
                    sucataMovimento.setDatamovimento(new Date());
                    sucataMovimento.setHorageracao("0");
                    sucataMovimento.setHoramovimento("0");

                    sucataMovimento.setEnviaremail("N");
                    sucataMovimento.setEmail("");

                    sucataMovimento.setCodigopeso(0);
                    sucataMovimento.setCodigominuta(0);

                    sucataMovimento.setSituacao("MANUAL");

                    sucataMovimento.setPrecounitario(0.0);
                    sucataMovimento.setValortotal(0.0);

                    sucataMovimento.setSituacaoPedido("N");
                    sucataMovimento.setGerarordem("N");
                    sucataMovimento.setPesoajustado(0.0);
                    sucataMovimento.setPesomovimento(0.0);
                    sucataMovimento.setPesopedido(0.0);
                    sucataMovimento.setQuantidade(0.0);
                    sucataMovimento.setPesoordemcompra(0.0);
                    sucataMovimento.setPercentualrendimento(0.0);
                    sucataMovimento.setPesosucata(0.0);

                    if (optCre.isSelected()) {  // lançamento manual de crédito de sucata
                        sucataMovimento.setDebitocredito("4 - CREDITO");
                        sucataMovimento.setSucata(txtSucata.getSelectedItem().toString());
                        sucataMovimento.setProduto(txtProduto.getSelectedItem().toString());
                        sucataMovimento.setPesosucata(txtPesoSucata.getDouble());

                    }

                    if (optDeb.isSelected()) {
                        sucataMovimento.setDebitocredito("3 - DEBITO");
                        sucataMovimento.setSucata(txtSucata.getSelectedItem().toString());
                        sucataMovimento.setProduto(txtProduto.getSelectedItem().toString());
                        sucataMovimento.setPesosucata(0.0);
                        sucataMovimento.setPercentualrendimento(100.0);
                        sucataMovimento.setPesomovimento(txtPesoSucata.getDouble());

                    }

                    if (!this.sucataMovimentoDAO.inserir(sucataMovimento)) {

                    } else {

                        this.sucata.setCliente(sucataMovimento.getCliente());
                        this.sucata.setEmpresa(sucataMovimento.getEmpresa());
                        this.sucata.setFilial(sucataMovimento.getFilial());
                        this.sucata.setPedido(Integer.valueOf(txtPedido.getText()));
                        this.sucata.setAutomoto(sucataMovimento.getAutomoto());
                        this.sucata.setDebitocredito(sucataMovimento.getDebitocredito());
                        this.sucata.setDatageracao(sucataMovimento.getDatageracao());
                        this.sucata.setDatageracao(sucataMovimento.getDatageracao());
                        this.sucata.setDatamovimento(sucataMovimento.getDatageracao());
                        this.sucata.setPercentualrendimento(sucataMovimento.getPercentualrendimento());
                        this.sucata.setPesosucata(sucataMovimento.getPesosucata());
                        this.sucata.setUsuario(sucataMovimento.getUsuario());
                        this.sucata.setObservacaomovimento(sucataMovimento.getObservacaomovimento());
                        this.sucata.setObservacaoacerto(sucataMovimento.getObservacaoacerto());
                        this.sucata.setSituacao(sucataMovimento.getSituacao());
                        this.sucata.setProduto(sucataMovimento.getProduto());
                        this.sucata.setSucata(sucataMovimento.getSucata());

                        if (this.sucata.getAutomoto().equals("IND")) {
                            this.sucata.setTransacao("90124");
                        }

                        if (!this.sucataDAO.inserir(sucata)) {

                        } else {

                        }

                    }

                }
            }
        }

    }

    private void buscarRentabilidade() throws SQLException {
        txtPesoSucata.setEnabled(true);
        txtRentabilidade.setValue(0);
        txtPesoChumbo.setEnabled(false);
    }

    private void atualizar() {
        if (veioCampo != null) {
            try {
                veioCampo.retornarSucata();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }
        if (veioCampoContaCorrente != null) {
            try {
                veioCampoContaCorrente.retornarSucata();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }
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

    public void removerRegistro(boolean retorno, String info) throws SQLException {
        if (retorno) {
            if (this.sucataMovimento != null) {
                if (sucataMovimento.getSequencia() > 0) {
                    sucataMovimento.setDebitocredito("0 - REMOVIDO");
                    sucataMovimento.setObservacaomovimento(info);
                    if (!this.sucataMovimentoDAO.remover(sucataMovimento)) {

                    } else {
                        txtObservacao.setText(info);
                        atualizar();
                    }

                }
            }
        }
    }

    private void limparTela() {
        txtPesoSucata.setValue(0);
        txtRentabilidade.setValue(0);
        txtPesoChumbo.setText("0");
        txtPesoSucata.setText("0");

        txtObservacao.setText("LANÇAMENTO DE SUCATA MANUAL");
        txtPedido.setText("0");
        txtNota.setText("0");
        txtOrdemCompra.setText("0");
        txtLancamento.setText("0");
        txtSequencia.setText("0");
        txtNotaEntrada.setText("0");
        txtPesoPedido.setText("0");
        txtPesoSaida.setText("0");
        txtPesoOrdem.setText("0");
        txtPesoEntrada.setText("0");

        txtSucata.setSelectedItem("SELECIONE");
        txtProduto.setSelectedItem("SELECIONE");

        txtTipoSucata.setEnabled(true);
        txtTipoSucata.requestFocus();
        btnSelecionarTipo.setEnabled(true);

    }

    private void DesabilitarCampo(boolean acao) {
        btnSelecionarTipo.setEnabled(acao);
        txtPesoSucata.setEnabled(acao);
        txtRentabilidade.setEnabled(true);
        txtPesoChumbo.setEnabled(acao);
        txtPesoSucata.setEnabled(acao);
        txtSucata.setEnabled(acao);
        txtProduto.setEnabled(acao);
        btnRentabilidade.setEnabled(acao);
        txtObservacao.setEnabled(acao);
        btnExcluir.setEnabled(acao);
        btnGravarManual.setEnabled(acao);
//        optDeb.setEnabled(acao);
//        optCre.setEnabled(acao);
    }

    private void DesabilitarCampoIndustrializacao(boolean acao) {
        txtSucata.setEnabled(acao);
        txtProduto.setEnabled(acao);
        btnRentabilidade.setEnabled(acao);
        txtObservacao.setEnabled(acao);

//        optDeb.setEnabled(acao);
//        optCre.setEnabled(acao);
    }

    private void calcularPesoChumbo() {
        double peso = txtPesoSucata.getDouble();
        double per = txtRentabilidade.getDouble();
        double res = 0;
        if (peso > 0 && per > 0) {
            per = per / 100;
            res = peso * per;
        }
        txtPesoChumbo.setValue(res);
    }

    private void calcularPesoSucata() {
        double peso = txtPesoChu.getDouble();
        double per = txtRentabilidadeChu.getDouble();
        double res = 0;
        if (peso > 0 && per > 0) {
            per = per / 100;
            res = peso / per;
        }
        txtPesoSucataChu.setValue(res);
    }

    private void calcularRentabilidade() {
        double peso = txtPesoSucataRen.getDouble();
        double per = 0;
        double res = txtPesoChumboRen.getDouble();
        if (peso > 0) {
            per = (peso / res) * 100;

        }
        txtRentabilidadeRen.setValue(per);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtSucata = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        txtTipoSucata = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtProduto = new javax.swing.JComboBox<>();
        btnRentabilidade = new javax.swing.JButton();
        btnSelecionarTipo = new javax.swing.JButton();
        txtLancamento = new org.openswing.swing.client.TextControl();
        txtSequencia = new org.openswing.swing.client.TextControl();
        txtCliente = new org.openswing.swing.client.TextControl();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtPedido = new org.openswing.swing.client.TextControl();
        txtNota = new org.openswing.swing.client.TextControl();
        txtNotaEntrada = new org.openswing.swing.client.TextControl();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        jLabel16 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtPesoSaida = new org.openswing.swing.client.NumericControl();
        txtPesoPedido = new org.openswing.swing.client.NumericControl();
        txtPesoEntrada = new org.openswing.swing.client.NumericControl();
        txtPesoOrdem = new org.openswing.swing.client.NumericControl();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        txtFilial = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        txtPesoSucata = new org.openswing.swing.client.NumericControl();
        jLabel13 = new javax.swing.JLabel();
        txtRentabilidade = new org.openswing.swing.client.NumericControl();
        jLabel11 = new javax.swing.JLabel();
        txtPesoChumbo = new org.openswing.swing.client.NumericControl();
        jLabel8 = new javax.swing.JLabel();
        btnCalcular = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        txtPesoChu = new org.openswing.swing.client.NumericControl();
        jLabel18 = new javax.swing.JLabel();
        txtRentabilidadeChu = new org.openswing.swing.client.NumericControl();
        jLabel20 = new javax.swing.JLabel();
        txtPesoSucataChu = new org.openswing.swing.client.NumericControl();
        jLabel21 = new javax.swing.JLabel();
        btnCalcular2 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        txtPesoSucataRen = new org.openswing.swing.client.NumericControl();
        jLabel22 = new javax.swing.JLabel();
        txtRentabilidadeRen = new org.openswing.swing.client.NumericControl();
        jLabel23 = new javax.swing.JLabel();
        txtPesoChumboRen = new org.openswing.swing.client.NumericControl();
        jLabel24 = new javax.swing.JLabel();
        btnCalcular3 = new javax.swing.JButton();
        optCre = new javax.swing.JRadioButton();
        optDeb = new javax.swing.JRadioButton();
        btnGravarManual = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setForeground(new java.awt.Color(0, 0, 153));

        jLabel2.setText("Tipo:");

        txtSucata.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtSucata.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "1001", "1002", "1003", "1004", "1005", "P2CBI0001", "REREM0010" }));
        txtSucata.setEnabled(false);

        jLabel10.setText("Observação");

        txtTipoSucata.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTipoSucata.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "IND" }));
        txtTipoSucata.setEnabled(false);

        jLabel15.setText("Produto:");

        jLabel17.setText("Sucata");

        txtProduto.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtProduto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "P3CLV0001", "P3CLV0004", "P3CLV0006", "P3CLV0007", "P3CLV0008", "P2CBI0001", "REREM0010", "P7PCA0079", "P7PCA0080", " " }));
        txtProduto.setEnabled(false);

        btnRentabilidade.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        btnRentabilidade.setEnabled(false);
        btnRentabilidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRentabilidadeActionPerformed(evt);
            }
        });

        btnSelecionarTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnSelecionarTipo.setEnabled(false);
        btnSelecionarTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarTipoActionPerformed(evt);
            }
        });

        txtLancamento.setEnabled(false);
        txtLancamento.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtSequencia.setEnabled(false);
        txtSequencia.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtCliente.setEnabled(false);
        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        jLabel6.setText("Cliente");

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtPedido.setEnabled(false);
        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNota.setEnabled(false);
        txtNota.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNotaEntrada.setEnabled(false);
        txtNotaEntrada.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNotaEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotaEntradaActionPerformed(evt);
            }
        });

        txtOrdemCompra.setEnabled(false);
        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel16.setText("Pedido");

        jLabel12.setText("Nota Saída");

        jLabel9.setText("NFC");

        jLabel5.setText("Ordem");

        txtPesoSaida.setDecimals(2);
        txtPesoSaida.setEnabled(false);
        txtPesoSaida.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtPesoPedido.setDecimals(2);
        txtPesoPedido.setEnabled(false);
        txtPesoPedido.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtPesoEntrada.setDecimals(2);
        txtPesoEntrada.setEnabled(false);
        txtPesoEntrada.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtPesoOrdem.setDecimals(2);
        txtPesoOrdem.setEnabled(false);
        txtPesoOrdem.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                            .addComponent(txtPesoPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(4, 4, 4)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(txtPesoSaida, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(txtNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtNotaEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtPesoEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPesoOrdem, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                            .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel12)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesoSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesoPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesoEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesoOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        txtObservacao.setColumns(20);
        txtObservacao.setRows(5);
        txtObservacao.setEnabled(false);
        jScrollPane1.setViewportView(txtObservacao);

        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtFilial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 - SELECIONE A FILIAL" }));

        jLabel3.setText("Filial");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso Sucata"));

        txtPesoSucata.setDecimals(2);
        txtPesoSucata.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoSucata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoSucataActionPerformed(evt);
            }
        });

        jLabel13.setText("Peso Sucata: ");

        txtRentabilidade.setDecimals(2);
        txtRentabilidade.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtRentabilidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRentabilidadeActionPerformed(evt);
            }
        });

        jLabel11.setText("% Rentabilildade:");

        txtPesoChumbo.setDecimals(2);
        txtPesoChumbo.setEnabled(false);
        txtPesoChumbo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoChumbo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoChumboActionPerformed(evt);
            }
        });

        jLabel8.setText("Peso Chumbo");

        btnCalcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        btnCalcular.setText("Calcular");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtPesoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(txtPesoChumbo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPesoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addGap(3, 3, 3)
                .addComponent(txtRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesoChumbo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCalcular)))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso Sucata"));

        txtPesoChu.setDecimals(2);
        txtPesoChu.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoChu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoChuActionPerformed(evt);
            }
        });

        jLabel18.setText("Peso Chumbo: ");

        txtRentabilidadeChu.setDecimals(2);
        txtRentabilidadeChu.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtRentabilidadeChu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRentabilidadeChuActionPerformed(evt);
            }
        });

        jLabel20.setText("% Rentabilildade:");

        txtPesoSucataChu.setDecimals(2);
        txtPesoSucataChu.setEnabled(false);
        txtPesoSucataChu.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoSucataChu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoSucataChuActionPerformed(evt);
            }
        });

        jLabel21.setText("Peso Sucata");

        btnCalcular2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        btnCalcular2.setText(" Calcular");
        btnCalcular2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcular2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtPesoChu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtRentabilidadeChu, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(txtPesoSucataChu, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCalcular2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPesoChu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel20)
                .addGap(3, 3, 3)
                .addComponent(txtRentabilidadeChu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesoSucataChu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCalcular2)))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso Sucata"));

        txtPesoSucataRen.setDecimals(2);
        txtPesoSucataRen.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoSucataRen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoSucataRenActionPerformed(evt);
            }
        });

        jLabel22.setText("Peso Sucata: ");

        txtRentabilidadeRen.setDecimals(2);
        txtRentabilidadeRen.setEnabled(false);
        txtRentabilidadeRen.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtRentabilidadeRen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRentabilidadeRenActionPerformed(evt);
            }
        });

        jLabel23.setText("% Rentabilildade:");

        txtPesoChumboRen.setDecimals(2);
        txtPesoChumboRen.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoChumboRen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoChumboRenActionPerformed(evt);
            }
        });

        jLabel24.setText("Peso Chumbo");

        btnCalcular3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        btnCalcular3.setText("Calcular");
        btnCalcular3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcular3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtPesoSucataRen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtRentabilidadeRen, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(txtPesoChumboRen, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCalcular3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPesoSucataRen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel23)
                .addGap(3, 3, 3)
                .addComponent(txtRentabilidadeRen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesoChumboRen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCalcular3)))
        );

        buttonGroup1.add(optCre);
        optCre.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optCre.setForeground(new java.awt.Color(0, 153, 0));
        optCre.setText("Crédito Manual");
        optCre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optCreActionPerformed(evt);
            }
        });

        buttonGroup1.add(optDeb);
        optDeb.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optDeb.setForeground(new java.awt.Color(255, 0, 0));
        optDeb.setText("Débito Manual");
        optDeb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optDebActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtFilial, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(4, 4, 4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtTipoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnSelecionarTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProduto, 0, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtSucata, 0, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(4, 4, 4))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(optCre, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(optDeb, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel2});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel15)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTipoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelecionarTipo)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSucata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optCre)
                    .addComponent(optDeb))
                .addGap(4, 4, 4)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnRentabilidade, txtSucata});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel5, jPanel7, jPanel8});

        btnGravarManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-e-fechar16x16.png"))); // NOI18N
        btnGravarManual.setText("Gravar Manual");
        btnGravarManual.setEnabled(false);
        btnGravarManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarManualActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-delete-16x16.png"))); // NOI18N
        btnExcluir.setText("Excluir Registro");
        btnExcluir.setEnabled(false);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(btnGravarManual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnExcluir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExcluir, btnGravarManual});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGravarManual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSair, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnExcluir, btnGravarManual, btnSair});

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnGravarManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarManualActionPerformed
        if (ManipularRegistros.pesos(" Gravar registro")) {
            try {
                gravarSucata("N");
            } catch (SQLException ex) {
                Logger.getLogger(SucataManualIndustrializacao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnGravarManualActionPerformed
    private String tipocalculo;
    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        try {
            SucataSenha sol = new SucataSenha();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado
            sol.setSize(400, 300);
            sol.setPosicao();
            sol.setRecebePalavraIndustrializacao(this, "ENTRADA", "", this.sucataMovimento);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnExcluirActionPerformed

    private String filialSelecionada;

    private String verificarFilial(String processo) {
        String filial = txtFilial.getSelectedItem().toString();
        int index = filial.indexOf("-");
        filialSelecionada = filial.substring(0, index).trim();
        if (filialSelecionada.equals("0")) {
            JOptionPane.showMessageDialog(null, "Atenção: Informe a Filial da Ordem de compra ",
                    "Atenção:", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Atenção: O Sistema ira gerar uma " + processo + " para a Filial  "
                    + txtFilial.getSelectedItem().toString(),
                    "Atenção:", JOptionPane.INFORMATION_MESSAGE);
        }
        return filialSelecionada;
    }

    private void gravarPedido() throws Exception {
        if (this.executar.equals("PV")) { // pedido
            if ((txtPesoChumbo.getDouble() > 0) && (txtPesoSucata.getDouble() > 0)) {
                JOptionPane.showMessageDialog(null, "Info: Esse processo vai gerar um pedido para baixar saldo total de sucata  ",
                        "Atenção:", JOptionPane.INFORMATION_MESSAGE);
                if (ManipularRegistros.pesos(" Gerar pedido de baixa de sucata?")) {

                    try {
                        gravarSucata("PV");

                    } catch (SQLException ex) {
                        Logger.getLogger(SucataManualIndustrializacao.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            if (this.executar.equals("PO")) { // pedido + ordem
                if ((txtPesoChumbo.getDouble() > 0) && (txtPesoSucata.getDouble() > 0)) {
                    if (ManipularRegistros.pesos(" Gravar registro")) {
                        try {
                            txtPedido.setValue(0);
                            gravarSucata("PO");

                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Erro: " + ex,
                                    "Erro:", JOptionPane.ERROR_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Erro: " + ex,
                                    "Erro:", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Erro: Peso(s) de sucata ou produto inválido ",
                            "Erro:", JOptionPane.ERROR_MESSAGE);
                }
            } else if (this.executar.equals("OC")) { // ordem
                String filial = verificarFilial("Ordem de Compra");
                if (filial.equals("0")) {

                } else {
                    if ((txtPesoChumbo.getDouble() > 0.0) && (txtPesoSucata.getDouble() > 0.0)) {
                        if (!txtPedido.getText().equals("0")) {
                            JOptionPane.showMessageDialog(null, " Atenção: O Sistema ira gerar uma ordem de compra sem vinvulo com o pedido de venda ",
                                    "Erro:", JOptionPane.INFORMATION_MESSAGE);
                        }
                        if (ManipularRegistros.pesos(" Gravar registro")) {
                            try {
                                gravarSucata("OC");

                            } catch (SQLException ex) {
                                Logger.getLogger(SucataManualIndustrializacao.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception ex) {
                                Logger.getLogger(SucataManualIndustrializacao.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Erro: Informe o pedido ou o peso ",
                                "Erro:", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        }
    }

    private void gerarProcessoERP() throws Exception {
        WSEmailAtendimento wsEma = new WSEmailAtendimento();
        wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
    }

    private void btnCalcular3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcular3ActionPerformed
        calcularRentabilidade();
    }//GEN-LAST:event_btnCalcular3ActionPerformed

    private void txtPesoChumboRenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoChumboRenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesoChumboRenActionPerformed

    private void txtRentabilidadeRenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRentabilidadeRenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRentabilidadeRenActionPerformed

    private void txtPesoSucataRenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoSucataRenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesoSucataRenActionPerformed

    private void btnCalcular2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcular2ActionPerformed
        calcularPesoSucata();
    }//GEN-LAST:event_btnCalcular2ActionPerformed

    private void txtPesoSucataChuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoSucataChuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesoSucataChuActionPerformed

    private void txtRentabilidadeChuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRentabilidadeChuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRentabilidadeChuActionPerformed

    private void txtPesoChuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoChuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesoChuActionPerformed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        calcularPesoChumbo();
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void txtPesoChumboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoChumboActionPerformed
        tipocalculo = "REVERSO";
        //
    }//GEN-LAST:event_txtPesoChumboActionPerformed

    private void txtRentabilidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRentabilidadeActionPerformed
        //
    }//GEN-LAST:event_txtRentabilidadeActionPerformed

    private void txtPesoSucataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoSucataActionPerformed
//        txtPesoSucata.setEnabled(true);
//        txtPesoSucata.requestFocus();
//        Double per = (txtPesoSucata.getDouble() / txtPesoSaida.getDouble() * 100);
//        txtPesoChumbo.setValue(txtPesoSaida.getDouble());
//        txtRentabilidade.setValue(per);
    }//GEN-LAST:event_txtPesoSucataActionPerformed

    private void txtNotaEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaEntradaActionPerformed
        txtPesoEntrada.requestFocus();
    }//GEN-LAST:event_txtNotaEntradaActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void optDebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optDebActionPerformed

        btnGravarManual.setEnabled(true);
    }//GEN-LAST:event_optDebActionPerformed

    private void optCreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optCreActionPerformed

        btnGravarManual.setEnabled(true);
    }//GEN-LAST:event_optCreActionPerformed

    private void btnSelecionarTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarTipoActionPerformed
        limparTela();
        DesabilitarCampo(false);
        DesabilitarCampoIndustrializacao(true);
        btnSelecionarTipo.setEnabled(true);
    }//GEN-LAST:event_btnSelecionarTipoActionPerformed

    private void btnRentabilidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRentabilidadeActionPerformed
        try {
            buscarRentabilidade();
        } catch (SQLException ex) {
            Logger.getLogger(SucataManualIndustrializacao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnRentabilidadeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnCalcular2;
    private javax.swing.JButton btnCalcular3;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnGravarManual;
    private javax.swing.JButton btnRentabilidade;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSelecionarTipo;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton optCre;
    private javax.swing.JRadioButton optDeb;
    private org.openswing.swing.client.TextControl txtCliente;
    private javax.swing.JComboBox<String> txtFilial;
    private org.openswing.swing.client.TextControl txtLancamento;
    private org.openswing.swing.client.TextControl txtNota;
    private org.openswing.swing.client.TextControl txtNotaEntrada;
    private javax.swing.JTextArea txtObservacao;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.NumericControl txtPesoChu;
    private org.openswing.swing.client.NumericControl txtPesoChumbo;
    private org.openswing.swing.client.NumericControl txtPesoChumboRen;
    private org.openswing.swing.client.NumericControl txtPesoEntrada;
    private org.openswing.swing.client.NumericControl txtPesoOrdem;
    private org.openswing.swing.client.NumericControl txtPesoPedido;
    private org.openswing.swing.client.NumericControl txtPesoSaida;
    private org.openswing.swing.client.NumericControl txtPesoSucata;
    private org.openswing.swing.client.NumericControl txtPesoSucataChu;
    private org.openswing.swing.client.NumericControl txtPesoSucataRen;
    private javax.swing.JComboBox<String> txtProduto;
    private org.openswing.swing.client.NumericControl txtRentabilidade;
    private org.openswing.swing.client.NumericControl txtRentabilidadeChu;
    private org.openswing.swing.client.NumericControl txtRentabilidadeRen;
    private org.openswing.swing.client.TextControl txtSequencia;
    private javax.swing.JComboBox<String> txtSucata;
    private javax.swing.JComboBox<String> txtTipoSucata;
    // End of variables declaration//GEN-END:variables
}
