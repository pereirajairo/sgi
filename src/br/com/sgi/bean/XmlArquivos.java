/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Date;

/**
 *
 * @author jairosilva
 */
public class XmlArquivos {

    private String caminho;
    private String filename;
    private String lastmodified;
    private String fileread;
    private Date lastdate;
    private String arqimp;

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the lastmodified
     */
    public String getLastmodified() {
        return lastmodified;
    }

    /**
     * @param lastmodified the lastmodified to set
     */
    public void setLastmodified(String lastmodified) {
        this.lastmodified = lastmodified;
    }

    /**
     * @return the fileread
     */
    public String getFileread() {
        return fileread;
    }

    /**
     * @param fileread the fileread to set
     */
    public void setFileread(String fileread) {
        this.fileread = fileread;
    }

    /**
     * @return the lastdate
     */
    public Date getLastdate() {
        return lastdate;
    }

    /**
     * @param lastdate the lastdate to set
     */
    public void setLastdate(Date lastdate) {
        this.lastdate = lastdate;
    }

    /**
     * @return the arqimp
     */
    public String getArqimp() {
        return arqimp;
    }

    /**
     * @param arqimp the arqimp to set
     */
    public void setArqimp(String arqimp) {
        this.arqimp = arqimp;
    }

    /**
     * @return the caminho
     */
    public String getCaminho() {
        return caminho;
    }

    /**
     * @param caminho the caminho to set
     */
    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
}
