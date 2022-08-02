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
public class NotaSerie {

    private Integer id = 0;
    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer nota = 0;
    private String notaserie;
    private Date notaemissao;
    private Integer cliente = 0;
    private String clientenome;
    private String clienteestado;
    private String clientecidade;
    private String produto;
    private String origem;
    private String produtoderivacao;
    private String produtodescricao;
    private String serie;
    private Double quantidade;
    private Date producaodata;
    private Integer pedido = 0;
    private Integer representante_id = 0;

    private String situacao;
    private String fa_categoria = "fa-ban";
    private String color_categoria = "danger";
    private String btn_gerar_garantia = "false";

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getFa_categoria() {
        return fa_categoria;
    }

    public void setFa_categoria(String fa_categoria) {
        this.fa_categoria = fa_categoria;
    }

    public String getColor_categoria() {
        return color_categoria;
    }

    public void setColor_categoria(String color_categoria) {
        this.color_categoria = color_categoria;
    }

    public String getBtn_gerar_garantia() {
        return btn_gerar_garantia;
    }

    public void setBtn_gerar_garantia(String btn_gerar_garantia) {
        this.btn_gerar_garantia = btn_gerar_garantia;
    }

    
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getNotaserie() {
        return notaserie;
    }

    public void setNotaserie(String notaserie) {
        this.notaserie = notaserie;
    }

    public Date getNotaemissao() {
        return notaemissao;
    }

    public void setNotaemissao(Date notaemissao) {
        this.notaemissao = notaemissao;
    }

    public Integer getCliente() {
        return cliente;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }

    public String getClientenome() {
        return clientenome;
    }

    public void setClientenome(String clientenome) {
        this.clientenome = clientenome;
    }

    public String getClienteestado() {
        return clienteestado;
    }

    public void setClienteestado(String clienteestado) {
        this.clienteestado = clienteestado;
    }

    public String getClientecidade() {
        return clientecidade;
    }

    public void setClientecidade(String clientecidade) {
        this.clientecidade = clientecidade;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getProdutoderivacao() {
        return produtoderivacao;
    }

    public void setProdutoderivacao(String produtoderivacao) {
        this.produtoderivacao = produtoderivacao;
    }

    public String getProdutodescricao() {
        return produtodescricao;
    }

    public void setProdutodescricao(String produtodescricao) {
        this.produtodescricao = produtodescricao;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Date getProducaodata() {
        return producaodata;
    }

    public void setProducaodata(Date producaodata) {
        this.producaodata = producaodata;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public Integer getRepresentante_id() {
        return representante_id;
    }

    public void setRepresentante_id(Integer representante_id) {
        this.representante_id = representante_id;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    
    
}
