/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Date;

/**
 *
 * @author jairosilva
 */
public class SucataEcoParametros {

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer ordemCompras;
    private String produto;
    private String derivacao;
    private String deposito;
    private String transacao;
    private String centroCustos;
    private Double valorSucata;
    private Integer seqIpo;
    private Integer contaFinanceira;
    private Integer ideExt;

    private String condicao;
    private String complemento;
    private String observacao;
    private String fornecedor;

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    
    
    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Integer getIdeExt() {
        return ideExt;
    }

    public void setIdeExt(Integer ideExt) {
        this.ideExt = ideExt;
    }

    public Integer getSeqIpo() {
        return seqIpo;
    }

    public void setSeqIpo(Integer seqIpo) {
        this.seqIpo = seqIpo;
    }

    public Integer getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(Integer contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public Integer getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Integer empresa) {
        this.empresa = empresa;
    }

    public Integer getFilial() {
        return filial;
    }

    public void setFilial(Integer filial) {
        this.filial = filial;
    }

    public Integer getOrdemCompras() {
        return ordemCompras;
    }

    public void setOrdemCompras(Integer ordemCompras) {
        this.ordemCompras = ordemCompras;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getDerivacao() {
        return derivacao;
    }

    public void setDerivacao(String derivacao) {
        this.derivacao = derivacao;
    }

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito;
    }

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }

    public String getCentroCustos() {
        return centroCustos;
    }

    public void setCentroCustos(String centroCustos) {
        this.centroCustos = centroCustos;
    }

    public Double getValorSucata() {
        return valorSucata;
    }

    public void setValorSucata(Double valorSucata) {
        this.valorSucata = valorSucata;
    }

}
