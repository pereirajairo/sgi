package br.com.sgi.interfaces;

import br.com.sgi.bean.Motorista;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceMotoristaDAO<T> {

   

    List<Motorista> getMotoristas(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Motorista getMotorista(String PESQUISA_POR, String PESQUISA) throws SQLException;

    
}
