/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

/**
 *
 * @author jairosilva
 */
public class ValidarCampos {

    private boolean campovalidado = false;

    public void validacao(String campo) {
        if (campo.isEmpty()) {
            campovalidado = false;
        } else if (campo == null) {
            campovalidado = false;
        }  else if (campo.equals(" ")) {
            campovalidado = false;
        } else {
            campovalidado = true;
        }
    }

    public void validacaoZeros(String campo) {
        if (campo.equals("0")) {
            campovalidado = false;
        } else if (campo.equals("0,00")) {
            campovalidado = false;
        } else if (campo.equals("0.0")) {
            campovalidado = false;
        } else {
            campovalidado = true;
        }
    }

    /**
     * @return the campovalidado
     */
    public boolean isCampovalidado() {
        return campovalidado;
    }

    /**
     * @param campovalidado the campovalidado to set
     */
    public void setCampovalidado(boolean campovalidado) {
        this.campovalidado = campovalidado;
    }
}
