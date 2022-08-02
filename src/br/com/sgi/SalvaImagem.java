/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi;

import static br.com.sgi.SalvaImagem.getSalvaImagem;
import static br.com.sgi.SalvaImagem.passwordCamera;
import static br.com.sgi.SalvaImagem.user;
import br.com.sgi.bean.CameraParametro;
import br.com.sgi.dao.CameraParametroDAO;
import br.com.sgi.main.Menu;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author jairo.silva
 */
public class SalvaImagem {
    
    public static String user;
    public static String passwordCamera;
    private static String urlCamera;

   

    public static void  getSalvaImagem() throws SQLException {
            CameraParametro cameraParametro  = new CameraParametro();
            CameraParametroDAO cameraParametroDAO = new CameraParametroDAO();
            cameraParametro = cameraParametroDAO.getCameraParametro();
            user = cameraParametro.getUsuarioCamera();
            passwordCamera = cameraParametro.getSenhaCamera();
            urlCamera = cameraParametro.getUrlCamera();
    }

    public static void SalvaImagemCamIP(String urlLocation, String nome) throws MalformedURLException, IOException {
        try {
             getSalvaImagem();
         
            Authenticator.setDefault(new MyAuthenticator());
            BufferedImage image = null;
           // URL url = new URL("http://192.168.4.200/ISAPI/Streaming/Channels/1/picture");
            URL url = new URL(urlCamera);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            Date dataAtual = new Date();
            DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");

            String dataFormatada = dateFormat.format(dataAtual);

            //  ImageIO.write(image, "jpeg", new File("C:\\Users\\matheus.luiz\\Desktop\\teste\\" + NomCam + "-" + dataFormatada + ".jpeg"));
            image = ImageIO.read(url);
            ImageIO.write(image, "jpg", new File("\\\\192.168.2.34\\Public\\ImagemCameraBalanca\\" + nome + "-" + dataFormatada + ".jpeg"));
            while ((str = in.readLine()) != null) {
                // System.out.println(str);
            }
            in.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro " + e,
                    "Erro:", JOptionPane.ERROR_MESSAGE);

        }

    }

}

class MyAuthenticator extends Authenticator {

    protected PasswordAuthentication getPasswordAuthentication() {
        try {
            getSalvaImagem();
        } catch (SQLException ex) {
            Logger.getLogger(MyAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
        String promptString = getRequestingPrompt();
        System.out.println(promptString);
        String hostname = getRequestingHost();
        System.out.println(hostname);
        InetAddress ipaddr = getRequestingSite();
        System.out.println(ipaddr);
        int port = getRequestingPort();

        //String username = "ti";
      // String password = "ti123456";
        return new PasswordAuthentication(user, passwordCamera.toCharArray());
    }

}
