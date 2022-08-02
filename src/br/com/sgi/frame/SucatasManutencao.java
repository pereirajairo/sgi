/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.SucataDAO;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.util.CompararDouble;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.UtilDatas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
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
public final class SucatasManutencao extends InternalFrame {

    private SucataMovimento sucataMovimento;
    private List<SucataMovimento> listSucataMovimento = new ArrayList<SucataMovimento>();
    private SucataMovimentoDAO sucataMovimentoDAO;

    private Sucata sucata;
    private SucataDAO sucataDAO;

    private Sucatas veioCampo;
    private UtilDatas utilDatas;

    private String datI;
    private String datF;
    private static Color COR_DEBITO = new Color(255, 0, 0);
    private static Color COR_CREDITO = new Color(66, 111, 66);
    private String processo;
    private Cliente cliente;
    private boolean addReg;

    private Double pesoSaldoSucata = 0.0;
    private Double pesoSaldoProduto = 0.0;

    public SucatasManutencao() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Gestão de sucatas "));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (sucataDAO == null) {
                this.sucataDAO = new SucataDAO();
            }
            if (sucataMovimentoDAO == null) {
                this.sucataMovimentoDAO = new SucataMovimentoDAO();
            }
            limpatela();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void getListarMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        PESQUISA += " and usu_debcre not in '0 - REMOVIDO'";
        listSucataMovimento = this.sucataMovimentoDAO.getSucatasMovimento(PESQUISA_POR, PESQUISA);
        if (listSucataMovimento != null) {
            carregarTabelaMovimento(false);

        }
    }

    public void carregarTabelaMovimento(boolean selecionar) throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableEnvio.getModel();
        modeloCarga.setNumRows(0);

        ImageIcon ProIcon = getImage("/images/money_add.png");
        ImageIcon gerIcon = getImage("/images/sitMedio.png");
        ImageIcon ManIcon = getImage("/images/overlays.png");
        ImageIcon Retorno = getImage("/images/veiculo_retorno.png");
        ImageIcon RetornoManual = getImage("/images/veiculo_retorno_manual.png");
        ImageIcon Envio = getImage("/images/veiculo_envio.png");
        ImageIcon Envio_manual = getImage("/images/veiculo_envio_manual.png");
        ImageIcon RetornoCancelado = getImage("/images/veiculo_envio_cancelar.png");
        ImageIcon Branco = getImage("/images/veiculo_envio_branco.png");

        lblProvisaoProduto.setText("PRO 0.00");
        lblCreditoSucata.setText("CRE 0.00");
        lblDebitoProduto.setText("DEB 0.00");
        lblSaldoProduto.setText("SLD 0.00");
        lblSomaSucataOutro.setText("REG 0.00");

        String situacao = "";
        String situacaoPeso = "";

        double pesoSucataCredito = 0.0;
        double pesoSucataDebito = 0.0;
        double pesoSucataSaldo = 0.0;
        double pesoCreditoSucataManual = 0.0;
        double pesoManualDebitoSucata = 0.0;

        double pesoProdutoDebitoAutomatico = 0.0;
        double pesoProdutoDebitoManual = 0.0;

        double pesoProdutoCredito = 0.0;
        double pesoProdutoSaldo = 0.0;

        double pesoPedidoMovimento = 0.0;

        double pesoG = 0.0;
        double pesoSaldoRegistro = 0.0;
        double pesoSaldoProduto = 0.0;
        double pesoPedido = 0.0;
        double pesoProvisionado = 0.0;
        double pesoAjustado = 0.0;
        double pesocalculado = 0.0;
        double pesoOrdem = 0.0;
        for (SucataMovimento suc : listSucataMovimento) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableEnvio.getColumnModel();
            SucatasManutencao.JTableRenderer renderers = new SucatasManutencao.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            switch (suc.getDebitocredito()) {
                case "1 - GERADO":
                    pesoG += suc.getPesosucata();
                    linha[0] = gerIcon;
                    if (suc.getSituacao().equals("MANUAL")) {
                        linha[0] = ManIcon;
                    }

                    situacao = "PEDIDO DE VENDA GERADO";
                    situacaoPeso = "PED " + suc.getProduto();
                    break;
                case "2 - PROVISIONADO":

                    linha[0] = ProIcon;
                    if (suc.getSituacao().equals("MANUAL")) {
                        linha[0] = ManIcon;
                    }

                    situacao = "PEDIDO DE VENDA LIBERADO";
                    situacaoPeso = "PED " + suc.getProduto();
                    break;
                case "3 - DEBITO":
                    linha[0] = Envio;
                    if (suc.getSituacao().equals("MANUAL")) {
                        linha[0] = Envio_manual;
                    }

                    break;
                case "4 - CREDITO":
                    linha[0] = Retorno;
                    if (suc.getSituacao().equals("MANUAL")) {
                        linha[0] = RetornoManual;
                    }

                    situacaoPeso = "CRE : " + suc.getSucata();

                    break;
                case "5 - REABILITADO":
                    linha[0] = gerIcon;
                    situacao = "REABILITDADO";
                    situacaoPeso = "PED : " + suc.getProduto();
                    break;
                case "6 - ORDEM MANUAL":
                    linha[0] = gerIcon;
                    situacao = "ORDEM COMPRA ANTES DO FATURAMENTO";
                    situacaoPeso = "PED : " + suc.getProduto();
                    break;

                case "7 - CANCELADO":
                    linha[0] = RetornoCancelado;
                    situacao = "NOTA FISCAL CANCELADA";
                    situacaoPeso = "NFV : " + suc.getProduto();
                    break;
                default:
                    break;
            }

            linha[1] = situacaoPeso;

            linha[2] = suc.getPedido();

            linha[3] = suc.getPesopedido();

            pesoAjustado += suc.getPesoajustado();
            pesoPedido = suc.getPesopedido();

            switch (suc.getDebitocredito()) {
                case "1 - GERADO":
                    pesoOrdem += suc.getPesoordemcompra();
                    pesoProvisionado += suc.getPesopedido();
                    linha[4] = suc.getPesomovimento();
                    if (pesoPedido < 0) {
                        pesoPedido = pesoPedido * -1;
                    }
                    suc.setPesosaldo(pesoPedido - suc.getPesosucata());
                    int ret = CompararDouble.comparar(pesoPedido, suc.getPesomovimento());
                    if (ret == 0) {
                        suc.setPesosaldo(0.0);
                    } else {
                        suc.setPesosaldo(suc.getPesomovimento() - pesoPedido);
                    }

                    linha[5] = FormatarPeso.mascaraPorcentagem(suc.getPesosaldo(), FormatarPeso.PORCENTAGEM);

                    linha[7] = suc.getSucata() + " REN % " + suc.getPercentualrendimento();
                    if (!suc.getAutomoto().equals("IND")) {
                        linha[4] = suc.getPesosaldo();
                        linha[7] = suc.getSucata();
                    }
                    break;
                case "2 - PROVISIONADO":
                    pesoOrdem += suc.getPesoordemcompra();
                    pesoProvisionado += suc.getPesopedido();
                    if (pesoPedido > 0) {
                        pesoPedido = pesoPedido * -1;
                    }
                    linha[4] = suc.getPesomovimento();
                    suc.setPesosaldo(pesoPedido - suc.getPesosucata());
                    linha[5] = FormatarPeso.mascaraPorcentagem(suc.getPesosaldo(), FormatarPeso.PORCENTAGEM);

                    linha[7] = suc.getSucata() + " REN % " + suc.getPercentualrendimento();

                    if (!suc.getAutomoto().equals("IND")) {
                        linha[4] = suc.getPesosaldo();
                        linha[7] = suc.getSucata();
                    }
                    break;
                case "3 - DEBITO":
                    if (suc.getSituacao().equals("MANUAL")) { // processo manual
                        linha[3] = suc.getPesosucata();

                        pesoManualDebitoSucata += suc.getPesosucata();

                        if (suc.getPesomovimento() < 0) {
                            suc.setPesomovimento(suc.getPesomovimento() * -1);

                        }
                        pesoPedidoMovimento += suc.getPesomovimento();

                        pesoProdutoDebitoManual += suc.getPesomovimento();

                        situacao = "LANÇAMENTO MANUAL DE DÉBITO";
                    } else {  // processo sistemico
                        pesoProdutoDebitoAutomatico += suc.getPesofaturado();
                        if (suc.getPercentualrendimento() > 0) {
                            pesoSucataDebito += suc.getPesofaturado() / (suc.getPercentualrendimento() / 100);
                        } else {
                            pesoSucataDebito += suc.getPesofaturado();

                        }
                    }

                    if (pesoProdutoDebitoAutomatico < 0) {
                        pesoProdutoDebitoAutomatico = pesoProdutoDebitoAutomatico * -1;
                    }
                    situacaoPeso = "DEB : " + suc.getProduto();
                    linha[1] = situacaoPeso;
                    linha[7] = suc.getSucata();
                    if (suc.getAutomoto().equals("IND")) {
                        if (suc.getPesosucata() < 0) {
                            pesocalculado = (suc.getPesofaturado()) + suc.getPesosaldo();
                            linha[4] = pesocalculado;
                            if (suc.getSituacao().equals("MANUAL")) {
                                linha[4] = suc.getPesomovimento();
                            }
                        } else {
                            linha[4] = suc.getPesosucata();

                        }

                        pesoSaldoProduto += suc.getPesosaldo();
                        if (suc.getSituacao().equals("MANUAL")) {
                            linha[5] = 0;
                        } else {
                            linha[5] = suc.getPesosucataS() + "  = " + suc.getPesosaldo();

                        }
                        linha[7] = suc.getSucata() + " REN % " + suc.getPercentualrendimento();
                    }

                    break;
                case "4 - CREDITO":
                    pesoAjustado = 0;
                    linha[3] = suc.getPesoordemcompra();
                    if (suc.getSituacao().equals("MANUAL")) {
                        pesoSucataCredito += suc.getPesosucata();
                        pesoCreditoSucataManual = suc.getPesosucata();
                        pesoPedidoMovimento += suc.getPesomovimento();
                        if (suc.getPercentualrendimento() > 0) {
                            pesoProdutoCredito += pesoSucataCredito * (suc.getPercentualrendimento() / 100);
                        } else {
                            pesoProdutoCredito = pesoSucataCredito;
                        }
                        situacao = "CREDITO DE SUCATA MANUAL";
                        linha[3] = pesoCreditoSucataManual;
                        linha[4] = suc.getPesomovimento();
                    } else {
                        pesoSucataCredito += suc.getPesorecebido();
                        linha[4] = suc.getPesosucataS();
                        if (suc.getPercentualrendimento() > 0) {
                            pesoProdutoCredito += suc.getPesorecebido() * (suc.getPercentualrendimento() / 100);
                        }
                    }

                    pesoSaldoRegistro = suc.getPesorecebido() - suc.getPesoordemcompra();
                    suc.setPesoajustado(FormatarNumeros.converterDoubleDoisDecimais(pesoSaldoRegistro));
                    linha[5] = FormatarPeso.mascaraPorcentagem(pesoSaldoRegistro, FormatarPeso.PORCENTAGEM);
                    linha[7] = suc.getSucata() + " REN % " + suc.getPercentualrendimento();
                    if (!suc.getAutomoto().equals("IND")) {
                        linha[7] = suc.getSucata();
                    } else {

                    }
                    break;

            }
            if (!suc.getAutomoto().equals("IND")) {
                pesoProdutoCredito = 0;
                pesoProdutoDebitoAutomatico = 0;
            }
            linha[6] = suc.getPesoajustado();

            linha[8] = suc.getOrdemcompra();
            linha[9] = suc.getPesoordemcompra();

            linha[10] = suc.getNotasaida();
            linha[11] = 0;
            if (suc.getNotasaida() > 0) {
                linha[11] = suc.getPesofaturado();
            }

            linha[12] = suc.getNotaentrada();
            linha[13] = suc.getPesorecebido();

            if (suc.getGerarordem() != null) {
                linha[14] = suc.getGerarordem();
            } else {
                linha[14] = "";
            }
            linha[15] = "";

            linha[16] = suc.getDatamovimentoS();
            linha[17] = suc.getCodigolancamento();
            linha[18] = suc.getSequencia();
            linha[19] = situacao;
            linha[20] = suc.getEmpresa();
            linha[21] = suc.getFilial();
            linha[22] = suc.getObservacaomovimento();

            modeloCarga.addRow(linha);
        }

        formatarCampoPeso(pesoSucataCredito, "CS");
        if (pesoManualDebitoSucata < 0) {
            pesoManualDebitoSucata = pesoManualDebitoSucata * -1;
        }
        formatarCampoPeso(pesoSucataDebito + pesoManualDebitoSucata, "DS");
        if (pesoSucataDebito < 0) {
            pesoSucataDebito = pesoSucataDebito * -1;
        }
        pesoSucataSaldo = pesoSucataCredito - (pesoSucataDebito + pesoManualDebitoSucata);
        formatarCampoPeso(pesoSucataSaldo, "SC");

        formatarCampoPeso(pesoProdutoCredito, "CP");

        formatarCampoPeso(pesoProdutoDebitoAutomatico + pesoProdutoDebitoManual, "DP");
        pesoProdutoSaldo = pesoProdutoCredito - (pesoProdutoDebitoAutomatico + pesoProdutoDebitoManual);
        formatarCampoPeso(pesoProdutoSaldo, "SP");

        formatarCampoPeso(pesoAjustado, "G");

        formatarCampoPeso(pesoProvisionado, "PP"); // provisão de produto
        formatarCampoPeso(pesoOrdem, "PS"); // provisão de sucata

        lblSaldoProduto.setForeground(COR_CREDITO);
        if (pesoProdutoSaldo <= 0) {
            lblSaldoProduto.setForeground(COR_DEBITO);
        }
    }

    private void formatarCampoPeso(double peso, String tipo) {

        String pesoS = FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM);
        if (tipo.equals("CS")) {
            lblCreditoSucata.setText(pesoS);
        }
        if (tipo.equals("DS")) {
            lblDebitoSucata.setText(pesoS);
        }
        if (tipo.equals("SC")) {
            lblSaldoSucata.setText(pesoS);
            this.pesoSaldoSucata = peso;
        }

        if (tipo.equals("CP")) {
            lblCreditoProduto.setText(pesoS);
        }
        if (tipo.equals("DP")) {
            lblDebitoProduto.setText(pesoS);
        }
        if (tipo.equals("SP")) {
            lblSaldoProduto.setText(pesoS);
            this.pesoSaldoProduto = peso;
        }
        if (tipo.equals("PP")) {
            lblProvisaoProduto.setText(pesoS);
        }
        if (tipo.equals("PS")) {
            lblProvisaoSucata.setText(pesoS);
        }

        if (tipo.equals("G")) {
            lblSomaSucataOutro.setText(pesoS);
        }
    }

    public void setRecebePalavra(Sucatas veioInput, String codigoFilial, Cliente cliente) throws Exception {
        this.veioCampo = veioInput;
        if (cliente != null) {
            if (cliente.getCodigo() > 0) {
                this.cliente = cliente;
                txtCodigo.setText(String.valueOf(cliente.getCodigo()));
                txtNomeCliente.setText(cliente.getNome());
                pegarDataDigitada();
                getListarMovimento("", " \nand usu_datger >= '" + datI + "'"
                        + " \nand usu_datger <= '" + datF + "'"
                        + " \nand usu_codcli = " + cliente.getCodigo());

                if (!codigoFilial.isEmpty()) {
                    txtEmpresa.setText("1");
                    txtFilial.setText(codigoFilial);
                    getFilial();
                }
            }

        }

    }
    private boolean gerarsucataManual = false;

    private void sair() {
        if (veioCampo != null) {
            try {
                veioCampo.retornarSucata();
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

        jTableEnvio.setRowHeight(40);
        jTableEnvio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTableEnvio.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(8).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(9).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(11).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(12).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(13).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(14).setCellRenderer(centralizado);
        jTableEnvio.getColumnModel().getColumn(16).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(17).setCellRenderer(direita);

        jTableEnvio.setAutoCreateRowSorter(true);
        jTableEnvio.setAutoResizeMode(0);

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public void limpatela() throws ParseException, Exception {
        txtEmpresaNome.setText("BATERIAS ERBS");
        txtPedido.setText("0");
        txtCodigo.setText("0");
        txtDatIni.setDate(this.utilDatas.retornaDataIni(new Date()));
        txtDatFim.setDate(this.utilDatas.retornaDataFim(new Date()));
        txtOrdemCompra.setText("0");
        txtNota.setText("0");
        txtNotaEntrada.setText("0");

    }

    private void getSucataOrdemCompra() {
        if (txtOrdemCompra.getValue() != null) {
            try {

                getListarMovimento("", " and usu_codemp = " + txtEmpresa.getText() + ""
                        + " and usu_codfil = " + txtFilial.getText() + ""
                        + " and usu_numocp = " + txtOrdemCompra.getText());

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    private void getSucataPedido() {

        if (txtPedido.getValue() != null) {
            try {

                getListarMovimento("", " and usu_codemp = " + txtEmpresa.getText() + ""
                        + " and usu_codfil = " + txtFilial.getText() + ""
                        + " and usu_numped = " + txtPedido.getText());

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void getSucataNota() {
        if (txtPedido.getValue() != null) {
            try {

                getListarMovimento("", " and usu_codemp = " + txtEmpresa.getText() + ""
                        + " and usu_codfil = " + txtFilial.getText() + ""
                        + " and usu_numnfv = " + txtNota.getText());

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private Filial filial;

    private void getFilial() throws SQLException {
        if (!txtFilial.getText().equals("0") && !txtEmpresa.getText().equals("0")) {
            FilialDAO dao = new FilialDAO();
            filial = new Filial();
            filial = dao.getFilia("", ""
                    + " and codemp  = " + txtEmpresa.getText() + " "
                    + " and codfil = " + txtFilial.getText());

            if (filial != null) {
                txtNomeFilial.setText(filial.getRazao_social());
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

    private void getSucataCliente() {
        if (txtCodigo.getValue() != null) {
            try {
                pegarDataDigitada();
                getListarMovimento("", " and usu_datger >= '" + datI + "'"
                        + " and usu_datger <= '" + datF + "'"
                        + "  and usu_codcli = " + txtCodigo.getText());

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            }

        }
    }

    void retornarSucata() throws ParseException, Exception {
        pegarDataDigitada();
        getListarMovimento("", " \nand usu_datger >= '" + datI + "'"
                + " \nand usu_datger <= '" + datF + "'"
                + " \nand usu_codcli = " + txtCodigo.getText().trim());
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
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtPedido = new org.openswing.swing.client.TextControl();
        txtCodigo = new org.openswing.swing.client.TextControl();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedEtq = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jpanelDebito = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableEnvio = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblSomaSucataOutro = new javax.swing.JLabel();
        lblProvisaoProduto = new javax.swing.JLabel();
        lblProvisaoSucata = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        btnHoras = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        txtDatFim = new org.openswing.swing.client.DateControl();
        jLabel6 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        txtEmpresa = new org.openswing.swing.client.TextControl();
        jButton9 = new javax.swing.JButton();
        txtFilial = new org.openswing.swing.client.TextControl();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNota = new org.openswing.swing.client.TextControl();
        btnHoras1 = new javax.swing.JButton();
        txtNotaEntrada = new org.openswing.swing.client.TextControl();
        txtNomeCliente = new org.openswing.swing.client.TextControl();
        txtNomeFilial = new org.openswing.swing.client.TextControl();
        txtEmpresaNome = new org.openswing.swing.client.TextControl();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblCreditoSucata = new javax.swing.JLabel();
        lblDebitoProduto = new javax.swing.JLabel();
        lblSaldoProduto = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnAlt = new javax.swing.JButton();
        btnNotaEntrada = new javax.swing.JButton();
        txtRentabilidade = new org.openswing.swing.client.NumericControl();
        btnRentabilidade = new javax.swing.JButton();
        lblDebitoSucata = new javax.swing.JLabel();
        lblCreditoProduto = new javax.swing.JLabel();
        lblSaldoSucata = new javax.swing.JLabel();
        btnManual = new javax.swing.JButton();
        btnAutomatico = new javax.swing.JButton();

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

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtPedido.setEnabled(false);
        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPedido.setUpperCase(true);
        txtPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPedidoActionPerformed(evt);
            }
        });

        txtCodigo.setEnabled(false);
        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCodigo.setRequired(true);
        txtCodigo.setUpperCase(true);
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        jLabel8.setText("Empresa");

        jLabel1.setText("NFC");

        jTabbedEtq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedEtqMouseClicked(evt);
            }
        });

        jpanelDebito.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableEnvio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Processo", "Pedido", "Peso Original", "Peso Env/Rec", "Diferença", "Peso Ajuste", "Sucata", "O.C", "Peso Ordem", "Nota Saída", "Peso Saída", "Nota Entrada", "Peso Recebido", "N", "#", "Emissão", "Movimento", "Sequencia", "Situação", "Empresa", "Filial", "Observação"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableEnvio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableEnvioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableEnvio);
        if (jTableEnvio.getColumnModel().getColumnCount() > 0) {
            jTableEnvio.getColumnModel().getColumn(0).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(0).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(1).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(2).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(3).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(4).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(5).setMinWidth(120);
            jTableEnvio.getColumnModel().getColumn(5).setPreferredWidth(120);
            jTableEnvio.getColumnModel().getColumn(5).setMaxWidth(120);
            jTableEnvio.getColumnModel().getColumn(6).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(6).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(7).setMinWidth(150);
            jTableEnvio.getColumnModel().getColumn(7).setPreferredWidth(150);
            jTableEnvio.getColumnModel().getColumn(7).setMaxWidth(150);
            jTableEnvio.getColumnModel().getColumn(8).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(8).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(8).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(9).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(9).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(9).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(10).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(10).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(10).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(11).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(11).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(11).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(12).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(12).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(12).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(13).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(14).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(14).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(14).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(15).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(15).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(15).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(16).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(16).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(16).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(17).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(17).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(17).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(18).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(18).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(18).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(19).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(19).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(19).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(20).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(20).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(20).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(21).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(21).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(21).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(22).setMinWidth(500);
            jTableEnvio.getColumnModel().getColumn(22).setPreferredWidth(500);
            jTableEnvio.getColumnModel().getColumn(22).setMaxWidth(500);
        }

        javax.swing.GroupLayout jpanelDebitoLayout = new javax.swing.GroupLayout(jpanelDebito);
        jpanelDebito.setLayout(jpanelDebitoLayout);
        jpanelDebitoLayout.setHorizontalGroup(
            jpanelDebitoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 862, Short.MAX_VALUE)
            .addGroup(jpanelDebitoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE))
        );
        jpanelDebitoLayout.setVerticalGroup(
            jpanelDebitoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
            .addGroup(jpanelDebitoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpanelDebito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jpanelDebito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedEtq.addTab("Conta Corrente", jPanel1);

        lblSomaSucataOutro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblSomaSucataOutro.setForeground(new java.awt.Color(102, 102, 102));
        lblSomaSucataOutro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSomaSucataOutro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money-m.png"))); // NOI18N
        lblSomaSucataOutro.setText("0.00");
        lblSomaSucataOutro.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso Ajustado"));
        lblSomaSucataOutro.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblSomaSucataOutro.setOpaque(true);

        lblProvisaoProduto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblProvisaoProduto.setForeground(new java.awt.Color(102, 102, 102));
        lblProvisaoProduto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProvisaoProduto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        lblProvisaoProduto.setText("0.00");
        lblProvisaoProduto.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso provisionado Pedido"));
        lblProvisaoProduto.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblProvisaoProduto.setOpaque(true);
        lblProvisaoProduto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblProvisaoProdutoMouseClicked(evt);
            }
        });
        lblProvisaoProduto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblProvisaoProdutoKeyPressed(evt);
            }
        });

        lblProvisaoSucata.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblProvisaoSucata.setForeground(new java.awt.Color(102, 102, 102));
        lblProvisaoSucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProvisaoSucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/indu.png"))); // NOI18N
        lblProvisaoSucata.setText("0.00");
        lblProvisaoSucata.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso previsto Sucata"));
        lblProvisaoSucata.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblProvisaoSucata.setOpaque(true);
        lblProvisaoSucata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblProvisaoSucataMouseClicked(evt);
            }
        });
        lblProvisaoSucata.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblProvisaoSucataKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblProvisaoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                    .addComponent(lblProvisaoProduto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSomaSucataOutro, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
                .addContainerGap(684, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSomaSucataOutro, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblProvisaoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblProvisaoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblProvisaoProduto, lblProvisaoSucata, lblSomaSucataOutro});

        jTabbedEtq.addTab("Totalizador", jPanel3);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        txtOrdemCompra.setEnabled(false);
        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtOrdemCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOrdemCompraActionPerformed(evt);
            }
        });

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

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel6.setText("Data Fim:");

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        txtEmpresa.setEnabled(false);
        txtEmpresa.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEmpresa.setRequired(true);
        txtEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmpresaActionPerformed(evt);
            }
        });
        txtEmpresa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEmpresaKeyTyped(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        jButton9.setText("Sair");
        jButton9.setBorder(null);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        txtFilial.setEnabled(false);
        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtFilial.setRequired(true);
        txtFilial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialActionPerformed(evt);
            }
        });

        jLabel5.setText("Filial");

        jLabel3.setText("Cliente");

        txtNota.setEnabled(false);
        txtNota.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotaActionPerformed(evt);
            }
        });

        btnHoras1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        btnHoras1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoras1ActionPerformed(evt);
            }
        });

        txtNotaEntrada.setEnabled(false);
        txtNotaEntrada.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNomeCliente.setEnabled(false);
        txtNomeCliente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNomeFilial.setEnabled(false);
        txtNomeFilial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtEmpresaNome.setEnabled(false);
        txtEmpresaNome.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel2.setText("NFV");

        jLabel7.setText("O.C");

        jLabel9.setText("Pedido");

        lblCreditoSucata.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblCreditoSucata.setForeground(new java.awt.Color(51, 102, 255));
        lblCreditoSucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCreditoSucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_retorno.png"))); // NOI18N
        lblCreditoSucata.setText("1000");
        lblCreditoSucata.setBorder(javax.swing.BorderFactory.createTitledBorder("Credito Sucata"));
        lblCreditoSucata.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblCreditoSucata.setOpaque(true);
        lblCreditoSucata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCreditoSucataMouseClicked(evt);
            }
        });

        lblDebitoProduto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblDebitoProduto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDebitoProduto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio.png"))); // NOI18N
        lblDebitoProduto.setText("1000");
        lblDebitoProduto.setBorder(javax.swing.BorderFactory.createTitledBorder("Débito  Chumbo"));
        lblDebitoProduto.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblDebitoProduto.setOpaque(true);
        lblDebitoProduto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDebitoProdutoMouseClicked(evt);
            }
        });

        lblSaldoProduto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblSaldoProduto.setForeground(new java.awt.Color(0, 102, 0));
        lblSaldoProduto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSaldoProduto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        lblSaldoProduto.setText("1000");
        lblSaldoProduto.setBorder(javax.swing.BorderFactory.createTitledBorder("Saldo Chumbo"));
        lblSaldoProduto.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblSaldoProduto.setOpaque(true);
        lblSaldoProduto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSaldoProdutoMouseClicked(evt);
            }
        });

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnAlt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-caixa-cheia-16x16.png"))); // NOI18N
        btnAlt.setText("Alt");
        btnAlt.setEnabled(false);
        btnAlt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAltActionPerformed(evt);
            }
        });

        btnNotaEntrada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/overlays.png"))); // NOI18N
        btnNotaEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNotaEntradaActionPerformed(evt);
            }
        });

        txtRentabilidade.setDecimals(2);
        txtRentabilidade.setEnabled(false);
        txtRentabilidade.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnRentabilidade.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnRentabilidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRentabilidadeActionPerformed(evt);
            }
        });

        lblDebitoSucata.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblDebitoSucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDebitoSucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/table_add.png"))); // NOI18N
        lblDebitoSucata.setText("1000");
        lblDebitoSucata.setBorder(javax.swing.BorderFactory.createTitledBorder("Débito Sucata"));
        lblDebitoSucata.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblDebitoSucata.setOpaque(true);

        lblCreditoProduto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblCreditoProduto.setForeground(new java.awt.Color(0, 102, 0));
        lblCreditoProduto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCreditoProduto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        lblCreditoProduto.setText("1000");
        lblCreditoProduto.setBorder(javax.swing.BorderFactory.createTitledBorder("Crédito Chumbo"));
        lblCreditoProduto.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblCreditoProduto.setOpaque(true);

        lblSaldoSucata.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblSaldoSucata.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSaldoSucata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/table_add.png"))); // NOI18N
        lblSaldoSucata.setText("1000");
        lblSaldoSucata.setBorder(javax.swing.BorderFactory.createTitledBorder("Saldo Sucata"));
        lblSaldoSucata.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblSaldoSucata.setOpaque(true);
        lblSaldoSucata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSaldoSucataMouseClicked(evt);
            }
        });

        btnManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder.png"))); // NOI18N
        btnManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManualActionPerformed(evt);
            }
        });

        btnAutomatico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/basket_add.png"))); // NOI18N
        btnAutomatico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutomaticoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(lblCreditoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCreditoProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDebitoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDebitoProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSaldoSucata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2)
                        .addComponent(lblSaldoProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
                            .addComponent(jLabel3))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                            .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNomeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(txtNomeFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnAlt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addComponent(txtEmpresaNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRentabilidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnManual, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAutomatico, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(btnHoras1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addGap(0, 0, 0)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDatFim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5))
                    .addComponent(jTabbedEtq))
                .addGap(2, 2, 2))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDatFim, txtDatIni});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnHoras1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnManual, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnAutomatico, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(txtNomeFilial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel7)
                                        .addComponent(txtRentabilidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(btnRentabilidade))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNomeCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAdd)
                                        .addComponent(btnAlt))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnHoras))
                                .addGap(8, 8, 8)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton3)))
                            .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)))
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton7)
                                .addGap(8, 8, 8)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnNotaEntrada)
                                    .addComponent(txtNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(txtEmpresaNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton6))
                            .addComponent(jLabel4))
                        .addGap(64, 64, 64)))
                .addGap(4, 4, 4)
                .addComponent(jTabbedEtq)
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCreditoSucata)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDebitoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCreditoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSaldoSucata)
                        .addComponent(lblDebitoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblSaldoProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnHoras1, txtNota});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnHoras, txtOrdemCompra});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton3, txtPedido});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAdd, txtNomeCliente});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnRentabilidade, txtRentabilidade});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton9, lblCreditoProduto, lblCreditoSucata, lblDebitoProduto, lblDebitoSucata, lblSaldoProduto, lblSaldoSucata});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAutomatico, btnManual, txtEmpresaNome});

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
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedCotacao.addTab("Sucatas", pnlForm);

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


    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        getSucataPedido();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTabbedEtqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedEtqMouseClicked
        int abaselecionada = jTabbedEtq.getSelectedIndex();

    }//GEN-LAST:event_jTabbedEtqMouseClicked

    private void btnHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHorasActionPerformed
        getSucataOrdemCompra();
    }//GEN-LAST:event_btnHorasActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        getSucataCliente();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        getSucataCliente();


    }//GEN-LAST:event_jButton7ActionPerformed

    private Integer lancamento;
    private Integer sequencia;
    private double pesoSelecionado;
    private void jTableEnvioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEnvioMouseClicked
        try {
            int linhaSelSit = jTableEnvio.getSelectedRow();
            int colunaSelSit = jTableEnvio.getSelectedColumn();

            String motivo = (jTableEnvio.getValueAt(linhaSelSit, 14).toString());
            lancamento = Integer.valueOf(jTableEnvio.getValueAt(linhaSelSit, 17).toString());

            sequencia = Integer.valueOf(jTableEnvio.getValueAt(linhaSelSit, 18).toString());

            this.sucataMovimento = new SucataMovimento();
            this.sucataMovimento = sucataMovimentoDAO.getSucataMovimento("LANCAMENTO", " and usu_codlan = " + lancamento + " \nand usu_seqmov = " + sequencia);

            if (this.sucataMovimento != null) {
                if (this.sucataMovimento.getCodigolancamento() > 0) {
                    this.addReg = false;
                    btnAlt.setEnabled(true);
                    this.processo = "AUTOMATICO";
                    if (this.sucataMovimento.getSituacao().equals("MANUAL")) {
                        this.processo = "MANUAL";
                    }
                    txtRentabilidade.setValue(this.sucataMovimento.getPercentualrendimento());
                    txtPedido.setText(jTableEnvio.getValueAt(linhaSelSit, 2).toString());
                    pesoSelecionado = Double.valueOf(jTableEnvio.getValueAt(linhaSelSit, 3).toString());

                    txtOrdemCompra.setText(jTableEnvio.getValueAt(linhaSelSit, 8).toString());
                    txtNota.setText(jTableEnvio.getValueAt(linhaSelSit, 10).toString());

                    txtNotaEntrada.setText(jTableEnvio.getValueAt(linhaSelSit, 12).toString());
                    txtEmpresa.setText(jTableEnvio.getValueAt(linhaSelSit, 20).toString());
                    txtFilial.setText(jTableEnvio.getValueAt(linhaSelSit, 21).toString());

                    getFilial();

                }
            }

        } catch (Exception ex) {
            Logger.getLogger(SucatasManutencao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jTableEnvioMouseClicked

    private void txtPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPedidoActionPerformed
        getSucataPedido();
    }//GEN-LAST:event_txtPedidoActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        getSucataCliente();
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        sair();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void btnHoras1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoras1ActionPerformed

        getSucataNota();
    }//GEN-LAST:event_btnHoras1ActionPerformed

    private void txtFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialActionPerformed
        txtCodigo.requestFocus();
    }//GEN-LAST:event_txtFilialActionPerformed

    private void txtEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmpresaActionPerformed
        txtFilial.requestFocus();
    }//GEN-LAST:event_txtEmpresaActionPerformed

    private void txtNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaActionPerformed
        getSucataNota();
    }//GEN-LAST:event_txtNotaActionPerformed

    private void txtOrdemCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOrdemCompraActionPerformed
        getSucataOrdemCompra();
    }//GEN-LAST:event_txtOrdemCompraActionPerformed

    private void txtEmpresaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmpresaKeyTyped
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            System.out.println("aaaaa");
        }
    }//GEN-LAST:event_txtEmpresaKeyTyped

    private void lblCreditoSucataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCreditoSucataMouseClicked
        if (txtCodigo.getValue() != null) {
            try {

                getListarMovimento("", " and suc.usu_codemp = " + txtEmpresa.getText() + ""
                        + " and suc.usu_codfil = " + txtFilial.getText() + "\n"
                        + " and suc.usu_codcli = " + txtCodigo.getText() + "\n"
                        + " and suc.usu_debcre ='4 - CREDITO' ");

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_lblCreditoSucataMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed

        try {
            SucatasManual sol = new SucatasManual();
            MDIFrame.add(sol, true);
            sol.setPosicao();
            if (this.sucataMovimento == null) {
                this.sucataMovimento = new SucataMovimento();
            }
            this.addReg = true;
            this.processo = "MANUAL";
            sol.setMaximum(true); // executa maximizado 
            sol.setRecebePalavra(this, this.sucataMovimento, this.filial, this.processo, this.sucata, this.cliente, this.addReg,
                    0.0, 0.0, "PO", txtPedido.getText(), txtNota.getText(), txtOrdemCompra.getText(), txtOrdemCompra.getText());
        } catch (Exception e) {
        }


    }//GEN-LAST:event_btnAddActionPerformed

    private void btnAltActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltActionPerformed
        try {
            SucatasManual sol = new SucatasManual();
            MDIFrame.add(sol, true);
            //   sol.setSize(900, 600);
            sol.setPosicao();
            sol.setMaximum(true); // executa maximizado 
            // this.sucataMovimento = new SucataMovimento();
            sol.setRecebePalavra(this, this.sucataMovimento, this.filial, this.processo, this.sucata, this.cliente, this.addReg,
                    0.0, 0.0, "EDIT", txtPedido.getText(), txtNota.getText(), txtOrdemCompra.getText(), "0");
        } catch (Exception e) {
        }
    }//GEN-LAST:event_btnAltActionPerformed

    private void btnNotaEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNotaEntradaActionPerformed
        if (txtNotaEntrada.getValue() != null) {
            try {

                getListarMovimento("", " and usu_codemp = " + txtEmpresa.getText() + ""
                        + " and usu_codfil = " + txtFilial.getText() + ""
                        + " and usu_numnfc = " + txtNotaEntrada.getText());

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btnNotaEntradaActionPerformed

    private void btnRentabilidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRentabilidadeActionPerformed
        if (txtRentabilidade.getValue() != null) {
            try {

                getListarMovimento("", " and suc.usu_codemp = " + txtEmpresa.getText() + ""
                        + " and suc.usu_codfil = " + txtFilial.getText() + "\n"
                        + " and suc.usu_codcli = " + txtCodigo.getText() + "\n"
                        + " and suc.usu_perren = " + txtRentabilidade.getText());

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btnRentabilidadeActionPerformed

    private void lblDebitoProdutoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDebitoProdutoMouseClicked
        if (txtCodigo.getValue() != null) {
            try {

                getListarMovimento("", " and suc.usu_codemp = " + txtEmpresa.getText() + ""
                        + " and suc.usu_codfil = " + txtFilial.getText() + "\n"
                        + " and suc.usu_codcli = " + txtCodigo.getText() + "\n"
                        + " and suc.usu_debcre ='3 - DEBITO' ");

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_lblDebitoProdutoMouseClicked

    private void lblProvisaoProdutoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblProvisaoProdutoKeyPressed

    }//GEN-LAST:event_lblProvisaoProdutoKeyPressed

    private void lblProvisaoProdutoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblProvisaoProdutoMouseClicked
        if (txtCodigo.getValue() != null) {
            try {

                getListarMovimento("", " and suc.usu_codemp = " + txtEmpresa.getText() + ""
                        + " and suc.usu_codfil = " + txtFilial.getText() + "\n"
                        + " and suc.usu_codcli = " + txtCodigo.getText() + "\n"
                        + " and suc.usu_debcre ='1 - GERADO' ");

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_lblProvisaoProdutoMouseClicked

    private void lblProvisaoSucataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblProvisaoSucataMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lblProvisaoSucataMouseClicked

    private void lblProvisaoSucataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblProvisaoSucataKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblProvisaoSucataKeyPressed

    private void btnManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManualActionPerformed
        if (txtCodigo.getValue() != null) {
            try {

                getListarMovimento("", " and suc.usu_codemp = " + txtEmpresa.getText() + ""
                        + " and suc.usu_codfil = " + txtFilial.getText() + "\n"
                        + " and suc.usu_codcli = " + txtCodigo.getText() + "\n"
                        + " and suc.usu_sitsuc = 'MANUAL'");

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btnManualActionPerformed

    private void btnAutomaticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutomaticoActionPerformed
        if (txtCodigo.getValue() != null) {
            try {

                getListarMovimento("", " and suc.usu_codemp = " + txtEmpresa.getText() + ""
                        + " and suc.usu_codfil = " + txtFilial.getText() + "\n"
                        + " and suc.usu_codcli = " + txtCodigo.getText() + "\n"
                        + " and suc.usu_sitsuc != 'MANUAL'");

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btnAutomaticoActionPerformed

    private void lblSaldoProdutoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSaldoProdutoMouseClicked

        try {
            SucatasManual sol = new SucatasManual();
            MDIFrame.add(sol, true);
            sol.setPosicao();
            this.sucataMovimento = new SucataMovimento();
            this.addReg = true;
            this.processo = "MANUAL";
            sol.setMaximum(true); // executa maximizado 
            sol.setRecebePalavra(this, this.sucataMovimento, this.filial, this.processo, this.sucata, this.cliente,
                    this.addReg, pesoSaldoSucata, pesoSaldoProduto,
                    "PV", txtPedido.getText(), txtNota.getText(), txtOrdemCompra.getText(), "0");
        } catch (Exception e) {
        }
    }//GEN-LAST:event_lblSaldoProdutoMouseClicked

    private void lblSaldoSucataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSaldoSucataMouseClicked
        try {

            SucatasManual sol = new SucatasManual();
            MDIFrame.add(sol, true);
            sol.setPosicao();
            this.sucataMovimento = new SucataMovimento();
            this.addReg = true;
            this.processo = "MANUAL";
            sol.setMaximum(true); // executa maximizado 
            sol.setRecebePalavra(this, this.sucataMovimento, this.filial, this.processo, this.sucata, this.cliente,
                    this.addReg, pesoSaldoSucata, pesoSaldoProduto,
                    "OC", txtPedido.getText(), txtNota.getText(), txtOrdemCompra.getText(), "0");
        } catch (Exception e) {
        }
    }//GEN-LAST:event_lblSaldoSucataMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAlt;
    private javax.swing.JButton btnAutomatico;
    private javax.swing.JButton btnHoras;
    private javax.swing.JButton btnHoras1;
    private javax.swing.JButton btnManual;
    private javax.swing.JButton btnNotaEntrada;
    private javax.swing.JButton btnRentabilidade;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedCotacao;
    private javax.swing.JTabbedPane jTabbedEtq;
    private javax.swing.JTable jTableEnvio;
    private javax.swing.JPanel jpanelDebito;
    private javax.swing.JLabel lblCreditoProduto;
    private javax.swing.JLabel lblCreditoSucata;
    private javax.swing.JLabel lblDebitoProduto;
    private javax.swing.JLabel lblDebitoSucata;
    private javax.swing.JLabel lblProvisaoProduto;
    private javax.swing.JLabel lblProvisaoSucata;
    private javax.swing.JLabel lblSaldoProduto;
    private javax.swing.JLabel lblSaldoSucata;
    private javax.swing.JLabel lblSomaSucataOutro;
    private javax.swing.JPanel pnlForm;
    private org.openswing.swing.client.TextControl txtCodigo;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.TextControl txtEmpresaNome;
    private org.openswing.swing.client.TextControl txtFilial;
    private org.openswing.swing.client.TextControl txtNomeCliente;
    private org.openswing.swing.client.TextControl txtNomeFilial;
    private org.openswing.swing.client.TextControl txtNota;
    private org.openswing.swing.client.TextControl txtNotaEntrada;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.NumericControl txtRentabilidade;
    // End of variables declaration//GEN-END:variables
}
