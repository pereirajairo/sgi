/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.CondicaoPagamento;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.CondicaoPagamentoDAO;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.SucataDAO;
import br.com.sgi.dao.SucataEcoDao;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.TratarXml;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSEmailAtendimento;
import br.com.sgi.ws.WSNotaFiscalSaida;
import br.com.sgi.ws.WsOrdemDeCompra;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.table.DefaultTableCellRenderer;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class SucataManualAuto extends InternalFrame {

    private Sucata sucata;
    private List<Pedido> listPedido = new ArrayList<Pedido>();
    private SucataDAO sucataDAO;
    private SucataMovimentoDAO sucataMovimentoDAO;
    private SucataMovimento sucataMovimento;
    private SucatasManutencao veioCampo;
    private SucataContaCorrente veioCampoContaCorrente;
    private UtilDatas utilDatas;

    private String processo;

    private Cliente cliente;

    private boolean newReg;
    private String executar;

    private Usuario usuario;

    public SucataManualAuto() {
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
            //   optOrd.setVisible(false);
            optPed.setVisible(false);
            btnGravarOrdem.setVisible(false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void getProcessoUsuario() throws SQLException {
        this.usuario = Menu.getUsuario();
    }

    public void preencherComboCondicao(Integer id) throws SQLException, Exception {
        CondicaoPagamentoDAO dao = new CondicaoPagamentoDAO();
        List<CondicaoPagamento> list = new ArrayList<CondicaoPagamento>();
        String cod;
        String des;
        String desger;
        txtCondPgto.removeAllItems();
        list = dao.getCondicaoPagamentos("", " and  codemp = 1  ");
        if (list != null) {
            for (CondicaoPagamento co : list) {
                cod = co.getCodigo();
                des = co.getDescricao();
                desger = cod + " - " + des;
                txtCondPgto.addItem(desger);
            }
        }
    }

    public void preencherComboFilial(Integer id) throws SQLException, Exception {
        FilialDAO filialDAO = new FilialDAO();
        List<Filial> listFilial = new ArrayList<Filial>();
        String cod;
        String des;
        String desger;
        txtFilial.removeAllItems();

        if(id==11){
            listFilial = filialDAO.getFilias("", " and  codemp = 1 and codfil in (" + id + ")  ");
        }else{
            listFilial = filialDAO.getFilias("", " and  codemp = 1 and codfil in (" + id + ",1)  ");
        }
        

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
        preencherComboFilial(sucataMovimento.getFilialsucata());
        if (sucataMovimento != null) {
            if (sucataMovimento.getCodigolancamento() > 0) {
                popularCampo(sucataMovimento);
            }
        }
    }

    private Filial filial;
    private String origem;

    public void setRecebePalavraConta(SucataContaCorrente veioInput, SucataMovimento sucataMovimento, Filial filial, Sucata sucata, Cliente cliente, Filial filialfaturamento,
            String origem) throws Exception {
        this.veioCampoContaCorrente = veioInput;
        this.sucataMovimento = sucataMovimento;
        this.sucata = new Sucata();
        this.cliente = new Cliente();
        this.filial = new Filial();

        this.origem = origem;
        this.filial = filial;
        if (filial != null) {
            if (filial.getFilial() > 0) {
                txtTransacao.setText(filial.getTransacao_complemento());
                txtCondPgto.setSelectedItem(filial.getCondicao_pgto_complemento());
            }
        }

        this.cliente = cliente;
        txtCliente.setText(cliente.getCodigo() + " - " + cliente.getNome());
        txtEstadoCliente.setText(cliente.getEstado());
        DesabilitarCampo(false);
        if (sucataMovimento != null) {
            if (sucataMovimento.getFilial() == 0) {
                sucataMovimento.setFilial(filial.getFilial());
            }
            if (sucataMovimento.getFilialsucata() != 0) {
                preencherComboFilial(sucataMovimento.getFilialsucata());
            } else {
                preencherComboFilial(sucataMovimento.getFilial());
            }
        }
        limparTela();
        int filial_id = 0;
        if (sucataMovimento != null) {
            if (sucataMovimento.getCodigolancamento() > 0) {
                popularCampo(sucataMovimento);
                filialSelecionada = verificarFilial("");
                filial_id = Integer.valueOf(filialSelecionada.trim());
                if (filial_id == 11) {
                    txtSucata.setSelectedItem("1003");
                } else {
                    txtSucata.setSelectedItem("1001");
                }

                if (sucataMovimento.getPesofaturado() > 0) {
                    txtSerie.setText(filial.getSerie());
                    if (filialfaturamento != null) {
                        txtEstadoFilial.setText(filialfaturamento.getEstado());
                        txtTransacao.setText("5405W");
                        if (filialfaturamento.getEstado().equals("SC")) {
                            txtTransacao.setText("6102W");
                            if (txtEstadoCliente.getText().equals(txtEstadoFilial.getText())) {
                                txtTransacao.setText("5102W");
                            }
                        }

                    }
                }
            }
        }

    }

    private void popularCampo(SucataMovimento sucataMovimento) {
     
        txtSucata.setSelectedItem(sucataMovimento.getSucata());
        if (sucataMovimento.getSucata() == null) {
            txtSucata.setSelectedItem("1001");
        }
        if (sucataMovimento.getSucata().isEmpty()) {
            txtSucata.setSelectedItem("1001");
        }

        sucataMovimento.setPesosucata(0.0);

        if (sucataMovimento.getAutomoto().equals("AUT")) {
            txtTipoSucata.setSelectedItem("AUTO");
        }
        if (sucataMovimento.getAutomoto().equals("MOT")) {
            txtTipoSucata.setSelectedItem("MOTO");
        }

        if (origem.equals("MANUTENCAO")) {
            txtPeso.setValue(sucataMovimento.getPesorecebido());
        } else {
            txtPeso.setValue(sucataMovimento.getPesofaturado());
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
        txtPreco.setValue(sucataMovimento.getPrecounitario());
        txtValorOrdem.setValue(sucataMovimento.getValortotal());
        btnExcluir.setEnabled(true);
        if (sucataMovimento.getPedido() > 0 || sucataMovimento.getOrdemcompra() > 0) {
            btnExcluir.setEnabled(false);
            if (this.sucataMovimento.getDebitocredito().equals("1 - GERADO")) {
                btnExcluir.setEnabled(true);
            }
            btnExcluir.setEnabled(true);
        }

        if (txtOrdemCompra.equals("0") || txtOrdemCompra.getText().isEmpty()) {
            optOrd.setEnabled(true);
            optOrd.setVisible(true);
        }

    }

    public void atualizarNotaCOmplementar(boolean retorno, String info) throws SQLException {
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

    private void salvar() throws SQLException, Exception {
        this.sucataMovimento.setGerarordem("OCP");
        this.sucataMovimento.setEnviaremail("N");
        this.sucataMovimento.setEnviaremail("");
        this.sucataMovimento.setDebitocredito("3 - DEBITO");
        if (!txtOrdemCompra.getText().equals("0")) {
            this.sucataMovimento.setOrdemcompra(Integer.valueOf(txtOrdemCompra.getText()));
        }
        this.sucataMovimento.setPesosucata(txtPeso.getDouble());
        if (!this.sucataMovimentoDAO.gerarOrdem(this.sucataMovimento)) {
        } else {
            if (!sucataDAO.gerarOrdem(sucataMovimento)) {
            } else {
                gerarProcessoERP();
                atualizar();
            }
        }

    }

    private boolean validarCampos() {
        boolean retorno = true;
        if (!optCre.isSelected() && !optDeb.isSelected() && !optNfv.isSelected() && !optOrd.isSelected() && !optPed.isSelected()) {
            retorno = false;
            JOptionPane.showMessageDialog(null, "ERRO: Selecione o tipo de transação Pedido/Ordem/Crédito Manual/Débito Manual",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (txtPeso.getDouble() == 0) {
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

    private void gravarSucata(String gerarpedido) throws SQLException, ParseException {

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
                    sucataMovimento.setSequencia(sucataMovimentoDAO.proxCodCad(sucataMovimento.getCodigolancamento()));
                    sucataMovimento.setEmpresa(1);
                    String filial = txtFilial.getSelectedItem().toString();
                    int index = filial.indexOf("-");
                    filialSelecionada = filial.substring(0, index).trim();
                    sucataMovimento.setFilial(Integer.valueOf(filialSelecionada));
                    sucataMovimento.setFilialsucata(Integer.valueOf(filialSelecionada));

                    sucataMovimento.setCliente(cliente.getCodigo());
                    sucataMovimento.setPedido(Integer.valueOf(txtPedido.getText()));
                    sucataMovimento.setNotasaida(Integer.valueOf(txtNota.getText()));
                    sucataMovimento.setNotaentrada(Integer.valueOf(txtNotaEntrada.getText()));
                    sucataMovimento.setOrdemcompra(Integer.valueOf(txtOrdemCompra.getText()));
                    sucataMovimento.setPesosaldo(0.0);
                    sucataMovimento.setQuantidadedevolvida(0.0);
                    sucataMovimento.setPesodevolvido(0.0);
                    sucataMovimento.setPesofaturado(txtPesoSaida.getDouble());
                    sucataMovimento.setPesorecebido(txtPesoEntrada.getDouble());
                    sucataMovimento.setDatageracaoS(this.utilDatas.converterDateToStr(new Date()));

                    if (txtTipoSucata.getSelectedItem().toString().equals("AUTO")) {
                        sucataMovimento.setAutomoto("AUT");
                    }

                    if (txtTipoSucata.getSelectedItem().toString().equals("MOTO")) {
                        sucataMovimento.setAutomoto("MOT");
                    }

                    sucataMovimento.setObservacaomovimento(txtObservacao.getText());

                    sucataMovimento.setObservacaoacerto("SUCATA REGISTRADA MANUALMENTE PELO USUÁRIO ");
                    sucataMovimento.setNumerotitulo("");
                    sucataMovimento.setTipomovimento("V");
                    sucataMovimento.setPercentualrendimento(0.0);

                    sucataMovimento.setUsuario(0);
                    sucataMovimento.setDatageracao(new Date());
                    sucataMovimento.setDatamovimento(new Date());
                    sucataMovimento.setHorageracao("0");
                    sucataMovimento.setHoramovimento("0");

                    sucataMovimento.setEnviaremail("N");
                    sucataMovimento.setEmail("");

                    sucataMovimento.setCodigopeso(0);
                    sucataMovimento.setCodigominuta(0);
                    sucataMovimento.setSucata(txtSucata.getSelectedItem().toString());
                    sucataMovimento.setSituacao("MANUAL");

                    if (optNfv.isSelected()) {  // NOTAS FISCAIS
                        sucataMovimento.setSituacao("MANUAL");
                        sucataMovimento.setDebitocredito("4 - CREDITO");
                        sucataMovimento.setProduto("COIMP0002");
                        sucataMovimento.setPesoajustado(0.0);
                        sucataMovimento.setPesomovimento(0.0);
                        sucataMovimento.setGerarordem("NFV");
                        sucataMovimento.setQuantidade(1.0);
                        sucataMovimento.setOrdemcompra(0);
                        sucataMovimento.setPesoordemcompra(0.0);
                        sucataMovimento.setNotasaida(0);

                        sucataMovimento.setPesorecebido(0.0);

                        sucataMovimento.setPesofaturado(txtPeso.getDouble());
                        sucataMovimento.setPesosucata(txtPeso.getDouble());
                        sucataMovimento.setPrecounitario(txtPreco.getDouble());
                        sucataMovimento.setValortotal(txtValorOrdem.getDouble());
                        if (txtTransacao.getText() != null) {
                            sucataMovimento.setTransacao_complemento(txtTransacao.getText());
                        }

                        sucataMovimento.setCondicao_pgto_complemento(txtCondPgto.getSelectedItem().toString());

                        sucataMovimento.setSerie(txtSerie.getText());
                    }
                    if (optCre.isSelected()) {  // lançamento manual de crédito de sucata
                        sucataMovimento.setDebitocredito("4 - CREDITO");
                        sucataMovimento.setPesosucata(txtPeso.getDouble());
                        sucataMovimento.setProduto(txtProduto.getSelectedItem().toString());
                        sucataMovimento.setPesoajustado(0.0);
                        sucataMovimento.setPesoajustado(0.0);
                        sucataMovimento.setPesomovimento(0.0);
                        sucataMovimento.setGerarordem("N");
                        sucataMovimento.setPesopedido(0.0);
                        sucataMovimento.setQuantidade(0.0);
                        sucataMovimento.setPesoordemcompra(0.0);
                    }

                    if (optDeb.isSelected()) {
                        sucataMovimento.setPedido(Integer.valueOf(txtPedido.getText().trim()));
                        sucataMovimento.setPesomovimento(txtPeso.getDouble());
                        sucataMovimento.setProduto(txtProduto.getSelectedItem().toString());
                        sucataMovimento.setGerarordem("N");
                        sucataMovimento.setDebitocredito("3 - DEBITO");
                        sucataMovimento.setPesosucata(0.0);
                        sucataMovimento.setPesoajustado(0.0);
                        sucataMovimento.setPesopedido(0.0);
                        sucataMovimento.setQuantidade(0.0);
                        sucataMovimento.setPesoordemcompra(0.0);
                    }

                    if (optOrd.isSelected()) {
                        sucataMovimento.setDebitocredito("1 - GERADO");
                        sucataMovimento.setSituacao("AUTOMATICO");
                        sucataMovimento.setPesosucata(txtPeso.getDouble());
                        sucataMovimento.setPesosucata(0.0);
                        sucataMovimento.setPesoordemcompra(txtPeso.getDouble());
                        sucataMovimento.setProduto(txtSucata.getSelectedItem().toString());

                        sucataMovimento.setPesorecebido(0.0);
                        sucataMovimento.setPesoajustado(0.0);
                        sucataMovimento.setPesomovimento(0.0);
                        sucataMovimento.setGerarordem("N");
                        sucataMovimento.setPedido(0);
                        sucataMovimento.setPesopedido(0.0);
                        sucataMovimento.setQuantidade(0.0);

                        sucataMovimento.setNotasaida(0);
                        sucataMovimento.setPesofaturado(0.0);
                        sucataMovimento.setNotaentrada(0);

                        if (!filialSelecionada.equals("1")) {
                            filialSelecionada = "1";
                        }
                        if (filialSelecionada.equals("10")) {
                            filialSelecionada = "10";
                        }
                        if (filialSelecionada.equals("11")) {
                            filialSelecionada = "11";
                        }
                        if (filialSelecionada.equals("13")) {
                            filialSelecionada = "13";
                        }

                        sucataMovimento.setFilial(Integer.valueOf(filialSelecionada));
                    }

                    if (this.sucataMovimento.getCliente() == 10469 || this.sucataMovimento.getCliente() == 17231 || this.sucataMovimento.getCliente() == 16654) {
                        this.sucataMovimento.setTransacao("901HB");
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
                            atualizar();
                        }

                    }

                }
            }
        }

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

    private void calcularValor() {
        txtObservacao.setText("");
        this.processo = "NOTAFISCAL";
        double peso = txtPeso.getDouble();
        txtValorOrdem.setValue(0);
        if (peso != 0) {

            if (peso <= 0) {
                JOptionPane.showMessageDialog(null, "Informe o peso desejado",
                        "ATENÇÃO:", JOptionPane.INFORMATION_MESSAGE);
            } else {
                double preco = txtPreco.getDouble();
                double pesofaturado = txtPeso.getDouble();
                double saldo = 0;

//                txtPesoSaldo.setValue(saldo);
//                txtPesoSucata.setValue(saldo);
//           
                if (peso < 0) {
                    peso = peso * -1;
                }

                optNfv.setEnabled(false);

                if (sucataMovimento.getNotasaida() > 0) {
                    if ((peso > 0) && (preco > 0)) {

                        if (origem.equals("NOTA")) {

                            optNfv.setEnabled(true);

                        }

                        double valor = peso * preco;
                        txtValorOrdem.setValue(valor);

                        txtObservacao.setText("Gerar nota fiscal complementar:\n"
                                + "Peso: " + txtPeso.getDouble() + " \n"
                                + "Valor Unitário: " + txtPreco.getDouble() + "\n"
                                + "Valor Total: " + valor + "\n"
                                + "Código: COIM0002");
                        txtPeso.requestFocus();

                    }
                }

                txtPeso.requestFocus();
            }

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
        txtPeso.setValue(0);

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
        txtPreco.setText("0");
        txtValorOrdem.setText("0");

        txtProduto.setSelectedItem("SELECIONE");
        txtTipoSucata.setEnabled(true);
        txtTipoSucata.requestFocus();
        btnSelecionarTipo.setEnabled(true);

    }

    private void DesabilitarCampo(boolean acao) {
        btnSelecionarTipo.setEnabled(acao);
        txtPeso.setEnabled(acao);

        txtSucata.setEnabled(acao);
        txtProduto.setEnabled(acao);
        btnRentabilidade.setEnabled(acao);
        txtPreco.setEnabled(acao);
        txtValorOrdem.setEnabled(acao);
        txtObservacao.setEnabled(acao);

        btnExcluir.setEnabled(acao);

        btnGravarOrdem.setEnabled(acao);
        btnGravarManual.setEnabled(acao);

        optNfv.setEnabled(acao);
        optCre.setEnabled(acao);
        optDeb.setEnabled(acao);
        optOrd.setEnabled(acao);
        btnRentabilidade.setEnabled(acao);

        btnSelecionarTipo.setEnabled(true);

    }

    private void DesabilitarCampoAutomobilistico(boolean acao) {
        if (txtTipoSucata.getSelectedItem().equals("AUTO")) {
            txtProduto.setSelectedItem("AUTO");

        }
        if (txtTipoSucata.getSelectedItem().equals("MOTO")) {
            txtProduto.setSelectedItem("AUTO");
        }

        txtSucata.setEnabled(acao);
        txtObservacao.setEnabled(acao);
        btnRentabilidade.setEnabled(acao);
        popularCampo(sucataMovimento);
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
        jLabel11 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnGravarOrdem = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        txtSucata = new javax.swing.JComboBox<>();
        txtPreco = new org.openswing.swing.client.NumericControl();
        txtPeso = new org.openswing.swing.client.NumericControl();
        txtValorOrdem = new org.openswing.swing.client.NumericControl();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btnGravarManual = new javax.swing.JButton();
        txtTipoSucata = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        btnExcluir = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        txtProduto = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        btnGravarNota = new javax.swing.JButton();
        btnRentabilidade = new javax.swing.JButton();
        btnCalcular = new javax.swing.JButton();
        btnSelecionarTipo = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        optCre = new javax.swing.JRadioButton();
        optDeb = new javax.swing.JRadioButton();
        optOrd = new javax.swing.JRadioButton();
        optPed = new javax.swing.JRadioButton();
        optNfv = new javax.swing.JRadioButton();
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
        btnNotaFiscal = new javax.swing.JButton();
        txtTransacao = new org.openswing.swing.client.TextControl();
        txtCondPgto = new javax.swing.JComboBox<>();
        txtSerie = new org.openswing.swing.client.TextControl();
        txtEstadoCliente = new org.openswing.swing.client.TextControl();
        jLabel20 = new javax.swing.JLabel();
        txtEstadoFilial = new org.openswing.swing.client.TextControl();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setForeground(new java.awt.Color(0, 0, 153));

        jLabel11.setText("Transação");

        jLabel2.setText("Tipo:");

        jLabel1.setText("Valor:");

        btnGravarOrdem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravarOrdem.setText("Gerar OC");
        btnGravarOrdem.setEnabled(false);
        btnGravarOrdem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarOrdemActionPerformed(evt);
            }
        });

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit16x16.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        txtSucata.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtSucata.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "1001", "1002", "1003", "1004", "1005", "P2CBI0001" }));
        txtSucata.setEnabled(false);

        txtPreco.setDecimals(2);
        txtPreco.setEnabled(false);
        txtPreco.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecoActionPerformed(evt);
            }
        });

        txtPeso.setDecimals(2);
        txtPeso.setEnabled(false);
        txtPeso.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPeso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoActionPerformed(evt);
            }
        });

        txtValorOrdem.setDecimals(2);
        txtValorOrdem.setEnabled(false);
        txtValorOrdem.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel10.setText("Observação");

        jLabel13.setText("Peso Sucata: ");

        jLabel14.setText("Serie Nota");

        btnGravarManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-e-fechar16x16.png"))); // NOI18N
        btnGravarManual.setText("Gravar Manual");
        btnGravarManual.setEnabled(false);
        btnGravarManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarManualActionPerformed(evt);
            }
        });

        txtTipoSucata.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTipoSucata.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AUTO", "MOTO" }));
        txtTipoSucata.setEnabled(false);

        jLabel15.setText("Produto:");

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-delete-16x16.png"))); // NOI18N
        btnExcluir.setText("Excluir Registro");
        btnExcluir.setEnabled(false);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        jLabel17.setText("Sucata");

        txtProduto.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtProduto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AUTO", "MOTO" }));
        txtProduto.setEnabled(false);
        txtProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProdutoActionPerformed(evt);
            }
        });

        jLabel18.setText("Valor Total:");

        jLabel19.setText("Condição Pgto");

        btnGravarNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        btnGravarNota.setText("Gerar NF");
        btnGravarNota.setEnabled(false);
        btnGravarNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarNotaActionPerformed(evt);
            }
        });

        btnRentabilidade.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        btnRentabilidade.setEnabled(false);
        btnRentabilidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRentabilidadeActionPerformed(evt);
            }
        });

        btnCalcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        btnSelecionarTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnSelecionarTipo.setEnabled(false);
        btnSelecionarTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarTipoActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup1.add(optCre);
        optCre.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optCre.setForeground(new java.awt.Color(0, 153, 0));
        optCre.setText("Crédito Manual");
        optCre.setEnabled(false);
        optCre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optCreActionPerformed(evt);
            }
        });

        buttonGroup1.add(optDeb);
        optDeb.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optDeb.setForeground(new java.awt.Color(255, 0, 0));
        optDeb.setText("Débito Manual");
        optDeb.setEnabled(false);
        optDeb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optDebActionPerformed(evt);
            }
        });

        buttonGroup1.add(optOrd);
        optOrd.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optOrd.setText("Ordem Compra");
        optOrd.setEnabled(false);
        optOrd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optOrdActionPerformed(evt);
            }
        });

        buttonGroup1.add(optPed);
        optPed.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optPed.setText("Pedido");
        optPed.setEnabled(false);
        optPed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optPedActionPerformed(evt);
            }
        });

        buttonGroup1.add(optNfv);
        optNfv.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optNfv.setText("Nota Complementar");
        optNfv.setEnabled(false);
        optNfv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optNfvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optNfv, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optCre, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optDeb, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(optOrd, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(optPed, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optNfv)
                    .addComponent(optCre)
                    .addComponent(optDeb)
                    .addComponent(optOrd)
                    .addComponent(optPed))
                .addGap(2, 2, 2))
        );

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
                            .addComponent(txtPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                            .addComponent(txtPesoPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(4, 4, 4)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNota, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                            .addComponent(txtPesoSaida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNotaEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                            .addComponent(txtPesoEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPesoOrdem, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8))))
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
        txtFilial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialActionPerformed(evt);
            }
        });

        jLabel3.setText("Filial");

        btnNotaFiscal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        btnNotaFiscal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNotaFiscalActionPerformed(evt);
            }
        });

        txtTransacao.setEnabled(false);
        txtTransacao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtCondPgto.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCondPgto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "0", "05D", "3", "30", "0001", "1010" }));

        txtSerie.setEnabled(false);
        txtSerie.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtEstadoCliente.setEnabled(false);
        txtEstadoCliente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel20.setText("Estado");

        txtEstadoFilial.setEnabled(false);
        txtEstadoFilial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)
                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnGravarNota)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGravarOrdem)
                        .addGap(2, 2, 2)
                        .addComponent(btnGravarManual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtTipoSucata, 0, 1, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnSelecionarTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtProduto, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtSucata, 0, 0, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtPreco, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnNotaFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtValorOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEstadoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEstadoFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel3)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel14))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtFilial, 0, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtSerie, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCondPgto, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTransacao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(2, 2, 2))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel2});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel13, jLabel18});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExcluir, btnGravarManual, btnGravarOrdem});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(txtLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addComponent(jLabel20)
                            .addComponent(txtEstadoFilial, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtTipoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addComponent(btnSelecionarTipo, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPreco, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNotaFiscal, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtTransacao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel19)
                                    .addComponent(txtCondPgto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnCalcular)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(jLabel15)
                                                    .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(7, 7, 7)
                                                .addComponent(jLabel13)))
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(txtSucata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(txtValorOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel17)
                                                    .addComponent(jLabel18)
                                                    .addComponent(jLabel14))))))))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGravarNota, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGravarOrdem, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                            .addComponent(btnGravarManual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSair, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                        .addGap(6, 6, 6))
                    .addComponent(txtEstadoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtPeso, txtValorOrdem});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnRentabilidade, txtPreco, txtSucata});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnExcluir, btnGravarManual, btnGravarNota, btnGravarOrdem, btnSair});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void btnGravarOrdemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarOrdemActionPerformed
        if (ManipularRegistros.pesos(" Gerar ordem compra?")) {
            try {
                gravarPedido();
            } catch (SQLException ex) {
                Logger.getLogger(SucataManualAuto.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(SucataManualAuto.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnGravarOrdemActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void txtPesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoActionPerformed
        tipocalculo = "NORMAL";
        calcularValor();

    }//GEN-LAST:event_txtPesoActionPerformed

    private void btnGravarManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarManualActionPerformed
        if (ManipularRegistros.pesos(" Gravar registro")) {
            try {
                gravarSucata("N");
            } catch (SQLException ex) {
                Logger.getLogger(SucataManualAuto.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(SucataManualAuto.class.getName()).log(Level.SEVERE, null, ex);
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
            sol.setRecebePalavraAuto(this, "ENTRADA", "", this.sucataMovimento);

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

        }
        return filialSelecionada;
    }

    private void gravarPedido() throws Exception {

        if (this.executar.equals("PV")) { // pedido
            if ((txtPeso.getDouble() > 0)) {
                JOptionPane.showMessageDialog(null, "Info: Esse processo vai gerar um pedido para baixar saldo total de sucata  ",
                        "Atenção:", JOptionPane.INFORMATION_MESSAGE);
                if (ManipularRegistros.pesos(" Gerar pedido de baixa de sucata?")) {

                    try {
                        gravarSucata("PV");
                        if (optPed.isSelected()) {
                            //gerarProcessoERP();

                            atualizar();

                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(SucataManualAuto.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            if (this.executar.equals("NFV")) { //NOTA COMPLEMENTAR

                try {
                    gravarSucata("NFV");
                    if (optNfv.isSelected()) {
                        // gerarProcessoERP();
                        String condicao = txtCondPgto.getSelectedItem().toString();
                        int index = condicao.indexOf("-");
                        String condicaoselecionada = condicao.substring(0, index).trim();

                        gerarNotaFiscal(txtTransacao.getText(), condicaoselecionada.trim());
                        atualizar();
                        optNfv.setEnabled(false);
                        btnGravarNota.setEnabled(false);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro: " + ex,
                            "Erro:", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro: " + ex,
                            "Erro:", JOptionPane.ERROR_MESSAGE);
                }

            } else if (this.executar.equals("OCP")) { // ordem de compra
                if ((txtPeso.getDouble() > 0) && (txtOrdemCompra.getText().equals("0"))) {
                    try {
                        gravarSucata("OCP");
                        if (optOrd.isSelected()) {
                            // gerarProcessoERP();
                            gerarOrdemCompra();
                            atualizar();
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro: " + ex,
                                "Erro:", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro: " + ex,
                                "Erro:", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Erro: Peso(s) de sucata ou produto inválido ",
                            "Erro:", JOptionPane.ERROR_MESSAGE);
                }

            }
        }
    }

    private void gerarNotaFiscal(String transacao, String condicao) throws Exception {

        if (!txtTransacao.getText().isEmpty()) {
            getProcessoUsuario();
            this.sucataMovimento.setTransacao_complemento(txtTransacao.getText());
            this.sucataMovimento.setCondicao_pgto_complemento(condicao);

//        this.sucataMovimento.setTransacao_complemento("5102W");// cd
//        this.sucataMovimento.setTransacao_complemento("5405W");// cd
//        this.sucataMovimento.setCondicao_pgto_complemento("30");
            txtObservacao.setText(WSNotaFiscalSaida.chamarMetodoWsXmlHttpSapiens(this.sucataMovimento, this.usuario.getNome(), this.usuario.getSenha()));
            TratarXml xml = new TratarXml();
            xml.GravarArquivo(txtObservacao.getText(), cliente.getCodigo().toString());
            NotaFiscal nota = xml.getNota();
            if (nota != null) {
                String retorno = "Empresa : " + nota.getEmpresa() + "\n";
                retorno += "Filial : " + nota.getFilial() + "\n";
                retorno += "Nota Fiscal : " + nota.getNotafiscal() + "\n";
                retorno += "Série Fiscal : " + nota.getSerie() + "\n";

                retorno += "Cliente : " + sucataMovimento.getCliente() + " - " + cliente.getNome() + "\n";
                retorno += "Produto : " + sucataMovimento.getProduto() + "\n";
                retorno += "Preço Unitário : " + sucataMovimento.getPrecounitario() + "\n";
                retorno += "Quantidade  : " + sucataMovimento.getQuantidade() + "\n";
                retorno += "Valor Total : " + sucataMovimento.getValortotal() + "\n";
                retorno += "Situação : " + nota.getSituacao() + "\n";
                txtObservacao.setText(retorno);
                if (sucataMovimento.getCodigolancamento() > 0 && nota.getSituacao().equals("OK")) {
                    sucataMovimento.setObservacaomovimento(txtObservacao.getText());
                    sucataMovimento.setNotasaida(nota.getNotafiscal());
                    sucataMovimento.setSerie(nota.getSerie());

                    if (sucataMovimentoDAO.atualizarNota(sucataMovimento)) {

                    }
                } else {
                    removerRegistro(true, "Erro para gerar a nota ao gerar a nota fiscal complementar");
                }
            }
        } else {
            Mensagem.mensagem("ERROR", "Verifique a transação");
        }

    }

    private void gerarOrdemCompra() throws SQLException {
        String empresa = "1";
        String fornecedor = String.valueOf(sucataMovimento.getCliente());
        Double peso = txtPeso.getDouble();
        String re = "";
        String sNumOcp = "";
        String resultado = "";
        String erro = "";
        SucataEcoParametros sucataEcoParametros = new SucataEcoParametros();
        SucataEcoDao sucataEcoDao = new SucataEcoDao();
        sucataEcoParametros = sucataEcoDao.getSucataEcoParamentros(this.filialSelecionada, empresa);
        sucataEcoParametros.setTransacao("90417");
        WsOrdemDeCompra wsOrdemDeCompra = new WsOrdemDeCompra();
        try {
            re = wsOrdemDeCompra.ordemDeCompraSucataEcoSapiens(sucataEcoParametros, fornecedor, peso);
            int intretorno = re.indexOf("<mensagemRetorno>");
            int intFinalRetorno = re.indexOf("</mensagemRetorno>");
            resultado = re.substring(intretorno + 17, intFinalRetorno);
            int retornoNumOcp = re.indexOf("<numOcp>");
            int retornoNumOcpFim = re.indexOf("</numOcp>");
            int retornoErro = re.indexOf("<retorno>");
            int retornoErroFim = re.indexOf("</retorno>");
            sNumOcp = re.substring(retornoNumOcp + 8, retornoNumOcpFim);
            erro = re.substring(retornoErro + 9, retornoErroFim);

            if (resultado.equals("Processado com sucesso.")) {

                txtOrdemCompra.setText(sNumOcp.trim());
                txtObservacao.setText("ORDEM DE COMPRA GERADA COM SUCESSO: " + sNumOcp);

            } else if (resultado.equals("Ocorreram erros.")) {
                txtObservacao.setText(resultado);
            }
            txtObservacao.setText("Situação de ordem de compra: ");
            if (sucataMovimento.getCodigolancamento() > 0) {
                sucataMovimento.setObservacaomovimento(txtObservacao.getText());
                sucataMovimento.setOrdemcompra(Integer.valueOf(sNumOcp.trim()));
                sucataMovimento.setGerarordem("N");
                sucataMovimento.getGerarordem();
                if (sucataMovimentoDAO.atualizarOrdemCompra(sucataMovimento)) {

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SucataEcoLancamento.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void gerarProcessoERP() throws Exception {
        WSEmailAtendimento wsEma = new WSEmailAtendimento();
        wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
    }

    private void pesoFinalCreditoDebito() {

        txtPreco.setValue(0);
        txtValorOrdem.setValue(0);
        txtPedido.setText("0");
        txtNota.setText("0");
        txtOrdemCompra.setText("0");

        txtNotaEntrada.setText("0");
        txtPesoPedido.setText("0");
        txtPesoSaida.setText("0");
        txtPesoOrdem.setText("0");
        txtPesoEntrada.setText("0");

    }

    private void optCreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optCreActionPerformed
        txtLancamento.setText("0");
        txtSequencia.setText("0");
        btnGravarNota.setEnabled(false);
        btnGravarOrdem.setEnabled(false);
        btnGravarManual.setEnabled(true);
        txtObservacao.setText("LANÇAMENTO DE CREDITO MANUAL, PESO " + txtPeso.getText() + " REFERENTE À: ");
        pesoFinalCreditoDebito();
    }//GEN-LAST:event_optCreActionPerformed

    private void optDebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optDebActionPerformed
        txtLancamento.setText("0");
        txtSequencia.setText("0");
        btnGravarNota.setEnabled(false);
        btnGravarOrdem.setEnabled(false);
        btnGravarManual.setEnabled(true);
        txtObservacao.setText("LANÇAMENTO DE DEBITO MANUAL, PESO " + txtPeso.getText() + " REFERENTE À: ");
        pesoFinalCreditoDebito();
    }//GEN-LAST:event_optDebActionPerformed

    private void btnRentabilidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRentabilidadeActionPerformed

        if (origem.equals("NOTA")) {
            optCre.setEnabled(false);
            optDeb.setEnabled(false);
            optOrd.setEnabled(false);
            optNfv.setEnabled(true);

        } else {
            optCre.setEnabled(true);
            optDeb.setEnabled(true);
            optOrd.setEnabled(true);
            optNfv.setEnabled(false);
        }
        txtPreco.setEnabled(true);
        txtPeso.setEnabled(true);
        txtPreco.setText("7.10");
        txtPeso.setText("1");
        txtPeso.requestFocus();
    }//GEN-LAST:event_btnRentabilidadeActionPerformed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        tipocalculo = "NORMAL";
        calcularValor();
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void btnSelecionarTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarTipoActionPerformed

        DesabilitarCampoAutomobilistico(true);
        btnSelecionarTipo.setEnabled(true);
        try {
            if (origem.equals("NOTA")) {
                preencherComboCondicao(1);
            }

        } catch (Exception ex) {
            Logger.getLogger(SucataManualAuto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSelecionarTipoActionPerformed

    private void optOrdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optOrdActionPerformed

        txtPedido.setText("0");
        txtNotaEntrada.setText("0");
        txtNota.setText("0");
        txtOrdemCompra.setText("0");

        this.executar = "OCP";
        btnGravarOrdem.setVisible(true);
        btnGravarOrdem.setEnabled(true);
        btnGravarNota.setEnabled(false);
        btnGravarManual.setEnabled(false);
        txtObservacao.setText("SERA GERADO UMA ORDEM DE COMPRA SEM VINCULO COM A NOTA FISCAL DE SAÍDA ");


    }//GEN-LAST:event_optOrdActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void optPedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPedActionPerformed

        this.executar = "PV";
        txtPedido.setText("0");
        btnGravarNota.setEnabled(true);
        btnGravarOrdem.setEnabled(false);
        btnGravarManual.setEnabled(false);
        btnExcluir.setEnabled(false);
        String mesagem = txtObservacao.getText();
        mesagem += "\n ATENÇÃO:  Essa opção não irá gerar Ordem Compra";
        txtObservacao.setText(mesagem);
    }//GEN-LAST:event_optPedActionPerformed

    private void txtNotaEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaEntradaActionPerformed
        txtPesoEntrada.requestFocus();
    }//GEN-LAST:event_txtNotaEntradaActionPerformed

    private void btnGravarNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarNotaActionPerformed
        if ((txtPeso.getDouble() > 0) && (txtPesoSaida.getDouble() > 0) && (!txtNota.getText().equals("0"))) {
            String filial = verificarFilial("Nota Fiscal Complementar");
            if (!filial.equals("0")) {
                if (ManipularRegistros.pesos(" Gerar nota complementar para o cliente \n" + txtCliente.getText() + " \nfilial " + txtFilial.getSelectedItem() + " ?")) {
                    try {
                        gravarPedido();
                    } catch (SQLException ex) {
                        Mensagem.mensagemRegistros("ERRO", ex.toString());

                    } catch (Exception ex) {
                        Logger.getLogger(SucatasManual.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                Mensagem.mensagemRegistros("ERRO", "Filial não selecionada");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Erro: Peso(s) de sucata ou produto inválido ",
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGravarNotaActionPerformed

    private void optNfvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optNfvActionPerformed
        btnGravarNota.setEnabled(true);
//        btnGravarManual.setEnabled(false);
//        optCre.setEnabled(true);
//        optDeb.setEnabled(true);
//        optPed.setEnabled(false);
//        optOrd.setEnabled(false);
////        if (filial != null) {
////            if (filial.getFilial() > 0) {
////                txtTransacao.setText(filial.getTransacao_complemento());
////                txtCondPgto.setSelectedItem(filial.getCondicao_pgto_complemento());
////            }
////        }

        this.executar = "NFV";
    }//GEN-LAST:event_optNfvActionPerformed

    private void txtPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecoActionPerformed
        calcularValor();
    }//GEN-LAST:event_txtPrecoActionPerformed

    private void btnNotaFiscalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNotaFiscalActionPerformed
        calcularValor();

    }//GEN-LAST:event_btnNotaFiscalActionPerformed

    private void txtFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialActionPerformed
//        if (txtFilial.getSelectedIndex() != -1) {
//            if (!txtFilial.getSelectedItem().toString().equals("0-SELECIONE")) {
////                String filial = txtFilial.getSelectedItem().toString();
////              
////                int index = filial.indexOf("-");
////                String fil = filial.substring(0, index).trim();
////                txtTransacao.setText("6102W");
////                if(fil.equals("1")){
////                    txtTransacao.setText("5102W");
////                }
//
//            }
//        }
    }//GEN-LAST:event_txtFilialActionPerformed

    private void txtProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProdutoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProdutoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnGravarManual;
    private javax.swing.JButton btnGravarNota;
    private javax.swing.JButton btnGravarOrdem;
    private javax.swing.JButton btnNotaFiscal;
    private javax.swing.JButton btnRentabilidade;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSelecionarTipo;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton optCre;
    private javax.swing.JRadioButton optDeb;
    private javax.swing.JRadioButton optNfv;
    private javax.swing.JRadioButton optOrd;
    private javax.swing.JRadioButton optPed;
    private org.openswing.swing.client.TextControl txtCliente;
    private javax.swing.JComboBox<String> txtCondPgto;
    private org.openswing.swing.client.TextControl txtEstadoCliente;
    private org.openswing.swing.client.TextControl txtEstadoFilial;
    private javax.swing.JComboBox<String> txtFilial;
    private org.openswing.swing.client.TextControl txtLancamento;
    private org.openswing.swing.client.TextControl txtNota;
    private org.openswing.swing.client.TextControl txtNotaEntrada;
    private javax.swing.JTextArea txtObservacao;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.NumericControl txtPeso;
    private org.openswing.swing.client.NumericControl txtPesoEntrada;
    private org.openswing.swing.client.NumericControl txtPesoOrdem;
    private org.openswing.swing.client.NumericControl txtPesoPedido;
    private org.openswing.swing.client.NumericControl txtPesoSaida;
    private org.openswing.swing.client.NumericControl txtPreco;
    private javax.swing.JComboBox<String> txtProduto;
    private org.openswing.swing.client.TextControl txtSequencia;
    private org.openswing.swing.client.TextControl txtSerie;
    private javax.swing.JComboBox<String> txtSucata;
    private javax.swing.JComboBox<String> txtTipoSucata;
    private org.openswing.swing.client.TextControl txtTransacao;
    private org.openswing.swing.client.NumericControl txtValorOrdem;
    // End of variables declaration//GEN-END:variables
}
