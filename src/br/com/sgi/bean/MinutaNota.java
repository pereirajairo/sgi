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
public class MinutaNota {

    private Integer codigolancamento = 0;
    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer codigoCliente = 0;
    private Integer notafiscal = 0;
    private Integer codigominuta = 0;
    private Integer usuario = 0;
    private Integer numerocarga = 0;
    private Double peso = 0.0;
    private Double quantidade = 0.0;
    private Double quantidadeVolume = 0.0;
    private String observacao;
    private Date emissao;
    private String emissaoS;
    private Date embarque;
    private String situacao;
    private String enviaremail;

    private String transacao;
    
    private Minuta minuta;
    private Cliente CadCliente;

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }

    
    
    public String getEmissaoS() {
        return emissaoS;
    }

    public void setEmissaoS(String emissaoS) {
        this.emissaoS = emissaoS;
    }

 
    
    
    public Integer getCodigolancamento() {
        return codigolancamento;
    }

    public void setCodigolancamento(Integer codigolancamento) {
        this.codigolancamento = codigolancamento;
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

    public Integer getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(Integer codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Integer getNotafiscal() {
        return notafiscal;
    }

    public void setNotafiscal(Integer notafiscal) {
        this.notafiscal = notafiscal;
    }

    public Integer getCodigominuta() {
        return codigominuta;
    }

    public void setCodigominuta(Integer codigominuta) {
        this.codigominuta = codigominuta;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public Integer getNumerocarga() {
        return numerocarga;
    }

    public void setNumerocarga(Integer numerocarga) {
        this.numerocarga = numerocarga;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getQuantidadeVolume() {
        return quantidadeVolume;
    }

    public void setQuantidadeVolume(Double quantidadeVolume) {
        this.quantidadeVolume = quantidadeVolume;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Date getEmbarque() {
        return embarque;
    }

    public void setEmbarque(Date embarque) {
        this.embarque = embarque;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getEnviaremail() {
        return enviaremail;
    }

    public void setEnviaremail(String enviaremail) {
        this.enviaremail = enviaremail;
    }

    public Minuta getMinuta() {
        return minuta;
    }

    public void setMinuta(Minuta minuta) {
        this.minuta = minuta;
    }

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
    }

}
