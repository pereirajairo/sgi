package br.com.sgi.interfaces;

import br.com.sgi.bean.GarantiaPortal;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceGarantiaPortalDAO<T> {

    boolean alterar(T t) throws SQLException;

    boolean liberar(T t, int contador, int qtdreg) throws SQLException;

    List<GarantiaPortal> getGarantiaPortals(String PESQUISA_POR, String PESQUISA) throws SQLException;

 

    List<GarantiaPortal> getGarantiaPortalsItens(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<GarantiaPortal> getGarantiaReceber(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
