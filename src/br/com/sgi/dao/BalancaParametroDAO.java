/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.BalancaParametro;
import br.com.sgi.bean.Funcionario;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceBalancaParametroDAO;
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
public class BalancaParametroDAO implements InterfaceBalancaParametroDAO<BalancaParametro> {

    private String BalNom;
    private String ParametroCOM;
    private UtilDatas utilDatas;

    private List<BalancaParametro> getBalancaParametros(ResultSet rs) throws SQLException, ParseException {
        List<BalancaParametro> resultado = new ArrayList<BalancaParametro>();

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
    public BalancaParametro getBalancaParametro() throws SQLException {
       
         BalancaParametro e = new BalancaParametro();
        Scanner in = null;
        try {
            in = new Scanner(new FileReader("ConfiguracaoBalanca.prop"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BalancaParametroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (in.hasNextLine()) {
            String line = in.nextLine();
           // System.out.println(line);
            String aParCom = line.substring(0, 3);
            String aBar = line.substring(0, 7);
            if (aParCom.equals("COM")) {
                ParametroCOM = line.substring(4, 8);
             //   System.out.println(ParametroCOM);
            }
            if (aBar.equals("BALANCA")) {
                int pos = line.length();
                // pos = (8 - pos) + 1;
                BalNom = line.substring(8, pos);
              //  System.out.println(BalNom.trim());

            }
        }
       
        e.setPortaCom(ParametroCOM);
        e.setNomeBalanca(BalNom);
        return e;
    }

    @Override
    public List<BalancaParametro> getBalancaParametros(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
