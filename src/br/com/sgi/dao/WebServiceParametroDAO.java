/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;


import br.com.sgi.bean.WebServiceParametro;

import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceWebServiceParametroDAO;
import br.com.sgi.util.UtilDatas;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class WebServiceParametroDAO implements InterfaceWebServiceParametroDAO<WebServiceParametro> {

    private String ipWebService;
    private String portaWebService;
    private UtilDatas utilDatas;

    private List<WebServiceParametro> getCameraParametros(ResultSet rs) throws SQLException, ParseException {
        List<WebServiceParametro> resultado = new ArrayList<WebServiceParametro>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
          //  Funcionario e = new Funcionario();
          //  e.setCodigoFuncionario(rs.getInt("NumCad"));
           // e.setNomeFuncionario(rs.getString("NomFun"));

            // resultado.add(e);
        }
        return resultado;
    }
 
    @Override
    public WebServiceParametro getWebServiceParametro() throws SQLException {
       
         WebServiceParametro e = new WebServiceParametro();
        Scanner in = null;
        try {
            in = new Scanner(new FileReader("ConfiguracaoWebService.prop"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WebServiceParametroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (in.hasNextLine()) {
            String line = in.nextLine();
           // System.out.println(line);
            String aIp = line.substring(0, 2);
            String aPorta = line.substring(0, 5);
            if (aIp.equals("IP")) {
                int pos = line.length();
                ipWebService = line.substring(3, pos);
                System.out.println(line.substring(3, pos));
            }
            if (aPorta.equals("PORTA")) {
                int pos = line.length();
                // pos = (8 - pos) + 1;
                portaWebService = line.substring(6, pos);
               System.out.println(line.substring(6, pos));

            }             
        
        }
       
       e.setIpWebService(ipWebService.trim());
       e.setPortaWebService(portaWebService.trim());

        return e;
    }

    @Override
    public List<WebServiceParametro> getWebServiceParametros(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
