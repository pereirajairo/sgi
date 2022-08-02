/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Atendimento;
import br.com.sgi.bean.AtendimentoLigacao;
import br.com.sgi.bean.BasePasso;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Contas;
import br.com.sgi.bean.Motivo;
import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.bean.NotaFiscalItens;
import br.com.sgi.bean.PedidoHub;
import br.com.sgi.bean.SituacaoCliente;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.AtendimentoDAO;
import br.com.sgi.dao.AtendimentoLigacaoDAO;
import br.com.sgi.dao.ClienteDAO;
import br.com.sgi.dao.MotivoDaoDAO;
import br.com.sgi.dao.NotaFiscalDAO;
import br.com.sgi.dao.NotaFiscalItensDAO;
import br.com.sgi.dao.PedidoHubDAO;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ConversaoHoras;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSEmailAtendimento;
import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author jairosilva
 */
public final class CRMClientesAtendimento extends InternalFrame {

    private Cliente cliente;
    private ClienteDAO clienteDAO;
    private List<Cliente> lstCliente = new ArrayList<Cliente>();

    private AtendimentoLigacao atendimentoLigacao;
    private AtendimentoLigacaoDAO atendimentoLigacaoDAO;
    private List<AtendimentoLigacao> lstAtendimentoLigacao = new ArrayList<AtendimentoLigacao>();

    private List<Motivo> lstMotivo = new ArrayList<Motivo>();
    private Motivo motivo;

    private UtilDatas utilDatas;
    private CRMClientes veioCampo;
    private CRMClientesGeral veioCampoGeral;
    private CRMAgendas veioCampoAgenda;
    private Atendimento atendimento;
    private AtendimentoDAO atendimentoDAO;
    private Integer clientecodigo;
    private Integer lancamento;

    private List<PedidoHub> listPedidoHub = new ArrayList<PedidoHub>();
    private PedidoHubDAO pedidoHubDAO;

    public CRMClientesAtendimento() {
        try {
            initComponents();

            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }

            if (clienteDAO == null) {
                this.clienteDAO = new ClienteDAO();
            }

            if (atendimentoDAO == null) {
                this.atendimentoDAO = new AtendimentoDAO();
            }
            if (atendimentoLigacaoDAO == null) {
                this.atendimentoLigacaoDAO = new AtendimentoLigacaoDAO();
            }
            txtHoraLancamento.setVisible(false);
            txtDataCompra.setVisible(false);
            getPassos();
            getMotivos("L");
            getUsuarioLogado();
            DesabilitarTab();
            txtObservacaoDetalhada.setLineWrap(true);
            txtObservacaoDetalhada.setWrapStyleWord(true);

            txtOutrasMarcas.setLineWrap(true);
            txtOutrasMarcas.setWrapStyleWord(true);

            txtSolucao.setLineWrap(true);
            txtSolucao.setWrapStyleWord(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void desabilitarCampo(boolean acao) {
        btnAddLigacao.setEnabled(acao);
        txtContatoLigacao.setEnabled(acao);
        txtDataNovaVisita.setEnabled(acao);
        txtMotivo.setEnabled(acao);
        txtPassos.setEnabled(acao);
        txtObservacaoDetalhada.setEnabled(acao);
        txtOutrasMarcas.setEnabled(acao);
        txtSolucaoMotivo.setEnabled(false);
        txtEmailPara.setEnabled(acao);
        txtSolucao.setEnabled(false);
        btnGravarSolucao.setEnabled(false);
        txtPedido.setEnabled(false);
        btnFile.setEnabled(acao);
    }

    private Usuario usuario;

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        UsuarioERPDAO dao = new UsuarioERPDAO();

        usuario = dao.getUsuario(Menu.username.toLowerCase());
        return usuario;
    }

    private void getPassos() {
        BasePasso basePasso = new BasePasso();
        Map<String, String> mapas = basePasso.getPassos();
        for (String uf : mapas.keySet()) {
            txtPassos.addItem(mapas.get(uf));
        }
    }

    private void getMotivos(String acao) throws SQLException {
        MotivoDaoDAO dao = new MotivoDaoDAO();
        lstMotivo = dao.getMotivos();
        if (acao.equals("L")) {
            for (Motivo mot : lstMotivo) {
                if (mot.getCodigo() > 0) {
                    txtMotivo.addItem(mot.getCodigo() + "-" + mot.getDescricao());
                }
            }
        }

        if (acao.equals("S")) {
            for (Motivo mot : lstMotivo) {
                if (mot.getCodigo() > 0) {
                    txtSolucaoMotivo.addItem(mot.getCodigo() + "-" + mot.getDescricao());
                }
            }
        }
    }

    private void getSelecaoEmail(Contas contas) throws PropertyVetoException, Exception {
        CRMEmail sol = new CRMEmail();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        // sol.setMaximum(true); // executa maximizado 
        sol.setSize(600, 400);
        sol.setRecebePalavraEmailAtendimento(this);
    }

    private void getArquivos(String diretorio) throws PropertyVetoException, Exception {
        CRMArquivos sol = new CRMArquivos();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        // sol.setMaximum(true); // executa maximizado 
        sol.setSize(600, 400);
        sol.setRecebeArquivos(this, "X:\\ERBS\\CRM\\CLIENTE\\" + txtCodigo.getText() + "\\", txtCodigo.getText(), txtNome.getText());
    }

    private void getNota(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException {
        NotaFiscal nf = new NotaFiscal();
        NotaFiscalDAO dao = new NotaFiscalDAO();
        nf = dao.getNotaFiscalCarteira(PESQUISA_POR, PESQUISA, DATA);
        if (nf != null) {
            if (nf.getEmpresa() >= 0) {
                txtEstado.setText(cliente.getEstado());
                txtDataCompra.setDate(nf.getEmissao());
                this.cliente.setSituacao(nf.getSituacao());
            } else {
                this.cliente.setSituacao("SEM COMPRA");
            }
        }

    }

    private void getNotaItens(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException, Exception {
        NotaFiscalItensDAO dao = new NotaFiscalItensDAO();
        List<NotaFiscalItens> lstNotaItens = new ArrayList<NotaFiscalItens>();
        lstNotaItens = dao.getNotaFiscalItens(PESQUISA_POR, PESQUISA, DATA);
        if (lstNotaItens != null) {
            carregarTabelaItens(lstNotaItens);
        }

    }

    public void carregarTabelaItens(List<NotaFiscalItens> lstNotaItens) throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableItens.getModel();
        modeloCarga.setNumRows(0);
        jTableItens.setRowHeight(40);
        jTableItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon CreIcon = getImage("/images/sitBom.png");

        for (NotaFiscalItens cli : lstNotaItens) {
            Object[] linha = new Object[5];
            TableColumnModel columnModel = jTableItens.getColumnModel();
            CRMClientesAtendimento.JTableRenderer renderers = new CRMClientesAtendimento.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = CreIcon;

            linha[1] = cli.getQuantidade();
            linha[2] = cli.getMes() + " " + cli.getAno();
            linha[3] = cli.getCadProduto().getDescricaoproduto();
            modeloCarga.addRow(linha);
        }

    }

    private void getSituacaoCliente(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException, Exception {
        NotaFiscalItensDAO dao = new NotaFiscalItensDAO();
        List<SituacaoCliente> lst = new ArrayList<SituacaoCliente>();
        lst = dao.getSituacoes(PESQUISA_POR, PESQUISA, DATA);
        if (lst != null) {
            if (lst.size() <= 0) {
                SituacaoCliente sit = new SituacaoCliente();
                sit.setDescricao("INATIVO");
                sit.setCodigo("SEM COMPRAS");
                lst = new ArrayList<SituacaoCliente>();

                lst.add(sit);
            }
            carregarTabelaSituacao(lst);
        }

    }

    public void carregarTabelaSituacao(List<SituacaoCliente> lst) throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableItens.getModel();
        modeloCarga.setNumRows(0);
        jTableItens.setRowHeight(40);
        jTableItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon CreIcon = getImage("/images/sitBom.png");
        ImageIcon InaIcon = getImage("/images/sitRuim.png");
        ImageIcon PreIcon = getImage("/images/sitMedio.png");
        for (SituacaoCliente cli : lst) {
            Object[] linha = new Object[5];
            TableColumnModel columnModel = jTableItens.getColumnModel();
            CRMClientesAtendimento.JTableRenderer renderers = new CRMClientesAtendimento.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            if (cli.getDescricao().equals("INATIVO")) {
                linha[0] = InaIcon;
                txtSituacao.setSelectedItem("INATIVO");
            } else {
                if (cli.getDescricao().equals("ATIVO")) {
                    linha[0] = CreIcon;
                    txtSituacao.setSelectedItem("ATIVO");
                } else {
                    linha[0] = PreIcon;
                    txtSituacao.setSelectedItem("PRÉ-INATIVO");
                }

            }

            linha[1] = cli.getDescricao();
            linha[2] = cli.getDias_ultimo_faturamento();
            linha[3] = cli.getEmissaoS();
            linha[4] = cli.getCodigo();
            modeloCarga.addRow(linha);
        }

    }

    private void getLancamento(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException, Exception {
        List<Atendimento> lst = new ArrayList<Atendimento>();
        lst = atendimentoDAO.getAtendimentos(PESQUISA_POR, PESQUISA);
        if (lst != null) {
            carregarTabelaAtendimento(lst);
        }

    }

    public void carregarTabelaAtendimento(List<Atendimento> lst) throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableLancamento.getModel();
        modeloCarga.setNumRows(0);
        jTableLancamento.setRowHeight(40);
        jTableLancamento.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon CreIcon = getImage("/images/sitRuim.png");
        ImageIcon sitSai = getImage("/images/sitBom.png");
        String situacao = "";
        for (Atendimento cli : lst) {
            Object[] linha = new Object[10];
            TableColumnModel columnModel = jTableLancamento.getColumnModel();
            CRMClientesAtendimento.JTableRenderer renderers = new CRMClientesAtendimento.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            if (!cli.getSituacaoobservacao().equals("R")) {
                linha[0] = CreIcon;
                situacao = "SEM SOLUÇÃO";
            } else {
                linha[0] = sitSai;
                situacao = "OK";
            }
            linha[1] = cli.getSequencialancamento();
            linha[2] = cli.getMotivoobservacao() + " - " + cli.getMotivo().getDescricao();
            linha[3] = cli.getSituacaoobservacao();
            linha[4] = cli.getDatalancamento();
            linha[5] = cli.getDatavista();
            linha[6] = situacao;
            modeloCarga.addRow(linha);
        }

    }

    private void getPedidos(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        this.pedidoHubDAO = new PedidoHubDAO();
        listPedidoHub = this.pedidoHubDAO.getPedidoClientes(PESQUISA_POR, PESQUISA);
        if (listPedidoHub != null) {
            carregarTabela();
        }
    }

    public void carregarTabela() throws Exception {

        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableCarga.getModel();
        modeloCarga.setNumRows(0);
        ImageIcon BomIcon = getImage("/images/sitBom.png");
        ImageIcon RuimIcon = getImage("/images/sitRuim.png");
        ImageIcon AndaIcon = getImage("/images/sitAnd.png");
        ImageIcon HubIcon = getImage("/images/sitMedio.png");

        ImageIcon MotIcon = getImage("/images/moto_erbs.png");
        ImageIcon AutIcon = getImage("/images/carros.png");

        double peso = 0;
        double peso_sucata = 0;
        double qtdy = 0;
        double qtdy_bat = 0;
        double qtdy_com = 0;

        double qtdped = 0;

        for (PedidoHub ped : listPedidoHub) {
            Object[] linha = new Object[23];
            TableColumnModel columnModel = jTableCarga.getColumnModel();

            // TableCellRenderer renderer = new PedidoEmbarque.ColorirRenderer();
            CRMClientesAtendimento.JTableRenderer renderers = new CRMClientesAtendimento.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            columnModel.getColumn(9).setCellRenderer(renderers);
            linha[0] = AutIcon;
            if (ped.getLinha().equals("BM")) {
                linha[0] = MotIcon;
            }

            linha[1] = ped.getPedido();
            linha[2] = ped.getCliente();
            linha[3] = ped.getCadCliente().getNome();
            linha[4] = ped.getCadCliente().getEstado();
            linha[5] = ped.getCadCliente().getCidade();
            linha[6] = ped.getDatapedidoS();
            linha[7] = ped.getDiatransporte();
            linha[9] = AndaIcon;
            if (ped.getSituacao().equals("FATURADO")) {
                linha[9] = BomIcon;

            }

            linha[8] = ped.getDatafaturarS();
            linha[10] = false;
            linha[11] = ped.getSituacao();
            linha[12] = ped.getPesopedido();
            if (ped.getPesopedido() > 0) {
                peso += ped.getPesopedido();
            }

            linha[13] = ped.getQuantidade();
            if (ped.getQuantidade() > 0) {
                qtdy_bat += ped.getQuantidade();
            }

            linha[15] = ped.getFilial();
            if (ped.getCadNotaFiscal() != null) {
                linha[14] = ped.getCadNotaFiscal().getNotafiscal();
                linha[16] = ped.getCadNotaFiscal().getEmissaoS();

            }
            linha[17] = ped.getCadNotaFiscal().getSerie();
            linha[18] = ped.getTransacao();
            linha[19] = ped.getTransportadora() + " - " + ped.getCadTransportadora().getNomeTransportadora();
            linha[21] = ped.getSucata_id();
            if (ped.getSucata_id() > 0) {
                peso_sucata += ped.getPesopedido();
            }
            linha[22] = ped.getPesopedido();
            qtdped++;
            modeloCarga.addRow(linha);
        }
//        lblPeso.setText(FormatarPeso.mascaraPorcentagem(peso, FormatarPeso.PORCENTAGEM));
//        lblQtdy.setText(FormatarPeso.mascaraPorcentagem(qtdy_bat, FormatarPeso.PORCENTAGEM));
//        lblQtdyPedido.setText(FormatarPeso.mascaraPorcentagem(qtdped, FormatarPeso.PORCENTAGEM));
//        lblPesoSucata.setText(FormatarPeso.mascaraPorcentagem(peso_sucata, FormatarPeso.PORCENTAGEM));

    }

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

        // jTableCarga.getColumnModel().getColumn(9).setCellRenderer(direita);
        //jTableCarga.getColumnModel().getColumn(10).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(12).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(13).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(14).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(15).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(16).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(17).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(18).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(20).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(21).setCellRenderer(direita);
        jTableCarga.getColumnModel().getColumn(22).setCellRenderer(direita);
        jTableCarga.setRowHeight(40);
        jTableCarga.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCarga.setAutoResizeMode(0);
        jTableCarga.setAutoCreateRowSorter(true);

    }

    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }
    private boolean ligacaoCliente;

    public void setRecebePalavra(CRMClientes veioInput, Cliente cliente, String PROCESSO,
            String SITUACAO,
            String AUTOMOTO) throws Exception {
        this.veioCampo = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Cadastro de Ocorrências"));
        this.cliente = cliente;
        if (cliente != null) {
            if (cliente.getCodigo() > 0) {
                if (cliente.getCodigo() == 0) {
                    cliente.setGruponome("NÃO INFORMADAO");
                }
            }
        }
        popularTela(cliente);

        optAuto.setSelected(false);
        optAuto.setSelected(false);

        if (AUTOMOTO.equals("AUTO")) {
            optAuto.setSelected(true);
        }
        if (AUTOMOTO.equals("MOTO")) {
            optAuto.setSelected(true);
        }

        txtSituacaoCliente.setText(SITUACAO);

        getNota("CLIENTE", " and E140NFV.codcli = " + cliente.getCodigo_cliente(), new Date());
        //  getNotaItens("CLIENTE", " and E140NFV.codcli = " + cliente.getCodigo_cliente(), new Date());
        getSituacaoCliente("CLIENTE", " and a.codcli = " + cliente.getCodigo_cliente(), new Date());
        getLancamento("CLIENTE", " and obs.codcli = " + cliente.getCodigo_cliente(), new Date());
        desabilitarCampo(false);
        addNewReg = true;

        carregarLigacoes("", " and usu_codcli = " + txtSequenciaLancamento.getText());
        ligacaoCliente = true;
        txtContatoLigacao.requestFocus();
    }

    public void setRecebePalavraGeral(CRMClientesGeral veioInput, Cliente cliente, String PROCESSO,
            String SITUACAO,
            String AUTOMOTO) throws Exception {
        this.veioCampoGeral = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Cadastro de Ocorrências"));
        this.cliente = cliente;
        if (cliente != null) {
            if (cliente.getCodigo() > 0) {
                if (cliente.getCodigo() == 0) {
                    cliente.setGruponome("NÃO INFORMADAO");
                }
            }
        }
        popularTela(cliente);

        optAuto.setSelected(false);
        optAuto.setSelected(false);

        if (AUTOMOTO.equals("AUTO")) {
            optAuto.setSelected(true);
        }
        if (AUTOMOTO.equals("MOTO")) {
            optAuto.setSelected(true);
        }

        txtSituacaoCliente.setText(SITUACAO);

        getNota("CLIENTE", " and E140NFV.codcli = " + cliente.getCodigo_cliente(), new Date());
        //  getNotaItens("CLIENTE", " and E140NFV.codcli = " + cliente.getCodigo_cliente(), new Date());
        getSituacaoCliente("CLIENTE", " and a.codcli = " + cliente.getCodigo_cliente(), new Date());
        getLancamento("CLIENTE", " and obs.codcli = " + cliente.getCodigo_cliente(), new Date());
        desabilitarCampo(false);
        addNewReg = true;

        carregarLigacoes("", " and usu_codcli = " + txtSequenciaLancamento.getText());
        ligacaoCliente = true;
        txtContatoLigacao.requestFocus();
    }

    public void setRecebePalavraAgendas(CRMAgendas veioInput, Cliente cliente, String PROCESSO) throws Exception {
        this.veioCampoAgenda = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Cadastro de Ocorrências"));
        this.cliente = cliente;
        popularTela(cliente);

        getNota("CLIENTE", " and E140NFV.codcli = " + cliente.getCodigo_cliente(), new Date());

        getSituacaoCliente("CLIENTE", " and a.codcli = " + cliente.getCodigo_cliente(), new Date());
        getLancamento("CLIENTE", " and obs.codcli = " + cliente.getCodigo_cliente(), new Date());
        desabilitarCampo(false);
        addNewReg = true;
        txtContatoLigacao.requestFocus();

        carregarLigacoes("", " and usu_codcli = " + txtCodigo.getText());
        ligacaoCliente = true;

    }

    private void popularTela(Cliente cliente) {
        txtCodigo.setText(cliente.getCodigo_cliente().toString());
        txtNome.setText(cliente.getNome());
        txtDataLancamento.setDate(new Date());
        // txtDataNovaVisita.setDate(new Date());
        txtHoraLancamento.setText(utilDatas.retornarHoras(new Date()));
        txtCidade.setText(cliente.getCidade());
        txtTelefones.setText(cliente.getTelefone());

        txtGrupoEmpresa.setText(cliente.getGrupocodigo() + " " + cliente.getGruponome());

        if (cliente.getGrupocodigo().equals("0")) {
            txtGrupoEmpresa.setText(cliente.getCadClienteGrupo().getCodgrp() + " " + cliente.getCadClienteGrupo().getNome());
        }

        txtCodRep.setText(cliente.getCadRepresentante().getCodigo().toString());
        txtNomRep.setText(cliente.getCadRepresentante().getNome());

        txtCodVen.setText(cliente.getCadVendedor().getCodigo().toString());
        txtNomVen.setText(cliente.getCadVendedor().getNome());

        if (cliente.getData_ultimo_faturamento() == null) {
            txtSituacao.setSelectedItem("SEM COMPRA");
        }
        if (cliente.getDias_ultimo_faturamento() > 90) {
            txtSituacao.setSelectedItem("INATIVO");
        }

        if (cliente.getDias_ultimo_faturamento() <= 90) {
            txtSituacao.setSelectedItem("ATIVO");
        }

    }
    private boolean addNewReg;

    private void novoAtendimento(boolean addReg, String tipo) throws ParseException {
        if (addReg) {
            desabilitarCampo(addReg);
            popularTela(cliente);
            atendimento = new Atendimento();
            txtMotivo.setSelectedItem("0-SELECIONE");
            txtMotivo.setEnabled(true);
            atendimento.setObservacaodetalhada("1) POR QUAL MOTIVO O SENHOR DEIXOU DE COMPRAR? \n\n\n"
                    + "2) QUAL MARCA O SENHOR ESTÁ TRABALHANDO? \n"
                    + "    MOURA, HELIAR, PIONEIRO, CRAL,BOSH, ACDELCO\n\n"
                    + "3) QUANTAS BATERIAS O SENHOR COSTUMA VENDER? \n\n\n"
                    + "4) O SENHOR SABIA QUE A ERBS TAMBÉM É DE PRIMEIRA LINHA? \n\n\n"
                    + "5) O SENHOR JÁ FEZ A COMPARAÇÃO DE CUSTO E BENEFÍCIOS DE TER NOSSAS BATERIAS ? \n\n\n");
            if (tipo.equals("POS")) {
                atendimento.setObservacaodetalhada("1 - A entrega ocorreu dentro do esperado ?\n\n"
                        + "2 - As Baterias chegaram conforme o pedido?\n\n"
                        + "3 - A qualidade do nosso produto está conforme sua necessidade?\n\n"
                        + "4 - Qual o Principal ponto positivo em trabalhar com a Erbs?\n\n"
                        + "5 - O que pomos evoluir para melhor atender sua empresa?\n\n");
                txtMotivo.setSelectedItem("620-PÓS-VENDA");
                txtMotivo.setEnabled(false);
            }
            txtObservacaoDetalhada.setText(atendimento.getObservacaodetalhada());
            String data = this.utilDatas.converterDateToStr(new Date());
            atendimento.setObservacao("Atendimento gerado pelo Atendente : " + this.usuario.getNome() + " Data: " + data);
            txtObservacaoAtendimento.setText(atendimento.getObservacao());
            //  atendimento.setConcorrecia("MOURA, HELIAR, PIONEIRO, TUDOR, BOSCH");
            txtOutrasMarcas.setText("INFORME");
            txtSequenciaLancamento.setText("0");

            txtPassos.setSelectedItem("1-REGISTRO");
            txtContatoLigacao.setText("");
            btnGravar.setEnabled(true);
            this.addNewReg = addReg;
            HabilitarTab();
            txtMotivo.requestFocus();
            btnFile.setEnabled(false);

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

    private void gravarRegistro() throws SQLException, Exception {
        this.ligacaoCliente = false;
        if (addNewReg) { // inserir registro
            if (validarCampos()) {
                atendimento = new Atendimento();
                popularCampo();
                atendimento.setSequencialancamento(this.atendimentoDAO.proxCodCad(cliente.getCodigo_cliente()));
                if (atendimento.getSequencialancamento() == 0) {
                    atendimento.setSequencialancamento(1);
                }
                txtSequenciaLancamento.setText(String.valueOf(atendimento.getSequencialancamento()));
                if (!atendimentoDAO.inserir(atendimento)) {

                } else {
                    this.addNewReg = false;
                    btnGravar.setEnabled(false);
                    getLancamento("CLIENTE", " and obs.codcli = " + cliente.getCodigo_cliente(), new Date());
                    if (!txtEmailPara.getText().isEmpty()) {
                        WSEmailAtendimento wsEma = new WSEmailAtendimento();
                        wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
                    }
                    btnSolucao.setEnabled(false);
                    if (this.atendimento.getSequencialancamento() > 0) {
                        btnSolucao.setEnabled(true);
                    }

                    this.gravarligacao = true;
                    salvarLigacao();
                    if (!txtSequenciaLancamento.getText().equals("0")) {
                        btnFile.setEnabled(true);
                    }

                }
            }
        } else { // aletar registro
            popularCampo();
            if (validarCampos()) {
                if (!atendimentoDAO.alterar(atendimento)) {

                } else {
                    this.addNewReg = false;
                    btnGravar.setEnabled(false);
                    getLancamento("CLIENTE", " and obs.codcli = " + cliente.getCodigo_cliente(), new Date());
                    if (!txtEmailPara.getText().isEmpty()) {
                        WSEmailAtendimento wsEma = new WSEmailAtendimento();
                        wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
                    }
                    btnSolucao.setEnabled(false);
                    if (this.atendimento.getSequencialancamento() > 0) {
                        btnSolucao.setEnabled(true);
                    }

                }
            }

        }

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

    private void popularCampo() throws ParseException {

        atendimento.setCliente(cliente);
        atendimento.setCodigocliente(cliente.getCodigo_cliente());
        atendimento.setTabelapreco("0");
        atendimento.setConcorrecia("");
        atendimento.setContatoobservacao(txtContatoLigacao.getText());
        if (txtDataNovaVisita.getValue() != null) {
            atendimento.setDataproximavisita(txtDataNovaVisita.getDate());
        } else {
            atendimento.setDataproximavisita(null);
        }

        atendimento.setEmpresacliente(0);
        atendimento.setIndexp(0);
        atendimento.setLancamentodata(new Date());
        atendimento.setLancamentohora(CalculoHora(0));

        if (usuario.getId() != null) {
            if (atendimento.getLancamentousuario() > 0) {

            } else {
                atendimento.setLancamentousuario(usuario.getId());
            }

        } else {
            atendimento.setLancamentousuario(0);
        }

        String cod = txtMotivo.getSelectedItem().toString();
        int index = cod.indexOf("-");
        String codcon = "0";
        if (index > 0) {
            codcon = cod.substring(0, index);
            atendimento.setMotivoobservacao(Integer.valueOf(codcon));
        }

        atendimento.setObservacao(txtObservacaoAtendimento.getText());
        atendimento.setObservacaodetalhada(txtObservacaoDetalhada.getText());
        atendimento.setObservacaotipo("M");
        atendimento.setOrigemcliente(0);
        atendimento.setSequenciacontato(0);
        atendimento.setOrigemsequencia(0);
        atendimento.setPedido(0);
        atendimento.setPedidoempresa(0);
        atendimento.setPedidofilial(0);
        cod = txtPassos.getSelectedItem().toString();
        index = cod.indexOf("-");
        if (index > 0) {
            codcon = cod.substring(0, index);
            atendimento.setProximopasso(Integer.valueOf(codcon));
        }

        atendimento.setSequenciaitempedido(0);
        atendimento.setSolucao("");
        atendimento.setSituacaoobservacao("G");
        atendimento.setOutramarcas(txtOutrasMarcas.getText());
        if (!txtEmailPara.getText().isEmpty()) {
            atendimento.setEnviarEmail("S");
            atendimento.setEmail(txtEmailPara.getText());
        }

        atendimento.setSituacao(txtSituacaoCliente.getText());

        if (optAuto.isSelected()) {
            atendimento.setAutomoto("AUTO");
        }
        if (optMoto.isSelected()) {
            atendimento.setAutomoto("MOTO");
        }

    }

    private boolean validarCampos() {
        boolean retorno = true;
        String selecao = "";
        if (optAuto.isSelected()) {
            selecao = "AUTO";
        }
        if (optMoto.isSelected()) {
            selecao = "MOTO";
        }

        if (txtMotivo.getSelectedItem().equals("0-SELECIONE")) {
            JOptionPane.showMessageDialog(null, "Motivo obrigatório ",
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            txtMotivo.requestFocus();
            retorno = false;

        } else {
            if (txtContatoLigacao.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Contato é obrigatório ",
                        "Erro:", JOptionPane.ERROR_MESSAGE);
                txtMotivo.requestFocus();
                retorno = false;
            } else if (txtSituacaoCliente.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione a situação do cliente. \n"
                        + " Clique sobre a situação  na tabela abaixo ",
                        "Erro:", JOptionPane.ERROR_MESSAGE);
                txtMotivo.requestFocus();
                retorno = false;
            } else if (selecao.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione a linha do cliente Auto/Moto ",
                        "Erro:", JOptionPane.ERROR_MESSAGE);
                txtMotivo.requestFocus();
                retorno = false;
            }
        }

        return retorno;

    }

    private void getMotivo(Integer id) throws SQLException {
        btnGravar.setEnabled(false);
        this.addNewReg = false;
        if (id > 0) {
            getMotivos("L");
            getMotivos("S");
            btnSolucao.setEnabled(false);
            this.atendimento = new Atendimento();
            this.atendimento = atendimentoDAO.getAtendimento("", " and obs.codcli =" + cliente.getCodigo_cliente() + " and seqobs =" + id);
            if (this.atendimento != null) {
                if (this.atendimento.getSequencialancamento() > 0) {
                    desabilitarCampo(true);
                    btnSolucao.setEnabled(true);
                    txtSequenciaLancamento.setText(atendimento.getSequencialancamento().toString());
                    txtObservacaoDetalhada.setText(atendimento.getObservacaodetalhada());
                    txtObservacaoAtendimento.setText(atendimento.getObservacao());
                    txtOutrasMarcas.setText(atendimento.getOutramarcas());
                    txtMotivo.setSelectedItem(atendimento.getMotivoobservacao() + "-" + atendimento.getMotivo().getDescricao().trim());
                    txtDataLancamento.setDate(atendimento.getLancamentodata());
                    txtContatoLigacao.setText(atendimento.getContatoobservacao());
                    if (atendimento.getProximopasso() == 1) {
                        txtPassos.setSelectedItem("1-REGISTRO");
                    } else {
                        if (atendimento.getProximopasso() == 2) {
                            txtPassos.setSelectedItem("2-FAZER PROPOSTA");
                        } else if (atendimento.getProximopasso() == 2) {
                            txtPassos.setSelectedItem("3-GERAR PEDIDO");
                        } else if (atendimento.getProximopasso() == 3) {
                            txtPassos.setSelectedItem("4-LIGAR NOVAMENTE");
                        }
                    }
                    if (atendimento.getDataproximavisita() != null) {
                        txtDataNovaVisita.setDate(atendimento.getDataproximavisita());
                    } else {
                        txtDataNovaVisita.setDate(null);
                    }

                    txtSolucao.setText(atendimento.getSolucao());
                    txtDataSolucao.setDate(atendimento.getSolucaodata());
                    txtPedido.setText(atendimento.getPedido().toString());

                    Motivo mot = new Motivo();
                    MotivoDaoDAO dao = new MotivoDaoDAO();

                    mot = dao.getMotivo("", " and codmot = " + atendimento.getSolucaomotivo());
                    txtSolucaoMotivo.setSelectedItem("0-SELECIONE");
                    if (mot != null) {
                        if (mot.getCodigo() > 0) {
                            txtSolucaoMotivo.setSelectedItem(atendimento.getSolucaomotivo() + "-" + mot.getDescricao());

                        } else {

                        }
                    }

                    // txtSituacaoCliente.setText(atendimento.getSituacao());
                    optAuto.setSelected(false);
                    optMoto.setSelected(false);
                    if (atendimento.getAutomoto() != null) {
                        if (atendimento.getAutomoto().equals("AUTO")) {
                            optAuto.setSelected(true);
                        }
                        if (atendimento.getAutomoto().equals("MOTO")) {
                            optMoto.setSelected(true);
                        }
                    }

                    HabilitarTab();
                    this.addNewReg = false;
                    btnGravar.setEnabled(true);

                    carregarLigacoes("", " and usu_codcli = " + txtCodigo.getText() + " \n"
                            + " and usu_codmot = " + txtSequenciaLancamento.getText());
                }
            }
        }
    }

    private void gravarSolucao() throws SQLException, Exception {

        if (txtCodigo.getText().equals("0")) {
            JOptionPane.showMessageDialog(null, "ERRO: Problema de cadastro. Procure a TI ",
                    "Atenção", JOptionPane.ERROR_MESSAGE);
        } else if (txtSequenciaLancamento.getText().equals("0")) {
            JOptionPane.showMessageDialog(null, "ERRO: Problema de cadastro. Procure a TI ",
                    "Atenção", JOptionPane.ERROR_MESSAGE);
        } else if (!txtSolucao.getText().isEmpty()) {
            String cod = txtSolucaoMotivo.getSelectedItem().toString();
            int index = cod.indexOf("-");
            String codcon = cod.substring(0, index);
            if (codcon.equals("0")) {
                JOptionPane.showMessageDialog(null, "ERRO: Informe o código da  solução ",
                        "Atenção", JOptionPane.ERROR_MESSAGE);
                return;

            } else {
                atendimento.setSolucaomotivo(Integer.valueOf(codcon));
                if (usuario.getId() != null) {
                    atendimento.setSolucaousuario(usuario.getId());
                } else {
                    atendimento.setSolucaousuario(0);
                }

                atendimento.setSolucao(txtSolucao.getText());
                atendimento.setSolucaodata(txtDataSolucao.getDate());
                atendimento.setSolucaohora(CalculoHora(0));
                atendimento.setPedido(0);
                if (txtPedido.getValue() != null) {
                    atendimento.setPedido(Integer.valueOf(txtPedido.getText()));
                }

                if (!atendimentoDAO.gravarSolucao(atendimento)) {

                } else {
                    getLancamento("CLIENTE", " and obs.codcli = " + cliente.getCodigo_cliente(), new Date());
                    btnGravarSolucao.setEnabled(false);
                    txtSolucao.requestFocus();

                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "ERRO: Informe a solução ",
                    "Atenção", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void novaSolucao() throws SQLException {
        txtSolucao.setText("");
        txtPedido.setText("0");
        txtSolucao.setEnabled(true);
        txtSolucaoMotivo.setEnabled(true);
        txtDataSolucao.setDate(new Date());
        txtPedido.setEnabled(true);
        txtSolucaoMotivo.setEnabled(true);
        btnGravarSolucao.setEnabled(true);

        getMotivos("S");

    }

    public void selecionarArquivo(String tipo) throws SQLException, Exception {

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Arquivos *.pdf", "pdf"));
        fc.setCurrentDirectory(new File("c:\\"));
        fc.setDialogTitle("Buscar arquivo ");

        int option = fc.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                System.out.println("Arquivo selecionado: " + file.getCanonicalPath());

                File files = new File("X:\\ERBS\\CRM\\CLIENTE\\" + file.getName());
                files.delete();

                CopiarArquivos(file.getPath(), file.getName(), tipo);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        null, "Não foi possivel carregar o arquivo " + ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Não foi possivel carregar a foto");
        }
    }

    private String caminho;
    private FileInputStream origem;
    private FileOutputStream destino;

    public final void CopiarArquivos(String caminho, String nomfile, String tipo) throws SQLException, Exception {

        String nome;
        nome = JOptionPane.showInputDialog("Digite o nome do arquivo:");

        if (!nome.isEmpty()) {
            boolean registrar = true;
            this.caminho = caminho;
            String caminhoDestino = null;

            caminhoDestino = "X:\\ERBS\\CRM\\CLIENTE\\" + txtCodigo.getText() + "\\";

            File diretorio = new File(caminhoDestino); // ajfilho é uma pasta!
            if (!diretorio.exists()) {
                diretorio.mkdir(); //cria somente um diretório, 
            } else {
                System.out.println("Diretório já existente");
            }
            String extension = "";

            int i = nomfile.lastIndexOf('.');
            int p = Math.max(nomfile.lastIndexOf('/'), nomfile.lastIndexOf('\\'));

            if (i > p) {
                extension = nomfile.substring(i + 1);
            }
            Date dataAtual = new Date();
            DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
            String dataFormatada = dateFormat.format(dataAtual);
            nomfile = txtCodigo.getText() + "_" + txtSequenciaLancamento.getText();
            try {
                this.origem = new FileInputStream(caminho);
                this.destino = new FileOutputStream(caminhoDestino + nome.toUpperCase() + "_" + nomfile + "_" + dataFormatada + "." + extension);
                IOUtils.copy(this.origem, this.destino);
                this.origem.close();
                this.destino.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
                registrar = false;

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
                registrar = false;

            } catch (Error e) {
                JOptionPane.showMessageDialog(null, e, "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
                registrar = false;

            }

            if (registrar) {
                JOptionPane.showMessageDialog(null, nomfile, "enviado com sucesso!", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Informe o nome do arquivo", "Ocorreu um erro!", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void DesabilitarTab() {
        desabilitarCampo(false);
        jTabbedPane.setEnabledAt(0, true);
        jTabbedPane.setEnabledAt(1, false);
        jTabbedPane.setEnabledAt(2, false);
        jTabbedPane.setEnabledAt(3, false);

        jTabbedPane.setSelectedIndex(0);
        btnSelecionar.setEnabled(false);
        btnFile.setEnabled(false);
        txtSequenciaLancamento.setText("0");

    }

    public void HabilitarTab() {
        jTabbedPane.setEnabledAt(0, true);
        jTabbedPane.setEnabledAt(1, true);
        jTabbedPane.setEnabledAt(2, true);
        jTabbedPane.setSelectedIndex(1);
        txtContatoLigacao.requestFocus();
    }

    void retornarEmail(String email) {
        txtEmailPara.setText("");
        if (!email.isEmpty()) {
            txtEmailPara.setText(email);
        }
    }

    private void novaLigacao(boolean gravarligacao) throws Exception {

        if (!txtCodigo.getText().equals("0")) {
            if (!txtSequenciaLancamento.getText().equals("0")) {

                getUsuarioLogado();
                CRMLigacao sol = new CRMLigacao();
                MDIFrame.add(sol, true);
                sol.setSize(600, 400);
                sol.setPosicao();
                sol.setRecebePalavra(this, txtCodigo.getText(), txtNome.getText(),
                        txtSequenciaLancamento.getText(),
                        this.usuario, gravarligacao, this.atendimentoLigacao, this.ligacaoCliente, "CLI");

                if (ligacaoCliente) {
                    btnAddLigacao.setEnabled(false);
                }

            }
        }

    }

    private boolean gravarligacao;

    private void buscarLicacao(Integer lancamento, Integer atendimento) throws SQLException, Exception {
        if ((lancamento > 0) && (atendimento > 0)) {
            atendimentoLigacao = new AtendimentoLigacao();
            atendimentoLigacao = this.atendimentoLigacaoDAO.getAtendimentoLigacao("", " and usu_codlan = " + lancamento + " and usu_codmot = " + atendimento);
            if (atendimentoLigacao != null) {
                if (atendimentoLigacao.getCodigolancamento() > 0) {

                    novaLigacao(false);

                }
            }
        }
    }

    public void retornarLigacao(String PESQUISA, String PESQUISA_POR) throws SQLException {
        carregarLigacoes(PESQUISA, PESQUISA_POR);

    }

    private void carregarLigacoes(String PESQUISA, String PESQUISA_POR) throws SQLException {
        PESQUISA_POR += " and usu_tipcon = 'CLI' \n";
        lstAtendimentoLigacao = this.atendimentoLigacaoDAO.getAtendimentoLigacaos(PESQUISA, PESQUISA_POR);
        btnLigacoes.setText("0");
        if (lstAtendimentoLigacao != null) {
            tabelaLigacoes();
            btnLigacoes.setText(String.valueOf(lstAtendimentoLigacao.size()));
        }
    }

    private void tabelaLigacoes() {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableLigacoes.getModel();
        modeloCarga.setNumRows(0);
        jTableLigacoes.setRowHeight(40);
        jTableLigacoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon CreIcon = getImage("/images/Telefone.png");
        for (AtendimentoLigacao cli : lstAtendimentoLigacao) {
            Object[] linha = new Object[10];
            TableColumnModel columnModel = jTableLigacoes.getColumnModel();
            CRMClientesAtendimento.JTableRenderer renderers = new CRMClientesAtendimento.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = CreIcon;

            linha[1] = cli.getCodigolancamento();
            linha[2] = cli.getDataligcaoS();
            linha[3] = cli.getHoraligacaoS();
            linha[4] = cli.getDescricaoligacao();
            linha[5] = cli.getCodigoatendimento();

            columnModel.getColumn(6).setCellRenderer(renderers);
            linha[6] = CreIcon;
            modeloCarga.addRow(linha);
        }

    }

    private void salvarLigacao() throws SQLException, ParseException {

        if (!txtCodigo.getText().equals("0")) {
            if (!txtSequenciaLancamento.getText().equals("0")) {
                try {
                    if (gravarligacao) {
                        atendimentoLigacao = new AtendimentoLigacao();
                        atendimentoLigacao.setCodigoatendimento(Integer.valueOf(txtSequenciaLancamento.getText()));
                        atendimentoLigacao.setCodigolancamento(atendimentoLigacaoDAO.proxCodCad(atendimentoLigacao.getCodigoatendimento()));
                        atendimentoLigacao.setCodigocliente(Integer.valueOf(txtCodigo.getText()));

                        atendimentoLigacao.setDataligacao(new Date());
                        atendimentoLigacao.setDataconversao(new Date());
                        atendimentoLigacao.setDescricaoligacao("INICIO DE ATENDIMENTO AO CLIENTE");
                        atendimentoLigacao.setUsuario(this.usuario.getId());
                        atendimentoLigacao.setTipoconta("CLI");
                        atendimentoLigacao.setPedido(0);
                        atendimentoLigacao.setConvertido("N");
                        atendimentoLigacao.setHoraligacao(CalculoHora(0));

                        if (!atendimentoLigacaoDAO.inserir(atendimentoLigacao)) {

                        } else {

                        }

                    }
                } catch (SQLException ex) {

                } finally {
                    carregarLigacoes("", " and usu_codcli = " + txtCodigo.getText() + " and usu_codmot = " + txtSequenciaLancamento.getText());

                }

            }
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
        jLabel12 = new javax.swing.JLabel();
        txtCodigo = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        txtNome = new org.openswing.swing.client.TextControl();
        txtMotivo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLancamento = new javax.swing.JTable();
        btnSelecionar = new javax.swing.JButton();
        btnNovo1 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnNovoPosvenda = new javax.swing.JButton();
        Arquivos = new javax.swing.JButton();
        txtHoraLancamento = new org.openswing.swing.client.TextControl();
        txtDataCompra = new org.openswing.swing.client.DateControl();
        jPanelGeral = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        txtObservacaoAtendimento = new org.openswing.swing.client.TextControl();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtOutrasMarcas = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableItens = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        txtSituacao = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        btnGravar = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        txtEmailPara = new org.openswing.swing.client.TextControl();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtObservacaoDetalhada = new javax.swing.JTextArea();
        btnEmail = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        txtSolucaoMotivo = new javax.swing.JComboBox<>();
        txtDataSolucao = new org.openswing.swing.client.DateControl();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        btnSolucao = new javax.swing.JButton();
        btnGravarSolucao = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtSolucao = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        txtPedido = new org.openswing.swing.client.TextControl();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTableLigacoes = new javax.swing.JTable();
        btnAddLigacao = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTableCarga = new javax.swing.JTable();
        btnPedidos = new javax.swing.JButton();
        txtContatoLigacao = new org.openswing.swing.client.TextControl();
        jLabel3 = new javax.swing.JLabel();
        txtDataLancamento = new org.openswing.swing.client.DateControl();
        txtDataNovaVisita = new org.openswing.swing.client.DateControl();
        txtCidade = new org.openswing.swing.client.TextControl();
        txtEstado = new org.openswing.swing.client.TextControl();
        txtPassos = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtSequenciaLancamento = new org.openswing.swing.client.TextControl();
        jLabel14 = new javax.swing.JLabel();
        txtTelefones = new org.openswing.swing.client.TextControl();
        jLabel15 = new javax.swing.JLabel();
        btnLigacoes = new javax.swing.JButton();
        txtSituacaoCliente = new org.openswing.swing.client.TextControl();
        optAuto = new javax.swing.JRadioButton();
        optMoto = new javax.swing.JRadioButton();
        btnFile = new javax.swing.JButton();
        txtGrupoEmpresa = new org.openswing.swing.client.TextControl();
        txtCodVen = new org.openswing.swing.client.TextControl();
        txtNomVen = new org.openswing.swing.client.TextControl();
        txtCodRep = new org.openswing.swing.client.TextControl();
        txtNomRep = new org.openswing.swing.client.TextControl();
        jLabel4 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clientes");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jLabel12.setText("Cliente");

        txtCodigo.setEnabled(false);
        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        jLabel1.setText("Nome");

        txtNome.setEnabled(false);
        txtNome.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        txtMotivo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtMotivo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));
        txtMotivo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtMotivoMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtMotivoMousePressed(evt);
            }
        });

        jLabel2.setText("Motivo");

        jTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPaneMouseClicked(evt);
            }
        });

        jTableLancamento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Id", "Descrição", "Situação", "Data", "Reagendado", "Solução"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLancamento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableLancamentoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableLancamento);
        if (jTableLancamento.getColumnModel().getColumnCount() > 0) {
            jTableLancamento.getColumnModel().getColumn(0).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(1).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(3).setMinWidth(200);
            jTableLancamento.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTableLancamento.getColumnModel().getColumn(3).setMaxWidth(200);
            jTableLancamento.getColumnModel().getColumn(4).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(5).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableLancamento.getColumnModel().getColumn(6).setMinWidth(100);
            jTableLancamento.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableLancamento.getColumnModel().getColumn(6).setMaxWidth(100);
        }

        btnSelecionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/accept.png"))); // NOI18N
        btnSelecionar.setText("Selecionar");
        btnSelecionar.setEnabled(false);
        btnSelecionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarActionPerformed(evt);
            }
        });

        btnNovo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnNovo1.setText("Novo Atendimento ");
        btnNovo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovo1ActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        jButton1.setText("Fechar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnNovoPosvenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/posvenda.png"))); // NOI18N
        btnNovoPosvenda.setText("Pós Venda");
        btnNovoPosvenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoPosvendaActionPerformed(evt);
            }
        });

        Arquivos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        Arquivos.setText("Arquivos");
        Arquivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ArquivosActionPerformed(evt);
            }
        });

        txtHoraLancamento.setEnabled(false);
        txtHoraLancamento.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtDataCompra.setEnabled(false);
        txtDataCompra.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(btnNovo1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNovoPosvenda, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Arquivos, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDataCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtHoraLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnNovo1, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                            .addComponent(btnNovoPosvenda, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                            .addComponent(btnSelecionar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                            .addComponent(Arquivos, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDataCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHoraLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {Arquivos, btnNovo1, btnNovoPosvenda, btnSelecionar, jButton1});

        jTabbedPane.addTab("Registros", jPanel4);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Considerações"));

        txtObservacaoAtendimento.setEnabled(false);
        txtObservacaoAtendimento.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtOutrasMarcas.setColumns(20);
        txtOutrasMarcas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtOutrasMarcas.setLineWrap(true);
        txtOutrasMarcas.setRows(10);
        txtOutrasMarcas.setWrapStyleWord(true);
        jScrollPane4.setViewportView(txtOutrasMarcas);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtObservacaoAtendimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4))
                .addGap(2, 2, 2))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtObservacaoAtendimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Situação"));

        jTableItens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "#", "Descrição", "Dias", "Ult. Fat", "Linha"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableItens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableItensMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableItens);
        if (jTableItens.getColumnModel().getColumnCount() > 0) {
            jTableItens.getColumnModel().getColumn(0).setMinWidth(50);
            jTableItens.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableItens.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableItens.getColumnModel().getColumn(2).setMinWidth(100);
            jTableItens.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableItens.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableItens.getColumnModel().getColumn(3).setMinWidth(100);
            jTableItens.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableItens.getColumnModel().getColumn(3).setMaxWidth(100);
        }

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/basket_add.png"))); // NOI18N
        jButton2.setText("Produtos");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtSituacao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "ATIVO", "INATIVO", "SEM COMPRA" }));
        txtSituacao.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtSituacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGap(2, 2, 2))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Registro"));

        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravar.setText("Gravar");
        btnGravar.setEnabled(false);
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        jButton6.setText("Sair");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        txtEmailPara.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtObservacaoDetalhada.setColumns(20);
        txtObservacaoDetalhada.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtObservacaoDetalhada.setLineWrap(true);
        txtObservacaoDetalhada.setRows(15);
        txtObservacaoDetalhada.setWrapStyleWord(true);
        jScrollPane3.setViewportView(txtObservacaoDetalhada);

        btnEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/email.png"))); // NOI18N
        btnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btnEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmailPara, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGravar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(218, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEmail)
                    .addComponent(txtEmailPara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGravar)
                    .addComponent(jButton6)))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                    .addGap(32, 32, 32)))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGravar, jButton6, txtEmailPara});

        javax.swing.GroupLayout jPanelGeralLayout = new javax.swing.GroupLayout(jPanelGeral);
        jPanelGeral.setLayout(jPanelGeralLayout);
        jPanelGeralLayout.setHorizontalGroup(
            jPanelGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGeralLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(jPanelGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );
        jPanelGeralLayout.setVerticalGroup(
            jPanelGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGeralLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanelGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelGeralLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(2, 2, 2))
        );

        jTabbedPane.addTab("Atendimento", jPanelGeral);

        txtSolucaoMotivo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtSolucaoMotivo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));
        txtSolucaoMotivo.setEnabled(false);
        txtSolucaoMotivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSolucaoMotivoActionPerformed(evt);
            }
        });

        txtDataSolucao.setEnabled(false);
        txtDataSolucao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel13.setText("Motivo Solução");

        jLabel16.setText("Data Solução");

        jLabel17.setText("Solução");

        btnSolucao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnSolucao.setEnabled(false);
        btnSolucao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolucaoActionPerformed(evt);
            }
        });

        btnGravarSolucao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravarSolucao.setText("Gravar");
        btnGravarSolucao.setEnabled(false);
        btnGravarSolucao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarSolucaoActionPerformed(evt);
            }
        });

        txtSolucao.setColumns(20);
        txtSolucao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSolucao.setLineWrap(true);
        txtSolucao.setRows(15);
        txtSolucao.setWrapStyleWord(true);
        jScrollPane5.setViewportView(txtSolucao);

        jLabel18.setText("Pedido");

        txtPedido.setEnabled(false);
        txtPedido.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSolucaoMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDataSolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(btnSolucao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnGravarSolucao)))))
                .addGap(0, 329, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(jLabel18)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSolucaoMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataSolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSolucao)
                    .addComponent(btnGravarSolucao)
                    .addComponent(txtPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGravarSolucao, btnSolucao, txtDataSolucao, txtSolucaoMotivo});

        jTabbedPane.addTab("Solução", jPanel5);

        jTableLigacoes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "LIGAÇÕES", "DATA", "HORÁRIO", "INFO", "ATENDIMENTO", "#l"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLigacoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableLigacoesMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(jTableLigacoes);
        if (jTableLigacoes.getColumnModel().getColumnCount() > 0) {
            jTableLigacoes.getColumnModel().getColumn(0).setMinWidth(50);
            jTableLigacoes.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableLigacoes.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableLigacoes.getColumnModel().getColumn(1).setMinWidth(100);
            jTableLigacoes.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableLigacoes.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableLigacoes.getColumnModel().getColumn(2).setMinWidth(100);
            jTableLigacoes.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableLigacoes.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableLigacoes.getColumnModel().getColumn(3).setMinWidth(100);
            jTableLigacoes.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableLigacoes.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableLigacoes.getColumnModel().getColumn(5).setMinWidth(100);
            jTableLigacoes.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableLigacoes.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableLigacoes.getColumnModel().getColumn(6).setMinWidth(50);
            jTableLigacoes.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTableLigacoes.getColumnModel().getColumn(6).setMaxWidth(50);
        }

        btnAddLigacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bug_link.png"))); // NOI18N
        btnAddLigacao.setText("Nova");
        btnAddLigacao.setEnabled(false);
        btnAddLigacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLigacaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1143, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btnAddLigacao)
                        .addContainerGap())))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddLigacao, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        jTabbedPane.addTab("Ligações", jPanel6);

        jTableCarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Pedido", "Cliente", "Nome", "UF", "Cidade", "Data Pedido", "Dias Transporte", "Data PARA Faturamento", "#", "Liberar", "Situação", "Peso", "Quantidade", "Nota", "Filial", "Emissão", "Serie", "Transação", "Transportadora", "Pré_fatura", "Sucata ID", "Peso Sucata"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true
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
        jScrollPane6.setViewportView(jTableCarga);
        if (jTableCarga.getColumnModel().getColumnCount() > 0) {
            jTableCarga.getColumnModel().getColumn(0).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(2).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(2).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(2).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(3).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(3).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(3).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(4).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(5).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(5).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(6).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(7).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(8).setMinWidth(120);
            jTableCarga.getColumnModel().getColumn(8).setPreferredWidth(120);
            jTableCarga.getColumnModel().getColumn(8).setMaxWidth(120);
            jTableCarga.getColumnModel().getColumn(9).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(10).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(10).setMaxWidth(0);
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
            jTableCarga.getColumnModel().getColumn(16).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(16).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(16).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(17).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(17).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(17).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(18).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(18).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(19).setMinWidth(300);
            jTableCarga.getColumnModel().getColumn(19).setPreferredWidth(300);
            jTableCarga.getColumnModel().getColumn(19).setMaxWidth(300);
            jTableCarga.getColumnModel().getColumn(20).setMinWidth(0);
            jTableCarga.getColumnModel().getColumn(20).setPreferredWidth(0);
            jTableCarga.getColumnModel().getColumn(20).setMaxWidth(0);
            jTableCarga.getColumnModel().getColumn(21).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(21).setMaxWidth(100);
            jTableCarga.getColumnModel().getColumn(22).setMinWidth(100);
            jTableCarga.getColumnModel().getColumn(22).setPreferredWidth(100);
            jTableCarga.getColumnModel().getColumn(22).setMaxWidth(100);
        }

        btnPedidos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPedidos.setText("Pedidos ");
        btnPedidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedidosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(btnPedidos)
                .addGap(0, 1052, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1145, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(btnPedidos)
                .addGap(0, 245, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                    .addGap(31, 31, 31)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)))
        );

        jTabbedPane.addTab("Pedidos", jPanel7);

        txtContatoLigacao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel3.setText("Contato");

        txtDataLancamento.setEnabled(false);
        txtDataLancamento.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtDataNovaVisita.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtCidade.setEnabled(false);
        txtCidade.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtEstado.setEnabled(false);
        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtPassos.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel5.setText("Proximos Passos");

        jLabel6.setText("Estado");

        jLabel7.setText("Nova Visita");

        jLabel8.setText("Lançamento");

        jLabel11.setText("Cidade");

        txtSequenciaLancamento.setEnabled(false);
        txtSequenciaLancamento.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel14.setText("Sequencia");

        txtTelefones.setEnabled(false);
        txtTelefones.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTelefones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefonesActionPerformed(evt);
            }
        });

        jLabel15.setText("Telefone(s)");

        btnLigacoes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Telefone.png"))); // NOI18N
        btnLigacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLigacoesActionPerformed(evt);
            }
        });

        txtSituacaoCliente.setEnabled(false);
        txtSituacaoCliente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        buttonGroup1.add(optAuto);
        optAuto.setText("Auto");

        buttonGroup1.add(optMoto);
        optMoto.setText("Moto");

        btnFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder.png"))); // NOI18N
        btnFile.setEnabled(false);
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        txtGrupoEmpresa.setEnabled(false);
        txtGrupoEmpresa.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtCodVen.setEnabled(false);
        txtCodVen.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNomVen.setEnabled(false);
        txtNomVen.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtCodRep.setEnabled(false);
        txtCodRep.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNomRep.setEnabled(false);
        txtNomRep.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel4.setText("Grupo Empresas");

        jLabel19.setText("Nome");

        jLabel20.setText("Vendedor");

        jLabel21.setText("Representante");

        jLabel22.setText("Nome");

        jLabel9.setText("Status");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel3))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtMotivo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(271, 271, 271)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtSituacaoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(optAuto)
                                        .addGap(5, 5, 5)
                                        .addComponent(optMoto)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addComponent(btnLigacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnFile, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtContatoLigacao, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTelefones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel8)
                                        .addComponent(txtSequenciaLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(txtDataLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPassos, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel11)
                            .addComponent(txtCidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtEstado, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                            .addComponent(txtDataNovaVisita, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtGrupoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCodVen, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel20))
                                .addGap(1, 1, 1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtNomVen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addGap(181, 181, 181)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCodRep, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel21))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txtNomRep, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel1)
                            .addComponent(jLabel6)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSequenciaLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(jLabel3))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel8)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtContatoLigacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtDataLancamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTelefones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel9)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSituacaoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(optAuto)
                                    .addComponent(optMoto)
                                    .addComponent(btnLigacoes)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(btnFile))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDataNovaVisita, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPassos)
                            .addComponent(txtMotivo))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel19))
                        .addComponent(jLabel20))
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtGrupoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodVen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomVen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodRep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomRep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFile, btnLigacoes, optAuto, optMoto, txtDataNovaVisita, txtMotivo, txtPassos, txtSituacaoCliente});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        //
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        //
    }//GEN-LAST:event_txtNomeActionPerformed

    private void jTableItensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableItensMouseClicked
        int linhaSelSit = jTableItens.getSelectedRow();
        int colunaSelSit = jTableItens.getSelectedColumn();
        // txtSituacaoCliente.setText(jTableItens.getValueAt(linhaSelSit, 1).toString());
        String selecao = jTableItens.getValueAt(linhaSelSit, 4).toString();
        optAuto.setSelected(false);
        optMoto.setSelected(false);

        if (selecao.equals("BA")) {
            optAuto.setSelected(true);
        }
        if (selecao.equals("BM")) {
            optMoto.setSelected(true);
        }

    }//GEN-LAST:event_jTableItensMouseClicked

    private void jTableLancamentoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableLancamentoMouseClicked
        int linhaSelSit = jTableLancamento.getSelectedRow();
        int colunaSelSit = jTableLancamento.getSelectedColumn();
        Integer id = Integer.valueOf(jTableLancamento.getValueAt(linhaSelSit, 1).toString());
        txtSequenciaLancamento.setText(String.valueOf(id));
        btnSelecionar.setEnabled(false);
        ligacaoCliente = false;
        if (evt.getClickCount() == 2) {
            btnSelecionar.setEnabled(true);
            try {
                getMotivo(Integer.valueOf(txtSequenciaLancamento.getText()));

            } catch (SQLException ex) {
                Logger.getLogger(CRMClientesAtendimento.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTableLancamentoMouseClicked

    private void btnSolucaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolucaoActionPerformed
        try {
            if (!txtSequenciaLancamento.getText().equals("0") && !txtCodigo.getText().equals("0")) {
                novaSolucao();

            }
        } catch (SQLException ex) {
            Logger.getLogger(CRMClientesAtendimento.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSolucaoActionPerformed

    private void btnGravarSolucaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarSolucaoActionPerformed
        if (ManipularRegistros.pesos(" Deseja gravar solução")) {
            try {
                gravarSolucao();

            } catch (Exception ex) {
                Logger.getLogger(CRMClientesAtendimento.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnGravarSolucaoActionPerformed

    private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
        try {
            getMotivo(Integer.valueOf(txtSequenciaLancamento.getText()));

        } catch (SQLException ex) {
            Logger.getLogger(CRMClientesAtendimento.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSelecionarActionPerformed

    private void btnNovo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovo1ActionPerformed
        try {
            this.tipoAtendimento = "ATE";
            novoAtendimento(true, this.tipoAtendimento);

        } catch (ParseException ex) {
            Logger.getLogger(CRMClientesAtendimento.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnNovo1ActionPerformed

    private void jTabbedPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPaneMouseClicked
        int abaselecionada = jTabbedPane.getSelectedIndex();
        if (abaselecionada == 0) {
            DesabilitarTab();
            try {
                carregarLigacoes("", " and usu_codcli = " + txtCodigo.getText());

            } catch (SQLException ex) {
                Logger.getLogger(CRMClientesAtendimento.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTabbedPaneMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            CRMProdutosFaturamento sol = new CRMProdutosFaturamento();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado 
            sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavra(this, this.cliente, "");

        } catch (PropertyVetoException ex) {
            Logger.getLogger(CRMClientes.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(CRMClientes.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        if (ManipularRegistros.pesos("Deseja gravar atendimento ?")) {
            try {
                gravarRegistro();
                if (this.tipoAtendimento != null) {
                    if (this.tipoAtendimento.equals("POS")) {
                        if (ManipularRegistros.pesos(" Solucionar atendimento ?")) {
                            txtSolucao.setText("PÓS-VENDA");
                            getMotivos("S");
                            txtSolucaoMotivo.setSelectedItem("620-PÓS-VENDA");

                            txtPedido.setText("0");
                            txtDataSolucao.setDate(new Date());

                            gravarSolucao();
                        } else {
                            btnGravar.setEnabled(true);

                        }
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(CRMClientesAtendimento.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(CRMClientesAtendimento.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnGravarActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        try {
            getSelecaoEmail(null);

        } catch (Exception ex) {
            Logger.getLogger(CRMClientesAtendimento.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnEmailActionPerformed

    private void btnAddLigacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLigacaoActionPerformed
        if (ManipularRegistros.pesos(" Deseja Gerar nova ligação? ")) {
            try {
                this.atendimentoLigacao = new AtendimentoLigacao();
                novaLigacao(true);

            } catch (Exception ex) {
                Logger.getLogger(CRMClientesAtendimento.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {

        }
    }//GEN-LAST:event_btnAddLigacaoActionPerformed

    private void jTableLigacoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableLigacoesMouseClicked
        int linhaSelSit = jTableLigacoes.getSelectedRow();
        int colunaSelSit = jTableLigacoes.getSelectedColumn();
        Integer lancamento = Integer.valueOf(jTableLigacoes.getValueAt(linhaSelSit, 1).toString());
        Integer atendimento = Integer.valueOf(jTableLigacoes.getValueAt(linhaSelSit, 5).toString());

        if (evt.getClickCount() == 2) {
            try {
                txtSequenciaLancamento.setText(String.valueOf(atendimento));

                buscarLicacao(lancamento, atendimento);

            } catch (SQLException ex) {
                Logger.getLogger(CRMClientesAtendimento.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (Exception ex) {
                Logger.getLogger(CRMClientesAtendimento.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTableLigacoesMouseClicked

    private void btnLigacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLigacoesActionPerformed
        jTabbedPane.setEnabledAt(3, true);

        jTabbedPane.setSelectedIndex(3);
    }//GEN-LAST:event_btnLigacoesActionPerformed

    private void txtSolucaoMotivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSolucaoMotivoActionPerformed


    }//GEN-LAST:event_txtSolucaoMotivoActionPerformed

    private void txtMotivoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMotivoMouseClicked

    }//GEN-LAST:event_txtMotivoMouseClicked

    private void txtMotivoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMotivoMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMotivoMousePressed

    private String tipoAtendimento;
    private void btnNovoPosvendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoPosvendaActionPerformed
        try {
            this.tipoAtendimento = "POS";
            novoAtendimento(true, this.tipoAtendimento);

        } catch (ParseException ex) {
            Logger.getLogger(CRMClientesAtendimento.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnNovoPosvendaActionPerformed

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        try {
            selecionarArquivo("A");
        } catch (Exception ex) {
            Logger.getLogger(CRMClientesAtendimento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFileActionPerformed

    private void ArquivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ArquivosActionPerformed
        try {
            getArquivos("X:\\ERBS\\CRM\\CLIENTE\\" + txtCodigo.getText() + "\\");
        } catch (Exception ex) {
            Logger.getLogger(CRMClientesAtendimento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ArquivosActionPerformed

    private void jTableCargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCargaMouseClicked
        //        int linhaSelSit = jTableCarga.getSelectedRow();
        //        int colunaSelSit = jTableCarga.getSelectedColumn();
        //        btnLiberar.setEnabled(false);
        //        if (evt.getClickCount() == 1) {
        //            try {
        //                txtPedido.setText(jTableCarga.getValueAt(linhaSelSit, 1).toString());
        //                txtCliente.setText(jTableCarga.getValueAt(linhaSelSit, 2).toString());
        //                // getPedido("", " and ped.usu_numped = '" + txtPedido.getText() + "'");
        //            } catch (SQLException ex) {
        //                Logger.getLogger(FatPedidoHub.class.getName()).log(Level.SEVERE, null, ex);
        //            }
        //        } else if (evt.getClickCount() == 2) {
        //            try {
        //                novoRegistro("MOTO", "");
        //            } catch (PropertyVetoException ex) {
        //                Mensagem.mensagemRegistros("ERRO", ex.toString());
        //            } catch (Exception ex) {
        //                Mensagem.mensagemRegistros("ERRO", ex.toString());
        //            }
        //        }
    }//GEN-LAST:event_jTableCargaMouseClicked

    private void btnPedidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedidosActionPerformed
        if (txtCodigo.getText().isEmpty()) {
            Mensagem.mensagemRegistros("ERRO", "Informe o cliente");
        } else {
            try {

                String sql = " and ped.codcli = '" + txtCodigo.getText() + "' ";
                getPedidos("SIT", sql);
            } catch (Exception ex) {
                Mensagem.mensagemRegistros("ERRO", ex.toString());
            }
        }
    }//GEN-LAST:event_btnPedidosActionPerformed

    private void txtTelefonesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefonesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefonesActionPerformed

    private Integer contadorEmail = 0;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Arquivos;
    private javax.swing.JButton btnAddLigacao;
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnFile;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnGravarSolucao;
    private javax.swing.JButton btnLigacoes;
    private javax.swing.JButton btnNovo1;
    private javax.swing.JButton btnNovoPosvenda;
    private javax.swing.JButton btnPedidos;
    private javax.swing.JButton btnSelecionar;
    private javax.swing.JButton btnSolucao;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanelGeral;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTableCarga;
    private javax.swing.JTable jTableItens;
    private javax.swing.JTable jTableLancamento;
    private javax.swing.JTable jTableLigacoes;
    private javax.swing.JRadioButton optAuto;
    private javax.swing.JRadioButton optMoto;
    private org.openswing.swing.client.TextControl txtCidade;
    private org.openswing.swing.client.TextControl txtCodRep;
    private org.openswing.swing.client.TextControl txtCodVen;
    private org.openswing.swing.client.TextControl txtCodigo;
    private org.openswing.swing.client.TextControl txtContatoLigacao;
    private org.openswing.swing.client.DateControl txtDataCompra;
    private org.openswing.swing.client.DateControl txtDataLancamento;
    private org.openswing.swing.client.DateControl txtDataNovaVisita;
    private org.openswing.swing.client.DateControl txtDataSolucao;
    private org.openswing.swing.client.TextControl txtEmailPara;
    private org.openswing.swing.client.TextControl txtEstado;
    private org.openswing.swing.client.TextControl txtGrupoEmpresa;
    private org.openswing.swing.client.TextControl txtHoraLancamento;
    private javax.swing.JComboBox<String> txtMotivo;
    private org.openswing.swing.client.TextControl txtNomRep;
    private org.openswing.swing.client.TextControl txtNomVen;
    private org.openswing.swing.client.TextControl txtNome;
    private org.openswing.swing.client.TextControl txtObservacaoAtendimento;
    private javax.swing.JTextArea txtObservacaoDetalhada;
    private javax.swing.JTextArea txtOutrasMarcas;
    private javax.swing.JComboBox<String> txtPassos;
    private org.openswing.swing.client.TextControl txtPedido;
    private org.openswing.swing.client.TextControl txtSequenciaLancamento;
    private javax.swing.JComboBox<String> txtSituacao;
    private org.openswing.swing.client.TextControl txtSituacaoCliente;
    private javax.swing.JTextArea txtSolucao;
    private javax.swing.JComboBox<String> txtSolucaoMotivo;
    private org.openswing.swing.client.TextControl txtTelefones;
    // End of variables declaration//GEN-END:variables
}
