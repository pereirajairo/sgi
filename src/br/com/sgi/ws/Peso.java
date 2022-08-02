/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.ws;

/**
 *
 * @author marcelosiedler
 */
public class Peso {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {

        Peso http = new Peso();
        String pesoBalanca = "00000100kg";

        System.out.println("Peso da balança " + pesoBalanca);
        String peso = pesoBalanca.substring(pesoBalanca.lastIndexOf("kg"));
        System.out.println(peso);
        char pegouPeso = pesoBalanca.charAt(pesoBalanca.length() - 1);
        String pesoPegado = String.valueOf(pegouPeso);
        System.out.println("peso pegado "+pesoPegado);
        if (pesoPegado.equals("g")) {
            int tam = pesoBalanca.length();
            tam = tam - 2; // deconta 2 caracter do kg
            System.out.println("tamanho " + tam);
            String pesos = pesoBalanca.substring(0, pesoBalanca.lastIndexOf("kg"));
            //String pesos = result.substring(0,tam);
            System.out.println("pesar  " + pesos);

        }

    }

}
