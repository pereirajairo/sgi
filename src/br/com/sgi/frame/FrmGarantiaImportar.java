/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.Fornecedor;
import br.com.sgi.bean.Garantia;
import br.com.sgi.bean.GarantiaItens;
import br.com.sgi.bean.GarantiaPortal;
import br.com.sgi.bean.Imagem;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.FornecedorDAO;
import br.com.sgi.dao.GarantiaDAO;
import br.com.sgi.dao.GarantiaItensDAO;
import br.com.sgi.dao.GarantiaPortalDAO;
import br.com.sgi.dao.ImagemDAO;
import br.com.sgi.main.Menu;
import br.com.sgi.main.UsuarioLogado;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.Mensagem;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WsOrdemDeCompra;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;

/**
 *
 * @author jairosilva
 */
public final class FrmGarantiaImportar extends InternalFrame {

    private GarantiaItens garantiaItens;
    private GarantiaItensDAO garantiaItensDAO;

    private GarantiaPortal garantiaPortal;
    private GarantiaPortalDAO garantiaPortalDAO;
    private List<GarantiaPortal> lstGarantia = new ArrayList<GarantiaPortal>();
    private List<GarantiaPortal> lstGarantiaCliente = new ArrayList<GarantiaPortal>();
    private List<GarantiaPortal> lstGarantiaPortalItens = new ArrayList<GarantiaPortal>();

    private UtilDatas utilDatas;
    private boolean addnewreg = true;
    private String acao;
    private Integer id;

    private FrmGarantiaNovo veioCampoGarantiaNovo;
    private List<Imagem> listImagem = new ArrayList<Imagem>();
    private ImagemDAO imagemDAO;
    private Imagem imagem;

    private File f, destino;

    public FrmGarantiaImportar() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Garantia "));
            this.setSize(800, 500);
            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (garantiaPortalDAO == null) {
                this.garantiaPortalDAO = new GarantiaPortalDAO();
            }
            if (imagemDAO == null) {
                imagemDAO = new ImagemDAO();
            }
            limparCampos();
            txtDisposicao.setLineWrap(true);
            txtDisposicao.setWrapStyleWord(true);
            txtDescricaoProblema.setLineWrap(true);
            txtDescricaoProblema.setWrapStyleWord(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    public void setRecebePalavra(FrmGarantiaNovo veioInput) throws Exception {
        this.veioCampoGarantiaNovo = veioInput;
        this.tipo = "ANALISE";
        btnOcorrencia.setEnabled(false);
        // getGarantia("analise", "AND gar.situacao in ('ANALISE','NOTA GERADA') AND gar.importada = 'N'  AND gar.notafiscalgarantia in (0) ");
        getGarantia("analise", "AND gar.situacao in ('ANALISE','PROCEDENTE') AND gar.importada = 'N'   ");

    }

    public void setRecebePalavraOcorrencias(FrmGarantiaNovo veioInput) throws Exception {
        this.veioCampoGarantiaNovo = veioInput;
        this.tipo = "OCORRENCIA";
        btnAnalisar.setEnabled(false);
        btnReceber.setEnabled(false);
        // getGarantia("analise", "AND gar.situacao in ('ANALISE','NOTA GERADA') AND gar.importada = 'N'  AND gar.notafiscalgarantia in (0) ");
        getGarantia("analise", "AND gar.situacao in ('NOTA GERADA','IMPROCEDENTE','PROCEDENTE') AND gar.importada = 'S'   ");

    }

    private void getGarantia(String pesquisa, String pesquisa_por) throws SQLException, Exception {
        limparCampos();
        jTabbedEtq.setSelectedIndex(0);
        lstGarantia = garantiaPortalDAO.getGarantiaPortals(pesquisa, pesquisa_por);

        carregarTabela();
        if (lstGarantia.size() > 0) {
        } else {
            btnImprocedente.setEnabled(true);
            if (jTableItem.getRowCount() > 0) {
                DefaultTableModel modelo = (DefaultTableModel) jTableItem.getModel();
                modelo.setNumRows(0);
            }
            Mensagem.mensagem("ERROR", "Não existem grantias para processar");
        }

    }

    private void getGarantiaReceber(String pesquisa, String pesquisa_por) throws SQLException, Exception {
        limparCampos();
        jTabbedEtq.setSelectedIndex(0);
        lstGarantia = garantiaPortalDAO.getGarantiaReceber(pesquisa, pesquisa_por);

        carregarTabela();
        if (lstGarantia.size() > 0) {
        } else {
            if (jTableItem.getRowCount() > 0) {
                DefaultTableModel modelo = (DefaultTableModel) jTableItem.getModel();
                modelo.setNumRows(0);
            }
            Mensagem.mensagem("ERROR", "Não existem grantias para processar");
        }

    }

    public void importarGarantiaParaERP(String empresa, String obs, String tiponota) throws SQLException, ParseException, Exception {
        if (!txtFornecedor.getText().isEmpty() && !txtNota.getText().isEmpty()) {
            Fornecedor fornecedor = new Fornecedor();
            FornecedorDAO fDao = new FornecedorDAO();
            fornecedor = fDao.getFornecedor("codigo", " and codfor = " + txtFornecedor.getText());

            boolean gravarSerie = true;
            boolean gravar = true;

            GarantiaDAO dao = new GarantiaDAO();

            Garantia gar = new Garantia();
            gar = dao.getGarantia("NOTA", " and usu_codfor =" + txtFornecedor.getText().trim() + " and usu_numnfc = " + txtNota.getText().trim() + " ");
            if (gar != null) {
                if (gar.getUsu_codfor() > 0) { // nota ja importada
                    gravar = false;
                    JOptionPane.showMessageDialog(null, "ERRO. Nota  " + gar.getUsu_numnfc() + " Lançada para o cliente " + gar.getUsu_codfor(),
                            "Atenção: ", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (gravar) {
                if (fornecedor != null) {
                    if (fornecedor.getCodfor() > 0) {
                        Garantia garantia = new Garantia();
                        garantia.setUsu_empdes(Integer.valueOf(empresa));
                        garantia.setUsu_codfil(1);

                        garantia.setUsu_cgccpf(fornecedor.getCgccpf());

                        garantia.setUsu_codfor(fornecedor.getCodfor());
                        garantia.setUsu_codpro("");
                        garantia.setUsu_codsnf("");
                        garantia.setUsu_datemi(new Date());

                        garantia.setUsu_garfis("");

                        garantia.setUsu_numdoc(0);

                        garantia.setUsu_numnfc(Integer.valueOf(txtNota.getText()));
                        garantia.setUsu_obsnfc(obs);
                        garantia.setUsu_retter("");
                        garantia.setUsu_sitnfc(1);
                        garantia.setUsu_tipnfc(tiponota);
                        if (!dao.inserir(garantia)) {
                        } else { // inseriu o cabeçalho 
                            gravarItensGarantia(garantia);
                            getGarantia("analise", "AND gar.situacao in ('ANALISE','NOTA GERADA') AND gar.importada = 'N'");

                        }
                    }
                }
            }

        } else {
            JOptionPane.showMessageDialog(null, "ERRO: Fornecedor/Nota não informado ",
                    "Atenção: ", JOptionPane.ERROR_MESSAGE);
        }

    }
    private static Usuario usuario;

    private void getProcessoUsuario() throws SQLException {
        this.usuario = new Usuario();
        this.usuario = Menu.getUsuario();
    }

    private void gravarItensGarantia(Garantia garantia) throws SQLException, ParseException {
        getProcessoUsuario();
        if (garantia != null) {
            if (garantia.getUsu_codfor() > 0) {
                if (jTableItem.getRowCount() > 0) {
                    garantiaItensDAO = new GarantiaItensDAO();
                    String id = "0";
                    Integer contador = 0;
                    Integer qtdreg = 0;
                    for (int i = 0; i < jTableItem.getRowCount(); i++) {
                        if ((Boolean) jTableItem.getValueAt(i, 8)) {
                            qtdreg++;
                        }
                    }
                    int seqite = 0;
                    for (int i = 0; i < jTableItem.getRowCount(); i++) {
                        if ((Boolean) jTableItem.getValueAt(i, 8)) {
                            id = (jTableItem.getValueAt(i, 1).toString());
                            if (!id.isEmpty()) { // tem pedido
                                GarantiaItens gar = new GarantiaItens();
                                // chaves - usu_empdes, usu_numnfc,usu_codfor,usu_seqite,usu_codfil
                                gar.setUsu_empdes(garantia.getUsu_empdes());
                                gar.setUsu_codfor(garantia.getUsu_codfor());
                                gar.setUsu_numnfc(garantia.getUsu_numnfc());
                                gar.setUsu_codfil(garantia.getUsu_codfil());
                                seqite++;
                                gar.setUsu_seqite(seqite);
                                //usu_codpro,usu_codemp,usu_excarq,usu_numsep,usu_itepro, 
                                gar.setUsu_codpro(jTableItem.getValueAt(i, 2).toString());
                                gar.setUsu_codemp(1);
                                gar.setUsu_excarq("");
                                gar.setUsu_numsep(jTableItem.getValueAt(i, 5).toString());
                                gar.setUsu_itepro("");
                                //usu_preuni,usu_codder,usu_codusu,usu_codmot,usu_codsnf
                                String preco = jTableItem.getValueAt(i, 20).toString();
                                gar.setUsu_preuni(FormatarNumeros.converterStringToDouble(preco));

                                gar.setUsu_codder(jTableItem.getValueAt(i, 4).toString());

                                gar.setUsu_codusu(this.usuario.getId());

                                gar.setUsu_codsnf(jTableItem.getValueAt(i, 10).toString());
                                //usu_forgar,usu_numocr,usu_insaut,usu_motant,usu_sitgar 
                                gar.setUsu_forgar("");
                                gar.setUsu_numocr(0);
                                gar.setUsu_insaut("");
                                gar.setUsu_motant(0);
                                gar.setUsu_sitgar("");
                                //usu_tmpgar,usu_datemi,usu_datlim,usu_tipnfc,usu_codcli,
                                gar.setUsu_tmpgar(jTableItem.getValueAt(i, 12).toString() + " meses ");

                                String data = jTableItem.getValueAt(i, 11).toString();
                                if (!data.isEmpty()) {
                                    gar.setUsu_datemi(this.utilDatas.converterDataddmmyyyy(data));
                                } else {
                                    gar.setUsu_datemi(new Date());
                                }

                                data = jTableItem.getValueAt(i, 13).toString();
                                if (!data.isEmpty()) {
                                    gar.setUsu_datlim(this.utilDatas.converterDataddmmyyyy(data));
                                } else {
                                    gar.setUsu_datlim(new Date());
                                }

                                gar.setUsu_tipnfc(garantia.getUsu_tipnfc());
                                gar.setUsu_codcli(Integer.valueOf(jTableItem.getValueAt(i, 6).toString()));
                                //usu_tipnfs,usu_numnfv,usu_desprb,usu_dessol,usu_tmpfat, 
                                gar.setUsu_tipnfs("S");
                                gar.setUsu_numnfv(Integer.valueOf(jTableItem.getValueAt(i, 9).toString()));
                                gar.setUsu_desprb(jTableItem.getValueAt(i, 19).toString());
                                gar.setUsu_dessol("");
                                gar.setUsu_tmpfat(jTableItem.getValueAt(i, 16).toString() + " meses ");
                                //usu_tmptot,usu_empemi,usu_obsipc,usu_certif,usu_datcer, 
                                gar.setUsu_tmptot("");
                                gar.setUsu_empemi(0);
                                gar.setUsu_obsipc("");
                                gar.setUsu_certif(0);
                                gar.setUsu_datcer(null);
                                //usu_datrom,usu_numoat,usu_numrom,usu_itediv, 
                                gar.setUsu_datrom(null);
                                gar.setUsu_numoat(0);
                                gar.setUsu_numrom(0);
                                gar.setUsu_itediv("");
                                String mot = jTableItem.getValueAt(i, 21).toString();
                                if (!mot.isEmpty()) {
                                    gar.setUsu_codmot(Integer.valueOf(mot));
                                } else {
                                    gar.setUsu_codmot(0);
                                }

                                contador++;
                                if (!garantiaItensDAO.inserir(gar, contador, qtdreg)) {

                                } else {
                                    GarantiaPortal garpor = new GarantiaPortal();
                                    garpor.setId(Integer.valueOf(id));
                                    garpor.setSituacao("S");
                                    garpor.setEmpresa_destino(garantia.getUsu_empdes());
                                    garpor.setSequencia_erp(gar.getUsu_seqite());
                                    if (!garantiaPortalDAO.importadaERP(garpor, 1, 1)) {

                                    }

                                }

                            }
                        }
                    }
                    if (qtdreg > 0) {
                    } else {
                        Mensagem.mensagem("ERROR", " Selecione a garantia");
                    }

                } else {
                    Mensagem.mensagem("ERROR", " Selecione a garantia");
                }
            }
        }

    }

    public void carregarTabela() throws Exception {
        redColunastab();
        int linhas = 0;
        ImageIcon NotaIcon = getImage("/images/pedido_faturado.png");

        ImageIcon FechIcon = getImage("/images/pedido_producao.png");

        DefaultTableModel modelo = (DefaultTableModel) jTableGarantia.getModel();
        modelo.setNumRows(linhas);

        for (GarantiaPortal gar : lstGarantia) {
            Object[] linha = new Object[12];
            TableColumnModel columnModel = jTableGarantia.getColumnModel();
            FrmGarantiaImportar.JTableRenderer renderers = new FrmGarantiaImportar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = NotaIcon;
            if (gar.getSituacao().equals("ANALISE")) {
                linha[0] = FechIcon;
            }

            linha[1] = gar.getId();
            linha[2] = gar.getGarantiacodigode();
            linha[3] = gar.getGaratiasde();
            linha[4] = this.utilDatas.converterDateToStr(gar.getDataabertura());
            linha[5] = gar.getNotafiscalgarantia();
            linha[6] = gar.getPontogarantia_id();
            linha[7] = gar.getTipo();
            linha[8] = gar.getAgrupador();
            linha[9] = gar.getFornecedor_erp();
            linha[10] = gar.getEmpresa_destino();
            modelo.addRow(linha);
        }

    }

    private String tipo;

    private void getGarantiaItens(String info) throws SQLException, Exception {
        String sql = "";
        switch (tipo) {
            case "ANALISE":
                sql = "AND gar.situacao in ('ANALISE') AND gar.importada = 'N'";
                lstGarantiaPortalItens = garantiaPortalDAO.getGarantiaPortalsItens("itens", " \nand gar.agrupador = " + txtAgrupador.getText()
                        + "\n and notafiscalgarantia = " + txtNota.getText() + "\n"
                        + " and pontogarantia_id = " + txtCodigo.getText() + " \n" + sql);
                break;
            case "NOTA GERADA":
                sql = "AND gar.situacao in ('PROCEDENTE') AND gar.importada = 'N'";
                lstGarantiaPortalItens = garantiaPortalDAO.getGarantiaPortalsItens("itens", "\n and notafiscalgarantia = " + txtNota.getText() + "\n"
                        + " and pontogarantia_id = " + txtCodigo.getText() + " \n" + sql);
                break;

            case "OCORRENCIA":
                sql = " AND gar.situacao in ('NOTA GERADA','IMPROCEDENTE','PROCEDENTE') AND gar.importada = 'S'";
                lstGarantiaPortalItens = garantiaPortalDAO.getGarantiaPortalsItens("itens", "\n and notafiscalgarantia = " + txtNota.getText() + "\n"
                        + " and pontogarantia_id = " + txtCodigo.getText() + " \n" + sql);
                break;
            default:
                sql = "AND  importada = 'S'";
                lstGarantiaPortalItens = garantiaPortalDAO.getGarantiaPortalsItens("itens", "\n and notafiscalgarantia = " + txtNota.getText() + "\n"
                        + " and pontogarantia_id = " + txtCodigo.getText() + " \n" + sql);
                break;
        }

        if (lstGarantiaPortalItens != null) {
            if (lstGarantiaPortalItens.size() > 0) {

                jTabbedEtq.setSelectedIndex(1);
                btnImprocedente.setEnabled(true);

            }

            carregarTabelaItens();
        }

    }

    public void carregarTabelaItens() throws Exception {
        redColunastab();
        int linhas = 0;
        ImageIcon NotaIcon = getImage("/images/accept.png");
        ImageIcon DigiIcon = getImage("/images/sitRuim.png");
        ImageIcon FechIcon = getImage("/images/sitAnd.png");
        ImageIcon InspIcon = getImage("/images/sitMedio.png");

        DefaultTableModel modelo = (DefaultTableModel) jTableItem.getModel();
        modelo.setNumRows(linhas);

        for (GarantiaPortal gar : lstGarantiaPortalItens) {
            Object[] linha = new Object[30];
            TableColumnModel columnModel = jTableItem.getColumnModel();
            FrmGarantiaImportar.JTableRenderer renderers = new FrmGarantiaImportar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = DigiIcon;
            if(gar.isGerarGarantia()){
                linha[0] = NotaIcon;
            }
            

            linha[1] = gar.getId();
            linha[2] = gar.getProduto();
            linha[3] = gar.getProdutodescricao();
            linha[4] = gar.getProdutoderivacao();
            linha[5] = gar.getSerie();
            linha[6] = gar.getCliente();
            linha[7] = gar.getClientenome();
            linha[8] = true;
            linha[9] = gar.getNota();
            linha[10] = gar.getNotaserie();
            linha[11] = this.utilDatas.converterDateToStr(gar.getNotaemissao());
            linha[12] = gar.getGarantiames();
            linha[13] = this.utilDatas.converterDateToStr(gar.getPrazogarantia());
            linha[14] = "30";
            linha[15] = this.utilDatas.converterDateToStr(gar.getPrazogarantiamaximo());
            linha[16] = gar.getTempousomes();
            linha[17] = gar.getSituacao();
            linha[18] = gar.getDescricaoproblema();
            linha[19] = gar.getSequenciaitem();
            linha[20] = gar.getPrecounitario();

            linha[21] = gar.getMotivo_codigo();
            linha[22] = gar.getMotivo_descricao();
            linha[23] = gar.getEmpresa_destino();
            linha[24] = gar.getSequencia_erp();
            modelo.addRow(linha);
        }

    }

    private void pesquisarTable(String pesquisa) {
        DefaultTableModel tabela_pedidos = (DefaultTableModel) jTableItem.getModel();

        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabela_pedidos);
        jTableItem.setRowSorter(sorter);
        //  pesquisa = txtNumSep.getText();

        if (pesquisa.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<TableModel, Object> rf = null;
                try {
                    rf = RowFilter.regexFilter(pesquisa, 5);
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                sorter.setRowFilter(rf);
            } catch (PatternSyntaxException pse) {
                System.err.println("Erro");
            }
        }
    }

    public void HabilitarTabPdu() {
        jTabbedEtq.setEnabledAt(0, true);
        jTabbedEtq.setEnabledAt(1, true);
        jTabbedEtq.setSelectedIndex(0);

    }

    public void HabilitarTabMat() {
        jTabbedEtq.setEnabledAt(0, true);
        jTabbedEtq.setEnabledAt(1, true);
        jTabbedEtq.setSelectedIndex(1);
    }

    private void limparCampos() {
        txtDisposicao.setText("");
        txtDescricaoProblema.setText("");
        txtProduto.setText("");
        txtDescricao.setText("");
        txtSerie.setText("");
        txtCodigo.setText("0");
        txtNome.setText("");
        txtAgrupador.setText("");
        txtNota.setText("0");
        txtId.setText("");
        DefaultTableModel modelo = (DefaultTableModel) jTableImagens.getModel();
        modelo.setNumRows(0);

        lblImagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bateriaindu.png")));
        lblCaminho.setText("");
        
         btnImprocedente.setEnabled(false);

    }

    public void redColunastab() {
        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();

        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableGarantia.setRowHeight(40);
        jTableGarantia.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTableItem.setRowHeight(40);
        jTableItem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableItem.setAutoResizeMode(0);
        jTableItem.setAutoCreateRowSorter(true);

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

    private void liberarGarantia(String acao) throws SQLException, Exception {
        if (jTableItem.getRowCount() > 0) {
            String id = "0";
            Integer contador = 0;
            Integer qtdreg = 0;
            for (int i = 0; i < jTableItem.getRowCount(); i++) {
                if ((Boolean) jTableItem.getValueAt(i, 8)) {
                    qtdreg++;
                }
            }
            for (int i = 0; i < jTableItem.getRowCount(); i++) {
                if ((Boolean) jTableItem.getValueAt(i, 8)) {
                    id = (jTableItem.getValueAt(i, 1).toString());
                    if (!id.isEmpty()) { // tem pedido
                        GarantiaPortal gar = new GarantiaPortal();
                        gar.setId(Integer.valueOf(id));
                        gar.setSituacao(acao);
                        gar.setDisposicao(txtDisposicao.getText());
                        contador++;
                        garantiaPortalDAO.liberar(gar, contador, qtdreg);
                    }
                }
            }
            if (qtdreg > 0) {
                getGarantiaItens("");
                getGarantia("analise", "AND gar.situacao in ('ANALISE','NOTA GERADA') AND gar.importada = 'N'");
                limparCampos();
            } else {
                Mensagem.mensagem("ERROR", " Selecione a garantia");
            }

        } else {
            Mensagem.mensagem("ERROR", " Selecione a garantia");
        }

    }

    private void garantiaImprocedente(String acao) throws SQLException, Exception {
        if (jTableItem.getRowCount() > 0) {
            String id = "0";
            Integer contador = 0;
            Integer qtdreg = 0;
            for (int i = 0; i < jTableItem.getRowCount(); i++) {
                if ((Boolean) jTableItem.getValueAt(i, 8)) {
                    qtdreg++;
                }
            }
            for (int i = 0; i < jTableItem.getRowCount(); i++) {
                if ((Boolean) jTableItem.getValueAt(i, 8)) {
                    id = (jTableItem.getValueAt(i, 1).toString());
                    if (!id.isEmpty()) { // tem pedido
                        GarantiaPortal gar = new GarantiaPortal();
                        gar.setId(Integer.valueOf(id));
                        gar.setSituacao(acao);
                        gar.setDisposicao(txtDisposicao.getText());
                        contador++;
                        if (!garantiaPortalDAO.liberar(gar, contador, qtdreg)) {

                        } else {
                            GarantiaItensDAO garIteDAO = new GarantiaItensDAO();

                            garantiaItens = new GarantiaItens();
                            String cod = jTableItem.getValueAt(i, 24).toString();
                            if (cod.isEmpty()) {
                                cod = "0";
                            }

                            garantiaItens.setUsu_seqite(Integer.valueOf(cod));
                            garantiaItens.setUsu_numnfc(Integer.valueOf(txtNota.getText()));
                            garantiaItens.setUsu_codfor(Integer.valueOf(txtFornecedor.getText()));
                            garantiaItens.setUsu_codfil(1);
                            garantiaItens.setUsu_empdes(Integer.valueOf(txtEmpresa.getText()));
                            garantiaItens.setUsu_numsep(gar.getSerie());
                            garantiaItens.setUsu_codpro(gar.getProduto());
                            garantiaItens.setUsu_codder(gar.getProdutoderivacao());
                            garantiaItens.setUsu_sitgar("FG");
                            garantiaItens.setUsu_obsipc(txtDisposicao.getText());

                            if (garIteDAO.alterar(garantiaItens)) {

                            }
                        }

                    }
                }
            }
            if (qtdreg > 0) {
                tipo = "OCORRENCIA_CADASTRO";
                getGarantiaItens("");
                //  getGarantia("analise", "AND gar.situacao in ('NOTA GERADA') AND gar.importada = 'S'");
                // limparCampos();
            } else {
                Mensagem.mensagem("ERROR", " Selecione a garantia");
            }

        } else {
            Mensagem.mensagem("ERROR", " Selecione a garantia");
        }

    }

    public void exibirFoto(String id) throws SQLException, IOException {
        imagem = new Imagem();
        imagem = imagemDAO.getImagem(Long.valueOf(id));
        if (imagem != null) {
            if (imagem.getId() > 0) {
                lblCaminho.setText(imagem.getNomeImagem());
                ImageIcon imageIcon = new ImageIcon(imagem.getCaminho()); // load the image to a imageIcon
                Image image = imageIcon.getImage(); // transform it 
                Image newimg = image.getScaledInstance(lblImagem.getWidth(), lblImagem.getHeight(), java.awt.Image.SCALE_SMOOTH); // scale it the smooth way 
                ImageIcon icon = new ImageIcon(newimg);
                lblImagem.setIcon(icon);
               
            }
        }

    }

    private void getImagens(Integer pedido) throws SQLException, Exception {
        listImagem = imagemDAO.getImagensGarantias("GAR", " and ima.registro_id = " + txtId.getText());
        if (listImagem != null) {
            carregarTabelaImagens();
        }
    }

    public void carregarTabelaImagens() throws Exception {
        DefaultTableModel modeloCarga = (DefaultTableModel) jTableImagens.getModel();
        modeloCarga.setNumRows(0);
        jTableImagens.setRowHeight(40);
        jTableImagens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ImageIcon CreIcon = getImage("/images/user_suit.png");

        for (Imagem ima : listImagem) {
            Object[] linha = new Object[4];
            TableColumnModel columnModel = jTableImagens.getColumnModel();
            FrmGarantiaImportar.JTableRenderer renderers = new FrmGarantiaImportar.JTableRenderer();
            columnModel.getColumn(0).setCellRenderer(renderers);

            linha[0] = ima.getId();
            linha[1] = ima.getNomeImagem();
            modeloCarga.addRow(linha);
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

        jPanel2 = new javax.swing.JPanel();
        jTabbedEtq = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableGarantia = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableItem = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        txtCodigoDisposicao = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDisposicao = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtDescricaoProblema = new javax.swing.JTextArea();
        txtProduto = new org.openswing.swing.client.TextControl();
        txtDescricao = new org.openswing.swing.client.TextControl();
        txtSerie = new org.openswing.swing.client.TextControl();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableImagens = new javax.swing.JTable();
        lblImagem = new javax.swing.JLabel();
        btnVerImagens = new javax.swing.JButton();
        lblCaminho = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnImportar = new javax.swing.JButton();
        btnFinalizar1 = new javax.swing.JButton();
        btnAnalisar = new javax.swing.JButton();
        btnLiberarGarantia = new javax.swing.JButton();
        btnNegar = new javax.swing.JButton();
        btnReceber = new javax.swing.JButton();
        btnOcorrencia = new javax.swing.JButton();
        btnImprocedente = new javax.swing.JButton();
        txtNome = new org.openswing.swing.client.TextControl();
        txtCodigo = new org.openswing.swing.client.TextControl();
        txtId = new org.openswing.swing.client.TextControl();
        txtNota = new org.openswing.swing.client.TextControl();
        txtAgrupador = new org.openswing.swing.client.TextControl();
        jLabel1 = new javax.swing.JLabel();
        Nome = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtFornecedor = new org.openswing.swing.client.TextControl();
        jLabel9 = new javax.swing.JLabel();
        txtEmpresa = new org.openswing.swing.client.TextControl();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setForeground(new java.awt.Color(0, 0, 153));

        jTabbedEtq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedEtqMouseClicked(evt);
            }
        });

        jTableGarantia.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jTableGarantia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "ID", "Codigo", "Nome", "Data", "Nota", "ID Acesso", "Tipo", "Agrupador", "Fornecedor", "Empresa"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableGarantia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableGarantiaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableGarantia);
        if (jTableGarantia.getColumnModel().getColumnCount() > 0) {
            jTableGarantia.getColumnModel().getColumn(0).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(0).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(1).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(2).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(2).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(4).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(5).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(6).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(7).setMinWidth(50);
            jTableGarantia.getColumnModel().getColumn(7).setPreferredWidth(50);
            jTableGarantia.getColumnModel().getColumn(7).setMaxWidth(50);
            jTableGarantia.getColumnModel().getColumn(8).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(9).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableGarantia.getColumnModel().getColumn(10).setMinWidth(100);
            jTableGarantia.getColumnModel().getColumn(10).setPreferredWidth(100);
            jTableGarantia.getColumnModel().getColumn(10).setMaxWidth(100);
        }

        jTabbedEtq.addTab("Garantias", jScrollPane1);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Produtos"));

        jTableItem.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jTableItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "id", "Produto", "Descrição", "Derivação", "Serie", "Cliente", "Razão Social", "Gravar", "Nota", "Série", "Emissão", "Garantia", "Limite Garantia", "Dias Aceite", "Limite Máximo", "Tempo Uso", "Situação", "Descrição do problema", "SeqIpv", "Preco", "Motivo", "Descricao", "Empresa", "Seque"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableItemMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableItem);
        if (jTableItem.getColumnModel().getColumnCount() > 0) {
            jTableItem.getColumnModel().getColumn(0).setMinWidth(50);
            jTableItem.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableItem.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableItem.getColumnModel().getColumn(1).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(1).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(2).setMinWidth(150);
            jTableItem.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTableItem.getColumnModel().getColumn(2).setMaxWidth(150);
            jTableItem.getColumnModel().getColumn(3).setMinWidth(300);
            jTableItem.getColumnModel().getColumn(3).setPreferredWidth(300);
            jTableItem.getColumnModel().getColumn(3).setMaxWidth(300);
            jTableItem.getColumnModel().getColumn(4).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(4).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(5).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(5).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(6).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(6).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(7).setMinWidth(300);
            jTableItem.getColumnModel().getColumn(7).setPreferredWidth(300);
            jTableItem.getColumnModel().getColumn(7).setMaxWidth(300);
            jTableItem.getColumnModel().getColumn(8).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(8).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(9).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(9).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(12).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(12).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(12).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(13).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(13).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(14).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(14).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(14).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(15).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(15).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(15).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(16).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(16).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(16).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(17).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(17).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(17).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(18).setMinWidth(200);
            jTableItem.getColumnModel().getColumn(18).setPreferredWidth(200);
            jTableItem.getColumnModel().getColumn(18).setMaxWidth(200);
            jTableItem.getColumnModel().getColumn(19).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(19).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(19).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(20).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(20).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(20).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(21).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(21).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(21).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(22).setMinWidth(200);
            jTableItem.getColumnModel().getColumn(22).setPreferredWidth(200);
            jTableItem.getColumnModel().getColumn(22).setMaxWidth(200);
            jTableItem.getColumnModel().getColumn(23).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(23).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(23).setMaxWidth(100);
            jTableItem.getColumnModel().getColumn(24).setMinWidth(100);
            jTableItem.getColumnModel().getColumn(24).setPreferredWidth(100);
            jTableItem.getColumnModel().getColumn(24).setMaxWidth(100);
        }

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 402, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Borda"));

        txtCodigoDisposicao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodigoDisposicao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BATERIA [ xxx ]  MES FORA DO PRAZO DE GARANTIA", "BATERIA APRESENTA SINAIS DE MAL USO" }));
        txtCodigoDisposicao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoDisposicaoActionPerformed(evt);
            }
        });

        jLabel2.setText("Disposição");

        txtDisposicao.setColumns(20);
        txtDisposicao.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDisposicao.setRows(3);
        jScrollPane2.setViewportView(txtDisposicao);

        txtDescricaoProblema.setEditable(false);
        txtDescricaoProblema.setColumns(20);
        txtDescricaoProblema.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDescricaoProblema.setLineWrap(true);
        txtDescricaoProblema.setRows(3);
        jScrollPane4.setViewportView(txtDescricaoProblema);

        txtProduto.setEnabled(false);
        txtProduto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtDescricao.setEnabled(false);
        txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtSerie.setEnabled(false);
        txtSerie.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel8.setText("Serie");

        jLabel7.setText("Descrição");

        jLabel6.setText("Produto");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtCodigoDisposicao, 0, 427, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane4)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCodigoDisposicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSerie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCodigoDisposicao, txtDescricao, txtProduto, txtSerie});

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedEtq.addTab("Itens", jPanel4);

        jTableImagens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Seq.", "Imagem"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableImagens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableImagensMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTableImagens);
        if (jTableImagens.getColumnModel().getColumnCount() > 0) {
            jTableImagens.getColumnModel().getColumn(0).setMinWidth(50);
            jTableImagens.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableImagens.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        lblImagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/work.png"))); // NOI18N
        lblImagem.setAutoscrolls(true);
        lblImagem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImagemMouseClicked(evt);
            }
        });

        btnVerImagens.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Importar.png"))); // NOI18N
        btnVerImagens.setText("Ver Imagens");
        btnVerImagens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerImagensActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerImagens, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCaminho, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(7, 7, 7))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVerImagens)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lblCaminho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImagem, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedEtq.addTab("Imagens", jPanel3);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnImportar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnImportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Importar.png"))); // NOI18N
        btnImportar.setText("IMPORTAR");
        btnImportar.setEnabled(false);
        btnImportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarActionPerformed(evt);
            }
        });

        btnFinalizar1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnFinalizar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        btnFinalizar1.setText("SAIR");
        btnFinalizar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizar1ActionPerformed(evt);
            }
        });

        btnAnalisar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnAnalisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-caixa-cheia-16x16.png"))); // NOI18N
        btnAnalisar.setText("ANALISAR");
        btnAnalisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalisarActionPerformed(evt);
            }
        });

        btnLiberarGarantia.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnLiberarGarantia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pedido_faturado.png"))); // NOI18N
        btnLiberarGarantia.setText("LIBERAR");
        btnLiberarGarantia.setEnabled(false);
        btnLiberarGarantia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiberarGarantiaActionPerformed(evt);
            }
        });

        btnNegar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnNegar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruby_delete.png"))); // NOI18N
        btnNegar.setText("NEGAR");
        btnNegar.setEnabled(false);
        btnNegar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNegarActionPerformed(evt);
            }
        });

        btnReceber.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnReceber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cadeado.png"))); // NOI18N
        btnReceber.setText("RECEBER");
        btnReceber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceberActionPerformed(evt);
            }
        });

        btnOcorrencia.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnOcorrencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cadeado.png"))); // NOI18N
        btnOcorrencia.setText("Ocorrências");
        btnOcorrencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOcorrenciaActionPerformed(evt);
            }
        });

        btnImprocedente.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnImprocedente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cadeado.png"))); // NOI18N
        btnImprocedente.setText("Improcedente");
        btnImprocedente.setEnabled(false);
        btnImprocedente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprocedenteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnAnalisar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLiberarGarantia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNegar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReceber)
                .addGap(6, 6, 6)
                .addComponent(btnImportar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOcorrencia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnImprocedente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFinalizar1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnImportar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnOcorrencia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprocedente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNegar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnReceber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnLiberarGarantia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAnalisar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFinalizar1))
                .addGap(2, 2, 2))
        );

        txtNome.setEnabled(false);
        txtNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtCodigo.setEnabled(false);
        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtId.setEnabled(false);
        txtId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtNota.setEnabled(false);
        txtNota.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtAgrupador.setEnabled(false);
        txtAgrupador.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel1.setText("Código");

        Nome.setText("Nome");

        jLabel3.setText("Agrupador");

        jLabel4.setText("Nota");

        jLabel5.setText("Id");

        txtFornecedor.setEnabled(false);
        txtFornecedor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel9.setText("Fornecedor");

        txtEmpresa.setEnabled(false);
        txtEmpresa.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel10.setText("Empresa");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedEtq, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(Nome)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAgrupador, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(2, 2, 2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Nome)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAgrupador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedEtq)
                        .addGap(4, 4, 4)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jTableGarantiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableGarantiaMouseClicked
        int linhaSelSit = jTableGarantia.getSelectedRow();
        int colunaSelSit = jTableGarantia.getSelectedColumn();
        limparCampos();

        idLct = Integer.valueOf(jTableGarantia.getValueAt(linhaSelSit, 1).toString());
        txtCodigo.setText(jTableGarantia.getValueAt(linhaSelSit, 2).toString());

        txtNome.setText(jTableGarantia.getValueAt(linhaSelSit, 3).toString());
        txtNota.setText(jTableGarantia.getValueAt(linhaSelSit, 5).toString());
        txtId.setText(jTableGarantia.getValueAt(linhaSelSit, 6).toString());
        String tipo = jTableGarantia.getValueAt(linhaSelSit, 7).toString();
        txtAgrupador.setText(jTableGarantia.getValueAt(linhaSelSit, 8).toString());
        txtFornecedor.setText(jTableGarantia.getValueAt(linhaSelSit, 9).toString());
        txtEmpresa.setText(jTableGarantia.getValueAt(linhaSelSit, 10).toString());
        txtDisposicao.setText("");
        txtDescricaoProblema.setText("");

        try {
            if (evt.getClickCount() == 2) {
                getGarantiaItens(tipo);
            }

        } catch (Exception ex) {
            Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jTableGarantiaMouseClicked

    private void jTabbedEtqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedEtqMouseClicked
        //
    }//GEN-LAST:event_jTabbedEtqMouseClicked

    private void btnImportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarActionPerformed
        try {
            FrmGarantiaImportarNota sol = new FrmGarantiaImportarNota();
            MDIFrame.add(sol, true);
            sol.setSize(600, 300);
            sol.setPosicao();
            // sol.setMaximum(true); // executa maximizado

            sol.setRecebePalavra(this, txtFornecedor.getText(), txtNota.getText(), txtNome.getText());
        } catch (PropertyVetoException ex) {
            Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FrmGarantiaNovo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImportarActionPerformed

    private Integer idLct = 0;
    private void jTableItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableItemMouseClicked
        int linhaSelSit = jTableItem.getSelectedRow();
        int colunaSelSit = jTableItem.getSelectedColumn();
        idLct = Integer.valueOf(jTableItem.getValueAt(linhaSelSit, 1).toString());

        txtId.setText(jTableItem.getValueAt(linhaSelSit, 1).toString());
        txtProduto.setText(jTableItem.getValueAt(linhaSelSit, 2).toString());
        txtDescricao.setText(jTableItem.getValueAt(linhaSelSit, 3).toString());
        txtSerie.setText(jTableItem.getValueAt(linhaSelSit, 5).toString());
        txtDescricaoProblema.setText(jTableItem.getValueAt(linhaSelSit, 18).toString());
        // txtEmpresa.setText(jTableItem.getValueAt(linhaSelSit, 23).toString());
        //txtSequenciaErp.setText(jTableItem.getValueAt(linhaSelSit, 24).toString());

        if (evt.getClickCount() == 2) {
            try {
                if (!txtId.getText().isEmpty()) {
                    getImagens(Integer.valueOf(txtId.getText()));
                } else {
                    getImagens(0);
                }

            } catch (Exception ex) {
                Logger.getLogger(SucataContaCorrente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }//GEN-LAST:event_jTableItemMouseClicked

    private void btnFinalizar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizar1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnFinalizar1ActionPerformed

    private void btnAnalisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalisarActionPerformed
        btnImportar.setEnabled(false);
        btnLiberarGarantia.setEnabled(true);
        btnNegar.setEnabled(true);
        this.tipo = "ANALISE";
        try {
            getGarantia("analise", "AND gar.situacao in ('ANALISE') AND gar.importada = 'N'  AND gar.notafiscalgarantia in ('0')");
        } catch (Exception ex) {
            Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAnalisarActionPerformed

    private void btnLiberarGarantiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiberarGarantiaActionPerformed
        if (txtCodigo.getText().equals("0")) {
            Mensagem.mensagem("ERROR", "Selecione a garantia para processar");
        } else {
            if (ManipularRegistros.pesos(" Deseja liberar as garantias ")) {
                try {
                    txtDisposicao.setText("GARANTIA LIBERADA PARA EMITIR A NOTA FISCAL");
                    liberarGarantia("PROCEDENTE");
                } catch (SQLException ex) {
                    Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }//GEN-LAST:event_btnLiberarGarantiaActionPerformed

    private void btnNegarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNegarActionPerformed
        if (txtDisposicao.getText().isEmpty()) {
            Mensagem.mensagem("ERROR", "Informe a disposição da garantia");
        } else {
            if (ManipularRegistros.pesos(" Deseja negar as garantias ")) {
                try {
                    liberarGarantia("IMPROCEDENTE");
                } catch (SQLException ex) {
                    Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnNegarActionPerformed

    private void txtCodigoDisposicaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoDisposicaoActionPerformed
        if (txtCodigoDisposicao.getSelectedIndex() != -1) {
            txtDisposicao.setText(txtCodigoDisposicao.getSelectedItem().toString());

        }
    }//GEN-LAST:event_txtCodigoDisposicaoActionPerformed

    private String nomeImagem;
    private void jTableImagensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableImagensMouseClicked
        int linhaSelSit = jTableImagens.getSelectedRow();
        int colunaSelSit = jTableImagens.getSelectedColumn();
        String id = jTableImagens.getValueAt(linhaSelSit, 0).toString();
        nomeImagem = jTableImagens.getValueAt(linhaSelSit, 1).toString();
        if (!id.isEmpty()) {
            try {
                exibirFoto(id);
            } catch (SQLException ex) {
                Mensagem.mensagem("ERROR", ex.toString());
            } catch (IOException ex) {
                Mensagem.mensagem("ERROR", ex.toString());
            }
        }
    }//GEN-LAST:event_jTableImagensMouseClicked

    private void btnVerImagensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerImagensActionPerformed
        try {

            if (!txtId.getText().isEmpty()) {
                getImagens(Integer.valueOf(txtId.getText()));
            } else {
                getImagens(0);
            }

        } catch (Exception ex) {
            Logger.getLogger(SucataContaCorrente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnVerImagensActionPerformed


    private void lblImagemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImagemMouseClicked
        if (evt.getClickCount() == 2) {
            try {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                desktop.open(new File(this.imagem.getCaminho()));
            } catch (IOException ex) {
                Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
            }
//              f = new File(lblCaminho.getText());
//            String NomeDoArquivo = f.getName();
//            File destino = new File("C:\\Temp\\" + NomeDoArquivo);
//            System.out.println("" + destino.toString());
//            destino = destino.getAbsoluteFile();
//            try {
//                Files.copy(f.toPath(), destino.toPath());
//                JOptionPane.showMessageDialog(null, "Salvo com sucesso!");
//            } catch (IOException ex) {
//
//                JOptionPane.showMessageDialog(null, "Erro! " + ex);
//            }
        }
    }//GEN-LAST:event_lblImagemMouseClicked

    private void btnReceberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceberActionPerformed
        btnImportar.setEnabled(true);
        btnLiberarGarantia.setEnabled(false);
        btnNegar.setEnabled(false);
        this.tipo = "NOTA GERADA";
        try {
            getGarantiaReceber("analise", "AND gar.situacao in ('PROCEDENTE') AND gar.importada = 'N'  AND gar.notafiscalgarantia not in  (0)  \n");
        } catch (Exception ex) {
            Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnReceberActionPerformed

    private void btnOcorrenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOcorrenciaActionPerformed
        btnImportar.setEnabled(false);
        btnLiberarGarantia.setEnabled(false);
        btnNegar.setEnabled(false);
        this.tipo = "OCORRENCIA";
        try {
            getGarantiaReceber("analise", "AND gar.situacao in ('PROCEDENTE') AND gar.importada = 'S'  AND gar.notafiscalgarantia not in  (0)  \n");
        } catch (Exception ex) {
            Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnOcorrenciaActionPerformed

    private void btnImprocedenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprocedenteActionPerformed
        if (txtDisposicao.getText().isEmpty()) {
            Mensagem.mensagem("ERROR", "Informe a disposição da garantia");
        } else {
            if (ManipularRegistros.pesos(" Deseja negar as garantias ")) {
                try {
                    garantiaImprocedente("IMPROCEDENTE");
                } catch (SQLException ex) {
                    Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(FrmGarantiaImportar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnImprocedenteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Nome;
    private javax.swing.JButton btnAnalisar;
    private javax.swing.JButton btnFinalizar1;
    private javax.swing.JButton btnImportar;
    private javax.swing.JButton btnImprocedente;
    private javax.swing.JButton btnLiberarGarantia;
    private javax.swing.JButton btnNegar;
    private javax.swing.JButton btnOcorrencia;
    private javax.swing.JButton btnReceber;
    private javax.swing.JButton btnVerImagens;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedEtq;
    private javax.swing.JTable jTableGarantia;
    private javax.swing.JTable jTableImagens;
    private javax.swing.JTable jTableItem;
    private javax.swing.JLabel lblCaminho;
    private javax.swing.JLabel lblImagem;
    private org.openswing.swing.client.TextControl txtAgrupador;
    private org.openswing.swing.client.TextControl txtCodigo;
    private javax.swing.JComboBox<String> txtCodigoDisposicao;
    private org.openswing.swing.client.TextControl txtDescricao;
    private javax.swing.JTextArea txtDescricaoProblema;
    private javax.swing.JTextArea txtDisposicao;
    private org.openswing.swing.client.TextControl txtEmpresa;
    private org.openswing.swing.client.TextControl txtFornecedor;
    private org.openswing.swing.client.TextControl txtId;
    private org.openswing.swing.client.TextControl txtNome;
    private org.openswing.swing.client.TextControl txtNota;
    private org.openswing.swing.client.TextControl txtProduto;
    private org.openswing.swing.client.TextControl txtSerie;
    // End of variables declaration//GEN-END:variables
}
