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
public class Transportadora {

    private Integer codigoTransportadora = 0;
    private String nomeTransportadora;
    private String cidade;
    private String estado;
    private String apelido;
    private String fornecedor = "0";

    public Transportadora() {

    }

    public Transportadora(Integer codigoTransportadora, String nomeTransportadora) {
        this.codigoTransportadora = codigoTransportadora;
        this.nomeTransportadora = nomeTransportadora;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    
    
    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public Integer getCodigoTransportadora() {
        return codigoTransportadora;
    }

    public void setCodigoTransportadora(Integer codigoTransportadora) {
        this.codigoTransportadora = codigoTransportadora;
    }

    public String getNomeTransportadora() {
        return nomeTransportadora;
    }

    public void setNomeTransportadora(String nomeTransportadora) {
        this.nomeTransportadora = nomeTransportadora;
    }

}
