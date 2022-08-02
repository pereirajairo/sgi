/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author jairosilva
 */
public class FormatarNumeros {

    public static double converterToDouble(double peso) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Locale Local = new Locale("pt", "BR");

    public static double converterStringToDouble(String valor) {
        System.out.println(valor);
        Double valorDouble = Double.parseDouble(valor);
        System.out.println("\n"+valorDouble);
        
        return valorDouble;

    }

    public static String converterMonetario(Double valor) {
        NumberFormat format = DecimalFormat.getCurrencyInstance(new Locale("pt", "BR"));
        format.setMinimumFractionDigits(2);
        String valorFormatado = format.format(valor);
        System.out.println(valorFormatado);
        return valorFormatado;
    }

    public String converterToString(Double vlr) {
        DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Local));
        String valfor = df.format(vlr);
        return valfor;
    }

    public static Double converterToDouble(String valor) {
        Double valorConvertido = 0.0;
        String valorConverter = valor;
        valorConverter = valorConverter.replace(".", "");
        valorConverter = valorConverter.replace(",", ".");
        valorConvertido = Double.parseDouble(valorConverter);
        return valorConvertido;

    }

    public Double multiplicarDouble(Double vlr1, Double vlr2) {
        Double valor = vlr1 * vlr2;
        return valor;

    }

    public static double converterDoubleDoisDecimais(double precoDouble) {
        DecimalFormat fmt = new DecimalFormat("0.00");
        String string = fmt.format(precoDouble);
        String[] part = string.split("[,]");
        String string2 = part[0] + "." + part[1];
        double preco = Double.parseDouble(string2);
        return preco;
    }

    public static String removerPontos(String valor) {
        valor = valor.replace(".", "");
        valor = valor.replace(",", ".");
        return valor;

    }

    public static String ConverterDouble(double valor) {
        double d = valor;
        DecimalFormat df = new DecimalFormat("###,###");
        System.out.println(df.format(d));
        return df.format(d);
    }

    public static String FormatarDouble(double valor) {
        double d = valor;
        DecimalFormat df = new DecimalFormat("######.####");
        System.out.println(df.format(d));
        return df.format(d);
    }

    public static String converterDoubleString(double precoDouble) {
        /*Transformando um double em 2 casas decimais*/
        DecimalFormat fmt = new DecimalFormat("0.00");	 //limita o número de casas decimais	
        String string = fmt.format(precoDouble);
        String[] part = string.split("[,]");
        String preco = part[0] + "." + part[1];
        return preco;
    }
}
