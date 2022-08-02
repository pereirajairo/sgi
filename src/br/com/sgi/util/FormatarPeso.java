/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

import java.text.DecimalFormat;

/**
 *
 * @author jairosilva
 */
public final class FormatarPeso {

    public static final DecimalFormat PORCENTAGEM = new DecimalFormat("###,###,##0.00");
    public static final DecimalFormat PORCENTAGEM_QTDY = new DecimalFormat("###,###,##0");

    public static String mascaraPorcentagem(double valor, DecimalFormat porcentagem) {
        String per = porcentagem.format(valor);
        per = per.replaceAll(",", ".");
        return per;
    }

    public static Double limpaValorEmbalagem(String Valor) {
        double codPeso = 0;
        int index = Valor.indexOf("KG");
        if (index > 0) {
            String codcon = Valor.substring(0, index);
            codPeso = Double.parseDouble(codcon.trim());
        }
        System.out.println("Peso " + Valor + " convertido " + codPeso);
        return codPeso;

    }

    public static String mascaraQuantidade(double valor, DecimalFormat porcentagem) {
        String per = porcentagem.format(valor);
        per = per.replaceAll(",", ".");
        return per;
    }
}
