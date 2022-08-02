package br.com.sgi.interfaces;

import br.com.sgi.bean.CargaItensImpureza;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceCargaItensImpurezaDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    List<CargaItensImpureza> getCargaItensImpurezas(String PESQUISA_POR, String PESQUISA) throws SQLException;

    CargaItensImpureza getCargaItensImpureza(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
