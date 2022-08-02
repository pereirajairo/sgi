package br.com.sgi.interfaces;

import br.com.sgi.bean.BalancaParametro;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceBalancaParametroDAO<T> {

    List<BalancaParametro> getBalancaParametros(String PESQUISA_POR, String PESQUISA) throws SQLException;
  
    BalancaParametro getBalancaParametro() throws SQLException;
    
}
