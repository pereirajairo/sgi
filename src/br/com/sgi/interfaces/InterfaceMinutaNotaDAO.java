package br.com.sgi.interfaces;

import br.com.sgi.bean.MinutaNota;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceMinutaNotaDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t, Integer qtdreg, Integer contador) throws SQLException;

    List<MinutaNota> getMinutaNotas(String PESQUISA_POR, String PESQUISA) throws SQLException;

    MinutaNota getMinutaNota(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
