package br.com.sgi.interfaces;


import br.com.sgi.bean.SucataPDV;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceSucataPDVDao<T> {

    boolean remover(T t) throws SQLException;    

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

 
    List<SucataPDV> getSucataPDVs(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    SucataPDV getSucataPDV(String PESQUISAR_POR, String PESQUISA) throws SQLException;
}
