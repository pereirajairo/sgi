package br.com.sgi.interfaces;

import br.com.sgi.bean.Fornecedor;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceFornecedorDAO<T> {

    List<Fornecedor> getFornecedors(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Fornecedor getFornecedor(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
