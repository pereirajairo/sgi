/*
 * To change this license header; choose License Headers in Project Properties.
 * To change this template file; choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Date;

/**
 *
 * @author jairo.silva
 */
public class PedidoHub {

    private static final long serialVersionUID = 2606254772949774110L;
    private Integer id = 0;
    private Integer cliente = 0;
    private Integer pedido = 0;
    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer notafiscal = 0;
    private Integer conta_id = 0;
    private Integer codigoacesso = 0;
    
    private String serienota;
   

    private String clientenome;
    private String situacao;
    private String gerarminutacoleta = "N";

    private Date datafaturar;
    private Date dataenvio;
    private Date dataseparacao;
    private Date dataretorno;
    private Date dataentrega;
    private Date dataminuta;
    private Date datapedido;

    private String datafaturarS;
    private String datapedidoS;
    private String transacao;
    private String tabelapreco;

    private Double quantidade = 0.0;
    private Double quantidade_lidos = 0.0;
    private Double quantidade_saldo = 0.0;
    private Double perSeparacao = 0.0;
    private String perSeparacaoS = "0";

    private Double perEntrega = 0.0;
    private String perEntregaS = "0";

    private String cidade;
    private String estado;


    private String endereco;
    private String color_categoria;
    private String fa_categoria;
    private String fa_minuta = "globe";
    private String color_minuta = "danger";

    private String color_categoria_serie;
    private String fa_categoria_serie;

    private String btn_enviar_pedido;
    private String btn_enviar;
    private String btn_entregar;
    private String btn_excluir_serie;
    private String btn_confimar_entrega;
    private String btn_minuta;

    //sucata 
    private Integer sucata_id = 0;
    private String color_sucata = "default";
    private String situacaoSucata = "PEDIDO SEM SUCATA";
    private Double pesosucata = 0.0;
    private Double pesorecebido = 0.0;
    private Double pesosaldo = 0.0;
    private Double pesopedido = 0.0;
    private Integer diatransporte = 0;

    private String situacaoPedido;
    private String situacaoMinuta;
    private String situacaoPedidoHub;
    private String liberadoHub;

    private String operacao;
    private String situacaoserie;
    private String integrar = "N";
    private String integrarnota = "N";
    private String linha;

    private Integer transportadora = 0;

    private Cliente CadCliente;
    private NotaFiscal CadNotaFiscal;
    private Transportadora CadTransportadora;

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

    public Integer getConta_id() {
        return conta_id;
    }

    public void setConta_id(Integer conta_id) {
        this.conta_id = conta_id;
    }

    public Integer getCodigoacesso() {
        return codigoacesso;
    }

    public void setCodigoacesso(Integer codigoacesso) {
        this.codigoacesso = codigoacesso;
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

    public String getGerarminutacoleta() {
        return gerarminutacoleta;
    }

    public void setGerarminutacoleta(String gerarminutacoleta) {
        this.gerarminutacoleta = gerarminutacoleta;
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

    public Date getDataentrega() {
        return dataentrega;
    }

    public void setDataentrega(Date dataentrega) {
        this.dataentrega = dataentrega;
    }

    public Date getDataminuta() {
        return dataminuta;
    }

    public void setDataminuta(Date dataminuta) {
        this.dataminuta = dataminuta;
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

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }

    public String getTabelapreco() {
        return tabelapreco;
    }

    public void setTabelapreco(String tabelapreco) {
        this.tabelapreco = tabelapreco;
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

    public Double getPerSeparacao() {
        return perSeparacao;
    }

    public void setPerSeparacao(Double perSeparacao) {
        this.perSeparacao = perSeparacao;
    }

    public String getPerSeparacaoS() {
        return perSeparacaoS;
    }

    public void setPerSeparacaoS(String perSeparacaoS) {
        this.perSeparacaoS = perSeparacaoS;
    }

    public Double getPerEntrega() {
        return perEntrega;
    }

    public void setPerEntrega(Double perEntrega) {
        this.perEntrega = perEntrega;
    }

    public String getPerEntregaS() {
        return perEntregaS;
    }

    public void setPerEntregaS(String perEntregaS) {
        this.perEntregaS = perEntregaS;
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

    public String getFa_minuta() {
        return fa_minuta;
    }

    public void setFa_minuta(String fa_minuta) {
        this.fa_minuta = fa_minuta;
    }

    public String getColor_minuta() {
        return color_minuta;
    }

    public void setColor_minuta(String color_minuta) {
        this.color_minuta = color_minuta;
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

    public String getBtn_enviar_pedido() {
        return btn_enviar_pedido;
    }

    public void setBtn_enviar_pedido(String btn_enviar_pedido) {
        this.btn_enviar_pedido = btn_enviar_pedido;
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

    public String getBtn_excluir_serie() {
        return btn_excluir_serie;
    }

    public void setBtn_excluir_serie(String btn_excluir_serie) {
        this.btn_excluir_serie = btn_excluir_serie;
    }

    public String getBtn_confimar_entrega() {
        return btn_confimar_entrega;
    }

    public void setBtn_confimar_entrega(String btn_confimar_entrega) {
        this.btn_confimar_entrega = btn_confimar_entrega;
    }

    public String getBtn_minuta() {
        return btn_minuta;
    }

    public void setBtn_minuta(String btn_minuta) {
        this.btn_minuta = btn_minuta;
    }

    public Integer getSucata_id() {
        return sucata_id;
    }

    public void setSucata_id(Integer sucata_id) {
        this.sucata_id = sucata_id;
    }

    public String getColor_sucata() {
        return color_sucata;
    }

    public void setColor_sucata(String color_sucata) {
        this.color_sucata = color_sucata;
    }

    public String getSituacaoSucata() {
        return situacaoSucata;
    }

    public void setSituacaoSucata(String situacaoSucata) {
        this.situacaoSucata = situacaoSucata;
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

    public Double getPesopedido() {
        return pesopedido;
    }

    public void setPesopedido(Double pesopedido) {
        this.pesopedido = pesopedido;
    }

    public Integer getDiatransporte() {
        return diatransporte;
    }

    public void setDiatransporte(Integer diatransporte) {
        this.diatransporte = diatransporte;
    }

    public String getSituacaoPedido() {
        return situacaoPedido;
    }

    public void setSituacaoPedido(String situacaoPedido) {
        this.situacaoPedido = situacaoPedido;
    }

    public String getSituacaoMinuta() {
        return situacaoMinuta;
    }

    public void setSituacaoMinuta(String situacaoMinuta) {
        this.situacaoMinuta = situacaoMinuta;
    }

    public String getSituacaoPedidoHub() {
        return situacaoPedidoHub;
    }

    public void setSituacaoPedidoHub(String situacaoPedidoHub) {
        this.situacaoPedidoHub = situacaoPedidoHub;
    }

    public String getLiberadoHub() {
        return liberadoHub;
    }

    public void setLiberadoHub(String liberadoHub) {
        this.liberadoHub = liberadoHub;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getSituacaoserie() {
        return situacaoserie;
    }

    public void setSituacaoserie(String situacaoserie) {
        this.situacaoserie = situacaoserie;
    }

    public String getIntegrar() {
        return integrar;
    }

    public void setIntegrar(String integrar) {
        this.integrar = integrar;
    }

    public String getIntegrarnota() {
        return integrarnota;
    }

    public void setIntegrarnota(String integrarnota) {
        this.integrarnota = integrarnota;
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

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
    }

    public NotaFiscal getCadNotaFiscal() {
        return CadNotaFiscal;
    }

    public void setCadNotaFiscal(NotaFiscal CadNotaFiscal) {
        this.CadNotaFiscal = CadNotaFiscal;
    }

    public Transportadora getCadTransportadora() {
        return CadTransportadora;
    }

    public void setCadTransportadora(Transportadora CadTransportadora) {
        this.CadTransportadora = CadTransportadora;
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

  
    
    
    
    
    

}
