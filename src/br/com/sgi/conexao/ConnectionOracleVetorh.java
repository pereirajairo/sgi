/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author matheus.luiz
 */
public class ConnectionOracleVetorh {
    
       public static Connection openConnection() throws Exception {
        Connection dbconn = null;
        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Properties db_credentials = new Properties();

//            db_credentials.put("user", "sapiens");
//            db_credentials.put("password", "sbresapiens");
//            dbconn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.3:1521:dbsenior", db_credentials);

            db_credentials.put("user", "vetorh");
            db_credentials.put("password", "sbrevetorh");
            dbconn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.3:1521:dbsenior", db_credentials);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro de conexão" + ex);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Erro de conexão" + ex);
        } finally {

            return dbconn;
        }
    }
    
}
