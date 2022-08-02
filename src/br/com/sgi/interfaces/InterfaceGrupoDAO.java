package br.com.sgi.interfaces;

import br.com.sgi.bean.ClienteGrupo;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceGrupoDAO<T> {

    ClienteGrupo getGrupo(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<ClienteGrupo> getClienteGrupos(String PESQUISAR_POR, String PESQUISA) throws SQLException;
}
