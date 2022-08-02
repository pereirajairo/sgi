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
public class OrdemCompraComissao {

    private Integer empresa;
    private Integer filial;
    private Integer representante;
    private String dataBase;
    private Integer numeroOrdemCompra;
    private Integer codigoFornecedor;
    private Double valorLiquido;
    private Double valorIrf;
    private Integer empresaDestino;
    private Integer filialDestino;
    private String emailRep;

    public Integer getRepresentante() {
        return representante;
    }

    public void setRepresentante(Integer representante) {
        this.representante = representante;
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public Double getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(Double valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public Double getValorIrf() {
        return valorIrf;
    }

    public void setValorIrf(Double valorIrf) {
        this.valorIrf = valorIrf;
    }

    public Integer getEmpresaDestino() {
        return empresaDestino;
    }

    public void setEmpresaDestino(Integer empresaDestino) {
        this.empresaDestino = empresaDestino;
    }

    public Integer getFilialDestino() {
        return filialDestino;
    }

    public void setFilialDestino(Integer filialDestino) {
        this.filialDestino = filialDestino;
    }

    public String getEmailRep() {
        return emailRep;
    }

    public void setEmailRep(String emailRep) {
        this.emailRep = emailRep;
    }
    
    public Integer getNumeroOrdemCompra() {
        return numeroOrdemCompra;
    }

    public void setNumeroOrdemCompra(Integer numeroOrdemCompra) {
        this.numeroOrdemCompra = numeroOrdemCompra;
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

    public Integer getCodigoFornecedor() {
        return codigoFornecedor;
    }

    public void setCodigoFornecedor(Integer codigoFornecedor) {
        this.codigoFornecedor = codigoFornecedor;
    }

}
