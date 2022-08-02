package br.com.sgi.interfaces;

import br.com.sgi.bean.Balanca;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceBalancaDAO<T> {
       

    List<Balanca> getBalancas(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Balanca getBalanca(String PESQUISA_POR, String PESQUISA) throws SQLException;
    
}
