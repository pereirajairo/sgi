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
public class Imagem {

    private static final long serialVersionUID = 2606254772911774110L;
    private Integer id = 0;
    private String tipoImagem;
    private String nomeImagem;
    private Date dataInclusao;
    private byte[] fileBlob;
    private String fileString;
    private String descricao;
    private String processo;
    private Integer registro_id = 0;
    private Integer sequencia = 0;
    private String caminho;
    private String btn_gravar = "true";

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public Integer getRegistro_id() {
        return registro_id;
    }

    public void setRegistro_id(Integer registro_id) {
        this.registro_id = registro_id;
    }

    public Imagem() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the tipoImagem
     */
    public String getTipoImagem() {
        return tipoImagem;
    }

    /**
     * @param tipoImagem the tipoImagem to set
     */
    public void setTipoImagem(String tipoImagem) {
        this.tipoImagem = tipoImagem;
    }

    /**
     * @return the nomeImagem
     */
    public String getNomeImagem() {
        return nomeImagem;
    }

    /**
     * @param nomeImagem the nomeImagem to set
     */
    public void setNomeImagem(String nomeImagem) {
        this.nomeImagem = nomeImagem;
    }

    /**
     * @return the dataInclusao
     */
    public Date getDataInclusao() {
        return dataInclusao;
    }

    /**
     * @param dataInclusao the dataInclusao to set
     */
    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    /**
     * @return the fileBlob
     */
    public byte[] getFileBlob() {
        return fileBlob;
    }

    /**
     * @param fileBlob the fileBlob to set
     */
    public void setFileBlob(byte[] fileBlob) {
        this.fileBlob = fileBlob;
    }

    /**
     * @return the fileString
     */
    public String getFileString() {
        return fileString;
    }

    /**
     * @param fileString the fileString to set
     */
    public void setFileString(String fileString) {
        this.fileString = fileString;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public String getBtn_gravar() {
        return btn_gravar;
    }

    public void setBtn_gravar(String btn_gravar) {
        this.btn_gravar = btn_gravar;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
    
    

}
