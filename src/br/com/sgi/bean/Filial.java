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
public class Filial {

    private static final long serialVersionUID = 2606254772949774110L;
    private Integer id = 0;
    private Integer empresa_id = 0;
    private Integer filial;
    private String razao_social;
    private String email_apontar;
    private String apontar_erp;
    private String produtosucata;
    private String situacao;
    private String deposito;
    private Date created;
    private Date modified;
    
    private String estado;

    private Empresa empresa;
    private Double precoSucata;
    private String transacao_complemento = "0001";
    private String condicao_pgto_complemento = "SELECIONE";
    private String serie;
    
    private String cnpj;
    private String diretorio;

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getDiretorio() {
        return diretorio;
    }

    public void setDiretorio(String diretorio) {
        this.diretorio = diretorio;
    }
    
    

    public Double getPrecoSucata() {
        return precoSucata;
    }

    public void setPrecoSucata(Double precoSucata) {
        this.precoSucata = precoSucata;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getTransacao_complemento() {
        return transacao_complemento;
    }

    public void setTransacao_complemento(String transacao_complemento) {
        this.transacao_complemento = transacao_complemento;
    }

    public String getCondicao_pgto_complemento() {
        return condicao_pgto_complemento;
    }

    public void setCondicao_pgto_complemento(String condicao_pgto_complemento) {
        this.condicao_pgto_complemento = condicao_pgto_complemento;
    }

    public Filial() {

    }

    public Filial(Integer id, Integer empresa_id, Integer filial, Empresa empresa) {
        this.id = id;
        this.empresa_id = empresa_id;
        this.filial = filial;
        this.empresa = empresa;

    }

    public Filial(Integer id, Integer filial, String razao_social, String deposito) {
        this.id = id;
        this.razao_social = razao_social;
        this.filial = filial;
        this.deposito = deposito;
    }

    public String getProdutosucata() {
        return produtosucata;
    }

    public void setProdutosucata(String produtosucata) {
        this.produtosucata = produtosucata;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the razao_social
     */
    public String getRazao_social() {
        return razao_social;
    }

    /**
     * @param razao_social the razao_social to set
     */
    public void setRazao_social(String razao_social) {
        this.razao_social = razao_social;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * @return the empresa_id
     */
    public Integer getEmpresa_id() {
        return empresa_id;
    }

    /**
     * @param empresa_id the empresa_id to set
     */
    public void setEmpresa_id(Integer empresa_id) {
        this.empresa_id = empresa_id;
    }

    /**
     * @return the email_apontar
     */
    public String getEmail_apontar() {
        return email_apontar;
    }

    /**
     * @param email_apontar the email_apontar to set
     */
    public void setEmail_apontar(String email_apontar) {
        this.email_apontar = email_apontar;
    }

    /**
     * @return the apontar_erp
     */
    public String getApontar_erp() {
        return apontar_erp;
    }

    /**
     * @param apontar_erp the apontar_erp to set
     */
    public void setApontar_erp(String apontar_erp) {
        this.apontar_erp = apontar_erp;
    }

    /**
     * @return the situacao
     */
    public String getSituacao() {
        return situacao;
    }

    /**
     * @param situacao the situacao to set
     */
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    /**
     * @param empresa the empresa to set
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    /**
     * @return the empresa
     */
    public Empresa getEmpresa() {
        return empresa;
    }

    public Integer getFilial() {
        return filial;
    }

    public void setFilial(Integer filial) {
        this.filial = filial;
    }

    /**
     * @return the deposito
     */
    public String getDeposito() {
        return deposito;
    }

    /**
     * @param deposito the deposito to set
     */
    public void setDeposito(String deposito) {
        this.deposito = deposito;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    
}
