package br.com.sgi.interfaces;

import br.com.sgi.bean.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceClienteDAO<T> {

    List<Cliente> getClientes(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<Cliente> getClientesMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Cliente getCliente(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Integer QuatidadeTituloVencido(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Cliente getClienteMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Cliente getClienteSucata(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<Cliente> getClientesSucata(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
