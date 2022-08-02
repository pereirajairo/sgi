package br.com.sgi.interfaces;

import br.com.sgi.bean.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceUsuarioDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    List<Usuario> getUsuarios() throws SQLException;

    Usuario getUsuario(String login) throws SQLException;
}
