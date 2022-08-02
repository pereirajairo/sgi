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
public class Sucata {

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer cliente = 0;
    private Integer pedido = 0;
    private Integer codigolancamento = 0;
    private Integer sequencia = 0;
    private Integer notasaida = 0;
    private Integer notaentrada = 0;
    private Integer ordemcompra = 0;
    private String produto;
    private String sucata;
    private String serie;
    private Double pesosucata = 0.0;
    private String pesosucataS;
    private Double pesoajustado;
    private Double pesomovimento;
    private Double quantidade;
    private Double quantidadedevolvida;
    private Double pesodevolvido;
    private String debitocredito;
    private String automoto;
    private String observacaomovimento;
    private String observacaoacerto;
    private String numerotitulo;
    private String codigotitulo;
    private String tipomovimento;
    private Double percentualrendimento = 0.0;
    private Double pesoordemcompra = 0.0;
    private Integer usuario = 0;
    private Date datageracao;
    private String datageracaoS;
    private Date datamovimento;
    private String datamovimentoS;
    private String horageracao;
    private String horamovimento;
    private String gerarordem;
    private String enviaremail;
    private String email;
    private String transacao;

    private Integer ano;
    private Integer mes;

    private Double pesopedido = 0.0;
    private Double pesorecebido = 0.0;
    private Double pesosaldo = 0.0;
    private Double pesofaturado = 0.0;
    private Integer filialsucata = 0;
    private String situacao;
    private Integer codigopeso = 0;
    private Integer codigominuta = 0;

    private Cliente CaCliente;

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }

    
    
    
    public String getPesosucataS() {
        return pesosucataS;
    }

    public void setPesosucataS(String pesosucataS) {
        this.pesosucataS = pesosucataS;
    }

    public Cliente getCaCliente() {
        return CaCliente;
    }

    public void setCaCliente(Cliente CaCliente) {
        this.CaCliente = CaCliente;
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

    public Integer getCodigolancamento() {
        return codigolancamento;
    }

    public void setCodigolancamento(Integer codigolancamento) {
        this.codigolancamento = codigolancamento;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public Integer getNotasaida() {
        return notasaida;
    }

    public void setNotasaida(Integer notasaida) {
        this.notasaida = notasaida;
    }

    public Integer getNotaentrada() {
        return notaentrada;
    }

    public void setNotaentrada(Integer notaentrada) {
        this.notaentrada = notaentrada;
    }

    public Integer getOrdemcompra() {
        return ordemcompra;
    }

    public void setOrdemcompra(Integer ordemcompra) {
        this.ordemcompra = ordemcompra;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getSucata() {
        return sucata;
    }

    public void setSucata(String sucata) {
        this.sucata = sucata;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Double getPesosucata() {
        return pesosucata;
    }

    public void setPesosucata(Double pesosucata) {
        this.pesosucata = pesosucata;
    }

    public Double getPesoajustado() {
        return pesoajustado;
    }

    public void setPesoajustado(Double pesoajustado) {
        this.pesoajustado = pesoajustado;
    }

    public Double getPesomovimento() {
        return pesomovimento;
    }

    public void setPesomovimento(Double pesomovimento) {
        this.pesomovimento = pesomovimento;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getQuantidadedevolvida() {
        return quantidadedevolvida;
    }

    public void setQuantidadedevolvida(Double quantidadedevolvida) {
        this.quantidadedevolvida = quantidadedevolvida;
    }

    public Double getPesodevolvido() {
        return pesodevolvido;
    }

    public void setPesodevolvido(Double pesodevolvido) {
        this.pesodevolvido = pesodevolvido;
    }

    public String getDebitocredito() {
        return debitocredito;
    }

    public void setDebitocredito(String debitocredito) {
        this.debitocredito = debitocredito;
    }

    public String getAutomoto() {
        return automoto;
    }

    public void setAutomoto(String automoto) {
        this.automoto = automoto;
    }

    public String getObservacaomovimento() {
        return observacaomovimento;
    }

    public void setObservacaomovimento(String observacaomovimento) {
        this.observacaomovimento = observacaomovimento;
    }

    public String getObservacaoacerto() {
        return observacaoacerto;
    }

    public void setObservacaoacerto(String observacaoacerto) {
        this.observacaoacerto = observacaoacerto;
    }

    public String getNumerotitulo() {
        return numerotitulo;
    }

    public void setNumerotitulo(String numerotitulo) {
        this.numerotitulo = numerotitulo;
    }

    public String getCodigotitulo() {
        return codigotitulo;
    }

    public void setCodigotitulo(String codigotitulo) {
        this.codigotitulo = codigotitulo;
    }

    public String getTipomovimento() {
        return tipomovimento;
    }

    public void setTipomovimento(String tipomovimento) {
        this.tipomovimento = tipomovimento;
    }

    public Double getPercentualrendimento() {
        return percentualrendimento;
    }

    public void setPercentualrendimento(Double percentualrendimento) {
        this.percentualrendimento = percentualrendimento;
    }

    public Double getPesoordemcompra() {
        return pesoordemcompra;
    }

    public void setPesoordemcompra(Double pesoordemcompra) {
        this.pesoordemcompra = pesoordemcompra;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public Date getDatageracao() {
        return datageracao;
    }

    public void setDatageracao(Date datageracao) {
        this.datageracao = datageracao;
    }

    public String getDatageracaoS() {
        return datageracaoS;
    }

    public void setDatageracaoS(String datageracaoS) {
        this.datageracaoS = datageracaoS;
    }

    public Date getDatamovimento() {
        return datamovimento;
    }

    public void setDatamovimento(Date datamovimento) {
        this.datamovimento = datamovimento;
    }

    public String getDatamovimentoS() {
        return datamovimentoS;
    }

    public void setDatamovimentoS(String datamovimentoS) {
        this.datamovimentoS = datamovimentoS;
    }

    public String getHorageracao() {
        return horageracao;
    }

    public void setHorageracao(String horageracao) {
        this.horageracao = horageracao;
    }

    public String getHoramovimento() {
        return horamovimento;
    }

    public void setHoramovimento(String horamovimento) {
        this.horamovimento = horamovimento;
    }

    public String getGerarordem() {
        return gerarordem;
    }

    public void setGerarordem(String gerarordem) {
        this.gerarordem = gerarordem;
    }

    public String getEnviaremail() {
        return enviaremail;
    }

    public void setEnviaremail(String enviaremail) {
        this.enviaremail = enviaremail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Double getPesopedido() {
        return pesopedido;
    }

    public void setPesopedido(Double pesopedido) {
        this.pesopedido = pesopedido;
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

    public Double getPesofaturado() {
        return pesofaturado;
    }

    public void setPesofaturado(Double pesofaturado) {
        this.pesofaturado = pesofaturado;
    }

    public Integer getFilialsucata() {
        return filialsucata;
    }

    public void setFilialsucata(Integer filialsucata) {
        this.filialsucata = filialsucata;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Integer getCodigopeso() {
        return codigopeso;
    }

    public void setCodigopeso(Integer codigopeso) {
        this.codigopeso = codigopeso;
    }

    public Integer getCodigominuta() {
        return codigominuta;
    }

    public void setCodigominuta(Integer codigominuta) {
        this.codigominuta = codigominuta;
    }

}
