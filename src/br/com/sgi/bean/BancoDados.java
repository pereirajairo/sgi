/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

/**
 *
 * @author jairosilva
 */
public class BancoDados {
    private static final long serialVersionUID = 2606254772949774110L;
    private Integer id;
    private String database_bancotype_banco;

    private String user_banco;
    private String password_banco;
    private String driver_banco;
    private String database_banco;
    private String situacao_banco;
    private String host_banco;
    private String schema_banco;
    private String conexao_name;
    private String porta;

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
     * @return the database_bancotype_banco
     */
    public String getDatabasetype() {
        return database_bancotype_banco;
    }

    /**
     * @param database_bancotype_banco the database_bancotype_banco to set
     */
    public void setDatabasetype(String database_bancotype_banco) {
        this.database_bancotype_banco = database_bancotype_banco;
    }

    /**
     * @return the user_banco
     */
    public String getUser() {
        return user_banco;
    }

    /**
     * @param user_banco the user_banco to set
     */
    public void setUser(String user_banco) {
        this.user_banco = user_banco;
    }

    /**
     * @return the password_banco
     */
    public String getPassword() {
        return password_banco;
    }

    /**
     * @param password_banco the password_banco to set
     */
    public void setPassword(String password_banco) {
        this.password_banco = password_banco;
    }

    /**
     * @return the driver_banco
     */
    public String getDriver() {
        return driver_banco;
    }

    /**
     * @param driver_banco the driver_banco to set
     */
    public void setDriver(String driver_banco) {
        this.driver_banco = driver_banco;
    }

    /**
     * @return the database_banco
     */
    public String getDatabase() {
        return database_banco;
    }

    /**
     * @param database_banco the database_banco to set
     */
    public void setDatabase(String database_banco) {
        this.database_banco = database_banco;
    }

    /**
     * @return the situacao_banco
     */
    public String getSituacao() {
        return situacao_banco;
    }

    /**
     * @param situacao_banco the situacao_banco to set
     */
    public void setSituacao(String situacao_banco) {
        this.situacao_banco = situacao_banco;
    }

    /**
     * @return the host_banco
     */
    public String getHost() {
        return host_banco;
    }

    /**
     * @param host_banco the host_banco to set
     */
    public void setHost(String host_banco) {
        this.host_banco = host_banco;
    }

    /**
     * @return the schema_banco
     */
    public String getSchema() {
        return schema_banco;
    }

    /**
     * @param schema_banco the schema_banco to set
     */
    public void setSchema(String schema_banco) {
        this.schema_banco = schema_banco;
    }

    /**
     * @return the conexao_name
     */
    public String getConexao_name() {
        return conexao_name;
    }

    /**
     * @param conexao_name the conexao_name to set
     */
    public void setConexao_name(String conexao_name) {
        this.conexao_name = conexao_name;
    }

}
