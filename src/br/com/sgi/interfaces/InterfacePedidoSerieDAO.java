package br.com.sgi.interfaces;

import br.com.sgi.bean.MinutaPedido;
import br.com.sgi.bean.PedidoHub;
import br.com.sgi.bean.PedidoHubProduto;
import br.com.sgi.bean.PedidoHubSerie;
import java.sql.SQLException;
import java.util.List;

public interface InterfacePedidoSerieDAO<T> {

    List<PedidoHub> getPedidoHubs(String PESQUISA_POR, String PESQUISA) throws SQLException;

    boolean inserir(PedidoHub t, int qtdreg, int contador) throws SQLException;

    boolean inserirProduto(PedidoHub t, List<PedidoHubProduto> lista, int qtdreg, int contador) throws SQLException;

    boolean liberarParaEntrega(MinutaPedido t, int qtdreg, int contador) throws SQLException;

    //
    List<PedidoHubSerie> getPedidoHubSeries(String PESQUISA_POR, String PESQUISA) throws SQLException;

    boolean atualizar(T t, int qtdreg, int contador) throws SQLException;

    boolean integrarPedidoApp(List<PedidoHubProduto> lista) throws SQLException;

    boolean atualizarSerieIntegrada(PedidoHubProduto ph) throws SQLException;

    boolean integrarSerieERP(List<PedidoHubSerie> lista) throws SQLException;

    List<PedidoHub> getPedidoHubsSucata(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
