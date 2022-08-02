/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 *
 * @author jairosilva
 */
public class ConnectionOracleSapHomologacao {

    public static Connection openConnection() throws Exception {
        Connection dbconn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            Properties db_credentials = new Properties();


            db_credentials.put("user", "sapienshomo");
            db_credentials.put("password", "sbresapienshomo");
            dbconn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.4:1521:dbsenior", db_credentials);

        } finally {
            return dbconn;
        }
    }
}
