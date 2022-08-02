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
public class CargaItens {

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer numerocarga = 0;
    private Integer fornecedor = 0;
    private String nomeFornecedor;
    private Integer sequenciacarga = 0;
    private String produto;
    private String descricao;
    private Double quantidadePrevista = 0.0;
    private String observacao;
    private Integer usuariocadastro = 0;
    private Integer horascadastro = 0;
    private Date datacadastro;
    private Integer sequenciaPeso = 0;
    private Double pesoItem = 0.0;
    private Date dataIntegracao;
    private Integer horasItegracao = 0;
    private Integer usuarioIntegracao = 0;
    private Integer usuarioAlteracao;
    private Integer usu_horalt = 0;
    private Date dataAlteracao;
    private String serieNota;
    private Integer nota = 0;
    private Integer sequenciaItem = 0;
    private Double precoUnitario = 0.0;
    private String unidadeMedida;
    private String codigoEmbalagem = "";
    private Double pesoEmbalagem = 0.0;
    private Double pesoBruto = 0.0;
    private Double pesoLiquido = 0.0;
    private Double pesoUnitario = 0.0;
    private Double pesoImpureza = 0.0;

    public Double getPesoImpureza() {
        return pesoImpureza;
    }

    public void setPesoImpureza(Double pesoImpureza) {
        this.pesoImpureza = pesoImpureza;
    }

    public Integer getSequenciacarga() {
        return sequenciacarga;
    }

    public void setSequenciacarga(Integer sequenciacarga) {
        this.sequenciacarga = sequenciacarga;
    }
    
    
    
    private String situacaoPesagem = "Não Gravado";

    public String getSituacaoPesagem() {
        return situacaoPesagem;
    }

    public void setSituacaoPesagem(String situacaoPesagem) {
        this.situacaoPesagem = situacaoPesagem;
    }
    
    
    

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    public Double getPesoUnitario() {
        return pesoUnitario;
    }

    public void setPesoUnitario(Double pesoUnitario) {
        this.pesoUnitario = pesoUnitario;
    }

    public Double getPesoItem() {
        return pesoItem;
    }

    public void setPesoItem(Double pesoItem) {
        this.pesoItem = pesoItem;
    }

    public Double getPesoEmbalagem() {
        return pesoEmbalagem;
    }

    public void setPesoEmbalagem(Double pesoEmbalagem) {
        this.pesoEmbalagem = pesoEmbalagem;
    }

    /**
     * @return the codigoEmbalagem
     */
    public String getCodigoEmbalagem() {
        return codigoEmbalagem;
    }

    /**
     * @param codigoEmbalagem the codigoEmbalagem to set
     */
    public void setCodigoEmbalagem(String codigoEmbalagem) {
        this.codigoEmbalagem = codigoEmbalagem;
    }

    public Integer getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Integer empresa) {
        this.empresa = empresa;
    }

    public Integer getFilial() {
        return filial;
    }

    public void setFilial(Integer filial) {
        this.filial = filial;
    }

    public Integer getNumerocarga() {
        return numerocarga;
    }

    public void setNumerocarga(Integer numerocarga) {
        this.numerocarga = numerocarga;
    }

    public Integer getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Integer fornecedor) {
        this.fornecedor = fornecedor;
    }

  

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getQuantidadePrevista() {
        return quantidadePrevista;
    }

    public void setQuantidadePrevista(Double quantidadePrevista) {
        this.quantidadePrevista = quantidadePrevista;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Integer getUsuariocadastro() {
        return usuariocadastro;
    }

    public void setUsuariocadastro(Integer usuariocadastro) {
        this.usuariocadastro = usuariocadastro;
    }

    public Integer getHorascadastro() {
        return horascadastro;
    }

    public void setHorascadastro(Integer horascadastro) {
        this.horascadastro = horascadastro;
    }

    public Date getDatacadastro() {
        return datacadastro;
    }

    public void setDatacadastro(Date datacadastro) {
        this.datacadastro = datacadastro;
    }

    public Integer getSequenciaPeso() {
        return sequenciaPeso;
    }

    public void setSequenciaPeso(Integer sequenciaPeso) {
        this.sequenciaPeso = sequenciaPeso;
    }

    public Date getDataIntegracao() {
        return dataIntegracao;
    }

    public void setDataIntegracao(Date dataIntegracao) {
        this.dataIntegracao = dataIntegracao;
    }

    public Integer getHorasItegracao() {
        return horasItegracao;
    }

    public void setHorasItegracao(Integer horasItegracao) {
        this.horasItegracao = horasItegracao;
    }

    public Integer getUsuarioIntegracao() {
        return usuarioIntegracao;
    }

    public void setUsuarioIntegracao(Integer usuarioIntegracao) {
        this.usuarioIntegracao = usuarioIntegracao;
    }

    public Integer getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(Integer usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public Integer getUsu_horalt() {
        return usu_horalt;
    }

    public void setUsu_horalt(Integer usu_horalt) {
        this.usu_horalt = usu_horalt;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getSerieNota() {
        return serieNota;
    }

    public void setSerieNota(String serieNota) {
        this.serieNota = serieNota;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public Integer getSequenciaItem() {
        return sequenciaItem;
    }

    public void setSequenciaItem(Integer sequenciaItem) {
        this.sequenciaItem = sequenciaItem;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(Double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public Double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(Double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

}
