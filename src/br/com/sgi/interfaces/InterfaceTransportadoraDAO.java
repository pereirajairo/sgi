package br.com.sgi.interfaces;

import br.com.sgi.bean.Transportadora;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceTransportadoraDAO<T> {

   

    List<Transportadora> getTransportadoras(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Transportadora getTransportadora(String PESQUISA_POR, String PESQUISA) throws SQLException;

    
}
