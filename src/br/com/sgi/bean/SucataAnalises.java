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
public class SucataAnalises {

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer nota = 0;
    private String tabela;
    private Date emissao_nota;
    private Date emissao_pedido;
    private Date fechamento_pedido;
    private Date emissao_sucata;
    private Integer cliente = 0;
    private String nome;
    private Double peso_bruto_nota = 0.0;
    private Double peso_sucata = 0.0;
    private Double peso_item_nota = 0.0;
    private Double quantidade = 0.0;
    private String estado;
    private String cidade;
    private String transportadora;
    private String agrupamento;
    private String nome_filial;
    private Integer lancamento = 0;
    private Integer sequencia = 0;
    private Integer ordem_compra = 0;
    private Double peso_ordem_compra = 0.0;
    private Integer pedido_sucata = 0;
    private Integer pedido_nota = 0;
    private String linha;
    private Integer nota_entrada = 0;
    private Double peso_entrada = 0.0;
    private String descricao_transacao;
    private String pedido_transacao;
    private String gerar_sucata;
    private String liberadoentrega;
    private String serienota;
    private String sucata;
    
    private Cliente CadCliente;

    public String getPedido_transacao() {
        return pedido_transacao;
    }

    public void setPedido_transacao(String pedido_transacao) {
        this.pedido_transacao = pedido_transacao;
    }

    public String getGerar_sucata() {
        return gerar_sucata;
    }

    public void setGerar_sucata(String gerar_sucata) {
        this.gerar_sucata = gerar_sucata;
    }

    public String getDescricao_transacao() {
        return descricao_transacao;
    }

    public void setDescricao_transacao(String descricao_transacao) {
        this.descricao_transacao = descricao_transacao;
    }

    public Integer getNota_entrada() {
        return nota_entrada;
    }

    public void setNota_entrada(Integer nota_entrada) {
        this.nota_entrada = nota_entrada;
    }

    public Double getPeso_entrada() {
        return peso_entrada;
    }

    public void setPeso_entrada(Double peso_entrada) {
        this.peso_entrada = peso_entrada;
    }

    public Double getPeso_ordem_compra() {
        return peso_ordem_compra;
    }

    public void setPeso_ordem_compra(Double peso_ordem_compra) {
        this.peso_ordem_compra = peso_ordem_compra;
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

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public Date getEmissao_nota() {
        return emissao_nota;
    }

    public void setEmissao_nota(Date emissao_nota) {
        this.emissao_nota = emissao_nota;
    }

    public Date getEmissao_pedido() {
        return emissao_pedido;
    }

    public void setEmissao_pedido(Date emissao_pedido) {
        this.emissao_pedido = emissao_pedido;
    }

    public Date getFechamento_pedido() {
        return fechamento_pedido;
    }

    public void setFechamento_pedido(Date fechamento_pedido) {
        this.fechamento_pedido = fechamento_pedido;
    }

    public Date getEmissao_sucata() {
        return emissao_sucata;
    }

    public void setEmissao_sucata(Date emissao_sucata) {
        this.emissao_sucata = emissao_sucata;
    }

    public Integer getCliente() {
        return cliente;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPeso_bruto_nota() {
        return peso_bruto_nota;
    }

    public void setPeso_bruto_nota(Double peso_bruto_nota) {
        this.peso_bruto_nota = peso_bruto_nota;
    }

    public Double getPeso_sucata() {
        return peso_sucata;
    }

    public void setPeso_sucata(Double peso_sucata) {
        this.peso_sucata = peso_sucata;
    }

    public Double getPeso_item_nota() {
        return peso_item_nota;
    }

    public void setPeso_item_nota(Double peso_item_nota) {
        this.peso_item_nota = peso_item_nota;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTransportadora() {
        return transportadora;
    }

    public void setTransportadora(String transportadora) {
        this.transportadora = transportadora;
    }

    public String getAgrupamento() {
        return agrupamento;
    }

    public void setAgrupamento(String agrupamento) {
        this.agrupamento = agrupamento;
    }

    public String getNome_filial() {
        return nome_filial;
    }

    public void setNome_filial(String nome_filial) {
        this.nome_filial = nome_filial;
    }

    public Integer getLancamento() {
        return lancamento;
    }

    public void setLancamento(Integer lancamento) {
        this.lancamento = lancamento;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public Integer getOrdem_compra() {
        return ordem_compra;
    }

    public void setOrdem_compra(Integer ordem_compra) {
        this.ordem_compra = ordem_compra;
    }

    public Integer getPedido_sucata() {
        return pedido_sucata;
    }

    public void setPedido_sucata(Integer pedido_sucata) {
        this.pedido_sucata = pedido_sucata;
    }

    public Integer getPedido_nota() {
        return pedido_nota;
    }

    public void setPedido_nota(Integer pedido_nota) {
        this.pedido_nota = pedido_nota;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public String getLiberadoentrega() {
        return liberadoentrega;
    }

    public void setLiberadoentrega(String liberadoentrega) {
        this.liberadoentrega = liberadoentrega;
    }

    public String getSerienota() {
        return serienota;
    }

    public void setSerienota(String serienota) {
        this.serienota = serienota;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
    }

    public String getSucata() {
        return sucata;
    }

    public void setSucata(String sucata) {
        this.sucata = sucata;
    }
    
    

}
