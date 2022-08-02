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
public class CargaItensImpureza {

    /**
     * @return the quantidadePeso
     */
    public Double getQuantidadePeso() {
        return quantidadePeso;
    }

    /**
     * @param quantidadePeso the quantidadePeso to set
     */
    public void setQuantidadePeso(Double quantidadePeso) {
        this.quantidadePeso = quantidadePeso;
    }

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer numeroCarga = 0;
    private Integer fornecedor = 0;
    private Integer sequenciacarga = 0;
    private Integer sequenciaCadastro = 0;

    private String produto;
    private Double quantidade = 0.0;
    private Double quantidadePeso = 0.0;
    private Double percentualDesconto = 0.0;
    private Double pesoImpureza = 0.0;
    private Double pesoTotalCarga = 0.0;

    private Integer codigoImpureza = 0;
    private Date dataCadastro;
    private Integer ususarioCadastro;
    private Integer horarioCadastro;

    private Impureza impureza;

    public Integer getSequenciaCadastro() {
        return sequenciaCadastro;
    }

    public void setSequenciaCadastro(Integer sequenciaCadastro) {
        this.sequenciaCadastro = sequenciaCadastro;
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

    public Integer getNumeroCarga() {
        return numeroCarga;
    }

    public void setNumeroCarga(Integer numeroCarga) {
        this.numeroCarga = numeroCarga;
    }

    public Integer getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Integer fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Integer getSequenciacarga() {
        return sequenciacarga;
    }

    public void setSequenciacarga(Integer sequenciacarga) {
        this.sequenciacarga = sequenciacarga;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(Double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public Double getPesoImpureza() {
        return pesoImpureza;
    }

    public void setPesoImpureza(Double pesoImpureza) {
        this.pesoImpureza = pesoImpureza;
    }

    public Double getPesoTotalCarga() {
        return pesoTotalCarga;
    }

    public void setPesoTotalCarga(Double pesoTotalCarga) {
        this.pesoTotalCarga = pesoTotalCarga;
    }

    public Integer getCodigoImpureza() {
        return codigoImpureza;
    }

    public void setCodigoImpureza(Integer codigoImpureza) {
        this.codigoImpureza = codigoImpureza;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Integer getUsusarioCadastro() {
        return ususarioCadastro;
    }

    public void setUsusarioCadastro(Integer ususarioCadastro) {
        this.ususarioCadastro = ususarioCadastro;
    }

    public Integer getHorarioCadastro() {
        return horarioCadastro;
    }

    public void setHorarioCadastro(Integer horarioCadastro) {
        this.horarioCadastro = horarioCadastro;
    }

    public Impureza getImpureza() {
        return impureza;
    }

    public void setImpureza(Impureza impureza) {
        this.impureza = impureza;
    }

}
