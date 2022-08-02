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
public class BasePasso {

    private final Map<String, String> passos;

    public BasePasso() {
        this.passos = new TreeMap<>();
        this.passos.put("1", "1-REGISTRO");
        this.passos.put("2", "2-FAZER PROPOSTA");
        this.passos.put("3", "3-GERAR PEDIDO");
        this.passos.put("4", "4-LIGAR NOVAMENTE");
    }

    public String getNome(String passo) {
        return getPassos().get(passo);
    }

    /**
     * @return the estados
     */
    public Map<String, String> getPassos() {
        return passos;
    }
}
