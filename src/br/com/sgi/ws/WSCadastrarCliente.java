/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author jairo.silva
 */
public class WSCadastrarCliente {

    private final String USER_AGENT = "Mozilla/5.0";
    private String StaGer;

    public static String mensagem;

    public String executar(String usuario, String senha) throws Exception {
    //OFCIAL
        //String url = "http://192.168.2.193:18080/sapiensweb/conector?SIS=CO&LOGIN=SID&ACAO=EXESENHA&NOMUSU=" + usuario.toLowerCase() + "&SENUSU=" + senha + "&PROXACAO=SID.SRV.REGRA&NUMREG=108";
        // TESTE
        //  String url = "http://192.168.2.115:8080/sapiensweb/conector?SIS=CO&LOGIN=SID&ACAO=EXESENHA&NOMUSU=" + usuario.toLowerCase() + "&SENUSU=" + senha + "&PROXACAO=SID.SRV.REGRA&NUMREG=107";
        //HOMOL
        String url = "http://192.168.2.106:18080/sapiensweb/conector?SIS=CO&LOGIN=SID&ACAO=EXESENHA&NOMUSU=" + usuario.toLowerCase() + "&SENUSU=" + senha + "&PROXACAO=SID.SRV.REGRA&NUMREG=108";

        
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
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
        if (resposta.startsWith("WS - OK")) {
            this.StaGer = "Gerado com sucesso";
        } else {
            this.StaGer = "Erro de geração";
        }
        //  print result
        System.out.println("Resposta" + response.toString());
        System.out.println("ATUTENTICADO" + this.StaGer);
        System.out.println("URL" + url);
        mensagem = response.toString();
        return response.toString();
    }
}
