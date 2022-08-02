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
public class Garantia {

    private Integer usu_empdes;
    private Integer usu_codfor;
    private Integer usu_numnfc;
    private String usu_cgccpf;
    private Integer usu_sitnfc;
    private String situacao;
    private String situacaogeral;
    private Integer usu_numdoc;

    private Date usu_datemi;
    private String usu_codpro;

    private String usu_tipnfc;
    private String usu_retter;
    private String usu_obsnfc;
    private Integer usu_codfil;
    private String usu_codsnf;
    private String usu_garfis;

    private Fornecedor CadFornecedor;
    private Cliente CadCliente;

    public Integer getUsu_empdes() {
        return usu_empdes;
    }

    public void setUsu_empdes(Integer usu_empdes) {
        this.usu_empdes = usu_empdes;
    }

    public Integer getUsu_codfor() {
        return usu_codfor;
    }

    public void setUsu_codfor(Integer usu_codfor) {
        this.usu_codfor = usu_codfor;
    }

    public Integer getUsu_numnfc() {
        return usu_numnfc;
    }

    public void setUsu_numnfc(Integer usu_numnfc) {
        this.usu_numnfc = usu_numnfc;
    }

    public String getUsu_cgccpf() {
        return usu_cgccpf;
    }

    public void setUsu_cgccpf(String usu_cgccpf) {
        this.usu_cgccpf = usu_cgccpf;
    }

    public Integer getUsu_sitnfc() {
        return usu_sitnfc;
    }

    public void setUsu_sitnfc(Integer usu_sitnfc) {
        this.usu_sitnfc = usu_sitnfc;
    }

    public Integer getUsu_numdoc() {
        return usu_numdoc;
    }

    public void setUsu_numdoc(Integer usu_numdoc) {
        this.usu_numdoc = usu_numdoc;
    }

    public Date getUsu_datemi() {
        return usu_datemi;
    }

    public void setUsu_datemi(Date usu_datemi) {
        this.usu_datemi = usu_datemi;
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

    public String getUsu_retter() {
        return usu_retter;
    }

    public void setUsu_retter(String usu_retter) {
        this.usu_retter = usu_retter;
    }

    public String getUsu_obsnfc() {
        return usu_obsnfc;
    }

    public void setUsu_obsnfc(String usu_obsnfc) {
        this.usu_obsnfc = usu_obsnfc;
    }

    public Integer getUsu_codfil() {
        return usu_codfil;
    }

    public void setUsu_codfil(Integer usu_codfil) {
        this.usu_codfil = usu_codfil;
    }

    public String getUsu_codsnf() {
        return usu_codsnf;
    }

    public void setUsu_codsnf(String usu_codsnf) {
        this.usu_codsnf = usu_codsnf;
    }

    public String getUsu_garfis() {
        return usu_garfis;
    }

    public void setUsu_garfis(String usu_garfis) {
        this.usu_garfis = usu_garfis;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Fornecedor getCadFornecedor() {
        return CadFornecedor;
    }

    public void setCadFornecedor(Fornecedor CadFornecedor) {
        this.CadFornecedor = CadFornecedor;
    }

    public Cliente getCadCliente() {
        return CadCliente;
    }

    public void setCadCliente(Cliente CadCliente) {
        this.CadCliente = CadCliente;
    }

    public String getSituacaogeral() {
        return situacaogeral;
    }

    public void setSituacaogeral(String situacaogeral) {
        this.situacaogeral = situacaogeral;
    }
    
    

}
