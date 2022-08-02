package br.com.sgi.interfaces;

import br.com.sgi.bean.Garantia;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceGarantiaDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    List<Garantia> getGarantias(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Garantia getGarantia(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
