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
public class NotaFiscalItens {

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer notafiscal = 0;
    private String produto;
    private String origem;
    private Double quantidade = 0.0;
    private Integer mes;
    private Integer ano;
    private Produto cadProduto;
    private Date emissao;
    private double dias_ultimo_faturamento = 0.0;

    private String emissaoS;
    private Double pesoLiquido = 0.0;
    private Double pesoBruto = 0.0;

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

    public Integer getNotafiscal() {
        return notafiscal;
    }

    public void setNotafiscal(Integer notafiscal) {
        this.notafiscal = notafiscal;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Produto getCadProduto() {
        return cadProduto;
    }

    public void setCadProduto(Produto cadProduto) {
        this.cadProduto = cadProduto;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public double getDias_ultimo_faturamento() {
        return dias_ultimo_faturamento;
    }

    public void setDias_ultimo_faturamento(double dias_ultimo_faturamento) {
        this.dias_ultimo_faturamento = dias_ultimo_faturamento;
    }

    public String getEmissaoS() {
        return emissaoS;
    }

    public void setEmissaoS(String emissaoS) {
        this.emissaoS = emissaoS;
    }

    public Double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(Double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public Double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(Double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }
    
    

}
