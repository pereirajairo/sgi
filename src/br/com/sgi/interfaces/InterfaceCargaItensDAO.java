package br.com.sgi.interfaces;

import br.com.sgi.bean.CargaItens;
import br.com.sgi.bean.OrdemCompra;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceCargaItensDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

  

    boolean inserir(T t) throws SQLException;

    boolean gravarEmbalagem(T t) throws SQLException;

    List<CargaItens> getCargaItens(String PESQUISA_POR, String PESQUISA) throws SQLException;

    CargaItens getCargaItem(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
