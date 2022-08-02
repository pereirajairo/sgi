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
public class Minuta {

    private Integer usu_codemp = 0;
    private Integer usu_codfil = 0;
    private Integer usu_codlan = 0;
    private Integer usu_codtra = 0;
    private Integer usu_codemb = 0;
    private Integer usu_numnfv = 0;
    
    private Integer usu_numpfa = 0;
    private Integer usu_numane = 0;
    
    private String transacao = "";
    
    private String usu_sitmin;
    private Date usu_datemi;
    private Date usu_datlib;
    private Date usu_datsai;
    private Integer usu_usuger = 0;
    private Integer usu_usulib = 0;
    private String usu_obsmin;
    private String usu_libmot;
    private Integer usu_codmtr = 0;
    private String usu_plavei;

    private String usu_nommin;

    private Double usu_pesfat = 0.0;
    private Double usu_qtdfat = 0.0;
    private Integer usu_qtdped = 0;
    private Integer usu_qtdvol = 0;
    private Transportadora CadTransportadora;

    // dados de pesagem da balança
    private Double usu_pesbal = 0.0;
    private Double usu_pesbalsal = 0.0;
    private Integer usu_ticbal = 0;
    private String usu_obspes;
    private Integer usu_libmindiv;
    private String usu_orimin;
    private String usu_sitsuc;
    
    private MinutaPedido minutaPedido;

    public Integer getUsu_codemp() {
        return usu_codemp;
    }

    public void setUsu_codemp(Integer usu_codemp) {
        this.usu_codemp = usu_codemp;
    }

    public Integer getUsu_codfil() {
        return usu_codfil;
    }

    public void setUsu_codfil(Integer usu_codfil) {
        this.usu_codfil = usu_codfil;
    }

    public Integer getUsu_codlan() {
        return usu_codlan;
    }

    public void setUsu_codlan(Integer usu_codlan) {
        this.usu_codlan = usu_codlan;
    }

    public Integer getUsu_codtra() {
        return usu_codtra;
    }

    public void setUsu_codtra(Integer usu_codtra) {
        this.usu_codtra = usu_codtra;
    }

    public String getUsu_sitmin() {
        return usu_sitmin;
    }

    public void setUsu_sitmin(String usu_sitmin) {
        this.usu_sitmin = usu_sitmin;
    }

    public Date getUsu_datemi() {
        return usu_datemi;
    }

    public void setUsu_datemi(Date usu_datemi) {
        this.usu_datemi = usu_datemi;
    }

    public Date getUsu_datlib() {
        return usu_datlib;
    }

    public void setUsu_datlib(Date usu_datlib) {
        this.usu_datlib = usu_datlib;
    }

    public Date getUsu_datsai() {
        return usu_datsai;
    }

    public void setUsu_datsai(Date usu_datsai) {
        this.usu_datsai = usu_datsai;
    }

    public Integer getUsu_usuger() {
        return usu_usuger;
    }

    public void setUsu_usuger(Integer usu_usuger) {
        this.usu_usuger = usu_usuger;
    }

    public Integer getUsu_usulib() {
        return usu_usulib;
    }

    public void setUsu_usulib(Integer usu_usulib) {
        this.usu_usulib = usu_usulib;
    }

    public String getUsu_obsmin() {
        return usu_obsmin;
    }

    public void setUsu_obsmin(String usu_obsmin) {
        this.usu_obsmin = usu_obsmin;
    }

    public String getUsu_libmot() {
        return usu_libmot;
    }

    public void setUsu_libmot(String usu_libmot) {
        this.usu_libmot = usu_libmot;
    }

    public Integer getUsu_codmtr() {
        return usu_codmtr;
    }

    public void setUsu_codmtr(Integer usu_codmtr) {
        this.usu_codmtr = usu_codmtr;
    }

    public String getUsu_plavei() {
        return usu_plavei;
    }

    public void setUsu_plavei(String usu_plavei) {
        this.usu_plavei = usu_plavei;
    }

    public Transportadora getCadTransportadora() {
        return CadTransportadora;
    }

    public void setCadTransportadora(Transportadora CadTransportadora) {
        this.CadTransportadora = CadTransportadora;
    }

    public Double getUsu_pesfat() {
        return usu_pesfat;
    }

    public void setUsu_pesfat(Double usu_pesfat) {
        this.usu_pesfat = usu_pesfat;
    }

    public Double getUsu_qtdfat() {
        return usu_qtdfat;
    }

    public void setUsu_qtdfat(Double usu_qtdfat) {
        this.usu_qtdfat = usu_qtdfat;
    }

    public Integer getUsu_qtdped() {
        return usu_qtdped;
    }

    public void setUsu_qtdped(Integer usu_qtdped) {
        this.usu_qtdped = usu_qtdped;
    }

    public Integer getUsu_qtdvol() {
        return usu_qtdvol;
    }

    public void setUsu_qtdvol(Integer usu_qtdvol) {
        this.usu_qtdvol = usu_qtdvol;
    }

   
    
    

    public Double getUsu_pesbal() {
        return usu_pesbal;
    }

    public void setUsu_pesbal(Double usu_pesbal) {
        this.usu_pesbal = usu_pesbal;
    }

    public Double getUsu_pesbalsal() {
        return usu_pesbalsal;
    }

    public void setUsu_pesbalsal(Double usu_pesbalsal) {
        this.usu_pesbalsal = usu_pesbalsal;
    }

    public Integer getUsu_ticbal() {
        return usu_ticbal;
    }

    public void setUsu_ticbal(Integer usu_ticbal) {
        this.usu_ticbal = usu_ticbal;
    }

    public String getUsu_obspes() {
        return usu_obspes;
    }

    public void setUsu_obspes(String usu_obspes) {
        this.usu_obspes = usu_obspes;
    }

    public Integer getUsu_libmindiv() {
        return usu_libmindiv;
    }

    public void setUsu_libmindiv(Integer usu_libmindiv) {
        this.usu_libmindiv = usu_libmindiv;
    }

    public String getUsu_orimin() {
        return usu_orimin;
    }

    public void setUsu_orimin(String usu_orimin) {
        this.usu_orimin = usu_orimin;
    }

    public String getUsu_sitsuc() {
        return usu_sitsuc;
    }

    public void setUsu_sitsuc(String usu_sitsuc) {
        this.usu_sitsuc = usu_sitsuc;
    }

    public String getUsu_nommin() {
        return usu_nommin;
    }

    public void setUsu_nommin(String usu_nommin) {
        this.usu_nommin = usu_nommin;
    }

    public Integer getUsu_codemb() {
        return usu_codemb;
    }

    public void setUsu_codemb(Integer usu_codemb) {
        this.usu_codemb = usu_codemb;
    }

    public MinutaPedido getMinutaPedido() {
        return minutaPedido;
    }

    public void setMinutaPedido(MinutaPedido minutaPedido) {
        this.minutaPedido = minutaPedido;
    }

    public Integer getUsu_numpfa() {
        return usu_numpfa;
    }

    public void setUsu_numpfa(Integer usu_numpfa) {
        this.usu_numpfa = usu_numpfa;
    }

    public Integer getUsu_numane() {
        return usu_numane;
    }

    public void setUsu_numane(Integer usu_numane) {
        this.usu_numane = usu_numane;
    }

    public Integer getUsu_numnfv() {
        return usu_numnfv;
    }

    public void setUsu_numnfv(Integer usu_numnfv) {
        this.usu_numnfv = usu_numnfv;
    }

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }
    
    

}
