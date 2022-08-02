/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Date;

/**
 *
 * @author jefferson.luiz
 */
public class ComissaoTitulos {

    private Integer empresa;
    private Integer filial;
    private String numeroTitulo;
    private String tipoTitulo;
    private String transacao;
    private String situacao;
    private Date dataEmissao;
    private Date dataEntrada;
    private Integer cliente;
    private Integer representante;
    private String observacoes;
    private Date vencimentoOriginal;
    private Double valorOriginal;
    private Integer formaPagamento;
    private Double valorAberto;
    private Double percentualComissao;
    private Double baseComissao;
    private Double valorComissao;
    private String serieNota;
    private Integer numeroNota;
    private Integer pedido;
    private String GrupoContasRec;
    private String Categoria;
    private Cliente CadCliente;
    private Representante CadRepresentante;
  
    private ClienteGrupo CadClienteGrupo;
    
    public ClienteGrupo getCadClienteGrupo() {
        return CadClienteGrupo;
    }

    public void setCadClienteGrupo(ClienteGrupo CadClienteGrupo) {
        this.CadClienteGrupo = CadClienteGrupo;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String Categoria) {
        this.Categoria = Categoria;
    }

    public String getGrupoContasRec() {
        return GrupoContasRec;
    }

    public void setGrupoContasRec(String GrupoContasRec) {
        this.GrupoContasRec = GrupoContasRec;
    }

    public Representante getCadRepresentante() {
        return CadRepresentante;
    }

    public void setCadRepresentante(Representante CadRepresentante) {
        this.CadRepresentante = CadRepresentante;
    }

    public Double getValorComissao() {
        return valorComissao;
    }

    public void setValorComissao(Double valorComissao) {
        this.valorComissao = valorComissao;
    }

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
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

    public String getNumeroTitulo() {
        return numeroTitulo;
    }

    public void setNumeroTitulo(String numeroTitulo) {
        this.numeroTitulo = numeroTitulo;
    }

    public String getTipoTitulo() {
        return tipoTitulo;
    }

    public void setTipoTitulo(String tipoTitulo) {
        this.tipoTitulo = tipoTitulo;
    }

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Integer getCliente() {
        return cliente;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }       

    public Integer getRepresentante() {
        return representante;
    }

    public void setRepresentante(Integer representante) {
        this.representante = representante;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Date getVencimentoOriginal() {
        return vencimentoOriginal;
    }

    public void setVencimentoOriginal(Date vencimentoOriginal) {
        this.vencimentoOriginal = vencimentoOriginal;
    }

    public Double getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(Double valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public Integer getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(Integer formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public Double getValorAberto() {
        return valorAberto;
    }

    public void setValorAberto(Double valorAberto) {
        this.valorAberto = valorAberto;
    }

    public Double getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(Double percentualComissao) {
        this.percentualComissao = percentualComissao;
    }

    public Double getBaseComissao() {
        return baseComissao;
    }

    public void setBaseComissao(Double baseComissao) {
        this.baseComissao = baseComissao;
    }

    public String getSerieNota() {
        return serieNota;
    }

    public void setSerieNota(String serieNota) {
        this.serieNota = serieNota;
    }

    public Integer getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(Integer numeroNota) {
        this.numeroNota = numeroNota;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

}
