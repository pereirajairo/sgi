package br.com.sgi.interfaces;

import br.com.sgi.bean.WebServiceParametro;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceWebServiceParametroDAO<T> {

    List<WebServiceParametro> getWebServiceParametros(String PESQUISA_POR, String PESQUISA) throws SQLException;
  
    WebServiceParametro getWebServiceParametro() throws SQLException;
    
}
