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
public class PedidoReport {

    private Integer usu_codemp;
    private Integer usu_codfil;
    private Integer usu_codcli;
    private Integer usu_numped;
    private Integer usu_codlan;
    private Date usu_datenv;
    private Date usu_datemi;
    private String usu_sitenv;
    private String usu_horenv;
    private String usu_motenv;
    private String usu_linped;
    private Double usu_qtdped = 0.0;
    private Double usu_pesped = 0.0;
    private Double usu_numdia = 0.0;
    private String usu_logenv;
    private Integer usu_seqmes = 0;
    private String usu_nomset;
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

    public Integer getUsu_codlan() {
        return usu_codlan;
    }

    public void setUsu_codlan(Integer usu_codlan) {
        this.usu_codlan = usu_codlan;
    }

    public Date getUsu_datenv() {
        return usu_datenv;
    }

    public void setUsu_datenv(Date usu_datenv) {
        this.usu_datenv = usu_datenv;
    }

    public String getUsu_sitenv() {
        return usu_sitenv;
    }

    public void setUsu_sitenv(String usu_sitenv) {
        this.usu_sitenv = usu_sitenv;
    }

    public String getUsu_horenv() {
        return usu_horenv;
    }

    public void setUsu_horenv(String usu_horenv) {
        this.usu_horenv = usu_horenv;
    }

    public String getUsu_motenv() {
        return usu_motenv;
    }

    public void setUsu_motenv(String usu_motenv) {
        this.usu_motenv = usu_motenv;
    }

    public String getUsu_linped() {
        return usu_linped;
    }

    public void setUsu_linped(String usu_linped) {
        this.usu_linped = usu_linped;
    }

   

    public String getUsu_logenv() {
        return usu_logenv;
    }

    public void setUsu_logenv(String usu_logenv) {
        this.usu_logenv = usu_logenv;
    }

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
    }

    public Double getUsu_qtdped() {
        return usu_qtdped;
    }

    public void setUsu_qtdped(Double usu_qtdped) {
        this.usu_qtdped = usu_qtdped;
    }

    public Double getUsu_pesped() {
        return usu_pesped;
    }

    public void setUsu_pesped(Double usu_pesped) {
        this.usu_pesped = usu_pesped;
    }

    public Integer getUsu_seqmes() {
        return usu_seqmes;
    }

    public void setUsu_seqmes(Integer usu_seqmes) {
        this.usu_seqmes = usu_seqmes;
    }

    public Date getUsu_datemi() {
        return usu_datemi;
    }

    public void setUsu_datemi(Date usu_datemi) {
        this.usu_datemi = usu_datemi;
    }

    public Double getUsu_numdia() {
        return usu_numdia;
    }

    public void setUsu_numdia(Double usu_numdia) {
        this.usu_numdia = usu_numdia;
    }

    public String getUsu_nomset() {
        return usu_nomset;
    }

    public void setUsu_nomset(String usu_nomset) {
        this.usu_nomset = usu_nomset;
    }
    
    
    
    

}
