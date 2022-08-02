package br.com.sgi.interfaces;

import br.com.sgi.bean.ControlePesoChumbo;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceControlePesoChumboDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    List<ControlePesoChumbo> getControlePesoChumbos(String PESQUISA_POR, String PESQUISA) throws SQLException;

    ControlePesoChumbo getControlePesoChumbo(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
