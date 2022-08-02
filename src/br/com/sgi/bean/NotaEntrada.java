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
public class NotaEntrada {

    private Integer empresa;
    private Integer filial;
    private Integer nota;
    private Integer fornecedor;
    private Integer ordem_compra;
    private String produto;
    private Double quantidade_recebida;
    private Double quantidade_ordem;
    private String serie;
    private String trancacao;
    private Double valorNota;


    public Integer getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(Integer contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }
    private Integer contaFinanceira;

    public Double getValorNota() {
        return valorNota;
    }

    public void setValorNota(Double valorNota) {
        this.valorNota = valorNota;
    }

    public String getTrancacao() {
        return trancacao;
    }

    public void setTrancacao(String trancacao) {
        this.trancacao = trancacao;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
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

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public Integer getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Integer fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Integer getOrdem_compra() {
        return ordem_compra;
    }

    public void setOrdem_compra(Integer ordem_compra) {
        this.ordem_compra = ordem_compra;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Double getQuantidade_recebida() {
        return quantidade_recebida;
    }

    public void setQuantidade_recebida(Double quantidade_recebida) {
        this.quantidade_recebida = quantidade_recebida;
    }

    public Double getQuantidade_ordem() {
        return quantidade_ordem;
    }

    public void setQuantidade_ordem(Double quantidade_ordem) {
        this.quantidade_ordem = quantidade_ordem;
    }

}
