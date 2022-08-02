/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class DbDAO implements InterfaceDbDAO<Params_banco_app> {

    @Override
    public Params_banco_app getParams_banco_app() throws SQLException {
        Params_banco_app banco = new Params_banco_app();
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");

            File f = new File("src//database//base.db");
            String absolute = f.getAbsolutePath();

            c = DriverManager.getConnection("jdbc:sqlite:" + absolute);
            c.setAutoCommit(false);
            stmt = c.createStatement();
            System.out.println("Opened database successfully" + c);

            ResultSet rs = stmt.executeQuery("SELECT * FROM banco");
            while (rs.next()) {
                banco.setId(rs.getInt("id"));
                banco.setDatabasetype(rs.getString("databasetype"));
                banco.setDriverName(rs.getString("driverName"));
                banco.setServerName(rs.getString("serverName"));
                banco.setMydatabase(rs.getString("mydatabase"));
                banco.setUsername(rs.getString("username"));
                banco.setPassword(rs.getString("password"));
                banco.setUrl(rs.getString("url"));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Erro " + e.getClass().getName() + "\n " + e.getMessage(),
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
     //   System.out.println("Operation done successfully");

        return banco;
    }

}
