package br.com.sgi.interfaces;

import br.com.sgi.bean.Representante;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceRepresentanteDAO<T> {

    List<Representante> getRepresentantes(String PESQUISA_POR, String PESQUISA) throws SQLException;
    
     List<Representante> getRepresentantesHub(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Representante getRepresentante(String PESQUISA_POR, String PESQUISA) throws SQLException;


}
