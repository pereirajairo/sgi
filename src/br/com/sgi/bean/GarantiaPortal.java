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
public class GarantiaPortal {

    private Integer id = 0;
    private Integer agrupador = 0;

    private Integer fornecedor_erp = 0;

    private Integer empresa_destino = 0;
    private Integer sequencia_erp = 0;
    private Integer nota = 0;
    private Integer sequenciaitem = 0;
    private Integer pontogarantia_id = 0;

    private Integer pedido = 0;
    private Integer cliente = 0;
    private Integer usuario_id = 0;
    private Integer cliente_id = 0;
    private Integer representante_id = 0;
    private Integer motivo_id = 0;
    private String motivo_codigo = "0";
    private String motivo_descricao = "Não encontrado";

    private Integer conta_id = 0;
    private String disposicao;
    private String clientenome;
    private String clienteestado;
    private String clientecidade;
    private String serie = "";
    private String produto;
    private String produtodescricao;
    private String produtoderivacao;
    private Date notaemissao;
    private Date prazogarantia;
    private Date prazogarantiamaximo;
    private Date dataabertura;

    private Double precounitario = 0.0;
    private String notaserie;
    private String certificado;

    private double garantiames = 18;
    private int garantiadia = 500;

    private String descricaoproblema;
    private Double tempouso;
    private String situacao;
    private String situacaogeral;

    private String horaabertura;
    private String ipglobal;
    private String email;

    private double tempousodia = 0;
    private double tempousomes = 0;
    private Double quantidade = 1.0;

    private String notafiscalgarantia = "0";
    private Date notaFiscalGarantiaData;

    private Date created;
    private Date modified;

    private boolean gerarGarantia = false;

    private String garatiasde = "";
    private String garantiacodigode = "";
    private String tipo = "";

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
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

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getProdutodescricao() {
        return produtodescricao;
    }

    public void setProdutodescricao(String produtodescricao) {
        this.produtodescricao = produtodescricao;
    }

    public Date getNotaemissao() {
        return notaemissao;
    }

    public void setNotaemissao(Date notaemissao) {
        this.notaemissao = notaemissao;
    }

    public double getGarantiames() {
        return garantiames;
    }

    public void setGarantiames(double garantiames) {
        this.garantiames = garantiames;
    }

    public int getGarantiadia() {
        return garantiadia;
    }

    public void setGarantiadia(int garantiadia) {
        this.garantiadia = garantiadia;
    }

    public Date getPrazogarantia() {
        return prazogarantia;
    }

    public void setPrazogarantia(Date prazogarantia) {
        this.prazogarantia = prazogarantia;
    }

    public String getDescricaoproblema() {
        return descricaoproblema;
    }

    public void setDescricaoproblema(String descricaoproblema) {
        this.descricaoproblema = descricaoproblema;
    }

    public Double getTempouso() {
        return tempouso;
    }

    public void setTempouso(Double tempouso) {
        this.tempouso = tempouso;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Date getDataabertura() {
        return dataabertura;
    }

    public void setDataabertura(Date dataabertura) {
        this.dataabertura = dataabertura;
    }

    public String getHoraabertura() {
        return horaabertura;
    }

    public void setHoraabertura(String horaabertura) {
        this.horaabertura = horaabertura;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public double getTempousodia() {
        return tempousodia;
    }

    public void setTempousodia(double tempousodia) {
        this.tempousodia = tempousodia;
    }

    public double getTempousomes() {
        return tempousomes;
    }

    public void setTempousomes(double tempousomes) {
        this.tempousomes = tempousomes;
    }

    public Integer getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(Integer usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getIpglobal() {
        return ipglobal;
    }

    public void setIpglobal(String ipglobal) {
        this.ipglobal = ipglobal;
    }

    public String getProdutoderivacao() {
        return produtoderivacao;
    }

    public void setProdutoderivacao(String produtoderivacao) {
        this.produtoderivacao = produtoderivacao;
    }

    public boolean isGerarGarantia() {
        return gerarGarantia;
    }

    public void setGerarGarantia(boolean gerarGarantia) {
        this.gerarGarantia = gerarGarantia;
    }

    public Integer getMotivo_id() {
        return motivo_id;
    }

    public void setMotivo_id(Integer motivo_id) {
        this.motivo_id = motivo_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getConta_id() {
        return conta_id;
    }

    public void setConta_id(Integer conta_id) {
        this.conta_id = conta_id;
    }

    public String getNotafiscalgarantia() {
        return notafiscalgarantia;
    }

    public void setNotafiscalgarantia(String notafiscalgarantia) {
        this.notafiscalgarantia = notafiscalgarantia;
    }

    public Date getNotaFiscalGarantiaData() {
        return notaFiscalGarantiaData;
    }

    public void setNotaFiscalGarantiaData(Date notaFiscalGarantiaData) {
        this.notaFiscalGarantiaData = notaFiscalGarantiaData;
    }

    public Integer getSequenciaitem() {
        return sequenciaitem;
    }

    public void setSequenciaitem(Integer sequenciaitem) {
        this.sequenciaitem = sequenciaitem;
    }

    public Integer getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(Integer cliente_id) {
        this.cliente_id = cliente_id;
    }

    public Integer getRepresentante_id() {
        return representante_id;
    }

    public void setRepresentante_id(Integer representante_id) {
        this.representante_id = representante_id;
    }

    public String getGaratiasde() {
        return garatiasde;
    }

    public void setGaratiasde(String garatiasde) {
        this.garatiasde = garatiasde;
    }

    public String getGarantiacodigode() {
        return garantiacodigode;
    }

    public void setGarantiacodigode(String garantiacodigode) {
        this.garantiacodigode = garantiacodigode;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(Integer agrupador) {
        this.agrupador = agrupador;
    }

    public String getDisposicao() {
        return disposicao;
    }

    public void setDisposicao(String disposicao) {
        this.disposicao = disposicao;
    }

    public Double getPrecounitario() {
        return precounitario;
    }

    public void setPrecounitario(Double precounitario) {
        this.precounitario = precounitario;
    }

    public String getNotaserie() {
        return notaserie;
    }

    public void setNotaserie(String notaserie) {
        this.notaserie = notaserie;
    }

    public String getCertificado() {
        return certificado;
    }

    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    public Date getPrazogarantiamaximo() {
        return prazogarantiamaximo;
    }

    public void setPrazogarantiamaximo(Date prazogarantiamaximo) {
        this.prazogarantiamaximo = prazogarantiamaximo;
    }

    public Integer getPontogarantia_id() {
        return pontogarantia_id;
    }

    public void setPontogarantia_id(Integer pontogarantia_id) {
        this.pontogarantia_id = pontogarantia_id;
    }

    public Integer getFornecedor_erp() {
        return fornecedor_erp;
    }

    public void setFornecedor_erp(Integer fornecedor_erp) {
        this.fornecedor_erp = fornecedor_erp;
    }

    public String getMotivo_codigo() {
        return motivo_codigo;
    }

    public void setMotivo_codigo(String motivo_codigo) {
        this.motivo_codigo = motivo_codigo;
    }

    public String getMotivo_descricao() {
        return motivo_descricao;
    }

    public void setMotivo_descricao(String motivo_descricao) {
        this.motivo_descricao = motivo_descricao;
    }

    public Integer getEmpresa_destino() {
        return empresa_destino;
    }

    public void setEmpresa_destino(Integer empresa_destino) {
        this.empresa_destino = empresa_destino;
    }

    public Integer getSequencia_erp() {
        return sequencia_erp;
    }

    public void setSequencia_erp(Integer sequencia_erp) {
        this.sequencia_erp = sequencia_erp;
    }

    public String getSituacaogeral() {
        return situacaogeral;
    }

    public void setSituacaogeral(String situacaogeral) {
        this.situacaogeral = situacaogeral;
    }

    
    
}
