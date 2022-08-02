package br.com.sgi.interfaces;

import br.com.sgi.bean.PedidoHub;
import br.com.sgi.bean.PedidoHubProduto;
import java.sql.SQLException;
import java.util.List;

public interface InterfacePedidoHubDAO<T> {

    // Busca os pedidos do ERP
    List<PedidoHubProduto> getPedidoProdutos(String PESQUISA_POR, String PESQUISA) throws SQLException;

    // Integra com o HUB
    public boolean integrarPedidoApp(PedidoHub t, int qtdreg, int contador) throws SQLException;

    boolean integrarPedidoProdutoApp(List<PedidoHubProduto> lista) throws SQLException;
}
