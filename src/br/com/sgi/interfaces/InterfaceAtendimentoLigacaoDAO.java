package br.com.sgi.interfaces;


import br.com.sgi.bean.AtendimentoLigacao;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceAtendimentoLigacaoDAO<T> {

    boolean remover(T t) throws SQLException;
    
     boolean deletar(Integer id, String tipocaonta) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

 
    List<AtendimentoLigacao> getAtendimentoLigacaos(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    AtendimentoLigacao getAtendimentoLigacao(String PESQUISAR_POR, String PESQUISA) throws SQLException;
}
