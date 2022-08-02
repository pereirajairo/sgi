/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Produto;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.ProdutoDAO;
import br.com.sgi.dao.SucataDAO;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
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
public final class SucatasManual extends InternalFrame {

    private Sucata sucata;
    private List<Pedido> listPedido = new ArrayList<Pedido>();
    private SucataDAO sucataDAO;
    private SucataMovimentoDAO sucataMovimentoDAO;
    private SucataMovimento sucataMovimento;
    private SucatasManutencao veioCampo;
    private SucataContaCorrente veioCampoContaCorrente;
    private UtilDatas utilDatas;

    private String datI;
    private String datF;
    private String processo;

    private Cliente cliente;

    private boolean newReg;
    private String executar;

    public SucatasManual() {
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

        listFilial = filialDAO.getFilias("", " and codemp = 1 and codfil in (1,10,11,12,14,14)");
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
        double perren = 0.0;
        double preco = 0.0;
        double valor = 0.0;
        limparTela();
        DesabilitarCampo(false);
        preencherComboFilial(1);
        if (sucataMovimento != null) {
            if (sucataMovimento.getCodigolancamento() > 0) {
                if (filial != null) {
                    if (filial.getFilial() > 0) {
                        txtSucata.setSelectedItem(filial.getProdutosucata());
                    }
                }
                txtTipoSucata.setEnabled(true);
                txtTipoSucata.requestFocus();
                btnSelecionarTipo.setEnabled(true);

                txtRentabilidade.setValue(sucataMovimento.getPercentualrendimento());
                perren = sucataMovimento.getPercentualrendimento() / 100;
                String tipo = sucataMovimento.getAutomoto();
                if (tipo.equals("AUT")) {
                    tipo = "AUTO";
                }
                if (tipo.equals("MOT")) {
                    tipo = "MOTO";
                }
                txtTipoSucata.setSelectedItem(tipo);

                txtPeso.setValue(sucataMovimento.getPesosucata());

                if (!sucataMovimento.getDebitocredito().equals("3 - DEBITO") && !sucataMovimento.getDebitocredito().equals("4 - CREDITO")) {

                    if (perren > 0) {
                        sucataMovimento.setPesosucata(sucataMovimento.getPesosucata() / perren);
                    }

                }

                txtPesoSucata.setValue(sucataMovimento.getPesosucata());
                txtPesoSaldo.setValue(sucataMovimento.getPesomovimento());
                if (sucataMovimento.getPesosucata() < 0) {
                    txtPeso.setValue(sucataMovimento.getPesosucata() * -1);
                    txtPesoSucata.setValue(sucataMovimento.getPesosucata() * -1);
                    txtPesoSaldo.setValue(sucataMovimento.getPesosucata() * -1);
                    txtPesoSaldo.setValue(sucataMovimento.getPesomovimento() * -1);
                }
                if (sucataMovimento.getDebitocredito().equals("3 - DEBITO")) {
                    optDeb.setSelected(true);
                }
                preco = 0.0;
                txtPreco.setValue(preco);
                if (sucataMovimento.getDebitocredito().equals("4 - CREDITO")) {
                    optCre.setSelected(true);
                    txtPesoSaldo.setValue(sucataMovimento.getPesosucata() * perren);
                    txtValorOrdem.setValue(txtPreco.getDouble() * preco);
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
                txtPreco.setValue(sucataMovimento.getQuantidadedevolvida());
                txtValorOrdem.setValue(sucataMovimento.getPesodevolvido());

                if (sucataMovimento.getPedido() > 0 || sucataMovimento.getOrdemcompra() > 0) {
                    btnExcluir.setEnabled(false);
                    optPedInd.setEnabled(false);
                    optDeb.setEnabled(false);
                    optCre.setEnabled(false);
                }

            } else {
                setTitle(ClientSettings.getInstance().getResources().getResource("Movimento de sucata "));
                txtTipoSucata.setEnabled(true);
                txtTipoSucata.requestFocus();
                btnSelecionarTipo.setEnabled(true);
                txtPeso.setValue(pesoSucata);
                txtPesoSaldo.setValue(pesoProduto);
                txtPesoSucata.setValue(pesoSucata);

                txtPedido.setText(pedido);
                txtPesoPedido.setValue(sucataMovimento.getPesopedido());
                txtNota.setText(nota);
                txtPesoSaida.setValue(sucataMovimento.getPesofaturado());
                txtOrdemCompra.setText(ordem);
                txtPesoOrdem.setValue(sucataMovimento.getPesoordemcompra());
                txtNotaEntrada.setText(notaentrada);
                txtPesoEntrada.setValue(sucataMovimento.getPesorecebido());

            }
        }
    }

    public void setRecebePalavraConta(SucataContaCorrente veioInput, SucataMovimento sucataMovimento, Filial filial,
            String processo, Sucata sucata, Cliente cliente, boolean addReg,
            double pesoSucata, double pesoProduto, String executar,
            String pedido,
            String nota,
            String ordem,
            String notaentrada) throws Exception {
        this.veioCampoContaCorrente = veioInput;
        this.processo = processo;
        this.sucataMovimento = sucataMovimento;
        this.sucata = new Sucata();
        this.cliente = new Cliente();
        this.cliente = cliente;
        this.newReg = addReg;
        this.executar = executar;

        txtCliente.setText(cliente.getCodigo() + " - " + cliente.getNome());
        double perren = 0.0;
        double preco = 0.0;
        double valor = 0.0;
        limparTela();
        DesabilitarCampo(false);
        preencherComboFilial(1);
        if (sucataMovimento != null) {
            if (sucataMovimento.getCodigolancamento() > 0) {
                if (filial != null) {
                    if (filial.getFilial() > 0) {
                        txtSucata.setSelectedItem(filial.getProdutosucata());
                    }
                }
                txtTipoSucata.setEnabled(true);
                txtTipoSucata.requestFocus();
                btnSelecionarTipo.setEnabled(true);

                txtRentabilidade.setValue(sucataMovimento.getPercentualrendimento());
                perren = sucataMovimento.getPercentualrendimento() / 100;
                String tipo = sucataMovimento.getAutomoto();
                if (tipo.equals("AUT")) {
                    tipo = "AUTO";
                }
                if (tipo.equals("MOT")) {
                    tipo = "MOTO";
                }
                txtTipoSucata.setSelectedItem(tipo);

                txtPeso.setValue(sucataMovimento.getPesosucata());

                if (!sucataMovimento.getDebitocredito().equals("3 - DEBITO") && !sucataMovimento.getDebitocredito().equals("4 - CREDITO")) {

                    if (perren > 0) {
                        sucataMovimento.setPesosucata(sucataMovimento.getPesosucata() / perren);
                    }

                }

                txtPesoSucata.setValue(sucataMovimento.getPesosucata());
                txtPesoSaldo.setValue(sucataMovimento.getPesomovimento());
                if (sucataMovimento.getPesosucata() < 0) {
                    txtPeso.setValue(sucataMovimento.getPesosucata() * -1);
                    txtPesoSucata.setValue(sucataMovimento.getPesosucata() * -1);
                    txtPesoSaldo.setValue(sucataMovimento.getPesosucata() * -1);
                    txtPesoSaldo.setValue(sucataMovimento.getPesomovimento() * -1);
                }
                if (sucataMovimento.getDebitocredito().equals("3 - DEBITO")) {
                    optDeb.setSelected(true);
                }
                preco = 0.0;
                txtPreco.setValue(preco);
                if (sucataMovimento.getDebitocredito().equals("4 - CREDITO")) {
                    optCre.setSelected(true);
                    txtPesoSaldo.setValue(sucataMovimento.getPesosucata() * perren);
                    txtValorOrdem.setValue(txtPreco.getDouble() * preco);
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
                txtPreco.setValue(sucataMovimento.getQuantidadedevolvida());
                txtValorOrdem.setValue(sucataMovimento.getPesodevolvido());

                if (sucataMovimento.getPedido() > 0 || sucataMovimento.getOrdemcompra() > 0) {
                    btnExcluir.setEnabled(false);
                    optPedInd.setEnabled(false);
                    optDeb.setEnabled(false);
                    optCre.setEnabled(false);
                }

            } else {
                setTitle(ClientSettings.getInstance().getResources().getResource("Movimento de sucata "));
                txtTipoSucata.setEnabled(true);
                txtTipoSucata.requestFocus();
                btnSelecionarTipo.setEnabled(true);
                txtPeso.setValue(pesoSucata);
                txtPesoSaldo.setValue(pesoProduto);
                txtPesoSucata.setValue(pesoSucata);

                txtPedido.setText(pedido);
                txtPesoPedido.setValue(sucataMovimento.getPesopedido());
                txtNota.setText(nota);
                txtPesoSaida.setValue(sucataMovimento.getPesofaturado());
                txtOrdemCompra.setText(ordem);
                txtPesoOrdem.setValue(sucataMovimento.getPesoordemcompra());
                txtNotaEntrada.setText(notaentrada);
                txtPesoEntrada.setValue(sucataMovimento.getPesorecebido());

            }
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
        this.sucataMovimento.setPesosucata(txtPeso.getDouble());
        if (!this.sucataMovimentoDAO.gerarOrdem(this.sucataMovimento)) {
        } else {
            if (!sucataDAO.gerarOrdem(sucataMovimento)) {
            } else {
//                ExecutarWS ws = new ExecutarWS();
//                ws.executar(Menu.username, Menu.userpwd);
                atualizar();
            }
        }

    }

    private void salvarNota() throws SQLException, Exception {

        this.sucataMovimento.setGerarordem("NFV");
        this.sucataMovimento.setEnviaremail("");
        this.sucataMovimento.setSituacao("MANUAL");
        this.sucataMovimento.setDebitocredito("3 - DEBITO");
        this.sucataMovimento.setPesoajustado(txtPeso.getDouble());
        this.sucataMovimento.setPesosaldo(txtPesoSaldo.getDouble());
        if (!this.sucataMovimentoDAO.gerarNota(this.sucataMovimento)) {
        } else {
            if (!sucataDAO.gerarNota(sucataMovimento)) {

            } else {
//                ExecutarWS ws = new ExecutarWS();
//                ws.executar(Menu.username, Menu.userpwd);
                atualizar();
            }
        }

    }

    private boolean validarCampos() {
        boolean retorno = true;
        if (!optCre.isSelected() && !optDeb.isSelected() && !optPedInd.isSelected() && !optOrd.isSelected() && !optPed.isSelected() && !optNfv.isSelected()) {
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
                    sucataMovimento.setSequencia(sucataMovimentoDAO.proxCodCad(sucataMovimento.getCodigolancamento()));
                    sucataMovimento.setEmpresa(1);
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

                    sucataMovimento.setSituacao("MANUAL");
                    if (optPedInd.isSelected() || optPed.isSelected()) {
                        sucataMovimento.setGerarordem(gerarpedido);
                        sucataMovimento.setDebitocredito("1 - GERADO");
                        sucataMovimento.setPesosucata(txtPesoSaldo.getDouble());
                        sucataMovimento.setProduto(txtProduto.getSelectedItem().toString());
                        sucataMovimento.setPesoajustado(0.0);
                        sucataMovimento.setPesomovimento(0.0);
                        sucataMovimento.setPesoordemcompra(txtPeso.getDouble());
                        if (optPed.isSelected()) {
                            sucataMovimento.setPesoordemcompra(0.0);
                        }

                        sucataMovimento.setPesopedido(txtPesoSaldo.getDouble());
                        sucataMovimento.setQuantidade(txtPesoSaldo.getDouble());
                        sucataMovimento.setSituacao("AUTOMATICO");
                    }

                    if (optCre.isSelected()) {
                        sucataMovimento.setDebitocredito("4 - CREDITO");
                        sucataMovimento.setPesosucata(txtPesoSucata.getDouble());
                        sucataMovimento.setProduto(txtProduto.getSelectedItem().toString());
                        sucataMovimento.setPesoajustado(txtPesoSaldo.getDouble());
                        sucataMovimento.setPesomovimento(txtPesoSaldo.getDouble());
                        sucataMovimento.setGerarordem("N");
                        sucataMovimento.setPesopedido(txtPesoSaldo.getDouble());
                        sucataMovimento.setQuantidade(txtPesoSaldo.getDouble());

                    }

                    if (optDeb.isSelected()) {
                        sucataMovimento.setDebitocredito("3 - DEBITO");
                        sucataMovimento.setPesosucata(txtPesoSucata.getDouble());
                        sucataMovimento.setProduto(txtProduto.getSelectedItem().toString());
                        sucataMovimento.setPesoajustado(txtPesoSaldo.getDouble());
                        sucataMovimento.setPesomovimento(txtPesoSaldo.getDouble());
                        sucataMovimento.setGerarordem("N");
                        sucataMovimento.setPesopedido(txtPesoSaldo.getDouble());
                        sucataMovimento.setQuantidade(txtPesoSaldo.getDouble());
                    }

                    if (optOrd.isSelected()) {
                        sucataMovimento.setDebitocredito("1 - GERADO");
                        sucataMovimento.setPesosucata(txtPesoSucata.getDouble() * -1);
                        sucataMovimento.setProduto(txtProduto.getSelectedItem().toString());
                        sucataMovimento.setPesoajustado(0.0);
                        sucataMovimento.setPesomovimento(0.0);
                        sucataMovimento.setGerarordem("OCP");
                        sucataMovimento.setPesopedido(txtPesoSaldo.getDouble() * -1);
                        sucataMovimento.setQuantidade(txtPesoSaldo.getDouble());
                        sucataMovimento.setOrdemcompra(0);
                        sucataMovimento.setNotasaida(Integer.valueOf(txtNota.getText()));
                        sucataMovimento.setFilial(Integer.valueOf(filialSelecionada));
                    }
                    sucataMovimento.setPedido(Integer.valueOf(txtPedido.getText()));
                    sucataMovimento.setNotasaida(Integer.valueOf(txtNota.getText()));
                    sucataMovimento.setNotaentrada(Integer.valueOf(txtNotaEntrada.getText()));
                    sucataMovimento.setOrdemcompra(Integer.valueOf(txtOrdemCompra.getText()));
                    sucataMovimento.setPesofaturado(txtPesoSaida.getDouble());
                    sucataMovimento.setPesorecebido(txtPesoEntrada.getDouble());

                    if (optNfv.isSelected()) {
                        sucataMovimento.setDebitocredito("4 - CREDITO");

                        sucataMovimento.setProduto("COIMP0002");

                        sucataMovimento.setPesoajustado(0.0);
                        sucataMovimento.setPesomovimento(0.0);
                        sucataMovimento.setGerarordem("NFV");

                        sucataMovimento.setQuantidade(1.0);
                        sucataMovimento.setOrdemcompra(0);
                        sucataMovimento.setPesoordemcompra(0.0);
                        sucataMovimento.setNotasaida(1);
                        sucataMovimento.setFilial(Integer.valueOf(filialSelecionada));
                        sucataMovimento.setPedido(Integer.valueOf(txtPedido.getText()));
                        sucataMovimento.setPesopedido(txtPesoPedido.getDouble());

                        sucataMovimento.setNotaentrada(0);
                        sucataMovimento.setOrdemcompra(0);
                        sucataMovimento.setPesofaturado(txtPeso.getDouble());
                        sucataMovimento.setPesosucata(txtPeso.getDouble());
                        sucataMovimento.setPesorecebido(0.0);
                        sucataMovimento.setQuantidadedevolvida(txtPreco.getDouble());
                        sucataMovimento.setPesodevolvido(txtValorOrdem.getDouble());
                    }

                    sucataMovimento.setAutomoto(txtTipoSucata.getSelectedItem().toString());
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
                    sucataMovimento.setPercentualrendimento(txtRentabilidade.getDouble());

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
                            if (!optPedInd.isSelected()) {
                                atualizar();
                            }

                        }

                    }

                }
            }
        }

    }

    private void buscarRentabilidade() throws SQLException {
        txtPesoSaldo.setEnabled(false);
        if (txtTipoSucata.getSelectedItem().equals("IND")) {
            ProdutoDAO dao = new ProdutoDAO();
            Produto pro = new Produto();
            pro = dao.getProdutoLigacao("", " and codcli =" + this.cliente.getCodigo() + "\n"
                    + "and codpro = '" + txtSucata.getSelectedItem().toString() + "'\n ");
            if (pro != null) {
                if (pro.getEmpresa() > 0) {
                    txtRentabilidade.setValue(pro.getRentabilidade());
                    txtPeso.setEnabled(true);
                    txtPeso.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, " Rentabilidade não encontrada", "PROBLEMAS", JOptionPane.ERROR_MESSAGE, null);
                }
            } else {
                JOptionPane.showMessageDialog(null, " Rentabilidade não encontrada", "PROBLEMAS", JOptionPane.ERROR_MESSAGE, null);
            }

        } else {
            txtPeso.setEnabled(true);
            txtPeso.requestFocus();
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

        double peso = txtPeso.getDouble();
        double rent = txtRentabilidade.getDouble();
        double pesoconvertido = 0.0;
        if (peso != 0) {
            if (processo.equals("MANUAL")) {
                txtPesoSucata.setValue(peso);
                txtPesoSaldo.setValue(peso);
                btnGravarManual.setEnabled(false);
                if (optPedInd.isSelected()) {
                    btnGravarManual.setEnabled(false);
                }

                if (rent > 0) {
                    pesoconvertido = peso * (rent / 100);
                    txtPesoSaldo.setValue(pesoconvertido);
                }

            } else {
                if (peso <= 0) {
                    JOptionPane.showMessageDialog(null, "Informe o peso desejado",
                            "ATENÇÃO:", JOptionPane.INFORMATION_MESSAGE);
                } else {

                    double preco = txtPreco.getDouble();
                    double pesofaturado = txtPesoSaida.getDouble();
                    double saldo = pesofaturado - peso;
                    txtPesoSaldo.setValue(saldo);
                    txtPesoSucata.setValue(saldo);
                    if (peso < 0) {
                        peso = peso * -1;
                    }

                    if ((peso > 0) && (preco > 0)) {
                        double valor = peso * preco;
                        txtValorOrdem.setValue(valor);
                        btnGravarNota.setEnabled(false);
                        btnGravarOrdem.setEnabled(false);
                        if (this.processo.equals("NFV")) {
                            btnGravarOrdem.setEnabled(false);
                            btnGravarNota.setEnabled(false);

                        }
                        txtObservacao.setText("Gerar nota fiscal complementar:\n"
                                + "Peso: " + txtPeso.getDouble() + " \n"
                                + "Valor Unitário: " + txtPreco.getDouble() + "\n"
                                + "Valor Total: " + valor + "\n"
                                + "Código: COIM0002");
                        txtPeso.requestFocus();
                    }
                }
            }
        }

    }

    private void calcularValorReverso() {
        btnGravarNota.setEnabled(false);
        btnGravarOrdem.setEnabled(false);
        btnGravarManual.setEnabled(false);
        double peso = txtPesoSaldo.getDouble();
        double rent = txtRentabilidade.getDouble();
        double pesoconvertido = 0.0;
        if (peso != 0) {

            if (processo.equals("MANUAL")) {

                btnGravarManual.setEnabled(true);

                if (rent > 0) {
                    pesoconvertido = peso / (rent / 100);
                    txtPeso.setValue(pesoconvertido);
                    txtPesoSucata.setValue(pesoconvertido);

                }
                txtRentabilidade.requestFocus();

            } else {

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
        txtRentabilidade.setValue(0);
        txtPesoSaldo.setText("0");
        txtPesoSucata.setText("0");
        //   txtPedido.setValue(0);
        txtSucata.setSelectedItem("SELECIONE");
        txtObservacao.setText("LANÇAMENTO DE SUCATA MANUAL");

    }

    private void DesabilitarCampo(boolean acao) {
        btnSelecionarTipo.setEnabled(acao);
        txtPeso.setEnabled(acao);
        txtRentabilidade.setEnabled(acao);
        txtPesoSaldo.setEnabled(acao);
        txtPesoSucata.setEnabled(acao);
        txtSucata.setEnabled(acao);
        txtProduto.setEnabled(acao);
        btnRentabilidade.setEnabled(acao);
        txtPreco.setEnabled(acao);
        txtValorOrdem.setEnabled(acao);
        txtObservacao.setEnabled(acao);

        btnCalcularPeso.setEnabled(acao);
        btnExcluir.setEnabled(acao);
        btnGravarNota.setEnabled(acao);
        btnGravarOrdem.setEnabled(acao);
        btnGravarManual.setEnabled(acao);

        optPedInd.setEnabled(acao);
        optDeb.setEnabled(acao);
        optCre.setEnabled(acao);
    }

    private void DesabilitarCampoIndustrializacao(boolean acao) {
        txtSucata.setEnabled(acao);
        txtProduto.setEnabled(acao);
        btnRentabilidade.setEnabled(acao);
        txtObservacao.setEnabled(acao);
        btnCalcularPeso.setEnabled(acao);
        optPedInd.setEnabled(acao);
        optPed.setEnabled(acao);
        optDeb.setEnabled(acao);
        optCre.setEnabled(acao);
        optOrd.setEnabled(acao);
    }

    private void DesabilitarCampoAutomobilistico(boolean acao) {
        if (txtTipoSucata.getSelectedItem().equals("AUTO")) {
            txtProduto.setSelectedItem("AUTO");
        }
        if (txtTipoSucata.getSelectedItem().equals("MOTO")) {
            txtProduto.setSelectedItem("MOTO");
        }
        txtSucata.setEnabled(acao);
        txtObservacao.setEnabled(acao);
        btnCalcularPeso.setEnabled(acao);
        btnRentabilidade.setEnabled(acao);
        optOrd.setEnabled(acao);
        optNfv.setEnabled(acao);

        optDeb.setEnabled(acao);
        optCre.setEnabled(acao);
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
        btnCalcularPeso = new javax.swing.JButton();
        btnGravarOrdem = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        txtSucata = new javax.swing.JComboBox<>();
        txtPreco = new org.openswing.swing.client.NumericControl();
        txtPeso = new org.openswing.swing.client.NumericControl();
        txtValorOrdem = new org.openswing.swing.client.NumericControl();
        jLabel10 = new javax.swing.JLabel();
        btnGravarNota = new javax.swing.JButton();
        txtPesoSucata = new org.openswing.swing.client.NumericControl();
        txtPesoSaldo = new org.openswing.swing.client.NumericControl();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btnGravarManual = new javax.swing.JButton();
        txtTipoSucata = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        txtRentabilidade = new org.openswing.swing.client.NumericControl();
        btnExcluir = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        txtProduto = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        btnGravarPedido = new javax.swing.JButton();
        btnRentabilidade = new javax.swing.JButton();
        btnCalcular = new javax.swing.JButton();
        btnSelecionarTipo = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        optPedInd = new javax.swing.JRadioButton();
        optCre = new javax.swing.JRadioButton();
        optDeb = new javax.swing.JRadioButton();
        optNfv = new javax.swing.JRadioButton();
        optOrd = new javax.swing.JRadioButton();
        optPed = new javax.swing.JRadioButton();
        txtLancamento = new org.openswing.swing.client.TextControl();
        txtSequencia = new org.openswing.swing.client.TextControl();
        txtCliente = new org.openswing.swing.client.TextControl();
        jLabel6 = new javax.swing.JLabel();
        btnPedido1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtPedido = new org.openswing.swing.client.TextControl();
        btnPedido = new javax.swing.JButton();
        txtNota = new org.openswing.swing.client.TextControl();
        btnNota = new javax.swing.JButton();
        txtNotaEntrada = new org.openswing.swing.client.TextControl();
        btnPedido2 = new javax.swing.JButton();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        btnNota1 = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setForeground(new java.awt.Color(0, 0, 153));

        jLabel11.setText("% Rentabilildade:");

        jLabel2.setText("Tipo:");

        jLabel1.setText("Valor:");

        btnCalcularPeso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bricks.png"))); // NOI18N
        btnCalcularPeso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularPesoActionPerformed(evt);
            }
        });

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
        txtValorOrdem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValorOrdemActionPerformed(evt);
            }
        });

        jLabel10.setText("Observação");

        btnGravarNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        btnGravarNota.setText("Gerar NF");
        btnGravarNota.setEnabled(false);
        btnGravarNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarNotaActionPerformed(evt);
            }
        });

        txtPesoSucata.setDecimals(2);
        txtPesoSucata.setEnabled(false);
        txtPesoSucata.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoSucata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoSucataActionPerformed(evt);
            }
        });

        txtPesoSaldo.setDecimals(2);
        txtPesoSaldo.setEnabled(false);
        txtPesoSaldo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPesoSaldo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoSaldoActionPerformed(evt);
            }
        });

        jLabel13.setText("Peso Sucata: ");

        jLabel14.setText("Saldo Ajustado:");

        btnGravarManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-e-fechar16x16.png"))); // NOI18N
        btnGravarManual.setText("Gravar Manual");
        btnGravarManual.setEnabled(false);
        btnGravarManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarManualActionPerformed(evt);
            }
        });

        txtTipoSucata.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTipoSucata.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "MOTO", "AUTO", "IND" }));
        txtTipoSucata.setEnabled(false);

        jLabel15.setText("Produto:");

        txtRentabilidade.setDecimals(2);
        txtRentabilidade.setEnabled(false);
        txtRentabilidade.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtRentabilidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRentabilidadeActionPerformed(evt);
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

        jLabel17.setText("Sucata");

        txtProduto.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtProduto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "P3CLV0001", "P3CLV0004", "P3CLV0006", "P3CLV0007", "P3CLV0008", "P2CBI0001", "REREM0010", "AUTO", "MOTO" }));
        txtProduto.setEnabled(false);

        jLabel18.setText("Valor Total:");

        jLabel19.setText("Peso Final:");

        btnGravarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-como-16x16.png"))); // NOI18N
        btnGravarPedido.setText("Gerar Pedido");
        btnGravarPedido.setEnabled(false);
        btnGravarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarPedidoActionPerformed(evt);
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
        btnCalcular.setEnabled(false);
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

        buttonGroup1.add(optPedInd);
        optPedInd.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optPedInd.setText("Pedido Industrialização");
        optPedInd.setEnabled(false);
        optPedInd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optPedIndActionPerformed(evt);
            }
        });

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

        buttonGroup1.add(optNfv);
        optNfv.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        optNfv.setText("Nota Complementar");
        optNfv.setEnabled(false);
        optNfv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optNfvActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(optPedInd, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(optPed, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optOrd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optNfv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optCre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optDeb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(optPedInd)
                    .addComponent(optOrd)
                    .addComponent(optNfv)
                    .addComponent(optCre)
                    .addComponent(optDeb)
                    .addComponent(optPed)))
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

        btnPedido1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-como-16x16.png"))); // NOI18N
        btnPedido1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedido1ActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtPedido.setEnabled(false);
        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-como-16x16.png"))); // NOI18N
        btnPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedidoActionPerformed(evt);
            }
        });

        txtNota.setEnabled(false);
        txtNota.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-como-16x16.png"))); // NOI18N
        btnNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNotaActionPerformed(evt);
            }
        });

        txtNotaEntrada.setEnabled(false);
        txtNotaEntrada.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNotaEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotaEntradaActionPerformed(evt);
            }
        });

        btnPedido2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-como-16x16.png"))); // NOI18N
        btnPedido2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedido2ActionPerformed(evt);
            }
        });

        txtOrdemCompra.setEnabled(false);
        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnNota1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-como-16x16.png"))); // NOI18N
        btnNota1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNota1ActionPerformed(evt);
            }
        });

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
                    .addComponent(jLabel16)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPesoPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNota, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel12)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtPesoSaida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(48, 48, 48)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtNotaEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPedido2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtPesoEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(48, 48, 48)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPesoOrdem, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNota1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                    .addComponent(btnNota1)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPedido2)
                    .addComponent(txtNotaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNota)
                    .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPedido)
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
        txtFilial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFilialMouseClicked(evt);
            }
        });
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
                        .addComponent(btnGravarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGravarOrdem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGravarNota)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtTipoSucata, 0, 0, Short.MAX_VALUE)
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
                                .addGap(12, 12, 12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtValorOrdem, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtPreco, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                                .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnPedido1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(2, 2, 2)
                                                .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnNotaFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(jLabel3))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtFilial, 0, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtPesoSucata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtRentabilidade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPesoSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCalcularPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(2, 2, 2))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel2});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel13, jLabel18});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExcluir, btnGravarManual, btnGravarNota, btnGravarOrdem});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(txtLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(11, 11, 11)
                                        .addComponent(jLabel2))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel11)
                                        .addComponent(txtRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtTipoSucata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSelecionarTipo, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnNotaFiscal)
                                .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(txtPesoSucata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPesoSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnCalcularPeso)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel14)
                                .addGap(6, 6, 6))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel15)
                                            .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCalcular)
                                        .addComponent(btnPedido1))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(jLabel13)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel17)
                                            .addComponent(jLabel18)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(txtSucata, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRentabilidade, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(txtValorOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGravarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGravarOrdem, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(btnGravarNota, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(btnGravarManual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSair, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtPeso, txtValorOrdem});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCalcularPeso, btnNotaFiscal, btnRentabilidade, txtPreco, txtSucata});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnExcluir, btnGravarManual, btnGravarNota, btnGravarOrdem, btnGravarPedido, btnSair});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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


    private void btnCalcularPesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularPesoActionPerformed
        calcularValor();
        if (processo.equals("MANUAL")) {
            txtPesoSaldo.setEnabled(true);
        }
    }//GEN-LAST:event_btnCalcularPesoActionPerformed

    private void txtPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecoActionPerformed
        calcularValor();
    }//GEN-LAST:event_txtPrecoActionPerformed

    private void btnGravarOrdemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarOrdemActionPerformed
        if (ManipularRegistros.pesos(" Gerar ordem compra?")) {
            try {
                if (processo.equals("EDIT")) {
                    salvar();
                } else {
                    gravarPedido();
                }

            } catch (SQLException ex) {
                Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
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

    private void txtValorOrdemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValorOrdemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorOrdemActionPerformed

    private void btnGravarNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarNotaActionPerformed
        String filial = verificarFilial("Nota Fiscal Complementar");
        if (!filial.equals("0")) {
            if (ManipularRegistros.pesos(" Gerar nota complementar para o cliente \n" + txtCliente.getText() + " \nfilial " + txtFilial.getSelectedItem())) {
                try {
                    gravarPedido();
                } catch (SQLException ex) {
                    Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            Mensagem.mensagemRegistros("ERRO", "Filial não selecionada");
        }
    }//GEN-LAST:event_btnGravarNotaActionPerformed

    private void txtPesoSucataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoSucataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesoSucataActionPerformed

    private void txtPesoSaldoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoSaldoActionPerformed
        tipocalculo = "REVERSO";
        calcularValorReverso();
    }//GEN-LAST:event_txtPesoSaldoActionPerformed

    private void btnGravarManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarManualActionPerformed
        if (ManipularRegistros.pesos(" Gravar registro")) {
            try {
                gravarSucata("N");
            } catch (SQLException ex) {
                Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnGravarManualActionPerformed
    private String tipocalculo;
    private void txtRentabilidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRentabilidadeActionPerformed
        if (tipocalculo.equals("NORMAL")) {
            calcularValor();
        } else {
            calcularValorReverso();
        }
    }//GEN-LAST:event_txtRentabilidadeActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        try {
            SucataSenha sol = new SucataSenha();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado
            sol.setSize(400, 300);
            sol.setPosicao();
            sol.setRecebePalavra(this, "ENTRADA", "", this.sucataMovimento);

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
            if ((txtPesoSaldo.getDouble() > 0) && (txtPeso.getDouble() > 0)) {
                JOptionPane.showMessageDialog(null, "Info: Esse processo vai gerar um pedido para baixar saldo total de sucata  ",
                        "Atenção:", JOptionPane.INFORMATION_MESSAGE);
                if (ManipularRegistros.pesos(" Gerar pedido de baixa de sucata?")) {

                    try {
                        gravarSucata("PV");
                        if (optPedInd.isSelected()) {
                            WSEmailAtendimento wsEma = new WSEmailAtendimento();
                            wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
                            atualizar();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            if (this.executar.equals("PO")) { // pedido + ordem
                if ((txtPesoSaldo.getDouble() > 0) && (txtPeso.getDouble() > 0)) {
                    if (ManipularRegistros.pesos(" Gravar registro")) {
                        try {
                            txtPedido.setValue(0);
                            gravarSucata("PO");
                            if (optPedInd.isSelected()) {
                                WSEmailAtendimento wsEma = new WSEmailAtendimento();
                                wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
                                atualizar();
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
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
                    if ((txtPesoSaldo.getDouble() > 0.0) && (txtPeso.getDouble() > 0.0)) {
                        if (!txtPedido.getText().equals("0")) {
                            JOptionPane.showMessageDialog(null, " Atenção: O Sistema ira gerar uma ordem de compra sem vinvulo com o pedido de venda ",
                                    "Erro:", JOptionPane.INFORMATION_MESSAGE);
                        }
                        if (ManipularRegistros.pesos(" Gravar registro")) {
                            try {
                                gravarSucata("OC");
                                if (optOrd.isSelected()) {
                                    WSEmailAtendimento wsEma = new WSEmailAtendimento();
                                    wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
                                    atualizar();
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception ex) {
                                Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Erro: Informe o pedido ou o peso ",
                                "Erro:", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (this.executar.equals("NFV")) { // ordem

                if ((txtPesoSaldo.getDouble() > 0.0) && (txtPeso.getDouble() > 0.0)) {
                    try {
                        gravarSucata("NFV");
                        if (optNfv.isSelected()) {
                            //   WSEmailAtendimento wsEma = new WSEmailAtendimento();
                            //    wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
                            atualizar();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Erro: Informe o pedido ou o peso ",
                            "Erro:", JOptionPane.ERROR_MESSAGE);
                }

            }
        }
    }

    private void btnGravarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarPedidoActionPerformed
        try {
            gravarPedido();
        } catch (Exception ex) {
            Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGravarPedidoActionPerformed

    private void optPedIndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPedIndActionPerformed

        this.executar = "PO";
        txtPedido.setText("0");
        txtPesoPedido.setText("0");
        txtNota.setText("0");
        txtPesoSaida.setText("0");
        txtOrdemCompra.setText("");
        txtPesoOrdem.setValue("");
        txtNotaEntrada.setText("0");
        txtPesoEntrada.setText("0");

        btnGravarPedido.setEnabled(true);
        btnGravarManual.setEnabled(false);
        btnGravarOrdem.setEnabled(false);
        optPed.setEnabled(true);
        txtObservacao.setText("O Sistema ira gerar um Pedido de Produto e uma ordem de compra de sucata.");


    }//GEN-LAST:event_optPedIndActionPerformed

    private void optCreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optCreActionPerformed
        btnGravarPedido.setEnabled(false);
        btnGravarOrdem.setEnabled(false);
        btnGravarNota.setEnabled(false);
        btnGravarManual.setEnabled(true);
    }//GEN-LAST:event_optCreActionPerformed

    private void optDebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optDebActionPerformed
        btnGravarPedido.setEnabled(false);
        btnGravarOrdem.setEnabled(false);
        btnGravarNota.setEnabled(false);
        btnGravarManual.setEnabled(true);
    }//GEN-LAST:event_optDebActionPerformed

    private void btnRentabilidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRentabilidadeActionPerformed
        try {
            buscarRentabilidade();
        } catch (SQLException ex) {
            Logger.getLogger(SucatasManual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnRentabilidadeActionPerformed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        tipocalculo = "NORMAL";
        calcularValor();
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void btnSelecionarTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarTipoActionPerformed

        if (this.executar.equals("PO")) {
            limparTela();
        }
        DesabilitarCampo(false);
        if (txtTipoSucata.getSelectedItem().toString().equals("IND")) {
            DesabilitarCampoIndustrializacao(true);

        } else {
            DesabilitarCampoAutomobilistico(true);
        }
        btnSelecionarTipo.setEnabled(true);
    }//GEN-LAST:event_btnSelecionarTipoActionPerformed

    private void btnPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedidoActionPerformed
        if (this.newReg) {
            txtPedido.setValue(0);
            txtPedido.setEnabled(true);
        } else {

        }

    }//GEN-LAST:event_btnPedidoActionPerformed

    private void optNfvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optNfvActionPerformed
        btnGravarNota.setEnabled(true);
        optCre.setEnabled(false);
        optDeb.setEnabled(false);
        optPedInd.setEnabled(false);
        optPed.setEnabled(false);
        optOrd.setEnabled(false);
        this.executar = "NFV";
    }//GEN-LAST:event_optNfvActionPerformed

    private void optOrdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optOrdActionPerformed

        this.executar = "OC";
        btnGravarOrdem.setEnabled(true);
        txtPedido.setEnabled(false);
        btnGravarNota.setEnabled(false);
        btnGravarPedido.setEnabled(false);
        txtObservacao.setText("O Sistema ira gerar uma Ordem de Compra. \n"
                + "Atenção: Se existir uma ordem compra gerada para o pedido em  questão, a ordem sera subistituida. ");


    }//GEN-LAST:event_optOrdActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnPedido1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedido1ActionPerformed
        txtPeso.setEnabled(true);
        //  optOrd.setSelected(true);
        optOrd.setEnabled(true);
    }//GEN-LAST:event_btnPedido1ActionPerformed

    private void btnNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNotaActionPerformed
        txtNota.setEnabled(true);
        txtNota.requestFocus();
    }//GEN-LAST:event_btnNotaActionPerformed

    private void optPedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optPedActionPerformed

        this.executar = "PV";
        txtPedido.setText("0");
        btnGravarPedido.setEnabled(true);
        btnGravarOrdem.setEnabled(false);
        btnGravarNota.setEnabled(false);
        btnGravarManual.setEnabled(false);
        btnExcluir.setEnabled(false);
        String mesagem = txtObservacao.getText();
        mesagem += "\n ATENÇÃO:  Essa opção não irá gerar Ordem Compra";
        txtObservacao.setText(mesagem);
    }//GEN-LAST:event_optPedActionPerformed

    private void btnPedido2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedido2ActionPerformed
        txtNotaEntrada.setEnabled(true);
        txtNotaEntrada.requestFocus();
        txtPesoEntrada.setEnabled(true);
        txtPesoEntrada.setText("0");
    }//GEN-LAST:event_btnPedido2ActionPerformed

    private void btnNota1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNota1ActionPerformed
        txtOrdemCompra.setEnabled(true);
        txtOrdemCompra.requestFocus();
    }//GEN-LAST:event_btnNota1ActionPerformed

    private void txtNotaEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaEntradaActionPerformed
        txtPesoEntrada.requestFocus();
    }//GEN-LAST:event_txtNotaEntradaActionPerformed

    private void txtFilialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFilialMouseClicked
        //
    }//GEN-LAST:event_txtFilialMouseClicked

    private void txtFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialActionPerformed
        //
    }//GEN-LAST:event_txtFilialActionPerformed

    private void btnNotaFiscalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNotaFiscalActionPerformed
        btnGravarNota.setEnabled(false);
        optCre.setEnabled(false);
        optDeb.setEnabled(false);
        optPedInd.setEnabled(false);
        optPed.setEnabled(false);
        optOrd.setEnabled(false);
        txtPreco.setEnabled(true);
        txtPreco.requestFocus();
        txtPreco.setText("7.80");
        this.processo = "NOTAFISCAL";
    }//GEN-LAST:event_btnNotaFiscalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnCalcularPeso;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnGravarManual;
    private javax.swing.JButton btnGravarNota;
    private javax.swing.JButton btnGravarOrdem;
    private javax.swing.JButton btnGravarPedido;
    private javax.swing.JButton btnNota;
    private javax.swing.JButton btnNota1;
    private javax.swing.JButton btnNotaFiscal;
    private javax.swing.JButton btnPedido;
    private javax.swing.JButton btnPedido1;
    private javax.swing.JButton btnPedido2;
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
    private javax.swing.JRadioButton optPedInd;
    private org.openswing.swing.client.TextControl txtCliente;
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
    private org.openswing.swing.client.NumericControl txtPesoSaldo;
    private org.openswing.swing.client.NumericControl txtPesoSucata;
    private org.openswing.swing.client.NumericControl txtPreco;
    private javax.swing.JComboBox<String> txtProduto;
    private org.openswing.swing.client.NumericControl txtRentabilidade;
    private org.openswing.swing.client.TextControl txtSequencia;
    private javax.swing.JComboBox<String> txtSucata;
    private javax.swing.JComboBox<String> txtTipoSucata;
    private org.openswing.swing.client.NumericControl txtValorOrdem;
    // End of variables declaration//GEN-END:variables
}
