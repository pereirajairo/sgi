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
public class Caixa {

    private Integer codigoCaixa = 0;
    private Double pesoCaixa = 0.0;
    private String emusuCaixa ="";
    private String situacaoCaixa ="";
    private String codigoProduto ="";
    private Integer codigoBalanca ;

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public Integer getCodigoBalanca() {
        return codigoBalanca;
    }

    public void setCodigoBalanca(Integer codigoBalanca) {
        this.codigoBalanca = codigoBalanca;
    }



    public Integer getCodigoCaixa() {
        return codigoCaixa;
    }

    public void setCodigoCaixa(Integer codigoCaixa) {
        this.codigoCaixa = codigoCaixa;
    }

    public Double getPesoCaixa() {
        return pesoCaixa;
    }

    public void setPesoCaixa(Double pesoCaixa) {
        this.pesoCaixa = pesoCaixa;
    }

    public String getEmusuCaixa() {
        return emusuCaixa;
    }

    public void setEmusuCaixa(String emusuCaixa) {
        this.emusuCaixa = emusuCaixa;
    }

    public String getSituacaoCaixa() {
        return situacaoCaixa;
    }

    public void setSituacaoCaixa(String situacaoCaixa) {
        this.situacaoCaixa = situacaoCaixa;
    }

   
 

    
}
