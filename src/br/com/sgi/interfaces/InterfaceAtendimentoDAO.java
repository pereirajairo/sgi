package br.com.sgi.interfaces;


import br.com.sgi.bean.Atendimento;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceAtendimentoDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    boolean gravarSolucao(T t) throws SQLException;

    List<Atendimento> getAtendimentos(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    Atendimento getAtendimento(String PESQUISAR_POR, String PESQUISA) throws SQLException;
}
