/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Date;

/**
 *
 * @author jairo.silva
 */
public class Tela {

    private Integer codigoTela = 0;
    private String nomeTela = "";
    private String codigoInteno = "";

    public String getCodigoInteno() {
        return codigoInteno;
    }

    public void setCodigoInteno(String codigoInteno) {
        this.codigoInteno = codigoInteno;
    }

    public Integer getCodigoTela() {
        return codigoTela;
    }

    public void setCodigoTela(Integer codigoTela) {
        this.codigoTela = codigoTela;
    }

    public String getNomeTela() {
        return nomeTela;
    }

    public void setNomeTela(String nomeTela) {
        this.nomeTela = nomeTela;
    }

  
 

    
}
