/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.SalvaImagem;
import br.com.sgi.TirarPrint;
import br.com.sgi.bean.CargaAbertura;
import br.com.sgi.bean.CargaItens;
import br.com.sgi.bean.CargaRegistro;
import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaNota;
import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.bean.OrdemCompra;
import br.com.sgi.bean.OrdemCompraItens;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Produto;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.dao.CargaAberturaDAO;
import br.com.sgi.dao.CargaItensDAO;
import br.com.sgi.dao.NotaFiscalDAO;
import br.com.sgi.dao.OrdemCompraDAO;
import br.com.sgi.integracao.SerialComm;
import java.awt.Component;
import java.awt.Dimension;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;
import br.com.sgi.util.ConversaoHoras;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;

/**
 *
 * @author jairosilva
 */
public final class IntegrarPesosRegistrar extends InternalFrame {
    
    private CargaRegistro cargaRegistro;
    private CargaAberturaDAO cargaRegistroDAO;
    private List<CargaItens> listaCargaItens = new ArrayList<CargaItens>();
    private CargaItens cargaItens;
    private CargaItensDAO cargaItensDAO;
    private IntegrarPesos veioCampo;
    private UtilDatas utilDatas;
    private boolean addNewReg;
    
    private String origemPesagem;
    
    public IntegrarPesosRegistrar() {
        try {
            initComponents();
            if (cargaRegistroDAO == null) {
                cargaRegistroDAO = new CargaAberturaDAO();
            }
            if (cargaItensDAO == null) {
                cargaItensDAO = new CargaItensDAO();
            }
            if (utilDatas == null) {
                utilDatas = new UtilDatas();
            }
            txtPesoDescontoImpureza.setValue(0);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
        
    }
    private List<OrdemCompraItens> listaOrdens = new ArrayList<OrdemCompraItens>();
    
    private void getOrdemCompraItens(String PESQUISA_POR, String PESQUISA) throws SQLException, Exception {
        desabilitaCampo();
        limparCampos(TIPO_PESAGEM);
        OrdemCompraDAO dao = new OrdemCompraDAO();
        listaOrdens = dao.getOrdemCompraItenss(PESQUISA_POR, PESQUISA);
        if (listaOrdens != null) {
            if (listaOrdens.size() > 0) {
                carregarTabela();
                if (addNewReg) {
                    habilitarCampo();
                    txtPlaca.requestFocus();
                }
            }
        }
        
    }
    
    public void carregarTabela() throws Exception {
        addNewReg = true;
        ImageIcon sitok = getImage("/images/sitBom.png");
        ImageIcon RuiIcon = getImage("/images/sitRuim.png");
        ImageIcon Conferindo = getImage("/images/bricks.png");
        int linhas = 0;
        redColunastab();
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableItens.getModel();
        modeloCarga.setNumRows(linhas);
        Object[] linha = new Object[15];
        for (OrdemCompraItens prg : listaOrdens) {
            TableColumnModel columnModel = jTableItens.getColumnModel();
            IntegrarPesosRegistrar.JTableRenderer renderers = new IntegrarPesosRegistrar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            columnModel.getColumn(2).setCellRenderer(renderers);
            
            linha[0] = Conferindo;
            linha[1] = prg.getNumeroOrdemCompra();
            
            if (prg.getOrdemCompra().getSituacaoaprovacao().equals("APR")) {
                linha[2] = sitok;
            } else {
                addNewReg = false;
                linha[2] = RuiIcon;
            }
            
            linha[3] = prg.getProduto().getCodigoproduto();
            linha[4] = prg.getProduto().getDescricaoproduto() + " - " + COMPLEMENTO;
            linha[5] = prg.getQuantidadePedida();
            linha[6] = "0";
            linha[7] = prg.getUnidadeMedida();
            linha[8] = "EMB";
            linha[9] = prg.getPesoLiquido();
            linha[10] = prg.getOrdemCompra().getCodigoFornecedor();
            linha[11] = prg.getOrdemCompra().getNomeFornecedor();
            linha[12] = "Não Informado";
            linha[13] = "Não Gravado";
            modeloCarga.addRow(linha);
        }
        
    }
    
    private static String TIPO_PESAGEM;
    private static String PROCESSO;
    private String COMPLEMENTO;
    private String processoOrdem;
    private boolean obrigarOrdem = false;
    
    public void setRecebePalavra(IntegrarPesos veioInput, String TIPO_PESAGEM, String PROCESSO, CargaRegistro cargaRegistro, String COMPLEMENTO) throws Exception {
        this.veioCampo = veioInput;
        this.PROCESSO = PROCESSO;
        // this.processoOrdem = COMPLEMENTO;
        this.COMPLEMENTO = COMPLEMENTO;
        this.obrigarOrdem = false;
        this.cargaRegistro = cargaRegistro;
        IntegrarPesosRegistrar.TIPO_PESAGEM = TIPO_PESAGEM;
        if (veioInput != null) {
            if (cargaRegistro.getPesoEntrada() == 0) {
                SerialComm s = new SerialComm();
                s.execute();
            }
        }
        limparCampos(TIPO_PESAGEM);
        lblTipo.setText("Informe a ordem de compra");
        setTitle(ClientSettings.getInstance().getResources().getResource("Descarga de produtos(s) "));
        btnPesOcp.setEnabled(false);
        
        switch (PROCESSO) {
            case "DS":
                this.obrigarOrdem = true;
                this.PROCESSO = "D";
                btnPesOcp.setEnabled(true);
                txtOrdemCompra.setEnabled(false);
                txtInfo.setText("DESCARGA DE SUCATA DE INDUSTRIALIZAÇÃO");
                
                break;
            case "D":
                txtOrdemCompra.setText("0");
                txtInfo.setText("");
                if (COMPLEMENTO.equals("ECO")) {
                    btnPesOcp.setEnabled(true);
                    txtOrdemCompra.setEnabled(false);
                    txtInfo.setText("DESCARGA DE SUCATA DE AUTO");
                }
                break;
            case "CP":
                txtOrdemCompra.setText("0");
                txtInfo.setText("");
                
                btnPesOcp.setEnabled(true);
                txtOrdemCompra.setEnabled(false);
                txtInfo.setText("COLETA DE PEDIDO");
                
                break;
            case "CI":
                desabilitaCampo();
                carregarProcessoColeta("", PROCESSO);
                btnPesPed.setEnabled(false);
                btnPesPro.setEnabled(true);
                txtPlaca.setText("EMP-0000");
                txtTranspotadora.setText("0 - INTERNO");
                txtInfo.setText("PESAGEM INTERNA");
                
                break;
            case "CM":
                desabilitaCampo();
                carregarProcessoColeta("", PROCESSO);
                btnPesPed.setEnabled(true);
                btnPesPro.setEnabled(true);
                txtInfo.setText("CARGA DE MOTO");
                break;
            case "CA":
                desabilitaCampo();
                carregarProcessoColeta("", PROCESSO);
                btnPesPed.setEnabled(true);
                btnPesPro.setEnabled(true);
                txtInfo.setText("CARGA DE AUTO");
                btnPesMin.setEnabled(true);
                break;
            case "ECO":
                carregarProcessoColeta("", PROCESSO);
                btnPesPed.setEnabled(true);
                btnPesPro.setEnabled(true);
                btnPesOcp.setEnabled(true);
                btnPesMin.setEnabled(true);
                txtInfo.setText("SUCATA DE ECO");
            
            default:
                break;
            // code block
        }
        btnPesMin.setEnabled(false);
        if (PROCESSO.equals("DS") || PROCESSO.equals("D")) {
            
        } else {
            txtOrdemCompra.setEnabled(false);
            txtOrdemCompra.setText("0");
            lblTipo.setText("Informe a nota fiscal");
            setTitle(ClientSettings.getInstance().getResources().getResource("Coleta de produto(s) "));
        }
        
        btnImprimir.setEnabled(false);
        btnAlterar.setEnabled(false);
        if (!txtTicket.getText().equals("0")) {
            btnImprimir.setEnabled(true);
            btnAlterar.setEnabled(true);
        }
        
    }
    
    public void setRecebePalavraSaida(IntegrarPesos veioInput, String TIPO_PESAGEM, String PROCESSO, CargaRegistro cargaRegistro) throws Exception {
        this.veioCampo = veioInput;
        this.PROCESSO = PROCESSO;
        this.cargaRegistro = cargaRegistro;
        IntegrarPesosRegistrar.TIPO_PESAGEM = TIPO_PESAGEM;
        
        if (veioInput != null) {
            if (cargaRegistro.getPesoSaida() == 0) {
                SerialComm s = new SerialComm();
                s.execute();
            }
        }
        
        txtPesoSaida.setEnabled(true);
        btnPesarSaida.setEnabled(true);
        popularTela();
        getCarregarListaProdutosPesos();
        if (cargaRegistro.getPesoSaida() > 0.0) {
            desabilitaCampo();
        } else {
            jTableItens.setEnabled(false);
        }
        btnImprimir.setEnabled(false);
        if (!txtTicket.getText().equals("0")) {
            btnImprimir.setEnabled(true);
        }
        btnEmb.setEnabled(false);
        btnImp.setEnabled(false);
        txtOrdemCompra.setEnabled(false);
        btnGravarRegistro.setEnabled(false);
        txtInfo.setEnabled(true);
        if (cargaRegistro != null) {
            if (cargaRegistro.getNumerocarga() > 0) {
                btnAlterar.setEnabled(true);
                txtInfo.setEnabled(true);
            }
        }
        
    }
    
    public void carregarProcessoColeta(String PESQUISA_POR, String PESQUISA) throws Exception {
        if (this.pedido == null) {
            this.pedido = new Pedido();
        }
        txtPlaca.requestFocus();
        OrdemCompraDAO dao = new OrdemCompraDAO();
        listaOrdens = dao.getOrdemCompraColeta(PESQUISA_POR, PESQUISA, this.pedido);
        carregarTabela();
        habilitarCampo();
        
    }
    
    private Pedido pedido;
    
    public void retornarOrdemCompra(String PESQUISA_POR, String PESQUISA, String EMPRESA, String FILIAL, String processo, Pedido pedido, String complemento) throws Exception {
        String slq = "and ocp.codemp = " + EMPRESA + " and  ocp.codfil = " + FILIAL + " and  ocp.numocp =" + PESQUISA_POR + " ";
        txtOrdemCompra.setText(PESQUISA_POR);
        if (!complemento.isEmpty()) {
            COMPLEMENTO = complemento;
        }
        
        if (processo.equals("ENTRADA")) {
            TIPO_PESAGEM = "ENTRADA";
            PROCESSO = "D";
            txtInfo.setText("DESCARGA DE SUCATA " + pedido.getCliente() + " - " + pedido.getCadCliente().getNome());
        } else {
            this.pedido = pedido;
            TIPO_PESAGEM = "COLETA_INDUSTRIALIZACAO";
            PROCESSO = "CMETAIS"; // COLETA DE METAIS
            txtInfo.setText("COLETA DE CHUMBO " + pedido.getCliente() + " - " + pedido.getCadCliente().getNome());
            if (complemento.equals("VENDA_METAIS")) {
                String cli = pedido.getCadCliente().getNome();
                int tam = pedido.getCadCliente().getNome().length();
                if (tam > 30) {
                     cli = pedido.getCadCliente().getNome().substring(0, 30);
                    
                }
                TIPO_PESAGEM = "VENDA_METAIS";
                PROCESSO = "VMETAIS"; // VENDA DE METAIS
                txtInfo.setText("PEDIDO " + txtOrdemCompra.getText() + " PARA O CLIENTE  " + pedido.getCliente() + " - " + cli);
            }
        }
        getRetornoOrdem(slq);
    }
    private List<NotaFiscal> listaNotaFiscal = new ArrayList<NotaFiscal>();
    
    public void retornarNotaFiscal(String PESQUISA_POR, String PESQUISA, String EMPRESA, String FILIAL) throws Exception {
        this.listaOrdens = new ArrayList<OrdemCompraItens>();
        txtOrdemCompra.setText(PESQUISA);
        NotaFiscalDAO notafiscalDAO = new NotaFiscalDAO();
        String sql = " and nfv.codemp =" + EMPRESA + " and nfv.codfil = " + FILIAL + " and nfv.numnfv in(" + PESQUISA + ")";
        listaNotaFiscal = notafiscalDAO.getNotaFiscais("NFV", sql);
        jTableItens.setRowHeight(42);
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableItens.getModel();
        modeloCarga.setNumRows(0);
        int cont = 0;
        for (NotaFiscal nf : listaNotaFiscal) {
            Object[] linha = new Object[15];
            cont++;
            linha[1] = nf.getNotafiscal();
            linha[3] = "MOTO";
            linha[4] = "COLETA DE BATERIAS MOTO";
            if (PROCESSO.equals("CA")) {
                linha[3] = "AUTO";
                linha[4] = "COLETA DE BATERIAS AUTO";
            }
            linha[5] = nf.getQuantidade();
            linha[6] = "0";
            linha[9] = nf.getPesoLiquido();
            linha[10] = nf.getCodigocliente();
            linha[11] = nf.getCliente().getNome();
            linha[12] = nf.getNotafiscal();
            txtTranspotadora.setText(nf.getTransportadora() + " - " + nf.getNomeTransportadora());
            this.transportadora = new Transportadora();
            this.transportadora.setCodigoTransportadora(nf.getTransportadora());
            modeloCarga.addRow(linha);
            
            OrdemCompraItens ord = new OrdemCompraItens();
            ord.setOrdemCompra(new OrdemCompra());
            Produto pro = new Produto();
            ord.setNumeroOrdemCompra(nf.getNotafiscal());
            pro.setCodigoproduto("MOTO");
            pro.setDescricaoproduto("COLETA DE MOTO");
            ord.getOrdemCompra().setNomeFornecedor(nf.getCliente().getNome());
            
            if (PROCESSO.equals("CA")) {
                pro.setCodigoproduto("AUTO");
                pro.setDescricaoproduto("COLETA DE AUTO");
                ord.getOrdemCompra().setNomeFornecedor(nf.getCliente().getNome());
            }
            
            ord.setProduto(pro);
            ord.setEmpresa(1);
            ord.setFilial(1);
            ord.getOrdemCompra().setCodigoFornecedor(nf.getCodigocliente());
            ord.getOrdemCompra().setSituacaoaprovacao("APR");
            ord.setPesoBruto(nf.getPesoBruto());
            ord.setQuantidadePedida(nf.getQuantidade());
            ord.setNotafiscal(nf.getNotafiscal());
            ord.setSequenciaItem(cont);
            ord.setUnidadeMedida("PC");
            listaOrdens.add(ord);
            
        }
        
    }
    
    public void retornarMinuta(String PESQUISA_POR, String PESQUISA, Minuta minuta, List<MinutaNota> lstMinutaNota) throws Exception {
        limparCampos(TIPO_PESAGEM);
        if (PESQUISA_POR.equals("CANCELAR")) {
            txtOrdemCompra.setText("");
            carregarProcessoColeta("", PROCESSO);
        } else {
            txtOrdemCompra.setText(PESQUISA);
            if (minuta.getCadTransportadora() != null) {
                txtTranspotadora.setText(minuta.getUsu_codtra() + " - " + minuta.getCadTransportadora().getNomeTransportadora());
                this.transportadora = new Transportadora();
                this.transportadora = minuta.getCadTransportadora();
            }
            if (listaOrdens.size() > 0) {
                listaOrdens.clear();
            }
            
            jTableItens.setRowHeight(42);
            DefaultTableModel modeloCarga = (DefaultTableModel) jTableItens.getModel();
            modeloCarga.setNumRows(0);
            int cont = 0;
            for (MinutaNota nf : lstMinutaNota) {
                Object[] linha = new Object[15];
                cont++;
                linha[1] = nf.getNotafiscal();
                linha[3] = "MOTO";
                linha[4] = "COLETA DE BATERIAS MOTO";
                if (PROCESSO.equals("CA")) {
                    linha[3] = "AUTO";
                    linha[4] = "COLETA DE BATERIAS AUTO";
                }
                linha[5] = nf.getQuantidade();
                linha[6] = "0";
                linha[9] = nf.getPeso();
                linha[10] = nf.getCodigoCliente();
                linha[11] = nf.getCadCliente().getNome();
                linha[12] = nf.getNotafiscal();
                
                modeloCarga.addRow(linha);
                
                OrdemCompraItens ord = new OrdemCompraItens();
                ord.setOrdemCompra(new OrdemCompra());
                Produto pro = new Produto();
                ord.setNumeroOrdemCompra(nf.getNotafiscal());
                pro.setCodigoproduto("MOTO");
                pro.setDescricaoproduto("COLETA DE MOTO");
                ord.getOrdemCompra().setNomeFornecedor(nf.getCadCliente().getNome());
                
                if (PROCESSO.equals("CA")) {
                    pro.setCodigoproduto("AUTO");
                    pro.setDescricaoproduto("COLETA DE AUTO");
                    ord.getOrdemCompra().setNomeFornecedor(nf.getCadCliente().getNome());
                }
                
                ord.setProduto(pro);
                ord.setEmpresa(1);
                ord.setFilial(1);
                ord.getOrdemCompra().setCodigoFornecedor(nf.getCodigoCliente());
                ord.getOrdemCompra().setSituacaoaprovacao("APR");
                ord.setPesoBruto(nf.getPeso());
                ord.setQuantidadePedida(nf.getQuantidade());
                ord.setNotafiscal(nf.getNotafiscal());
                ord.setSequenciaItem(cont);
                ord.setUnidadeMedida("PC");
                listaOrdens.add(ord);
            }
            txtPlaca.requestFocus();
        }
        
    }
    
    private boolean camposObrigatorio(String campo) {
        boolean retorno = true;
        if (txtPlaca.getText().isEmpty()) {
            retorno = false;
            Mensagem.mensagemRegistros("ERRO", "Placa é obrigatório");
            txtPlaca.requestFocus();
        } else {
            if (txtMotorista.getText().isEmpty()) {
                retorno = false;
                Mensagem.mensagemRegistros("ERRO", "Motorista é obrigatório");
                txtMotorista.requestFocus();
            } else if (txtTranspotadora.getText().isEmpty()) {
                retorno = false;
                Mensagem.mensagemRegistros("ERRO", "Transportadora  é obrigatório");
                txtTranspotadora.requestFocus();
            } else if (PROCESSO.isEmpty()) {
                retorno = false;
                Mensagem.mensagemRegistros("ERRO", "Tipo de pesagem  é obrigatório");
            } else if (jTableItens.getRowCount() <= 0) {
                retorno = false;
                Mensagem.mensagemRegistros("ERRO", "Produtos é obrigatório");
            }
        }
        return retorno;
    }
    
    private void popularRegistro() {
        try {
            
            cargaRegistro = new CargaRegistro();
            
            if (txtInfo.getValue() != null) {
                cargaRegistro.setObservacaoCarga(txtInfo.getText());
            }
            
            if (!PROCESSO.equals("D")) {
                this.PROCESSO = "C";
            }
            cargaRegistro.setEmpresa(1);
            cargaRegistro.setFilial(1);
            cargaRegistro.setPlaca(txtPlaca.getText());
            cargaRegistro.setDataEntrada(new Date());
            cargaRegistro.setNomeMotorista(txtMotorista.getText());
            cargaRegistro.setTipoCarga(PROCESSO);
            cargaRegistro.setOrdemCompra(txtOrdemCompra.getText());
            cargaRegistro.setCodigoTransportadora(transportadora.getCodigoTransportadora());
            
        } catch (Exception e) {
        }
    }
    
    private void salvarRegistroEmail() throws SQLException, Exception {
        
        cargaRegistro.setEnviarEmial("S");
        if (cargaRegistroDAO.gravarEmail(cargaRegistro)) {
            
        }
        
    }
    
    private void salvarRegistro() throws SQLException, Exception {
        if (camposObrigatorio("")) {
            popularRegistro();
            if (addNewReg) {
                cargaRegistro.setNumerocarga(cargaRegistroDAO.proxCodCad());
                txtTicket.setText(String.valueOf(cargaRegistro.getNumerocarga()));
                cargaRegistro.setHoraEntrada(ConversaoHoras.ConverterHoras(txtHoraEntrada.getText()));
                if (salvarAbertura()) {
                    if (!cargaRegistroDAO.inserir(cargaRegistro)) {
                        btnPesarEntrada.setEnabled(false);
                    } else {
                        btnPesarEntrada.setEnabled(true);
                        txtPesoEntrada.setEnabled(true);
                        btnAlterar.setEnabled(true);
                        if (!salvarItens()) {
                        } else {
                            getCarregarListaProdutosPesos();
                        }
                    }
                }
            } else {
                cargaRegistro.setObservacaoCarga(txtInfo.getText());
                cargaRegistro.setNumerocarga(Integer.valueOf(txtTicket.getText()));
                if (!cargaRegistroDAO.alterar(cargaRegistro)) {
                    
                }
            }
            addNewReg = false;
            desabilitaCampo();
            txtOrdemCompra.setEnabled(false);
        } else {
            
        }
        
    }
    
    private void salvarRegistroAlteracao() throws SQLException, Exception {
        if (camposObrigatorio("")) {
            cargaRegistro.setObservacaoCarga(txtInfo.getText());
            cargaRegistro.setNumerocarga(Integer.valueOf(txtTicket.getText()));
            if (!cargaRegistroDAO.alterar(cargaRegistro)) {
                
            }
        }
        addNewReg = false;
        desabilitaCampo();
        txtOrdemCompra.setEnabled(false);
        
    }
    
    private boolean salvarItens() throws SQLException {
        boolean retorno = false;
        for (OrdemCompraItens oc : listaOrdens) {
            if (oc.getNumeroOrdemCompra() >= 0) {
                try {
                    CargaItens ite = new CargaItens();
                    ite.setDataAlteracao(new Date());
                    ite.setDataIntegracao(new Date());
                    ite.setDatacadastro(new Date());
                    ite.setDescricao(oc.getProduto().getDescricaoproduto() + " - " + COMPLEMENTO);
                    ite.setEmpresa(cargaRegistro.getEmpresa());
                    ite.setFilial(cargaRegistro.getFilial());
                    ite.setFornecedor(oc.getOrdemCompra().getCodigoFornecedor());
                    ite.setHorascadastro(ConversaoHoras.ConverterHoras(txtHoraEntrada.getText()));
                    if (oc.getNotafiscal() > 0) {
                        ite.setNota(oc.getNotafiscal());
                    } else {
                        ite.setNota(Integer.valueOf(txtNotaFiscal.getText()));
                    }
                    
                    ite.setNumerocarga(cargaRegistro.getNumerocarga());
                    ite.setPesoBruto(oc.getPesoBruto());
                    ite.setPesoLiquido(oc.getPesoLiquido());
                    ite.setProduto(oc.getProduto().getCodigoproduto());
                    ite.setQuantidadePrevista(oc.getQuantidadePedida());
                    ite.setSequenciaItem(oc.getSequenciaItem());
                    ite.setSequenciaPeso(ite.getNumerocarga());
                    ite.setSequenciacarga(cargaItensDAO.proxCodCad());
                    ite.setUnidadeMedida(oc.getUnidadeMedida());
                    ite.setUsuarioAlteracao(0);
                    ite.setUsuarioIntegracao(0);
                    ite.setUsuariocadastro(0);
                    ite.setProduto(ite.getProduto());
                    retorno = cargaItensDAO.inserir(ite);
                } catch (NumberFormatException | SQLException | ParseException e) {
                    JOptionPane.showMessageDialog(null, "ERRO " + e);
                }
            }
        }
        return retorno;
    }
    
    private boolean salvarItensNota() throws SQLException {
        boolean retorno = false;
        for (NotaFiscal nf : listaNotaFiscal) {
            if (nf.getNotafiscal() >= 0) {
                try {
                    CargaItens ite = new CargaItens();
                    ite.setDataAlteracao(new Date());
                    ite.setDataIntegracao(new Date());
                    ite.setDatacadastro(new Date());
                    ite.setDescricao("COLETA DE BATERIAS MOTO");
                    ite.setProduto("MOTO");
                    
                    if (PROCESSO.equals("CA")) {
                        ite.setDescricao("COLETA DE BATERIAS AUTO");
                        ite.setProduto("AUTO");
                    }
                    
                    ite.setEmpresa(cargaRegistro.getEmpresa());
                    ite.setFilial(cargaRegistro.getFilial());
                    ite.setFornecedor(nf.getCodigocliente());
                    ite.setHorascadastro(ConversaoHoras.ConverterHoras(txtHoraEntrada.getText()));
                    ite.setNota(Integer.valueOf(txtNotaFiscal.getText()));
                    ite.setNumerocarga(cargaRegistro.getNumerocarga());
                    ite.setPesoBruto(nf.getPesoLiquido());
                    ite.setPesoLiquido(nf.getPesoLiquido());
                    
                    ite.setQuantidadePrevista(100.0);
                    ite.setSequenciaItem(1);
                    ite.setSequenciaPeso(ite.getNumerocarga());
                    ite.setSequenciacarga(cargaItensDAO.proxCodCad());
                    ite.setUnidadeMedida("PC");
                    ite.setUsuarioAlteracao(0);
                    ite.setUsuarioIntegracao(0);
                    ite.setUsuariocadastro(0);
                    ite.setProduto(ite.getProduto());
                    retorno = cargaItensDAO.inserir(ite);
                } catch (NumberFormatException | SQLException | ParseException e) {
                    JOptionPane.showMessageDialog(null, "ERRO " + e);
                }
            }
        }
        return retorno;
    }
    
    private void getCarregarListaProdutosPesos() throws SQLException, Exception {
        listaCargaItens = cargaItensDAO.getCargaItens("CARGA", " and usu_nrocar =" + this.cargaRegistro.getNumerocarga());
        carregarListaProdutosPesos();
        totalizadorPesos();
    }
    
    private void totalizadorPesos() throws SQLException {
        double peso = 0.0;
        for (CargaItens prg : listaCargaItens) {
            if (prg.getPesoEmbalagem() > 0) {
                peso += prg.getPesoEmbalagem();
            }
        }
        txtDescontoEmbalagem.setValue(peso);
    }
    
    public void carregarListaProdutosPesos() throws Exception {
        addNewReg = true;
        ImageIcon sitok = getImage("/images/sitBom.png");
        ImageIcon RuiIcon = getImage("/images/sitRuim.png");
        ImageIcon Conferindo = getImage("/images/bricks.png");
        
        int linhas = 0;
        redColunastab();
        
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableItens.getModel();
        modeloCarga.setNumRows(linhas);
        Object[] linha = new Object[15];
        for (CargaItens prg : listaCargaItens) {
            TableColumnModel columnModel = jTableItens.getColumnModel();
            IntegrarPesosRegistrar.JTableRenderer renderers = new IntegrarPesosRegistrar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);
            columnModel.getColumn(2).setCellRenderer(renderers);
            
            linha[0] = Conferindo;
            linha[1] = prg.getNumerocarga();
            linha[2] = sitok;
            
            linha[3] = prg.getProduto();
            linha[4] = prg.getDescricao();
            linha[5] = prg.getQuantidadePrevista();
            linha[6] = prg.getPesoItem();
            linha[7] = prg.getUnidadeMedida();
            linha[8] = prg.getCodigoEmbalagem();
            linha[9] = prg.getPesoEmbalagem();
            linha[10] = prg.getFornecedor();
            linha[11] = prg.getNomeFornecedor();
            if (prg.getNomeFornecedor() == null) {
                prg.setNomeFornecedor("");
            }
            origemPesagem = prg.getFornecedor() + " - " + prg.getNomeFornecedor();
            linha[12] = prg.getNota();
            linha[13] = prg.getSituacaoPesagem();
            linha[14] = prg.getSequenciacarga();
            modeloCarga.addRow(linha);
        }
    }
    
    private boolean salvarAbertura() throws SQLException {
        CargaAbertura abe = new CargaAbertura();
        abe.setEmpresa(cargaRegistro.getEmpresa());
        abe.setFilial(cargaRegistro.getFilial());
        abe.setFornecedor(cargaRegistro.getFornecedor());
        abe.setNumerocarga(cargaRegistro.getNumerocarga());
        boolean retorno = cargaRegistroDAO.inserirCarga(abe);
        return retorno;
    }
    
    private void buscarPesagem(String placa) throws SQLException {
        cargaRegistro = new CargaRegistro();
        cargaRegistro = cargaRegistroDAO.getCargaRegistro("PLACA", " and car.usu_plavei = '" + placa + "' and car.usu_sitcar = 'A'");
        if (cargaRegistro != null) {
            if (cargaRegistro.getNumerocarga() > 0) {
                desabilitaCampo();
                Mensagem.mensagemRegistros("ERRO", "Veículo não deu saída");
                
            } else {
                habilitarCampo();
                txtMotorista.requestFocus();
            }
        } else {
            habilitarCampo();
            txtMotorista.requestFocus();
        }
    }
    
    private void habilitarCampo() {
        txtPlaca.setEnabled(true);
        txtMotorista.setEnabled(true);
        txtTranspotadora.setEnabled(true);
        btnGravarRegistro.setEnabled(true);
        txtNotaFiscal.setEnabled(true);
        txtInfo.setEnabled(true);
    }
    
    private void desabilitaCampo() {
        txtInfo.setEnabled(true);
        txtPlaca.setEnabled(false);
        txtMotorista.setEnabled(false);
        txtTranspotadora.setEnabled(false);
        btnGravarRegistro.setEnabled(false);
        txtNotaFiscal.setEnabled(false);
        txtPesoEntrada.setEnabled(false);
        txtPesoSaida.setEnabled(false);
        btnPesarSaida.setEnabled(false);
        btnPesOcp.setEnabled(false);
        btnPesPro.setEnabled(false);
        btnPesMin.setEnabled(false);
        
        jTableItens.setEnabled(false);
        btnEmb.setEnabled(false);
        btnImp.setEnabled(false);
        
        btnImprimir.setEnabled(false);
        btnEnvEma.setEnabled(false);
        btnPesPed.setEnabled(false);
        if (!txtTicket.getText().isEmpty()) {
            btnImprimir.setEnabled(true);
            btnEnvEma.setEnabled(true);
        }
        btnPesarEntrada.setEnabled(false);
        if (cargaRegistro.getPesoEntrada() == 0) {
            btnPesarEntrada.setEnabled(true);
            txtPesoEntrada.setEnabled(true);
        }
        if (cargaRegistro.getPesoSaida() > 0) {
            jTableItens.setEnabled(true);
            btnImp.setEnabled(true);
        }
        
    }
    
    private void popularTela() {
        txtOrdemCompra.setText(cargaRegistro.getOrdemCompra());
        txtTicket.setText(cargaRegistro.getNumerocarga().toString());
        txtMotorista.setText(cargaRegistro.getNomeMotorista());
        
        txtPesoLiquido.setValue(cargaRegistro.getPesoLiquidoCarga());
        txtPesoBruto.setValue(cargaRegistro.getPesoVeiculo());
        
        txtPesoDescontoImpureza.setValue(cargaRegistro.getPesoDescontoImpureza());
        
        txtDataEntrada.setDate(cargaRegistro.getDataEntrada());
        txtPlaca.setText(cargaRegistro.getPlaca());
        txtNotaFiscal.setText("0");
        if (cargaRegistro.getCodigoTransportadora() > 0) {
            transportadora = new Transportadora();
            transportadora = cargaRegistro.getTransportadora();
            txtTranspotadora.setText(transportadora.getCodigoTransportadora() + " - " + transportadora.getNomeTransportadora());
        } else {
            txtTranspotadora.setText(" 0 - PRÓPRIA");
        }
        
        if (TIPO_PESAGEM.equals("ENTRADA")) {
            txtPesoEntrada.setText(String.valueOf(cargaRegistro.getPesoEntrada()));
            txtDataEntrada.setDate(new Date());
            txtHoraEntrada.setText(this.utilDatas.retornarHoras(new Date()));
            
            txtPesoSaida.setText("0");
            txtDataSaida.setDate(null);
            txtHoraSaida.setText("");
        }
        if (TIPO_PESAGEM.equals("SAIDA")) {
            txtPesoEntrada.setText(String.valueOf(cargaRegistro.getPesoEntrada()));
            txtDataEntrada.setDate(cargaRegistro.getDataEntrada());
            txtHoraEntrada.setText(ConversaoHoras.converterMinutosHora(cargaRegistro.getHoraEntrada()));
            txtDataSaida.setDate(new Date());
            txtHoraSaida.setText(this.utilDatas.retornarHoras(new Date()));
            if (cargaRegistro.getPesoSaida() > 0) {
                txtDataSaida.setDate(cargaRegistro.getDataSaida());
                txtHoraSaida.setText(ConversaoHoras.converterMinutosHora(cargaRegistro.getHoraSaida()));
                txtPesoSaida.setValue(this.cargaRegistro.getPesoSaida());
            }
        }
        
        if (cargaRegistro.getPesoEntrada() > 0) {
            txtPesoEntrada.setText(String.valueOf(cargaRegistro.getPesoEntrada()));
            
        }
        if (cargaRegistro.getObservacaoCarga() != null) {
            txtInfo.setText(cargaRegistro.getObservacaoCarga());
        }
        
    }
    
    private void limparCampos(String TIPO_PESAGEM) {
        
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableItens.getModel();
        modeloCarga.setNumRows(0);
        
        txtDataEntrada.setValue(null);
        txtDataSaida.setValue(null);
        txtHoraEntrada.setText("");
        txtHoraSaida.setText("");
        txtMotorista.setText("");
        txtTranspotadora.setText("");
        txtTicket.setText("0");
        txtPlaca.setText("");
        txtPesoLiquido.setText("");
        txtNotaFiscal.setText("");
        
        txtPesoBruto.setValue(0);
        txtDescontoEmbalagem.setValue(0);
        txtPesoDescontoImpureza.setValue(0);
        txtPesoLiquido.setValue(0);
        txtNotaFiscal.setText("0");
        if (TIPO_PESAGEM.equals("ENTRADA")) {
            txtDataEntrada.setDate(new Date());
            txtHoraEntrada.setText(this.utilDatas.retornarHoras(new Date()));
        }
        if (TIPO_PESAGEM.equals("SAIDA")) {
            txtDataSaida.setDate(new Date());
            txtHoraSaida.setText(this.utilDatas.retornarHoras(new Date()));
        }
        
    }
    
    public static void getPesoBalanca(String peso) {
        txtPesoAutomatico.setText(peso);
        
        if (TIPO_PESAGEM.equals("ENTRADA")) {
            txtPesoEntrada.setText(peso);
        } else {
            txtPesoSaida.setText(peso);
        }
    }
    
    public void retonarEmbalagem() throws Exception {
        getCarregarListaProdutosPesos();
        calcularPeso();
        atualizarPesoEmbalagem();
        atualizarPesoLiquido();
    }
    
    public void atualizarPesoEmbalagem() throws Exception {
        cargaRegistro.setPesoEmbalagen(txtDescontoEmbalagem.getDouble());
        if (!cargaRegistroDAO.atualizarPesoEmbalagem(cargaRegistro)) {
        } else {
        }
    }
    
    public void atualizarPesoLiquido() throws Exception {
        double peso = txtPesoLiquido.getDouble();
        if (peso < 0) {
            peso = peso * -1;
        }
        cargaRegistro.setPesoLiquidoCarga(peso);
        if (!cargaRegistroDAO.atualizarPesoLiquido(cargaRegistro)) {
        } else {
        }
    }
    
    public void retonarDescontos(Double pesoDesconto) throws Exception {
        txtPesoDescontoImpureza.setValue(pesoDesconto);
        atualizarPesoDesconto();
        calcularPeso();
        atualizarPesoLiquido();
    }
    
    public void atualizarPesoDesconto() throws Exception {
        cargaRegistro.setPesoDescontoImpureza(txtPesoDescontoImpureza.getDouble());
        if (!cargaRegistroDAO.atualizarPesoDesconto(cargaRegistro)) {
        } else {
        }
    }
    
    public void atualizarPesoEntrda() throws Exception {
        if (!txtPesoEntrada.getText().isEmpty()) {
            if (ManipularRegistros.gravarRegistros(" gravar peso de entrada para esse ")) {
                cargaRegistro.setPesoEntrada(Double.valueOf(txtPesoEntrada.getText().trim()));
                System.out.println("br.com.recebimento.frame.IntegrarPesosRegistrar.atualizarPesoEntrda()\n" + txtPesoEntrada.getText());
                
                if (!cargaRegistroDAO.atualizarPesoEntrada(cargaRegistro)) {
                } else {
                    System.out.println("br.com.recebimento.frame.IntegrarPesosRegistrar.atualizarPesoEntrda()\n peso ok");
                    calcularPeso();
                    atualizarPesoLiquido();
                    desabilitaCampo();
                    System.out.println("br.com.recebimento.frame.IntegrarPesosRegistrar.atualizarPesoEntrda()\n CalcularPeso, Atualizar, Desa OK");
                    
                    SalvaImagem.SalvaImagemCamIP("OC", txtOrdemCompra.getText() + " _TIC" + txtTicket.getText() + "-" + txtPlaca.getText() + "_E");
                    TirarPrint.tirarPrint(txtTicket.getText() + "-" + txtPlaca.getText() + "_E.png");
                }
            }
        }
    }
    
    public void atualizarPesoSaida() throws Exception {
        
        if (!txtPesoSaida.getText().isEmpty()) {
            if (ManipularRegistros.gravarRegistros(" gravar peso de saída para esse ")) {
                txtOrdemCompra.setEnabled(false);
                cargaRegistro.setPesoSaida(Double.valueOf(txtPesoSaida.getText().trim()));
                Double pesEnt = Double.valueOf(txtPesoEntrada.getText());
                Double pesSai = Double.valueOf(txtPesoSaida.getText());
                cargaRegistro.setPesoVeiculo(pesEnt - pesSai);
                cargaRegistro.setDataSaida(new Date());
                cargaRegistro.setHoraSaida(ConversaoHoras.ConverterHoras(txtHoraSaida.getText()));
                cargaRegistro.setSituacaoCarga("F");
                if (!cargaRegistroDAO.atualizarPesoSaída(cargaRegistro)) {
                } else {
                    calcularPeso();
                    atualizarPesoLiquido();
                    txtOrdemCompra.setEnabled(false);
                    desabilitaCampo();
                    SalvaImagem.SalvaImagemCamIP("", txtTicket.getText() + "-" + txtPlaca.getText() + "_S");
                    TirarPrint.tirarPrint(txtTicket.getText() + "-" + txtPlaca.getText() + "_S.png");
                    
                }
            }
        }
        
    }
    
    private void calcularPeso() {
        
        Double peso = cargaRegistro.getPesoEntrada() - cargaRegistro.getPesoSaida();
        if (peso < 0) {
            peso = peso * -1;
        }
        txtPesoBruto.setValue(peso);
        txtPesoLiquido.setValue(txtPesoBruto.getDouble() - (txtDescontoEmbalagem.getDouble() + txtPesoDescontoImpureza.getDouble()));
        
    }
    private Transportadora transportadora;
    
    void recebendoTransportes(String codigoTransportadora, String nomeTransportadora) {
        transportadora = new Transportadora();
        transportadora.setCodigoTransportadora(Integer.valueOf(codigoTransportadora));
        transportadora.setNomeTransportadora(nomeTransportadora);
        txtTranspotadora.setText(codigoTransportadora + " - " + nomeTransportadora);
    }
    
    private void getPedidosVenda(Pedido pedido) throws Exception {
        if (txtOrdemCompra.getText().equals("0")) {
            habilitarCampo();
            txtPlaca.requestFocus();
        } else {
            
            carregarProcessoColeta("", PROCESSO);
        }
    }
    private Connection con;
    
    private void openConnection() throws Exception {
        this.con = ConnectionOracleSap.openConnection();
    }
    
    public final void gerarRelatatorio(String valor) throws SQLException, Exception {
        try {
            openConnection();
            String caminho = new File("report//FichaPeso.jrxml").getAbsolutePath();
            if (cargaRegistro.getTipoCarga().equals("C")) {
                origemPesagem = "CARGA DE BATERIA DA " + cargaRegistro.getDocumentMotorista();
                caminho = new File("report//FichaPesoColeta.jrxml").getAbsolutePath();
            }
            
            try {
                HashMap param = new HashMap();
                param.put("vnCarga", valor);
                param.put("vHoraEntrada", txtHoraEntrada.getText());
                param.put("vHoraSaida", txtHoraSaida.getText());
                param.put("vTransportadora", txtTranspotadora.getText());
                param.put("vOrigem", origemPesagem);
                
                JasperReport relatorio = JasperCompileManager.compileReport(caminho);
                JasperPrint print = JasperFillManager.fillReport(relatorio, param, this.con);
                JasperViewer view = new JasperViewer(print, false);
                view.setExtendedState(MAXIMIZED_BOTH);
                view.setIconImage(null);
                //   view.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/logo16x16.png")));
                JasperViewer.setDefaultLookAndFeelDecorated(true);
                view.setTitle("Ticket " + valor);
                view.setVisible(true);
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(null, "Problemas, procure a T.I  " + ex);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Problemas, procure a T.I  " + ex);
        } finally {
            con.close();
        }
    }
    
    private void getRetornoOrdem(String SQL) {
        try {
            if (obrigarOrdem && txtOrdemCompra.getText().equals("0")) {
                JOptionPane.showMessageDialog(null, "Erro: Ordem de compra é obrigatório ",
                        "Erro:", JOptionPane.ERROR_MESSAGE);
            } else {
                if (TIPO_PESAGEM.equals("ENTRADA") && PROCESSO.equals("D")) {
                    getOrdemCompraItens("ORDEM", SQL);
                } else {// pegar a nota
                    getPedidosVenda(this.pedido);
                }
                btnPesOcp.setEnabled(true);
                btnPesPro.setEnabled(false);
                btnPesPed.setEnabled(false);
                if (txtOrdemCompra.getText().equals("0") && PROCESSO.equals("D")) {
                    btnPesOcp.setEnabled(true);
                    btnPesPro.setEnabled(true);
                } else {
                    btnPesPed.setEnabled(true);
                    if (PROCESSO.equals("D")) {
                        btnPesOcp.setEnabled(true);
                        btnPesPed.setEnabled(false);
                    }
                    
                    btnPesPro.setEnabled(true);
                    txtPlaca.setEnabled(true);
                    txtPlaca.requestFocus();
                }
                
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            
        }
    }
    
    class ButtonRenderer extends JButton implements TableCellRenderer {
        
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            //  System.out.println(" value " + value.toString());
            return this;
        }
    }
    
    public void setPosicao() {
        Dimension d = this.getDesktopPane().getSize();
        this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
    }
    
    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        
        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        
        jTableItens.setRowHeight(42);
        jTableItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableItens.getColumnModel().getColumn(5).setCellRenderer(direita);
        jTableItens.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTableItens.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTableItens.getColumnModel().getColumn(8).setCellRenderer(direita);
        jTableItens.setAutoResizeMode(0);
    }
    
    private ImageIcon getImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return (new ImageIcon(url));
        } else {
            return null;
            
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelGeral = new javax.swing.JPanel();
        jPanelLista = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableItens = new javax.swing.JTable();
        btnEmb = new javax.swing.JButton();
        btnImp = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        btnEnvEma = new javax.swing.JButton();
        jpAbertura = new javax.swing.JPanel();
        txtPlaca = new org.openswing.swing.client.TextControl();
        txtTicket = new org.openswing.swing.client.TextControl();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtHoraEntrada = new org.openswing.swing.client.TextControl();
        txtHoraSaida = new org.openswing.swing.client.TextControl();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtMotorista = new org.openswing.swing.client.TextControl();
        txtDataEntrada = new org.openswing.swing.client.DateControl();
        txtDataSaida = new org.openswing.swing.client.DateControl();
        txtOrdemCompra = new org.openswing.swing.client.TextControl();
        lblTipo = new javax.swing.JLabel();
        btnPesOcp = new javax.swing.JButton();
        txtNotaFiscal = new org.openswing.swing.client.TextControl();
        jLabel2 = new javax.swing.JLabel();
        btnPesquisarTrans1 = new javax.swing.JButton();
        btnPesPro = new javax.swing.JButton();
        btnPesPed = new javax.swing.JButton();
        btnPesMin = new javax.swing.JButton();
        txtTranspotadora = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanelProduto = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        btnPesarEntrada = new javax.swing.JButton();
        btnPesarSaida = new javax.swing.JButton();
        txtDescontoEmbalagem = new org.openswing.swing.client.NumericControl();
        txtPesoEntrada = new org.openswing.swing.client.TextControl();
        txtPesoSaida = new org.openswing.swing.client.TextControl();
        txtPesoDescontoImpureza = new org.openswing.swing.client.NumericControl();
        txtPesoLiquido = new org.openswing.swing.client.NumericControl();
        txtPesoAutomatico = new org.openswing.swing.client.TextControl();
        txtPesoBruto = new org.openswing.swing.client.NumericControl();
        jLabel5 = new javax.swing.JLabel();
        txtInfo = new org.openswing.swing.client.TextControl();
        btnGravarRegistro = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();

        setClosable(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(false);
        setTitle("Integrar Ordens");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        jPanelGeral.setPreferredSize(new java.awt.Dimension(590, 380));

        jPanelLista.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTableItens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Ticket", "#", "Produto", "Descriçao", "Qtdy", "Qtdy Emb", "Uni. Med", "Embalagem", "Peso", "Código", "Nome", "Nota", "Atenção", "SequenciaCarga"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, false, false, false, false, false, false, true, false
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
        jScrollPane1.setViewportView(jTableItens);
        if (jTableItens.getColumnModel().getColumnCount() > 0) {
            jTableItens.getColumnModel().getColumn(0).setMinWidth(50);
            jTableItens.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableItens.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableItens.getColumnModel().getColumn(1).setMinWidth(100);
            jTableItens.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableItens.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableItens.getColumnModel().getColumn(2).setMinWidth(0);
            jTableItens.getColumnModel().getColumn(2).setPreferredWidth(0);
            jTableItens.getColumnModel().getColumn(2).setMaxWidth(0);
            jTableItens.getColumnModel().getColumn(3).setMinWidth(100);
            jTableItens.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableItens.getColumnModel().getColumn(3).setMaxWidth(100);
            jTableItens.getColumnModel().getColumn(4).setMinWidth(300);
            jTableItens.getColumnModel().getColumn(4).setPreferredWidth(300);
            jTableItens.getColumnModel().getColumn(4).setMaxWidth(300);
            jTableItens.getColumnModel().getColumn(5).setMinWidth(100);
            jTableItens.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableItens.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableItens.getColumnModel().getColumn(6).setMinWidth(100);
            jTableItens.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableItens.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableItens.getColumnModel().getColumn(7).setMinWidth(50);
            jTableItens.getColumnModel().getColumn(7).setPreferredWidth(50);
            jTableItens.getColumnModel().getColumn(7).setMaxWidth(50);
            jTableItens.getColumnModel().getColumn(8).setMinWidth(100);
            jTableItens.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableItens.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableItens.getColumnModel().getColumn(9).setMinWidth(100);
            jTableItens.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableItens.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableItens.getColumnModel().getColumn(10).setMinWidth(100);
            jTableItens.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableItens.getColumnModel().getColumn(10).setMaxWidth(100);
            jTableItens.getColumnModel().getColumn(11).setMinWidth(300);
            jTableItens.getColumnModel().getColumn(11).setPreferredWidth(300);
            jTableItens.getColumnModel().getColumn(11).setMaxWidth(300);
            jTableItens.getColumnModel().getColumn(12).setMinWidth(0);
            jTableItens.getColumnModel().getColumn(12).setPreferredWidth(0);
            jTableItens.getColumnModel().getColumn(12).setMaxWidth(0);
            jTableItens.getColumnModel().getColumn(13).setMinWidth(0);
            jTableItens.getColumnModel().getColumn(13).setPreferredWidth(0);
            jTableItens.getColumnModel().getColumn(13).setMaxWidth(0);
            jTableItens.getColumnModel().getColumn(14).setMinWidth(0);
            jTableItens.getColumnModel().getColumn(14).setPreferredWidth(0);
            jTableItens.getColumnModel().getColumn(14).setMaxWidth(0);
        }

        javax.swing.GroupLayout jPanelListaLayout = new javax.swing.GroupLayout(jPanelLista);
        jPanelLista.setLayout(jPanelListaLayout);
        jPanelListaLayout.setHorizontalGroup(
            jPanelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 947, Short.MAX_VALUE)
        );
        jPanelListaLayout.setVerticalGroup(
            jPanelListaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        btnEmb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnEmb.setText("Emb");
        btnEmb.setEnabled(false);
        btnEmb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmbActionPerformed(evt);
            }
        });

        btnImp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnImp.setText("Ins");
        btnImp.setEnabled(false);
        btnImp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImpActionPerformed(evt);
            }
        });

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        btnImprimir.setText("Imp");
        btnImprimir.setEnabled(false);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnEnvEma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/email.png"))); // NOI18N
        btnEnvEma.setText("Ema");
        btnEnvEma.setEnabled(false);
        btnEnvEma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnvEmaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGeralLayout = new javax.swing.GroupLayout(jPanelGeral);
        jPanelGeral.setLayout(jPanelGeralLayout);
        jPanelGeralLayout.setHorizontalGroup(
            jPanelGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGeralLayout.createSequentialGroup()
                .addGroup(jPanelGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGeralLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanelGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEmb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnImp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEnvEma))
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGeralLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jPanelLista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        jPanelGeralLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEmb, btnEnvEma, btnImp, btnImprimir, btnSair});

        jPanelGeralLayout.setVerticalGroup(
            jPanelGeralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelLista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelGeralLayout.createSequentialGroup()
                .addComponent(btnEmb, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnImp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImprimir)
                .addGap(2, 2, 2)
                .addComponent(btnEnvEma, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSair))
        );

        jPanelGeralLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEmb, btnEnvEma, btnImp, btnImprimir, btnSair});

        jTabbedPane1.addTab("Produtos", jPanelGeral);

        jpAbertura.setBorder(javax.swing.BorderFactory.createTitledBorder("Abertura de Pesagem"));

        txtPlaca.setEnabled(false);
        txtPlaca.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtPlaca.setRequired(true);
        txtPlaca.setToolTipText("AAA-9999");
        txtPlaca.setUpperCase(true);
        txtPlaca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPlacaActionPerformed(evt);
            }
        });

        txtTicket.setEnabled(false);
        txtTicket.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTicket.setRequired(true);
        txtTicket.setToolTipText("TICKET DE PESAGEM");

        jLabel3.setText("Placa");

        jLabel4.setText("Ticket");

        txtHoraEntrada.setEnabled(false);
        txtHoraEntrada.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtHoraSaida.setEnabled(false);
        txtHoraSaida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel6.setText("Data Entrada");

        jLabel7.setText("Hora Entrada");

        jLabel8.setText("Data Saída");

        jLabel9.setText("Hora Saída");

        jLabel10.setText("Motorista");

        txtMotorista.setEnabled(false);
        txtMotorista.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtMotorista.setRequired(true);
        txtMotorista.setToolTipText("MOTORISTA");
        txtMotorista.setUpperCase(true);
        txtMotorista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMotoristaActionPerformed(evt);
            }
        });

        txtDataEntrada.setEnabled(false);
        txtDataEntrada.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtDataSaida.setEnabled(false);
        txtDataSaida.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        txtOrdemCompra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtOrdemCompra.setRequired(true);
        txtOrdemCompra.setToolTipText("INFORME A ORDEM DE COMPRA");
        txtOrdemCompra.setUpperCase(true);
        txtOrdemCompra.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOrdemCompraFocusGained(evt);
            }
        });
        txtOrdemCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOrdemCompraActionPerformed(evt);
            }
        });

        lblTipo.setText("Código");

        btnPesOcp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/chart_organisation.png"))); // NOI18N
        btnPesOcp.setEnabled(false);
        btnPesOcp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btnPesOcpFocusGained(evt);
            }
        });
        btnPesOcp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesOcpActionPerformed(evt);
            }
        });

        txtNotaFiscal.setEnabled(false);
        txtNotaFiscal.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtNotaFiscal.setRequired(true);
        txtNotaFiscal.setToolTipText("TRANSPORTADORA");
        txtNotaFiscal.setUpperCase(true);
        txtNotaFiscal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNotaFiscalActionPerformed(evt);
            }
        });

        jLabel2.setText("Nota Fiscal");

        btnPesquisarTrans1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page_white_find.png"))); // NOI18N
        btnPesquisarTrans1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarTrans1ActionPerformed(evt);
            }
        });

        btnPesPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/book.png"))); // NOI18N
        btnPesPro.setEnabled(false);
        btnPesPro.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btnPesProFocusGained(evt);
            }
        });
        btnPesPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesProActionPerformed(evt);
            }
        });

        btnPesPed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money_add.png"))); // NOI18N
        btnPesPed.setEnabled(false);
        btnPesPed.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btnPesPedFocusGained(evt);
            }
        });
        btnPesPed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesPedActionPerformed(evt);
            }
        });

        btnPesMin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/table_add.png"))); // NOI18N
        btnPesMin.setEnabled(false);
        btnPesMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btnPesMinFocusGained(evt);
            }
        });
        btnPesMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesMinActionPerformed(evt);
            }
        });

        txtTranspotadora.setEnabled(false);
        txtTranspotadora.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel1.setText("Transportadora");

        javax.swing.GroupLayout jpAberturaLayout = new javax.swing.GroupLayout(jpAbertura);
        jpAbertura.setLayout(jpAberturaLayout);
        jpAberturaLayout.setHorizontalGroup(
            jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpAberturaLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jpAberturaLayout.createSequentialGroup()
                        .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpAberturaLayout.createSequentialGroup()
                                .addComponent(lblTipo)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jpAberturaLayout.createSequentialGroup()
                                .addComponent(txtOrdemCompra, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPesMin, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(btnPesOcp, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(btnPesPro, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(btnPesPed, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(txtPlaca, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)))
                            .addComponent(txtMotorista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpAberturaLayout.createSequentialGroup()
                        .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTicket, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(txtDataEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtHoraEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDataSaida, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                            .addComponent(jLabel8))
                        .addGap(4, 4, 4))
                    .addGroup(jpAberturaLayout.createSequentialGroup()
                        .addComponent(txtTranspotadora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesquisarTrans1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jpAberturaLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNotaFiscal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHoraSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel9))
                .addGap(2, 2, 2))
        );
        jpAberturaLayout.setVerticalGroup(
            jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpAberturaLayout.createSequentialGroup()
                .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpAberturaLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(lblTipo)))
                    .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtOrdemCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTicket, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addComponent(txtDataEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtHoraEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDataSaida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtHoraSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesOcp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPesPro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPesPed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPesMin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpAberturaLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(2, 2, 2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpAberturaLayout.createSequentialGroup()
                        .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPesquisarTrans1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpAberturaLayout.createSequentialGroup()
                        .addGroup(jpAberturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNotaFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMotorista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTranspotadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel14.setText("Peso Automático");

        jLabel15.setText("Peso entrada");

        jLabel16.setText("Peso saída");

        jLabel17.setText("Desconto embalagem");

        jLabel18.setText("Desconto outros");

        jLabel19.setText("Peso Líquido");

        btnPesarEntrada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application_edit.png"))); // NOI18N
        btnPesarEntrada.setText("Pesar");
        btnPesarEntrada.setEnabled(false);
        btnPesarEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesarEntradaActionPerformed(evt);
            }
        });

        btnPesarSaida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application.png"))); // NOI18N
        btnPesarSaida.setText("Pesar");
        btnPesarSaida.setEnabled(false);
        btnPesarSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesarSaidaActionPerformed(evt);
            }
        });

        txtDescontoEmbalagem.setDecimals(2);
        txtDescontoEmbalagem.setEnabled(false);
        txtDescontoEmbalagem.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtDescontoEmbalagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescontoEmbalagemActionPerformed(evt);
            }
        });

        txtPesoEntrada.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPesoEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoEntradaActionPerformed(evt);
            }
        });

        txtPesoSaida.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        txtPesoDescontoImpureza.setDecimals(2);
        txtPesoDescontoImpureza.setEnabled(false);
        txtPesoDescontoImpureza.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        txtPesoLiquido.setDecimals(2);
        txtPesoLiquido.setEnabled(false);
        txtPesoLiquido.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        txtPesoAutomatico.setEnabled(false);
        txtPesoAutomatico.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        txtPesoBruto.setDecimals(2);
        txtPesoBruto.setEnabled(false);
        txtPesoBruto.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N

        jLabel5.setText("Peso Bruto");

        javax.swing.GroupLayout jPanelProdutoLayout = new javax.swing.GroupLayout(jPanelProduto);
        jPanelProduto.setLayout(jPanelProdutoLayout);
        jPanelProdutoLayout.setHorizontalGroup(
            jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProdutoLayout.createSequentialGroup()
                .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(txtPesoAutomatico, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProdutoLayout.createSequentialGroup()
                        .addComponent(txtPesoEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesarEntrada))
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProdutoLayout.createSequentialGroup()
                        .addComponent(txtPesoSaida, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesarSaida))
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesoBruto, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDescontoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesoDescontoImpureza, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel18))
                .addGap(2, 2, 2)
                .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesoLiquido, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                    .addComponent(jLabel19))
                .addGap(2, 2, 2))
        );
        jPanelProdutoLayout.setVerticalGroup(
            jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProdutoLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelProdutoLayout.createSequentialGroup()
                        .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(jLabel14)
                                .addComponent(jLabel19)
                                .addComponent(jLabel16)
                                .addComponent(jLabel5)
                                .addComponent(jLabel18))
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelProdutoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtPesoEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPesoAutomatico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtPesoSaida, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtPesoBruto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDescontoEmbalagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPesoDescontoImpureza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPesoLiquido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnPesarEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesarSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        jPanelProdutoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtDescontoEmbalagem, txtPesoDescontoImpureza, txtPesoEntrada, txtPesoLiquido, txtPesoSaida});

        jTabbedPane2.addTab("Pesagem", jPanelProduto);

        txtInfo.setEnabled(false);
        txtInfo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnGravarRegistro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        btnGravarRegistro.setText("Gravar");
        btnGravarRegistro.setEnabled(false);
        btnGravarRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarRegistroActionPerformed(evt);
            }
        });

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-salvar-como-16x16.png"))); // NOI18N
        btnAlterar.setEnabled(false);
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jpAbertura, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(txtInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAlterar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGravarRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jpAbertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterar)
                    .addComponent(btnGravarRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAlterar, btnGravarRegistro, txtInfo});

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
        //registrarAcoesDoTeclado(jPanelGeral);
    }//GEN-LAST:event_formKeyTyped

    private void txtPlacaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPlacaActionPerformed
        try {
            buscarPesagem(txtPlaca.getText());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtPlacaActionPerformed

    private void txtMotoristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMotoristaActionPerformed
        txtTranspotadora.requestFocus();
    }//GEN-LAST:event_txtMotoristaActionPerformed

    private void btnGravarRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarRegistroActionPerformed
        try {
            salvarRegistro();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGravarRegistroActionPerformed

    private void btnEmbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmbActionPerformed
        try {
            desabilitaCampo();
            IntegrarPesosDescarga sol = new IntegrarPesosDescarga();
            MDIFrame.add(sol, true);
            sol.setMaximum(false); // executa maximizado 
            sol.setSize(800, 500);
            sol.setPosicao();
            try {
                sol.setRecebePalavra(this, cargaItens);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro " + ex,
                        "Erro:", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (PropertyVetoException ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnEmbActionPerformed

    private void txtOrdemCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOrdemCompraActionPerformed
        if (!txtOrdemCompra.getText().isEmpty()) {
            String pesquisar = txtOrdemCompra.getText();
            String slq = "and ocp.codemp =  1 and   ocp.codfil = 1 and  ocp.numocp = " + txtOrdemCompra.getText();
            getRetornoOrdem(slq);
        } else {
            Mensagem.mensagemRegistros("ERROR", "Informe 0 para continuar");
        }
    }//GEN-LAST:event_txtOrdemCompraActionPerformed

    private void txtOrdemCompraFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOrdemCompraFocusGained
        if (TIPO_PESAGEM.equals("ENTRADA")) {
            
            this.addNewReg = true;
        } else {
            this.addNewReg = false;
        }

    }//GEN-LAST:event_txtOrdemCompraFocusGained

    private void btnPesOcpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesOcpActionPerformed
        
        try {
            IntegrarOrdemCompraGeral sol = new IntegrarOrdemCompraGeral();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado 
            //  sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavra(this, this.COMPLEMENTO);
            
        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnPesOcpActionPerformed

    private void txtNotaFiscalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNotaFiscalActionPerformed
        btnGravarRegistro.requestFocus();
    }//GEN-LAST:event_txtNotaFiscalActionPerformed
    

    private void jTableItensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableItensMouseClicked
        
        try {
            cargaItens = new CargaItens();
            int linhaSelSit = jTableItens.getSelectedRow();
            int colunaSelSit = jTableItens.getSelectedColumn();
            String seqcar = jTableItens.getValueAt(linhaSelSit, 14).toString();
            cargaItens = cargaItensDAO.getCargaItem("CARGA", "and usu_seqcar = " + seqcar);
            btnEmb.setEnabled(false);
            btnImp.setEnabled(true);
            
            if (cargaItens != null) {
                if ((cargaItens.getSequenciacarga() > 0) && (cargaRegistro.getPesoEntrada() > 0.0)) {
                    btnEmb.setEnabled(true);
                    if (txtPesoSaida.equals("0.0") || txtPesoSaida.getText().isEmpty() || txtPesoSaida.getText().equals("ERRO")) {
                        btnImp.setEnabled(false);
                        
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jTableItensMouseClicked

    private void btnImpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImpActionPerformed
        try {
            
            desabilitaCampo();
            IntegrarPesosImpureza sol = new IntegrarPesosImpureza();
            MDIFrame.add(sol, true);
            sol.setMaximum(false); // executa maximizado 
            sol.setSize(900, 600);
            sol.setPosicao();
            sol.setRecebePalavra(this, cargaItens, txtPesoLiquido.getDouble());
            
        } catch (PropertyVetoException ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImpActionPerformed

    private void btnPesarEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesarEntradaActionPerformed
        try {
            atualizarPesoEntrda();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPesarEntradaActionPerformed

    private void btnPesarSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesarSaidaActionPerformed
        try {
            atualizarPesoSaida();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPesarSaidaActionPerformed

    private void btnPesquisarTrans1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarTrans1ActionPerformed
        
        try {
            IntegrarTransportadora sol = new IntegrarTransportadora();
            MDIFrame.add(sol, true);
            sol.setMaximum(false); // executa maximizado 
            sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavra(this);
            
        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        

    }//GEN-LAST:event_btnPesquisarTrans1ActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        
        if (txtPesoEntrada.getText().equals("00000") || txtPesoEntrada.getText().equals("ERRO")) {
            boolean retorno = ManipularRegistros.pesos("Ticket sem registro de PESO, deseja Sair?");
            if (retorno) {
                if (veioCampo != null) {
                    try {
                        veioCampo.retornarPeso();
                        this.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Problemas." + ex);
                    }
                }
            }
        } else {
            if (veioCampo != null) {
                try {
                    veioCampo.retornarPeso();
                    this.dispose();
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Problemas." + ex);
                }
            }
        }
        

    }//GEN-LAST:event_btnSairActionPerformed

    private void txtPesoEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoEntradaActionPerformed
        try {
            atualizarPesoEntrda();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtPesoEntradaActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        try {
            gerarRelatatorio(txtTicket.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnPesProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesProActionPerformed
        try {
            IntegrarProduto sol = new IntegrarProduto();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado 
            //  sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavra(this, this.COMPLEMENTO);
            
        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
            
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPesProActionPerformed

    private void btnPesProFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnPesProFocusGained
        txtPlaca.requestFocus();
    }//GEN-LAST:event_btnPesProFocusGained

    private void btnPesOcpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnPesOcpFocusGained
        txtPlaca.requestFocus();
    }//GEN-LAST:event_btnPesOcpFocusGained

    private void btnPesPedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnPesPedFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPesPedFocusGained

    private void btnPesPedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesPedActionPerformed
        try {
            IntegrarPedidoVenda sol = new IntegrarPedidoVenda();
            MDIFrame.add(sol, true);
            sol.setMaximum(true); // executa maximizado 
            //   sol.setSize(800, 500);
            sol.setPosicao();
            sol.setRecebePalavra(this);
            
        } catch (PropertyVetoException ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPesPedActionPerformed

    private void btnEnvEmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnvEmaActionPerformed
        try {
            salvarRegistroEmail();
            
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnEnvEmaActionPerformed

    private void btnPesMinFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnPesMinFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPesMinFocusGained

    private void btnPesMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesMinActionPerformed
        
        try {
            frmMinutasGeral sol = new frmMinutasGeral();
            MDIFrame.add(sol, true);
            // sol.setMaximum(true); // executa maximizado
            sol.setSize(400, 200);
            sol.setPosicao();
            
            sol.setRecebePalavra(this, this.PROCESSO);
        } catch (Exception ex) {
            Logger.getLogger(IntegrarPesosRegistrar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPesMinActionPerformed

    private void txtDescontoEmbalagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescontoEmbalagemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescontoEmbalagemActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        if (ManipularRegistros.gravarRegistros(" Alterar ")) {
            try {
                this.addNewReg = false;
                salvarRegistroAlteracao();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro " + ex,
                        "Erro:", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro " + ex,
                        "Erro:", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAlterarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnEmb;
    private javax.swing.JButton btnEnvEma;
    private javax.swing.JButton btnGravarRegistro;
    private javax.swing.JButton btnImp;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnPesMin;
    private javax.swing.JButton btnPesOcp;
    private javax.swing.JButton btnPesPed;
    private javax.swing.JButton btnPesPro;
    private javax.swing.JButton btnPesarEntrada;
    private javax.swing.JButton btnPesarSaida;
    private javax.swing.JButton btnPesquisarTrans1;
    private javax.swing.JButton btnSair;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanelGeral;
    private javax.swing.JPanel jPanelLista;
    private javax.swing.JPanel jPanelProduto;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTableItens;
    private javax.swing.JPanel jpAbertura;
    private javax.swing.JLabel lblTipo;
    private org.openswing.swing.client.DateControl txtDataEntrada;
    private org.openswing.swing.client.DateControl txtDataSaida;
    private org.openswing.swing.client.NumericControl txtDescontoEmbalagem;
    private org.openswing.swing.client.TextControl txtHoraEntrada;
    private org.openswing.swing.client.TextControl txtHoraSaida;
    private org.openswing.swing.client.TextControl txtInfo;
    private org.openswing.swing.client.TextControl txtMotorista;
    private org.openswing.swing.client.TextControl txtNotaFiscal;
    private org.openswing.swing.client.TextControl txtOrdemCompra;
    private static org.openswing.swing.client.TextControl txtPesoAutomatico;
    private org.openswing.swing.client.NumericControl txtPesoBruto;
    private org.openswing.swing.client.NumericControl txtPesoDescontoImpureza;
    public static org.openswing.swing.client.TextControl txtPesoEntrada;
    private org.openswing.swing.client.NumericControl txtPesoLiquido;
    private static org.openswing.swing.client.TextControl txtPesoSaida;
    private org.openswing.swing.client.TextControl txtPlaca;
    private org.openswing.swing.client.TextControl txtTicket;
    private org.openswing.swing.client.TextControl txtTranspotadora;
    // End of variables declaration//GEN-END:variables
}
