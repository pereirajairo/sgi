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
public class Pedido {

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer cliente = 0;
    private Integer pedido = 0;
    private Integer nota = 0;
    private Integer numeropre = 0;
    private Integer numeroanalise = 0;
    private Integer sucata_id = 0;
    private Integer codigominuta = 0;

    private String pedidobloqueado;

    private Date emissao;
    private String emissaoS;
    private Date DataAgendamento;
    private String DataAgendamentoS;
    private Date databloqueio;
    private String databloqueioS;

    private Date dataliberacao;
    private String dataliberacaoS;

    private Date dataSeparacao;
    private String dataSeparacaoS;

    private Date data_para_faturar;
    private String data_para_faturarS;

    private Date entrega;
    private String entregaS;

    private Date datagendada;

    private Integer dia_transporte = 0;
    private Double qtddia_separacao = 0.0;
    private Double qtddia_separacao_emissao = 0.0;
    private Double qtddia_emissao_hoje = 0.0;
    private Double qtddia_atrazo = 0.0;

    private String produto;
    private String transacao;
    private String tipodocumento;
    private String tipodpedido;
    private String situacaopre;
    private Double peso = 0.0;
    private Double quantidade = 0.0;
    private Double quantidadedias = 0.0;
    private Double quantidadediascomercial = 0.0;
    private Double percentualRentabilidade = 0.0;
    private Double pesoRentabilidade = 0.0;

    private String situacaoLogistica;
    private String observacaobloqueio;
    private String observacaoliberacao;
    private Integer usuario = 0;
    private Integer usuarioliberacao = 0;

    private String enviaremail;
    private String emailpara;
    private String liberarMinuta;
    private String liberadoHub;

    private String linha; // Auto ou Moto

    private String situacaoPedido;

    public String getLiberadoHub() {
        return liberadoHub;
    }

    public void setLiberadoHub(String liberadoHub) {
        this.liberadoHub = liberadoHub;
    }

    public Date getDatagendada() {
        return datagendada;
    }

    public void setDatagendada(Date datagendada) {
        this.datagendada = datagendada;
    }

    public Integer getNumeropre() {
        return numeropre;
    }

    public void setNumeropre(Integer numeropre) {
        this.numeropre = numeropre;
    }

    public Integer getNumeroanalise() {
        return numeroanalise;
    }

    public void setNumeroanalise(Integer numeroanalise) {
        this.numeroanalise = numeroanalise;
    }

    public Double getQtddia_atrazo() {
        return qtddia_atrazo;
    }

    public void setQtddia_atrazo(Double qtddia_atrazo) {
        this.qtddia_atrazo = qtddia_atrazo;
    }

    private Integer codigoTransportadora = 0;

    private Transportadora CadTransportadora;

    private Cliente CadCliente;

    public Date getDataSeparacao() {
        return dataSeparacao;
    }

    public void setDataSeparacao(Date dataSeparacao) {
        this.dataSeparacao = dataSeparacao;
    }

    public String getDataSeparacaoS() {
        return dataSeparacaoS;
    }

    public void setDataSeparacaoS(String dataSeparacaoS) {
        this.dataSeparacaoS = dataSeparacaoS;
    }

    public Date getData_para_faturar() {
        return data_para_faturar;
    }

    public void setData_para_faturar(Date data_para_faturar) {
        this.data_para_faturar = data_para_faturar;
    }

    public String getData_para_faturarS() {
        return data_para_faturarS;
    }

    public void setData_para_faturarS(String data_para_faturarS) {
        this.data_para_faturarS = data_para_faturarS;
    }

    public Integer getDia_transporte() {
        return dia_transporte;
    }

    public void setDia_transporte(Integer dia_transporte) {
        this.dia_transporte = dia_transporte;
    }

    public Double getQtddia_separacao() {
        return qtddia_separacao;
    }

    public void setQtddia_separacao(Double qtddia_separacao) {
        this.qtddia_separacao = qtddia_separacao;
    }

    public Double getQtddia_separacao_emissao() {
        return qtddia_separacao_emissao;
    }

    public void setQtddia_separacao_emissao(Double qtddia_separacao_emissao) {
        this.qtddia_separacao_emissao = qtddia_separacao_emissao;
    }

    public Double getQtddia_emissao_hoje() {
        return qtddia_emissao_hoje;
    }

    public void setQtddia_emissao_hoje(Double qtddia_emissao_hoje) {
        this.qtddia_emissao_hoje = qtddia_emissao_hoje;
    }

    public Integer getCodigoTransportadora() {
        return codigoTransportadora;
    }

    public void setCodigoTransportadora(Integer codigoTransportadora) {
        this.codigoTransportadora = codigoTransportadora;
    }

    public Transportadora getCadTransportadora() {
        return CadTransportadora;
    }

    public void setCadTransportadora(Transportadora CadTransportadora) {
        this.CadTransportadora = CadTransportadora;
    }

    public Double getQuantidadediascomercial() {
        return quantidadediascomercial;
    }

    public void setQuantidadediascomercial(Double quantidadediascomercial) {
        this.quantidadediascomercial = quantidadediascomercial;
    }

    public Date getDataAgendamento() {
        return DataAgendamento;
    }

    public void setDataAgendamento(Date DataAgendamento) {
        this.DataAgendamento = DataAgendamento;
    }

    public String getDataAgendamentoS() {
        return DataAgendamentoS;
    }

    public void setDataAgendamentoS(String DataAgendamentoS) {
        this.DataAgendamentoS = DataAgendamentoS;
    }

    public String getDatabloqueioS() {
        return databloqueioS;
    }

    public void setDatabloqueioS(String databloqueioS) {
        this.databloqueioS = databloqueioS;
    }

    public String getDataliberacaoS() {
        return dataliberacaoS;
    }

    public void setDataliberacaoS(String dataliberacaoS) {
        this.dataliberacaoS = dataliberacaoS;
    }

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
    }

    public Double getQuantidadedias() {
        return quantidadedias;
    }

    public void setQuantidadedias(Double quantidadedias) {
        this.quantidadedias = quantidadedias;
    }

    public String getEnviaremail() {
        return enviaremail;
    }

    public void setEnviaremail(String enviaremail) {
        this.enviaremail = enviaremail;
    }

    public String getEmailpara() {
        return emailpara;
    }

    public void setEmailpara(String emailpara) {
        this.emailpara = emailpara;
    }

    public Integer getUsuarioliberacao() {
        return usuarioliberacao;
    }

    public void setUsuarioliberacao(Integer usuarioliberacao) {
        this.usuarioliberacao = usuarioliberacao;
    }

    public String getObservacaoliberacao() {
        return observacaoliberacao;
    }

    public void setObservacaoliberacao(String observacaoliberacao) {
        this.observacaoliberacao = observacaoliberacao;
    }

    public String getSituacaoLogistica() {
        return situacaoLogistica;
    }

    public void setSituacaoLogistica(String situacaoLogistica) {
        this.situacaoLogistica = situacaoLogistica;
    }

    public String getObservacaobloqueio() {
        return observacaobloqueio;
    }

    public void setObservacaobloqueio(String observacaobloqueio) {
        this.observacaobloqueio = observacaobloqueio;
    }

    public Date getDatabloqueio() {
        return databloqueio;
    }

    public void setDatabloqueio(Date databloqueio) {
        this.databloqueio = databloqueio;
    }

    public Date getDataliberacao() {
        return dataliberacao;
    }

    public void setDataliberacao(Date dataliberacao) {
        this.dataliberacao = dataliberacao;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    private Produto CadProduto;

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
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

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Date getEntrega() {
        return entrega;
    }

    public void setEntrega(Date entrega) {
        this.entrega = entrega;
    }

    public String getEmissaoS() {
        return emissaoS;
    }

    public void setEmissaoS(String emissaoS) {
        this.emissaoS = emissaoS;
    }

    public String getEntregaS() {
        return entregaS;
    }

    public void setEntregaS(String entregaS) {
        this.entregaS = entregaS;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
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

    public Produto getCadProduto() {
        return CadProduto;
    }

    public void setCadProduto(Produto CadProduto) {
        this.CadProduto = CadProduto;
    }

    public String getLiberarMinuta() {
        return liberarMinuta;
    }

    public void setLiberarMinuta(String liberarMinuta) {
        this.liberarMinuta = liberarMinuta;
    }

    public String getSituacaoPedido() {
        return situacaoPedido;
    }

    public void setSituacaoPedido(String situacaoPedido) {
        this.situacaoPedido = situacaoPedido;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public Integer getSucata_id() {
        return sucata_id;
    }

    public void setSucata_id(Integer sucata_id) {
        this.sucata_id = sucata_id;
    }

    public String getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(String tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public String getSituacaopre() {
        return situacaopre;
    }

    public void setSituacaopre(String situacaopre) {
        this.situacaopre = situacaopre;
    }

    public Integer getCodigominuta() {
        return codigominuta;
    }

    public void setCodigominuta(Integer codigominuta) {
        this.codigominuta = codigominuta;
    }

    public String getTipodpedido() {
        return tipodpedido;
    }

    public void setTipodpedido(String tipodpedido) {
        this.tipodpedido = tipodpedido;
    }

    public Double getPercentualRentabilidade() {
        return percentualRentabilidade;
    }

    public void setPercentualRentabilidade(Double percentualRentabilidade) {
        this.percentualRentabilidade = percentualRentabilidade;
    }

    public Double getPesoRentabilidade() {
        return pesoRentabilidade;
    }

    public void setPesoRentabilidade(Double pesoRentabilidade) {
        this.pesoRentabilidade = pesoRentabilidade;
    }

    public String getPedidobloqueado() {
        return pedidobloqueado;
    }

    public void setPedidobloqueado(String pedidobloqueado) {
        this.pedidobloqueado = pedidobloqueado;
    }

    
    
}
