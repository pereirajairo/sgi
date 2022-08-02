/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.conexao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class ConnectionSqlServer {

    public static Properties getProp() throws IOException {
        Properties props = new Properties();
        FileInputStream file = new FileInputStream(
                "./properties/config_sqlserver.properties");
        props.load(file);
        return props;

    }

    public static Connection openConnection() throws Exception {

        String hostname;
        String portname;
        String servicename;
        String username = null;
        String userpassword = null;
        String databasename;
        String connection = null;

        //Itagres
//        hostname = ("srvsql01");
//        portname = ("1433");
//        servicename = ("sapiens_prod");
//        username = ("sapiens_prod");
//        userpassword = ("pds2772@");
//        databasename = ("sapiens_prod");
//        connection = "jdbc:sqlserver://" + hostname + "\\SQLEXPRESS:1433;databaseName=" + databasename
//                + ";user=" + username + ";password=" + userpassword;
        //Itagres teste
//        hostname = ("srvsql01");
//        portname = ("1433");
//        servicename = ("sapiens_homologacao");
//        username = ("sa");
//        userpassword = ("pds2772@");
//        databasename = ("sapiens_homologacao");
//        connection = "jdbc:sqlserver://" + hostname + "\\SQLEXPRESS:1433;databaseName=" + databasename
//                + ";user=" + username + ";password=" + userpassword;

        //pack bac
//        hostname = ("SRV01-PB");
//        portname = ("1433");
//        servicename = ("SAPIENS_PROD");
//        username = ("SAPIENS_PROD");
//        userpassword = ("seniorsapiens");
//        databasename = ("SAPIENS_PROD");
//        connection = "jdbc:sqlserver://" + hostname + "\\SQLEXPRESS:1433;databaseName=" + databasename
//                + ";user=" + username + ";password=" + userpassword;
       //  use
        hostname = ("SVR-KROMUS-DC");
        portname = ("1433");
        servicename = ("Senior");
        username = ("sapiens");
        userpassword = ("Adm2009");
        databasename = ("Sapiens");
        connection = "jdbc:sqlserver://" + hostname + "\\SQLEXPRESS:0;databaseName=" + databasename
                + ";user=" + username + ";password=" + userpassword;
        
//        hostname = ("192.168.0.22");
//        portname = ("1433");
//        servicename = ("sapiens");
//        username = ("sapiens");
//        userpassword = ("sapiens_");
//        databasename = ("sapiens");
//        connection = "jdbc:sqlserver://" + hostname + "\\SQLEXPRESS:1433;databaseName=" + databasename
//                + ";user=" + username + ";password=" + userpassword;
        Connection dbconn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Properties db_credentials = new Properties();
            db_credentials.put("user", username);
            db_credentials.put("password", userpassword);
            dbconn = DriverManager.getConnection(connection, db_credentials);

        } catch (ClassNotFoundException cl) {
            System.out.println("ERRO " + cl.toString());
            JOptionPane.showMessageDialog(null, "Erro " + cl,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            System.out.println("ERRO " + ex.toString());
        } finally {
            return dbconn;
        }
    }
}
