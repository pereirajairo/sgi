package br.com.sgi.interfaces;

import br.com.sgi.bean.Preco;
import br.com.sgi.bean.Produto;
import java.sql.SQLException;

public interface InterfacePrecoDAO<T> {

    Preco getPreco(Produto produto) throws SQLException;
}
