package br.com.sgi.conexao;

import java.sql.*;
import javax.swing.JOptionPane;

public class ConexaoMySql {

    public static Connection getConexao() {
        try {
            // Carregando o JDBC Driver padrão  
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            // Configurando a nossa conexão com um banco de dados//  
//
//            String serverName = "localhost";      //Servidor
//            String mydatabase = "erbsdigital";        //nome do seu banco de dados  
//            String username = "erbsdigital";
//            String password = "erbsdigital";          //sua senha de acesso

//            String serverName = "192.168.2.64";      //Servidor
//            String mydatabase = "venfast";        //nome do seu banco de dados  
//            String username = "venfast";          //nome de um usuário de seu BD        
//            String password = "#_venfast_#";          //sua senha de acesso


            String serverName = "192.168.2.30";      //Servidor
            String mydatabase = "erbsdigital";      //nome do seu banco de dados  
            String username = "erbsdigital";         //nome de um usuário de seu BD        
            String password = "#_erbsdigital_#";     //sua senha de acesso

            String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            Connection connection = DriverManager.getConnection(url, username, password);

            return connection;
        } catch (ClassNotFoundException e) {  //Driver não encontrado  
            JOptionPane.showMessageDialog(null, e);
            return null;
        } catch (SQLException e) {
            //Não conseguindo se conectar ao banco  
            JOptionPane.showMessageDialog(null, e);
            return null;
        }

    }

}
