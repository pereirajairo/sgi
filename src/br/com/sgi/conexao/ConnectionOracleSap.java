/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.conexao;

import br.com.sgi.bean.Database;
import br.com.sgi.main.Menu;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class ConnectionOracleSap {

    public static String user;
    private static String password;
    private static String database;
    public static String hostname;
    private static String port;

    public Database getDatabase(Database dbase) {

        return dbase;
    }

    public static Connection openConnection() throws Exception {
        Connection dbconn = null;
        try {
            Database db = Menu.getDb();
            user = db.getUser();
            password = db.getPassword();
            database = db.getDatabase();
            hostname = db.getHostname();
            port = db.getPort();
            // base oficial
            String conexao = "jdbc:oracle:thin:@" + hostname + ":" + port + ":" + database + "";
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Properties db_credentials = new Properties();
            db_credentials.put("user", user);
            db_credentials.put("password", password);
            dbconn = DriverManager.getConnection(conexao, db_credentials);

            // testes
           // Class.forName("oracle.jdbc.driver.OracleDriver");

           // Properties db_credentials = new Properties();


//            db_credentials.put("user", "sapiensteste1");
//            db_credentials.put("password", "sbresapiensteste1");
//            dbconn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.4:1521:dbsenior", db_credentials);


        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro de conexão" + ex);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Erro de conexão" + ex);
        } finally {

            return dbconn;
        }
    }

}
