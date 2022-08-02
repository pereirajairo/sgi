/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

/**
 *
 * @author jairosilva
 */
public class Permissao {

   private Integer codigoUsuario;
   private Integer codigoTela;
   private String codigoMenu;
   private String codigMenu1;



    public String getCodigoMenu() {
        return codigoMenu;
    }

    public void setCodigoMenu(String codigoMenu) {
        this.codigoMenu = codigoMenu;
    }

    public Integer getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(Integer codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public Integer getCodigoTela() {
        return codigoTela;
    }

    public void setCodigoTela(Integer codigoTela) {
        this.codigoTela = codigoTela;
    }
}
