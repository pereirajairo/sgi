package br.com.sgi.interfaces;

import br.com.sgi.bean.Pedido;

import java.sql.SQLException;

public interface InterfaceProcessoERPDAO<T> {

    Pedido getPedido(String PESQUISA, String PESQUISA_POR) throws SQLException;

}
