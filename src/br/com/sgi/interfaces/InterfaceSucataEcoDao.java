package br.com.sgi.interfaces;


import br.com.sgi.bean.SucataEco;
import br.com.sgi.bean.SucataEcoParametros;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceSucataEcoDao<T> {

    boolean remover(T t) throws SQLException;
    
    boolean deletar(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

 
    List<SucataEco> getSucataEcos(String PESQUISAR_POR, String PESQUISA) throws SQLException;

    SucataEco getSucataEco(String PESQUISAR_POR, String PESQUISA) throws SQLException;
    
     SucataEcoParametros getSucataEcoParamentrosOC(String Filial, String OrdemCompra, String Fornecedor) throws SQLException;
   
     SucataEcoParametros getSucataEcoParamentros(String Filial, String Empresa) throws SQLException;
}
