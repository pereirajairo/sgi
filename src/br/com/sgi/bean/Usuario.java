/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

/**
 *
 * @author jairosilva
 */
public class Usuario {

    private static final long serialVersionUID = 2606254772949774110L;
    private Integer id=0;
    private String nome;
    private String senha;
    private String situacao;
    private Integer ultnum=0;
    private String CodEmp;
    private String Codfil;

    
    
    public String getCodEmp() {
        return CodEmp;
    }

    public void setCodEmp(String CodEmp) {
        this.CodEmp = CodEmp;
    }

    public String getCodfil() {
        return Codfil;
    }

    public void setCodfil(String Codfil) {
        this.Codfil = Codfil;
    }

    public Integer getUltnum() {
        return ultnum;
    }

    public void setUltnum(Integer ultnum) {
        this.ultnum = ultnum;
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
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the senha
     */
    public String getSenha() {
        return senha;
    }

    /**
     * @param senha the senha to set
     */
    public void setSenha(String senha) {
        this.senha = senha;
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

}
