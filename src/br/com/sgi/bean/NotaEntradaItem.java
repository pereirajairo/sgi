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
public class NotaEntradaItem {

    private Integer empresa;
    private Integer filial;
    private Integer nota;
    private Integer fornecedor;
    private Integer filial_oc;
    private Integer ordem_compra;
    private String produto;
    private Double quantidade_recebida;
    private Double quantidade_ordem;
    private String serie;
    private String derivacao;
    private Double valorSucata;
    private String deposito;
    private String centroCusto;
    private Integer seqIpo;
    private Integer contaFinanceira;

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
        

    public String getCentroCusto() {
        return centroCusto;
    }

    public void setCentroCusto(String centroCusto) {
        this.centroCusto = centroCusto;
    }

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito;
    }
    
    
    public Integer getFilial_oc() {
        return filial_oc;
    }

    public void setFilial_oc(Integer filial_oc) {
        this.filial_oc = filial_oc;
    }

    public Double getValorSucata() {
        return valorSucata;
    }

    public void setValorSucata(Double valorSucata) {
        this.valorSucata = valorSucata;
    }

    public String getDerivacao() {
        return derivacao;
    }

    public void setDerivacao(String derivacao) {
        this.derivacao = derivacao;
    }

    public String getTrancacao() {
        return trancacao;
    }

    public void setTrancacao(String trancacao) {
        this.trancacao = trancacao;
    }
    private String trancacao;
   

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
