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
public class Atendimento {

    private Integer codigocliente = 0;
    private Integer sequencialancamento = 0;
    private String observacaotipo;
    private String observacao;
    private Integer lancamentousuario = 0;
    private Date lancamentodata;
    private Integer lancamentohora = 0;
    private String solucao;
    private Integer solucaousuario = 0;
    private Date solucaodata;
    private Integer solucaohora = 0;
    private Date dataproximavisita;
    private Integer sequenciacontato = 0;
    private String situacaoobservacao;

    private Integer indexp = 0;
    private Integer motivoobservacao = 0;
    private Integer solucaomotivo = 0;
    private Date visitadata;
    private Integer visitasequencia = 0;
    private Integer origemcliente = 0;
    private Integer origemsequencia = 0;
    private Integer pedidoempresa = 0;
    private Integer pedidofilial = 0;
    private Integer pedido = 0;
    private Integer sequenciaitempedido = 0;
    private String varser;
    private Integer empresacliente = 0;
    private String tabelapreco = "";
    private String observacaodetalhada;

    private String contatoobservacao;
    private Integer proximopasso = 0;
    private String concorrecia;
    private String outramarcas;

    private String enviarEmail;
    private String email;

    private String datalancamento;
    private String datavista;
    
    private String situacao;
    private String automoto;
    
    
    private MotivoSolucao motivoSolucao;

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getAutomoto() {
        return automoto;
    }

    public void setAutomoto(String automoto) {
        this.automoto = automoto;
    }

    
    
    public MotivoSolucao getMotivoSolucao() {
        return motivoSolucao;
    }

    public void setMotivoSolucao(MotivoSolucao motivoSolucao) {
        this.motivoSolucao = motivoSolucao;
    }
    
    
    

    public String getTabelapreco() {
        return tabelapreco;
    }

    public void setTabelapreco(String tabelapreco) {
        this.tabelapreco = tabelapreco;
    }

    public String getDatalancamento() {
        return datalancamento;
    }

    public void setDatalancamento(String datalancamento) {
        this.datalancamento = datalancamento;
    }

    public String getDatavista() {
        return datavista;
    }

    public void setDatavista(String datavista) {
        this.datavista = datavista;
    }

    private Motivo motivo;

    public Motivo getMotivo() {
        return motivo;
    }

    public void setMotivo(Motivo motivo) {
        this.motivo = motivo;
    }

    public String getEnviarEmail() {
        return enviarEmail;
    }

    public void setEnviarEmail(String enviarEmail) {
        this.enviarEmail = enviarEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOutramarcas() {
        return outramarcas;
    }

    public void setOutramarcas(String outramarcas) {
        this.outramarcas = outramarcas;
    }

    public String getConcorrecia() {
        return concorrecia;
    }

    public void setConcorrecia(String concorrecia) {
        this.concorrecia = concorrecia;
    }

    public String getContatoobservacao() {
        return contatoobservacao;
    }

    public void setContatoobservacao(String contatoobservacao) {
        this.contatoobservacao = contatoobservacao;
    }

    public Integer getProximopasso() {
        return proximopasso;
    }

    public void setProximopasso(Integer proximopasso) {
        this.proximopasso = proximopasso;
    }

    private Cliente cliente;

    public Integer getCodigocliente() {
        return codigocliente;
    }

    public void setCodigocliente(Integer codigocliente) {
        this.codigocliente = codigocliente;
    }

    public Integer getSequencialancamento() {
        return sequencialancamento;
    }

    public void setSequencialancamento(Integer sequencialancamento) {
        this.sequencialancamento = sequencialancamento;
    }

    public String getObservacaotipo() {
        return observacaotipo;
    }

    public void setObservacaotipo(String observacaotipo) {
        this.observacaotipo = observacaotipo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Integer getLancamentousuario() {
        return lancamentousuario;
    }

    public void setLancamentousuario(Integer lancamentousuario) {
        this.lancamentousuario = lancamentousuario;
    }

    public Date getLancamentodata() {
        return lancamentodata;
    }

    public void setLancamentodata(Date lancamentodata) {
        this.lancamentodata = lancamentodata;
    }

    public Integer getLancamentohora() {
        return lancamentohora;
    }

    public void setLancamentohora(Integer lancamentohora) {
        this.lancamentohora = lancamentohora;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    public Integer getSolucaousuario() {
        return solucaousuario;
    }

    public void setSolucaousuario(Integer solucaousuario) {
        this.solucaousuario = solucaousuario;
    }

    public Date getSolucaodata() {
        return solucaodata;
    }

    public void setSolucaodata(Date solucaodata) {
        this.solucaodata = solucaodata;
    }

    public Integer getSolucaohora() {
        return solucaohora;
    }

    public void setSolucaohora(Integer solucaohora) {
        this.solucaohora = solucaohora;
    }

    public Date getDataproximavisita() {
        return dataproximavisita;
    }

    public void setDataproximavisita(Date dataproximavisita) {
        this.dataproximavisita = dataproximavisita;
    }

    public Integer getSequenciacontato() {
        return sequenciacontato;
    }

    public void setSequenciacontato(Integer sequenciacontato) {
        this.sequenciacontato = sequenciacontato;
    }

    public String getSituacaoobservacao() {
        return situacaoobservacao;
    }

    public void setSituacaoobservacao(String situacaoobservacao) {
        this.situacaoobservacao = situacaoobservacao;
    }

    public Integer getIndexp() {
        return indexp;
    }

    public void setIndexp(Integer indexp) {
        this.indexp = indexp;
    }

    public Integer getMotivoobservacao() {
        return motivoobservacao;
    }

    public void setMotivoobservacao(Integer motivoobservacao) {
        this.motivoobservacao = motivoobservacao;
    }

    public Integer getSolucaomotivo() {
        return solucaomotivo;
    }

    public void setSolucaomotivo(Integer solucaomotivo) {
        this.solucaomotivo = solucaomotivo;
    }

    public Date getVisitadata() {
        return visitadata;
    }

    public void setVisitadata(Date visitadata) {
        this.visitadata = visitadata;
    }

    public Integer getVisitasequencia() {
        return visitasequencia;
    }

    public void setVisitasequencia(Integer visitasequencia) {
        this.visitasequencia = visitasequencia;
    }

    public Integer getOrigemcliente() {
        return origemcliente;
    }

    public void setOrigemcliente(Integer origemcliente) {
        this.origemcliente = origemcliente;
    }

    public Integer getOrigemsequencia() {
        return origemsequencia;
    }

    public void setOrigemsequencia(Integer origemsequencia) {
        this.origemsequencia = origemsequencia;
    }

    public Integer getPedidoempresa() {
        return pedidoempresa;
    }

    public void setPedidoempresa(Integer pedidoempresa) {
        this.pedidoempresa = pedidoempresa;
    }

    public Integer getPedidofilial() {
        return pedidofilial;
    }

    public void setPedidofilial(Integer pedidofilial) {
        this.pedidofilial = pedidofilial;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public Integer getSequenciaitempedido() {
        return sequenciaitempedido;
    }

    public void setSequenciaitempedido(Integer sequenciaitempedido) {
        this.sequenciaitempedido = sequenciaitempedido;
    }

    public String getVarser() {
        return varser;
    }

    public void setVarser(String varser) {
        this.varser = varser;
    }

    public Integer getEmpresacliente() {
        return empresacliente;
    }

    public void setEmpresacliente(Integer empresacliente) {
        this.empresacliente = empresacliente;
    }

    public String getObservacaodetalhada() {
        return observacaodetalhada;
    }

    public void setObservacaodetalhada(String observacaodetalhada) {
        this.observacaodetalhada = observacaodetalhada;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

}
