package br.com.sgi.interfaces;

import br.com.sgi.bean.OrdemCompra;
import br.com.sgi.bean.OrdemCompraItens;
import br.com.sgi.bean.Pedido;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceOdemCompraDAO<T> {

    List<OrdemCompraItens> getOrdemCompraItenss(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<OrdemCompraItens> getOrdemCompraColeta(String PESQUISA_POR, String PESQUISA, Pedido pedido) throws SQLException;

    OrdemCompraItens getOrdemCompraItens(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<OrdemCompra> getOrdemCompras(String PESQUISA_POR, String PESQUISA) throws SQLException;

    boolean alterar(OrdemCompra t) throws SQLException;

}
