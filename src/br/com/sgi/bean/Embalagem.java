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
public class Embalagem {

    private Integer empresa;
    private Integer embalagem;
    private String descricaoEmbalagem;
    private Double pesoEmbalagem;

    public Integer getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Integer empresa) {
        this.empresa = empresa;
    }

    public Integer getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(Integer embalagem) {
        this.embalagem = embalagem;
    }

    public String getDescricaoEmbalagem() {
        return descricaoEmbalagem;
    }

    public void setDescricaoEmbalagem(String descricaoEmbalagem) {
        this.descricaoEmbalagem = descricaoEmbalagem;
    }

    public Double getPesoEmbalagem() {
        return pesoEmbalagem;
    }

    public void setPesoEmbalagem(Double pesoEmbalagem) {
        this.pesoEmbalagem = pesoEmbalagem;
    }

}
