package br.com.sgi.interfaces;

import br.com.sgi.bean.Pedido;

import java.sql.SQLException;
import java.util.List;

public interface InterfacePedidoDAO<T> {

    List<Pedido> getPedidos(String PESQUISA, String PESQUISA_POR) throws SQLException;

    List<Pedido> getPedidosExpedicao(String PESQUISA, String PESQUISA_POR) throws SQLException;

    List<Pedido> getPedidosExpedicaoSemPre(String PESQUISA, String PESQUISA_POR) throws SQLException;

    List<Pedido> getPedidosExpedicaoGeral(String PESQUISA, String PESQUISA_POR) throws SQLException;

    List<Pedido> getPedidosExpedicaoMetais(String PESQUISA, String PESQUISA_POR) throws SQLException;

    List<Pedido> getPedidosGerais(String PESQUISA, String PESQUISA_POR) throws SQLException;

    Pedido getPedido(String PESQUISA, String PESQUISA_POR) throws SQLException;

    Pedido getPedidoExpedicao(String PESQUISA, String PESQUISA_POR) throws SQLException;

    List<Pedido> getPedidosIndustrializacao(String PESQUISA, String PESQUISA_POR) throws SQLException;

    boolean liberarPedido(T t) throws SQLException;

    boolean AgendarDataPedido(T t) throws SQLException;

    boolean AtualizarMinuta(T t) throws SQLException;

    boolean removerMinutaPedido(T t) throws SQLException;

}
