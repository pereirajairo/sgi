package br.com.sgi.interfaces;

import br.com.sgi.bean.ComissaoTitulos;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceComissaoTitulosDAO<T> {

    List<ComissaoTitulos> getTitulos(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    ComissaoTitulos getTitulo(String PESQUISAR_POR, String PESQUISA) throws SQLException;
}
