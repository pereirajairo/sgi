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
public class SucataPDV {

    private Integer empresa = 0;
    private Integer filial = 0;
    private String serie = "";
    private Integer nota = 0;
    private Integer cliente = 0;
    private String nomeCliente = "";
    private Double pesoNota;
    private Double pesoSucata = 0.0;
    private String pesado;
    private Date dataSaida;
    private Integer horaSaida = 0;
    private Date dataLancamento;
    private Integer horaLancamento = 0;
    private Integer usuarioLancamento = 0;
    private String indicativoSucata = "";
    private Double precoSucata = 0.0;
    private Double precoTotalSucata = 0.0;
    private Filial cadFilial;

    public Filial getCadFilial() {
        return cadFilial;
    }

    public void setCadFilial(Filial cadFilial) {
        this.cadFilial = cadFilial;
    }

    public String getIndicativoSucata() {
        return indicativoSucata;
    }

    public void setIndicativoSucata(String indicativoSucata) {
        this.indicativoSucata = indicativoSucata;
    }

    public Double getPrecoSucata() {
        return precoSucata;
    }

    public void setPrecoSucata(Double precoSucata) {
        this.precoSucata = precoSucata;
    }

    public Double getPrecoTotalSucata() {
        return precoTotalSucata;
    }

    public void setPrecoTotalSucata(Double precoTotalSucata) {
        this.precoTotalSucata = precoTotalSucata;
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
