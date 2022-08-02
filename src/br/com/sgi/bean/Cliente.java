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
public class Cliente {

    private Integer empresa = 0;
    private Integer filial = 0;
    private Integer codigo_cliente = 0;

    private String nome;
    private String cidade;
    private String estado;
    private String endereco;

    private String fantasia;
    private String numero;
    private String cpfcnpj;

    private String bairro;
    private String cep;
    private String telefone;
    private String email;
    private String grupo_empresa;
    private Integer representante = 0;

    private Integer id = 0;
    private Integer tabela_id = 0;
    private Integer grupocliente_id = 0;
    private Integer representante_id = 0;
    private Integer empresa_id = 0;
    private Integer filial_id = 0;

    private String origem;
    private Date data_ultimo_faturamento;
    private String data_ultimo_faturamentoS;
    private String situacao;
    private double dias_ultimo_faturamento = 0.0;

    private String pedido = "0";
    private Double quatidade_fat_ano = 0.0;
    private String ligar;
    private Integer quantidade_atendimento = 0;

    private Integer codigo = 0;

    private String grupocodigo = "0";

    private String gruponome = "NÃO INFORMADO";

    private ClienteGrupo CadClienteGrupo;

    private Representante CadRepresentante;

    private Vendedor CadVendedor;

    public Cliente() {

    }

    public Cliente(Integer codigo, String nome, String estado) {
        this.codigo = codigo;
        this.nome = nome;
        this.estado = estado;
    }

    public Cliente(Integer codigo, String nome, String estado, String cidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.estado = estado;
        this.cidade = cidade;
    }

    public Representante getCadRepresentante() {
        return CadRepresentante;
    }

    public void setCadRepresentante(Representante CadRepresentante) {
        this.CadRepresentante = CadRepresentante;
    }

    public ClienteGrupo getCadClienteGrupo() {
        return CadClienteGrupo;
    }

    public void setCadClienteGrupo(ClienteGrupo CadClienteGrupo) {
        this.CadClienteGrupo = CadClienteGrupo;
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

    public Integer getCodigo_cliente() {
        return codigo_cliente;
    }

    public void setCodigo_cliente(Integer codigo_cliente) {
        this.codigo_cliente = codigo_cliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCpfcnpj() {
        return cpfcnpj;
    }

    public void setCpfcnpj(String cpfcnpj) {
        this.cpfcnpj = cpfcnpj;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGrupo_empresa() {
        return grupo_empresa;
    }

    public void setGrupo_empresa(String grupo_empresa) {
        this.grupo_empresa = grupo_empresa;
    }

    public Integer getRepresentante() {
        return representante;
    }

    public void setRepresentante(Integer representante) {
        this.representante = representante;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTabela_id() {
        return tabela_id;
    }

    public void setTabela_id(Integer tabela_id) {
        this.tabela_id = tabela_id;
    }

    public Integer getGrupocliente_id() {
        return grupocliente_id;
    }

    public void setGrupocliente_id(Integer grupocliente_id) {
        this.grupocliente_id = grupocliente_id;
    }

    public Integer getRepresentante_id() {
        return representante_id;
    }

    public void setRepresentante_id(Integer representante_id) {
        this.representante_id = representante_id;
    }

    public Integer getEmpresa_id() {
        return empresa_id;
    }

    public void setEmpresa_id(Integer empresa_id) {
        this.empresa_id = empresa_id;
    }

    public Integer getFilial_id() {
        return filial_id;
    }

    public void setFilial_id(Integer filial_id) {
        this.filial_id = filial_id;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public Date getData_ultimo_faturamento() {
        return data_ultimo_faturamento;
    }

    public void setData_ultimo_faturamento(Date data_ultimo_faturamento) {
        this.data_ultimo_faturamento = data_ultimo_faturamento;
    }

    public String getData_ultimo_faturamentoS() {
        return data_ultimo_faturamentoS;
    }

    public void setData_ultimo_faturamentoS(String data_ultimo_faturamentoS) {
        this.data_ultimo_faturamentoS = data_ultimo_faturamentoS;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public double getDias_ultimo_faturamento() {
        return dias_ultimo_faturamento;
    }

    public void setDias_ultimo_faturamento(double dias_ultimo_faturamento) {
        this.dias_ultimo_faturamento = dias_ultimo_faturamento;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getGrupocodigo() {
        return grupocodigo;
    }

    public void setGrupocodigo(String grupocodigo) {
        this.grupocodigo = grupocodigo;
    }

    public String getGruponome() {
        return gruponome;
    }

    public void setGruponome(String gruponome) {
        this.gruponome = gruponome;
    }

    public Vendedor getCadVendedor() {
        return CadVendedor;
    }

    public void setCadVendedor(Vendedor CadVendedor) {
        this.CadVendedor = CadVendedor;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public Double getQuatidade_fat_ano() {
        return quatidade_fat_ano;
    }

    public void setQuatidade_fat_ano(Double quatidade_fat_ano) {
        this.quatidade_fat_ano = quatidade_fat_ano;
    }

    public String getLigar() {
        return ligar;
    }

    public void setLigar(String ligar) {
        this.ligar = ligar;
    }

    public Integer getQuantidade_atendimento() {
        return quantidade_atendimento;
    }

    public void setQuantidade_atendimento(Integer quantidade_atendimento) {
        this.quantidade_atendimento = quantidade_atendimento;
    }

   
    

}
