package br.com.sgi.interfaces;

import br.com.sgi.bean.Caixa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceCaixaDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    boolean alterarEmuso(T t) throws SQLException;
      
    List<Caixa> getCaixas(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Caixa getCaixa(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
