/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

/**
 *
 * @author jairosilva
 */
public class CompararDouble {

    public static int comparar(double v1, double v2) {
         int i1 = Double.compare(v1, v2);

        if (i1 > 0) {
            System.out.println("First is grater");
        } else if (i1 < 0) {
            System.out.println("Second is grater");
        } else {
            System.out.println("Both are equal");
        }
        return i1;
    }

}
