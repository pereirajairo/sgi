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
public class Impureza {
    private Integer codigo;
    private String descricao;
    private String tipo;
    private Double percentualImpureza = 0.0;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    
    
    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPercentualImpureza() {
        return percentualImpureza;
    }

    public void setPercentualImpureza(Double percentualImpureza) {
        this.percentualImpureza = percentualImpureza;
    }
            
             
    
}
