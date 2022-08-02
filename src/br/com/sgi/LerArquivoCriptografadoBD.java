/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi;

import br.com.sgi.bean.Database;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author matheus.luiz
 */
public class LerArquivoCriptografadoBD {

    Database db = new Database();

    public Database getDb() {
        return db;
    }

    public void setDb(Database db) {
        this.db = db;
    }

    public void LerArquivoCriptogrado() throws FileNotFoundException, IOException {

        Scanner input = new Scanner(System.in);

        ArquivoTextoEscrita type = new ArquivoTextoEscrita();
        ArquivoTextoLeitura read = new ArquivoTextoLeitura();
        Criptografe cript = new Criptografe();
        String text, name_arc, cod = "", decod;
        int qtde = 0;

        //System.out.print("\nName of the new archive to decrypt: ");
        
        Scanner in = new Scanner(new FileReader("ConexaoDatabase"));

        String texto = "";

        decod = in.nextLine();
        cript.descriptografar("ConexaoDatabase", 500, decod);
        //System.out.print("Text decrypted: ");
        read.abrirArquivo(decod);
        do {
            text = read.ler();

            if (text == null) {

            } else {
                //System.out.print("\n\t" + text);
                texto += text + "\n";
            }
        } while (text != null);
        read.fecharArquivo();

        Database bancoDados = new Database();
        int i = 0;
        String DBuser;
        String DBpassword;
        String DBhostname;
        String DBport;
        String DBdatabase;
        Scanner iin = new Scanner(texto);
        while (i <= 4) {
            String line = iin.nextLine();
            String cDBuser = line.substring(0, 5);
            String cDBpassword = line.substring(0, 9);
            String cDBhostname = line.substring(0, 9);
            String cDBport = line.substring(0, 5);
            String cDBdatabase = line.substring(0, 9);
            if (cDBuser.equals("user=")) {
                int pos = line.length();
                DBuser = line.substring(5, pos);
                bancoDados.setUser(DBuser.trim());
            } else if (cDBpassword.equals("password=")) {
                int pos = line.length();
                DBpassword = line.substring(9, pos);
                bancoDados.setPassword(DBpassword.trim());
            } else if (cDBhostname.equals("hostname=")) {
                int pos = line.length();
                DBhostname = line.substring(9, pos);
                bancoDados.setHostname(DBhostname.trim());
            } else if (cDBport.equals("port=")) {
                int pos = line.length();
                DBport = line.substring(5, pos);
                bancoDados.setPort(DBport.trim());
            } else if (cDBdatabase.equals("database=")) {
                int pos = line.length();
                DBdatabase = line.substring(9, pos);
                bancoDados.setDatabase(DBdatabase.trim());
            }
            i++;
        }

        this.db = bancoDados;
        i = 0;
    }

}
