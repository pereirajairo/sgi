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
public class PedidoHubProduto {

    private Integer transportadora = 0;
    private Integer id = 0;
    private Integer cliente = 0;
    private Integer pedido = 0;
    private Integer pedido_id = 0;
    private Integer conta_id = 0;
    private Integer sequenciaitem = 0;
    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer notafiscal = 0;
    private Integer codigoacesso = 0;
    private Integer representante = 0;
    private String operacao;
    private String clientenome;
    private String situacao;
    private String situacaoserie;
    private Date datapedido;
    private String datapedidoS;
    private Date datafaturar;
    private String datafaturarS;
    private String linha;
    private Date dataenvio;
    private Date dataseparacao;
    private Date dataretorno;
    private String produto;
    private String descricao;
    private Double quantidade = 0.0;
    private Double quantidade_lidos = 0.0;
    private Double quantidade_saldo = 0.0;
    private Double pesoproduto = 0.0;
    private Double quantidadeCaixa = 0.0;
    private double  quantidadeVolume = 0.0;
    private int totalVolumes = 0;
    
    private String cidade;
    private String estado;
    private String btn_enviar;
    private String btn_entregar;
    private String serienota;
    private String endereco;
    private String color_categoria;
    private String fa_categoria;

    private String color_categoria_serie;
    private String fa_categoria_serie;

    // pedido
    private Double pesopedido = 0.0;

    //sucata 
    private Integer sucata_id = 0;

    private Double pesosucata = 0.0;
    private Double pesorecebido = 0.0;
    private Double pesosaldo = 0.0;

    private Cliente CadCliente;
    private NotaFiscal CadNotaFiscal;
    private Transportadora CadTransportadora;

    public Integer getPedido_id() {
        return pedido_id;
    }

    public void setPedido_id(Integer pedido_id) {
        this.pedido_id = pedido_id;
    }

    public Integer getConta_id() {
        return conta_id;
    }

    public void setConta_id(Integer conta_id) {
        this.conta_id = conta_id;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public Integer getTransportadora() {
        return transportadora;
    }

    public void setTransportadora(Integer transportadora) {
        this.transportadora = transportadora;
    }

    public Transportadora getCadTransportadora() {
        return CadTransportadora;
    }

    public void setCadTransportadora(Transportadora CadTransportadora) {
        this.CadTransportadora = CadTransportadora;
    }

    public NotaFiscal getCadNotaFiscal() {
        return CadNotaFiscal;
    }

    public void setCadNotaFiscal(NotaFiscal CadNotaFiscal) {
        this.CadNotaFiscal = CadNotaFiscal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCliente() {
        return cliente;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public Integer getSequenciaitem() {
        return sequenciaitem;
    }

    public void setSequenciaitem(Integer sequenciaitem) {
        this.sequenciaitem = sequenciaitem;
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

    public Integer getNotafiscal() {
        return notafiscal;
    }

    public void setNotafiscal(Integer notafiscal) {
        this.notafiscal = notafiscal;
    }

    public Integer getCodigoacesso() {
        return codigoacesso;
    }

    public void setCodigoacesso(Integer codigoacesso) {
        this.codigoacesso = codigoacesso;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getClientenome() {
        return clientenome;
    }

    public void setClientenome(String clientenome) {
        this.clientenome = clientenome;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Date getDataenvio() {
        return dataenvio;
    }

    public void setDataenvio(Date dataenvio) {
        this.dataenvio = dataenvio;
    }

    public Date getDataseparacao() {
        return dataseparacao;
    }

    public void setDataseparacao(Date dataseparacao) {
        this.dataseparacao = dataseparacao;
    }

    public Date getDataretorno() {
        return dataretorno;
    }

    public void setDataretorno(Date dataretorno) {
        this.dataretorno = dataretorno;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getQuantidade_lidos() {
        return quantidade_lidos;
    }

    public void setQuantidade_lidos(Double quantidade_lidos) {
        this.quantidade_lidos = quantidade_lidos;
    }

    public Double getQuantidade_saldo() {
        return quantidade_saldo;
    }

    public void setQuantidade_saldo(Double quantidade_saldo) {
        this.quantidade_saldo = quantidade_saldo;
    }

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

    public String getBtn_enviar() {
        return btn_enviar;
    }

    public void setBtn_enviar(String btn_enviar) {
        this.btn_enviar = btn_enviar;
    }

    public String getBtn_entregar() {
        return btn_entregar;
    }

    public void setBtn_entregar(String btn_entregar) {
        this.btn_entregar = btn_entregar;
    }

    public String getSerienota() {
        return serienota;
    }

    public void setSerienota(String serienota) {
        this.serienota = serienota;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getColor_categoria() {
        return color_categoria;
    }

    public void setColor_categoria(String color_categoria) {
        this.color_categoria = color_categoria;
    }

    public String getFa_categoria() {
        return fa_categoria;
    }

    public void setFa_categoria(String fa_categoria) {
        this.fa_categoria = fa_categoria;
    }

    public String getColor_categoria_serie() {
        return color_categoria_serie;
    }

    public void setColor_categoria_serie(String color_categoria_serie) {
        this.color_categoria_serie = color_categoria_serie;
    }

    public String getFa_categoria_serie() {
        return fa_categoria_serie;
    }

    public void setFa_categoria_serie(String fa_categoria_serie) {
        this.fa_categoria_serie = fa_categoria_serie;
    }

    public Integer getSucata_id() {
        return sucata_id;
    }

    public void setSucata_id(Integer sucata_id) {
        this.sucata_id = sucata_id;
    }

    public Double getPesosucata() {
        return pesosucata;
    }

    public void setPesosucata(Double pesosucata) {
        this.pesosucata = pesosucata;
    }

    public Double getPesorecebido() {
        return pesorecebido;
    }

    public void setPesorecebido(Double pesorecebido) {
        this.pesorecebido = pesorecebido;
    }

    public Double getPesosaldo() {
        return pesosaldo;
    }

    public void setPesosaldo(Double pesosaldo) {
        this.pesosaldo = pesosaldo;
    }

    public Integer getRepresentante() {
        return representante;
    }

    public void setRepresentante(Integer representante) {
        this.representante = representante;
    }

    public String getSituacaoserie() {
        return situacaoserie;
    }

    public void setSituacaoserie(String situacaoserie) {
        this.situacaoserie = situacaoserie;
    }

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
    }

    public Date getDatapedido() {
        return datapedido;
    }

    public void setDatapedido(Date datapedido) {
        this.datapedido = datapedido;
    }

    public String getDatapedidoS() {
        return datapedidoS;
    }

    public void setDatapedidoS(String datapedidoS) {
        this.datapedidoS = datapedidoS;
    }

    public Date getDatafaturar() {
        return datafaturar;
    }

    public void setDatafaturar(Date datafaturar) {
        this.datafaturar = datafaturar;
    }

    public String getDatafaturarS() {
        return datafaturarS;
    }

    public void setDatafaturarS(String datafaturarS) {
        this.datafaturarS = datafaturarS;
    }

    public Double getPesopedido() {
        return pesopedido;
    }

    public void setPesopedido(Double pesopedido) {
        this.pesopedido = pesopedido;
    }

    public Double getPesoproduto() {
        return pesoproduto;
    }

    public void setPesoproduto(Double pesoproduto) {
        this.pesoproduto = pesoproduto;
    }

    public Double getQuantidadeCaixa() {
        return quantidadeCaixa;
    }

    public void setQuantidadeCaixa(Double quantidadeCaixa) {
        this.quantidadeCaixa = quantidadeCaixa;
    }

    public double getQuantidadeVolume() {
        return quantidadeVolume;
    }

    public void setQuantidadeVolume(double quantidadeVolume) {
        this.quantidadeVolume = quantidadeVolume;
    }

    public int getTotalVolumes() {
        return totalVolumes;
    }

    public void setTotalVolumes(int totalVolumes) {
        this.totalVolumes = totalVolumes;
    }

 
    
    

}
