/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.PedidoHub;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataAnalises;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.PedidoHubDAO;
import br.com.sgi.dao.SucataAnalisesDAO;
import br.com.sgi.dao.SucataDAO;
import br.com.sgi.dao.SucataEcoDao;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.FormatarPeso;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSEmailAtendimento;
import br.com.sgi.ws.WsOrdemDeCompra;

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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;

/**
 *
 * @author jairosilva
 */
public final class SucataTriagemMinuta extends InternalFrame {

    private List<SucataAnalises> listSucataAnalises = new ArrayList<SucataAnalises>();
    private SucataAnalisesDAO sucataanalisesDAO;
    private SucataAnalises sucataanalises;
    private String datIni;
    private String datFim;
    private UtilDatas utilDatas;
    private Cliente cliente;
    private String codigoEmpresa;
    private String codigoFilial;

    private SucataMovimentoDAO sucataMovimentoDAO;
    private SucataMovimento sucataMovimento;

    private Sucata sucata;

    private SucataDAO sucataDAO;

    private String pesquisa_por;
    private String pesquisa;
    private double pesoPedido = 0;
    private double pesoNota = 0;

    private Transportadora transportadora;

    public SucataTriagemMinuta() {
        try {
            initComponents();
            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

            if (sucataanalisesDAO == null) {
                sucataanalisesDAO = new SucataAnalisesDAO();
            }

            this.setSize(800, 500);
            txtDatIni.setDate(utilDatas.retornaDataIniMes(new Date()));
            txtDatFim.setDate(utilDatas.retornaDataFim(new Date()));

            pegarDataDigitada();
            getUsuarioLogado();
            iniciarBarraTabela("DATA", " and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void getNota(String PESQUISA_POR, String PESQUISA) throws SQLException {
        if (sucataanalisesDAO == null) {
            sucataanalisesDAO = new SucataAnalisesDAO();
        }
        sucataanalises = new SucataAnalises();
        sucataanalises = sucataanalisesDAO.getSucataAnalises(PESQUISA_POR, PESQUISA);

        if (sucataanalises != null) {
            if (sucataanalises.getNota() > 0) {

            }
        }
    }

    private double pesoSelecionado = 0;
    private double quantidadeSelecionado = 0;

    public void selecionarNotaRange() throws SQLException, Exception {
        List<MinutaPedido> listminutaPedido = new ArrayList<MinutaPedido>();
        Minuta minuta = new Minuta();
        String empresa = "";
        String filial = "";
        String nota = "";
        String serie = "";
        String nome = "";
        String estado = "";
        String cidade = "";
        int notaentrada = 0;
        if (jTableEnvio.getRowCount() > 0) {

            for (int i = 0; i < jTableEnvio.getRowCount(); i++) {
                notaentrada = Integer.valueOf(jTableEnvio.getValueAt(i, 10).toString());
                if (notaentrada == 0) {
                    if ((Boolean) jTableEnvio.getValueAt(i, 14)) {

                        if (jTableEnvio.getValueAt(i, 15).equals("S")) {

                            nota = (jTableEnvio.getValueAt(i, 1).toString());
                            estado = (jTableEnvio.getValueAt(i, 13).toString());
                            cidade = (jTableEnvio.getValueAt(i, 13).toString());

                            nome = (jTableEnvio.getValueAt(i, 17).toString());
                            filial = (jTableEnvio.getValueAt(i, 18).toString());
                            empresa = (jTableEnvio.getValueAt(i, 26).toString());
                            serie = (jTableEnvio.getValueAt(i, 27).toString());
                            pesoSelecionado += Double.valueOf(jTableEnvio.getValueAt(i, 2).toString());
                            quantidadeSelecionado += 0;
                            if (!nota.isEmpty()) {
                                getNota("", " and E140NFV.CODEMP = '" + empresa + "' and E140NFV.CODFIL = '" + filial + "' and E140NFV.NUMNFV = '" + nota + "' and E140NFV.CODSNF = '" + serie + "'");

                                if (sucataanalises != null) {
                                    if (sucataanalises.getNota() > 0) {
                                        MinutaPedido minutaPedido = new MinutaPedido();
                                        minutaPedido.setCadMinuta(minuta);
                                        minutaPedido.setUsu_codcli(sucataanalises.getCliente());
                                        minutaPedido.setUsu_codemp(sucataanalises.getEmpresa());
                                        minutaPedido.setUsu_codfil(sucataanalises.getFilial());
                                        minutaPedido.setUsu_codlan(0);
                                        minutaPedido.setUsu_codori(sucataanalises.getLinha());
                                        minutaPedido.setUsu_codpes(0);
                                        minutaPedido.setUsu_codsnf(sucataanalises.getSerienota());
                                        minutaPedido.setUsu_codtpr("");
                                        minutaPedido.setUsu_datemi(new Date());
                                        minutaPedido.setEmissaoS(this.utilDatas.converterDateToStr(sucataanalises.getEmissao_nota()));
                                        minutaPedido.setUsu_datlib(null);
                                        minutaPedido.setUsu_lansuc(Integer.valueOf(jTableEnvio.getValueAt(i, 19).toString()));
                                        minutaPedido.setUsu_numnfv(sucataanalises.getNota());
                                        minutaPedido.setUsu_numped(sucataanalises.getPedido_nota());
                                        minutaPedido.setUsu_codsnf(sucataanalises.getSerienota());
                                        minutaPedido.setUsu_obsmin("GERANDO MINUTA NOTA " + sucataanalises.getNota() + " SERIE " + sucataanalises.getSerienota());
                                        minutaPedido.setUsu_pesbal(0.0);
                                        minutaPedido.setUsu_pesnfv(sucataanalises.getPeso_bruto_nota());
                                        minutaPedido.setUsu_pesped(sucataanalises.getPeso_bruto_nota());
                                        minutaPedido.setUsu_pesrec(0.0);
                                        minutaPedido.setUsu_pessuc(sucataanalises.getPeso_bruto_nota());
                                        minutaPedido.setUsu_qtdfat(sucataanalises.getQuantidade());
                                        minutaPedido.setUsu_qtdped(sucataanalises.getQuantidade());
                                        quantidadeSelecionado += sucataanalises.getQuantidade();
                                        minutaPedido.setUsu_qtdvol(0.0);
                                        minutaPedido.setUsu_seqite(0);
                                        minutaPedido.setUsu_sitmin("ANDAMENTO");
                                        minutaPedido.setUsu_tnspro(sucataanalises.getPedido_transacao());
                                        minutaPedido.setCadCliente(sucataanalises.getCadCliente());

                                        listminutaPedido.add(minutaPedido);

                                    }
                                }
                            }

                        }
                    }
                }
            }

            minuta.setUsu_pesfat(pesoSelecionado);
            minuta.setUsu_qtdfat(quantidadeSelecionado);
            if (transportadora != null) {
                if (transportadora.getCodigoTransportadora() > 0) {
                    minuta.setCadTransportadora(transportadora);
                }
            }

            if (listminutaPedido != null) {
                if (listminutaPedido.size() > 0) {
                    novoRegistroMinuta("GERAR", nota, minuta, listminutaPedido);
                }
            }
        }
    }

    private void novoRegistroMinuta(String PROCESSO, String selecionar, Minuta minuta, List<MinutaPedido> listminutaPedido) throws PropertyVetoException, Exception {
//        frmMinutasGerar sol = new frmMinutasGerar();
//        MDIFrame.add(sol, true);
//        sol.setPosicao();
//        sol.setMaximum(true); // executa maximizado 
//        sol.setRecebePalavraNota(this, PROCESSO, selecionar, minuta, listminutaPedido);
    }

    public void iniciarBarraTabela(String pesquisa_por, String pesquisa) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Filtrando processo ");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getListarContaCorrente(pesquisa_por, pesquisa);
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                barra.setString("Filtro carregado");
            }
        };
        worker.execute();
    }

    private String linha;

    public void getListarContaCorrente(String pesquisa_por, String pesquisa) throws SQLException, Exception {
        pesquisa += " and e140nfv.codfil = 11";
        if (linha.equals("AUT")) {
            listSucataAnalises = sucataanalisesDAO.getSucataAnalisesCd("", pesquisa);
        }
        if (linha.equals("MOT")) {
            listSucataAnalises = sucataanalisesDAO.getSucataAnalisesCd("", pesquisa);
        }

        if (listSucataAnalises != null) {
            carregarTabelaContaCorrente(false);
        }
    }

    public void iniciarBarraTabelaGeral(String pesquisa_por, String pesquisa) throws SQLException, Exception {
        barra.setVisible(true);
        barra.setIndeterminate(true);
        barra.setStringPainted(true);
        barra.setString("Filtrando processo ");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                getListarContaCorrenteGeral(pesquisa_por, pesquisa);
                return null;
            }

            @Override
            protected void done() {
                barra.setIndeterminate(false);
                barra.setString("Filtro carregado");
            }
        };
        worker.execute();
    }

    public void getListarContaCorrenteGeral(String pesquisa_por, String pesquisa) throws SQLException, Exception {

        listSucataAnalises = sucataanalisesDAO.getSucataAnalisesGeral("", pesquisa);
        if (listSucataAnalises != null) {
            carregarTabelaContaCorrente(false);
        }
    }

    public void carregarTabelaContaCorrente(boolean selecionar) throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableEnvio.getModel();
        modeloCarga.setNumRows(0);

        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon IndIcon = getImage("/images/bateriaindu.png");
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuiIcon = getImage("/images/sitRuim.png");

        double peso_nota = 0;
        double peso_sucata = 0;
        double peso_ordem = 0;
        int erros_suc = 0;
        int erros_ocp = 0;
        int cont = 0;

        for (SucataAnalises suc : listSucataAnalises) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableEnvio.getColumnModel();
            SucataTriagemMinuta.JTableRenderer renderers = new SucataTriagemMinuta.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            columnModel.getColumn(7).setCellRenderer(renderers);
            columnModel.getColumn(9).setCellRenderer(renderers);
            columnModel.getColumn(12).setCellRenderer(renderers);
            linha[0] = AutIcon;
            if (suc.getLinha().equals("MOTO")) {
                linha[0] = MotIcon;
            }

            linha[1] = suc.getNota();
            linha[2] = suc.getPeso_bruto_nota();

            linha[3] = suc.getPedido_nota();
            linha[4] = suc.getPeso_ordem_compra();

            linha[5] = suc.getOrdem_compra();
            linha[6] = suc.getPeso_ordem_compra();
            linha[7] = BomIcon;
            if ((suc.getOrdem_compra() == 0) && (!suc.getPedido_transacao().equals("902HB"))) {
                linha[7] = RuiIcon;
                erros_ocp++;
            }

            linha[8] = suc.getPeso_sucata();
            linha[9] = BomIcon;
            if (suc.getLancamento() == 0) {
                linha[9] = RuiIcon;
                erros_suc++;
            }

            linha[10] = suc.getNota_entrada();
            linha[11] = suc.getPeso_entrada();
            linha[12] = BomIcon;
            if (suc.getNota_entrada() == 0) {
                linha[12] = RuiIcon;
            }
            linha[13] = suc.getEstado();
            linha[14] = false;
            linha[15] = "S";
            if (suc.getNota_entrada() > 0) {
                linha[15] = "REGISTRO NFC";
            } else {
                if (suc.getLiberadoentrega().equals("M")) {
                    linha[15] = "MINUTA GERADA";

                }
            }

            linha[16] = suc.getCliente();
            linha[17] = suc.getNome();
            linha[18] = suc.getFilial();

            linha[19] = suc.getLancamento();
            linha[20] = suc.getSequencia();

            if (suc.getEmissao_pedido() != null) {
                linha[21] = this.utilDatas.converterDateToStr(suc.getEmissao_pedido());
            }
            if (suc.getEmissao_nota() != null) {
                linha[22] = this.utilDatas.converterDateToStr(suc.getEmissao_nota());
            }
            if (suc.getEmissao_sucata() != null) {
                linha[23] = this.utilDatas.converterDateToStr(suc.getEmissao_sucata());

            }

            linha[24] = suc.getTabela();
            linha[25] = "Gerar: " + suc.getGerar_sucata() + " - " + suc.getPedido_transacao() + " - " + suc.getDescricao_transacao();
            linha[26] = suc.getEmpresa();
            linha[27] = suc.getSerienota();
            linha[28] = suc.getCadCliente().getCidade();
            linha[29] = suc.getPedido_transacao();
            peso_nota += suc.getPeso_bruto_nota();
            peso_ordem += suc.getPeso_ordem_compra();
            peso_sucata += suc.getPeso_sucata();
            cont++;

            modeloCarga.addRow(linha);

        }

        formatarCampoPeso(peso_nota, "N");
        formatarCampoPeso(peso_ordem, "O");
        formatarCampoPeso(peso_sucata, "S");
        formatarCampoPeso(erros_ocp + erros_suc, "E");

    }

    private void formatarCampoPeso(double peso, String tipo) {
        String pesoS = FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM);
        if (tipo.equals("S")) {
            lblPesoSucatas.setText(pesoS);
        }
        if (tipo.equals("N")) {
            lblPesoNota.setText(pesoS);
        }
        if (tipo.equals("O")) {
            lblPesoOrdem.setText(pesoS);

        }

        if (tipo.equals("E")) {
            lblErros.setText(pesoS);
        }

    }

    private Filial filial;

    private void gravarSucata(String gerarpedido) throws SQLException, Exception {
        Sucata sucata = new Sucata();
        SucataDAO sucataDAO = new SucataDAO();

        if (txtLancamento.getDouble() == 0.0) {
            if (sucataMovimentoDAO == null) {
                sucataMovimentoDAO = new SucataMovimentoDAO();
                sucataMovimento = new SucataMovimento();
            }
            sucata.setCodigolancamento(sucataDAO.proxCodCad());
            sucataMovimento.setCodigolancamento(sucata.getCodigolancamento());
            if (sucataMovimento.getCodigolancamento() == 0) {
                sucataMovimento.setCodigolancamento(1);
            }
            sucataMovimento.setSequencia(1);
            sucataMovimento.setEmpresa(1);
            sucataMovimento.setFilial(Integer.valueOf(txtFilial.getText()));
            sucataMovimento.setFilialsucata(sucataMovimento.getFilial());

            sucataMovimento.setPedido(Integer.valueOf(txtPedido.getText()));
            sucataMovimento.setCliente(Integer.valueOf(txtCliente.getText()));

            Cliente cli = new Cliente();
            cli.setCodigo(sucataMovimento.getCliente());
            cli.setNome(txtNomeCliente.getText());
            cli.setCidade(txtCidade.getText());
            cli.setEstado(txtEstado.getText());
            sucataMovimento.setCadCliente(cli);

            sucataMovimento.setUsuario(0);
            sucataMovimento.setDatageracao(new Date());
            sucataMovimento.setDatamovimento(new Date());
            sucataMovimento.setHorageracao("0");
            sucataMovimento.setHoramovimento("0");
            sucataMovimento.setEnviaremail("N");
            sucataMovimento.setEmail("");
            sucataMovimento.setCodigopeso(0);
            sucataMovimento.setCodigominuta(0);
            sucataMovimento.setSituacao("AUTOMATICO");
            sucataMovimento.setGerarordem("N");
            sucataMovimento.setDebitocredito("3 - DEBITO");

            sucataMovimento.setPesosucata(0.0);
            sucataMovimento.setOrdemcompra(0);
            sucataMovimento.setPesoordemcompra(0.0);
            sucataMovimento.setProduto("AUTO");
            sucataMovimento.setSucata("1003");
            sucataMovimento.setAutomoto(this.linha);

            sucataMovimento.setSerie(txtSerie.getText());
            sucataMovimento.setTransacao(txtTransacao.getText());

            sucataMovimento.setPesoajustado(0.0);
            sucataMovimento.setPesomovimento(0.0);

            sucataMovimento.setPedido(Integer.valueOf(txtPedido.getText()));
            if (sucataMovimento.getPedido() > 0) {
                sucataMovimento.setPesopedido(pesoNota);
            } else {
                sucataMovimento.setPesopedido(0.0);
            }

            sucataMovimento.setQuantidade(1.0);
            sucataMovimento.setNotasaida(Integer.valueOf(txtNotaSaida.getText()));
            sucataMovimento.setPesofaturado(pesoNota);
            sucataMovimento.setObservacaomovimento("REGISTRO DE SUCATA GERADO PARA ATENDER A NOTA " + sucataMovimento.getNotasaida());
            sucataMovimento.setObservacaoacerto("REISTRO DE SUCATA DO CD " + txtFilial.getText() + " NOTA " + sucataMovimento.getNotasaida() + "  PEDIDO  " + txtPedido.getText());

            if (!this.sucataMovimentoDAO.inserir(sucataMovimento)) {

            } else {
                this.sucataDAO = new SucataDAO();
                this.sucata = new Sucata();
                this.sucata.setCliente(sucataMovimento.getCliente());
                this.sucata.setEmpresa(sucataMovimento.getEmpresa());
                this.sucata.setFilial(sucataMovimento.getFilial());
                this.sucata.setCodigolancamento(sucataMovimento.getCodigolancamento());
                this.sucata.setPedido(Integer.valueOf(txtPedido.getText()));
                this.sucata.setAutomoto(sucataMovimento.getAutomoto());
                this.sucata.setDebitocredito(sucataMovimento.getDebitocredito());

                this.sucata.setPercentualrendimento(0.0);
                this.sucata.setPesosucata(sucataMovimento.getPesosucata());
                this.sucata.setUsuario(sucataMovimento.getUsuario());
                this.sucata.setObservacaomovimento(sucataMovimento.getObservacaomovimento());
                this.sucata.setObservacaoacerto(sucataMovimento.getObservacaoacerto());
                this.sucata.setSituacao(sucataMovimento.getSituacao());
                this.sucata.setProduto(sucataMovimento.getProduto());
                this.sucata.setSucata(sucataMovimento.getSucata());

                this.sucata.setDatageracao(new Date());
                this.sucata.setDatamovimento(new Date());

                this.sucata.setPesofaturado(pesoNota);
                this.sucata.setPesopedido(this.sucataMovimento.getPesopedido());
                this.sucata.setGerarordem("N");
                this.sucata.setEnviaremail("N");
                this.sucata.setTransacao("90159");

                if (!this.sucataDAO.inserir(this.sucata)) {

                } else {
                    gravarApp(sucataMovimento, 1, 1);
                    iniciarBarraTabela("DATA", "   and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");
                    // gerarOrdemCompra("11");
                }

            }

        }

    }

    public void gravarApp(SucataMovimento suc, int qtdreg, int contador) throws SQLException {
        PedidoHub ped = new PedidoHub();
        PedidoHubDAO pedDao = new PedidoHubDAO();
        ped.setEmpresa(suc.getEmpresa());
        ped.setFilial(suc.getFilial());
        ped.setPedido(suc.getPedido());
        ped.setCliente(suc.getCliente());
        ped.setClientenome(suc.getCadCliente().getNome());
        ped.setCidade(suc.getCadCliente().getCidade());
        ped.setEstado(suc.getCadCliente().getEstado());
        ped.setCodigoacesso(52);
        ped.setConta_id(3);
        ped.setSucata_id(suc.getCodigolancamento());

        ped.setDataenvio(new Date());
        ped.setDataretorno(null);
        ped.setDataseparacao(new Date());

        ped.setId(0);
        ped.setNotafiscal(suc.getNotasaida());

        ped.setPesorecebido(0.0);
        ped.setPesosaldo(0.0);
        ped.setPesosucata(suc.getPesofaturado());

        ped.setQuantidade(suc.getQuantidade());

        ped.setOperacao("PEDIDO ENVIADO PARA ENTREGA");
        ped.setSituacao("FATURADO");
        ped.setSituacaoserie("SEPARADO");
        ped.setSerienota(suc.getSerie());
        ped.setIntegrar("N");
        ped.setIntegrarnota("N");

        ped.setSerienota(suc.getSerie());
        ped.setTransacao(suc.getTransacao());

        ped.setId(pedDao.proxCodCadPed());
        if (!pedDao.inserir(ped, qtdreg, contador)) {
        } else {

        }

    }

    private void gerarOrdemCompra(String filial) throws SQLException {
        String empresa = "1";
        //String filial = "1";
        String fornecedor = String.valueOf(sucataMovimento.getCliente());
        Double peso = pesoNota;
        String re = "";
        String sNumOcp = "";
        String resultado = "";
        String erro = "";
        SucataEcoParametros sucataEcoParametros = new SucataEcoParametros();
        SucataEcoDao sucataEcoDao = new SucataEcoDao();
        sucataEcoParametros = sucataEcoDao.getSucataEcoParamentros(filial, empresa);
        if (sucataEcoParametros != null) {
            if (sucataEcoParametros.getEmpresa() > 0) {
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

                    txtObservacao.setText(resultado);

                    if (resultado.equals("Processado com sucesso.")) {

                        txtOrdemCompra.setText(sNumOcp.trim());
                        //txtObservacao.setText("ORDEM DE COMPRA GERADA COM SUCESSO: " + sNumOcp);

                    } else if (resultado.equals("Ocorreram erros.")) {
                        txtObservacao.setText(resultado);
                    }

                    if (sucataMovimento.getCodigolancamento() > 0) {
                        sucataMovimento.setObservacaomovimento(txtObservacao.getText());
                        sucataMovimento.setOrdemcompra(Integer.valueOf(sNumOcp.trim()));
                        sucataMovimento.setPesoordemcompra(sucataMovimento.getPesofaturado());
                        sucataMovimento.setGerarordem("N");
                        sucataMovimento.getGerarordem();

                        if (sucataMovimentoDAO.atualizarOrdemCompra(sucataMovimento)) {

                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(SucataEcoLancamento.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (Exception ex) {
                    Logger.getLogger(SucataEcoLancamento.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private void gerarProcessoERP() throws Exception {
        WSEmailAtendimento wsEma = new WSEmailAtendimento();
        wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
    }

    private void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableEnvio.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(3).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(8).setCellRenderer(direita);
        //    jTableEnvio.getColumnModel().getColumn(9).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(10).setCellRenderer(direita);
        //  jTableEnvio.getColumnModel().getColumn(11).setCellRenderer(direita);
        jTableEnvio.getColumnModel().getColumn(12).setCellRenderer(direita);
        //   jTableEnvio.getColumnModel().getColumn(13).setCellRenderer(direita);
        // jTableEnvio.getColumnModel().getColumn(14).setCellRenderer(centralizado);
        //  jTableEnvio.getColumnModel().getColumn(16).setCellRenderer(direita);
        //  jTableEnvio.getColumnModel().getColumn(17).setCellRenderer(direita);
        jTableEnvio.setRowHeight(40);
        jTableEnvio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTableEnvio.setAutoCreateRowSorter(true);
        jTableEnvio.setAutoResizeMode(0);

    }

    public void retornarCliente(String PESQUISA_POR, String PESQUISA, Cliente cliente) throws Exception {
        this.cliente = new Cliente();
        this.cliente = cliente;
        txtCliente.setText(cliente.getCodigo().toString());
        txtNomeCliente.setText(cliente.getNome());
        //   txtGrupo.setText(cliente.getGrupocodigo() + " - " + cliente.getGruponome());
        getCliente();
        pegarDataDigitada();
        getListarContaCorrente("DATA", " and usu_datger >= '" + datIni + "\n"
                + "' and usu_datger <='" + datFim + "'\n"
                + " and usu_codcli = " + cliente.getCodigo() + "");

    }

    private void getCliente() throws SQLException, Exception {
        ClienteDAO dao = new ClienteDAO();
        this.cliente = dao.getClienteSucata("CLI", " and codcli = " + txtCliente.getText());
        if (cliente != null) {
            if (cliente.getCodigo() > 0) {
                txtNomeCliente.setText(cliente.getNome());
                if ((cliente.getGrupocodigo() == null) || (cliente.getGrupocodigo().equals("0"))) {
                    cliente.setGruponome("Não informado");
                    cliente.setGrupocodigo("0");

                }
                //  txtGrupo.setText(cliente.getGrupocodigo() + " - " + cliente.getGruponome());
                getListarContaCorrente("", "and usu_codcli = " + txtCliente.getText());

            }
        }

    }

    void retornarSucata() throws ParseException, Exception {
        try {
            getListarContaCorrente("", " and usu_datger >= '" + datIni + "'"
                    + " and usu_datger <= '" + datFim + "'"
                    + "  and usu_codcli = " + txtCliente.getText());
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }

    private void pegarDataDigitada() throws ParseException {
        datIni = this.utilDatas.converterDateToStr(txtDatIni.getDate());
        datFim = this.utilDatas.converterDateToStr(txtDatFim.getDate());
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

    private Usuario usuario;

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        usuario.setId(Menu.getUsuario().getId());

        return usuario;

    }

    void retornarMinuta() throws Exception {
        iniciarBarraTabela("DATA", " and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        lblPesoSucatas = new javax.swing.JLabel();
        lblPesoOrdem = new javax.swing.JLabel();
        lblPesoNota = new javax.swing.JLabel();
        lblErros = new javax.swing.JLabel();
        btnGerar = new javax.swing.JButton();
        btnBaterias1 = new javax.swing.JButton();
        btnBaterias2 = new javax.swing.JButton();
        jTabSucata = new javax.swing.JTabbedPane();
        jPanelMovimento = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableEnvio = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        btnHoras1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtLancamento = new org.openswing.swing.client.NumericControl();
        jLabel5 = new javax.swing.JLabel();
        txtSequencia = new org.openswing.swing.client.NumericControl();
        jLabel6 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        txtNotaSaida = new org.openswing.swing.client.TextControl();
        txtCliente = new org.openswing.swing.client.TextControl();
        txtNomeCliente = new org.openswing.swing.client.TextControl();
        jLabel11 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnPesquisarGrupo1 = new javax.swing.JButton();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        jLabel2 = new javax.swing.JLabel();
        txtDatIni = new org.openswing.swing.client.DateControl();
        txtDatFim = new org.openswing.swing.client.DateControl();
        btnPesPer = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtCidade = new org.openswing.swing.client.TextControl();
        txtEstado = new org.openswing.swing.client.TextControl();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtSerie = new org.openswing.swing.client.TextControl();
        txtFilial = new org.openswing.swing.client.TextControl();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtTransacao = new org.openswing.swing.client.TextControl();
        jLabel14 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservacao = new javax.swing.JTextArea();
        barra = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Triagem Sucata");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblPesoSucatas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblPesoSucatas.setForeground(new java.awt.Color(51, 102, 255));
        lblPesoSucatas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPesoSucatas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_retorno.png"))); // NOI18N
        lblPesoSucatas.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso Sucata"));
        lblPesoSucatas.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblPesoSucatas.setOpaque(true);
        lblPesoSucatas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPesoSucatasMouseClicked(evt);
            }
        });

        lblPesoOrdem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblPesoOrdem.setForeground(new java.awt.Color(0, 102, 0));
        lblPesoOrdem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPesoOrdem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        lblPesoOrdem.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso Ordem"));
        lblPesoOrdem.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblPesoOrdem.setOpaque(true);

        lblPesoNota.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblPesoNota.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPesoNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/veiculo_envio.png"))); // NOI18N
        lblPesoNota.setBorder(javax.swing.BorderFactory.createTitledBorder("Peso Nota"));
        lblPesoNota.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblPesoNota.setOpaque(true);
        lblPesoNota.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPesoNotaMouseClicked(evt);
            }
        });

        lblErros.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblErros.setForeground(new java.awt.Color(0, 102, 0));
        lblErros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblErros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sitRuim.png"))); // NOI18N
        lblErros.setBorder(javax.swing.BorderFactory.createTitledBorder("Erros"));
        lblErros.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblErros.setOpaque(true);

        btnGerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder.png"))); // NOI18N
        btnGerar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnGerar.setEnabled(false);
        btnGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarActionPerformed(evt);
            }
        });

        btnBaterias1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/carros.png"))); // NOI18N
        btnBaterias1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnBaterias1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaterias1ActionPerformed(evt);
            }
        });

        btnBaterias2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/moto_erbs.png"))); // NOI18N
        btnBaterias2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnBaterias2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaterias2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lblPesoNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPesoSucatas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPesoOrdem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(lblErros, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnBaterias1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBaterias2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnBaterias1, btnBaterias2, btnGerar});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPesoSucatas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPesoNota, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblErros, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPesoOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
            .addComponent(btnBaterias1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(btnGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnBaterias2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGerar, lblErros, lblPesoNota, lblPesoOrdem, lblPesoSucatas});

        jPanelMovimento.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jTableEnvio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Nota", "Peso", "Pedido", "Peso", "Ordem compra", "Peso", "#", "Peso Sucata", "#", "Nota Entrada", "Peso Entrada", "#", "Estado", "Selecionar", "#", "Cliente", "Razão Social", "Filial", "Movimento", "Sequencia", "Data Pedido", "Data Nota", "Data Sucata", "Tabela", "Descrição Faturamento", "Empresa", "Serie", "Cidade", "Transacao"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, true, false, true, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableEnvio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableEnvioMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTableEnvioMouseEntered(evt);
            }
        });
        jScrollPane4.setViewportView(jTableEnvio);
        if (jTableEnvio.getColumnModel().getColumnCount() > 0) {
            jTableEnvio.getColumnModel().getColumn(0).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(0).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(1).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(2).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(2).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(3).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(4).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(4).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(5).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(6).setMinWidth(80);
            jTableEnvio.getColumnModel().getColumn(6).setPreferredWidth(80);
            jTableEnvio.getColumnModel().getColumn(6).setMaxWidth(80);
            jTableEnvio.getColumnModel().getColumn(7).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(7).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(7).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(8).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(9).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(9).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(9).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(10).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(11).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(11).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(11).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(12).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(12).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(12).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(13).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(13).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(13).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(14).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(14).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(14).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(15).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(15).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(15).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(16).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(16).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(16).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(17).setMinWidth(300);
            jTableEnvio.getColumnModel().getColumn(17).setPreferredWidth(300);
            jTableEnvio.getColumnModel().getColumn(17).setMaxWidth(300);
            jTableEnvio.getColumnModel().getColumn(18).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(18).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(18).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(19).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(19).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(19).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(20).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(20).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(20).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(21).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(21).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(21).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(22).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(22).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(22).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(23).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(23).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(23).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(24).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(24).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(24).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(25).setMinWidth(200);
            jTableEnvio.getColumnModel().getColumn(25).setPreferredWidth(200);
            jTableEnvio.getColumnModel().getColumn(25).setMaxWidth(200);
            jTableEnvio.getColumnModel().getColumn(26).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(26).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(26).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(27).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(27).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(27).setMaxWidth(100);
            jTableEnvio.getColumnModel().getColumn(28).setMinWidth(200);
            jTableEnvio.getColumnModel().getColumn(28).setPreferredWidth(200);
            jTableEnvio.getColumnModel().getColumn(28).setMaxWidth(200);
            jTableEnvio.getColumnModel().getColumn(29).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(29).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(29).setMaxWidth(100);
        }

        jLabel7.setText("Pedido");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btnHoras1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leilao.png"))); // NOI18N
        btnHoras1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoras1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Nota Saída");

        txtLancamento.setEnabled(false);
        txtLancamento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel5.setText("Lancamento");

        txtSequencia.setEnabled(false);
        txtSequencia.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel6.setText("Seq");

        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtNotaSaida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        txtNomeCliente.setEnabled(false);
        txtNomeCliente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel11.setText("Nome");

        jLabel1.setText("ID");

        btnPesquisarGrupo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesquisarGrupo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarGrupo1ActionPerformed(evt);
            }
        });

        txtOrdemCompra.setEnabled(false);
        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtOrdemCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOrdemCompraActionPerformed(evt);
            }
        });

        jLabel2.setText("Ordem Compra");

        txtDatIni.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtDatFim.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnPesPer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesPer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesPerActionPerformed(evt);
            }
        });

        jLabel3.setText("Início");

        jLabel8.setText("Fim");

        txtCidade.setEnabled(false);
        txtCidade.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtEstado.setEnabled(false);
        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel9.setText("Cidade");

        jLabel10.setText("Estado");

        txtSerie.setEnabled(false);
        txtSerie.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtFilial.setEnabled(false);
        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel12.setText("Filial");

        jLabel13.setText("Serie");

        txtTransacao.setEnabled(false);
        txtTransacao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel14.setText("Transacao");

        javax.swing.GroupLayout jPanelMovimentoLayout = new javax.swing.GroupLayout(jPanelMovimento);
        jPanelMovimento.setLayout(jPanelMovimentoLayout);
        jPanelMovimentoLayout.setHorizontalGroup(
            jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtDatFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesPer, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesquisarGrupo1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(txtNomeCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtCidade, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(49, 49, 49)))
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(4, 4, 4))
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(5, 5, 5))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(83, 83, 83)))
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtNotaSaida, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHoras1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLancamento, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanelMovimentoLayout.setVerticalGroup(
            jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel8)
                    .addComponent(jLabel1)
                    .addComponent(jLabel11)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDatFim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnPesPer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnPesquisarGrupo1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNomeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnHoras1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMovimentoLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel4)
                                                .addComponent(jLabel5)
                                                .addComponent(jLabel6)
                                                .addComponent(jLabel12)
                                                .addComponent(jLabel13)
                                                .addComponent(jLabel14)))
                                        .addGap(2, 2, 2))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMovimentoLayout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNotaSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanelMovimentoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCliente, txtNomeCliente});

        jPanelMovimentoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnHoras1, btnPesquisarGrupo1, jButton3, txtNotaSaida, txtPedido});

        jPanelMovimentoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnPesPer, txtDatFim});

        jTabSucata.addTab("Nota", jPanelMovimento);

        txtObservacao.setColumns(20);
        txtObservacao.setRows(5);
        jScrollPane1.setViewportView(txtObservacao);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1028, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
        );

        jTabSucata.addTab("Info", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabSucata)
            .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jTabSucata)
                .addGap(2, 2, 2)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPesPerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesPerActionPerformed
        try {
            pegarDataDigitada();
            iniciarBarraTabela("DATA", " and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnPesPerActionPerformed

    private void btnHoras1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoras1ActionPerformed
        try {
            iniciarBarraTabela("DATA", " and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "' and e140nfv.numnfv = " + txtNotaSaida.getText());

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnHoras1ActionPerformed

    private String tipoSucata;

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            iniciarBarraTabela("DATA", " and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "' and E140IPV.numped = " + txtPedido.getText());

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_jButton3ActionPerformed


    private void jTableEnvioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEnvioMouseClicked
        try {

            int linhaSelSit = jTableEnvio.getSelectedRow();
            int colunaSelSit = jTableEnvio.getSelectedColumn();
            txtNotaSaida.setText(jTableEnvio.getValueAt(linhaSelSit, 1).toString());
            pesoNota = Double.valueOf(jTableEnvio.getValueAt(linhaSelSit, 2).toString());
            pesoPedido = pesoNota;
            txtPedido.setText(jTableEnvio.getValueAt(linhaSelSit, 3).toString());
            txtOrdemCompra.setText(jTableEnvio.getValueAt(linhaSelSit, 5).toString());

            txtEstado.setText(jTableEnvio.getValueAt(linhaSelSit, 13).toString());

            txtCliente.setText(jTableEnvio.getValueAt(linhaSelSit, 16).toString());
            txtFilial.setText(jTableEnvio.getValueAt(linhaSelSit, 18).toString());
            txtNomeCliente.setText(jTableEnvio.getValueAt(linhaSelSit, 17).toString());

            txtLancamento.setText(jTableEnvio.getValueAt(linhaSelSit, 19).toString());
            txtSequencia.setText(jTableEnvio.getValueAt(linhaSelSit, 20).toString());
            txtSerie.setText(jTableEnvio.getValueAt(linhaSelSit, 27).toString());
            txtCidade.setText(jTableEnvio.getValueAt(linhaSelSit, 28).toString());
            txtTransacao.setText(jTableEnvio.getValueAt(linhaSelSit, 29).toString());

            btnGerar.setEnabled(false);
            if (txtLancamento.getText().equals("0.0")) {
                btnGerar.setEnabled(true);

            }

        } catch (Exception ex) {
            Logger.getLogger(SucatasManutencao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTableEnvioMouseClicked

    private void lblPesoSucatasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPesoSucatasMouseClicked
        if (!txtCliente.getText().isEmpty()) {
            try {

                getListarContaCorrente("", "  and usu_codcli = " + txtCliente.getText() + "\n"
                        + " and usu_debcre ='4 - CREDITO' ");

            } catch (ParseException ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(SucatasManutencao.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_lblPesoSucatasMouseClicked

    private void lblPesoNotaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPesoNotaMouseClicked
        try {
            pegarDataDigitada();
            iniciarBarraTabela("DATA", " and IPC.numnfc IS NULL and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }


    }//GEN-LAST:event_lblPesoNotaMouseClicked

    private void btnPesquisarGrupo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarGrupo1ActionPerformed
        try {
            iniciarBarraTabela("DATA", " and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "' and e140nfv.codcli = " + txtCliente.getText());

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnPesquisarGrupo1ActionPerformed

    private void txtOrdemCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOrdemCompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOrdemCompraActionPerformed

    private void btnGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarActionPerformed
        if (pesoNota > 0) {
            if (ManipularRegistros.gravarRegistros(" Gravar ")) {
                try {
                    gravarSucata("");
                } catch (SQLException ex) {
                    Logger.getLogger(SucataTriagemMinuta.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (Exception ex) {
                    Logger.getLogger(SucataTriagemMinuta.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            Mensagem.mensagem("ERROR", "Peso zerado");
        }

    }//GEN-LAST:event_btnGerarActionPerformed

    private void jTableEnvioMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEnvioMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jTableEnvioMouseEntered

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        try {
            iniciarBarraTabela("DATA", " and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "' and e140nfv.codcli = " + txtCliente.getText());

        } catch (Exception ex) {
            Logger.getLogger(SucataTriagemMinuta.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnBaterias1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaterias1ActionPerformed
        try {
            pegarDataDigitada();
            this.linha = "AUT";
            iniciarBarraTabela("DATA", "   and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnBaterias1ActionPerformed

    private void btnBaterias2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaterias2ActionPerformed
        try {
            pegarDataDigitada();
            this.linha = "MOT";
            iniciarBarraTabela("DATA", "   and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");

        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnBaterias2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnBaterias1;
    private javax.swing.JButton btnBaterias2;
    private javax.swing.JButton btnGerar;
    private javax.swing.JButton btnHoras1;
    private javax.swing.JButton btnPesPer;
    private javax.swing.JButton btnPesquisarGrupo1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelMovimento;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabSucata;
    private javax.swing.JTable jTableEnvio;
    private javax.swing.JLabel lblErros;
    private javax.swing.JLabel lblPesoNota;
    private javax.swing.JLabel lblPesoOrdem;
    private javax.swing.JLabel lblPesoSucatas;
    private org.openswing.swing.client.TextControl txtCidade;
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private org.openswing.swing.client.TextControl txtEstado;
    private org.openswing.swing.client.TextControl txtFilial;
    private org.openswing.swing.client.NumericControl txtLancamento;
    private org.openswing.swing.client.TextControl txtNomeCliente;
    private org.openswing.swing.client.TextControl txtNotaSaida;
    private javax.swing.JTextArea txtObservacao;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.NumericControl txtSequencia;
    private org.openswing.swing.client.TextControl txtSerie;
    private org.openswing.swing.client.TextControl txtTransacao;
    // End of variables declaration//GEN-END:variables
}
