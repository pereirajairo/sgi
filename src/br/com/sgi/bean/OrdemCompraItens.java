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
public class OrdemCompraItens {

    private Integer empresa = 1;
    private Integer filial = 1;
    private Integer numeroOrdemCompra = 0;
    private Integer sequenciaItem = 0;
private Integer notafiscal=0;
    private String complementoProdutoOrdem;
    private String unidadeMedida;
    private Double pesoLiquido = 0.0;
    private Double pesoBruto = 0.0;
    private Double quantidadePedida = 0.0;
    private Double quantidadeRecebida = 0.0;
    private Double quantidadeAberta = 0.0;

    private OrdemCompra ordemCompra;
    private Produto produto;

    public Integer getNotafiscal() {
        return notafiscal;
    }

    public void setNotafiscal(Integer notafiscal) {
        this.notafiscal = notafiscal;
    }

    
    
    
    public OrdemCompra getOrdemCompra() {
        return ordemCompra;
    }

    public void setOrdemCompra(OrdemCompra ordemCompra) {
        this.ordemCompra = ordemCompra;
    }

    /**
     * @return the produto
     */
    public Produto getProduto() {
        return produto;
    }

    /**
     * @param produto the produto to set
     */
    public void setProduto(Produto produto) {
        this.produto = produto;
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

    public Integer getNumeroOrdemCompra() {
        return numeroOrdemCompra;
    }

    public void setNumeroOrdemCompra(Integer numeroOrdemCompra) {
        this.numeroOrdemCompra = numeroOrdemCompra;
    }

    public Integer getSequenciaItem() {
        return sequenciaItem;
    }

    public void setSequenciaItem(Integer sequenciaItem) {
        this.sequenciaItem = sequenciaItem;
    }

    public String getComplementoProdutoOrdem() {
        return complementoProdutoOrdem;
    }

    public void setComplementoProdutoOrdem(String complementoProdutoOrdem) {
        this.complementoProdutoOrdem = complementoProdutoOrdem;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(Double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public Double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(Double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public Double getQuantidadePedida() {
        return quantidadePedida;
    }

    public void setQuantidadePedida(Double quantidadePedida) {
        this.quantidadePedida = quantidadePedida;
    }

    public Double getQuantidadeRecebida() {
        return quantidadeRecebida;
    }

    public void setQuantidadeRecebida(Double quantidadeRecebida) {
        this.quantidadeRecebida = quantidadeRecebida;
    }

    public Double getQuantidadeAberta() {
        return quantidadeAberta;
    }

    public void setQuantidadeAberta(Double quantidadeAberta) {
        this.quantidadeAberta = quantidadeAberta;
    }

}
