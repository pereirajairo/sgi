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
public class VinculoGrupo {

  private Integer checkbox;
  private Integer codigoTela;
  private Integer codigoGrupo;
  private String nomeTela;
  private Integer sequeciaLigacao;

    public Integer getSequeciaLigacao() {
        return sequeciaLigacao;
    }

    public void setSequeciaLigacao(Integer sequeciaLigacao) {
        this.sequeciaLigacao = sequeciaLigacao;
    }


  private boolean  selecionar;

    public boolean isSelecionar() {
        return selecionar;
    }

    public void setSelecionar(boolean selecionar) {
        this.selecionar = selecionar;
    }
    public Integer getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(Integer checkbox) {
        this.checkbox = checkbox;
    }

    public Integer getCodigoTela() {
        return codigoTela;
    }

    public void setCodigoTela(Integer codigoTela) {
        this.codigoTela = codigoTela;
    }

    public Integer getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(Integer codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public String getNomeTela() {
        return nomeTela;
    }

    public void setNomeTela(String nomeTela) {
        this.nomeTela = nomeTela;
    }

    
}
