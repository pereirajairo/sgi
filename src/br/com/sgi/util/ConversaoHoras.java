/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

import java.text.ParseException;

/**
 *
 * @author jairosilva
 */
public class ConversaoHoras {

    public static Integer ConverterHoras(String hora) throws ParseException {
        String horas = hora.substring(0, 2);
        String minutos = hora.substring(3, 5);
        int hor = Integer.valueOf(horas);
        int min = Integer.valueOf(minutos);
        int totMin = (hor * 60) + min;
        return totMin;
    }

    public static String converterMinutosHora(long valor) {
        long minutos = valor % 60;
        long horas = (valor - minutos) / 60;
       // System.out.println(horas + ":" + minutos);
        return horas + ":" + minutos;
    }

}
