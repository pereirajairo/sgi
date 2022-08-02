/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Date;

/**
 *
 * @author jairo.silva teste
 */
public class BalancaLancamento {

    private Integer codigoBalanca = 0;

    private Integer codigoCaixa = 0;
    private Integer codigoEmpresa = 0;
    private Integer codigoFilial = 0;
    private Integer codigoFuncionario = 0;
    private Integer codigoLancamento = 0;
    private Double pesoBalanca = 0.0;
    private String codigoProduto = "";
    private Date dataLancamento;
    private Integer horaLancamento = 0;
    private String nomeFuncionario = "";
    private Double pesoLiquido;
    private Integer codigoEmbalagem;
    private Integer CodigoRelacionado =0; 
    private String mostrarPeso;
    private Integer codigoAgrupamento;
    private Integer  agrupamentoRelacionado;
    private Integer codigoBalancaDestino = 0;

    public Integer getCodigoBalancaDestino() {
        return codigoBalancaDestino;
    }

    public void setCodigoBalancaDestino(Integer codigoBalancaDestino) {
        this.codigoBalancaDestino = codigoBalancaDestino;
    }

    public Integer getAgrupamentoRelacionado() {
        return agrupamentoRelacionado;
    }

    public void setAgrupamentoRelacionado(Integer agrupamentoRelacionado) {
        this.agrupamentoRelacionado = agrupamentoRelacionado;
    }

    public Integer getCodigoAgrupamento() {
        return codigoAgrupamento;
    }

    public void setCodigoAgrupamento(Integer codigoAgrupamento) {
        this.codigoAgrupamento = codigoAgrupamento;
    }

    public String getMostrarPeso() {
        return mostrarPeso;
    }

    public void setMostrarPeso(String mostrarPeso) {
        this.mostrarPeso = mostrarPeso;
    }
    public Integer getCodigoRelacionado() {
        return CodigoRelacionado;
    }

    public void setCodigoRelacionado(Integer caixaRelacionado) {
        this.CodigoRelacionado = caixaRelacionado;
    }
    
        private Balanca balanca;
    private Produto produto;
    private Embalagem embalagem;

    public Integer getCodigoEmbalagem() {
        return codigoEmbalagem;
    }

    public void setCodigoEmbalagem(Integer codigoEmbalagem) {
        this.codigoEmbalagem = codigoEmbalagem;
    }

    public Double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(Double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }



    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Embalagem getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(Embalagem embalagem) {
        this.embalagem = embalagem;
    }

    public Balanca getBalanca() {
        return balanca;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String NomeFuncionario) {
        this.nomeFuncionario = NomeFuncionario;
    }

    public void setBalanca(Balanca balanca) {
        this.balanca = balanca;
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

    public Integer getCodigoEmpresa() {
        return codigoEmpresa;
    }

    public void setCodigoEmpresa(Integer codigoEmpresa) {
        this.codigoEmpresa = codigoEmpresa;
    }

    public Integer getCodigoFilial() {
        return codigoFilial;
    }

    public void setCodigoFilial(Integer codigoFilial) {
        this.codigoFilial = codigoFilial;
    }

    public Integer getCodigoFuncionario() {
        return codigoFuncionario;
    }

    public void setCodigoFuncionario(Integer codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }

    public Integer getCodigoLancamento() {
        return codigoLancamento;
    }

    public void setCodigoLancamento(Integer codigoLancamento) {
        this.codigoLancamento = codigoLancamento;
    }

    public Double getPesoBalanca() {
        return pesoBalanca;
    }

    public void setPesoBalanca(Double pesoBalanca) {
        this.pesoBalanca = pesoBalanca;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Integer getHoraLancamento() {
        return horaLancamento;
    }

    public void setHoraLancamento(Integer horaLancamento) {
        this.horaLancamento = horaLancamento;
    }

}
