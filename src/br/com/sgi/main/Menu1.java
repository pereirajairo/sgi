package br.com.sgi.main;

import br.com.sgi.ChamarWebServiceERP;
import br.com.sgi.bean.Usuario;

import br.com.sgi.dao.UsuarioDAO;

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
import org.openswing.swing.internationalization.java.*;
import org.openswing.swing.miscellaneous.client.TipInternalFrame;
import org.openswing.swing.miscellaneous.client.TipPanelContent;

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
public class Menu1 implements MDIController, LoginController {

    private ClienteFachada clientFacade = null;
    private Connection conn = null;
    private Hashtable domains = new Hashtable();
    private String username = null;
    private Properties idiomas = new Properties();
    private static Usuario usuario;
    private UsuarioDAO usuarioDAO;
    private static UsuarioLogado usuarioLogado;

    public Menu1() {

        //createConnection();
        clientFacade = new ClienteFachada(conn);

        Hashtable xmlFiles = new Hashtable();
        xmlFiles.put("PT_BR", "recursos/Resources_pt_br.xml");
        xmlFiles.put("EN", "recursos/Resources_en.xml");
        xmlFiles.put("IT", "recursos/Resources_it.xml");

        ClientSettings clientSettings = new ClientSettings(
                new XMLResourcesFactory(xmlFiles, false),
                domains);

        idiomas.setProperty("EN", "English");
        idiomas.setProperty("IT", "Italiano");
        idiomas.setProperty("PT_BR", "Português do Brasil");
        ClientSettings.getInstance().setLanguage("PT_BR");

        ClientSettings.PERC_TREE_FOLDER = "folder3.gif";
        ClientSettings.BACKGROUND = "background2.jpg";

        ClientSettings.TREE_BACK = "treeback2.jpg";
        ClientSettings.ICON_MENU_FILE_CHANGE_USER = "user.png";
        ClientSettings.ICON_MENU_FILE_CHANGE_LANGUAGE = "overlays.png";
        ClientSettings.ICON_MENU_FILE_EXIT = "stop.gif";

        try {
            ClientSettings.ICON_FILENAME = "logo16x16.png";
            ClientSettings.BACKGROUND = "background4.jpg";
            ClientSettings.TREE_BACK = "treeback2.jpg";
            ClientSettings.VIEW_BACKGROUND_SEL_COLOR = true;
            ClientSettings.VIEW_MANDATORY_SYMBOL = true;
            ClientSettings.FILTER_PANEL_ON_GRID = true;

            ClientSettings.RELOAD_LAST_VO_ON_FORM = true;
            // ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jtattoo.plaf.aero.AeroLookAndFeel";
            ClientSettings.LOOK_AND_FEEL_CLASS_NAME = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        LoginDialog d = new LoginDialog(
                null,
                false,
                this,
                "Autenticação Work EDI",
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
        GenericStatusPanel userPanel = new GenericStatusPanel();
        userPanel.setColumns(12);
        MDIFrame.addStatusComponent(userPanel);
        userPanel.setText(username);

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
        return "Sistema de Pesagem";
    }

    /**
     * @return text to view in the about dialog window
     */
    @Override
    public String getAboutText() {
        return "Pesagem \n"
                + "\n"
                + "Contato: \n"
                + "Copyright: Copyright (C) 2020 \n"
                + "Autor: Jairo Pereira";
    }

    /**
     * @return image name to view in the about dialog window
     */
    @Override
    public String getAboutImage() {
        return "work.png";
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
        /*JDialog d = new LoginDialog(
         parentFrame,
         true,
         this,
         "Autenticação",
         "Login",
         'L',
         "Sair",
         'S',
         "Armazenar Informação",
         "Pesagem ",
         null,
         idiomas,
         ClientSettings.getInstance().getResources().getLanguageId());*/
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

        ChamarWebServiceERP ws = new ChamarWebServiceERP();
        String retorno = ws.logarErp(usuarioDig, senhaDig);

        if (!retorno.equals("200")) {
            retornoLogin = false;
            JOptionPane.showMessageDialog(null, "Credenciais inválidas");
        } else {
            //   JOptionPane.showMessageDialog(null, retorno);
        }

        return retornoLogin;
    }

    public static UsuarioLogado getUsrLogado() {
        return Menu1.usuarioLogado;
    }

    public static Usuario getUsuario() {
        return Menu1.usuario;
    }

    public static void main(String[] argv) {
        new Menu1();

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

        // show tip of the day internal frame...
        showTipFrame();
        //configura os botões da barra de ferramentas
        //mdi.addButtonToToolBar("sitBom.png", "XMLs a processar");
        // mdi.addButtonToToolBar("sitMedio.png", "Servidor de Arquivos");
        // mdi.addSeparatorToToolBar();
        //  mdi.addButtonToToolBar("sitRuim.png", "Sair da Aplicação");

    }

    /**
     * @return <code>true</code> if the MDI frame must show a change language
     * menu in the menubar, <code>false</code> no change language menu item will
     * be added
     */
    @Override
    public boolean viewChangeLanguageInMenuBar() {
        return true;
    }

    /**
     * @return list of languages supported by the application
     */
    @Override
    public ArrayList getLanguages() {
        ArrayList list = new ArrayList();
        list.add(new Language("EN", "English"));
        list.add(new Language("IT", "Italiano"));
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

        //Menu pai
        ApplicationFunction n1 = new ApplicationFunction("Cadastro", null);
        ApplicationFunction n13 = new ApplicationFunction("Paramentros", "parametros", "application_edit.png", "getParamentros");

        ApplicationFunction n2 = new ApplicationFunction("Recebimento", null);
        ApplicationFunction n23 = new ApplicationFunction("Pesagem", "pesos", "application_edit.png", "getIntegrarPesos");

        ApplicationFunction n3 = new ApplicationFunction("Minuta de embarque", null);
        ApplicationFunction n31 = new ApplicationFunction("Gerar minuta", "gerarminuto", "chart_organisation.png", "getMinuta");

        ApplicationFunction n5 = new ApplicationFunction("Sucata", null);
        ApplicationFunction n51 = new ApplicationFunction("Sucata", "sucata", "chart_organisation.png", "getSucata");

        n1.add(n13);
        n2.add(n23);
        n3.add(n31);
        n5.add(n51);

        // root.add(n1);
        root.add(n2);
        root.add(n3);
        root.add(n5);

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
                    "Pesagem",
                    "Sistema em desenvolvimento",
                    "By Jairo Pereira da Silva "
                };
            }

            /**
             * @return list of tips
             */
            @Override
            public String[] getTips() {
                return new String[]{
                    "Pesagem  \n",
                    "<html><body>Tabelas de preço\n"
                    + "<ul>"
                    + "<li>Peça: - </li>\n"
                    + "<li>Peça: - </li>"
                    + "<li>Peça: - </li>\n"
                    + "<li>Peça: - </li>"
                    + "</ul></body></html>"
                };
            }
        });
        tipFrame1.setShowCheck(false);
        MDIFrame.add(tipFrame1);
    }
}
