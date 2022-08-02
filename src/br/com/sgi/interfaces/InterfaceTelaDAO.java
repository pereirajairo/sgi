package br.com.sgi.interfaces;

import br.com.sgi.bean.Tela;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceTelaDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    List<Tela> getTelas(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Tela getTela(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
