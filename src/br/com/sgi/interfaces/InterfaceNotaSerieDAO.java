package br.com.sgi.interfaces;


import br.com.sgi.bean.NotaSerie;

import java.sql.SQLException;
import java.util.List;

public interface InterfaceNotaSerieDAO<T> {

   
    List<NotaSerie> getNotaSeries(String pesquisa_por, String pesquisa) throws SQLException;

    boolean integrarNotaSerieApp(List<NotaSerie> lista) throws SQLException;

    boolean atualizarNotaSerieIntegrada(NotaSerie ph) throws SQLException;


}
