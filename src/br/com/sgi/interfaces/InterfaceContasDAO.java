package br.com.sgi.interfaces;

import br.com.sgi.bean.Contas;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceContasDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean exportarERP(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    boolean gravarSolucao(T t) throws SQLException;

    List<Contas> getContas(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    Contas getConta(String PESQUISAR_POR, String PESQUISA) throws SQLException;
}
