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
public class Contas {

    private Integer usu_codcli = 0;
    private String usu_nomcli;
    private String usu_apecli;
    private String usu_cgccpf;
    private String usu_endcli;
    private String usu_cidcli;
    private String usu_baicli;
    private String usu_concli; // Contato
    private String usu_sigufs;
    private String usu_telcli;
    private String usu_emacli;
    private String usu_tipcon; // lead, suspect, prospec 
    private String usu_obscli;
    private String usu_sitcon;  // ativo, inativo
    private Integer usu_coderp = 0;
    private Integer usu_codusu = 0;
    private Date usu_datcad;
     private String usu_datcadS;
    private Integer usu_motobs = 0;
    private Integer usu_horcad = 0;
    private String usu_solobs;
    private String usu_codram;
    private String usu_segatu; // Auto
    private String usu_numend;

    private Integer usu_codrep = 0;
    private Integer usu_codven = 0;
    private String usu_envema;
    private String usu_emapar;
    
    private Date usu_datdis; // Data de Retorno
    private String usu_datdisS; // Data de Retorno
    
    
    private String usu_experp = "N";
     private String usu_insest = "";

    public String getUsu_insest() {
        return usu_insest;
    }

    public void setUsu_insest(String usu_insest) {
        this.usu_insest = usu_insest;
    }

     
     
    public String getUsu_experp() {
        return usu_experp;
    }

    public void setUsu_experp(String usu_experp) {
        this.usu_experp = usu_experp;
    }
    
    
    
    private Usuario CadUsuario;

    public Date getUsu_datdis() {
        return usu_datdis;
    }

    public void setUsu_datdis(Date usu_datdis) {
        this.usu_datdis = usu_datdis;
    }

    public String getUsu_datdisS() {
        return usu_datdisS;
    }

    public void setUsu_datdisS(String usu_datdisS) {
        this.usu_datdisS = usu_datdisS;
    }

    
    
    
    public String getUsu_datcadS() {
        return usu_datcadS;
    }

    public void setUsu_datcadS(String usu_datcadS) {
        this.usu_datcadS = usu_datcadS;
    }

    public Usuario getCadUsuario() {
        return CadUsuario;
    }

    public void setCadUsuario(Usuario CadUsuario) {
        this.CadUsuario = CadUsuario;
    }
    
    
    
    
    
    private Representante CadRepresentante;

    public Representante getCadRepresentante() {
        return CadRepresentante;
    }

    public void setCadRepresentante(Representante CadRepresentante) {
        this.CadRepresentante = CadRepresentante;
    }
    
    
            

    public String getUsu_envema() {
        return usu_envema;
    }

    public void setUsu_envema(String usu_envema) {
        this.usu_envema = usu_envema;
    }

    public String getUsu_emapar() {
        return usu_emapar;
    }

    public void setUsu_emapar(String usu_emapar) {
        this.usu_emapar = usu_emapar;
    }

    public Integer getUsu_codrep() {
        return usu_codrep;
    }

    public void setUsu_codrep(Integer usu_codrep) {
        this.usu_codrep = usu_codrep;
    }

    public Integer getUsu_codven() {
        return usu_codven;
    }

    public void setUsu_codven(Integer usu_codven) {
        this.usu_codven = usu_codven;
    }

    private RamoAtividade CadramoAtividade;

    public RamoAtividade getCadramoAtividade() {
        return CadramoAtividade;
    }

    public void setCadramoAtividade(RamoAtividade CadramoAtividade) {
        this.CadramoAtividade = CadramoAtividade;
    }

    public String getUsu_numend() {
        return usu_numend;
    }

    public void setUsu_numend(String usu_numend) {
        this.usu_numend = usu_numend;
    }

    private Motivo CadMotivo;

    public Integer getUsu_codcli() {
        return usu_codcli;
    }

    public void setUsu_codcli(Integer usu_codcli) {
        this.usu_codcli = usu_codcli;
    }

    public String getUsu_nomcli() {
        return usu_nomcli;
    }

    public void setUsu_nomcli(String usu_nomcli) {
        this.usu_nomcli = usu_nomcli;
    }

    public String getUsu_apecli() {
        return usu_apecli;
    }

    public void setUsu_apecli(String usu_apecli) {
        this.usu_apecli = usu_apecli;
    }

    public String getUsu_cgccpf() {
        return usu_cgccpf;
    }

    public void setUsu_cgccpf(String usu_cgccpf) {
        this.usu_cgccpf = usu_cgccpf;
    }

    public String getUsu_endcli() {
        return usu_endcli;
    }

    public void setUsu_endcli(String usu_endcli) {
        this.usu_endcli = usu_endcli;
    }

    public String getUsu_cidcli() {
        return usu_cidcli;
    }

    public void setUsu_cidcli(String usu_cidcli) {
        this.usu_cidcli = usu_cidcli;
    }

    public String getUsu_baicli() {
        return usu_baicli;
    }

    public void setUsu_baicli(String usu_baicli) {
        this.usu_baicli = usu_baicli;
    }

    public String getUsu_concli() {
        return usu_concli;
    }

    public void setUsu_concli(String usu_concli) {
        this.usu_concli = usu_concli;
    }

    public String getUsu_sigufs() {
        return usu_sigufs;
    }

    public void setUsu_sigufs(String usu_sigufs) {
        this.usu_sigufs = usu_sigufs;
    }

    public String getUsu_telcli() {
        return usu_telcli;
    }

    public void setUsu_telcli(String usu_telcli) {
        this.usu_telcli = usu_telcli;
    }

    public String getUsu_emacli() {
        return usu_emacli;
    }

    public void setUsu_emacli(String usu_emacli) {
        this.usu_emacli = usu_emacli;
    }

    public String getUsu_tipcon() {
        return usu_tipcon;
    }

    public void setUsu_tipcon(String usu_tipcon) {
        this.usu_tipcon = usu_tipcon;
    }

    public String getUsu_obscli() {
        return usu_obscli;
    }

    public void setUsu_obscli(String usu_obscli) {
        this.usu_obscli = usu_obscli;
    }

    public String getUsu_sitcon() {
        return usu_sitcon;
    }

    public void setUsu_sitcon(String usu_sitcon) {
        this.usu_sitcon = usu_sitcon;
    }

    public Integer getUsu_coderp() {
        return usu_coderp;
    }

    public void setUsu_coderp(Integer usu_coderp) {
        this.usu_coderp = usu_coderp;
    }

    public Integer getUsu_codusu() {
        return usu_codusu;
    }

    public void setUsu_codusu(Integer usu_codusu) {
        this.usu_codusu = usu_codusu;
    }

    public Date getUsu_datcad() {
        return usu_datcad;
    }

    public void setUsu_datcad(Date usu_datcad) {
        this.usu_datcad = usu_datcad;
    }

    public Integer getUsu_motobs() {
        return usu_motobs;
    }

    public void setUsu_motobs(Integer usu_motobs) {
        this.usu_motobs = usu_motobs;
    }

    public Integer getUsu_horcad() {
        return usu_horcad;
    }

    public void setUsu_horcad(Integer usu_horcad) {
        this.usu_horcad = usu_horcad;
    }

    public String getUsu_solobs() {
        return usu_solobs;
    }

    public void setUsu_solobs(String usu_solobs) {
        this.usu_solobs = usu_solobs;
    }

    public String getUsu_codram() {
        return usu_codram;
    }

    public void setUsu_codram(String usu_codram) {
        this.usu_codram = usu_codram;
    }

    public String getUsu_segatu() {
        return usu_segatu;
    }

    public void setUsu_segatu(String usu_segatu) {
        this.usu_segatu = usu_segatu;
    }

    public Motivo getCadMotivo() {
        return CadMotivo;
    }

    public void setCadMotivo(Motivo CadMotivo) {
        this.CadMotivo = CadMotivo;
    }

}
