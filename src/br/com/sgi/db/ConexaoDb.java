/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class ConexaoDb {

    private static Connection con;
    private static Statement st;

    public static Connection getConexao() {

        Connection con = null;
        try {

            Class.forName("org.sqlite.JDBC");

            File f = new File("src//database//base.db");
            String absolute = f.getAbsolutePath();

            con = DriverManager.getConnection("jdbc:sqlite:" + absolute);
            System.out.println("Opened database successfully");
            return con;
        } catch (ClassNotFoundException e) {  //Driver não encontrado  
            JOptionPane.showMessageDialog(null, "Erro " + e,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro " + e,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }
}
