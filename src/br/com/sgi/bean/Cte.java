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
public class Cte {

    private Integer usu_codlan = 0;
    private Integer usu_codemp = 0;
    private Integer usu_codfil = 0;
    private Integer usu_codtra = 0;
    private Integer usu_numocp = 0;
    private Date usu_datlan;
    private String usu_numcte;
    private String usu_chacte;
    private double usu_valcte = 0;
    private double usu_valnfv = 0;
    private double usu_pesnfv = 0;
    private String usu_estdes;
    private Date usu_datval;
    private double usu_perfrefat = 0;
    private double usu_valfrepes = 0;

    private Integer usu_numnfv = 0;
    private Integer usu_codcli = 0;
    private String usu_codsnf;
    private String usu_tnspro;
    private String usu_tipfre; // tipo de frete Venda, Garantia e Orçamento
    private String usu_linpro; // linha de Auto ou Moto
    private double usu_pesfat = 0;
    private double usu_pesfre = 0;
    private String usu_gerocp;
    private double usu_qtdpro = 0;

    private Integer usu_codfor = 0;
    private String usu_codccu;
    private String usu_ctafin;
    private String usu_sitocp;
    private String usu_cplocp;

    private String usu_codpro;
    private String usu_codser;
    private Integer totalcte=0;
    

    private Transportadora CadTransportadora;

    public Integer getTotalcte() {
        return totalcte;
    }

    public void setTotalcte(Integer totalcte) {
        this.totalcte = totalcte;
    }

    
    
    
    public Transportadora getCadTransportadora() {
        return CadTransportadora;
    }

    public void setCadTransportadora(Transportadora CadTransportadora) {
        this.CadTransportadora = CadTransportadora;
    }

    public String getUsu_codpro() {
        return usu_codpro;
    }

    public void setUsu_codpro(String usu_codpro) {
        this.usu_codpro = usu_codpro;
    }

    public String getUsu_codser() {
        return usu_codser;
    }

    public void setUsu_codser(String usu_codser) {
        this.usu_codser = usu_codser;
    }

    private NotaFiscal CadNotaFiscal;

    public NotaFiscal getCadNotaFiscal() {
        return CadNotaFiscal;
    }

    public void setCadNotaFiscal(NotaFiscal CadNotaFiscal) {
        this.CadNotaFiscal = CadNotaFiscal;
    }

    public Integer getUsu_codfor() {
        return usu_codfor;
    }

    public void setUsu_codfor(Integer usu_codfor) {
        this.usu_codfor = usu_codfor;
    }

    public String getUsu_codccu() {
        return usu_codccu;
    }

    public void setUsu_codccu(String usu_codccu) {
        this.usu_codccu = usu_codccu;
    }

    public String getUsu_ctafin() {
        return usu_ctafin;
    }

    public void setUsu_ctafin(String usu_ctafin) {
        this.usu_ctafin = usu_ctafin;
    }

    public String getUsu_sitocp() {
        return usu_sitocp;
    }

    public void setUsu_sitocp(String usu_sitocp) {
        this.usu_sitocp = usu_sitocp;
    }

    public String getUsu_cplocp() {
        return usu_cplocp;
    }

    public void setUsu_cplocp(String usu_cplocp) {
        this.usu_cplocp = usu_cplocp;
    }

    public Integer getUsu_numnfv() {
        return usu_numnfv;
    }

    public void setUsu_numnfv(Integer usu_numnfv) {
        this.usu_numnfv = usu_numnfv;
    }

    public Integer getUsu_codcli() {
        return usu_codcli;
    }

    public void setUsu_codcli(Integer usu_codcli) {
        this.usu_codcli = usu_codcli;
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

    public String getUsu_tipfre() {
        return usu_tipfre;
    }

    public void setUsu_tipfre(String usu_tipfre) {
        this.usu_tipfre = usu_tipfre;
    }

    public String getUsu_linpro() {
        return usu_linpro;
    }

    public void setUsu_linpro(String usu_linpro) {
        this.usu_linpro = usu_linpro;
    }

    public double getUsu_pesfat() {
        return usu_pesfat;
    }

    public void setUsu_pesfat(double usu_pesfat) {
        this.usu_pesfat = usu_pesfat;
    }

    public double getUsu_pesfre() {
        return usu_pesfre;
    }

    public void setUsu_pesfre(double usu_pesfre) {
        this.usu_pesfre = usu_pesfre;
    }

    public String getUsu_gerocp() {
        return usu_gerocp;
    }

    public void setUsu_gerocp(String usu_gerocp) {
        this.usu_gerocp = usu_gerocp;
    }

    public double getUsu_qtdpro() {
        return usu_qtdpro;
    }

    public void setUsu_qtdpro(double usu_qtdpro) {
        this.usu_qtdpro = usu_qtdpro;
    }

    public Integer getUsu_codlan() {
        return usu_codlan;
    }

    public void setUsu_codlan(Integer usu_codlan) {
        this.usu_codlan = usu_codlan;
    }

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

    public Integer getUsu_codtra() {
        return usu_codtra;
    }

    public void setUsu_codtra(Integer usu_codtra) {
        this.usu_codtra = usu_codtra;
    }

    public Integer getUsu_numocp() {
        return usu_numocp;
    }

    public void setUsu_numocp(Integer usu_numocp) {
        this.usu_numocp = usu_numocp;
    }

    public Date getUsu_datlan() {
        return usu_datlan;
    }

    public void setUsu_datlan(Date usu_datlan) {
        this.usu_datlan = usu_datlan;
    }

    public String getUsu_numcte() {
        return usu_numcte;
    }

    public void setUsu_numcte(String usu_numcte) {
        this.usu_numcte = usu_numcte;
    }

    public String getUsu_chacte() {
        return usu_chacte;
    }

    public void setUsu_chacte(String usu_chacte) {
        this.usu_chacte = usu_chacte;
    }

    public double getUsu_valcte() {
        return usu_valcte;
    }

    public void setUsu_valcte(double usu_valcte) {
        this.usu_valcte = usu_valcte;
    }

    public double getUsu_valnfv() {
        return usu_valnfv;
    }

    public void setUsu_valnfv(double usu_valnfv) {
        this.usu_valnfv = usu_valnfv;
    }

    public double getUsu_pesnfv() {
        return usu_pesnfv;
    }

    public void setUsu_pesnfv(double usu_pesnfv) {
        this.usu_pesnfv = usu_pesnfv;
    }

    public String getUsu_estdes() {
        return usu_estdes;
    }

    public void setUsu_estdes(String usu_estdes) {
        this.usu_estdes = usu_estdes;
    }

    public Date getUsu_datval() {
        return usu_datval;
    }

    public void setUsu_datval(Date usu_datval) {
        this.usu_datval = usu_datval;
    }

    public double getUsu_perfrefat() {
        return usu_perfrefat;
    }

    public void setUsu_perfrefat(double usu_perfrefat) {
        this.usu_perfrefat = usu_perfrefat;
    }

    public double getUsu_valfrepes() {
        return usu_valfrepes;
    }

    public void setUsu_valfrepes(double usu_valfrepes) {
        this.usu_valfrepes = usu_valfrepes;
    }

}
