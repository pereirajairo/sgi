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
public class NotaFiscal {

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer notafiscal = 0;
    private String serie;
    private Integer codigocliente = 0;

    private Integer transportadora = 0;

    private Double quantidade = 0.0;
    private Double quantidadevolume = 0.0;
    private Integer pedido = 0;
    private Date emissao;
    private String emissaoS;
    private Double pesoLiquido = 0.0;
    private Double pesoBruto = 0.0;
    private Double valorLiquido = 0.0;
    private String origem;
    private String linhaProduto;

    private String transacao;
    private String nomeTransportadora;
    private String situacao;

    private Integer mes;
    private Integer ano;
    private double dias_ultimo_faturamento = 0.0;

    private Cliente cliente;

    public String getLinhaProduto() {
        return linhaProduto;
    }

    public void setLinhaProduto(String linhaProduto) {
        this.linhaProduto = linhaProduto;
    }

    public Double getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(Double valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
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

    public Integer getCodigocliente() {
        return codigocliente;
    }

    public void setCodigocliente(Integer codigocliente) {
        this.codigocliente = codigocliente;
    }

    public Integer getTransportadora() {
        return transportadora;
    }

    public void setTransportadora(Integer transportadora) {
        this.transportadora = transportadora;
    }

    public Integer getNotafiscal() {
        return notafiscal;
    }

    public void setNotafiscal(Integer notafiscal) {
        this.notafiscal = notafiscal;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getQuantidadevolume() {
        return quantidadevolume;
    }

    public void setQuantidadevolume(Double quantidadevolume) {
        this.quantidadevolume = quantidadevolume;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
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

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }

    public String getNomeTransportadora() {
        return nomeTransportadora;
    }

    public void setNomeTransportadora(String nomeTransportadora) {
        this.nomeTransportadora = nomeTransportadora;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
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

    public double getDias_ultimo_faturamento() {
        return dias_ultimo_faturamento;
    }

    public void setDias_ultimo_faturamento(double dias_ultimo_faturamento) {
        this.dias_ultimo_faturamento = dias_ultimo_faturamento;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    
    
    

}
