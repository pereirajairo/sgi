package br.com.sgi.interfaces;

import br.com.sgi.bean.BancoDados;
import java.sql.SQLException;

public interface InterfaceBancoDadoDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    BancoDados getBancoDados() throws SQLException;
}
