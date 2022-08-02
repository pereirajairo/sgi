/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

/**
 *
 * @author jairo.silva
 */
public class Representante {
    private Integer codigo;
    private String nome;
    private String email;
    
    private String codigoHub;
    private String nomeHub;
    private String depositoHub;
    

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigoHub() {
        return codigoHub;
    }

    public void setCodigoHub(String codigoHub) {
        this.codigoHub = codigoHub;
    }

    public String getNomeHub() {
        return nomeHub;
    }

    public void setNomeHub(String nomeHub) {
        this.nomeHub = nomeHub;
    }

    public String getDepositoHub() {
        return depositoHub;
    }

    public void setDepositoHub(String depositoHub) {
        this.depositoHub = depositoHub;
    }
    
    
    
}
