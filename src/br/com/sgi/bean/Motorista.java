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
public class Motorista {

    private Integer codmtr;
    private Integer codtra;
    private String nommot;
    private Transportadora CadTransportadora;

    public Integer getCodmtr() {
        return codmtr;
    }

    public void setCodmtr(Integer codmtr) {
        this.codmtr = codmtr;
    }

    public Integer getCodtra() {
        return codtra;
    }

    public void setCodtra(Integer codtra) {
        this.codtra = codtra;
    }

    public String getNommot() {
        return nommot;
    }

    public void setNommot(String nommot) {
        this.nommot = nommot;
    }

    public Transportadora getCadTransportadora() {
        return CadTransportadora;
    }

    public void setCadTransportadora(Transportadora CadTransportadora) {
        this.CadTransportadora = CadTransportadora;
    }
    
    
    
    

}
