/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

/**
 *
 * @author jairosilva
 */
public class Produto {

    private Integer empresa = 0;
    private String codigoproduto = "0";
    private String descricaoproduto;
    private String familiaproduto;

    private Double rentabilidade;
    private Double precoinsumo = 0.0;

    public Produto() {

    }

    public Produto(Integer empresa, String codigoproduto, String descricaoproduto) {
        this.empresa = empresa;
        this.codigoproduto = codigoproduto;
        this.descricaoproduto = descricaoproduto;
    }

    public Double getPrecoinsumo() {
        return precoinsumo;
    }

    public void setPrecoinsumo(Double precoinsumo) {
        this.precoinsumo = precoinsumo;
    }

    public Double getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(Double rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public Integer getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Integer empresa) {
        this.empresa = empresa;
    }

    public String getCodigoproduto() {
        return codigoproduto;
    }

    public void setCodigoproduto(String codigoproduto) {
        this.codigoproduto = codigoproduto;
    }

    public String getDescricaoproduto() {
        return descricaoproduto;
    }

    public void setDescricaoproduto(String descricaoproduto) {
        this.descricaoproduto = descricaoproduto;
    }

    public String getFamiliaproduto() {
        return familiaproduto;
    }

    public void setFamiliaproduto(String familiaproduto) {
        this.familiaproduto = familiaproduto;
    }

}
