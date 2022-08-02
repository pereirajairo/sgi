package br.com.sgi.interfaces;


import br.com.sgi.bean.RamoAtividade;

import java.sql.SQLException;
import java.util.List;

public interface InterfaceRamoAtividadeDAO<T> {

    List<RamoAtividade> getRamoAtividades() throws SQLException;

}
