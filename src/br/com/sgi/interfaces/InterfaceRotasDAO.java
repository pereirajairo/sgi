package br.com.sgi.interfaces;


import br.com.sgi.bean.Rotas;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceRotasDAO<T> {

    
    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    boolean gravarSolucao(T t) throws SQLException;

    List<Rotas> getRotas(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    Rotas getRota(String PESQUISAR_POR, String PESQUISA) throws SQLException;
}
