package br.com.sgi.main;

import br.com.sgi.ChamarWebServiceERP;
import br.com.sgi.LerArquivoCriptografadoBD;
import br.com.sgi.bean.BancoDados;
import br.com.sgi.bean.Database;
import br.com.sgi.bean.Permissao;
import br.com.sgi.bean.Usuario;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.SalvaImagem;
import br.com.sgi.bean.ComputadorAcesso;
import br.com.sgi.bean.UsuarioWs;

import br.com.sgi.dao.UsuarioDAO;
import br.com.sgi.dao.UsuarioERPDAO;
import br.com.sgi.dao.VinculoGrupoDAO;
import java.io.IOException;

import org.openswing.swing.tree.java.OpenSwingTreeNode;
import java.util.*;
import org.openswing.swing.mdi.client.*;
import org.openswing.swing.util.client.*;
import org.openswing.swing.permissions.client.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.openswing.swing.mdi.java.ApplicationFunction;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openswing.swing.internationalization.java.*;
import org.openswing.swing.miscellaneous.client.TipInternalFrame;
import org.openswing.swing.miscellaneous.client.TipPanelContent;
import org.openswing.swing.util.java.Consts;

/**
 * <p>
 * Title: OpenSwing Demo</p>
 * <p>
 * Description: Used to start application from main method: it creates an MDI
 * Frame app.</p>
 * <p>
 * Copyright: Copyright (C) 2006 Mauro Carniel</p>
 * <p>
 * </p>
 *
 * @author Mauro Carniel
 * @version 1.0
 */
public class Menu implements MDIController, LoginController {

    private ClienteFachada clientFacade = null;
    private LerArquivoCriptografadoBD ler = null;
    private Connection conn = null;
    private Hashtable domains = new Hashtable();
    public static String username = null;
    public static String userpwd = null;
    private Properties idiomas = new Properties();
    private static Usuario usuario;
    private String senhaDig;
    private String usuarioDig;

    private UsuarioDAO usuarioDAO;
    private static UsuarioLogado usuarioLogado;
    private static Database db;

    private static UsuarioWs usuarioWs;

    private static SalvaImagem salvaImagem;
    private Integer codigoUsuario;

    private static ComputadorAcesso computadorAcesso;

    public Menu() throws IOException {

        ler = new LerArquivoCriptografadoBD();

        try {
            ler.LerArquivoCriptogrado();
            Menu.db = ler.getDb();
        } catch (IOException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }

        //createConnection();
        clientFacade = new ClienteFachada(conn);

        Hashtable xmlFiles = new Hashtable();
        xmlFiles.put("PT_BR", "recursos/Resources_pt_br.xml");
        //   xmlFiles.put("EN", "recursos/Resources_en.xml");
        //  xmlFiles.put("IT", "recursos/Resources_it.xml");

        ClientSettings clientSettings = new ClientSettings(
                new XMLResourcesFactory(xmlFiles, false),
                domains);

        //  idiomas.setProperty("EN", "English");
        //  idiomas.setProperty("IT", "Italiano");
        idiomas.setProperty("PT_BR", "Português do Brasil");
        ClientSettings.getInstance().setLanguage("PT_BR");

        try {
            ClientSettings.ICON_MENU_FILE_EXIT = "stop.gif";
            ClientSettings.ICON_MENU_FILE_CHANGE_LANGUAGE = "overlays.png";
            ClientSettings.ICON_MENU_FILE_CHANGE_USER = "user.png";
            ClientSettings.PERC_TREE_FOLDER = "folder3.gif";
            ClientSettings.ICON_FILENAME = "logo16x16.png";
            ClientSettings.ICON_POPUP_MENU_REDUCE_ICON = "logo16x16.png";
            ClientSettings.BACK_IMAGE_DISPOSITION = Consts.BACK_IMAGE_STRETCHED;
            ClientSettings.BACKGROUND = "back_ground_crm_erbs.png";
            ClientSettings.TREE_BACK = "treeback2.jpg";

            ClientSettings.VIEW_BACKGROUND_SEL_COLOR = true;
            ClientSettings.VIEW_MANDATORY_SYMBOL = true;
            ClientSettings.FILTER_PANEL_ON_GRID = true;
            ClientSettings.RELOAD_LAST_VO_ON_FORM = true;
            ClientSettings.ALLOW_OR_OPERATOR = false;
            ClientSettings.INCLUDE_IN_OPERATOR = false;
            ClientSettings.SHOW_SCROLLBARS_IN_MDI = true;

            try {
                javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel"); //oficial
                // ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        LoginDialog d = new LoginDialog(
                null,
                false,
                this,
                "SGI - Sistema de Gerenciamento Interno",
                "Login",
                'L',
                "Sair",
                'S',
                "Armazenar Informações",
                "Autenticação");

    }

    /**
     * Method called after MDI creation.
     */
    @Override
    public void afterMDIcreation(MDIFrame frame) {
        BancoDados b = new BancoDados();
        GenericStatusPanel userPanel = new GenericStatusPanel();
        userPanel.setColumns(50);
        MDIFrame.addStatusComponent(userPanel);
        userPanel.setText("USUARIO : " + username + " BASE: " + ConnectionOracleSap.user.toUpperCase() + " IP: " + ConnectionOracleSap.hostname.toUpperCase());
        MDIFrame.addStatusComponent(new Clock());

    }

    /**
     * @see JFrame getExtendedState method
     */
    @Override
    public int getExtendedState() {
        return JFrame.MAXIMIZED_BOTH;
    }

    /**
     * @return client facade, invoked by the MDI Frame tree/menu
     */
    @Override
    public ClientFacade getClientFacade() {
        return clientFacade;
    }

    /**
     * Method used to destroy application.
     */
    @Override
    public void stopApplication() {
        System.exit(0);
    }

    /**
     * Defines if application functions must be viewed inside a tree panel of
     * MDI Frame.
     *
     * @return <code>true</code> if application functions must be viewed inside
     * a tree panel of MDI Frame, <code>false</code> no tree is viewed
     */
    @Override
    public boolean viewFunctionsInTreePanel() {
        return false;
    }

    /**
     * Defines if application functions must be viewed in the menubar of MDI
     * Frame.
     *
     * @return <code>true</code> if application functions must be viewed in the
     * menubar of MDI Frame, <code>false</code> otherwise
     */
    @Override
    public boolean viewFunctionsInMenuBar() {
        return true;
    }

    /**
     * @return <code>true</code> if the MDI frame must show a login menu in the
     * menubar, <code>false</code> no login menu item will be added
     */
    @Override
    public boolean viewLoginInMenuBar() {
        return true;
    }

    /**
     * @return application title
     */
    @Override
    public String getMDIFrameTitle() {
        return "SGI - Sistema de Gerenciamento Interno";
    }

    /**
     * @return text to view in the about dialog window
     */
    @Override
    public String getAboutText() {
        return "ERBS Baterias - Sistema Interno \n"
                + "\n"
                + "Contato: Ramal 383 \n"
                + "Copyright: Copyright (C) 2020 \n"
                + "Autor: TI ERBS";
    }

    /**
     * @return image name to view in the about dialog window
     */
    @Override
    public String getAboutImage() {
        return "erbs.png";
    }

    /**
     * @param parentFrame parent frame
     * @return a dialog window to logon the application; the method can return
     * null if viewLoginInMenuBar returns false
     */
    @Override
    public JDialog viewLoginDialog(JFrame parentFrame) {

        LoginDialog d = new LoginDialog(
                parentFrame,
                false,
                this,
                "Autenticação",
                "Login",
                'L',
                "Sair",
                'S',
                "Armazenar Informações",
                "Pesagem"
        );

        return d;
    }

    /**
     * @return maximum number of failed login
     */
    @Override
    public int getMaxAttempts() {
        return 3;
    }

    /**
     * Method called by MDI Frame to authenticate the user.
     *
     * @param loginInfo login information, like username, password, ...
     * @return <code>true</code> if user is correcly authenticated,
     * <code>false</code> otherwise
     * @throws java.lang.Exception
     */
    @Override
    public boolean authenticateUser(Map loginInfo) throws Exception {
        boolean retornoLogin = true;
        String senhaDig;
        String usuarioDig;
        senhaDig = (String) loginInfo.get("password");
        usuarioDig = (String) loginInfo.get("username");

        usuarioWs = new UsuarioWs();
        usuarioWs.setNome(usuarioDig.trim());
        usuarioWs.setSenha(senhaDig.trim());

        ChamarWebServiceERP ws = new ChamarWebServiceERP();
        String retorno = ws.logarErp(usuarioDig, senhaDig);

        if (!retorno.equals("200")) {
            retornoLogin = false;
            JOptionPane.showMessageDialog(null, "Credenciais inválidas");
        } else {
            //   JOptionPane.showMessageDialog(null, retorno);

            usuario = new Usuario();
            UsuarioERPDAO dao = new UsuarioERPDAO();
            usuario = dao.getUsuario(usuarioDig);

            if (usuario != null) {
                if (usuario.getId() > 0) {
                    usuario.setSenha(senhaDig);
                    System.out.print(retorno);
                    this.username = usuarioDig;
                    this.userpwd = senhaDig;
                    codigoUsuario = usuario.getId();

                    ResourceBundle rb; // Tradução
                    Locale locale = Locale.getDefault();
                    rb = ResourceBundle.getBundle("recursos/Resources", locale);

                    if (rb != null) {
                        if (!rb.getString("id").equals("0")) {
                            Menu.computadorAcesso = new ComputadorAcesso();
                            Menu.computadorAcesso.setColunas(Integer.valueOf(rb.getString("colunas")));
                        }
                    }

                }

            } else {
                JOptionPane.showMessageDialog(null, retorno);
            }

        }

        return retornoLogin;
    }

    public static UsuarioWs getUsuarioWs() {
        return Menu.usuarioWs;
    }

    public static ComputadorAcesso getComputadorAcesso() {
        return Menu.computadorAcesso;
    }

    public static UsuarioLogado getUsrLogado() {
        return Menu.usuarioLogado;
    }

    public static Usuario getUsuario() {
        return Menu.usuario;
    }

    public static Database getDb() {
        return db;
    }

    public static void setDb(Database db) {
        Menu.db = db;
    }

    public static void main(String[] argv) throws IOException {
        new Menu();

    }

    /**
     * Method called by LoginDialog to notify the sucessful login.
     *
     * @param loginInfo login information, like username, password, ...
     */
    @Override
    public void loginSuccessful(Map loginInfo) {
        username = loginInfo.get("username").toString().toUpperCase();

        ClientSettings.getInstance().setLanguage("PT_BR");

        MDIFrame mdi = new MDIFrame(this);

        // show tip of the day internal frame... dica do dia//
        //   showTipFrame(); 
        //configura os botões da barra de ferramentas
        // mdi.addButtonToToolBar("sitBom.png", "XMLs a processar");
        // mdi.addButtonToToolBar("sitMedio.png", "Servidor de Arquivos");
        //    mdi.addSeparatorToToolBar();
        //    mdi.addButtonToToolBar("sitRuim.png", "Sair da Aplicação");
    }

    /**
     * @return <code>true</code> if the MDI frame must show a change language
     * menu in the menubar, <code>false</code> no change language menu item will
     * be added
     */
    @Override
    public boolean viewChangeLanguageInMenuBar() {
        return false;
    }

    /**
     * @return list of languages supported by the application
     */
    @Override
    public ArrayList getLanguages() {
        ArrayList list = new ArrayList();
        //list.add(new Language("EN", "English"));
        //list.add(new Language("IT", "Italiano"));
        list.add(new Language("PT_BR", "Portugês do Brasil"));
        return list;
    }

    /**
     * @return application functions (ApplicationFunction objects), organized as
     * a tree
     */
    @Override
    public DefaultTreeModel getApplicationFunctions() {
        DefaultMutableTreeNode root = new OpenSwingTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(root);

        VinculoGrupoDAO vinculoGrupoDAO = new VinculoGrupoDAO();

        List<Permissao> listPermissao = new ArrayList<Permissao>();
        try {
            listPermissao = vinculoGrupoDAO.getPermissaoAcesso("", codigoUsuario.toString());
        } catch (SQLException ex) {
            Logger.getLogger(Menu11.class.getName()).log(Level.SEVERE, null, ex);
        }

        Menu.usuarioLogado = new UsuarioLogado();
        Menu.usuarioLogado.setProcessoUsuario("ALL");

        //Menu pai
        ApplicationFunction n1 = new ApplicationFunction("Cadastro", null);
        ApplicationFunction n112 = new ApplicationFunction("Crachá de Funcionario", "funcionario", "icons8-cracha-16x16.png", "getCadastroFuncionario");
        ApplicationFunction n113 = new ApplicationFunction("Paramentros", "parametros", "application_edit.png", "getParamentros");
        ApplicationFunction n115 = new ApplicationFunction("Caixas de Pesagem", "caixa de pesagem", "icons8-caixa-cheia-16x16.png", "getCadastroCaixa");
        ApplicationFunction n116 = new ApplicationFunction("Telas", "telas", "icons8-monitor-16x16.png", "getCadastroTela");
        ApplicationFunction n117 = new ApplicationFunction("Vinculo Tela X Grupo", "vinculo tela X grupo", "icons8-monitor-16x16.png", "getVinculoTelaGrupo");
        n1.add(n113);
        n1.add(n112);
        n1.add(n115);
        n1.add(n116);
        n1.add(n117);

        ApplicationFunction n2 = new ApplicationFunction("Recebimento", null);
        ApplicationFunction n213 = new ApplicationFunction("Pesagem", "pesos", "application_edit.png", "getIntegrarPesos");
        ApplicationFunction n517N2 = new ApplicationFunction("Minuta Metais", "pedido", "cadeado.png", "getFrmMinutasExpedicaoMetais");
        ApplicationFunction n1112N2 = new ApplicationFunction("Minutas", "minutas", "leilao.png", "getFrmMinutasExpedicao");
        n2.add(n213);
        n2.add(n517N2);
        n2.add(n1112N2);

        ApplicationFunction n3 = new ApplicationFunction("Logística", null);
        ApplicationFunction n311 = new ApplicationFunction("Pedido", "pedido", "location.png", "getPedido");
        ApplicationFunction n315 = new ApplicationFunction("Pedidos de HUB", "pedido", "cadeado.png", "getPedidoFaturamentoHub");
        ApplicationFunction n312 = new ApplicationFunction("Cte", "Cte", "bricks.png", "getXmlCte");
        ApplicationFunction n313 = new ApplicationFunction("Minuta", "minuta", "chart_organisation.png", "getMinuta");
        ApplicationFunction n314 = new ApplicationFunction("Minuta Hub", "minuta", "connect.png", "getMinutaHub");

        n3.add(n311);
        n3.add(n315);
        n3.add(n312);
        n3.add(n313);
        n3.add(n314);

        ApplicationFunction n4 = new ApplicationFunction("Atendimento", null);
        ApplicationFunction n411 = new ApplicationFunction("Clientes", "clientes", "cliente.png", "getClientes");
        ApplicationFunction n412 = new ApplicationFunction("Atendimentos", "atendimentos", "calendar.png", "getAgendas");
        ApplicationFunction n413 = new ApplicationFunction("Leads", "leads", "lead.png", "getContas");
        ApplicationFunction n414 = new ApplicationFunction("Clientes Carteira", "Carteira Crm", "bricks.png", "getClientesGeral");
        n4.add(n411);
        n4.add(n412);
        n4.add(n413);
        n4.add(n414);

        ApplicationFunction n5 = new ApplicationFunction("Sucata", null);
        ApplicationFunction n512 = new ApplicationFunction("Sucata ECO", "sucata", "auto.png", "getSucataContaCorrente");
        ApplicationFunction n518 = new ApplicationFunction("Sucata Hub", "hub", "connect.png", "getMinutaHub");
        ApplicationFunction n519 = new ApplicationFunction("Minuta Hub", "minuta", "chart_organisation.png", "getMinuta");

        ApplicationFunction n513 = new ApplicationFunction("Sucata Metais", "sucata", "bateriaindu.png", "getSucataContaCorrenteIndustrializacao");
        ApplicationFunction n517 = new ApplicationFunction("Minuta Metais", "pedido", "cadeado.png", "getFrmMinutasExpedicaoMetais");

        ApplicationFunction n514 = new ApplicationFunction("Triagem Saída", "sucata", "leilao.png", "getSucataAnalises");
        ApplicationFunction n515 = new ApplicationFunction("Lancamento Sucata ECO", "lançamento sucata eco", "calculator.png", "getLancementoSucataEco");
        ApplicationFunction n516 = new ApplicationFunction("Triagem Entrada", "lançamento sucata eco", "bricks.png", "getSucataAnalisesEntrada");

        n5.add(n512);
        n5.add(n518);
        n5.add(n519);
        n5.add(n513);
        n5.add(n517);

        n5.add(n514);

        n5.add(n516);

        ApplicationFunction n6 = new ApplicationFunction("Balança", null);
        ApplicationFunction n611 = new ApplicationFunction("Pesagem de Refugo", "pesagem de Refugo", "balanca_industrial.png", "getBalancinha");
        n6.add(n611);

        ApplicationFunction n7 = new ApplicationFunction("PDV", null);
        ApplicationFunction n712 = new ApplicationFunction("SUCATA PDV", "sucata pdv", "bug_link.png", "getConsultaSucataPDV");
        n7.add(n712);

        ApplicationFunction n8 = new ApplicationFunction("Faturamento", null);
        ApplicationFunction n811 = new ApplicationFunction("Pedidos", "pedido", "book.png", "getPedidoFaturamento");
        ApplicationFunction n812 = new ApplicationFunction("Pedidos de HUB", "pedido", "cadeado.png", "getPedidoFaturamentoHub");
        ApplicationFunction n813 = new ApplicationFunction("Minutas Faturar", "minutas", "megaphonefat.png", "getFatPedidoFaturar");
        ApplicationFunction n814 = new ApplicationFunction("Report Pedido", "minuta", "connect.png", "getPedidoReport");
        n8.add(n811);
        n8.add(n812);
        n8.add(n813);
        n8.add(n814);

        ApplicationFunction n9 = new ApplicationFunction("Comissões", null);
        ApplicationFunction n911 = new ApplicationFunction("Comissões", "Apuração das Comissões", "bricks.png", "getComissao");
        n9.add(n911);

        ApplicationFunction n10 = new ApplicationFunction("Garantia", null);
        ApplicationFunction n1101 = new ApplicationFunction("Garantia", "Garantias", "box.png", "getGarantia");
        n10.add(n1101);

        ApplicationFunction n11 = new ApplicationFunction("Expedição", null);
        ApplicationFunction n1111 = new ApplicationFunction("Pedidos", "pedido", "indu.png", "getFatPedidoExpedicao");
        ApplicationFunction n1112 = new ApplicationFunction("Minutas", "minutas", "leilao.png", "getFrmMinutasExpedicao");
        ApplicationFunction n1113 = new ApplicationFunction("Minutas Faturar", "minutas", "megaphonefat.png", "getFatPedidoFaturar");

        n11.add(n1111);
        n11.add(n1112);
        n11.add(n1113);

        ApplicationFunction n12 = new ApplicationFunction("CD", null);
        ApplicationFunction n2111 = new ApplicationFunction("Sucata", "sucata", "calculator.png", "getSucataTriagemMinuta");
        n12.add(n2111);

        ApplicationFunction n13 = new ApplicationFunction("HUB", null);
        ApplicationFunction n3111 = new ApplicationFunction("Transferencia", "transferencia", "veiculo_envio_manual.png", "getHubTransferencia");
        n13.add(n3111);
        if (this.usuario != null) {
            if (this.usuario.getId() > 0) {
                if (this.usuario.getUltnum() == 999) {//admin
                    root.add(n1);
                    root.add(n2);
                    root.add(n3);
                    root.add(n4);
                    root.add(n5);
                    //root.add(n6);
                    root.add(n7);
                    root.add(n8);
                    root.add(n9);
                    root.add(n10);
                    root.add(n11);
                    root.add(n12);
                    root.add(n13);

                } else {
                    if (usuario.getId() == 42) { // pamela
                        root.add(n2);
                        root.add(n4);
                        root.add(n5);
                        root.add(n7);
                        root.add(n9);
                    } else if (usuario.getId() == 40) { // rose
                        root.add(n2);
                        root.add(n4);
                        root.add(n5);
                        root.add(n7);
                        root.add(n9);
                    } else {
                        if (listPermissao != null) {
                            for (Permissao permissao : listPermissao) {
                                String Codigo = permissao.getCodigoMenu();
                                //  String concat = Codigo.substring(0, 2);
                                switch (Codigo) {
                                    //n1.add(n11);
                                    case "n1":
                                        root.add(n1);
                                        break;
                                    case "n2":
                                        root.add(n2);
                                        break;
                                    case "n3":
                                        root.add(n3);
                                        root.add(n13);
                                        break;
                                    case "n4":
                                        root.add(n4);
                                        break;
                                    case "n5":
                                        Menu.usuarioLogado.setProcessoUsuario("MET");
                                        root.add(n5);
                                        break;
                                    case "n6":
                                        root.add(n6);
                                        break;
                                    case "n7":
                                        root.add(n7);
                                        break;
                                    case "n8":
                                        root.add(n8);
                                        break;
                                    case "n9":
                                        root.add(n9);
                                        break;
                                    case "n10":
                                        root.add(n10);
                                        break;
                                    case "n11":
                                        Menu.usuarioLogado.setProcessoUsuario("EXP");
                                        root.add(n11);
                                    case "n12":
                                        root.add(n12);
                                        break;
                                    default:
                                        break;

                                }

                            }

                        }
                    }
                }
            }
        }

        return model;
    }

    /**
     * Create the database connection
     */
    private void createConnection() {
    }

    /**
     * @return <code>true</code> if the MDI frame must show a panel in the
     * bottom, containing last opened window icons, <code>false</code> no panel
     * is showed
     */
    @Override
    public boolean viewOpenedWindowIcons() {
        return true;
    }

    @Override
    public boolean viewFileMenu() {
        return true;
    }

    /**
     * Show 'tip of the day' internal frame.
     */
    private void showTipFrame() {
        final TipInternalFrame tipFrame1 = new TipInternalFrame(new TipPanelContent() {
            /**
             * @return list of titles, for each tip
             */
            @Override
            public String[] getTitles() {
                return new String[]{
                    "ERBS Baterias - Sistema Interno",
                    "Sistema em desenvolvimento",
                    "By TI ERBS  "
                };
            }

            /**
             * @return list of tips
             */
            @Override

            public String[] getTips() {
                return new String[]{ /*        "ERBS Baterias - Sistema Interno  \n",
                    "<html><body>Tabelas de preço\n"
                    + "<ul>"
                    + "<li>Peça: - </li>\n"
                    + "<li>Peça: - </li>"
                    + "<li>Peça: - </li>\n"
                    + "<li>Peça: - </li>"
                    + "</ul></body></html>"
                 */};

            }

        });
        tipFrame1.setShowCheck(false);
        MDIFrame.add(tipFrame1);
    }
}
