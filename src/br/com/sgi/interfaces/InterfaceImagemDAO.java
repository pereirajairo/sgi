package br.com.sgi.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface InterfaceImagemDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    List<T> getImagens(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    List<T> getImagensGarantias(String PESQUISAR_POR, String PESQUISA) throws SQLException;
    
    T getImagem(Long id) throws SQLException;

}
