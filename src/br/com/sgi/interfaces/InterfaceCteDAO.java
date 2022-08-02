package br.com.sgi.interfaces;

import br.com.sgi.bean.Cte;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceCteDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    boolean atualizarOC(T t, int qtdreg, int contador) throws SQLException;

    List<Cte> getCtes(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    List<Cte> getCtesAgrupado(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    Cte getCte(String PESQUISAR_POR, String PESQUISA) throws SQLException;
}
