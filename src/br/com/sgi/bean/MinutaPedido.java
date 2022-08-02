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
public class MinutaPedido {

    private Integer usu_codemp = 0;
    private Integer usu_codfil = 0;
    private Integer usu_codlan = 0;
    private Integer usu_codtra = 0;
    private Integer usu_codcli = 0;
    private Integer usu_numped = 0;
    private Integer usu_codpes = 0;
    private Integer usu_lansuc = 0;
    private Integer usu_numnfv = 0;
    private Integer usu_seqite = 0;
    private Integer usu_numpfa = 0;
    private Integer usu_numana = 0;
    private Integer usu_pessalbal = 0;

    private String usu_codsnf;
    private String usu_tnspro;
    private String usu_codtpr;
    private String usu_codori;
    private Double usu_pesped = 0.0;
    private Double usu_pesnfv = 0.0;
    private Double usu_pessuc = 0.0;
    private Double usu_pessld = 0.0;
    private Double usu_pesbal = 0.0;
    private Double usu_pesrec = 0.0;
    private Double usu_qtdped = 0.0;
    private Double usu_qtdvol = 0.0;
    private Double usu_qtdfat = 0.0;
    private Double usu_salsuc = 0.0;
    
    private String tipopedido;
        private String transacao;
    private String tipodocumento;
  

    private String usu_sitmin;
    private String usu_obsmin;
    private Date usu_datemi;
    private Date usu_datlib;

    private String emissaoS;
    private String usu_ticbal;
    private String usu_obspes;

    private Minuta CadMinuta;
    private Cliente CadCliente;

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

    public Integer getUsu_codcli() {
        return usu_codcli;
    }

    public void setUsu_codcli(Integer usu_codcli) {
        this.usu_codcli = usu_codcli;
    }

    public Integer getUsu_numped() {
        return usu_numped;
    }

    public void setUsu_numped(Integer usu_numped) {
        this.usu_numped = usu_numped;
    }

    public Integer getUsu_codpes() {
        return usu_codpes;
    }

    public void setUsu_codpes(Integer usu_codpes) {
        this.usu_codpes = usu_codpes;
    }

    public Integer getUsu_lansuc() {
        return usu_lansuc;
    }

    public void setUsu_lansuc(Integer usu_lansuc) {
        this.usu_lansuc = usu_lansuc;
    }

    public Integer getUsu_numnfv() {
        return usu_numnfv;
    }

    public void setUsu_numnfv(Integer usu_numnfv) {
        this.usu_numnfv = usu_numnfv;
    }

    public Integer getUsu_seqite() {
        return usu_seqite;
    }

    public void setUsu_seqite(Integer usu_seqite) {
        this.usu_seqite = usu_seqite;
    }

    public String getUsu_codsnf() {
        return usu_codsnf;
    }

    public void setUsu_codsnf(String usu_codsnf) {
        this.usu_codsnf = usu_codsnf;
    }

    public String getUsu_tnspro() {
        return usu_tnspro;
    }

    public void setUsu_tnspro(String usu_tnspro) {
        this.usu_tnspro = usu_tnspro;
    }

    public String getUsu_codtpr() {
        return usu_codtpr;
    }

    public void setUsu_codtpr(String usu_codtpr) {
        this.usu_codtpr = usu_codtpr;
    }

    public String getUsu_codori() {
        return usu_codori;
    }

    public void setUsu_codori(String usu_codori) {
        this.usu_codori = usu_codori;
    }

    public Double getUsu_pesped() {
        return usu_pesped;
    }

    public void setUsu_pesped(Double usu_pesped) {
        this.usu_pesped = usu_pesped;
    }

    public Double getUsu_pesnfv() {
        return usu_pesnfv;
    }

    public void setUsu_pesnfv(Double usu_pesnfv) {
        this.usu_pesnfv = usu_pesnfv;
    }

    public Double getUsu_pessuc() {
        return usu_pessuc;
    }

    public void setUsu_pessuc(Double usu_pessuc) {
        this.usu_pessuc = usu_pessuc;
    }

    public Double getUsu_pessld() {
        return usu_pessld;
    }

    public void setUsu_pessld(Double usu_pessld) {
        this.usu_pessld = usu_pessld;
    }

    public Double getUsu_pesbal() {
        return usu_pesbal;
    }

    public void setUsu_pesbal(Double usu_pesbal) {
        this.usu_pesbal = usu_pesbal;
    }

    public Double getUsu_pesrec() {
        return usu_pesrec;
    }

    public void setUsu_pesrec(Double usu_pesrec) {
        this.usu_pesrec = usu_pesrec;
    }

    public Double getUsu_qtdped() {
        return usu_qtdped;
    }

    public void setUsu_qtdped(Double usu_qtdped) {
        this.usu_qtdped = usu_qtdped;
    }

    public Double getUsu_qtdvol() {
        return usu_qtdvol;
    }

    public void setUsu_qtdvol(Double usu_qtdvol) {
        this.usu_qtdvol = usu_qtdvol;
    }

    public Double getUsu_qtdfat() {
        return usu_qtdfat;
    }

    public void setUsu_qtdfat(Double usu_qtdfat) {
        this.usu_qtdfat = usu_qtdfat;
    }

    public String getUsu_sitmin() {
        return usu_sitmin;
    }

    public void setUsu_sitmin(String usu_sitmin) {
        this.usu_sitmin = usu_sitmin;
    }

    public String getUsu_obsmin() {
        return usu_obsmin;
    }

    public void setUsu_obsmin(String usu_obsmin) {
        this.usu_obsmin = usu_obsmin;
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

    public Minuta getCadMinuta() {
        return CadMinuta;
    }

    public void setCadMinuta(Minuta CadMinuta) {
        this.CadMinuta = CadMinuta;
    }

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
    }

    public String getEmissaoS() {
        return emissaoS;
    }

    public void setEmissaoS(String emissaoS) {
        this.emissaoS = emissaoS;
    }

    public Integer getUsu_numpfa() {
        return usu_numpfa;
    }

    public void setUsu_numpfa(Integer usu_numpfa) {
        this.usu_numpfa = usu_numpfa;
    }

    public Integer getUsu_numana() {
        return usu_numana;
    }

    public void setUsu_numana(Integer usu_numana) {
        this.usu_numana = usu_numana;
    }

    public Integer getUsu_pessalbal() {
        return usu_pessalbal;
    }

    public void setUsu_pessalbal(Integer usu_pessalbal) {
        this.usu_pessalbal = usu_pessalbal;
    }

    public Double getUsu_salsuc() {
        return usu_salsuc;
    }

    public void setUsu_salsuc(Double usu_salsuc) {
        this.usu_salsuc = usu_salsuc;
    }

    public String getUsu_ticbal() {
        return usu_ticbal;
    }

    public void setUsu_ticbal(String usu_ticbal) {
        this.usu_ticbal = usu_ticbal;
    }

    public String getUsu_obspes() {
        return usu_obspes;
    }

    public void setUsu_obspes(String usu_obspes) {
        this.usu_obspes = usu_obspes;
    }

    public String getTipopedido() {
        return tipopedido;
    }

    public void setTipopedido(String tipopedido) {
        this.tipopedido = tipopedido;
    }

  
    
    

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }

    public String getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(String tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

  
    
    

}
