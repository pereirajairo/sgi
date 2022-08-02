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
public class ControlePesoChumbo {

    private Integer codigoAgrupador= 0;
    private String situacaoLancamento = "";
    private String codigoProduto ="";
    private Integer codigoBalancaDestino ;

    public Integer getCodigoAgrupador() {
        return codigoAgrupador;
    }

    public void setCodigoAgrupador(Integer codigoAgrupador) {
        this.codigoAgrupador = codigoAgrupador;
    }

    public String getSituacaoLancamento() {
        return situacaoLancamento;
    }

    public void setSituacaoLancamento(String situacaoLancamento) {
        this.situacaoLancamento = situacaoLancamento;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public Integer getCodigoBalancaDestino() {
        return codigoBalancaDestino;
    }

    public void setCodigoBalancaDestino(Integer codigoBalancaDestino) {
        this.codigoBalancaDestino = codigoBalancaDestino;
    }
    
}
