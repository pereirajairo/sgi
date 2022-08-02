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
public class AtendimentoLigacao {

    private Integer codigocliente = 0; // cliente
    private Integer codigolancamento = 0; // sequencial
    private Integer codigoatendimento = 0; // codigo do atendimento
    private Date dataligacao;
    private String dataligcaoS;
    private String descricaoligacao;
    private Integer usuario;
    private Integer vendedor;
    private Integer horaligacao = 0;
    private String horaligacaoS;
    private String tipoconta; // cliente ou lead
    private String convertido; // cliente ou lead
    private Date dataconversao;
    private Integer pedido;

    public String getDataligcaoS() {
        return dataligcaoS;
    }

    public void setDataligcaoS(String dataligcaoS) {
        this.dataligcaoS = dataligcaoS;
    }

    public String getHoraligacaoS() {
        return horaligacaoS;
    }

    public void setHoraligacaoS(String horaligacaoS) {
        this.horaligacaoS = horaligacaoS;
    }

    
    
    
    
    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }
    
    

    public Integer getCodigocliente() {
        return codigocliente;
    }

    public void setCodigocliente(Integer codigocliente) {
        this.codigocliente = codigocliente;
    }

    public Integer getCodigolancamento() {
        return codigolancamento;
    }

    public void setCodigolancamento(Integer codigolancamento) {
        this.codigolancamento = codigolancamento;
    }

    public Integer getCodigoatendimento() {
        return codigoatendimento;
    }

    public void setCodigoatendimento(Integer codigoatendimento) {
        this.codigoatendimento = codigoatendimento;
    }

    public Date getDataligacao() {
        return dataligacao;
    }

    public void setDataligacao(Date dataligacao) {
        this.dataligacao = dataligacao;
    }

    public String getDescricaoligacao() {
        return descricaoligacao;
    }

    public void setDescricaoligacao(String descricaoligacao) {
        this.descricaoligacao = descricaoligacao;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public Integer getVendedor() {
        return vendedor;
    }

    public void setVendedor(Integer vendedor) {
        this.vendedor = vendedor;
    }

    public Integer getHoraligacao() {
        return horaligacao;
    }

    public void setHoraligacao(Integer horaligacao) {
        this.horaligacao = horaligacao;
    }

    public String getTipoconta() {
        return tipoconta;
    }

    public void setTipoconta(String tipoconta) {
        this.tipoconta = tipoconta;
    }

    public String getConvertido() {
        return convertido;
    }

    public void setConvertido(String convertido) {
        this.convertido = convertido;
    }

    public Date getDataconversao() {
        return dataconversao;
    }

    public void setDataconversao(Date dataconversao) {
        this.dataconversao = dataconversao;
    }
    
    

}
