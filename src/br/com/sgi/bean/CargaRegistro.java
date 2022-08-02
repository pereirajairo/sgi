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
public class CargaRegistro {

    private Integer empresa = 1;
    private Integer filial = 1;
    private Integer numerocarga = 0;
    private Integer fornecedor = 0;
    private String placa;
    private String nomeMotorista;
    private String documentMotorista;
    private Double pesoEntrada = 0.0;
    private Date dataEntrada;
    private String dataEntradaS;
    private String horaEntradaS;
    private Integer horaEntrada;

    private Double pesoSaida = 0.0;
    private Date dataSaida;
    private String dataSaidaS;
    private Integer horaSaida;
    private String horaSaidaS;

    private Double pesoVeiculo = 0.0;

    private Double pesoDescontoEmbalagens = 0.0;

    // calcular o desconto de agua, ferro e outros
    private Double percentualDescontoImpureza = 0.0;
    private Double pesoDescontoImpureza = 0.0;
    private Double pesoEmbalagen = 0.0;

    private Double pesoLiquidoCarga = 0.0;

    private String observacaoCarga;
    private Integer usuarioCadastro;
    private Integer usuarioAlteracao;
    private Integer horaAlteracao;
    private Date dataAlteracao;
    private String situacaoCarga="A";
    private String tipoCarga;

    private String ordemCompra;
    private String enviarEmial = "N";
    
    private String infoMinuta;
    
    
    private Integer codigoTransportadora = 0;
    private Transportadora transportadora;

    public String getEnviarEmial() {
        return enviarEmial;
    }

    public void setEnviarEmial(String enviarEmial) {
        this.enviarEmial = enviarEmial;
    }

    
    
    public Transportadora getTransportadora() {
        return transportadora;
    }

    public void setTransportadora(Transportadora transportadora) {
        this.transportadora = transportadora;
    }
    
    

    public Integer getCodigoTransportadora() {
        return codigoTransportadora;
    }

    public void setCodigoTransportadora(Integer codigoTransportadora) {
        this.codigoTransportadora = codigoTransportadora;
    }
    

    public Double getPercentualDescontoImpureza() {
        return percentualDescontoImpureza;
    }

    public void setPercentualDescontoImpureza(Double percentualDescontoImpureza) {
        this.percentualDescontoImpureza = percentualDescontoImpureza;
    }

    public Double getPesoDescontoImpureza() {
        return pesoDescontoImpureza;
    }

    public void setPesoDescontoImpureza(Double pesoDescontoImpureza) {
        this.pesoDescontoImpureza = pesoDescontoImpureza;
    }

    public Double getPesoEmbalagen() {
        return pesoEmbalagen;
    }

    public void setPesoEmbalagen(Double pesoEmbalagen) {
        this.pesoEmbalagen = pesoEmbalagen;
    }

    public String getOrdemCompra() {
        return ordemCompra;
    }

    public void setOrdemCompra(String ordemCompra) {
        this.ordemCompra = ordemCompra;
    }

    public Double getPesoLiquidoCarga() {
        return pesoLiquidoCarga;
    }

    public void setPesoLiquidoCarga(Double pesoLiquidoCarga) {
        this.pesoLiquidoCarga = pesoLiquidoCarga;
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

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getNomeMotorista() {
        return nomeMotorista;
    }

    public void setNomeMotorista(String nomeMotorista) {
        this.nomeMotorista = nomeMotorista;
    }

    public String getDocumentMotorista() {
        return documentMotorista;
    }

    public void setDocumentMotorista(String documentMotorista) {
        this.documentMotorista = documentMotorista;
    }

    public Double getPesoEntrada() {
        return pesoEntrada;
    }

    public void setPesoEntrada(Double pesoEntrada) {
        this.pesoEntrada = pesoEntrada;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getDataEntradaS() {
        return dataEntradaS;
    }

    public void setDataEntradaS(String dataEntradaS) {
        this.dataEntradaS = dataEntradaS;
    }

    public String getHoraEntradaS() {
        return horaEntradaS;
    }

    public void setHoraEntradaS(String horaEntradaS) {
        this.horaEntradaS = horaEntradaS;
    }

    public Integer getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(Integer horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Double getPesoSaida() {
        return pesoSaida;
    }

    public void setPesoSaida(Double pesoSaida) {
        this.pesoSaida = pesoSaida;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getDataSaidaS() {
        return dataSaidaS;
    }

    public void setDataSaidaS(String dataSaidaS) {
        this.dataSaidaS = dataSaidaS;
    }

    public Integer getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(Integer horaSaida) {
        this.horaSaida = horaSaida;
    }

    public String getHoraSaidaS() {
        return horaSaidaS;
    }

    public void setHoraSaidaS(String horaSaidaS) {
        this.horaSaidaS = horaSaidaS;
    }

    public Double getPesoVeiculo() {
        return pesoVeiculo;
    }

    public void setPesoVeiculo(Double pesoVeiculo) {
        this.pesoVeiculo = pesoVeiculo;
    }

    public Double getPesoDescontoEmbalagens() {
        return pesoDescontoEmbalagens;
    }

    public void setPesoDescontoEmbalagens(Double pesoDescontoEmbalagens) {
        this.pesoDescontoEmbalagens = pesoDescontoEmbalagens;
    }

    public String getObservacaoCarga() {
        return observacaoCarga;
    }

    public void setObservacaoCarga(String observacaoCarga) {
        this.observacaoCarga = observacaoCarga;
    }

    public Integer getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(Integer usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public Integer getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(Integer usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public Integer getHoraAlteracao() {
        return horaAlteracao;
    }

    public void setHoraAlteracao(Integer horaAlteracao) {
        this.horaAlteracao = horaAlteracao;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getSituacaoCarga() {
        return situacaoCarga;
    }

    public void setSituacaoCarga(String situacaoCarga) {
        this.situacaoCarga = situacaoCarga;
    }

    public String getTipoCarga() {
        return tipoCarga;
    }

    public void setTipoCarga(String tipoCarga) {
        this.tipoCarga = tipoCarga;
    }

    public String getInfoMinuta() {
        return infoMinuta;
    }

    public void setInfoMinuta(String infoMinuta) {
        this.infoMinuta = infoMinuta;
    }

    
    
}
