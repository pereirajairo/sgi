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
public class WebServiceParametro {

    private String ipWebService = "";
    private String portaWebService = "";
    private String usuario;
    private String senha;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getIpWebService() {
        return ipWebService;
    }

    public void setIpWebService(String ipWebService) {
        this.ipWebService = ipWebService;
    }

    public String getPortaWebService() {
        return portaWebService;
    }

    public void setPortaWebService(String portaWebService) {
        this.portaWebService = portaWebService;
    }

}
