/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Date;

/**
 *
 * @author jairosilva
 */
public class SucataEco {

    private Integer empresa = 0;
    private Integer filial = 0;
    private String serie = "";
    private Integer nota = 0;
    private Integer cliente = 0;
    private String nomeCliente = "";
    private Double pesoNota;
    private Double pesoSucata;
    private String pesado;
    private Date dataSaida;
    private Integer horaSaida = 0;
    private Date dataLancamento;
    private Integer horaLancamento = 0;
    private Integer usuarioLancamento = 0;
    private Integer numeroOC= 0;
    private String codigoSucata;
    private Integer notaEntrada;
    private Integer pedidoSucata;
    private Double pesoOC;

    public Integer getPedidoSucata() {
        return pedidoSucata;
    }

    public void setPedidoSucata(Integer pedidoSucata) {
        this.pedidoSucata = pedidoSucata;
    }

    public Double getPesoOC() {
        return pesoOC;
    }

    public void setPesoOC(Double pesoOC) {
        this.pesoOC = pesoOC;
    }

    public String getCodigoSucata() {
        return codigoSucata;
    }

    public void setCodigoSucata(String codigoSucata) {
        this.codigoSucata = codigoSucata;
    }

    public Integer getNotaEntrada() {
        return notaEntrada;
    }

    public void setNotaEntrada(Integer notaEntrada) {
        this.notaEntrada = notaEntrada;
    }

    public Integer getNumeroOC() {
        return numeroOC;
    }

    public void setNumeroOC(Integer numeroOC) {
        this.numeroOC = numeroOC;
    }

    
     public Double getPesoSucata() {
        return pesoSucata;
    }

    public void setPesoSucata(Double pesoSucata) {
        this.pesoSucata = pesoSucata;
    }
    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Integer getHoraLancamento() {
        return horaLancamento;
    }

    public void setHoraLancamento(Integer horaLancamento) {
        this.horaLancamento = horaLancamento;
    }

    public Integer getUsuarioLancamento() {
        return usuarioLancamento;
    }

    public void setUsuarioLancamento(Integer usuarioLancamento) {
        this.usuarioLancamento = usuarioLancamento;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Integer getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(Integer horaSaida) {
        this.horaSaida = horaSaida;
    }

    public String getPesado() {
        return pesado;
    }

    public void setPesado(String pesado) {
        this.pesado = pesado;
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

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public Integer getCliente() {
        return cliente;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public Double getPesoNota() {
        return pesoNota;
    }

    public void setPesoNota(Double pesoNota) {
        this.pesoNota = pesoNota;
    }

}
