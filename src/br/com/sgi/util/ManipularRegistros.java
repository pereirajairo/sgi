/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class ManipularRegistros {

    private boolean retorno;

    public static boolean gravarRegistros(String acao) {
        boolean retorno = false;
        Object[] options = {" Sim ", " Não "};
        if (JOptionPane.showOptionDialog(null, "Deseja  " + acao + " este registro?", "Atenção:",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null,
                options, options[1]) == JOptionPane.YES_OPTION) {
            retorno = true;
        } else {
            retorno = false;
            JOptionPane.showMessageDialog(null, "Usuário cancelou a ação " + acao,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
        return retorno;
    }

    public static boolean pesos(String acao) {
        boolean retorno = false;
        Object[] options = {" Sim ", " Não "};
        if (JOptionPane.showOptionDialog(null, acao, "Atenção:",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE, null,
                options, options[1]) == JOptionPane.ERROR_MESSAGE) {
            retorno = true;
        } else {
            retorno = false;
            JOptionPane.showMessageDialog(null, "Usuário cancelou a ação " + acao,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
        }
        return retorno;
    }

    /**
     * @return the retorno
     */
    public boolean isRetorno() {
        return retorno;
    }

    /**
     * @param retorno the retorno to set
     */
    public void setRetorno(boolean retorno) {
        this.retorno = retorno;
    }
}
