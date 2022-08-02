/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.bean;

import java.util.Map;
import java.util.TreeMap;
 
/**
 *
 * @author jairo.silva
 */
public class BaseEstado {

    private final Map<String, String> estados;

    public BaseEstado() {
        this.estados = new TreeMap<>();
        this.estados.put("AC", "AC");
        this.estados.put("AP", "AP");
        this.estados.put("AM", "AM");
        this.estados.put("BA", "BA");
        this.estados.put("CE", "CE");
        this.estados.put("DF", "DF");
        this.estados.put("ES", "ES");
        this.estados.put("GO", "GO");
        this.estados.put("MA", "MA");
        this.estados.put("MT", "MT");
        this.estados.put("MS", "MS");
        this.estados.put("MG", "MG");
        this.estados.put("PA", "PA");
        this.estados.put("PB", "PB");
        this.estados.put("PR", "PR");
        this.estados.put("PE", "PE");
        this.estados.put("PI", "PI");
        this.estados.put("RJ", "RJ");
        this.estados.put("RN", "RN");
        this.estados.put("RS", "RS");
        this.estados.put("RO", "RO");
        this.estados.put("RR", "RR");
        this.estados.put("SC", "SC");
        this.estados.put("SP", "SP");
        this.estados.put("SE", "SE");
        this.estados.put("AL", "AL");
        this.estados.put("TO", "TO");
    }
    
    
    

    public String getMessageFromBundle(final String key) {
//        FacesContext context = FacesContext.getCurrentInstance();
//        Application app = context.getApplication();
//        ResourceBundle bundle = app.getResourceBundle(context, "msg");
        // String msg = bundle.getString(key);
        String msg = "";
        return msg;
    }

    public String getNomeEstado(String uf) {
        return getEstados().get(uf);
    }

    /**
     * @return the estados
     */
    public Map<String, String> getEstados() {
        return estados;
    }
}
