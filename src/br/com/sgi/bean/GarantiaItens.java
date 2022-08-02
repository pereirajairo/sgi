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
public class GarantiaItens {

    private Date usu_datlim;
    private Date usu_datemi;
    private Date usu_datrom;
    private Date usu_datcer;
    private Integer usu_seqite;
    private Integer usu_certif;
    private Integer usu_numocr;
    private Integer usu_codfor;
    private Integer usu_motant;
    private Integer usu_empdes;
    private Integer usu_numnfv;
    private Integer usu_empemi;
    private Integer usu_codcli;
    private Integer usu_codfil;
    private Double usu_preuni = 0.0;
    private Integer usu_numnfc;
    private Integer usu_codemp;
    private Integer usu_numrom;
    private Integer usu_codusu;
    private Integer usu_codmot;
    private Integer usu_numoat;
    private String usu_tmpfat;
    private String usu_tmptot;
    private String usu_sitgar;
    private String usu_obsipc;
    private String usu_codsnf;
    private String usu_itediv;
    private String usu_codder;
    private String usu_forgar;
    private String usu_insaut;
    private String usu_excarq;
    private String usu_numsep;
    private String usu_itepro;
    private String usu_tipnfs;
    private String usu_desprb;
    private String usu_dessol;
    private String usu_tmpgar;
    private String usu_codpro;
    private String usu_tipnfc;

    private Garantia CadGarantia;
    private Produto CadProduto;
    private Cliente CadCliente;

    public Date getUsu_datlim() {
        return usu_datlim;
    }

    public void setUsu_datlim(Date usu_datlim) {
        this.usu_datlim = usu_datlim;
    }

    public Date getUsu_datemi() {
        return usu_datemi;
    }

    public void setUsu_datemi(Date usu_datemi) {
        this.usu_datemi = usu_datemi;
    }

    public Date getUsu_datrom() {
        return usu_datrom;
    }

    public void setUsu_datrom(Date usu_datrom) {
        this.usu_datrom = usu_datrom;
    }

    public Date getUsu_datcer() {
        return usu_datcer;
    }

    public void setUsu_datcer(Date usu_datcer) {
        this.usu_datcer = usu_datcer;
    }

    public Integer getUsu_seqite() {
        return usu_seqite;
    }

    public void setUsu_seqite(Integer usu_seqite) {
        this.usu_seqite = usu_seqite;
    }

    public Integer getUsu_certif() {
        return usu_certif;
    }

    public void setUsu_certif(Integer usu_certif) {
        this.usu_certif = usu_certif;
    }

    public Integer getUsu_numocr() {
        return usu_numocr;
    }

    public void setUsu_numocr(Integer usu_numocr) {
        this.usu_numocr = usu_numocr;
    }

    public Integer getUsu_codfor() {
        return usu_codfor;
    }

    public void setUsu_codfor(Integer usu_codfor) {
        this.usu_codfor = usu_codfor;
    }

    public Integer getUsu_motant() {
        return usu_motant;
    }

    public void setUsu_motant(Integer usu_motant) {
        this.usu_motant = usu_motant;
    }

    public Integer getUsu_empdes() {
        return usu_empdes;
    }

    public void setUsu_empdes(Integer usu_empdes) {
        this.usu_empdes = usu_empdes;
    }

    public Integer getUsu_numnfv() {
        return usu_numnfv;
    }

    public void setUsu_numnfv(Integer usu_numnfv) {
        this.usu_numnfv = usu_numnfv;
    }

    public Integer getUsu_empemi() {
        return usu_empemi;
    }

    public void setUsu_empemi(Integer usu_empemi) {
        this.usu_empemi = usu_empemi;
    }

    public Integer getUsu_codcli() {
        return usu_codcli;
    }

    public void setUsu_codcli(Integer usu_codcli) {
        this.usu_codcli = usu_codcli;
    }

    public Integer getUsu_codfil() {
        return usu_codfil;
    }

    public void setUsu_codfil(Integer usu_codfil) {
        this.usu_codfil = usu_codfil;
    }

    public Double getUsu_preuni() {
        return usu_preuni;
    }

    public void setUsu_preuni(Double usu_preuni) {
        this.usu_preuni = usu_preuni;
    }

  
    
    

    public Integer getUsu_numnfc() {
        return usu_numnfc;
    }

    public void setUsu_numnfc(Integer usu_numnfc) {
        this.usu_numnfc = usu_numnfc;
    }

    public Integer getUsu_codemp() {
        return usu_codemp;
    }

    public void setUsu_codemp(Integer usu_codemp) {
        this.usu_codemp = usu_codemp;
    }

    public Integer getUsu_numrom() {
        return usu_numrom;
    }

    public void setUsu_numrom(Integer usu_numrom) {
        this.usu_numrom = usu_numrom;
    }

    public Integer getUsu_codusu() {
        return usu_codusu;
    }

    public void setUsu_codusu(Integer usu_codusu) {
        this.usu_codusu = usu_codusu;
    }

    public Integer getUsu_codmot() {
        return usu_codmot;
    }

    public void setUsu_codmot(Integer usu_codmot) {
        this.usu_codmot = usu_codmot;
    }

    public Integer getUsu_numoat() {
        return usu_numoat;
    }

    public void setUsu_numoat(Integer usu_numoat) {
        this.usu_numoat = usu_numoat;
    }

    public String getUsu_tmpfat() {
        return usu_tmpfat;
    }

    public void setUsu_tmpfat(String usu_tmpfat) {
        this.usu_tmpfat = usu_tmpfat;
    }

    public String getUsu_tmptot() {
        return usu_tmptot;
    }

    public void setUsu_tmptot(String usu_tmptot) {
        this.usu_tmptot = usu_tmptot;
    }

    public String getUsu_sitgar() {
        return usu_sitgar;
    }

    public void setUsu_sitgar(String usu_sitgar) {
        this.usu_sitgar = usu_sitgar;
    }

    public String getUsu_obsipc() {
        return usu_obsipc;
    }

    public void setUsu_obsipc(String usu_obsipc) {
        this.usu_obsipc = usu_obsipc;
    }

    public String getUsu_codsnf() {
        return usu_codsnf;
    }

    public void setUsu_codsnf(String usu_codsnf) {
        this.usu_codsnf = usu_codsnf;
    }

    public String getUsu_itediv() {
        return usu_itediv;
    }

    public void setUsu_itediv(String usu_itediv) {
        this.usu_itediv = usu_itediv;
    }

    public String getUsu_codder() {
        return usu_codder;
    }

    public void setUsu_codder(String usu_codder) {
        this.usu_codder = usu_codder;
    }

    public String getUsu_forgar() {
        return usu_forgar;
    }

    public void setUsu_forgar(String usu_forgar) {
        this.usu_forgar = usu_forgar;
    }

    public String getUsu_insaut() {
        return usu_insaut;
    }

    public void setUsu_insaut(String usu_insaut) {
        this.usu_insaut = usu_insaut;
    }

    public String getUsu_excarq() {
        return usu_excarq;
    }

    public void setUsu_excarq(String usu_excarq) {
        this.usu_excarq = usu_excarq;
    }

    public String getUsu_numsep() {
        return usu_numsep;
    }

    public void setUsu_numsep(String usu_numsep) {
        this.usu_numsep = usu_numsep;
    }

    public String getUsu_itepro() {
        return usu_itepro;
    }

    public void setUsu_itepro(String usu_itepro) {
        this.usu_itepro = usu_itepro;
    }

    public String getUsu_tipnfs() {
        return usu_tipnfs;
    }

    public void setUsu_tipnfs(String usu_tipnfs) {
        this.usu_tipnfs = usu_tipnfs;
    }

    public String getUsu_desprb() {
        return usu_desprb;
    }

    public void setUsu_desprb(String usu_desprb) {
        this.usu_desprb = usu_desprb;
    }

    public String getUsu_dessol() {
        return usu_dessol;
    }

    public void setUsu_dessol(String usu_dessol) {
        this.usu_dessol = usu_dessol;
    }

    public String getUsu_tmpgar() {
        return usu_tmpgar;
    }

    public void setUsu_tmpgar(String usu_tmpgar) {
        this.usu_tmpgar = usu_tmpgar;
    }

    public String getUsu_codpro() {
        return usu_codpro;
    }

    public void setUsu_codpro(String usu_codpro) {
        this.usu_codpro = usu_codpro;
    }

    public String getUsu_tipnfc() {
        return usu_tipnfc;
    }

    public void setUsu_tipnfc(String usu_tipnfc) {
        this.usu_tipnfc = usu_tipnfc;
    }

    public Garantia getCadGarantia() {
        return CadGarantia;
    }

    public void setCadGarantia(Garantia CadGarantia) {
        this.CadGarantia = CadGarantia;
    }

    public Produto getCadProduto() {
        return CadProduto;
    }

    public void setCadProduto(Produto CadProduto) {
        this.CadProduto = CadProduto;
    }

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
    }

}
