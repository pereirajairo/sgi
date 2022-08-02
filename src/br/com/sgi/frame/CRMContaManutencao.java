/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.frame;

import br.com.sgi.bean.AtendimentoLigacao;
import br.com.sgi.bean.BaseEstado;
import br.com.sgi.bean.Contas;
import br.com.sgi.bean.Motivo;
import br.com.sgi.bean.RamoAtividade;
import br.com.sgi.bean.Representante;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.AtendimentoLigacaoDAO;
import br.com.sgi.dao.ContasDAO;
import br.com.sgi.dao.MotivoDaoDAO;
import br.com.sgi.dao.RamoAtivadadeDAO;
import br.com.sgi.dao.RepresentanteDAO;
import br.com.sgi.dao.UsuarioERPDAO;
import static br.com.sgi.frame.BalancinhaFabrica.CalculoHora;
import static br.com.sgi.frame.CRMClientesAtendimento.CalculoHora;
import br.com.sgi.main.Menu;
import br.com.sgi.util.ConversaoHoras;
import br.com.sgi.util.ManipularRegistros;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.ws.WSCadastrarCliente;
import br.com.sgi.ws.WSEmailAtendimento;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
public final class CRMContaManutencao extends InternalFrame {

    private Contas contas;
    private List<Contas> listContas = new ArrayList<Contas>();
    private ContasDAO contasDAO;

    private UtilDatas utilDatas;

    private String datIni;
    private String datFim;

    private CRMConta veioCampo;

    private List<Motivo> lstMotivo = new ArrayList<Motivo>();
    private List<RamoAtividade> lstRamoAtividade = new ArrayList<RamoAtividade>();

    private AtendimentoLigacao atendimentoLigacao;
    private AtendimentoLigacaoDAO atendimentoLigacaoDAO;
    private List<AtendimentoLigacao> lstAtendimentoLigacao = new ArrayList<AtendimentoLigacao>();

    public CRMContaManutencao() {
        try {
            initComponents();
            setTitle(ClientSettings.getInstance().getResources().getResource("Portal |  Contas"));
            this.setSize(800, 500);

            if (utilDatas == null) {
                this.utilDatas = new UtilDatas();
            }
            if (contasDAO == null) {
                contasDAO = new ContasDAO();
            }
            if (atendimentoLigacaoDAO == null) {
                atendimentoLigacaoDAO = new AtendimentoLigacaoDAO();
            }
            getUsuarioLogado();
            getMotivos("L");
            getEstados();
            getRamosAtividade("");
            getRepresentantes("");

            txtObservacaoContato.setLineWrap(true);
            txtObservacaoContato.setWrapStyleWord(true);

            txtObservacaoSolucao.setLineWrap(true);
            txtObservacaoSolucao.setWrapStyleWord(true);
            jTabbedPane.setEnabledAt(1, false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERRO " + e.getMessage());
        }
    }

    private void getRepresentantes(String acao) throws SQLException {
        RepresentanteDAO dao = new RepresentanteDAO();
        List<Representante> lstRepresentante = new ArrayList<Representante>();
        lstRepresentante = dao.getRepresentantes("REP", "");

        for (Representante rep : lstRepresentante) {
            if (rep.getCodigo() > 0) {
                txtRepresentante.addItem(rep.getCodigo() + "-" + rep.getNome() + "-" + rep.getEmail());
            }
        }

        lstRepresentante = dao.getRepresentantes("VEN", "");

        for (Representante rep : lstRepresentante) {
            if (rep.getCodigo() > 0) {
                txtVendedor.addItem(rep.getCodigo() + "-" + rep.getNome() + "-" + rep.getEmail());
            }
        }
    }
    private boolean acao;

    public void setRecebePalavra(CRMConta veioInput, Contas contas, boolean acao) throws Exception {
        this.veioCampo = veioInput;
        setTitle(ClientSettings.getInstance().getResources().getResource("Cadastro de Conta"));
        this.contas = contas;
        this.AddNewReg = true;
        this.acao = acao;
        txtDataCadastro.setDate(new Date());
        txtDataRetorno.setDate(this.utilDatas.getHojeMais5());
        btnExcluir.setEnabled(false);
        if (contas != null) {
            if (contas.getUsu_codcli() > 0) {
                this.AddNewReg = false;
                btnErp.setEnabled(true);
                btnExcluir.setEnabled(true);
                popularTela();
                carregarLigacoes("", "\nand usu_codcli =" + txtCodigo.getText());
                setTitle(ClientSettings.getInstance().getResources().getResource("Manutenção de Conta " + txtNome.getText()));
            }
        }
        txtNome.requestFocus();

        if (acao) {
            btnExcluir.setEnabled(false);
        }

    }

    private void getSelecaoEmail(Contas contas) throws PropertyVetoException, Exception {
        CRMEmail sol = new CRMEmail();
        MDIFrame.add(sol, true);
        sol.setPosicao();
        // sol.setMaximum(true); // executa maximizado 
        sol.setSize(600, 400);
        sol.setRecebePalavraEmail(this);
    }

    public void retornarEmail(String email) {
        txtEmailPara.setText("");
        if (!email.isEmpty()) {
            txtEmailPara.setText(email);
        }
    }

    private boolean AddNewReg;

    private void salvar() throws SQLException, ParseException, Exception {
        btnErp.setEnabled(false);
        if (validarCampos()) {

            if (acao) {
                this.contas = new Contas();
                popularCampo();

                this.contas.setUsu_codcli(this.contasDAO.proxCodCad(0));
                if (this.contas.getUsu_codcli() < 0) {
                    this.contas.setUsu_codcli(1);
                }
                txtCodigo.setText(this.contas.getUsu_codcli().toString());

                if (!contasDAO.inserir(contas)) {

                } else {
                    btnErp.setEnabled(true);
                    this.gravarligacao = true;
                    salvarLigacao();
                    txtCodigo.setText(contas.getUsu_codcli().toString());
                    if (!txtEmailPara.getText().isEmpty()) {
                        WSEmailAtendimento wsEma = new WSEmailAtendimento();
                        wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
                    }
                }
            } else {
                if (AddNewReg) { //inserir
                    this.contas = new Contas();
                    popularCampo();

                    this.contas.setUsu_codcli(this.contasDAO.proxCodCad(0));
                    if (this.contas.getUsu_codcli() < 0) {
                        this.contas.setUsu_codcli(1);
                    }
                    txtCodigo.setText(this.contas.getUsu_codcli().toString());
                    if (!contasDAO.inserir(contas)) {

                    } else {
                        btnErp.setEnabled(true);
                        this.gravarligacao = true;
                        salvarLigacao();
                        txtCodigo.setText(contas.getUsu_codcli().toString());
                        if (!txtEmailPara.getText().isEmpty()) {
                            WSEmailAtendimento wsEma = new WSEmailAtendimento();
                            wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
                        }
                    }

                } else { // aletar
                    popularCampo();
                    if (!contasDAO.alterar(contas)) {

                    } else {
                        btnErp.setEnabled(true);
                        if (!txtEmailPara.getText().isEmpty()) {
                            WSEmailAtendimento wsEma = new WSEmailAtendimento();
                            wsEma.executar(Menu.username.toLowerCase(), Menu.userpwd);
                        }
                    }
                }

            }
        }
    }

    private void excluir() throws SQLException {
        if (!this.contasDAO.remover(contas)) {

        } else {
            if (!atendimentoLigacaoDAO.deletar(Integer.valueOf(txtCodigo.getText()), "LEAD")) {

            } else {

            }
            validarCliente("");

        }
    }

    private void exportarERP() throws SQLException, ParseException, Exception {
        popularCampo();
        if (!this.contasDAO.exportarERP(contas)) {

        } else {
            WSCadastrarCliente integrar = new WSCadastrarCliente();
            txtLog.setText(integrar.executar("procauto", "3n3rg14"));
        }
    }

    private void popularCampo() throws ParseException {
        contas.setCadMotivo(null);
        contas.setUsu_nomcli(txtNome.getText());
        contas.setUsu_apecli(txtFantasia.getText());
        contas.setUsu_baicli(txtBairro.getText());
        contas.setUsu_cgccpf(txtCnpj.getText());
        contas.setUsu_insest(txtInscEstatdual.getText());
        contas.setUsu_cidcli(txtCidade.getText());

        if (txtCodigoErp.getValue() != null) {
            contas.setUsu_coderp(Integer.valueOf(txtCodigoErp.getValue().toString()));
        }

        contas.setUsu_concli(txtConatoCLiente.getText());
        contas.setUsu_datcad(txtDataCadastro.getDate());
        contas.setUsu_emacli(txtEmail.getText());
        contas.setUsu_endcli(txtEndereco.getText());

        if (usuario.getId() != null) {
            contas.setUsu_codusu(usuario.getId());
        } else {
            contas.setUsu_codusu(0);
        }
        contas.setUsu_horcad(CalculoHora(0));

        String cod = txtMotivo.getSelectedItem().toString();
        int index = cod.indexOf("-");
        String codcon = cod.substring(0, index);
        contas.setUsu_motobs(Integer.valueOf(codcon));

        cod = txtRamo.getSelectedItem().toString();
        index = cod.indexOf("-");
        codcon = cod.substring(0, index);
        contas.setUsu_codram(codcon.trim());

        cod = txtVendedor.getSelectedItem().toString();
        index = cod.indexOf("-");
        codcon = cod.substring(0, index);
        contas.setUsu_codven(Integer.valueOf(codcon.trim()));

        cod = txtRepresentante.getSelectedItem().toString();
        index = cod.indexOf("-");
        codcon = cod.substring(0, index);
        contas.setUsu_codrep(Integer.valueOf(codcon.trim()));

        contas.setUsu_nomcli(txtNome.getText());

        contas.setUsu_obscli(txtObservacaoContato.getText());

        if (txtSegmento.getSelectedItem().toString().isEmpty()) {
            txtSegmento.setSelectedItem("SELECIONE");
        }
        contas.setUsu_segatu(txtSegmento.getSelectedItem().toString());

        contas.setUsu_sigufs(txtEstado.getSelectedItem().toString());
        contas.setUsu_sitcon(txtSituacao.getSelectedItem().toString());

        contas.setUsu_solobs(txtObservacaoSolucao.getText());

        contas.setUsu_telcli(txtTelefone.getText());
        contas.setUsu_tipcon(txtTipoConta.getSelectedItem().toString());

        contas.setUsu_numend(txtNumero.getText());

        contas.setUsu_envema("N");
        contas.setUsu_emapar(txtEmailPara.getText());
        if (!contas.getUsu_emapar().isEmpty()) {
            contas.setUsu_envema("S");
        }
        if (txtDataRetorno != null) {
            contas.setUsu_datdis(txtDataRetorno.getDate());
        }

    }

    private boolean validarCampos() {
        boolean retorno = true;
        if (txtNome.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "nome obrigatório ",
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            retorno = false;
        } else {
            if (txtCidade.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cidade obrigatório ",
                        "Erro:", JOptionPane.ERROR_MESSAGE);
                retorno = false;
                txtCidade.requestFocus();

            } else if (txtTelefone.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Telefone obrigatório ",
                        "Erro:", JOptionPane.ERROR_MESSAGE);
                retorno = false;
                txtTelefone.requestFocus();
            } else if (txtEstado.getSelectedItem().equals("SELECIONE")) {
                JOptionPane.showMessageDialog(null, "Informe o Estado ",
                        "Erro:", JOptionPane.ERROR_MESSAGE);
                txtEstado.requestFocus();
                retorno = false;
            }
        }

        return retorno;

    }

    private void popularTela() throws SQLException {
        txtDataCadastro.setDate(contas.getUsu_datcad());
        txtCodigo.setText(contas.getUsu_codcli().toString());
        txtNumero.setText(contas.getUsu_numend());
        txtNome.setText(contas.getUsu_nomcli());
        txtBairro.setText(contas.getUsu_baicli());
        txtCidade.setText(contas.getUsu_cidcli());
        txtCodigoErp.setText((contas.getUsu_coderp().toString()));
        txtConatoCLiente.setText(contas.getUsu_concli());

        txtEmail.setText(contas.getUsu_emacli());
        txtEndereco.setText(contas.getUsu_endcli());
        txtMotivo.setSelectedItem(contas.getUsu_motobs().toString() + "-" + contas.getCadMotivo().getDescricao());

        txtObservacaoContato.setText(contas.getUsu_obscli());
        txtSegmento.setSelectedItem(contas.getUsu_segatu());
        txtEstado.setSelectedItem(contas.getUsu_sigufs());
        txtSituacao.setSelectedItem(contas.getUsu_sitcon());
        txtObservacaoSolucao.setText(contas.getUsu_solobs());

        txtTelefone.setText(contas.getUsu_telcli());
        txtTipoConta.setSelectedItem(contas.getUsu_tipcon());

        txtCnpj.setText(contas.getUsu_cgccpf());
        txtInscEstatdual.setText(contas.getUsu_insest());
        txtFantasia.setText(contas.getUsu_apecli());

        txtRamo.setSelectedItem(contas.getUsu_codram() + "-" + contas.getCadramoAtividade().getDescricao());

        Representante rep = new Representante();
        if (contas.getUsu_codven() > 0) {
            RepresentanteDAO dao = new RepresentanteDAO();
            rep = dao.getRepresentante("VEN", " and rep.codrep = " + contas.getUsu_codven());
            if (rep != null) {
                if (rep.getCodigo() > 0) {
                    txtVendedor.setSelectedItem(contas.getUsu_codven() + "-" + rep.getNome() + "-" + rep.getEmail().toLowerCase());
                }
            }

        }

        if (contas.getUsu_codrep() > 0) {
            txtRepresentante.setSelectedItem(contas.getUsu_codrep() + "-" + contas.getCadRepresentante().getNome() + "-" + contas.getCadRepresentante().getEmail().toLowerCase());

        }

        txtEmailPara.setText(contas.getUsu_emapar());
        if (contas.getUsu_datdis() != null) {
            txtDataRetorno.setDate(contas.getUsu_datdis());
        } else {
            txtDataRetorno.setDate(null);
        }

    }
    private Usuario usuario;

    private Usuario getUsuarioLogado() throws SQLException {
        usuario = new Usuario();
        UsuarioERPDAO dao = new UsuarioERPDAO();

        usuario = dao.getUsuario(Menu.username.toLowerCase());
        return usuario;
    }

    private void getEstados() {
        BaseEstado baseEstado = new BaseEstado();
        Map<String, String> mapas = baseEstado.getEstados();
        for (String uf : mapas.keySet()) {
            txtEstado.addItem(mapas.get(uf));
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

    }

    private void getRamosAtividade(String acao) throws SQLException {
        RamoAtivadadeDAO dao = new RamoAtivadadeDAO();
        lstRamoAtividade = dao.getRamoAtividades();

        for (RamoAtividade mot : lstRamoAtividade) {
            if (!mot.getCodigo().isEmpty()) {
                txtRamo.addItem(mot.getCodigo() + "-" + mot.getDescricao());
            }
        }

    }

    private void validarCliente(String retorno) {
        if (veioCampo != null) {
            try {
                veioCampo.retornarContas("", "");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Problemas." + ex);
            } finally {
                this.dispose();
            }
        }
    }

    private void novaLigacao(boolean gravarligacao) throws Exception {
        if (!txtCodigo.getText().equals("0")) {
            getUsuarioLogado();
            CRMLigacao sol = new CRMLigacao();
            MDIFrame.add(sol, true);
            sol.setSize(600, 400);
            sol.setPosicao();
            sol.setRecebePalavraConta(this, txtCodigo.getText(), txtNome.getText(),
                    txtCodigo.getText(),
                    this.usuario, gravarligacao, this.atendimentoLigacao, true, "LEAD");

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
        PESQUISA_POR += " and usu_tipcon = 'LEAD' \n";
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
            CRMContaManutencao.JTableRenderer renderers = new CRMContaManutencao.JTableRenderer();
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

            try {
                if (gravarligacao) {
                    getUsuarioLogado();
                    atendimentoLigacao = new AtendimentoLigacao();

                    atendimentoLigacao.setCodigolancamento(atendimentoLigacaoDAO.proxCodCad(atendimentoLigacao.getCodigoatendimento()));
                    atendimentoLigacao.setCodigoatendimento(atendimentoLigacao.getCodigolancamento());
                    atendimentoLigacao.setCodigocliente(Integer.valueOf(txtCodigo.getText()));

                    atendimentoLigacao.setDataligacao(new Date());
                    atendimentoLigacao.setDataconversao(new Date());
                    atendimentoLigacao.setDescricaoligacao("INICIO DE ATENDIMENTO AO LEAD");
                    atendimentoLigacao.setUsuario(this.usuario.getId());
                    atendimentoLigacao.setTipoconta("LEAD");
                    atendimentoLigacao.setPedido(0);
                    atendimentoLigacao.setConvertido("N");
                    atendimentoLigacao.setHoraligacao(CalculoHora(0));

                    if (!atendimentoLigacaoDAO.inserir(atendimentoLigacao)) {

                    } else {

                    }

                }
            } catch (SQLException ex) {

            } finally {
                carregarLigacoes("", " and usu_codcli = " + txtCodigo.getText());
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        txtCodigo = new org.openswing.swing.client.TextControl();
        txtNome = new org.openswing.swing.client.TextControl();
        txtTipoConta = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtEndereco = new org.openswing.swing.client.TextControl();
        txtNumero = new org.openswing.swing.client.TextControl();
        txtCidade = new org.openswing.swing.client.TextControl();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtBairro = new org.openswing.swing.client.TextControl();
        txtEstado = new javax.swing.JComboBox<>();
        txtTelefone = new org.openswing.swing.client.TextControl();
        txtEmail = new org.openswing.swing.client.TextControl();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtMotivo = new javax.swing.JComboBox<>();
        txtRamo = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtConatoCLiente = new org.openswing.swing.client.TextControl();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtCodigoErp = new org.openswing.swing.client.NumericControl();
        txtSituacao = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        txtDataCadastro = new org.openswing.swing.client.DateControl();
        jLabel15 = new javax.swing.JLabel();
        txtVendedor = new javax.swing.JComboBox<>();
        txtRepresentante = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtEmailPara = new org.openswing.swing.client.TextControl();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        btnEmail = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        txtDataRetorno = new org.openswing.swing.client.DateControl();
        jLabel25 = new javax.swing.JLabel();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObservacaoSolucao = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservacaoContato = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTableLigacoes = new javax.swing.JTable();
        btnAddLigacao = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        btnLigacoes = new javax.swing.JButton();
        txtSegmento = new javax.swing.JComboBox<>();
        btnErp = new javax.swing.JButton();
        txtFantasia = new org.openswing.swing.client.TextControl();
        jLabel26 = new javax.swing.JLabel();
        txtCnpj = new org.openswing.swing.client.TextControl();
        txtInscEstatdual = new org.openswing.swing.client.TextControl();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quote");
        setMinimumSize(new java.awt.Dimension(571, 60));
        setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));
        jPanel2.setPreferredSize(new java.awt.Dimension(590, 380));

        txtCodigo.setEnabled(false);
        txtCodigo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNome.setRequired(true);
        txtNome.setUpperCase(true);

        txtTipoConta.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTipoConta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LEAD", "SUSPEC", "PROSPEC" }));

        jLabel1.setText("ID");

        jLabel2.setText("Razão Social");

        jLabel3.setText("Tipo conta");

        txtEndereco.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEndereco.setRequired(true);
        txtEndereco.setUpperCase(true);

        txtNumero.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtNumero.setRequired(true);
        txtNumero.setUpperCase(true);

        txtCidade.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCidade.setRequired(true);
        txtCidade.setUpperCase(true);

        jLabel4.setText("Endereço");

        jLabel5.setText("Numero");

        jLabel6.setText("cidade");

        txtBairro.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtBairro.setRequired(true);
        txtBairro.setUpperCase(true);

        txtEstado.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE" }));

        txtTelefone.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtTelefone.setUpperCase(true);

        txtEmail.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel7.setText("Bairro");

        jLabel8.setText("Estado");

        jLabel9.setText("Telefone");

        jLabel10.setText("Email");

        txtMotivo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtRamo.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtRamo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));

        jLabel12.setText("Motivo");

        jLabel13.setText("Ramo");

        jLabel14.setText("Segmento");

        txtConatoCLiente.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtConatoCLiente.setUpperCase(true);

        jLabel16.setText("Contato");

        jLabel17.setText("Código ERP");

        txtCodigoErp.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        txtSituacao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GERADO", "RESOLVIDO", "CANCELADO" }));

        jLabel18.setText("Situação");

        txtDataCadastro.setEnabled(false);
        txtDataCadastro.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel15.setText("Data");

        txtVendedor.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtVendedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));

        txtRepresentante.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtRepresentante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0-SELECIONE" }));

        jLabel20.setText("Vendendor");

        jLabel21.setText("Representante");

        txtEmailPara.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel23.setText("Emial");

        jLabel24.setText("Emails");

        btnEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/email.png"))); // NOI18N
        btnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmailActionPerformed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N", "S" }));

        jLabel22.setText("Enviar o termo de consentimento?");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        jButton1.setText("Salvar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.gif"))); // NOI18N
        jButton2.setText("Sair");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.setEnabled(false);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        txtDataRetorno.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N

        jLabel25.setText("Retorno");

        txtObservacaoSolucao.setColumns(20);
        txtObservacaoSolucao.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtObservacaoSolucao.setLineWrap(true);
        txtObservacaoSolucao.setRows(10);
        txtObservacaoSolucao.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txtObservacaoSolucao);

        txtObservacaoContato.setColumns(20);
        txtObservacaoContato.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtObservacaoContato.setLineWrap(true);
        txtObservacaoContato.setRows(10);
        txtObservacaoContato.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtObservacaoContato);

        jLabel11.setText("Observação");

        jLabel19.setText("Solução");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel19))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)))
        );

        jTabbedPane.addTab("Atendimento", jPanel1);

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
            jTableLigacoes.getColumnModel().getColumn(0).setMinWidth(100);
            jTableLigacoes.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableLigacoes.getColumnModel().getColumn(0).setMaxWidth(100);
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
            jTableLigacoes.getColumnModel().getColumn(6).setMinWidth(100);
            jTableLigacoes.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTableLigacoes.getColumnModel().getColumn(6).setMaxWidth(100);
        }

        btnAddLigacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnAddLigacao.setText("Add");
        btnAddLigacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLigacaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 891, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnAddLigacao)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(2, 2, 2))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(btnAddLigacao)
                .addGap(2, 2, 2))
        );

        jTabbedPane.addTab("Ligações", jPanel3);

        txtLog.setColumns(20);
        txtLog.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtLog.setLineWrap(true);
        txtLog.setRows(10);
        txtLog.setWrapStyleWord(true);
        jScrollPane3.setViewportView(txtLog);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 895, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 895, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 74, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Log Integração", jPanel4);

        btnLigacoes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Telefone.png"))); // NOI18N
        btnLigacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLigacoesActionPerformed(evt);
            }
        });

        txtSegmento.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtSegmento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "AUTO", "MOTO", "AUTO,MOTO", "METAIS" }));

        btnErp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sitBom.png"))); // NOI18N
        btnErp.setText("ERP");
        btnErp.setEnabled(false);
        btnErp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnErpActionPerformed(evt);
            }
        });

        txtFantasia.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtFantasia.setRequired(true);
        txtFantasia.setUpperCase(true);

        jLabel26.setText("Fantasia");

        txtCnpj.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtCnpj.setRequired(true);
        txtCnpj.setUpperCase(true);

        txtInscEstatdual.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtInscEstatdual.setRequired(true);
        txtInscEstatdual.setUpperCase(true);

        jLabel27.setText("Inscrição Estadual");

        jLabel28.setText("CNPJ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(293, 293, 293)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(txtDataRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(txtDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtTipoConta, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(204, 204, 204))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEndereco, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFantasia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtCidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtCnpj, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(4, 4, 4))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtInscEstatdual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(2, 2, 2))))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtBairro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(129, 129, 129)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtTelefone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(129, 129, 129)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMotivo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(txtRamo, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSegmento, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)))
            .addComponent(jTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtConatoCLiente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLigacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigoErp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(txtSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(txtEmailPara, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addComponent(btnErp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluir))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtVendedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(txtRepresentante, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel26)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel15))
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTipoConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtDataRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(8, 8, 8)
                        .addComponent(txtFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtInscEstatdual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(3, 3, 3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addComponent(jLabel9))
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtConatoCLiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLigacoes)
                    .addComponent(txtCodigoErp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSegmento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRepresentante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEmail)
                    .addComponent(txtEmailPara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnErp)
                    .addComponent(btnExcluir)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCodigo, txtNome, txtTipoConta});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtBairro, txtEmail, txtEstado, txtTelefone});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtMotivo, txtRamo});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnLigacoes, txtCodigoErp, txtConatoCLiente, txtSituacao});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEmail, btnErp, btnExcluir, jButton1, jButton2, txtEmailPara});

        jTabbedPane1.addTab("Contas", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 917, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (ManipularRegistros.gravarRegistros("Gravar ")) {
            try {
                salvar();
            } catch (SQLException ex) {
                Logger.getLogger(CRMContaManutencao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(CRMContaManutencao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CRMContaManutencao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        validarCliente("");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmailActionPerformed
        try {
            getSelecaoEmail(null);
        } catch (Exception ex) {
            Logger.getLogger(CRMContaManutencao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnEmailActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        if (ManipularRegistros.gravarRegistros(" excluir ")) {
            try {
                excluir();
            } catch (SQLException ex) {
                Logger.getLogger(CRMContaManutencao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void jTableLigacoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableLigacoesMouseClicked
        int linhaSelSit = jTableLigacoes.getSelectedRow();
        int colunaSelSit = jTableLigacoes.getSelectedColumn();
        Integer lancamento = Integer.valueOf(jTableLigacoes.getValueAt(linhaSelSit, 1).toString());
        Integer atendimento = Integer.valueOf(jTableLigacoes.getValueAt(linhaSelSit, 5).toString());

        if (evt.getClickCount() == 2) {
            try {
                //   txtSequenciaLancamento.setText(String.valueOf(atendimento));

                buscarLicacao(lancamento, atendimento);
            } catch (SQLException ex) {
                Logger.getLogger(CRMClientesAtendimento.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CRMClientesAtendimento.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTableLigacoesMouseClicked

    private void btnAddLigacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLigacaoActionPerformed
        try {
            this.atendimentoLigacao = new AtendimentoLigacao();
            novaLigacao(true);
        } catch (Exception ex) {
            Logger.getLogger(CRMClientesAtendimento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAddLigacaoActionPerformed

    private void btnLigacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLigacoesActionPerformed
        jTabbedPane.setEnabledAt(1, true);
        jTabbedPane.setSelectedIndex(1);
    }//GEN-LAST:event_btnLigacoesActionPerformed

    private void btnErpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnErpActionPerformed
        if (ManipularRegistros.gravarRegistros(" Exportar para o ERP ")) {
            try {
                exportarERP();
            } catch (SQLException ex) {
                Logger.getLogger(CRMContaManutencao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CRMContaManutencao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnErpActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddLigacao;
    private javax.swing.JButton btnEmail;
    private javax.swing.JButton btnErp;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnLigacoes;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableLigacoes;
    private org.openswing.swing.client.TextControl txtBairro;
    private org.openswing.swing.client.TextControl txtCidade;
    private org.openswing.swing.client.TextControl txtCnpj;
    private org.openswing.swing.client.TextControl txtCodigo;
    private org.openswing.swing.client.NumericControl txtCodigoErp;
    private org.openswing.swing.client.TextControl txtConatoCLiente;
    private org.openswing.swing.client.DateControl txtDataCadastro;
    private org.openswing.swing.client.DateControl txtDataRetorno;
    private org.openswing.swing.client.TextControl txtEmail;
    private org.openswing.swing.client.TextControl txtEmailPara;
    private org.openswing.swing.client.TextControl txtEndereco;
    private javax.swing.JComboBox<String> txtEstado;
    private org.openswing.swing.client.TextControl txtFantasia;
    private org.openswing.swing.client.TextControl txtInscEstatdual;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JComboBox<String> txtMotivo;
    private org.openswing.swing.client.TextControl txtNome;
    private org.openswing.swing.client.TextControl txtNumero;
    private javax.swing.JTextArea txtObservacaoContato;
    private javax.swing.JTextArea txtObservacaoSolucao;
    private javax.swing.JComboBox<String> txtRamo;
    private javax.swing.JComboBox<String> txtRepresentante;
    private javax.swing.JComboBox<String> txtSegmento;
    private javax.swing.JComboBox<String> txtSituacao;
    private org.openswing.swing.client.TextControl txtTelefone;
    private javax.swing.JComboBox<String> txtTipoConta;
    private javax.swing.JComboBox<String> txtVendedor;
    // End of variables declaration//GEN-END:variables
}
