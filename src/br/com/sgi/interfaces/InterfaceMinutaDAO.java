package br.com.sgi.interfaces;

import br.com.sgi.bean.Minuta;
import br.com.sgi.bean.MinutaPedido;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceMinutaDAO<T> {

    boolean remover(T t) throws SQLException;
    
    

    boolean alterar(T t) throws SQLException;

    boolean gravarDadosPesagem(T t) throws SQLException;

    boolean gravarDadosMinuta(T t, MinutaPedido mp) throws SQLException;

    boolean imprimir(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    List<Minuta> getMinutas(String PESQUISA_POR, String PESQUISA, String geral) throws SQLException;

    List<Minuta> getMinutasDetalhada(String PESQUISA_POR, String PESQUISA, String geral) throws SQLException;

    Minuta getMinuta(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
