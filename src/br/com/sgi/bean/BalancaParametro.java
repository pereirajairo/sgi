/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

/**
 *
 * @author jairo.silva
 */
public class BalancaParametro {


    private Integer codigoBalanca = 0;
    private String nomeBalanca = "";
    private String portaCom ="";
   
      
    
    public Integer getCodigoBalanca() {
        return codigoBalanca;
    }
 
    public void setCodigoBalanca(Integer codigoBalanca) {
        this.codigoBalanca = codigoBalanca;
    }
    
    public String getPortaCom(){
        return portaCom;
    }
    
    public void setPortaCom(String portaCom){
        this.portaCom = portaCom;
    }
    
    public String getNomeBalanca() {
        return nomeBalanca;
    }

    public void setNomeBalanca(String nomeBalanca) {
        this.nomeBalanca = nomeBalanca;
    }

}
