/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.CargaRegistro;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Embalagem;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.PedidoHubProduto;
import br.com.sgi.dao.CargaAberturaDAO;
import br.com.sgi.dao.EmbalagemDAO;
import br.com.sgi.dao.MinutaDAO;
import br.com.sgi.dao.MinutaPedidoDAO;
import br.com.sgi.dao.PedidoDAO;
import br.com.sgi.dao.PedidoProdutoDAO;
import br.com.sgi.dao.ProdutoDAO;
import br.com.sgi.frame.faturamento.pnPedidoPadrao;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSRelatorio;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.sql.SQLException;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class frmMinutasExpedicaoGerar extends InternalFrame {

    private List<NotaFiscal> lstNotaFiscal = new ArrayList<NotaFiscal>();

    private Minuta minuta;
    private MinutaDAO minutaDAO;

    private MinutaPedido minutapedido;
    private List<MinutaPedido> listminutaPedido = new ArrayList<MinutaPedido>();
    private MinutaPedidoDAO minutaPedidoDAO;

    private List<PedidoHubProduto> listPedidoProduto = new ArrayList<PedidoHubProduto>();

    private List<Pedido> listPedido = new ArrayList<Pedido>();
    private List<Pedido> listPedidoSemPre = new ArrayList<Pedido>();

    private Pedido pedido;

    private CargaRegistro cargaRegistro;

    private FatPedidoExpedicao veioCampo;
    private frmMinutasExpedicao veioCampoExpedicao;
    private pnPedidoPadrao veioCampoPedidoFaturar;
    private SucataContaCorrenteIndustrializacao veioCampoSucataInd;
    private FatPedidoExpedicaoMetais veioCampoMetais;
    private UtilDatas utilDatas;

    private boolean addReg;
    private double pesoSelecionado = 0;
    private double quantidadeSelecionado = 0;
    private String pedidoSelecionado = "0";
    private Integer clienteSelecionado = 0;
    private String PROCESSO;

    public frmMinutasExpedicaoGerar() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Minutas notas"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (minutaDAO == null) {
                this.minutaDAO = new MinutaDAO();
            }
            if (minutaPedidoDAO == null) {
                this.minutaPedidoDAO = new MinutaPedidoDAO();
            }

            getEmbalagens();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private boolean popularCampo() {
        boolean registrar = true;
        if (txtQtdVolume.getText().isEmpty()) {
            registrar = false;
            Mensagem.mensagem("ERROR", "Informe o Volume");
        } else {
            if (txtQtdVolume.getText().equals("0")) {
                registrar = false;
                Mensagem.mensagem("ERROR", "Informe o Volume");
            } else if (txtEmbalagem.getSelectedItem().equals("SELECIONE")) {
                registrar = false;
                Mensagem.mensagem("ERROR", "Informe a embalagem");
            } else {

                int cli = 0;

                if (listPedido.size() > 0) {
                    for (Pedido p : listPedido) {
                        if (p.getPedido() > 0) {
                            cli = p.getCliente();
                            if (cli != this.clienteSelecionado) {

                                registrar = false;
                                Mensagem.mensagem("ERROR", "Cliente da minuta esta diferente dos pedidos");
                                break;
                            }
                        }

                    }
                }

                if (listPedidoSemPre.size() > 0) {
                    for (Pedido p : listPedidoSemPre) {
                        if (p.getPedido() > 0) {
                            cli = p.getCliente();
                            if (cli != this.clienteSelecionado) {
                                registrar = false;
                                Mensagem.mensagem("ERROR", "Cliente da minuta esta diferente dos pedidos");
                                break;
                            }
                        }

                    }
                }

            }
        }
        if (registrar) {
            minuta.setUsu_datemi(new Date());
            minuta.setUsu_codtra(0);
            minuta.setUsu_codemp(Integer.valueOf(txtEmpresa.getText()));
            minuta.setUsu_codfil(Integer.valueOf(txtFilial.getText()));
            minuta.setUsu_codlan(Integer.valueOf(txtMinuta.getText()));
            minuta.setUsu_sitmin(txtSituacao.getSelectedItem().toString());
            minuta.setUsu_qtdfat(txtQtdy.getDouble());
            minuta.setUsu_pesfat(txtPesoSelecionado.getDouble());
            double vol = txtQtdVolume.getDouble();
            int volmin = (int) vol;
            minuta.setUsu_qtdvol(volmin);

            minuta.setUsu_codmtr(0);
            minuta.setUsu_plavei("");

            minuta.setUsu_usuger(0);
            minuta.setUsu_datlib(new Date());
            minuta.setUsu_datsai(new Date());
            minuta.setUsu_orimin("EXP");
            if (minuta.getUsu_libmot().equals("I")) {
                minuta.setUsu_orimin("MET");
            }
            if (minuta.getUsu_libmot().equals("G")) {
                minuta.setUsu_orimin("MG");
            }
            if (txtObservacao.getValue() != null) {
                minuta.setUsu_obsmin(txtObservacao.getValue().toString());
            }
            minuta.setUsu_nommin(txtNomeMinuta.getText());
            String emb = txtEmbalagem.getSelectedItem().toString();
            int index = emb.indexOf("-");
            String codemb = emb.substring(0, index).trim();
            minuta.setUsu_codemb(Integer.valueOf(codemb)); // utiliado para guardar a embalagem
        }

        return registrar;
    }

    private void getEmbalagens() throws SQLException, Exception {

        String emb = "";
        List<Embalagem> lista = new ArrayList<Embalagem>();
        ProdutoDAO dao = new ProdutoDAO();
        lista = dao.getEmbalagems();
        if (lista != null) {
            if (lista.size() > 0) {
                for (Embalagem e : lista) {
                    emb = e.getEmbalagem() + " - " + e.getDescricaoEmbalagem();
                    txtEmbalagem.addItem(emb);
                }

            }
        }

    }

    private void cancelarMinuta(String situacao) throws SQLException, Exception {
        if (minuta != null) {
            if (minuta.getUsu_codlan() > 0) {
                this.minuta.setUsu_sitmin(situacao);
                if (!minutaDAO.remover(minuta)) {

                } else {
                    if (!minutaDAO.removerItens(minuta)) {

                    } else {
                        PedidoDAO dao = new PedidoDAO();
                        Pedido p = new Pedido();
                        p.setCodigominuta(minuta.getUsu_codlan());
                        if (!dao.removerMinutaPedido(p)) {

                        } else {

                        }
                    }
                }

            }
        }
    }

    private boolean alterouDados;

    private void gravar() throws SQLException, Exception {
        alterouDados = false;
        btnEditarMinutaPedido.setEnabled(false);
        btnInserirMinutaPedido.setEnabled(false);
        if (popularCampo()) {
            if (addReg) {
                minuta.setUsu_codlan(this.minutaDAO.proxCodCad());
                if (!this.minutaDAO.inserir(minuta)) {
                } else {
                    txtMinuta.setText(String.valueOf(minuta.getUsu_codlan()));
                    selecionarRange();
                }
            } else {
                if (!this.minutaDAO.alterar(minuta)) {

                } else {
                    alterouDados = true;
                    btnEditarMinutaPedido.setEnabled(true);
                    btnInserirMinutaPedido.setEnabled(true);
                }
            }
        }
        addReg = false;
    }

    public void selecionarRange() throws SQLException, Exception {
        if (jTableCarga.getRowCount() > 0) {
            PedidoDAO daoPedido = new PedidoDAO();
            String numeroPedido = "0";
            Integer qtdreg = jTableCarga.getRowCount();
            Integer contador = 0;

            String situacaoDocumento = "";
            String atualizarVolume = "N";
            int prefatura = 0;
            int analise = 0;
            int filial = 0;

            if (jTableCarga.getRowCount() > 0) {
                for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                    if ((Boolean) jTableCarga.getValueAt(i, 4)) {
                        numeroPedido = (jTableCarga.getValueAt(i, 1).toString());
                        if (!numeroPedido.isEmpty()) { // tem pedido
                            MinutaPedido minutaPedido = new MinutaPedido();
                            minutaPedido.setCadMinuta(minuta);
                            minutaPedido.setUsu_numnfv(0);
                            minutaPedido.setUsu_numpfa(Integer.valueOf(jTableCarga.getValueAt(i, 2).toString()));
                            minutaPedido.setUsu_numana(Integer.valueOf(jTableCarga.getValueAt(i, 3).toString()));
                            minutaPedido.setUsu_tnspro(jTableCarga.getValueAt(i, 5).toString());
                            minutaPedido.setUsu_codcli(Integer.valueOf(jTableCarga.getValueAt(i, 7).toString()));
                            Cliente cli = new Cliente();
                            cli.setCodigo(minutaPedido.getUsu_codcli());
                            cli.setNome(jTableCarga.getValueAt(i, 8).toString());
                            cli.setEstado(jTableCarga.getValueAt(i, 9).toString());
                            cli.setCidade(jTableCarga.getValueAt(i, 10).toString());
                            minutaPedido.setCadCliente(cli);

                            minutaPedido.setUsu_pesped(Double.valueOf(jTableCarga.getValueAt(i, 16).toString()));
                            minutaPedido.setUsu_qtdped(Double.valueOf(jTableCarga.getValueAt(i, 17).toString()));
                            minutaPedido.setUsu_pesnfv(minutaPedido.getUsu_pesped());
                            minutaPedido.setUsu_qtdfat(minutaPedido.getUsu_qtdped());
                            minutaPedido.setUsu_pessuc(minutaPedido.getUsu_pesped());

                            if (veioCampoSucataInd != null) {
                                minutaPedido.setUsu_pesnfv(txtQtdy.getDouble());
                                minutaPedido.setUsu_qtdfat(txtQtdy.getDouble());
                                if (perren > 0) {
                                    minutaPedido.setUsu_pessuc(minutaPedido.getUsu_pesped() / (perren / 100));
                                }
                            }

                            minutaPedido.setUsu_codemp(1);
                            minutaPedido.setUsu_codtpr("");

                            minutaPedido.setUsu_numped(Integer.valueOf(numeroPedido));
                            minutaPedido.setUsu_obsmin("GERANDO MINUTA PEDIDO " + numeroPedido);

                            minutaPedido.setUsu_qtdvol(0.0);
                            minutaPedido.setUsu_sitmin(txtSituacao.getSelectedItem().toString());
                            minutaPedido.setUsu_codfil(Integer.valueOf(jTableCarga.getValueAt(i, 20).toString()));

                            situacaoDocumento = jTableCarga.getValueAt(i, 26).toString();
                            if (situacaoDocumento.equals("NF")) {
                                atualizarVolume = "S";
                                prefatura = minutaPedido.getUsu_numpfa();
                                analise = minutaPedido.getUsu_numana();
                                filial = minutaPedido.getUsu_codfil();
                            }

                            minutaPedido.setUsu_codori("");
                            minutaPedido.setUsu_codsnf("");
                            minutaPedido.setUsu_lansuc(0);
                            minutaPedido.setUsu_codpes(0);
                            minutaPedido.setUsu_pesbal(0.0);
                            minutaPedido.setUsu_pesrec(0.0);

                            minutaPedido.setUsu_datemi(new Date());
                            minutaPedido.setUsu_datlib(new Date());
                            minutaPedido.setUsu_codlan(this.minuta.getUsu_codlan());
                            minutaPedido.setUsu_seqite(minutaPedidoDAO.proxCodCad());
                            contador++;
                            if (!minutaPedidoDAO.inserir(minutaPedido, qtdreg, contador)) {

                            } else {
                                pedido = new Pedido();
                                pedido.setEmpresa(minutaPedido.getUsu_codemp());
                                pedido.setFilial(minutaPedido.getUsu_codfil());
                                pedido.setPedido(minutaPedido.getUsu_numped());
                                pedido.setCliente(minutaPedido.getUsu_codcli());
                                pedido.setCodigominuta(minutaPedido.getUsu_codlan());

                                pedido.setLiberarMinuta("S");
                                daoPedido.AtualizarMinuta(pedido);

                            }

                        }

                    }
                }
                if (atualizarVolume.equals("S")) {
                    if (filial > 0 && analise > 0 && prefatura > 0) {
                        this.minuta.setUsu_codfil(filial);
                        this.minuta.setUsu_numpfa(prefatura);
                        this.minuta.setUsu_numane(analise);
//                        if (this.minutaPedidoDAO.gravarDadosMinuta(minuta)) {
//
//                        }

                    }
                }

            }
        }
    }

    private void getPedido(String PESQUISA_POR, String PESQUISA) throws SQLException {
        pedido = new Pedido();
        PedidoDAO pedidoDAO = new PedidoDAO();
        pedido = pedidoDAO.getPedido(PESQUISA_POR, PESQUISA);
        if (pedido != null) {
            if (pedido.getPedido() > 0) {

            }
        }
    }

    private void sair() throws Exception {
        if (veioCampo != null) {
            try {
                veioCampo.retornarPedido();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }
        if (veioCampoExpedicao != null) {
            try {
                veioCampoExpedicao.retornarMinuta();
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            }
        }

        if (veioCampoPedidoFaturar != null) {
         
            this.dispose();
        }

        if (veioCampoSucataInd != null) {
            this.dispose();
        }

        if (veioCampoMetais != null) {
            veioCampoMetais.retornarPedido();
            this.dispose();
        }
    }

    public void retornar() throws Exception {
        getListarMinuta(" minuta ", " and usu_codlan = " + minuta.getUsu_codlan());
    }

    public void getListarMinuta(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        listminutaPedido = this.minutaPedidoDAO.getMinutaPedidos(PESQUISA_POR, PESQUISA);
        if (listminutaPedido != null) {
            carregarTabelaMinutaNota();

        }
        btnEditarMinutaPedido.setEnabled(false);
        btnInserirMinutaPedido.setEnabled(false);
    }

    public void carregarTabelaMinutaNota() throws Exception {
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
        ImageIcon MktIcon = getImage("/images/bateriaindu.png");
        ImageIcon HubIcon = getImage("/images/truck_red.png");

        ImageIcon NotIcon = getImage("/images/Nota.png");
        ImageIcon RomIcon = getImage("/images/NotaRomaneio.png");
        ImageIcon NotGarIcon = getImage("/images/NotaGarantia.png");
        ImageIcon NgIcon = getImage("/images/book.png");
        for (MinutaPedido mp : listminutaPedido) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            frmMinutasExpedicaoGerar.JTableRenderer renderers = new frmMinutasExpedicaoGerar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            linha[0] = BomIcon;
            if (mp.getUsu_numpfa() == 0) {
                linha[0] = MedIcon;
            }

            linha[1] = mp.getUsu_numped();
            linha[2] = mp.getUsu_numpfa();
            linha[3] = mp.getUsu_numana();
            linha[4] = true;
            linha[5] = mp.getUsu_tnspro();
            columnModel.getColumn(6).setCellRenderer(renderers);
            linha[6] = NotIcon;
            if (mp.getTipodocumento().equals("R")) {
                linha[6] = RomIcon;
            }
            if (mp.getTipodocumento().equals("NG")) {
                linha[6] = NotGarIcon;
            }
            linha[7] = mp.getUsu_codcli();
            linha[8] = mp.getCadCliente().getNome();
            linha[9] = mp.getCadCliente().getEstado();
            linha[10] = mp.getCadCliente().getCidade();

            linha[11] = mp.getEmissaoS(); // data da pré
            columnModel.getColumn(14).setCellRenderer(renderers);
            linha[14] = AutIcon;
            if (mp.getUsu_codori().equals("BM")) {
                linha[14] = MotIcon;
            }
            if (mp.getUsu_codori().equals("GAR")) {
                linha[14] = GarIcon;
            }
            if (mp.getUsu_codori().equals("MKT")) {
                linha[14] = MktIcon;
            }
            if (mp.getUsu_tnspro().equals("902HB")) {
                linha[14] = HubIcon;
            }
            if (mp.getUsu_tnspro().equals("MG")) {
                linha[14] = NgIcon;
            }
            linha[15] = mp.getUsu_sitmin();
            linha[16] = mp.getUsu_pesped();
            linha[17] = mp.getUsu_qtdped();
            linha[19] = mp.getUsu_codemp();
            linha[20] = mp.getUsu_codfil();

            linha[21] = mp.getEmissaoS();
            linha[12] = 0;
            linha[23] = 0;
            linha[24] = mp.getUsu_codtra() + " - ";
            linha[25] = mp.getUsu_seqite();
            linha[26] = mp.getTipodocumento();

            modeloCarga.addRow(linha);

        }

    }

    public void setRecebePedidoMetais(SucataContaCorrenteIndustrializacao veioInput,
            List<Pedido> listPedido,
            String cliente,
            String autmot,
            String clienteselecionado) throws Exception {

        this.PROCESSO = "PEDIDO";
        this.veioCampoSucataInd = veioInput;
        this.minuta = new Minuta();
        this.listPedido = listPedido;
        this.clienteSelecionado = Integer.valueOf(clienteselecionado);
        minuta.setUsu_libmot(autmot);
        txtSituacao.setSelectedItem("LIBERADA");
        btnGravar.setEnabled(false);

        txtQtdy.setEnabled(false);
        txtQtdVolume.setEnabled(true);
        txtObservacao.setEnabled(true);
        txtEmbalagem.setEnabled(true);

        txtEmpresa.setText("1");
        txtFilial.setText("1");
        txtEmissao.setDate(new Date());
        txtMinuta.setText("0");
        txtQtdVolume.setValue(1);
        txtQtdVolume.requestFocus();

        txtEmbalagem.setSelectedItem("11 - KILO(S)");

        addReg = true;

        if (listPedido.size() > 0) {
            txtNomeMinuta.setText(cliente);
            carregarTabela(listPedido, null);
            btnGravar.setEnabled(true);
        }

    }

    public void setRecebePedidoMetaisGeral(FatPedidoExpedicaoMetais veioInput,
            List<Pedido> listPedido,
            String cliente,
            String autmot,
            String clienteselecionado) throws Exception {

        this.PROCESSO = "PEDIDO";
        this.veioCampoMetais = veioInput;
        this.minuta = new Minuta();
        this.listPedido = listPedido;
        this.clienteSelecionado = Integer.valueOf(clienteselecionado);
        minuta.setUsu_libmot(autmot);
        txtSituacao.setSelectedItem("LIBERADA");
        btnGravar.setEnabled(false);

        txtQtdy.setEnabled(false);
        txtQtdVolume.setEnabled(true);
        txtObservacao.setEnabled(true);
        txtEmbalagem.setEnabled(true);

        txtEmpresa.setText("1");
        txtFilial.setText("1");
        txtEmissao.setDate(new Date());
        txtMinuta.setText("0");
        txtQtdVolume.setValue(1);
        txtQtdVolume.requestFocus();
        txtEmbalagem.setSelectedItem("11 - KILO(S)");
        addReg = true;

        if (listPedido.size() > 0) {
            txtNomeMinuta.setText(cliente);
            carregarTabela(listPedido, null);
            btnGravar.setEnabled(true);
        }

    }

    public void setRecebeSemPedido(FatPedidoExpedicaoMetais veioInput) throws Exception {
        addReg = true;
        this.minuta = new Minuta();
        this.veioCampoMetais = veioInput;

        this.PROCESSO = "MINUTA_GERAL";
        txtSituacao.setSelectedItem("LIBERADA");

        txtQtdy.setEnabled(true);
        txtQtdy.setValue(1);

        txtQtdVolume.setEnabled(true);
        txtQtdVolume.setValue(1);

        txtObservacao.setEnabled(true);
        txtEmbalagem.setEnabled(true);

        txtEmpresa.setText("1");
        txtFilial.setText("1");
        txtEmissao.setDate(new Date());
        txtMinuta.setText("0");
        txtEmbalagem.setSelectedItem("11 - KILO(S)");
        txtNomeMinuta.setText("");
        btnGravar.setEnabled(true);

        txtCodigoTicket.setEnabled(true);

        txtQtdVolume.requestFocus();

    }

    public void setRecebePedido(FatPedidoExpedicao veioInput,
            List<Pedido> listPedido,
            List<Pedido> listPedidoSemPre,
            String cliente,
            String autmot,
            String clienteselecionado) throws Exception {

        this.PROCESSO = "PEDIDO";
        this.veioCampo = veioInput;
        this.minuta = new Minuta();

        this.listPedido = listPedido;
        this.listPedidoSemPre = listPedidoSemPre;
        this.clienteSelecionado = Integer.valueOf(clienteselecionado);

        minuta.setUsu_libmot(autmot);
        if (autmot.equals("BA")) {
            minuta.setUsu_libmot("A");
        }
        if (autmot.equals("BM")) {
            minuta.setUsu_libmot("M");
        }

        txtSituacao.setSelectedItem("LIBERADA");
        btnGravar.setEnabled(false);
        txtQtdVolume.setEnabled(true);
        txtObservacao.setEnabled(true);
        txtEmbalagem.setEnabled(true);
        txtEmpresa.setText("1");
        txtFilial.setText("1");
        txtEmissao.setDate(new Date());
        txtMinuta.setText("0");
        txtQtdVolume.setValue(1);
        txtQtdVolume.requestFocus();
        addReg = true;

        if (listPedido.size() > 0 || listPedidoSemPre.size() > 0) {
            txtNomeMinuta.setText(cliente);
            carregarTabela(listPedido, listPedidoSemPre);
            btnGravar.setEnabled(true);
        }

    }

    public void carregarTabela(List<Pedido> listPedido, List<Pedido> listPedidoSemPre) throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon MedIcon = getImage("/images/cadeado.png");
        ImageIcon ReaIcon = getImage("/images/sitAnd.png");

        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");
        ImageIcon MetIcon = getImage("/images/bateriaindu.png");

        ImageIcon GarIcon = getImage("/images/ruby_delete.png");
        ImageIcon MktIcon = getImage("/images/garantias.png");

        ImageIcon NotIcon = getImage("/images/Nota.png");
        ImageIcon RomIcon = getImage("/images/NotaRomaneio.png");
        ImageIcon NotGarIcon = getImage("/images/NotaGarantia.png");
        double peso = 0;
        double qtdy = 0;
        double qtdped = 0;

        for (Pedido ped : listPedido) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            frmMinutasExpedicaoGerar.JTableRenderer renderers = new frmMinutasExpedicaoGerar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = BomIcon;
            if (ped.getSituacaopre().equals("2")) {
                linha[0] = RuimIcon;
            }

            linha[1] = ped.getPedido();
            linha[2] = ped.getNumeropre();
            linha[3] = ped.getNumeroanalise();

            linha[4] = true;

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

            if (ped.getLinha().equals("METAIS") || ped.getLinha().equals("RE")) {
                linha[14] = MetIcon;
            }
            if (!ped.getLinha().equals("BM") && !ped.getLinha().equals("BA") && !ped.getLinha().equals("METAIS") && !ped.getLinha().equals("RE")) {
                linha[14] = MktIcon;
            }

            ped.setLiberarMinuta("N");
            TableCellRenderer renderer = new frmMinutasExpedicaoGerar.ColorirRenderer();
            jTableCarga.getColumnModel().getColumn(15).setCellRenderer(renderer);
            linha[15] = ped.getLiberarMinuta();

            linha[16] = ped.getPeso();

            linha[17] = ped.getQuantidade();
            linha[18] = ped.getSituacaoLogistica();
            linha[19] = ped.getEmpresa();
            linha[20] = ped.getFilial();
            linha[21] = ped.getEmissaoS();
            linha[22] = ped.getDataAgendamentoS();
            linha[23] = ped.getQtddia_atrazo();
            linha[24] = ped.getCadTransportadora().getCodigoTransportadora() + " - " + ped.getCadTransportadora().getNomeTransportadora();
            linha[25] = ped.getSituacaopre();
            linha[26] = ped.getTipodocumento();
            linha[27] = ped.getPercentualRentabilidade();
            qtdped++;
            peso += ped.getPeso();
            qtdy += ped.getQuantidade();
            if (ped.getTransacao().equals("90124")) {
                txtObservacao.setText("Rentabilidade " + ped.getPercentualRentabilidade() + ""
                        + "\nPeso de sucata à baixar " + ped.getPesoRentabilidade());
            }

            modeloCarga.addRow(linha);
        }
        if (listPedidoSemPre != null) {
            if (!listPedidoSemPre.isEmpty()) {
                for (Pedido ped : listPedidoSemPre) {
                    Object[] linha = new Object[30];
                    TableColumnModel columnModel = jTableCarga.getColumnModel();

                    frmMinutasExpedicaoGerar.JTableRenderer renderers = new frmMinutasExpedicaoGerar.JTableRenderer();
                    columnModel.getColumn(0).setCellRenderer(renderers);

                    linha[0] = MedIcon;

                    linha[1] = ped.getPedido();
                    linha[2] = ped.getNumeropre();
                    linha[3] = ped.getNumeroanalise();

                    linha[4] = true;

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

                    if (ped.getLinha().equals("METAIS")) {
                        linha[14] = MetIcon;
                    }
                    if (!ped.getLinha().equals("BM") || ped.getLinha().equals("BA") || ped.getLinha().equals("METAIS")) {
                        linha[14] = MktIcon;
                    }

                    ped.setLiberarMinuta("N");
                    TableCellRenderer renderer = new frmMinutasExpedicaoGerar.ColorirRenderer();
                    jTableCarga.getColumnModel().getColumn(15).setCellRenderer(renderer);
                    linha[15] = ped.getLiberarMinuta();

                    linha[16] = ped.getPeso();

                    linha[17] = ped.getQuantidade();
                    linha[18] = ped.getSituacaoLogistica();
                    linha[19] = ped.getEmpresa();
                    linha[20] = ped.getFilial();
                    linha[21] = ped.getEmissaoS();
                    linha[22] = ped.getDataAgendamentoS();
                    linha[23] = ped.getQtddia_atrazo();
                    linha[24] = ped.getCadTransportadora().getCodigoTransportadora() + " - " + ped.getCadTransportadora().getNomeTransportadora();
                    linha[25] = ped.getSituacaopre();
                    linha[26] = ped.getTipodocumento();
                    qtdped++;
                    peso += ped.getPeso();
                    qtdy += ped.getQuantidade();

                    modeloCarga.addRow(linha);
                }
            }
        }
        txtPesoSelecionado.setValue(0);
        if (peso > 0) {
            txtPesoSelecionado.setValue(peso);
        }
        if (qtdy > 0) {
            txtQtdy.setValue(qtdy);
        }

    }

    public void setRecebePalavraManutencao(frmMinutasExpedicao veioInput,
            Minuta minuta) throws Exception {
        desabiltar(false);
        this.veioCampoExpedicao = veioInput;
        this.minuta = new Minuta();
        this.minuta = minuta;
        this.addReg = false;
        btnEditar.setEnabled(true);
        this.PROCESSO = "MINUTA";
        if (minuta != null) {
            if (minuta.getUsu_codlan() > 0) {
                popularTela();
                btnInserirMinutaPedido.setEnabled(true);
                btnExcluir.setEnabled(true);
                if (minuta.getUsu_numnfv() > 0) {
                    btnExcluir.setEnabled(false);
                }
            }

        }

    }

    public void setRecebePalavraFaturar(pnPedidoPadrao veioInput,
            Minuta minuta) throws Exception {
        desabiltar(false);
        this.veioCampoPedidoFaturar = veioInput;
        this.minuta = new Minuta();
        this.minuta = minuta;
        this.addReg = false;
        btnEditar.setEnabled(true);
        btnFaturar.setEnabled(false);
        this.PROCESSO = "MINUTA";
        if (minuta != null) {
            btnFaturar.setEnabled(true);
            popularTela();

        }

    }

    private void desabiltar(boolean acao) {
        btnVolumes.setEnabled(acao);
        btnGravar.setEnabled(acao);
    }

    private void popularTela() throws Exception {
        if (minuta.getUsu_codlan() > 0 || minuta.getUsu_codfil() > 0) {
            txtNumNfv.setText(String.valueOf(minuta.getUsu_numnfv()));
            txtEmissao.setDate(minuta.getUsu_datemi());
            txtPesoSelecionado.setValue(minuta.getUsu_pesfat());
            txtQtdy.setValue(minuta.getUsu_qtdfat());
            txtObservacao.setText(minuta.getUsu_obsmin());
            txtMinuta.setText(minuta.getUsu_codlan().toString());
            txtEmpresa.setText(minuta.getUsu_codemp().toString());
            txtFilial.setText(minuta.getUsu_codfil().toString());
            txtNomeMinuta.setText(minuta.getUsu_nommin());
            txtQtdVolume.setValue(minuta.getUsu_qtdvol());
            txtSituacao.setSelectedItem(minuta.getUsu_sitmin());
            txtObservacao.setText(minuta.getUsu_obsmin());
            txtEmbalagem.setSelectedItem("SELECIONE");
            if (minuta.getUsu_codemb() > 0) { // tem embalagem
                Embalagem emb = new Embalagem();
                EmbalagemDAO dao = new EmbalagemDAO();
                emb = dao.getEmbalagem(" codigo ", " and codemb = " + minuta.getUsu_codemb());
                if (emb != null) {
                    if (emb.getEmbalagem() > 0) { // achou o cadastro da embalagem
                        txtEmbalagem.setSelectedItem(emb.getEmbalagem() + " - " + emb.getDescricaoEmbalagem());
                    } else {

                    }
                }
            }

            btnGravar.setEnabled(false);
            txtEmbalagem.setEnabled(true);
            if (minuta.getUsu_codlan() > 0) {
                btnGravar.setEnabled(true);
                if (!minuta.getUsu_sitmin().equals("ABERTA")) {
                    btnGravar.setEnabled(false);
                    txtSituacao.setEnabled(false);
                    txtEmbalagem.setEnabled(false);
                }
                getListarMinuta(" minuta ", " and usu_codlan = " + minuta.getUsu_codlan());
            } else {
                txtEmissao.setDate(new Date());
                txtSituacao.setSelectedItem("LIBERADA");
                if (listminutaPedido != null) {
                    if (listminutaPedido.size() > 0) {
                        carregarTabelaMinutaNota();
                    }
                }
            }

        }
    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableCarga.setRowHeight(40);
        jTableCarga.getColumnModel().getColumn(1).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(2).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(3).setCellRenderer(direita);
        // jTableCarga.getColumnModel().getColumn(4).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(11).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(15).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(16).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(17).setCellRenderer(direita);

        jTableCarga.getColumnModel().getColumn(20).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(21).setCellRenderer(direita);

        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoCreateRowSorter(true);
        jTableCarga.setAutoResizeMode(0);
        // jTableCarga.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        jTabPro.setRowHeight(40);

    }

    private void alterarDados() {
        try {
            FrmMinutasManutencao sol = new FrmMinutasManutencao();
            MDIFrame.add(sol, true);
            // sol.setMaximum(true); // executa maximizado
            sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavra(this, minutapedido);

        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesos.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getPedidosProdutos() throws SQLException, Exception {
        PedidoProdutoDAO dao = new PedidoProdutoDAO();
        String selecionar = "";
        if (jTableCarga.getRowCount() > 0) {
            for (int i = 0; i < jTableCarga.getRowCount(); i++) {
                selecionar += (jTableCarga.getValueAt(i, 1).toString() + ",");
            }
            int tam = selecionar.length();
            if (tam > 0) {
                if (!selecionar.isEmpty()) {
                    selecionar = selecionar.substring(0, tam - 1);
                    listPedidoProduto = dao.getPedidoProdutos("pedido", "\n and ped.numped  in (" + selecionar + ")");
                    if (listPedidoProduto != null) {
                        if (listPedidoProduto.size() > 0) {
                            carregarTabelaProduto();
                        }
                    }
                }
            }
        }
    }

    public void carregarTabelaProduto() throws Exception {
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTabPro.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        int qtdvol = 0;
        for (PedidoHubProduto mp : listPedidoProduto) {
            Object[] linha = new Object[20];
            TableColumnModel columnModel = jTabPro.getColumnModel();
            frmMinutasExpedicaoGerar.JTableRenderer renderers = new frmMinutasExpedicaoGerar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            linha[0] = BomIcon;
            if (mp.getQuantidadeCaixa() == 0) {
                linha[0] = RuimIcon;
            }

            linha[1] = mp.getPedido();
            linha[2] = mp.getProduto();
            linha[3] = mp.getDescricao();
            linha[4] = mp.getDatapedidoS();
            linha[5] = mp.getQuantidade();
            linha[6] = mp.getPesoproduto();
            linha[7] = mp.getPesopedido();
            linha[8] = mp.getQuantidadeCaixa();
            linha[9] = mp.getTotalVolumes();
            qtdvol += mp.getTotalVolumes();

            modeloCarga.addRow(linha);
        }

        txtQtdVolume.setValue(qtdvol);

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }

    public final void gerarRelatatorio(String valor) throws SQLException, Exception {

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            try {
                String Data = "";
                String arquivo = "1";
                String relatorio = "RFNF601";

                String entrada = "<ECodLan=" + valor + ">";
                String diretorio = "\\\\SRV-SPNS01\\Senior_ERP\\Sapiens\\Relatorios\\Expedicao\\";
                WSRelatorio.chamarMetodoWsXmlHttpSapiens(arquivo, relatorio, entrada, diretorio,"tsfNormal");
                desktop.open(new File(diretorio + arquivo + ".IMP"));
            } catch (Exception ex) {
                Logger.getLogger(Comissao.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado");
        } catch (Error e) {
            JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
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

    private void saldoSucata() throws SQLException, Exception {

        if (txtCodigoTicket.getText().isEmpty()) {
            txtCodigoTicket.setText("0");
            txtPesoBalanca.setValue(0);
            Mensagem.mensagem("ERROR", "INFORME UM CÓDIGO VALIDO");
        } else {
            cargaRegistro = new CargaRegistro();
            CargaAberturaDAO dao = new CargaAberturaDAO();
            cargaRegistro = dao.getCargaRegistroMinuta("PLACA", " and car.usu_nrocar = '" + txtCodigoTicket.getText() + "'");

            txtPesoBalanca.setValue(0);
            if (cargaRegistro != null) {
                if (cargaRegistro.getNumerocarga() > 0) {
                    txtPesoBalanca.setValue(cargaRegistro.getPesoLiquidoCarga());
                    txtQtdy.setValue(cargaRegistro.getPesoLiquidoCarga());
                    txtPesoSelecionado.setValue(cargaRegistro.getPesoLiquidoCarga());
                    if (this.PROCESSO.equals("MINUTA_GERAL")) {
                        txtNumNfv.setText("0");
                        txtSeqMin.setText("0");
                        txtObservacao.setText(" Produto " + cargaRegistro.getInfoMinuta() + "\n Transportadora : "
                                + cargaRegistro.getTransportadora().getCodigoTransportadora() + " - " + cargaRegistro.getTransportadora().getNomeTransportadora());
                        txtNomeMinuta.setText("TICKET BALANÇA " + txtCodigoTicket.getText() + " PESO " + txtPesoBalanca.getText());
                        popularMinutaGeral();
                    }

                    calcularSucata();

                } else {
                    Mensagem.mensagem("ERROR", "PESO NÃO ENCONTRADO");
                }
            } else {
                Mensagem.mensagem("ERROR", "PESO NÃO ENCONTRADO");
            }

        }

    }

   

    private void popularMinutaGeral() throws Exception {
        if (cargaRegistro != null) {
            if (cargaRegistro.getNumerocarga() > 0) {
                listminutaPedido = new ArrayList<>();
                MinutaPedido mi = new MinutaPedido();

                Cliente cli = new Cliente();
                cli.setCodigo_cliente(0);
                cli.setNome("MINUTA GERAL");
                cli.setEstado(" ");
                cli.setCidade(" ");
                mi.setCadCliente(cli);

                this.minuta.setUsu_libmot("G");
                this.minuta.setUsu_codemp(1);
                this.minuta.setUsu_codfil(1);
                mi.setCadMinuta(minuta);

                mi.setTipodocumento("NG");
                mi.setTipopedido("MG");
                mi.setTransacao("MG");
                mi.setUsu_codcli(0);
                mi.setUsu_codemp(1);
                mi.setUsu_codfil(1);
                mi.setUsu_codlan(0);
                mi.setUsu_codori("MG");
                mi.setUsu_codpes(Integer.valueOf(txtCodigoTicket.getText()));
                mi.setUsu_codsnf("MG");
                mi.setUsu_codtpr("");
                mi.setUsu_codtra(0);
                mi.setUsu_datemi(new Date());
                mi.setUsu_datlib(new Date());
                mi.setUsu_lansuc(0);
                mi.setUsu_numana(0);
                mi.setUsu_numnfv(0);
                mi.setUsu_numped(0);
                mi.setUsu_numpfa(0);
                mi.setUsu_obsmin(txtObservacao.getValue().toString());
                mi.setUsu_pesbal(txtPesoBalanca.getDouble());
                mi.setUsu_pesnfv(mi.getUsu_pesbal());
                mi.setUsu_pesped(mi.getUsu_pesbal());
                mi.setUsu_pesrec(0.0);
                mi.setUsu_pessalbal(0);
                mi.setUsu_pessld(0.0);
                mi.setUsu_pessuc(0.0);
                mi.setUsu_qtdfat(txtQtdy.getDouble());
                mi.setUsu_qtdped(1.0);
                mi.setUsu_qtdvol(txtQtdVolume.getDouble());
                mi.setUsu_salsuc(0.0);
                mi.setUsu_seqite(1);
                mi.setUsu_sitmin(txtSituacao.getSelectedItem().toString());
                mi.setUsu_ticbal(txtCodigoTicket.getText());
                mi.setUsu_tnspro("MG");

                listminutaPedido.add(mi);
                if (listminutaPedido != null) {
                    if (listminutaPedido.size() > 0) {
                        carregarTabelaMinutaNota();
                    }
                }
            }
        }

    }

    private void calcularSucata() {

        if (perren > 0 && txtQtdy.getDouble() > 0) {
            txtPesoSelecionado.setValue(txtQtdy.getDouble());
            double pesren = txtQtdy.getDouble() / (perren / 100);
            txtObservacao.setText("Ticket Balança: " + txtCodigoTicket.getText() + ""
                    + "\nPeso Chumbo: " + FormatarNumeros.converterDoubleDoisDecimais(txtPesoSelecionado.getDouble()) + ""
                    + "\n% Rentabilidade: " + perren + ""
                    + "\nPeso de sucata à baixar:  " + FormatarNumeros.converterDoubleDoisDecimais(pesren));

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
        jLabel7 = new javax.swing.JLabel();
        btnGravar = new javax.swing.JButton();
        txtQtdy = new org.openswing.swing.client.NumericControl();
        txtNomeMinuta = new org.openswing.swing.client.TextControl();
        txtSituacao = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        txtEmissao = new org.openswing.swing.client.DateControl();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtMinuta = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        txtEmpresa = new org.openswing.swing.client.TextControl();
        txtFilial = new org.openswing.swing.client.TextControl();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPesoSelecionado = new org.openswing.swing.client.NumericControl();
        jLabel5 = new javax.swing.JLabel();
        txtQtdVolume = new org.openswing.swing.client.NumericControl();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pPed = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTabPro = new javax.swing.JTable();
        txtObservacao = new org.openswing.swing.client.TextAreaControl();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtEmbalagem = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        btnEditar = new javax.swing.JButton();
        txtSeqMin = new org.openswing.swing.client.TextControl();
        btnVolumes = new javax.swing.JButton();
        btnEditarMinutaPedido = new javax.swing.JButton();
        btnInserirMinutaPedido = new javax.swing.JButton();
        btnQtdy = new javax.swing.JButton();
        txtCodigoTicket = new org.openswing.swing.client.TextControl();
        lblTicket = new javax.swing.JLabel();
        btnTicket = new javax.swing.JButton();
        txtPesoBalanca = new org.openswing.swing.client.NumericControl();
        btnExcluir = new javax.swing.JButton();
        txtNumNfv = new org.openswing.swing.client.TextControl();
        jLabel9 = new javax.swing.JLabel();
        btnFaturar = new javax.swing.JButton();

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

        jLabel7.setText("Descrição Minuta");

        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravar.setEnabled(false);
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        txtQtdy.setDecimals(2);
        txtQtdy.setEnabled(false);
        txtQtdy.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtQtdy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQtdyActionPerformed(evt);
            }
        });

        txtNomeMinuta.setEnabled(false);
        txtNomeMinuta.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNomeMinuta.setUpperCase(true);
        txtNomeMinuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeMinutaActionPerformed(evt);
            }
        });

        txtSituacao.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ABERTA", "LIBERADA", "EXCLUIDA", "CONCLUIDA" }));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        jButton2.setText("Sair");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        txtEmissao.setEnabled(false);
        txtEmissao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel8.setText("Emissão");

        jLabel10.setText("Volumes");

        jLabel12.setText("Quantidade");

        txtMinuta.setEnabled(false);
        txtMinuta.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel1.setText("Minuta");

        txtEmpresa.setEnabled(false);
        txtEmpresa.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtFilial.setEnabled(false);
        txtFilial.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel2.setText("Empresa");

        jLabel3.setText("Filial");

        txtPesoSelecionado.setDecimals(2);
        txtPesoSelecionado.setEnabled(false);
        txtPesoSelecionado.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel5.setText("Peso");

        txtQtdVolume.setEnabled(false);
        txtQtdVolume.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtQtdVolume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQtdVolumeActionPerformed(evt);
            }
        });

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Pedido", "Pré_fatura", "Analise", "Gerar", "Transação", "#", "Cliente", "Nome", "UF", "Cidade", "Data Pré", "Transporte", "Data Fat", "#", "Situação", "Peso", "Quantidade", "Situação", "Empresa", "Filial", "Emissão", "Agendado", "Dias Atrazo", "Transportadora", "ID", "SitNfv", "% Rentabilidade"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true
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
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(0);
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
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(50);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(50);
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
            jTableCarga.getColumnModel().getColumn(14).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(14).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(14).setMaxWidth(80);
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
            jTableCarga.getColumnModel().getColumn(20).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(20).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(20).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(21).setMinWidth(80);
            jTableCarga.getColumnModel().getColumn(21).setPreferredWidth(80);
            jTableCarga.getColumnModel().getColumn(21).setMaxWidth(80);
            jTableCarga.getColumnModel().getColumn(22).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(22).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(22).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(23).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(23).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(23).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(24).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(24).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(24).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(25).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(25).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(25).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(26).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(26).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(26).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(27).setMinWidth(150);
            jTableCarga.getColumnModel().getColumn(27).setPreferredWidth(150);
            jTableCarga.getColumnModel().getColumn(27).setMaxWidth(150);
        }

        javax.swing.GroupLayout pPedLayout = new javax.swing.GroupLayout(pPed);
        pPed.setLayout(pPedLayout);
        pPedLayout.setHorizontalGroup(
            pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 982, Short.MAX_VALUE)
            .addGroup(pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 982, Short.MAX_VALUE))
        );
        pPedLayout.setVerticalGroup(
            pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 183, Short.MAX_VALUE)
            .addGroup(pPedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pedidos", pPed);

        jTabPro.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Pedido", "Produto", "Descrição", "Entrega", "Quantidade", "Peso", "Peso Total", "Qtd Por Caixa", "Volume"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTabPro);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 982, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        jTabbedPane1.addTab("Produtos", jPanel1);

        txtObservacao.setEnabled(false);
        txtObservacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel11.setText("Observação");

        jLabel6.setText("Situação");

        txtEmbalagem.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtEmbalagem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE" }));

        jLabel4.setText("Embalagem");

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnEditar.setEnabled(false);
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        txtSeqMin.setEnabled(false);
        txtSeqMin.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        btnVolumes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Importar.png"))); // NOI18N
        btnVolumes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolumesActionPerformed(evt);
            }
        });

        btnEditarMinutaPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bug_link.png"))); // NOI18N
        btnEditarMinutaPedido.setEnabled(false);
        btnEditarMinutaPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarMinutaPedidoActionPerformed(evt);
            }
        });

        btnInserirMinutaPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnInserirMinutaPedido.setEnabled(false);
        btnInserirMinutaPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirMinutaPedidoActionPerformed(evt);
            }
        });

        btnQtdy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Importar.png"))); // NOI18N
        btnQtdy.setEnabled(false);
        btnQtdy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQtdyActionPerformed(evt);
            }
        });

        txtCodigoTicket.setEnabled(false);
        txtCodigoTicket.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtCodigoTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoTicketActionPerformed(evt);
            }
        });

        lblTicket.setText("Ticket Pesagem");

        btnTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/balanca_industrial.png"))); // NOI18N
        btnTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTicketActionPerformed(evt);
            }
        });

        txtPesoBalanca.setDecimals(2);
        txtPesoBalanca.setEnabled(false);
        txtPesoBalanca.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPesoBalanca.setTextAlignment(SwingConstants.RIGHT);
        txtPesoBalanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoBalancaActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cadeado.png"))); // NOI18N
        btnExcluir.setEnabled(false);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        txtNumNfv.setEnabled(false);
        txtNumNfv.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel9.setText("Nota Fiscal");

        btnFaturar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnFaturar.setEnabled(false);
        btnFaturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFaturarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(btnEditar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGravar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditarMinutaPedido)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInserirMinutaPedido)
                .addGap(12, 12, 12)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTicket)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCodigoTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTicket)
                .addGap(4, 4, 4)
                .addComponent(txtPesoBalanca, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(txtObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(txtQtdVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVolumes)
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(txtQtdy, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQtdy)
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(6, 6, 6))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNomeMinuta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(txtNumNfv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addGap(90, 90, 90)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtEmbalagem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(4, 4, 4))
                            .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(6, 6, 6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(txtSituacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEmissao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(btnExcluir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFaturar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSeqMin, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane1)
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton2, jLabel7});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel1)
                            .addComponent(jLabel9))
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNomeMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMinuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExcluir)
                            .addComponent(txtNumNfv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFaturar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtQtdVolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtQtdy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPesoSelecionado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEmissao, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnVolumes)
                                    .addComponent(btnQtdy)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTicket, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel11)
                                .addGap(5, 5, 5)
                                .addComponent(txtObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jTabbedPane1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton4)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnEditarMinutaPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnInserirMinutaPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtPesoBalanca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtCodigoTicket, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap())
                    .addComponent(txtSeqMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGravar, jButton2, jButton4});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtEmissao, txtSituacao});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnVolumes, txtQtdVolume});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnExcluir, txtMinuta});

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
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jTabbedCotacao.addTab("Minutas", pnlForm);

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

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            gerarRelatatorio(txtMinuta.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            sair();
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicaoGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtNomeMinutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeMinutaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeMinutaActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        try {
            if (ManipularRegistros.gravarRegistros(" Incluir  ")) {
                gravar();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex);
        }
    }//GEN-LAST:event_btnGravarActionPerformed

    private void txtQtdVolumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQtdVolumeActionPerformed
        txtObservacao.requestFocus();
    }//GEN-LAST:event_txtQtdVolumeActionPerformed

    private double perren = 0;

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        int linhaSelSit = jTableCarga.getSelectedRow();
        int colunaSelSit = jTableCarga.getSelectedColumn();
        txtCodigoTicket.setText("0");
        txtPesoBalanca.setText("0");
        txtSeqMin.setText(jTableCarga.getValueAt(linhaSelSit, 25).toString());
        if ((this.veioCampoSucataInd != null) || (this.veioCampoMetais != null)) {
            perren = Double.valueOf(jTableCarga.getValueAt(linhaSelSit, 27).toString());

            txtCodigoTicket.setEnabled(true);
            btnTicket.setEnabled(true);
            txtCodigoTicket.requestFocus();
        }
        if (!txtSeqMin.getText().isEmpty()) {
            try {
                if (evt.getClickCount() == 2 && PROCESSO.equals("MINUTA")) {
                    minutapedido = new MinutaPedido();
                    minutapedido = minutaPedidoDAO.getMinutaPedido(" seq ", "and usu_seqite = " + txtSeqMin.getText().trim());
                    if (minutapedido != null) {
                        if (minutapedido.getUsu_seqite() > 0) {
                            this.minutapedido.setCadMinuta(minuta);
                            btnEditarMinutaPedido.setEnabled(true);
                            btnEditarMinutaPedido.setText(" Sequencia " + this.minutapedido.getUsu_seqite());
                        }
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(frmMinutasExpedicaoGerar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        if (this.minuta.getUsu_codlan() > 0) {
            txtQtdVolume.setEnabled(true);
            txtEmbalagem.setEnabled(true);
            btnGravar.setEnabled(true);
            btnInserirMinutaPedido.setEnabled(true);
            btnEditarMinutaPedido.setEnabled(false);

        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnVolumesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolumesActionPerformed
        try {
            getPedidosProdutos();
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasExpedicaoGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnVolumesActionPerformed

    private void btnEditarMinutaPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarMinutaPedidoActionPerformed
        alterarDados();
    }//GEN-LAST:event_btnEditarMinutaPedidoActionPerformed

    private void btnInserirMinutaPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirMinutaPedidoActionPerformed
        this.minutapedido = new MinutaPedido();
        this.minutapedido.setCadMinuta(minuta);
        this.minutapedido.setUsu_numped(0);
        alterarDados();
    }//GEN-LAST:event_btnInserirMinutaPedidoActionPerformed

    private void btnQtdyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQtdyActionPerformed
        calcularSucata();
    }//GEN-LAST:event_btnQtdyActionPerformed

    private void txtQtdyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQtdyActionPerformed
        calcularSucata();
    }//GEN-LAST:event_txtQtdyActionPerformed

    private void txtCodigoTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoTicketActionPerformed
        try {
            saldoSucata();
        } catch (SQLException ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtCodigoTicketActionPerformed

    private void btnTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketActionPerformed
        try {
            saldoSucata();
        } catch (SQLException ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(frmMinutasGerar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnTicketActionPerformed

    private void txtPesoBalancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoBalancaActionPerformed
        // saldoSucata();
    }//GEN-LAST:event_txtPesoBalancaActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed

        if (ManipularRegistros.gravarRegistros(" cancelar ")) {
            try {
                cancelarMinuta("CANCELADA");
            } catch (SQLException ex) {
                Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnFaturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFaturarActionPerformed
         if (ManipularRegistros.gravarRegistros(" Baixar ")) {
            try {
                cancelarMinuta("FATURADA");
            } catch (SQLException ex) {
                Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(frmMinutasExpedicao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnFaturarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEditarMinutaPedido;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFaturar;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnInserirMinutaPedido;
    private javax.swing.JButton btnQtdy;
    private javax.swing.JButton btnTicket;
    private javax.swing.JButton btnVolumes;
    private javax.swing.JButton jButton2;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTabPro;
    private javax.swing.JTabbedPane jTabbedCotacao;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JLabel lblTicket;
    private javax.swing.JPanel pPed;
    private javax.swing.JPanel pnlForm;
    private org.openswing.swing.client.TextControl txtCodigoTicket;
    private javax.swing.JComboBox<String> txtEmbalagem;
    private org.openswing.swing.client.DateControl txtEmissao;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.TextControl txtFilial;
    private org.openswing.swing.client.TextControl txtMinuta;
    private org.openswing.swing.client.TextControl txtNomeMinuta;
    private org.openswing.swing.client.TextControl txtNumNfv;
    private org.openswing.swing.client.TextAreaControl txtObservacao;
    private org.openswing.swing.client.NumericControl txtPesoBalanca;
    private org.openswing.swing.client.NumericControl txtPesoSelecionado;
    private org.openswing.swing.client.NumericControl txtQtdVolume;
    private org.openswing.swing.client.NumericControl txtQtdy;
    private org.openswing.swing.client.TextControl txtSeqMin;
    private javax.swing.JComboBox<String> txtSituacao;
    // End of variables declaration//GEN-END:variables
}
