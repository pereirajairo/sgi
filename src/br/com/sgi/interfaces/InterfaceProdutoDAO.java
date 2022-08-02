package br.com.sgi.interfaces;

import br.com.sgi.bean.Embalagem;
import br.com.sgi.bean.Produto;

import java.sql.SQLException;
import java.util.List;

public interface InterfaceProdutoDAO<T> {

    List<Produto> getProdutos(String familia) throws SQLException;

    Produto getProduto(Integer empresa, String produto) throws SQLException;

    Produto getProdutoLigacao(String PESQUISA, String PESQUISA_POR) throws SQLException;

    List<Embalagem> getEmbalagems() throws SQLException;

}
