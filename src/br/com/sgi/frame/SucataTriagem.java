/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataAnalises;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.SucataAnalisesDAO;
import br.com.sgi.dao.SucataDAO;
import br.com.sgi.dao.SucataEcoDao;
import br.com.sgi.dao.SucataMovimentoDAO;
import br.com.sgi.dao.TransportadoraDAO;
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

/**
 *
 * @author jairosilva
 */
public final class SucataTriagem extends InternalFrame {
    
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
    
    private Transportadora transportadora;
    
    public SucataTriagem() {
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
            lblPesoOrdem.setVisible(false);
            pegarDataDigitada();
            getUsuarioLogado();
            iniciarBarraTabela("DATA", " and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");
            
            preencherCombo();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }
    
    public void preencherCombo() throws SQLException {
        TransportadoraDAO dao = new TransportadoraDAO();
        List<Transportadora> list = new ArrayList<Transportadora>();
        txtTransportadora.removeAllItems();
        txtTransportadora.addItem("0 - SELECIONE");
        list = dao.getTransportadoras(" tra ", " and codtra in (4,204) ");
        String cod = "";
        String des = "";
        String desger = "";
        if (list != null) {
            for (Transportadora tra : list) {
                cod = tra.getCodigoTransportadora().toString();
                des = tra.getNomeTransportadora();
                desger = cod + " - " + des;
                txtTransportadora.addItem(desger);
            }
        }
        
        txtEstado.removeAllItems();
        BaseEstado estado = new BaseEstado();
        Map<String, String> mapas = estado.getEstados();
        for (String uf : mapas.keySet()) {
            txtEstado.addItem(mapas.get(uf));
        }
        
    }
    
    private void pesquisarTable(String pesquisa) {
        DefaultTableModel tabela_pedidos = (DefaultTableModel) jTableEnvio.getModel();
        
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabela_pedidos);
        jTableEnvio.setRowSorter(sorter);
        pesquisa = txtPesquisar.getText().toUpperCase();
        
        if (pesquisa.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<TableModel, Object> rf = null;
                try {
                    rf = RowFilter.regexFilter(pesquisa, 3);
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                sorter.setRowFilter(rf);
            } catch (PatternSyntaxException pse) {
                System.err.println("Erro");
            }
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
        frmMinutasGerar sol = new frmMinutasGerar();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        sol.setMaximum(true); // executa maximizado 
        sol.setRecebePalavraNota(this, PROCESSO, selecionar, minuta, listminutaPedido);
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
        
        pesquisa += " and e140nfv.codfil not in (11)";
        
        if (linha.equals("AUTO")) {
            listSucataAnalises = sucataanalisesDAO.getSucataAnalisess("", pesquisa);
        }
        if (linha.equals("MOTO")) {
            listSucataAnalises = sucataanalisesDAO.getSucataAnalisessMoto("", pesquisa);
        }
        if (linha.equals("IND")) {
            listSucataAnalises = sucataanalisesDAO.getSucataAnalisessInd("", pesquisa);
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
            SucataTriagem.JTableRenderer renderers = new SucataTriagem.JTableRenderer();
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
            if ((suc.getOrdem_compra() == 0) && (!suc.getPedido_transacao().equals("902HB") && (!suc.getPedido_transacao().equals("90124")))) {
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
            
            peso_nota += suc.getPeso_bruto_nota();
            peso_ordem += suc.getPeso_ordem_compra();
            peso_sucata += suc.getPeso_sucata();
            cont++;
            System.out.println("br.com.recebimento.frame.SucataTriagem.carregarTabelaContaCorrente() Antes" + cont);
            modeloCarga.addRow(linha);
            
            System.out.println("br.com.recebimento.frame.SucataTriagem.carregarTabelaContaCorrente() depois" + cont);
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
            sucataMovimento.setFilial(1);
            sucataMovimento.setFilialsucata(1);
            sucataMovimento.setPedido(Integer.valueOf(txtPedido.getText()));
            sucataMovimento.setCliente(Integer.valueOf(txtCliente.getText()));
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
            sucataMovimento.setSucata("1001");
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
                
                this.sucata.setTransacao("90417");
                if (this.sucata.getAutomoto().equals("IND")) {
                    this.sucata.setTransacao("90124");
                }
                
                if (!this.sucataDAO.inserir(this.sucata)) {
                    
                } else {
                    gerarOrdemCompra();
                }
                
            }
            
        }
        
    }
    
    private void gerarOrdemCompra() throws SQLException {
        String empresa = "1";
        String filial = "1";
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
                    
                    if (resultado.equals("Processado com sucesso.")) {
                        
                        txtOrdemCompra.setText(sNumOcp.trim());
                        txtObservacao.setText("ORDEM DE COMPRA GERADA COM SUCESSO: " + sNumOcp);
                        
                    } else if (resultado.equals("Ocorreram erros.")) {
                        txtObservacao.setText(resultado);
                    }
                    txtObservacao.setText("Situa????o de ordem de compra: ");
                    if (sucataMovimento.getCodigolancamento() > 0) {
                        sucataMovimento.setObservacaomovimento(txtObservacao.getText());
                        sucataMovimento.setOrdemcompra(Integer.valueOf(sNumOcp.trim()));
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
                    cliente.setGruponome("N??o informado");
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
        btnBaterias = new javax.swing.JButton();
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
        txtTransportadora = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        txtEstado = new javax.swing.JComboBox<>();
        btnHoras2 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtPesquisar = new org.openswing.swing.client.TextControl();
        jLabel10 = new javax.swing.JLabel();
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

        btnBaterias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png"))); // NOI18N
        btnBaterias.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnBaterias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBateriasActionPerformed(evt);
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
                .addComponent(lblErros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(btnGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBaterias1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBaterias2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBaterias, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnBaterias, btnBaterias1, btnBaterias2, btnGerar});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnBaterias)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblPesoSucatas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblPesoNota, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblErros, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblPesoOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(btnBaterias1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBaterias2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBaterias, btnGerar, lblErros, lblPesoNota, lblPesoOrdem, lblPesoSucatas});

        jPanelMovimento.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jTableEnvio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Nota", "Peso", "Pedido", "Peso", "Ordem compra", "Peso", "#", "Peso Sucata", "#", "Nota Entrada", "Peso Entrada", "#", "Estado", "Selecionar", "#", "Cliente", "Raz??o Social", "Filial", "Movimento", "Sequencia", "Data Pedido", "Data Nota", "Data Sucata", "Tabela", "Descri????o Faturamento", "Empresa", "Serie"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, true, false, true, false, false, false, false, true, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, true
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
            jTableEnvio.getColumnModel().getColumn(11).setMinWidth(0);
            jTableEnvio.getColumnModel().getColumn(11).setPreferredWidth(0);
            jTableEnvio.getColumnModel().getColumn(11).setMaxWidth(0);
            jTableEnvio.getColumnModel().getColumn(12).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(12).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(12).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(13).setMinWidth(50);
            jTableEnvio.getColumnModel().getColumn(13).setPreferredWidth(50);
            jTableEnvio.getColumnModel().getColumn(13).setMaxWidth(50);
            jTableEnvio.getColumnModel().getColumn(14).setMinWidth(100);
            jTableEnvio.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableEnvio.getColumnModel().getColumn(14).setMaxWidth(100);
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

        jLabel4.setText("Nota Sa??da");

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

        jLabel3.setText("In??cio");

        jLabel8.setText("Fim");

        txtTransportadora.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTransportadora.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtTransportadora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransportadoraActionPerformed(evt);
            }
        });

        jLabel9.setText("Transportadora");

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));
        txtEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstadoActionPerformed(evt);
            }
        });

        btnHoras2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnHoras2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoras2ActionPerformed(evt);
            }
        });

        jLabel12.setText("Estado");

        txtPesquisar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisarActionPerformed(evt);
            }
        });

        jLabel10.setText("Pesquisar pedido na tabela");

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
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtNomeCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE))
                .addGap(2, 2, 2))
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                                .addComponent(txtPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7)))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtTransportadora, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMovimentoLayout.createSequentialGroup()
                                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtEstado, 0, 207, Short.MAX_VALUE)
                                    .addComponent(txtNotaSaida, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnHoras1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnHoras2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(10, 10, 10))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLancamento, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtSequencia, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)))
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanelMovimentoLayout.setVerticalGroup(
            jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel8)
                    .addComponent(jLabel1)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDatIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatFim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPesPer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarGrupo1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                        .addComponent(jLabel6)))
                                .addGap(2, 2, 2))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMovimentoLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNotaSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSequencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnHoras2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelMovimentoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtTransportadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addGroup(jPanelMovimentoLayout.createSequentialGroup()
                        .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1004, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
        );

        jTabSucata.addTab("Info", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabSucata, javax.swing.GroupLayout.DEFAULT_SIZE, 1009, Short.MAX_VALUE)
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
    
    private double pesoPedido = 0;
    private double pesoNota = 0;
    private void jTableEnvioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEnvioMouseClicked
        try {
            
            int linhaSelSit = jTableEnvio.getSelectedRow();
            int colunaSelSit = jTableEnvio.getSelectedColumn();
            txtNotaSaida.setText(jTableEnvio.getValueAt(linhaSelSit, 1).toString());
            pesoNota = Double.valueOf(jTableEnvio.getValueAt(linhaSelSit, 2).toString());
            pesoPedido = pesoNota;
            txtPedido.setText(jTableEnvio.getValueAt(linhaSelSit, 3).toString());
            txtOrdemCompra.setText(jTableEnvio.getValueAt(linhaSelSit, 5).toString());
            
            txtCliente.setText(jTableEnvio.getValueAt(linhaSelSit, 16).toString());
            txtNomeCliente.setText(jTableEnvio.getValueAt(linhaSelSit, 17).toString());
            txtLancamento.setText(jTableEnvio.getValueAt(linhaSelSit, 19).toString());
            txtSequencia.setText(jTableEnvio.getValueAt(linhaSelSit, 20).toString());
            
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

    private void btnBateriasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBateriasActionPerformed
        try {
            pegarDataDigitada();
            this.linha = "IND";
            iniciarBarraTabela("DATA", "   and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");
            
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnBateriasActionPerformed

    private void btnGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarActionPerformed
        if (pesoNota > 0) {
            if (ManipularRegistros.gravarRegistros(" Gravar ")) {
                try {
                    gravarSucata("");
                } catch (SQLException ex) {
                    Logger.getLogger(SucataTriagem.class
                            .getName()).log(Level.SEVERE, null, ex);
                    
                } catch (Exception ex) {
                    Logger.getLogger(SucataTriagem.class
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
            Logger.getLogger(SucataTriagem.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_txtClienteActionPerformed

    private void txtTransportadoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransportadoraActionPerformed
        //
    }//GEN-LAST:event_txtTransportadoraActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            if (txtTransportadora.getSelectedIndex() != -1) {
                if (!txtTransportadora.getSelectedItem().toString().equals("SELECIONE")) {
                    String cod = txtTransportadora.getSelectedItem().toString();
                    int index = cod.indexOf("-");
                    String codcon = cod.substring(0, index);
                    TransportadoraDAO dao = new TransportadoraDAO();
                    transportadora = new Transportadora();
                    this.transportadora = dao.getTransportadora(" codtra ", "and codtra = " + codcon);
                    
                    iniciarBarraTabela(" transportadoda ", "and IPC.numnfc IS NULL  and e140nfv.datemi >= '" + datIni + "' "
                            + " and e140nfv.datemi <='" + datFim + "'"
                            + " and e140nfv.codtra = " + codcon + "\n");
                    
                }
            }
        } catch (Exception e) {
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void txtEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadoActionPerformed
        //
    }//GEN-LAST:event_txtEstadoActionPerformed

    private void btnHoras2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoras2ActionPerformed
        
        if (txtEstado.getSelectedIndex() != -1) {
            if (!txtEstado.getSelectedItem().toString().equals("TODOS")) {
                
                try {
                    iniciarBarraTabelaGeral(" transportadoda ", " and e140nfv.datemi >= '" + datIni + "' "
                            + " and e140nfv.datemi <='" + datFim + "'"
                            + " and e085cli.sigufs = '" + txtEstado.getSelectedItem().toString() + "'\n");
                    
                } catch (Exception ex) {
                    Logger.getLogger(SucataTriagem.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnHoras2ActionPerformed

    private void txtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisarActionPerformed
        pesquisarTable(txtPesquisar.getText());
    }//GEN-LAST:event_txtPesquisarActionPerformed

    private void btnBaterias1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaterias1ActionPerformed
        try {
            pegarDataDigitada();
            this.linha = "AUTO";
            iniciarBarraTabela("DATA", "   and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");
            
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnBaterias1ActionPerformed

    private void btnBaterias2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaterias2ActionPerformed
        try {
            pegarDataDigitada();
            this.linha = "MOTO";
            iniciarBarraTabela("DATA", "   and e140nfv.datemi >= '" + datIni + "' and e140nfv.datemi <='" + datFim + "'");
            
        } catch (Exception ex) {
            Mensagem.mensagemRegistros("ERRO", ex.toString());
        }
    }//GEN-LAST:event_btnBaterias2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton btnBaterias;
    private javax.swing.JButton btnBaterias1;
    private javax.swing.JButton btnBaterias2;
    private javax.swing.JButton btnGerar;
    private javax.swing.JButton btnHoras1;
    private javax.swing.JButton btnHoras2;
    private javax.swing.JButton btnPesPer;
    private javax.swing.JButton btnPesquisarGrupo1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private org.openswing.swing.client.TextControl txtCliente;
    private org.openswing.swing.client.DateControl txtDatFim;
    private org.openswing.swing.client.DateControl txtDatIni;
    private javax.swing.JComboBox<String> txtEstado;
    private org.openswing.swing.client.NumericControl txtLancamento;
    private org.openswing.swing.client.TextControl txtNomeCliente;
    private org.openswing.swing.client.TextControl txtNotaSaida;
    private javax.swing.JTextArea txtObservacao;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.TextControl txtPesquisar;
    private org.openswing.swing.client.NumericControl txtSequencia;
    private javax.swing.JComboBox<String> txtTransportadora;
    // End of variables declaration//GEN-END:variables
}
