/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi;

import br.com.sgi.bean.WebServiceParametro;
import br.com.sgi.dao.WebServiceParametroDAO;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

/**
 *
 * @author jairo.silva
 */
public class ChamarWebServiceERP {

    private final String USER_AGENT = "Mozilla/5.0";
    private String StaGer;
    private static WebServiceParametro webServiceParametro = new WebServiceParametro();
    private static WebServiceParametroDAO webServiceParametroDAO = new WebServiceParametroDAO();

    private static String ip = "";
    private static String porta = "";
    private static String wsUsuario;
    private static String wsSenha;

    private static void CarregaParametroWebService() throws SQLException {
        webServiceParametro = webServiceParametroDAO.getWebServiceParametro();
        ip = webServiceParametro.getIpWebService();
        porta = webServiceParametro.getPortaWebService();

    }

    public String logarErp(String usuario, String senha) throws Exception {
        CarregaParametroWebService();
       // ip = "192.168.2.105";
       // porta = "28080";
        String url = "http://" + ip + ":" + porta + "/sapiensweb/conector?SIS=CO&LOGIN=SID&ACAO=EXESENHA&NOMUSU=" + usuario + "&SENUSU=" + senha + "";

        System.out.println(url);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        //  System.out.println("\nSending 'GET' request to URL : " + url);
        // System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String resposta = response.toString();
        if (resposta.startsWith("ERRO")) {
            this.StaGer = "ERRO";
        } else {
            this.StaGer = "200";
        }
        //  print result
        //   System.out.println("Resposta" + response.toString());
        //  System.out.println("ATUTENTICADO" + this.StaGer);
        //  System.out.println("URL" + url);
        return StaGer;
    }

}
