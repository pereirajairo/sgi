package br.com.sgi.interfaces;

import br.com.sgi.bean.ComissaoTitulos;
import br.com.sgi.bean.ComissaoTitulosMovimentos;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceComissaoTitulosMovimentosDAO<T> {

    List<ComissaoTitulosMovimentos> getTitulos(String PESQUISAR_POR, String PESQUISA) throws SQLException;
    
    List<ComissaoTitulosMovimentos> getTitulosTipo(String PESQUISAR_POR, String PESQUISA) throws SQLException;
    
    List<ComissaoTitulosMovimentos> getTitulosPercentual(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    ComissaoTitulosMovimentos getTitulo(String PESQUISAR_POR, String PESQUISA) throws SQLException;
}
