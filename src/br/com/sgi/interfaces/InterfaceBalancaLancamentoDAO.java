package br.com.sgi.interfaces;

import br.com.sgi.bean.BalancaLancamento;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceBalancaLancamentoDAO<T> {
       
    boolean inserir(T t) throws SQLException;

    List<BalancaLancamento> getBalancaLancamentos(String PESQUISA_POR, String PESQUISA) throws SQLException;

    BalancaLancamento getBalancaLancamento(String PESQUISA_POR, String PESQUISA , String ORDER) throws SQLException;
    
    List<BalancaLancamento> getBalancaLancamentosAgrupados(String PESQUISA_POR, String PESQUISA) throws SQLException;
    
    List<BalancaLancamento> getBalancaLancamentosChumbo(String PESQUISA_POR, String PESQUISA) throws SQLException;

    BalancaLancamento getBalancaLancamentoChumbo(String PESQUISA_POR, String PESQUISA , String ORDER) throws SQLException;
}
