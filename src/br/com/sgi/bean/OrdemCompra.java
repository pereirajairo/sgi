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
public class OrdemCompra {

    private Integer empresa=0;
    private Integer filial=0;
    private Integer numeroOrdemCompra=0;
    private Integer codigoFornecedor=0;
    private String nomeFornecedor;
    private String situacaoOrdemCompra;
    private String situacaoaprovacao;
    private String dataemissao;
    private String apelidoFornecedor;
    private String cnpj;
    private Double peso;
    private String cidade;
    private String estado;
    private Double quantidade;

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }
    
    
    

    public String getApelidoFornecedor() {
        return apelidoFornecedor;
    }

    public void setApelidoFornecedor(String apelidoFornecedor) {
        this.apelidoFornecedor = apelidoFornecedor;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }
    
    
    
    
      /**
     * @return the dataemissao
     */
    public String getDataemissao() {
        return dataemissao;
    }

    /**
     * @param dataemissao the dataemissao to set
     */
    public void setDataemissao(String dataemissao) {
        this.dataemissao = dataemissao;
    }

    

    /**
     * @return the situacaoaprovacao
     */
    public String getSituacaoaprovacao() {
        return situacaoaprovacao;
    }

    /**
     * @param situacaoaprovacao the situacaoaprovacao to set
     */
    public void setSituacaoaprovacao(String situacaoaprovacao) {
        this.situacaoaprovacao = situacaoaprovacao;
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

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    public String getSituacaoOrdemCompra() {
        return situacaoOrdemCompra;
    }

    public void setSituacaoOrdemCompra(String situacaoOrdemCompra) {
        this.situacaoOrdemCompra = situacaoOrdemCompra;
    }

}
