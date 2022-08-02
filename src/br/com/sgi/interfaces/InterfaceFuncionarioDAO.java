package br.com.sgi.interfaces;

import br.com.sgi.bean.Funcionario;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceFuncionarioDAO<T> {

    
    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;
    
    List<Funcionario> getFuncionarios(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Funcionario getFuncionario(String PESQUISA_POR, String PESQUISA) throws SQLException;
    
     List<Funcionario> getFuncionariosSapiens(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Funcionario getFuncionarioSapiens(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
