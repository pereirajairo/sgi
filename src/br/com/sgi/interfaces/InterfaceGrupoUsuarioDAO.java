package br.com.sgi.interfaces;

import br.com.sgi.bean.GrupoUsuario;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceGrupoUsuarioDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    List<GrupoUsuario> getGrupoUsuarios(String PESQUISA_POR, String PESQUISA) throws SQLException;

    GrupoUsuario getGrupoUsuario(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
