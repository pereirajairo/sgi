package br.com.sgi.main;

import br.com.sgi.ChamarWebServiceERP;
import br.com.sgi.LerArquivoCriptografadoBD;
import br.com.sgi.bean.BancoDados;
import br.com.sgi.bean.Database;
import br.com.sgi.bean.Permissao;
import br.com.sgi.bean.Usuario;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.SalvaImagem;
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
public class Menu11 implements MDIController, LoginController {

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

    public Menu11() throws IOException {

        ler = new LerArquivoCriptografadoBD();

        try {
            ler.LerArquivoCriptogrado();
            Menu11.db = ler.getDb();
        } catch (IOException ex) {
            Logger.getLogger(Menu11.class.getName()).log(Level.SEVERE, null, ex);
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
            // ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jtattoo.plaf.mint.MintLookAndFeel"; //
            // ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jtattoo.plaf.hifi.HiFiLookAndFeel"; //  tud preto
            //  ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jtattoo.plaf.mcwin.McWinLookAndFeel"; 
            //  ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jtattoo.plaf.acryl.AcrylLookAndFeel";// Bordas Preta
            //  javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
            // ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";
            //   ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";
            // ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jtattoo.plaf.smart.SmartLookAndFeel";
            //  ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "javax.swing.plaf.metal.MetalLookAndFeel";
            //ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jtattoo.plaf.mint.MintLookAndFeel"; //OK
            // ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel"; // preferifo

//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("windows".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            //   ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";
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
                    System.out.print(retorno);
                    this.username = usuarioDig;
                    this.userpwd = senhaDig;
                    codigoUsuario = usuario.getId();

                }

            } else {
                JOptionPane.showMessageDialog(null, retorno);
            }

        }

        return retornoLogin;
    }

    public static UsuarioWs getUsuarioWs() {
        return Menu11.usuarioWs;
    }

    public static UsuarioLogado getUsrLogado() {
        return Menu11.usuarioLogado;
    }

    public static Usuario getUsuario() {
        return Menu11.usuario;
    }

    public static Database getDb() {
        return db;
    }

    public static void setDb(Database db) {
        Menu11.db = db;
    }

    public static void main(String[] argv) throws IOException {
        new Menu11();

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

        //Menu pai
        ApplicationFunction n1 = new ApplicationFunction("Cadastro", null);
        ApplicationFunction n12 = new ApplicationFunction("Crachá de Funcionario", "funcionario", "icons8-cracha-16x16.png", "getCadastroFuncionario");
        ApplicationFunction n13 = new ApplicationFunction("Paramentros", "parametros", "application_edit.png", "getParamentros");
        ApplicationFunction n15 = new ApplicationFunction("Caixas de Pesagem", "caixa de pesagem", "icons8-caixa-cheia-16x16.png", "getCadastroCaixa");
        ApplicationFunction n16 = new ApplicationFunction("Telas", "telas", "icons8-monitor-16x16.png", "getCadastroTela");
        ApplicationFunction n17 = new ApplicationFunction("Vinculo Tela X Grupo", "vinculo tela X grupo", "icons8-monitor-16x16.png", "getVinculoTelaGrupo");
        n1.add(n13);
        n1.add(n12);
        n1.add(n15);
        n1.add(n16);
        n1.add(n17);

        ApplicationFunction n2 = new ApplicationFunction("Recebimento", null);
        ApplicationFunction n23 = new ApplicationFunction("Pesagem", "pesos", "application_edit.png", "getIntegrarPesos");
        n2.add(n23);

        ApplicationFunction n3 = new ApplicationFunction("Logística", null);
        ApplicationFunction n31 = new ApplicationFunction("Pedido", "pedido", "location.png", "getPedido");
        ApplicationFunction n32 = new ApplicationFunction("Cte", "Cte", "bricks.png", "getXmlCte");
        ApplicationFunction n33 = new ApplicationFunction("Minuta", "minuta", "chart_organisation.png", "getMinuta");
        n3.add(n31);
        n3.add(n32);
        n3.add(n33);

        ApplicationFunction n4 = new ApplicationFunction("Atendimento", null);
        ApplicationFunction n41 = new ApplicationFunction("Clientes", "clientes", "cliente.png", "getClientes");
        ApplicationFunction n42 = new ApplicationFunction("Atendimentos", "atendimentos", "calendar.png", "getAgendas");
        ApplicationFunction n43 = new ApplicationFunction("Leads", "leads", "lead.png", "getContas");
        ApplicationFunction n44 = new ApplicationFunction("Clientes Carteira", "Carteira Crm", "bricks.png", "getClientesGeral");

        n4.add(n41);
        n4.add(n42);
        n4.add(n43);
        n4.add(n44);

        ApplicationFunction n5 = new ApplicationFunction("Sucata", null);
        ApplicationFunction n52 = new ApplicationFunction("Auto", "sucata", "calculator.png", "getSucataContaCorrente");
        ApplicationFunction n53 = new ApplicationFunction("Industrialização", "sucata", "bricks.png", "getSucataContaCorrenteIndustrializacao");
        ApplicationFunction n54 = new ApplicationFunction("Triagem", "sucata", "leilao.png", "getSucataAnalises");
        ApplicationFunction n55 = new ApplicationFunction("Lancamento Sucata ECO", "lançamento sucata eco", "calculator.png", "getLancementoSucataEco");

        n5.add(n52);
        n5.add(n53);
        n5.add(n54);
        n5.add(n55);

        ApplicationFunction n6 = new ApplicationFunction("Balança", null);
        ApplicationFunction n61 = new ApplicationFunction("Pesagem de Refugo", "pesagem de Refugo", "balanca_industrial.png", "getBalancinha");
        n6.add(n61);

        ApplicationFunction n7 = new ApplicationFunction("PDV", null);
        ApplicationFunction n72 = new ApplicationFunction("SUCATA PDV", "sucata pdv", "bug_link.png", "getConsultaSucataPDV");
        n7.add(n72);

        ApplicationFunction n8 = new ApplicationFunction("Faturamento", null);
        ApplicationFunction n81 = new ApplicationFunction("Pedidos", "pedido", "book.png", "getPedidoFaturamento");
        ApplicationFunction n82 = new ApplicationFunction("Pedidos Hub", "pedido", "indu.png", "getPedidoFaturamentoHub");
        n8.add(n81);
        n8.add(n82);

        ApplicationFunction n9 = new ApplicationFunction("Comissões", null);
        ApplicationFunction n91 = new ApplicationFunction("Comissões", "Apuração das Comissões", "bricks.png", "getComissao");
        n9.add(n91);

        ApplicationFunction n10 = new ApplicationFunction("Garantia", null);
        ApplicationFunction n101 = new ApplicationFunction("Garantia", "Garantias", "box.png", "getGarantia");
        n10.add(n101);

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
                    } else if (usuario.getId() == 16) { // Pereira
                        root.add(n2);

                    } else if (usuario.getId() == 6) { // Pereira
                        root.add(n2);

                    } else {
                        if (listPermissao != null) {
                            for (Permissao permissao : listPermissao) {
                                String Codigo = permissao.getCodigoMenu();
                                String concat = Codigo.substring(0, 2);
                                switch (Codigo) {
                                    //n1.add(n11);
                                    case "n91":
                                        n9.add(n91);
                                        break;
                                    case "n11":
                                        break;
                                    case "n12":
                                        n1.add(n12);
                                        break;
                                    case "n13":
                                        n1.add(n13);
                                        break;
                                    case "n15":
                                        n1.add(n15);
                                        break;
                                    case "n16":
                                        n1.add(n16);
                                        break;
                                    case "n17":
                                        n1.add(n17);
                                        break;
                                    case "n23":
                                        n2.add(n23);
                                        break;
                                    case "n31":
                                        n3.add(n31);
                                        break;
                                    case "n41":
                                        n4.add(n41);
                                        break;
                                    case "n42":
                                        n4.add(n42);
                                        break;
                                    case "n43":
                                        n4.add(n43);
                                        break;
                                    case "n51":

                                        //    n5.add(n51);
                                        break;
                                    case "n52":
                                        n5.add(n52);
                                        break;
                                    case "n53":
                                        n5.add(n53);
                                        break;
                                    case "n54":
                                        n5.add(n54);
                                        break;
                                    case "n55":
                                        n5.add(n55);
                                        break;
                                    case "n61":
                                        n6.add(n61);
                                        break;
                                    case "n71":
                                        //  n7.add(n71);
                                        break;
                                    case "n72":
                                        n7.add(n72);
                                        break;
                                    default:
                                        break;
                                }

                            }
                            for (Permissao permissao : listPermissao) {
                                String Codigo = permissao.getCodigoMenu();
                                String concat = Codigo.substring(0, 2);
                                switch (concat) {
                                    case "n1":
                                        root.add(n1);
                                        break;
                                    case "n2":
                                        root.add(n2);
                                        break;
                                    case "n3":
                                        root.add(n3);
                                        break;
                                    case "n4":
                                        root.add(n4);
                                        break;
                                    case "n5":
                                        root.add(n5);
                                        //  root.add(n9);
                                        break;
                                    case "n6":
                                        root.add(n6);
                                        break;
                                    case "n7":
                                        root.add(n7);
                                        break;
                                    case "n9":
                                        root.add(n9);
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
