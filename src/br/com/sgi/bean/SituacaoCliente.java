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
public class SituacaoCliente {

    private String codigo;
    private String descricao;
    private Date emissao;
    private double dias_ultimo_faturamento = 0.0;
    private String emissaoS;

    public String getEmissaoS() {
        return emissaoS;
    }

    public void setEmissaoS(String emissaoS) {
        this.emissaoS = emissaoS;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public double getDias_ultimo_faturamento() {
        return dias_ultimo_faturamento;
    }

    public void setDias_ultimo_faturamento(double dias_ultimo_faturamento) {
        this.dias_ultimo_faturamento = dias_ultimo_faturamento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
