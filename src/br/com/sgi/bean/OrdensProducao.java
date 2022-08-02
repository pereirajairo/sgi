/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Date;


public class OrdensProducao {

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer ordenproducao = 0;
    //
    private String linha_producao;
    private String produto="";
    private String produto_descricao;
    private String situacao = "";
    //
    private Double quantidade = 0.0;
    private Double quantidade_saldo = 0.0;
    private Double quantidade_produzida = 0.0;
    private Double quantidade_defeito = 0.0;
    private Double quantidade_embalagem = 0.0;

    //
    private Date data_producao;

    private Date data_prev_inicio;
    private Date data_prev_fim;

    private String analisada;
    private String situacaoERP;

    private Integer empresa_id;
    private Integer filial_id;
    private Integer pedido;
    private Integer id = 0;
    private String horasapontamento;
    private String derivacao;
    private String origem="";

    private String inicado;
    private String color_situacao;
    private String fa_situacao;
    private String operacao_apontar;
    private String deposito;

    public Double getQuantidade_Realizada_1() {
        return quantidade_Realizada_1;
    }

    public void setQuantidade_Realizada_1(Double quantidade_Realizada_1) {
        this.quantidade_Realizada_1 = quantidade_Realizada_1;
    }

    public Double getQuantidade_Realizada_2() {
        return quantidade_Realizada_2;
    }

    public void setQuantidade_Realizada_2(Double quantidade_Realizada_2) {
        this.quantidade_Realizada_2 = quantidade_Realizada_2;
    }

    public Double getQuantidade_Realizada_3() {
        return quantidade_Realizada_3;
    }

    public void setQuantidade_Realizada_3(Double quantidade_Realizada_3) {
        this.quantidade_Realizada_3 = quantidade_Realizada_3;
    }

    private String iniciado = "Iniciar";
    private String label;
    private String minifabrica = "";
    private String relatorioPrd = "";
    private String sublote = "";
    private String barcode;
    //
    private String dataproducaostr;
    private String maquina;
    
    private  Double quantidade_Realizada_1;
    private  Double quantidade_Realizada_2;
    private  Double quantidade_Realizada_3;
    /**
     * @return the empresa
     */
    public Integer getEmpresa() {
        return empresa;
    }

    /**
     * @param empresa the empresa to set
     */
    public void setEmpresa(Integer empresa) {
        this.empresa = empresa;
    }

    /**
     * @return the filial
     */
    public Integer getFilial() {
        return filial;
    }

    /**
     * @param filial the filial to set
     */
    public void setFilial(Integer filial) {
        this.filial = filial;
    }

    /**
     * @return the ordenproducao
     */
    public Integer getOrdenproducao() {
        return ordenproducao;
    }

    /**
     * @param ordenproducao the ordenproducao to set
     */
    public void setOrdenproducao(Integer ordenproducao) {
        this.ordenproducao = ordenproducao;
    }

    /**
     * @return the linha_producao
     */
    public String getLinha_producao() {
        return linha_producao;
    }

    /**
     * @param linha_producao the linha_producao to set
     */
    public void setLinha_producao(String linha_producao) {
        this.linha_producao = linha_producao;
    }

    /**
     * @return the produto
     */
    public String getProduto() {
        return produto;
    }

    /**
     * @param produto the produto to set
     */
    public void setProduto(String produto) {
        this.produto = produto;
    }

    /**
     * @return the quantidade
     */
    public Double getQuantidade() {
        return quantidade;
    }

    /**
     * @param quantidade the quantidade to set
     */
    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    /**
     * @return the quantidade_saldo
     */
    public Double getQuantidade_saldo() {
        return quantidade_saldo;
    }

    /**
     * @param quantidade_saldo the quantidade_saldo to set
     */
    public void setQuantidade_saldo(Double quantidade_saldo) {
        this.quantidade_saldo = quantidade_saldo;
    }

    /**
     * @return the data_producao
     */
    public Date getData_producao() {
        return data_producao;
    }

    /**
     * @param data_producao the data_producao to set
     */
    public void setData_producao(Date data_producao) {
        this.data_producao = data_producao;
    }

    /**
     * @return the quantidade_produzida
     */
    public Double getQuantidade_produzida() {
        return quantidade_produzida;
    }

    /**
     * @param quantidade_produzida the quantidade_produzida to set
     */
    public void setQuantidade_produzida(Double quantidade_produzida) {
        this.quantidade_produzida = quantidade_produzida;
    }

    /**
     * @return the situacao
     */
    public String getSituacao() {
        return situacao;
    }

    /**
     * @param situacao the situacao to set
     */
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    /**
     * @return the quantidade_defeito
     */
    public Double getQuantidade_defeito() {
        return quantidade_defeito;
    }

    /**
     * @param quantidade_defeito the quantidade_defeito to set
     */
    public void setQuantidade_defeito(Double quantidade_defeito) {
        this.quantidade_defeito = quantidade_defeito;
    }

    /**
     * @return the analisada
     */
    public String getAnalisada() {
        return analisada;
    }

    /**
     * @param analisada the analisada to set
     */
    public void setAnalisada(String analisada) {
        this.analisada = analisada;
    }

    /**
     * @return the empresa_id
     */
    public Integer getEmpresa_id() {
        return empresa_id;
    }

    /**
     * @param empresa_id the empresa_id to set
     */
    public void setEmpresa_id(Integer empresa_id) {
        this.empresa_id = empresa_id;
    }

    /**
     * @return the filial_id
     */
    public Integer getFilial_id() {
        return filial_id;
    }

    /**
     * @param filial_id the filial_id to set
     */
    public void setFilial_id(Integer filial_id) {
        this.filial_id = filial_id;
    }

    /**
     * @return the situacaoERP
     */
    public String getSituacaoERP() {
        return situacaoERP;
    }

    /**
     * @param situacaoERP the situacaoERP to set
     */
    public void setSituacaoERP(String situacaoERP) {
        this.situacaoERP = situacaoERP;
    }

    /**
     * @return the pedido
     */
    public Integer getPedido() {
        return pedido;
    }

    /**
     * @param pedido the pedido to set
     */
    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the horasapontamento
     */
    public String getHorasapontamento() {
        return horasapontamento;
    }

    /**
     * @param horasapontamento the horasapontamento to set
     */
    public void setHorasapontamento(String horasapontamento) {
        this.horasapontamento = horasapontamento;
    }

    /**
     * @return the derivacao
     */
    public String getDerivacao() {
        return derivacao;
    }

    /**
     * @param derivacao the derivacao to set
     */
    public void setDerivacao(String derivacao) {
        this.derivacao = derivacao;
    }

    /**
     * @return the origem
     */
    public String getOrigem() {
        return origem;
    }

    /**
     * @param origem the origem to set
     */
    public void setOrigem(String origem) {
        this.origem = origem;
    }

    /**
     * @return the inicado
     */
    public String getInicado() {
        return inicado;
    }

    /**
     * @param inicado the inicado to set
     */
    public void setInicado(String inicado) {
        this.inicado = inicado;
    }

    /**
     * @return the color_situacao
     */
    public String getColor_situacao() {
        return color_situacao;
    }

    /**
     * @param color_situacao the color_situacao to set
     */
    public void setColor_situacao(String color_situacao) {
        this.color_situacao = color_situacao;
    }

    /**
     * @return the fa_situacao
     */
    public String getFa_situacao() {
        return fa_situacao;
    }

    /**
     * @param fa_situacao the fa_situacao to set
     */
    public void setFa_situacao(String fa_situacao) {
        this.fa_situacao = fa_situacao;
    }

    /**
     * @return the operacao_apontar
     */
    public String getOperacao_apontar() {
        return operacao_apontar;
    }

    /**
     * @param operacao_apontar the operacao_apontar to set
     */
    public void setOperacao_apontar(String operacao_apontar) {
        this.operacao_apontar = operacao_apontar;
    }

    /**
     * @return the deposito
     */
    public String getDeposito() {
        return deposito;
    }

    /**
     * @param deposito the deposito to set
     */
    public void setDeposito(String deposito) {
        this.deposito = deposito;
    }

    /**
     * @return the data_prev_inicio
     */
    public Date getData_prev_inicio() {
        return data_prev_inicio;
    }

    /**
     * @param data_prev_inicio the data_prev_inicio to set
     */
    public void setData_prev_inicio(Date data_prev_inicio) {
        this.data_prev_inicio = data_prev_inicio;
    }

    /**
     * @return the data_prev_fim
     */
    public Date getData_prev_fim() {
        return data_prev_fim;
    }

    /**
     * @param data_prev_fim the data_prev_fim to set
     */
    public void setData_prev_fim(Date data_prev_fim) {
        this.data_prev_fim = data_prev_fim;
    }

    /**
     * @return the iniciado
     */
    public String getIniciado() {
        return iniciado;
    }

    /**
     * @param iniciado the iniciado to set
     */
    public void setIniciado(String iniciado) {
        this.iniciado = iniciado;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the minifabrica
     */
    public String getMinifabrica() {
        return minifabrica;
    }

    /**
     * @param minifabrica the minifabrica to set
     */
    public void setMinifabrica(String minifabrica) {
        this.minifabrica = minifabrica;
    }

    /**
     * @return the relatorioPrd
     */
    public String getRelatorioPrd() {
        return relatorioPrd;
    }

    /**
     * @param relatorioPrd the relatorioPrd to set
     */
    public void setRelatorioPrd(String relatorioPrd) {
        this.relatorioPrd = relatorioPrd;
    }

    /**
     * @return the sublote
     */
    public String getSublote() {
        return sublote;
    }

    /**
     * @param sublote the sublote to set
     */
    public void setSublote(String sublote) {
        this.sublote = sublote;
    }

    /**
     * @return the barcode
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * @param barcode the barcode to set
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * @return the quantidade_embalagem
     */
    public Double getQuantidade_embalagem() {
        return quantidade_embalagem;
    }

    /**
     * @param quantidade_embalagem the quantidade_embalagem to set
     */
    public void setQuantidade_embalagem(Double quantidade_embalagem) {
        this.quantidade_embalagem = quantidade_embalagem;
    }

    /**
     * @return the dataproducaostr
     */
    public String getDataproducaostr() {
        return dataproducaostr;
    }

    /**
     * @param dataproducaostr the dataproducaostr to set
     */
    public void setDataproducaostr(String dataproducaostr) {
        this.dataproducaostr = dataproducaostr;
    }

    /**
     * @return the produto_descricao
     */
    public String getProduto_descricao() {
        return produto_descricao;
    }

    /**
     * @param produto_descricao the produto_descricao to set
     */
    public void setProduto_descricao(String produto_descricao) {
        this.produto_descricao = produto_descricao;
    }

    /**
     * @return the maquina
     */
    public String getMaquina() {
        return maquina;
    }

    /**
     * @param maquina the maquina to set
     */
    public void setMaquina(String maquina) {
        this.maquina = maquina;
    }

    public void setProduto(Produto produto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
