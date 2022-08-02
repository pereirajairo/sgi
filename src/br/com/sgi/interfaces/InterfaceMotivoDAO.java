package br.com.sgi.interfaces;


import br.com.sgi.bean.Motivo;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceMotivoDAO<T> {

  

    List<Motivo> getMotivos() throws SQLException;

    Motivo getMotivo(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
