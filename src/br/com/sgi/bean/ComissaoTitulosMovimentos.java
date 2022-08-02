/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Date;

/**
 *
 * @author jefferson.luiz
 */
public class ComissaoTitulosMovimentos {
 private ComissaoTitulos CadComissaoTitulos;
    private Integer empresa;
    private Integer filial;
    private String numeroTitulo;
    private String tipoTitulo;
    private Integer sequencia;
    private String linha;
    private Integer quantidade;
    private String transacaoMov;
    private Date dataMov;
    private Date dataEmissao;
    private Date vencimentoAtual;
    private String dataMovS;
    private String dataPagamentoS;
    private String dataLiberacaoS;
    private String dataEmissaoS;
    private String vencimentoAtualS;
    private Double valorAberto;
    private Date dataPagamento;
    private Integer formaPag;
    private Date dataLiberacao;    
    private Double valorMov;
    private Double valorDesc;
    private Double juros;
    private Double percentualComissao;
    private Double multas;
    private Double encargos;
    private Double valorOutros;
    private Double valorLiquido;
    private Double valorBase;
    private Integer notaFiscal;
    private Integer pedido;
    private Double valorComissao;
    private Date ultimoPagamento;
    private Integer filialRelacionado;
    private String tituloRelacionado;
    private String tipoTituloRelacionado;
    private String indicativoPago;
    private Integer filialSubRelacionado;
    private String tituloSubRelacionado;
    private String tipoTituloSubRelacionado;
    private String categoriaRep;
    private Integer empresaComissao;
    private Double diferencaBase;
    private Integer ideExt;
    private String servico;
    private String complementoServ;
    private String condPagOc;
    private String descCondPagOc;
    private Integer fornecedor;
    private String emailRep;
    private Double valorLiq;
    private Double valorIrf;
    private String regimeTrib;
    private Integer repOc;
    private Double valorComissaoItem;
    private Integer numeroOC;
    private String comissaoValidada;

    public String getComissaoValidada() {
        return comissaoValidada;
    }

    public void setComissaoValidada(String comissaoValidada) {
        this.comissaoValidada = comissaoValidada;
    }

    public Integer getNumeroOC() {
        return numeroOC;
    }

    public void setNumeroOC(Integer numeroOC) {
        this.numeroOC = numeroOC;
    }  
    
    public Double getValorComissaoItem() {
        return valorComissaoItem;
    }

    public void setValorComissaoItem(Double valorComissaoItem) {
        this.valorComissaoItem = valorComissaoItem;
    }


    public Integer getRepOc() {
        return repOc;
    }

    public void setRepOc(Integer repOc) {
        this.repOc = repOc;
    }

    public Double getValorLiq() {
        return valorLiq;
    }

    public void setValorLiq(Double valorLiq) {
        this.valorLiq = valorLiq;
    }

    public Double getValorIrf() {
        return valorIrf;
    }

    public void setValorIrf(Double valorIrf) {
        this.valorIrf = valorIrf;
    }

    public String getRegimeTrib() {
        return regimeTrib;
    }

    public void setRegimeTrib(String regimeTrib) {
        this.regimeTrib = regimeTrib;
    }

  
    public String getDescCondPagOc() {
        return descCondPagOc;
    }

    public void setDescCondPagOc(String descCondPagOc) {
        this.descCondPagOc = descCondPagOc;
    }

    public Integer getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Integer fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getEmailRep() {
        return emailRep;
    }

    public void setEmailRep(String emailRep) {
        this.emailRep = emailRep;
    }
    

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public String getComplementoServ() {
        return complementoServ;
    }

    public void setComplementoServ(String complementoServ) {
        this.complementoServ = complementoServ;
    }

    public String getCondPagOc() {
        return condPagOc;
    }

    public void setCondPagOc(String condPagOc) {
        this.condPagOc = condPagOc;
    }

    public Integer getIdeExt() {
        return ideExt;
    }

    public void setIdeExt(Integer ideExt) {
        this.ideExt = ideExt;
    }
    
    public Double getDiferencaBase() {
        return diferencaBase;
    }

    public void setDiferencaBase(Double diferencaBase) {
        this.diferencaBase = diferencaBase;
    }

    public Integer getEmpresaComissao() {
        return empresaComissao;
    }

    public void setEmpresaComissao(Integer empresaComissao) {
        this.empresaComissao = empresaComissao;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Integer getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(Integer notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public String getCategoriaRep() {
        return categoriaRep;
    }

    public void setCategoriaRep(String categoriaRep) {
        this.categoriaRep = categoriaRep;
    }

    public ComissaoTitulos getCadComissaoTitulos() {
        return CadComissaoTitulos;
    }

    public void setCadComissaoTitulos(ComissaoTitulos CadComissaoTitulos) {
        this.CadComissaoTitulos = CadComissaoTitulos;
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

    public String getNumeroTitulo() {
        return numeroTitulo;
    }

    public void setNumeroTitulo(String numeroTitulo) {
        this.numeroTitulo = numeroTitulo;
    }

    public String getTipoTitulo() {
        return tipoTitulo;
    }

    public void setTipoTitulo(String tipoTitulo) {
        this.tipoTitulo = tipoTitulo;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public String getTransacaoMov() {
        return transacaoMov;
    }

    public void setTransacaoMov(String transacaoMov) {
        this.transacaoMov = transacaoMov;
    }

    public Date getDataMov() {
        return dataMov;
    }

    public void setDataMov(Date dataMov) {
        this.dataMov = dataMov;
    }

    public Date getVencimentoAtual() {
        return vencimentoAtual;
    }

    public void setVencimentoAtual(Date vencimentoAtual) {
        this.vencimentoAtual = vencimentoAtual;
    }

    public Double getValorAberto() {
        return valorAberto;
    }

    public void setValorAberto(Double valorAberto) {
        this.valorAberto = valorAberto;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Integer getFormaPag() {
        return formaPag;
    }

    public void setFormaPag(Integer formaPag) {
        this.formaPag = formaPag;
    }

    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }

    public Double getValorMov() {
        return valorMov;
    }

    public void setValorMov(Double valorMov) {
        this.valorMov = valorMov;
    }

    public Double getValorDesc() {
        return valorDesc;
    }

    public void setValorDesc(Double valorDesc) {
        this.valorDesc = valorDesc;
    }

    public Double getJuros() {
        return juros;
    }

    public void setJuros(Double juros) {
        this.juros = juros;
    }

    public Double getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(Double percentualComissao) {
        this.percentualComissao = percentualComissao;
    }

    public Double getMultas() {
        return multas;
    }

    public void setMultas(Double multas) {
        this.multas = multas;
    }

    public Double getEncargos() {
        return encargos;
    }

    public void setEncargos(Double encargos) {
        this.encargos = encargos;
    }

    public Double getValorOutros() {
        return valorOutros;
    }

    public void setValorOutros(Double valorOutros) {
        this.valorOutros = valorOutros;
    }

    public Double getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(Double valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public Double getValorBase() {
        return valorBase;
    }

    public void setValorBase(Double valorBase) {
        this.valorBase = valorBase;
    }

    public Double getValorComissao() {
        return valorComissao;
    }

    public void setValorComissao(Double valorComissao) {
        this.valorComissao = valorComissao;
    }

    public Date getUltimoPagamento() {
        return ultimoPagamento;
    }

    public void setUltimoPagamento(Date ultimoPagamento) {
        this.ultimoPagamento = ultimoPagamento;
    }

    public Integer getFilialRelacionado() {
        return filialRelacionado;
    }

    public void setFilialRelacionado(Integer filialRelacionado) {
        this.filialRelacionado = filialRelacionado;
    }

    public String getTituloRelacionado() {
        return tituloRelacionado;
    }

    public void setTituloRelacionado(String tituloRelacionado) {
        this.tituloRelacionado = tituloRelacionado;
    }

    public String getTipoTituloRelacionado() {
        return tipoTituloRelacionado;
    }

    public void setTipoTituloRelacionado(String tipoTituloRelacionado) {
        this.tipoTituloRelacionado = tipoTituloRelacionado;
    }

    public String getIndicativoPago() {
        return indicativoPago;
    }

    public void setIndicativoPago(String indicativoPago) {
        this.indicativoPago = indicativoPago;
    }

    public Integer getFilialSubRelacionado() {
        return filialSubRelacionado;
    }

    public void setFilialSubRelacionado(Integer filialSubRelacionado) {
        this.filialSubRelacionado = filialSubRelacionado;
    }

    public String getTituloSubRelacionado() {
        return tituloSubRelacionado;
    }

    public void setTituloSubRelacionado(String tituloSubRelacionado) {
        this.tituloSubRelacionado = tituloSubRelacionado;
    }

    public String getTipoTituloSubRelacionado() {
        return tipoTituloSubRelacionado;
    }

    public void setTipoTituloSubRelacionado(String tipoTituloSubRelacionado) {
        this.tipoTituloSubRelacionado = tipoTituloSubRelacionado;
    }

    public String getDataMovS() {
        return dataMovS;
    }

    public void setDataMovS(String dataMovS) {
        this.dataMovS = dataMovS;
    }

    public String getDataEmissaoS() {
        return dataEmissaoS;
    }

    public void setDataEmissaoS(String dataEmissaoS) {
        this.dataEmissaoS = dataEmissaoS;
    }

    public String getVencimentoAtualS() {
        return vencimentoAtualS;
    }

    public void setVencimentoAtualS(String vencimentoAtualS) {
        this.vencimentoAtualS = vencimentoAtualS;
    }

    public String getDataPagamentoS() {
        return dataPagamentoS;
    }

    public void setDataPagamentoS(String dataPagamentoS) {
        this.dataPagamentoS = dataPagamentoS;
    }

    public String getDataLiberacaoS() {
        return dataLiberacaoS;
    }

    public void setDataLiberacaoS(String dataLiberacaoS) {
        this.dataLiberacaoS = dataLiberacaoS;
    }
}
