/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.interfaces;

import br.com.sgi.bean.Embalagem;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author matheus.luiz
 */
public interface InterfaceEmbalagemDAO<T> {
    

    List <Embalagem> getEmbalagems() throws SQLException;

    Embalagem getEmbalagem(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
