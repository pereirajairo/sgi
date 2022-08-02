package br.com.sgi.interfaces;

import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceMinutaPedidoDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean gravarDadosMinuta(Minuta t) throws SQLException;

    boolean inserir(T t, Integer qtdreg, Integer contador) throws SQLException;

    List<MinutaPedido> getMinutaPedidos(String PESQUISA_POR, String PESQUISA) throws SQLException;

    MinutaPedido getMinutaPedido(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
