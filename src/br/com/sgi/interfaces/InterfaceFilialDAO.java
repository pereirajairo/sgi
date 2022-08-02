package br.com.sgi.interfaces;

import br.com.sgi.bean.Filial;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceFilialDAO<T> {

    List<Filial> getFilias(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Filial getFilia(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
